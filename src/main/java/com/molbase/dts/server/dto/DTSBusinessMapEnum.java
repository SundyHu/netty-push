package com.molbase.dts.server.dto;

public enum DTSBusinessMapEnum {

	ADOPT(1, "通过"), ACTIVATION(2, "激活"), UNUSED(3, "作废"), OVERDEL(4,"被覆盖删除");

	private int status;
	private String desc;

	private DTSBusinessMapEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}
	private DTSBusinessMapEnum(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status + "";
	}
	public int status() {
		return status;
	}
	public String desc() {
		return desc;
	}
	public static boolean hasValue(int status){
		return status>0 && status<4 ? true:false;
	}
}
