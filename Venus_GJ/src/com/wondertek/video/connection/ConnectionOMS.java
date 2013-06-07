package com.wondertek.video.connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class ConnectionOMS extends ConnectionImpl{


	private static boolean Util_WaitforConnConnected = false;

	private static Runnable	connectTask = null;
	private static Thread threadMonit = null;

	private static final String	OMS_APTYPE_CMWAP_NAME		= "wap";
	private static final String	OMS_APTYPE_CMNET_NAME		= "internet";
	
	private static String		mOMSInterfaceName			= "";

	@Override
	public void OpenDataConnection(int networktype) {
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

		connectTask = new Runnable(){
			public void run() {
				ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
				
				Util.Trace("Ophone open WAP...");
				mOMSInterfaceName = "";
				int currentTimes = 30;
				int result = -1;
				while (currentTimes-- > 0) {
					if(apnIsConnected())
					{
						VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_Connected, 0);
						return;
					}
					Util.Trace("connected currentTimes=" + currentTimes);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				Util.Trace("...Failure...2");
				Util.m_nNetwork_Connected_Type = Util.Network_Connected_Unknown;
				VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_InvalidAPN, 0);
			}
		};
		
		threadMonit = new Thread(null, connectTask, "OMS APN");
		threadMonit.start();
	}
	
	private boolean apnIsConnected()
	{
		try {
			Context context = VenusApplication.getInstance().getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) 
			{
				Util.Trace("G3WLANHttp:: "+"APN isn't connected! #");
				return false;
			}
			else 
			{
				NetworkInfo interestInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(interestInfo.isAvailable() == false)
				{
					return false;
				}
				if(interestInfo.isConnected() != true)
				{
					return false;
				}
			
			}
			
			Util.Trace("G3WLANHttp::" + "APN is connected! ");
			return true;
		} catch(Exception e){
			Util.Trace(e.toString());
		}
		
		Util.Trace("G3WLANHttp:: " + "APN isn't connected! &");
		return false;
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
						VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)0));
					}
				}
				else
				{
					if( Util_WaitforConnConnected == true )
					{
						Util.Trace(Util.currentWifiSSID +" Send ENetworkStatus_ConnectionExp (0, 1)");
						Util_WaitforConnConnected = false;
						Util.WIFI_STATE = Util.WIFI_STATE_CONNECTED;
						VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)1));

					}
				}
			}
			else if(net_type == Util.Network_Connected_WAP || net_type == Util.Network_Connected_NET)	//WAP. NET.
			{
				if(Util.getConnectivityManager() == null)
				{
					break;
				}
				NetworkInfo ni = Util.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if( Util_WaitforConnConnected == false && (ni == null || ni.isConnected() == false) )
				{
					//The current connection may be lost
					if(net_type == Util.Network_Connected_WAP)
						Util.Trace( " WAP Send ENetworkStatus_ConnectionExp (1, 0)");
					else
						Util.Trace( " NET Send ENetworkStatus_ConnectionExp (2, 0)");
					Util_WaitforConnConnected = true;
					VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)net_type, (short)0));
				}
				else
				{
					if(Util_WaitforConnConnected == true && isOMSAPNConnected(APN_TYPE) )
					{
						Util.Trace("OMS WAP Send ENetworkStatus_ConnectionExp (1, 1)");
						Util_WaitforConnConnected = false;
						VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)1, (short)1));

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
			VenusActivity.getInstance().nativesendevent(Util.WDM_AIRPLANE, on, 0);
			break;
		}
		default :
			break;
		}
	}

	@Override
	public void DeInit() {
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		connMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, OMS_APTYPE_CMWAP_NAME);
	}

	@Override
	public void Init() {
		Util.Trace("OMS sdk version = " + Util.GetSDK());
		Util.setMultipleNetworkChip(Util.NetworkChip_Multiple);
	}

	@Override
	public String GetInterfaceName() {
		return mOMSInterfaceName;
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
		return 0xFF;
	}

	@Override
	public String GetCurrentAPNPort() {
		return "";
	}

	@Override
	public String GetCurrentAPNProxy() {
		return "";
	}
	
	private boolean openOMSWifi()
	{
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		Util.Trace("Ophone open WiFi...");
		int currentTimes = 30;
		int result = -1;

		while (currentTimes-- > 0) {

			result = connMan.startUsingNetworkFeature(ConnectivityManager.TYPE_WIFI, OMS_APTYPE_CMNET_NAME);

			if (result == 0) {
				NetworkInfo arrInfo[] = connMan.getAllNetworkInfo();
				int len = arrInfo.length;
				for (int i = 0; i < len; i++) {
					NetworkInfo ni = arrInfo[i];
					if ("WIFI".equals(ni.getTypeName()) && ni.getType() == ConnectivityManager.TYPE_WIFI)
					{
						boolean bConn = ni.isConnected();
						if(bConn)
						{
							try {
								Method method1 = Class.forName("android.net.NetworkInfo").getMethod("getInterfaceName");
								mOMSInterfaceName = (String) method1.invoke(ni);
								if(mOMSInterfaceName == null) mOMSInterfaceName = "";
								
//								String interfacename = (String) method1.invoke(ni);
//								Method method2 = Class.forName("java.net.Socket").getMethod("setInterface", String.class);
//								method2.invoke(null, GlobalSetting.LOCAL_PARAM_SOCKET_NAME);
//								method2.invoke(null, "ccinet1");
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							Util.m_nNetwork_Connected_Type = Util.Network_Connected_WiFi;
							Util.Trace("...Success...");
							return true;
						}
						Util.Trace("...Failure...1");
						return false;
					}
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		Util.Trace("...Failure...2");
		return false;
	}

	private boolean isOMSAPNConnected( int apnType) {
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo arrInfo[] = connMan.getAllNetworkInfo();
		
		if(Util.GetSDK() != Util.SDK_OMS_26)
		{
			int len = arrInfo.length;
			for (int i = 0; i < len; i++) {
				NetworkInfo ni = arrInfo[i];
				String sExtraInfo	= ni.getExtraInfo(); 
				Util.Trace("isOMSAPNConnected sExtraInfo =" +sExtraInfo);
				if ( sExtraInfo == null || ( "cmwap".equals(sExtraInfo) == false && "cmnet".equals(sExtraInfo) == false ) )
				{
					continue;
				}

				if(apnType == APN_TYPE_WAP)
				{
					if (checkProxy(ni) == false)
						continue;
				}

				boolean bConn = ni.isConnected();
				if (bConn) {
					try {
						Method method1 = Class.forName("android.net.NetworkInfo").getMethod("getInterfaceName");
						/**
						 * 	 ApType			InterfaceName	ExtraInfo			NetworkType		TypeName		SubType	SubtypeName	
						 * 		
						 *   wap			"pdh1"			"cmwap"				6				"MOBILE_OMS"	8		"HSDPA"
						 *   internet		"pdh0"			"cmnet"				0				"MOBILE"		8		"HSDPA"
						 *   internet		null			null				1				"WIFI"/"MOBILE"	0		""
						 *   mms			null			null				2				"MOBILE_MMS"	0		"UNKNOWN"	
						 *   supl			null			null				3				"MOBILE_SUPL"	0		"UNKNOWN"
						 *   dun			null			null				4				"MOBILE_DUN"	0		"UNKNOWN"
						 *   hipri			null			null				5				"MOBILE_HIPRI"	0		"UNKNOWN"
						 *   
						 *   
						 */
						mOMSInterfaceName = (String) method1.invoke(ni);
					if(mOMSInterfaceName == null) mOMSInterfaceName = "";
						Util.Trace("OMS interface name = " + mOMSInterfaceName);
//						String interfacename = (String) method1.invoke(ni);
//						Method method2 = Class.forName("java.net.Socket").getMethod("setInterface", String.class);
//						method2.invoke(null, GlobalSetting.LOCAL_PARAM_SOCKET_NAME);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return bConn;
			
			}
		}
		else
		{
			//TODO
		}

		return false;
	}

	private boolean checkProxy(NetworkInfo ni) {
		String apn1 = ni.getExtraInfo();
		String apnType = null;

		Method method1;
		try {
			method1 = Class.forName("android.net.NetworkInfo").getMethod("getApType");
			apnType = (String) method1.invoke(ni);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (apn1 == null || apnType == null) {
			return false;
		}
		final String where = "(type = '" + apnType + "' ) AND ( apn = '" + apn1	+ "' )";
		final String[] PROJECTION = { "type", "apn", "proxy", "port", "mcc", "mnc" };
		Cursor cursor = VenusActivity.appContext.getContentResolver().query(APN_TABLE_URI, PROJECTION, where, null, null);
		if (cursor != null) {
			try {
				while (cursor.moveToNext()) {
					String type = cursor.getString(0);
					String apn = cursor.getString(1);
					String proxy = cursor.getString(2);
					String port = cursor.getString(3);
					String mcc = cursor.getString(4);
					String mnc = cursor.getString(5);
					
					if ( (TAG_TYPE_WAP_PROXY.equals(proxy) || TAG_TYPE_WAP_PROXY_E.equals(proxy)) && TAG_TYPE_WAP_PORT.equals(port)) {
						Util.Trace( "-----find-----");
						Util.Trace("info:" + apn + "--" + type + "--"
								+ proxy + "----" + port + "--" + mcc + "--"
								+ mnc);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return false;
	}

	@Override
	public void PostEvent(int event, Bundle bundle) {
		
	}
	
	@Override
	public void OpenNetSetting() {
		
	}
	
	@Override
	public void OpenDataConnectionSetting()
	{
		
	}
}
