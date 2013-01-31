package com.wondertek.video.connection;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

public class ConnectionImpl {

	public static String TAG_TYPE_WAP_APN			= "cmwap";
	public static String TAG_TYPE_WAP_NAME			= "MobileVideo3";
	public static String TAG_TYPE_NET_APN			= "cmnet";
	public static String TAG_TYPE_NET_NAME			= "MobileVideo3";
	
	public static String TAG_TYPE_WAP_PROXY			= "10.0.0.172";
	public static String TAG_TYPE_WAP_PROXY_E		= "010.000.000.172";
	public static String TAG_TYPE_WAP_PORT			= "80";
	
	public static final int APN_TYPE_WAP		= 0;
	public static final int APN_TYPE_NET		= 1;
	public static final int APN_TYPE_WAP_2		= 2;
	public static final int APN_TYPE_WAP_3		= 3;
	public static final int APN_TYPE_UNKNOWN	= -1;
	public static int APN_TYPE					= APN_TYPE_WAP;

	public static final Uri PREFERRED_APN_URI = Uri
	.parse("content://telephony/carriers/preferapn");		//URI for the current APN configuration. /data/data/com.android.providers.telephony/databases/telephony.db
	public static final Uri APN_TABLE_URI = Uri
	.parse("content://telephony/carriers");					//URI for the list of all APNs. /data/data/com.android.providers.telephony/shared_prefs/preferred-apn.xml
	public static final Uri PREFERRED_APN_URI_7260 = Uri
	.parse("content://telephony/carriers/preferapn_2");		//URI for Coolpad 7260
	
	public static final int APN_STATE_ALREADY_ACTIVE 			= 0;
	public static final int APN_STATE_REQUEST_STARTED			= 1;
	public static final int APN_STATE_TYPE_NOT_AVAILABLE		= 2;
	public static final int APN_STATE_UNKNOWN					= -1;
	
	public static final String TAG_ID 			= "_id";
	public static final String TAG_APN			= "apn";
	public static final String TAG_NAME		= "name";
	public static final String TAG_NUMERIC		= "numeric";
	public static final String TAG_MCC			= "mcc";
	public static final String TAG_MNC			= "mnc";
	public static final String TAG_USER		= "user";
	public static final String TAG_PASSWORD	= "password";
	public static final String TAG_SERVER		= "server";
	public static final String TAG_PROXY		= "proxy";
	public static final String TAG_PORT		= "port";
	public static final String TAG_MMSPROXY	= "mmsproxy";
	public static final String TAG_MMSPORT		= "mmsport";
	public static final String TAG_AUTHTYPE	= "authtype";
	public static final String TAG_TYPE		= "type";
	public static final String TAG_CURRENT		= "current";
	
	public static final String TAG_TYPE_Value	= "default,internet,httpproxy,wap";

	public static final int EVENT_ID_CHANGE_ROUTE				= 0;
	public static final int EVENT_ID_SYSTEM_PAUSE				= 1;
	public static final int EVENT_ID_SYSTEM_RESUME				= 2;
	
	public static final int TYPE_MOBILE_2 = 7;
	public int usernetworktype = -1;
	
	class WapInfo
	{
		private int id = -1;
		private String proxy	= "";
		private String port		= "";
		private String apn		= "";
		private String name		= "";
		
		public WapInfo(int id, String proxy, String port)
		{
			this.id = id;
			this.proxy = proxy;
			this.port = port;
		}
		
		public WapInfo(int id, String proxy, String port, String apn, String name)
		{
			this.id = id;
			this.proxy = proxy;
			this.port = port;
			this.apn = apn;
			this.name = name;
		}
		
		public int getType()
		{
			return id;
		}
		
		public String getProxy()
		{
			return proxy;
		}
		
		public String getApn()
		{
			return apn;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getPort()
		{
			return port;
		}
	}
	
	class APNInfo
	{
		private int m_apiID 	= -1;
		private String m_proxy	= "";
		private String m_port	= "";
		
		public APNInfo()
		{
			
		}
		
		public void SetApnID(int apiID)
		{
			m_apiID = apiID;
		}
		
		public void SetProxy(String proxy)
		{
			m_proxy = proxy;
		}
		
		public void SetPort(String port)
		{
			m_port = port;
		}
		
		public int GetApnID()
		{
			return m_apiID;
		}
		
		public String getProxy()
		{
			return m_proxy;
		}
		
		public String getPort()
		{
			return m_port;
		}
	}

	public void Init(){};
	
	public void OpenDataConnection(int networktype){};
	
	public void OpenNetSetting(){} 
	
	public void OpenDataConnectionSetting(){}
	
	public int GetDataConnectionState(){return 1;}
	
	public String GetInterfaceName(){return "";}
	
	public void setUsernetworktype(int networktype){usernetworktype = networktype;}
	
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
	public int GetCurrentAPNType(){return 0;}
	
	public String GetCurrentAPNProxy(){return "";}

	public String GetCurrentAPNPort(){return "";}
	
	public void HandleMessage(Message msg){}
	
	public void DeInit(){}
	
	public void PostEvent(int event, Bundle bundle){}
}
