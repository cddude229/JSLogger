package com.awesomecat.jslogger;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.Random;

public class PreParserTest {

	@Test
	public void testValidRegularExpression() throws Exception {
		//assertTrue(PreParser.validRegularExpression(""));
		
		// Null
		assertFalse("Null should be false", PreParser.validRegularExpression(null));
		assertFalse("Empty string should be false", PreParser.validRegularExpression(""));

		// Test start/stop slash
		assertFalse("Does not contain valid start or stop slash", PreParser.validRegularExpression("^dude$"));
		assertFalse("Does not contain valid start slash", PreParser.validRegularExpression("^dude$/"));
		assertFalse("Does not contain valid stop slash", PreParser.validRegularExpression("/^dude$"));
		assertFalse("Does not contain valid stop slash (escaped end slash)", PreParser.validRegularExpression("/^dude$\\/"));
		
		// Test start/stop anchors
		assertFalse("Does not contain valid start or stop anchor", PreParser.validRegularExpression("/dude/"));
		assertFalse("Does not contain valid start anchor", PreParser.validRegularExpression("/dude$/"));
		assertFalse("Does not contain valid stop anchor", PreParser.validRegularExpression("/^dude/"));
		assertFalse("Start anchor can not be escaped", PreParser.validRegularExpression("/\\^dude$/"));
		assertFalse("Stop anchor can not be escaped", PreParser.validRegularExpression("/^dude\\$/"));

		// Test allowing valid flags
		String s = randomAllowedFlag();
		System.out.println("s: "+ s); //DEBUG
		assertTrue("Valid flag ("+s+") marked as invalid", PreParser.validRegularExpression("/^dude$/"+s));
		assertFalse("Invalid flag marked as valid", PreParser.validRegularExpression("/^dude$/#"));
		//assertFalse("Don't allow duplicate flags", PreParser.validRegularExpression("/^dude$/"+s+s));

		// Test blocking
		assertFalse("Can not contain an unescaped +", PreParser.validRegularExpression("/^dude+$/"));
		assertFalse("Can not contain an unescaped *", PreParser.validRegularExpression("/^dude*$/"));
		assertFalse("Can not contain a quantifier without final value", PreParser.validRegularExpression("/^dude{65,}$/"));
		assertFalse("Can not contain a quantifier without final value", PreParser.validRegularExpression("/^dude{5,}$/"));
		assertFalse("Can not contain a quantifier without final value", PreParser.validRegularExpression("/^dude{,}$/"));
		//assertFalse("Can not accept a quantifier with final value > initial value", PreParser.validRegularExpression("/^dude{6,3}$/"));
		//assertFalse("Can not accept a quantifier with final value under 1", PreParser.validRegularExpression("/^dude{,-3}$/"));
		//assertFalse("Can not accept a quantifier with final value under 1", PreParser.validRegularExpression("/^dude{,0}$/"));
		assertTrue("Should accept an escaped +", PreParser.validRegularExpression("/^dude\\+$/"));
		assertTrue("Should accept an escaped *", PreParser.validRegularExpression("/^dude\\*$/"));
		assertTrue("Should accept a quantifier with a final value", PreParser.validRegularExpression("/^dude{1,5}$/"));
		assertTrue("Should accept a quantifier with a final value", PreParser.validRegularExpression("/^dude{,5}$/"));
		assertTrue("Should accept a quantifier with only one value", PreParser.validRegularExpression("/^dude{4}$/"));
		assertTrue("Should accept a quantifier with a fina value of 1", PreParser.validRegularExpression("/^dude{,1}$/"));
	}

	Random rand = new Random();
	public String randomAllowedFlag(){
		String flags = PreParser.allowedFlags;
		int n = rand.nextInt(flags.length());
		return flags.substring(n, n+1);
	}

	@Test
	public void testIsValidFlag() throws Exception {
		for(int i=0;i<PreParser.allowedFlags.length();i++){
			assertTrue(
				"Checking that all allowed flags past check",
				PreParser.isValidFlag(PreParser.allowedFlags.substring(i, i+1))
			);
		}
	}

	@Test
	public void testValidWindowSize() throws Exception {
		assertTrue(PreParser.validWindowSize("1"));
		assertTrue(PreParser.validWindowSize("10"));
		assertTrue(PreParser.validWindowSize("100"));
		assertTrue(PreParser.validWindowSize("010"));
		assertFalse(PreParser.validWindowSize("hi"));
		assertFalse(PreParser.validWindowSize("0"));
		assertFalse(PreParser.validWindowSize("-1"));
		assertFalse(PreParser.validWindowSize("0.5"));
		assertFalse(PreParser.validWindowSize("1.5"));
	}
}
