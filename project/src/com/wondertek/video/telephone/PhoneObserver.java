package com.wondertek.video.telephone;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


/**
 * Listener Phone State
 * @author yuhongwei
 *
 */
public class PhoneObserver {
    private static final String TAG = "PhoneObserver";
    private static PhoneObserver instance = null;
    private Context context;
    private TelephonyManager telMgr;
    private int phoneSignalStrength = 0;
    private PhoneStateListener listener = new PhoneStateListener() {
		// TODO Auto-generated method stub
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.d(TAG, "onSignalStrengthsChanged");
            if (signalStrength.isGsm()) {
            	phoneSignalStrength = signalStrength.getGsmSignalStrength();
            } else {
            	phoneSignalStrength = signalStrength.getCdmaDbm();
            }
		}
    };
    
    private PhoneObserver(Context cxt) {
    	context = cxt;
        telMgr = (TelephonyManager)(context.getSystemService(Context.TELEPHONY_SERVICE));
    }
    
    public static PhoneObserver getInstance(Context cxt) {
    	if (instance == null) {
    		instance = new PhoneObserver(cxt);
    	}
    	return instance;
    }
    
    public static PhoneObserver getInstance() {
    	return instance;
    }
    
    public void enablePhoneStateListener(int events) {
    	telMgr.listen(listener, events);
    }
    
    public void disablePhoneStateListener() {
    	telMgr.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
    
    public int getSignalStrength() {
        if (phoneSignalStrength != 99) {
        	return phoneSignalStrength * 2 - 113;
        }
        return phoneSignalStrength;
    }
    
    public int getCellId() {
    	GsmCellLocation location = (GsmCellLocation)telMgr.getCellLocation();
        if (location != null) {
        	return location.getCid();
        } else {
        	return -1;
        }
    }
    
    public String getMacAddress() {
    	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo info = wifiMgr.getConnectionInfo();
    	return info.getMacAddress();
    }

}
