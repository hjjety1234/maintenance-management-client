package com.wondertek.video.arplugin;


import com.ahmobile.ar.Dominoes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ArPluginMgr {
	private static final String TAG = "ArPluginMgr";
	private Context mContext;
	
	public ArPluginMgr(Context cxt) {
		mContext = cxt;
	}
	
    public void javaStartArPlugin(String name) {
        Log.d(TAG, "javaStartArPlugin Name: " + name);
    	if (name != null && !name.trim().equals("")) {
    		if (name.trim().equals("Dominoes")) {
                startArPluginActivity(Dominoes.class);
    		}
    	}
    }
	
	public void startArPluginActivity(Class<? extends Activity> cls) {
		Log.d(TAG, ">>>startArPluginActivity<<<");
		Intent intent = new Intent(mContext, cls);
		mContext.startActivity(intent);
	}

}
