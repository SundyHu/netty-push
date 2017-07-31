package com.molbase.dts.server.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.molbase.dts.server.dao.DTSObjectMetaMapper;
import com.molbase.dts.server.dto.DTSObjectMetaDto;
import com.molbase.dts.server.dto.TupleObjectMetaType;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;

public class CacheObjMetaData {

	static Logger logger = Logger.getLogger(CacheObjMetaData.class);
	
	private static final ConcurrentHashMap<ObjectMetaType,DTSObjectMetaDto> map = new ConcurrentHashMap<ObjectMetaType,DTSObjectMetaDto>();
	
	private static SqlSessionFactory sqlSessionFactory;
	
	static{
		sqlSessionFactory = MybatisSqlSessionUtil.getSqlSessionFactory(DataSourceEnvironment.dts);
	}
	
	private CacheObjMetaData() {}
	
	public static void load(){
		SqlSession sqlSession = null;
		try{
			sqlSession = sqlSessionFactory.openSession();
			List<DTSObjectMetaDto> list = sqlSession.getMapper(DTSObjectMetaMapper.class).queryAll();
			for(DTSObjectMetaDto dto:list){
				map.put(new ObjectMetaType(dto.getType()), dto);
			}
			list = null;
		}finally{
			sqlSession.close();
			sqlSession = null ;
		}
	}
	
	public static DTSObjectMetaDto getCachedDTSObjectMetaDto(ObjectMetaType type){
		return map.get(type);
	}
	
	public static boolean containMetaType(String key){
		return map.containsKey(new ObjectMetaType(key));
	}
	
	public static TupleObjectMetaType parseObjectMetaTypeMap(String metaTypeStr){
		try{
			String[] arr = metaTypeStr.split("@");
			ObjectMetaType source = ObjectMetaType.contain(arr[0]);
			ObjectMetaType target = ObjectMetaType.contain(arr[1]);
			if(null!=source && null!=target){
				return new TupleObjectMetaType(source, target);
			}
			return null;
		}catch(Exception e){
			logger.error("错误，parseObjectMetaTypeMap："+e.getMessage());
			return null;
		}
	}
	
	public static class ObjectMetaType {

		@Override
		public boolean equals(Object obj) {
			return this.toString().equals(obj.toString());
		}

		@Override
		public int hashCode() {
			return this.metaType.hashCode();
		}

		private String metaType;

		public String getMetaType() {
			return metaType;
		}

		@Override
		public String toString() {
			return this.metaType;
		}

		protected ObjectMetaType(String metaType) {
			this.metaType = metaType;
		}
		
		public static ObjectMetaType contain(String key){
			return (null!=key && CacheObjMetaData.containMetaType(key))? new ObjectMetaType(key):null;
		}
		
		@SuppressWarnings("unused")
		private ObjectMetaType(){}
	}
}
