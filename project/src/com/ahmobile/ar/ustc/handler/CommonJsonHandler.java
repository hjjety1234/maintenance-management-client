package com.ahmobile.ar.ustc.handler;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理Json对象
 * @author WINDFREE
 *
 */
public class CommonJsonHandler  extends JsonHandler<Map<String, String>  > {

	@Override
	protected Map<String, String> extractFromJson(JSONObject jsonObj)
			throws JSONException {
		return jsonObjToMap(jsonObj);
	}

}
