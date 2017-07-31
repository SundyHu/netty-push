package com.molbase.dts.server.dto;

import java.util.Date;

public class DTSBusinessMapDto {

	private Integer id; //流水ID
	private Integer userId; //会员ID
	private String userName; //会员名
	private Integer bizId; //业务ID
	private String bizName; //业务名称
	private String billId; //其他业务系统的单据ID/编码
	private String billName; //单据名称
	private Integer customerId; //客户ID
	private String customerName; //客户名称
	private Integer status; //状态
	private String operator; //操作人
	private Date validDate;
	private Date invalidDate;
	private Date createTime;
	private Date lastUpdateTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getBizId() {
		return bizId;
	}
	public void setBizId(Integer bizId) {
		this.bizId = bizId;
	}
	public String getBizName() {
		return bizName;
	}
	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBillName() {
		return billName;
	}
	public void setBillName(String billName) {
		this.billName = billName;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Date getValidDate() {
		return validDate;
	}
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}
	public Date getInvalidDate() {
		return invalidDate;
	}
	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	static final String EMPTY_STR = "";
	
	public DTSBusinessMapDto(){}
	public DTSBusinessMapDto(boolean init){
		this();
		setUserName(EMPTY_STR);
		setBillName(EMPTY_STR);
		setCustomerName(EMPTY_STR);
		setOperator(EMPTY_STR);
		setStatus(DTSBusinessMapEnum.ADOPT.status()); //默认创建时都是通过状态
	}
}
