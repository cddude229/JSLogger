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

	public String getAssociatedId(int expressionId){
		return store.createAssociatedId(sessionId, expressionId);
	}

	public int registerExpression(Expression expression){
		return store.storeExpression(expression);
	}

	public String registerExpressionAndGetAssociatedId(Expression expression){
		return getAssociatedId(registerExpression(expression));
	}

}
