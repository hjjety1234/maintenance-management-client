package com.wondertek.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;


public class AppFakeActivity extends Activity {
	private static String SELF	= "AppFakeActivity ";
	private static AppActivity instance = null;
	private LinearLayout al;
	private AppFakeActivity appFakeActivity = null;
	
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
		al = new LinearLayout(this);
		al.setOrientation(LinearLayout.VERTICAL);
		al.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		setContentView(al);
		appFakeActivity = this;
		new Thread(new Runnable(){

			public void run() {
				while(true)
				{
					if(al.getWidth()>0 && al.getHeight()>0)
					{
						int orientation = getResources().getConfiguration().orientation;
						int width = al.getWidth();
						int height = al.getHeight();
						Message msg = new Message();
						VenusApplication.getInstance();
						msg.what = VenusApplication.MSG_ID_BOOT_APPACTIVITY;
						Bundle bundle = new Bundle();
						bundle.putInt("ID", 0);
						bundle.putInt("orientation", orientation);
						bundle.putInt("Width", al.getWidth());
						bundle.putInt("Height", al.getHeight());
						msg.setData(bundle);
						VenusApplication.getInstance().addActivity(appFakeActivity);
						VenusApplication.getInstance();
						VenusApplication.applicationHandler.sendMessage( msg );
						break;
					}
				}
			}
		}).start();
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
		Util.SetIntentData(getIntent());
		Util.executeIntentData();
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
