package com.phonegap;

import org.json.JSONArray;
import org.json.JSONException;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.wondertek.video.VenusActivity;

import android.util.Log;
import android.view.View;

public class WDPage extends Plugin {
	public static final String TAG = "WDPage";
	public static final String ACTION_LOADPAGE="loadpage";
	public static final String ACTION_VIDEOURL="videourl";
	public static final int MSG_LOADPAGE_ID = 0;
	public static final int MSG_VIDEOURL_ID = 1;
	
	public String callbackid;
	private static WDPage instance = null;
	public static WDPage getInstance()
	{
		if(instance == null)
		{
			instance = new WDPage();
		}
		
		return instance;
	}
	
	/**
     * Constructor.
     */
    public WDPage() {
    	instance = this;
    }
	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		// TODO Auto-generated method stub
		PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        try {
        	if (action.equals(ACTION_LOADPAGE)) {
        		this.loadpage(args.getString(0));
        	}
        	else if(action.equals(ACTION_VIDEOURL)){
        		this.videourl(args.getString(0));
        	}
            return new PluginResult(status, result);
        } catch (JSONException e) {
            return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
        }
	}
	
	
	@Override
	public boolean isSynch(String action) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onOverrideUrlLoading(String url) {
		// TODO Auto-generated method stub
		Log.d(TAG, "url=" +url);
		if (url.startsWith(ACTION_LOADPAGE)) {
			loadpage(url.substring(ACTION_LOADPAGE.length()+1).replace('/', '\\'));
        }
		else if(url.startsWith(ACTION_VIDEOURL)){
			videourl(url.substring(ACTION_VIDEOURL.length()+1).replace('/', '\\'));
		}
		return true;
		//super.onOverrideUrlLoading(url);
	}
	
	public void loadpage(String url)
	{
		VenusActivity.getInstance().webViewRoot.setVisibility(View.INVISIBLE);
		VenusActivity.getInstance().sendWDPageEvent(MSG_LOADPAGE_ID, url);
	}
	
	public void videourl(String url)
	{
		VenusActivity.getInstance().webViewRoot.setVisibility(View.INVISIBLE);
		VenusActivity.getInstance().sendWDPageEvent(MSG_VIDEOURL_ID, url);
	}
}
