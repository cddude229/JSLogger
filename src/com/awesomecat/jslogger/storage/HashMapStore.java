package com.awesomecat.jslogger.storage;

import java.util.ArrayList;
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
			this.creationTime = (sessionId == staticSessionId?Long.MAX_VALUE:java.util.Calendar.getInstance().getTimeInMillis());
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
		/*
		 * Steps:
		 * 1) Iterate over all associated IDs
		 * 2) Find the corresponding matching Expression
		 * 3) Get validDuration from Expression
		 * 4) if validDuration < currentTime - associatedId.creationTime, then delete it (use deleteAssociatedId(String id))
		 */
		ArrayList<String> deletes = new ArrayList<String>();
		for(String id : associatedIdStore.keySet()){
			// Get associated ID, find expression, see if we should delete it
			AssociatedId a = associatedIdStore.get(id);
			if(a.sessionId == staticSessionId) return; // Don't remove static
			Expression e = getExpression(a.expressionId);
			int validDuration = e.validDuration;
			if(validDuration < java.util.Calendar.getInstance().getTimeInMillis() - a.creationTime){
				// Mark for delete.  We can't delete here because it messes up the iterator
				deletes.add(id);
			}
		}
		
		// Finally delete them
		for(String id : deletes){
			deleteAssociatedId(id);
		}
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
		// There is no space limit for static sessionId
		if(sessionId != staticSessionId && getAssociatedIds(sessionId, expressionId).length > getWindowSize(expressionId)){
			deleteOldestAssociatedId(sessionId, expressionId);
		}

		// Insert our new item and return new id
		String id = generateAssociatedId();
		associatedIdStore.put(id, new AssociatedId(sessionId, expressionId));
		return id;
	}

	@Override
	public void deleteOldestAssociatedId(int sessionId, int expressionId) {
		if(sessionId == staticSessionId) return;
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

	@Override
	public Expression getExpressionFromAssociatedId(String associatedId) {
		int expressionId = getExpressionIdFromAssociatedId(associatedId);
		if(expressionId == -1) return null;
		return getExpression(expressionId);
	}

	@Override
	public int getExpressionIdFromAssociatedId(String associatedId) {
		AssociatedId a = associatedIdStore.get(associatedId);
		if(a == null) return -1;
		return a.expressionId;
	}
}