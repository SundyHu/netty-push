package com.molbase.dts.server.dto;

import java.util.Date;

/**
 * dts.broker
 * @author changgen.xu
 */
public class DTSBrokerDto {

	private Integer id;
	private String name;
	private String dataType;
	private String desc;
	private String content;
	private Date createTime;
	private Date lastUpdateTime;
	
	public DTSBrokerDto(){}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "DTSBrokerDto [id=" + id + ", name=" + name + ", dataType="
				+ dataType + ", desc=" + desc + ", content=" + content+ ", createTime=" + createTime
				+ ", lastUpdateTime=" + lastUpdateTime + "]";
	}
}
