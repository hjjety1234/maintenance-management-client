package com.wondertek.video.update;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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
				R.drawable.ic_menu_save);
		notification.contentView.setTextViewText(R.id.status_text, "正在下载...");
		notification.contentView.setProgressBar(R.id.status_progress, 100, 0,
				false);

		// Notification Manager
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
		notificationManager.notify(42, notification);

		new AsyncDownloadTask().execute(updateInfo.getRemoteApkUri());
		VenusApplication.startAppFakeActivity();
	}

	public static void setUpdateInfo(UpdateInfo info) {
		updateInfo = info;
	}

	public static UpdateInfo getUpdateInfo() {
		return updateInfo;
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
				OutputStream output = new FileOutputStream(
						updateInfo.getLocalApkPath());
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
			Log.d(TAG, "[onProgressUpdate] progress: " + i);
			notification.contentView.setProgressBar(R.id.status_progress, 100,
					i, false);
			notificationManager.notify(42, notification);
			if (i == 100) {
				notificationManager.cancel(42);
				AppManager.getInstance(VenusActivity.getInstance()).InstallApp(
						updateInfo.getLocalApkPath());
			}
		}

	}
}
