package com.wondertek.video.msgpush.implbyself;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Building a Service to polling request a message from server background
 * @author yuhongwei
 *
 */

public class MsgPushService extends Service {
    private static final String TAG = "MsgPushService";

	private Thread mThread;
	private BroadcastReceiver msgReceiver;
	private TelephonyManager telephonyManager;
	private SharedPreferences sharedPrefs;
    
	private Handler mHandler = new Handler();
	private BroadcastReceiver mReceiver = new MyRecevier();
	private PhoneStateListener mListener = new MyListener();
    
	@Override
	public void onCreate() {
		super.onCreate();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		sharedPrefs = getSharedPreferences(Constants.MSG_PUSH_DEFINITION,
				Context.MODE_PRIVATE);
        
		mThread = new PollingThread(this);
		msgReceiver = new MsgPushReceiver(this);

		startPollingThread();
		registerNotificationReceiver();
		registerConnectivityReceiver();
	}

	@Override
	public void onDestroy() {
		unregisterNotificationReceiver();
		unregisterConnectivityReceiver();
		stopPollingThread();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void startPollingThread() {
		if (!mThread.isAlive()) {
			try {
				mThread.start();
			} catch (IllegalThreadStateException e) {
				mThread = new PollingThread(this);
				mThread.start();
			}
		}
	}

	public void stopPollingThread() {
		if (mThread.isAlive()) {
			try {
				mThread.interrupt();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private void registerNotificationReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
		registerReceiver(msgReceiver, filter);
	}

	private void unregisterNotificationReceiver() {
		unregisterReceiver(msgReceiver);
	}

	private void registerConnectivityReceiver() {
		telephonyManager.listen(mListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		telephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);

		unregisterReceiver(mReceiver);
	}

	public static Intent getIntent() {
		return new Intent(Constants.SERVICENAME);
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPrefs;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public class MyListener extends PhoneStateListener {
		@Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);
			Log.d(TAG, "onDataConnectionStateChanged()...");
			Log.d(TAG, "Data Connection State = " + getState(state));

			if (state == TelephonyManager.DATA_CONNECTED) {
				startPollingThread();
			}
		}

		private String getState(int state) {
			switch (state) {
			case 0:
				return "DATA_DISCONNECTED";
			case 1:
				return "DATA_CONNECTING";
			case 2:
				return "DATA_CONNECTED";
			case 3:
				return "DATA_SUSPENDED";
			}
			return "DATA_<UNKNOWN>";
		}
	}

	public class MyRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive()...");
			String action = intent.getAction();
			Log.d(TAG, "action=" + action);

			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();

			if (networkInfo != null) {
				Log.d(TAG, "Network Type  = " + networkInfo.getTypeName());
				Log.d(TAG, "Network State = " + networkInfo.getState());
				if (networkInfo.isConnected()) {
					Log.d(TAG, "Network connected");
					startPollingThread();
				}
				// } else {
				// Util.Trace("Network unavailable");
				// stopPollingThread();
			}
		}
	}
}
