package com.wondertek.video.connection;

import java.lang.reflect.Method;
import java.util.ArrayList;

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

public class ConnectionGeneral extends ConnectionImpl{

	public static final int APN_SWITCH_TYPE_STATIC		= 0;		//Just use the current APN configuration.
	public static final int APN_SWITCH_TYPE_DYNAMIC		= 1;		//App will switch the APN to 'WAP' automatically.
	public static int m_APN_Switch_Type					= APN_SWITCH_TYPE_STATIC;
	
	private static int preApnId = -1;
	private static int destApnId = -1;
	private static int defaultAPNType 			= -1;
	
	private static int 	connectCnt = 30;
	private static Runnable	connectTask = null;
	private static Thread threadMonit = null;
	
	private static String m_Device_APN_Proxy		= "";
	private static String m_Device_APN_Port			= "";
	
	private static boolean Util_WaitforConnConnected = false;
	
	private boolean		bSettingShowFlag = false;
	
	private static boolean bDCSettingShowFlag	= false;
	private static boolean bDCConnected		 = false;
	private boolean m_bNetworkOpening = false;
	
	private static ArrayList<WapInfo> wapInfoList = new ArrayList<WapInfo>();
	
	class WapInfo
	{
		private int id = -1;
		private String proxy	= "";
		private String port		= "";
		
		public WapInfo(int id, String proxy, String port)
		{
			this.id = id;
			this.proxy = proxy;
			this.port = port;
		}
		
		public int getType()
		{
			return id;
		}
		
		public String getProxy()
		{
			return proxy;
		}
		
		public String getPort()
		{
			return port;
		}
	}
	
	@Override
	public void OpenDataConnection() {
		if(m_APN_Switch_Type == APN_SWITCH_TYPE_DYNAMIC)
		{
			//Util.queryDefaultAPNId();
			//Util.EnumerateAPNs();
			ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			try {
				IsDataConnectionOpened();
	            if (!bDCConnected)
	            {
	            	VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_DataConnected, 0);
	            	return;
	            }
			} catch (Exception e) {
				Util.Trace("@ exception: " + e);
			}
			
			SetCurrentAPN(destApnId, true);
		}
		else
		{
			UseCurrentAPN();
		}
		
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
				if(Util.getConnectivityManager() == null)
				{
					break ;
				}
				
				IsDataConnectionOpened();
				if (!bDCConnected)
				{
	            	VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_DataConnected, 0);
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
					VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)net_type, (short)0));
				}
				else
				{
					if(m_APN_Switch_Type == APN_SWITCH_TYPE_DYNAMIC)
					{
						if(isAPNSelected(APN_TYPE))
						{
							Util.Trace("WAP Send ENetworkStatus_ConnectionExp (1, 1)");
							Util_WaitforConnConnected = false;
							VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)1, (short)1));

						}
					}
					else
					{
						if(net_type == Util.Network_Connected_WAP)
							Util.Trace("WAP Send ENetworkStatus_ConnectionExp (1, 1)");
						else if(net_type == Util.Network_Connected_NET)
							Util.Trace("NET Send ENetworkStatus_ConnectionExp (2, 1)");
							
						Util_WaitforConnConnected = false;
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)net_type, (short)1));
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

	//[_id, name, numeric, mcc, mnc, apn, user, server, password, proxy, port, mmsproxy, mmsport, mmsc, authtype, type, current, preset]
	public void queryDefaultAPNId() {
		String proxy 	= "";
		String port		= "";
		String current	= "";
		String type		= "";
		defaultAPNType = APN_TYPE_UNKNOWN;
		Util.Trace("queryDefaultAPNId:: m_APN_Switch_Type="+m_APN_Switch_Type);
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
						preApnId = Integer.parseInt(c.getString(i));
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
				if(m_APN_Switch_Type == APN_SWITCH_TYPE_DYNAMIC)
				{
					if ( ((TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))
					|| (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))) && "1".equals(current)) {
						if(isApnCfg_TypeCorrect(type))
						{
							m_Device_APN_Proxy	= proxy;
							m_Device_APN_Port	= port;
							defaultAPNType = APN_TYPE_WAP;
							Util.Trace("Default APN type is WAP. [" + m_Device_APN_Proxy + ":" + m_Device_APN_Port + "]");
						}
						else
						{
							defaultAPNType = APN_TYPE_UNKNOWN;
							Util.Trace("Default APN's type config is not [" + TAG_TYPE_Value + "]");
						}
					}
					else if( proxy == null || port == null || "".equals(proxy) || "".equals(port) )
					{
						defaultAPNType = APN_TYPE_NET;
						Util.Trace("Default APN type is NET");
					}
				}
				else
				{
					if ( (proxy != null && proxy.length() > 0) && (port != null && port.length() > 0) ) {
						m_Device_APN_Proxy	= proxy;
						m_Device_APN_Port	= port;
						defaultAPNType = APN_TYPE_WAP;
						Util.Trace("Default APN type is WAP. [" + m_Device_APN_Proxy + ":" + m_Device_APN_Port + "]");
					}
					else if( proxy == null || port == null || "".equals(proxy) || "".equals(port) )
					{
						defaultAPNType = APN_TYPE_NET;
						Util.Trace("Default APN type is NET");
					}
				}
				Util.Trace("queryDefaultAPNId: defaultAPNType = "+defaultAPNType);
				c.close();
				return ;
			}
			c.close();
		}
		Util.Trace("queryDefaultAPNId: Invalid Uri of Preferapn");
	}

	public int EnumerateAPNs() {
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
					
					if(m_APN_Switch_Type == APN_SWITCH_TYPE_DYNAMIC)
					{
						if(APN_TYPE == APN_TYPE_WAP)
						{
							if ( ( (TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) 
									|| (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) ) 
							&& "1".equals(current) /*&& TAG_TYPE_WAP_APN.equalsIgnoreCase(apn) 
							&& (TAG_TYPE_WAP_NAME.equalsIgnoreCase(name) || "cmwap".equalsIgnoreCase(name) )*/ )
							{
								if(isApnCfg_TypeCorrect(type))
								{
									Util.Trace("...Find apn type is " + "APN_TYPE_WAP" + "[" + c.getPosition() + "]");
									findApn = true;
								}
								else
								{
									Util.Trace("...Sorry, the type cfg of WAP APN is not [" + TAG_TYPE_Value + "]");
									findApn = false;
								}
								break;
							}
						

						}
						else if(APN_TYPE == APN_TYPE_NET)
						{
							if ( ("".equals(proxy) && "".equals(port))&& "1".equals(current) )
							{
								Util.Trace("...Find apn type is " + "APN_TYPE_WAP" + "[" + c.getPosition() + "]");
								findApn = true;
								break;
							}
						}
					}
					else
					{
						if(APN_TYPE == APN_TYPE_WAP)
						{
							//if ( ( (TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) || (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) ) /*&& "1".equals(current) && TAG_TYPE_WAP_APN.equalsIgnoreCase(apn) && TAG_TYPE_WAP_NAME.equalsIgnoreCase(name)*/)
							if ( (proxy != null && proxy.length() > 0) && (port != null && port.length() > 0) )
							{
								findApn = true;
								break;
							}
						}
						else if(APN_TYPE == APN_TYPE_NET)
						{
							if ( ("".equals(proxy) && "".equals(port))&& "1".equals(current) )
							{
								findApn = true;
								break;
							}
						}
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

	private void detectAPNConnectionStart()
	{
		connectCnt = 15;
		connectTask = null;
		if(threadMonit != null)
		{
			try {
				threadMonit.stop();
				threadMonit.join(500);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		connectTask = new Runnable(){
			public void run() {
				while (true) {
					try{
						Thread.sleep(2000);
						connectCnt--;
						if(connectCnt<0)
						{
							Util.Trace("Open APN failure...");
							m_Device_APN_Proxy	= "";
							m_Device_APN_Port	= "";
							m_bNetworkOpening = false;
							VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_InvalidAPN, 0);
							return ;
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					
					if (apnIsConnected()) {
						Util.Trace("Open APN successfully... 1");
						m_Device_APN_Proxy	= TAG_TYPE_WAP_PROXY;
						m_Device_APN_Port	= TAG_TYPE_WAP_PORT;
						m_bNetworkOpening = false;
						VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
						Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP;
						Util_WaitforConnConnected = false;
						return ;
					}
				}
			}
		};
		threadMonit = new Thread(null, connectTask, "APN");
		threadMonit.start();
	}

	private boolean isAPNSelected(int apnType)
	{
		boolean ret =false;
		String proxy 	= "";
		String port		= "";
		ContentResolver resolver = VenusApplication.getInstance()
				.getContentResolver();
		Cursor c = resolver.query(PREFERRED_APN_URI, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				String[] columnNames = c.getColumnNames();
				for (String columnIndex : columnNames) {
					int i = c.getColumnIndex(columnIndex);

					if(TAG_PROXY.equals(columnIndex))
					{
						proxy = c.getString(i);
					}
					else if(TAG_PORT.equals(columnIndex))
					{
						port = c.getString(i);
					}
				}
				
				if(apnType == APN_TYPE_WAP)
				{
					if ((TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))
							|| (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))) {
						ret = true;
					}
				}
				else if(apnType == APN_TYPE_NET)
				{
					if("".equals(proxy) && "".equals(port))
						ret = true;
				}
			}
		}
		if(c != null) c.close();
		return ret;
	}

	private void UseCurrentAPN() {
		if(defaultAPNType == APN_TYPE_WAP)
		{
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP;
		}
		else if(defaultAPNType == APN_TYPE_NET)
		{
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_NET;
		}
		else
		{
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_InvalidAPN, 0);
		}
	}
	
	private static boolean isApnCfg_TypeCorrect(String apnDefaultType)
	{
		boolean ret = true; //"default,internet,httpproxy,wap"
		
		String ua = Util.getUserAgent();

		if(ua.equalsIgnoreCase("htc_s610d_android") && !TAG_TYPE_Value.equals(apnDefaultType))
		{
			ret = false;
		}
		return ret;
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
	
	public boolean SetCurrentAPN(int id, boolean detectNetwork) {
		
		if(m_bNetworkOpening == true)
			return true;
		boolean res = false;
		
		Util.initPhoneManufaturer();

		m_bNetworkOpening = true;

		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		ContentValues values = new ContentValues();
		
		if(Util.GetSDK() != Util.SDK_ANDROID_40 && Util.GetSDK() != Util.SDK_ANDROID_41 && !Util.getUserAgent().startsWith("yulong_7260_android"))
		{
			if (defaultAPNType != APN_TYPE) {
				if(id > 0)
				{
					values.put("apn_id", id);
					try {
						int num = resolver.update(PREFERRED_APN_URI, values,
								null, null); // The device will turn off/on the
														// connection of APN again
														// automatically.
						Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name", "apn" }, "_id=" + id, null, null);
						if (c != null) {
							res = true;
							c.close();
						}
						//SetDataConnectionEnable(false);
						//SetDataConnectionEnable(true);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else
				{
					int nAPNID = -1;
					if (APN_TYPE == APN_TYPE_WAP)
						nAPNID = createCmwapAPNCfg(TAG_TYPE_WAP_APN, TAG_TYPE_WAP_NAME, TAG_TYPE_WAP_PROXY, TAG_TYPE_WAP_PORT, "1");
					else if (APN_TYPE == APN_TYPE_NET)
						nAPNID = createCmwapAPNCfg(TAG_TYPE_NET_APN, TAG_TYPE_NET_NAME, "", "", "1");
					EnumerateAPNs();
					return res = SetCurrentAPN(nAPNID, detectNetwork);
				
				}
			}
		
			queryDefaultAPNId();
			if(detectNetwork)
				detectAPNConnectionStart();
		}
		else
		{
			if(defaultAPNType != APN_TYPE)
			{
				Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...1");
				VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_ShowNetSetting, 0);
			}
			else
			{
				if(detectNetwork)
					detectAPNConnectionStart();
			}
		}

		return res;
	}
	
	private int createCmwapAPNCfg(String apn, String name, String proxy, String port, String current)
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
		if ( uriWithID == null )
			return -1;		
		
		String id = uriWithID.getLastPathSegment();
		if(id == null)
			return -1;
		
		return Integer.parseInt(id);
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

	private String getSPN()	//Service Provider Name (SPN).
	{
		TelephonyManager telephonyManager = (TelephonyManager) VenusActivity.appContext.getSystemService(Context.TELEPHONY_SERVICE);
		String spn = telephonyManager.getSimOperatorName ();
		if(spn != null)
		{
			return spn;
		}
		return "China Mobile"; 
	}

	@Override
	public void DeInit() {
		//TODO
	}

	@Override
	public void Init() {
		//China Mobile WAP
		wapInfoList.add(new WapInfo(2, "10.0.0.172", "80"));
		wapInfoList.add(new WapInfo(2, "010.000.000.172", "80"));
		
		//China Telecom WAP
		wapInfoList.add(new WapInfo(3, "10.0.0.200", "80"));
		wapInfoList.add(new WapInfo(3, "010.000.000.200", "80"));
		
		//China Unicom WAP
		wapInfoList.add(new WapInfo(4, "10.0.0.172", "80"));
		wapInfoList.add(new WapInfo(4, "010.000.000.172", "80"));

		//Detect the type of SIM card
		String imsi = (String)VenusActivity.getInstance().javaGetMachineInfo(VenusActivity.EMachineInfo_IMSI);
		if(imsi != null && imsi.length() > 0)
		{
			
			if(imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007"))
			{
				//China Mobile
				TAG_TYPE_WAP_APN = "cmwap";
				TAG_TYPE_WAP_NAME = "cmwap";
				TAG_TYPE_NET_APN = "cmnet";
				TAG_TYPE_NET_NAME = "cmnet";
				
				TAG_TYPE_WAP_PROXY			= "10.0.0.172";
				TAG_TYPE_WAP_PROXY_E		= "010.000.000.172";
				TAG_TYPE_WAP_PORT			= "80";
				
				
			}
			else if(imsi.startsWith("46001"))
			{
				//China Unicom
				TAG_TYPE_WAP_APN = "wap";
				TAG_TYPE_WAP_NAME = "wap";
				TAG_TYPE_NET_APN = "net";
				TAG_TYPE_NET_NAME = "net";
				
				TAG_TYPE_WAP_PROXY			= "10.0.0.172";
				TAG_TYPE_WAP_PROXY_E		= "010.000.000.172";
				TAG_TYPE_WAP_PORT			= "80";
			}
			else if(imsi.startsWith("46003"))
			{
				//China Telecom
				TAG_TYPE_WAP_APN = "ctwap";
				TAG_TYPE_WAP_NAME = "ctwap";
				TAG_TYPE_NET_APN = "ctnet";
				TAG_TYPE_NET_NAME = "ctnet";
				
				TAG_TYPE_WAP_PROXY			= "10.0.0.200";
				TAG_TYPE_WAP_PROXY_E		= "010.000.000.200";
				TAG_TYPE_WAP_PORT			= "80";
			}
		}
		queryDefaultAPNId();
		if(Util.GetSDK() != Util.SDK_ANDROID_40 && Util.GetSDK() != Util.SDK_ANDROID_41)
		{
			EnumerateAPNs();
		}

		Util.setMultipleNetworkChip(Util.NetworkChip_Single);
	}

	@Override
	public String GetInterfaceName() {
		return "";
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
		int i = 0;
		int n = wapInfoList.size();
		int id = 0xFF;
		
		queryDefaultAPNId();
		if(defaultAPNType == APN_TYPE_WAP)
		{
			for(; i<n; i++)
			{
				String proxy = ((WapInfo)wapInfoList.get(i)).getProxy();
				String port = ((WapInfo)wapInfoList.get(i)).getPort();
				
				if( m_Device_APN_Proxy.equalsIgnoreCase( proxy ) && m_Device_APN_Port.equalsIgnoreCase( port ))
				{
					id = ((WapInfo)wapInfoList.get(i)).getType();
					break;
				}
			}
		}
		else if(defaultAPNType == APN_TYPE_NET)
		{
			id = 1;
		}
		
		
		if( id == 0xFF && (m_Device_APN_Proxy != null && m_Device_APN_Proxy.length() > 0) && (m_Device_APN_Port != null && m_Device_APN_Port.length() > 0) )
		{
			id = 0;
		}
		
		Util.Trace("GetCurrentAPNType:: id = " + id);
		return id;
	}

	@Override
	public String GetCurrentAPNPort() {
		if( m_Device_APN_Proxy != null && m_Device_APN_Proxy.length() > 0 )
		{
			return m_Device_APN_Proxy ;
		}
		return "";
	}

	@Override
	public String GetCurrentAPNProxy() {
		if( m_Device_APN_Port != null && m_Device_APN_Port.length() > 0 )
		{
			return m_Device_APN_Port;
		}
		return "";
	}

	@Override
	public void PostEvent(int event, Bundle bundle) {
		switch(event)
		{
		case EVENT_ID_SYSTEM_PAUSE :
		{
			break;
		}
		case EVENT_ID_SYSTEM_RESUME :
		{
			
			if (bDCSettingShowFlag)
			{
				Util.Trace("@@ in PostEvent bDCSettingShowFlag: " + bDCSettingShowFlag);
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
			
			if(Util.GetSDK() == Util.SDK_ANDROID_40 || Util.GetSDK() == Util.SDK_ANDROID_41 || Util.getUserAgent().startsWith("yulong_7260_android"))
			{
				if(bSettingShowFlag)
				{
					bSettingShowFlag = false;
					if(m_APN_Switch_Type == APN_SWITCH_TYPE_DYNAMIC)
					{
						new Thread(new Runnable(){

							public void run() {
								int detectCount = 10;
								while(detectCount>0)
								{
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
									}
									queryDefaultAPNId();
									if(defaultAPNType == APN_TYPE_WAP)
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
					else
					{
						//TODO
					}
				}
			}
			break;
		}
		case EVENT_ID_CHANGE_ROUTE :
			break;
		default :
			break;
		}
		
	}

	/**
	 * To show the UI of APN list
	 * @param 
	 * @param 
	 * @param
	 * @return
	 */
	@Override
	public void OpenNetSetting() {
		//Show the Setting view for user
		Intent intent = new Intent(android.provider.Settings.ACTION_APN_SETTINGS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		VenusActivity.appContext.startActivity(intent);
		bSettingShowFlag = true;
	}
	
	/**
	 * To show the UI of Data Connection Switch
	 * @param 
	 * @param 
	 * @param
	 * @return
	 */
	@Override
	public void OpenDataConnectionSetting()
	{
		Intent intent = null;
        if (Util.getUserAgent().startsWith("htc"))
        {
        	intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        }
        else if(Util.GetSDK() == Util.SDK_ANDROID_23 || Util.GetSDK() == Util.SDK_ANDROID_40
        		|| Util.getUserAgent().startsWith("samsung_gt-s5570_android"))
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
	
	public int GetDataConnectionState()
	{
		int switchOpen = 0;
		ConnectivityManager connMan = (ConnectivityManager)VenusActivity.appActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
				Class<?> cls = Class.forName("android.net.ConnectivityManager");
	            Method method1 = cls.getDeclaredMethod("getMobileDataEnabled");
	            boolean bDCSwitchOpen = (Boolean) method1.invoke(connMan);
	            Util.Trace("IN OPENDATA ** bDCSwitchOpen : " + bDCSwitchOpen);
	            
	            if (!bDCSwitchOpen)
	            {
	            	switchOpen = 0;
	            }
	            else
	            {
	            	switchOpen = 1;
	            }
	            return switchOpen;
		} catch (Exception e) {
			Util.Trace("@ exception: " + e.getMessage());
		}
		return switchOpen;
	}
}
