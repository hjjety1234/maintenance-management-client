package com.wondertek.video.update;

import android.content.Context;

public class UpdateObserver {
	private static final String TAG = "UpdateObserver";
	private Context mContext = null;
	private String appKey = null;
	private String version = null;
	private ProgressNofity notify = null; 
	// update information 
	private String releaseLog = null;
	private String localApkPath = null;
	private String localPatchPath = null;
	private String localNewApkPath = null;
	private String remoteApkUri = null;
	private String remotePatchUri = null;
	private long remoteApkSize = 0;
	private long remotePatchSize = 0;
	
	
	// initialize member variables
	public UpdateObserver(Context context) {
		mContext = context;
	}
	
	// process message push 
	public void procMessage(String msg) {
	}
	
	// if this is a update message
	private boolean isUpdateMessage(String msg) {
		return true;
	}
	
	// get update information
	private boolean getUpdateInfo() {
		return true;
	}
	
	// show version information
	private void showAlterDialog() {
		return;
	}	
}
