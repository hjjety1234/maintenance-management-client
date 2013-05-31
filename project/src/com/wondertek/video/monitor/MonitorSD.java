package com.wondertek.video.monitor;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MonitorSD extends MonitorBase{
	
	
	public MonitorSD()
	{
		super();
	}
	
	public MonitorSD(Object h)
	{
		super();
		mHandle = h;
	}
	
	@Override  
	public void onReceive(Context context, Intent intent)
	{
		Util.Trace("MonitorSD:: " + intent.getAction());
		VenusActivity.getInstance().nativesendevent(Util.WDM_SD, 0, 0);
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
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		
		return intentFilter;
	}
	

	
}
