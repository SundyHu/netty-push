package com.molbase.dts.server.dto;

import java.util.Date;

import com.molbase.dts.server.constant.ServerConstants;

/**
 * dts.object_map
 * @author changgen.xu
 */
public class DTSObjectMapDto implements ServerConstants {

	private Integer id;
	private String sourceType;
	private String sourceObjectId;
	private Integer sourceObjectDataId;
	private String targetType;
	private String targetObjectId;
	private Integer targetObjectDataId;
	private String operUser;
	private Date createTime;
	private Date lastUpdateTime;
	
	public DTSObjectMapDto(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceObjectId() {
		return sourceObjectId;
	}

	public void setSourceObjectId(String sourceObjectId) {
		this.sourceObjectId = sourceObjectId;
	}

	public Integer getSourceObjectDataId() {
		return sourceObjectDataId;
	}

	public void setSourceObjectDataId(Integer sourceObjectDataId) {
		this.sourceObjectDataId = sourceObjectDataId;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetObjectId() {
		return targetObjectId;
	}

	public void setTargetObjectId(String targetObjectId) {
		this.targetObjectId = targetObjectId;
	}

	public Integer getTargetObjectDataId() {
		return targetObjectDataId;
	}

	public void setTargetObjectDataId(Integer targetObjectDataId) {
		this.targetObjectDataId = targetObjectDataId;
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
		return "DTSObjectMapDto [id=" + id + ", sourceType=" + sourceType
				+ ", sourceObjectId=" + sourceObjectId
				+ ", sourceObjectDataId=" + sourceObjectDataId
				+ ", targetType=" + targetType + ", targetObjectId="
				+ targetObjectId + ", targetObjectDataId=" + targetObjectDataId
				+ ", operUser=" + operUser + ", createTime=" + createTime
				+ ", lastUpdateTime=" + lastUpdateTime + "]";
	}

	public DTSObjectMapDto(String sourceType, String sourceObjectId, String targetType) {
		super();
		this.sourceType = sourceType;
		this.sourceObjectId = sourceObjectId;
		this.targetType = targetType;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	/*public DTSObjectMapDto(String sourceType, String sourceObjectId,
			String targetType, String targetObjectId) {
		super();
		this.sourceType = sourceType;
		this.sourceObjectId = sourceObjectId;
		this.targetType = targetType;
		this.targetObjectId = targetObjectId;
		this.operUser = "管理员";
	}*/
	
	public DTSObjectMapDto(String sourceType, String sourceObjectId,
			String targetType, String targetObjectId, String operUser) {
		super();
		this.sourceType = sourceType;
		this.sourceObjectId = sourceObjectId;
		this.targetType = targetType;
		this.targetObjectId = targetObjectId;
		this.operUser = operUser;
	}
	
}
