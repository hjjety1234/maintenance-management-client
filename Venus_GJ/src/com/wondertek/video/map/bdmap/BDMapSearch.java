package com.wondertek.video.map.bdmap;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author yuhongwei
 *
 */
public class BDMapSearch {
	private static final String TAG = "BDPoiSearch";
	private static BDMapSearch instance = null;
	private Context mContext;
    private BMapManager mBMapMgr;
    
    private BDMapSearch(Context cxt, BMapManager bm) {
    	mContext = cxt;
    	mBMapMgr = bm;
    }
    
    public static BDMapSearch getInstance(Context cxt, BMapManager bm) {
    	if (instance == null) {
    		instance = new BDMapSearch(cxt, bm);
    	}
    	return instance;
    }
    
    public MKSearch getMKSeMkSearch() {
    	MKSearch search = new MKSearch();
    	search.init(mBMapMgr, new MKSearchListener() {
			
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult result, int error) {
                Log.d(TAG, "onGetWalkingRouteResult");
				if (error == 0) {
                    if (mContext instanceof Activity) {
                    	MapView mMapView = (MapView)BDMapManager.getInstance().getMapView();
                    	RouteOverlay ro = new RouteOverlay((Activity)mContext, mMapView);
                        ro.setData(result.getPlan(0).getRoute(0));
                    	mMapView.getOverlays().clear();
                    	mMapView.getOverlays().add(ro);
                    	mMapView.invalidate();
                    	mMapView.getController().animateTo(result.getStart().pt);
                    }
					
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
							"bdmap_route_search_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
				}
			}
			
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult result, int error) {
                Log.d(TAG, "onGetTransitRouteResult");
				if (error == 0) {
                    if (mContext instanceof Activity) {
                    	MapView mMapView = (MapView)BDMapManager.getInstance().getMapView();
                        TransitOverlay to = new TransitOverlay((Activity)mContext, mMapView);
                        to.setData(result.getPlan(0));
                    	mMapView.getOverlays().clear();
                    	mMapView.getOverlays().add(to);
                    	mMapView.invalidate();
                    	mMapView.getController().animateTo(result.getStart().pt);
                    }
					
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
							"bdmap_route_search_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
				}
				
			}
			
			@Override
			public void onGetPoiResult(MKPoiResult result, int type, int error) {
                Log.d(TAG, "onGetPoiResult");
                Handler handler = BDMapManager.getInstance().getHandler();
				if (error == 0) {
                    if (mContext instanceof Activity) {
                    	ArrayList<MKPoiResult> res = result.getMultiPoiResult();
                    	if (res == null) {
                    		ArrayList<MKPoiInfo> pois = result.getAllPoi();
                    		StringBuilder sb = new StringBuilder("{");
                    		if (pois != null && pois.size() > 0) {
                    			sb.append("\"data1\":[");
                    			for (int i = 0; i < pois.size(); i++) {
                    				MKPoiInfo poi = pois.get(i);
                    				sb.append("{\"latitude\":\"" +poi.pt.getLatitudeE6()+"\", ");
                    				sb.append("\"longitude\":\"" +poi.pt.getLongitudeE6()+"\",");
                    				sb.append("\"name\":\"" +poi.name+"\",");
                                    sb.append("\"address\":\"" +poi.address+"\",");
                                    sb.append("\"phoneNum\":\"" +poi.phoneNum+"\"}");
                                    if ( i < pois.size() - 1) {
                                    	sb.append(",");
                                    }
                    			}
                    			sb.append("]");
                    		}
                    		sb.append("}");
                            handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_POISEARCH_RESULT, sb.toString()));
                    	} else {
                    		if (res.size() > 0) {
                    			StringBuilder sb = new StringBuilder("{");
                    			for (int i = 0; i < res.size(); i++) {
                    				ArrayList<MKPoiInfo> pois = res.get(i).getAllPoi();
                    				sb.append("\"data"+ i+"\":[");
                                    for (int j = 0; j < pois.size(); j++) {
                                    	MKPoiInfo poi = pois.get(j);
                                    	sb.append("{\"latitude\":\"" +poi.pt.getLatitudeE6()+"\", ");
                        				sb.append("\"longitude\":\"" +poi.pt.getLongitudeE6()+"\",");
                        				sb.append("\"name\":\"" +poi.name+"\",");
                                        sb.append("\"address\":\"" +poi.address+"\",");
                                        sb.append("\"phoneNum\":\"" +poi.phoneNum+"\"}");
                                        if ( j < pois.size() - 1) {
                                        	sb.append(",");
                                        }
                                    }
                                    sb.append("]");
                                    if (i < res.size() - 1) {
                                    	sb.append(",");
                                    }
                    			}
                    			sb.append("}");
                    			handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_POISEARCH_RESULT, sb.toString()));
                    		}
                    		handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_POISEARCH_RESULT, "{}"));
                    	}
                    }
				} else {
                    handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_POISEARCH_RESULT, "{}"));
					Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
							"bdmap_poi_search_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
				}
			}
			
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult result, int error) {
                Log.d(TAG, "onGetDrivingRouteResult");
				if (error == 0) {
					if (mContext instanceof Activity) {
                        MapView mMapView = (MapView)BDMapManager.getInstance().getMapView();
						RouteOverlay ro = new RouteOverlay((Activity)mContext, mMapView);
                        ro.setData(result.getPlan(0).getRoute(0));
						mMapView.getOverlays().clear();
						mMapView.getOverlays().add(ro);
						mMapView.invalidate();
						mMapView.getController().animateTo(result.getStart().pt);
					}
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
							"bdmap_route_search_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
				}
			}
			
			@Override
			public void onGetAddrResult(MKAddrInfo result, int error) {
                Log.d(TAG, "onGetAddrResult");
                Handler handler = BDMapManager.getInstance().getHandler();
				if (error == 0) {
					if (result != null) {
						String strResult ="{\"address\":\""+result.strAddr +"\","+"\"city\":\""+result.addressComponents.city +"\"}";
						handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_GEOCODER_RESULT, strResult));
					}
				} else {
					handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_GEOCODER_RESULT, "{}"));
                    if (error == 100) {
                    	Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
                    			"bdmap_geocode_reverse_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
                    } else {
                    	Toast.makeText(mContext, mContext.getResources().getString(mContext.getResources().getIdentifier(
                    			"bdmap_geocode_reverse_error", "string", mContext.getPackageName())), Toast.LENGTH_LONG).show();
                    }
				}
				
			}
		});
        
    	return search;
    }
    
}
