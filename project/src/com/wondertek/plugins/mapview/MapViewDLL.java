package com.wondertek.plugins.mapview;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.mapabc.mapapi.core.GeoPoint;
import com.wondertek.plugins.mapview.gdmap.Constants;
import com.wondertek.plugins.mapview.gdmap.GDMap;
import com.wondertek.video.VenusApplication;

public class MapViewDLL{
    /** Called when the activity is first created. */
	public final static String TAG_GD = "GDMAP";
	public static GDMap gdMap = null;
	public Activity m_activity = null;
	public static MapViewDLL sInstance = null;
	public native void  nativeSearchCallback(String strResult);
	public native void  nativeReverseCallback(String strResult);
	public native void  nativeCurrentPositionCallback(String strResult);
	
	private Handler mapHandler =new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == Constants.REOCODER_RESULT){
				Log.d(TAG_GD,(String)msg.obj);
				nativeReverseCallback((String)msg.obj);
			}else if(msg.what == Constants.FIRST_LOCATION_GOEPOINT){
				gdMap.animateTo((GeoPoint)msg.obj);
				Log.d(TAG_GD,((GeoPoint)msg.obj).toString());
			}else if(msg.what == Constants.LOCATION_GOEPOINT){
				Log.d(TAG_GD,(String)msg.obj);
				nativeCurrentPositionCallback((String)msg.obj);
			} else if (msg.what == Constants.ROUTE_SEARCH_RESULT) {
				gdMap.myRoute.showRouteResult();
			} else if (msg.what == Constants.POISEARCH){
				
				String data = gdMap.myPoiSearch.doPoisearchSuccess();
				Log.d(TAG_GD,data);
				nativeSearchCallback(data);
			} else if (msg.what == Constants.ERROR){
				String errormsg = (String)msg.obj;
				if(errormsg!=null)
					Log.d(TAG_GD,errormsg);
			}
		}
	};
	
	private MapViewDLL(Activity context)
	{
		m_activity  = context;
		gdMap = new GDMap(m_activity, mapHandler);
	}
	
	public static MapViewDLL getInstance(Activity context)
	{
		if(sInstance == null)
			sInstance = new MapViewDLL(context);
		
		return sInstance;
	}
    
    public static boolean javaSetMapViewRect(int x,int y,int w,int h)
	{
		if(gdMap != null)
			gdMap.setMapViewRect(x, y, w, h);
		else
			return false;
		return true; 
	}
	
	public static boolean javaShowMapView(boolean bshow)
	{
		if(gdMap == null)
			return false;
		
		int nShowState = View.GONE;
		if(bshow)
			nShowState = View.VISIBLE;
		else
			nShowState = View.INVISIBLE;
		
		gdMap.setShow(bshow);
		return true;
	}
	
	public static void javaSetMapViewCenter(int latitude, int longitude,String name ,boolean bremoveAllOverley)
	{
		Log.d(TAG_GD,"javaSetMapViewCenter latitude =" + latitude + ", longitude =" + longitude + ", name="+name);
		if(gdMap == null)
			return;
	
		gdMap.SetMapViewCenter(latitude, longitude,name,bremoveAllOverley);
	}
	
	public static void javaStartGetCurrentPosition()
	{
		Log.d(TAG_GD,"invoke javaStartGetCurrentPosition");
		if(gdMap == null)
			return;
		gdMap.GetLocationManager();
	}
	
	public static boolean javaStartSearchNearBy(String key, int latitude, int longitude, int radius)
	{
		Log.d(TAG_GD,"javaStartSearchNearBy latitude =" + latitude + ", longitude =" + longitude + ", key="+key);
		if(gdMap == null)
			return false;
		
		gdMap.StartSearchNearBy(key, latitude, longitude, radius);
		return true;
	}
	
	public static boolean javaShowWeatherDailog(int nlatitude, int nlongitude, int ntype, String address, String description)
	{
		Log.d(TAG_GD,"ShowWeatherDailog latitude =" + nlatitude + ", longitude =" + nlongitude + ", ntype="+ntype + ", address" +address +",description=" +description);
		if(gdMap == null)
			return false;
		
		gdMap.ShowWeatherDailog(nlatitude, nlongitude, ntype, address, description);
		return true;
	}
	
	public static boolean javaPoiSearchInCity(String cityName,String address)
	{
		Log.d(TAG_GD,"javaPoiSearchInCity cityName =" + cityName + ", address =" + address );
		if(gdMap == null)
			return false;
		
		gdMap.PoiSearchInCity(cityName, address);
		return true;
	}
	
	public static boolean javaStartreverseGeocode(int latitude, int longitude)
	{
		Log.d(TAG_GD,"javaStartreverseGeocode latitude =" + latitude + ", longitude =" +longitude);
		if(gdMap == null)
			return false;
		gdMap.getAddress(latitude*1e-6,longitude*1e-6);
		return true;
	}
	
	public static boolean javaSearchRoute(int routeType, String startCityname,String startAddress, int startlatitude, int startlongitude, String endCityname,String endAddress, int endlatitude, int endlongitude)
	{
		Log.d(TAG_GD,"javaStartSearchNearBy  routeType,  startCityname, startAddress,  startlatitude,  startlongitude,  endCityname, endAddress, endlatitude, endlongitude =("+
				routeType+","+startCityname+","+ startAddress+","+ startlatitude+","+startlongitude+","+ endCityname+","+endAddress+","+endlatitude+","+endlongitude+")");
		if(gdMap == null)
			return false;
		
		gdMap.searchRouteResult(routeType, startCityname, startAddress, startlatitude, startlongitude, endCityname, endAddress, endlatitude, endlongitude);
		return true;
	}
	
	static {
		  String libpath = VenusApplication.appAbsPath
				    + "/lib2/mapview/libmapview.so";
				  System.load(libpath);
	}
}