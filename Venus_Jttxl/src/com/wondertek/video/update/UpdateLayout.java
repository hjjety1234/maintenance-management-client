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
import java.util.Locale;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.wbtech.common.CommonUtil;
import com.wondertek.jttxl.R;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.appmanager.AppManager;
import com.wondertek.video.msgpush.NotificationDetailsActivity;

public class UpdateLayout extends RelativeLayout {
	private static final String TAG = "UpdateLayout";
	private static Context mContext;

	private static UpdateInfo updateInfo = null;
	private static boolean bIncrementalUpdate = true;

	private Notification notification = null;
	private NotificationManager notificationManager = null;

	public UpdateLayout(Context context) {
		super(context);
		mContext = context;
		inflate(context, R.layout.update_view, this);
	}

	public void showUpdateInfo(UpdateInfo updateInfo) {
		Log.d(TAG, ">>>showUpdateInfo<<<");
		if (updateInfo == null) {
			Log.w(TAG, "[showUpdateInfo] updateInfo is null!");
			return;
		}
		UpdateLayout.updateInfo = updateInfo;

		TextView appVersion = (TextView) findViewById(R.id.app_version);
		appVersion.setText(updateInfo.getNumber());

		TextView appSize = (TextView) findViewById(R.id.app_size);
		appSize.setText(getReadableSize(updateInfo.getRemoteApkSize()));

		TextView releaseLog = (TextView) findViewById(R.id.release_log);
		releaseLog.setText(updateInfo.getReleaseLog());

		View deleteLine = (View) findViewById(R.id.delete_line);
		TextView patchSize = (TextView) findViewById(R.id.patch_size);
		if (isIncrementalUpdate() == true) {
			patchSize.setText(getReadableSize(updateInfo.getRemotePatchSize()));
			deleteLine.setVisibility(VISIBLE);
			patchSize.setVisibility(VISIBLE);
		} else {
			deleteLine.setVisibility(INVISIBLE);
			patchSize.setVisibility(INVISIBLE);
		}

		ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_update);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateService.removeUpdateInfo();
				UpdateService.actionStop(mContext);
			}
		});

		ImageButton updateButton = (ImageButton) findViewById(R.id.do_update);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateService.removeUpdateInfo();

				// configure the intent
				Intent intent = new Intent(mContext,
						NotificationDetailsActivity.class);
				final PendingIntent pendingIntent = PendingIntent.getActivity(
						VenusApplication.getInstance(), 0, intent, 0);

				// configure the notification
				notification = new Notification(R.drawable.icon, "正在下载...",
						System.currentTimeMillis());
				notification.flags = notification.flags
						| Notification.FLAG_ONGOING_EVENT;
				// set notification view
				notification.contentView = new RemoteViews(VenusApplication
						.getInstance().getPackageName(),
						R.layout.download_progress);
				notification.contentIntent = pendingIntent;
				notification.contentView.setImageViewResource(R.id.status_icon,
						R.drawable.icon);
				notification.contentView.setTextViewText(R.id.status_text,
						"正在下载...");
				notification.contentView.setProgressBar(R.id.status_progress,
						100, 0, false);

				// Notification Manager
				notificationManager = (NotificationManager) VenusApplication
						.getInstance()
						.getSystemService(
								VenusApplication.getInstance().NOTIFICATION_SERVICE);
				notificationManager.notify(42, notification);
				if (bIncrementalUpdate == false)
					new AsyncDownloadTask().execute(UpdateLayout.updateInfo
							.getRemoteApkUri());
				else
					new AsyncDownloadTask().execute(UpdateLayout.updateInfo
							.getRemotePatchUri());
			}
		});

	}

	public static boolean isIncrementalUpdate() {
		if (updateInfo == null) {
			Log.w(TAG, "[isIncrementalUpdate] updateInfo is null.");
			return false;
		}
		new File(UpdateInfo.localDownloadDir).mkdirs();

		bIncrementalUpdate = true;

		if (updateInfo.getRemotePatchUri() == null
				|| updateInfo.getRemotePatchUri().trim().equals("")) {
			Log.d(TAG, "[isIncrementalUpdate] remote patch uri is empty!");
			bIncrementalUpdate = false;
		}

		if (new File(UpdateInfo.localApkPath).exists() == false) {
			Log.d(TAG, "[isIncrementalUpdate] local apk is not exist!");
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
			Log.d(TAG, "[isIncrementalUpdate] local md5sum: " + md5);
		} catch (IOException e) {
			Log.e(TAG, "[isIncrementalUpdate] " + e.getMessage());
			bIncrementalUpdate = false;
		}

		if (!md5.equals(updateInfo.getMd5sum())) {
			Log.d(TAG, "[isIncrementalUpdate] local: " + md5
					+ " is not same as remote md5: " + updateInfo.getMd5sum());
			bIncrementalUpdate = false;
		}
		return bIncrementalUpdate;
	}

	public String getReadableSize(long l) {
		if (l < 1024) {
			return String.format(Locale.CHINA, "%dB", l);
		} else if (l < 1048576) {
			return String.format(Locale.CHINA, "%.2fKB", l / 1024.0);
		} else if (l < 1073741824) {
			return String.format(Locale.CHINA, "%.2fMB", l / 1048576.0);
		} else {
			return String.format(Locale.CHINA, "%.2fGB", l / 1073741824.0);
		}
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
				VenusApplication.startAppFakeActivity();
				if (bIncrementalUpdate == false)
					AppManager.getInstance(VenusActivity.getInstance())
							.InstallApp(updateInfo.getLocalApkPath());
				else {
					Log.d(TAG,
							"[onProgressUpdate] trying to load libbsdiff.so...");
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

					// Intent intent = new Intent(Intent.ACTION_VIEW);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.setAction(android.content.Intent.ACTION_VIEW);
					// intent.setDataAndType(
					// Uri.fromFile(new File(UpdateInfo.localApkPath)),
					// "application/vnd.android.package-archive");
					// mContext.startActivity(intent);
				}
			}
		}

	}

}
