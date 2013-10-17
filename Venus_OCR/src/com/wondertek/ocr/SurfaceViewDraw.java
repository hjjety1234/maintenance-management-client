package com.wondertek.ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SurfaceViewDraw extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.AutoFocusCallback {
	private static String TAG  = "SurfaceViewDraw";
	private SurfaceView svCamera = null;
	private TouchView mView = null;
	protected SurfaceHolder mSurfaceHolder = null;
	private Button btnTakePic;

	private Camera mCamera; 		// 这个是hardware的Camera对象
	private boolean isOpen = false; // 相机是否打开
	private ToneGenerator tone;
	private String imgPath = "/mnt/sdcard/ocr.jpg";
	
	private boolean bIsButtonClicked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideStatusBar();
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		svCamera = (SurfaceView) findViewById(R.id.svCamera);
		svCamera.setFocusable(true);  
		svCamera.setFocusableInTouchMode(true);
		svCamera.setClickable(true); 
		svCamera.setOnClickListener(new View.OnClickListener() { 
	        @Override 
	        public void onClick(View v) { 
	        	bIsButtonClicked = false;
	        	mCamera.autoFocus(SurfaceViewDraw.this); 
	        } 
	    }); 
		btnTakePic = (Button) findViewById(R.id.btnTakePic);
		btnTakePic.setOnClickListener(new ClickEvent());
		mSurfaceHolder = svCamera.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mView = (TouchView)findViewById(R.id.left_top_view);
		Intent intent = getIntent();
		String path = intent.getExtras().getString("image_path");
		if (path != null && path.trim().equals("") == false) {
			Log.d(TAG, "[onCreate] image path is : " + path);
			imgPath = path;
		}
	}
	
	// 识别按钮消息处理函数
	class ClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btnTakePic) {
				if (isOpen) {
					bIsButtonClicked = true;
					mCamera.autoFocus(SurfaceViewDraw.this);
				}
			}
		}
	}
	
	// 点击快门按钮的消息处理
	ShutterCallback mShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			if (tone == null)
				tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
			tone.startTone(ToneGenerator.TONE_PROP_BEEP);
		}
	};
	
	/**
	 * Jpeg格式压缩
	 */
	PictureCallback mjpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, ">>>onPictureTaken<<<");
			File imgFile = new File(imgPath);
			try {
				FileOutputStream fos = new FileOutputStream(imgFile);
				fos.write(data);
				fos.close();
				Log.v(TAG, "[onPictureTaken] New Image saved:" + imgFile);

			} catch (Exception error) {
				Log.d(TAG, "[onPictureTaken] " + imgPath + " not saved: " + error.getMessage());
			} finally {
				data = null;
				System.gc();
				if (getParent() == null) {
				    setResult(Activity.RESULT_OK, null);
				} else {
				    getParent().setResult(Activity.RESULT_OK, null);
				}
				finish();
			}
		}
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, ">>>surfaceCreated<<<");
		if (!isOpen) {
			mCamera = Camera.open();
			isOpen = true;
		}
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception exception) {
			Log.e(TAG, "Exception trying to set preview");
			if (mCamera != null){
				mCamera.release();
				mCamera = null;
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(TAG, ">>>surfaceChanged<<<");
		Log.i(TAG, "Preview: surfaceChanged() - size now " + w + "x" + h);
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		try {
			Camera.Parameters mParameters = mCamera.getParameters();
			mParameters.set("orientation", "landscape");
			for (Integer i : mParameters.getSupportedPreviewFormats()) {
				Log.i(TAG, "supported preview format: " + i);
			} 

			List<Size> sizes = mParameters.getSupportedPreviewSizes();
			for (Size size : sizes) {
				Log.i(TAG, "supported preview size: " + size.width + "x" + size.height);
			}
			mCamera.setParameters(mParameters); // apply the changes
		} catch (Exception e) {
			// older phone - doesn't support these calls
		}
		updateBufferSize(); // then use them to calculate
		Size p = mCamera.getParameters().getPreviewSize();
		Log.i(TAG, "Preview: checking it was set: " + p.width + "x" + p.height); // DEBUG
		mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, ">>>surfaceDestroyed<<<");
		if (isOpen) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			isOpen = false;
		}
	}

	/**
	 * 关闭相机
	 */
	public void closeCamera() {
		if (isOpen) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			isOpen = false;
		}
	}

	/**
	 * 停止拍照预览
	 */
	public void stopPreview() {
		if (isOpen) {
			mCamera.stopPreview();
		}
	}

	/**
	 * 启动拍照预览
	 */
	public void startPreview() {
		if (isOpen) {
			mCamera.startPreview();
		}
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isOpen) {
			closeCamera();
		}
	}

	// 在 Activity.setCurrentView()之前调用
	public void hideStatusBar() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window curWindow = this.getWindow();
		curWindow.setFlags(flag, flag);
	}

	public class SizeComparator implements Comparator<Camera.Size> {
		public SizeComparator() {
		}

		public int compare(Camera.Size paramSize1, Camera.Size paramSize2) {
			return paramSize2.width * paramSize2.height - paramSize1.width * paramSize1.height;
		}
	}
	
	private void updateBufferSize() {
		// prepare a buffer for copying preview data to
		int h = mCamera.getParameters().getPreviewSize().height;
		int w = mCamera.getParameters().getPreviewSize().width;
		int bitsPerPixel = ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat());
	}

	/* (non-Javadoc)
	 * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[], android.hardware.Camera)
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
	}

	/* (non-Javadoc)
	 * @see android.hardware.Camera.AutoFocusCallback#onAutoFocus(boolean, android.hardware.Camera)
	 */
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		Log.d(TAG, "onAutoFocus");
		if (bIsButtonClicked == true) {
			bIsButtonClicked = false;
			Camera.Parameters param = mCamera.getParameters();
			param.setPictureSize(1280, 720);
			mCamera.setParameters(param);
			btnTakePic.setBackgroundResource(R.drawable.pai);
			mCamera.takePicture(mShutterCallback, null, null, mjpegCallback);
		}
	}
}