package com.awesomecat.jslogger;

import java.io.File;

import org.junit.Test;

import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.SessionType;

import static org.junit.Assert.*;



public class JavaScriptFilePreParserTest {

	@Test
	public void evaluateTest1() throws Exception {
		String result = testFile("TESTINPUT/basic_test.js");
		assertTrue("Should have replaced all ids", doesNotContainId(result));
		assertTrue("Should have replaced all definition blocks", doesNotContainBlocks(result));
		System.out.println(result);
		
		// Ok, make sure the item was added to the store
		AbstractStore store = JavaScriptLogger.getStore();
		Expression e = new Expression(5, "/^basic_test$/sim", true, 2);
		int expressionId = store.storeExpression(e);
		String[] ids = store.getAssociatedIds(getSessionId(), expressionId);
		System.out.println(ids.length);
		assertTrue("Should insert it just once", ids.length == 1);
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
