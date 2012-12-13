package com.awesomecat.jslogger.storage;

import java.util.Random;

import com.awesomecat.jslogger.JavaScriptLogger;


public abstract class AbstractStore {

	private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
	private static final int idLength = JavaScriptLogger.getConfig().getInt("associatedIdLength");





	////////////////////////////////////////////////////////////////////
	// Expressions
	////////////////////////////////////////////////////////////////////

	/**
	 * Returns the expression for the given id.
	 * @param id
	 * @return Returns null if nothing found
	 */
	abstract public Expression getExpression(int id);

	/**
	 * Gets an expression when given an associated id
	 * @param associatedId
	 * @return Null if no match
	 */
	abstract public Expression getExpressionFromAssociatedId(String associatedId);

	/**
	 * Gets an expression id when given an associated id
	 * @param associatedId
	 * @return -1 if no match
	 */
	abstract public int getExpressionIdFromAssociatedId(String associatedId);

	/**
	 * Stores a new expression and returns new ID.  If expression matches existing one, returns old ID
	 * @param expression
	 * @return Returns the expression ID
	 */
	abstract public int storeExpression(Expression expression);

	/**
	 * Returns the window size for a given expression id
	 * @param expressionId
	 * @return -1 if invalid expression
	 */
	public int getWindowSize(int expressionId){
		Expression exp = getExpression(expressionId);
		if(exp == null) return -1;
		return exp.windowSize;
	}





	////////////////////////////////////////////////////////////////////
	// Sessions
	////////////////////////////////////////////////////////////////////

	/**
	 * Gets a session id for us, given the type of the session and the value to lookup on. Will generate a new ID if no match found
	 * @param type
	 * @param value
	 * @return
	 */
	abstract public int getSessionId(SessionType type, String value);





	////////////////////////////////////////////////////////////////////
	// Associated IDs
	////////////////////////////////////////////////////////////////////

	/**
	 * Deletes any associated ID deemed to be expired
	 */
	abstract public void deleteExpiredAssociatedIds();

	/**
	 * Deletes an associated ID from the store
	 * @param id
	 */
	abstract public void deleteAssociatedId(String id);

	/**
	 * Returns a set of associated IDs that match the session and expression IDs
	 * @param sessionId
	 * @param expressionId
	 * @return May be empty
	 */
	abstract public String[] getAssociatedIds(int sessionId, int expressionId);

	/**
	 * Creates a new associated ID for this case
	 * @param sessionId
	 * @param expressionId
	 * @return
	 */
	abstract public String createAssociatedId(int sessionId, int expressionId);

	/**
	 * Deletes the oldest ID matching this.  This is because of the sliding window
	 * @param sessionId
	 * @param expressionId
	 */
	abstract public void deleteOldestAssociatedId(int sessionId, int expressionId);

	/**
	 * Generates a new, unused ID.  No guarantee to be unique, but typically 1 / (64^10) probability.
	 * @return The new ID. Contains A-Za-z0-9-_
	 */
	final static public String generateAssociatedId(){
		// NOTE: We're going to assume no conflicts because of how unlikely they are.  1 / (64^10) is the probability
		Random r = new Random();
		int i;
		String id = "";
		for(int j=0;j<idLength;j++){
			i = r.nextInt(characters.length());
			id += characters.substring(i, i+1);
		}
		return id;
	}

	/**
	 * Determines if the given ID is valid (And thus SQL secure)
	 * @param id
	 * @return valid?
	 */
	final static public boolean isValidAssociatedId(String id){
		for(int i=0;i<id.length();i++){
			if(characters.indexOf(id.substring(i, i+1)) < 0){
				return false;
			}
		}
		return true;
	}





	////////////////////////////////////////////////////////////////////
	// Combining it all
	////////////////////////////////////////////////////////////////////

	/**
	 * Determines if the associated ID matches the session and expression IDs
	 * @param associatedId
	 * @param sessionId
	 * @param expressionId
	 * @return
	 */
	public boolean matchAssociatedId(String associatedId, int sessionId, int expressionId){
		return matchAssociatedId(associatedId, sessionId, expressionId, false);
	}

	/**
	 * Determines if the associated ID  matches the session and expression IDs. If deleteOnDiscovery=true, will remove from store
	 * @param associatedId
	 * @param sessionId
	 * @param expressionId
	 * @param deleteOnDiscovery Remove the associatedID from the store after match is confirmed
	 * @return
	 */
	public boolean matchAssociatedId(String associatedId, int sessionId, int expressionId, boolean deleteOnDiscovery){
		String[] ids = getAssociatedIds(sessionId, expressionId);
		for(String s : ids){
			if(s.equals(associatedId)){
				if(deleteOnDiscovery){
					deleteAssociatedId(associatedId);
				}
				
				return true;
			}
		}
		return false;
	}
}