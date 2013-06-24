package com.wbtech.ums;

import android.util.Log;

import com.wbtech.common.CommonUtil;
import com.wondertek.video.VenusActivity;

public class UmsAgentInterface {

	private static final String TAG = UmsAgentInterface.class.getSimpleName();
	
	private static boolean canPause = false;
	
	public static void javaOnEvent(String event_id, String label) {
		Log.d(TAG, "javaOnEvent: " + event_id + " " + label);
		if (event_id.equals("postClientData")) {
			CommonUtil.updateConfig();
			UmsAgent.postClientData(VenusActivity.appActivity);
		}else if (event_id.equals("onLoadStart")) {
			UmsAgent.onLoadStart(VenusActivity.appActivity);
		} else if (event_id.equals("onLoadFinish")) {
			UmsAgent.onLoadFinish(VenusActivity.appActivity);
		} else {
			UmsAgent.onEvent(VenusActivity.appActivity, event_id, label);
		}
	}
	
	public static void javaOnError(String msg) {
		Log.i(TAG, "javaOnError: " + msg);
		UmsAgent.onError(VenusActivity.appActivity, msg);
	}
	
	public static void javaSetDefaultReportPolicy(int reportModel) {
		Log.i(TAG, "javaSetDefaultReportPolicy: " + reportModel);
		UmsAgent.setDefaultReportPolicy(VenusActivity.appActivity, reportModel);
	}
	
	public static void javaOnPause() {
		if (canPause == true) {
			Log.i(TAG, "javaOnPause");
			new Thread(){
				@Override
				public void run() {
					// onPause会调用网络接口，耗时比较长，因此在工作线程执行
					UmsAgent.onPause(VenusActivity.appActivity);
					Log.i(TAG, "javaOnPause finished!");
				}
			}.start();
			canPause = false;
		}
	}

	public static void javaOnResume(final String scene, final String tag) {
		Log.i(TAG, "javaOnResume: " + scene + tag);
		new Thread(){
			@Override
			public void run() {
				int i = 0;
				while (i++ < 10000) {
					if ( canPause == false ) {
						UmsAgent.onResume(VenusActivity.appActivity, scene, tag);
						canPause = true;
						Log.i(TAG, "javaOnResume finished!");
						break;
					}
				}
			}
		}.start();
	}
}
