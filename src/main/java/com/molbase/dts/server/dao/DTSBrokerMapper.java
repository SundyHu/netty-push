package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.molbase.dts.server.dto.DTSBrokerDto;

public interface DTSBrokerMapper {

	@Select(value="select * from broker")
	List<Map<String,Object>> queryAll();
	
	DTSBrokerDto findById(Integer brokerId);
	
	void updateById(DTSBrokerDto brokerDto);
}
