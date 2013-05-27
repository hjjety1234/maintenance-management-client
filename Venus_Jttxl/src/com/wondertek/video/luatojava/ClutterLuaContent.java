package com.wondertek.video.luatojava;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentResolver;
import android.provider.CallLog;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class ClutterLuaContent extends LuaContent {
	private static final String ACTION_CTRACE = "CTrace";
	private static final String ACTION_CADD = "CAdd";
	private static final String ACTION_CCBTEST = "CCBTest";
	private static final String ACTION_CCBTESTTWO = "CCBTestTwo";
	private static final String ACTION_DELETECALLCONTENT = "DeleteCallContent";
	private static final String ACTION_DELETE_CALLLOG = "deleteCallLog";
	private static ClutterLuaContent instance = null;

	public static ClutterLuaContent getInstance()
	{
		if(instance == null)
		{
			instance = new ClutterLuaContent();
		}
		
		return instance;
	}
	
	/**
     * Constructor.
     */
    public ClutterLuaContent() {
    	instance = this;
    }
    
	@Override
	public LuaResult execute(String action, JSONArray args) {
		// TODO Auto-generated method stub
		LuaResult.Status status = LuaResult.Status.OK;
        String result = "";
        
        try {
        	if (action.equals(ACTION_CTRACE)) {
        		CTrace(args.getString(0));
        		return null;
        	}
        	else if (action.equals(ACTION_CADD)) {
        		result += CAdd(args.getInt(0),args.getInt(1));
        	}
        	else if(action.equals(ACTION_CCBTEST))
        	{
        		result += CCBTest(args.getInt(0),args.getInt(1));
        	}
        	else if(action.equals(ACTION_CCBTESTTWO))
        	{
        		CCBTestTwo(args.getString(0), args.getString(1), args.getString(2));
        		return null;
        	}
        	else if(action.equals(ACTION_DELETECALLCONTENT))
        	{
        		DeleteCallContent();
        		return null;
        	}
        	else if(action.equals(ACTION_DELETE_CALLLOG)) 
        	{
        		result += deleteCallLog(args.getString(0));
        	}
        	else
        	{
        		return new LuaResult(LuaResult.Status.INVALID_ACTION);
        	}
            return new LuaResult(status, result);
        } catch (JSONException e) {
            return new LuaResult(LuaResult.Status.JSON_EXCEPTION);
        }
	}

	@Override
	public boolean isSynch(String action) {
		// TODO Auto-generated method stub
		if (action.equals(ACTION_CCBTEST)) {
			return false;
		}
		return true;
	}
	
	public void CTrace(String str)
	{
		Util.Trace("ClutterLuaContent:CTrace = " + str);
	}
	
	public int CAdd(int a, int b)
	{
		return a + b;
	}
	
	public int CCBTest(int a, int b)
	{
		for(int i=0; i<b;i++)
		{
			try {
				Thread.sleep(a);
				Util.Trace("sleep i= " + i + ", time =" + a);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return a*b;
	}
	
	public void CCBTestTwo(String str1,String str2,String str3)
	{
		Util.Trace("CCBTestTwo =" + str1 + ","+str2+ ","+str3);
	}
	
	public void DeleteCallContent()
	{
		Util.Trace("DeleteCallContent");
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}
	
	public boolean deleteCallLog(String number) {
		Util.Trace("[deleteCallLog] number: " + number);
		if (number == null) {
			return false;
		}
		ContentResolver resolver = VenusActivity.appActivity
				.getContentResolver();
		int ret = resolver.delete(CallLog.Calls.CONTENT_URI,
				CallLog.Calls.NUMBER + "=?", new String[] { number });
		if (ret > 0)
			return true;
		else
			return false;
	}
}
