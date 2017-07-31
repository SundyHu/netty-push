package com.molbase.dts.util;

public class UnitConvertUtil {

	public static String convert(String val){
		switch (val) {
			case "01":
				return "吨";
			case "02":
				return "千克";
			case "03":
				return "瓶";
			case "04":
				return "桶";
			case "05":
				return "盒";
			case "06":
				return "箱";
			case "07":
				return "包";
			default:
				return "未知";
		}
	}
}
