package com.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerUtil {

	public static Object deserialize(String str){
		Object obj =  JSON.parse(str);
		return obj;
	}
	
	public static String serialize(Object obj){
		try{
			return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteMapNullValue);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object deserializeArray(String str){
		Object obj =  JSONArray.parseArray(str,JSONArray.class);
		return obj;
	}
	
	public static String serializeArray(Object obj){
		return JSONArray.toJSONString(obj);
	}
	
}
