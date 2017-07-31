package com.molbase.dts.server.dto;

import java.util.Date;

import com.molbase.dts.server.constant.ServerConstants;

/**
 * dts.object_meta
 * @author changgen.xu
 *
 */
public class DTSObjectMetaDto implements ServerConstants {

	private Integer id;
	private String type; //主体的类型标识
	
	private String tableName;//主体所在的表名
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private String objectIdKey; //主键唯一字段
	private String IndexKey; //提供索引查询字段
	private String listDisplayKey;
	private String matchDisplayKey;
	private String content; //all fields
	private String version; //所有字段自然排序
	
	private Integer brokerId;
	private Date createTime;
	private Date lastUpdateTime;
	
	public DTSObjectMetaDto(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObjectIdKey() {
		return objectIdKey;
	}

	public void setObjectIdKey(String objectIdKey) {
		this.objectIdKey = objectIdKey;
	}

	public String getIndexKey() {
		return IndexKey;
	}

	public void setIndexKey(String indexKey) {
		IndexKey = indexKey;
	}

	public String getListDisplayKey() {
		return listDisplayKey;
	}

	public void setListDisplayKey(String listDisplayKey) {
		this.listDisplayKey = listDisplayKey;
	}

	public String getMatchDisplayKey() {
		return matchDisplayKey;
	}

	public void setMatchDisplayKey(String matchDisplayKey) {
		this.matchDisplayKey = matchDisplayKey;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
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
		return "DTSObjectMetaDto [id=" + id + ", type=" + type
				+ ", objectIdKey=" + objectIdKey + ", IndexKey=" + IndexKey
				+ ", listDisplayKey=" + listDisplayKey + ", matchDisplayKey="
				+ matchDisplayKey + ", content=" + content + ", version="
				+ version + ", brokerId=" + brokerId + ", createTime="
				+ SDF_FULL.format(createTime) + ", lastUpdateTime=" + SDF_FULL.format(lastUpdateTime) + "]";
	}
	
}
