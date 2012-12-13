package com.awesomecat.jslogger;

import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;

/**
 * Used to be able to register expressions and obtain associated IDs when parsing a file
 */
public class StaticMapper implements Mapper {
	private final AbstractStore store;
	public StaticMapper(AbstractStore store){
		this.store = store;
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
		return store.getStaticIdForExpression(registerExpression(expression));
	}

}
