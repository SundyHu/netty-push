package com.molbase.dts.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author changgen.xu
 * @date 2016年3月5日 上午10:06:17
 * @desc HTTPClient GET/POST Util
 */
public class HttpUtil {

	private static ThreadLocal<CloseableHttpClient> httpClientContainer = new ThreadLocal<CloseableHttpClient>();
	private static final String CHARSET = "UTF-8";

	public static String get(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	public static String post(String url, Map<String, String> params) {
		return doPost(url, params, CHARSET);
	}

	/**
	 * HTTP Get 获取内容
	 * @param url 请求的url地址 ?之前的地址
	 * @param params 请求的参数
	 * @param charset 编码格式
	 * @return 页面内容
	 */
	private static String doGet(String url, Map<String, String> params, String charset) {
		if (StringUtil.isEmpty(url)) {
			return null;
		}
		CloseableHttpClient httpClient = getHttpClient();
		CloseableHttpResponse response = null;
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpGet httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeHttpClient(response);
		}
		return null;
	}

	/**
	 * HTTP Post 获取内容
	 * @param url 请求的url地址 ?之前的地址
	 * @param params 请求的参数
	 * @param charset 编码格式
	 * @return 页面内容
	 */
	private static String doPost(String url, Map<String, String> params, String charset) {
		if (StringUtil.isEmpty(url)) {
			return null;
		}
		CloseableHttpClient httpClient = getHttpClient();
		CloseableHttpResponse response = null;
		try {
			List<NameValuePair> pairs = null;
			if (params != null && !params.isEmpty()) {
				pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);
			if (pairs != null && pairs.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
			}
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeHttpClient(response);
		}
		return null;
	}

	private static CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpClient = httpClientContainer.get();
		if (httpClient == null) {
			RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(60000).build();
			httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		}
		httpClientContainer.set(httpClient);
		return httpClient;
	}

	private static void closeHttpClient(CloseableHttpResponse response) {
		CloseableHttpClient httpClient = httpClientContainer.get();
		try {
			if (null != response) {
				response.close();
			}
			if (null != httpClient) {
				httpClient.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClientContainer.remove();
		}
	}

}