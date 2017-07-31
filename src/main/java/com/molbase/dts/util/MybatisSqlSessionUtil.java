package com.molbase.dts.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

/**
 * mybatis连接初始化和sqlSessionFactory获取
 * @author changgen.xu
 */
public class MybatisSqlSessionUtil {

	static Logger logger = Logger.getLogger(MybatisSqlSessionUtil.class);
	
	public static enum DataSourceEnvironment {
        dts,
        molbase;
    }
	
	private static ConcurrentHashMap<DataSourceEnvironment, SqlSessionFactory> 
		sqlSessionFacotryMap = new ConcurrentHashMap<DataSourceEnvironment, SqlSessionFactory>();
	
	/**
	 * 初始化SqlSessionFactory
	 * @param DataSourceEnvironment id
	 */
	public static void initSqlSessionFactory(DataSourceEnvironment id){
		if(!sqlSessionFacotryMap.containsKey(id)){
			InputStream is = null;
			try {
				is = Resources.getResourceAsStream("Configuration.xml");
				sqlSessionFacotryMap.put(id, new SqlSessionFactoryBuilder().build(is, id.toString()));
			} catch (Exception e) {
				logger.error("MybatisSqlSessionUtil初始化SqlSessionFacotry失败："+e.getMessage());
			} finally{
				try {
					if(null!=is){
						is.close();
					}
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	/**
	 * 获取SqlSessionFactory
	 * @param DataSourceEnvironment id
	 * @return
	 */
	public static SqlSessionFactory getSqlSessionFactory(DataSourceEnvironment id) {
		try {
			if(!sqlSessionFacotryMap.containsKey(id)){
				initSqlSessionFactory(id);
			}
			return sqlSessionFacotryMap.get(id);
		} catch (Exception e) {
			logger.error("获取sqlSessionFacotry错误："+id.toString()+",错误信息："+e.getMessage());
			return null;
		}
	}
	
}
