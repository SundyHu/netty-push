package com.molbase.dts.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class ResponseJSONUtil {

	static Logger logger = Logger.getLogger(ResponseJSONUtil.class);
	
	/**
	 * 执行返回结果输出
	 * @param response
	 * @param returnValue
	 */
	public static void doWriteJSON(HttpServletResponse response, Object returnValue){
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");
			out = response.getWriter();
			out.write(JSON.toJSONStringWithDateFormat(returnValue, "yyyy-MM-dd HH:mm:ss"));
		} catch (IOException e) {
			logger.error("doWriteJSON错误:"+e.getMessage());
		}finally{
			out.flush();
			out.close();
			out = null;
		}
	}

}
