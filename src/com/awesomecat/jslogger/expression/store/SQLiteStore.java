package com.awesomecat.jslogger.expression.store;

import com.awesomecat.jslogger.expression.Expression;

public class SQLiteStore extends ExpressionStore {

	@Override
	public Expression getRegularExpression(String id) {
		// TODO: Retrieve this expression from the database
		return null;
	}

	@Override
	public void storeRegularExpression(String id, Expression exp) {
		// TODO: Store this expression in the database
	}
}