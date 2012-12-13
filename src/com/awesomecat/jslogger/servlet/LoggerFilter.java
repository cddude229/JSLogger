package com.awesomecat.jslogger.servlet;

import java.io.IOException;

import javax.servlet.*;

import com.awesomecat.jslogger.JavaScriptLogger;



public final class LoggerFilter implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("LoggerFilter accessed");
		if("1".equals(request.getParameter("jsLogger"))){

			// Ok, write to our log file
			boolean out = JavaScriptLogger.handleLogging(request);

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