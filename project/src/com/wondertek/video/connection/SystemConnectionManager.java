package com.wondertek.video.connection;

import android.os.Bundle;
import android.os.Message;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class SystemConnectionManager {
	
	private static SystemConnectionManager instance = null;

	private ConnectionImpl connectionImpl = null;

	private SystemConnectionManager()
	{
		if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_GENERAL)
		{
			connectionImpl = new ConnectionGeneral();
		}
		else if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_OMS)
		{
			connectionImpl = new ConnectionOMS();
		}
		else if(VenusActivity.PHONE_PLATFORM == Util.PHONE_PLATFORM_CUSTOM)
		{
			connectionImpl = new ConnectionMultiplex();
		}
	}

	public static SystemConnectionManager getInstance()
	{
		if(instance == null)
		{
			instance = new SystemConnectionManager();
		}
		return instance;
	}

	public void Init()
	{
		connectionImpl.Init();
	}

	public void OpenDataConnection()
	{
		Util.WIFI_STATE	= Util.WIFI_STATE_IDLE;
		Util.m_nNetwork_Connected_Type = Util.Network_Connected_Unknown;
		if(Util.getWifiManager().isWifiEnabled())
		{
			if( Util.getMultipleNetworkChip() == Util.NetworkChip_Single || Util.getMultipleNetworkChip() == Util.NetworkChip_Unknow )
				Util.getWifiManager().setWifiEnabled(false);
		}
		connectionImpl.OpenDataConnection();
	}
	
	public void OpenNetSetting()
	{
		connectionImpl.OpenNetSetting();
	}
	
	public void OpenDataConnectionSetting()
	{
		connectionImpl.OpenDataConnectionSetting();
	}
	
	public int GetDataConnectionState()
	{
		return connectionImpl.GetDataConnectionState();
	}

	public void HandleMessage(Message msg)
	{
		connectionImpl.HandleMessage(msg);
	}

	public int GetCurrentAPNType()
	{
		return connectionImpl.GetCurrentAPNType();
	}

	public String GetCurrentAPNProxy()
	{
		return connectionImpl.GetCurrentAPNProxy();
	}

	public String GetCurrentAPNPort()
	{
		return connectionImpl.GetCurrentAPNPort();
	}

	public String GetInterfaceName()
	{
		return connectionImpl.GetInterfaceName();
	}

	public void PostEvent(int event, Bundle bundle)
	{
		connectionImpl.PostEvent(event, bundle);
	}
	
	public void DeInit()
	{
		connectionImpl.DeInit();
	}
}
