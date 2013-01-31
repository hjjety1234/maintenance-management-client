/**
* 工程名: 	SasMobile
* 文件名: 	GetSingleHandler.java
* 创建人:  	ycxiong
* 创建时间: 	2011-7-11 下午04:04:53
* 版权所有：	Copyright (c) 2011 苏州科大恒星信息技术有限公司  
* 文件描述: 描述该文件的作用
* -----------------------------变更记录 ----------------------------- 
* 日期        		变更人      		版本号  		变更描述  
* ------------------------------------------------------------------  
* 2011-7-11     ycxiong   	1.0.0       	first created  
*/
package com.ahmobile.ar.ustc.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.ahmobile.ar.ustc.domain.HttpGetSingleResult;


/**
 * @since SasMobile 1.0.0
 * @version 1.0 2011-7-11
 * @author ycxiong
 */
public class GetSingleHandler extends JsonHandler<HttpGetSingleResult> {

	/**
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 * @see cn.com.starit.sas.mobile.handlers.JsonHandler#extractFromJson(org.json.JSONObject)
	 */
	@Override
	protected HttpGetSingleResult extractFromJson(JSONObject jsonObj) throws JSONException 
	{
		HttpGetSingleResult httpGetSingleResult = new HttpGetSingleResult();
		String result = jsonObj.getString("result");
		String desc = jsonObj.getString("desc");
		httpGetSingleResult.setResult(result);
		httpGetSingleResult.setDesc(desc);
		httpGetSingleResult.setMap(jsonObjToMap(jsonObj));
		
		return httpGetSingleResult;
	}

}
