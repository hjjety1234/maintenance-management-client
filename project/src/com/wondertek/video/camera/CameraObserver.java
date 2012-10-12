package com.wondertek.video.camera;

import java.io.File;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import android.database.Cursor;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class CameraObserver {
	private static String TAG = "CameraObserver";
	private static CameraObserver instance = null;
	private VenusActivity venusHandle = null;

	public Camera camera;
	private Bitmap cameraBitmap;
	
	public String strPhotoPath;
	public String strPhotoName;
	public long photoSize;
	public int photoType;
	public String photoCreateTime;
	public int itemCount = 0;


	public final static int EPHOTOTYPE_PNG = 0;
	public final static int EPHOTOTYPE_JPG = 1;
	public final static int EPHOTOTYPE_BMP = 2;
	public final static int EPHOTOTYPE_STREAM = 3;

	public void init() {
		Util.Trace(TAG + "CameraObserver: init()");
		
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	private CameraObserver(VenusActivity va)
	{
		venusHandle = va;
	}
	
	
	
	public static CameraObserver getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new CameraObserver(va);
		}
		
		return instance;
	}

	public boolean isCameraReady() {
		String status=Environment.getExternalStorageState();

		if(!status.equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(VenusActivity.appActivity, "sd¿¨²»´æÔÚ",Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public void initCamera(final String path, int photoType) {

		if (isCameraReady()) {
			this.strPhotoPath = Environment.getExternalStorageDirectory() +"/image/";//"/data/data/com.wondertek.mobilevideo3/image/";
			this.strPhotoName = path.substring(path.lastIndexOf("/")+1);//System.currentTimeMillis() + "." + "jpg";
			this.photoType = photoType;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			this.photoCreateTime = formatter.format(System.currentTimeMillis());
		}
	}

	public void releaseCamera() {
	}
	
	public void runCamera() {
		File dir =new File(this.strPhotoPath);
		if(!dir.exists())
			dir.mkdirs();
		
		File file =new File(this.strPhotoPath+this.strPhotoName);
		if(file.exists())
			this.strPhotoName = System.currentTimeMillis() + "." + "jpg";
		
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		File f = new File(dir,this.strPhotoName);
		
		Uri u = Uri.fromFile(f);
		
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
		
		VenusActivity.appActivity.startActivityForResult(intent, VenusActivity.CAMERA_RESULT);
	}
	 
	public void runAlbum()
	{
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        VenusActivity.appActivity.startActivityForResult(intent, VenusActivity.REQUEST_PICKER_ALBUM);
	}

	public void Album_SetParams(String imgPath)
	{
		this.strPhotoPath = imgPath.substring(0, imgPath.lastIndexOf("/") + 1);
		this.strPhotoName = imgPath.substring(imgPath.lastIndexOf("/")+1);
		this.callback();
	}
	
	public native void nativecamerareturn(String strPhotoPath, String strPhotoName, long photoSize, int photoType, String photoCreateTime);
	public void callback() {
		// TODO Auto-generated method stub
		File dir=new File(this.strPhotoPath+this.strPhotoName);
		if(dir.exists())
			nativecamerareturn(this.strPhotoPath,this.strPhotoName,(long)0,this.photoType,this.photoCreateTime);
	}
	
	
}
