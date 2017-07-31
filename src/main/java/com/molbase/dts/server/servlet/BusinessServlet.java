package com.molbase.dts.server.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dto.DTSBusinessMapDto;
import com.molbase.dts.server.dto.DTSBusinessMapEnum;
import com.molbase.dts.server.dto.DTSBusinessMetaDto;
import com.molbase.dts.server.service.BusinessService;
import com.molbase.dts.util.ResponseJSONUtil;

/**
 * @author changgen.xu
 * dts v2 business相关后台处理逻辑
 */
@WebServlet(description = "business逻辑控制", urlPatterns = { "/biz.htm" })
public class BusinessServlet extends HttpServlet implements ServerConstants {

	private static final long serialVersionUID = 4933883159833220888L;

	static Logger logger = Logger.getLogger(BusinessServlet.class);
	
	BusinessService businessService;
	
    public BusinessServlet() {
        super();
        logger.info("BusinessServlet constructed!");
        businessService = new BusinessService();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request,response);
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
		case "bizQuery":
			this.doBizListQuery(request, response);
			break;
		case "bizBind":
			this.doBizBind(request, response);
			break;
		case "bizMetaList":
			this.loadBizMetaList(request, response);
			break;
		case "bizMetaListAll":
			this.loadBizMetaListAll(request, response);
			break;
		case "bizMetaFind":
			this.findBizMeta(request, response);
			break;
		case "bizMetaSave":
			this.bizMetaSave(request, response);
			break;
		case "bizStatusChange":
			this.bizStatusChange(request, response);
		default:
			ResponseJSONUtil.doWriteJSON(response,"请求参数错误!");
			break;
		}
	}

    //列表查询
    protected void doBizListQuery(HttpServletRequest request, HttpServletResponse response){
    	Map<String,Object> params = new HashMap<String,Object>();
    	String bizId = request.getParameter("bizId");
    	String status = request.getParameter("status");
    	bizId = (bizId==null || "".equals(bizId))?"0":bizId;
    	status = (status==null || "".equals(status))?"0":status;
		params.put("bizId", Integer.parseInt(bizId));
		params.put("field", request.getParameter("field"));
		params.put("keyword", request.getParameter("keyword"));
		params.put("status", Integer.parseInt(status));
		//分页参数
		int pageIndex = null==request.getParameter("page")?1:Integer.valueOf(request.getParameter("page"));
		params.put("size",PAGE_SIZE);//默认10条
		params.put("start", (pageIndex-1) * PAGE_SIZE);
    	ResponseJSONUtil.doWriteJSON(response,this.businessService.queryBizList(pageIndex,PAGE_SIZE,params));
    }
    
    //数据写入
    protected void doBizBind(HttpServletRequest request, HttpServletResponse response){
    	DTSBusinessMapDto dto = new DTSBusinessMapDto(true);
    	dto.setUserId(Integer.parseInt(request.getParameter("userId")));
    	dto.setUserName(request.getParameter("userName"));
    	dto.setCustomerId(Integer.parseInt(request.getParameter("customerId")));
    	dto.setCustomerName(request.getParameter("customerName"));
    	dto.setBillId(request.getParameter("billId"));
    	dto.setBillName(request.getParameter("billName"));
    	dto.setBizId(Integer.parseInt(request.getParameter("bizId")));
    	dto.setBizName(request.getParameter("bizName"));
    	dto.setStatus(DTSBusinessMapEnum.ADOPT.status());
    	JSONObject user = (JSONObject) request.getSession().getAttribute(SESSION_CURRENT_USER);
    	dto.setOperator(null==user?"管理员":user.get("user_name").toString());
    	int affect = this.businessService.saveBizDto(dto);
    	ResponseJSONUtil.doWriteJSON(response,affect>0?1:0);
    }
    
    protected void loadBizMetaList(HttpServletRequest request, HttpServletResponse response){
    	Map<String,Object> params = new HashMap<String,Object>();
    	int pageIndex = null==request.getParameter("page")?1:Integer.valueOf(request.getParameter("page"));
		params.put("size",PAGE_SIZE);//默认10条
		params.put("start", (pageIndex-1) * PAGE_SIZE);
    	ResponseJSONUtil.doWriteJSON(response,this.businessService.loadBizMetaList(pageIndex,PAGE_SIZE,params));
    }
    
    protected void loadBizMetaListAll(HttpServletRequest request, HttpServletResponse response){
    	ResponseJSONUtil.doWriteJSON(response,this.businessService.loadBizMetaListAll());
    }
    
    protected void findBizMeta(HttpServletRequest request, HttpServletResponse response){
    	int id = 0;
    	try{
    		id = Integer.parseInt(request.getParameter("id"));
    	}catch(NumberFormatException nfe){
    		ResponseJSONUtil.doWriteJSON(response,0); //参数有误，返回0
    		return;
    	}
    	ResponseJSONUtil.doWriteJSON(response,this.businessService.findBizMetaById(id));
    }
    
    protected void bizMetaSave(HttpServletRequest request, HttpServletResponse response){
    	DTSBusinessMetaDto dto = new DTSBusinessMetaDto();
    	try{
    		String id = request.getParameter("id");
    		id = null==id?"0":id;
    		dto.setId(Integer.parseInt(id));
        	dto.setName(request.getParameter("name"));
        	dto.setBind(Integer.parseInt(request.getParameter("bind")));
        	dto.setOverride(Integer.parseInt(request.getParameter("override")));
    	}catch(NumberFormatException nfe){
    		ResponseJSONUtil.doWriteJSON(response,0); //参数有误，返回0
    		return;
    	}
    	JSONObject user = (JSONObject) request.getSession().getAttribute(SESSION_CURRENT_USER);
    	dto.setOperator(null==user?"管理员":user.get("user_name").toString());
    	ResponseJSONUtil.doWriteJSON(response,this.businessService.saveBizMetaInfo(dto));
    }
    
    public void bizStatusChange(HttpServletRequest request, HttpServletResponse response){
    	try{
    		Integer id = Integer.parseInt(request.getParameter("id"));
    		Integer status = Integer.parseInt(request.getParameter("status"));
    		ResponseJSONUtil.doWriteJSON(response,this.businessService.bizStatusChange(id, status));
    	}catch(Exception nfe){
    		ResponseJSONUtil.doWriteJSON(response,0); //参数有误，返回0
    		return;
    	}
    }
}
