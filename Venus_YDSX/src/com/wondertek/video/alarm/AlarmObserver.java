package com.wondertek.video.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.wondertek.video.VenusActivity;

public class AlarmObserver {
	public static final String TAG = "AlarmObserver";
	public VenusActivity venusHandle;
	private static AlarmObserver instance = null; 
	
	
	private static boolean num[] = {false,false,false,false,false,false,false,false,false,false};
	private static String des[] = {"","","","","","","","","",""};
	private Calendar c =null;
	private AlarmManager am =null;

	private AlarmObserver(VenusActivity va) {
		venusHandle = va;
	}
	
	public static AlarmObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new AlarmObserver(va);
		}
		return instance;
	}
	
	public int javaSetAlarm(int hourOfDay, int minute,int periods,int param4)
	{
		int rcode=0;
		for(rcode=0;rcode<10;rcode++)
		{
			if(!num[rcode])
				break;
		}
		
		if(rcode>=10)
			return -1;
		
		c = Calendar.getInstance(); 
		c.setTimeInMillis(System.currentTimeMillis());   
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);   
		c.set(Calendar.MINUTE, minute);   
		c.set(Calendar.SECOND, 0);   
		c.set(Calendar.MILLISECOND, 0);   
		Intent intent = new Intent(VenusActivity.appActivity,AlarmReceiver.class);   
		PendingIntent pi = PendingIntent.getBroadcast(VenusActivity.appActivity, rcode, intent, PendingIntent.FLAG_CANCEL_CURRENT);   
		am = (AlarmManager) VenusActivity.appActivity.getSystemService(Activity.ALARM_SERVICE);   
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		if(periods>0)
			am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), (periods*60*1000), pi);
		des[rcode]=hourOfDay+":"+minute+"\",\"periods\":\""+periods;
		num[rcode] = true;
		return rcode;
	}
	
	public boolean javaCancelAlarm(int rcode)
	{
		if(c==null||am==null)
			return false;
		Intent intent = new Intent(VenusActivity.appActivity,AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(VenusActivity.appActivity, rcode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(pi);
		des[rcode] = "";
		num[rcode] = false;
		return true;
	}
	
	public String javaGetAlarm()
	{
		String nRet="{";
		for(int i=0;i<10;i++)
		{	
			if(num[i])
			{
				if(nRet.equals("{"))
					nRet+="\"alarm\":[";
				if(!nRet.equals("{\"alarm\":["))
					nRet+=",";
				nRet+="{\"id\":\""+i+"\","+"\"time\":\""+des[i]+"\"}";
			}
		}
		if(!nRet.equals("{"))
			nRet+="]";
		nRet +="}";
		Log.d(TAG, "nRet=" +nRet);
		return nRet;
	}
}
