package com.msgs;

import java.io.Serializable;

public class Params implements Serializable {
    private static final long serialVersionUID = 1L;
    private String jsonStr;
    
	public String getJsonStr() {
		return jsonStr;
	}
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}
}
