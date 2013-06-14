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
		Log.d(TAG, "javaOnError: " + msg);
		UmsAgent.onError(VenusActivity.appActivity, msg);
	}
	
	public static void javaSetDefaultReportPolicy(int reportModel) {
		Log.d(TAG, "javaSetDefaultReportPolicy: " + reportModel);
		UmsAgent.setDefaultReportPolicy(VenusActivity.appActivity, reportModel);
	}
	
	public static void javaOnPause() {
		if (canPause == true) {
			Log.d(TAG, "javaOnPause");
			new Thread(){
				@Override
				public void run() {
					// onPause会调用网络接口，耗时比较长，因此在工作线程执行
					UmsAgent.onPause(VenusActivity.appActivity);
					canPause = false;
				}
			}.start();
		}
	}

	public static void javaOnResume(final String scene, final String tag) {
		Log.d(TAG, "javaOnPause: " + scene + tag);
		new Thread(){
			int i = 0;
			@Override
			public void run() {
				while (canPause == false && i++ < 2000) {
					UmsAgent.onResume(VenusActivity.appActivity, scene, tag);
					canPause = true;
				}
			}
		}.start();
	}
}
