package com.wondertek.video.luatojava.base;

import org.json.JSONArray;
import org.json.JSONObject;

public class LuaResult {
	private final int status;
	private final String message;
	
	public LuaResult(Status status) {
		this.status = status.ordinal();
		this.message = "'" + LuaResult.StatusMessages[this.status] + "'";
	}
	
	public LuaResult(Status status, String message) {
		this.status = status.ordinal();
		this.message = JSONObject.quote(message);
	}

	public LuaResult(Status status, JSONArray message) {
		this.status = status.ordinal();
		this.message = message.toString();
	}

	public LuaResult(Status status, JSONObject message) {
		this.status = status.ordinal();
		this.message = message.toString();
	}

	public LuaResult(Status status, int i) {
		this.status = status.ordinal();
		this.message = ""+i;
	}

	public LuaResult(Status status, float f) {
		this.status = status.ordinal();
		this.message = ""+f;
	}

	public LuaResult(Status status, boolean b) {
		this.status = status.ordinal();
		this.message = ""+b;
	}
	
	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
	
	public String getJSONString() {
		return "{status:" + this.status + ",message:" + this.message + "}";
	}
	
	public static String[] StatusMessages = new String[] {
		"No result",
		"OK",
		"OK data is async",
		"Class not found",
		"Invalid action",
		"JSON error",
		"Error"
	};
	
	public enum Status {
		NO_RESULT,
		OK,
		OK_ASYNC,
		CLASS_NOT_FOUND_EXCEPTION,
		INVALID_ACTION,
		JSON_EXCEPTION,
		ERROR
	}
}
