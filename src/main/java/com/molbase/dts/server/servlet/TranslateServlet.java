package com.molbase.dts.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.molbase.dts.server.cache.CacheObjMetaData;

/**
 * 翻译接口测试页面请求处理
 */
@WebServlet(name="/TranslateServlet" , urlPatterns = { "/translate.htm" })
public class TranslateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TranslateServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(null!=request.getParameter("do")){
			CacheObjMetaData.load();
		}
		request.getRequestDispatcher("WEB-INF/view/search/translate.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
