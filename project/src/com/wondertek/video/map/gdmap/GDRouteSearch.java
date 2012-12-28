package com.wondertek.video.map.gdmap;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.RouteMessageHandler;
import com.mapabc.mapapi.map.RouteOverlay;
import com.mapabc.mapapi.route.Route;
import com.mapabc.mapapi.route.Route.FromAndTo;

/**
 * 
 * @author yuhongwei
 *
 */

public class GDRouteSearch implements RouteMessageHandler {
	private static final String TAG = "GDRouteSearch";
	private static GDRouteSearch instance = null;
	private int mode = Route.DrivingDefault;
	private RouteOverlay routeOverlay = null;
	private Context context;
	private MapView mMapView;
    private List<Route> result;

	private GDRouteSearch(Context cxt, MapView mv) {
		Log.d(TAG, ">>>GDRouteSearch<<<");
		this.context = cxt;
		this.mMapView = mv;
	}

	public static GDRouteSearch getInstance(Context cxt, MapView mv) {
		if (instance == null) {
			instance = new GDRouteSearch(cxt, mv);
		}
		return instance;
	}

	public boolean routeSearch(final GeoPoint start, GeoPoint end, int type) {
		if (start == null || end == null) {
			return false;
		}
		final FromAndTo ft = new FromAndTo(start, end);
		setRouteSearchMode(type);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setMessage(context.getResources().getString(context.getResources().getIdentifier(
        		"gdmap_searching", "string", context.getPackageName())));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					result = Route.calculateRoute((Activity) context, ft, mode);
					if (result != null && result.size() > 0) {
                        if (dialog.isShowing())
                        	dialog.dismiss();
						Handler handler = GDMapManager.getInstance(context).getHandler();
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_ROUTESEARCH_RESULT));
					}
				} catch (Exception e) {
                    if (dialog.isShowing())
                    	dialog.dismiss();
					e.printStackTrace();
                    Toast.makeText(context, context.getResources().getString(context.getResources().
                    		getIdentifier("gdmap_route_error", "string", context.getPackageName())), Toast.LENGTH_LONG).show();
				}
			}
		});
		t.start();
        dialog.show();
		return true;
	}

	public void drawRouteOnMap(int routeId) {
		Log.d(TAG, "drawRouteOnMap RouteId: " + routeId);
        if (result != null) {
			if (routeOverlay != null) {
				routeOverlay.removeFromMap(mMapView);
			}
	        Route route = result.get(routeId);
	        if (route == null) 
	        	return;
	        
	        mMapView.getController().setZoom(15);
	        mMapView.getController().animateTo(route.getStartPos());
			routeOverlay = new RouteOverlay((MapActivity)context, route);
            routeOverlay.enableDrag(false);
			Paint  carLine = new Paint();
			carLine.setStyle(Style.STROKE);
			carLine.setColor(Color.rgb(9,129,240));
			carLine.setAlpha(180);
			carLine.setStrokeWidth(8.0f);
			carLine.setStrokeJoin(Join.ROUND);
			carLine.setStrokeCap(Cap.ROUND);
			carLine.setAntiAlias(true);
			switch (getRouteType(mode)) {
				case GDMapConstants.GDMAP_BUS_ROUTE:
					routeOverlay.setBusLinePaint(carLine);
					break;
				case GDMapConstants.GDMAP_CAR_ROUTE:
					routeOverlay.setCarLinePaint(carLine);
					break;
				default:
					routeOverlay.setFootLinePaint(carLine);
					break;
			}
			routeOverlay.registerRouteMessage(this);
			routeOverlay.addToMap(mMapView);
        } else {
            Toast.makeText(context, context.getResources().getString(context.getResources().
            		getIdentifier("gdmap_route_error", "string", context.getPackageName())), Toast.LENGTH_LONG).show();
        }
	}

	public String getRouteData(Route route) {
		String routeInfo="the overview info about the recent route is:"+route.getOverview();
		return routeInfo;
	}

	public void removeRouteOnMap() {
		if (routeOverlay != null) {
			routeOverlay.removeFromMap(mMapView);
		}
	}

	@Override
	public void onDrag(MapView mapView, RouteOverlay overlay, int index, GeoPoint pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDragBegin(MapView mapView, RouteOverlay overlay, int index,
			GeoPoint pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDragEnd(MapView mapView, RouteOverlay overlay, int index,
			GeoPoint pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onRouteEvent(MapView mapView, RouteOverlay overlay, int index,
			int pos) {
		// TODO Auto-generated method stub
		return false;
	}

	private void setRouteSearchMode(int mode) {
		Log.d(TAG, "setRouteSearchMode Mode: " + mode);
		switch (mode) {
		case GDMapConstants.GDMAP_DRIVING_Default:
			this.mode = Route.DrivingDefault;
			break;
		case GDMapConstants.GDMAP_DRIVING_LeastDistance:
			this.mode = Route.DrivingLeastDistance;
			break;
		case GDMapConstants.GDMAP_DRIVING_NoFastRoad:
			this.mode = Route.DrivingNoFastRoad;
			break;
		case GDMapConstants.GDMAP_DRIVING_SaveMoney:
			this.mode = Route.DrivingSaveMoney;
			break;
		case GDMapConstants.GDMAP_BUS_Default:
			this.mode = Route.BusDefault;
			break;
		case GDMapConstants.GDMAP_BUS_LeastChange:
			this.mode = Route.BusLeaseChange;
			break;
		case GDMapConstants.GDMAP_BUS_LeastWalk:
			this.mode = Route.BusLeaseWalk;
			break;
		case GDMapConstants.GDMAP_BUS_MostComfortable:
			this.mode = Route.BusMostComfortable;
			break;
		case GDMapConstants.GDMAP_BUS_SaveMoney:
			this.mode = Route.BusSaveMoney;
			break;
		default:
			break;
		}
	}
    
	public String getRouteData(){
		StringBuilder data = new StringBuilder();
		data.append('{');
		try {
			if (result != null && result.size() > 0) {
				data.append("\"RoutesInfo\":[");
				for(int i =0; i< result.size();i++) {
					Route rt = result.get(i);
					data.append("{\"Mode\":\"" +rt.getMode()+"\",");
					data.append("\"Length\":\"" +rt.getLength()+"\",");
					data.append("\"OverView\":\"" +rt.getOverview()+"\",");
					data.append("\"StartPos\":\"" +rt.getStartPos()+"\",");
					data.append("\"StartPlace\":\"" +rt.getStartPlace()+"\",");
					data.append("\"TargetPos\":\"" +rt.getTargetPos()+"\",");
					data.append("\"TargetPlace\":\"" +rt.getTargetPlace()+"\",");
					data.append("\"LowerLeftPoint\":\"" +rt.getLowerLeftPoint()+"\",");
					data.append("\"UpperRightPoint\":\"" +rt.getUpperRightPoint()+"\",");
					data.append("\"SegmentOverview\":[");
					for(int j=0;j<rt.getStepCount();j++){
						data.append("{\"StepInfo\":\"" +rt.getStepedDescription(j)+"\"}");
						if(j<rt.getStepCount()-1)
							data.append(',');
					}
					data.append("]}");
					if(i<result.size()-1)
						data.append(',');
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		data.append("]}");
		return data.toString();
	}

	private int getRouteType(int mode) {
		switch (mode) {
		case GDMapConstants.GDMAP_BUS_Default:
		case GDMapConstants.GDMAP_BUS_LeastChange:
		case GDMapConstants.GDMAP_BUS_LeastWalk:
		case GDMapConstants.GDMAP_BUS_MostComfortable:
		case GDMapConstants.GDMAP_BUS_SaveMoney:
			return GDMapConstants.GDMAP_BUS_ROUTE;
		case GDMapConstants.GDMAP_DRIVING_Default:
		case GDMapConstants.GDMAP_DRIVING_LeastDistance:
		case GDMapConstants.GDMAP_DRIVING_NoFastRoad:
		case GDMapConstants.GDMAP_DRIVING_SaveMoney:
			return GDMapConstants.GDMAP_CAR_ROUTE;
		default:
			return GDMapConstants.GDMAP_WALK_ROUTE;
		}
	}
}
