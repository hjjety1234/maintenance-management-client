package com.wondertek.video.map.bdmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

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
import com.wondertek.video.map.IMapPlugin;
import com.wondertek.video.map.MapPluginMgr;

/**
 * 
 * @author yuhongwei
 *
 */
@SuppressWarnings("deprecation")
public class BDMapManager implements IMapPlugin {
	private static final String TAG = "BDMapManager";
	private static BDMapManager instance = null;
    private Context mContext;
    private BMapManager mBMapMgr;
    private MapView mMapView;
    private LocationListener mLocationListener = null;
    private MKSearch mSearch = null;
    private List<View> mPopViewList = new ArrayList<View>();
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BDMapConstants.BDMAP_LOCATION_FINISHED:
					Log.d(TAG, "Current Position: " + (String)msg.obj);
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
	        mBMapMgr.start();
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
        if (mLocationListener != null) {
        	mBMapMgr.getLocationManager().removeUpdates(mLocationListener);
        }
		mBMapMgr.stop();
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
        if (mLocationListener == null) {
        	mLocationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					Log.d(TAG, "onLocationChanged");
					int geoLat = (int)(location.getLatitude() * 1e6);
					int geoLog = (int)(location.getLongitude() * 1e6);
					String str ="{\"latitude\":\""+geoLat+"\",\"longitude\":\""+geoLog+"\"}";
					Message msg = handler.obtainMessage();
					msg.what = BDMapConstants.BDMAP_LOCATION_FINISHED;
					msg.obj = str;
					handler.sendMessage(msg);					
				}
			};
        	mBMapMgr.getLocationManager().requestLocationUpdates(mLocationListener);
        }
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
	
}
