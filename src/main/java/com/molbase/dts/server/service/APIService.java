package com.molbase.dts.server.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;
import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dao.DTSBusinessMapMapper;
import com.molbase.dts.server.dao.DTSBusinessMetaMapper;
import com.molbase.dts.server.dao.DTSObjectDataMapper;
import com.molbase.dts.server.dao.DTSObjectMapMapper;
import com.molbase.dts.server.dao.DTSProcessLogMapper;
import com.molbase.dts.server.dto.APIResultObject;
import com.molbase.dts.server.dto.DTSBusinessMapAPIDto;
import com.molbase.dts.server.dto.DTSBusinessMapDto;
import com.molbase.dts.server.dto.DTSBusinessMetaDto;
import com.molbase.dts.server.dto.DTSObjectDataDto;
import com.molbase.dts.server.dto.DTSObjectMapDto;
import com.molbase.dts.server.dto.DTSProcessLogDto;
import com.molbase.dts.server.service.rpc.NettyBrokerRPCService;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;

/**
 * @des 对外发布的接口请求业务处理Bean
 * @author changgen.xu
 * @date 2015年11月25日 下午7:22:32
 */
public class APIService implements ServerConstants {

	static Logger logger = Logger.getLogger(APIService.class);
	
	SqlSessionFactory sqlSessionFactory;
	NettyBrokerRPCService brokerRPCService;
	
	public APIService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
		this.brokerRPCService = new NettyBrokerRPCService();
	}
	
	/**
	 * 根据参数在object_map中寻找是否有关系存在，不存在返回空！
	 * 获取targetId执行rpc查找broker端object数据返回，不存在执行本地object_data查询，
	 * 将broker端最新object数据更新到object_data中缓存，
	 * @param sourceMetaType
	 * @param sourceId
	 * @param targetMetaType
	 */
	public DTSObjectDataDto doTranslate(ObjectMetaType sourceMetaType, String sourceId, ObjectMetaType targetMetaType){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSObjectMapMapper objectMapMapper = sqlSession.getMapper(DTSObjectMapMapper.class);
			DTSObjectDataMapper objectDataMapper = sqlSession.getMapper(DTSObjectDataMapper.class);
			// 查询map关系是否存在
			DTSObjectMapDto mapDto = objectMapMapper.findMapByUniKey(new DTSObjectMapDto(sourceMetaType.getMetaType(), sourceId, targetMetaType.getMetaType()));
			if(null==mapDto) return null;
			//根据map关系的targetObjectId查broker数据
			DTSObjectDataDto targetDataDto = this.brokerRPCService.findObjectById(CacheObjMetaData
					.getCachedDTSObjectMetaDto(ObjectMetaType.contain(mapDto
							.getTargetType())), mapDto.getTargetObjectId());
			if(null!=targetDataDto){//找到啦
				objectDataMapper.update(targetDataDto);
				return targetDataDto;
			}
			//broker端返回空，查本地缓存
			targetDataDto = objectDataMapper.findByTypeAndObjId(new DTSObjectDataDto(mapDto
					.getTargetType(), mapDto.getTargetObjectId()));
			return targetDataDto;
		}finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 记录翻译日志
	 * @param dto
	 */
	public void logInsert(DTSProcessLogDto dto){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			sqlSession.getMapper(DTSProcessLogMapper.class).insertLog(dto);
		}finally{
			sqlSession.close();
		}
	}
	
	/**
	 * 业务关系数据保存
	 * 1.判断bizId是否正确,并加载business_meta的绑定设置
	 * 2.a1对1.可覆盖:没有则写入，有则先切换覆盖状态，再写入新数据
	 * 2.b1对1.不可覆盖:没有则写入，有则提示错误，已经申请，不能添加
	 * 2.c1对N.直接写入
	 * @param dto
	 * @return
	 */
	public APIResultObject insertBizMapDto(DTSBusinessMapDto dto){
		APIResultObject resultObj = new APIResultObject();
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSBusinessMetaMapper bizMetaMapper = sqlSession.getMapper(DTSBusinessMetaMapper.class);
			DTSBusinessMetaDto bizMetaDto = bizMetaMapper.findDtoById(dto.getBizId());
			if(null==bizMetaDto){
				resultObj.setStatus(RET_STATUS_NOT_FOUND);
				resultObj.setData("请求参数[bizId]错误!");
				return resultObj;
			}
			DTSBusinessMapMapper bizMapMapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
			List<DTSBusinessMapDto> list = bizMapMapper.findByBizCustomerOne(dto);
			if(bizMetaDto.getBind()==BIZ_META_BIND_1 && bizMetaDto.getOverride() == BIZ_META_OVERRIDE_YES){
				if(!list.isEmpty()){ //已经有，执行覆盖
					bizMapMapper.deleteBizMap(list.get(0));//旧的状态切为4
				}
				bizMapMapper.insert(dto);
				resultObj.setSuccess();
			}else if(bizMetaDto.getBind()==BIZ_META_BIND_1 && bizMetaDto.getOverride() == BIZ_META_OVERRIDE_NO){
				if(list.isEmpty()){ //不存在
					bizMapMapper.insert(dto);
					resultObj.setSuccess();
					return resultObj;
				}
				//存在，判断是否为原来已经绑定的(失效)关系
				list = bizMapMapper.findByBizUserCustomer(dto);
				if(!list.isEmpty()){ //将原来失效的关系重新激活
					bizMapMapper.reActiveBizMap(dto);
					resultObj.setSuccess();
				}else{
					resultObj.setStatus(RET_STATUS_NOT_FOUND);
					resultObj.setData("该客户已经存在有1:1绑定关系，不允许覆盖！");
				}
			}else if(bizMetaDto.getBind() == BIZ_META_BIND_N){
				bizMapMapper.insert(dto);
				resultObj.setSuccess();
			}
		}catch(Exception e){
			logger.error("");
			resultObj.setStatus(RET_STATUS_NOT_FOUND);
			resultObj.setData("该客户已经存在有1:1绑定关系，不允许覆盖！");
		}finally{
			sqlSession.close();
		}
		return resultObj;
	}
	
	/**
	 * 根据查询参数查询对应关系数据返回
	 * @param params
	 * @return
	 */
	public APIResultObject loadBizMapData(Map<String,Object> params){
		APIResultObject resultObj = new APIResultObject();
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			List<DTSBusinessMapAPIDto> list = sqlSession.getMapper(DTSBusinessMapMapper.class).apiLoadQuery(params);
			resultObj.setStatus(RET_STATUS_SUCESS);
			resultObj.setData(list);
		}catch(Exception e){
			logger.error("API查询bizMap数据错误:"+e.getMessage());
			resultObj.setStatus(RET_STATUS_NOT_FOUND);
			resultObj.setData(new String[]{});
		}finally{
			sqlSession.close();
		}
		return resultObj;
	}
	
//	/**
//	 * 删除关系(作废)V1
//	 * @param params
//	 * @return
//	 */
//	public APIResultObject delBizMapData(Map<String,Object> params){
//		APIResultObject resultObj = new APIResultObject();
//		SqlSession sqlSession = null;
//		try{
//			sqlSession = sqlSessionFactory.openSession(true);
//			DTSBusinessMapMapper mapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
//			List<DTSBusinessMapDto> list = mapper.findByBizBill(params);
//			/*if("1".equals(params.get("bizId"))){ //针对同路人特殊处理
//				list = mapper.findByBizUserId(params);
//			}else{
//				list = mapper.findByBizBill(params);
//			}*/
//			if(list.isEmpty()){ //没有绑定关系，不做处理
//				resultObj.setStatus(RET_STATUS_NOT_FOUND);
//				resultObj.setData("该业务和单据没有对应的会员和客户绑定关系！");
//			}else{
//				/*if("1".equals(params.get("bizId"))){
//					mapper.delByBizUser(params);
//				}else{
//					mapper.delByBizBill(params);
//				}*/
//				mapper.delByBizBill(params);
//				resultObj.setStatus(RET_STATUS_SUCESS);
//				resultObj.setData("操作成功！");
//			}
//		}catch(Exception e){
//			logger.error("API作废bizMap错误:"+e.getMessage());
//			resultObj.setStatus(RET_STATUS_NOT_FOUND);
//			resultObj.setData("操作失败！");
//		}finally{
//			sqlSession.close();
//		}
//		return resultObj;
//	}
	
	/**
	 * 删除关系(作废)V2
	 * @param params
	 * @return
	 */
	public APIResultObject delBizMapData(Map<String,Object> params){
		APIResultObject resultObj = new APIResultObject();
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSBusinessMapMapper mapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
			List<DTSBusinessMapDto> list = mapper.findByBizBillUser(params);
			if(list.isEmpty()){ //没有绑定关系，不做处理
				resultObj.setStatus(RET_STATUS_NOT_FOUND);
				resultObj.setData("该业务和单据没有对应的会员和客户绑定关系！");
			}else{
				mapper.delByBizBillUser(params);
				resultObj.setStatus(RET_STATUS_SUCESS);
				resultObj.setData("操作成功！");
			}
		}catch(Exception e){
			logger.error("API作废bizMap错误:"+e.getMessage());
			resultObj.setStatus(RET_STATUS_NOT_FOUND);
			resultObj.setData("操作失败！");
		}finally{
			sqlSession.close();
		}
		return resultObj;
	}
	
	/**
	 * 金融激活
	 * @param params
	 * @return
	 */
	public APIResultObject bizMapActive(Map<String,Object> params){
		APIResultObject resultObj = new APIResultObject();
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSBusinessMapMapper mapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
			List<DTSBusinessMapDto> list = mapper.findByBizBill(params);
			if(list.isEmpty()){ //没有绑定关系，不做处理
				resultObj.setStatus(RET_STATUS_NOT_FOUND);
				resultObj.setData("该业务和单据没有对应的会员和客户绑定关系！");
			}else{
				mapper.activeByBizBill(params);
				resultObj.setStatus(RET_STATUS_SUCESS);
				resultObj.setData("操作成功！");
			}
		}catch(Exception e){
			logger.error("API激活bizMap错误:"+e.getMessage());
			resultObj.setStatus(RET_STATUS_NOT_FOUND);
			resultObj.setData("操作失败！");
		}finally{
			sqlSession.close();
		}
		return resultObj;
	}
}
