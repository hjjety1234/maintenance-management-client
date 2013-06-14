package com.wondertek.video.map.gdmap;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Address;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.MapAbcException;
import com.mapabc.mapapi.geocoder.Geocoder;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.Overlay;

/**
 * 
 * @author yuhongwei
 *
 */
public class GDMapPointOverlay extends Overlay {
	private static final String TAG = "GDMapPointOverlay";
	private static GDMapPointOverlay instance = null;
	private static View pointPopView = null;
	private Context context;
	private MapView mMapView;

	private GDMapPointOverlay(Context cxt, MapView mv) {
		this.context = cxt;
        this.mMapView = mv;
	}

	public static GDMapPointOverlay getInstance(Context cxt, MapView mv) {
		if (instance == null) {
			instance = new GDMapPointOverlay(cxt, mv);
		}
		return instance;
	}
    
	public void removePointOverlay() {
		if (pointPopView != null) {
			mMapView.removeView(pointPopView);
			mMapView.getOverlays().remove(instance);
		}
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(final GeoPoint point, final MapView mapView) {
		if (pointPopView != null) {
			mapView.removeView(pointPopView);
		}
		pointPopView = ((Activity) context).getLayoutInflater().inflate(context.getResources().
				getIdentifier("gdmap_popview_point", "layout", context.getPackageName()), null);
		TextView textView = (TextView) pointPopView.findViewById(context.getResources().
				getIdentifier("PoiName", "id", context.getPackageName()));
		Geocoder mTop = new Geocoder((Activity) context);
		List<Address> addressList=new ArrayList<Address>();
		Address address = new Address(null);
		try {
			addressList = mTop.getFromLocation(point.getLatitudeE6() * (1E-6),
					point.getLongitudeE6() * (1E-6), 1);
			if (addressList.size()>0) {
				address = addressList.get(0);
				textView.setText(address.getAdminArea()+address.getFeatureName()+'\n'+"点此确定");
				mapView.addView(pointPopView, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						point, 0, 0, LayoutParams.CENTER));
			} else {
				Log.d("Address", "address hasn't been found");
			}
		} catch (MapAbcException e) {
			e.printStackTrace();
			Log.d("Geocoder", "getFromLocation failed");
		}
		final Address adr = address;
		pointPopView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, ">>>onClick<<<");
				StringBuilder sb = new StringBuilder();
				sb.append("{\"latitude\":\"" +point.getLatitudeE6()+"\",");
				sb.append("\"longitude\":\"" +point.getLongitudeE6()+"\",");
				sb.append("\"CountryCode\":\"" +adr.getCountryCode()+"\",");
				sb.append("\"CountryName\":\"" +adr.getCountryName() +"\",");
				sb.append("\"AdminArea\":\"" +adr.getAdminArea()+"\",");
				sb.append("\"FeatureName\":\"" +adr.getFeatureName()+"\",");
				sb.append("\"tel\":\"" +adr.getPhone() +"\",");
				sb.append("\"Premises\":\"" +adr.getPremises() +"\",");
				sb.append("\"SubLocality\":\"" +adr.getSubLocality()+"\"}");
				Log.d(TAG, sb.toString());
				Handler handler = GDMapManager.getInstance(context).getHandler();
				handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIDETAIL, sb.toString()));
				mapView.removeView(pointPopView);
				mapView.getOverlays().remove(this);
			}
		});
		return super.onTap(point, mapView);
	}
}