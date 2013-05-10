package com.wondertek.video.caller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

/**
 * 业务配置文件读写类
 * 
 * @author 何武
 * 
 */
public class ConfigUtil {
	private static final String TAG = "ConfigUtil";

	/**
	 * 获取配置文件中对应键的值
	 * 
	 * @return 返回配置文件中键对应的值
	 */
	public static String getValue() {
		Log.d(TAG, ">>>getValue<<<");
		FileInputStream fis = null;
		try {
			File file = new File(Constants.SCALE_FACTOR_FILE);
			if (file.exists() == false)
				file.createNewFile();
			fis = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 设置配置文件中的值
	 * 
	 * @param value
	 */
	public static void setValue(String value) {
		Log.d(TAG, ">>>setValue<<<");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			File file = new File(Constants.SCALE_FACTOR_FILE);
			if (file.exists() == false)
				file.createNewFile();
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			osw.write(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (osw != null)
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
