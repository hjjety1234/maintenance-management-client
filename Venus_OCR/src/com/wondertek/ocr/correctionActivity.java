package com.wondertek.ocr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class correctionActivity extends Activity {
	
	private static final String TAG = "correctionActivity";
	private ImageView running = null;
	private int index = 0;
	Handler handler = new Handler();
	
    private static final int TEXT_MSG = 0;
	Handler handler2 = new Handler(){		
		@Override 
        public void handleMessage(Message msg) {
			switch (msg.what){
				case TEXT_MSG:
					 {
						 if(reveal != null){
							 revealView.setText(reveal);
						 }
						 
						 if(loss != null){
							 lossView.setText(loss);
						 }
					}
					break;
        		default:
					break;
				 
			}
		}
	};

	private TextView revealView = null;
	private TextView lossView = null;
	private ImageView back  = null;
	private ImageView next = null;
	private String IDNo = null;
	private String reveal = null;
	private String loss = null;
	private String URL_Reveal = "http://apis.juhe.cn/idcard/leak?key=ea3b17fbc5c205d5bfbb82adea26192e";
	private String URL_loss = "http://apis.juhe.cn/idcard/loss?key=ea3b17fbc5c205d5bfbb82adea26192e";
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Log.d(TAG, ">>>run<<<");
			
			index = (index+1)%8;
			switch(index){
				case 0:
					running.setImageResource(R.drawable.sfrun1);
					break;
				case 1:
					running.setImageResource(R.drawable.sfrun2);
					break;
				case 2:
					running.setImageResource(R.drawable.sfrun3);
					break;
				case 3:
					running.setImageResource(R.drawable.sfrun4);
					break;
				case 4:
					running.setImageResource(R.drawable.sfrun5);
					break;
				case 5:
					running.setImageResource(R.drawable.sfrun6);
					break;
				case 6:
					running.setImageResource(R.drawable.sfrun7);
					break;
				case 7:
					running.setImageResource(R.drawable.sfrun8);
					break;
				default:
					break;
			}
			
			handler.postDelayed(this, 500);

			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_correction);
		
		running = (ImageView)findViewById(R.id.running);
		
		revealView = (TextView)findViewById(R.id.reveal);
		lossView   = (TextView)findViewById(R.id.loss);
		back = (ImageView)findViewById(R.id.back);
		next = (ImageView)findViewById(R.id.next);
		
		Bundle extras = getIntent().getExtras();
		IDNo = extras.getString("IDNo");
		
		
		this.back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		this.next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

			}
		});

		handler.postDelayed(runnable, 500);
		
		new Thread(){
			 @Override
			 public void run(){
				 
				 if(IDNo != null){
					 reveal = getHttpRequest(URL_Reveal);
					 loss  = getHttpRequest(URL_loss);
					 handler2.sendMessage(handler2.obtainMessage(TEXT_MSG, 0, 0));	
				 }
				 handler.removeCallbacks(runnable);
			 }
		}.start();

	}
	
	
	private String getHttpRequest(String URL_str)
	{
		String res = null;
		try{
			String URL = URL_str+"&cardno="+IDNo.substring(0,18)+"&dtype=json";
			Log.d(TAG,URL);
			
			HttpResponse response = HttpClientget(URL);
			
			if(response != null) {
				StatusLine statusLine = response.getStatusLine();
				
				if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				{
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
							.readLine()) {
						builder.append(s);
					}
					
					Log.d(TAG, "response: " + builder.toString());
					JSONObject obj = new JSONObject(builder.toString());
					int code = obj.getInt("resultcode");
					if(code == 200){
						JSONObject result = (JSONObject) obj.get("result");
						
						if(result != null){
							res = (String) result.get("tips");
						}
					}
				}
			}

			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;

	}
	
	
	private HttpResponse HttpClientget(String URL)
	{
		BasicHttpParams httpParams = new BasicHttpParams();  
		
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);  
		  
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);  
  
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192); 
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
			HttpResponse response = httpclient.execute(new HttpGet(URL));
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private HttpResponse HttpClientPost(String URL, String upload)
	{
		BasicHttpParams httpParams = new BasicHttpParams();  
		
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);  
		  
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);  
  
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192); 
		
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		try {
			HttpPost post = new HttpPost(URL); 
			post.addHeader("content-type", "application/x-www-form-urlencoded");
			post.setEntity(new StringEntity(upload));
			
			HttpResponse response = httpclient.execute(post);
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	

}
