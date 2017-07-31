package com.molbase.dts.server.constant;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ServerConstants {

	public final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final Integer RET_STATUS_SUCESS = 200;
	public static final Integer RET_STATUS_NOT_FOUND = 404;
	
	public final String INDEX_ACTION_HASID = "hasId";
	public final String INDEX_ACTION_DOBIND = "doBind";//data bind by ids

	public final String SEARCH_VIEW_UTOC = "utoc";
	public final String SEARCH_VIEW_CTOF = "ctof";
	public final String SEARCH_VIEW_GTOP = "gtop";
	
	public final Set<String> SEARCH_VIEW_SET = new HashSet<String>(Arrays.asList(new String[]{"utoc","ctof","gtop","uclist","bizset","goods"}));
	
//	public static enum ObjectMetaType {
//		user,customer,u8_customer,u8_vendor,molbase_goods,u8_product;
//		
//		public static ObjectMetaType contain(String val){
//			if (user.toString().equals(val)){
//				return user;
//			}else if(customer.toString().equals(val)){
//				return customer;
//			}else if(u8_customer.toString().equals(val)){
//				return u8_customer;
//			}else if(u8_vendor.toString().equals(val)){
//				return u8_vendor;
//			}else if(molbase_goods.toString().equals(val)){
//				return molbase_goods;
//			}else if(u8_product.toString().equals(val)){
//				return u8_product;
//			}else{
//				return null;
//			}
//		}
//
//    }
	
	public final String JSON_403_RESULT = "请求错误！！！[403]";
	public final String JSON_200_RESULT = "处理成功！！！[200]";
	public final String JSON_500_RESULT = "处理失败！！！[500]";
	
	public final Integer LOG_TYPE_BINDMAP = 2;
	public final Integer LOG_TYPE_DELMAP = 4;
	public final Integer LOG_TYPE_TRANSFAIL = 5;
	public final Integer LOG_TYPE_TRANSSUCCESS = 6;
	
	public final String TRANSLATE_SUCCESS = "翻译成功,requestType:%s,targetType:%s,sourceId:%s";
	public final String TRANSLATE_FAILURE = "翻译失败,requestType:%s,targetType:%s,sourceId:%s";
	
	public final Integer PAGE_SIZE = 10;
	public final Integer PAGINATION_LINKNUM = 6;
	
	public final String SESSION_CURRENT_USER = "user";
	
	public final String MOLBASE_GOODS = "molbase_goods";
	public final String U8_PRODUCT = "u8_product";
	
	public final String BIZ_API_OPERATOR = "API_%s_%s";
	
	public final int BIZ_META_BIND_1 = 1; //1:1
	public final int BIZ_META_BIND_N = 2; //1:N
	public final int BIZ_META_OVERRIDE_YES = 1; //可以覆盖
	public final int BIZ_META_OVERRIDE_NO = 2; //不可覆盖
}
