package com.awesomecat.jslogger.storage;

import java.io.Serializable;

public class Expression implements Serializable {
	/**
	 * IGNORE. Used by serializable
	 */
	private static final long serialVersionUID = -8127473370473769283L;

	/**
	 * Duration of which this expression is valid
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

	/**
	 * Is this inside a static file?
	 */
	public final boolean staticFile;
	
	public Expression(int validDuration, String expression, boolean runOnce, int windowSize){
		this(validDuration, expression, runOnce, windowSize, false);
	}

	public Expression(int validDuration, String expression, boolean runOnce, int windowSize, boolean staticFile){
		this.validDuration = validDuration;
		this.creationTime = getCurrentTime();
		this.expression = expression;
		this.runOnce = runOnce;
		this.windowSize = windowSize;
		this.staticFile = staticFile;
	}
	
	public static long getCurrentTime(){
		return java.util.Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + (runOnce ? 1231 : 1237);
		result = prime * result + validDuration;
		result = prime * result + windowSize;
		result = prime * result + (staticFile ? 1231 : 1237);
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
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (runOnce != other.runOnce)
			return false;
		if (validDuration != other.validDuration)
			return false;
		if (staticFile != other.staticFile)
			return false;
		if (windowSize != other.windowSize)
			return false;
		return true;
	}

	

}
