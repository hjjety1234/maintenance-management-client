package com.wondertek.video.monitor;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

/*
EXTRA_PRESENT:
	true	Battery is present.
	false	Battery is not present.
	
EXTRA_TECHNOLOGY:
	Li-ion
	
EXTRA_PLUGGED:
	0x01	AC CHARGER
	0x02	USB PORT CHARGER
	
EXTRA_SCALE:
	100		0~SCALE
	
EXTRA_HEALTH:
	0x01	BATTERY_HEALTH_UNKNOWN
	0x02	BATTERY_HEALTH_GOOD
	0x03	BATTERY_HEALTH_OVERHEAT
	0x04	BATTERY_HEALTH_DEAD
	0x05	BATTERY_HEALTH_OVER_VOLTAGE
	0x06	BATTERY_HEALTH_UNSPECIFIED_FAILURE
	
EXTRA_STATUS:
	0x02	BATTERY_STATUS_CHARGING
	0x03	BATTERY_STATUS_DISCHARGING
	
EXTRA_LEVEL:
	76

 */
public class MonitorBattery extends MonitorBase{
	
	
	public MonitorBattery()
	{
		super();
	}
	
	public MonitorBattery(Object h)
	{
		super();
		mHandle = h;
	}
	
	@Override  
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();  
		
		if(Intent.ACTION_BATTERY_CHANGED.equals(action))
		{
			Bundle b=intent.getExtras();  
			
//			Object[] lstName=b.keySet().toArray();  
//			for(int i=0;i<lstName.length;i++)  
//			{  
//				String keyName=lstName[i].toString();  
//				Util.Trace(keyName + " = " + String.valueOf(b.get(keyName)));  
//			}
			
			if(b != null)
			{
//				boolean isPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
//				
//				String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
//				
//				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//				
//				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//				
//				int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
//				
				if(Util.GetSDK() == Util.SDK_ANDROID_15 || Util.GetSDK() == Util.SDK_ANDROID_16 || Util.GetSDK() == Util.SDK_OMS_15 || Util.GetSDK() == Util.SDK_OMS_16)
				{
					((VenusActivity)mHandle).batteryInfo.charging = 0;
					((VenusActivity)mHandle).batteryInfo.rawlevel = 50;
				}
				else
				{
					Class<?> cls;
					try {
						cls = Class.forName("android.os.BatteryManager");
						Field field;
						field = cls.getField("EXTRA_STATUS");
						String fieldValue = (String)field.get(null);
						int status = intent.getIntExtra(fieldValue, 0);
						
						field = cls.getField("EXTRA_LEVEL");
						fieldValue = (String)field.get(null);
						int rawlevel = intent.getIntExtra(fieldValue, -1);
						
						//Save these data
						if(status == 0x02)
							((VenusActivity)mHandle).batteryInfo.charging = 1;
						else
							((VenusActivity)mHandle).batteryInfo.charging = 0;
						
						((VenusActivity)mHandle).batteryInfo.rawlevel = rawlevel;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else if(Intent.ACTION_BATTERY_LOW.equals(action))
		{
			Util.Trace("Battery is LOW:");
		}
//		else if(Intent.ACTION_BATTERY_OKAY.equals(action))
//		{
//			Util.Trace("Battery is OK:");
//		}
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
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
//		intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
		
		return intentFilter;
	}
	

	
} 
