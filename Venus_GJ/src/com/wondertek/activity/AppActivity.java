package com.wondertek.activity;

//BDMAP
//import com.baidu.mapapi.MapActivity;

//import com.phonegap.DroidGap;
import android.widget.AbsoluteLayout;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.analytics.ReportPolicy;
//GDMAP
import com.mapabc.mapapi.map.MapActivity;

//import com.baidu.mapapi.MapActivity;
//import com.mapabc.mapapi.map.MapActivity;
//for plugin lotuseed
//import com.lotuseed.android.Lotuseed;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.map.bdmap.BDMapManager;
import com.wondertek.video.map.gdmap.GDMapManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;

//phonegap
//public class AppActivity extends DroidGap {
//MAP
//public class AppActivity extends MapActivity  {
public class AppActivity extends MapActivity {
	private static String SELF	= "AppActivity ";
	private static AppActivity instance = null;
	
	public AppActivity()
	{
		VenusActivity.getInstance(this).onPreInit();
	}
	
	public synchronized static AppActivity getInstance()
	{
		if(instance == null)
		{
			instance = new AppActivity();
		}
		return instance;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Util.Trace( SELF+"onCreate");
		super.onCreate(savedInstanceState);	
		
		VenusActivity.getInstance().onCreate(savedInstanceState);

		VenusApplication.getInstance().addActivity(this);
		//VenusApplication.getInstance().addActivity(AlixDemo.getInstance());
		//MobclickAgent.setDefaultReportPolicy(this, ReportPolicy.REALTIME);
		//MobclickAgent.setDebugMode(false);
	}

	@Override
	protected void onPause()
	{
		Util.Trace( SELF+"onPause");
		VenusActivity.getInstance().onPause();
		//MobclickAgent.onPause(this);
		super.onPause();
		//for plugin lotuseed
		//Lotuseed.onPause(this);
	}
	
	@Override
	protected void onResume() {
		Util.Trace( SELF+"onResume");
		VenusActivity.getInstance().onResume();
		//MobclickAgent.onResume(this);
		super.onResume();
		//for plugin lotuseed
		//Lotuseed.onResume(this);
	}

	@Override
	protected void onStop() {
		Util.Trace( SELF+"onStop");
		VenusActivity.getInstance().onStop();
		super.onStop();
	}
	@Override
	public void onDestroy() {
		Util.Trace( SELF+"onDestroy");
		VenusActivity.getInstance().onDestroy();
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Util.Trace( SELF+"onKeyDown");
		if( VenusActivity.getInstance().onKeyDown(keyCode, event) )
		{
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Util.Trace( SELF+"onKeyUp");
		if( VenusActivity.getInstance().onKeyUp(keyCode, event) )
		{
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		VenusActivity.getInstance().onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(SELF, "onActivityResult");
		if (requestCode == VenusActivity.REQUEST_GPS) {
			Util.Trace("Request GPS Setting callback");
			GDMapManager.getInstance().GetCurrentPositionFuncCalled(true);
			return;
		}
		if(resultCode == RESULT_OK)
		{
			if(requestCode == VenusActivity.CAMERA_RESULT)
			{
				VenusActivity.getInstance().cameraObserver.callback();
			}
			else if(requestCode == VenusActivity.CALL_RESULT)
			{
				VenusActivity.getInstance().callObserver.callback();
			}	
			else if(requestCode == VenusActivity.REQUEST_PICKER_ALBUM)
			{
				 if (data != null) {
			         Uri imgUri = data.getData();
			         Log.v("", "imgUri: " + imgUri.toString());

			          String[] proj = { MediaStore.Images.Media.DATA };
			          Cursor cursor = this.managedQuery(imgUri, proj, null, null, null);
			          if (cursor != null && cursor.moveToFirst()) {
			               String imgPath = cursor.getString(0);
			               Log.v("", "imgPath: " + imgPath);
			               VenusActivity.getInstance().cameraObserver.Album_SetParams(imgPath);
			           }
		            }

			}
		}
	}
	
//BDMAP
//	@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}

@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		VenusActivity.getInstance().onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	//phonegap	
	public void loadUrl(String url)
	{
//		super.loadUrl(url);		
	}

	public void setviewsize(int x,int y, int width, int height)
	{
//		super.setviewsize(x,y,width,height);
	}
	
	public AbsoluteLayout GetWebViewRoot()
    {
//		super.GetWebViewRoot().setBackgroundColor(0);
//    	return super.GetWebViewRoot();
		return null;
    }
	
	public int getResID(String name, String defType)
	{
		return getResources().getIdentifier(name, defType, getPackageName());
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
