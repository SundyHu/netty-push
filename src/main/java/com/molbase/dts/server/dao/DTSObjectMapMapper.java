package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;

import com.molbase.dts.server.dto.DTSObjectMapDto;

public interface DTSObjectMapMapper {

	List<DTSObjectMapDto> queryAll();
	int inertBindMap(DTSObjectMapDto dto);
	int deleteBindMap(DTSObjectMapDto dto);
	@Delete("delete from object_map where id=#{mapId,jdbcType=INTEGER}")
	int deleteBindMap(Integer mapId);
	List<Map<String,Object>> findBySourceType(Map<String,Object> params);
	Map<String,Object> findTotalBySourceType(Map<String,Object> params);
	DTSObjectMapDto findMapByUniKey(DTSObjectMapDto dto);
	DTSObjectMapDto findById(Integer mapId);
	List<Map<String,Object>> queryBySourceIds(Map<String,Object> params);
	Map<String,Object> queryBySourceId(Map<String,Object> params);
	List<String> loadDistinctOperator();
}
