package com.msgs;

public class ReqMsg extends BaseMsg {
	private static final long serialVersionUID = 1L;
	private String reqId;
	public ReqMsg() {
        super();
        setType(MsgType.ASK);
    }
	
    public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}


	private Params params;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
