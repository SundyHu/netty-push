package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
/**
 * desc:字符串压缩/解压工具，提供gzip和zip两种方式，压缩后的字符串使用base64转码
 * */
 
public class ZipUtils {
 
    /**
     * 
     * 使用gzip进行压缩
     * @throws IOException 
     */
    public static String gzip(String primStr) throws IOException {
    	String str= new Base64().encodeAsString(primStr.getBytes());
    	if (str == null || str.length() == 0) {
    	      return str;
    	    }
    	    ByteArrayOutputStream out = new ByteArrayOutputStream();
    	    GZIPOutputStream gzip = new GZIPOutputStream(out);
    	    gzip.write(str.getBytes());
    	    gzip.close();
		return out.toString("ISO-8859-1");
    }
 
    /**
     * 使用gzip进行解压缩
     * @param compressedStr
     * @return 解压后的字符串
     * @throws IOException 
     */
    public static String gunzip(String str) throws IOException {
    	 if (str == null || str.length() == 0) {
    	      return str;
    	    }
    	    ByteArrayOutputStream out = new ByteArrayOutputStream();
    	    ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
    	    GZIPInputStream gunzip = new GZIPInputStream(in);
    	    byte[] buffer = new byte[256];
    	    int n;
    	    while ((n = gunzip.read(buffer)) >= 0) {
    	      out.write(buffer, 0, n);
    	    }
        return new String(new Base64().decode(out.toString()),"UTF-8") ;
    }
    
    public static void main(String[] args) throws IOException {
		System.out.println(gzip("大神给发的撒范德萨广东省广东省高"));
		System.out.println(gunzip(gzip("大神给发的撒范德萨广东省广东省高")));
		
	}
}