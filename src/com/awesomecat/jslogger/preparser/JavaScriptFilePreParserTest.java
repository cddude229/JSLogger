package com.awesomecat.jslogger.preparser;

import java.io.File;

import org.junit.Test;

import com.awesomecat.jslogger.JavaScriptLogger;
import com.awesomecat.jslogger.mapper.SessionMapper;
import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.SessionType;

import static org.junit.Assert.*;



public class JavaScriptFilePreParserTest {

	@Test
	public void basicTest() throws Exception {
		String result = testFile("TESTINPUT/basic_test.js");
		assertTrue("Should have replaced all ids", doesNotContainId(result));
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));
		
		// Ok, make sure the item was added to the store
		AbstractStore store = JavaScriptLogger.getStore();
		Expression e = new Expression(5, "/^basic_test$/sim", true, 2);
		int expressionId = store.storeExpression(e);
		String[] ids = store.getAssociatedIds(getSessionId(), expressionId);
		assertTrue("Should insert it just once", ids.length == 1);
	}

	@Test
	public void multipleLogCallsTest() throws Exception {
		String result = testFile("TESTINPUT/multiple_log_calls.js");
		assertTrue("Should have replaced all ids", doesNotContainId(result));
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));

		// Ok, make sure the item was added to the store only once
		AbstractStore store = JavaScriptLogger.getStore();
		Expression e = new Expression(5, "/^multiple_log_calls$/sim", true, 2);
		int expressionId = store.storeExpression(e);
		String[] ids = store.getAssociatedIds(getSessionId(), expressionId);
		assertTrue("Should insert it just once", ids.length == 1);
	}

	@Test
	public void multipleBlocksTest() throws Exception {
		String result = testFile("TESTINPUT/multiple_blocks.js");
		assertTrue("Should have replaced all ids", doesNotContainId(result));
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));

		// Ok, make sure each item was added to the store only once
		AbstractStore store = JavaScriptLogger.getStore();
		Expression e1 = new Expression(5, "/^multiple_blocks1$/sim", true, 2);
		Expression e2 = new Expression(5, "/^multiple_blocks2$/sim", true, 2);
		int expressionId1 = store.storeExpression(e1);
		int expressionId2 = store.storeExpression(e2);
		String[] ids1 = store.getAssociatedIds(getSessionId(), expressionId1);
		String[] ids2 = store.getAssociatedIds(getSessionId(), expressionId2);
		assertTrue("Should insert it just once", ids1.length == 1);
		assertTrue("Should insert it just once", ids2.length == 1);
	}

	@Test
	public void unmatchedLogCallsTest() throws Exception {
		String result = testFile("TESTINPUT/unmatched_log_calls.js");
		assertTrue("Should have not replaced all ids", !doesNotContainId(result));
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));
		assertTrue("Should have replaced all of the first type", result.indexOf("$id=5") < 0);
	}

	@Test
	public void notDefaultParametersTest() throws Exception {
		String result = testFile("TESTINPUT/non_default_parameters.js");
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));

		// Ok, make sure each item was added to the store only once
		AbstractStore store = JavaScriptLogger.getStore();
		Expression e = new Expression(91, "/^non_default_parameters$/sim", false, 6);
		int expressionId = store.storeExpression(e);
		String[] ids1 = store.getAssociatedIds(getSessionId(), expressionId);
		assertTrue("Should insert it just once", ids1.length == 1);
	}

	@Test
	public void invalidInputTest() throws Exception {
		try {
			String result;

			result = testFile("TESTINPUT/incorrect_parameter_blankid.js");
			assertTrue("Should not have replaced all definition blocks (blank id)", !doesNotContainBlocks(result));

			result = testFile("TESTINPUT/incorrect_parameter_duration.js");
			assertTrue("Should not have replaced all definition blocks (duration)", !doesNotContainBlocks(result));

			result = testFile("TESTINPUT/incorrect_parameter_missingid.js");
			assertTrue("Should not have replaced all definition blocks (missing id)", !doesNotContainBlocks(result));

			result = testFile("TESTINPUT/incorrect_parameter_runonce.js");
			assertTrue("Should not have replaced all definition blocks (runonce)", !doesNotContainBlocks(result));

			result = testFile("TESTINPUT/incorrect_parameter_validate.js");
			assertTrue("Should not have replaced all definition blocks (validate)", !doesNotContainBlocks(result));

			result = testFile("TESTINPUT/incorrect_parameter_windowsize.js");
			assertTrue("Should not have replaced all definition blocks (windowsize)", !doesNotContainBlocks(result));

		} catch(Exception e){
			assertTrue("The JavaScriptFilePreParser should never throw an error for invalid parameters", false);
		}
		
	}
	
	@Test
	public void shouldErrorTests() throws Exception {
		File file = new File("ASDFASDFASDF/file_does_not_exist.js");
		try {
			JavaScriptFilePreParser.evaluateFile(file, getMapper());
		} catch(Exception e){
			assertTrue("PreParser should error if file can't be found", true);
		}
		
		File file2 = new File(".");
		try {
			JavaScriptFilePreParser.evaluateFile(file2, getMapper());
		} catch(Exception e){
			assertTrue("PreParser should error if given a directory", true);
		}
		
		// Did not write a test for no read perms
	}






	////////////////////////////////////////////////////////
	// HELPER METHODs
	////////////////////////////////////////////////////////
	/**
	 * Gets a dummy session ID based off localhost IP
	 * @return
	 */
	private int getSessionId(){
		return JavaScriptLogger.getSessionId(SessionType.IP, "127.0.0.1");
	}

	private SessionMapper mapper = null;
	/**
	 * Builds a dummy mapper for us
	 * @return
	 */
	private SessionMapper getMapper(){
		if(mapper == null){
			int sessionId = getSessionId();
			mapper = JavaScriptLogger.buildSessionMapper(sessionId);
		}
		return mapper;
	}

	/**
	 * Attempts to parse a file.  Returns the string result.
	 * @param path
	 * @return
	 */
	private String testFile(String path){
		File file = new File(path);
		try {
			return JavaScriptFilePreParser.evaluateFile(file, getMapper());
		} catch(Exception e){
			assertFalse("Should not throw an error", true);
		}
		return "";
	}
	
	/**
	 * Determines if it validly replaced IDs in the string s
	 * @param s
	 * @return
	 */
	private boolean doesNotContainId(String s){
		return s.indexOf("$id") < 0;
	}

	/**
	 * Determines if it validly deletes the comment blocks from s
	 * @param s
	 * @return
	 */
	private boolean doesNotContainBlocks(String s){
		return s.indexOf("/**#") < 0 && s.indexOf("#**/") < 0;
	}
}