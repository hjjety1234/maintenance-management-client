package com.wondertek.video.msgpush.mqtt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wondertek.video.caller.Constants;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

public class MsgBomb {
	private static final String TAG = "MsgBomb";
	private static String bombPath = null;
	private Context mContext;

	public MsgBomb(Context context) {
		this.mContext = context;
		ContextWrapper cw = new ContextWrapper(mContext);
		File directory = cw.getDir("", Context.MODE_PRIVATE);
		bombPath = directory.getAbsolutePath() + "/bomb.dat";
		Log.d(TAG, "[MsgBomb] bomb path: " + bombPath);
	}

	public boolean create() {
		Log.d(TAG, ">>>create<<<");
		File f = new File(bombPath);
		FileOutputStream fos = null;
		byte[] data = new byte[]{0x00}; 
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			Log.d(TAG, "create bomb failed!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void delete() {
		Log.d(TAG, ">>>create<<<");
		File f = new File(bombPath);
		if (f.exists()) {
			f.delete();
			Log.d(TAG, "[delete] " + bombPath + " has been deleted!");
		}
	}

	public boolean exists() {
		File f = new File(bombPath);
		return f.exists();
	}
	
	public void check() {
		if (exists() == true) {
			File db = new File(Constants.DATABASE_NAME);
			if (db.exists()) db.delete();
			System.exit(0);
		}
	}
}
