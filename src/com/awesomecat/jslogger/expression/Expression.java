package com.awesomecat.jslogger.expression;

import java.io.Serializable;

public class Expression implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8127473370473769283L;

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
	public final long creationTime;
	
	/**
	 * The actual regular expression
	 */
	public final String expression;
	
	/**
	 * Is this a run-once log line?
	 */
	public final boolean runOnce;
	
	/**
	 * The size of retained windows
	 */
	public final int windowSize;
	
	public Expression(){
		this(null, null, null, 0, null, true, 2);
	}
	
	public Expression(String sessionIP, String sessionCookie, String sessionIdentifier, int validDuration, String expression, boolean runOnce, int windowSize){
		this.sessionIP = sessionIP;
		this.sessionCookie = sessionCookie;
		this.sessionIdentifier = sessionIdentifier;
		this.validDuration = validDuration;
		this.creationTime = getCurrentTime();
		this.expression = expression;
		this.runOnce = runOnce;
		this.windowSize = windowSize;
	}
	
	public static long getCurrentTime(){
		return java.util.Calendar.getInstance().getTimeInMillis();
	}

}
