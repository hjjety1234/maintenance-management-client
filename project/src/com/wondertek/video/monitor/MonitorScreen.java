package com.wondertek.video.monitor;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MonitorScreen extends MonitorBase{
	
	private static final int  ScreenLockEventType_OFF 			= 0;
	private static final int  ScreenLockEventType_ON 			= 1;
	private static final int  ScreenLockEventType_PRESENT 		= 2;
	
	private static boolean bScreenPresent = true;

	public MonitorScreen()
	{
		super();
	}
	
	public MonitorScreen(Object h)
	{
		super();
		mHandle = h;
	}
	
	public static boolean getScreenState()
	{
		return bScreenPresent;
	}

	@Override  
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();  
		Util.Trace("WriteLogs:  ACTION::"+action);
		
		if(Intent.ACTION_SCREEN_OFF.equals(action))
		{
			bScreenPresent = false;
			VenusActivity.getInstance().nativesendevent(Util.WDM_SCREENLOCK, ScreenLockEventType_OFF, 0);
		}
		else if(Intent.ACTION_SCREEN_ON.equals(action))
		{
			bScreenPresent = false;
			VenusActivity.getInstance().nativesendevent(Util.WDM_SCREENLOCK, ScreenLockEventType_ON, 0);
		}
		else if(Intent.ACTION_USER_PRESENT.equals(action))
		{
			bScreenPresent = true;
			VenusActivity.getInstance().nativesendevent(Util.WDM_SCREENLOCK, ScreenLockEventType_PRESENT, 0);
		}
	}
	
	@Override
	public boolean Init(VenusActivity h) {
		this.mHandle = h;
		return true;
	}
	
	@Override
	public boolean DeInit(VenusActivity h)
	{
		return true;
	}
	
	@Override
	public IntentFilter getIntentFilter()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_USER_PRESENT);	/*Broadcast Action: Sent when the user is present after device wakes up (e.g when the keyguard is gone).*/
		
		return intentFilter;
	}
	

	
} 
