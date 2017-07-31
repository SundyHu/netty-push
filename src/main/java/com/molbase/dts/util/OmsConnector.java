package com.molbase.dts.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * @author changgen.xu
 * @date 2016年3月5日 上午10:16:05
 * @desc OMS订单产品详情调用
 * //api88.molbase.com/crm/order/detail?order_sn=1456278297675&cksn=1e2722306c5bd4b498d5037ec3950e77
 */
public class OmsConnector {
	
	static Logger logger = Logger.getLogger(OmsConnector.class);
	
	static final String URL = "http://order.molbase.com/crm/order/detail";
	static final String CKSNKEY = "crm";
	
	private String getCksn(){
		return StandardMD5.getMD5Str(URL+CKSNKEY);
	} 
	
	/**
	 * 根据订单号获取OMS订单详情JSON
	 * @param orderSn
	 * @return JSON string
	 */
	public String callOMSOrderJSON(String orderSn){
		Map<String,String> params = new HashMap<String,String>();
		params.put("order_sn", orderSn);
		params.put("cksn", getCksn());
		logger.info("CALL OMS WITH ORDERNO:"+orderSn);
		return HttpUtil.post(URL, params);
	}
	
	/**
	 * 将返回结果item_list解析成List<LinkedHashMap>
	 * @param orderSn
	 * @param isParse
	 * @return
	 */
	public List<LinkedHashMap> callOMSOrderJSON(String orderSn, boolean isParse){
		String jsonStr = this.callOMSOrderJSON(orderSn);
		JSONObject json = JSONObject.parseObject(jsonStr);
		String done = json.getString("done");
		String retval = json.getString("retval");
		if(StringUtil.isEmpty(done) || !"true".equals(done) || StringUtil.isEmpty(retval) || "false".equals(retval)){
			logger.error("调用订单返回结果有误~~");
		}else{
			//解析itemList
			String itemList = JSONObject.parseObject(retval).getString("item_list");
			return JSONObject.parseArray(itemList, LinkedHashMap.class);
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		String orderSn = "1456743218428";
		List<LinkedHashMap> list = new OmsConnector().callOMSOrderJSON(orderSn,true);
		for(LinkedHashMap m:list){
			System.out.println(m);
		}
	}*/
}
