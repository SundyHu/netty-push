package com.molbase.dts.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dto.TupleObjectMetaType;
import com.molbase.dts.server.service.IndexService;
import com.molbase.dts.util.ResponseJSONUtil;

@WebServlet(name="IndexServlet", urlPatterns="/index.htm")
public class IndexServlet extends HttpServlet implements ServerConstants {
	
	private static final long serialVersionUID = -9132120496901862029L;

	static Logger logger = Logger.getLogger(IndexServlet.class);
	
	IndexService indexService;

	public IndexServlet() {
        super();
        logger.info("IndexServlet constructed!");
        indexService = new IndexService();
    }

	/**
	 * Get请转发到首页页面
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("WEB-INF/view/index/index.jsp").forward(request, response);
	}

	/**
	 * Post请求接收ajax数据交互请求
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//必要参数:action,请求类型分发:doBind|hasId
		switch (null==request.getParameter("action")?"null":request.getParameter("action")) {
		case INDEX_ACTION_HASID:
			this.findExistById(request, response);
			break;
		case INDEX_ACTION_DOBIND:
			this.objectBind(request, response);
			break;
		default:
			ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
			break;
		}
	}
	
	/**
	 * 根据ID快速建立绑定关系
	 * @param request
	 * @param response
	 */
	protected void objectBind(HttpServletRequest request, HttpServletResponse response){
		TupleObjectMetaType metaTypeTuple = CacheObjMetaData.parseObjectMetaTypeMap(request.getParameter("metaTypeStr"));
		if(null==metaTypeTuple){
			ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
			return;
		}
		String sourceId = null==request.getParameter("source_id")?"":request.getParameter("source_id");
		String targetId = null==request.getParameter("target_id")?"":request.getParameter("target_id");
		if("".equals(sourceId) || "".equals(targetId)){
			ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
		}else{
			boolean result = indexService.findMapExists(metaTypeTuple.getMetaType1(), sourceId, metaTypeTuple.getMetaType2());
			if(result){ //存在了，别再重复添加绑定关系了
				ResponseJSONUtil.doWriteJSON(response,"binded");
			}else{//不存在，好吧！你绑定吧
				JSONObject user = (JSONObject) request.getSession().getAttribute(SESSION_CURRENT_USER);
				result = indexService.doDataFastBind(metaTypeTuple.getMetaType1(), sourceId, metaTypeTuple.getMetaType2(), targetId, null==user?"管理员":user.get("user_name").toString());
				ResponseJSONUtil.doWriteJSON(response,
						result == true ? JSON_200_RESULT : JSON_500_RESULT);
			}
		}
	}
	
	/**
	 * 根据ID找是否存在!
	 * @param request
	 * @param response
	 * @param findType
	 * response true:存在，false：不存在
	 */
	protected void findExistById(HttpServletRequest request, HttpServletResponse response){
		ObjectMetaType metaType = ObjectMetaType.contain(null==request.getParameter("metaType")?"":request.getParameter("metaType"));
		String id = null==request.getParameter("id")?"":request.getParameter("id");
		if(null!=metaType && !"".equals(id)){
			boolean result = this.indexService.findObjectExists(metaType, id);
			ResponseJSONUtil.doWriteJSON(response, result);
		}else{
			ResponseJSONUtil.doWriteJSON(response, false);
		}
	}
	
}
