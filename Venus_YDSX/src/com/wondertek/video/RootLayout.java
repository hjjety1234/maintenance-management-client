package com.wondertek.video;

import android.content.Context;
import android.util.Log;
import android.widget.AbsoluteLayout;

public class RootLayout extends AbsoluteLayout {

	private static final String TAG = "RootLayout";
	private OnResizeListener mListener; 
	private int maxheight = 0;

	public interface OnResizeListener { 
		void OnResize(int maxh,int h); 
	} 

	public void setOnResizeListener(OnResizeListener l) { 
		mListener = l; 
	} 

	public RootLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSizeChanged:: width = " + w + " height = " + h + " oldWidth = " + oldw + " oldHeight = " + oldh);
		//TODO send oldh-h
		if(maxheight == 0)
			maxheight = h;
		
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			mListener.OnResize(maxheight,maxheight - h); 
		} 
	}

}
