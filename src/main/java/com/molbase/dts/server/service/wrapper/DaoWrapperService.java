package com.molbase.dts.server.service.wrapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dao.DTSObjectDataMapper;
import com.molbase.dts.server.dao.DTSObjectMapMapper;
import com.molbase.dts.server.dao.DTSProcessLogMapper;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSObjectMapDto;
import com.molbase.dts.server.dto.DTSObjectMetaDto;
import com.molbase.dts.server.dto.DTSProcessLogDto;
import com.molbase.dts.server.service.pagination.DefaultPagination;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.server.service.pagination.QueryHandler;
import com.molbase.dts.server.service.rpc.NettyBrokerRPCService;
import com.molbase.dts.util.JSONMapUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;
import com.molbase.dts.util.UnitConvertUtil;

/**
 * @author changgen.xu
 * @date 2015年12月2日 下午3:27:14
 * @desc 封装多步骤操作DB的业务逻辑
 */
public class DaoWrapperService implements ServerConstants {

	static Logger logger = Logger.getLogger(DaoWrapperService.class);
	
	SqlSessionFactory sqlSessionFactory;
	NettyBrokerRPCService brokerRPCService;
	
	public DaoWrapperService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
		this.brokerRPCService = new NettyBrokerRPCService();
	}
	
	/**
	 * object_data数据保存，存在时覆盖！
	 * @param dto
	 * @return
	 */
	public DTSObjectDataDto objectDataSave(DTSObjectDataDto dto){
		if(null == dto) return null;
		SqlSession sqlSession = null;
		try{
			sqlSession = this.sqlSessionFactory.openSession(true);
			DTSObjectDataMapper mapper = sqlSession.getMapper(DTSObjectDataMapper.class);
			//查询是否存在有
			DTSObjectDataDto row = mapper.findByTypeAndObjId(dto);
			//根据结果判断执行insert/update
			if(null==row){
				mapper.insert(dto);
			}else{
				dto.setId(row.getId());
				mapper.update(dto);
			}
		}finally{
			sqlSession.close();
		}
		return dto;
	}
	
	public DTSObjectDataDto findLocalObjectData(DTSObjectMetaDto dto, String objId){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession();
			return sqlSession.getMapper(DTSObjectDataMapper.class).findByTypeAndObjId(new DTSObjectDataDto(dto.getType(), objId));
		}finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 数据绑定的操作统一处理逻辑
	 * @param sourceType
	 * @param sourceId
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	public boolean doDataBind(ObjectMetaType sourceType, String[] sourceIds, ObjectMetaType targetType, String targetId, String operUser){
		//查询tareget的object_data并保存本地缓存
		DTSObjectDataDto targetObjDataDto = brokerRPCService.findObjectById(CacheObjMetaData.getCachedDTSObjectMetaDto(targetType), targetId);
		if(null==targetObjDataDto){ //broker未查询到，查询本地
			targetObjDataDto = this.findLocalObjectData(CacheObjMetaData.getCachedDTSObjectMetaDto(targetType), targetId);
		}else{ //broker查询到，保存本地
			targetObjDataDto = this.objectDataSave(targetObjDataDto);
		}
		if(null==targetObjDataDto) return false;
		//多个sourceId
		List<DTSObjectDataDto> sourceObjDataDtoList = new ArrayList<DTSObjectDataDto>();
		for(String sourceId:sourceIds){
			//查询source的object_data并保存本地缓存
			DTSObjectDataDto sourceObjDataDto = brokerRPCService.findObjectById(CacheObjMetaData.getCachedDTSObjectMetaDto(sourceType), sourceId);
			if(null==sourceObjDataDto){ //broker未查询到，查询本地
				sourceObjDataDto = this.findLocalObjectData(CacheObjMetaData.getCachedDTSObjectMetaDto(sourceType), sourceId);
			}else{ //broker查询到，保存本地
				sourceObjDataDto = this.objectDataSave(sourceObjDataDto);
			}
			sourceObjDataDtoList.add(sourceObjDataDto);
		}
		//执行source和target的关系保存
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);//此处设置自动提交
			DTSObjectMapMapper mapper = sqlSession.getMapper(DTSObjectMapMapper.class);
			int affectRows = 0;
			for(DTSObjectDataDto sourceObjDataDto:sourceObjDataDtoList){
				if(null==sourceObjDataDto) continue;
				//组装参数
				DTSObjectMapDto dto = new DTSObjectMapDto();
				dto.setSourceType(sourceType.toString());
				dto.setTargetType(targetType.toString());
				dto.setSourceObjectId(sourceObjDataDto.getName1());
				dto.setTargetObjectId(String.valueOf(targetId));
				dto.setSourceObjectDataId(sourceObjDataDto.getId());
				dto.setTargetObjectDataId(targetObjDataDto.getId());
				dto.setOperUser(operUser);
				//判断关系是否已经存在
				DTSObjectMapDto existsMap = mapper.findMapByUniKey(new DTSObjectMapDto(dto.getSourceType(), dto.getSourceObjectId(), dto.getTargetType()));
				if(null!=existsMap) continue;
				int affect = mapper.inertBindMap(dto);
				if(affect>0){
					DTSProcessLogMapper logMapper = sqlSession.getMapper(DTSProcessLogMapper.class);
					logMapper.insertLog(new DTSProcessLogDto(LOG_TYPE_BINDMAP, "["+operUser+"]绑定了:sourceType:"+sourceType.toString()+",sourceId:"+sourceObjDataDto.getName1()+",targetType:"+targetType.toString()+",targetId:"+targetId));
					affectRows++;
				}
			}
			return affectRows>0?true:false;
		}finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 搜索页，数据查询操作
	 * @param objMetaDto
	 * @param name1
	 * @param name2
	 * @return
	 */
	public Pagination<DTSObjectDataDto> doQueryObjectByParams(ObjectMetaType objMetaType, Map<String,Object> params){
		//调用broker查询远端DB中数据
		Pagination<DTSObjectDataDto> pagination = brokerRPCService.queryObject(CacheObjMetaData.getCachedDTSObjectMetaDto(objMetaType), params);
		if(pagination.getCurrData().isEmpty()){//broker查询结果为空，执行本地查询
			SqlSession sqlSession = null;
			try{
				sqlSession = sqlSessionFactory.openSession();
				//走查询本地缓存数据的逻辑，判断是否是good搜索
				final List<DTSObjectDataDto> list = new ArrayList<DTSObjectDataDto>();
				int total = 0;
				DTSObjectDataMapper mapper = sqlSession.getMapper(DTSObjectDataMapper.class);
				int pageIndex = (Integer)params.get("pageIndex");
				int pageSize = (Integer)params.get("pageSize");
				params.put("type", objMetaType.getMetaType());
				if(objMetaType.getMetaType().equals(MOLBASE_GOODS)){
					list.addAll(mapper.queryObjectGoodsData(params));
					LinkedHashMap<String, Object> jsonObj = null;
					for(DTSObjectDataDto dataDto:list){
						jsonObj = JSONMapUtil.deserializeLinkedHashMap(dataDto.getContent());
						dataDto.setStoreName(jsonObj.get("store_name").toString());
						dataDto.setAuthLevel("2".equals(jsonObj.get("auth_level").toString())?"强认证":"未认证或标识认证");
						dataDto.setPack(jsonObj.get("def_pack_num").toString()+jsonObj.get("def_unit").toString()+"/"+jsonObj.get("base_unit").toString());
					}
					total = mapper.queryObjectGoodsDataTotal(params);
				}else if(objMetaType.getMetaType().equals(U8_PRODUCT)){
					//new DTSObjectDataDto(objMetaType.toString(), params.get("name1"), params.get("name2")))
					list.addAll(mapper.queryObjectData(params));
					LinkedHashMap<String, Object> jsonObj = null;
					for(DTSObjectDataDto dataDto:list){
						jsonObj = JSONMapUtil.deserializeLinkedHashMap(dataDto.getContent());
						dataDto.setUnit(UnitConvertUtil.convert(jsonObj.containsKey("cComUnitCode")?jsonObj.get("cComUnitCode").toString():""));
					}
					total = mapper.queryObjectDataTotal(params);
				}else{
					//new DTSObjectDataDto(objMetaType.toString(), params.get("name1"), params.get("name2"))
					list.addAll(mapper.queryObjectData(params));
					total = mapper.queryObjectDataTotal(params);
				}
				final int t = total;
				return new DefaultPagination<DTSObjectDataDto>(pageIndex, pageSize, new QueryHandler<DTSObjectDataDto>() {
					@Override
					public int getTotalElements() {
						return t;
					}
					@Override
					public List<DTSObjectDataDto> getCurrData(int pageIndex, int pageSize) {
						return list;
					}
				}, PAGINATION_LINKNUM);
			}finally{
				sqlSession.close();
			}
		}
		return pagination;
	}
	
	/**
	 * 是否已经绑定了
	 * @param sourceType
	 * @param sourceId
	 * @param targetType
	 * @return true：绑定了，false：没绑定
	 */
	public boolean findMapHasBinded(ObjectMetaType sourceType, String sourceId, ObjectMetaType targetType){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSObjectMapDto mapDto = sqlSession.getMapper(DTSObjectMapMapper.class)
					.findMapByUniKey(new DTSObjectMapDto(sourceType.getMetaType(), sourceId, targetType.getMetaType()));
			return null==mapDto?false:true;
		}finally{
			sqlSession.close();
		}
	}
}
