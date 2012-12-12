package com.awesomecat.jslogger.storage;


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

	@Override
	public void deleteOldExpressions() {
		// TODO: Clean out any expressions that might have expired
	}

	@Override
	public void deleteExpression(String id) {
		// TODO: Delete a specific expression
	}
}