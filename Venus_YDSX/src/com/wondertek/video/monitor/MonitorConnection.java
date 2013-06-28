package com.wondertek.video.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

/*
 * When we switch the WIFI AP, the following flow will be caused
 * 
EXTRA_EXTRA_INFO: null
EXTRA_IS_FAILOVER: 0
EXTRA_REASON: null

EXTRA_EXTRA_INFO: cmwap
EXTRA_IS_FAILOVER: 1
EXTRA_REASON: apnSwitched

EXTRA_EXTRA_INFO: null
EXTRA_IS_FAILOVER: 0
EXTRA_REASON: null

EXTRA_EXTRA_INFO: cmwap
EXTRA_IS_FAILOVER: 0
EXTRA_REASON: dataDisabled

EXTRA_EXTRA_INFO: null
EXTRA_IS_FAILOVER: 1
EXTRA_REASON: null
*/
/*
public class MonitorWiFi extends MonitorBase{
	
	private static String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public MonitorWiFi()
	{
		super();
	}
	
	public MonitorWiFi(Object h)
	{
		super();
		mHandle = h;
	}
	
	@Override  
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();  
		Util.Trace("ACTION::"+action);
		
		if( ACTION.equals(action) && (Util.m_nNetwork_Connected_Type == Util.Network_Connected_WiFi) )
		{
			Bundle b=intent.getExtras();
			
			if(b != null)
			{
				String extraInfo = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
				Util.Trace("EXTRA_EXTRA_INFO: "+extraInfo);
				
				boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
				Util.Trace("EXTRA_IS_FAILOVER: "+ (isFailover==true?1:0) );
				
				String extraReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
				Util.Trace("EXTRA_REASON: "+extraReason);
				
				NetworkInfo ni = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
				if(ni != null)
				{
					if(ni.getType() == ConnectivityManager.TYPE_WIFI)
					{
						//TODO
					}
					else if(ni.getType() == ConnectivityManager.TYPE_MOBILE)
					{
						//TODO
					}
				}
				
				if(isFailover == false && extraInfo == null && extraReason == null)	//WiFi disconnected
				{
					//The current connection may be lost
					Util.Trace("MonitorWiFi: Send ENetworkError_Trans_Fail");
					//((VenusActivity)mHandle).nativesendevent(Util.MsgFromJava_WLan_DialUp, Util.ENetworkStatus_UserDisConnected, 0);
					((VenusActivity)mHandle).nativesendevent(Util.MsgFromJava_WLan_Network, Util.ENetworkError_Trans_Fail, 0);
				}
			}
		}
	}
	
	@Override
	public boolean Init(VenusActivity h) {
		this.mHandle = h;
		return true;
	}
	
	
	@Override
	public IntentFilter getIntentFilter()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		
		return intentFilter;
	}
	

	
} 
*/
public class MonitorConnection extends MonitorBase{
	
	public MonitorConnection()
	{
		super();
	}
	
	public MonitorConnection(Object h)
	{
		super();
		mHandle = h;
	}
	
	@Override  
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();  
		Util.Trace("ACTION::"+action);	
		
		if( (Util.m_nNetwork_Connected_Type == Util.Network_Connected_WiFi) 
				&& 
				( 
						( (action.equalsIgnoreCase(WifiManager.WIFI_STATE_CHANGED_ACTION) || action.equalsIgnoreCase(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||	action.equalsIgnoreCase(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION) ||	action.equalsIgnoreCase(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) ) 
								&& (Util.WIFI_STATE	== Util.WIFI_STATE_CONNECTED)
						)
						|| (Util.Util_WaitforConnConnected == true)
				)
		)
		{
			Message msg = Util.getInstance().getUtilHandler().obtainMessage(Util.Util_HANDLE_MSG_ID_ConnectionChange);
			Bundle bundle = new Bundle();
			bundle.putInt("NET_TYPE", Util.Network_Connected_WiFi);
			msg.setData(bundle);
			Util.getInstance().getUtilHandler().removeMessages(Util.Util_HANDLE_MSG_ID_ConnectionChange);
			Util.getInstance().getUtilHandler().sendMessageDelayed(msg, 1000);
		}
		else if( action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION) )
		{
			Util.Trace("MonitorConnection:: Util.m_nNetwork_Connected_Type = " + Util.m_nNetwork_Connected_Type);
			if(Util.m_nNetwork_Connected_Type == Util.Network_Connected_WAP || Util.m_nNetwork_Connected_Type == Util.Network_Connected_NET)
			{
				NetworkInfo mNetworkInfo = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				NetworkInfo.State state = mNetworkInfo.getState();
				if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.DISCONNECTED)
				{
					Message msg = Util.getInstance().getUtilHandler().obtainMessage(Util.Util_HANDLE_MSG_ID_ConnectionChange);
					Bundle bundle = new Bundle();
					bundle.putInt("NET_TYPE", Util.m_nNetwork_Connected_Type);
					msg.setData(bundle);
					Util.getInstance().getUtilHandler().removeMessages(Util.Util_HANDLE_MSG_ID_ConnectionChange);
					Util.getInstance().getUtilHandler().sendMessageDelayed(msg, 1000);
				}
			}
			else if( Util.m_nNetwork_Connected_Type == Util.Network_Connected_WiFi )
			{
				//TODO
			}
		}
		else if( action.equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED) )
		{
			Message msg = Util.getInstance().getUtilHandler().obtainMessage(Util.Util_HANDLE_MSG_ID_AIRPLANE_MODE_CHANGED);
			Util.getInstance().getUtilHandler().removeMessages(Util.Util_HANDLE_MSG_ID_AIRPLANE_MODE_CHANGED);
			Util.getInstance().getUtilHandler().sendMessageDelayed(msg, 500);
		}
	}
	
	@Override
	public boolean Init(VenusActivity h) {
		this.mHandle = h;
		return true;
	}
	
	@Override
	public boolean DeInit(VenusActivity h)
	{
		return true;
	}
	
	@Override
	public IntentFilter getIntentFilter()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);	//"android.net.conn.CONNECTIVITY_CHANGE"
		intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		
		return intentFilter;
	}
	

	
} 
