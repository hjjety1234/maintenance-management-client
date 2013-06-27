package com.wondertek.video.smsspam;

import com.wondertek.video.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SMSSpamMgr {
	
	private static SMSSpamMgr instance = null;
	private static SharedPreferences sharePrefs = null;
	
	public SMSSpamMgr(Context ctx) {
		sharePrefs = ctx.getSharedPreferences(SMSSpamConstant.SMS_SPAM_MGR, 
				Context.MODE_PRIVATE);
	}
	
	public static SMSSpamMgr getInstance(Context ctx) {
		if (instance == null) {
			instance = new SMSSpamMgr(ctx);
		}
		return instance;
	}
	
	public void javaSetSpamCondition(String key, String value) {
		Util.Trace(">>>javaSetSpamCondition key: " + key + " value: " + value + "<<<");
		if (key.equals("address")) {
			setSpamAddress(value);
		} else if (key.equals("body")) {
			setSpamBody(value);
		}
	}
	
	public String javaGetSpamCondition(String key) {
		Util.Trace(">>>javaGetSpamCondition key: " + key + "<<<");
		if (key.equals("address")) {
			return getSpamAddress();
		} else if (key.equals("body")) {
			return getSpamBody();
		}
		return null;
	}
	
	public void javaSetSpamState(boolean bEnable) {
		Util.Trace(">>>javaSetSpamState: " + bEnable + "<<<");
		Editor editor = sharePrefs.edit();
		editor.putBoolean(SMSSpamConstant.SMS_SPAM_ISON, bEnable);
		editor.commit();
	}
	
	public boolean javaGetSpamState() {
		Util.Trace(">>>javaGetSpamState<<<");
		return sharePrefs.getBoolean(SMSSpamConstant.SMS_SPAM_ISON, false);
	}
	
	public void javaSetNotifyStyle(String key, boolean bl) {
		Util.Trace(">>>javaSetNotifyStyle key: " + key + " bl: " + bl + "<<<");
		if (key.equals("Notify")) {
			setNotifyEnable(bl);
		} else if (key.equals("Sound")) {
			setNotifySound(bl);
		} else if (key.equals("Vibrate")) {
			setNotifyVibrate(bl);
		} else if (key.equals("Toast")) {
			setNotifyToast(bl);
		}
	}
	
	public boolean javaGetNotifyStyle(String key) {
		Util.Trace(">>>javaGetNotifyStyle key: " + key + "<<<");
		if (key.equals("Notify")) {
			return getNotifyEnable();
		} else if (key.equals("Sound")) {
			return getNotifySound();
		} else if (key.equals("Vibrate")) {
			return getNotifyVibrate();
		} else if (key.equals("Toast")) {
			return getNotifyToast();
		}
		return false;
	}
	
	public void setSpamAddress(String addr) {
		Editor editor = sharePrefs.edit();
		editor.putString(SMSSpamConstant.SMS_SPAM_ADDR, addr);
		editor.commit();
	}
	
	public void setSpamBody(String body) {
		Editor editor = sharePrefs.edit();
		editor.putString(SMSSpamConstant.SMS_SPAM_BODY, body);
		editor.commit();
	}
	
	public String getSpamAddress() {
		return sharePrefs.getString(SMSSpamConstant.SMS_SPAM_ADDR, "");
	}

	public String getSpamBody() {
		return sharePrefs.getString(SMSSpamConstant.SMS_SPAM_BODY, "");
	}
	
	public void setNotifyEnable(boolean bl) {
		Editor editor = sharePrefs.edit();
		editor.putBoolean(SMSSpamConstant.SMS_NOTIFY_ENABLE, bl);
		editor.commit();
	}
	
	public boolean getNotifyEnable() {
		return sharePrefs.getBoolean(SMSSpamConstant.SMS_NOTIFY_ENABLE, true);
	}
	
	public void setNotifySound(boolean bl) {
		Editor editor = sharePrefs.edit();
		editor.putBoolean(SMSSpamConstant.SMS_NOTIFY_SOUND, bl);
		editor.commit();
	}
	
	public boolean getNotifySound() {
		return sharePrefs.getBoolean(SMSSpamConstant.SMS_NOTIFY_SOUND, true);
	}
	
	public void setNotifyVibrate(boolean bl) {
		Editor editor = sharePrefs.edit();
		editor.putBoolean(SMSSpamConstant.SMS_NOTIFY_VIBRATE, bl);
		editor.commit();
	}
	
	public boolean getNotifyVibrate() {
		return sharePrefs.getBoolean(SMSSpamConstant.SMS_NOTIFY_VIBRATE, false);
	}
	
	public void setNotifyToast(boolean bl) {
		Editor editor = sharePrefs.edit();
		editor.putBoolean(SMSSpamConstant.SMS_NOTIFY_TOAST, bl);
		editor.commit();
	}
	
	public boolean getNotifyToast() {
		return sharePrefs.getBoolean(SMSSpamConstant.SMS_NOTIFY_TOAST, false);
	}
	
}
