package com.wondertek.video.update;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class UpdateService extends Service {
	private static final String TAG = "UpdateService";
	private static Context mContext;
	private static UpdateLayout updateLayout = null;
	private static UpdateInfo updateInfo = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// save update information
	public static void setUpdateInfo(UpdateInfo updateInfo, Context context) {
		if (updateInfo == null) {
			Log.w(TAG, "[setUpdateInfo] updateInfo is null.");
		}
		UpdateService.updateInfo = updateInfo;
		UpdateService.mContext = context;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showUpdateInfo();
		return super.onStartCommand(intent, flags, startId);
	}

	// Static method to start the service
	public static void actionStart(Context ctx) {
		Log.d(TAG, ">>>actionStart<<<");
		mContext = ctx;
		Intent i = new Intent(ctx, UpdateService.class);
		ctx.startService(i);
	}

	// Static method to stop the service
	public static void actionStop(Context ctx) {
		Log.d(TAG, ">>>actionStop<<<");
		mContext = ctx;
		Intent i = new Intent(ctx, UpdateService.class);
		ctx.stopService(i);
	}

	// show update information
	public void showUpdateInfo() {
		Log.d(TAG, ">>>showUpdateInfo<<<");
		if (updateInfo == null) {
			Log.w(TAG, "[showUpdateInfo] updateInfo is null.");
			return;
		}
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

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

		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.gravity = Gravity.CENTER;

		if (updateLayout != null)
			wm.removeView(updateLayout);

		updateLayout = new UpdateLayout(this);
		wm.addView(updateLayout, params);
		updateLayout.showUpdateInfo(updateInfo);
	}

	public static void removeUpdateInfo() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		if (updateLayout != null) {
			wm.removeView(updateLayout);
			updateLayout = null;
		}
	}

}
