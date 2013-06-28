package com.wondertek.video.msgpush;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * Manage Message Push
 * @author yuhongwei
 *
 */
public class MsgPushManager {
    private static final int MSG_PUSH_ANDROIDPN = 0x1000;
    private static final int MSG_PUSH_DEFINITION = 0x1002;
    private static final int MSG_PUSH_TYPE = MSG_PUSH_DEFINITION;
    public static final String MSG_PUSH_PREFS = "MsgPushSharePrefs";
    
    private static MsgPushManager instance = null;
	private static boolean notificationMessageUpdated = false;
    private boolean isUserChanged = false;
	private Handler handler = null;
	private Runnable runnable;
	private Context mContext;
    private IMsgPush mMsgPush;
    
	private MsgPushManager (Context cxt) {
		this.mContext = cxt;
        switch (MSG_PUSH_TYPE) {
			case MSG_PUSH_ANDROIDPN :
				mMsgPush = new AndroidpnMsgPush(mContext);
				break;
			case MSG_PUSH_DEFINITION :
				mMsgPush = new DefinedBySelfMsgPush(mContext);
				break;
			default :
				mMsgPush = null;
				break;
		}
	}
    
	public static MsgPushManager getInstance(Context cxt) {
		if (instance == null) {
			instance = new MsgPushManager(cxt);
		}
		return instance;
	}
	
	public static MsgPushManager getInstance() {
		return instance;
	}
	
	public void init() {
		if (mMsgPush != null) {
			System.load(VenusApplication.appAbsPath	+ "lib2/msgpush/libmsgpush.so");
			mMsgPush.initializeService();
		}
	}
	
	public void javaSetServiceStartOrStop(boolean isStart) {
		if (mMsgPush != null) {
			mMsgPush.setServiceEnable(isStart);
		}
	}
	
	public boolean javaGetServiceStatus() {
		if (mMsgPush != null) {
			return mMsgPush.getServiceState();
		}
		return false;
	}
	
	public void javaSetXppHostIp(String url) {
		if (mMsgPush != null) {
            mMsgPush.setBindUrl(url);
		}
	}
	
	public void javaSetXppHostPort(int hostPort) {
		if (mMsgPush != null) {
            mMsgPush.setBindPort(hostPort);
		}
	}
	
	public void javaSetApiKey(String apiKey) {
		if (mMsgPush != null) {
            mMsgPush.setMsgPushApiKey(apiKey);
		}
	}
    
	public void javaSetUser(String key, String value) {
		if (key != null && !"".equals(key.trim())) {
			if (key.trim().equals("username")) {
				isUserChanged = checkUser(value);
			}
            if (mMsgPush != null) {
            	mMsgPush.setUserDefinition(key, value);
            }
		}
	}
    
	public String javaGetUser(String key) {
		if (key != null && !"".equals(key.trim())) {
			if (mMsgPush != null) {
				return mMsgPush.getUserDefinition(key);
			}
		}
        return null;
	}
    
	public boolean isUserChanged() {
		return isUserChanged;
	}
	
	public void javaSetNotifyStyle(String key, int value) {
        boolean bEnable = value != 0 ? true : false;
		if (mMsgPush != null) {
            if (key != null && !"".equals(key.trim())) 
            	mMsgPush.setNotifyStyle(key, bEnable);
		}
	}
	
	public boolean javaGetNotifyStyle(String key) {
		if (mMsgPush != null) {
            if (key != null && !"".equals(key.trim()))
            	return mMsgPush.getNotifyStyle(key);
		}
        return false;
	}
    
	public native void nativeSendNotificationMessage(String title, String message, String uri);
	
	public void notificationCallBack() {
		if (notificationMessageUpdated) {
			handler = new Handler();
			runnable = new Runnable() {
				@Override
				public void run() {
					Util.Trace("===notificationCallBack ====");
					sendNotificationMessage();
					handler.removeCallbacks(runnable);
					handler = null;
				}
			};
			handler.postDelayed(runnable, 500);
			
			notificationMessageUpdated = false;
		}
	}
	
	public static void setNotificationMessageUpdated() {
		notificationMessageUpdated = true;
	}
	
	private void sendNotificationMessage() {
		Util.Trace("===sendNotificationMessage ====");
		SharedPreferences sharedPrefs = mContext.getSharedPreferences(
                MSG_PUSH_PREFS, Context.MODE_PRIVATE);
		String notificationId = sharedPrefs.getString("NOTIFICATION_ID", "");
		Util.Trace("notificationId = " + notificationId);
		String notificationTitle = sharedPrefs.getString("NOTIFICATION_TITLE", "");
		Util.Trace("notificationTitle = " + notificationTitle);
		String notificationMessage = sharedPrefs.getString("NOTIFICATION_MESSAGE", "");
		Util.Trace("notificationMessage = " + notificationMessage);
		String notificationUri = sharedPrefs.getString("NOTIFICATION_URI", "");
		Util.Trace("notificationUri = " + notificationUri);
		if ( ! "".equals(notificationId)) {
			nativeSendNotificationMessage(notificationTitle, notificationMessage, notificationUri);
		}
	}
	
	private boolean checkUser(String name) {
		if (!name.trim().equals(javaGetUser("username"))) {
			return true;
		} else {
			return false;
		}
	}
	
}
