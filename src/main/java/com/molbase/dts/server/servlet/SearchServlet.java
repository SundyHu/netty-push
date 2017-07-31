package com.molbase.dts.server.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.TupleObjectMetaType;
import com.molbase.dts.server.service.SearchService;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.util.ResponseJSONUtil;

/**
 * @author changgen.xu
 * 搜索页Web逻辑处理
 */
@WebServlet(description = "搜索页逻辑控制", urlPatterns = { "/search.htm" })
public class SearchServlet extends HttpServlet implements ServerConstants {
	private static final long serialVersionUID = -596890151688876234L;

	static Logger logger = Logger.getLogger(SearchServlet.class);
	
	SearchService searchService;
	
    public SearchServlet() {
        super();
        logger.info("SearchServlet constructed!");
        searchService = new SearchService();
    }

	/**
	 * Get请求根据请求参数分发到三个不同的search绑定页面
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String view = null==request.getParameter("view")?"":request.getParameter("view");
    	if(SEARCH_VIEW_SET.contains(view)){
    		request.getRequestDispatcher("WEB-INF/view/search/"+view+".jsp").forward(request, response);
		}else{
			response.sendRedirect("/dts/static/html/404.html");
		}
	}
    
    /**
     * 绑定关系时候搜索结果，每次最多返回10条
     * @param request
     * @param metaType
     */
    private void loadQueryNoBindObject(HttpServletRequest request, HttpServletResponse response, ObjectMetaType metaType){
    	final String query_id = request.getParameter("object_id");
    	final String query_name = request.getParameter("object_name");
    	Map<String,Object> params = new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;{
			put("name1", query_id);
			put("name2", query_name);
		}};
		if(null!=request.getParameter("store_name")){
			params.put("store_name", request.getParameter("store_name"));
		}
		if(null!=request.getParameter("target_type")){
			params.put("targetType", request.getParameter("target_type"));
		}
		//分页参数
		int pageIndex = null==request.getParameter("page")?1:Integer.valueOf(request.getParameter("page"));
		params.put("size",PAGE_SIZE);//默认10条
		params.put("start", (pageIndex-1) * PAGE_SIZE);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", PAGE_SIZE);
    	Pagination<DTSObjectDataDto> pagination = this.searchService.queryObjectByMetaType(metaType,params);
    	ResponseJSONUtil.doWriteJSON(response,pagination);
    }
    
	/**
	 * Post请求处理:
	 * 1.搜索页Ajax数据交互逻辑
	 * 2.数据搜索请求
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//必要参数：action标识本次请求动作
    	switch (null==request.getParameter("action")?"":request.getParameter("action")) {
		case "bindedSearch":
			this.bindedSearch(request, response);
			break;
		case "noBindSearch":
			this.noBindSearch(request, response);
			break;
		case "doBind":
			this.objectBind(request, response);
			break;
		case "deleteMap":
			this.doDelete(request, response);
			break;
		case "findMapExists":
			this.findMapExists(request, response);
			break;
		case "orderFind":
			this.findGoodsByOrder(request, response);
			break;
		case "operList":
			this.loadOperList(request, response);
			break;
		default:
			response.sendRedirect("/static/html/404.html");
			break;
		}
		
	}
    
    /**
     * 已经绑定的数据查询
     * @param request
     * @param response
     * response JSON
     */
    protected void bindedSearch(HttpServletRequest request, HttpServletResponse response){
    	ObjectMetaType sourceType = ObjectMetaType.contain(request.getParameter("source_type"));
    	if(null==sourceType){ //必要参数，source_type
    		ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
    	}else{ //target_type参数可有可无
    		Map<String,Object> params = new HashMap<String,Object>();
    		params.put("sourceType", sourceType.getMetaType());
    		params.put("id", request.getParameter("query_bind_id"));
    		params.put("name", request.getParameter("query_bind_name"));
			params.put("operator",
					(null == request.getParameter("operator") || "0".equals(request.getParameter("operator"))) ? ""
							: request.getParameter("operator"));
    		ObjectMetaType targetType = ObjectMetaType.contain(request.getParameter("target_type"));
    		if(null!=targetType) params.put("targetType", targetType.getMetaType());
    		//分页参数
    		int pageIndex = null==request.getParameter("page")?1:Integer.valueOf(request.getParameter("page"));
    		params.put("size",PAGE_SIZE);//默认10条
    		params.put("start", (pageIndex-1) * PAGE_SIZE);
        	ResponseJSONUtil.doWriteJSON(response,this.searchService.findMapByMetaTypes(pageIndex,PAGE_SIZE,params));
    	}
    }
    
    /**
     * 执行绑定操作
     * @param request
     * @param response
     * response true:绑定成功，false绑定失败
     */
    protected void objectBind(HttpServletRequest request, HttpServletResponse response){
    	//必要参数：metaTypeStr,source_id,target_id
		TupleObjectMetaType tupleMetaType = CacheObjMetaData.parseObjectMetaTypeMap(request.getParameter("metaTypeStr"));
		String targetId = null==request.getParameter("target_id")?"":request.getParameter("target_id");
		String sourceStr = null==request.getParameter("source_id")?"":request.getParameter("source_id");
		String[] sourceId = null;
		if(sourceStr.indexOf(",")>-1){
			sourceId = sourceStr.split(",");
		}else{
			sourceId = new String[]{sourceStr};
		}
		if(null==tupleMetaType || "".equals(sourceStr) || "".equals(targetId)){
			ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
			return;
		}
		JSONObject user = (JSONObject) request.getSession().getAttribute(SESSION_CURRENT_USER);
		boolean result = this.searchService.doDataFastBind(
				tupleMetaType.getMetaType1(), sourceId,
				tupleMetaType.getMetaType2(), targetId, null==user?"管理员":user.get("user_name").toString());
		ResponseJSONUtil.doWriteJSON(response, result == true ? JSON_200_RESULT
				: JSON_500_RESULT);
    }
    
    /**
     * 处理未绑定关系的数据搜索请求
     * @param request
     * @param response
     * response 搜搜结果JOSN
     */
    protected void noBindSearch(HttpServletRequest request, HttpServletResponse response){
    	//必要参数：metaType
    	ObjectMetaType metaType = ObjectMetaType.contain(request.getParameter("metaType"));
    	if(null!=metaType){
    		this.loadQueryNoBindObject(request,response,metaType);
    	}else{
    		ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
    	}
    }
    

    /**
     * 处理search页面已经绑定的关系删除请求
     * Delete方式请求
     */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer mapId = null==request.getParameter("mapId")?0:Integer.parseInt(request.getParameter("mapId"));
		if(mapId>0){
			JSONObject user = (JSONObject) request.getSession().getAttribute(SESSION_CURRENT_USER);
			boolean result = this.searchService.deleteMapData(mapId,null==user?"管理员":user.get("user_name").toString());
			ResponseJSONUtil.doWriteJSON(response, result==true?JSON_200_RESULT:JSON_500_RESULT);
		}else{
			ResponseJSONUtil.doWriteJSON(response,JSON_403_RESULT);
		}
	}

	/**
	 * 搜索页绑定前先判断是否已经存在了！
	 * @param request
	 * @param response
	 * true:存在了，false:不存在
	 */
	protected void findMapExists(HttpServletRequest request, HttpServletResponse response){
		TupleObjectMetaType tupleMetaType = CacheObjMetaData.parseObjectMetaTypeMap(request.getParameter("metaTypeStr"));
		String sourceId = null==request.getParameter("source_id")?"":request.getParameter("source_id");
		if(null==tupleMetaType || "".equals(sourceId)){
			ResponseJSONUtil.doWriteJSON(response,true);
		}else{
			boolean result = searchService.findMapExists(tupleMetaType.getMetaType1(), sourceId, tupleMetaType.getMetaType2());
			ResponseJSONUtil.doWriteJSON(response,result);
		}
	}
	
	/**
	 * 根据订单号，拉取OMS的订单详情产品列表
	 * @param request
	 * @param response
	 */
	protected void findGoodsByOrder(HttpServletRequest request, HttpServletResponse response){
		String ordersn = null==request.getParameter("ordersn")?"":request.getParameter("ordersn");
		if("".equals(ordersn)){
			ResponseJSONUtil.doWriteJSON(response,new String[]{});
		}else{
			List<LinkedHashMap> list = searchService.loadOrderProductList(ordersn);
			ResponseJSONUtil.doWriteJSON(response,list);
		}
	}
	
	/**
	 * 加载当前map中所有的操作人记录
	 * @return
	 */
	protected void loadOperList(HttpServletRequest request, HttpServletResponse response){
		ResponseJSONUtil.doWriteJSON(response, searchService.loadMapDistinctOperaotr());
	}
}
