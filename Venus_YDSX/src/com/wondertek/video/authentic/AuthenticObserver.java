package com.wondertek.video.authentic;

import com.wondertek.video.authentic.IAutoService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
public class AuthenticObserver {		
		
	private static AuthenticObserver instance = null;
	private VenusActivity venusHandle = null;
	
	private IAutoService autoService = null;
	private ServiceConnection serviceConnection = null;
	private ServiceMon srvMonitor = null;
	
	// SSL service name
	public static final String KOAL_SERVICE="koal.ssl.vpn.service";
	
	private AuthenticObserver(VenusActivity va)
	{
		venusHandle = va;
	}
	
	public static AuthenticObserver getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new AuthenticObserver(va);
		}
		
		return instance;
	}
	
	
	/*
	init
	*/
	void javaInit()
	{
		Util.Trace("Authentic java init");
		// broadcast receiver 
		srvMonitor = new ServiceMon();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_INTENT_STARTSERVER_INPROC);
		filter.addAction(ACTION_INTENT_STARTSERVER_SUCCESS);
		filter.addAction(ACTION_INTENT_STARTSERVER_FAILURE);
		filter.addAction(ACTION_INTENT_DOWNLOADCFG_SUCCESS);
		filter.addAction(ACTION_INTENT_STOPSERVER_SUCCESS);
		filter.addAction(ACTION_INTENT_UPGRADE);
		filter.addAction(ACTION_INTENT_NETWORK_CONNECTED);
		filter.addAction(ACTION_INTENT_NETWORK_DISCONNECTED);
		filter.addAction(ACTION_INTENT_CHECKAPPS_SUCCESS);
		filter.addAction(ACTION_INTENT_CHECKAPPS_FAILURE);
		venusHandle.getInstance().appActivity.registerReceiver(srvMonitor, filter);
		
		// binding ssl service
		Intent intent = new Intent(KOAL_SERVICE);
		venusHandle.getInstance().appActivity.startService(intent);
				
		serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				// TODO Auto-generated method stub
				autoService = IAutoService.Stub.asInterface(service);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				autoService = null;
			}
		};
		venusHandle.getInstance().appActivity.bindService(intent, serviceConnection, venusHandle.getInstance().appActivity.BIND_AUTO_CREATE);
		Util.Trace("Authentic java javaInit Out");
	}
	
	/*
	start service
	*/
	void javaStart()
	{
		Util.Trace("Authentic java start service");
		try {
			if (!autoService.isStarted()) {
				Util.Trace("Authentic java autoService.start()");
				autoService.start();
			}
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	stop service
	*/
	void javaStop()
	{
		Util.Trace("Authentic java stop service");
		try 
		{
			autoService.stop();
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	get Apps
	*/
	String javaGetApps()
	{
		Util.Trace("Authentic java get Apps");
		try 
		{
			return autoService.getApps();
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/*
	get CertInfo
	*/
	String javaGetCertInfo(int opt)
	{
		Util.Trace("Authentic java  getCertInfo: " + opt);
		try 
		{
			return autoService.getCertInfo(opt);
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/*
	get IsStarted
	*/
	boolean javaIsStarted()
	{
		Util.Trace("Authentic java  isStarted: ");
		try 
		{
			return autoService.isStarted();
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	set ServerAddr
	*/
	void javaSetServerAddr(String addr, String port)
	{
		Util.Trace("Authentic java SetServerAddr addr : " + addr + " port : " + port);
		try 
		{
			autoService.setServerAddr(addr, port);
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	Upgrade
	*/
	void javaUpgrade()
	{
		Util.Trace("Authentic java Upgrade");
		try 
		{
			autoService.upgrade();
		} 
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// SSL broadcast msg
	public static final String ACTION_INTENT_DATA="data";
	// SSL broadcast msg: Starting service
	public static final String ACTION_INTENT_STARTSERVER_INPROC="koal.ssl.broadcast.startserver.inproc";
	// SSL broadcast msg: Start service success
	public static final String ACTION_INTENT_STARTSERVER_SUCCESS="koal.ssl.broadcast.startserver.success";
	// SSL broadcast msg: Start service fail
	public static final String ACTION_INTENT_STARTSERVER_FAILURE="koal.ssl.broadcast.startserver.failure";
	// SSL broadcast msg: Downloadcfg success
	public static final String ACTION_INTENT_DOWNLOADCFG_SUCCESS="koal.ssl.broadcast.downloadcfg.success";
	// SSL broadcast msg: stopserver.success
	public static final String ACTION_INTENT_STOPSERVER_SUCCESS="koal.ssl.broadcast.stopserver.success";
	// SSL broadcast msg: upgrade
	public static final String ACTION_INTENT_UPGRADE="koal.ssl.broadcast.upgrade";
	// SSL broadcast msg: (wifi/apn)network.connected
	public static final String ACTION_INTENT_NETWORK_CONNECTED="koal.ssl.broadcast.network.connected";
	// SSL broadcast msg: (wifi/apn)network disconnected
	public static final String ACTION_INTENT_NETWORK_DISCONNECTED="koal.ssl.broadcast.network.disconnected";
	// SSL broadcast msg: checkapps success
	public static final String ACTION_INTENT_CHECKAPPS_SUCCESS= "koal.ssl.broadcast.checkapps.success";
	// SSL broadcast msg: checkapps failure
	public static final String ACTION_INTENT_CHECKAPPS_FAILURE= "koal.ssl.broadcast.checkapps.failure";
	
	private static final String MSG_KEY="data";
	private static final int MSG_SHOWLOG = 1;
	private static final int MSG_UPGRADE = 2;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String data = msg.getData().getString(MSG_KEY);;
			
			switch(msg.what) {
				case MSG_SHOWLOG:
					Util.Trace(data);			
				break;
				
				case MSG_UPGRADE:
					Util.Trace("need Upgrade"); 
				break;
				
				default:
				break;
			}
		};
	};
	
	private class ServiceMon extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String data = intent.getStringExtra(ACTION_INTENT_DATA);
			if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_INPROC)) {
				handleMessage(handler, MSG_SHOWLOG, data);
			} 
			else if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_SUCCESS)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_STARTSERVER_SUCCES");
			}
			else if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_FAILURE)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_STARTSERVER_FAILURE");
			}
			else if (intent.getAction().equals(ACTION_INTENT_DOWNLOADCFG_SUCCESS)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_DOWNLOADCFG_SUCCESS");
			}
			else if (intent.getAction().equals(ACTION_INTENT_STOPSERVER_SUCCESS)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_STOPSERVER_SUCCESS");
			}
			else if (intent.getAction().equals(ACTION_INTENT_UPGRADE)) {
				handleMessage(handler, MSG_UPGRADE, data);
			}
			else if (intent.getAction().equals(ACTION_INTENT_NETWORK_CONNECTED)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_NETWORK_CONNECTED");
			}
			else if (intent.getAction().equals(ACTION_INTENT_NETWORK_DISCONNECTED)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_NETWORK_DISCONNECTED");
			}
			else if (intent.getAction().equals(ACTION_INTENT_CHECKAPPS_SUCCESS)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_CHECKAPPS_SUCCESS" + data);
			}
			else if (intent.getAction().equals(ACTION_INTENT_CHECKAPPS_FAILURE)) {
				handleMessage(handler, MSG_SHOWLOG, "Safety Authentic msg : ACTION_INTENT_CHECKAPPS_FAILURE" + data);
			}
			
		}
	}
	
	private void handleMessage(Handler h, int msgID, String data)
	{
		Message msg = new Message();
		msg.what = msgID;
		Bundle bundle = new Bundle();
		bundle.putString(MSG_KEY, data);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}
