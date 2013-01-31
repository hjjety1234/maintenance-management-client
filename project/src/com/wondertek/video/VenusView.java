package com.wondertek.video;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

class VenusView extends SurfaceView implements SurfaceHolder.Callback
{
	/**
	 * 
	 */
	private final VenusActivity venusActivity;
	private int sdkint;
	private SurfaceHolder holder;
	public VenusView(VenusActivity venusActivity, Context context) {
		super(context);
		this.venusActivity = venusActivity;
		holder = this.getHolder();
		holder.addCallback(this);
		sdkint= Build.VERSION.SDK_INT;
		this.setFocusableInTouchMode(true);
		this.venusActivity.nativeinit(VenusActivity.screenWidth, VenusActivity.screenHeight, VenusActivity.statusHeight, null, VenusActivity.mActivityFullName, VenusApplication.appAbsPath, VenusApplication.appPassiveStart);
		this.venusActivity.refashHandler.sendEmptyMessageDelayed(VenusActivity.GUIUPDATEIDENTIFIER, 50);

        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
            	Util.Trace("LongClick: (" + mLastMotionX + "," + mLastMotionY + ")");
            	VenusView.this.venusActivity.nativesendevent(3, mLastMotionX, mLastMotionY);
            }
        };
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		holder.setFormat(PixelFormat.RGBA_8888);
		this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		holder.setFormat(PixelFormat.RGBA_8888);
		this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.venusActivity.nativeupdatemaincanvas(null,sdkint);
	}

	private int mLastMotionX, mLastMotionY;

    private boolean isMoved;

    private Runnable mLongPressRunnable;

    private static final int TOUCH_SLOP = 20;
    
    public int getTouchType( int nAction )
    {
        switch(nAction&MotionEvent.ACTION_MASK)  
        {  
        case MotionEvent.ACTION_DOWN:  			
        case MotionEvent.ACTION_POINTER_1_DOWN: 
        case MotionEvent.ACTION_POINTER_2_DOWN:  
            return 1;  
        case MotionEvent.ACTION_MOVE:  
        	return 2;
        case MotionEvent.ACTION_POINTER_1_UP:  	
        case MotionEvent.ACTION_POINTER_2_UP:
        	return 3;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL: 
        	return 4;    
        }	 
         
        return 0; 
    }

	public boolean onTouchEvent(MotionEvent event) {
		int count = event.getPointerCount(); 
		int nType = getTouchType(event.getAction()); 
		if ( count == 1 )
		{   
			if ( this.venusActivity.nativetouchevent( count, nType, event.getPointerId(0), (int)event.getX(0), (int)event.getY(0),
					-1, 0, 0) != 0 )
			{
				return true; 
			}   
		} 
		else
		{
			if ( this.venusActivity.nativetouchevent( count, nType, event.getPointerId(0), (int)event.getX(0), (int)event.getY(0),
					event.getPointerId(1), (int)event.getX(1), (int)event.getY(1)) != 0 )
			{
				return true;
			} 
		}

		int x = (int) event.getX();
        int y = (int) event.getY();
		this.venusActivity.nativesendevent(event.getAction(), x, y);

		switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionX = x;
            mLastMotionY = y;
            isMoved = false;
            postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
            break;
        case MotionEvent.ACTION_MOVE:
            if(isMoved) break;
            if(Math.abs(mLastMotionX-x) > TOUCH_SLOP
                    || Math.abs(mLastMotionY-y) > TOUCH_SLOP) {
                isMoved = true;
                removeCallbacks(mLongPressRunnable);
            }
            break;
        case MotionEvent.ACTION_UP:
            removeCallbacks(mLongPressRunnable);
            break;
        }
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_CALL) || (keyCode == KeyEvent.KEYCODE_MENU) ||
			(keyCode == KeyEvent.KEYCODE_SEARCH) || (keyCode == 92)) {
			Util.Trace("ignore key:" + keyCode + " event:"+ event.toString());
			// return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_CALL)|| (keyCode == KeyEvent.KEYCODE_MENU) ||
			(keyCode == KeyEvent.KEYCODE_SEARCH) || (keyCode == 92)) {
			Util.Trace("ignore key:" + keyCode + " event:"+ event.toString());
			// return true;
		}

		return super.onKeyLongPress(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_CALL) || (keyCode == KeyEvent.KEYCODE_MENU)||
			(keyCode == KeyEvent.KEYCODE_SEARCH) || (keyCode == 92)) {
			Util.Trace( "ignore key:" + keyCode + " event:"+ event.toString());
			// return true;
		}

		return super.onKeyUp(keyCode, event);
	}
}