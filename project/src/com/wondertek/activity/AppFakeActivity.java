package com.wondertek.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;


public class AppFakeActivity extends Activity {
	private static String SELF	= "AppFakeActivity ";
	private static AppActivity instance = null;
	
	public AppFakeActivity()
	{
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
		
		Message msg = new Message();
		VenusApplication.getInstance();
		msg.what = VenusApplication.MSG_ID_BOOT_APPACTIVITY;
		Bundle bundle = new Bundle();
		bundle.putInt("ID", 0);
		msg.setData(bundle);
		VenusApplication.getInstance().addActivity(this);
		VenusApplication.getInstance();
		VenusApplication.applicationHandler.sendMessage( msg );
//		UmsAgent.setDefaultReportPolicy(this, 1);
//		UmsAgent.postClientData(this);
//		UmsAgent.onError(this);
	}
	
	@Override
	protected void onPause()
	{
		Util.Trace( SELF+"onPause");
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Util.Trace( SELF+"onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Util.Trace( SELF+"onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Util.Trace( SELF+"onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Util.Trace( SELF+"onKeyUp");
		return super.onKeyUp(keyCode, event);
	}
}
