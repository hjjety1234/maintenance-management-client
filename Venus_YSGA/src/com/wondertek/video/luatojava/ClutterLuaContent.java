package com.wondertek.video.luatojava;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.cmcc.Login.SecurityLogin;
import com.android.cmcc.bean.CmccLocation;
import com.android.cmcc.bean.LocationParam;
import com.android.cmcc.listener.CmccLocationListener;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class ClutterLuaContent extends LuaContent implements CmccLocationListener {
	private static final String TAG = "ClutterLuaContent";
	private static final String ACTION_CTRACE = "CTrace";
	private static final String ACTION_CADD = "CAdd";
	private static final String ACTION_CCBTEST = "CCBTest";
	private static final String ACTION_CCBTESTTWO = "CCBTestTwo";
	private static final String ACTION_DELETECALLCONTENT = "DeleteCallContent";
	private static final String ACTION_JD_LOCATE = "GetLocationByJD";
	private static final String ACTION_GET_NETWORK_TYPE = "GetNetworkType";
	private static ClutterLuaContent instance = null;
	private static CmccLocation mCmccLocation = null;
	private static SecurityLogin mClient = null;
	private static LocationParam locParam = null;

	public static ClutterLuaContent getInstance() {
		if(instance == null) {
			instance = new ClutterLuaContent();
		}
		return instance;
	}
	
	/**
     * 
     */
    public ClutterLuaContent() {
    	instance = this;
    	mClient = new SecurityLogin(VenusActivity.appActivity);
		locParam = new LocationParam();
		locParam.setServiceId("AHKQXT201307191705");
		locParam.setOffSet(false);
		locParam.setLocType("2");
		mClient.setCmccLocationListener(this);
		mClient.setLocationParam(locParam);
		mClient.start();
    }
    
	/**
	 * @revised hewu <hewu2008@gmail.com>
	 * @date 2013/08/01 新增基地定位接口
	 */
	@Override
	public LuaResult execute(String action, JSONArray args) {
		LuaResult.Status status = LuaResult.Status.OK;
        String result = "";
        try {
        	if (action.equals(ACTION_CTRACE)) {
        		CTrace(args.getString(0));
        		return null;
        	} else if (action.equals(ACTION_CADD)) {
        		result += CAdd(args.getInt(0),args.getInt(1));
        	} else if(action.equals(ACTION_CCBTEST)) {
        		result += CCBTest(args.getInt(0),args.getInt(1));
        	} else if(action.equals(ACTION_CCBTESTTWO)){
        		CCBTestTwo(args.getString(0), args.getString(1), args.getString(2));
        		return null;
        	} else if(action.equals(ACTION_DELETECALLCONTENT)) {
        		DeleteCallContent();
        		return null;
        	}else if (action.equals(ACTION_GET_NETWORK_TYPE)) {
        		result = getNetworkType();
        	}else if (action.equals(ACTION_JD_LOCATE)) {
        		Log.d(TAG, "[execute] try to get current location...");
        		mCmccLocation = null;
        		locParam.setLocType("2");
    			locParam.setOffSet(false);
    			mClient.setLocationParam(locParam);
    			mClient.locCapability();
    			while (mCmccLocation == null) {
    				try {
    					Thread.sleep(500);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			result = String.format("%f %f", mCmccLocation.getLatitude(), mCmccLocation.getLongitude());
        	} else {
        		return new LuaResult(LuaResult.Status.INVALID_ACTION);
        	}
            return new LuaResult(status, result);
        } catch (JSONException e) {
            return new LuaResult(LuaResult.Status.JSON_EXCEPTION);
        }
	}

	@Override
	public boolean isSynch(String action) {
		if (action.equals(ACTION_CCBTEST)) {
			return false;
		}else if (action.equals(ACTION_JD_LOCATE)) {
			return false;
		}
		return true;
	}
	
	public void CTrace(String str) {
		Util.Trace("ClutterLuaContent:CTrace = " + str);
	}
	
	public int CAdd(int a, int b) {
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
		sendIntent.setDataAndType(Uri.parse("file:///mnt/sdcard/3D/TOPGIRL.mp4"), "video/mp4");
		VenusActivity.appActivity.startActivity(sendIntent); 
	}
	
	public void DeleteCallContent() {
		Util.Trace("DeleteCallContent");
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}

	@Override
	public void onReceiveLocation(CmccLocation cmccLocation) {
		mCmccLocation = cmccLocation;
	}
	
	/**
	* @Description 获取当前的网络类型
	* @return GSM, TD-SCDMA
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-18 下午2:30:53
	*/
	public String getNetworkType() {
		TelephonyManager teleMan =  
	            (TelephonyManager)VenusActivity.appActivity.getSystemService(VenusActivity.appContext.TELEPHONY_SERVICE);
		int networkType = teleMan.getNetworkType();
		String strType = "Unknown";
		switch (networkType) {
			case 7:
				strType = "1xRTT";
			    break;      
			case 4:
				strType = "CDMA";
			    break;      
			case 2:
				strType = "EDGE";
			    break;  
			case 14:
				strType = "eHRPD";
			    break;      
			case 5:
				strType = "EVDO rev. 0";
			    break;  
			case 6:
				strType = "EVDO rev. A";
			    break;  
			case 12:
				strType = "EVDO rev. B";
			    break;  
			case 1:
				strType = "GPRS";
			    break;      
			case 8:
				strType = "HSDPA";
			    break;      
			case 10:
				strType = "HSPA";
			    break;          
			case 15:
				strType = "HSPA+";
			    break;          
			case 9:
				strType = "HSUPA";
			    break;          
			case 11:
				strType = "iDen";
			    break;
			case 13:
				strType = "LTE";
			    break;
			case 3:
				strType = "UMTS";
			    break;          
			case 0:
				strType = "Unknown";
			    break;
		}
		Log.d(TAG, "[getNetworkType] strType:" + strType);
		return strType;
	}
}
