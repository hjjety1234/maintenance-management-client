package com.wondertek.video.caller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

public class XmlService extends Thread {
	private static String TAG = "XmlService";
	private String num;
	private TextView regionText;

	public XmlService(String num, TextView regionText) {
		this.num = num;
		this.regionText = regionText;
	}

	@Override
	public void run() {
		Log.d(TAG, "[run]");
		if (num == null) {
			Log.w(TAG, "[run] number is null!");
			return;
		}
		regionText.setText(num);
		try {
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream("query.xml");
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);
			byte[] data = out.toByteArray();
			String xml = new String(data);
			xml = xml.replace("#", num);
			Log.d(TAG, "[run] xml: " + xml);
			byte[] sendData = xml.getBytes("UTF-8");
			URL url = new URL(
					"http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/soap+xml; charset=utf-8");
			conn.setRequestProperty("Content-Length",
					String.valueOf(sendData.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(sendData);
			int code = conn.getResponseCode();
			if (code == 200) {
				regionText.setText(parse(conn.getInputStream()));
			} else {
				Log.d(TAG, "[run] response code: " + code);
				InputStream ips = conn.getInputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				while ((bytesRead = ips.read(buffer)) != -1)
					bos.write(buffer, 0, bytesRead);
				String response = new String(bos.toByteArray());
				Log.d(TAG, "[run] response : " + response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parse(InputStream inputStream) throws Exception {
		Log.d(TAG, "[parse]");
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser
				.next())
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("getMobileCodeInfoResult".equals(parser.getName()))
					return parser.nextText();
			}
		return null;
	}
}