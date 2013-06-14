package com.wondertek.video.call;

import android.content.Intent;
import android.net.Uri;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class CallObserver {
	public static final String TAG = "call";

	private static CallObserver instance = null;
	
	private CallObserver(VenusActivity va) {

	}
	
	public static CallObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new CallObserver(va);
		}

		return instance;
	}
	
	public void initCall(final String phoneNumber, int nType, int nCall) {
		Util.Trace(TAG + "------on initcall");
		Util.Trace(TAG + "phoneNumber: " + phoneNumber);
		if (phoneNumber == null)
			return;

		String strPhoneNumber = "tel:" + phoneNumber;
		
		Intent intent = new Intent((nCall==1)? Intent.ACTION_CALL:Intent.ACTION_DIAL);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(strPhoneNumber));
		Util.Trace(TAG + "intent: " + intent);
		VenusActivity.appActivity.startActivityForResult(intent, VenusActivity.CALL_RESULT);
	}
	
	public void callback()
	{
		
	}
}
