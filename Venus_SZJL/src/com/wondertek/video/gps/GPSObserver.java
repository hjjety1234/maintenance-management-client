package com.wondertek.video.gps;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tencent.mm.sdk.Build;
import com.wondertek.szjl.R;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class GPSObserver {
	private static String TAG = "WDGPS";
	private static GPSObserver instance = null;
	private VenusActivity venusHandle = null;
	private LocationManager mLocationManager = null;
	private static List<String> providers;
	private boolean bListener = false;

	private final static double PI = 3.14159265358979323; //pai
	private final static double RADIUS = 6371229; //earth

	private GpsStatus gpsStatus = null;

	private boolean gpsBeginState;
	private boolean gpsCurState;
	
	private boolean mInjectGps = false;
	
	private GPSObserver(VenusActivity va) {
		venusHandle = va;
	}

	public static GPSObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new GPSObserver(va);
		}

		return instance;
	}

	private boolean isGPSEnable() {
		if(mLocationManager==null)
			mLocationManager = (LocationManager) VenusActivity.appActivity.getSystemService(Context.LOCATION_SERVICE);
			
		if(mLocationManager==null)
			return false;
		
		providers = mLocationManager.getAllProviders();
		if(providers!=null && providers.size() > 0)
		{
			for(String provider :providers)
			{
				Log.d(TAG, "provider: " + provider);
				if(provider.equals("gps"))
					return true;
			}
		}
		
		mLocationManager = null;
		return false;
	}
	
	private boolean getGPSState() {
		if(mLocationManager==null)
			mLocationManager = (LocationManager) VenusActivity.appActivity.getSystemService(Context.LOCATION_SERVICE);

		if(mLocationManager==null)
			return false;
		
		injectGps();

		return mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}

	private void openGPS()
	{
		if(isGPSEnable())
			if(!mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				// add pj
				showGpsAlert();
				toggleGPS();
			}
	}
	
	/**
	 * inject XTRA data in order to get a faster fix.
	 * @author hewu <hewu2008@gmail.com>
	 */
	public void injectGps()
	{
		if (mInjectGps == false) {
			Log.d(TAG, "on injectGps");
			mLocationManager.sendExtraCommand("gps", "force_xtra_injection", null);
			mInjectGps = true;
		}
	}
	
	/**
	 * show open GPS setting dialog if GPS is disabled.
	 * @author hewu <hewu2008@gmail.com>
	 */
	public void showGpsAlert() {
        final Dialog dialog = new Dialog(VenusActivity.appActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.gps);
        Button gps_yes = (Button)dialog.findViewById(R.id.button_yes);
        gps_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VenusActivity.appActivity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				dialog.dismiss();
			}
		});
        Button gps_cancel = (Button)dialog.findViewById(R.id.button_cancel);
        gps_cancel.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        });
        final Handler threadHandler = new Handler(); ;  
        new Thread() {
        	@Override
        	public void run() {
        		 threadHandler.post( new Runnable(){
					@Override
					public void run() {
						dialog.show();
					}
        		 });
        	}
        }.start();
	}

	private void closeGPS()
	{
		if(isGPSEnable())
			if(mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
				toggleGPS();		
	}

	public boolean initGPS() {
		Log.d(TAG, "on initGPS");
		
		if(bListener)
			return true;

		if(isGPSEnable())
		{
			if(!mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
				toggleGPS();
			
			if(mLocationManager.addGpsStatusListener(gpsListener))
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
			else
				return false;
			bListener = true;
			return true;
		}
		else
			return false;
	}
	
	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			GpsStatus status = mLocationManager.getGpsStatus(null);
			switch (event) {
				case GpsStatus.GPS_EVENT_STARTED:
					Log.d(TAG, "on GPS_EVENT_STARTED");
					break;
				case GpsStatus.GPS_EVENT_STOPPED:
					Log.d(TAG, "on GPS_EVENT_STOPPED");
					break;
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					break;
			}
		}
	};

	public int getDistance(float startLongitude, float startLatitude,
			float endLongitude, float endLatitude) {
		Log.d(TAG, "on getdistance");
		double x, y, distance;

		x = (endLongitude - startLongitude) * PI * RADIUS
				* Math.cos(((startLatitude + endLatitude) / 2) * PI / 180)
				/ 180;
		y = (endLatitude - startLatitude) * PI * RADIUS / 180;
		distance = Math.hypot(x, y);
		return (int) distance;
	}

	private final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "on locationchanged");
			Log.d(TAG, "Time:" +location.getTime());
			Log.d(TAG, "经度:" +location.getLongitude());
			Log.d(TAG, "纬度:" +location.getLatitude());
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			nativegpsreturn(formatter.format(location.getTime()), (int)(location.getAccuracy()), (int)(location.getLongitude()*10e6),(int)(location.getLatitude()*10e6),
					(int)location.getSpeed(),(int)location.getBearing(),(int)location.getAltitude());
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "GSP is close");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, "GSP is open");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(status==LocationProvider.AVAILABLE){  
				Log.d(TAG, "GPS is available");  
            }else if(status==LocationProvider.OUT_OF_SERVICE){  
            	Log.d(TAG, "GPS out of service");  
            }else if(status==LocationProvider.TEMPORARILY_UNAVAILABLE){  
            	Log.d(TAG, "GPS temporarily unavailable");  
            }  
		}
	};

	public boolean stopGPS() {
		Log.d(TAG, "on stop");
		if(mLocationManager==null)
			return true;
		mLocationManager.removeGpsStatusListener(gpsListener);
		mLocationManager.removeUpdates(mLocationListener);
		mLocationManager = null;	
		bListener = false;
		return true;
	}
	
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));

		try {
			PendingIntent.getBroadcast(
					VenusApplication.getInstance().getApplicationContext(), 0,
					gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	public void getGPSData()
	{
		if(mLocationManager == null)
			return;
		Location location =mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location == null) {
			Log.d(TAG, "location == null");
			nativegpsreturn("", 0, 0, 0, 0, 0, 0);
			return;
		}else if (new Date().getTime() - location.getTime() > 30000) {
			Log.d(TAG, "location expired");
			nativegpsreturn("", 0, 0, 0, 0, 0, 0);
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Log.d(TAG, "Time:" + formatter.format(location.getTime()));
		Log.d(TAG, "Longitude:" + location.getLongitude());
		Log.d(TAG, "Latitude:" + location.getLatitude());
		Log.d(TAG, "Accuracy:" + location.getAccuracy());
		nativegpsreturn(formatter.format(location.getTime()), (int)(location.getAccuracy()), (int)(location.getLongitude()*10e6),(int)(location.getLatitude()*10e6),
				(int)location.getSpeed(),(int)location.getBearing(),(int)location.getAltitude());
	}

	public native void nativegpsreturn(String time, int accuracy,
			int longitude, int latitude, int speed, int bearing,
			int altitude);
}