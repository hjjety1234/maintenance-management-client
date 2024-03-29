package com.wondertek.video;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.wondertek.video.connection.SystemConnectionManager;
import com.wondertek.video.g3wlan.client.G3WLANHttp;

public class Util {
	private static String TAG = "WriteLogs";
	
	private static Util instance = null;
	
	public static int PHONE_PLATFORM_GENERAL 	= 0;
	public static int PHONE_PLATFORM_OMS	 	= 1;
	public static int PHONE_PLATFORM_CUSTOM	 	= 2;
	
	private static String WLAN_PING_URL				= "http://www.baidu.com";
	private static String WLAN_PORTAL__URL			= "http://www.10086.com/index.html";

	//Message to the engine
	
	
    //////////////////////////////////////////////////////////////////////////
    //Base Event
	public final static int  WDM_NULL =           0x0000;
	public final static int  WDM_RUN  =           0x0001;
	public final static int  WDM_STOP   =         0x0002;
	public final static int  WDM_ACTIVATE  =      0x0004;
	public final static int  WDM_MOUSEDOWN =      0x0005;
	public final static int  WDM_MOUSEUP   =      0x0006;
	public final static int  WDM_MOUSEMOVE  =     0x0007;
	public final static int  WDM_MOUSELONGPRESS=  0x0008;
	public final static int  WDM_KEYDOWN    =     0x0009;
	public final static int  WDM_KEYUP      =     0x000a;
	public final static int  WDM_KEYLONGPRESS =   0x000b;
	public final static int  WDM_KEYSYSTEM  =     0x000c;
	public final static int  WDM_TIMER      =     0x000d;
	public final static int  WDM_DRAW       =     0x000e;

	public final static int  WDM_GestureBegin =   0x000f;
	public final static int  WDM_GestureMove  =   0x0010;
	public final static int  WDM_GestureEnd   =   0x0011;

   //Custom SysEvent
	public final static int  WDM_SYSPAUSE     =   0x0100;
	public final static int  WDM_SYSRESUME    =   0x0101;
	public final static int  WDM_SCREENLOCK   =   0x0102;
	public final static int  WDM_SCREENROTATE =   0x0103;
	public final static int  WDM_DIALUP       =   0x0104 ;//  use WDM_NETWORK
	public final static int  WDM_NETWORK     =    0x0105;
	public final static int  WDM_HTTPPIPE     =   0x0106;
	public final static int  WDM_TURNBACKLIGHT=   0x0107;
	public final static int  WDM_SMS         =    0x0108;
	public final static int  WDM_WLAN        =    0x0109;
	public final static int  WDM_APPSTART       = 0x010a;
	public final static int  WDM_APPINSTALL     = 0x010b;
	public final static int  WDM_APPUNINSTALL    =0x010c;
	public final static int  WDM_AIRPLANE       = 0x010e;
	public final static int  WDM_SD           =   0x010f;
	public final static int  WDM_CONTACTS       = 0x0110;
	public final static int  WDM_VOICEINPUT    =  0x0111;
	public final static int  WDM_PHONEGAPMSG    = 0x0112;
	public final static int  WDM_STRINGEVENT    = 0x0113;
	public final static int  WDM_SEARCHCONTACTS = 0x0114;
	public final static int  WDM_CONTACTSGROUP    = 0x0115;
	public final static int  WDM_EACHCONTACTSGROUPINFO = 0x0116;
	
//Engine Event 
	public final static int  WDM_ENGINE_0      =  0x0800;
	public final static int  WDM_ENGINE_MAX    =  0x0fff;

//User Event
	public final static int  WDM_USER         =   0x1000;

//Touch Event
	public final static int  TOUCH_CANCEL     =   0x0000;
	public final static int  TOUCH_DOWN       =   0x0005;
	public final static int  TOUCH_UP         =   0x0006;
	public final static int  TOUCH_MOVE       =   0x0007;
	public final static int  TOUCH_LONGPRESS  =   0x0008;

	//Status of Network
	public final static int ENetworkStatus_Connecting 			= 0;
	public final static int ENetworkStatus_Connected			= 1;
	public final static int ENetworkStatus_DisConnected			= 2;
	public final static int ENetworkStatus_UserDisConnected		= 3;
	public final static int ENetworkStatus_Invalid				= 4;
	public final static int ENetworkStatus_UnKnownError			= 5;
	public final static int ENetworkStatus_KeepConnected		= 6;
	public final static int ENetworkStatus_ConnectionExp		= 7;

	public final static int ENetworkError_Trans_Fail				= 0;
	public final static int ENetworkError_Trans_Login				= 1;
	public final static int ENetworkError_Trans_InvalidAPN			= 2;
	public final static int ENetworkError_Trans_WLanAutoConnClosed	= 3;
	public final static int ENetworkError_Trans_Password			= 4;
	public final static int ENetworkError_Trans_ShowNetSetting		= 5;
	public final static int ENetworkError_Trans_DataConnected		= 6;
	public final static int ENetworkError_Trans_ConnectedChange		= 7;
	
	//WLAN Type
	public final static int EWLanType_Normal		= 0;
	public final static int EWLanType_Encryption	= 1;
	public final static int EWLanType_Portal		= 2;
	
	public final static int Network_Connected_Unknown	= -1;
	public final static int Network_Connected_WiFi	= 0;
	public final static int Network_Connected_WAP	= 1;
	public final static int Network_Connected_NET	= 2;
	public final static int Network_Connected_WAP_2	= 3;
	public final static int Network_Connected_WAP_3	= 4;
	public static int m_nNetwork_Connected_Type	 = Network_Connected_Unknown;	//0 - WiFi, 1 - CMWAP, -1 - Unknown
	
	public final static int WIFI_STATE_IDLE			= 0;
	public final static int WIFI_STATE_CONNECTING		= 1;
	public final static int WIFI_STATE_CONNECTED		= 2;
	public static int WIFI_STATE				= WIFI_STATE_IDLE;
	
	
	public final static int NetworkChip_Unknow		= -1;
	public final static int NetworkChip_Single		= 0;
	public final static int NetworkChip_Multiple	= 1;
	private static int m_nMultipleNetworkChip	= NetworkChip_Unknow;		//0 - Single, 1 - Multiple, -1 - Unknown
	
	
	public  final static int SDK_ANDROID_15			= 0;
	public  final static int SDK_ANDROID_16			= 1;
	public  final static int SDK_ANDROID_21			= 2;
	public  final static int SDK_ANDROID_22			= 3;
	public  final static int SDK_ANDROID_23			= 4;
	public  final static int SDK_ANDROID_31			= 5;
	public  final static int SDK_ANDROID_32			= 6;
	public  final static int SDK_ANDROID_33			= 7;
	public  final static int SDK_ANDROID_40			= 8;
	public  final static int SDK_OMS_15				= 9;
	public  final static int SDK_OMS_16				= 10;
	public  final static int SDK_OMS_20				= 11;
	public  final static int SDK_OMS_25				= 12;
	public  final static int SDK_OMS_26				= 13;
	public  final static int SDK_ANDROID_41			= 14;
	public  final static int SDK_ANDROID_42			= 15;
	
	private static int mSDK							= SDK_ANDROID_21;
	
/////////////////////PAY ATTENTION TO THIS SECTION/////////////////////////////
//	private final static String wifiAutoConnect_UA_Table[] = {
//		"t80_android",
//		"t81_android",
//		"t89_android",
//		"hisense_t92_android",
//	};
///////////////////////////////////////////////////////////////////////////////
	
	
	private Util()
	{
		//TODO
	}

	public static Util getInstance()
	{
		if(instance == null)
		{
			instance = new Util();
		}
		return instance;
	}

	public void Init()
	{

		debug = Integer.parseInt(VenusApplication.getInstance().getResString("debug"));
		
		//Init SDK 
		if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_GENERAL || VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_CUSTOM)
		{
			String strSDK = Build.VERSION.SDK;
			Util.Trace("Build.VERSION.SDK = " + strSDK);
			int sdk = Integer.parseInt(strSDK);
			
			if(sdk == 3)
			{
				mSDK = SDK_ANDROID_15;
			}
			else if(sdk == 4)
			{
				mSDK = SDK_ANDROID_16;
			}
			else if(sdk>4 && sdk<=7)
			{
				mSDK = SDK_ANDROID_21;
			}
			else if(sdk == 8)
			{
				mSDK = SDK_ANDROID_22;
			}
			else if(sdk >= 9 && sdk <=10)
			{
				mSDK = SDK_ANDROID_23;
			}
			else if(sdk == 11)
			{
				mSDK = SDK_ANDROID_31;
			}
			else if(sdk == 12)
			{
				mSDK = SDK_ANDROID_32;	
			}
			else if(sdk == 13)
			{
				mSDK = SDK_ANDROID_33;
			}
			else if(sdk >=14 && sdk <= 15)
			{
				mSDK = SDK_ANDROID_40;
			}
			else if(sdk == 16)
			{
				mSDK = SDK_ANDROID_41;
			}
			else if(sdk == 17)
			{
				mSDK = SDK_ANDROID_42;
			}
		}
		else if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_OMS)
		{
			String strSDK = Build.VERSION.SDK;
			Util.Trace("Build.VERSION.SDK = " + strSDK);
			int sdk = Integer.parseInt(strSDK);
			if(sdk == 3)
			{
				mSDK = SDK_OMS_15;
			}
			else if(sdk == 4)
			{
				mSDK = SDK_OMS_16;
			}
			else if(sdk >= 5 && sdk <= 7)
			{
				mSDK = SDK_OMS_20;
			}
			else if(sdk == 8)
			{
				mSDK = SDK_OMS_25;
			}
			else if(sdk >= 9 && sdk <=10)
			{
				mSDK = SDK_OMS_26;
			}
		}
		
		brandStr = Build.BRAND;
		if(GetSDK() != SDK_ANDROID_15 && GetSDK() != SDK_OMS_15)
		{
			//Reflect
			try {
				Class<?> cls = Class.forName("android.os.Build");
				Field field = cls.getField("MANUFACTURER");
				if (field != null)
				{
					manufacturerStr = (String)field.get(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public final static int Util_HANDLE_MSG_ID_ConnectionChange			= 0;
	public final static int Util_HANDLE_MSG_ID_AIRPLANE_MODE_CHANGED	= 1;
	public final static int Util_HANDLE_MSG_ID_WIFIChange				= 2;

	public static boolean Util_WaitforConnConnected = false; 
	private Handler utilHandler = new Handler()	{
		public void handleMessage(Message msg) {
			SystemConnectionManager.getInstance().HandleMessage(msg);
		}
	};
	
	public Handler getUtilHandler()
	{
		return utilHandler;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final static int WLanEventType_HaveLogin					= 0;
	private final static int WLanEventType_IsPortal						= 1;
	private final static int WLanEventType_NetworkStop					= 2;

	private static boolean   mContactsChange = true;
	private static boolean   mGetContactsFinish = true;
	private static String    mSearchcondition = null;
	
	private static  String params1 = null;
	private static  String params2 = null;
	private static  String params3 = null;
	private static  String params4 = null;
	
	public static void SetContactsChange(boolean change)
	{
		mContactsChange = change; 
	}

	/**
	 * 
	 * android.os.AsyncTask<Params, Progress, Result>
	 * 
	 * The three types used by an asynchronous task are the following:
	 * 		1. Params, the type of the parameters sent to the task upon execution.
	 * 		2. Progress, the type of the progress units published during the background computation.
	 * 		3. Result, the type of the result of the background computation.
	 * 
	 */
	
	public class UtilAsynchTask extends AsyncTask<String, Void, Void> {
		
		//Runs on the UI thread before doInBackground(Params...)
		@Override 
        protected void onPreExecute() {
			//TODO
        }

		//Override this method to perform a computation on a background thread.
		@Override
		protected Void doInBackground(String... params) {
			if( "WLanHaveLogin".equals(params[0]) )
			{
				if( G3WLANHttp.getInstance().wlanHaveLogin() )
				{
					VenusActivity.getInstance().nativesendevent(Util.WDM_WLAN, WLanEventType_HaveLogin, 1);
				}
				else
				{
					VenusActivity.getInstance().nativesendevent(Util.WDM_WLAN, WLanEventType_HaveLogin, 0);
				}
			}
			else if( "WLanIsPortal".equals(params[0]) )
			{
				Object paramArray[] = {params[1],};
				int result = VenusActivity.getInstance().nativeExec("WLan_IsPortal", paramArray, paramArray.length);
				VenusActivity.getInstance().nativesendevent(Util.WDM_WLAN, WLanEventType_IsPortal, result);
			}
			else if( "NetworkStop".equals(params[0]))
			{
				int result = VenusActivity.getInstance().nativeExec("NetworkStop", null, 0);
				VenusActivity.getInstance().nativesendevent(Util.WDM_WLAN, WLanEventType_NetworkStop, result);
			}
			else if( "GetContacts".equals(params[0]))
			{
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							if(mContactsChange)
							{
								mContactsChange = false;
								Util.Trace("---Contacts have been changed---");
								android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
								boolean error = false;
								FileWriter writer = null;
								File contactFile = new File(VenusApplication.getInstance().appAbsPath, "contacts.txt");
								if (contactFile.exists()) {
									contactFile.delete();
								}
								String contactsList = VenusActivity.getInstance().getContacts();
								if(contactsList.length() > 0)
								{
									contactsList = contactsList.substring(0, contactsList.length()-1);
									try {
										contactFile.createNewFile();
										writer = new FileWriter(contactFile, true);
										writer.write(contactsList);
									} catch (IOException e) {
										error = true;
										Util.Trace(e.toString());
									} finally {
										try {
											if(writer != null) writer.close();
											if(error && contactFile.exists())
											{
												contactFile.delete();
											}
											if(contactFile.exists())
											{
												Util.Trace("Get contacts SUCCESS");
											}
											else
											{
												Util.Trace("Get contacts FAIL");
											}
										} catch (IOException e) {
										}
									}
								}
							}
							VenusActivity.getInstance().nativesendevent(Util.WDM_CONTACTS, 0, 0);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "GetSearchContacts".equals(params[0]))
			{
				mSearchcondition = params[1];
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---Contacts have been changed---");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							boolean error = false;
							FileWriter writer = null;
							File contactFile = new File(VenusApplication.getInstance().appAbsPath, "searchcontacts.txt");
							if (contactFile.exists()) {
								contactFile.delete();
							}
							String contactsList = VenusActivity.getInstance().getSearchContacts(mSearchcondition);
							if(contactsList.length() > 0)
							{
								contactsList = contactsList.substring(0, contactsList.length()-1);
								try {
									contactFile.createNewFile();
									writer = new FileWriter(contactFile, true);
									writer.write(contactsList);
								} catch (IOException e) {
									error = true;
									Util.Trace(e.toString());
								} finally {
									try {
										if(writer != null) writer.close();
										if(error && contactFile.exists())
										{
											contactFile.delete();
										}
										if(contactFile.exists())
										{
											Util.Trace("Get contacts SUCCESS");
										}
										else
										{
											Util.Trace("Get contacts FAIL");
										}
									} catch (IOException e) {
									}
								}
							}
							VenusActivity.getInstance().nativesendevent(Util.WDM_SEARCHCONTACTS, 0, 0);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "GetContactsGroup".equals(params[0]))
			{
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---Contacts have been changed---");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							boolean error = false;
							FileWriter writer = null;
							File contactFile = new File(VenusApplication.getInstance().appAbsPath, "contactsgroup.txt");
							if (contactFile.exists()) {
								contactFile.delete();
							}
							String contactsList = VenusActivity.getInstance().getContactsGroup();
							if(contactsList.length() > 0)
							{
								contactsList = contactsList.substring(0, contactsList.length()-1);
								try {
									contactFile.createNewFile();
									writer = new FileWriter(contactFile, true);
									writer.write(contactsList);
								} catch (IOException e) {
									error = true;
									Util.Trace(e.toString());
								} finally {
									try {
										if(writer != null) writer.close();
										if(error && contactFile.exists())
										{
											contactFile.delete();
										}
										if(contactFile.exists())
										{
											Util.Trace("Get contacts SUCCESS");
										}
										else
										{
											Util.Trace("Get contacts FAIL");
										}
									} catch (IOException e) {
									}
								}
							}
							VenusActivity.getInstance().nativesendevent(Util.WDM_CONTACTSGROUP, 0, 0);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "GetEachContactsGroupInfo".equals(params[0]))
			{
				mSearchcondition = params[1];
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---Contacts have been changed---");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							boolean error = false;
							FileWriter writer = null;
							File contactFile = new File(VenusApplication.getInstance().appAbsPath, "eachcontactsgroupinfo.txt");
							if (contactFile.exists()) {
								contactFile.delete();
							}
							String contactsList = VenusActivity.getInstance().getEachContactsGroupInfo(mSearchcondition);
							if(contactsList.length() > 0)
							{
								contactsList = contactsList.substring(0, contactsList.length()-1);
								try {
									contactFile.createNewFile();
									writer = new FileWriter(contactFile, true);
									writer.write(contactsList);
								} catch (IOException e) {
									error = true;
									Util.Trace(e.toString());
								} finally {
									try {
										if(writer != null) writer.close();
										if(error && contactFile.exists())
										{
											contactFile.delete();
										}
										if(contactFile.exists())
										{
											Util.Trace("Get contacts SUCCESS");
										}
										else
										{
											Util.Trace("Get contacts FAIL");
										}
									} catch (IOException e) {
									}
								}
							}
							VenusActivity.getInstance().nativesendevent(Util.WDM_EACHCONTACTSGROUPINFO, 0, 0);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "AddContact".equals(params[0]))
			{
				 params1 = params[1];
				 params2 = params[2];
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---AddContact--run-");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							VenusActivity.getInstance().addContact(params1,  params2);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "DeleteContact".equals(params[0]))
			{
			    params1 = params[1];
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---DeleteContact--run-");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							VenusActivity.getInstance().deleteContact(params1);
							mGetContactsFinish = true;
						}}).start();
				}
			}else if( "EditContact".equals(params[0]))
			{
			    params1 = params[1];
			    params2 = params[2];
			    params3 = params[3];
				params4 = params[4];
				if(mGetContactsFinish)
				{
					mGetContactsFinish = false;
					new Thread(new Runnable(){

						public void run() {
							Util.Trace("---EditContact-run--");
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							VenusActivity.getInstance().editContact(params1, params2, params3, params4);
							mGetContactsFinish = true;
						}}).start();
				}
			}
			
			//Can invoke publishProgress() to communicate with the main UI thread
			return null;
		}
		
		//Runs on the UI thread after publishProgress(Progress...) is invoked.
		@Override  
        protected void onProgressUpdate(Void... progress) { 
			//TODO
			super.onProgressUpdate(progress);
        } 
		
		//Runs on the UI thread after doInBackground(Params...)
		@Override		
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);			
		} 
		
		//Runs on the UI thread after cancel(boolean) is invoked.
		@Override 
        protected void onCancelled() { 
            super.onCancelled(); 
        }  
	
	}
	
	public UtilAsynchTask getAsyncTask()
	{
		return new UtilAsynchTask();
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void setMultipleNetworkChip(int multi)
	{
		m_nMultipleNetworkChip = multi;
	}
	
	public static int getMultipleNetworkChip()
	{
		return m_nMultipleNetworkChip;
	}
	
	public static void detectNetworkChip()
	{
		m_nMultipleNetworkChip = NetworkChip_Single;
		if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_GENERAL)
		{
			ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfoArray[] = connMan.getAllNetworkInfo();
			if(netinfoArray != null)
			{
				int i = 0;
				
				for(; i<netinfoArray.length; i++)
				{
					NetworkInfo netInfo = netinfoArray[i];
					Util.Trace("Network Type = " + netInfo.getType());
					if(netInfo.getType() != ConnectivityManager.TYPE_WIFI )
					{
						/** Indicates whether network connectivity is possible. A network is unavailable when a persistent or semi-persistent condition prevents the possibility of connecting to that network. Examples include
	    						- The device is out of the coverage area for any network of this type.
	    						- The device is on a network other than the home network (i.e., roaming), and data roaming has been disabled.
	    						- The device's radio is turned off, e.g., because airplane mode is enabled.
						 */
						//if(netInfo.getState() == NetworkInfo.State.CONNECTED /*&& netInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED*/)
						if(netInfo.getState() == NetworkInfo.State.CONNECTED && netInfo.isAvailable() == true)
						{
							m_nMultipleNetworkChip = NetworkChip_Multiple;
							break;
						}
					}
				}
			}
		}
		else if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_CUSTOM)
		{
			m_nMultipleNetworkChip = NetworkChip_Single;
		}
		else if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_OMS)
		{
			m_nMultipleNetworkChip = NetworkChip_Multiple;
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static WifiManager mWifiManager = null;
	private static ConnectivityManager mConnectivityManager = null;
	public static String currentWifiSSID = "";
	
	public static WifiManager getWifiManager() {
		if (null == mWifiManager) {
			mWifiManager = (WifiManager) VenusActivity.appActivity.getSystemService(VenusActivity.appActivity.WIFI_SERVICE);
		}

		if (null == mWifiManager) {
			Trace(TAG + ": get wifi error!");
		}

		return mWifiManager;
	}
	
	public static ConnectivityManager getConnectivityManager()
	{
		if(mConnectivityManager == null)
		{
			mConnectivityManager = (ConnectivityManager)VenusActivity.appActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return mConnectivityManager;
	}
	public static void setCurrentWifiSSID(String ssid)
	{
		if(ssid != null)
			currentWifiSSID = ssid;
		else
			currentWifiSSID = "";
	}
	
	public static synchronized boolean wifiIsConnected(String ssid)
	{
		NetworkInfo wifiNetInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo.State state = wifiNetInfo.getState();
		String SSID = null;
		WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
		
		if(wifiInfo != null)
		{
			SSID = wifiInfo.getSSID();
            int sdk = Util.GetSDK();
            if (sdk == Util.SDK_ANDROID_42 && SSID.startsWith("\"") && SSID.endsWith("\""))
            {
                SSID = SSID.substring(1, SSID.length()-1);
            }
		}

		if(state != null && SSID != null && state == NetworkInfo.State.CONNECTED && SSID.equals(ssid))
			return true;
		return false;
	}
	
	public static int GetAirplaneMode()
	{
		ContentResolver cr = VenusActivity.appActivity.getContentResolver();
		int on = 0;
		if( Settings.System.getString(cr,Settings.System.AIRPLANE_MODE_ON).equals("0") )
		{
			on = 0;
		}
		else
		{
			on = 1;
		}
		return on;
	}

	public static String GetCurrentAPNProxy()
	{
		return SystemConnectionManager.getInstance().GetCurrentAPNProxy();
	}

	public static String GetCurrentAPNPort()
	{
		return SystemConnectionManager.getInstance().GetCurrentAPNPort();
	}

	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	        	
	            NetworkInterface intf = en.nextElement();
	            
	            String name		= intf.getName();
	            String name1	= intf.getDisplayName();
	            Util.Trace("Interface: " + name + ", " + name1);
	            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) 
	            {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                String ip = inetAddress.getHostAddress().toString();
//	                if (!inetAddress.isLoopbackAddress()) {
//	                    return inetAddress.getHostAddress().toString();
//	                }
	                Util.Trace("ip=" + ip);
	            }
	            
	        }
	    } catch (SocketException ex) {
	        Trace(TAG + ": " +  ex.toString());
	    }
	    return null;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Module: Brightness mode & Time-out Mode 
//
//Description:
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int SCREEN_BRIGHTNESS_MODE 		= -1;
	private static int SCREEN_TIME_OUT				= -1;
	
	public static void saveMachineSettings(ContentResolver aContentResolver)
	{
		try {
			SCREEN_BRIGHTNESS_MODE = Settings.System.getInt(aContentResolver, "screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			SCREEN_TIME_OUT = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void restoreMachineSettings(ContentResolver aContentResolver)
	{
		//SCREEN_BRIGHTNESS_MODE
		if(SCREEN_BRIGHTNESS_MODE != -1)
			Settings.System.putInt(aContentResolver, "screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/, SCREEN_BRIGHTNESS_MODE);
		
		//SCREEN_TIME_OUT
		Settings.System.putInt(aContentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SCREEN_TIME_OUT);
	}
	
	private static int debug = 0;
	
	public static synchronized void Trace(String format)
	{
		if (debug == 0)
		{
			return;
		}

		Log.d(TAG, format==null?"Error: null String!!!":format);
		
		String logfilePath = "/sdcard/Dresden.log";//VenusApplication.getInstance().appAbsPath + "/module/error_log.txt";
		File logFile = new File(logfilePath);

		if(logFile.exists())
		{
			
			try {
				BufferedWriter output = new BufferedWriter(new FileWriter(logFile, true/*Append write*/));
				output.append(format==null?"Error: null String!!!":format);
				output.append("\n");
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	public static void deleteDir(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return; 				//Check the arguments
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 		//Delete all files
			else if (file.isDirectory())
				deleteDir(file); 	//Delete the directory in recursion
		}
		dir.delete();				//Delete the directory
	}
	
	public static void clearOldData(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            // some file or directory that must be reserved
            if ("lib".equals(file.getName()) || "userdata".equals(file.getName())) {
                continue;
            }
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir(file);
        }
    }

	public static int buildInt32(short high, short low)
	{
		int i = high;
		i = (i<<16) | (low&0xFFFF);
		return i;
	}
	
	public static void setUrl(String pingUrl, String portalUrl)
	{
		if(pingUrl != null)
			WLAN_PING_URL = pingUrl;
		if(portalUrl != null)
			WLAN_PORTAL__URL = portalUrl;
	}
	
	public static String getPingUrl()
	{
		return WLAN_PING_URL;
	}
	
	public static String getPortalUrl()
	{
		return WLAN_PORTAL__URL;
	}
	
	public static int lookupHost(String hostname) {
	    InetAddress inetAddress;
	    try {
	        inetAddress = InetAddress.getByName(hostname);
	    } catch (UnknownHostException e) {
	        return -1;
	    }
	    byte[] addrBytes;
	    int addr;
	    addrBytes = inetAddress.getAddress();
	    addr = ((addrBytes[3] & 0xff) << 24)
	            | ((addrBytes[2] & 0xff) << 16)
	            | ((addrBytes[1] & 0xff) << 8)
	            |  (addrBytes[0] & 0xff);
	    return addr;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final String[] factoryMap = new String[] { 
		"MOTOROLA",		"MOTO",		"DOPOD",	"HTC",		"SAMSUNG",		"SONYERICSSON",		"SONY ERICSSON",	"DELL",		"ZTE",		"HUAWEI", 
		"TCL", 			"MEIZU", 	"LG", 		"LENOVO", 	"ASUS",			"ACER", 			"YULONG", 			"CoolPad", 	"Toshiba",	"Philips", 
		"NEWLAND",		"LGE", 		"K-Touch", 	"Haier", 	"HISENSE", 		"Amoi", 			"SHARP",			"T-SMART",
		};
	
	private static final int PHONE_TYPE_COUNT					= 26;

	private static int		phoneManufaturer		= -1;

	private static String manufacturerStr = "";
	private static String brandStr = "";
	private static final String KEYSTRING_USER_AGENT = "user_agent_key"; 	//For OMS
	private static String User_Agent = null;

	private static String content_id = "";

	/**
	 * Get the platform mold, such as Android and OMS
	 * 
	 * @return
	 * 		0 - Android
	 * 		1 - OMS
	 */
	@SuppressWarnings("finally")
	public static int initPhonePlatform()
	{
		int plat = 0;
		String platform = VenusApplication.getInstance().getResString("platform");
		if(platform != null && platform.length() > 0)
		{
			plat = Integer.parseInt(platform);
			if (!detectAndroidOrOPhone())
			{
				plat = 1;
			}
		}

		return plat;
	}

	private static boolean detectAndroidOrOPhone()
	{
		boolean bAndroidPlatForm = true;
        try {
        	Class.forName("oms.content.Action");        //ophone                
        	bAndroidPlatForm = false;        
        }catch (ClassNotFoundException e) {             //android phone        
        }
        return bAndroidPlatForm;
	}
	
	private static FileInputStream getOMSPropertyStream()
	{
		try {		
			File property = new java.io.File("/opl/etc/properties.xml");
			if (property.exists()) {
				return new FileInputStream(property);
			} else {
				property = new java.io.File("/opl/etc/product_properties.xml");
				if (property.exists()) {
					return new FileInputStream(property);
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getProperties(String key, String content)
	{
		String STARTKEY = "<"+key+">";
        String ENDKEY = "</"+key+">";
        content = content.replace("\r", "");
        content = content.replace("\n", "");
       
        int startIndex = content.indexOf(STARTKEY) + STARTKEY.length();
        int endIndex = content.indexOf(ENDKEY);
        if(startIndex > -1 && endIndex > -1)
        {
        	return content.substring(startIndex, endIndex);
        }
        else
        	return null; 
	}
	
	public static String getUserAgent()
	{
		if(User_Agent == null)
		{
			if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_GENERAL || VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_CUSTOM)
			{
				boolean flag = false;
				for (String fact : factoryMap) {
					if (manufacturerStr != null
							&& manufacturerStr.equalsIgnoreCase(fact)) {
						if (!Build.MODEL.toLowerCase().contains(
								manufacturerStr.toLowerCase())) {
							User_Agent = manufacturerStr + "_"
									+ Build.MODEL;
							flag = true;
						}
						break;
					}
					if (brandStr != null && brandStr.equalsIgnoreCase(fact)) {
						if (!Build.MODEL.toLowerCase().contains(
								brandStr.toLowerCase())) {
							User_Agent = brandStr + "_"
									+ Build.MODEL;
							flag = true;
						}
						break;
					}
				}
				if (!flag) {
					User_Agent = Build.MODEL;
				}
				User_Agent = User_Agent.toLowerCase()
						.replaceAll(" ", "_");
				User_Agent = User_Agent.replaceAll("/", "_");
				User_Agent = User_Agent + "_"
						+ "android";
				String apk_id = VenusApplication.getInstance().getResString("apk_id");
				if(!apk_id.equals("A000"))	//'A000' is for 'com.cmcc.mobilevideo + com.wondertek.activity.AppFakeActivity + CMCC'
					User_Agent += "_" + VenusApplication.getInstance().getResString("apk_id");
			} else {
				try {
					FileInputStream is = getOMSPropertyStream();
	                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
	                byte buf[] = new byte[1024];
	                for(int k = 0; -1 != (k = is.read(buf));)
	                	bytearrayoutputstream.write(buf, 0, k); 
	                String fileString = new String(bytearrayoutputstream.toByteArray(), "UTF-8"); 
	                String v = getProperties(KEYSTRING_USER_AGENT, fileString);
	                int eIndex = v.indexOf("/1.0");
	                if(eIndex < 0)
	                	eIndex = v.indexOf("/2.0");
	                String brand = (manufacturerStr != "" && !manufacturerStr.equalsIgnoreCase("unknown")) ? manufacturerStr : brandStr;
	                if(brand != null)
	                {
	                	brand = brand.toLowerCase();
	                	String brand_product = v.substring(0, eIndex).toLowerCase();
	                	if(brand_product.startsWith(brand))
	                	{
	                		String p = brand_product.substring(brand.length());
	                		if(p != null && p.startsWith("_"))
	                		{
	                			User_Agent = brand_product;
	                		}
	                		else
	                		{
	                			User_Agent = brand + "_" + p;
	                		}
	                	}
	                	else
	                	{
	                		if(brand_product.startsWith("_"))
	                		{
	                			User_Agent = brand + brand_product;
	                		}
	                		else
	                		{
	                			User_Agent = brand + "_" + brand_product;
	                		}
	                	}
	                }
	                else
	                {
	                	User_Agent = v.substring(0, eIndex);
	                }
	                User_Agent = User_Agent.toLowerCase().replaceAll(" ", "_");
					User_Agent = User_Agent.replaceAll("/", "_");
	                User_Agent = User_Agent + "_" + "android";
	                String apk_id = VenusApplication.getInstance().getResString("apk_id");
					if(!apk_id.equals("A000"))	//'A000' is for 'com.cmcc.mobilevideo + com.wondertek.activity.AppFakeActivity + CMCC'
						User_Agent += "_" + VenusApplication.getInstance().getResString("apk_id");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return User_Agent;
	}
	
	public static void initPhoneManufaturer()
	{
		int i = 0;
		if(phoneManufaturer < 0)
		{
			String fact = null;
			for (; i<PHONE_TYPE_COUNT; i++) {
				fact = factoryMap[i];
				//Util.Trace("Build.MANUFACTURER = " + Build.MANUFACTURER);
				if ( (manufacturerStr != null && manufacturerStr.equalsIgnoreCase(fact))|| (brandStr != null && brandStr.equalsIgnoreCase(fact)) ) {
					phoneManufaturer = i;
					break;
				}
			}
		}
	}
	
	public static boolean isWifiAutoConnected()
	{

		boolean autoConnected =true;
		boolean expetion = false;
		
//		String ua = getUserAgent();		
//		int n = wifiAutoConnect_UA_Table.length;
//		for(int i=0; i<n; i++)
//		{
//			if(ua.equalsIgnoreCase(wifiAutoConnect_UA_Table[i]))
//			{
//				try {
//					Method method1 = Class.forName("android.os.SystemProperties")
//							.getMethod("get", String.class);
//					String flag = (String) method1.invoke(null,
//							"persist.sys.wlan.autoconnect");
//					if (flag != null && flag.equals("0"))
//					{
//						autoConnected = false;
//						Util.Trace("Check the Wifi auto-connect item!!!");
//					}
//					else
//						autoConnected = true;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			}
//		}
		
		Method method1;
		try {
			method1 = Class.forName("android.os.SystemProperties")
					.getMethod("get", String.class);
			String flag = (String) method1.invoke(null,
					"persist.sys.wlan.autoconnect");
			if (flag != null && flag.equals("0"))
			{
				autoConnected = false;
			}
			else
			{
				autoConnected = true;
			}
		} catch (SecurityException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			expetion = true;
			Util.Trace(e.toString());
			e.printStackTrace();
		} finally {
			if(expetion)
				autoConnected = true;
			Util.Trace("WLAN autoConnected = "  + autoConnected);
		}
		return autoConnected;
	}
	
	public static int GetSDK()
	{
		return mSDK;
	}

	public static void SetIntentData(Intent intent)
    {
    	content_id  = intent.getStringExtra("content_id") != null ? intent.getStringExtra("content_id") : "";
    	Util.Trace("SetIntentData content_id: " + content_id);
    }
    
    public static void executeIntentData()
    {
    	if(!content_id.equals(""))
    	{
    		String strMsg = "content_id|" + content_id;
			if ( VenusActivity.getInstance() != null 
					&& VenusApplication.getInstance().bAppActivityIsRunning == true )
				VenusActivity.getInstance().nativesendeventstring(VenusActivity.Enum_StringEventID_INTENT_DATA, strMsg);
			else
				VenusActivity.startParam = strMsg;

    	}
    	Util.Trace("VenusActivity.startParam strMsg = " + VenusActivity.startParam);
    	//TODO
    }
    
    public static int getCurNetworkType()
    {
    	int nType = -1;
    	NetworkInfo ni = Util.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	if(ni != null)
    		nType = ni.getSubtype();
    	return nType;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void OpenSettingForType(String strType) {
		// TODO Auto-generated method stub
		if(strType.trim().equalsIgnoreCase("GPS"))
		{
			Intent intent = new Intent();
	        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        try
	        {
	        	VenusActivity.getInstance().appActivity.startActivity(intent);
	                   
	           
	        } catch(ActivityNotFoundException ex)
	        {
	            intent.setAction(Settings.ACTION_SETTINGS);
	            try {
	            	VenusActivity.getInstance().appActivity.startActivity(intent);
	            } catch (Exception e) {
	            }
	        }
		}
	}
}