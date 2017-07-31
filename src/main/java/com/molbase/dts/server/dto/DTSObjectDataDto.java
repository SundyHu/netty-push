package com.molbase.dts.server.dto;

import java.util.Date;

import com.molbase.dts.server.constant.ServerConstants;

/**
 * dts.object_data
 * @author changgen.xu
 *
 */
public class DTSObjectDataDto implements ServerConstants{

	private Integer id;
	private String type;
	private String name1;
	private String name2;
	private String name3;
	private String content;
	private Integer objectMetaId;
	private Date createTime;
	private Date lastUpdateTime;
	
	public DTSObjectDataDto(){
		this.bind = 0;
	}

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

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getName3() {
		return name3;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getObjectMetaId() {
		return objectMetaId;
	}

	public void setObjectMetaId(Integer objectMetaId) {
		this.objectMetaId = objectMetaId;
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
		return "DTSObjectDataDto [id=" + id + ", type=" + type + ", name1="
				+ name1 + ", name2=" + name2 + ", name3=" + name3
				+ ", content=" + content + ", objectMetaId=" + objectMetaId
				+ ", createTime=" + SDF_FULL.format(createTime) + ", lastUpdateTime="
				+ SDF_FULL.format(lastUpdateTime) + "]";
	}

	public DTSObjectDataDto(String type, String name1) {
		super();
		this.type = type;
		this.name1 = name1;
		this.bind=0;
	}

	public DTSObjectDataDto(String type, String name1, String name2) {
		super();
		this.type = type;
		this.name1 = name1;
		this.name2 = name2;
		this.bind=0;
	}
	
	//针对molbase_goods补充的字段：store_name,auth_level,pack
	private String storeName; //店铺名
	private String authLevel; //0未认证1标识认证2强认证
	private String pack; //包装
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getAuthLevel() {
		return authLevel;
	}
	public void setAuthLevel(String authLevel) {
		this.authLevel = authLevel;
	}
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	
	//针对u8_product添加的计量单位字段:unit
	private String unit;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	//针对是否已经存在绑定关系的标记字段:binded:1(已经绑定),0(未绑定)
	private int bind;
	public int getBind() {
		return bind;
	}
	public void setBind(int bind) {
		this.bind = bind;
	}
}
