package com.wondertek.video.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class MonitorHeadset extends MonitorBase {

	private static final String FILTER_ACTION = "android.intent.action.HEADSET_PLUG";
	
	private static boolean headsetIsPlugged = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Util.Trace("====onReceive=======");
		if (intent.hasExtra("state")) {
			/*
			if (intent.getIntExtra("state", 0) == 0) {
				Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
			} else if (intent.getIntExtra("state", 0) == 1) {
				Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
			}
			*/
			headsetIsPlugged = (intent.getIntExtra("state", 0) == 0) ? false : true;
			Util.Trace("headsetIsPlugged:" + headsetIsPlugged);
		}
	}
	
	public static boolean getHeadsetStatus() {
		Util.Trace("getHeadsetStatus:" + headsetIsPlugged);
		return headsetIsPlugged;
	}

	@Override
	public boolean Init(VenusActivity h) {
		// TODO Auto-generated method stub
		mHandle = h;
		return true;
	}

	@Override
	public boolean DeInit(VenusActivity h) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IntentFilter getIntentFilter() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(FILTER_ACTION);
		return filter;
	}

}
