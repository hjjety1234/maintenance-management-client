package com.wondertek.video.msgpush.implbyself;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * @author yuhongwei
 */

public class HeartSendThread extends Thread {
    private static final String TAG = "HeartSendThread";
    private MsgPushService mService;
    private int mRetry;
    
    public HeartSendThread(MsgPushService server) {
    	mService = server;
    	mRetry = 0;
    }

	@Override
	public void run() {
		Log.d(TAG, "[HeartSendThread Start!]");
		while (!isInterrupted()) {
			try {
				Thread.sleep(Constants.HEART_SEND_INTERVAL * 1000);
				sendHeartContent();
			} catch (InterruptedException e) {
				Log.d(TAG, "[HeartSendThread Interrupted!]");
				e.printStackTrace();
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
		Log.d(TAG, "[HeartSendThread End!]");
	}
    
	private void sendHeartContent() {
		HttpPost postRequest = new HttpPost(Constants.HEART_SEND_URL);
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("content", Constants.HEART_SEND_CONTENT));
		try {
			postRequest.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
            	// TODO: sucess
            } else {
            	// TODO: failed
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
