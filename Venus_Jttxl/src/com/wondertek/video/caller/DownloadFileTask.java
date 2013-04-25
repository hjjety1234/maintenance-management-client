package com.wondertek.video.caller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadFileTask extends AsyncTask<String, Integer, String> {
	private final static String TAG = "DownloadFileTask";
	private String filename = null;

	public DownloadFileTask(String filename) {
		this.filename = filename;
	}

	@Override
	protected String doInBackground(String... sUrl) {
		try {
			URL url = new URL(sUrl[0]);
			Log.d(TAG, "[doInBackground] uri: " + url);
			URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100%
			// progress bar
			int fileLength = connection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(Constants.LOC_PIC_DIR
					+ filename);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress((int) (total * 100 / fileLength));
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

	public String requestResourceUri() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = httpclient.execute(new HttpGet(
					Constants.REQ_PIC_URL_PREFIX + this.filename));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String respString = out.toString();
				Log.d(TAG, "[requestResourceUri] respString: " + respString);
				return respString;
			} else {
				response.getEntity().getContent().close();
				Log.d(TAG, "[requestResourceUri] status code : " + statusLine.getStatusCode());
				Log.d(TAG, "[requestResourceUri] status phrase : " + statusLine.getReasonPhrase());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}