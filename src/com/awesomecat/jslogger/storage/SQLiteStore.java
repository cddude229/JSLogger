package com.awesomecat.jslogger.storage;


// TODO: @Chris Implement SQLiteStore for when we need static to be stored over time and survive a restart
public class SQLiteStore extends AbstractStore {

	@Override
	public Expression getExpression(int id) {
		return null;
	}

	@Override
	public int storeExpression(Expression expression) {
		return 0;
	}

	@Override
	public int getSessionId(SessionType type, String value) {
		return 0;
	}

	@Override
	public void deleteExpiredAssociatedIds() {
	}

	@Override
	public void deleteAssociatedId(String id) {
	}

	@Override
	public String[] getAssociatedIds(int sessionId, int expressionId) {
		return null;
	}

	@Override
	public String createAssociatedId(int sessionId, int expressionId) {
		return null;
	}

	@Override
	public void deleteOldestAssociatedId(int sessionId, int expressionId) {
	}

}