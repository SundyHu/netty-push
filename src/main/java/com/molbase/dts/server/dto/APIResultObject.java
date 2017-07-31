package com.molbase.dts.server.dto;

import java.io.Serializable;

/**
 * API接口返回的结果对象
 * @author changgen.xu
 * 2015年11月26日 下午3:53:02
 */
public class APIResultObject implements Serializable {

	private static final long serialVersionUID = -1408824297325865656L;
	
	private Integer status;
	private Object data;
	
	public APIResultObject(){}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setSuccess(){
		this.status = 200;
		this.data = "操作成功！";
	}
	
	public APIResultObject(Integer status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}

	@Override
	public String toString() {
		return "APIResultObject [status=" + status + ", data=" + data + "]";
	}
	
}
