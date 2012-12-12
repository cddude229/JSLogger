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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + (runOnce ? 1231 : 1237);
		result = prime * result + validDuration;
		result = prime * result + windowSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression other = (Expression) obj;
		if (creationTime != other.creationTime)
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (runOnce != other.runOnce)
			return false;
		if (validDuration != other.validDuration)
			return false;
		if (windowSize != other.windowSize)
			return false;
		return true;
	}

	

}
