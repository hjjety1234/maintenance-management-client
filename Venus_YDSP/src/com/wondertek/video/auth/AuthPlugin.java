package com.wondertek.video.auth;

import com.telecom.sdk_auth_ui.OnLoginListener;
import com.telecom.sdk_auth_ui.OnModifyListener;
import com.telecom.sdk_auth_ui.TysxOA;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
public class AuthPlugin {		
		
	private static AuthPlugin instance = null;
	private VenusActivity venusHandle = null;
	private TysxOA m_TysxOA = null;
	public OnModifyListener m_onModifyListener = null;
	public OnLoginListener m_onLoginListener = null;
	
	static {
    	System.load(VenusApplication.appAbsPath + "/lib2/auth/libauth.so");
    }
	
	private AuthPlugin(VenusActivity va)
	{
		venusHandle = va;
		m_TysxOA  =new TysxOA();
		m_onModifyListener = new OnModifyListener() {
			
			@Override
			public void onModifyPwdClick(String arg0) {
				// TODO Auto-generated method stub
				nativemodifypwdcb(arg0);
			}
		};
		
		m_onLoginListener = new OnLoginListener() {
			
			@Override
			public void OnLoginClick(String arg0) {
				// TODO Auto-generated method stub
				nativelogincb(arg0);
			}
		};
	}
	
	public static AuthPlugin getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new AuthPlugin(va);
		}
		
		return instance;
	}
	
	
	/*
	javaLoading
	*/
	String javaLoading(String devId,String appId,String appSecret,String sdkUrl,String channelId)
	{
		String returnStr = m_TysxOA.loading(venusHandle.appActivity, devId, appId, appSecret, sdkUrl, channelId);
		Util.Trace("javaLoading returnStr=" + returnStr);
		return returnStr;
	}
	
	/*
	javaLogin
	*/
	void javaLogin(String userToken, String devId, String appId, String appSecret)
	{
		m_TysxOA.login(venusHandle.appActivity, userToken, devId, appId, appSecret,m_onLoginListener);
	}
	
	/*
	javaLogout
	*/
	String javaLogout(String userToken, String devId, String appId, String appSecret)
	{
		return m_TysxOA.logout(userToken, devId, appId, appSecret);
	}
	
	/*
	javaModifyUserPwd
	*/
	void javaModifyUserPwd(String userToken, String devId, String appId, String appSecret)
	{
		m_TysxOA.modifyUserPwd(venusHandle.appActivity, userToken, devId, appId, appSecret, m_onModifyListener);
	}
	
	/*
	javaAuthorize
	*/
	String javaAuthorize(String userToken, String devId, String appId, String appSecret, String productId, String contentId)
	{
		return m_TysxOA.authorize(userToken, devId, appId, appSecret, productId, contentId);
	}
	/*
	javaSubscribe
	*/
	String javaSubscribe(String userToken, String devId, String appId, String appSecret, String productId, String contentId, String purchaseType, String partner, String seller, String notify_url)
	{
		return m_TysxOA.subscribe(userToken, devId, appId, appSecret, productId, contentId, purchaseType, partner, seller, notify_url);
	}
	/*
	javaGetUserSubscribe
	*/
	String javaGetUserSubscribe(String userToken, String devId, String appId, String appSecret)
	{
		return m_TysxOA.getUserSubscribe(userToken, devId, appId, appSecret);
	}
	/*
	javaUnSubscribe
	*/
	String javaUnSubscribe(String userToken, String devId, String appId, String appSecret, String productId, String subId)
	{
		return m_TysxOA.unSubscribe(userToken, devId, appId, appSecret, productId, subId);
	}
	/*
	javaGetPlayInfo
	*/
	String javaGetPlayInfo(String userToken, String devId, String appId, String appSecret, String pType, String productId, String contentId)
	{
		return m_TysxOA.getPlayInfo(userToken, devId, appId, appSecret, pType, productId, contentId);
	}
	
	public native void nativelogincb(String strResult);
	public native void nativemodifypwdcb(String strResult);
}
