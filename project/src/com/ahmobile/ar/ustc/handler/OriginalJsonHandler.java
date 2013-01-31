package com.ahmobile.ar.ustc.handler;

import org.json.JSONException;
import org.json.JSONObject;

public class OriginalJsonHandler extends JsonHandler<JSONObject> {
	

	@Override
	protected JSONObject extractFromJson(JSONObject jsonObj)
			throws JSONException {
		
		return jsonObj;
	}

}
