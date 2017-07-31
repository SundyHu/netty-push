package com.molbase.dts.server.service.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSObjectMetaDto;
import com.molbase.dts.server.netty.Ask;
import com.molbase.dts.server.netty.AskImp;
import com.molbase.dts.server.service.pagination.DefaultPagination;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.server.service.pagination.QueryHandler;
import com.molbase.dts.util.JSONMapUtil;
import com.molbase.dts.util.StringUtil;
import com.molbase.dts.util.UnitConvertUtil;

/**
 * 处理所有需要调用Broker查询数据的Biz
 * @author changgen.xu
 * @date 2015年12月2日 上午11:07:50
 * @desc 根据metaDto配置组装SQL语句，转换对应的brokerId执行RPC，处理返回结果
 */
public class NettyBrokerRPCService implements ServerConstants {

	static Logger logger = Logger.getLogger(NettyBrokerRPCService.class);
	
	Ask rpcAsk;
	
	public NettyBrokerRPCService(){
		this.rpcAsk = new AskImp();
	}
	
	private static final String SQL_ID_EXISTS = "select %s from %s where %s='%s'";
	/**
	 * 根据ID查找是否该ID存在
	 * @param metaDto
	 * @param objectId
	 * @return
	 * SQL模板：select * from tableName where id=?
	 */
	@SuppressWarnings("rawtypes")
	public List<HashMap> findObjectIdExist(DTSObjectMetaDto objMetaDto, String objectId){
		String sql = String.format(SQL_ID_EXISTS, objMetaDto.getObjectIdKey(), objMetaDto.getTableName(), objMetaDto.getObjectIdKey(), objectId);
		logger.info("组装SQL:"+sql+",调用brokerId:"+objMetaDto.getBrokerId());
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql);
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		return JSONMapUtil.deserializeListHashMap(json);
	}
	
	private static final String SQL_FIND_OBJECT = "select * from %s where %s='%s'";
	/**
	 * 查询出实体封装成object_data返回
	 * @param objMetaDto
	 * @param objectId
	 * @return DTSObjectDataDto
	 */
	public DTSObjectDataDto findObjectById(DTSObjectMetaDto objMetaDto, String objectId){
		if(objMetaDto.getType().equals(MOLBASE_GOODS)){
			return findObjectByIdGoods(objMetaDto,objectId);
		}else{
			return findObjectByIdNormal(objMetaDto,objectId);
		}
	}
	
	private DTSObjectDataDto findObjectByIdGoods(DTSObjectMetaDto objMetaDto, String objectId){
		String sql = "select sp.*,es.store_name,es.auth_level from molbase_mall.sm_product sp left join molbase.ecm_store es on sp.store_id=es.store_id where id="+objectId;
		logger.info("组装SQL:"+sql);
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql);
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return null;
		HashMap map = objArray.get(0);
		DTSObjectDataDto dataDto = new DTSObjectDataDto();
		dataDto.setType(objMetaDto.getType());
		dataDto.setObjectMetaId(objMetaDto.getId());
		dataDto.setContent(JSON.toJSONString(map));
		String[] indexFieldArr = objMetaDto.getIndexKey().split(",");
		dataDto.setName1(map.get(indexFieldArr[0]).toString());
		dataDto.setName2(map.get(indexFieldArr[1]).toString());
		dataDto.setName3(indexFieldArr.length>2?map.get(indexFieldArr[2]).toString():"0");
		return dataDto;
	}
	
	private DTSObjectDataDto findObjectByIdNormal(DTSObjectMetaDto objMetaDto, String objectId){
		String sql = String.format(SQL_FIND_OBJECT, objMetaDto.getTableName(), objMetaDto.getObjectIdKey(), objectId);
		logger.info("组装SQL:"+sql+",调用brokerId:"+objMetaDto.getBrokerId());
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql);
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return null;
		//非空，取出数据进行解析
		HashMap map = objArray.get(0);
		DTSObjectDataDto dataDto = new DTSObjectDataDto();
		dataDto.setType(objMetaDto.getType());
		dataDto.setObjectMetaId(objMetaDto.getId());
		dataDto.setContent(JSON.toJSONString(map));
		//关键字段
		String[] indexFieldArr = objMetaDto.getIndexKey().split(",");
		dataDto.setName1(map.get(indexFieldArr[0]).toString());
		dataDto.setName2(map.get(indexFieldArr[1]).toString());
		dataDto.setName3(indexFieldArr.length>2?map.get(indexFieldArr[2]).toString():"0");
		return dataDto;
	}
	
	private static final String SQL_QUERY_OBJECT = "select * from %s where 1=1";
	/**
	 * 关键字查询实体
	 * @param objMetaDto
	 * @param name1
	 * @param name2
	 * @return limit 10
	 */
	public Pagination<DTSObjectDataDto> queryObject(final DTSObjectMetaDto objMetaDto, final Map<String,Object> params){
		int pageIndex = (Integer)params.get("pageIndex");
		int pageSize = (Integer)params.get("pageSize");
		if(StringUtil.isEmpty(params.get("name1").toString()) && StringUtil.isEmpty(params.get("name2").toString())){
			return new DefaultPagination<DTSObjectDataDto>(pageIndex, pageSize, new QueryHandler<DTSObjectDataDto>() {
				@Override
				public int getTotalElements() {
					return 0;
				}
				@Override
				public List<DTSObjectDataDto> getCurrData(int pageIndex, int pageSize) {
					return new ArrayList<DTSObjectDataDto>();
				}
			}, PAGINATION_LINKNUM);
		}
		//根据metaType判断是普通查询还是molbase_goods查询
		if(objMetaDto.getType().equals(MOLBASE_GOODS)){
			return new DefaultPagination<DTSObjectDataDto>(pageIndex, pageSize, new QueryHandler<DTSObjectDataDto>() {
				@Override
				public int getTotalElements() {
					return queryObjectGoodsTotal(objMetaDto, params);
				}
				@Override
				public List<DTSObjectDataDto> getCurrData(int pageIndex, int pageSize) {
					return queryObjectGoods(objMetaDto, params);
				}
			}, PAGINATION_LINKNUM);
		}else{
			return new DefaultPagination<DTSObjectDataDto>(pageIndex, pageSize, new QueryHandler<DTSObjectDataDto>() {
				@Override
				public int getTotalElements() {
					return queryObjectNormalTotal(objMetaDto, params);
				}
				@Override
				public List<DTSObjectDataDto> getCurrData(int pageIndex, int pageSize) {
					return queryObjectNormal(objMetaDto, params);
				}
			}, PAGINATION_LINKNUM);
		}
	}
	
	/**
	 * 普通查询
	 * @param objMetaDto
	 * @param name1
	 * @param name2
	 * @return
	 */
	private List<DTSObjectDataDto> queryObjectNormal(DTSObjectMetaDto objMetaDto, Map<String,Object> params){
		StringBuilder sql = new StringBuilder(String.format(SQL_QUERY_OBJECT, objMetaDto.getTableName()));
		String[] indexFieldArr = objMetaDto.getIndexKey().split(",");
		if(!StringUtil.isEmpty(params.get("name1"))){
			sql.append(" and "+indexFieldArr[0]+" like '%"+params.get("name1").toString().replace("'","\\'")+"%'");
		}
		if(!StringUtil.isEmpty(params.get("name2"))){
			sql.append(" and "+indexFieldArr[1]+" like '%"+params.get("name2").toString().replace("'","\\'")+"%'");
		}
		/*if(objMetaDto.getBrokerId()==3 && "ecm_goods".equals(objMetaDto.getTableName())){
			StringBuilder tmp_sql = new StringBuilder("select * from "+objMetaDto.getTableName()+" where 1=1 ");
			if(!StringUtil.isEmpty(name1)){
				tmp_sql.append(" and "+indexFieldArr[0]+" = '"+name1.replace("'","\\'")+"'");
			}
			if(!StringUtil.isEmpty(name2)){
				tmp_sql.append(" and "+indexFieldArr[1]+" like '%"+name2.replace("'","\\'")+"%'");
			}
			sql = new StringBuilder(tmp_sql.toString());
		}*/
		if(params.containsKey("start") && params.containsKey("size")){
			sql.append(" limit "+params.get("start")+","+params.get("size"));
		}else{
			sql.append(" limit 10");
		}
		logger.info("组装SQL:"+sql.toString()+",调用brokerId:"+objMetaDto.getBrokerId());
		//执行调用
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql.toString());
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return new ArrayList<DTSObjectDataDto>();
		//非空，执行解析
		List<DTSObjectDataDto> list = new ArrayList<DTSObjectDataDto>();
		for(HashMap map:objArray){
			DTSObjectDataDto dataDto = new DTSObjectDataDto();
			dataDto.setType(objMetaDto.getType());
			dataDto.setObjectMetaId(objMetaDto.getId());
			dataDto.setContent(JSON.toJSONString(map));
			dataDto.setName1(map.get(indexFieldArr[0]).toString());
			dataDto.setName2(map.get(indexFieldArr[1]).toString());
			dataDto.setName3(indexFieldArr.length>2?map.get(indexFieldArr[2]).toString():"0");
			if(map.containsKey("cComUnitCode")){
				dataDto.setUnit(UnitConvertUtil.convert(map.get("cComUnitCode").toString()));
			}
			list.add(dataDto);
		}
		return list;
	}
	
	/**
	 * 普通查询total
	 * @param objMetaDto
	 * @param name1
	 * @param name2
	 * @return
	 */
	private int queryObjectNormalTotal(DTSObjectMetaDto objMetaDto, Map<String,Object> params){
		StringBuilder sql = new StringBuilder(String.format("select count(1) total from %s where 1=1", objMetaDto.getTableName()));
		String[] indexFieldArr = objMetaDto.getIndexKey().split(",");
		if(!StringUtil.isEmpty(params.get("name1"))){
			sql.append(" and "+indexFieldArr[0]+" like '%"+params.get("name1").toString().replace("'","\\'")+"%'");
		}
		if(!StringUtil.isEmpty(params.get("name2"))){
			sql.append(" and "+indexFieldArr[1]+" like '%"+params.get("name2").toString().replace("'","\\'")+"%'");
		}
		logger.info("组装SQL:"+sql.toString()+",调用brokerId:"+objMetaDto.getBrokerId());
		//执行调用
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql.toString());
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return 0;
		return Integer.parseInt(objArray.get(0).get("total").toString());
	}
	
	/**
	 * molbase.mall库的molbase_goods类型数据查询逻辑
	 * @return
	 */
	private List<DTSObjectDataDto> queryObjectGoods(DTSObjectMetaDto objMetaDto, Map<String,Object> params){
		StringBuilder sql = new StringBuilder("select sp.*,es.store_name,es.auth_level from molbase_mall.sm_product sp left join molbase.ecm_store es on sp.store_id=es.store_id where 1=1");
		if(!StringUtil.isEmpty(params.get("name1"))){
			sql.append(" and sp.id="+params.get("name1").toString().replace("'","\\'"));
		}
		if(!StringUtil.isEmpty(params.get("name2"))){
			sql.append(" and sp.name regexp '"+params.get("name2").toString().replace("'","\\'")+"'");
		}
		if(!StringUtil.isEmpty(params.get("store_name"))){
			sql.append(" and es.store_name regexp '"+params.get("store_name").toString().replace("'","\\'")+"'");
		}
		sql.append(" order by es.auth_level desc");
		//分页参数
		if(params.containsKey("start") && params.containsKey("size")){
			sql.append(" limit "+params.get("start")+","+params.get("size"));
		}else{
			sql.append(" limit 10");
		}
		logger.info("组装SQL:"+sql.toString()+",调用brokerId:"+objMetaDto.getBrokerId());
		//执行调用
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql.toString());
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return new ArrayList<DTSObjectDataDto>();
		//非空，执行解析
		List<DTSObjectDataDto> list = new ArrayList<DTSObjectDataDto>();
		for(HashMap map:objArray){
			DTSObjectDataDto dataDto = new DTSObjectDataDto();
			dataDto.setType(objMetaDto.getType());
			dataDto.setObjectMetaId(objMetaDto.getId());
			dataDto.setContent(JSON.toJSONString(map));
			dataDto.setName1(map.get("id").toString());
			dataDto.setName2(map.get("name").toString());
			dataDto.setName3(map.get("puritys").toString());
			dataDto.setStoreName(map.get("store_name").toString());
			dataDto.setPack(map.get("def_pack_num").toString()+map.get("def_unit").toString()+"/"+map.get("base_unit").toString());
			dataDto.setAuthLevel("2".equals(map.get("auth_level").toString())?"强认证":"未认证或标识认证");
			list.add(dataDto);
		}
		return list;
	}
	
	/**
	 * molbase.mall库的molbase_goods类型数据总数
	 * @return
	 */
	private int queryObjectGoodsTotal(DTSObjectMetaDto objMetaDto, Map<String,Object> params){
		StringBuilder sql = new StringBuilder("select count(sp.id) total from molbase_mall.sm_product sp left join molbase.ecm_store es on sp.store_id=es.store_id where 1=1");
		if(!StringUtil.isEmpty(params.get("name1"))){
			sql.append(" and sp.id="+params.get("name1").toString().replace("'","\\'"));
		}
		if(!StringUtil.isEmpty(params.get("name2"))){
			sql.append(" and sp.name regexp '"+params.get("name2").toString().replace("'","\\'")+"'");
		}
		if(!StringUtil.isEmpty(params.get("store_name"))){
			sql.append(" and es.store_name regexp '"+params.get("store_name").toString().replace("'","\\'")+"'");
		}
		logger.info("组装SQL:"+sql.toString()+",调用brokerId:"+objMetaDto.getBrokerId());
		//执行调用
		String json = rpcAsk.req(String.valueOf(objMetaDto.getBrokerId()),sql.toString());
		logger.info("Broker:"+objMetaDto.getBrokerId()+",返回结果:"+json);
		List<HashMap> objArray = JSONMapUtil.deserializeListHashMap(json);
		if(objArray.isEmpty()) return 0;
		return Integer.parseInt(objArray.get(0).get("total").toString());
	}
}
