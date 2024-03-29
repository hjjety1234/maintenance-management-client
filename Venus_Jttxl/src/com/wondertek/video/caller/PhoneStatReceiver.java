package com.wondertek.video.caller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.wondertek.video.VenusApplication;

import net.sqlcipher.database.SQLiteDatabase;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class PhoneStatReceiver extends BroadcastReceiver {
	public static final String CUSTOM_INTENT = "jason.wei.custom.intent.action.TEST";
	public static final String POPUP_POS = "POPUP_POS";
	private static final String TAG = "PhoneStatReceiver";
	private static boolean IS_OUTGOING_CALL = false;
	static FloatRelativeLayout relativeLayout = null;
	private static KeyguardLock keyguard = null;
	TelephonyManager telMgr;
	private static Context mContext;
	private static Timer timer = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Log.d(TAG, "[handleMessage] call screen isn't on top... ");
				removePopup((Context) msg.obj);
			} else if (msg.what == 2) {
				Log.d(TAG,
						"[handleMessage] handle get employee info by http method... ");
				List<Employee> empList = new ArrayList<Employee>();
				empList.add((Employee) msg.obj);
				addPopup(empList, mContext);
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onReceive(final Context context, final Intent intent) {
		telMgr = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);
		mContext = context;
		SQLiteDatabase.loadLibs(context);
		// outgoing call
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			final String number = intent
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			IS_OUTGOING_CALL = true;
			Log.d(TAG, "[onReceive] outgoing caller number: " + number);
			// search sqlite database
			DbHelper helper = new DbHelper(context);
			List<Employee> empList = helper.getEmployee(number);
			if (empList != null && empList.size() > 0) {
				addToHistory(number, "1", context, empList.get(0).getEmpid());
				addPopup(empList, context);
			} else {
				Log.w(TAG, "[onReceive] can't find the outgoing caller in sqlite database.");
			}
			return;
		}

		switch (telMgr.getCallState()) {
		case TelephonyManager.CALL_STATE_RINGING:
			final String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			IS_OUTGOING_CALL = false;
			Log.d(TAG, "[onReceive] incoming call number:" + number);
			// search sqlite database
			DbHelper helper = new DbHelper(context);
			List<Employee> empList = helper.getEmployee(number);
			if (empList != null && empList.size() > 0) {
				// record to history first
				addToHistory(number, "0", context, empList.get(0).getEmpid());
				addPopup(empList, context);
			} else {
				Log.w(TAG, "[onReceive] can't find the incoming caller in sqlite database.");
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d(TAG, "[onReceive] CALL_STATE_OFFHOOK");
			if (IS_OUTGOING_CALL == false)
				removePopup(context);
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d(TAG, "[onReceive] CALL_STATE_IDLE");
			removePopup(context);
			// enable keyguard
			if (keyguard != null) {
				keyguard.reenableKeyguard();
				keyguard = null;
				Log.d(TAG, "[onReceive] enable keyguard.");
			}
			break;
		}
	}

	// add record to history
	public void addToHistory(String num, String type, Context context,
			String empid) {
		DbHelper helper = new DbHelper(context);
		helper.recordCallHistory(num, type, empid, null);
	}

	// add popup window
	public void addPopup(List<Employee> empList, Context c) {
		Log.d(TAG,">>>addPopup<<<");
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
						| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		params.flags |= WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

		// disable keyguard
		KeyguardManager manager = (KeyguardManager) c
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (keyguard == null && manager.inKeyguardRestrictedInputMode()) {
			keyguard = manager.newKeyguardLock(TAG);
			keyguard.disableKeyguard();
			Log.d(TAG, "[addPopup] disable keyguard.");
		}

		SharedPreferences popupPos = c.getSharedPreferences(POPUP_POS,
				Context.MODE_PRIVATE);
		int rx = popupPos.getInt("rawX", 0);
		int ry = popupPos.getInt("rawY", 250);
		// read scale factor from config file
		float scaleFactor = 1.0f;
		String strfactor = ConfigUtil.getValue();
		if (strfactor !=null && !strfactor.trim().equals("")) {
			try {
				Log.d(TAG, "[addPopup] read scale factor is: " + strfactor);
				scaleFactor = Integer.valueOf(strfactor.trim()) / 100.0f;
			}catch (Exception e1) {
				e1.printStackTrace();
				scaleFactor = 1.0f;
			}
		}
		float pivotY = popupPos.getFloat("pivotY", 0.0f);
		float pivotX = popupPos.getFloat("pivotX", 0.0f);
		Log.d(TAG, "[addPopup] rawX: " + rx + " rawY: " + ry);
		params.x = rx;
		params.y = ry;

		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.gravity = Gravity.TOP;

		if (relativeLayout != null) {
			Log.d(TAG, "[addPopup] clear previous popup window...");
			removePopup(c);
		}
		relativeLayout = new FloatRelativeLayout(c, params, empList);
		wm.addView(relativeLayout, params);
		
		relativeLayout.setScaleEnabled(true);
		relativeLayout.scale(scaleFactor, pivotX, pivotY);

		timer = new Timer();
		timer.scheduleAtFixedRate(new DetectCallSceenTask(c), 5000, 5000);
	}

	// remove popup window
	public void removePopup(Context c) {
		Log.d(TAG, "[removePopup]");
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		if (relativeLayout != null) {
			relativeLayout.removeAllViews();
			wm.removeView(relativeLayout);
			relativeLayout = null;
			timer.cancel();
		} else {
			Log.w(TAG, "[removePopup] line layout is NULL");
		}
	}

	private class DetectCallSceenTask extends TimerTask {
		private Context mContext;

		public DetectCallSceenTask(Context context) {
			mContext = context;
		}

		@Override
		public void run() {
			if (isCallSceenOn() == false) {
				timer.cancel();
				Message message = new Message();
				message.what = 1;
				message.obj = mContext;
				handler.sendMessage(message);
			}
		}

		private boolean isCallSceenOn() {
			Log.d(TAG, ">>>isCallSceenOn<<<");
			ActivityManager manager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = manager
					.getRunningTasks(1);
			Log.d(TAG,
					"[isCallSceenOn] top activity class name: "
							+ taskInfo.get(0).topActivity.getClassName());
			if ("com.android.phone.InCallScreen"
					.equals(taskInfo.get(0).topActivity.getClassName())
					|| "com.android.phone.PrivilegedOutgoingCallBroadcaster"
							.equals(taskInfo.get(0).topActivity.getClassName())
					|| "com.android.phone.OutgoingCallBroadcaster"
							.equals(taskInfo.get(0).topActivity.getClassName())
					|| "com.sonyericsson.home.HomeActivity".equals(taskInfo
							.get(0).topActivity.getClassName())) {
				Log.d(TAG, "[isCallSceenOn] true ");
				return true;
			} else {
				Log.d(TAG, "[isCallSceenOn] false ");
				return false;
			}
		}
	}

}
