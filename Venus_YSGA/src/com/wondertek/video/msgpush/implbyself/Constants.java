package com.wondertek.video.msgpush.implbyself;

/**
 * Constants for Push Service
 * @author yuhongwei
 *
 */
public class Constants {
    
    public static final String MSG_ID = "MsgId";
    public static final String MSG_ISNEW = "IsNew";
	public static final String MSG_TITLE = "Title";
	public static final String MSG_SUMMARY = "Summary";
	public static final String MSG_URI = "MsgUri";
    public static final String MSG_NEXT = "MsgNext";
	
    // NOTIFICATION FIELDS
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";
    
	public static final String MSG_PUSH_DEFINITION = "MsgPushDefinition";
	public static final String MSG_PUSH_SERVER = "Server";
	public static final String MSG_PUSH_ENABLE = "Enable";
	public static final String MSG_PUSH_ICON = "Icon";
    
	public static final String NOTIFICATION_ENABLE = "NotificationEnable";
	public static final String NOTIFICATION_SOUND = "NotificationSound";
	public static final String NOTIFICATION_VIBRATE = "NotificationVibrate";
	public static final String NOTIFICATION_TOAST = "NotificationToast";
    
	public static final String USER_NAME = "UserName";
	public static final String USER_PASSWORD = "UserPassword";
	public static final String USER_INTERVAL = "UserInterval";
	
	public static final String ACTION_SHOW_NOTIFICATION = "com.wondertek.video.msgpush.implbyself.SHOW_NOTIFICATION";
	public static final String SERVICENAME = "com.wondertek.video.msgpush.implbyself.MsgPushService";

	public static final int TIMEOUT = 6 * 1000; 
	public static final int BUFFER = 1024;	

	public static final int RETRY = 5;
}
