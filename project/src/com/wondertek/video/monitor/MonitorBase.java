package com.wondertek.video.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.wondertek.video.VenusActivity;

public abstract class MonitorBase extends BroadcastReceiver{
	protected  Object mHandle;
	
	public MonitorBase() {
		super();
    }
	
	public abstract void onReceive(Context context, Intent intent);
	
	
	//Custom method list.
	public abstract boolean Init(VenusActivity h);
	public abstract boolean DeInit(VenusActivity h);
	public abstract IntentFilter getIntentFilter();
	
	
	
}
