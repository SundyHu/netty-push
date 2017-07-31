package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectSer {

	public ObjectSer() {

	}
	
	 /**
     * 对象序列化为字符串
     */
    public static String serialize(Object obj) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);  
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");//必须是ISO-8859-1
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");//编码后字符串不是乱码（不编也不影响功能）
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    /**
     * 字符串 反序列化为 对象
     */
    public static Object unSerialize(String serStr) throws Exception {
        String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream); 
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }

}
