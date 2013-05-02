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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wbtech.common.CommonUtil;
import com.wondertek.jttxl.R;
import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.caller.Constants;
import com.wondertek.video.caller.Employee;
import com.wondertek.video.msgpush.NotificationDetailsActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	private UpdateInfo getUpdateInfo(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONObject version = obj.getJSONObject("version");
		int isNeedUpdate = version.getInt("isNeedUpdate");
		if (isNeedUpdate != 0) {
			int apk_size = version.getInt("apk_size");
			String releaseLog = version.getString("releaseLog");
			String url = version.getString("url");
			UpdateInfo updateInfo = new UpdateInfo();
			updateInfo.setReleaseLog(releaseLog);
			updateInfo.setRemoteApkUri(url);
			updateInfo.setRemoteApkSize(apk_size);
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

		// do update
		ProgressNofity.setUpdateInfo(updateInfo);
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(
				mContext, ProgressNofity.class), 0);

		// Change the name of the notification here
		n.setLatestEventInfo(mContext, "集团通讯录存在新版本，点击更新!",
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
		}
	}
}
