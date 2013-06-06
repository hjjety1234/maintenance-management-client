package com.wondertek.video.notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.wondertek.ict4.R;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.VenusConstEventString;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

public class CNotificationCustom extends BroadcastReceiver
{
	
	NotificationManager m_NotificationManager = null;
	
	static CNotificationCustom s_notification = null; 
	
	private String m_strPackeName  = null;
	private String m_strStartParam = null;
	private int    m_nCurProcessID = -1;
	static String m_notificationText = "";
	static boolean bBackground = false;
	
	static public CNotificationCustom getInstance()
	{
		if ( s_notification == null )
		{
			s_notification = new CNotificationCustom();
		}
		
		return s_notification;
	}
	
	public void onPause()
	{
		bBackground = true;
	}
	
	public void onResume()
	{
		bBackground = false;
		if ( m_notificationText != "" )
		{
			VenusActivity.getInstance().nativesendeventstring(VenusActivity.Enum_StringEventID_NOTIFICATION_TEXT, m_notificationText);
			m_notificationText = "";
		}
	}
	
	public void ExitApp()
	{
		if ( m_nCurProcessID == -1 )
			return;
		
		DeleteNotification(m_nCurProcessID);
	}
	
	public void setStartInfo( String strPackeName, String strStartParam )
	{
		m_strPackeName  = strPackeName;
		m_strStartParam = strStartParam;
	}
	
	public Bitmap getIconBitmpa( String iconBitmap ) 
	{
		Bitmap bitmap = null;
		if ( iconBitmap != null )
			bitmap = BitmapFactory.decodeFile( iconBitmap );
		
		if ( bitmap == null )
		{
			bitmap = BitmapFactory.decodeResource(VenusApplication.getInstance().getResources(), R.drawable.icon);
		}
		
		return bitmap;
	}
	
	static final String notificationType = "strNotificationType";
	static final String notificationEventString = "strNotificationEventString";
	static final String startPacketName  = "strPacketName";
	static final String startParam	 	 = "startParam";
	public boolean ShowNotificationText(int nID, String showTitle, String showContent, String content, 
			String iconBitmap, int enableSound, int vibrate, int canBeDelete, String eventString )
	{
		Intent intent = new Intent(VenusConstEventString.CLICK_NOTIFICATIONTEXT_ACTION);
		intent.putExtra(notificationType, content);
		if ( eventString != null )
			intent.putExtra(notificationEventString, eventString);
		else
			intent.putExtra(notificationEventString, "");
		
		if ( startPacketName != null )
			intent.putExtra(startPacketName, m_strPackeName);
		else 
			intent.putExtra(startPacketName, "");
		
		if ( m_strStartParam != null )
			intent.putExtra(startParam, m_strStartParam);
		else 
			intent.putExtra(startParam, "");
		
		PendingIntent pIntent = PendingIntent.getBroadcast(VenusApplication.getInstance(), nID, intent, 0);
		
		Notification m_notification = new Notification(R.drawable.icon, showTitle, System.currentTimeMillis());
		m_notification.contentIntent = pIntent;
		m_notification.contentView = new RemoteViews(VenusApplication.getInstance().getPackageName(), R.layout.notification_showtext);
		
		if ( enableSound != 0 )
		{
			m_notification.flags |= Notification.DEFAULT_SOUND ;
			m_notification.sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		
		if ( vibrate != 0 )
		{
			long[] vibrate1 = {1000,1000,1000,1000};
			m_notification.vibrate = vibrate1;
		}
		
		if ( canBeDelete != 0 )
		{
			m_notification.flags |= Notification.DEFAULT_VIBRATE;
		}
		else
		{
			m_notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
		
		Bitmap bitmap = getIconBitmpa(iconBitmap);
		if ( bitmap != null )
		{
			m_notification.contentView.setImageViewBitmap(R.id.iconTextNotification, bitmap);
		}
		
		String strTime = null;
		SimpleDateFormat   formatter  =  new  SimpleDateFormat("HH:mm");
		strTime = formatter.format(new Date());
		m_notification.contentView.setTextViewText(R.id.notificationTextTime,strTime );
		m_notification.contentView.setTextViewText(R.id.showNoitficationTextContent,showContent );
		
		if ( m_NotificationManager == null ) 
		{
			m_NotificationManager = (NotificationManager) VenusApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		m_NotificationManager.notify(nID, m_notification);
		
		if ( m_nCurProcessID == nID )
			m_nCurProcessID = -1;

		return true;
	}
	
	public boolean ShowNotificationStandardText(int nID, String showTitle, String showContent, String content, 
			String iconBitmap, int enableSound, int vibrate, int canBeDelete, 
			String showContentSecond, String eventString )
	{
		Intent intent = new Intent(VenusConstEventString.CLICK_NOTIFICATIONTEXT_ACTION);
		intent.putExtra(notificationType, content);
		
		if ( eventString != null )
			intent.putExtra(notificationEventString, eventString);
		else
			intent.putExtra(notificationEventString, "");
		
		if ( startPacketName != null )
			intent.putExtra(startPacketName, m_strPackeName);
		else 
			intent.putExtra(startPacketName, "");
		
		if ( m_strStartParam != null )
			intent.putExtra(startParam, m_strStartParam);
		else 
			intent.putExtra(startParam, "");
		
		PendingIntent pIntent = PendingIntent.getBroadcast(VenusApplication.getInstance(), nID, intent, 0);
		
		Notification m_notification = null;
		m_notification = new Notification(R.drawable.icon, showTitle, System.currentTimeMillis());

		m_notification.contentIntent = pIntent;
		m_notification.contentView = new RemoteViews(VenusApplication.getInstance().getPackageName(), R.layout.notification_standard);
		
		if ( enableSound != 0 )
		{
			m_notification.flags |= Notification.DEFAULT_SOUND ;
			m_notification.sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		
		if ( vibrate != 0 )
		{
			long[] vibrate1 = {1000,1000,1000,1000};
			m_notification.vibrate = vibrate1;
		}
		
		if ( canBeDelete != 0 )
		{
			m_notification.flags |= Notification.DEFAULT_VIBRATE;
		}
		else
		{
			m_notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
		
		Bitmap bitmap = getIconBitmpa(iconBitmap);
		if ( bitmap != null )
		{
			m_notification.contentView.setImageViewBitmap(R.id.iconTextNotification, bitmap);
		}
		
		String strTime = null;
		SimpleDateFormat   formatter  =  new  SimpleDateFormat("HH:mm");
		strTime = formatter.format(new Date());
		m_notification.contentView.setTextViewText(R.id.notificationTextTime,strTime );
		m_notification.contentView.setTextViewText(R.id.showNoitficationTextContent,showContent );
		m_notification.contentView.setTextViewText(R.id.showNoitficationTextSecondContent,showContentSecond );
		
		if ( m_NotificationManager == null ) 
		{
			m_NotificationManager = (NotificationManager) VenusApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		m_NotificationManager.notify(nID, m_notification);
		
		if ( m_nCurProcessID == nID )
			m_nCurProcessID = -1;
		
		return true;
	}
	
	public void DeleteNotification(int nID)
	{
		if ( m_NotificationManager == null ) 
		{
			m_NotificationManager = (NotificationManager) VenusApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		m_NotificationManager.cancel(nID);
	}
	
	Notification m_lastProcessNotification = null;
	public boolean showNotificationProcess(int nID, String showTitle, String showContent, 
			String content, int nPercent,
			String iconBitmap, int enableSound, int vibrate, int canBeDelete)
	{	
		if ( m_lastProcessNotification!=null && nID==m_nCurProcessID )
		{
			String strTime = null;
			SimpleDateFormat   formatter  =  new  SimpleDateFormat("HH:mm");
			strTime = formatter.format(new Date());
			
			m_lastProcessNotification.contentView.setTextViewText(R.id.notificationProcessTime,strTime );
			m_lastProcessNotification.contentView.setTextViewText(R.id.showNoitficationProcessContent,showContent );
			m_lastProcessNotification.contentView.setTextViewText(R.id.processpercent, 	Integer.toString(nPercent) + "%" );
			m_lastProcessNotification.contentView.setProgressBar(R.id.progressbar, 100, nPercent, false);
			m_NotificationManager.notify(nID, m_lastProcessNotification);
			return true;
		}
		
		Intent intent = new Intent(VenusConstEventString.CLICK_NOTIFICATIONPROCESS_ACTION);
		intent.putExtra(notificationType, content);
		
		if ( startPacketName != null )
			intent.putExtra(startPacketName, m_strPackeName);
		else 
			intent.putExtra(startPacketName, "");
		
		if ( m_strStartParam != null )
			intent.putExtra(startParam, m_strStartParam);
		else 
			intent.putExtra(startParam, "");
		
		PendingIntent pIntent = PendingIntent.getBroadcast(VenusApplication.getInstance(), nID, intent, 0);
		
		Notification m_notification = new Notification(R.drawable.icon, showTitle, System.currentTimeMillis());
		m_notification.contentIntent = pIntent;
		m_notification.contentView = new RemoteViews(VenusApplication.getInstance().getPackageName(), R.layout.notification_process);
		
		if ( enableSound != 0 )
		{
			m_notification.flags |= Notification.DEFAULT_SOUND ;
			m_notification.sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}
		
		if ( vibrate != 0 )
		{
			long[] vibrate1 = {1000,1000,1000,1000};
			m_notification.vibrate = vibrate1;
		}
		// 
	//	if ( canBeDelete != 0 )
	//	{
			m_notification.flags |= Notification.DEFAULT_VIBRATE;
	//	}
	//	else
	//	{
	//		m_notification.flags |= Notification.FLAG_AUTO_CANCEL;
	//	}
		
		Bitmap bitmap = getIconBitmpa(iconBitmap);
		if ( bitmap != null )
		{
			m_notification.contentView.setImageViewBitmap(R.id.iconProcessNotification, bitmap);
		}
		
		String strTime = null;
		SimpleDateFormat   formatter  =  new  SimpleDateFormat("HH:mm");
		strTime = formatter.format(new Date());
		m_notification.contentView.setTextViewText(R.id.notificationProcessTime,strTime );
		m_notification.contentView.setTextViewText(R.id.showNoitficationProcessContent,showContent );
		m_notification.contentView.setTextViewText(R.id.processpercent,Integer.toString(nPercent) + "%" );
		m_notification.contentView.setProgressBar(R.id.progressbar, 100, nPercent, false);
		
		if ( m_NotificationManager == null ) 
		{
			m_NotificationManager = (NotificationManager) VenusApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		m_NotificationManager.notify(nID, m_notification);
		m_lastProcessNotification = m_notification;
		m_nCurProcessID = nID;

		return true;
	}
	
    private void startAppActivity(Context context, String packageName, String argumentValue, String activityClassName)
    {
    	PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
    	Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
    	resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    	resolveIntent.setPackage(pi.packageName);
    	List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
    	ResolveInfo ri = apps.iterator().next();
    	if (ri != null ) 
    	{
	    	String packageName1 = ri.activityInfo.packageName;
	    	
	    	String className = null;
	    	if ( activityClassName != "" )
	    		className = activityClassName;
	    	else
	    		className = ri.activityInfo.name;
	    	
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
	    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
	
	    	ComponentName cn = new ComponentName(packageName1, className);
	    	intent.setComponent(cn);
	    	intent.putExtra("argument", argumentValue);
	
	    	context.startActivity(intent);
    	}
    }
    
	Handler delayMsg = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = (Intent)msg.obj;
			if ( VenusActivity.getInstance() != null 
					&& VenusApplication.bAppActivityIsRunning == true )
			{
				VenusActivity.startParam = intent.getStringExtra(notificationType);
				sendMsg(intent);
			}
			else
			{
				Message tmp = new Message();
				tmp.obj = intent;
				delayMsg.sendMessageDelayed(tmp, 200);
			}
		}
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String packetName 	= intent.getStringExtra(startPacketName);
		String param 		= intent.getStringExtra(startParam);
		if ( packetName != null )
		{	
			if ( (packetName.compareToIgnoreCase(VenusApplication.getInstance().getApplicationContext().getPackageName()) == 0 ||
					packetName.compareTo("self") == 0 )
				 && VenusActivity.getInstance() == null )
			{	
				VenusApplication.startAppActivity(0);
				
				if ( VenusActivity.getInstance() == null )
				{	
					Message tmp = new Message();
					tmp.obj = intent;
					delayMsg.sendMessageDelayed(tmp, 300);
					return;
				}
			}
			else if ( packetName.length() != 0 && packetName.compareTo("self") != 0 
					&& packetName.compareToIgnoreCase(VenusApplication.getInstance().getApplicationContext().getPackageName()) != 0)
			{
				startAppActivity( context, packetName, startParam, "" );
				return;
			}
		}
				
		sendMsg(intent);
	}
	
	public void startAppByID( Intent intent )
	{
		String   strEventString = intent.getStringExtra(notificationEventString);
			
		if ( strEventString == null ) {
			VenusApplication.startAppActivity(0);
		} else if ( strEventString.equals(VenusConstEventString.CLICK_NOTIFICATION_EVENT_DOWNCOMPLETE) ) {
			VenusApplication.startAppActivity(VenusApplication.MSG_ID_RECEIVE_NOTIFICATION_CUSTOM_MESSAGE);
		} else if ( strEventString.equals(VenusConstEventString.CLICK_NOTIFICATION_EVENT_PULLNEW) ){
			VenusApplication.startAppActivity(VenusApplication.MSG_ID_RECEIVE_NOTIFICATION_NEWS_NOTIFY);
		} else if ( strEventString.equals(VenusConstEventString.CLICK_NOTIFICATION_EVENT_COMMUNITY) ){
			VenusApplication.startAppActivity(VenusApplication.MSG_ID_RECEIVE_COMMUNITY);
		} else
			VenusApplication.startAppActivity(0);
	}
	
	public void sendMsg(Intent intent)
	{
		if ( VenusActivity.getInstance() == null )
		{	
			startAppByID(intent);
			
			Message tmp = new Message();
			tmp.obj = intent;
			delayMsg.sendMessageDelayed(tmp, 300);
			return;
		}
		else
			startAppByID(intent);
		
		if( intent.getAction().equals(VenusConstEventString.CLICK_NOTIFICATIONTEXT_ACTION) )
		{
			String   strContent     = intent.getStringExtra(notificationType);
			String   strEventString = intent.getStringExtra(notificationEventString);
			if ( strEventString.equals(VenusConstEventString.CLICK_NOTIFICATION_EVENT_PULLNEW) )
			{
				Message msg = new Message();
				msg.what = VenusApplication.MSG_ID_RECEIVE_NOTIFICATION_NEWS_NOTIFY;
				VenusApplication.applicationHandler.sendMessage( msg );
			}
			else if ( strEventString.equals(VenusConstEventString.CLICK_NOTIFICATION_EVENT_COMMUNITY) )
			{
				Message msg = new Message();
				msg.what = VenusApplication.MSG_ID_RECEIVE_COMMUNITY;
				VenusApplication.applicationHandler.sendMessage( msg );
			}
			else
			{
				if ( bBackground == true )
					m_notificationText = strContent;
				else
					VenusActivity.getInstance().nativesendeventstring(VenusActivity.Enum_StringEventID_NOTIFICATION_TEXT, strContent);
			}
		}
		else if ( intent.getAction().equals(VenusConstEventString.CLICK_NOTIFICATIONPROCESS_ACTION) )
		{
			String   strContent    = intent.getStringExtra(notificationType);
			VenusActivity.getInstance().nativesendeventstring(VenusActivity.Enum_StringEventID_NOTIFICATION_PROCESS, strContent);
		}
	}
}
