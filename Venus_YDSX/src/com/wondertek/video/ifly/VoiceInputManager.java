package com.wondertek.video.ifly;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.iflytek.recognizer.IRecognizedListener;
import com.iflytek.recognizer.VoiceRecognizer;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class VoiceInputManager {

	public static final String TAG = "VoiceInputManager";
	private static final String SCENE_ID = "SceneId";
	private static final String SESSION_ID = "Sid";
	private static final String RETURN_CODE = "ReturnCode";

	private static VoiceInputManager instance;

	private String mResultText = "";
	private Context mContext;
	VoiceRecognizer mDialog;

	private IRecognizedListener mListener = new IRecognizedListener() {
		@Override
		public void onRecognizedOver(Intent arg0) {
			Map<String, String> props = new HashMap<String, String>(8);
			Bundle bundle = arg0.getExtras();
			Set<String> keys = bundle.keySet();
			for (String key : keys) {
				android.util.Log.v(TAG, key + " = " + bundle.getString(key));
				if ("ReturnDesc".equals(key)) {
					// these attributes may cause error, so we discard them
				} else {
					props.put(key, bundle.getString(key));
				}
			}

			JSONObject obj = new JSONObject(props);
			mResultText = obj.toString();

			android.util.Log.v(TAG, "text: " + mResultText);
			android.util.Log.v(TAG, "length: " + mResultText.length());

			boolean ret = hasText(arg0.getStringExtra(RETURN_CODE));
			VenusActivity.getInstance().nativesendevent(
					Util.WDM_VOICEINPUT, ret ? 1 : 0, 0);
		}
	};

	public static VoiceInputManager getInstance() {
		if (instance == null) {
			instance = new VoiceInputManager();
		}
		return instance;
	}

	public VoiceInputManager() {
		mContext = VenusActivity.appActivity;
		mDialog = new VoiceRecognizer(mContext);
	}

	public boolean startRecognizer(String sceneId, String sId) {
		Intent intent = new Intent();
		intent.putExtra(SCENE_ID, sceneId);
		intent.putExtra(SESSION_ID, sId);
		mResultText = "";

		mDialog.startRecognize(intent, mListener);

		return true;
	}

	public String getLastText() {
		return mResultText;
	}

	private boolean hasText(String retCode) {
		return ("0000".equals(retCode) || "0001".equals(retCode));
	}
}
