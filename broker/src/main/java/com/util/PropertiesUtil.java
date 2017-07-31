package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class PropertiesUtil {
	
	/**
	 * Load properties from the given resource.
	 * @param resource the resource to load from
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 */
	public static Properties loadProperties(Resource resource) throws IOException {
		Properties props = new Properties();
		fillProperties(props, resource);
		return props;
	}

	/**
	 * Fill the given properties from the given resource.
	 * @param props the Properties instance to fill
	 * @param resource the resource to load from
	 * @throws IOException if loading failed
	 */
	public static void fillProperties(Properties props, Resource resource) throws IOException {
		InputStream is = resource.getInputStream();
		try {
			props.load(is);
		}
		finally {
			if(null!=is){
				is.close();
			}else{
				System.out.println("is null");
			}
			
		}
	}
	
	public static void fillProperties(Properties props,InputStream is) throws IOException {
		try {
			props.load(is);
		}
		finally {
			if(null!=is){
				is.close();
			}else{
				System.out.println("is null");
			}
			
		}
	}
	
	public static Map<String, String> toMap(Properties props){
		Iterator<Object> ite = props.keySet().iterator();
		HashMap<String, String> map = new HashMap<String, String>();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			String value = props.getProperty(key);
			map.put(key, value);
		}
		return map;
	}
	
}
