package com.ahmobile.ar.ustc;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 复写Application
 * 提供线程安全的HttpClient
 * @author WINDFREE
 *
 */
public class ApplicationEx extends Application {
	private static final String TAG = "ApplicationEx";
	public static String username ="";
	public static String userid = "";
	public static String areaid="";
//	public static String ipPort = "www.starit.com.cn:8080";//测试
	public static String ipPort = "202.97.0.60:9443"; 
	public static int timeoutSec = 40;
	public static boolean NetWorkStatus = false;
 
	public static String phoneNumber = "-1";
	private static HttpClient httpClient;
	public  static  String IMEI;
	/**
	 * 服务根地址，用来拼装服务地址
	 */
	public static String SERV_ROOT_ADDR = "JTZQ-Service/sa";
	
	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = createHttpClient();
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String phone = telephonyManager.getLine1Number(); //获取用户电话号码 可能为空
		IMEI=telephonyManager.getDeviceId();
		if (!"".equals(phone) && null!=phone && !"null".equals(phone)){
			phoneNumber = phone;
		}
		Log.d(TAG, "电话号码"+phoneNumber);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		shutdownHttpClient();
	}

	public static HttpClient getHttpClient() {
		if(httpClient ==null){
			httpClient = createHttpClient();
		}
		return httpClient;
	}

	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private static HttpClient createHttpClient() {
		Log.d(TAG, "createHttpClient()...");
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, timeoutSec * 1000);
		HttpConnectionParams.setSoTimeout(params, timeoutSec * 1000);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		//ClientConnectionManager负责管理HttpClient的HTTP连接
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		return new DefaultHttpClient(conMgr, params);
	}
}
