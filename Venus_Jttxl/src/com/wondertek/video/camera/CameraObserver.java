package com.wondertek.video.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
	public boolean bFlag = false;
	
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
			Toast.makeText(VenusActivity.appActivity, "sd卡不存在",Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	public void initCamera(String path, int photoType) {

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
		if(bFlag)
		{
			File f = new File(dir,this.strPhotoName);
			Uri u = Uri.fromFile(f);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
		}
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
		Bitmap bimap = checkMaxImage(this.strPhotoPath+this.strPhotoName);
		if(bimap!=null)
		{
			disposeImage(bimap, 0,this.strPhotoPath+ "temp" +this.strPhotoName);
			this.strPhotoName = "temp" +this.strPhotoName;
		}
		File dir=new File(this.strPhotoPath+this.strPhotoName);
		if(dir.exists())
			nativecamerareturn(this.strPhotoPath,this.strPhotoName,(long)0,this.photoType,this.photoCreateTime);
	}
	
	public void callback(String strPath,int orientation)
	{
		if(!strPath.trim().equals(""))
		{
			disposeImage(strPath, orientation,this.strPhotoPath+this.strPhotoName);
			File dir=new File(this.strPhotoPath+this.strPhotoName);
			if(dir.exists())
				nativecamerareturn(this.strPhotoPath,this.strPhotoName,(long)0,this.photoType,this.photoCreateTime);
		}
	}

	public void callback(Bitmap bitmap) {
		// TODO Auto-generated method stub
		try {
			FileOutputStream out = new FileOutputStream(this.strPhotoPath+this.strPhotoName);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			bFlag = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File dir=new File(this.strPhotoPath+this.strPhotoName);
		if(dir.exists())
			nativecamerareturn(this.strPhotoPath,this.strPhotoName,(long)0,this.photoType,this.photoCreateTime);
	}
	
	public Bitmap checkMaxImage(String srcPath)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
		if(bitmap.getWidth()>1024||bitmap.getHeight()>1024)
			return bitmap;
		return null;
	}
	
	public void disposeImage(Bitmap bitmap,int orientation, String decPath)
	{
		Matrix matrix = new Matrix();
		int max = bitmap.getWidth()>bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
		float fScale = (float) (1000.0/max);
		if(fScale>1.0 && fScale <= 0.0001)
		{
			fScale = 1;
		}
		matrix.postScale(fScale, fScale);
		matrix.postRotate(orientation);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		try {
			FileOutputStream out = new FileOutputStream(decPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disposeImage(String srcPath,int orientation, String decPath)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
		disposeImage(bitmap,orientation,decPath);
	}
}
