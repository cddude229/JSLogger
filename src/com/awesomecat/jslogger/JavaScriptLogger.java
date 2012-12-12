package com.awesomecat.jslogger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.HashMapStore;
import com.awesomecat.jslogger.storage.SessionType;

public class JavaScriptLogger {
    private static Logger logger = Logger.getLogger("jslogger");
    private static AbstractStore store = new HashMapStore();
    private static Level jsLogLevel = Level.ERROR;
    

    
    public static boolean handleLogging(ServletRequest request, SessionType sessionType, String sessionValue){
    	// No null values allowed
    	if(request == null) return false;

    	// Get message and associatedId. Exit if null
    	String message = request.getParameter("message");
    	String associatedId = request.getParameter("logid");
    	
    	return handleLogging(message, associatedId, sessionType, sessionValue);
    }

    public static boolean handleLogging(String message, String associatedId, SessionType sessionType, String sessionValue){
        // No null values allowed
    	if(message == null || associatedId == null || sessionType == null || sessionValue == null) return false;

    	// Get our sessionId
    	int sessionId = store.getSessionId(sessionType, sessionValue);
    	
    	// Validate that the log id exists
    	int expressionId = store.getExpressionIdFromAssociatedId(associatedId);

    	// Validate that it exists still.
    	Expression e = store.getExpression(expressionId);
    	if(e == null) return false;
    	boolean deleteOnDiscovery = e.runOnce;
    	if(!store.matchAssociatedId(associatedId, sessionId, expressionId, deleteOnDiscovery)){
    		return false; // ID did not match
    	}
   
    	// At this point, the associated ID has been matched.  Let's see if the message matches the regular expression!
    	Pattern p = buildPatternFromExpression(e.expression);
    	Matcher m = p.matcher(message);
    	if(!m.find()) return false; // No valid match
    	
    	// Ok, lastly, let's log the message.
    	logger.log(jsLogLevel, message);
    	
    	return true;
    }
    
    private static Pattern buildPatternFromExpression(String exp){
    	// First, strip off flags
    	String flags = exp.substring(exp.lastIndexOf("/")+1);
    	String p = exp.substring(0, exp.lastIndexOf("/")+1);

    	// Now, get int value for flags
    	ArrayList<String> arrayListFlags = new ArrayList<String>();
    	for(String f : flags.split("")){
    		arrayListFlags.add(f);
    	}
    	int flagsInt = PreParser.convertFlagsToConstants(arrayListFlags);

    	// Finish strong
    	return Pattern.compile(p, flagsInt);
    }
}
