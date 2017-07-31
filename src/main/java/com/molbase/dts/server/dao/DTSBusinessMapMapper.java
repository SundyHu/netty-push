package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import com.molbase.dts.server.dto.DTSBusinessMapAPIDto;
import com.molbase.dts.server.dto.DTSBusinessMapDto;

public interface DTSBusinessMapMapper {

	int insert(DTSBusinessMapDto dto);
	List<Map<String,Object>> queryByParams(Map<String,Object> params);
	Map<String,Object> queryToalbyParams(Map<String,Object> params);
	int statusUpdateById(Map<String,Object> params);
	//1:1时Query
	List<DTSBusinessMapDto> findByBizCustomerOne(DTSBusinessMapDto dto);
	List<DTSBusinessMapDto> findByBizUserCustomer(DTSBusinessMapDto dto);
	int deleteBizMap(DTSBusinessMapDto dto);
	List<DTSBusinessMapAPIDto> apiLoadQuery(Map<String,Object> params);
	List<DTSBusinessMapDto> findByBizBill(Map<String,Object> params);
	List<DTSBusinessMapDto> findByBizUserId(Map<String,Object> params); //同路人
	List<DTSBusinessMapDto> findByBizBillUser(Map<String,Object> params);
	int delByBizBill(Map<String,Object> params);
	int delByBizUser(Map<String,Object> params); //针对同路人
	int delByBizBillUser(Map<String,Object> params);
	int activeByBizBill(Map<String,Object> params);
	//1:1关系激活
	int reActiveBizMap(DTSBusinessMapDto dto);
}
