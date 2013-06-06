package com.wondertek.video.caller;

import android.util.Log;

import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.environment2.Environment2;

public class Constants {
	private static final String TAG = "Constants";

	public static String getLocPicDir() {
		return getAbsolutePath("picturedownload/");
	}
	public static final String REQ_PIC_URL_PREFIX = "http://120.209.131.147:8088/mobile/image/";
	public static final String RES_PIC_URL_PREFIX = "http://120.209.131.147:8088/";
	public static final String CALLER_INFO_URL_PREFIX = "http://120.209.131.147:8088/mobile/UserInfo/download?key=";
	

	//public static final String REQ_PIC_URL_PREFIX = "http://120.209.138.173:8080/mobile/image/";
	//public static final String RES_PIC_URL_PREFIX = "http://120.209.138.173:8080/";
	//public static final String CALLER_INFO_URL_PREFIX = "http://120.209.138.173:8080/mobile/UserInfo/download?key=";
	public static final String RES_LOGO_URL_PREFIX = "http://120.209.138.173:8080/resources/mobileImg/";

	private static String getAbsolutePath(String filename) {
		/*
		if (Environment2.isSecondaryExternalStorageAvailable()
				&& Environment2.isExternalStorageEmulated() == true) {
			try {
				filename = Environment2.getSecondaryExternalStorageDirectory()
						.getAbsolutePath() + "/" + filename;
				Log.d(TAG, "[getAbsolutePath] secondary storage: " + filename);
				return filename;
			} catch (Exception e) {
				Log.d(TAG, "[getAbsolutePath] exception: " + e.getMessage());
			}
		}*/
		String internalStorage = VenusActivity.getInstance().javaGetSDPath();
		if (!internalStorage.equals("")) {
			filename = internalStorage + "/" + filename;
			Log.d(TAG, "[getAbsolutePath] internal storage: " + filename);
		} else {
			filename = VenusApplication.appAbsPath + "module/com_wondertek_tx/" + filename;
			Log.d(TAG, "[getAbsolutePath] application private storage: "
					+ filename);
		}
		return filename;
	}

	public static String getDatabaseName() {
		return getAbsolutePath("sqlitedownload/jttxlDatabase");
	}

	public static String getTempDatabaseName() {
		return getAbsolutePath("sqlitedownload/jttxlDatabase_encrypted");
	}

	// SCALE FACTOR FILE
	public static String getScaleFactorFile() {
		return getAbsolutePath("sqlitedownload/config.json");
	}
}
