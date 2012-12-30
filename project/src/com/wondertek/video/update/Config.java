package com.wondertek.video.update;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

public class Config {
	
	private static String update_server				= null;
	private static String update_res_name 			= null;
	private static String update_res_ver_json		= null;
	private static boolean INSTALL_VENUS_ZIP_LIVE		= false;
	
	private static final String updateID			= ".updateID";
	
	//public static final String UPDATE_SERVER = "http://192.168.1.191:8000/mobilevideo3/speedTest/";//"http://10.20.147.117/jtapp12/";
	//public static final String UPDATE_SERVER = "http://180.168.71.6:9009/mobilevideo3/speedTest/";
	//public static final String UPDATE_APKNAME = "jtapp-12-updateapksamples.apk";
	//public static final String UPDATE_VERJSON = "ver.json";
	//public static final String UPDATE_SAVENAME = "updateapksamples.apk";
	
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			VenusApplication.getInstance();
			verCode = context.getPackageManager().getPackageInfo(
					VenusApplication.packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			Util.Trace(e.getMessage());
		}
		return verCode;
	}
	
	public static String getVerName(Context context) {
		String verName = "";
		try {
			VenusApplication.getInstance();
			verName = context.getPackageManager().getPackageInfo(
					VenusApplication.packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			Util.Trace(e.getMessage());
		}
		return verName;	

	}
	
	public static void setUpdateServer(String s)
	{
		update_server = s;
	}
	
	public static String getUpdateServer()
	{
		return update_server;
	}
	
	
	public static void setUpdateResName(String s)
	{
		update_res_name = s;
	}
	
	public static String getUpdateResName()
	{
		return update_res_name;
	}
	
	
	public static void setUpdateResVerJSON(String s)
	{
		update_res_ver_json = s;
	}
	
	public static String getUpdateResVerJSON()
	{
		return update_res_ver_json;
	}
	
	public static void setInstallVenusZipLive(boolean live)
	{
		INSTALL_VENUS_ZIP_LIVE = live;
	}
	
	public static boolean getInstallVenusZipLive()
	{
		return INSTALL_VENUS_ZIP_LIVE;
	}
	
	public static String getUpdateIndication()
	{
		return updateID;
	}
}
