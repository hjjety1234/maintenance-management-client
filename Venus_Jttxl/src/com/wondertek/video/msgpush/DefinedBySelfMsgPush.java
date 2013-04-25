package com.wondertek.video.msgpush;

import com.wondertek.video.msgpush.implbyself.Constants;
import com.wondertek.video.msgpush.implbyself.MsgPushService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * To Implement Message Push by Myself 
 * @author yuhongwei
 *
 */

public class DefinedBySelfMsgPush implements IMsgPush {
    private static final String TAG = "MsgPushDefinition";
    private Context mContext;
    private SharedPreferences mSharePrefs;
    private boolean bServiceEnable;
    
    public DefinedBySelfMsgPush(Context cxt) {
    	mContext = cxt;
        mSharePrefs = mContext.getSharedPreferences(
        		Constants.MSG_PUSH_DEFINITION, Context.MODE_PRIVATE);
        bServiceEnable = mSharePrefs.getBoolean(Constants.MSG_PUSH_ENABLE, false);
    }
   
	@Override
	public void initializeService() {
		Log.d(TAG, ">>>initialize<<<");
        setNotificationIcon(mContext.getResources().	getIdentifier("notification", 
        		"drawable", mContext.getPackageName()));
		if (bServiceEnable) {
			mContext.startService(MsgPushService.getIntent());
		}
	}

	@Override
	public void setServiceEnable(boolean bEnable) {
		Log.d(TAG, "[setServiceEnable] bEnable: " + bEnable);
        if (bEnable) {
        	mContext.startService(MsgPushService.getIntent());
        } else {
        	mContext.stopService(MsgPushService.getIntent());
        }
        bServiceEnable = bEnable;
        Editor edit = mSharePrefs.edit();
        edit.putBoolean(Constants.MSG_PUSH_ENABLE, bEnable);
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
        edit.putString(Constants.MSG_PUSH_SERVER, url);
        edit.commit();
	}

	@Override
	public String getBindUrl() {
		Log.d(TAG, "[getBindUrl]");
		return	mSharePrefs.getString(Constants.MSG_PUSH_SERVER, "");
	}

	@Override
	public void setBindPort(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBindPort() {
		// TODO Auto-generated method stub
		return -1;
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
		// TODO Auto-generated method stub
		
	}
    
	private void setNotificationIcon(int iconId) {
		Editor editor = mSharePrefs.edit();
		editor.putInt(Constants.MSG_PUSH_ICON, iconId);
		editor.commit();
	}
    
}
