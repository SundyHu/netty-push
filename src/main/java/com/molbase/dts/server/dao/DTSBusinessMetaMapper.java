package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import com.molbase.dts.server.dto.DTSBusinessMetaDto;

public interface DTSBusinessMetaMapper {

	int insert(DTSBusinessMetaDto dto);
	int update(DTSBusinessMetaDto dto);
	List<Map<String,Object>> queryByParams(Map<String,Object> params);
	Map<String,Object> queryToalbyParams(Map<String,Object> params);
	Map<String,Object> findById(int id);
	DTSBusinessMetaDto findDtoById(int id);
	List<Map<String,Object>> queryAll();
}
