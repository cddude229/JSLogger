package com.awesomecat.jslogger.mapper;

import com.awesomecat.jslogger.storage.Expression;

/**
 * Used to be able to register expressions and obtain associated IDs when parsing a file
 */
public interface Mapper {
	/**
	 * Will register an expression object and return the ID that should be in the JavaScript log call
	 * @param expression
	 * @return The associated ID to be put into the log call
	 */
	public String registerExpressionAndGetAssociatedId(Expression expression);
}
