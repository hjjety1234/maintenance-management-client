package com.wondertek.video.monitor;

import com.wondertek.video.VenusActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
