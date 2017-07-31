package com.molbase.dts.server.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import com.molbase.dts.server.cache.CacheObjMetaData;
import com.molbase.dts.server.netty.NettyServerBootstrap;
import com.molbase.dts.util.ConfigUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil;
import com.molbase.dts.util.MybatisSqlSessionUtil.DataSourceEnvironment;
import com.util.Config;
/**
 * 系统启动初始化mybatis连接池
 */
@WebListener
public class ApplicationListener implements ServletContextListener {

	static Logger logger = Logger.getLogger(ApplicationListener.class);
    public ApplicationListener() {
    	
    }

    public void contextDestroyed(ServletContextEvent sce) {
    	NettyServerBootstrap nettyServerBootstrap=NettyServerBootstrap.getInstance();
    	if(null!=nettyServerBootstrap){
    		nettyServerBootstrap.close();
    	}
    }

    public void contextInitialized(ServletContextEvent sce)  { 
    	try {
    		logger.info("系统启动，初始化Mybatis连接池:"+DataSourceEnvironment.dts.toString());
        	MybatisSqlSessionUtil.initSqlSessionFactory(DataSourceEnvironment.dts);
        	logger.info("设置全局变量contextPath:"+sce.getServletContext().getContextPath());
        	sce.getServletContext().setAttribute("contextPath", sce.getServletContext().getContextPath());
        	logger.info("加载object_meta数据到缓存map");
        	CacheObjMetaData.load();
        	
			ConfigUtil.init();
			logger.info("---------初始化配置文件----------config.properties");
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error("---------初始化配置文件----------config.properties-----失败---"+e1);
		}
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NettyServerBootstrap.instance(Integer.parseInt(Config.apps.get("port"))).start();
				}catch (Exception e) {
					e.printStackTrace();
					logger.error("netty server start failed---------"+e);
				}
			}
		}).start();
	}
	
}
