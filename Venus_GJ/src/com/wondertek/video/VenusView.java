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
	private final int MAX_TOUCHCOUNT = 10;
	private int sdkint;
	private int touch[];
	public VenusView(VenusActivity venusActivity, Context context) {
		super(context);
		this.venusActivity = venusActivity;
		sdkint= Build.VERSION.SDK_INT;
		touch = new int[2+MAX_TOUCHCOUNT*3];
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		holder.setFormat(PixelFormat.RGBA_8888);
		this.setFocusableInTouchMode(true);
		this.venusActivity.nativeinit(VenusActivity.fakeScreenWidth, VenusActivity.fakeScreenHeight, VenusActivity.fakeScreenStatusBarHeight, null, VenusActivity.mActivityFullName, VenusApplication.appAbsPath, VenusApplication.appPassiveStart);
		this.venusActivity.refashHandler.sendEmptyMessageDelayed(VenusActivity.GUIUPDATEIDENTIFIER, 50);

        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
            	Util.Trace("LongClick: (" + mLastMotionX + "," + mLastMotionY + ")");
            	VenusView.this.venusActivity.nativesendevent(Util.WDM_MOUSELONGPRESS, mLastMotionX, mLastMotionY);
            }
        };
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//add pj
		venusActivity.VenusViewHolder = holder;
		this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//add pj
		venusActivity.VenusViewHolder = holder;
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

	public void sendTouchEvent(MotionEvent event)
	{
		int count = event.getPointerCount();
		int action = (event.getAction() & 0xff);

		int type = touch[0];
		int actionIndex = ((event.getAction()>>8) & 0xff);
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
		case 0x5:	// ACTION_POINTER_DOWN
			type = Util.TOUCH_DOWN;
			touch[1] = event.getPointerId(actionIndex);
			touch[2] = 1;
			touch[3] = event.getPointerId(actionIndex);
			touch[4] = (int) event.getX(actionIndex);
			touch[5] = (int) event.getY(actionIndex);
			break;
		case MotionEvent.ACTION_UP:
		case 0x6:	// ACTION_POINTER_UP
			type = Util.TOUCH_UP;
			touch[1] = event.getPointerId(actionIndex);
			touch[2] = 1;
			touch[3] = event.getPointerId(actionIndex);
			touch[4] = (int) event.getX(actionIndex);
			touch[5] = (int) event.getY(actionIndex);
			break;
		case MotionEvent.ACTION_MOVE:
			type = Util.TOUCH_MOVE;
			touch[1] = event.getPointerId(actionIndex);
			touch[2] = count;
			for (actionIndex = 0; actionIndex < count; actionIndex++)
			{
				touch[actionIndex*3+3] = event.getPointerId(actionIndex);
				touch[actionIndex*3+4] = (int) event.getX(actionIndex);
				touch[actionIndex*3+5] = (int) event.getY(actionIndex);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			type = Util.TOUCH_CANCEL;
			touch[1] = 0;
			touch[2] = 0;
			break;
		}
		touch[0] = type;

		//Util.Trace("serializeTouchEvent "+action);
		this.venusActivity.nativesendtouchevent(touch);
	}

	long moveTick = 0;
	int moveX = 0;
	int moveY = 0;
	final int moveSense = 2;
	final long moveDelay = 25;	//ms
	public boolean onTouchEvent(MotionEvent event) {
		int count = event.getPointerCount(); 
		int nType = getTouchType(event.getAction());
		if (nType != MotionEvent.ACTION_DOWN)
			sendTouchEvent(event);

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

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			long tick = System.currentTimeMillis();
			if (Math.abs(moveTick-tick) < moveDelay || (Math.abs(moveX-x) +  Math.abs(moveY-y)) < moveSense)
				return true;
			else {
				moveTick = tick;
				moveX = x;
				moveY = y;
			}
		}

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

		if (nType == MotionEvent.ACTION_DOWN)
			sendTouchEvent(event);
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