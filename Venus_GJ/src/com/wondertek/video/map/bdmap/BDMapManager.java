package com.wondertek.video.map.bdmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MapView.LayoutParams;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.map.IMapPlugin;
import com.wondertek.video.map.MapPluginMgr;
import com.wondertek.xsgj.R;

/**
 * 
 * @author yuhongwei
 *
 */
@SuppressWarnings("deprecation")
public class BDMapManager implements IMapPlugin {
	private static final String TAG = "BDMapManager";
	private static BDMapManager instance = null;
	// add pj
	private LocationClient mLocClient = null;
	private boolean mbIsGetCurrentPositionCalled = false;
	private long mStartTime = 0;
    private Context mContext;
    private BMapManager mBMapMgr;
    private MapView mMapView;
	private LocationListener mLocationListener = null;
	// add pj
    private BDLocationListener mBDLocationListener = null;
    private MKSearch mSearch = null;
    private List<View> mPopViewList = new ArrayList<View>();
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BDMapConstants.BDMAP_LOCATION_FINISHED:
					Log.d(TAG, "BDMAP_LOCATION_FINISHED: " + (String)msg.obj);
					MapPluginMgr.getInstance().nativeCurrentPositionCallback((String)msg.obj);
                    disableLocationManager();
					break;
				case BDMapConstants.BDMAP_POISEARCH_RESULT:
					Log.d(TAG, "PoiSearch Result: " );
					MapPluginMgr.getInstance().nativeSearchCallback((String)msg.obj);
					break;
				case BDMapConstants.BDMAP_GEOCODER_RESULT:
					Log.d(TAG, "GeoCoder Result: " + (String)msg.obj);
                    MapPluginMgr.getInstance().nativeReverseCallback((String)msg.obj);
					break;
				case BDMapConstants.BDMAP_ROUTESEARCH_RESULT:
					Log.d(TAG, ">>>RouteSearch Result<<<");
					break;
				case BDMapConstants.BDMAP_AUTOLOCATION:
					Log.d(TAG, "AutoLocation changed");
					break;
				case BDMapConstants.BDMAP_SIGNTOMAP:
					Log.d(TAG, "Sing to Map");
					break;
				case BDMapConstants.BDMAP_POIDETAIL:
					Log.d(TAG, "Search Detail");
					break;
				case BDMapConstants.BDMAP_POIPRESSED:
					Log.d(TAG, "POI PopView Pressed: " + (String)msg.obj);
                    MapPluginMgr.getInstance().nativePoiPopViewPressedCallback((String)msg.obj);
                    break;
				case BDMapConstants.BDMAP_ERROR:
					break;
				default:
					break;
			}
		}
    };
	
    private BDMapManager(Context cxt) {
		mContext = cxt;
		if (mContext instanceof MapActivity) {
			mBMapMgr = new BMapManager(mContext);
			mBMapMgr.init(BDMapConstants.BDMAP_API_KEYS, new BDMapGeneralListener());
	        mMapView = new MapView(mContext);
	        // mBMapMgr.start();
	        // add pj
	        mLocClient = new LocationClient(VenusApplication.getInstance());
	        ((MapActivity)mContext).initMapActivity(mBMapMgr);
	        mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
	        mMapView.setVisibility(View.INVISIBLE);
	        mMapView.setBuiltInZoomControls(true);
	        mMapView.getController().setZoom(16);
	        mMapView.setDrawOverlayWhenZooming(true);
		}
	}
    
	public static BDMapManager getInstance(Context cxt) {
		if (instance == null) {
			instance = new BDMapManager(cxt);
		}
		return instance;
	}
	
	public static BDMapManager getInstance() {
		return instance;
	}
	
	@Override
	public View getMapView() {
		return mMapView;
	}
	
	@Override
	public void start() {
        if (mLocationListener != null) {
        	mBMapMgr.getLocationManager().requestLocationUpdates(mLocationListener);
        }
		mBMapMgr.start();
	}

	@Override
	public void stop() {
		Log.d(TAG, "[stop]");
        if (mLocationListener != null) {
        	mBMapMgr.getLocationManager().removeUpdates(mLocationListener);
        }
		if (mBDLocationListener != null) {
			mLocClient.unRegisterLocationListener(mBDLocationListener);
			mBDLocationListener = null;
		}
		if (mLocClient.isStarted()) 
			mLocClient.stop();
	}
    
	@Override
	public void setMapViewRect(int x, int y, int width, int height) {
		Log.d(TAG, "setMapViewRect: (" + x + ", " + y + ", " + width + ", " + height + ")");
        mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
        if (mMapView.getVisibility() == View.INVISIBLE) {
        	mMapView.setVisibility(View.VISIBLE);
        }
	}

	@Override
	public void setMapViewVisible(boolean bVisible) {
		Log.d(TAG, "setMapViewVisible: " + bVisible);
		mMapView.setVisibility(bVisible ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void setAutoLocationButton(boolean bEnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getCurrentPosition() {
		Log.d(TAG, "getCurrentPosition");
		setLocationOption();
		if (mBDLocationListener == null)
			mBDLocationListener = new WBDLocationListenner();
		mLocClient.registerLocationListener(mBDLocationListener);
		if (mLocClient.isStarted() == false)
			mLocClient.start();
		if (isGpsEnable() == false)
			showGpsAlert();
		mStartTime = SystemClock.elapsedRealtime();
		mbIsGetCurrentPositionCalled = true;
	}

	@Override
	public void signPointOnMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPoiPopView(int latitude, int longitude, String desc,
			boolean bRemoveCover) {
		Log.d(TAG, "showPoiPopView  latitude: " + latitude + "  longitude: " + longitude + " desc: " + desc 
				+ " RemoveCover: " + bRemoveCover);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mMapView.getController().animateTo(point);
        if (bRemoveCover) {
        	removeAllOverlays();
        	removeAllPopViews();
        }
        if (!desc.trim().equals("")) {
			View popview = BDPopView.getInstance(mContext).getItemPopView(desc);
			mMapView.addView(popview, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, point, LayoutParams.BOTTOM_CENTER));
			mPopViewList.add(popview);
		}

	}

	@Override
	public void showPoisPopView(String pois, boolean bRemoveCover) {
		// TODO Auto-generated method stub
        Log.d(TAG, "ShowPoisPopView pois:" + pois + " bRemoveCover" + bRemoveCover);
        if (bRemoveCover) {
        	removeAllOverlays();
        	removeAllPopViews();
        }
        List<PoiItem> list = parsePoisJson(pois);
        for (int i = 0; i < list.size(); i++) {
        	PoiItem poi = list.get(i);
        	View view = BDPopView.getInstance(mContext).getPoiPopView(poi.poiId, poi.desc);
        	GeoPoint point = new GeoPoint(poi.latitude, poi.longitude);
        	mMapView.addView(view, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
        			LayoutParams.WRAP_CONTENT, point, LayoutParams.BOTTOM));
        	mPopViewList.add(view);
        }
	}

	@Override
	public void poiSearch(String key, int latitude, int longitude, int radius) {
		Log.d(TAG, "poiSearch  key: " + key + " latitude: " + latitude + " longitude: " + longitude + " radius: " + radius);
		GeoPoint point = new GeoPoint(latitude, longitude);
        if (mSearch == null) {
        	mSearch = BDMapSearch.getInstance(mContext, mBMapMgr).getMKSeMkSearch();
        }
        if (!key.trim().equals("")) {
        	String[] keys = key.split(",");
            if (keys.length > 1) {
            	mSearch.poiMultiSearchNearBy(keys, point, radius);
            } else {
            	mSearch.poiSearchNearBy(key, point, radius);
            }
        }
	}

	@Override
	public void poiSearch(String key, String city) {
		Log.d(TAG, "poiSearch cityName: " + city + " key: " + key);
		if (mSearch == null) {
			mSearch = BDMapSearch.getInstance(mContext, mBMapMgr).getMKSeMkSearch();
		}
		if (!key.trim().equals("")) {
			mSearch.poiSearchInCity(city, key);
		}
	}

	@Override
	public void geoCode(int latitude, int longitude) {
		start();
		Log.d(TAG, "GeoCode: [" + (double)latitude * 1e-6 + ", " + (double)longitude * 1e-6 + "]");
        if (mSearch == null) {
        	mSearch = BDMapSearch.getInstance(mContext, mBMapMgr).getMKSeMkSearch();
        }
        GeoPoint point = new GeoPoint(latitude, longitude);
        mSearch.reverseGeocode(point);
	}

	@Override
	public void geoCode(String addr) {
		// TODO Auto-generated method stub
        
	}

	@Override
	public boolean searchRoute(int routeType, String startCityname,String startAddress, int startlatitude, int startlongitude, 
			String endCityname,String endAddress, int endlatitude, int endlongitude) {
		// TODO Auto-generated method stub
		if (mSearch == null) {
			mSearch = BDMapSearch.getInstance(mContext, mBMapMgr).getMKSeMkSearch();
		}
		MKPlanNode stNode = new MKPlanNode();
		if (startlatitude != 0 && startlongitude != 0) {
			stNode.pt = new GeoPoint(startlatitude, endlongitude);
		} else {
			if (startAddress != null && !startAddress.trim().equals("")) {
				stNode.name = startAddress;
			} else {
				return false;
			}
		}
		MKPlanNode enNode = new MKPlanNode();
		if (endlatitude != 0 && endlongitude != 0) {
			enNode.pt = new GeoPoint(endlatitude, endlongitude);
		} else {
			if (endAddress != null && !endAddress.trim().equals("")) {
				enNode.name = endAddress;
			} else {
				return false;
			}
		}
		switch (routeType) {
			case BDMapConstants.BDMAP_ROUTE_SearchType_Driving :
                int dret = mSearch.drivingSearch(startCityname, stNode, endCityname, enNode);
                return dret == 0 ? true : false;
			case BDMapConstants.BDMAP_ROUTE_SearchType_Bus :
                int bret = mSearch.transitSearch(startCityname, stNode, enNode);
                return bret == 0 ? true : false;
			case BDMapConstants.BDMAP_ROUTE_SearchType_Walking :
				int wret = mSearch.walkingSearch(startCityname, stNode, endCityname, enNode);
				return wret == 0 ? true : false;
			default :
				return false;
		}
	}

	@Override
	public void drawRoute(int routeId) {
		// TODO Auto-generated method stub

	}

	@Override
    public void showWeather(int nlatitude, int nlongitude, int ntype, String title, String desc) {
		Log.d(TAG, "ShowWeather: [" + (double)nlatitude * 1e-6 + ", " + nlongitude * 1e-6 + "] Weather: " + 
				ntype +" title: " + title + " desc: " + desc);
		// TODO Auto-generated method stub
	}
	
	@Override
	public void destroyMap() {
		disableLocationManager();
		mBMapMgr.destroy();
	}
    
	public Handler getHandler() {
		return handler;
	}
	
	private void disableLocationManager() {
		if (mLocationListener != null) {
			mBMapMgr.getLocationManager().removeUpdates(mLocationListener);
			mLocationListener = null;
		}
		if (mBDLocationListener != null) {
			mLocClient.unRegisterLocationListener(mBDLocationListener);
			mBDLocationListener = null;
		}
		if (mLocClient.isStarted()) 
			mLocClient.stop();
	}
    
	private List<PoiItem> parsePoisJson(String json) {
		List<PoiItem> list = new ArrayList<PoiItem>();
		try {
			JSONArray objs = new JSONObject(json).getJSONArray("pois");
			for (int i = 0; i < objs.length(); i++) {
				JSONObject obj = (JSONObject)objs.opt(i);
				PoiItem poi = new PoiItem();
				poi.poiId = obj.getString("id");
				poi.latitude = obj.getInt("latitude");
				poi.longitude = obj.getInt("longitude");
				poi.desc = obj.getString("desc");
				list.add(poi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void removeAllOverlays() {
        
	}

	private void removeAllPopViews() {
		while (mPopViewList.size() > 0) {
			View popview = mPopViewList.remove(mPopViewList.size() - 1);
			popview.setVisibility(View.GONE);
			mMapView.removeView(popview);
		}
	}
	
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setScanSpan(5000);
		option.setOpenGps(true);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setPoiNumber(2);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}
	
	private boolean isGpsEnable() {
		LocationManager mLocationManager = (LocationManager) 
				VenusActivity.appActivity.getSystemService(Context.LOCATION_SERVICE);
		return mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}
	
	private void showGpsAlert() {
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
    
	class BDMapGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().
					getIdentifier("bdmap_network_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
            if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().
						getIdentifier("bdmap_apikey_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
            }
		}
		
	}
    
	class PoiItem {
		public String poiId;
		public int latitude;
		public int longitude;
		public String desc;
	}
	
	public class WBDLocationListenner implements BDLocationListener {
		@Override
		public synchronized void onReceiveLocation(BDLocation location) {
			if (location == null || mStartTime == 0 || mbIsGetCurrentPositionCalled == false)
				return ;
			long elapsedTime = (SystemClock.elapsedRealtime() - mStartTime) / 1000;
			if (elapsedTime <= 20 && location.getRadius() > 300) {
				return;
			}else if (elapsedTime <= 30 && location.getRadius() > 600) {
				return;
			}
			String desc = "";
			String type = "";
			mStartTime = 0;
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				type = "gps";
				String msg = String.format("GPS定位成功：误差%.1f, 耗时%d秒.", location.getRadius(), elapsedTime);
				Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				desc = location.getAddrStr();
				type = "network";
				String msg = String.format("网络定位成功：误差%.1f, 耗时%d秒.", location.getRadius(), elapsedTime);
				Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			}
			int geoLat = (int)(location.getLatitude() * 1e6);
			int geoLog = (int)(location.getLongitude() * 1e6);
			String str ="{\"latitude\":\""+geoLat+"\",\"longitude\":\""+geoLog+
					"\",\"radius\":\""+location.getRadius()+"\",\"desc\":\""+desc+ "\",\"type\":\""+type+"\"}";
			Message msg = handler.obtainMessage();
			msg.what = BDMapConstants.BDMAP_LOCATION_FINISHED;
			msg.obj = str;
			Log.d(TAG, "sendMessage");
			handler.sendMessage(msg);	
			mbIsGetCurrentPositionCalled = false;
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : "); 
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			} 
			if(poiLocation.hasPoi()){
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			}else{				
				sb.append("noPoi information");
			}
		}
	}
}
