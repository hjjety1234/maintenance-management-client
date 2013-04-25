package com.wondertek.video.caller;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wondertek.jttxl.R;

public class FloatRelativeLayout extends RelativeLayout {
	private static final String TAG = "FloatRelativeLayout";
	private Context mContext;
	private Employee employee;
	private float rx;
	private float ry;
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
		//这里要根据用户是否有照片来加载不同的Layout
		if (employee.getPicutre() != null
				&& !employee.getPicutre().trim().equals("")) {
			File dir = new File(Constants.LOC_PIC_DIR);
			dir.mkdir();
			String localPicPath = Constants.LOC_PIC_DIR + employee.getPicutre();
			Log.d(TAG, "[initLayout] local picture path: " + localPicPath);
			File pic = new File(localPicPath);
			if (pic.exists() == true) {
				inflate(context, R.layout.float_view_withphoto, this);
			}
			else
				inflate(context, R.layout.float_view, this);
		}
		else
		{
			inflate(context, R.layout.float_view, this);
		}
		initLayout();
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
				//&& !employee.getHeadship().equals(defaultHeadship))
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
		iv.setAlpha(100);

		// change caller name font color
		// TextView callerName = (TextView) findViewById(R.id.caller_name);
		// callerName.setTextColor(getResources().getColor(R.color.white));
		//
		// // change caller headship font color
		// TextView headship = (TextView) findViewById(R.id.headship);
		// headship.setTextColor(getResources().getColor(R.color.white));
		//
		// // set caller department information
		// TextView dept = (TextView) findViewById(R.id.deptinfo);
		// dept.setTextColor(getResources().getColor(R.color.white));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		rawX = event.getRawX();
		rawY = event.getRawY() - 25;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			rx = event.getX();
			ry = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			updateViewPosition();
			rx = 0;
			ry = 0;
			break;
		}
		return true;
	}

	private void updateViewPosition() {
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
}
