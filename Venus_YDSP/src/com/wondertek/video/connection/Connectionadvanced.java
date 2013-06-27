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
import com.wondertek.video.appmanager.AppManager;
import com.wondertek.video.connection.ConnectionImpl.WapInfo;

public class Connectionadvanced extends ConnectionImpl{
	private static int m_Device_ApnId 				= -1;//
	private static int m_Device_APNType 			= APN_TYPE_UNKNOWN;
	private static String m_Device_APN_Proxy		= "";
	private static String m_Device_APN_Port			= "";
	
	private static int m_Cur_ApnId					= -1;
	private static int m_Cur_APNType 			= APN_TYPE_UNKNOWN;
	private static int m_User_APNType			= APN_TYPE_UNKNOWN;
	private static String m_Cur_APN_Proxy		= "";
	private static String m_Cur_APN_Port			= "";
	
	private boolean	bSettingShowFlag 		= false;
	private boolean bDCSettingShowFlag	= false;
	
	private static ArrayList<WapInfo> wapInfoList = new ArrayList<WapInfo>();
	@Override
	public void Init() {
		// TODO Auto-generated method stub
		//China Mobile WAP
		wapInfoList.add(new WapInfo(1, "10.0.0.172", "80"));
		wapInfoList.add(new WapInfo(1, "010.000.000.172", "80"));
		
		//China Mobile NET
		wapInfoList.add(new WapInfo(2, "", ""));
		wapInfoList.add(new WapInfo(2, "", ""));
				
		//China Telecom WAP
		wapInfoList.add(new WapInfo(3, "10.0.0.200", "80"));
		wapInfoList.add(new WapInfo(3, "010.000.000.200", "80"));
		
		//China Unicom WAP
		wapInfoList.add(new WapInfo(4, "10.0.0.172", "80"));
		wapInfoList.add(new WapInfo(4, "010.000.000.172", "80"));

		RefalshDefaultAPNId();
		
		Util.setMultipleNetworkChip(Util.NetworkChip_Single);
	}
	
	public void OpenDataConnection(int nType) {
		// TODO Auto-generated method stub
		ConectedAPN(nType);
	}

	@Override
	public void OpenNetSetting() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(android.provider.Settings.ACTION_APN_SETTINGS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		VenusActivity.appContext.startActivity(intent);
		bSettingShowFlag = true;
	}

	@Override
	public void OpenDataConnectionSetting() {
		// TODO Auto-generated method stub
		if (Util.getUserAgent().startsWith("motorola_mb520_android"))
		{
			AppManager.getInstance(VenusActivity.getInstance()).RunApp("com.motorola.blur.datamanager.app");
		}
		else
		{
			Intent intent = null;
	        if (Util.getUserAgent().startsWith("htc") || Util.getUserAgent().startsWith("huawei_u8815_android"))
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
		}
		bDCSettingShowFlag = true;
	}

	@Override
	public String GetInterfaceName() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int GetCurrentAPNType() {
		// TODO Auto-generated method stub
		RefalshDefaultAPNId();
		int nAPNType = GetAPNType(m_Cur_APN_Proxy,m_Cur_APN_Port);
		Util.Trace("java adv GetCurrentAPNType m_Cur_APN_Proxy =" +m_Cur_APN_Proxy);
		Util.Trace("java adv GetCurrentAPNType m_Cur_APN_Port =" +m_Cur_APN_Port);
		Util.Trace("java adv GetCurrentAPNType nAPNType =" +nAPNType);
		return nAPNType;
	}

	@Override
	public String GetCurrentAPNProxy() {
		// TODO Auto-generated method stub
		if( m_Cur_APN_Proxy != null && m_Cur_APN_Proxy.length() > 0 )
		{
			return m_Cur_APN_Proxy;
		}
		return "";
	}

	@Override
	public String GetCurrentAPNPort() {
		// TODO Auto-generated method stub
		if( m_Cur_APN_Port != null && m_Cur_APN_Port.length() > 0 )
		{
			return m_Cur_APN_Port;
		}
		return "";
	}

	@Override
	public void HandleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Util.Util_HANDLE_MSG_ID_ConnectionChange :
		{
			if(m_Cur_ApnId == -1)
				break;
			Bundle bundle = msg.getData();
			int state = bundle.getInt("STATE");
			int net_type = Util.m_nNetwork_Connected_Type;
			Util.Trace("net_type = "+net_type);
			
			if(Util.getConnectivityManager() == null)
			{
				break ;
			}
			
			if (state == 0)
			{
            	VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_DataConnected, 0);
			}else if(state == 1)
			{
				APNInfo apnInfo = GetCurAPNID();
				if(m_Cur_ApnId !=apnInfo.GetApnID())
				{
					if(m_Cur_APNType != GetAPNType(apnInfo.getProxy(),apnInfo.getPort()))
					{
						Util.Trace("@@@@@MSG_NETWORK_ERROR");
						VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_ConnectedChange, 0);
					}
				}
			}
			break;
		}
		case Util.Util_HANDLE_MSG_ID_WIFIChange:
		{
			Util.Trace("==Util_HANDLE_MSG_ID_WIFIChange Util.m_nNetwork_Connected_Type=" +Util.m_nNetwork_Connected_Type);
			if(Util.m_nNetwork_Connected_Type != Util.Network_Connected_WiFi)
				break;
			if(Util.getWifiManager().isWifiEnabled() == false || Util.wifiIsConnected(Util.currentWifiSSID) == false)
			{
					Util.Trace(Util.currentWifiSSID +" Send ENetworkStatus_ConnectionExp (0, 0)");
					Util.WIFI_STATE = Util.WIFI_STATE_IDLE;
					VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)0));
			}
			else
			{
					Util.Trace(Util.currentWifiSSID +" Send ENetworkStatus_ConnectionExp (0, 1)");
					Util.WIFI_STATE = Util.WIFI_STATE_CONNECTED;
					VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_ConnectionExp, Util.buildInt32((short)0, (short)1));
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
		// TODO Auto-generated method stub
		if(m_Device_ApnId!=-1)
			SetCurrentAPN(m_Device_ApnId, false);
	}

	@Override
	public void PostEvent(int event, Bundle bundle) {
		// TODO Auto-generated method stub
		switch(event)
		{
		case EVENT_ID_SYSTEM_RESUME :
		{
			
			if (bDCSettingShowFlag)
			{
				Util.Trace("@@ in PostEvent bDCSettingShowFlag: " + bDCSettingShowFlag);
				bDCSettingShowFlag = false;
				if (!IsDataConnectionOpened())
	            {
	            	VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_DataConnected, 0);
	            	return;
	            }
	            else
	            {
	            	Util.Trace("@@ data connection successful!!!");
	            	detectAPNConnectionStart();
	            	break;
	            }
			}
			
			if(Util.GetSDK() == Util.SDK_ANDROID_40 || Util.GetSDK() == Util.SDK_ANDROID_41 || Util.getUserAgent().startsWith("yulong_7260_android"))
			{
				if(bSettingShowFlag)
				{
					bSettingShowFlag = false;
					new Thread(new Runnable(){
						public void run() {
							int detectCount = 5;
							while(detectCount>0)
							{
								if(apnIsConnected())
								{
									Util.Trace("Open APN successfully... 2");
									VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_Connected, 0);
									return;
								}
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
								}
								
								detectCount--;
							}
							Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...2");
							VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_ShowNetSetting, 0);
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
	
	public APNInfo GetCurAPNID()
	{
		APNInfo apnInfo = null;
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		Cursor c = resolver.query(PREFERRED_APN_URI, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				apnInfo = new APNInfo();
				String[] columnNames = c.getColumnNames();
				for (String columnIndex : columnNames) {
					int i = c.getColumnIndex(columnIndex);
					if (TAG_ID.equals(columnIndex)) 
					{
						apnInfo.SetApnID(Integer.parseInt(c.getString(i)));
					}
					else if(TAG_PROXY.equals(columnIndex))
					{
						apnInfo.SetProxy(c.getString(i));
					}
					else if(TAG_PORT.equals(columnIndex))
					{
						apnInfo.SetPort(c.getString(i));
					}
				}
			}
			c.close();
		}
		return apnInfo;
	}
	
	public void RefalshDefaultAPNId() {
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
					if (TAG_ID.equals(columnIndex)) 
					{
						if(m_Device_ApnId == -1)
							m_Device_ApnId = Integer.parseInt(c.getString(i));
						m_Cur_ApnId = Integer.parseInt(c.getString(i));
						
					}
					else if(TAG_PROXY.equals(columnIndex))
					{
						proxy = c.getString(i);
					}
					else if(TAG_PORT.equals(columnIndex))
					{
						port = c.getString(i);
					}
				}
				
				if ( (proxy != null && proxy.length() > 0) && (port != null && port.length() > 0) ) {
					if(m_Device_APN_Proxy.equals(""))
						m_Device_APN_Proxy	= proxy;
					m_Cur_APN_Proxy = proxy;
					if(m_Device_APN_Port.equals(""))
						m_Device_APN_Port	= port;
					m_Cur_APN_Port = port;
					
					m_Cur_APNType = GetAPNType(proxy,port);
					if(m_Device_APNType == APN_TYPE_UNKNOWN)
						m_Device_APNType = m_Cur_APNType;
					
					Util.Trace(" APN type is WAP. [" + proxy + ":" + port + "]");
				}
				else if( proxy == null || port == null || "".equals(proxy) || "".equals(port) )
				{
					m_Cur_APNType = GetAPNType(proxy,port);
					if(m_Device_APNType == APN_TYPE_UNKNOWN)
						m_Device_APNType = m_Cur_APNType;
					m_Cur_APN_Proxy = "";
					m_Cur_APN_Port = "";
					
					Util.Trace("APN type is NET");
				}

				Util.Trace("queryDefaultAPNId: m_Cur_APNType = "+m_Cur_APNType);
				c.close();
				return ;
			}
			c.close();
		}
		Util.Trace("queryDefaultAPNId: Invalid Uri of Preferapn");
	}
	
	public void ConectedAPN(int nType)
	{
		m_User_APNType = nType;
		Util.Trace("@@@ConectedAPN nType =" + nType);
		Util.Trace("@@@ConectedAPN m_Cur_APNType =" + m_Cur_APNType);
		
		if(m_Cur_APNType == nType)
		{
			VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_Connected, 0);
			SetNetworkConnectedType(nType);
		}
		else
		{
			int nAPNID = FindAPNID(nType);
			if(nAPNID > 0)
			{
				SetCurrentAPN(nAPNID, true);
			}
			else
			{
				nAPNID = CreateAPN(nType);
				SetCurrentAPN(nAPNID, true);
			}
		}
	}
	
	public void SetNetworkConnectedType(int nType)
	{
		switch(nType)
		{
		case APN_TYPE_WAP:
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP;
			break;
		case APN_TYPE_NET:
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_NET;
			break;
		case APN_TYPE_WAP_2:
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP_2;
			break;
		case APN_TYPE_WAP_3:
			Util.m_nNetwork_Connected_Type = Util.Network_Connected_WAP_3;
			break;
		}
	}
	
	public int FindAPNID(int nType)
	{
		int nAPNID = -1;
		
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
			if (c.moveToLast()) {
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
					
					if(nType == APN_TYPE_WAP)
					{
						if (((TAG_TYPE_WAP_PROXY.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port)) 
								|| (TAG_TYPE_WAP_PROXY_E.equals(proxy) && TAG_TYPE_WAP_PORT.equals(port))) 
								&& "1".equals(current) && TAG_TYPE_WAP_APN.equalsIgnoreCase(apn) 
								&& (TAG_TYPE_WAP_NAME.equalsIgnoreCase(name) || "cmwap".equalsIgnoreCase(name) ) )
						{
							if(isApnCfg_TypeCorrect(type))
							{
								Util.Trace("...Find apn type is " + nType + ",[" + c.getPosition() + "]");
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
					else if(nType == APN_TYPE_NET)
					{
						Util.Trace("nType == APN_TYPE_NE");
						if ( ("".equals(proxy) && "".equals(port)) )
						{
							Util.Trace("...Find apn type is " + nType + ",[" + c.getPosition() + "]");
							findApn = true;
							break;
						}
					}else
					{
						WapInfo wapInfo = GetWapInfo(nType);
						
						if(wapInfo == null)
						{
							findApn = false;
							break;
						}
						
						if (wapInfo.getProxy().equals(proxy) && wapInfo.getPort().equals(port))
						{
							if(isApnCfg_TypeCorrect(type))
							{
								Util.Trace("...Find apn type is " + nType + ",[" + c.getPosition() + "]");
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
				} while (c.moveToPrevious());
			}
			c.close();
		}
		if (findApn) {
			nAPNID = Integer.parseInt(id);
		} else {
			nAPNID = -1;
		}
		
		return nAPNID;
	}
	
	private WapInfo GetWapInfo(int nType)
	{
		WapInfo wapInfo = null;
		int n = wapInfoList.size();
		
		for(int i = 0; i<n; i++)
		{
			if(nType == ((WapInfo)wapInfoList.get(i)).getType())
			{
				wapInfo = (WapInfo)wapInfoList.get(i);
				break;
			}
		}
		return wapInfo;
	}
	
	private int GetAPNType(String proxy, String port)
	{
		int nTypeID = APN_TYPE_UNKNOWN;
		if(proxy == null || port == null || "".equals(proxy) || "".equals(port))
			return APN_TYPE_NET;
		WapInfo wapInfo = null;
		int n = wapInfoList.size();
		Util.Trace("GetAPNType n =" +n);
		for(int i = 0; i<n; i++)
		{
			wapInfo = (WapInfo)wapInfoList.get(i);
			if(proxy.equals(wapInfo.getProxy()) && port.equals(wapInfo.getPort()))
			{
				nTypeID = wapInfo.getType();
				break;
			}
		}
		return nTypeID;
	}
	
	private boolean isApnCfg_TypeCorrect(String apnDefaultType)
	{
		boolean ret = true;
		
		String ua = Util.getUserAgent();

		if(ua.equalsIgnoreCase("htc_s610d_android") && !TAG_TYPE_Value.equals(apnDefaultType))
		{
			ret = false;
		}
		return ret;
	}
	
	public boolean SetCurrentAPN(int id, boolean detectNetwork) {
		if(id<0)
			return false;
		
		boolean res = false;	
		Util.initPhoneManufaturer();
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		ContentValues values = new ContentValues();

		if(Util.GetSDK() != Util.SDK_ANDROID_40 && Util.GetSDK() != Util.SDK_ANDROID_41 && !Util.getUserAgent().startsWith("yulong_7260_android"))
		{
			values.put("apn_id", id);
			try {
				int num = resolver.update(PREFERRED_APN_URI, values,
						null, null); 
				Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name", "apn" }, "_id=" + id, null, null);
				if (c != null) {
					res = true;
					c.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if(detectNetwork)
				detectAPNConnectionStart();
		}
		else
		{
			
			Util.Trace("Send message ENetworkError_Trans_ShowNetSetting out...1");
			VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_ShowNetSetting, 0);
			
			if(detectNetwork)
				detectAPNConnectionStart();
		}

		return res;
	}

	public int CreateAPN(int nType)
	{
		int nAPNID = -1;
		WapInfo wapInfo = GetWapInfo(nType);
		if(wapInfo != null)
		{
			if(Util.GetSDK() != Util.SDK_ANDROID_40 && Util.GetSDK() != Util.SDK_ANDROID_41 && !Util.getUserAgent().startsWith("yulong_7260_android"))
				nAPNID = createCmwapAPNCfg(wapInfo.getApn(), wapInfo.getName(), wapInfo.getProxy(), wapInfo.getPort(), "1");
			else
				VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_ShowNetSetting, 0);
		}else
		{
			Util.Trace("CreateAPN nType=" + nType + ",Error!!!!");
		}
		return nAPNID;
	}

	private int createCmwapAPNCfg(String apn, String name, String proxy,
			String port, String current) {
		// TODO Auto-generated method stub
		ContentResolver resolver = VenusApplication.getInstance().getContentResolver();
		
		//One record format:
		//[_id, name, numeric, mcc, mnc, apn, user, server, password, proxy, port, mmsproxy, mmsport, mmsc, authtype, type, current, preset]
		ContentValues values = new ContentValues();

		values.put(TAG_APN, 	apn);
		values.put(TAG_NAME,	name);
		values.put(TAG_NUMERIC,	getNumeric());
		values.put(TAG_MCC,		getMCC());
		values.put(TAG_MNC,		getMNC());
		values.put(TAG_USER,	"");
		values.put(TAG_PASSWORD,"");
		values.put(TAG_SERVER,	"");		
		values.put(TAG_PROXY,	proxy);
		values.put(TAG_PORT,	port);
		values.put(TAG_MMSPROXY,"");
		values.put(TAG_MMSPORT,	"");
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
	
	private void detectAPNConnectionStart()
	{
		Runnable connectTask = new Runnable(){
			public void run() {
				int connectCnt = 15; 
				while (true) {
					try{
						Thread.sleep(1000);
						connectCnt--;
						if(connectCnt<0)
						{
							Util.Trace("Open APN failure...");
							VenusActivity.getInstance().nativesendevent(Util.WDM_NETWORK, Util.ENetworkError_Trans_InvalidAPN, 0);
							break;
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					
					if (apnIsConnected()) {
						Util.Trace("Open APN successfully... 1");
						VenusActivity.getInstance().nativesendevent(Util.WDM_DIALUP, Util.ENetworkStatus_Connected, 0);
						SetNetworkConnectedType(m_User_APNType);
						return ;
					}
				}
			}
		};
		Thread threadMonit = new Thread(null, connectTask, "APN");
		threadMonit.start();
	}
	
	private boolean apnIsConnected()
	{
		try{
			Context context = VenusApplication.getInstance().getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) 
			{
				NetworkInfo interestInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(interestInfo.isAvailable() && interestInfo.isConnected())
					return true;
			}
		} catch(Exception e){
			Util.Trace(e.toString());
		}

		Util.Trace("G3WLANHttp:: " + "APN isn't connected! &");
		return false;
	}

	private boolean IsDataConnectionOpened()
	{
		boolean bDCConnected = true;
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
