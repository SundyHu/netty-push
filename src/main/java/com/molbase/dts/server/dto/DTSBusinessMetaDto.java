package com.molbase.dts.server.dto;

public class DTSBusinessMetaDto {

	private Integer id;
	private String name;
	private Integer bind;
	private Integer override;
	private String operator;
	
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
	public Integer getBind() {
		return bind;
	}
	public void setBind(Integer bind) {
		this.bind = bind;
	}
	public Integer getOverride() {
		return override;
	}
	public void setOverride(Integer override) {
		this.override = override;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
}
