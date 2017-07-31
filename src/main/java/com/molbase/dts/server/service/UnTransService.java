package com.molbase.dts.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.constant.ServerConstants;
import com.molbase.dts.server.dao.DTSProcessLogMapper;
import com.molbase.dts.server.service.pagination.DefaultPagination;
import com.molbase.dts.server.service.pagination.Pagination;
import com.molbase.dts.server.service.pagination.QueryHandler;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;

public class UnTransService implements ServerConstants {

	static Logger logger = Logger.getLogger(UnTransService.class);
	
	SqlSessionFactory sqlSessionFactory;
	
	public UnTransService(){
		this.sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
	}
	
	public Pagination<Map<String,Object>> pageQueryUntrans(int pageIndex, int pageSize){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession(true);
			final DTSProcessLogMapper mapper = sqlSession.getMapper(DTSProcessLogMapper.class);
			return new DefaultPagination<Map<String,Object>>(pageIndex, pageSize, new QueryHandler<Map<String,Object>>() {
				@Override
				public int getTotalElements() {
					return Integer.parseInt(mapper.countByType(LOG_TYPE_TRANSFAIL).get("total").toString());
				}
				@Override
				public List<Map<String,Object>> getCurrData(final int pageIndex, int pageSize) {
				    return mapper.pageQueryByType(new HashMap<String, Object>(){
						private static final long serialVersionUID = 581472812908477048L;
						{
				    		put("start", (pageIndex-1) * PAGE_SIZE);
				    		put("size", PAGE_SIZE);
				    		put("type", LOG_TYPE_TRANSFAIL);
				    	}
				    });
				}
			}, PAGINATION_LINKNUM);
		}finally{
			sqlSession.close();
		}
	}
}
