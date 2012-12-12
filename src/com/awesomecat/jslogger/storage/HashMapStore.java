package com.awesomecat.jslogger.storage;

import java.util.HashMap;

public class HashMapStore extends AbstractStore {

	private HashMap<Integer, Expression> expressionStore = new HashMap<Integer, Expression>();

	private HashMap<String, AssociatedId> associatedIdStore = new HashMap<String, AssociatedId>();

	private HashMap<Integer, String> sessionIpStore = new HashMap<Integer, String>();

	private HashMap<Integer, String> sessionUsernameStore = new HashMap<Integer, String>();
	
	private class AssociatedId {
		final int sessionId;
		final int expressionId;
		final long creationTime;
		public AssociatedId(int sessionId, int expressionId){
			this.sessionId = sessionId;
			this.expressionId = expressionId;
			this.creationTime = java.util.Calendar.getInstance().getTimeInMillis();
		}
	}

	@Override
	public Expression getExpression(int id) {
		if(expressionStore.containsKey(id)){
			return expressionStore.get(id);
		}
		return null;
	}

	private int nextExpressionId = 0;
	@Override
	public int storeExpression(Expression expression) {
		for(Integer i : expressionStore.keySet()){
			if(expression.equals(expressionStore.get(i))){
				return i;
			}
		}
		int i = nextExpressionId++;
		expressionStore.put(i, expression);
		return i;
	}

	private int nextSessionId = 0;
	@Override
	public int getSessionId(SessionType type, String value) {
		HashMap<Integer, String> store;
		if(type == SessionType.IP){
			store = sessionIpStore;
		} else {
			store = sessionUsernameStore;
		}
		for(Integer i : store.keySet()){
			if(value.equals(store.get(i))){
				return i;
			}
		}
		int i = nextSessionId++;
		store.put(i, value);
		return i;
	}

	@Override
	public void deleteExpiredAssociatedIds() {
		// TODO @Chris delete expired associated IDs
	}

	@Override
	public void deleteAssociatedId(String id) {
		associatedIdStore.remove(id);
	}

	@Override
	public String[] getAssociatedIds(int sessionId, int expressionId) {
		int windowSize = getWindowSize(expressionId);
		String[] s = new String[windowSize];
		int used = 0;

		// Try to find matching items
		for(String id : associatedIdStore.keySet()){
			AssociatedId a = associatedIdStore.get(id);
			if(a.sessionId == sessionId && a.expressionId == expressionId){
				s[used++] = id;
				if(used == windowSize) return s; // Break out if we can
			}
		}

		// Ok, we overfilled... only return what we need
		String[] s2 = new String[used];
		for(int i=0;i<used;i++){
			s2[i] = s[i];
		}
		return s2;
	}

	@Override
	public String createAssociatedId(int sessionId, int expressionId) {
		// Clear a space if it's already taken
		if(getAssociatedIds(sessionId, expressionId).length > getWindowSize(expressionId)){
			deleteOldestAssociatedId(sessionId, expressionId);
		}

		// Insert our new item and return new id
		String id = generateAssociatedId();
		associatedIdStore.put(id, new AssociatedId(sessionId, expressionId));
		return id;
	}

	@Override
	public void deleteOldestAssociatedId(int sessionId, int expressionId) {
		// Get all current items
		String[] ids = getAssociatedIds(sessionId, expressionId);
		String minId = ids[0];
		long minTime = Long.MAX_VALUE;
		
		// Iterate over them and find min time
		for(String id : ids){
			AssociatedId a = associatedIdStore.get(id);
			if(a.creationTime < minTime){
				minTime = a.creationTime;
				minId = id;
			}
		}

		// Delete min time
		deleteAssociatedId(minId);
	}

}