package com.wondertek.video.msgpush.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.wbtech.common.CommonUtil;
import com.wondertek.video.msgpush.IMsgPush;
import com.wondertek.video.msgpush.implbyself.Constants;

/**
 * To Implement MQTT Message Push
 * 
 * @author hewu
 * 
 */

public class MqttMsgPush implements IMsgPush {
	private static final String TAG = "MqttMsgPush";
	private Context mContext;
	private SharedPreferences mSharePrefs;
	private boolean bServiceEnable;
	private String imei;

	private final String mDefaultHost = "120.209.131.150";
	private final int mDefaultPort = 1883;
	private final String mDefaultAppkey = "5af2f40e1bf4e13cdbafbea1057affd0";

	public MqttMsgPush(Context cxt) {
		mContext = cxt;
		mSharePrefs = mContext.getSharedPreferences(MqttPushService.TAG,
				Context.MODE_PRIVATE);

		// get device id
		imei = CommonUtil.getDeviceID(mContext);
		if (imei == null || "".equals(imei) || "null".equals(imei)) {
			imei = "unknowDeviceid";
		}

		// set default SharedPreferences
		Editor editor = mSharePrefs.edit();
		editor.putString(MqttPushService.PREF_DEVICE_ID, imei);
		editor.putString(MqttPushService.APPKEY, mDefaultAppkey);
		editor.putInt(MqttPushService.QUALITIES_OF_SERVICE, 0);
		editor.putString(MqttPushService.MQTT_HOST, mDefaultHost);
		editor.putInt(MqttPushService.MQTT_BROKER_PORT_NUM, mDefaultPort);
		editor.commit();

		bServiceEnable = mSharePrefs.getBoolean(MqttPushService.PREF_STARTED,
				false);
	}

	@Override
	public void initializeService() {
		Log.d(TAG, ">>>initialize<<<");
	}

	@Override
	public void setServiceEnable(boolean bEnable) {
		Log.d(TAG, "[setServiceEnable] bEnable: " + bEnable);
		if (bEnable) {
			MqttPushService.actionStart(mContext);
		} else {
			MqttPushService.actionStop(mContext);
		}
		bServiceEnable = bEnable;
		Editor edit = mSharePrefs.edit();
		edit.putBoolean(MqttPushService.PREF_STARTED, bEnable);
		edit.commit();
	}

	@Override
	public boolean getServiceState() {
		Log.d(TAG, "[getServiceEnable]");
		return bServiceEnable;
	}

	@Override
	public void setBindUrl(String url) {
		Log.d(TAG, "[setBindUrl]  url: " + url);
		Editor edit = mSharePrefs.edit();
		edit.putString(MqttPushService.MQTT_HOST, url);
		edit.commit();
	}

	@Override
	public String getBindUrl() {
		Log.d(TAG, "[getBindUrl]");
		return mSharePrefs.getString(MqttPushService.MQTT_HOST, "");
	}

	@Override
	public void setBindPort(int port) {
		Log.d(TAG, "[setBindPort]  port: " + port);
		Editor edit = mSharePrefs.edit();
		edit.putInt(MqttPushService.MQTT_BROKER_PORT_NUM, port);
		edit.commit();

	}

	@Override
	public int getBindPort() {
		Log.d(TAG, "[getBindPort]");
		return mSharePrefs.getInt(MqttPushService.MQTT_BROKER_PORT_NUM, 0);
	}

	@Override
	public void setNotifyStyle(String key, boolean bEnable) {
		Log.d(TAG, "[setNotifyStyle]  key: " + key + "  bEnable: " + bEnable);
		Editor edit = mSharePrefs.edit();
		if (key.equalsIgnoreCase("Notification")) {
			edit.putBoolean(Constants.NOTIFICATION_ENABLE, bEnable);
		} else if (key.equalsIgnoreCase("Sound")) {
			edit.putBoolean(Constants.NOTIFICATION_SOUND, bEnable);
		} else if (key.equalsIgnoreCase("Vibrate")) {
			edit.putBoolean(Constants.NOTIFICATION_VIBRATE, bEnable);
		} else if (key.equalsIgnoreCase("Toast")) {
			edit.putBoolean(Constants.NOTIFICATION_TOAST, bEnable);
		}
		edit.commit();
	}

	@Override
	public boolean getNotifyStyle(String key) {
		Log.d(TAG, "[getNotifyStyle]  key: " + key);
		if (key.equalsIgnoreCase("Notification")) {
			mSharePrefs.getBoolean(Constants.NOTIFICATION_ENABLE, true);
		} else if (key.equalsIgnoreCase("Sound")) {
			mSharePrefs.getBoolean(Constants.NOTIFICATION_SOUND, true);
		} else if (key.equalsIgnoreCase("Vibrate")) {
			mSharePrefs.getBoolean(Constants.NOTIFICATION_VIBRATE, false);
		} else if (key.equalsIgnoreCase("Toast")) {
			mSharePrefs.getBoolean(Constants.NOTIFICATION_TOAST, false);
		}
		return false;
	}

	@Override
	public void setUserDefinition(String key, String value) {
		Log.d(TAG, "[setUserDefinition]  key: " + key + "  value: " + value);
		Editor edit = mSharePrefs.edit();
		if (key.equalsIgnoreCase("username")) {
			edit.putString(Constants.USER_NAME, value);
		} else if (key.equalsIgnoreCase("password")) {
			edit.putString(Constants.USER_PASSWORD, value);
		} else if (key.equalsIgnoreCase("interval")) {
			edit.putString(Constants.USER_INTERVAL, value);
		}
		edit.commit();
	}

	@Override
	public String getUserDefinition(String key) {
		Log.d(TAG, "[getUserDefinition]  key: " + key);
		if (key.equalsIgnoreCase("username")) {
			mSharePrefs.getString(Constants.USER_NAME, "");
		} else if (key.equalsIgnoreCase("password")) {
			mSharePrefs.getString(Constants.USER_PASSWORD, "");
		} else if (key.equalsIgnoreCase("interval")) {
			mSharePrefs.getString(Constants.USER_INTERVAL, "");
		}
		return null;
	}

	@Override
	public void setMsgPushApiKey(String apiKey) {
		Log.d(TAG, "[setMsgPushApiKey]  apiKey: " + apiKey);
		Editor edit = mSharePrefs.edit();
		edit.putString(MqttPushService.APPKEY, apiKey);
		edit.commit();

	}
}
