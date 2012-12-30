package com.wondertek.plugins.mapview.gdmap;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.poisearch.PoiPagedResult;
import com.mapabc.mapapi.poisearch.PoiSearch;
import com.mapabc.mapapi.poisearch.PoiSearch.Query;
import com.mapabc.mapapi.poisearch.PoiTypeDef;
import com.mapabc.mapapi.route.Route;

public class MyRoute {

	private static String TAG_GD = "Gaode";
	private ProgressDialog progDialog = null;
	private Activity venusActivity;
	private MapController mMapController = null;
	private int mode = Route.DrivingDefault;
	private List<Route> mRouteResult = null;
	private String mStrStart = null;
	private String mStrEnd = null;
	private GeoPoint mStartPoint = null;
	private GeoPoint mEndPoint = null;
	private String mStartCity = "";
	private String mEndCity = "";
	private PoiPagedResult mStartSearchResult = null;
	private PoiPagedResult mEndSearchResult = null;
	private RouteOverlay mRouteOverlay = null;
	private Handler mHandler;
	public int mflag = -1;
	public MapView mMapView;

	private static MyRoute sInstance = null;

	public static MyRoute getInstance(Activity appActivity,
			MapController mapController,MapView mapView,Handler handler) {
		if (sInstance == null) {
			sInstance = new MyRoute(appActivity, mapController, mapView, handler);
		}
		return sInstance;
	}

	public static MyRoute getInstance() {
		return sInstance;
	}

	public MyRoute(Activity activity, MapController controller,MapView mapView,
			Handler handler) {
		venusActivity = activity;
		mMapController = controller;
		mMapView = mapView;
		mHandler = handler;
	}
	

	public void setMode(int routeType)
	{
		if(routeType == Constants.SearchType_Driving)
			mode = Route.DrivingDefault;
		else if(routeType == Constants.SearchType_Bus)
			mode = Route.BusDefault;
		else if(routeType == Constants.SearchType_Walking)
			mode = Route.DrivingDefault;
	}

	public boolean searchRouteResult(String strStart, String startCity,
			String strEnd, String endCity) {
		if(strStart==null || strEnd ==null)
			return false;
		mStrStart = strStart;
		mStartCity = startCity;
		mStrEnd = strEnd;
		mEndCity = endCity;
		startSearchResult();
		return true;
	}

	public boolean searchRouteResult(String strStart, String startCity,
			GeoPoint endPoint) {
		if(strStart==null || endPoint == null)
			return false;
		mStrStart = strStart;
		mStartCity = startCity;
		mEndPoint = endPoint;
		startSearchResult();
		return true;
	}

	public boolean searchRouteResult(GeoPoint startPoint, String strEnd,
			String endCity) {
		if(startPoint ==null||strEnd==null)
			return false;
		mStartPoint = startPoint;
		mStrEnd = strEnd;
		mEndCity = endCity;
		endSearchResult();
		return true;
	}

	public boolean searchRouteResult(GeoPoint startPoint, GeoPoint endPoint, int modeType) {
		if (startPoint == null || endPoint == null)
			return false;
		Log.d(TAG_GD,"invoke  searchRouteResult startPoint =" + startPoint.toString() + ", endPoint = " + endPoint.toString());
		mStartPoint = startPoint;
		mEndPoint = endPoint;
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
				endPoint);
	//	setMode(modeType);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
						Log.d(TAG_GD,"invoke com.mapabc.mapapi.route.Route fuc --- calculateRoute");
						mRouteResult = Route.calculateRoute(venusActivity,
							fromAndTo, mode);
						if (mRouteResult == null || mRouteResult.size() <= 0)
						{
							Log.d(TAG_GD,"mRouteResult =Route.calculateRoute(venusActivity,fromAndTo, mode),mRouteResult is null");
							return;
						}
						
						Route route = mRouteResult.get(0);
						if (route == null) {
							Log.d(TAG_GD,"mRouteResult =Route.calculateRoute(venusActivity,fromAndTo, mode) and route = mRouteResult.get(0),route is null");
							return;
						}

					//	if (mRouteOverlay != null) {
					//		mRouteOverlay.removeFromMap(mMapView);
					//	}

						mHandler.sendMessage(Message.obtain(mHandler,
								Constants.ROUTE_SEARCH_RESULT, mStartPoint));

						

				//	}
				} catch (Exception e) {
					Log.d(TAG_GD,"invoke searchRouteResult is Exception:" +e.getMessage());
					// TODO Auto-generated catch block
				//	progDialog.dismiss();
				}
			}
		});
		t.start();
		return true;
	}
	
	public void showRouteResult()
	{
		if (mRouteResult == null || mRouteResult.size() <= 0)
			return;

		Route route = mRouteResult.get(0);
		if (route == null) {
			return;
		}

		if (mRouteOverlay != null) {
			mRouteOverlay.removeFromMap(mMapView);
		}

		mRouteOverlay = new RouteOverlay((MapActivity)venusActivity, route);
		//mRouteOverlay.registerRouteMessage((AppActivity)venusActivity); 

		mRouteOverlay.enableDrag(false);

		mRouteOverlay.addToMap(mMapView); 
		
		if(mEndPoint!=null)
			mMapController.animateTo(mEndPoint);
		
		mStrStart = null;
		mStartCity = "";
		mStrEnd = null;
		mEndCity = "";
		mStartPoint = null;
		mEndPoint = null;

	}
	
	public void removeRouteOverlay()
	{
		if (mRouteOverlay != null) {
			mRouteOverlay.removeFromMap(mMapView);
		}
	}

	public void showRouteOverlay(){
		
	}
	
	public void startSearchResult() {
		if (mStartPoint != null) {
			endSearchResult();
		} else {
			final Query startQuery = new Query(mStrStart, PoiTypeDef.All,
					mStartCity);
			;
			progDialog = ProgressDialog.show(venusActivity, null,
					 venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_progress_msg", "string", venusActivity.getPackageName())));
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						PoiSearch poiSearch = new PoiSearch(venusActivity,
								startQuery); 
						mStartSearchResult = poiSearch.searchPOI();
						if (progDialog.isShowing())
							doRouteStartSearch();
					} catch (Exception e) {
						e.printStackTrace();
						progDialog.dismiss();
						mHandler.sendMessage(Message.obtain(
								mHandler, Constants.ROUTE_SEARCH_ERROR,
								venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_startmsg2", "string", venusActivity.getPackageName())) + mStrStart + venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_startmsg2", "string", venusActivity.getPackageName()))));
					} 
				}

			});
			t.start();
		}
	}


	public void endSearchResult() {
		if (mEndPoint != null) {
			searchRouteResult(mStartPoint, mEndPoint,Route.DrivingDefault);
		} else {
			final Query endQuery = new Query(mStrEnd, PoiTypeDef.All, mEndCity);
			progDialog = ProgressDialog.show(venusActivity, null,
					venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_progress_msg", "string", venusActivity.getPackageName())), true, false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						PoiSearch poiSearch = new PoiSearch(venusActivity,
								endQuery); 

						mEndSearchResult = poiSearch.searchPOI();

						if (progDialog.isShowing())
							doRouteEndSearch();
					} catch (Exception e) {
						progDialog.dismiss();
						mHandler.sendMessage(Message.obtain(mHandler,
								Constants.ROUTE_SEARCH_ERROR,venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_endmsg1", "string", venusActivity.getPackageName()))
								+ mStrEnd 
								+ venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_endmsg2", "string", venusActivity.getPackageName()))));
					}
				}

			});
			t.start();
		}
	}

	private void doRouteStartSearch() {
		progDialog.dismiss();
		if (mStartSearchResult.getPageCount() == 0) {
			mHandler.sendMessage(Message.obtain(
					mHandler, Constants.ROUTE_SEARCH_ERROR,
					venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_startmsg1", "string", venusActivity.getPackageName()))
					+ mStrStart 
					+ venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_startmsg1", "string", venusActivity.getPackageName())).toString()));
			return;
		}
		List<PoiItem> poiItems = null;

		try {
			poiItems = mStartSearchResult.getPage(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mHandler.sendMessage(Message.obtain(mHandler,
				Constants.ROUTE_START_SEARCH, poiItems));

	}

	private void doRouteEndSearch() {
		progDialog.dismiss();
		if (mEndSearchResult == null || mEndSearchResult.getPageCount() == 0) {
			mHandler.sendMessage(Message.obtain(
					mHandler, Constants.ROUTE_SEARCH_ERROR,
					venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_endmsg1", "string", venusActivity.getPackageName())) 
					+ mStrEnd 
					+ venusActivity.getResources().getText(venusActivity.getResources().getIdentifier("gdmap_search_endmsg2", "string", venusActivity.getPackageName())).toString()));
			return;

		}
		List<PoiItem> poiItems = null;
		try {
			poiItems = mEndSearchResult.getPage(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mHandler.sendMessage(Message.obtain(mHandler,
				Constants.ROUTE_END_SEARCH, poiItems));
	}

}
