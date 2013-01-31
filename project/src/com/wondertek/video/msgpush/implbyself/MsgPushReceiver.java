package com.wondertek.video.msgpush.implbyself;

import java.util.Random;

import com.wondertek.video.msgpush.NotificationDetailsActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Response for receiveing a message from Server
 * @author yuhongwei
 *
 */

public class MsgPushReceiver extends BroadcastReceiver {
	private static final String TAG = "MsgPushReceiver";

	private MsgPushService mService;
	private SharedPreferences mSharePrefs;

	public MsgPushReceiver(MsgPushService s) {
		mService = s;
        mSharePrefs = mService.getSharedPreferences();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "[MsgPushReceiver] onReceive()...");
		String action = intent.getAction();
		Log.d(TAG, "action=" + action);

		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
			Bundle bundle = intent.getExtras();
			notify(context, bundle);
		} 
	}
    
	private void notify(Context context, Bundle bundle) {
		String title = bundle.getString(Constants.MSG_TITLE);
		Log.d(TAG, "MSG_TITLE: " + title);
		String summary = bundle.getString(Constants.MSG_SUMMARY);
		Log.d(TAG, "MSG_SUMMARY: " + summary);
		String uri = bundle.getString(Constants.MSG_URI);
		Log.d(TAG, "MSG_URI: " + uri);
		
		if (isNotificationEnabled()) {
			if (isNotificationToastEnabled()) {
				Toast.makeText(context, title, Toast.LENGTH_LONG).show();
			}
			Notification notification = new Notification();
			notification.icon = getNotificationIcon();
			notification.defaults = Notification.DEFAULT_LIGHTS;
			if (isNotificationSoundEnabled()) {
				notification.defaults |= Notification.DEFAULT_SOUND;
			}
			if (isNotificationVibrateEnabled()) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.when = System.currentTimeMillis();
			notification.tickerText = summary;
            
			Intent intent = new Intent(context,
                    NotificationDetailsActivity.class);
            intent.putExtra(Constants.NOTIFICATION_ID, "0");
            intent.putExtra(Constants.NOTIFICATION_TITLE, title);
            intent.putExtra(Constants.NOTIFICATION_MESSAGE, summary);
            intent.putExtra(Constants.NOTIFICATION_URI, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(context, title, summary,
                    contentIntent);
            NotificationManager notificationManager = (NotificationManager) context
    				.getSystemService(Context.NOTIFICATION_SERVICE);
            Random random = new Random(System.currentTimeMillis());
            notificationManager.notify(random.nextInt(), notification);
		}
	}

/*
	private void notify(Context context, Bundle bundle) {
		String title = bundle.getString(Constants.MSG_TITLE);
		String message = bundle.getString(Constants.MSG_SUMMARY);

		Notification notification = new Notification();
		notification.icon = mService.getSharedPreferences().getInt(
				Constants.MSG_PUSH_ICON, 0);
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.when = System.currentTimeMillis();
		notification.tickerText = title;

		Intent intent = new Intent();
		intent.setAction(Constants.ACTION_CLICK_NOTIFICATION);
		intent.putExtra(Constants.RESPTEXT, bundle
				.getString(Constants.RESPTEXT));
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, contentIntent);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int id = bundle.getString(Constants.TAGCID).hashCode();
		notificationManager.notify(id, notification);
	}
*/

	private int getNotificationIcon() {
        return mSharePrefs.getInt(Constants.MSG_PUSH_ICON, 0);
    }

    private boolean isNotificationEnabled() {
        return mSharePrefs.getBoolean(Constants.NOTIFICATION_ENABLE, true);
    }

    private boolean isNotificationSoundEnabled() {
        return mSharePrefs.getBoolean(Constants.NOTIFICATION_SOUND, true);
    }

    private boolean isNotificationVibrateEnabled() {
        return mSharePrefs.getBoolean(Constants.NOTIFICATION_VIBRATE, false);
    }

    private boolean isNotificationToastEnabled() {
        return mSharePrefs.getBoolean(Constants.NOTIFICATION_TOAST, false);
    }

}
