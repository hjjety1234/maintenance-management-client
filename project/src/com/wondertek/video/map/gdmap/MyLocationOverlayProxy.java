package com.wondertek.video.map.gdmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.location.LocationProviderProxy;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.Projection;
import com.mapabc.mapapi.map.MyLocationOverlay;

/**
 * 
 * @author yuhongwei
 *
 */
public class MyLocationOverlayProxy extends MyLocationOverlay{
	private static final String TAG = "MyLocationOverlayProxy";
	private static MyLocationOverlayProxy instance = null;
	private Bitmap locationMark=null;
	private Context context;
    private GeoPoint mCurGeoPoint = null;

	private MyLocationOverlayProxy(Context context, MapView mapView) {
		super(context, mapView);
		locationMark = ((BitmapDrawable) context.getResources().getDrawable(context.getResources().
				getIdentifier("gdmap_iconlocr_nor", "drawable", context.getPackageName()))).getBitmap();
		this.context = context;
	}

	public static MyLocationOverlayProxy getInstance(Context cxt, MapView mv) {
		if (instance == null) {
			instance = new MyLocationOverlayProxy(cxt, mv);
		}
		return instance;
	}
/*
	@Override
	protected void drawMyLocation(Canvas canvas,  MapView mapView, final Location mLocation,
			GeoPoint point, long time) {
		Log.d(TAG, ">>>drawMyLocation<<<");
		Projection pj = mapView.getProjection();
		if (mLocation != null) {
			Point mMapCoords=pj.toPixels(point, null);
			final float radius = pj.metersToEquatorPixels(mLocation.getAccuracy());	
			Paint mCirclePaint = new Paint();
			mCirclePaint.setAntiAlias(true);
			mCirclePaint.setARGB(35, 131, 182, 222);
			mCirclePaint.setAlpha(50);
			mCirclePaint.setStyle(Style.FILL);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);
			mCirclePaint.setARGB(225, 131, 182, 222);
			mCirclePaint.setAlpha(150);
			mCirclePaint.setStyle(Style.STROKE);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);				
			canvas.drawBitmap(locationMark, mMapCoords.x-locationMark.getWidth()/2 - 0.5f, 
					mMapCoords.y-locationMark.getHeight()/2 - 0.5f, new Paint());
		}
	}
*/
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long time) {
		// TODO Auto-generated method stub
        Log.d(TAG, ">>>draw<<<");
        Projection pj = mapView.getProjection();
        Location location = getLastFix();
        if (mCurGeoPoint != null && location != null) {
        	Point mMapCoords=pj.toPixels(mCurGeoPoint, null);
			final float radius = pj.metersToEquatorPixels(location.getAccuracy());	
            Paint mCirclePaint = new Paint();
			mCirclePaint.setAntiAlias(true);
			mCirclePaint.setARGB(35, 131, 182, 222);
			mCirclePaint.setAlpha(50);
			mCirclePaint.setStyle(Style.FILL);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);
			mCirclePaint.setARGB(225, 131, 182, 222);
			mCirclePaint.setAlpha(150);
			mCirclePaint.setStyle(Style.STROKE);
			canvas.drawCircle(mMapCoords.x, mMapCoords.y, radius, mCirclePaint);
			Matrix matrix = new Matrix();
            matrix.reset();
            matrix.setTranslate(mMapCoords.x - locationMark.getWidth()/2 - 0.5f, mMapCoords.y - locationMark.getHeight()/2 - 0.5f);
            matrix.postRotate(getOrientation(), mMapCoords.x, mMapCoords.y);
            canvas.drawBitmap(locationMark, matrix, null);
        }
        return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, ">>>onLocationChanged<<<");
		mCurGeoPoint = new GeoPoint((int)(location.getLatitude() * 1e6), 
				(int)(location.getLongitude() * 1e6));
		Handler handler = GDMapManager.getInstance().getHandler();
		handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_AUTOLOCATION, mCurGeoPoint));
		super.onLocationChanged(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged");
		if (status == LocationProviderProxy.OUT_OF_SERVICE || status == LocationProviderProxy.TEMPORARILY_UNAVAILABLE) {
			Toast.makeText(context, context.getResources().getString(context.getResources().
					getIdentifier("gdmap_provider_error", "string", context.getPackageName())), Toast.LENGTH_LONG).show();
		}
		super.onStatusChanged(provider, status, extras);
	}

}
