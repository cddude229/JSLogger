package com.awesomecat.jslogger.storage;

import java.io.Serializable;

public class Expression implements Serializable {
	/**
	 * IGNORE. Used by serializable
	 */
	private static final long serialVersionUID = -8127473370473769283L;

	/**
	 * Duration qq of which this expression is valid
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
	 * The size of retained window
	 */
	public final int windowSize;
	
	public Expression(){
		this(0, null, true, 2);
	}
	
	public Expression(int validDuration, String expression, boolean runOnce, int windowSize){
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
