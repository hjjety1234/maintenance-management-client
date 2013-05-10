package com.wondertek.video.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wbtech.common.CommonUtil;
import com.wondertek.jttxl.R;

public class UpdateObserver {
	private static final String TAG = "UpdateObserver";
	private static UpdateObserver instance = null;
	private Context mContext = null;

	public static UpdateObserver getInstance(Context context) {
		if (instance == null) {
			instance = new UpdateObserver(context);
		}
		return instance;
	}

	// initialize member variables
	public UpdateObserver(Context context) {
		mContext = context;
	}

	// get update information
	private UpdateInfo requestUpdateInfo() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		// format request uri
		String appkey = CommonUtil.getAppKey(mContext);
		String strVersion = CommonUtil.getCurVersion(mContext);
		String requestUri = String
				.format("http://120.209.131.146/webcloud/sso/sso_upgrade.html?appkey=%s&version=%s",
						appkey, strVersion);
		Log.d(TAG, "[getUpdateInfo] request uri: " + requestUri);
		// do request
		try {
			response = httpclient.execute(new HttpGet(requestUri));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
						.readLine()) {
					builder.append(s);
				}
				String json = builder.toString();
				Log.d(TAG, "[getUpdateInfo] response: " + json);
				return getUpdateInfo(json);
			} else {
				response.getEntity().getContent().close();
				Log.d(TAG,
						"[getUpdateInfo] status code : "
								+ statusLine.getStatusCode());
				Log.d(TAG,
						"[getUpdateInfo] status phrase : "
								+ statusLine.getReasonPhrase());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// convert json string to update information object
	private UpdateInfo getUpdateInfo(String json)  {
		JSONObject obj = null;
		JSONObject version = null;
		int isNeedUpdate = 0;
		try {
			obj = new JSONObject(json);
			version = obj.getJSONObject("version");
			isNeedUpdate = version.getInt("isNeedUpdate");
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (isNeedUpdate != 0) {
			// get release log
			String releaseLog = "";
			try {
				releaseLog = version.getString("releaseLog");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] releaseLog is null!");
			}
			
			// get apk uri
			String url = "";
			try {
				url = version.getString("url");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] url is null!");
			}
			
			// get apk size 
			int apk_size = 0;
			try {
				apk_size = version.getInt("apk_size");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] apk_size is null!");
			}
			
			// get patch info 
			String patchUrl = "";
			try {
				patchUrl = version.getString("patchUrl");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] patchUrl is null!");
			}
			
			int patch_size =  0;
			try {
				patch_size = version.getInt("patch_size");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] patch_size is null!");
			}
			
			// get local package md5sum 
			String md5sum = "";
			try {
				md5sum = version.getString("shalNum");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] md5sum is null!");
			}
			
			// get remote version
			String number = "";
			try {
				number = version.getString("number");
			}catch (Exception e) {
				Log.d(TAG, "[getUpdateInfo] number is null!");
			}
			
			// construct update info object 
			UpdateInfo updateInfo = new UpdateInfo();
			updateInfo.setReleaseLog(releaseLog);
			updateInfo.setRemoteApkUri(url);
			updateInfo.setRemoteApkSize(apk_size);
			updateInfo.setRemotePatchUri(patchUrl);
			updateInfo.setRemotePatchSize(patch_size);
			updateInfo.setMd5sum(md5sum);
			updateInfo.setNumber(number);
			
			return updateInfo;
		} else
			return null;
	}

	// show version update notification
	private void showUpateNotification(UpdateInfo updateInfo) {
		Notification n = new Notification();

		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.flags |= Notification.FLAG_AUTO_CANCEL;

		n.defaults = Notification.DEFAULT_ALL;

		n.icon = R.drawable.icon;
		n.when = System.currentTimeMillis();

		// do update invoke update service
		UpdateService.setUpdateInfo(updateInfo, mContext);
		Intent i = new Intent(mContext, UpdateService.class);
		PendingIntent pi = PendingIntent.getService(mContext, 0, i, 0);

		// Change the name of the notification here
		n.setLatestEventInfo(mContext, "集团通讯录新版发布，点击更新!",
				updateInfo.getReleaseLog(), pi);
		NotificationManager mNotifMan = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Random random = new Random(System.currentTimeMillis());
		mNotifMan.notify(random.nextInt(), n);
		return;
	}

	// do update
	public void update() {
		UpdateInfo updateInfo = requestUpdateInfo();
		if (updateInfo != null) {
			showUpateNotification(updateInfo);
		}else {
			Log.w(TAG, "[update] get update info failed!");
		}
	}
}
