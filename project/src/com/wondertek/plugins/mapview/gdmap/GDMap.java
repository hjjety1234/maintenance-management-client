package com.wondertek.plugins.mapview.gdmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas.VertexMode;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.MapAbcException;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.core.ServerUrlSetting;
import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.location.LocationProviderProxy;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.poisearch.PoiSearch;

public class GDMap implements LocationListener {
	private static String TAG_GD = "Gaode";
	private Activity venusActivity;
	private MapController mMapController;
	private Handler mHandler;

	public MapView mMapView;
	private ProgressDialog progDialog = null;
	private com.mapabc.mapapi.geocoder.Geocoder coder;
	private String addressName;
	private ArrayList<View> mPopViewList;
	private static final int WEATHER_DUOYUN = 0;
	private static final int WEATHER_QING = 1;
	private static final int WEATHER_YU = 2;
	private static final int WEATHER_LEIYU = 3;
	private static final int WEATHER_WU = 4;
	private static final int WEATHER_XUE = 5;

	//private MyPoiSearch myPoiSearch = null;
	public MyRoute myRoute = null;
	public MyPoiSearch myPoiSearch = null;

	
	private LocationManagerProxy locationManager = null;
	
	private static GDMap sInstance = null;

	public static GDMap getInstance(Activity appActivity,
			Handler handler) {
		if (sInstance == null) {
			sInstance = new GDMap(appActivity, handler);
		}
		return sInstance;
	}

	public static GDMap getInstance() {
		return sInstance;
	}

	public GDMap(Activity activity, Handler handler) {
		venusActivity = activity;
		mHandler = handler;
		init();
	}

	public void init() {
        mMapView = new MapView(venusActivity,"5bcb93cef3bf75163c161bb7cea81b302f3815e5d9935e84bd763cd4b8be14cb7861f1266f659f31");
        mMapView.setBuiltInZoomControls(true); 
        mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
		ServerUrlSetting serverUrlSetting = new ServerUrlSetting();
		serverUrlSetting.strSateliteTmcUrl = "http://192.168.13.179:8088/mapabc/maptile";
		serverUrlSetting.strPoiSearchUrl = "http://192.168.13.179:7001";
		mMapView.setServerUrl(serverUrlSetting);
        mMapController = mMapView.getController();
        mMapController.setZoom(15);
        coder = new com.mapabc.mapapi.geocoder.Geocoder(venusActivity,"5bcb93cef3bf75163c161bb7cea81b302f3815e5d9935e84bd763cd4b8be14cb7861f1266f659f31");
        progDialog = new ProgressDialog(venusActivity);
        mPopViewList = new ArrayList<View>();
	}

	public MapView getMapView() {
		return mMapView;
	}

	public void setMapViewRect(int x, int y, int w, int h) {
		mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h, x, y));
		if (mMapView.getVisibility() == View.INVISIBLE)
			mMapView.setVisibility(View.VISIBLE);
	}

	public void setShow(boolean bShow) {
		if (bShow)
			mMapView.setVisibility(View.VISIBLE);
		else
			mMapView.setVisibility(View.INVISIBLE);
	}
	
	public void ShowWeatherDailog(int nlatitude, int nlongitude, int ntype, String address, String description)
	{
		GeoPoint point = new GeoPoint(nlatitude, nlongitude);
		mMapController.setCenter(point);
		mMapController.setZoom(16);
		
		View popView = venusActivity.getLayoutInflater().inflate(
				venusActivity.getResources().getIdentifier("popupweather", "layout", venusActivity.getPackageName()), null);
		
		TextView name = (TextView) popView.findViewById(venusActivity.getResources().getIdentifier("Address", "id", venusActivity.getPackageName()));
		
		name.setText(description);
		
		ImageView image = (ImageView) popView.findViewById(venusActivity.getResources().getIdentifier("ImageWeather", "id", venusActivity.getPackageName()));
		
		switch(ntype)
		{
		case WEATHER_DUOYUN:
			image.setImageResource(venusActivity.getResources().getIdentifier("duoyun", "drawable", venusActivity.getPackageName()));
			break;
		case WEATHER_QING:
			image.setImageResource(venusActivity.getResources().getIdentifier("qing", "drawable", venusActivity.getPackageName()));
			break;
		case WEATHER_YU:
			image.setImageResource(venusActivity.getResources().getIdentifier("yu", "drawable", venusActivity.getPackageName()));
			break;
		case WEATHER_LEIYU:
			image.setImageResource(venusActivity.getResources().getIdentifier("leiyu", "drawable", venusActivity.getPackageName()));
			break;
		case WEATHER_WU:
			image.setImageResource(venusActivity.getResources().getIdentifier("wu", "drawable", venusActivity.getPackageName()));
			break;
		case WEATHER_XUE:
			image.setImageResource(venusActivity.getResources().getIdentifier("xue", "drawable", venusActivity.getPackageName()));
			break;
		}
		
		mMapView.addView(popView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.CENTER));
		mPopViewList.add(popView);
	}

	public void SetMapViewCenter(int x, int y, String name,
			boolean bremoveAllOverley) {
		GeoPoint point = new GeoPoint(x, y);
		mMapController.setCenter(point);
		mMapController.setZoom(16);
		if(!name.trim().equals("")){
			View popView = venusActivity.getLayoutInflater().inflate(
					venusActivity.getResources().getIdentifier("map_popview", "layout", venusActivity.getPackageName()), null);
			TextView name_ = (TextView) popView.findViewById(venusActivity.getResources().getIdentifier("point_name", "id", venusActivity.getPackageName()));
			if(bremoveAllOverley){
				while(mPopViewList.size()>0){
					View childPopView =mPopViewList.remove(mPopViewList.size()-1);
					childPopView.setVisibility(View.INVISIBLE);
					mMapView.removeView(childPopView);
				}
				
				if(myRoute!=null)
					myRoute.removeRouteOverlay();
			}
			mMapView.addView(popView, new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
					MapView.LayoutParams.CENTER));
			mPopViewList.add(popView);
			name_.setText(name);
		}
	}
	
	public void animateTo(GeoPoint point) {
		mMapController.animateTo(point);
	}
	
	
	
	public void GetLocationManager()
	{
		if(locationManager == null)
		{
			locationManager = LocationManagerProxy.getInstance(venusActivity,"5bcb93cef3bf75163c161bb7cea81b302f3815e5d9935e84bd763cd4b8be14cb7861f1266f659f31");
			EnableMyLocation();
		}
		else
			Log.d(TAG_GD,"GetLocationManager locationManager is running");
	}
	
	public boolean EnableMyLocation() {
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		cri.setAltitudeRequired(false);
		cri.setBearingRequired(false);
		cri.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(cri, true);
		Log.d(TAG_GD,"bestProvider =" +bestProvider);
		if(bestProvider == null || bestProvider.equals(""))
		{
			bestProvider = LocationProviderProxy.MapABCNetwork;
		}
		Log.d(TAG_GD,"bestProvider =" +bestProvider);
		Log.d(TAG_GD,"requestLocationUpdates arg1=" + LocationProviderProxy.MapABCNetwork +"arg2=2000,arg3=10,arg4=this");
		
		locationManager.requestLocationUpdates(bestProvider, 2000, 10,
				this);
		
		TelephonyManager tm = (TelephonyManager)venusActivity.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d(TAG_GD,"====TelephonyManager device ID = " + tm.getDeviceId());
		Log.d(TAG_GD,"====TelephonyManager software version number for the device = " + tm.getDeviceSoftwareVersion());
		Log.d(TAG_GD,"====TelephonyManager phone number string for line 1 = " + tm.getLine1Number());
		Log.d(TAG_GD,"====TelephonyManager ISO country code equivalent of the current registered operator's MCC = " + tm.getNetworkCountryIso());
		Log.d(TAG_GD,"====TelephonyManager NetworkOperator = " + tm.getNetworkOperator());
		Log.d(TAG_GD,"====TelephonyManager NetworkOperatorName = " + tm.getNetworkOperatorName());
		Log.d(TAG_GD,"====TelephonyManager getSimCountryIso = " + tm.getSimCountryIso());
		Log.d(TAG_GD,"====TelephonyManager getSimOperator = " + tm.getSimOperator());
		Log.d(TAG_GD,"====TelephonyManager getSimOperatorName = " + tm.getSimOperatorName());
		Log.d(TAG_GD,"====TelephonyManager getSimSerialNumber = " + tm.getSimSerialNumber());
		Log.d(TAG_GD,"====TelephonyManager getSubscriberId = " + tm.getSubscriberId());
		Log.d(TAG_GD,"====TelephonyManager getVoiceMailAlphaTag = " + tm.getVoiceMailAlphaTag());
		Log.d(TAG_GD,"====TelephonyManager getVoiceMailNumber = " + tm.getVoiceMailNumber());
		
		List<NeighboringCellInfo> ls = tm.getNeighboringCellInfo();
		for(int i = 0; i<ls.size();i++)
		{
			Log.d(TAG_GD,"====TelephonyManager getNeighboringCellInfo NeighboringCellInfo(" +i +").toString = " + ls.get(i).toString());
		}
		return true;
	}

	public void DisableMyLocation() {
		locationManager.removeUpdates(this);
		locationManager.destory();
		locationManager =null;
	}
	
	
	
	
	public void searchRouteResult(int routeType,String startCity,String strStart, int startlatitude, int startlongitude,
			String endCity, String strEnd, int endlatitude, int endlongitude ){
		if(myRoute == null)
			myRoute = MyRoute.getInstance(venusActivity,mMapController,mMapView,mHandler);
		
		GeoPoint startPoint = null;
		GeoPoint endPoint = null;
		if(startlatitude != 0 && startlongitude!=0){
			strStart = null;
			startPoint = new GeoPoint(startlatitude, startlongitude);
		}
		
		if(endlatitude != 0 && endlongitude !=0)
		{
			strEnd = null;
			endPoint = new GeoPoint(endlatitude, endlongitude);
		}
		
		
		
		boolean bRet = myRoute.searchRouteResult(startPoint,endPoint,routeType);
		
		if(!bRet){
			if(strStart ==null || strStart.trim().equals("") || endPoint == null)
				bRet = false;
			else{
				startPoint = GetFromLocationName(strStart);
				bRet = myRoute.searchRouteResult(startPoint,endPoint,routeType);
			}
		}
		
		if(!bRet){
			if(startPoint == null || strEnd == null || strEnd.trim().equals(""))
				bRet = false;
			else{
				endPoint = GetFromLocationName(strEnd);
				bRet = myRoute.searchRouteResult(startPoint,endPoint,routeType);
			}
		}
		
		if(!bRet){
			if(strStart == null || strStart.trim().equals("") || strEnd == null || strEnd.trim().equals(""))
				bRet = false;
			else{
				startPoint = GetFromLocationName(strStart);
				endPoint = GetFromLocationName(strEnd);
				myRoute.searchRouteResult(startPoint,endPoint,routeType);
			}
		}
			
			
			
//		if(!bRet)
//			myRoute.searchRouteResult(strStart, startCity,endPoint);
//		if(!bRet)
//			myRoute.searchRouteResult(startPoint,strEnd, endCity);
//		if(!bRet)
//			myRoute.searchRouteResult(strStart, startCity,strEnd, endCity);
	}
	
	
	public void StartSearchNearBy(String key, int latitude, int longitude, int radius)
	{
		if(myPoiSearch==null)
			myPoiSearch = MyPoiSearch.getInstance(venusActivity,mMapController,mMapView,mHandler);
		Log.d(TAG_GD,"invoke com.wondertek.video.gdmap.MyPoiSearch func ----- doSearch");
		myPoiSearch.doSearch(key, new GeoPoint(latitude,longitude),radius);
	}
	
	public void PoiSearchInCity(String cityName,String address)
	{
		if(myPoiSearch==null)
			myPoiSearch = MyPoiSearch.getInstance(venusActivity,mMapController,mMapView,mHandler);
		Log.d(TAG_GD,"invoke com.wondertek.video.gdmap.MyPoiSearch func ----- doSearch");
		myPoiSearch.doSearch(address, cityName);
	}
	
	
	public void getAddress(final double mlat,final double mLon){
		Thread t = new Thread(new Runnable() 
		{
			public void run()
			{
				progDialog.dismiss();
				try {
					Log.d(TAG_GD,"invoke com.mapabc.mapapi.geocoder.Geocode func ----- getFromLocation(mlat,mLon,3)");
					List<Address> address = coder.getFromLocation(mlat,mLon,3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						//addressName=addres.getAdminArea() + addres.getSubLocality() + addres.getFeatureName();
						String strResult ="{\"address\":\"";
						if(addres.getSubLocality()!=null)
							strResult += addres.getSubLocality(); 
						
						if(addres.getFeatureName()!=null)
						{
							if(addres.getSubLocality()!=null)
								strResult += ",";
							strResult += addres.getFeatureName();
						}
						
						strResult += "\","+"\"city\":\""+addres.getAdminArea() +"\"}";
						
						for(int i =0;i <address.size();i++)
						{
							Log.d(TAG_GD,i+":address=" + address.get(i) +",city=" + address.get(i));
						}
						
						mHandler.sendMessage(Message
								.obtain(mHandler, Constants.REOCODER_RESULT,strResult));
					}
					else
					{
						Log.d(TAG_GD,"invoke  address = coder.getFromLocation(mlat,mLon,3), address is null");
						mHandler.sendMessage(Message
								.obtain(mHandler, Constants.REOCODER_RESULT,"{}"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG_GD,"invoke  getAddress is Exception:" + e.getMessage());
					mHandler.sendMessage(Message
							.obtain(mHandler, Constants.ERROR));
					mHandler.sendMessage(Message
							.obtain(mHandler, Constants.REOCODER_RESULT,"{}"));
				}
		
			}
		});
	
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_progress_msg1", "string", venusActivity.getPackageName())));
		progDialog.show();
		t.start();
	}
	
	public GeoPoint GetFromLocationName(String locationName)
	{
		try {
			Log.d(TAG_GD,"invoke com.mapabc.mapapi.geocoder.Geocode func ----- getFromLocationName locationName=" + locationName );
			List<Address> list = coder.getFromLocationName(locationName, 5);
			if(list != null && list.size() > 0){
				Address address = list.get(0);
				
				int latitude =(int) (address.getLatitude()*1e6);
				int longitude =(int) (address.getLongitude()*1e6);
				return new GeoPoint(latitude,longitude);
			}
			else
			{
				Log.d(TAG_GD,"invoke list = coder.getFromLocationName(locationName, 5), list is null" );
			}
		} catch (MapAbcException e) {
			// TODO Auto-generated catch block
			Log.d(TAG_GD,"invoke GetFromLocationName is MapAbcException =" + e.getErrorMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			int geoLat = (int) (location.getLatitude()*1e6);
			int geoLng = (int) (1e6 * location.getLongitude());
			String str ="{\"latitude\":\""+geoLat+"\",\"longitude\":\""+geoLng+"\"}";
			Message msg = new Message();
			msg.what = Constants.LOCATION_GOEPOINT;
			msg.obj=str;
		
			if(mHandler!=null)
				mHandler.sendMessage(msg);
			DisableMyLocation();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
