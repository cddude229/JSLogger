package com.awesomecat.jslogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.awesomecat.jslogger.mapper.SessionMapper;
import com.awesomecat.jslogger.mapper.StaticMapper;
import com.awesomecat.jslogger.preparser.JavaScriptFilePreParser;
import com.awesomecat.jslogger.preparser.PreParserHelper;
import com.awesomecat.jslogger.storage.AbstractStore;
import com.awesomecat.jslogger.storage.Expression;
import com.awesomecat.jslogger.storage.HashMapStore;
import com.awesomecat.jslogger.storage.SQLiteStore;
import com.awesomecat.jslogger.storage.SessionType;

public class JavaScriptLogger {
    private static Logger logger = Logger.getLogger("jslogger");
    private static AbstractStore store = (getConfig().getBoolean("persistData")?new SQLiteStore():new HashMapStore());
    private static Level jsLogLevel = Level.ERROR;
    private static Configuration config = null;
    private static RateLimiter rateLimiter = new RateLimiter();

    public static void main(String[] args) throws IOException {
    	// This is how we create static files on the fly
    	if(args.length != 3){
    		System.out.println(String.format("Not enough arguments. Given %s, need 3", args.length));
    		return;
    	}
    	
    	if(!args[0].equals("--staticfile")){
    		System.out.println("Currently only staticfile generation is supported.");
    		return;
    	}
    	
    	File inFile = new File(args[1]), outFile = new File(args[2]);
    	if(inFile.exists() == false){
    		System.out.println("In file does not exist.");
    		return;
    	}
    	
    	if(inFile.isDirectory()){
    		System.out.println("In file can not be a directory.");
    		return;
    	}
    	
    	if(outFile.isDirectory()){
    		System.out.println("Out file can not be a directory.");
    		return;
    	}
    	
    	// Ok, now we can make a static file :P
    	String result = JavaScriptFilePreParser.evaluateFile(inFile, new StaticMapper(store));
    	if(outFile.exists()){
    		// If the file exists already, delete it first
    		outFile.delete();
    	}
		outFile.createNewFile();
    	PrintWriter out = new PrintWriter(outFile);
    	out.write(result);
    	out.flush();
    	out.close();
    }
    
    public static Configuration getConfig() throws RuntimeException {
    	if(config == null){
    		try {
    			config = new XMLConfiguration("jsLoggerConfiguration.xml");
    		} catch(Exception e){
    			throw new RuntimeException("Unable to load configuration.");
    		}
    	}
    	return config;
    }
    
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
    	
    	// Check if the session ID is currently rate limited
    	if(rateLimiter.isUserLimited(sessionId)) return false;
    	
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
    	rateLimiter.addData(sessionId, 1, message.length());
    	
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
    	int flagsInt = PreParserHelper.convertFlagsToConstants(arrayListFlags);

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
