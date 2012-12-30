package com.wondertek.video.wifi;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.connection.SystemConnectionManager;
import com.wondertek.video.g3wlan.client.G3WLANHttp;
//import com.android.internal.telephony.ITelephony;

public class WifiObserver {
	private static WifiObserver instance = null;
	private VenusActivity venusHandle = null;
	
	private final static int NUM_OpenNetworksKept = 50;
	
	private List<ScanResult> mScanResults = null;
	public String mStrScanResult = "";
	
	private Runnable	wifiTask = null;
	private String		destSSID = "";
	private int 		wifiCnt  = 30;		//30s. TIME Limitation for connecting of wifi.
	private boolean		bWLanPortalFlag = false;
	
	private WifiObserver(VenusActivity va)
	{
		venusHandle = va;
	}
	
	public static WifiObserver getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new WifiObserver(va);
		}
		
		return instance;
	}
	
	public void SetUrl(String pingUrl, String portalUrl)
	{
		Util.setUrl(pingUrl, portalUrl);
	}
	
	public String GetPingUrl()
	{
		return Util.getPingUrl();
	}
	
	public String GetPortalUrl()
	{
		return Util.getPortalUrl();
	}
	
	/**
	 * This method will be deprecated
	 * 
	 * @return
	 */
	public boolean  OpenCMWAP()
	{
		return false;
	}
	
	public void OpenDataConnection()
	{
		/*
		Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
		Util.m_nNetwork_Connected_Type = Util.Network_Connected_Unknown;
		if(GetWifiEnabled())
		{
			if( Util.getMultipleNetworkChip() == Util.NetworkChip_Single || Util.getMultipleNetworkChip() == Util.NetworkChip_Unknow )
				Util.getWifiManager().setWifiEnabled(false);
		}
		if(VenusActivity.PHONE_PLATFORM == 0)
		{
			if(Util.m_APN_Switch_Type == Util.APN_SWITCH_TYPE_DYNAMIC)
			{
				//Util.queryDefaultAPNId();
				//Util.EnumerateAPNs();
				Util.SetCurrentAPN(Util.destApnId, true);
			}
			else
			{
				Util.UseCurrentAPN();
			}
		
		}
		else
		{
			Util.openOMSDataConnection(Util.APN_TYPE_WAP);
		}
		*/
		SystemConnectionManager.getInstance().OpenDataConnection();
	}
	
	public void OpenNetSetting()
	{
		SystemConnectionManager.getInstance().OpenNetSetting();
	}
	
	public void OpenDataConnectionSetting()
	{
		SystemConnectionManager.getInstance().OpenDataConnectionSetting();
	}
	
	public int GetDataConnectionState()
	{
		return SystemConnectionManager.getInstance().GetDataConnectionState();
	}
	public int GetCurrentAPNType()
	{
		return SystemConnectionManager.getInstance().GetCurrentAPNType();
	}
	
	public int GetAirplaneMode()
	{
		return Util.GetAirplaneMode();
	}

	public boolean GetNetworkStatus() {
		if (Util.getWifiManager().isWifiEnabled()) {
			WifiInfo mWifiInfo = Util.getWifiManager().getConnectionInfo();
			if (null != mWifiInfo && mWifiInfo.getIpAddress() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean GetWifiEnabled() {
		if (Util.getWifiManager().isWifiEnabled()) {
			return true;
		} else {
			return false;
		}

	}
	
	public boolean SetWifiEnable(boolean enable)
	{
		Util.Trace("SetWifiEnable:: enable="+enable);
		return Util.getWifiManager().setWifiEnabled(enable);
	}
	

	public String GetScanResults() {
		mStrScanResult = "";

		mScanResults = Util.getWifiManager().getScanResults();
		if (null == mScanResults) {
			return "";
		}

		for (int i = 0; i < mScanResults.size(); i++) {
			ScanResult s = mScanResults.get(i);

			int level = WifiManager.calculateSignalLevel(s.level, 5);
						//Max level is 5, as same as Java team.
			Util.Trace("javaGetScanResults: i="+i+"  ["+s.BSSID+","+s.SSID+","+s.capabilities+","+s.frequency+","+level+"]");
			String security = Wifi.getScanResultSecurity(s);
			security = security.equals(Wifi.OPEN)?"":security;
			boolean hasPwd = Wifi.isWifiAlreadyHasPwd(VenusActivity.appActivity.getApplicationContext(), Util.getWifiManager(), s);
			mStrScanResult += s.BSSID + "," 
							+ s.SSID + "," 
							+ security + "," 
							+ (hasPwd==true?1:0) + ","
							+ s.frequency + "," 
							+ level + ",";
		}
		
		return mScanResults.size()+";"+mStrScanResult;
	}
	
	//Assume that the DEST WiFi AP is been encrypt.
	public boolean WLanNeedTypePassword(String ssid)
	{
		WifiConfiguration wfCfg = null;
		String destCfgSSID = Wifi.convertToQuotedString(ssid);
		
		List<WifiConfiguration> wfCfgList = Util.getWifiManager()
				.getConfiguredNetworks();

		for (int i = 0; i < wfCfgList.size(); i++) {
			wfCfg = wfCfgList.get(i);
			Util.Trace("Configured AP:  BSSID= "+wfCfg.BSSID+" SSID="+wfCfg.SSID+"KeyManagement="+wfCfg.allowedKeyManagement.toString()+" netID="+wfCfg.networkId+" preSharedkey="+wfCfg.preSharedKey+ " status="+wfCfg.status);
			if (wfCfg.SSID.equals(destCfgSSID)) {
				if(wfCfg.preSharedKey != null && wfCfg.preSharedKey.length() != 0)
				{
					Util.Trace("Need to type password!!!");
					return true;
				}
				break;
			}
		}
		
		Util.Trace("Doesn't need to type password!!!");
		return false;
	}
	
	private ScanResult	m_scanResult = null;
	private String		m_password = null;

	public boolean ConnectAP(String ssid, String pwd, int wlanType) {
		
		Util.m_nNetwork_Connected_Type = Util.Network_Connected_Unknown;
		
		if(wlanType == Util.EWLanType_Normal || wlanType == Util.EWLanType_Encryption)
		{
			Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
			
			destSSID = ssid;
			
			Util.Trace("Now to connect Ap: "+ssid);
			if (null == ssid || ssid.length() <= 0) {
				Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
				if(Util.isWifiAutoConnected())
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
				else
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_WLanAutoConnClosed, 0);
				return false;
			}
			
			//Judge the current connection is the DEST connection or not.
			if (Util.wifiIsConnected( destSSID) ) {
				venusHandle.nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
				Util.detectNetworkChip();
				Util.m_nNetwork_Connected_Type = Util.Network_Connected_WiFi;
				Util.setCurrentWifiSSID(destSSID);
				Util.WIFI_STATE	= Util.WIFI_STATE_CONNECTED;
				return true;
			}

			//Ensure that the DEST connection is in the result list.
			List<ScanResult> srList = Util.getWifiManager().getScanResults();
			if (null == srList) {
				Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
				if(Util.isWifiAutoConnected())
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
				else
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_WLanAutoConnClosed, 0);
				return false;
			}
			ScanResult sr = null;
			int resIndex = -1;
			for (int i = 0; i < srList.size(); i++) {
				sr = srList.get(i);
				Util.Trace("Result List: ssid= "+sr.SSID);
				if(ssid.equals(sr.SSID)){
					resIndex = i;
				}
			}
			if(resIndex == -1)
			{
				Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
				if(Util.isWifiAutoConnected())
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
				else
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_WLanAutoConnClosed, 0);
				return false;
			}
			sr = srList.get(resIndex);
			Util.Trace("Found in result: BSSID="+sr.BSSID+" ssid="+sr.SSID+" capabilities="+sr.capabilities);

			
			m_scanResult	= sr;
			m_password		= pwd;
			
			wifiCnt = 30;
			wifiTask = new Runnable(){
				public void run() {
					int wifiStableCnt = 0;
					Util.WIFI_STATE	= Util.WIFI_STATE_CONNECTING;
					boolean operationSuccess = Wifi.connectToNetwork(VenusActivity.appActivity.getApplicationContext(), Util.getWifiManager(), m_scanResult, m_password, NUM_OpenNetworksKept);
					if(operationSuccess)
					{
						while (true) {
							if( Util.wifiIsConnected(destSSID) )
							{
								if(++wifiStableCnt >= 3)
								{
									Util.Trace("Connect ["+destSSID+"] SUCCESS");
									venusHandle.nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
									Util.detectNetworkChip();
									Util.m_nNetwork_Connected_Type = Util.Network_Connected_WiFi;
									Util.setCurrentWifiSSID(destSSID);
									Util.WIFI_STATE	= Util.WIFI_STATE_CONNECTED;
									return ;
								}
								wifiCnt++;
								//Util.Trace("__________wifiStableCnt = " + wifiStableCnt);

							}
							else
							{
								wifiStableCnt = 0;
								//Util.Trace("__________wifiStableCnt = " + wifiStableCnt);
							}
							Util.Trace("----Connecting "+destSSID+"...");
							try {
								Thread.sleep(1000);
								wifiCnt--;
								if(wifiCnt<0)
								{
									Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
									if(Util.isWifiAutoConnected())
										venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
									else
										venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_WLanAutoConnClosed, 0);
									return ;
								}
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
					}
					else
					{
						Util.Trace("Connect ["+destSSID+"] FAILURE");
						Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
						if(Util.isWifiAutoConnected())
						{
							if(Wifi.getPwdWrong())
								venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Password, 0);
							else
								venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
						}
						else
						{
							if(Wifi.getPwdWrong())
								venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Password, 0);
							else
								venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_WLanAutoConnClosed, 0);
						}
					}
				}
			};
			Thread threadMonit = new Thread(null, wifiTask, "Wifi");
			threadMonit.start();
		}
		else if(wlanType == Util.EWLanType_Portal)
		{
			destSSID = ssid;
			bWLanPortalFlag = true;
			Uri 	uri 	= Uri.parse(Util.getPortalUrl());
			Intent	intent	= new Intent(Intent.ACTION_VIEW, uri);
			VenusActivity.appActivity.startActivity(intent);
			Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
		}
		
		return true;
	}
	
	public String GetHttpRespond(String url, String postData, int postDataLength, int needrespond) {
		Util.Trace("====javaGetHttpRespond, url="+url);
		Util.Trace("====javaGetHttpRespond, postData="+postData);
		Util.Trace("====javaGetHttpRespond, postDataLength="+postDataLength);
		Util.Trace("====javaGetHttpRespond, needrespond="+needrespond);
		
		
		boolean bHttps = false;
		if (url.startsWith("https")) {
			Util.Trace("javaGetHttpRespond: HTTPS type");
			bHttps = true;
		}
		else
		{
			Util.Trace("javaGetHttpRespond: HTTP type");
		}
		
		if (postDataLength > 0) {
			Util.Trace("javaGetHttpRespond: POST");
			G3WLANHttp.getInstance().sendDataPost(bHttps, url, postData);
		}
		else {
			Util.Trace("javaGetHttpRespond: GET");
			G3WLANHttp.getInstance().sendDataGet(bHttps, url);
		}
		
		if (needrespond > 0) {
			Util.Trace("javaGetHttpRespond: RETURN with Response");
			String s = G3WLANHttp.getInstance().getResponse();
			Util.Trace("\n" + s + "\n");
			return s;
		}
		
		Util.Trace("javaGetHttpRespond: RETURN without Response");
		return "";
	}

	public String GetHttpRespondTimeOut(String url, String postData, int postDataLength, int needrespond, int connectTO, int readTO) {
		Util.Trace("====GetHttpRespondTimeOut, url="+url);
		Util.Trace("====GetHttpRespondTimeOut, postData="+postData);
		Util.Trace("====GetHttpRespondTimeOut, postDataLength="+postDataLength);
		Util.Trace("====GetHttpRespondTimeOut, needrespond="+needrespond);


		boolean bHttps = false;
		if (url.startsWith("https")) {
			Util.Trace("GetHttpRespondTimeOut: HTTPS type");
			bHttps = true;
		}
		else
		{
			Util.Trace("GetHttpRespondTimeOut: HTTP type");
		}
		
		if (postDataLength > 0) {
			Util.Trace("GetHttpRespondTimeOut: POST");
			G3WLANHttp.getInstance().sendDataPost(bHttps, url, postData, connectTO, readTO);
		}
		else {
			Util.Trace("GetHttpRespondTimeOut: GET");
			G3WLANHttp.getInstance().sendDataGet(bHttps, url, connectTO, readTO);
		}
		
		if (needrespond > 0) {
			Util.Trace("GetHttpRespondTimeOut: RETURN with Response");
			String s = G3WLANHttp.getInstance().getResponse();
			Util.Trace("\n" + s + "\n");
			return s;
		}

		Util.Trace("GetHttpRespondTimeOut: RETURN without Response");
		return "";
	}

	public String GetWifiInfo() {
		WifiInfo mWifiInfo = Util.getWifiManager().getConnectionInfo();
		if( null == mWifiInfo || null == mWifiInfo.getSSID())
		{
			Util.Trace("javaGetWifiInfo: WifiInfo is null or SSID is null");
			return "";
		}
		else
		{
			if(mWifiInfo.getSSID().matches(""))
			{
				Util.Trace("WifiInfo");
				Util.Trace("[");
				Util.Trace(""+mWifiInfo.getBSSID());
				Util.Trace(""+mWifiInfo.getSSID());
				Util.Trace(""+mWifiInfo.getIpAddress());
				Util.Trace(""+mWifiInfo.getMacAddress());
				Util.Trace("]");
				return "";				
			}
		}

		List<ScanResult> srList = Util.getWifiManager().getScanResults();
		if (null == srList) {
			Util.Trace("javaGetWifiInfo: No list of scan-result");
			return "";
		}

		ScanResult sr = null;
		for (int i = 0; i < srList.size(); i++) {
			sr = srList.get(i);
			if( sr.SSID.equals(mWifiInfo.getSSID()) ) {
				break;
			}
		}
		if (null == sr) {
			Util.Trace("javaGetWifiInfo: The dest WiFi configuration isn't in scan-result");
			return "";
		}

		//int ip = mWifiInfo.getIpAddress();
		String security = Wifi.getScanResultSecurity(sr);
    	String strConnectionInfo = 	sr.SSID + "," +
    								security + "," +
					    			sr.SSID  + "," +
					    			WifiManager.calculateSignalLevel(sr.level, 5) + ",";

		Util.Trace(strConnectionInfo);

		return strConnectionInfo;

	}
	
	/**
	 * Judge that whether the WLAN AP is portal accessibility or not.
	 * @param ssid
	 * @param 
	 * @param 
	 * @return	true - If the WLAN AP works as the CMCC.
	 * 			false - Don't need to pop the web browser for user.
	 * @Description
	 * 		Assume that we have connected to the DEST WLAN AP.
	 * 
	 */
	public boolean WLanIsPortal(String ssid)
	{
		Util.getInstance().getAsyncTask().execute("WLanIsPortal", ssid);
		return true;
	}
	
	/**
	 * If the AP has portal accessibility (such as CMCC), we need to know that whether we have login or not.
	 * @param ssid
	 * @param 
	 * @param 
	 * @return	true - We have login the AP already.
	 * 			false - We haven't login the AP.
	 * @Description
	 * 		Assume that we have connected to the DEST WLAN AP.
	 * 
	 */
	public boolean WLanHaveLogin(String ssid)
	{
		Util.getInstance().getAsyncTask().execute("WLanHaveLogin");
		return true;
	}
	
	/**
	 * Execute the 'NetworkStop' in asynchronous way in Java side.
	 * @param 
	 * @param 
	 * @param 
	 * @return	
	 * 		
	 * @Description
	 * 		Assume that we have connected to the DEST WLAN AP.
	 * 
	 */
	public void Network_AsynStop()
	{
		Util.getInstance().getAsyncTask().execute("NetworkStop");
	}

	public void dealWithWLan(char eventType)
	{
		if(eventType == VenusActivity.EVENT_PAUSE)
		{
			
		}
		else if(eventType == VenusActivity.EVENT_RESUME)
		{
			if(bWLanPortalFlag)
			{
				if(G3WLANHttp.getInstance().wlanHaveLogin())
				{
					Util.Trace("ENetworkStatus_Connected");
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_Connected, 0);
					Util.detectNetworkChip();
					Util.m_nNetwork_Connected_Type = Util.Network_Connected_WiFi;
					Util.setCurrentWifiSSID(destSSID);
					Util.WIFI_STATE	= Util.WIFI_STATE_CONNECTED;
				}
				else
				{
					Util.Trace("ENetworkError_Trans_Login");
					Util.setCurrentWifiSSID(null);
					Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
					venusHandle.nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Login/*ENetworkError_Trans_Fail*/, 0);
				}
				bWLanPortalFlag = false;
			}
		}
	}
}
