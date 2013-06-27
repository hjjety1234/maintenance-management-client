package com.wondertek.video.fractalad;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.fractalist.android.ads.ADView;
import com.fractalist.android.ads.AdStatusListener;
import com.fractalist.android.ads.FullScreenAdCloseListener;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class FractalADPlugin implements AdStatusListener, FullScreenAdCloseListener{		
		
	private static FractalADPlugin instance = null;
	private VenusActivity venusHandle = null;
	private ADView m_adView = null;
	private boolean bSendStatus = false;
	
	private static int E_ONFAIL 	= 0;
	private static int E_ONCLICK 	= 1;
	private static int E_ONRECEIVE 	= 2;
	private static int E_ONREFRESH 	= 3;
	private static int E_FSCLOSE 	= 4;
	private static int E_START 		= 5;
	private static int E_STOP 		= 6;
	private static int E_ERROR 		= 7;
	
	static {
    	System.load(VenusApplication.appAbsPath + "/lib2/fractalad/libfractalad.so");
    }
	
	private FractalADPlugin(VenusActivity va)
	{
		venusHandle = va;
		Handler hand=new Handler(){
        	public void handleMessage(Message msg) {
        		Log.d("Activity","msg.what =" + msg.what);
        		if(bSendStatus)
        		{
	                switch (msg.what) {
	                case 0:	
	                	nativeadstatus(E_STOP);
	        			break;
	        		case 1:	//Started
	        			nativeadstatus(E_START);
	        			break;
	        		default:
	        			nativeadstatus(E_ERROR);
	        		}
        		}
        	}
        };
        ADView.setPublishId("217");
        m_adView=new ADView(venusHandle.appActivity);
        m_adView.setStateHander(hand);
        m_adView.showFullScreenAd();
        m_adView.setFreshInterval(30);
        m_adView.setShowCloseButton(true);        
        m_adView.setFadeImage(true);
        m_adView.setBackgroundColor(Color.TRANSPARENT);
        m_adView.setAdStatusListener(this);
        m_adView.setFullScreenAdCloseListener(this);
        m_adView.setVisibility(View.INVISIBLE);
	}
	
	public View GetFractalADView()
	{
		return m_adView;
	}
	
	public void javaInit()
	{
		bSendStatus = true;
	}
	
	public void javaOpen()
	{
		m_adView.setVisibility(View.VISIBLE);
	}
	
	public void javaSetRect(int l, int t, int w, int h)
	{
		m_adView.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h, l, t));
	}
	
	public void javaClose()
	{
		m_adView.setVisibility(View.INVISIBLE);
	}
	
	public static FractalADPlugin getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new FractalADPlugin(va);
		}
		
		return instance;
	}

	@Override
	public void fullScreenAdClose(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		if(bSendStatus)
			nativeadstatus(E_ONCLICK);
	}

	@Override
	public void onFail() {
		// TODO Auto-generated method stub
		if(bSendStatus)
			nativeadstatus(E_ONFAIL);
	}

	@Override
	public void onReceiveAd() {
		// TODO Auto-generated method stub
		if(bSendStatus)
			nativeadstatus(E_ONRECEIVE);
	}

	@Override
	public void onRefreshAd() {
		// TODO Auto-generated method stub
		if(bSendStatus)
			nativeadstatus(E_ONREFRESH);
	}
	
	public native void nativeadstatus(int eventID);
}
