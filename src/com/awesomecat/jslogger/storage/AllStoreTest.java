package com.awesomecat.jslogger.storage;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;


public class AllStoreTest {
	
	private final Random r = new Random();
	
	@Test
	public void testAssocIdGeneration() throws Exception {
		int i = 0;
		String id;
		while(i++ < 100){
			id = AbstractStore.generateAssociatedId();
			assertTrue("generated ID ("+id+") should be valid", AbstractStore.isValidAssociatedId(id));
		}
	}
	
	@Test
	public void testHashMapStore() throws Exception {
		runTestSuite(new HashMapStore());
	}
	
	/*
	TODO: @Chris When SQLiteStore is done, enable this test
	
	@Test
	public void testSQLiteStore() throws Exception {
		runTestSuite(new SQLiteStore());
	}
	*/

	private void runTestSuite(AbstractStore store){
		// Expressions
		testStoredExpressionShouldEqualInput(store);

		// Sessions
		testSameIp(store);
		testSameUsername(store);
		testIpAndUsernameConflict(store);

		// Associated IDs
		testCreatingAssociatedIds(store);
	}

	private void testSameIp(AbstractStore store){
		String ip = "123.123.123."+r.nextInt(255);
		int id1 = store.getSessionId(SessionType.IP, ip);
		int id2 = store.getSessionId(SessionType.IP, ip);
		assertTrue("Same IP should return the same id", id1 == id2);
	}

	private void testSameUsername(AbstractStore store){
		String username = "abc123"+r.nextInt();
		int id1 = store.getSessionId(SessionType.USERNAME, username);
		int id2 = store.getSessionId(SessionType.USERNAME, username);
		assertTrue("Same username should return the same id", id1 == id2);
	}

	private void testIpAndUsernameConflict(AbstractStore store){
		String ip = "124.124.124."+r.nextInt(255);
		int id1 = store.getSessionId(SessionType.IP, ip);
		int id2 = store.getSessionId(SessionType.USERNAME, ip);
		assertFalse("Session ip and username should return different ids for the same input", id1 == id2);
	}

	private void testStoredExpressionShouldEqualInput(AbstractStore store){
		Expression e = new Expression();
		int id = store.storeExpression(e);
		assertTrue(
			"Stored expression (default constructor) should equal the input",
			e.equals(store.getExpression(id))
		);

		Expression e2 = new Expression(r.nextInt(), "Some expression", true, r.nextInt());
		int id2 = store.storeExpression(e2);
		assertTrue(
			"Stored expression (NOT default constructor) should equal the input",
			e2.equals(store.getExpression(id2))
		);
	}

	private void testCreatingAssociatedIds(AbstractStore store){
		Expression e = new Expression();
		String ip = "123.245.123.245";
		int expressionId = store.storeExpression(e);
		int sessionId = store.getSessionId(SessionType.IP, ip);
		String assocId1= store.createAssociatedId(sessionId, expressionId);
		
		// see if getAssociatedIds contains it...
		boolean found = false;
		for(String s : store.getAssociatedIds(sessionId, expressionId)){
			found = found || s.equals(assocId1);
		}
		assertTrue("Created ID was in getAssociatedIds", found);
		
		// Now, see if match works
		assertTrue("Associated id should match session and expression id", store.matchAssociatedId(assocId1, sessionId, expressionId));

		// Now, try deleting the assoc id and see if match fails
		store.deleteAssociatedId(assocId1);
		assertFalse("Deleted associated id should not match session and expression id", store.matchAssociatedId(assocId1, sessionId, expressionId));

		// Now, create a second id.  They can NOT be the same even if previous one was deleted
		String assocId2 = store.createAssociatedId(sessionId, expressionId);
		assertFalse("The two associated IDs should be different", assocId1.equals(assocId2));

		// Now, try match with delete.  Should return true first time, false the second
		assertTrue(
			"Associated id should match session and expression id, even if deleting",
			store.matchAssociatedId(assocId2, sessionId, expressionId, true)
		);
		assertFalse(
			"Associated id deleted with matchAssociatedId should not be found",
			store.matchAssociatedId(assocId2, sessionId, expressionId)
		);

	}
	
	// TODO: @Chris: Update the tests above to include the new getExpresionFromAssociatedId methods

}
