package com.molbase.dts.server.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dao.DTSBrokerMapper;
import com.molbase.dts.server.dao.DTSObjectDataMapper;
import com.molbase.dts.server.dao.DTSObjectMapMapper;
import com.molbase.dts.server.dto.DTSBrokerDto;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSObjectMapDto;
import com.molbase.dts.server.dto.DTSObjectMetaDto;
import com.molbase.dts.server.service.rpc.NettyBrokerRPCService;
import com.molbase.dts.server.service.wrapper.DaoWrapperService;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;

public class IndexService implements ServerConstants {

	static Logger logger = Logger.getLogger(IndexService.class);
	
	NettyBrokerRPCService brokerRPCService;
	SqlSessionFactory sqlSessionFactory;
	DaoWrapperService wrapperService;
	
	public IndexService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
		this.brokerRPCService = new NettyBrokerRPCService();
		this.wrapperService = new DaoWrapperService();
	}
	
	/**
	 * 根据传入的ID和obj_meta类型查找该id是否存在
	 * @param id
	 * @param metaId
	 * @return 是否存在，true:存在，false:不存在
	 */
	@SuppressWarnings("rawtypes")
	public boolean findObjectExists(ObjectMetaType objType, String objectId){
		//根据metaId转换获取brokerId,调用netty接口到相应的broker中查询
		DTSObjectMetaDto objMetaDto = CacheObjMetaData.getCachedDTSObjectMetaDto(objType);
		List<HashMap> rows = this.brokerRPCService.findObjectIdExist(objMetaDto, objectId);
		if(null==rows || rows.size()<1){
			//调用本地查询
			SqlSession sqlSession = null;
			try{
				sqlSession = sqlSessionFactory.openSession();
				DTSObjectDataDto dataDto = sqlSession.getMapper(DTSObjectDataMapper.class).findByTypeAndObjId(new DTSObjectDataDto(objMetaDto.getType(), String.valueOf(objectId)));
				return null==dataDto?false:true;
			}finally{
				sqlSession.close();
			}
		}
		return true;//存在
	}
	
	/**
	 * 根据map查询是否已经存在映射关系
	 * @return
	 */
	public boolean findMapExists(ObjectMetaType sourceType, String sourceId, ObjectMetaType targetType){
		return wrapperService.findMapHasBinded(sourceType, sourceId, targetType);
	}
	
	public boolean doDataFastBind(ObjectMetaType sourceType, String sourceId, ObjectMetaType targetType, String targetId, String operUser){
		//如果已经存在，直接返回true
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		DTSObjectMapDto mapDto = sqlSession.getMapper(DTSObjectMapMapper.class).findMapByUniKey(new DTSObjectMapDto(sourceType.getMetaType(), sourceId, targetType.getMetaType()));
		sqlSession.close();
		if(null != mapDto) return true;
		//否则，执行绑定
		return this.wrapperService.doDataBind(sourceType, new String[]{sourceId}, targetType, targetId, operUser);
	}
	
	public void updateBroker(Integer brokerId){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession();
			DTSBrokerDto brokerDto=new DTSBrokerDto();
			brokerDto.setId(brokerId);
			brokerDto.setContent(System.currentTimeMillis()+"");
			sqlSession.getMapper(DTSBrokerMapper.class).updateById(brokerDto);
			sqlSession.commit();
		}finally{
			sqlSession.close();
		}
	}
	
}
