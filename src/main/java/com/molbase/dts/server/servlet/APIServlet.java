package com.molbase.dts.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dto.APIResultObject;
import com.molbase.dts.server.dto.DTSBusinessMapDto;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSProcessLogDto;
import com.molbase.dts.server.dto.TupleObjectMetaType;
import com.molbase.dts.server.service.APIService;
import com.molbase.dts.util.JSONMapUtil;
import com.molbase.dts.util.ResponseJSONUtil;
import com.molbase.dts.util.StringUtil;

/**
 * 对外发布接口主控制Servlet
 * @author changgen.xu
 * 2015年11月26日 上午11:31:36
 */
@WebServlet(name="APIServlet", urlPatterns="/api/index.htm")
//example:/api/index.htm?action=translate&metaTypeStr=user@customer&sourceId=12
public class APIServlet extends HttpServlet implements ServerConstants {

	private static final long serialVersionUID = 8463887527315512249L;

	static Logger logger = Logger.getLogger(APIServlet.class);
	
	APIService apiService;
	
    public APIServlet() {
        super();
        logger.info("APIServlet constructed!");
        apiService = new APIService();
    }

    /**
     * 开发调试时允许get请求，上线后只能post调用API
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);
	}

	/**
	 * 接口逻辑分发控制
	 * action:1,u-c;2,c-f;3,g-p
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (null==request.getParameter("action")?"":request.getParameter("action")) {
		case "translate":
			this.translate(request, response);
			break;
		case "translateAll":
			this.translateAll(request, response);
			break;
		case "bizmap": //uc biz bind
			this.bizMapSave(request, response);
			break;
		case "bizload": //uc biz find
			this.bizMapQuery(request, response);
			break;
		case "bizdel": //uc biz delete
			this.bizMapDel(request, response);
			break;
		case "bizactive": //uc finance biz active
			this.bizMapFinanceActive(request, response);
			break;
		default:
			this.badParametes(response);
			break;
		}
	}
	
	/**
	 * action=translateAll
	 * params=JSON_STR
	 * @param request
	 * @param response
	 * return:
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void translateAll(HttpServletRequest request, HttpServletResponse response){
		String params_json = request.getParameter("params");
		if(null==params_json){
			ResponseJSONUtil.doWriteJSON(response,"[]");
			return;
		}
		try{
			//parseRequest params
			List<LinkedHashMap> params = JSON.parseArray(params_json, LinkedHashMap.class);
			List<DTSProcessLogDto> dtoList = new ArrayList<DTSProcessLogDto>();
			//doTranslate
			for(LinkedHashMap<String,Object> item : params){
				TupleObjectMetaType tupleMetaType = CacheObjMetaData.parseObjectMetaTypeMap(item.get("metaTypeStr").toString());
				DTSObjectDataDto targetDataDto = apiService.doTranslate(tupleMetaType.getMetaType1(), item.get("sourceId").toString(), tupleMetaType.getMetaType2());
				DTSProcessLogDto logDto = new DTSProcessLogDto();
				if(null == targetDataDto){//翻译失败，无对应的绑定关系
					item.put("targetId","");
					item.put("object_data",new Object());
					logDto.setType(LOG_TYPE_TRANSFAIL);
					logDto.setContent(String.format(TRANSLATE_FAILURE, tupleMetaType.getMetaType1(), tupleMetaType.getMetaType2(), item.get("sourceId").toString()));
				}else{//翻译成功，查询到绑定关系
					item.put("targetId",targetDataDto.getName1());
					item.put("object_data", JSONMapUtil.deserializeLinkedHashMap(targetDataDto.getContent()));
					logDto.setType(LOG_TYPE_TRANSSUCCESS);
					logDto.setContent(String.format(TRANSLATE_SUCCESS, tupleMetaType.getMetaType1(), tupleMetaType.getMetaType2(), item.get("sourceId").toString()));
				}
				dtoList.add(logDto);
			}
			//doResponse
			ResponseJSONUtil.doWriteJSON(response,params);
			//log
			for(DTSProcessLogDto logDto:dtoList){
				apiService.logInsert(logDto);
			}
		}catch(Exception e){
			logger.error("严重错误：translateAll错误，params:"+params_json+",错误信息:"+e.getMessage());
			ResponseJSONUtil.doWriteJSON(response,"[]");
		}
	}
	
	protected void translate(HttpServletRequest request, HttpServletResponse response){
		TupleObjectMetaType tupleMetaType = CacheObjMetaData.parseObjectMetaTypeMap(request.getParameter("metaTypeStr"));
		if(null==tupleMetaType || null==request.getParameter("sourceId")){
			this.badParametes(response);
		}else{//调用service翻译逻辑
			DTSObjectDataDto targetDataDto = apiService.doTranslate(tupleMetaType.getMetaType1(), request.getParameter("sourceId"), tupleMetaType.getMetaType2());
			JSONObject result = new JSONObject();
			result.put("sourceId", request.getParameter("sourceId"));
			DTSProcessLogDto logDto = new DTSProcessLogDto();
			if(null == targetDataDto){//翻译失败，无对应的绑定关系
				result.put("targetId","");
				result.put("targetData",new Object());
				logDto.setType(LOG_TYPE_TRANSFAIL);
				logDto.setContent(String.format(TRANSLATE_FAILURE, tupleMetaType.getMetaType1(), tupleMetaType.getMetaType2(), request.getParameter("sourceId")));
			}else{//翻译成功，查询到绑定关系
				result.put("targetId",targetDataDto.getName1());
				result.put("targetData", JSONMapUtil.deserializeLinkedHashMap(targetDataDto.getContent()));
				logDto.setType(LOG_TYPE_TRANSSUCCESS);
				logDto.setContent(String.format(TRANSLATE_SUCCESS, tupleMetaType.getMetaType1(), tupleMetaType.getMetaType2(), request.getParameter("sourceId")));
			}
			ResponseJSONUtil.doWriteJSON(response,new APIResultObject(RET_STATUS_SUCESS,result));
			//将本次翻译操作记录LOG中
			apiService.logInsert(logDto);
		}
	}
	
	protected void badParametes(HttpServletResponse response){
		ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "请求参数[action]错误！"));
	}
	
	/**
	 * bizMap关系数据保存
	 * @param request
	 * @param response
	 */
	protected void bizMapSave(HttpServletRequest request, HttpServletResponse response){
		DTSBusinessMapDto dto = new DTSBusinessMapDto(true);
		
		String bizId = request.getParameter("bizId"); //必要参数，来源哪项业务
		
		String userId = request.getParameter("userId");
		String userName = null==request.getParameter("userName")?"":request.getParameter("userName");
		
		String billId = request.getParameter("billId");
		String billName = null==request.getParameter("billName")?"":request.getParameter("billName");
		
		String customerId = request.getParameter("customerId");
		String customerName = null==request.getParameter("customerName")?"":request.getParameter("customerName");
		
		//判断必要请求参数必须有
		if(null==bizId || null==userId || null==billId || null==customerId){
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "缺少必要请求参数！"));
			return;
		}
		try{
			dto.setBizId(Integer.parseInt(bizId));
			dto.setUserId(Integer.parseInt(userId));
			dto.setUserName(userName);
			dto.setBillId(billId);
			dto.setBillName(billName);
			dto.setCustomerId(Integer.parseInt(customerId));
			dto.setCustomerName(customerName);
			//dto.setOperator(String.format(BIZ_API_OPERATOR, bizId, SDF_YMD.format(new Date()))); //接口产生的biz数据操作人:API_bizId_YYYYMMDD
			dto.setOperator("API");
		}catch(Exception e){
			logger.error("bizMapSave参数转换错误:"+e.getMessage());
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "请求参数格式错误！"));
			return;
		}
		APIResultObject result = apiService.insertBizMapDto(dto);
		ResponseJSONUtil.doWriteJSON(response, result);
	}
	
	/**
	 * 请求翻译关系
	 * @param request
	 * @param response
	 */
	protected void bizMapQuery(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> params = new HashMap<String,Object>();
		String userId = request.getParameter("userId");
		String bizId = request.getParameter("bizId");
		String customerId = request.getParameter("customerId");
		if(null==userId && null==bizId && null==customerId){
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "缺少必要查询参数或参数值！"));
			return;
		}
		if((StringUtil.isEmpty(userId) && !StringUtil.isEmpty(bizId) && StringUtil.isEmpty(customerId)) || (StringUtil.isEmpty(customerId) && !StringUtil.isEmpty(bizId) && StringUtil.isEmpty(userId))){
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "缺少必要查询参数或参数值！"));
			return;
		}
		//参数IS OK
		if(!StringUtil.isEmpty(userId)) params.put("userId", userId);
		if(!StringUtil.isEmpty(bizId)) params.put("bizId", bizId);
		if(!StringUtil.isEmpty(customerId)) params.put("customerId", customerId);
		ResponseJSONUtil.doWriteJSON(response, apiService.loadBizMapData(params));
	}
	
	/**
	 * biz绑定关系作废
	 * @param request
	 * @param response
	 */
	protected void bizMapDel(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> params = new HashMap<String,Object>();
		String bizId = request.getParameter("bizId");
		String billId = request.getParameter("billId");
		String userId = request.getParameter("userId");
		if(null==bizId || (null==bizId && null==userId)){
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "缺少必要参数！"));
			return;
		}
		params.put("bizId", bizId);
		if(null!=billId){params.put("billId", billId);}
		if(null!=userId){params.put("userId", userId);}
		ResponseJSONUtil.doWriteJSON(response, apiService.delBizMapData(params));
	}
	
	/**
	 * 金融biz激活
	 * @param request
	 * @param response
	 */
	protected void bizMapFinanceActive(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> params = new HashMap<String,Object>();
		String bizId = request.getParameter("bizId");
		String billId = request.getParameter("billId");
		if(null==bizId || null==billId){
			ResponseJSONUtil.doWriteJSON(response, new APIResultObject(RET_STATUS_NOT_FOUND, "缺少必要参数！"));
			return;
		}
		params.put("bizId", bizId);
		params.put("billId", billId);
		ResponseJSONUtil.doWriteJSON(response, apiService.bizMapActive(params));
	}
}
