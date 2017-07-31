package com.molbase.dts.server.dao;

import java.util.List;

import com.molbase.dts.server.dto.DTSObjectMetaDto;

public interface DTSObjectMetaMapper {

	List<DTSObjectMetaDto> queryAll();
	//DTSObjectMetaDto findByType(String type);
}
