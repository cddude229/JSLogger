package com.awesomecat.jslogger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Calendar;

public class RateLimiter {
	private class RateDataSet {
		public final Map<Integer, Integer> userToDataMap;
		public RateDataSet(){
			userToDataMap = new HashMap<Integer, Integer>();
		}

		public int getUserUsage(int sessionId){
			if(userToDataMap.containsKey(sessionId)){
				return userToDataMap.get(sessionId);
			}
			return 0;
		}

		public int addUserUsage(int sessionId, int usage){
			int current = 0;
			if(userToDataMap.containsKey(sessionId)){
				current = userToDataMap.get(sessionId);
			}
			userToDataMap.put(sessionId, current+usage);
			return current+usage;
		}
	}
	
	private final Map<Integer, RateDataSet> logsList;
	private final Map<Integer, RateDataSet> dataList;
	
	private final int pastMinutesToKeep = 5; // TODO: @Chris: Make this load from configuration
	
	public RateLimiter(){
		logsList = new HashMap<Integer, RateDataSet>();
		dataList = new HashMap<Integer, RateDataSet>();
	}

	/**
	 * Add a record of a user sending in a log message + some data
	 * @param sessionId Identity of user
	 * @param logs The number of log messages received
	 * @param data The amount of data, in kilobytes, that has been used
	 */
	public void addData(int sessionId, int logs, int data){
		// NOTE: We use logs (instead of assuming 1) because in the future, 
		// the JS portion might build a bulk of messages before sending them
		// Roll over to new minute
		int newMinute = handleRollover();
		dataList.get(newMinute).addUserUsage(sessionId, data);
		logsList.get(newMinute).addUserUsage(sessionId, logs);
	}

	/**
	 * Is the given user rate limited?
	 * @param sessionId Identify user by session ID
	 * @return True if so, false otherwise
	 */
	public boolean isUserLimited(int sessionId){
		// TODO: @Chris: Implement rate limiting the user
		return false;
	}

	private int lastMinuteHandled = -1;
	/**
	 * Rollsover to new minute if it's changed 
	 * @return The new minute to use
	 */
	private int handleRollover(){
		int newMinute = getCurrentMinute();
		if(newMinute != lastMinuteHandled){
			// Updated minute.  Clear newMinute before writing to it
			addDataSet(newMinute);
			lastMinuteHandled = newMinute;
		}
		return newMinute;
	}
	
	/**
	 * Removes and puts in a new data set for given minute
	 * @param minute
	 */
	private void addDataSet(int minute){
		logsList.put(minute, new RateDataSet());
		dataList.put(minute, new RateDataSet());
	}

	/**
	 * Gets the current minute to use as an index
	 * @return
	 */
	public static int getCurrentMinute(){
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

}
