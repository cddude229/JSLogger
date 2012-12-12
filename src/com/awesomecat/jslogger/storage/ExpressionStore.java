package com.awesomecat.jslogger.storage;

import java.util.Random;


public abstract class ExpressionStore {
	
	public static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
	public static final int idLength = 10; // TODO: Make this load from configuration

	/**
	 * Generates a new, unused ID.  No guarantee to be unique, but typically 1 / (64^10) probability.
	 * @return The new ID. Contains A-Za-z0-9-_
	 */
	public String unusedID(){
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
	 * Returns the expression for the given id.
	 * @param id
	 * @return Returns null if nothing found
	 */
	abstract public Expression getRegularExpression(String id);

	/**
	 * Stores a new expression. If ID is in use already, silently overrides it
	 * @param id
	 * @param expression
	 */
	abstract public void storeRegularExpression(String id, Expression expression);
	
	abstract public void deleteOldExpressions();
	
	abstract public void deleteExpression(String id);
}