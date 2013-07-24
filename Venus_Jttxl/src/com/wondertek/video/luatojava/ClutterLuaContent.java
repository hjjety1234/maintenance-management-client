package com.wondertek.video.luatojava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import android.util.Log;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class ClutterLuaContent extends LuaContent {
	private static final String ACTION_CTRACE = "CTrace";
	private static final String ACTION_CADD = "CAdd";
	private static final String ACTION_CCBTEST = "CCBTest";
	private static final String ACTION_CCBTESTTWO = "CCBTestTwo";

	private static final String ACTION_SETMAPZOOM = "SetMapZoom";
	private static final String ACTION_DELETECALLCONTENT = "DeleteCallContent";
	
	private static final String TAG = "ClutterLuaContent";
	private static final String ACTION_DELETE_CALLLOG = "deleteCallLog";
	private static final String ACTION_BACKUP_APK = "backupApk";
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
        	else if(action.equals(ACTION_SETMAPZOOM))
        	{
        		Log.d(TAG, " @@@ACTION_SETMAPZOOM: ");
        		// GDMapManager.getInstance().setMapZoom(args.getInt(0));
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
        	else if (action.equals(ACTION_BACKUP_APK)) 
        	{
        		result += backupApplication(args.getString(0), args.getString(1));
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

	/**
	* @Description 将app由data/app目录拷贝到sd卡下的指定目录中
	* @param appId 应用程序的ID号，如com.wondertek.jttxl
	* @param dest 需要将应用程序拷贝的目标位置
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-7-22 下午3:32:12
	*/
	private String backupApplication(String appId, String dest) {
		if (appId == null || appId.length() == 0 
				|| dest == null || dest.length() == 0) { 
			return "illegal parameters";
		}
		Util.Trace("[backupApplication] appId: " + appId + ", dest:" + dest);
		// check file /data/app/appId-1.apk exists
		String apkPath = "/data/app/" + appId + "-1.apk";
		File apkFile = new File(apkPath);
		if (apkFile.exists() == false) {
			return apkPath +  " doesn't exist!";
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(apkFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		// create dest folder if necessary
		int i = dest.lastIndexOf('/');
		if (i != -1) {
			File dirs = new File(dest.substring(0, i));
			dirs.mkdirs();
			dirs = null;
		}
		// do file copy operation
		byte[] c = new byte[1024];
		int slen;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			while ((slen = in.read(c, 0, c.length)) != -1)
				out.write(c, 0, slen);
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return e.getMessage();
				}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return e.getMessage();
				}
			}
		}
		return "success";
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
//        	VenusActivity.appActivity.startActivity(Intent.createChooser(sendIntent, "ѡ��Ӧ��"));
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
