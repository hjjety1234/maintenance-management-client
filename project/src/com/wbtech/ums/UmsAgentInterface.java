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
			UmsAgent.onPause(VenusActivity.appActivity);
			canPause = false;
		}
	}

	public static void javaOnResume(String scene, String tag) {
		if (canPause == false) {
			Log.d(TAG, "javaOnPause: " + scene);
			UmsAgent.onResume(VenusActivity.appActivity, scene, tag);
			canPause = true;
		}
	}
}
