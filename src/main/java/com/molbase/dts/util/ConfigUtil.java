package com.molbase.dts.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.molbase.dts.server.listener.ApplicationListener;
import com.util.Config;
import com.util.PropertiesUtil;

public class ConfigUtil {

	public static void init() throws IOException{
		InputStream is = ApplicationListener.class.getResourceAsStream("/config.properties");
		Properties properties = new Properties();
		properties.load(is);
		is.close();
		Config.apps.putAll(PropertiesUtil.toMap(properties));
	}
	
}
