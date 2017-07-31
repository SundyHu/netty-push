package com.molbase.dts.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

@WebFilter(
	urlPatterns = { "/*" }, 
	initParams = { 
		@WebInitParam(name="encoding", value = "UTF-8", description = "编码"),
})
public class ApplicationEncodingFilter implements Filter {

	FilterConfig filterConfig;

    public ApplicationEncodingFilter() {
    	super();
    }

    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
		response.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
		String request_uri = ((HttpServletRequest)request).getRequestURI();
		if(request_uri.endsWith("index.htm")){
			request.setAttribute("currentView", "index_htm");
		}else if(request_uri.endsWith("search.htm")){
			request.setAttribute("currentView", request.getParameter("view")+"_htm");
		}else if(request_uri.endsWith("translate.htm")){
			request.setAttribute("currentView", "translate_htm");
		}else if(request_uri.endsWith("untrans.htm")){
			request.setAttribute("currentView", "untrans_htm");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig initConfig) throws ServletException {
		this.filterConfig = initConfig;
	}

	@Override
	public void destroy() {
		
	}

}
