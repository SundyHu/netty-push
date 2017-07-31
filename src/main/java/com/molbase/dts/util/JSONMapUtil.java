package com.molbase.dts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class JSONMapUtil {

	static Logger logger = Logger.getLogger(JSONMapUtil.class);
	
	@SuppressWarnings("rawtypes")
	public static List<HashMap> deserializeListHashMap(String str){
		List<HashMap> resultList = null;
		try{
			logger.debug("deserialize JSON Array:"+str);
			resultList = JSON.parseArray(str, HashMap.class);
		}catch(Exception e){
			logger.error("JSONMapUtil.deserializeHashMapList反序列化错误:"+e.getMessage());
			resultList = new ArrayList<HashMap>();
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> deserializeLinkedHashMap(String str){
		LinkedHashMap<String, Object> result = null;
		try{
			logger.debug("deserialize JSON Object:"+str);
			result = JSON.parseObject(str, LinkedHashMap.class);
		}catch(Exception e){
			logger.error("JSONMapUtil.deserializeLinkedHashMap反序列化错误:"+e.getMessage());
			result = new LinkedHashMap<String, Object>();
		}
		return result;
	}
}
