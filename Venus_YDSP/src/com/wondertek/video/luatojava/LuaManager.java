package com.wondertek.video.luatojava;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;

import com.wondertek.video.Util;
import com.wondertek.video.luatojava.base.Constants;
import com.wondertek.video.luatojava.base.ILuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class LuaManager {
	private HashMap<String, ILuaContent> luas = new HashMap<String,ILuaContent>();
	private HashMap<String, String> luanames = new HashMap<String,String>();
	private static LuaManager instance = null;
	
	private LuaManager() {
		loadLuaConfig();
	}
	
	public void loadLuaConfig() {
		// TODO Auto-generated method stub
		for(int i =0; i < Constants.wapFeatureList.length; i++)
		{
			addLua(Constants.wapFeatureList[i].luaName,Constants.wapFeatureList[i].luaClass);
		}
	}

	public static LuaManager getInstance()
	{
		if(instance == null)
			instance = new LuaManager();
		return instance;
	}
	
	private void addLua(String luaName, String luaClass) {
		// TODO Auto-generated method stub
		luanames.put(luaName, luaClass);
	}


	public String exec(final String luaName,final String action, final String jsonArgs, final boolean async, final String callbackId) {
		boolean runAsync = async;
		LuaResult cr = null;
		try {
			final JSONArray args = new JSONArray(jsonArgs);
			final ILuaContent luaContent = this.getLuaContent(luaName);
			if (luaContent != null) {
				runAsync = async && !luaContent.isSynch(action);
				if (runAsync) {
					Thread thread = new Thread(new Runnable() {
						public void run() {
							LuaResult asyncCr = null;
							try {
								// Call execute on the plugin so that it can do it's thing
								asyncCr = luaContent.execute(action, args, callbackId);
								Util.Trace("callbackId=" + callbackId);
								Util.Trace("asyncCr=" + asyncCr.getJSONString());
							} catch (Exception e) {
								
							}
							if(!callbackId.trim().equals("") && asyncCr!=null && asyncCr.getStatus() != LuaResult.Status.NO_RESULT.ordinal())
							{
								Util.Trace("nativeAsyncRet");
								nativeAsyncRet(callbackId,asyncCr.getJSONString());
							}
						}
					});
					thread.start();
					return new LuaResult(LuaResult.Status.OK_ASYNC).getJSONString();
				} else {
					cr = luaContent.execute(action, args, callbackId);
				}
			}
			else
			{
				cr = new LuaResult(LuaResult.Status.CLASS_NOT_FOUND_EXCEPTION);
			}
		} catch (JSONException e) {
			Util.Trace("ERROR: "+e.toString());
			cr = new LuaResult(LuaResult.Status.JSON_EXCEPTION);
		}
		return ( cr != null ? cr.getJSONString() : "{ status: 0, message: 'all good' }" );
	}

	public void addService(String serviceType, String className) {
		luanames.put(serviceType, className);
    }
	
	private ILuaContent getLuaContent(String luaName) {
		// TODO Auto-generated method stub
		String classname = luanames.get(luaName);
    	if (luas.containsKey(classname)) {
    		return this.luas.get(classname);
    	} else {
	    	return this.addPlugin(luaName, classname);
	    }
	}
	
	@SuppressWarnings("rawtypes")
	private ILuaContent addPlugin(String luaName, String className) {
		try {
			Class c = getClassByName(className);
			if (isLuaContent(c)) {
				ILuaContent luaContent = (ILuaContent)c.newInstance();
				luas.put(className, luaContent);
				return luaContent;
			}
    	} catch (Exception e) {
    		  e.printStackTrace();
    		  System.out.println("Error adding LuaContent "+className+".");
    	}
    	return null;
    }
	
	@SuppressWarnings("rawtypes")
	private Class getClassByName(final String clazz) throws ClassNotFoundException {
		Class c = null;
		if (clazz != null) {
			c = Class.forName(clazz);
		}
		return c;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isLuaContent(Class c) {
		if (c != null) {
			return com.wondertek.video.luatojava.base.LuaContent.class.isAssignableFrom(c) || com.wondertek.video.luatojava.base.ILuaContent.class.isAssignableFrom(c);
		}
		return false;
	}
	
	public String javaLuaToJava(String luaName, String action,  String jsonArgs,  boolean async, String callbackId)
	{
		Util.Trace("==javaLuaToJava==");
		return exec(luaName, action, jsonArgs, async, callbackId);
	}
	
	public native void nativeAsyncRet(String callbackId, String strRet);
}
