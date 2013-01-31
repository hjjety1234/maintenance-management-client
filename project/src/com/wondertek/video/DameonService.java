package com.wondertek.video;

import java.io.File;

//import com.wondertek.video.sms.SMS;
//import com.wondertek.video.sms.SMSHandler;
//import com.wondertek.video.sms.SMSObserver;
import com.wondertek.activity.R;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;



public class DameonService extends Service {
	
	@Override
	public void onCreate() {
		Util.Trace("DameonService::onCreate===IN ");
		super.onCreate();
	}

	private void addTest() {
		Util.Trace("Add addTEST");

	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Util.Trace("DameonService: onDestroy()");
		start(this);
		//super.onDestroy();
	}

	
	public static String GetConfigXmlFilePath() {
		return VenusApplication.getInstance().appAbsPath + "Alert.xml";
	}
	private static long times = 0;
	public static Configuration appointmentXml = null;
	public static void start(Context c) {
		Intent intent = new Intent(c, DameonService.class);
		c.startService(intent);
		Util.Trace("DameonService::start===IN " + GetConfigXmlFilePath());
		if (null == appointmentXml) {
			appointmentXml = new Configuration();
			appointmentXml.load(GetConfigXmlFilePath());
			//appointmentXml.save(null);
		}
		
		long appStartTime = System.currentTimeMillis();
		int appointmentNum = appointmentXml.getAppointmentNum();
		Util.Trace("DameonService::start1===IN " + appStartTime + ";appointmentNum:" + appointmentNum);
		for(int i = appointmentNum-1; i >= 0; i--)
		{
			long xmlSysTimeValue = 0;
			String strSysTimeValue = appointmentXml.getWarnSysTimeValue(i);
			//Util.Trace("Configuration.strSysTimeValue=" + strSysTimeValue);
			if(strSysTimeValue != null)
				xmlSysTimeValue = Long.parseLong(strSysTimeValue);

			if(appStartTime > xmlSysTimeValue || (appStartTime <= 0 && xmlSysTimeValue >= 0))
			{	
				appointmentXml.deleteAppointment(i);
				Util.Trace("@@@@DameonService::sysTime: 0 delete " + strSysTimeValue); 
			}	
			else
			{
				String sysTime = String.valueOf(xmlSysTimeValue - appStartTime);
				appointmentXml.modifyTimeInterval(i, sysTime);
				Util.Trace("@@@@DameonService::sysTime remains: " + sysTime + " index " + i); 
			}
		}
		appointmentNum = appointmentXml.getAppointmentNum();

		for(int i = 0; i < appointmentNum; i++)
		{	
			String timeIntervalValue = appointmentXml.getTimeIntervalValue(i);
			Util.Trace("DameonService::start2 timeIntervalValue===IN " + timeIntervalValue);
			if(timeIntervalValue != null)
				times = Long.parseLong(timeIntervalValue);

			Util.Trace("DameonService::start3===IN times" + times);

			Message msg = new Message();
			msg.what = MSG_ID_NOTIFICATION_APPOINTMENT;
			Bundle bundle = new Bundle();
			bundle.putString("tickerText", VenusApplication.getInstance().getResString("tickerText"));
			bundle.putString("contentTitle", VenusApplication.getInstance().getResString("contentTitle"));
			bundle.putString("contentText", appointmentXml.getProgramValue(i));
			bundle.putString("resptext", appointmentXml.getUrlValue(i));
			msg.setData(bundle);
			if(times != 0)
				appointmentHandler.sendMessageDelayed(msg, times);
		}
	}
	
	public static void Service_addAppointment(String programID, String timeInterval, String programName, String programUrl)
	{	
		if (null == appointmentXml) {
			appointmentXml = new Configuration();
			appointmentXml.load(GetConfigXmlFilePath());
		}

		long appStartTime = System.currentTimeMillis();
		long warnSysTime = appStartTime + Long.parseLong(timeInterval) * 1000;
		
		String millTimeInterval = null;
		millTimeInterval = String.valueOf(Long.parseLong(timeInterval) * 1000);
		if(appointmentXml.getAppointment(programID) == null)
		{	
			appointmentXml.addAppointment(programID, millTimeInterval, programName, programUrl, String.valueOf(warnSysTime));
			int appointmentNum = appointmentXml.getAppointmentNum() - 1;
		
			times = Long.parseLong(timeInterval) * 1000;
			Message msg = new Message();
			msg.what = MSG_ID_NOTIFICATION_APPOINTMENT;
			Bundle bundle = new Bundle();
			bundle.putString("tickerText", VenusApplication.getInstance().getResString("tickerText"));
			bundle.putString("contentTitle", VenusApplication.getInstance().getResString("contentTitle"));
			bundle.putString("contentText", appointmentXml.getProgramValue(appointmentNum));
			bundle.putString("resptext", appointmentXml.getUrlValue(appointmentNum));
			msg.setData(bundle);
			if(times != 0)
				appointmentHandler.sendMessageDelayed(msg, times);
			Util.Trace("DameonService:: sendMessageDelayed" + programID + times + programName + programUrl);
		}
		else
		{
			Util.Trace(programID + "DameonService:: this id is exist");
		}

	}
	
	public static void Service_DeleteAppointment(String programID)
	{
		if (null == appointmentXml) {
			appointmentXml = new Configuration();
			appointmentXml.load(GetConfigXmlFilePath());
		}
		appointmentXml.deleteAppointment(programID);
		
		if(appointmentHandler != null)
		{	
			appointmentHandler.removeMessages(MSG_ID_NOTIFICATION_APPOINTMENT);
			int appointmentNum = appointmentXml.getAppointmentNum();
			
			long appStartTime = System.currentTimeMillis();
			for(int i = appointmentNum-1; i >= 0; i--)
			{
				long xmlSysTimeValue = 0;
				if(appointmentXml.getWarnSysTimeValue(i) != null)
					xmlSysTimeValue = Long.parseLong(appointmentXml.getWarnSysTimeValue(i));
				
				if(appStartTime > xmlSysTimeValue)
				{	
					appointmentXml.deleteAppointment(i);
				}	
				else
				{
					String sysTime = String.valueOf((Long.parseLong(appointmentXml.getWarnSysTimeValue(i))) - appStartTime);
					appointmentXml.modifyTimeInterval(i, sysTime);
				}
			}
			
			appointmentNum = appointmentXml.getAppointmentNum();

			for(int i = 0; i < appointmentNum; i++)
			{	
				String timeIntervalValue = appointmentXml.getTimeIntervalValue(i);
				//Util.Trace("Configuration::start1===IN " + timeIntervalValue);
				times = Long.parseLong(timeIntervalValue);
				//Util.Trace("Configuration::start===IN " + times);
			
				Message msg = new Message();
				msg.what = MSG_ID_NOTIFICATION_APPOINTMENT;
				Bundle bundle = new Bundle();
				bundle.putString("tickerText", VenusApplication.getInstance().getResString("tickerText"));
				bundle.putString("contentTitle", VenusApplication.getInstance().getResString("contentTitle"));
				bundle.putString("contentText", appointmentXml.getProgramValue(i));
				bundle.putString("resptext", appointmentXml.getUrlValue(i));
				msg.setData(bundle);
				if(times != 0)
					appointmentHandler.sendMessageDelayed(msg, times);
				
			}	
		}
	}
	
	public static void stop(Context c) {

	}
	public static final int MSG_ID_NOTIFICATION_APPOINTMENT			= 0;
	
	public static Handler appointmentHandler = new Handler(){
		public void handleMessage(Message message) {
			Util.Trace("appointmentHandler::handleMessage " );
			switch(message.what)
			{
			case MSG_ID_NOTIFICATION_APPOINTMENT :
				String tickerText = message.getData().getString("tickerText");
				String contentTitle = message.getData().getString("contentTitle");
				String contentText = message.getData().getString("contentText");
				String resptext = message.getData().getString("resptext");
				Util.Trace("DameonService::start1 handleMessage===IN " + tickerText + ":" + contentTitle);
				showNotification(tickerText, contentTitle, contentText, resptext);
				break;
			}
		}
	};
	
	public static void showNotification(String tickerText, String contentTitle, String contentText, String resptext)
	{
		Intent intent = new Intent("com.wondertek.mobilevideo3.appointmentclick");
		intent.putExtra("RESPTEXT", resptext);

		PendingIntent pIntent = PendingIntent.getBroadcast(VenusApplication.getInstance().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationManager manager = (NotificationManager) VenusApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

 		Notification notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.when = System.currentTimeMillis();
		notification.tickerText = tickerText;
		
 		notification.contentIntent = pIntent;
		notification.setLatestEventInfo(VenusApplication.getInstance().getApplicationContext(), contentTitle, contentText,
				pIntent);

		notification.defaults =Notification.DEFAULT_SOUND; 
		Util.Trace("DameonService::showNotification===IN " + tickerText + ":" + contentTitle);
		manager.notify(0, notification);
	}
}
