/**
 * 
 */
package com.wondertek.video.g3wlan.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Administrator
 *
 */
public class G3WLANHttp {
	
	private final String GD_JSESSIONID		= "JSESSIONID=";
	
	private final String BJ_PHPSESSID		= "PHPSESSID=";
	private final String BJ_IP 				= "221.176.1.140";

	
	//private HttpURLConnection httpConn;
	private String cookie = null;
	private String response = null;
	private String host = null;
	
	private static G3WLANHttp instance = null;
	
	public static G3WLANHttp getInstance()
	{
		if(instance == null)
		{
			instance = new G3WLANHttp();
		}
		return instance;
	}
	/*
	 * Getters/Setters methods
	 */
	public String getHost() {
		return host;
	}

	public String getResponse() {
		return response;
	}

	
	
	private HttpURLConnection initHttpConn(boolean flagHttps, String strHttpUrl) {
		HttpURLConnection mhttpConn = null;
		try {
			// when using HTTPS 
			if(flagHttps) {
				SSLContext sslCont = SSLContext.getInstance("TLS"); 
				sslCont.init(null, new TrustManager[]{new myTrustManager()}, new SecureRandom());
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCont.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(
						new MyHostnameVerifier(new URL(strHttpUrl).getHost()));
				mhttpConn = (HttpsURLConnection)new URL(strHttpUrl).openConnection();
			
			// when using HTTP
			} else {
				mhttpConn = (HttpURLConnection)new URL(strHttpUrl).openConnection();
			}
			
			HttpURLConnection.setFollowRedirects(false);
			mhttpConn.setDoInput(true);
			mhttpConn.setDoOutput(true);
			mhttpConn.setReadTimeout(15000);
			mhttpConn.setConnectTimeout(15000);
			/*mhttpConn.setRequestProperty("Accept-Language", "zh-cn");
			mhttpConn.setRequestProperty("Connection", "Keep-Alive");
			mhttpConn.setRequestProperty("Cache-Control", "no-cache");
			mhttpConn.setRequestProperty("Accept-Charset", "UTF-8");*/
			mhttpConn.setRequestProperty("Accept-Charset", "gb2312");
			mhttpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mhttpConn.setRequestProperty("User-Agent", "G3WLAN");
//			mhttpConn.setRequestProperty("Content-Type", "text/html");
			mhttpConn.setInstanceFollowRedirects(false);
			
		} 
		catch(Exception e) {
			e.printStackTrace();
			Util.Trace("initHttpConn exception: " + e.getMessage());
		}
		return mhttpConn;
	}

	private HttpURLConnection initHttpConn(boolean flagHttps, String strHttpUrl, int connectTO, int readTO) {
		HttpURLConnection mhttpConn = null;
		try {
			// when using HTTPS 
			if(flagHttps) {
				SSLContext sslCont = SSLContext.getInstance("TLS"); 
				sslCont.init(null, new TrustManager[]{new myTrustManager()}, new SecureRandom());
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCont.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(
						new MyHostnameVerifier(new URL(strHttpUrl).getHost()));
				mhttpConn = (HttpsURLConnection)new URL(strHttpUrl).openConnection();
			
			// when using HTTP
			} else {
				mhttpConn = (HttpURLConnection)new URL(strHttpUrl).openConnection();
			}
			
			HttpURLConnection.setFollowRedirects(false);
			mhttpConn.setDoInput(true);
			mhttpConn.setDoOutput(true);
			mhttpConn.setReadTimeout(readTO);
			mhttpConn.setConnectTimeout(connectTO);
			/*mhttpConn.setRequestProperty("Accept-Language", "zh-cn");
			mhttpConn.setRequestProperty("Connection", "Keep-Alive");
			mhttpConn.setRequestProperty("Cache-Control", "no-cache");
			mhttpConn.setRequestProperty("Accept-Charset", "UTF-8");*/
			mhttpConn.setRequestProperty("Accept-Charset", "gb2312");
			mhttpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mhttpConn.setRequestProperty("User-Agent", "G3WLAN");
//			mhttpConn.setRequestProperty("Content-Type", "text/html");
			mhttpConn.setInstanceFollowRedirects(false);
			
		} 
		catch(Exception e) {
			e.printStackTrace();
			Util.Trace("initHttpConn exception: " + e.getMessage());
		}
		return mhttpConn;
	}

	public boolean wlanIsPortal()
	{
		HttpURLConnection mhttpConn = null;
		try {
			mhttpConn = initHttpConn(false, Util.getPingUrl());
			if(mhttpConn==null)
				return false;
			// send out data using GET method
			mhttpConn.setRequestMethod("GET");
			mhttpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			// process HTTP 302-redirection 
			if(rspCode != HttpURLConnection.HTTP_OK)
			{
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP)
				{
					Util.Trace("G3WLANHttp:: " + "WLAN is AuthenPortal! ");
					mhttpConn.disconnect();
					return true;
				}
			}
		}
		catch(Exception e) {
			response = null;
			e.printStackTrace();
		}
		
		Util.Trace("G3WLANHttp:: " + "WLAN isn't AuthenPortal! ");
		if(mhttpConn != null) mhttpConn.disconnect();
		return false;
	}
	
	public boolean wlanHaveLogin()
	{
		HttpURLConnection mhttpConn = null;
		try {
			mhttpConn = initHttpConn(false, Util.getPingUrl());
			if(mhttpConn==null)
				return false;
			// send out data using GET method
			mhttpConn.setRequestMethod("GET");
			mhttpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Util.Trace("wlanHaveLogin:: GET response code: " + rspCode);
			
			// process HTTP 302-redirection 
			if(rspCode != -1)
			{
				Util.Trace("wlanHaveLogin:: WLAN is connected! ");
				mhttpConn.disconnect();
				return true;
			}
		}
		catch(Exception e) {
			response = null;
			e.printStackTrace();
			Util.Trace("wlanHaveLogin:: Exception," + e.toString() + " \n" + e.getMessage());
		}
		
		Util.Trace("G3WLANHttp:: " + "WLAN isn't connected! ");
		if(mhttpConn != null) mhttpConn.disconnect();
		return false;
	}
	
	public boolean sendDataPost(boolean isHttps, String strUrl, String outData) {
		HttpURLConnection mhttpConn = null;
		try {
			
			mhttpConn = initHttpConn(isHttps, strUrl);
			if(mhttpConn==null)
				return false;
			// send out data using POST method
			mhttpConn.setRequestMethod("POST");
			// add "Cookie" value
			mhttpConn.setRequestProperty("Cookie", cookie);
			Util.Trace("AuthenPortal:: " + "____cookie of SendDataPost(): " + cookie);
			Utils.writeLog("____cookie of SendDataPost(): " + cookie);
			
			mhttpConn.connect();
			
			DataOutputStream dout = new DataOutputStream(mhttpConn.getOutputStream());
			dout.write(outData.getBytes());
			dout.flush();
			dout.close();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Utils.writeLog("POST response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = mhttpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____RspCode of SendDataPost(): " + rspCode);
					Util.Trace("AuthenPortal::" + i + "____location of SendDataPost(): " + location);
					Utils.writeLog(i + "____location of SendDataPost(): " + location);
					
					mhttpConn.disconnect();
					
					mhttpConn = initHttpConn(false, location);
					if(mhttpConn==null)
						return false;
					mhttpConn.setRequestMethod("GET");
					// add "Cookie" value
					mhttpConn.setRequestProperty("Cookie", cookie);
					Util.Trace("AuthenPortal:: " + i + "____cookie of SendDataPost(): " + cookie);
					Utils.writeLog(i + "____cookie of SendDataPost(): " + cookie);
					
					mhttpConn.connect();
					
					rspCode = mhttpConn.getResponseCode();
					Utils.writeLog(i + " GET response code after 302: " + rspCode);
				} else {
					break;
				}
			}
			
			// process HTTP 200-OK
			if(rspCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(mhttpConn.getInputStream(), "gb2312"));

				String strLine;
				StringBuffer rspBuf = new StringBuffer(); 
				try {
					while((strLine=br.readLine()) != null) {
						 rspBuf.append(strLine + "\r\n");
					}
				}catch(Exception e)	{
					Util.Trace(e.toString());
				}
				response = rspBuf.toString();
				
				Util.Trace("AuthenPortal:: " + "Response of SendDataPost(): " + response);
				//writeLog("Response of SendDataPost(): " + response);
				br.close();
				
				mhttpConn.getInputStream().close();
				mhttpConn.disconnect();
				return true;
			}
			return false;
			
		}
		catch(Exception e) {
			response = null;
			Utils.writeLog("sendDataPost exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendDataPost(boolean isHttps, String strUrl, String outData, int connectTO, int readTO) {
		HttpURLConnection mhttpConn = null;
		try {
			
			mhttpConn = initHttpConn(isHttps, strUrl, connectTO, readTO);
			if(mhttpConn==null)
				return false;
			// send out data using POST method
			mhttpConn.setRequestMethod("POST");
			// add "Cookie" value
			mhttpConn.setRequestProperty("Cookie", cookie);
			Util.Trace("AuthenPortal:: " + "____cookie of SendDataPost(): " + cookie);
			Utils.writeLog("____cookie of SendDataPost(): " + cookie);
			
			mhttpConn.connect();
			
			DataOutputStream dout = new DataOutputStream(mhttpConn.getOutputStream());
			dout.write(outData.getBytes());
			dout.flush();
			dout.close();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Utils.writeLog("POST response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = mhttpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____RspCode of SendDataPost(): " + rspCode);
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataPost(): " + location);
					Utils.writeLog(i + "____location of SendDataPost(): " + location);
					
					mhttpConn.disconnect();
					
					mhttpConn = initHttpConn(false, location);
					if(mhttpConn==null)
						return false;
					
					mhttpConn.setRequestMethod("GET");
					// add "Cookie" value
					mhttpConn.setRequestProperty("Cookie", cookie);
					Util.Trace("AuthenPortal:: " + i + "____cookie of SendDataPost(): " + cookie);
					Utils.writeLog(i + "____cookie of SendDataPost(): " + cookie);
					
					mhttpConn.connect();
					
					rspCode = mhttpConn.getResponseCode();
					Utils.writeLog(i + " GET response code after 302: " + rspCode);
				} else {
					break;
				}
			}
			
			// process HTTP 200-OK
			if(rspCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(mhttpConn.getInputStream(), "gb2312"));

				String strLine;
				StringBuffer rspBuf = new StringBuffer(); 
				try {
					while((strLine=br.readLine()) != null) {
						 rspBuf.append(strLine + "\r\n");
					}
				}catch(Exception e)	{
					Util.Trace(e.toString());
				}
				response = rspBuf.toString();
				
				Util.Trace("AuthenPortal:: " + "Response of SendDataPost(): " + response);
				//writeLog("Response of SendDataPost(): " + response);
				br.close();
				
				mhttpConn.getInputStream().close();
				mhttpConn.disconnect();
				return true;
			}
			return false;
			
		}
		catch(Exception e) {
			response = null;
			Utils.writeLog("sendDataPost exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		
	}

	public boolean sendDataGet(boolean isHttps, String outDataUrl) {
		HttpURLConnection mhttpConn = null;
		try {
			
			mhttpConn = initHttpConn(isHttps, outDataUrl);
			if(mhttpConn==null)
				return false;
			// send out data using POST method
			mhttpConn.setRequestMethod("GET");
			mhttpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = mhttpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataGet(): " + location);

					mhttpConn.disconnect();
					
					mhttpConn = initHttpConn(false, location);
					if(mhttpConn==null)
						return false;
					mhttpConn.setRequestMethod("GET");
					mhttpConn.connect();
					
					rspCode = mhttpConn.getResponseCode();
					Util.Trace(i + " GET response code after 302: " + rspCode);
					
				} else {
					break;
				}
			}
			
			if(HttpsURLConnection.HTTP_OK == rspCode) {
				host = mhttpConn.getURL().getHost();
				
				String setCookie = mhttpConn.getHeaderField("Set-Cookie");
				Util.Trace("AuthenPortal:: " + i + "____setcookie of SendDataGet(): " + setCookie);
				
				// extract the cookie property
				if(setCookie != null) {
					String[] setcookieGroup = setCookie.split(";");
					for(String tmp : setcookieGroup) {
						if(	// for Guangdong: "JSESSIONID="
							tmp.trim().startsWith(GD_JSESSIONID) ||
							// for Beijing: "PHPSESSID"
							tmp.trim().startsWith(BJ_PHPSESSID) ) {
							
							cookie = tmp.trim();
							Util.Trace("AuthenPortal:: " + "set cookie of SendDataGet(): " + cookie);
							break;
							
						}
					}
				}
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(mhttpConn.getInputStream(), "gb2312"));
				String strLine;
				StringBuffer rspBuf = new StringBuffer(); 
				try {
					while((strLine=br.readLine()) != null) {
						 rspBuf.append(strLine + "\r\n");
					}
				}catch(Exception e)	{
					Util.Trace(e.toString());
				}
				response = rspBuf.toString();
				
				Util.Trace("AuthenPortal:: " + "Response of SendDataGet(): " + response);
				br.close();
				
				mhttpConn.getInputStream().close();
				mhttpConn.disconnect();
				return true;
			} 
			return false;
		}
		catch(Exception e) {
			response = null;
			Util.Trace("G3WLANHttp:: " + "sendDataGet exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendDataGet(boolean isHttps, String outDataUrl, int connectTO, int readTO) {
		HttpURLConnection mhttpConn = null;
		try {
			
			mhttpConn = initHttpConn(isHttps, outDataUrl, connectTO, readTO);
			if(mhttpConn==null)
				return false;
			// send out data using POST method
			mhttpConn.setRequestMethod("GET");
			mhttpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = mhttpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = mhttpConn.getHeaderField("Location");
					
					++i;
					//Util.Trace("AuthenPortal", i + "____RspCode of SendDataGet(): " + rspCode);
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataGet(): " + location);

					mhttpConn.disconnect();
					
					mhttpConn = initHttpConn(false, location);
					if(mhttpConn==null)
						return false;
					mhttpConn.setRequestMethod("GET");
					mhttpConn.connect();
					
					rspCode = mhttpConn.getResponseCode();
					Util.Trace(i + " GET response code after 302: " + rspCode);
					
				} else {
					break;
				}
			}
			
			if(HttpsURLConnection.HTTP_OK == rspCode) {
				host = mhttpConn.getURL().getHost();
				
				String setCookie = mhttpConn.getHeaderField("Set-Cookie");
				Util.Trace("AuthenPortal:: " + i + "____setcookie of SendDataGet(): " + setCookie);
				
				// extract the cookie property
				if(setCookie != null) {
					String[] setcookieGroup = setCookie.split(";");
					for(String tmp : setcookieGroup) {
						if(	// for Guangdong: "JSESSIONID="
							tmp.trim().startsWith(GD_JSESSIONID) ||
							// for Beijing: "PHPSESSID"
							tmp.trim().startsWith(BJ_PHPSESSID) ) {
							
							cookie = tmp.trim();
							Util.Trace("AuthenPortal:: " + "set cookie of SendDataGet(): " + cookie);
							break;
							
						}
					}
				}
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(mhttpConn.getInputStream(), "gb2312"));
				String strLine;
				StringBuffer rspBuf = new StringBuffer(); 
				try {
					while((strLine=br.readLine()) != null) {
						 rspBuf.append(strLine + "\r\n");
					}
				}catch(Exception e)	{
					Util.Trace(e.toString());
				}
				response = rspBuf.toString();
				
				Util.Trace("AuthenPortal:: " + "Response of SendDataGet(): " + response);
				br.close();
				
				mhttpConn.getInputStream().close();
				mhttpConn.disconnect();
				return true;
			} 
			return false;
		}
		catch(Exception e) {
			response = null;
			Util.Trace("G3WLANHttp:: " + "sendDataGet exception: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private class MyHostnameVerifier implements HostnameVerifier {
		private String hostname;
		
		public MyHostnameVerifier(String hostname) {
			this.hostname = hostname;
		}
		
		public boolean verify(String hostname, SSLSession session) {
			if (this.hostname.equals(hostname)) {
				return true;
			}
			return false;
		}
		
	}
	
	private class myTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public void checkServerTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	
	
	public String getCookie() {
		return cookie;
	}

//	private void writeLog(String log) {
//		File file = new File(LOG_FILE);
//		if (!file.exists()) {
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//	//			e.printStackTrace();
//			}
//		}
//		BufferedWriter out = null;
//		 try{
//			 // Create file 
//			 FileWriter fstream = new FileWriter(LOG_FILE, true);
//			 out = new BufferedWriter(fstream);
//			 SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss  ");
//			 String time = sdf.format(new Date(System.currentTimeMillis()));
//			 out.append(time);
//			 out.append(log);
//			 out.append("\r\n\r\n");
//		 }catch (Exception e){//Catch exception if any
//	//		 System.err.println("Error: " + e.getMessage());
//		 } finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//	//			e.printStackTrace();
//			}
//		 }
//	}
//	
}
