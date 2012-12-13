package com.awesomecat.jslogger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
    

    public static boolean handleLogging(ServletRequest request){
    	// No null values allowed
    	if(request == null) return false;

    	// Get message and associatedId. Exit if null
    	String message = request.getParameter("message");
    	String associatedId = request.getParameter("logid");
    	
    	int sessionId = getSessionId(request);
    	
    	return handleLogging(message, associatedId, sessionId);
    }

    public static boolean handleLogging(String message, String associatedId, int sessionId){
        // No null values allowed
    	if(message == null || associatedId == null) return false;
    	
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
   
    public static SessionMapper buildSessionMapper(int sessionId){
    	return new SessionMapper(sessionId, store);
    }

	
	private static final String USERNAME_ATTRIBUTE = "username";
	private static boolean SUPPORT_USERNAMES = false;
    public static int getSessionId(ServletRequest request){
		// Store the IP temporarily as session identifier
		SessionType sessionType = SessionType.IP;
		String sessionValue = request.getRemoteAddr();

		// Attempt to get the user name for session status
		if(SUPPORT_USERNAMES && request instanceof HttpServletRequest){
			HttpServletRequest req = (HttpServletRequest) request;
			Object username = req.getSession().getAttribute(USERNAME_ATTRIBUTE);
			if(username != null){
				sessionType = SessionType.USERNAME;
				sessionValue = username.toString();
			}
		}

		// Return our id
		return getSessionId(sessionType, sessionValue);
    }
    public static int getSessionId(SessionType sessionType, String sessionValue){
		return store.getSessionId(sessionType, sessionValue);	
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

    /**
     * ONLY USED FOR TESTING PURPOSES
     * @return
     */
	public static AbstractStore getStore() {
		return store;
	}
}
