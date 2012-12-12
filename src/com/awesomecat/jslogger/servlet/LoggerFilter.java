package com.awesomecat.jslogger.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.awesomecat.jslogger.JavaScriptLogger;
import com.awesomecat.jslogger.storage.SessionType;



public final class LoggerFilter implements Filter {
	//private FilterConfig filterConfig = null;
	
	private static final String USERNAME_ATTRIBUTE = "username";
	private static boolean SUPPORT_USERNAMES = false;

   
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if("1".equals(request.getParameter("jsLogger"))){
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

			// Ok, write to our log file
			boolean out = JavaScriptLogger.handleLogging(request, sessionType, sessionValue);

			response.getWriter().write(String.valueOf(out));
		} else {
			chain.doFilter(request, response);
		}
	}
	public void init(FilterConfig filterConfig) throws ServletException {
		//this.filterConfig = filterConfig;
	}
	@Override
	public void destroy() {}
}