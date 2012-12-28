package com.wondertek.video.phonegap;

import android.util.Log;
import android.view.View;

import com.wondertek.activity.AppActivity;
import com.wondertek.video.VenusActivity;

public class PhonegapObserver {
	private static String TAG = "PhonegapObserver";
	private static PhonegapObserver instance = null;
	private VenusActivity venusHandle = null;

	
	
	private PhonegapObserver(VenusActivity va) {
		venusHandle = va;
	}

	public static PhonegapObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new PhonegapObserver(va);
		}

		return instance;
	}
	
	public void createWindow(int nX,int nY,int nWidth, int nHeight)
	{
		((AppActivity)VenusActivity.appActivity).setviewsize(nX,nY,nWidth,nHeight);
	}
	
	public void openUrl(String url)
	{
		((AppActivity)VenusActivity.appActivity).loadUrl(url);
		venusHandle.setWebViewRoot(((AppActivity) VenusActivity.appActivity).GetWebViewRoot());
		show(true);
	}
	
	public void show(boolean bShow)
	{
		if(bShow)
			((AppActivity) VenusActivity.appActivity).GetWebViewRoot().setVisibility(View.VISIBLE);
		else
			((AppActivity) VenusActivity.appActivity).GetWebViewRoot().setVisibility(View.INVISIBLE);
	}
	
	public void command(int ncmd)
	{
		Log.d(TAG, "phonegap command");
	}
	
	public boolean isRun()
	{
		if(((AppActivity) VenusActivity.appActivity).GetWebViewRoot().getVisibility() == View.VISIBLE)
            return true;
		else
            return false;
	}
	
}