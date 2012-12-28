package com.wondertek.video.map.gdmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.location.LocationProviderProxy;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.RouteMessageHandler;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.poisearch.PoiTypeDef;
import com.wondertek.video.map.IMapPlugin;
import com.wondertek.video.map.MapPluginMgr;

/**
 * 
 * @author yuhongwei
 *
 */
@SuppressWarnings("deprecation")
public class GDMapManager implements IMapPlugin, RouteMessageHandler {
	private static final String TAG = "GDMapManager";
    private static GDMapManager instance = null;
	private Context mContext;
	private MapView mMapView;
	private MapController mMapController;
	private LocationManagerProxy locationManager = null;
	private MyLocationOverlay autoLocationOverlay = null;
	private GeoPoint currentPoint = null;
	private View locationPopView = null;
	private List<View> mPopViewList = new ArrayList<View>();
    private boolean bAutoLocationEnable = false;
    
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GDMapConstants.GDMAP_LOCATION_FINISHED:
					Log.d(TAG, "Current Position: " + (String)msg.obj);
					MapPluginMgr.getInstance().nativeCurrentPositionCallback((String)msg.obj);
					disableLocationManager();
					break;
				case GDMapConstants.GDMAP_POISEARCH_RESULT:
					mMapController.setZoom(14);
					GDPoiSearch.getInstance(mContext, mMapView).refreshMapView();
					String data = GDPoiSearch.getInstance(mContext, mMapView).getPoiSearchData();
					Log.d(TAG, "PoiSearch Data: " + data);
					MapPluginMgr.getInstance().nativeSearchCallback(data);
					break;
				case GDMapConstants.GDMAP_GEOCODER_RESULT:
					Log.d(TAG, "GeoCoder Result: " + (String)msg.obj);
					MapPluginMgr.getInstance().nativeReverseCallback((String)msg.obj);
					break;
				case GDMapConstants.GDMAP_ROUTESEARCH_RESULT:
					Log.d(TAG, ">>>RouteSearch Result<<<");
					GDRouteSearch routeSearch = GDRouteSearch.getInstance(mContext, mMapView);
					routeSearch.drawRouteOnMap(0);
					String routedata = routeSearch.getRouteData();
					MapPluginMgr.getInstance().nativeRoutesCallback(routedata);
					break;
				case GDMapConstants.GDMAP_AUTOLOCATION:
					Log.d(TAG, "AutoLocation changed");
					currentPoint = (GeoPoint)msg.obj;
					mMapController.setZoom(17);
					mMapController.animateTo(currentPoint);
					break;
				case GDMapConstants.GDMAP_SIGNTOMAP:
					Log.d(TAG, "Sing to Map");
					MapPluginMgr.getInstance().nativeSendMapPoint((String)msg.obj);
					break;
				case GDMapConstants.GDMAP_POIDETAIL:
					Log.d(TAG, "Search Detail");
					MapPluginMgr.getInstance().nativePoiItemDetailCallback((String)msg.obj);
					break;
				case GDMapConstants.GDMAP_ERROR:
					GDPoiSearch.getInstance(mContext, mMapView).mapError();
					break;
				default:
					break;
			}
		}
	};
    
	private LocationListener listener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == LocationProviderProxy.OUT_OF_SERVICE || 
				status == LocationProviderProxy.TEMPORARILY_UNAVAILABLE) {
                String err = mContext.getResources().getString(mContext.getResources().
                		getIdentifier("gdmap_location_error", "string", mContext.getPackageName()));
				Toast.makeText(mContext, err, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged");
			int geoLat = (int)(location.getLatitude() * 1e6);
			int geoLog = (int)(location.getLongitude() * 1e6);
			String str ="{\"latitude\":\""+geoLat+"\",\"longitude\":\""+geoLog+"\"}";
			Message msg = handler.obtainMessage();
			msg.what = GDMapConstants.GDMAP_LOCATION_FINISHED;
			msg.obj = str;
			handler.sendMessage(msg);
		}
	};
    
    private GDMapManager(Context cxt) {
		Log.d(TAG, ">>>GDMapManager<<<");
		mContext = cxt;
		mMapView = new MapView(mContext, GDMapConstants.GDMAP_API_KEYS);
		mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(0,0,0,0));
		mMapView.setBuiltInZoomControls(true);
//		mMapView.setVectorMap(true);
		mMapController = mMapView.getController();
        mMapController.animateTo(new GeoPoint(31848818, 117255403));
		mMapController.setZoom(15);
	}
    
    public static GDMapManager getInstance(Context cxt) {
    	if (instance == null) {
    		instance = new GDMapManager(cxt);
    	}
    	return instance;
    }
    
    public static GDMapManager getInstance() {
    	return instance;
    }
    
    @Override
	public View getMapView() {
		return mMapView;
	}
    
	@Override
	public void start() {
		enableAutoLocation();
	}

	@Override
	public void stop() {
		disableAutoLocation();
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
        if (!bVisible) {
        	disableAutoLocation();
        }
	}

	@Override
	public void setAutoLocationButton(boolean bEnable) {
		Log.d(TAG, "setAutoLocationButton: " + bEnable);
		bAutoLocationEnable = bEnable;
		if (locationPopView != null) {
			locationPopView.setVisibility(bEnable ? View.VISIBLE : View.INVISIBLE);
            if (!bEnable) {
            	disableAutoLocation();
            }
		} else {
			if (bEnable) {
				locationPopView = GDPopView.getInstance(mContext).getLocationPopView();
				DisplayMetrics dm = new DisplayMetrics();
				WindowManager wm = ((Activity)mContext).getWindowManager();
				wm.getDefaultDisplay().getMetrics(dm);
				Rect rc = new Rect();
				((Activity)mContext).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWindowVisibleDisplayFrame(rc);
                Drawable drawable = mContext.getResources().getDrawable(mContext.getResources().
                		getIdentifier("gdmap_btnloc_nor", "drawable", mContext.getPackageName()));
				mMapView.addView(locationPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT, dm.widthPixels - drawable.getIntrinsicWidth() / 2 - 15, 
						rc.top + 25, LayoutParams.CENTER));
			}
		}
	}

	@Override
	public void getCurrentPosition() {
		Log.d(TAG, "getCurrentPosition");
		if (locationManager == null) {
			locationManager = LocationManagerProxy.getInstance(mContext, GDMapConstants.GDMAP_API_KEYS);
			Criteria cri = new Criteria();
			cri.setAccuracy(Criteria.ACCURACY_COARSE);
			cri.setAltitudeRequired(false);
			cri.setBearingRequired(false);
			cri.setCostAllowed(false);
			String provider = locationManager.getBestProvider(cri, true);
			if (provider == null || provider.equals("")) {
				provider = LocationProviderProxy.MapABCNetwork;
			}
			locationManager.requestLocationUpdates(provider, GDMapConstants.GDMAP_LOCATION_UPDATE_MIN_TIME,
					GDMapConstants.GDMAP_LOCATION_UPDATE_MIN_DISTANCE, listener);
		} else {
			Log.d(TAG, "Location Manager Proxy is Running!");
		}
	}

	@Override
	public void signPointOnMap() {
		Log.d(TAG, "signPointOnMap");
        GDMapPointOverlay po = GDMapPointOverlay.getInstance(mContext, mMapView);
        mMapView.getOverlays().add(po);
	}

	@Override
	public void showPoiPopView(int latitude, int longitude, String desc,
			boolean bRemoveCover) {
		Log.d(TAG, "showPoiPopView  latitude: " + latitude + "  longitude: " + longitude + " desc: " + desc 
				+ " RemoveCover: " + bRemoveCover);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mMapController.setZoom(15);
		mMapController.animateTo(point);
		if (bRemoveCover) {
			removeAllPopViews();
			removeAllOverlays();
		}
		if (!desc.trim().equals("")) {
			View popview = GDPopView.getInstance(mContext).getItemPopView(desc);
			mMapView.addView(popview, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, point, LayoutParams.CENTER));
			mPopViewList.add(popview);
		}
	}

	@Override
	public void showPoisPopView(String pois, boolean bRemoveCover) {
		// TODO Auto-generated method stub

	}

	@Override
	public void poiSearch(String key, int latitude, int longitude, int radius) {
		Log.d(TAG, "poiSearch  key: " + key + " latitude: " + latitude + " longitude: " + longitude + " radius: " + radius);
		if (!key.trim().equals("")) {
			GDPoiSearch poiSearch = GDPoiSearch.getInstance(mContext, mMapView);
			GeoPoint point = new GeoPoint(latitude, longitude);
			poiSearch.search(key, "", point, radius);
		}
	}

	@Override
	public void poiSearch(String key, String city) {
		Log.d(TAG, "poiSearch cityName: " + city + " key: " + key);
        if (!key.trim().equals("")) {
        	GDPoiSearch poiSearch = GDPoiSearch.getInstance(mContext, mMapView);
        	poiSearch.search(key, PoiTypeDef.All, city);
        }
	}

	@Override
	public void geoCode(int latitude, int longitude) {
		Log.d(TAG, "GeoCode: [" + (double)latitude * 1e-6 + ", " + (double)longitude * 1e-6 + "]");
		geoCoder((double)latitude * 1e-6, (double)longitude * 1e-6);
	}

	@Override
	public void geoCode(String addr) {
		// // TODO Auto-generated method stub:
	}

	@Override
	public boolean searchRoute(int routeType, String startCityname,String startAddress, int startlatitude, int startlongitude, 
			String endCityname,String endAddress, int endlatitude, int endlongitude) {
		Log.d(TAG, "RouteSearch Start: { [" + (double)startlatitude * 1e-6 + ", " + (double)startlongitude * 1e-6 + "] addr:" + startAddress 
				+ " City: " + startCityname + "}  End: { [" + (double)endlatitude * 1e-6 + ", " + (double)endlongitude * 1e-6 + "] addr: " +
				endAddress + " City: " + endCityname + "}");
		if (mMapView != null) {
			removeAllPopViews();
			removeAllOverlays();
			GeoPoint startPoint = null;
			GeoPoint endPoint = null;
			if (startlatitude != 0 && startlongitude != 0) {
				startAddress = null;
				startPoint = new GeoPoint(startlatitude, startlongitude);
			}
			if (endlatitude != 0 && endlongitude != 0) {
				endAddress = null;
				endPoint = new GeoPoint(endlatitude, endlongitude);
			}
			boolean bRet = GDRouteSearch.getInstance(mContext, mMapView).routeSearch(startPoint, endPoint, routeType);
			if(!bRet){
				if(startAddress ==null || startAddress.trim().equals("") || endPoint == null)
					bRet = false;
				else{
					startPoint = getGeoPointFromName(startAddress);
					bRet = GDRouteSearch.getInstance(mContext, mMapView).routeSearch(startPoint, endPoint, routeType);
				}
			}
			if(!bRet){
				if(startPoint == null || endAddress == null || endAddress.trim().equals(""))
					bRet = false;
				else{
					endPoint = getGeoPointFromName(endAddress);
					bRet = GDRouteSearch.getInstance(mContext, mMapView).routeSearch(startPoint, endPoint, routeType);
				}
			}
			if(!bRet){
				if(startAddress == null || startAddress.trim().equals("") || endAddress == null || endAddress.trim().equals(""))
					bRet = false;
				else{
					startPoint = getGeoPointFromName(startAddress);
					endPoint = getGeoPointFromName(endAddress);
					bRet = GDRouteSearch.getInstance(mContext, mMapView).routeSearch(startPoint, endPoint, routeType);
				}
			}
			return bRet;
		} else {
			return false;
		}
	}

	@Override
	public void drawRoute(int routeId) {
		Log.d(TAG, "drawRoute  routeId: " + routeId);
        removeAllOverlays();
        removeAllPopViews();
        GDRouteSearch.getInstance(mContext, mMapView).drawRouteOnMap(routeId);
	}
    
	@Override
    public void showWeather(int nlatitude, int nlongitude, int ntype, String title, String desc) {
		Log.d(TAG, "ShowWeather: [" + (double)nlatitude * 1e-6 + ", " + nlongitude * 1e-6 + "] Weather: " + 
				ntype +" title: " + title + " desc: " + desc);
		removeAllPopViews();
		removeAllOverlays();
		GeoPoint point = new GeoPoint(nlatitude, nlongitude);
		mMapController.setCenter(point);
		mMapController.setZoom(13);
		View popview = GDPopView.getInstance(mContext).getWeatherPopView(ntype, title, desc);
		mMapView.addView(popview, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.CENTER));
		mPopViewList.add(popview);
	}
    
	@Override
	public void destroyMap() {
		disableAutoLocation();
		disableLocationManager();
	}

	@Override
	public void onDrag(MapView arg0, RouteOverlay arg1, int arg2, GeoPoint arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragBegin(MapView arg0, RouteOverlay arg1, int arg2,
			GeoPoint arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnd(MapView arg0, RouteOverlay arg1, int arg2,
			GeoPoint arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRouteEvent(MapView arg0, RouteOverlay arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
    
	public void enableAutoLocation() {
		if (autoLocationOverlay == null && bAutoLocationEnable) {
			autoLocationOverlay = MyLocationOverlayProxy.getInstance(mContext, mMapView);
			mMapView.getOverlays().add(autoLocationOverlay);
			autoLocationOverlay.enableMyLocation();
		} 
		if (currentPoint != null) {
			mMapController.animateTo(currentPoint);
		}
	}
	
	public Handler getHandler() {
		return handler;
	}	
    
	private void geoCoder(final double mLat, final double mLog) {
		final ProgressDialog progDialog = new ProgressDialog(mContext);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Geocoder geocoder = new Geocoder((Activity)mContext);
					List<Address> ads = geocoder.getFromLocation(mLat, mLog, GDMapConstants.GDMAP_GEOCODER_COUNT);
					if (ads != null && ads.size() > 0) {
						if(progDialog.isShowing()){
							progDialog.dismiss();    
						}
						Address ad = ads.get(0);
						StringBuilder data = new StringBuilder("{\"address\":\"");
						if (ad.getSubLocality() != null)
							data.append(ad.getSubLocality());
						if (ad.getFeatureName() != null) {
							if (ad.getSubLocality() !=null)
								data.append(',');
							data.append(ad.getFeatureName());
						}
						data.append("\","+"\"city\":\""+ad.getAdminArea() +"\"}");
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_GEOCODER_RESULT, data.toString()));
					} else {
                        if (progDialog.isShowing())
                        	progDialog.dismiss();
						Log.d(TAG,"geoCoder: [" + mLat + "," + mLog + "] result is null!");
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_GEOCODER_RESULT, "{}"));
					}
				} catch (Exception e) {
                    if (progDialog.isShowing())
                    	progDialog.dismiss();
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_ERROR));
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_GEOCODER_RESULT, "{}"));
					e.printStackTrace();
				}
			}
		});
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(mContext.getResources().getString(mContext.getResources().
				getIdentifier("gdmap_locationing", "string", mContext.getPackageName())));
		progDialog.show();
		t.start();
	}
	
	private GeoPoint getGeoPointFromName(String addr) {
		try {
			Geocoder geocoder = new Geocoder((Activity)mContext);
			List<Address> result = geocoder.getFromLocationName(addr, GDMapConstants.GDMAP_GEOCODER_COUNT);
			if (result != null && result.size() > 0) {
				Address ads = result.get(0);
				int latitude = (int)(ads.getLatitude() * 1e6);
				int longitude = (int)(ads.getLongitude() * 1e6);
				return new GeoPoint(latitude, longitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
    
	private void disableLocationManager() {
		if (locationManager != null) {
			locationManager.removeUpdates(listener);
			locationManager.destory();
			locationManager = null;
		}
	}
    
	private void disableAutoLocation() {
		if (autoLocationOverlay != null) {
			autoLocationOverlay.disableMyLocation();
			autoLocationOverlay = null;
		}
	}
	
	private void removeAllOverlays() {
        GDMapPointOverlay.getInstance(mContext, mMapView).removePointOverlay();
        GDPoiSearch.getInstance(mContext, mMapView).removeAllPOIs();
		GDRouteSearch.getInstance(mContext, mMapView).removeRouteOnMap();
	}

	private void removeAllPopViews() {
		while (mPopViewList.size() > 0) {
			View popview = mPopViewList.remove(mPopViewList.size() - 1);
			popview.setVisibility(View.GONE);
			mMapView.removeView(popview);
		}
	}
}
