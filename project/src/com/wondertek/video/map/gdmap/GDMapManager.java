package com.wondertek.video.map.gdmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
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

import com.mapabc.mapapi.core.OverlayItem;
import com.mapabc.mapapi.map.ItemizedOverlay;
import com.mapabc.mapapi.map.Projection;
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
import com.mapabc.mapapi.map.Overlay;
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
	private GDPoiOverlayEx poiOverlayEx = null;
	private GDPoiOverlayPerson poiOverlayPerson = null;
	private GDPoiOverlayBug poiOverlayBug = null;
	private GDPoiOverlayResource poiOverlayResource = null;
	private LineOverlay lineOverlay = null;
	private CircleOverlay circleOverlay = null;
	private GDPoiOverlayPersonEx poiOverlayPersonEx = null;
	private GDPoiOverlayYinhuan poiOverlayYinhuan = null;
	private GDPoiOverlayCar poiOverlayCar = null;
	private GuijihuifangOverlay poiOverlayGuiji = null;
	private OverItemT overItemT = null;
	
	private Stack<GDPoiOverlay> poiOverlaysPersonStack = new Stack<GDPoiOverlay>();
	
	Button button;
	Handler timer = new Handler();
	Runnable runnable = null;
	List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
	public int position;
	public String guijiPois;
	
	public List<View> mPopViewList = new ArrayList<View>();
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
				case GDMapConstants.GDMAP_GUIJIHUIFANG :
					Log.d(TAG, "@@@GDMAP_GUIJIHUIFANG: "+position);
					--position;
					if (position != -1) {
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
								if (poiOverlayGuiji != null) {
									//poiOverlayGuiji.removeFromMap();
									mMapView.getOverlays().remove(poiOverlayGuiji);  
									poiOverlayGuiji = null;
								}
								GeoPoint replayGeoPoint = geoPoints.get(position);
								poiOverlayGuiji = new GuijihuifangOverlay(mContext,
										mMapView, replayGeoPoint,guijiPois);
								mMapView.getOverlays().add(poiOverlayGuiji);
								mMapView.invalidate();
								timer.postDelayed(runnable, 200);

//							}
//						}).start();

					} else {
						position = geoPoints.size();
						button.setText(" 回放 ");
						geoPoints.clear();
						//timer.removeCallbacks(runnable);
					}
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
		mMapView.setVectorMap(false);
		mMapController = mMapView.getController();
        mMapController.animateTo(new GeoPoint(31848818, 117255403));
		mMapController.setZoom(7);
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
		mMapController.setZoom(12);
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
    //add pj
	public void setMapZoom(int grade) {
		mMapView.getController().setZoom(grade);
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
			
			mMapView.getController().setZoom(12);
			Drawable marker = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_icon_mis", "drawable", mContext.getPackageName()));
			Drawable markerEx = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("dingwei", "drawable", mContext.getPackageName()));
			Drawable markerpic1 = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_pic1", "drawable", mContext.getPackageName()));
			Drawable markerpicYinhuan = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_iconfire", "drawable", mContext.getPackageName()));
			Drawable markerpicCar = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_iconcar", "drawable", mContext.getPackageName()));
			Drawable markerPerson1 = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_person1", "drawable", mContext.getPackageName()));
			Drawable markerPerson2 = mContext.getResources().getDrawable(mContext.getResources().
					getIdentifier("gdmap_pois_person2", "drawable", mContext.getPackageName()));
			List<PoiItem> items = new ArrayList<PoiItem>();
			
			geoPoints.clear();
			position = -1;
			timer.removeCallbacks(runnable);
			
			
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
                String resmajor = poi.optString("resmajor");
                String  resname= poi.optString("resname");
                String resaddress = poi.optString("resaddress");
                String role = poi.optString("role");
                
                if (showType.equals("6") || showType.equals("7")){
                	break;
                }
                
                if (showType.equals("11")){
                	geoPoints.add(new GeoPoint(latitude, longitude));
                	continue;
                }
                
                PoiItem poiItem;
                if (showType.equals("3")){
            	   poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+company+";"+major+";"+name+";"+status, "");
                }else  if (showType.equals("4")){
                	poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+bugname+";"+buggrade+";"+bugstatus, "");
    			}else  if (showType.equals("5")){
                	poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+resmajor+";"+resname+";"+resaddress, "");
    			}else  if (showType.equals("8")){
             	   poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+company+";"+major+";"+name+";"+status, "");
                 }else  if (showType.equals("9")){
                 	poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+bugname+";"+buggrade+";"+bugstatus, "");
     			}else  if (showType.equals("10")){
                 	poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc+";"+name+";"+company+";"+status, "");
     			}else {
    				poiItem = new PoiItem(poiId, new GeoPoint(latitude, longitude), desc, "");
    			}
				items.add(poiItem);
				
				if (showType.equals("8")){
                	if (role.equals("1")){
    					if (items != null && items.size() > 0) {
    						poiOverlayPersonEx = new GDPoiOverlayPersonEx(markerPerson1, items);
    						poiOverlayPersonEx.addToMap(mMapView);
    						poiOverlaysPersonStack.push(poiOverlayPersonEx);
    						//poiOverlay.showPopupWindow(0);
    						mMapView.invalidate();
    					}
    					items.remove(0);
        				continue;
        			}else if (role.equals("0")){
        				if (items != null && items.size() > 0) {
    						poiOverlayPersonEx = new GDPoiOverlayPersonEx(markerPerson2, items);
    						poiOverlayPersonEx.addToMap(mMapView);
    						poiOverlaysPersonStack.push(poiOverlayPersonEx);
    						//poiOverlay.showPopupWindow(0);
    						mMapView.invalidate();
    					}
    					items.remove(0);
        				continue;
        			}
    			}
				
			}
			if (showType.equals("1")) {
				poiOverlay = new GDPoiOverlay(marker, items);
				poiOverlay.addToMap(mMapView);
			}else  if (showType.equals("2")){
				poiOverlayEx = new GDPoiOverlayEx(markerEx, items);
				poiOverlayEx.addToMap(mMapView);
			}else  if (showType.equals("3")){
				poiOverlayPerson = new GDPoiOverlayPerson(markerpic1, items);
				poiOverlayPerson.addToMap(mMapView);
			}else  if (showType.equals("4")){
				poiOverlayBug = new GDPoiOverlayBug(markerpic1, items);
				poiOverlayBug.addToMap(mMapView);
			}else  if (showType.equals("5")){
				poiOverlayResource = new GDPoiOverlayResource(markerpic1, items);
				poiOverlayResource.addToMap(mMapView);
			}else  if (showType.equals("6")){
				lineOverlay = new LineOverlay(pois);
				mMapView.getOverlays().add(lineOverlay);  
			}else  if (showType.equals("7")){
				circleOverlay = new CircleOverlay(pois);
				mMapView.getOverlays().add(circleOverlay);  
			}else  if (showType.equals("8")){
				poiOverlayPersonEx = new GDPoiOverlayPersonEx(markerpic1, items);
				poiOverlayPersonEx.addToMap(mMapView);
			}else  if (showType.equals("9")){
				poiOverlayYinhuan = new GDPoiOverlayYinhuan(markerpicYinhuan, items);
				poiOverlayYinhuan.addToMap(mMapView);
			}else  if (showType.equals("10")){
				poiOverlayCar = new GDPoiOverlayCar(markerpicCar, items);
				poiOverlayCar.addToMap(mMapView);
			}else  if (showType.equals("11")){
				//@@@轨迹回放初始化
				Log.d(TAG, "@@@轨迹回放初始化: ");
				//button = (Button) findViewById(R.id.button);
				View view = ((Activity) mContext).getLayoutInflater().inflate(mContext.getResources().
						getIdentifier("gdmap_popview_item_guijihuifang", "layout", mContext.getPackageName()), null);
			    button = (Button) view.findViewById(mContext.getResources().
						getIdentifier("gdmap_guijihuifang_button", "id", mContext.getPackageName()));
				//mMapView = (MapView) findViewById(R.id.main_mapView);
//				geoPoints.add(geoPoint1);
//				geoPoints.add(geoPoint2);
//				geoPoints.add(geoPoint3);
//				geoPoints.add(geoPoint4);
//				geoPoints.add(geoPoint5);
//				geoPoints.add(geoPoint6);
//				geoPoints.add(geoPoint7);
//				geoPoints.add(geoPoint8);

				position = geoPoints.size();
				guijiPois = pois;
				//Drawable marker = getResources().getDrawable(R.drawable.da_marker_red); // 得到需要标在地图上的资源
				Drawable guijiMarker = mContext.getResources().getDrawable(mContext.getResources().
						getIdentifier("gdmap_marker_red", "drawable", mContext.getPackageName()));
				guijiMarker.setBounds(0, 0, guijiMarker.getIntrinsicWidth(),
						guijiMarker.getIntrinsicHeight()); // 为maker定义位置和边界
				overItemT=new OverItemT(guijiMarker, this,pois);
				mMapView.getOverlays().add(overItemT); // 添加ItemizedOverlay实例到mMapView
				poiOverlayGuiji = new GuijihuifangOverlay(mContext, mMapView,
						geoPoints.get(--position),pois);
				mMapView.getOverlays().add(poiOverlayGuiji);
				runnable = new Runnable() {
					@Override
					public void run() {
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_GUIJIHUIFANG ));
					}
				};
				new Thread(runnable).start();
				
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					    if (button.getText().toString().trim().equals("回放")) {
							if (position > -1) {
								button.setText(" 停止 ");
								timer.postDelayed(runnable, 10);
							}
						} else {
							timer.removeCallbacks(runnable);
							button.setText(" 回放 ");
						}
					}
				});
				
				//if (button.getText().toString().trim().equals("回放")) {
//					if (position > -1) {
//						//button.setText(" 停止 ");
//						timer.postDelayed(runnable, 10);
//					}
				//} else {
				//	timer.removeCallbacks(runnable);
				//	button.setText(" 回放 ");
				//}
			}else {
				poiOverlayEx = new GDPoiOverlayEx(markerEx, items);
				poiOverlayEx.addToMap(mMapView);
			}
			//poiOverlay.addToMap(mMapView);
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
                        progDialog.dismiss();
						Log.d(TAG,"geoCoder: [" + mLat + "," + mLog + "] result is null!");
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_GEOCODER_RESULT, "{}"));
					}
				} catch (Exception e) {
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
		if (poiOverlay != null) {
			//poiOverlay.removeFromMap();
			mMapView.getOverlays().remove(poiOverlay);  
			poiOverlay = null;
		}
		if (poiOverlayEx != null) {
			//poiOverlayEx.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayEx);  
			poiOverlayEx = null;
		}
		if (poiOverlayPerson != null) {
			//poiOverlayPerson.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayPerson);  
			poiOverlayPerson = null;
		}
		if (poiOverlayBug != null) {
			//poiOverlayBug.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayBug);  
			poiOverlayBug = null;
		}
		if (poiOverlayResource != null) {
			//poiOverlayResource.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayResource);  
			poiOverlayResource = null;
		}
		if (lineOverlay != null) {
			mMapView.getOverlays().remove(lineOverlay);  
			lineOverlay = null;
		}
		if (circleOverlay != null) {
			mMapView.getOverlays().remove(circleOverlay);  
			circleOverlay = null;
		}
		if (poiOverlayPersonEx != null) {
			//poiOverlayPersonEx.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayPersonEx);  
			poiOverlayPersonEx = null;
		}
		if (poiOverlayYinhuan != null) {
			//poiOverlayYinhuan.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayYinhuan);  
			poiOverlayYinhuan = null;
		}
		if (poiOverlayCar != null) {
			//poiOverlayCar.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayCar);  
			poiOverlayCar = null;
		}
		if (poiOverlayGuiji != null) {
			//poiOverlayGuiji.removeFromMap();
			mMapView.getOverlays().remove(poiOverlayGuiji);  
			poiOverlayGuiji = null;
		}
		if (overItemT != null) {
			//overItemT.removeFromMap();
			mMapView.getOverlays().remove(overItemT);  
			overItemT = null;
		}
		while (!poiOverlaysPersonStack.empty()) {
			GDPoiOverlay ol = poiOverlaysPersonStack.pop();
            mMapView.getOverlays().remove(ol);
		}
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
			mPopViewList.add(view);
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
	 * 自定义线覆盖物
	 */
	public class LineOverlay extends Overlay {
			private String pois;
	
			private LineOverlay( String pois) {
				this.pois = pois;
			}
		  
	      @Override
	      public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	        super.draw(canvas, mapView, shadow);
	        //Log.d(TAG, "@@@draw=== pois: " + pois);
	        JSONObject jsObject;
	        String showType;
	        JSONArray poisArray = null;
	        try {
			     jsObject = new JSONObject(pois);
				 showType = jsObject.optString("showType");
				 poisArray = jsObject.getJSONArray("pois");
//				Log.d(TAG, " @@@length: "  +poisArray.length());
//				Log.d(TAG, " @@@showType: "  +showType);
			} catch (JSONException e) {
				Log.d(TAG, "draw " + e.getMessage());
			}
			for (int i = 0; i < poisArray.length()-1; ++i) {
				JSONObject poi = null;
				JSONObject poiNext = null;
				try {
					poi = poisArray.getJSONObject(i);
					poiNext = poisArray.getJSONObject(i+1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int longitude = poi.optInt("longitude");
				int latitude = poi.optInt("latitude");
//				Log.d(TAG, " @@@latitude: "  +latitude);
//				Log.d(TAG, " @@@longitude: "  +longitude);
				int longitudeNext = poiNext.optInt("longitude");
				int latitudeNext = poiNext.optInt("latitude");
//				Log.d(TAG, " @@@latitudeNext: "  +latitudeNext);
//				Log.d(TAG, " @@@longitudeNext: "  +longitudeNext);
			    GeoPoint startGeo = new GeoPoint (latitude, longitude);
		        GeoPoint endGeo = new GeoPoint (latitudeNext, longitudeNext);
		        Point startPoint = new Point();
		        Point endPoint = new Point();
		        mapView.getProjection().toPixels(startGeo, startPoint);
		        mapView.getProjection().toPixels(endGeo, endPoint);
		        Paint paintLine = new Paint();
		        paintLine.setColor(Color.RED);
		        paintLine.setStrokeWidth(5.0f);
		        paintLine.setStyle(Paint.Style.STROKE);
		        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
		              paintLine);
			}
	      }

	      @Override
	      public boolean onTap(GeoPoint arg0, MapView arg1) {
	        return super.onTap(arg0, arg1);
	      }
	   }
	
	/**
	 * 自定义圆覆盖物
	 */
	public class CircleOverlay extends Overlay {
			private String pois;
	
			private CircleOverlay( String pois) {
				this.pois = pois;
			}
		  
	      @Override
	      public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	        super.draw(canvas, mapView, shadow);
	        //Log.d(TAG, "@@@draw=== pois: " + pois);
	        JSONObject jsObject;
	        String showType;
	        JSONArray poisArray = null;
	        try {
			     jsObject = new JSONObject(pois);
				 showType = jsObject.optString("showType");
				 poisArray = jsObject.getJSONArray("pois");
				//Log.d(TAG, " @@@showType: "  +showType);
			} catch (JSONException e) {
				Log.d(TAG, "draw " + e.getMessage());
			}
			for (int i = 0; i < poisArray.length(); ++i) {
				JSONObject poi = null;
				JSONObject poiNext = null;
				try {
					poi = poisArray.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int longitude = poi.optInt("longitude");
				int latitude = poi.optInt("latitude");
				int radius = poi.optInt("radius");
//				Log.d(TAG, " @@@latitude: "  +latitude);
//				Log.d(TAG, " @@@longitude: "  +longitude);
//				Log.d(TAG, " @@@radius: "  +radius);
			    GeoPoint startGeo = new GeoPoint (latitude, longitude);
		        Point startPoint = new Point();
		        mapView.getProjection().toPixels(startGeo, startPoint);
		        Paint mCirclePaint = new Paint();
				 mCirclePaint.setAntiAlias(true);
				 mCirclePaint.setColor(Color.BLUE);
				 mCirclePaint.setAlpha(50);
				 mCirclePaint.setStyle(Style.FILL);
				 canvas.drawCircle(startPoint.x, startPoint.y, radius, mCirclePaint);
			}
	      }

	      @Override
	      public boolean onTap(GeoPoint arg0, MapView arg1) {
	        return super.onTap(arg0, arg1);
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
			mPopViewList.add(view);
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
			mPopViewList.add(view);
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
	 * 扩展的Poi点Overlay，支持查看人员(Ex版)。
	 */
	class GDPoiOverlayPersonEx extends GDPoiOverlay {
		public GDPoiOverlayPersonEx(Drawable marker, List<PoiItem> items) {
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
					getIdentifier("gdmap_popview_item_person_ex", "layout", mContext.getPackageName()), null);
			mPopViewList.add(view);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname1_Ex", "id", mContext.getPackageName()));
			title1.setText(strs[3]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname2_Ex", "id", mContext.getPackageName()));
			title2.setText(strs[4]);
			final TextView title3 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personname3_Ex", "id", mContext.getPackageName()));
			title3.setText(strs[1]+"-"+strs[2]);
			
			// 设置左侧导航按钮的点击消息处理
			final ImageButton btn_left= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_personbtn_Ex", "id", mContext.getPackageName()));
			btn_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_personbtn_Ex+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Person");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_guijibtn_Ex", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_guijibtn_Ex+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Guiji");
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
			mPopViewList.add(view);
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
	
	/**
	 * 扩展的Poi点Overlay，支持查看资源。
	 */
	class GDPoiOverlayResource extends GDPoiOverlay {
		public GDPoiOverlayResource(Drawable marker, List<PoiItem> items) {
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
					getIdentifier("gdmap_popview_item_resource", "layout", mContext.getPackageName()), null);
			mPopViewList.add(view);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_resname1", "id", mContext.getPackageName()));
			title1.setText(strs[1]+"-"+strs[2]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_resname2", "id", mContext.getPackageName()));
			title2.setText(strs[3]);
			
			// 设置右侧详情按钮的消息响应
			ImageButton btn_right = (ImageButton)view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_resbtn", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_resbtn1+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Resource");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			return view;
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持查看隐患。
	 */
	class GDPoiOverlayYinhuan extends GDPoiOverlay {
		public GDPoiOverlayYinhuan(Drawable marker, List<PoiItem> items) {
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
					getIdentifier("gdmap_popview_item_yinhuan", "layout", mContext.getPackageName()), null);
			mPopViewList.add(view);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_yinhuanname1", "id", mContext.getPackageName()));
			title1.setText(strs[1]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_yinhuanname2", "id", mContext.getPackageName()));
			title2.setText(strs[2]);
			final TextView title3 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_yinhuanname3", "id", mContext.getPackageName()));
			title3.setText(strs[3]);
			
			// 设置左侧导航按钮的点击消息处理
			final ImageButton btn_left= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_yinhuanbtn", "id", mContext.getPackageName()));
			btn_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_yinhuanbtn+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Yinhuan");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_zhoubianbtn", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_zhoubianbtn+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "Zhoubian");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			return view;
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持查看车辆。
	 */
	class GDPoiOverlayCar extends GDPoiOverlay {
		public GDPoiOverlayCar(Drawable marker, List<PoiItem> items) {
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
					getIdentifier("gdmap_popview_item_car", "layout", mContext.getPackageName()), null);
			mPopViewList.add(view);
			final TextView title1 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_carname1", "id", mContext.getPackageName()));
			title1.setText(strs[1]);
			final TextView title2 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_carname2", "id", mContext.getPackageName()));
			title2.setText(strs[2]);
			final TextView title3 = (TextView) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_carname3", "id", mContext.getPackageName()));
			title3.setText(strs[3]);
			
			// 设置右侧导航按钮的点击消息处理
			final ImageButton btn_right= (ImageButton) view.findViewById(mContext.getResources().
					getIdentifier("gdmap_item_carbtn", "id", mContext.getPackageName()));
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, ">>>+++++gdmap_item_carbtn+++++<<<");
					String str = String.format("{\"PoiId\":\"%s\", \"cmd\":\"%s\"}", item.getPoiId(), "CarGuiji");
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIPRESSED, str));
				}
			});
			
			return view;
		}
	}
	
	/**
	 * 扩展的Poi点Overlay，支持轨迹回放。
	 */
	public class GuijihuifangOverlay extends Overlay {
		private Context context;
		private MapView mapView;
		private GeoPoint geoPoint;
		private String pois;
		
		public GuijihuifangOverlay(Context context, MapView mapView, GeoPoint geoPoint,  String pois) {
			this.context = context;
			this.mapView = mapView;
			this.geoPoint = geoPoint;
			this.pois = pois;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			Point screenPts = new Point();
			this.mapView.getProjection().toPixels(geoPoint, screenPts);
			Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
					mContext.getResources().getIdentifier("gdmap_switch_icon", "drawable", mContext.getPackageName()));
			Log.d(TAG, ">>>++bmp+++1435+++++<<<==="+mContext.getResources().getIdentifier("gdmap_switch_icon", "drawable", mContext.getPackageName()));
//			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
//					mContext.getResources().getIdentifier("gdmap_switch_icon", "id", mContext.getPackageName()));
			
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
		}
	}
	
	/**
	 * OverItemT类。
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
		private Drawable marker;

		public OverItemT(Drawable marker, GDMapManager gdMapManager,String pois) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			
			try {
				JSONObject jsObject = new JSONObject(pois);
				String showType = jsObject.optString("showType");
				JSONArray poisArray = jsObject.getJSONArray("pois");
				//Log.d(TAG, " @@@length: "  +poisArray.length());
				//Log.d(TAG, " @@@showType: "  +showType);
				
				
				
				for (int i = 0; i < poisArray.length(); ++i) {
					JSONObject poi = poisArray.getJSONObject(i);
					int longitude = poi.optInt("longitude");
					int latitude = poi.optInt("latitude");
	                GeoList.add(new OverlayItem(new GeoPoint(latitude, longitude), "P1", "point1"));
				}
			} catch (JSONException e) {
				Log.d(TAG, "OverItemT " + e.getMessage());
			}
			
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			drawPolygon(canvas, mapView.getProjection());
			//boundCenterBottom(marker);
			boundCenter(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			return GeoList.get(i);
		}

		@Override
		public int size() {
			return GeoList.size();
		}

		private void drawPolygon(Canvas canvas, Projection projection) {
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setStyle(Paint.Style.STROKE);// 设置为空心
			p.setStrokeWidth(3);
			p.setColor(Color.RED);// 设置背景为红色
			Path path = new Path();
			for (int index = 0; index < size(); index++) {// 遍历GeoList
				OverlayItem overLayItem = getItem(index); // 得到给定索引的item
				Point point = projection.toPixels(overLayItem.getPoint(), null);

				if (index == 0) {
					path.moveTo(point.x, point.y);// 此点为多边形的起点
				} else {
					path.lineTo(point.x, point.y);
				}
			}
			canvas.drawPath(path, p);
		}
	}
	
}
