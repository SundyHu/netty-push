package com.molbase.dts.server.filter;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * 系统接口调用的编码过滤器
 * @author changgen.xu
 * 2015年11月25日 下午6:50:05
 */
@WebFilter(
	dispatcherTypes = {DispatcherType.REQUEST }, 
	urlPatterns = { "/api/*" }, 
	initParams = { 
		@WebInitParam(name="encoding", value = "UTF-8", description = "编码"),
		@WebInitParam(name= "contentType", value="application/json; charset=UTF-8", description="返回内容")
	})
public class APIEncodingSetFilter implements Filter {

	FilterConfig filterConfig;

    public APIEncodingSetFilter() {
    	super();
    }

    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
		response.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
		response.setContentType(this.filterConfig.getInitParameter("contentType"));
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
