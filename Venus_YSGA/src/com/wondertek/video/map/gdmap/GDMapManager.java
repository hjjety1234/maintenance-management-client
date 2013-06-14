package com.wondertek.video.map.gdmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.core.ServerUrlSetting;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.location.LocationProviderProxy;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.PoiOverlay;
import com.mapabc.mapapi.map.RouteMessageHandler;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.poisearch.PoiTypeDef;
import com.wondertek.video.Util;
import com.wondertek.video.map.IMapPlugin;
import com.wondertek.video.map.MapPluginMgr;
import com.wondertek.video.map.gdmap.GDPoiSearch.GDPoiOverlay;

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
	private GDPoiOverlay poiOverlay = null;
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
				case GDMapConstants.GDMAP_POIPRESSED :
					Log.d(TAG, "POI PopView Pressed: " + (String)msg.obj);
                    MapPluginMgr.getInstance().nativePoiPopViewPressedCallback((String)msg.obj);
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
			int geoLat = (int) (location.getLatitude() * 1e6);
			int geoLog = (int) (location.getLongitude() * 1e6);
			Bundle localBundle = location.getExtras();
			String desc = "0";
			if (localBundle != null) {
				desc = localBundle.getString("desc");
				Log.d(TAG, "cmzAddress" + desc);
			}
			String str = "{\"latitude\":\"" + geoLat + "\",\"longitude\":\""
					+ geoLog + "\",\"desc\":\"" + desc + "\"}";
			Log.d(TAG, "cmzAddress" + str);
			Message msg = handler.obtainMessage();
			msg.what = GDMapConstants.GDMAP_LOCATION_FINISHED;
			msg.obj = str;
			handler.sendMessage(msg);
		    locationManager.removeUpdates(listener);
		}
	};
    
    private GDMapManager(Context cxt) {
		Log.d(TAG, ">>>GDMapManager<<<");
		mContext = cxt;
		mMapView = new MapView(mContext, GDMapConstants.GDMAP_API_KEYS);
		mMapView.setLayoutParams(new AbsoluteLayout.LayoutParams(0,0,0,0));
		mMapView.setBuiltInZoomControls(true);
		mMapView.setVectorMap(false);
		mMapController = mMapView.getController();
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
		//Log.d(TAG, "@@@showPoisPopView  pois: " + pois + " RemoveCover: " + bRemoveCover);
		try {
			JSONObject jsObject = new JSONObject(pois);
			String showType = jsObject.optString("showType");
			JSONArray poisArray = jsObject.getJSONArray("pois");
			//Log.d(TAG, " @@@length: "  +poisArray.length());
			//Log.d(TAG, " @@@showType: "  +showType);
			if (bRemoveCover ==  true) {
				removeAllPopViews();
				removeAllOverlays();
			}
			
			mMapView.getController().setZoom(13);
			Drawable marker = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_icon_mis", "drawable", mContext.getPackageName()));
			Drawable markerEx = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("dingwei", "drawable", mContext.getPackageName()));
			Drawable markerpic1 = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_pic1", "drawable", mContext.getPackageName()));
			List<PoiItem> items = new ArrayList<PoiItem>();
			for (int i = 0; i < poisArray.length(); ++i) {
				JSONObject poi = poisArray.getJSONObject(i);
				int longitude = poi.optInt("longitude");
				int latitude = poi.optInt("latitude");
				String desc = poi.optString("desc");
                String poiId = poi.optString("id");
                String company = poi.optString("company");
                String major = poi.optString("major");
                String  name= poi.optString("name");
                String status = poi.optString("status");
                String bugname = poi.optString("bugname");
                String  buggrade= poi.optString("buggrade");
                String bugstatus = poi.optString("bugstatus");
               //Log.d(TAG, " @@@company: "  +company);
               // Log.d(TAG, " @@@name: "  +name);
               // Log.d(TAG, " @@@bugname: "  +bugname);
               // Log.d(TAG, " @@@buggrade: "  +buggrade);
                PoiItem poiItem;
                if (showType.equals("3")){
            	   poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+company+";"+major+";"+name+";"+status, "");
                }else  if (showType.equals("4")){
                	poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+bugname+";"+buggrade+";"+bugstatus, "");
    			}else {
    				poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc, "");
    			}
				items.add(poiItem);
			}
			if (showType.equals("1")) {
				poiOverlay = new GDPoiOverlay(marker, items);
			}else  if (showType.equals("2")){
				poiOverlay = new GDPoiOverlayEx(markerEx, items);
			}else  if (showType.equals("3")){
				poiOverlay = new GDPoiOverlayPerson(markerpic1, items);
			}else  if (showType.equals("4")){
				poiOverlay = new GDPoiOverlayBug(markerpic1, items);
			}else {
				poiOverlay = new GDPoiOverlayEx(markerEx, items);
			}
			poiOverlay.addToMap(mMapView);
			mMapView.invalidate();
		} catch (JSONException e) {
			Log.d(TAG, "showPoisPopView " + e.getMessage());
		}
		
	}

	@Override
	public void poiSearch(String key, int latitude, int longitude, int radius) {
		Log.d(TAG, "poiSearch  key: " + key + " latitude: " + latitude + " longitude: " + longitude + " radius: " + radius);
		if (!key.trim().equals("")) {
			removeAllOverlays();
            removeAllPopViews();
			GDPoiSearch poiSearch = GDPoiSearch.getInstance(mContext, mMapView);
			GeoPoint point = new GeoPoint(latitude, longitude);
			poiSearch.search(key, "", point, radius);
		}
	}

	@Override
	public void poiSearch(String key, String city) {
		Log.d(TAG, "poiSearch cityName: " + city + " key: " + key);
        if (!key.trim().equals("")) {
        	removeAllOverlays();
        	removeAllPopViews();
        	GDPoiSearch poiSearch = GDPoiSearch.getInstance(mContext, mMapView);
        	poiSearch.search(key, PoiTypeDef.All, city);
        }
	}

	@Override
	public void geoCode(int latitude, int longitude) {
		double tempLatitude = (double) latitude / 1000000;
		Log.d(TAG, "cmzcheng" + tempLatitude);
		if (tempLatitude > 180) {
			Log.d(TAG, "GeoCode7: [" + (double) latitude * 1e-7 + ", "
					+ (double) longitude * 1e-7 + "]");
			geoCoder((double) latitude / 10000000,
					(double) longitude / 10000000);

		} else {
			Log.d(TAG, "GeoCode6: [" + (double) latitude * 1e-6 + ", "
					+ (double) longitude * 1e-6 + "]");
			geoCoder((double) latitude / 1000000, (double) longitude / 1000000);
		}
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
					Geocoder geocoder = new Geocoder((Activity) mContext,
							GDMapConstants.GDMAP_API_KEYS);
					Log.d(TAG, "cmzAddress:new geocoder method ");
					List<Address> ads = geocoder.getFromRawGpsLocation(mLat,
							mLog, GDMapConstants.GDMAP_GEOCODER_COUNT);
					if (ads != null && ads.size() > 0) {
						if (progDialog.isShowing()) {
							progDialog.dismiss();
						}
						for (Address address : ads) {
							Log.d(TAG, "" + address.getFeatureName());
						}
						StringBuilder data = new StringBuilder(
								"{\"address\":\"");
						Address addres = ads.get(1);
						data.append(addres.getAdminArea() + " ");
						if (addres.getLocality() != null) {
							data.append(addres.getLocality() + " ");
						}
						if (addres.getSubLocality() != null) {
							data.append(addres.getSubLocality() + " ");
							if (ads.get(2).getFeatureName() != null)
								data.append(ads.get(2).getFeatureName() + " ");
						}
						data.append("����" + addres.getFeatureName() + "\"}");
						Log.d(TAG, "" + data);
						handler.sendMessage(Message.obtain(handler,
								GDMapConstants.GDMAP_GEOCODER_RESULT,
								data.toString()));
					} else {
						if (progDialog.isShowing())
							progDialog.dismiss();
						Log.d(TAG, "geoCoder: [" + mLat + "," + mLog
								+ "] result is null!");
						handler.sendMessage(Message.obtain(handler,
								GDMapConstants.GDMAP_GEOCODER_RESULT, "{}"));
					}
				} catch (Exception e) {
					if (progDialog.isShowing())
						progDialog.dismiss();
					handler.sendMessage(Message.obtain(handler,
							GDMapConstants.GDMAP_ERROR));
					handler.sendMessage(Message.obtain(handler,
							GDMapConstants.GDMAP_GEOCODER_RESULT, "{}"));
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
//		if (poiOverlay != null) {
//			poiOverlay.removeFromMap();
//			poiOverlay = null;
//		}
	}

	private void removeAllPopViews() {
		while (mPopViewList.size() > 0) {
			View popview = mPopViewList.remove(mPopViewList.size() - 1);
			popview.setVisibility(View.GONE);
			mMapView.removeView(popview);
		}
	}
	
	/**
	 * 基本的Poi点Overlay
	 */
	class GDPoiOverlay extends PoiOverlay {
		private Drawable marker;
		public GDPoiOverlay(Drawable marker, List<PoiItem> items) {
			super(marker, items);
			this.marker = marker;
			this.populate();
		}
		
		@Override
		protected LayoutParams getLayoutParam(int index) {
			PoiItem poi = getItem(index);
			int mHeight = marker.getIntrinsicHeight();
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					poi.getPoint(), 0, -mHeight, LayoutParams.BOTTOM_CENTER);
			return params;
		}
		
		@Override
		protected Drawable getPopupBackground() {
			return mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("wd_tip_pointer_button", "drawable", mContext.getPackageName()));
		}
		
		@Override
		protected Drawable getPopupMarker(PoiItem item) {
			return super.getPopupMarker(item);
		}
		
		/**
		 * 获取POI点的view
		 */
		@Override
		protected View getPopupView(final PoiItem item) {
			View view = ((Activity) mContext).getLayoutInflater().inflate(mContext.getResources().
					getIdentifier("gdmap_popview_item", "layout", mContext.getPackageName()), null);
			final TextView title = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("item_name", "id", mContext.getPackageName()));
			title.setText(item.getTitle());
			return view;
		}

		@Override
		protected boolean onTap(int index) {
			Log.d(TAG, ">>>onTap<<<" + " index: " + index);
			return super.onTap(index);
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持查看POI详情和导航功能。
	 */
	class GDPoiOverlayEx extends GDPoiOverlay {
		public GDPoiOverlayEx(Drawable marker, List<PoiItem> items) {
			super(marker, items);
		}

		@Override
		protected Drawable getPopupBackground() {
			return mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_qp_bg", "drawable", mContext.getPackageName()));
		}

		@Override
		protected Drawable getPopupMarker(PoiItem item) {
			return super.getPopupMarker(item);
		}
		
		/**
		 * 获取POI点的view
		 */
		@Override
		protected View getPopupView(final PoiItem item) {
			View view = ((Activity) mContext).getLayoutInflater().inflate(mContext.getResources().
					getIdentifier("gdmap_popview_item_ex", "layout", mContext.getPackageName()), null);
			final TextView title = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("item_name_ex", "id", mContext.getPackageName()));
			title.setText(item.getTitle());
//			final Button detail = (Button) view.findViewById(mContext.getResources().
//					getIdentifier("item_detail_btn", "id", mContext.getPackageName()));
//			detail.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					getPopupView(item);
//					detail.setText("");
//					Log.d(TAG, ">>>onClick<<<");
//					StringBuilder sb = new StringBuilder();
//					sb.append("{\"latitude\":\"" +item.getPoint().getLatitudeE6()+"\",");
//					sb.append("\"longitude\":\"" +item.getPoint().getLongitudeE6()+"\",");
//					sb.append("\"name\":\"" +item.getTitle()+"\",");
//					sb.append("\"address\":\"" +item.getSnippet()+"\",");
//					sb.append("\"tel\":\"" +item.getTel()+"\"}");
//					Log.d(TAG, sb.toString());
//					title.setText(item.getTitle()+"\n"+item.getSnippet()+"\n"+item.getTel());
//					Handler handler = GDMapManager.getInstance(mContext).getHandler();
//					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIDETAIL, sb.toString()));
//				}
//			});
			
			// 设置左侧详情按钮的消息响应
			ImageButton btnLeft = (ImageButton)view.findViewById(mContext.getResources().
					getIdentifier("btn_left", "id", mContext.getPackageName()));
			btnLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++PoIDetail+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "PoIDetail");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("btn_right", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++RouteSearch+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "RouteSearch");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			// 设置整个PoiView的点击消息处理
			/*
            view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, item.getPoiId()));
				}
			});
			*/
			return view;
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持查看人员。
	 */
	class GDPoiOverlayPerson extends GDPoiOverlay {
		public GDPoiOverlayPerson(Drawable marker, List<PoiItem> items) {
			super(marker, items);
		}

		@Override
		protected Drawable getPopupBackground() {
			return mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_qp_bg", "drawable", mContext.getPackageName()));
		}

		@Override
		protected Drawable getPopupMarker(PoiItem item) {
			return super.getPopupMarker(item);
		}
		
		/**
		 * 获取POI点的view
		 */
		@Override
		protected View getPopupView(final PoiItem item) {
			String []strs = item.getTitle().split(";");
			View view = ((Activity) mContext).getLayoutInflater().inflate(mContext.getResources().
					getIdentifier("gdmap_popview_item_person", "layout", mContext.getPackageName()), null);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname1", "id", mContext.getPackageName()));
			title1.setText(strs[1]+"-"+strs[2]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname2", "id", mContext.getPackageName()));
			title2.setText(strs[3]);
			final TextView title3 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname3", "id", mContext.getPackageName()));
			title3.setText(strs[4]);
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personbtn", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_personbtn+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Person");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			return view;
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持查看故障。
	 */
	class GDPoiOverlayBug extends GDPoiOverlay {
		public GDPoiOverlayBug(Drawable marker, List<PoiItem> items) {
			super(marker, items);
		}

		@Override
		protected Drawable getPopupBackground() {
			return mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_qp_bg", "drawable", mContext.getPackageName()));
		}

		@Override
		protected Drawable getPopupMarker(PoiItem item) {
			return super.getPopupMarker(item);
		}
		
		/**
		 * 获取POI点的view
		 */
		@Override
		protected View getPopupView(final PoiItem item) {
			String []strs = item.getTitle().split(";");
			View view = ((Activity) mContext).getLayoutInflater().inflate(mContext.getResources().
					getIdentifier("gdmap_popview_item_bug", "layout", mContext.getPackageName()), null);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_bugname1", "id", mContext.getPackageName()));
			title1.setText(strs[1]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_bugname2", "id", mContext.getPackageName()));
			title2.setText(strs[2]);
			final TextView title3 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_bugname3", "id", mContext.getPackageName()));
			title3.setText(strs[3]);
			
			// 设置左侧详情按钮的消息响应
			ImageButton btnLeft = (ImageButton)view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_bugbtn1", "id", mContext.getPackageName()));
			btnLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_bugbtn1+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Bug");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_bugbtn2", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_bugbtn2+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Person");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			return view;
		}
	}
}
