package com.awesomecat.jslogger;

public class Expression {
	/**
	 * Associated IP for this expression
	 */
	public final String sessionIP;
	
	/**
	 * Associated cookie value for this expression
	 */
	public final String sessionCookie;
	
	/**
	 * Associated identifier, typically a username or userid
	 */
	public final String sessionIdentifier;
	
	/**
	 * Length of which this expression is valid
	 */
	public final int validDuration;
	
	/**
	 * Time of creation
	 */
	public final int creationTime;
	
	/**
	 * The actual regular expression
	 */
	public final String expression;
	
	public Expression(){
		this(null, null, null, 0, null);
	}
	
	public Expression(String sessionIP, String sessionCookie, String sessionIdentifier, int validDuration, String expression){
		this.sessionIP = sessionIP;
		this.sessionCookie = sessionCookie;
		this.sessionIdentifier = sessionIdentifier;
		this.validDuration = validDuration;
		this.creationTime = 0; // TODO: Make this get current time
		this.expression = expression;
	}

}
