package com.wondertek.video.msgpush;

import com.wondertek.video.msgpush.androidpn.Constants;
import com.wondertek.video.msgpush.androidpn.ServiceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Use Androidpn SDK to Implements Message Push
 * @author yuhongwei
 *
 */

public class AndroidpnMsgPush implements IMsgPush {
    private static final String TAG = "MsgPushAndroidpn";
    private static final String MSG_PUSH_ANDROIDPN = "MsgPushAndroidpn";
    private static final String MSG_PUSH_SERVICE_ENABLE = "Service_Enable";
    private static final String MSG_PUSH_USERNAME = "Msg_Push_UserName";
    private static final String MSG_PUSH_PASSWORD = "Msg_Push_PassWord";
    private boolean bServiceEnable = false;
    private boolean bConditionChanged = false;
    private Context mContext;
    private ServiceManager msgServiceMgr;

    public AndroidpnMsgPush(Context cxt) {
    	mContext = cxt;
    	msgServiceMgr = new ServiceManager(mContext);
    }
    
	@Override
	public void initializeService() {
		Log.d(TAG, ">>>initialize<<<");
        SharedPreferences sharePrefs = mContext.getSharedPreferences(
        		MSG_PUSH_SERVICE_ENABLE, Context.MODE_PRIVATE);
    	bServiceEnable = sharePrefs.getBoolean(MSG_PUSH_SERVICE_ENABLE, false);
    	msgServiceMgr.setNotificationIcon(mContext.getResources().
    			getIdentifier("notification", "drawable", mContext.getPackageName()));
        if (bServiceEnable) {
        	msgServiceMgr.startService();
        }
	}

	@Override
	public void setServiceEnable(boolean bEnable) {
		Log.d(TAG, "[setServiceEnable] bEnable: " + bEnable);
        if (bEnable) {
            if (bConditionChanged && bServiceEnable) {
            	msgServiceMgr.stopService();
            }
        	msgServiceMgr.startService();
        } else {
        	msgServiceMgr.stopService();
        }
        bServiceEnable = bEnable;
        SharedPreferences sharePrefs = mContext.getSharedPreferences(
        		MSG_PUSH_SERVICE_ENABLE, Context.MODE_PRIVATE);
        Editor edit = sharePrefs.edit();
        edit.putBoolean(MSG_PUSH_SERVICE_ENABLE, bEnable);
        edit.commit();
	}

	@Override
	public boolean getServiceState() {
		Log.d(TAG, "[getServiceEnable]");
    	return bServiceEnable;
	}

	@Override
	public void setBindUrl(String url) {
		Log.d(TAG, "[setBindIp]  ip: " + url);
    	SharedPreferences sharePrefs = mContext.getSharedPreferences(
    			Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    	String oldHostIp = sharePrefs.getString(Constants.XMPP_HOST, "");
        if (!url.equals(oldHostIp)) {
        	Editor edit = sharePrefs.edit();
        	edit.putString(Constants.XMPP_HOST, url);
            edit.commit();
            bConditionChanged = true;
        }
	}

	@Override
	public String getBindUrl() {
		 Log.d(TAG, "[getBindIp]");
	    	SharedPreferences sharePrefs = mContext.getSharedPreferences(
	    			Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
	    	return sharePrefs.getString(Constants.XMPP_HOST, "");
	}

	@Override
	public void setBindPort(int port) {
		Log.d(TAG, "[setServiceEnable]  port: " + port);
    	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int  oldHostPort = sharedPrefs.getInt(Constants.XMPP_PORT, 0);
        if (oldHostPort != port) {
        	Editor edit = sharedPrefs.edit();
        	edit.putInt(Constants.XMPP_HOST, port);
        	edit.commit();
        	bConditionChanged = true;
        }
	}

	@Override
	public int getBindPort() {
		Log.d(TAG, "[getBindPort]");
    	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    	return sharedPrefs.getInt(Constants.XMPP_PORT, -1);
	}

	@Override
	public void setNotifyStyle(String key, boolean bEnable) {
		Log.d(TAG, "[setNotifyStyle]  key: " + key + "  bEnable: " + bEnable);
    	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sharedPrefs.edit();
        if (key.equalsIgnoreCase("Notification")) {
        	edit.putBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, bEnable);
        } else if (key.equalsIgnoreCase("Sound")) {
        	edit.putBoolean(Constants.SETTINGS_SOUND_ENABLED, bEnable);
        } else if (key.equalsIgnoreCase("Vibrate")) {
        	edit.putBoolean(Constants.SETTINGS_VIBRATE_ENABLED, bEnable);
        } else if (key.equalsIgnoreCase("Toast")) {
        	edit.putBoolean(Constants.SETTINGS_TOAST_ENABLED, bEnable);
        }
        edit.commit();
	}

	@Override
	public boolean getNotifyStyle(String key) {
		Log.d(TAG, "[getNotifyStyle]  key: " + key);
    	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    	 if (key.equalsIgnoreCase("Notification")) {
             return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, true);
         } else if (key.equalsIgnoreCase("Sound")) {
             return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
         } else if (key.equalsIgnoreCase("Vibrate")) {
             return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, false);
         } else if (key.equalsIgnoreCase("Toast")) {
             return sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
         }
    	return false;
	}

	@Override
	public void setUserDefinition(String key, String value) {
		Log.d(TAG, "[setUserDefinition]  key: " + key + "  value: " + value);
        if (key.equalsIgnoreCase("username")) {
        	setUserName(value);
        } else if (key.equalsIgnoreCase("password")) {
        	setPassWord(value);
        }
	}

	@Override
	public String getUserDefinition(String key) {
		Log.d(TAG, "[getUserDefinition]  key: " + key);
        SharedPreferences sharePrefs = mContext.getSharedPreferences(
        		MSG_PUSH_ANDROIDPN, Context.MODE_PRIVATE);
        if (key.equalsIgnoreCase("username")) {
        	return sharePrefs.getString(MSG_PUSH_USERNAME, "");
        } else if (key.equalsIgnoreCase("password")) {
        	return sharePrefs.getString(MSG_PUSH_PASSWORD, "");
        }
    	return null;
	}

	@Override
	public void setMsgPushApiKey(String apiKey) {
		Log.d(TAG, "[setMsgPushApiKey]  apiKey: " + apiKey);
    	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor edit = sharedPrefs.edit();
		edit.putString(Constants.API_KEY, apiKey);
		edit.commit();
	}
    
	private void setUserName(String name) {
        if (!getUserDefinition("username").equals(name.trim())) {
        	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
        			MSG_PUSH_ANDROIDPN, Context.MODE_PRIVATE);
        	Editor edit = sharedPrefs.edit();
        	edit.putString(MSG_PUSH_USERNAME, name.trim());
        	edit.commit();
        	bConditionChanged = true;
        }
	}
    
	private void setPassWord(String password) {
        if (!getUserDefinition("password").equals(password.trim())) {
        	SharedPreferences sharedPrefs = mContext.getSharedPreferences(
        			MSG_PUSH_ANDROIDPN, Context.MODE_PRIVATE);
        	Editor edit = sharedPrefs.edit();
        	edit.putString(MSG_PUSH_PASSWORD, password.trim());
        	edit.commit();
            bConditionChanged = true;
        }
	}
}
