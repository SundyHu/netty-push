package com.molbase.dts.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.dao.DTSBusinessMapMapper;
import com.molbase.dts.server.dao.DTSBusinessMetaMapper;
import com.molbase.dts.server.dto.DTSBusinessMapDto;
import com.molbase.dts.server.dto.DTSBusinessMapEnum;
import com.molbase.dts.server.dto.DTSBusinessMetaDto;
import com.molbase.dts.server.service.pagination.DefaultPagination;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.server.service.pagination.QueryHandler;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;

public class BusinessService {
	
	static Logger logger = Logger.getLogger(BusinessService.class);
	
	SqlSessionFactory sqlSessionFactory;
	
	static final int LINKNUM = 6;
	
	public BusinessService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
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
			}, LINKNUM);
		}finally{
			sqlSession.close();
		}
	}
	
	public int saveBizDto(DTSBusinessMapDto dto){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSBusinessMapMapper mapper = sqlSession.getMapper(DTSBusinessMapMapper.class);
			return mapper.insert(dto);
		}finally{
			sqlSession.close();
		}
	}
	
	public Map<String,Object> findBizMetaById(int id){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			return sqlSession.getMapper(DTSBusinessMetaMapper.class).findById(id);
		}finally{
			sqlSession.close();
		}
	}
	
	public int saveBizMetaInfo(DTSBusinessMetaDto dto){
		if(null == dto) return 0;
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			DTSBusinessMetaMapper mapper = sqlSession.getMapper(DTSBusinessMetaMapper.class);
			return dto.getId()>0?mapper.update(dto):mapper.insert(dto);
		}finally{
			sqlSession.close();
		}
	}
	
	public Pagination<Map<String,Object>> loadBizMetaList(int pageIndex, int pageSize, final Map<String,Object> params){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			final DTSBusinessMetaMapper mapper = sqlSession.getMapper(DTSBusinessMetaMapper.class);
			return new DefaultPagination<Map<String,Object>>(pageIndex, pageSize, new QueryHandler<Map<String,Object>>() {
				@Override
				public int getTotalElements() {
					return Integer.parseInt(mapper.queryToalbyParams(params).get("total").toString());
				}
				@Override
				public List<Map<String,Object>> getCurrData(int pageIndex, int pageSize) {
					return mapper.queryByParams(params);
				}
			}, LINKNUM);
		}finally{
			sqlSession.close();
		}
	}
	
	public List<Map<String,Object>> loadBizMetaListAll(){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			return sqlSession.getMapper(DTSBusinessMetaMapper.class).queryAll();
		}finally{
			sqlSession.close();
		}
	}
	
	public int bizStatusChange(int id, int status){
		if(!DTSBusinessMapEnum.hasValue(status)){
			return 0;
		}
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("id", id);
			params.put("status", status);
			return sqlSession.getMapper(DTSBusinessMapMapper.class).statusUpdateById(params);
		}finally{
			sqlSession.close();
		}
	}
}
