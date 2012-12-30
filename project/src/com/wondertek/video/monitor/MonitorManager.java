package com.wondertek.video.monitor;

import android.content.IntentFilter;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class MonitorManager {
	
	private static MonitorManager instance = null;

	private VenusActivity activityHandle;
	
	private boolean[] recvrActive = new boolean[MonitorCommon.MONITOR_TYPE_COUNT];
	
	private MonitorBase[] recvFactory =	{
			new MonitorBattery(),
			new MonitorScreen(),
			new MonitorConnection(),
			new MonitorSD(),
			new MonitorContacts(),
			new MonitorHeadset(),
			null
			};
	
	public MonitorManager(VenusActivity h) {
		instance = this;
		activityHandle = h;
	}

	public static MonitorManager getInstance(VenusActivity h) {
		if (instance == null) {
			instance = new MonitorManager(h);
		}
		return instance;
	}
	
	public boolean Init()
	{
		char i = 0;
		
		Util.Trace("WriteLogs:  MonitorManager::init");
		
		for(; i < MonitorCommon.MONITOR_TYPE_COUNT; i++)
		{
			recvrActive[i] = false;
			recvFactory[i].Init(activityHandle);
		}
		
		return true;
	}
	
	public boolean RegisterMonitor(char type)
	{
		Util.Trace("WriteLogs:  MonitorManager::RegisterMonitor type="+(int)type);
		if(type < MonitorCommon.MONITOR_TYPE_COUNT)
		{
			IntentFilter intentFilter = recvFactory[type].getIntentFilter();
			if(intentFilter != null)
			{
				VenusApplication.getInstance().getApplicationContext().registerReceiver(recvFactory[type], recvFactory[type].getIntentFilter());
				recvrActive[type] = true;
			}
			else
			{
				recvrActive[type] = false;
			}
			return true;
		}
		return false;
	}
	
	public void UnRegisterMonitor(char type)
	{
		Util.Trace("WriteLogs:  MonitorManager::UnRegisterMonitor type="+type);
		if(type < MonitorCommon.MONITOR_TYPE_COUNT)
		{
			recvFactory[type].DeInit(activityHandle);
			if(recvrActive[type])
			{
				VenusApplication.getInstance().getApplicationContext().unregisterReceiver(recvFactory[type]);
			}
			recvrActive[type] = false;
		}
	}
	
	public void DeInit()
	{
		Util.Trace("WriteLogs:  MonitorManager::DeInit");
	}
	
	
	
	
	
	
}
