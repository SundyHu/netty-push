package com.molbase.dts.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.util.MD5;

@WebServlet(name="LoginServlet", urlPatterns="/login.htm")
public class LoginServlet extends HttpServlet implements ServerConstants {

	private static final long serialVersionUID = -1655187487386804263L;
	
	static Logger logger = Logger.getLogger(LoginServlet.class);

	static final String MD5_USER_KEY = "697958599b2e6e0582f9de891cc9579c";
	static final String OA_AUTH_URL = "http://oa.molbase.org/oa/task/checkLogin";
	
	public LoginServlet() {
		super();
		logger.info("LoginServlet constructed!");
    }

	/**
	 * Get跳转登录页面
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		request.getRequestDispatcher("WEB-INF/view/base/login.jsp").forward(request, response);
	}

	/**
	 * Post处理登录操作
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(null==request.getParameter("code") || null==request.getParameter("pw")){
			request.setAttribute("errorMsg", "身份验证失败!");
			doGet(request,response);
		}
		JSONObject authUser = doUserAuth(request.getParameter("code"),request.getParameter("pw"));
		if(null!=authUser && authUser.containsKey("user_id")){//验证成功
			request.getSession().setAttribute(SESSION_CURRENT_USER, authUser);
			response.sendRedirect(request.getContextPath()+"/index.htm");//跳转首页
		}else{
			request.setAttribute("errorMsg", "身份验证失败!");
			doGet(request,response);
		}
	}
	
	protected static JSONObject doUserAuth(String loginCode, String password){
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(OA_AUTH_URL);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("login_code",loginCode));
		formparams.add(new BasicNameValuePair("password",password));
		String timestamp = String.valueOf(System.currentTimeMillis());
		formparams.add(new BasicNameValuePair("timestamp", timestamp));
		String check_key = MD5.getMD5Str(MD5_USER_KEY+loginCode+timestamp);
		formparams.add(new BasicNameValuePair("check_key", check_key));
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(formparams,"utf-8");
			httpPost.setEntity(entity);
			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				String response = EntityUtils.toString(httpEntity, "utf-8");
				logger.info("调用OA身份验证接口返回结果:"+response);
				return JSON.parseObject(response);
			}
		} catch (Exception e) {
			logger.error("调用OA接口验证身份错误："+e.getMessage());
		}finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
				logger.error("调用OA接口验证身份错误："+e.getMessage());
			}
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		JSONObject ss = doUserAuth("changgen.xu","molbase");
		System.out.println(ss.get("user_id"));
	}*/
}
