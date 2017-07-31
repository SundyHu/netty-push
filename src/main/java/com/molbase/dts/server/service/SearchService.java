package com.molbase.dts.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dao.DTSBusinessMapMapper;
import com.molbase.dts.server.dao.DTSObjectMapMapper;
import com.molbase.dts.server.dao.DTSProcessLogMapper;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSObjectMapDto;
import com.molbase.dts.server.dto.DTSProcessLogDto;
import com.molbase.dts.server.service.pagination.DefaultPagination;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.server.service.pagination.QueryHandler;
import com.molbase.dts.server.service.wrapper.DaoWrapperService;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;
import com.molbase.dts.util.OmsConnector;

public class SearchService implements ServerConstants {
	
	static Logger logger = Logger.getLogger(SearchService.class);
	
	SqlSessionFactory sqlSessionFactory;
	DaoWrapperService wrapperService;
	
	public SearchService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
		this.wrapperService = new DaoWrapperService();
	}
	
	public boolean deleteMapData(Integer mapId, String userName){
		//执行对实体映射关系的删除
		//删除成功后写操作日志
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(false);//不自动提交
			DTSObjectMapMapper mapMapper = sqlSession.getMapper(DTSObjectMapMapper.class);
			DTSObjectMapDto mapDto = mapMapper.findById(mapId);
			if(null!=mapDto){
				int affect = mapMapper.deleteBindMap(mapId);
				if(affect>0){
					DTSProcessLogMapper logMapper = sqlSession.getMapper(DTSProcessLogMapper.class);
					int addAffect = logMapper.insertLog(new DTSProcessLogDto(LOG_TYPE_DELMAP, "["+userName+"]删除了:"+mapId+",sourceType:"+mapDto.getSourceType()+",sourceId:"+mapDto.getSourceObjectId()+",targetType:"+mapDto.getTargetType()+",targetId:"+mapDto.getTargetObjectId()));
					if(affect>0 && addAffect>0){
						sqlSession.commit();
						return true;
					}
				}
			}else{
				return true;
			}
		}catch(Exception e){
			sqlSession.rollback();
			logger.error("deleteMapData删除错误："+e.getMessage());
		}finally{
			sqlSession.close();
		}
		return false;
	}
	
	/**
	 * 传入source_type查询
	 * @param metaType
	 * @return
	 */
	public Pagination<Map<String,Object>> findMapByMetaTypes(int pageIndex, int pageSize, final Map<String,Object> params){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			final DTSObjectMapMapper mapper = sqlSession.getMapper(DTSObjectMapMapper.class);
			return new DefaultPagination<Map<String,Object>>(pageIndex, pageSize, new QueryHandler<Map<String,Object>>() {
				@Override
				public int getTotalElements() {
					return Integer.parseInt(mapper.findTotalBySourceType(params).get("total").toString());
				}
				@Override
				public List<Map<String,Object>> getCurrData(int pageIndex, int pageSize) {
					return mapper.findBySourceType(params);
				}
			}, PAGINATION_LINKNUM);
		}finally{
			sqlSession.close();
		}
	}
	
	private boolean isSourceType(String type){
		return(type.equals("customer") || type.equals("molbase_goods"))?true:false;
	}
	
	/**
	 * 传入obj类型查找object
	 * @param id
	 * @param name
	 * @param metaType
	 * @return
	 */
	public Pagination<DTSObjectDataDto> queryObjectByMetaType(ObjectMetaType metaType, Map<String,Object> params){
		Pagination<DTSObjectDataDto> pagination = wrapperService.doQueryObjectByParams(metaType, params);
		if((!pagination.getCurrData().isEmpty()) && isSourceType(metaType.getMetaType())){
			List<DTSObjectDataDto> list = pagination.getCurrData();
			List<String> ids = new ArrayList<String>(); 
			for(DTSObjectDataDto dto:list) ids.add(dto.getName1());
			SqlSession sqlSession = null;
			try{
				sqlSession = sqlSessionFactory.openSession(true);
				params.put("sourceType", metaType.getMetaType());
				params.put("list", ids);
				List<Map<String,Object>> mapList = sqlSession.getMapper(DTSObjectMapMapper.class).queryBySourceIds(params);
				Set<String> mapIdSet = new HashSet<String>();
				for(Map<String,Object> m:mapList) mapIdSet.add(m.get("source_object_id").toString());
				for(DTSObjectDataDto dto:list){
					if(mapIdSet.contains(dto.getName1())) dto.setBind(1);
				}
				pagination.setCurrData(list);
			}finally{
				sqlSession.close();
			}
		}
		return pagination;
	}
	
	public boolean doDataFastBind(ObjectMetaType sourceType, String[] sourceIds, ObjectMetaType targetType, String targetId, String operUser){
		return this.wrapperService.doDataBind(sourceType, sourceIds, targetType, targetId, operUser);
	}
	
	public boolean findMapExists(ObjectMetaType sourceType, String sourceId, ObjectMetaType targetType){
		return wrapperService.findMapHasBinded(sourceType, sourceId, targetType);
	}
	
	protected static void removeMapEntry(Map<String, Object> map, String[] removeKeyArray){
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		Set<String> removeKeySet = new HashSet<String>(Arrays.asList(removeKeyArray));
		while(iter.hasNext()){
			Entry<String, Object> entry = iter.next();
			if(removeKeySet.contains(entry.getKey())){
				iter.remove();
			}
		}
	}
	
	public Pagination<Map<String,Object>> queryBizList(int pageIndex, int pageSize, final Map<String,Object> params){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			final DTSBusinessMapMapper mapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
			return new DefaultPagination<Map<String,Object>>(pageIndex, pageSize, new QueryHandler<Map<String,Object>>() {
				@Override
				public int getTotalElements() {
					return Integer.parseInt(mapper.queryToalbyParams(params).get("total").toString());
				}
				@Override
				public List<Map<String,Object>> getCurrData(int pageIndex, int pageSize) {
					return mapper.queryByParams(params);
				}
			}, PAGINATION_LINKNUM);
		}finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 根据订单ID加载OMS中的订单详情商品
	 * @param orderNo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<LinkedHashMap> loadOrderProductList(String orderNo){
		List<LinkedHashMap> itemList = new OmsConnector().callOMSOrderJSON(orderNo, true);
		if(null==itemList){
			return new ArrayList<LinkedHashMap>();
		}
		logger.info("d订单号:"+orderNo+" 从OMS获取到:"+itemList.size()+" 个产品");
		//标记出已经绑定的产品
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("sourceType", "molbase_goods");
			DTSObjectMapMapper mapper = sqlSession.getMapper(DTSObjectMapMapper.class);
			for(LinkedHashMap map:itemList){
				params.put("sourceObjectId", map.get("goods_id").toString());
				if(null != mapper.queryBySourceId(params)){ //存在有绑定关系,加上已关联的标记
					map.put("isBinded", 1);
				}
			}
		}finally{
			sqlSession.close();
		}
		return  itemList;
	}
	
	public List<String> loadMapDistinctOperaotr(){
		SqlSession sqlSession = null;
		List<String> list = new ArrayList<>();
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			list.addAll(sqlSession.getMapper(DTSObjectMapMapper.class).loadDistinctOperator());
		}finally{
			sqlSession.close();
		}
		return list;
	}
	
	/*public static void main(String[] args) {
		HashMap<String, Object> map = new HashMap<String, Object>(10);
		map.put("id", 1111);
		map.put("name", "asdfasfdafdasfd");
		map.put("content", "ssssssssssssssssssssssssssssssssss");
		map.put("gender", "男");
		System.out.println(map);
		removeMapEntry(map,new String[]{"content","gender"});
		System.out.println(map);
	}*/
}
