package com.molbase.dts.server.dao;

import java.util.List;
import java.util.Map;

import com.molbase.dts.server.dto.DTSObjectDataDto;

public interface DTSObjectDataMapper {

	int insert(DTSObjectDataDto obj);
	int update(DTSObjectDataDto obj);
	DTSObjectDataDto findByTypeAndObjId(DTSObjectDataDto params);
	List<DTSObjectDataDto> queryObjectData(Map<String, Object> params);
	List<DTSObjectDataDto> queryObjectGoodsData(Map<String, Object> params);
	int queryObjectDataTotal(Map<String, Object> params);
	int queryObjectGoodsDataTotal(Map<String, Object> params);
}
