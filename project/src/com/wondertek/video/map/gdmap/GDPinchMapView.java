package com.wondertek.video.map.gdmap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;

import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.ZoomButtonsController;

/**
 * 
 * @author hewu <hewu2008@gmail.com>
 * 
 */
public class GDPinchMapView extends MapView {
	private static final String TAG = "GDPinchMapView";
	public static final int ZOOM_IN = 0;
	public static final int ZOOM_OUT = 1;
	public static final int DURATION = 100;

	public static final float MIN_SCALE = 0.5f;
	public static final float MAX_SCALE = 2.5f;
	public static final float ZOOM = 0.1f;

	public static float SCALE_DIVISOR;

	public int mTouchSlop = 10, mZoomScale = 1, mOldZoomScale = 1;
	public int mMinZoomScale = 1, mMaxZoomScale = 2;

	public boolean mZooming = false;

	public float mZoomDelta = 0f, mLastZoomDelta = 1f, mScale = 1;
	public float mLastScale = 1, mScaleCenter;

	protected ZoomButtonsController mZoomController;
	protected MapController mMapController;

	protected static float density, x1, x2, y1, y2, x1_pre, y1_pre,
			dist_delta = 0, dist_curr = -1, dist_pre = -1, dist_scale = 0;

	private long mLastGestureTime;

	public GDPinchMapView(Context context, String apiKey) {
		super(context, apiKey);
		init();
	}

	public GDPinchMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GDPinchMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {

		mMapController = getController();

		density = getContext().getResources().getDisplayMetrics().density;
		mZoomScale = getZoomLevel();
		mMaxZoomScale = getMaxZoomLevel();

		SCALE_DIVISOR = getContext().getResources().getDisplayMetrics().widthPixels
				* density;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK, p_count = event
				.getPointerCount();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			// point 1 coords
			x1 = event.getX(0);
			y1 = event.getY(0);

			if (p_count > 1) {
				// point 2 coords
				x2 = event.getX(1);
				y2 = event.getY(1);

				// distance between
				dist_curr = (float) Math.sqrt(Math.pow(x2 - x1, 2)
						+ Math.pow(y2 - y1, 2));
				dist_delta = dist_curr - dist_pre;

				dist_scale = (float) Math.sqrt(Math.pow(x1_pre - x1, 2)
						+ Math.pow(y1_pre - y1, 2))
						/ SCALE_DIVISOR;

				mZoomDelta += dist_delta;
				mOldZoomScale = mZoomScale;

				ScaleAnimation scale = null;
				long now = android.os.SystemClock.uptimeMillis();
				if (now - mLastGestureTime > DURATION
						&& Math.abs(dist_delta) > mTouchSlop) {
					mLastGestureTime = 0;

					int mode = dist_delta > 0 ? ZOOM_IN
							: (dist_curr == dist_pre ? 2 : ZOOM_OUT);
					switch (mode) {
					case ZOOM_IN: // grow
						if (mZoomScale < mMaxZoomScale) {
							++mZoomScale;
							mScale += dist_scale;
						}
						break;
					case ZOOM_OUT: // shrink
						if (mZoomScale > mMinZoomScale) {
							--mZoomScale;
							mScale -= dist_scale;
						}
						break;
					}

					if (mZoomScale != mOldZoomScale) {
						mMapController.setZoom(mZoomScale);
						scale = new ScaleAnimation(mLastScale, mScale,
								mLastScale, mScale,
								ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
								ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
						scale.setFillAfter(true);
						scale.setDuration(DURATION);
						scale.setInterpolator(getContext(),
								android.R.anim.accelerate_interpolator);
						startAnimation(scale);
						Log.e("ZOOM SCALE", "From: " + mLastScale + ", To: "
								+ mScale);
					}

					mLastZoomDelta = mZoomDelta;
					mLastScale = mScale;
					mLastGestureTime = now;
				}

				x1_pre = x1;
				y1_pre = y1;
				dist_pre = dist_curr;

				return true;
			}
			break;
		case MotionEvent.ACTION_POINTER_1_UP:
			Log.e("Pointer Up", "Count: " + p_count);
			mScale = mLastScale = 1;
			mZoomDelta = mLastZoomDelta = 0;
			mLastGestureTime = android.os.SystemClock.uptimeMillis();
			clearAnimation();
			break;
		case MotionEvent.ACTION_POINTER_1_DOWN:
			Log.e("Pointer Down", "Count: " + p_count);
			x1 = x1_pre = event.getX(0);
			y1 = y1_pre = event.getY(0);
			mLastGestureTime = android.os.SystemClock.uptimeMillis();
			break;
		}
		return super.onTouchEvent(event);
	}

}
