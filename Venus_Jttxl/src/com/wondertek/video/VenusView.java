package com.wondertek.video;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;


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
		holder.setFormat(PixelFormat.RGBA_8888);
	//	holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);

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
	
	GL10 mGL;
	EGL10 mEgl;
    EGLDisplay mEglDisplay;
    EGLSurface mEglSurface;
    EGLConfig mEglConfig;
    EGLContext mEglContext;
    private int[] mValue;
    private int findConfigAttrib(EGL10 egl, EGLDisplay display,
            EGLConfig config, int attribute, int defaultValue) {

        if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            return mValue[0];
        }
        return defaultValue;
    }
    
	public void eglInitialize()
	{
		Util.Trace("Init opengl thread id=" + Thread.currentThread().getId());
		mEgl = (EGL10) EGLContext.getEGL();
		mValue = new int[2];
		
		mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        int[] version = new int[2];
        if(!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }
        
        int[] configSpec={
        EGL10.EGL_RED_SIZE, 8,
        EGL10.EGL_GREEN_SIZE, 8,
        EGL10.EGL_BLUE_SIZE, 8,
        EGL10.EGL_ALPHA_SIZE, 8,
        EGL10.EGL_DEPTH_SIZE, 0,
        EGL10.EGL_STENCIL_SIZE, 0,
        EGL10.EGL_NONE};
        
        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, null, 0,num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }

        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, numConfigs,num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        
        for (EGLConfig config : configs) {
        	int d = findConfigAttrib(mEgl, mEglDisplay, config, EGL10.EGL_DEPTH_SIZE, 0);
        	if (d >= configSpec[9] )
        	{
	            int r = findConfigAttrib(mEgl, mEglDisplay, config, EGL10.EGL_RED_SIZE, 0);
	            int g = findConfigAttrib(mEgl, mEglDisplay, config, EGL10.EGL_GREEN_SIZE, 0);
	            int b = findConfigAttrib(mEgl, mEglDisplay, config, EGL10.EGL_BLUE_SIZE, 0);
	            int a = findConfigAttrib(mEgl, mEglDisplay, config, EGL10.EGL_ALPHA_SIZE, 0);
	            if ((r == configSpec[1]) && (g == configSpec[3]) && (b == configSpec[5]) && (a == configSpec[7])){
	        		mEglConfig = config;
	        		break;
	            }
        	}
        }

        if (mEglConfig == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, null);
        if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
            mEglContext = null;
            throw new RuntimeException("eglCreateContext failed");
        }

       
        mEglSurface = null;
	}
	 private void eglDestroySurface() {
         if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
             mEgl.eglMakeCurrent(mEglDisplay, 
            		 EGL10.EGL_NO_SURFACE,
                     EGL10.EGL_NO_SURFACE,
                     EGL10.EGL_NO_CONTEXT);

             mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
             mEglSurface = null;
         }
     }
	 
	public boolean eglCreateSurface(int w, int h) {
        if (mEgl == null) {
            throw new RuntimeException("egl not initialized");
        }
        if (mEglDisplay == null) {
            throw new RuntimeException("eglDisplay not initialized");
        }
        if (mEglConfig == null) {
            throw new RuntimeException("mEglConfig not initialized");
        }

        eglDestroySurface();

        try {
        	mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, getHolder(), null);
        } catch (IllegalArgumentException e) {
        	Util.Trace("eglCreateWindowSurface error =" +mEgl.eglGetError());
        }
        
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
            	Util.Trace("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }
            return false;
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
        	Util.Trace("eglMakeCurrent error "+ mEgl.eglGetError());
            return false;
        }

        mGL = (GL10)mEglContext.getGL();
        mGL.glClearColor(0, 0, 0, 0);
        mGL.glShadeModel(GL10.GL_SMOOTH);
        mGL.glDisable(GL10.GL_DEPTH_TEST);
        mGL.glDisable(GL10.GL_STENCIL_TEST);
        mGL.glEnable(GL10.GL_DITHER);  
        mGL.glEnable(GL10.GL_SCISSOR_TEST);
        mGL.glEnable(GL10.GL_BLEND);
        mGL.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mGL.glPixelStorei(GL10.GL_PACK_ALIGNMENT ,4);
        mGL.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT ,4);
  
		mGL.glViewport(0, 0, w, h);
		mGL.glMatrixMode(GL10.GL_PROJECTION);
		mGL.glLoadIdentity();
		mGL.glOrthof (0, w, h, 0, 0, 10);
		mGL.glMatrixMode(GL10.GL_TEXTURE);
		mGL.glLoadIdentity();
		mGL.glMatrixMode(GL10.GL_MODELVIEW);
		mGL.glLoadIdentity();
		mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
       
		this.venusActivity.nativeEglOnCreateSurface();
        return true;
    }
	
	 public void eglSwap() {
     	if (mEglSurface!=null && mEglContext!=null ){
			if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
				int swapError = mEgl.eglGetError();
				if (swapError == EGL11.EGL_CONTEXT_LOST)
				{
					Util.Trace("egl context lost tid=");
				}
				else
				{
					Util.Trace("eglSwapBuffers error=" + swapError);
				}
			}
     	}
	}
	
	 public void eglFinish() {
		 if (mEglContext != null) {
        	 this.venusActivity.nativeEglOnDestroySurface();
        	 eglDestroySurface();
             if (!mEgl.eglDestroyContext(mEglDisplay, mEglContext)) {
            	 Util.Trace("eglDestroyContext error "+ mEgl.eglGetError());
             }
             mEglContext = null;
         }
         if (mEglDisplay != null) {
             mEgl.eglTerminate(mEglDisplay);
             mEglDisplay = null;
         }
     }
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		Util.Trace("eglCreateSurface w="+width+" height="+height);
	//	this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);
		eglCreateSurface(width,height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Util.Trace("eglInitialize error=");
	//	this.venusActivity.nativeupdatemaincanvas(holder.getSurface(),sdkint);
		eglInitialize();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Util.Trace("eglFinish error=");
	//	this.venusActivity.nativeupdatemaincanvas(null,sdkint);
		eglFinish();
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
			this.venusActivity.update_fast = 0;
		case 0x5:	// ACTION_POINTER_DOWN
			type = Util.TOUCH_DOWN;
			touch[1] = event.getPointerId(actionIndex);
			touch[2] = 1;
			touch[3] = event.getPointerId(actionIndex);
			touch[4] = (int) event.getX(actionIndex);
			touch[5] = (int) event.getY(actionIndex);
			break;
		case MotionEvent.ACTION_UP:
			this.venusActivity.update_fast = this.venusActivity.update_fastest;
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