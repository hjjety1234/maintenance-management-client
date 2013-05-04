package com.wondertek.video.caller;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wondertek.jttxl.R;

public class FloatRelativeLayout extends RelativeLayout {
	private static final String TAG = "FloatRelativeLayout";
	private Context mContext;
	private Employee employee;
	public boolean isScale = false;
	private float mScaleFactor = 1;
	private ScaleGestureDetector mScaleDetector;
	private float rx;
	private float ry;
	private float px;
	private float py;
	private float rawX;
	private float rawY;

	private WindowManager wm = (WindowManager) getContext()
			.getApplicationContext().getSystemService("window");

	private WindowManager.LayoutParams wmParams = null;

	public FloatRelativeLayout(Context context,
			WindowManager.LayoutParams params, Employee e) {
		super(context);
		employee = e;
		mContext = context;
		wmParams = params;
		mScaleDetector = new ScaleGestureDetector(context,
				new OnPinchListener());
		if (employee.getPicutre() != null
				&& !employee.getPicutre().trim().equals("")) {
			File dir = new File(Constants.LOC_PIC_DIR);
			dir.mkdir();
			String localPicPath = Constants.LOC_PIC_DIR + employee.getPicutre();
			Log.d(TAG, "[initLayout] local picture path: " + localPicPath);
			File pic = new File(localPicPath);
			if (pic.exists() == true) {
				inflate(context, R.layout.float_view_withphoto, this);
			} else {
				inflate(context, R.layout.float_view, this);
			}
		} else {
			inflate(context, R.layout.float_view, this);
		}
		initLayout();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d(TAG, "[dispatchDraw] isScale: " + isScale);

		// get SharedPreferences and editor
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = popupPos.edit();

		// get original matrix
		float[] values = new float[9];
		Matrix m = new Matrix();
		boolean isMatrixSaved = true;
		for (int i = 0; i < 9; ++i) {
			values[i] = popupPos.getFloat("f" + i, -1000);
			if (values[i] == -1000)
				isMatrixSaved = false;
		}
		if (isMatrixSaved == true) {
			m.setValues(values);
		} else {
			m = canvas.getMatrix();
			m.getValues(values);
			for (int i = 0; i < 9; ++i) {
				editor.putFloat("f" + i, values[i]);
			}
		}
		canvas.setMatrix(m);

		// save scale pivotX and pivotY
		editor.putFloat("scale", mScaleFactor);
		editor.putFloat("pivotX", px);
		editor.putFloat("pivotY", py);

		// get original width and height
		float width = popupPos.getFloat("width", 0.0f);
		float height = popupPos.getFloat("height", 0.0f);
		if (width < 0.01f || height < 0.01f) {
			width = getWidth();
			height = getHeight();
			editor.putFloat("width", width);
			editor.putFloat("height", height);
		}
		editor.commit();

		// do scale
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(mScaleFactor, mScaleFactor, px, py);
		super.dispatchDraw(canvas);
		canvas.restore();

		// set layout's width and height
		wmParams.width = (int) (width * mScaleFactor);
		wmParams.height = (int) (height * mScaleFactor);
		if (PhoneStatReceiver.relativeLayout != null)
			wm.updateViewLayout(this, wmParams);

		// debug output
		Log.d(TAG, "[dispatchDraw] width: " + getWidth());
		Log.d(TAG, "[dispatchDraw] height: " + getHeight());
	}

	public void scale(float scaleFactor, float pivotX, float pivotY) {
		Log.d(TAG, "[scale] scale:" + scaleFactor + ", pivotX:" + pivotX
				+ ", pivotY: " + pivotY);
		if (isScale == false || scaleFactor <= 0.8) {
			Log.w(TAG,
					"[scale] isScale is false or reach minmal limit, do nothing...");
			return;
		}
		if (scaleFactor < 1) {
			mScaleFactor = scaleFactor;
		} else {
			mScaleFactor = 1;
		}
		px = pivotX;
		py = pivotY;
		this.invalidate();
	}

	public void setScaleEnabled(boolean bEnable) {
		isScale = bEnable;
	}

	private void initLayout() {
		Log.d(TAG, "[initLayout]");

		// set caller name
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		callerName.setText(employee.getName());

		// set caller headship
		TextView headship = (TextView) findViewById(R.id.headship);
		String defaultHeadship = getResources().getString(
				R.string.defaultHeadship);
		if (!employee.getHeadship().trim().equals(""))
			// && !employee.getHeadship().equals(defaultHeadship))
			headship.setText(employee.getHeadship());

		// set caller department information
		TextView dept = (TextView) findViewById(R.id.deptinfo);
		dept.setText(employee.getDepartment());

		// set caller picture
		if (employee.getPicutre() != null
				&& !employee.getPicutre().trim().equals("")) {
			File dir = new File(Constants.LOC_PIC_DIR);
			dir.mkdir();
			String localPicPath = Constants.LOC_PIC_DIR + employee.getPicutre();
			Log.d(TAG, "[initLayout] local picture path: " + localPicPath);
			File pic = new File(localPicPath);
			if (pic.exists() == true) {
				Log.d(TAG, "[initLayout] show custom layout.");
				initCustomLayout();
			} else {
				Log.d(TAG, "[initLayout] trying to async download picture...");
				DownloadFileTask downloadFile = new DownloadFileTask(
						employee.getPicutre());
				String requestUri = downloadFile.requestResourceUri();
				if (requestUri != null) {
					String resourceUri = Constants.RES_PIC_URL_PREFIX
							+ requestUri;
					downloadFile.execute(resourceUri);
				} else {
					Log.w(TAG, "[initLayout] request resource uri failed!");
				}
			}
		} else {
			Log.w(TAG, "[initLayout] picture path is null.");
		}
	}

	private void initCustomLayout() {
		Log.d(TAG, "[initCustomLayout]");
		// change popup window background
		ImageView bg = (ImageView) findViewById(R.id.incoming_bg);
		Uri uri = Uri.parse(Constants.LOC_PIC_DIR + employee.getPicutre());
		bg.setImageURI(uri);

		// set caller info background visible
		ImageView iv = (ImageView) findViewById(R.id.caller_info_bg);
		iv.setVisibility(VISIBLE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		rawX = event.getRawX();
		rawY = event.getRawY();
		Log.d(TAG, "[onTouchEvent] MotionEvent: " + event.getAction());
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			rx = event.getX();
			ry = event.getY();
			isScale = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isScale == false) {
				updateViewPosition();
			} else {
				mScaleDetector.onTouchEvent(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isScale == false) {
				updateViewPosition();
			}
			isScale = false;
			rx = 0;
			ry = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			isScale = true;
			mScaleDetector.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isScale = false;
			break;
		}
		Log.d(TAG, "[onTouchEvent] isScale: " + isScale);
		return true;
	}

	private void updateViewPosition() {
		Log.d(TAG, ">>>updateViewPosition<<<");
		wmParams.x = (int) (rawX - rx);
		wmParams.y = (int) (rawY - ry);
		if (PhoneStatReceiver.relativeLayout != null)
			wm.updateViewLayout(this, wmParams);
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = popupPos.edit();
		editor.putInt("rawX", wmParams.x);
		editor.putInt("rawY", wmParams.y);
		editor.commit();
	}

	private class OnPinchListener extends SimpleOnScaleGestureListener {
		float startingSpan;
		float startFocusX;
		float startFocusY;

		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.d(TAG, ">>>onScaleBegin<<<");
			startingSpan = detector.getCurrentSpan();
			startFocusX = detector.getFocusX();
			startFocusY = detector.getFocusY();
			return true;
		}

		public boolean onScale(ScaleGestureDetector detector) {
			Log.d(TAG, ">>>onScale<<<");
			scale(detector.getCurrentSpan() / startingSpan, startFocusX,
					startFocusY);
			return true;
		}
	}
}
