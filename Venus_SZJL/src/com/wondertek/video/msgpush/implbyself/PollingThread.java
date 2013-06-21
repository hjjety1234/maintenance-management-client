package com.wondertek.video.msgpush.implbyself;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

/**
 * Polling request a message from The MsgPush Server
 * @author yuhongwei
 *
 */

public class PollingThread extends Thread {
    private static final String TAG = "PollingThread";

	private MsgPushService mService;
    private SharedPreferences mSharePrefs;

	private int defautDelay;
	private int mRetry;
	
	private boolean bIsNewMessage = false;

	public PollingThread(MsgPushService s) {
		mService = s;
		mSharePrefs = mService.getSharedPreferences();
		defautDelay = Integer.parseInt(mSharePrefs.getString(Constants.USER_INTERVAL, "600"));
		mRetry = 0;
	}

	public void run() {
		Log.d(TAG, "Thread Start...");
		int mDelay = defautDelay;
		while (!isInterrupted()) {
			try {
				Thread.sleep(mDelay * 1000);

				String req = buildRequest();
				Log.d(TAG, "[PollingThread] request: " + req);
				String resp = getMessage(req);
				Log.d(TAG, "[PollingThread] response: " + resp);

				final Bundle bundle = xmlParse(resp);
				mDelay = bundle.getInt(Constants.MSG_NEXT, defautDelay);
				mRetry = 0;

                if (bIsNewMessage) {
					mService.getHandler().post(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(
									Constants.ACTION_SHOW_NOTIFICATION);
							intent.putExtras(bundle);
							mService.sendBroadcast(intent);
						}
					});
                }

			} catch (InterruptedException e) {
				Log.d(TAG, "Thread Interrupted...");
				break;
			} catch (Exception e) {
				mRetry++;
				if (mRetry >= Constants.RETRY) {
					mService.getHandler().post(new Runnable() {
						@Override
						public void run() {
							mService.stopPollingThread();
						}
					});
				}
				e.printStackTrace();
			}
		}
		Log.d(TAG, "Thread End...");
	}

	private Bundle xmlParse(String resp) throws Exception {
		InputStream istream = new ByteArrayInputStream(resp.getBytes());
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(istream, "UTF-8");

		int event = parser.getEventType();
		Bundle bundle = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
				case XmlPullParser.START_DOCUMENT:
					bundle = new Bundle();
					break;
				case XmlPullParser.START_TAG:
					String tag = parser.getName().trim();
                    if (Constants.MSG_ISNEW.equals(tag)) {
                    	bIsNewMessage = parser.nextText().trim().equals("true") ? true : false;
                    } else if (Constants.MSG_ID.equals(tag)) {
                        saveMsgId(parser.nextText());
					} else if (Constants.MSG_TITLE.equals(tag)) {
						bundle.putString(Constants.MSG_TITLE, parser.nextText());
					} else if (Constants.MSG_SUMMARY.equals(tag)) {
						bundle.putString(Constants.MSG_SUMMARY, parser
								.nextText());
					} else if (Constants.MSG_URI.equals(tag)) {
						bundle.putString(Constants.MSG_URI, parser.nextText());
					}else if (Constants.MSG_NEXT.equals(tag)) {
						bundle.putInt(Constants.MSG_NEXT, Integer.parseInt(parser
								.nextText()));
					}
					break;
				default:
					break;
			}
			event = parser.next();
		}
		return bundle;
	}

	private String getMessage(String urlpath) throws Exception {
		String reap = null;

		// URL url = new URL(urlpath);
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setRequestMethod("GET");
		// conn.setConnectTimeout(Constants.TIMEOUT);
		// if (conn.getResponseCode() == 200) {
		// InputStream istream = conn.getInputStream();
		HttpGet request = new HttpGet(urlpath);
		HttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, Constants.TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, Constants.TIMEOUT);
		ConnManagerParams.setTimeout(params, Constants.TIMEOUT);
		HttpResponse response = client.execute(request);
		int code = response.getStatusLine().getStatusCode();
		if (code == HttpStatus.SC_OK) {
			InputStream istream = response.getEntity().getContent();

			OutputStream ostream = new ByteArrayOutputStream();
			byte[] b = new byte[Constants.BUFFER];

			int count = 0;
			while ((count = istream.read(b, 0, Constants.BUFFER)) != -1) {
				ostream.write(b, 0, count);
			}
			ostream.flush();

			ostream.close();
			istream.close();
			reap = ostream.toString();
		}
		return reap;
	}

	private String buildRequest() {
		SharedPreferences sharedPrefs = mService.getSharedPreferences();

		String server = sharedPrefs.getString(Constants.MSG_PUSH_SERVER, null);
		if (server != null) {
			StringBuffer sb = new StringBuffer(64);
			sb.append(server);

			String username = sharedPrefs.getString(Constants.USER_NAME, null);
			String password = sharedPrefs.getString(Constants.USER_PASSWORD, null);
            String msgId = sharedPrefs.getString(Constants.MSG_ID, null);
			if (username != null) {
				sb.append("?");
				sb.append(Constants.USER_NAME + "=" + username);
                if (password != null) {
                	sb.append("&");
                	sb.append(Constants.USER_PASSWORD + "=" + password);
                }
				if (msgId != null) {
					sb.append("&contId=" + msgId);
				}
			} else {
				if (msgId != null) {
					sb.append("?contId=" + msgId);
				}
			}
			return sb.toString();
		} else {
			return null;
		}
	}
    
	private void saveMsgId(String id) {
		SharedPreferences sharedPrefs = mService.getSharedPreferences();
		Editor edit = sharedPrefs.edit();
		edit.putString(Constants.MSG_ID, id);
		edit.commit();
	}

}
