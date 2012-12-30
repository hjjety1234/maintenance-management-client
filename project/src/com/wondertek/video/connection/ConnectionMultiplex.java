package com.wondertek.video.connection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class ConnectionMultiplex extends ConnectionImpl{

	private Runnable	connectTask = null;
	private Thread threadMonit = null;
	
	private String mInterfaceName = "";
	
	private int wapFeatureIndex = -1;
	
	private int defaultApnId 		= -1;
	private int destApnId 			= -1;
	private int currentAPNType 		= APN_TYPE_UNKNOWN;
	
	private static boolean Util_WaitforConnConnected = false;
	
	private static int sleeptime = 2000;
	
	private static boolean bInWapFeatureList 	= false;
	private static boolean bSettingShowFlag  	= false;
	private static boolean bDCSettingShowFlag	= false;
	
	private static boolean bDCConnected		 = false;
	private static boolean bHaveUsingNetworkFeature = false;
	
	//Table of wap feature for various devices
	private WapFeature wapFeatureList[] = {
			new WapFeature("xxx", 							"", 					WapFeature.FUNC_Android),	//Default case
			new WapFeature("t-smart_g08_android", 			"", 					WapFeature.FUNC_MTK),
			new WapFeature("t80_android", 					"", 					WapFeature.FUNC_MTK),
			new WapFeature("t81_android", 					"", 					WapFeature.FUNC_MTK),
			new WapFeature("t89_android", 					"", 					WapFeature.FUNC_MTK),
			new WapFeature("hisense_t92_android", 			"", 					WapFeature.FUNC_MTK),
			new WapFeature("lenovo_a366t_android", 			"", 					WapFeature.FUNC_MTK),
			new WapFeature("haier_ht-n8t_android", 			"enableCMWAP", 			WapFeature.FUNC_LeadCore),
			new WapFeature("lenovo_a66t_android", 			"enableWAP", 			WapFeature.FUNC_Samsung),
			new WapFeature("p610s_android", 				"wap", 					WapFeature.FUNC_LG),
			new WapFeature("k-touch_t660_android", 			"enableWAP", 			WapFeature.FUNC_Samsung),
			new WapFeature("samsung_gt-i9108_android", 		"", 					WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-i9108_android", 		"enableWAP", 			WapFeature.FUNC_Samsung),
			new WapFeature("samsung_gt-i9228_android", 		"", 					WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-i9308_android", 		"", 					WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-s5698_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("bird_t9108_android",			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("hisense_t860_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("t868_android", 					"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u970_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("lenovo_a698t_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("ts908_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("tcl-e928_td_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-s5368_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("athens15_td_v2_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("t-smart_d68_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-s5820_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("hisense_t95_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("athens15v2_gb_gemini_android", 	"",						WapFeature.FUNC_MTK),
			new WapFeature("gn700t_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u795_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("yulong_8020_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("lenovo_a798t_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("sphs_on_hsdroid_android", 		"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("rayhov_x9_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u960s3_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("v926_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("gt808_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("801t_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("k-touch_t760_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("x100_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("zte_u880f1_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("gn100t_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("huawei_t8950_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("tcl_e928_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("tf-t610_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("v913_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("htc_t528t_android", 			"enableHTTPPROXY",		WapFeature.FUNC_HTCA4),
			new WapFeature("huawei_t9510e_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("huawei_t8830pro_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("td998a_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("yulong_coolpad8050_android", 	"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("k-touch_t619_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("lenovo_a298t_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("ea9000_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-i9050_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("e09_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("zte_u790_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("coolpad8010_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("d515_android", 					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("desay_ts928_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("t-smart_i08_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("huawei_y_220t_android", 		"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("desay_ts808_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("d68x_android", 					"",						WapFeature.FUNC_MTK),
			new WapFeature("t-smart_d68x_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("gn168t_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("v930_android", 					"",						WapFeature.FUNC_MTK),
			new WapFeature("yulong_8060_android", 			"",						WapFeature.FUNC_MTK),
			new WapFeature("mastone_g9_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("zte_u880s2_android", 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("zte_u950_android",	 			"",						WapFeature.FUNC_MTK),
			new WapFeature("vivo_pd1203tg3_android", 		"",						WapFeature.FUNC_MTK),
			new WapFeature("bbk_s6t_android", 				"",						WapFeature.FUNC_MTK),
			new WapFeature("yulong_8150d_android",	 		"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u795+_android",	 			"",						WapFeature.FUNC_MTK),
			new WapFeature("g18_android",		 			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("samsung_gt-i8538_android",	 	"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u985_android",			 	"",						WapFeature.FUNC_MTK),
			new WapFeature("mt6515tdv2_phone_android",		"",						WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-p3108_android",		"",						WapFeature.FUNC_MTK),
			new WapFeature("samsung_gt-s7568_android",		"",						WapFeature.FUNC_MTK),
			new WapFeature("lephone_td9268_android",		"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("t858_android",					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("hedy_td710_android",			"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("zte_u887_android",				"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("blephone_td505_android",		"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("hisense_hs-t930_android",	 	"",						WapFeature.FUNC_MTK),
			new WapFeature("sh-631m_android",	 			"",						WapFeature.FUNC_MTK),
			new WapFeature("sh837m_android",	 			"",						WapFeature.FUNC_MTK),
			new WapFeature("yulong_8190_android",	 		"",						WapFeature.FUNC_MTK),
			new WapFeature("r811_android",	 				"",						WapFeature.FUNC_MTK),
			new WapFeature("tc005_android",					"enableWAP",			WapFeature.FUNC_Samsung),
			new WapFeature("samsung_gt-n7108_android",	 	"",						WapFeature.FUNC_MTK),
			new WapFeature("zte_u807_android",	 			"",						WapFeature.FUNC_MTK),
			new WapFeature("coolpad8070_android",			"enableWAP",			WapFeature.FUNC_Samsung),
			null
		};
	
	class WapFeature
	{
		private String id = "";
		private String feature = "";
		private int    funcID = 0;
		
		public static final int FUNC_Android 		= 0;
		public static final int FUNC_MTK 			= 1;
		public static final int FUNC_HTC 			= 2;
		public static final int FUNC_HTCA4 			= 3;
		public static final int FUNC_Moto 			= 4;
		public static final int FUNC_LG 			= 5;
		public static final int FUNC_Samsung 		= 6;
		public static final int FUNC_LeOS 			= 7;
		public static final int FUNC_LeadCore 		= 8;
		public static final int FUNC_Androidv4 		= 9;
		
		
		public WapFeature(String ua, String wapFeature, int fID)
		{
			id = ua;
			feature = wapFeature;
			funcID = fID;
		}
		
		public String getID()
		{
			return id;
		}
		
		public String getWapFeature()
		{
			return feature;
		}
		
		public int getFuncID()
		{
			return funcID;
		}
	}
	
	@Override
	public void OpenDataConnection() {
		connectTask = null;
		if(threadMonit != null)
		{
			try {
				threadMonit.stop();
				threadMonit.join(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//TestDC();
		
		final ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		connectTask = new Runnable(){
			public void run() {
				Util.Trace("Custom Phone start to open WAP...");
				mInterfaceName = "";
				int currentTimes = 15;
				int result = APN_STATE_UNKNOWN;
				boolean open = false;
				String feature = wapFeatureList[wapFeatureIndex].getWapFeature();
				
				if( isDCOpened() == false )
				{
					while (currentTimes-- > 0) {
						try {
								IsDataConnectionOpened();
					            if (!bDCConnected)
					            {
					            	VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_DataConnected, 0);
					            	if (bHaveUsingNetworkFeature)
					            	{
					            		//connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
					            		closeDC();
					            	}
					            	return;
					            }
						} catch (Exception e) {
							Util.Trace("@ exception: " + e);
						}
						
						if(result == APN_STATE_UNKNOWN)
						{
							if ( ((Util.GetSDK() == Util.SDK_ANDROID_40) || (Util.GetSDK() == Util.SDK_ANDROID_41)) && !bInWapFeatureList && (currentAPNType != APN_TYPE_WAP) )
							{
								Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...1");
								VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_ShowNetSetting, 0);
							}
							else
							{
								result = openDC();
							}
						}
						else 
						{
							if(isDCOpened())
							{
								open = true;
								break;
							}
						}

						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				else
				{
					open = true;
				}
				
				if(open)
				{
					Util.Trace("mInterfaceName = " + mInterfaceName);
					
					//Send the interface name to engine
					String interfaceValue = mInterfaceName;
					VenusActivity.getInstance().nativeSetParam("interfaceName", interfaceValue, interfaceValue.length());
					
					Util.Trace("...Success...");
					VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);

					Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP;
					Util_WaitforConnConnected = false;
				}
				else
				{
					Util.Trace("...Failure...1");

					Util.m_nNetwork_Connected_Type = Util.Network_Connected_Unknown;
					VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_InvalidAPN, 0);
				}
			}
		};
		
		threadMonit = new Thread(null, connectTask, "Multiplex APN");
		threadMonit.start();
	}

	@Override
	public void HandleMessage(Message msg) {

		switch (msg.what) {
		case Util.Util_HANDLE_MSG_ID_ConnectionChange :
		{
			Bundle bundle = msg.getData();
			int net_type = bundle.getInt("NET_TYPE");
			Util.Trace("net_type = "+net_type);
			if(net_type == Util.Network_Connected_WiFi)	//WIFI
			{
				if(Util.getWifiManager().isWifiEnabled() == false || Util.wifiIsConnected(Util.currentWifiSSID) == false)
				{
					if(Util_WaitforConnConnected == false)
					{
						//The current connection may be lost
						Util.Trace(Util.currentWifiSSID +" Send ENetworkStatus_ConnectionExp (0, 0)");
						
						Util.WIFI_STATE = Util.WIFI_STATE_IDLE;
						
						Util_WaitforConnConnected = true;
						//VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)0));
					}
				}
				else
				{
					if( Util_WaitforConnConnected == true )
					{
						Util.Trace(Util.currentWifiSSID +" Send ENetworkStatus_ConnectionExp (0, 1)");
						Util_WaitforConnConnected = false;
						Util.WIFI_STATE = Util.WIFI_STATE_CONNECTED;
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)1));
					}
				}
			}
			else if(net_type == Util.Network_Connected_WAP || net_type == Util.Network_Connected_NET)	//WAP. NET.
			{
				if( Util_WaitforConnConnected == false && isDCOpened() == false )
				{
					//The current connection may be lost
					if(net_type == Util.Network_Connected_WAP)
						Util.Trace( " WAP Send ENetworkStatus_ConnectionExp (1, 0)");
					else
						Util.Trace( " NET Send ENetworkStatus_ConnectionExp (2, 0)");
					Util_WaitforConnConnected = true;
					VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)net_type, (short)0));
				}
				else
				{
					if(Util_WaitforConnConnected == true && isDCOpened())
					{
						Util.Trace("Custom WAP Send ENetworkStatus_ConnectionExp (1, 1)");
						Util_WaitforConnConnected = false;
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)1, (short)1));

					}
				}
			}
			
			break;
		}
		case Util.Util_HANDLE_MSG_ID_AIRPLANE_MODE_CHANGED :
		{
			ContentResolver cr = VenusActivity.appContext.getContentResolver();
			int on = 0;
			if( Settings.System.getString(cr,Settings.System.AIRPLANE_MODE_ON).equals("0") )
			{
				on = 0;
			}
			else
			{
				on = 1;
			}
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_AIRPLANE_MODE_CHANGED, on, 0);
			break;
		}
		default :
			break;
		}

	}

	@Override
	public void Init() {
		String ua = Util.getUserAgent();

		if (!ua.startsWith("yulong_8811_android") && !ua.startsWith("lenovo_a278t_android"))
		{
			sleeptime = 2000;
		}
		else
		{
			sleeptime = 5000;
		}
		
		int i = 0;
		for(; wapFeatureList[i] != null; i++)
		{
			if(ua.startsWith(wapFeatureList[i].getID()))
			{
				if (ua.startsWith("samsung_gt-i9108_android"))
				{
					if (Util.GetSDK() == Util.SDK_ANDROID_40 || Util.GetSDK() == Util.SDK_ANDROID_41)
					{
						wapFeatureIndex = i;
						bInWapFeatureList = true;
						return;						
					}
					else
					{
						i++; // samsung i9108 for 2.3
					}
				}
				
				wapFeatureIndex = i;
				bInWapFeatureList = true;
				return ;
			}
		}
		wapFeatureIndex = 0;	//Default mode
	}

	@Override
	public void DeInit() {
		closeDC();
		RestoreApn();
	}

	@Override
	public String GetInterfaceName() {
		Util.Trace("ConnectionMultiplex::GetInterfaceName:: mInterfaceName = " + mInterfaceName);
		return mInterfaceName;
	}

	/**
	 * The current APN type. Script may set the Proxy & Port using this.
	 * 
	 * 0 	-			User-defined
	 * 1	- 			Net
	 * 2 	- 			China Mobile WAP
	 * 3 	-			China Telecom WAP
	 * 4 	-			China Unicom WAP
	 * 
	 * 0xFF - 			Invalid
	 * @return
	 */
	@Override
	public int GetCurrentAPNType() {
		// TODO Auto-generated method stub
		return 0xFF;
	}

	@Override
	public String GetCurrentAPNPort() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String GetCurrentAPNProxy() {
		// TODO Auto-generated method stub
		return "";
	}

	private void TestDC()
	{
		try {
			Class<?> cls = null;
			Field fields[] = null;
//			Util.Trace("**************android.net.ConnectivityManager 1********************");
//			cls = Class.forName("android.net.ConnectivityManager");
//			fields = cls.getDeclaredFields();
//			for(Field field : fields)
//			{
//				Util.Trace("field name: " + field.getName() + ", field value: " + field.get(null));
//			}
			
			Util.Trace("*************com.android.internal.telephony.Phone 2***********************");
			cls = Class.forName("com.android.internal.telephony.Phone");
			fields = cls.getDeclaredFields();
			for(Field field : fields)
			{
				Util.Trace("field name: " + field.getName() + ", field value: " + field.get(null));
			}
			
			Util.Trace("***************android.net.ConnectivityManager 3**********************");
			cls = Class.forName("android.net.ConnectivityManager");
			Method methods[] = cls.getDeclaredMethods();
			for(Method method : methods)
			{
				Util.Trace("method name: " + method.getName());
				Util.Trace("parameters type:");
				Class<?> paramClss[] = method.getParameterTypes();
				for(Class<?> paramCls : paramClss)
				{
					Util.Trace("    " + paramCls.toString());
				}
			}
			
			Util.Trace("**************android.net.NetworkInfo 4********************");
			cls = Class.forName("android.net.NetworkInfo");
			methods = cls.getDeclaredMethods();
			for(Method method : methods)
			{
				Util.Trace("method name: " + method.getName());
				Util.Trace("parameters type:");
				Class<?> paramClss[] = method.getParameterTypes();
				for(Class<?> paramCls : paramClss)
				{
					Util.Trace("    " + paramCls.toString());
				}
			}
			Util.Trace("");
		} catch (Exception e) {
			Util.Trace(e.toString());
			e.printStackTrace();
		}
	}

	private int openDC()
	{
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		int result = APN_STATE_UNKNOWN;
		
		Util.Trace("openDC...");
		if(wapFeatureIndex >= 0)
		{
			int funcID = wapFeatureList[wapFeatureIndex].getFuncID();
			String wapFeature = wapFeatureList[wapFeatureIndex].getWapFeature();
			switch(funcID)
			{
			case WapFeature.FUNC_Android :
			{
				defaultApnId = -1;
				destApnId = -1;
				currentAPNType = APN_TYPE_UNKNOWN;
				QueryDefaultAPNId();
				if(currentAPNType != APN_TYPE_WAP)
				{
					if(Util.GetSDK() == Util.SDK_ANDROID_40 || Util.GetSDK() == Util.SDK_ANDROID_41)
					{
						Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...3");
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_ShowNetSetting, 0);
					}
					else
					{
						EnumerateAPNs();
						if(destApnId != -1)
						{
							//The MobileVideo3 APN item is exist.
							Util.Trace("Custom use current APN item of system");
							if(SetCurrentAPN(destApnId))
								result = APN_STATE_REQUEST_STARTED;
							else
								result = APN_STATE_UNKNOWN;
						}
						else
						{
							//Create a new MobileVideo3 APN item.
							if (APN_TYPE == APN_TYPE_WAP)
								CreateCmwapAPNCfg(TAG_TYPE_WAP_APN, TAG_TYPE_WAP_NAME, TAG_TYPE_WAP_PROXY, TAG_TYPE_WAP_PORT, "1");
							EnumerateAPNs();
							if(destApnId != -1)
							{
								SetCurrentAPN(destApnId);
								result = APN_STATE_REQUEST_STARTED;
							}
							else
								result = APN_STATE_UNKNOWN;
						}
					}
				}
				else
				{
					result = APN_STATE_ALREADY_ACTIVE;
				}
				break;
			}
			case WapFeature.FUNC_MTK :
			{
				try {
					Class<?> cls = Class.forName("com.android.internal.telephony.Phone");
					Field field;
					field = cls.getDeclaredField("FEATURE_ENABLE_WAP");
					String feature = (String) field.get(null); // enableWAP
					Util.Trace("FEATURE_ENABLE_WAP -> " + feature);
					result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_HTC :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_HTCA4 :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Moto :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LG :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Samsung :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LeOS :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LeadCore :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Androidv4 :
			{
				result = APN_STATE_UNKNOWN;
				break;
			}
			default :
				result = APN_STATE_UNKNOWN;
				break;
			}
			
			bHaveUsingNetworkFeature = true;
		}

		return result;	
	}
	
	private boolean isDCOpened()
	{
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean open = false;
		int result = APN_STATE_UNKNOWN;
		Util.Trace("isDCOpened...");
		
		if (!IsDataConnectionOpened())
		{
        	VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_DataConnected, 0);
        	if (bHaveUsingNetworkFeature)
        	{
        		closeDC();
        	}
			return false;
		}
		
		if(wapFeatureIndex >= 0)
		{
			int funcID = wapFeatureList[wapFeatureIndex].getFuncID();
			String feature = wapFeatureList[wapFeatureIndex].getWapFeature();
			switch(funcID)
			{
			case WapFeature.FUNC_Android :
			{
				if(currentAPNType == APN_TYPE_UNKNOWN)
				{
					QueryDefaultAPNId();
				}
				if(currentAPNType == APN_TYPE_WAP && apnIsConnected())
				{
					open = true;
				}
				else
				{
					open = false;
				}
				break;
			}
			case WapFeature.FUNC_MTK :
			{
				try {
					Field field = Class.forName("android.net.ConnectivityManager").getDeclaredField("TYPE_MOBILE_WAP");
					int networkType = field.getInt(null);
					NetworkInfo netInfo = connMan.getNetworkInfo(networkType);
					if (netInfo != null && netInfo.isConnected())
					{
						//Bind the route to host. Why ???
						int hostAddress = Util.lookupHost(TAG_TYPE_WAP_PROXY);
						open = connMan.requestRouteToHost(networkType, hostAddress);
					}
				} catch (Exception e) {
					Util.Trace(e.toString());
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_HTC :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHTTPPROXY");
				if(result == APN_STATE_ALREADY_ACTIVE)
				{
					//TODO
					open = true;
				}
				break;
			}
			case WapFeature.FUNC_HTCA4 :
			{
				try {
					Field field = Class.forName("android.net.ConnectivityManager")
							.getDeclaredField("TYPE_MOBILE_HTTPPROXY"); // 15
					int networkType = field.getInt(null);
					NetworkInfo netInfo = connMan.getNetworkInfo(networkType);
					if (netInfo != null && netInfo.isConnected()) {
						//Bind the route to host. Why ???
						int hostAddress = Util.lookupHost(TAG_TYPE_WAP_PROXY);
						open = connMan.requestRouteToHost(networkType, hostAddress);
					}
				} catch (Exception e) {
					Util.Trace(e.toString());
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_Moto :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableWAP");
				if(result == APN_STATE_ALREADY_ACTIVE)
				{
					String config[] = { "wap", TAG_TYPE_WAP_PROXY };
					Method method1;
					try {
						Class<?> cls = Class.forName("android.net.ConnectivityManager");
						method1 = cls.getDeclaredMethod("configureNetwork",
								new Class[] { int.class, String[].class });
						method1.invoke(connMan,	ConnectivityManager.TYPE_MOBILE, config);
					} catch (Exception e) {
						e.printStackTrace();
					}
					open = true;
				}
				break;
			}
			case WapFeature.FUNC_LG :
			{
				NetworkInfo netInfo = connMan.getNetworkInfo(7);
				try {
					Method method0 = Class.forName("android.net.NetworkInfo").getMethod("getApType");
					String strApType = (String) method0.invoke(netInfo);
					if("wap".equals(strApType) && netInfo.isConnected())
					{
						Method method1 = Class.forName("android.net.NetworkInfo").getMethod("getInterfaceName");
						mInterfaceName = (String) method1.invoke(netInfo);
						if(mInterfaceName == null) mInterfaceName = "";
						
						//Bind the route to host. Why ???
						int hostAddress = Util.lookupHost(TAG_TYPE_WAP_PROXY);
						open = connMan.requestRouteToHost(7, hostAddress);

						open = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_Samsung :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature/*"enableWAP"*/);
				if(result == APN_STATE_ALREADY_ACTIVE)
				{
					try {
						Class<?> cs[] = { int.class, java.lang.String.class };
						Method method0 = connMan.getClass().getMethod("getInterfaceName", cs);
						Object[] params = { ConnectivityManager.TYPE_MOBILE, feature/*"enableWAP"*/ };
						mInterfaceName = (String) method0.invoke(connMan, params);
						if(mInterfaceName == null) mInterfaceName = "";
					} catch (Exception e) {
						Util.Trace(e.toString());
						e.printStackTrace();
					}
					open = true;
				}
				break;
			}
			case WapFeature.FUNC_LeOS :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableWAP");
				if(result == APN_STATE_ALREADY_ACTIVE)
				{
					try {
						Method method0 = connMan.getClass().getMethod("getInterfaceName", String.class);
						mInterfaceName = (String) method0.invoke(connMan, "enableWAP");
						if(mInterfaceName == null) mInterfaceName = "";
					} catch (Exception e) {
						Util.Trace(e.toString());
						e.printStackTrace();
					}
					open = true;
				}
				break;
			}
			case WapFeature.FUNC_LeadCore :
			{
				result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableCMWAP");
				if(result == APN_STATE_ALREADY_ACTIVE)
				{
					//TODO
					open = true;
				}
				break;
			}
			case WapFeature.FUNC_Androidv4 :
			{
				open = true;
				break;
			}
			default :
			{
				open = true;
				break;
			}
			}
			bHaveUsingNetworkFeature = true;
		}

		return open;
	}
	
	private void closeDC()
	{

		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(wapFeatureIndex >= 0)
		{
			int funcID = wapFeatureList[wapFeatureIndex].getFuncID();
			String wapFeature = wapFeatureList[wapFeatureIndex].getWapFeature();
			switch(funcID)
			{
			case WapFeature.FUNC_Android :
			{
				break;
			}
			case WapFeature.FUNC_MTK :
			{
				try {
					if (connMan != null) {
						Class<?> cls = Class.forName("com.android.internal.telephony.Phone");
						Field field = cls.getDeclaredField("FEATURE_ENABLE_WAP");
						String feature = (String) field.get(null); // enableWAP
						connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_HTC :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_HTCA4 :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Moto :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LG :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Samsung :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LeOS :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_LeadCore :
			{
				connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, wapFeature);
				break;
			}
			case WapFeature.FUNC_Androidv4 :
			{
				break;
			}
			default :
			{
				break;
			}
			}
		}
		bHaveUsingNetworkFeature = false;
	}
	
	/**
	 * Get the default APN configuration of system, such as APN-TYPE and the APN id.
	 * 
	 */
	private void QueryDefaultAPNId() {
		String proxy 	= "";
		String port		= "";
		String current	= "";
		String type		= "";
		
		ContentResolver resolver = VenusApplication.getInstance()
				.getContentResolver();
		Cursor c = resolver.query(PREFERRED_APN_URI, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				String[] columnNames = c.getColumnNames();
				for (String columnIndex : columnNames) {
					int i = c.getColumnIndex(columnIndex);
					if (TAG_ID.equals(columnIndex)) 
					{
						defaultApnId = Integer.parseInt(c.getString(i));
					}
					else if(TAG_PROXY.equals(columnIndex))
					{
						proxy = c.getString(i);
					}
					else if(TAG_PORT.equals(columnIndex))
					{
						port = c.getString(i);
					}
					else if(TAG_CURRENT.equals(columnIndex))
					{
						current = c.getString(i);
					}
					else if(TAG_TYPE.equals(columnIndex))
					{
						type = c.getString(i);
					}
				}

				if ( ((TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))
				|| (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))) && "1".equals(current)) {
					currentAPNType = APN_TYPE_WAP;
					Util.Trace("Custom Default APN type is WAP.");
				}
				else if( proxy == null || port == null || "".equals(proxy) || "".equals(port) )
				{
					currentAPNType = APN_TYPE_NET;
					Util.Trace("Custom Default APN type is NET");
				}

				Util.Trace("queryDefaultAPNId: currentAPNType = "+currentAPNType);
				c.close();
				return ;
			}
			c.close();
		}
		Util.Trace("QueryDefaultAPNId: Invalid Uri of Preferapn");
	}

	/**
	 * Find the CMWAP item in the list
	 * 
	 * @return
	 */
	private int EnumerateAPNs() {
		boolean findApn = false;
		Cursor c = VenusApplication.getInstance().getContentResolver().query(
				APN_TABLE_URI, null, null, null, null);
		String id = "";
		String apn = "";
		String name = "";
		String proxy = "";
		String port = "";
		String current = "";
		String type = "";
		
		if (c != null) {
			Util.Trace("APNs No. = " + c.getCount());
			if (c.moveToLast()/*c.moveToFirst()*/) {
				String[] columnNames = c.getColumnNames();
				do {
					id 		= "";
					apn		= "";
					name	= "";
					proxy	= "";
					port	= "";
					for (String columnIndex : columnNames) {
						int i = c.getColumnIndex(columnIndex);
						
						if (TAG_ID.equalsIgnoreCase(columnIndex))
						{
							id = c.getString(i);
						}
						else if (TAG_APN.equalsIgnoreCase(columnIndex))
						{
							apn = c.getString(i);
						}
						else if (TAG_NAME.equalsIgnoreCase(columnIndex))
						{
							name = c.getString(i);
						}
						else if (TAG_PROXY.equalsIgnoreCase(columnIndex))
						{
							proxy = c.getString(i);
						}
						else if (TAG_PORT.equalsIgnoreCase(columnIndex))
						{
							port = c.getString(i);
						}
						else if(TAG_CURRENT.equalsIgnoreCase(columnIndex))
						{
							current = c.getString(i);
						}
						else if(TAG_TYPE.equalsIgnoreCase(columnIndex))
						{
							type = c.getString(i);
						}
					}

					//if ( ( (TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) || (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) ) && "1".equals(current) )
					if ( ( (TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) || (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) ) && "1".equals(current) && TAG_TYPE_WAP_APN.equalsIgnoreCase(apn) && TAG_TYPE_WAP_NAME.equalsIgnoreCase(name))
					{
						Util.Trace("...Find apn type is " + "APN_TYPE_WAP" + "[" + c.getPosition() + "]");
						findApn = true;
						break;
					}
				} while (c.moveToPrevious()/*c.moveToNext()*/);
			}
			c.close();
		}
		if (findApn) {
			destApnId = Integer.parseInt(id);
		} else {
			destApnId = -1;
		}
		return destApnId;
	}
	
	private boolean SetCurrentAPN(int id) {
		boolean res = false;
		
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		ContentValues values = new ContentValues();

		if(id > 0)
		{
			values.put("apn_id", id);
			try {
				int num = resolver.update(PREFERRED_APN_URI, values,
						null, null); // The device will turn off/on the
												// connection of APN again
												// automatically.
				Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name", "apn" }, "_id=" + id, null, null);
				if(c != null) 
				{
					res = true;
					c.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			currentAPNType = APN_TYPE_WAP;
			res = true;
		}

		return res;
	}
	
	private int CreateCmwapAPNCfg(String apn, String name, String proxy, String port, String current)
	{
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		
		//One record format:
		//[_id, name, numeric, mcc, mnc, apn, user, server, password, proxy, port, mmsproxy, mmsport, mmsc, authtype, type, current, preset]
		ContentValues values = new ContentValues();

		values.put(TAG_APN, 	apn);
		values.put(TAG_NAME,	name);
		values.put(TAG_NUMERIC,		getNumeric());
		values.put(TAG_MCC,			getMCC());
		values.put(TAG_MNC,			getMNC());
		values.put(TAG_USER,		"");
		values.put(TAG_PASSWORD,	"");
		values.put(TAG_SERVER,		"");		
		values.put(TAG_PROXY,	proxy);
		values.put(TAG_PORT,	port);
		values.put(TAG_MMSPROXY,	"");
		values.put(TAG_MMSPORT,		"");
		if(Util.GetSDK() != Util.SDK_ANDROID_15 && Util.GetSDK() != Util.SDK_ANDROID_16)
		{
			values.put(TAG_AUTHTYPE,	"");	/*HTC Hero(G3) doesn't have this configuration*/
		}
		values.put(TAG_TYPE,		TAG_TYPE_Value);
		values.put(TAG_CURRENT,	current);
		
		Uri uriWithID = resolver.insert(APN_TABLE_URI, values);
		
		String id = uriWithID.getLastPathSegment();
		if(id == null)
			return -1;
		Util.Trace("CreateCmwapAPNCfg:: success id="+Integer.parseInt(id));
		return Integer.parseInt(id);
	}
	
	private void RestoreApn()
	{
		if(wapFeatureIndex >= 0)
		{
			int funcID = wapFeatureList[wapFeatureIndex].getFuncID();
			switch(funcID)
			{
			case WapFeature.FUNC_Android :
			{
				ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
				EnumerateAPNs();
				if(destApnId != -1 && destApnId != defaultApnId)
				{
					try {
					resolver.delete(APN_TABLE_URI, "_id=?", new String[] { destApnId + "" });
					SetDefaultApn(defaultApnId);
					} catch(Exception e) {
						Util.Trace("RestoreApn:: "+e.toString());
					}
				}
				break;
			}
			default :
				break;
				
			}
		}
	}
	
	private void SetDefaultApn(int id)
	{
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		ContentValues values = new ContentValues();

		if(id > 0)
		{
			values.put("apn_id", id);
			try {
				int num = resolver.update(PREFERRED_APN_URI, values,
						null, null); // The device will turn off/on the
												// connection of APN again
												// automatically.
				Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name", "apn" }, "_id=" + id, null, null);
				if(c != null) 
				{
					c.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getNumeric()
	{
		TelephonyManager telephonyManager = (TelephonyManager) VenusActivity.appContext.getSystemService(Context.TELEPHONY_SERVICE);
		String simOpr = telephonyManager.getSimOperator();

		if(simOpr != null)
			return simOpr;
		return "46002";
	}
	
	private String getMCC()
	{
		TelephonyManager telephonyManager = (TelephonyManager) VenusActivity.appContext.getSystemService(Context.TELEPHONY_SERVICE);
		String simOpr = telephonyManager.getSimOperator();
		String mcc = "460";
		if(simOpr != null && simOpr.length() > 3)
		{
			mcc = simOpr.substring(0, 3);
		}
		return mcc;
	}

	private String getMNC()
	{
		TelephonyManager telephonyManager = (TelephonyManager) VenusActivity.appContext.getSystemService(Context.TELEPHONY_SERVICE);
		String simOpr = telephonyManager.getSimOperator();
		String mnc = "02";
		if(simOpr != null && simOpr.length() >= 5)
		{
			mnc = simOpr.substring(3, simOpr.length());
		}
		return mnc;	
	}
	
	private boolean apnIsConnected()
	{
		try {
			Context context = VenusApplication.getInstance().getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) 
			{
				Util.Trace("Custom CMWAP isn't connected! #");
				return false;
			}
			else 
			{
				NetworkInfo interestInfo = null;
				if (!Util.getUserAgent().startsWith("yulong_8811_android"))
				{
					interestInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				}
				else
				{
					interestInfo = connectivity.getNetworkInfo(TYPE_MOBILE_2);
				}
				
				if(interestInfo.isAvailable() == false)
				{
					Util.Trace("Custom CMWAP is not availble!!!");
					return false;
				}
				if(interestInfo.isConnected() != true)
				{
					Util.Trace("Custom CMWAP is not connected!!!");
					return false;
				}
			}
			
			Util.Trace("Custom CMWAP is connected! @");
			return true;
		} catch(Exception e){
			Util.Trace(e.toString());
		}
		
		Util.Trace("Custom CMWAP isn't connected! &");
		return false;
	}
	
	@Override
	public void PostEvent(int event, Bundle bundle) {
		switch(event)
		{
		case EVENT_ID_CHANGE_ROUTE :
			if(Util.m_nNetwork_Connected_Type == Util.Network_Connected_WAP)
			{
				ProcessEvent_ChangeRoute(event, bundle);
			}
			break;
			
		case EVENT_ID_SYSTEM_PAUSE :
		{
			break;
		}
		case EVENT_ID_SYSTEM_RESUME :
		{
			Util.Trace("@@ in PostEvent bDCSettingShowFlag: " + bDCSettingShowFlag);
			if (bDCSettingShowFlag)
			{
				bDCSettingShowFlag = false;

				new Thread(new Runnable(){

					public void run() {
						int detectCount = 10;
						while(detectCount>0)
						{
							try {
								Thread.sleep(1000);

								IsDataConnectionOpened();			            
					            if (!bDCConnected)
					            {
					            	VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_DataConnected, 0);
					            	return;
					            }
					            else
					            {
					            	Util.Trace("@@ data connection successful!!!");
					            	OpenDataConnection();
					            	break;
					            }
							} catch (Exception e) {
								Util.Trace("@ exception: " + e);
							}
							
							detectCount--;
						}
					}
				}).start();

			}
			
			if(Util.GetSDK() == Util.SDK_ANDROID_40 || Util.GetSDK() == Util.SDK_ANDROID_41)
			{
				if(bSettingShowFlag)
				{
					bSettingShowFlag = false;

					new Thread(new Runnable(){

						public void run() {
							int detectCount = 10;
							while(detectCount>0)
							{
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
								}
								QueryDefaultAPNId();
								if(currentAPNType == APN_TYPE_WAP)
								{
									if(apnIsConnected())
									{
										Util.Trace("Open APN successfully... 2");
										VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
										Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP;
										Util_WaitforConnConnected = false;
										return;
									}
								}
								detectCount--;
							}
							Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...2");
							VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_ShowNetSetting, 0);
							
						}
					}).start();
				}
			}
			break;
		}
		default :
			break;
		}
	}
	
	private void ProcessEvent_ChangeRoute(int event, Bundle bundle)
	{
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean open = false;
		int result = -1;
		
		String route = "";
		if(bundle != null) route = bundle.getString("route");
		if(route == null || route.length() == 0)
			return;
		
		if(wapFeatureIndex >= 0)
		{
			int funcID = wapFeatureList[wapFeatureIndex].getFuncID();
			switch(funcID)
			{
			case WapFeature.FUNC_Android :
			{
				break;
			}
			case WapFeature.FUNC_MTK :
			{
				try {
					Class<?> cls = Class.forName("com.android.internal.telephony.Phone");
					Field field;
					field = cls.getDeclaredField("FEATURE_ENABLE_WAP");
					String feature = (String) field.get(null); // enableWAP
					Util.Trace("FEATURE_ENABLE_WAP -> " + feature);
					result  = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Util.Trace("PostEvent:: route=" + route);
				try {
					Field field = Class.forName("android.net.ConnectivityManager").getDeclaredField("TYPE_MOBILE_WAP");
					int networkType = field.getInt(null);
					NetworkInfo netInfo = connMan.getNetworkInfo(networkType);
					if (netInfo != null && netInfo.isConnected())
					{
						//Bind the route to host. Why ???
						int hostAddress = Util.lookupHost(route);
						open = connMan.requestRouteToHost(networkType, hostAddress);
						Util.Trace("PostEvent:: open=" + open);
					}
				} catch (Exception e) {
					Util.Trace(e.toString());
					e.printStackTrace();
				}
				break;
			}
			case WapFeature.FUNC_HTC :
			{
				break;
			}
			case WapFeature.FUNC_HTCA4 :
			{
                try {
                    Field field = Class.forName("android.net.ConnectivityManager")
                                    .getDeclaredField("TYPE_MOBILE_HTTPPROXY"); // 15
                    int networkType = field.getInt(null);
                    NetworkInfo netInfo = connMan.getNetworkInfo(networkType);
                    if (netInfo != null && netInfo.isConnected()) {
                        int hostAddress = Util.lookupHost(route);
                        open = connMan.requestRouteToHost(networkType, hostAddress);
                        Util.Trace("ProcessEvent_ChangeRoute: open: " + open);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
				break;
			}
			case WapFeature.FUNC_Moto :
			{
				break;
			}
			case WapFeature.FUNC_LG :
			{
				break;
			}
			case WapFeature.FUNC_Samsung :
			{
				break;
			}
			case WapFeature.FUNC_LeOS :
			{
				break;
			}
			case WapFeature.FUNC_LeadCore :
			{
				break;
			}
			case WapFeature.FUNC_Androidv4 :
			{
				break;
			}
			default :
			{
				break;
			}
			}
		}
	}
	
	@Override
	public void OpenNetSetting() {
		//Show the Setting view for user
		Intent intent = new Intent(android.provider.Settings.ACTION_APN_SETTINGS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		VenusActivity.appContext.startActivity(intent);
		bSettingShowFlag = true;
	}
	
	@Override
	public void OpenDataConnectionSetting()
	{
		Intent intent = null;
        if (Util.getUserAgent().startsWith("htc"))
        {
        	intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        }
        else if (Util.getUserAgent().startsWith("d68x_android"))
        {
        	intent = new Intent();                
        	intent.setAction("com.android.settings.SIM_MANAGEMENT_ACTIVITY");
        }
        else if(Util.GetSDK() == Util.SDK_ANDROID_23 || Util.GetSDK() == Util.SDK_ANDROID_40)
        {
        	try {
            	intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
            	ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
            	intent.setComponent(cName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(Util.GetSDK() == Util.SDK_ANDROID_22)
        {
        	try {
            	intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            } catch (Exception e) {        
                e.printStackTrace();
            }
        }
        else if(Util.GetSDK() == Util.SDK_ANDROID_41)
        {
        	try {
            	intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        VenusActivity.appContext.startActivity(intent);
        
        bDCSettingShowFlag = true;
	}
	
	private boolean IsDataConnectionOpened()
	{
		try {
			ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class<?> cls = Class.forName("android.net.ConnectivityManager");
	        Method method1 = cls.getDeclaredMethod("getMobileDataEnabled");
	        bDCConnected = (Boolean) method1.invoke(connMan);
		} catch (Exception e) {
			Util.Trace("!!! IsDataConnectionOpened : " + e);
		}

		return bDCConnected;
	}
}
