package com.wondertek.video.update;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.wondertek.jttxl.R;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.appmanager.AppManager;
import com.wondertek.video.msgpush.NotificationDetailsActivity;

public class ProgressNofity extends Activity {
	ProgressBar progressBar;
	private static UpdateInfo updateInfo = null;
	private final static String TAG = "ProgressNofity";
	private Notification notification = null;
	private NotificationManager notificationManager = null;
	private boolean bIncrementalUpdate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, ">>>onCreate<<<");

		// get the layout
		setContentView(R.layout.download_progress);
		setVisible(false);

		// configure the intent
		Intent intent = new Intent(this, NotificationDetailsActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		// configure the notification
		notification = new Notification(R.drawable.icon, "正在下载...",
				System.currentTimeMillis());
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;

		// set notification view
		notification.contentView = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.download_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon,
				R.drawable.icon);
		notification.contentView.setTextViewText(R.id.status_text, "正在下载...");
		notification.contentView.setProgressBar(R.id.status_progress, 100, 0,
				false);

		// Notification Manager
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
		notificationManager.notify(42, notification);

		// start download task
		doDownloadTask();
		VenusApplication.startAppFakeActivity();
	}

	public static void setUpdateInfo(UpdateInfo info) {
		updateInfo = info;
	}

	public static UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	public void doDownloadTask() {
		if (updateInfo == null) {
			Log.w(TAG, "[doDownloadTask] updateInfo is null.");
			return;
		}

		new File(UpdateInfo.localDownloadDir).mkdirs();

		bIncrementalUpdate = true;

		if (updateInfo.getRemotePatchUri() == null
				|| updateInfo.getRemotePatchUri().trim().equals("")) {
			Log.d(TAG, "[doDownloadTask] remote patch uri is empty!");
			bIncrementalUpdate = false;
		}

		if (new File(UpdateInfo.localApkPath).exists() == false) {
			Log.d(TAG, "[doDownloadTask] local apk is not exist!");
			bIncrementalUpdate = false;
		}

		String md5 = "";
		try {
			FileInputStream fis = new FileInputStream(new File(
					UpdateInfo.localApkPath));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[10240];
			int bytesRead;
			while ((bytesRead = fis.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			fis.close();
			byte[] data = bos.toByteArray();
			md5 = new String(Hex.encodeHex(DigestUtils.md5(data)));
			Log.d(TAG, "[doDownloadTask] local md5sum: " + md5);
		} catch (IOException e) {
			Log.e(TAG, "[doDownloadTask] " + e.getMessage());
			bIncrementalUpdate = false;
		}

		if (!md5.equals(updateInfo.getMd5sum())) {
			Log.d(TAG, "[doDownloadTask] local: " + md5
					+ " is not same as remote md5: " + updateInfo.getMd5sum());
			bIncrementalUpdate = false;
		}

		if (bIncrementalUpdate == false)
			new AsyncDownloadTask().execute(updateInfo.getRemoteApkUri());
		else
			new AsyncDownloadTask().execute(updateInfo.getRemotePatchUri());
	}

	class AsyncDownloadTask extends AsyncTask<String, Integer, String> {
		private final static String TAG = "AsyncDownloadTask";

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				URL url = new URL(sUrl[0]);
				Log.d(TAG, "[doInBackground] uri: " + url);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				String localpath = null;
				if (bIncrementalUpdate == false) {
					localpath = updateInfo.getLocalApkPath();
				} else {
					localpath = updateInfo.getLocalPatchPath();
				}
				OutputStream output = new FileOutputStream(localpath);
				byte data[] = new byte[10240];
				long total = 0;
				int count;
				int lastProgress = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					int progress = (int) (total * 100 / fileLength);
					if (progress != lastProgress) {
						publishProgress(progress);
						lastProgress = progress;
					}
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			int i = progress[0];
			notification.contentView.setProgressBar(R.id.status_progress, 100,
					i, false);
			notificationManager.notify(42, notification);
			if (i == 100) {
				notificationManager.cancel(42);
				if (bIncrementalUpdate == false)
					AppManager.getInstance(VenusActivity.getInstance())
							.InstallApp(updateInfo.getLocalApkPath());
				else {
					Log.d(TAG,
							"[onProgressUpdate] trying to load libbsdiff.so...");
					// String dirname = VenusApplication.appAbsPath
					// + "/lib2/appmanager/";
					// System.load(dirname + "libBsdiff.so");
					System.loadLibrary("Bsdiff");
					VenusActivity va = VenusActivity.getInstance();
					AppManager.getInstance(va).applyPatchToOldApk(
							UpdateInfo.localApkPath,
							UpdateInfo.localNewApkPath,
							UpdateInfo.localPatchPath);

					new File(UpdateInfo.localApkPath).delete();
					Log.d(TAG, "[onProgressUpdate] delete "
							+ UpdateInfo.localApkPath);

					new File(UpdateInfo.localNewApkPath).renameTo(new File(
							UpdateInfo.localApkPath));
					Log.d(TAG, "[onProgressUpdate] rename "
							+ UpdateInfo.localNewApkPath);

					AppManager.getInstance(VenusActivity.getInstance())
							.InstallApp(updateInfo.getLocalApkPath());
				}
			}
		}

	}
}
