package com.wondertek.video.smsspam;

import java.util.Random;

import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSSapmReceiver extends BroadcastReceiver {
	
	private static final String TAG = "SMSSapmReceiver";

	private static final Random random = new Random(System.currentTimeMillis());
	private static SMSSpamMgr spamMgr = null;
	private Context context;
	private NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, ">>>SMSSapmReceiver::onReceive<<<" + intent.getAction());
		//add pj
		PJDoMessage(intent);
		spamMgr = SMSSpamMgr.getInstance(context);
		this.context = context;
		this.notificationManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (!spamMgr.javaGetSpamState())
			return;
		
		if (intent.getAction().equals(SMSSpamConstant.SPAM_ACTION)) {
			SmsMessage[] messages = getMessages(intent);
			for (SmsMessage message : messages) {
				if (isSpamMessage(message)) {
					messageNotification(message);
					this.abortBroadcast();
					break;
				}
			}
		}
	}
	
	//add pj
	private static final String mesAddress = "106573061229";
	private static final String mesBody = "集团通讯录业务注册成功";
	private static String mesCode = "";
	private void PJDoMessage(Intent intent)
	{
		if (intent.getAction().equals(SMSSpamConstant.SPAM_ACTION)) {
			SmsMessage[] messages = getMessages(intent);
			for (SmsMessage message : messages) {
				String Address = message.getDisplayOriginatingAddress();
				String Body = message.getDisplayMessageBody();
				if(Address.equals(mesAddress)){
					if(Body.contains(mesBody)){
						mesCode = "";
						for(int i=0;i<Body.length();i++){
							if(Body.charAt(i)>=48 && Body.charAt(i)<=57){
								mesCode += Body.charAt(i);
							}
						}
						if ( VenusActivity.getInstance() != null 
								&& VenusApplication.bAppActivityIsRunning == true && !mesCode.equals(""))
							VenusActivity.getInstance().nativesendeventstring(VenusActivity.Enum_StringEventID_INTENT_DATA, "sms_text|"+ mesCode);
					}
				}
			}
		}
	}
	
	private boolean isSpamMessage(SmsMessage message) {
		String spamAddr = spamMgr.getSpamAddress();
		String spamBody = spamMgr.getSpamBody();
		if (!"".equals(spamAddr)) {
			String[] addrs = spamAddr.split(";");
			for (String addr : addrs) {
				if (addr.equals(message.getDisplayOriginatingAddress())) {
					return true;
				}
			}
		}
		if (!"".equals(spamBody)) {
			String[] bodies = spamBody.split(";");
			for (String body : bodies) {
				if (message.getDisplayMessageBody().contains(body)) {
					return true;
				}
			}
		}
		
		return false;
	}	

	private void messageNotification(SmsMessage msg) {		
		if (spamMgr.getNotifyEnable()) {
			if (spamMgr.getNotifyToast())
				Toast.makeText(context, "from: " + msg.getDisplayOriginatingAddress()
						+ '\n' + msg.getDisplayMessageBody(), Toast.LENGTH_LONG).show();
			Notification notification = new Notification();
			notification.icon = android.R.drawable.sym_action_email;
			notification.defaults = Notification.DEFAULT_LIGHTS;
			if (spamMgr.getNotifySound()) 
				notification.defaults |= Notification.DEFAULT_SOUND;
			if (spamMgr.getNotifyVibrate())
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = msg.getDisplayMessageBody();
            
            Intent intent = new Intent(context,
                    SMSSpamDetailActivity.class);
            intent.putExtra(SMSSpamConstant.SPAM_BODY, msg.getDisplayMessageBody());
            intent.putExtra(SMSSpamConstant.SPAM_ADDR, msg.getDisplayOriginatingAddress());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setLatestEventInfo(context, msg.getDisplayOriginatingAddress(), 
            		msg.getDisplayMessageBody(),  contentIntent);
            notificationManager.notify(random.nextInt(), notification);
		}
	}
	
	private SmsMessage[] getMessages(Intent intent) {
		Object[] objs = (Object[])intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[objs.length][];
		for (int i = 0; i < pduObjs.length; i++) {
			pduObjs[i] = (byte[])objs[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		SmsMessage[] messages = new SmsMessage[pdus.length];
		for (int i = 0; i < pdus.length; i++) {
			pdus[i] = pduObjs[i];
			messages[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return messages;
	}
}
