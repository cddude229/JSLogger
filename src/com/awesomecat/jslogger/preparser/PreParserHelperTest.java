package com.awesomecat.jslogger.preparser;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.Random;

public class PreParserHelperTest {

	@Test
	public void testValidRegularExpression() throws Exception {
		//assertTrue(PreParser.validRegularExpression(""));
		
		// Null
		assertFalse("Null should be false", PreParserHelper.validRegularExpression(null));
		assertFalse("Empty string should be false", PreParserHelper.validRegularExpression(""));

		// Test start/stop slash
		assertFalse("Does not contain valid start or stop slash", PreParserHelper.validRegularExpression("^dude$"));
		assertFalse("Does not contain valid start slash", PreParserHelper.validRegularExpression("^dude$/"));
		assertFalse("Does not contain valid stop slash", PreParserHelper.validRegularExpression("/^dude$"));
		assertFalse("Does not contain valid stop slash (escaped end slash)", PreParserHelper.validRegularExpression("/^dude$\\/"));
		
		// Test start/stop anchors
		assertFalse("Does not contain valid start or stop anchor", PreParserHelper.validRegularExpression("/dude/"));
		assertFalse("Does not contain valid start anchor", PreParserHelper.validRegularExpression("/dude$/"));
		assertFalse("Does not contain valid stop anchor", PreParserHelper.validRegularExpression("/^dude/"));
		assertFalse("Start anchor can not be escaped", PreParserHelper.validRegularExpression("/\\^dude$/"));
		assertFalse("Stop anchor can not be escaped", PreParserHelper.validRegularExpression("/^dude\\$/"));

		// Test allowing valid flags
		String s = randomAllowedFlag();
		System.out.println("s: "+ s); //DEBUG
		assertTrue("Valid flag ("+s+") marked as invalid", PreParserHelper.validRegularExpression("/^dude$/"+s));
		assertFalse("Invalid flag marked as valid", PreParserHelper.validRegularExpression("/^dude$/#"));
		assertTrue("Don't error with duplicate flags", PreParserHelper.validRegularExpression("/^dude$/"+s+s));

		// Test blocking
		assertFalse("Can not contain an unescaped +", PreParserHelper.validRegularExpression("/^dude+$/"));
		assertFalse("Can not contain an unescaped *", PreParserHelper.validRegularExpression("/^dude*$/"));
		assertFalse("Can not contain a quantifier without final value", PreParserHelper.validRegularExpression("/^dude{65,}$/"));
		assertFalse("Can not contain a quantifier without final value", PreParserHelper.validRegularExpression("/^dude{5,}$/"));
		assertFalse("Can not contain a quantifier without final value", PreParserHelper.validRegularExpression("/^dude{,}$/"));
		assertFalse("Can not accept a quantifier with final value > initial value", PreParserHelper.validRegularExpression("/^dude{6,3}$/"));
		assertFalse("Can not accept a quantifier with final value under 1", PreParserHelper.validRegularExpression("/^dude{,-3}$/"));
		assertFalse("Can not accept a quantifier with final value under 1", PreParserHelper.validRegularExpression("/^dude{,0}$/"));
		assertTrue("Should accept an escaped +", PreParserHelper.validRegularExpression("/^dude\\+$/"));
		assertTrue("Should accept an escaped *", PreParserHelper.validRegularExpression("/^dude\\*$/"));
		assertTrue("Should accept a quantifier with a final value", PreParserHelper.validRegularExpression("/^dude{1,5}$/"));
		assertTrue("Should accept a quantifier with only one value", PreParserHelper.validRegularExpression("/^dude{4}$/"));
	}

	Random rand = new Random();
	public String randomAllowedFlag(){
		String flags = PreParserHelper.allowedFlags;
		int n = rand.nextInt(flags.length());
		return flags.substring(n, n+1);
	}

	@Test
	public void testIsValidFlag() throws Exception {
		for(int i=0;i<PreParserHelper.allowedFlags.length();i++){
			assertTrue(
				"Checking that all allowed flags past check",
				PreParserHelper.isValidFlag(PreParserHelper.allowedFlags.substring(i, i+1))
			);
		}
	}

	@Test
	public void testValidWindowSize() throws Exception {
		assertTrue(PreParserHelper.validWindowSize("1"));
		assertTrue(PreParserHelper.validWindowSize("10"));
		assertTrue(PreParserHelper.validWindowSize("100"));
		assertTrue(PreParserHelper.validWindowSize("010"));
		assertFalse(PreParserHelper.validWindowSize("hi"));
		assertFalse(PreParserHelper.validWindowSize("0"));
		assertFalse(PreParserHelper.validWindowSize("-1"));
		assertFalse(PreParserHelper.validWindowSize("0.5"));
		assertFalse(PreParserHelper.validWindowSize("1.5"));
	}
}
