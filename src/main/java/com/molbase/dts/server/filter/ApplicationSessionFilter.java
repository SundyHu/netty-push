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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.molbase.dts.server.constant.ServerConstants;

/**
 * 登录状态验证
 */
@WebFilter(
	filterName = "ApplicationSessionFilter", 
	dispatcherTypes = { DispatcherType.REQUEST }, 
	urlPatterns = { "/*" })
public class ApplicationSessionFilter implements Filter,ServerConstants {

	public ApplicationSessionFilter() {

	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURL = httpRequest.getRequestURI().toString();
		String contextPath = request.getServletContext().getContextPath();
		if (requestURL.startsWith(contextPath.concat("/static"))
				|| requestURL.startsWith(contextPath.concat("/login"))
				|| requestURL.startsWith(contextPath.concat("/api"))) {
			chain.doFilter(request, response);
		} else {
			// 登录状态验证
			HttpSession session = httpRequest.getSession();
			JSONObject user = (JSONObject) session.getAttribute(SESSION_CURRENT_USER);
			if (null != user && Integer.parseInt(user.get("user_id").toString()) > 0) {
				// 已经登录，做权限审核验证
				chain.doFilter(request, response);
			} else {
				httpResponse.sendRedirect(contextPath+"/login.htm");
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}

}
