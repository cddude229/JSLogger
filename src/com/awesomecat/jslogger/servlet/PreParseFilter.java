package com.awesomecat.jslogger.servlet;

import java.io.IOException;

import javax.servlet.*;



public final class PreParseFilter implements Filter {
   
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}