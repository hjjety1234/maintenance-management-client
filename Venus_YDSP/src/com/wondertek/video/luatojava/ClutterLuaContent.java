package com.wondertek.video.luatojava;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.CallLog;
//add pj
import android.util.Log;

import com.wondertek.video.DameonService;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;
//add pj


public class ClutterLuaContent extends LuaContent {
	private static final String ACTION_CTRACE = "CTrace";
	private static final String ACTION_CADD = "CAdd";
	private static final String ACTION_CCBTEST = "CCBTest";
	private static final String ACTION_CCBTESTTWO = "CCBTestTwo";
	//add pj
	private static final String ACTION_SETAPPOINTMENTENABLE = "SetAppointmentEnable";
	private static final String ACTION_DELETECALLCONTENT = "DeleteCallContent";
	//add pj
	private static final String TAG = "ClutterLuaContent";
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
	public LuaResult execute(String action, JSONArray args, String callbackId) {
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
        	else if(action.equals(ACTION_SETAPPOINTMENTENABLE)) //add pj
        	{
        		Log.d(TAG, " @@@ACTION_SETAPPOINTMENTENABLE: ");
        		DameonService.Service_setAppointmentEnable(args.getInt(0));
        		return null;
        	}
        	else if(action.equals(ACTION_DELETECALLCONTENT))
        	{
        		DeleteCallContent();
        		return null;
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
		Intent sendIntent =new Intent();
		sendIntent.setAction(Intent.ACTION_VIEW);

//		sendIntent.setData(Uri.parse("file:///mnt/sdcard/3D/TOPGIRL.mp4"));
		sendIntent.setDataAndType(Uri.parse("file:///mnt/sdcard/3D/TOPGIRL.mp4"), "video/mp4");

//		sendIntent.setType("video/mp4");
//		sendIntent.setComponent(new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivit"));
//		final PackageManager packageManager = VenusActivity.appActivity.getPackageManager();
//        List<ResolveInfo> list = packageManager.queryIntentActivities(sendIntent, PackageManager.GET_ACTIVITIES);
//        if (list.size() > 0) {
//        	VenusActivity.appActivity.startActivity(Intent.createChooser(sendIntent, "选择应用"));
//   
//        } else {
//        }
        
		VenusActivity.appActivity.startActivity(sendIntent); 
	}
	
	public void DeleteCallContent()
	{
		Util.Trace("DeleteCallContent");
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}
}
