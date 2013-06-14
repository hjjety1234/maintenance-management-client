package com.wondertek.video.browser;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class SysBrowserObserver {
	public static final String TAG = "Browser";
	public String url;
	public VenusActivity venusHandle;
	private static SysBrowserObserver instance = null;
	
	private SysBrowserObserver(VenusActivity va) {
		venusHandle = va;
	}
	
	public static SysBrowserObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new SysBrowserObserver(va);
		}
		return instance;
	}
	
	void javaopenURL(String url)
	{
		if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
			return;
		}

		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			VenusActivity.appActivity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean javaopenFile(String filePaths)
	{
		Util.Trace(TAG + "filePaths: " + filePaths);

		File pFile = new File(filePaths);
		if (!pFile.exists())
		{
			Util.Trace(TAG + "!!! in if no exists");
			return false;
		}
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
  
		String type = getMIMEType(pFile);
		intent.setDataAndType(Uri.fromFile(pFile),type);
		VenusActivity.appActivity.startActivity(intent);
		
		return true;
	}
	
	private String getMIMEType(File pFile)
	{
		if (pFile == null)
			return null;
		
		String type = null;
		String fileName = pFile.getName();
		String end = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).toLowerCase(); 

		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")|| end.equals("xmf")
				||end.equals("ogg")||end.equals("wav"))
		{
			type = "audio"; 
		}
		else if(end.equals("3gp")||end.equals("mp4"))
		{
			type = "video";
		}
		else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
				end.equals("jpeg")||end.equals("bmp"))
		{
			type = "image";
		}
		else if(end.equals("apk")) 
		{  
			type = "application/vnd.android.package-archive"; 
		} 
		else
		{
			type="*";
		}
      
		type += "/*";
		
		return type; 
	}
}
