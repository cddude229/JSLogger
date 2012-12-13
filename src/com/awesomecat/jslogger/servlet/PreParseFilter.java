package com.awesomecat.jslogger.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import com.awesomecat.jslogger.JavaScriptFilePreParser;
import com.awesomecat.jslogger.JavaScriptLogger;
import com.awesomecat.jslogger.SessionMapper;



public final class PreParseFilter implements Filter {
   
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("Trying to handle js file");
		HttpServletRequest req = (HttpServletRequest) request;
		String path = getPath(req);

		// See if it's a JS file and then pre-parse
		if(path.length() > 4 && path.substring(path.length()-3).toLowerCase().equals(".js")){
			System.out.println("Handling js file");
			File f = new File(path);
			SessionMapper mapper = JavaScriptLogger.buildSessionMapper(
				JavaScriptLogger.getSessionId(req)
			);
			response.getWriter().write(
				JavaScriptFilePreParser.evaluateFile(f, mapper)
			);
		} else {
			chain.doFilter(request, response);
		}
	}
	
	private String getPath(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();
		return "." + servletPath + (pathInfo == null?"":pathInfo);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}