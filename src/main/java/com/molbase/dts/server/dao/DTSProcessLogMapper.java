package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.molbase.dts.server.dto.DTSProcessLogDto;

/**
 * @author changgen.xu
 */
public interface DTSProcessLogMapper {

	List<DTSProcessLogDto> queryAll();
	List<DTSProcessLogDto> findByType(Integer type);
	DTSProcessLogDto findById(Integer id);
	int insertLog(DTSProcessLogDto obj);
	List<Map<String,Object>> pageQueryByType(Map<String,Object> params);
	
	@Select("select count(id) total from process_log where `type`=#{type,jdbcType=INTEGER}")
	Map<String,Object> countByType(Integer type);
}
