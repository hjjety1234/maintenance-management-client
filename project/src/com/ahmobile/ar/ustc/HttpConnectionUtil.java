package com.ahmobile.ar.ustc;
/**
 * 工程名: 	SasMobile
 * 文件名: 	RestExecutor.java
 * 创建人:  	ycxiong
 * 创建时间: 	2011-7-9 下午12:01:20
 * 版权所有：	Copyright (c) 2011 苏州科大恒星信息技术有限公司  
 * 文件描述: 描述该文件的作用
 * -----------------------------变更记录 ----------------------------- 
 * 日期        		变更人      		版本号  		变更描述  
 * ------------------------------------------------------------------  
 * 2011-7-9     ycxiong   	1.0.0       	first created  
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.ahmobile.ar.ustc.domain.HttpGetListResult;
import com.ahmobile.ar.ustc.handler.GetListHandler;

import android.util.Log;
/**
 * 执行Restful请求
 * 
 * @since SasMobile 1.0.0
 * @version 1.0 2011-7-9
 * @author ycxiong
 */
public class HttpConnectionUtil {
	/**
	 * 定义HTT请求方法
	 * 
	 * @author WINDFREE
	 * 
	 */
	public static enum HttpMethod {
		GET, POST
	}

	private static final String LOG_TAG = "HttpConnectionUtil";
	static HttpClient client = ApplicationEx.getHttpClient();

	/**
	 * 组装Rest请求完整的URL 适用于GET/PUT操作
	 * 
	 * @param ip
	 *            远程服务器IP地址和端口号，如:192.168.2.1:8080
	 * @param serv
	 *            REST服务目录,不带"/"
	 * @param params
	 *            参数数组
	 * @return URL
	 */
	public static String compHttpURL(String ipPort, String serv, String... params) {
		StringBuffer buf = new StringBuffer("http://");
		buf.append(ipPort).append("/").append(ApplicationEx.SERV_ROOT_ADDR).append("/").append(serv).append("/");
		for (String param : params) {
			buf.append(param);
			buf.append("/");
		}
		return buf.substring(0, buf.length() - 1);
	}

	/**
	 * 发送get请求
	 * 
	 * @param <T>
	 * @param url
	 * @param handler
	 *            HttpResponse响应消息处理器
	 * @return HttpResponse经handler处理器处理后返回的实体; 如果服务器返回失败，则返回null
	 * @throws IOException
	 */
	public static <T> T get(String url, Handler<T> handler)
			throws IOException {
		return syncConnect(url,null,HttpMethod.GET,handler);
	}

	/**
	 * 发送post请求
	 * 
	 * @param <T>
	 * @param url
	 * @param postParams
	 *            post消息参数
	 * @param handler
	 *            HttpResponse响应消息处理器
	 * @return HttpResponse经handler处理器处理后返回的实体; 如果服务器返回失败，则返回null
	 * @throws IOException
	 */
	public static <T> T post(String url, Map<String, String> postParams,
			Handler<T> handler) throws IOException {
		return syncConnect(url,postParams,HttpMethod.POST,handler);
	}
	/**
	 *  请求
	 * @param <T>
	 * @param url
	 * @param params
	 * @param method
	 * @param callback
	 * @return
	 */
	public static <T> T syncConnect(final String url,
			final Map<String, String> params, final HttpMethod method,
			final Handler<T> callback) throws IOException{
		String json = null;
		BufferedReader reader = null;
		Log.d(LOG_TAG, "url=" + url);
		try {
			HttpClient client = ApplicationEx.getHttpClient();
			HttpUriRequest request = getRequest(url, params, method);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					sb.append(s);
				}
				json = sb.toString();
			}
		} 
		catch (SocketTimeoutException e) {
			Log.e(LOG_TAG, "连接超时：" + e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
		return callback.handle(json);
	}

	/**
	 *  请求
	 * @param <T>
	 * @param url
	 * @param params
	 * @param method
	 * @param callback
	 * @return
	 */
	public static HttpGetListResult syncConnect(final String url,
			final Map<String, String> params, final HttpMethod method,
			final GetListHandler callback) throws IOException{
		String json = null;
		BufferedReader reader = null;
		Log.d(LOG_TAG, "url=" + url);
		try {
			HttpClient client = ApplicationEx.getHttpClient();
			HttpUriRequest request = getRequest(url, params, method);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					sb.append(s);
				}
				json = sb.toString();
			}
		} 
		catch (SocketTimeoutException e) {
			Log.e(LOG_TAG, "连接超时：" + e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
		return callback.handle(json);
	}
	
	/**
	 * 构造请求对象
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param method
	 *            方法
	 * @return
	 */
	private static HttpUriRequest getRequest(String url, Map<String, String> params,
			HttpMethod method) {
		if (method.equals(HttpMethod.POST)) {
			List<NameValuePair> listParams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String name : params.keySet()) {
					listParams.add(new BasicNameValuePair(name, params
							.get(name)));
				}
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						listParams,HTTP.UTF_8);
				HttpPost request = new HttpPost(url);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				// Should not come here, ignore me.
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {
//			if (url.indexOf("?") < 0) {
//				url += "?";
//			}
			if (params != null) {
				for (String name : params.keySet()) {
					url += "&" + name + "="
							+ URLEncoder.encode(params.get(name));
				}
			}
			HttpGet request = new HttpGet(url);
			return request;
		}
	}
}
