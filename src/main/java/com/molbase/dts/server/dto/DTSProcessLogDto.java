package com.molbase.dts.server.dto;

import java.util.Date;

import com.molbase.dts.server.constant.ServerConstants;

/**
 * dts.process_log
 * @author changgen.xu
 *
 */
public class DTSProcessLogDto implements ServerConstants {

	private Integer id;
	private Integer type;
	private String content;
	private Date createTime;
	private Date lastUpdateTime;
	
	public DTSProcessLogDto(){}
	
	public DTSProcessLogDto(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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

	@Override
	public String toString() {
		return "DTSProcessLogDto [id=" + id + ", type=" + type + ", content="
				+ content + ", createTime=" + SDF_FULL.format(createTime) + ", lastUpdateTime="
				+ SDF_FULL.format(lastUpdateTime) + "]";
	}
	
}
