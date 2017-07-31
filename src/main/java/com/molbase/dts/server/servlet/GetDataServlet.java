package com.molbase.dts.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.molbase.dts.server.netty.Ask;
import com.molbase.dts.server.netty.AskImp;


@WebServlet(name="GetDataServlet", urlPatterns="/broker/get")
public class GetDataServlet extends HttpServlet {

	private static final long serialVersionUID = 8746129039913496015L;
	
	static Logger logger = Logger.getLogger(GetDataServlet.class);
	
	Ask ask;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetDataServlet() {
        super();
        ask=new AskImp();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		String clientid=request.getParameter("clientid");
		String sqlJson=request.getParameter("sqlexp");
		String resJson=ask.req(clientid,sqlJson);
		PrintWriter out = response.getWriter();
		out.print(resJson);
		out.flush();
		out.close();
		out = null;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
}
