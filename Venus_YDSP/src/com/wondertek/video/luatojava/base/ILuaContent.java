package com.wondertek.video.luatojava.base;

import org.json.JSONArray;

public interface ILuaContent {
	
	/**
	 * 执行java函数返回LuaResult格式数据
	 * 
	 * @param action 		执行函数名称
	 * @param args 			json格式参数表
	 * @return 				返回执行函数的一个LuaResult类型数据结果
	 */
	public LuaResult execute(String action, JSONArray args, String callbackId);
	
	/**
	 * 声明某些函数为异步调用接口
	 * @param action	执行函数名称
	 * @return 返回接口是否为异步执行接口
	 */
	public boolean isSynch(String action);
}
