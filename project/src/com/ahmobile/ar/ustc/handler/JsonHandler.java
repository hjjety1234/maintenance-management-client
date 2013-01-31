package com.ahmobile.ar.ustc.handler;
/**
* 工程名: 	SasMobile
* 文件名: 	MapListHandler.java
* 创建人:  	ycxiong
* 创建时间: 	2011-7-9 下午06:22:49
* 版权所有：	Copyright (c) 2011 苏州科大恒星信息技术有限公司  
* 文件描述: 描述该文件的作用
* -----------------------------变更记录 ----------------------------- 
* 日期        		变更人      		版本号  		变更描述  
* ------------------------------------------------------------------  
* 2011-7-9     ycxiong   	1.0.0       	first created  
*/

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


/**
 * JSON数据处理器抽象基类，本系统所有JSON格式的数据转换，均从此类集成
 * @since SasMobile 1.0.0
 * @version 1.0 2011-7-9
 * @author ycxiong
 */
public abstract class JsonHandler<T> implements Handler<T>
{
	private static final String LOG_TAG = "JsonHandler";
	/**
	* @param response
	* @return 如果没有响应内容，则返回null
	* @exception SasException 如果格式非法，抛出运行时异常
	* @see cn.com.starit.sas.mobile.communication.HttpResponseHandler#handle(org.apache.http.HttpResponse)
	*/
	public T handle(String jsonText) {
		if (jsonText == null){
			return null;
		}
		try {
//			jsonText = EntityUtils.toString(response.getEntity());
//			if (jsonText == null || jsonText.trim().equals("")){
//				return null;
//			}
			JSONObject obj = new JSONObject(jsonText);
			return extractFromJson(obj);
		} catch (Exception e) {
			Log.e(LOG_TAG, "提取HTTP信息异常", e);
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * 从Json中提取信息
	* @param obj
	* @return
	 */
	protected abstract T extractFromJson(JSONObject jsonObj) throws JSONException;
	
	/**
	 * 将JSON数组转换为ListMap格式
	* @param jsonArray
	* @return 
	* @throws JSONException
	 */
	protected List<Map<String, String>> jsonArrayToListMap(JSONArray jsonArray) throws JSONException
	{
		if (jsonArray == null){
			return null;
		}
		List<Map<String,String>> list = new LinkedList<Map<String,String>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			Map<String, String> map = jsonObjToMap(jsonObj);
			if (map != null){
				list.add(map);
			}
		}
		return list;
	}
	
	/**
	 * 将简单JSON对象转换为Map
	* @param jsonObj
	* @return
	* @throws JSONException
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, String> jsonObjToMap(JSONObject jsonObj) throws JSONException
	{
		if (jsonObj == null){
			return null;
		}
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		Iterator it = jsonObj.keys();
		while(it.hasNext()){
			String key = (String)it.next();
			String value = jsonObj.getString(key);
			map.put(key, value);
		}
		return map;
	}
}
