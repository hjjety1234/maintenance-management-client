package com.wbtech.common;

import android.content.Context;

import com.wbtech.ums.UmsAgent;

public class PostClientData extends Thread {
	private static final Object eventObject = new Object();
	private Context paramContext;

	public PostClientData(Context context) {
		this.paramContext = context;
	}

	@Override
	public void run() {
		try {
			synchronized (eventObject){
				UmsAgent.postClientDataProc(paramContext);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
