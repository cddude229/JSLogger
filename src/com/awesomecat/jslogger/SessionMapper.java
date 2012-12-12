package com.awesomecat.jslogger;

import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;

/**
 * Used to be able to register expressions and obtain associated IDs when parsing a file
 */
public class SessionMapper {
	private final int sessionId;
	private final AbstractStore store;
	public SessionMapper(int sessionId, AbstractStore store){
		this.sessionId = sessionId;
		this.store = store;
	}

	/**
	 * Gets an associated ID given an expression ID
	 * @param expressionId
	 * @return
	 */
	private String getAssociatedId(int expressionId){
		return store.createAssociatedId(sessionId, expressionId);
	}

	/**
	 * Registers an expression with the store
	 * @param expression
	 * @return The ID of the registered expression
	 */
	private int registerExpression(Expression expression){
		return store.storeExpression(expression);
	}

	/**
	 * Will register an expression object and return the ID that should be in the JavaScript log call
	 * @param expression
	 * @return The associated ID to be put into the log call
	 */
	public String registerExpressionAndGetAssociatedId(Expression expression){
		return getAssociatedId(registerExpression(expression));
	}

}
