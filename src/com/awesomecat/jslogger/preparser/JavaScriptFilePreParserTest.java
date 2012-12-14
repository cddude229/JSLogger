package com.awesomecat.jslogger.preparser;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import com.awesomecat.jslogger.JavaScriptLogger;
import com.awesomecat.jslogger.mapper.SessionMapper;
import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.SessionType;

import static org.junit.Assert.*;



// NOTE: Known bug: rm jslogger-data.db before running this test
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
		assertEquals("Should insert it just once", 1, ids.length);
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
		assertEquals("Should insert it just once", 1, ids.length);
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
		assertEquals("Should insert it just once", 1, ids1.length);
		assertEquals("Should insert it just once", 1, ids2.length);
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
		String[] ids = store.getAssociatedIds(getSessionId(), expressionId);
		assertEquals("Should insert it just once", 1, ids.length);
	}

	@Test
	public void missingParametersTest() throws Exception {
		try {
			String result = testFile("TESTINPUT/missing_parameters.js");
			assertTrue("Should have replaced all definition blocks despite missing optional parameters", doesNotContainBlocks(result));
			Configuration c = JavaScriptLogger.getConfig();

			// Look up item and make sure it was inserted
			Expression e = new Expression(
				c.getInt("blocks.defaultValues.valid-duration"),
				"/^missing_parameters$/sim",
				c.getBoolean("blocks.defaultValues.run-once"),
				c.getInt("blocks.defaultValues.window-size")
			);
			AbstractStore store = JavaScriptLogger.getStore();
			int expressionId = store.storeExpression(e);
			String[] ids = store.getAssociatedIds(getSessionId(), expressionId);
			assertEquals("Should insert it just once", 1, ids.length);
		} catch (Exception e){
			assertTrue("Should not error because of missing (optional) parameters.", false);
		}
	}

	@Test
	public void dontReplaceIdOutsideLogCallTest() throws Exception {
		String result = testFile("TESTINPUT/id_outside_logcalls.js");
		assertTrue("Should not have replaced id outside of log calls", !doesNotContainId(result));
	}

	@Test
	public void invalidInputTest() throws Exception {
		try {
			String result;
			String[] files = new String[]{
				"blankid", "duration", "missingid", "runonce", "validate", "windowsize"
			};
			for(String file : files){
				result = testFile(String.format("TESTINPUT/incorrect_parameter_%s.js", file));
				assertTrue(String.format("Should not have replaced all definition blocks (%s)", file), !doesNotContainBlocks(result));
			}

			result = testFile("TESTINPUT/empty_block.js");
			assertTrue("Should not have removed an empty comment block", !doesNotContainBlocks(result));

		} catch(Exception e){
			assertTrue("The JavaScriptFilePreParser should never throw an error for invalid parameters", false);
		}
	}
	
	@Test
	public void shouldErrorTests() throws Exception {
		File file = new File("ASDFASDFASDF/file_does_not_exist.js");
		try {
			JavaScriptFilePreParser.evaluateFile(file, getMapper());
			assertTrue("PreParser should error if file can't be found", false);
		} catch(Exception e){
		}
		
		File file2 = new File(".");
		try {
			JavaScriptFilePreParser.evaluateFile(file2, getMapper());
			assertTrue("PreParser should error if given a directory", false);
		} catch(Exception e){
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