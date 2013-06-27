package com.wondertek.video;

import java.io.File;
//add pj
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

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
			//add pj
			bundle.putString("tickerText", VenusApplication.getInstance().getString(R.string.tickerText));
			bundle.putString("contentTitle", VenusApplication.getInstance().getString(R.string.contentTitle));
			bundle.putString("contentText", appointmentXml.getProgramValue(i));
			bundle.putString("resptext", appointmentXml.getUrlValue(i));
			msg.setData(bundle);
			if(times != 0)
				appointmentHandler.sendMessageDelayed(msg, times);
		}
	}
	//add pj
	public static void Service_setAppointmentEnable(int integer)
	{	
		Util.Trace("@@@@@@@@@Service_setAppointmentEnable" + integer);
		if (integer == 1) {
			 boolean flag = string2File("1", VenusApplication.getInstance().appAbsPath + "AlertEnable.txt");
			 Util.Trace("@@@@@@@@@flag" + flag);
		}
		else {
			boolean flag = string2File("0", VenusApplication.getInstance().appAbsPath + "AlertEnable.txt");
			Util.Trace("@@@@@@@@@flag" + flag);
		}
	}
	
	//add pj
	/** 
     * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！) 
     */ 
    public static boolean string2File(String res, String filePath) { 
            boolean flag = true; 
            BufferedReader bufferedReader = null; 
            BufferedWriter bufferedWriter = null; 
            try { 
                    File distFile = new File(filePath); 
                    if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs(); 
                    bufferedReader = new BufferedReader(new StringReader(res)); 
                    bufferedWriter = new BufferedWriter(new FileWriter(distFile)); 
                    char buf[] = new char[1024];         //字符缓冲区 
                    int len; 
                    while ((len = bufferedReader.read(buf)) != -1) { 
                            bufferedWriter.write(buf, 0, len); 
                    } 
                    bufferedWriter.flush(); 
                    bufferedReader.close(); 
                    bufferedWriter.close(); 
            } catch (IOException e) { 
                    e.printStackTrace(); 
                    flag = false; 
                    return flag; 
            } finally { 
                    if (bufferedReader != null) { 
                            try { 
                                    bufferedReader.close(); 
                            } catch (IOException e) { 
                                    e.printStackTrace(); 
                            } 
                    } 
            } 
            return flag; 
    	
//    	boolean flag = true; 
//    	try {
//            FileWriter fw = new FileWriter(filePath);
//            String s;
//            if(str!=null && !str.trim().equals("")){
//                s = str;
//            }else{
//                s = "1";
//            }
//            fw.write(s,0,s.length());  
//            fw.flush();  
//        } catch (IOException e) {
//            e.printStackTrace();
//            flag = false;        
//        } 
//    	return flag; 
    }
	
	//add pj
    /** 
     * 文本文件转换为指定编码的字符串 
     */ 
    public static String file2String(String filePath) { 
    	FileReader fr;
        StringBuilder sb = new StringBuilder();
        //String str;
        try {
            fr = new FileReader(filePath);
            int ch = 0;  
            while((ch = fr.read())!=-1 )  
            {  
            sb.append((char)ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
        return sb.toString();
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
			//add pj
			Util.Trace("DameonService:: sendMessageDelayed" + VenusApplication.getInstance().getString(R.string.tickerText)
					+ VenusApplication.getInstance().getString(R.string.contentTitle));
			bundle.putString("tickerText", VenusApplication.getInstance().getString(R.string.tickerText));
			bundle.putString("contentTitle", VenusApplication.getInstance().getString(R.string.contentTitle));
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
				//add pj
				bundle.putString("tickerText", VenusApplication.getInstance().getString(R.string.tickerText));
				bundle.putString("contentTitle", VenusApplication.getInstance().getString(R.string.contentTitle));
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
				//add pj
				String bAlertEnable = file2String(VenusApplication.getInstance().appAbsPath + "AlertEnable.txt");
				Util.Trace("@@@@@@@@@@@@@bAlertEnable@@@ " + bAlertEnable);
				if (!bAlertEnable .equals("0")){
				   String tickerText = message.getData().getString("tickerText");
					String contentTitle = message.getData().getString("contentTitle");
					String contentText = message.getData().getString("contentText");
					String resptext = message.getData().getString("resptext");
					Util.Trace("DameonService::start1 handleMessage===IN " + tickerText + ":" + contentTitle);
					showNotification(tickerText, contentTitle, contentText, resptext);
				}
				break;
			}
		}
	};
	
	public static void showNotification(String tickerText, String contentTitle, String contentText, String resptext)
	{
		//add pj
		Intent intent = new Intent("com.wondertek.cnlive.appointmentclick");
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
