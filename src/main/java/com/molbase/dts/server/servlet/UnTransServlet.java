package com.molbase.dts.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.service.UnTransService;
import com.molbase.dts.util.ResponseJSONUtil;

/**
 * 未翻译成功接口调用请求记录查看
 */
@WebServlet(name="/UnTransServlet" , urlPatterns = { "/untrans.htm" })
public class UnTransServlet extends HttpServlet implements ServerConstants {
	private static final long serialVersionUID = -1644082331384268877L;

	static Logger logger = Logger.getLogger(UnTransServlet.class);
	
	UnTransService unTransService;
	
	public UnTransServlet() {
        super();
        unTransService = new UnTransService();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("WEB-INF/view/search/untrans.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer page = 1;
		try{
			page = Integer.parseInt(request.getParameter("page"));
		}catch(NumberFormatException nfe){}
		ResponseJSONUtil.doWriteJSON(response,this.unTransService.pageQueryUntrans(page, PAGE_SIZE));
	}

}
