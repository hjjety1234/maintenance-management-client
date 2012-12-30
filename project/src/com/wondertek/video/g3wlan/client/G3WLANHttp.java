/**
 * 
 */
package com.wondertek.video.g3wlan.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.wondertek.video.Util;

/**
 * @author Administrator
 *
 */
public class G3WLANHttp {
	
	private final String GD_JSESSIONID		= "JSESSIONID=";
	
	private final String BJ_PHPSESSID		= "PHPSESSID=";
	private final String BJ_IP 				= "221.176.1.140";

	
	private HttpURLConnection httpConn;
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

	
	
	private void initHttpConn(boolean flagHttps, String strHttpUrl) {
		
		try {
			// when using HTTPS 
			if(flagHttps) {
				SSLContext sslCont = SSLContext.getInstance("TLS"); 
				sslCont.init(null, new TrustManager[]{new myTrustManager()}, new SecureRandom());
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCont.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(
						new MyHostnameVerifier(new URL(strHttpUrl).getHost()));
				httpConn = (HttpsURLConnection)new URL(strHttpUrl).openConnection();
			
			// when using HTTP
			} else {
				httpConn = (HttpURLConnection)new URL(strHttpUrl).openConnection();
			}
			
			HttpURLConnection.setFollowRedirects(false);
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setReadTimeout(15000);
			httpConn.setConnectTimeout(15000);
			/*httpConn.setRequestProperty("Accept-Language", "zh-cn");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setRequestProperty("Accept-Charset", "UTF-8");*/
			httpConn.setRequestProperty("Accept-Charset", "gb2312");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("User-Agent", "G3WLAN");
//			httpConn.setRequestProperty("Content-Type", "text/html");
			httpConn.setInstanceFollowRedirects(false);
			
		} 
		catch(Exception e) {
			e.printStackTrace();
			Util.Trace("initHttpConn exception: " + e.getMessage());
		}
	}

	private void initHttpConn(boolean flagHttps, String strHttpUrl, int connectTO, int readTO) {
		
		try {
			// when using HTTPS 
			if(flagHttps) {
				SSLContext sslCont = SSLContext.getInstance("TLS"); 
				sslCont.init(null, new TrustManager[]{new myTrustManager()}, new SecureRandom());
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCont.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(
						new MyHostnameVerifier(new URL(strHttpUrl).getHost()));
				httpConn = (HttpsURLConnection)new URL(strHttpUrl).openConnection();
			
			// when using HTTP
			} else {
				httpConn = (HttpURLConnection)new URL(strHttpUrl).openConnection();
			}
			
			HttpURLConnection.setFollowRedirects(false);
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setReadTimeout(readTO);
			httpConn.setConnectTimeout(connectTO);
			/*httpConn.setRequestProperty("Accept-Language", "zh-cn");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setRequestProperty("Accept-Charset", "UTF-8");*/
			httpConn.setRequestProperty("Accept-Charset", "gb2312");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("User-Agent", "G3WLAN");
//			httpConn.setRequestProperty("Content-Type", "text/html");
			httpConn.setInstanceFollowRedirects(false);
			
		} 
		catch(Exception e) {
			e.printStackTrace();
			Util.Trace("initHttpConn exception: " + e.getMessage());
		}
	}

	public boolean wlanIsPortal()
	{
		try {
			initHttpConn(false, Util.getPingUrl());
			
			// send out data using GET method
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			// process HTTP 302-redirection 
			if(rspCode != HttpURLConnection.HTTP_OK)
			{
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP)
				{
					Util.Trace("G3WLANHttp:: " + "WLAN is AuthenPortal! ");
					httpConn.disconnect();
					return true;
				}
			}
		}
		catch(Exception e) {
			response = null;
			e.printStackTrace();
		}
		
		Util.Trace("G3WLANHttp:: " + "WLAN isn't AuthenPortal! ");
		if(httpConn != null) httpConn.disconnect();
		return false;
	}
	
	public boolean wlanHaveLogin()
	{
		try {
			initHttpConn(false, Util.getPingUrl());
			
			// send out data using GET method
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Util.Trace("wlanHaveLogin:: GET response code: " + rspCode);
			
			// process HTTP 302-redirection 
//			if(rspCode == HttpURLConnection.HTTP_OK)
			if(rspCode != -1)
			{
				Util.Trace("wlanHaveLogin:: WLAN is connected! ");
				httpConn.disconnect();
				return true;
			}
		}
		catch(Exception e) {
			response = null;
			e.printStackTrace();
			Util.Trace("wlanHaveLogin:: Exception," + e.toString() + " \n" + e.getMessage());
		}
		
		Util.Trace("G3WLANHttp:: " + "WLAN isn't connected! ");
		if(httpConn != null) httpConn.disconnect();
		return false;
	}
	
	public boolean sendDataPost(boolean isHttps, String strUrl, String outData) {
		try {
			
			initHttpConn(isHttps, strUrl);
			
			// send out data using POST method
			httpConn.setRequestMethod("POST");
			// add "Cookie" value
			httpConn.setRequestProperty("Cookie", cookie);
			Util.Trace("AuthenPortal:: " + "____cookie of SendDataPost(): " + cookie);
			Utils.writeLog("____cookie of SendDataPost(): " + cookie);
			
			httpConn.connect();
			
			DataOutputStream dout = new DataOutputStream(httpConn.getOutputStream());
			dout.write(outData.getBytes());
			dout.flush();
			dout.close();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Utils.writeLog("POST response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = httpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____RspCode of SendDataPost(): " + rspCode);
					Util.Trace("AuthenPortal::" + i + "____location of SendDataPost(): " + location);
					Utils.writeLog(i + "____location of SendDataPost(): " + location);
					
					httpConn.disconnect();
					
					initHttpConn(false, location);
					httpConn.setRequestMethod("GET");
					// add "Cookie" value
					httpConn.setRequestProperty("Cookie", cookie);
					Util.Trace("AuthenPortal:: " + i + "____cookie of SendDataPost(): " + cookie);
					Utils.writeLog(i + "____cookie of SendDataPost(): " + cookie);
					
					httpConn.connect();
					
					rspCode = httpConn.getResponseCode();
					Utils.writeLog(i + " GET response code after 302: " + rspCode);
				} else {
					break;
				}
			}
			
			// process HTTP 200-OK
			if(rspCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "gb2312"));

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
				
				httpConn.getInputStream().close();
				httpConn.disconnect();
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
		try {
			
			initHttpConn(isHttps, strUrl, connectTO, readTO);
			
			// send out data using POST method
			httpConn.setRequestMethod("POST");
			// add "Cookie" value
			httpConn.setRequestProperty("Cookie", cookie);
			Util.Trace("AuthenPortal:: " + "____cookie of SendDataPost(): " + cookie);
			Utils.writeLog("____cookie of SendDataPost(): " + cookie);
			
			httpConn.connect();
			
			DataOutputStream dout = new DataOutputStream(httpConn.getOutputStream());
			dout.write(outData.getBytes());
			dout.flush();
			dout.close();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Utils.writeLog("POST response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = httpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____RspCode of SendDataPost(): " + rspCode);
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataPost(): " + location);
					Utils.writeLog(i + "____location of SendDataPost(): " + location);
					
					httpConn.disconnect();
					
					initHttpConn(false, location);
					httpConn.setRequestMethod("GET");
					// add "Cookie" value
					httpConn.setRequestProperty("Cookie", cookie);
					Util.Trace("AuthenPortal:: " + i + "____cookie of SendDataPost(): " + cookie);
					Utils.writeLog(i + "____cookie of SendDataPost(): " + cookie);
					
					httpConn.connect();
					
					rspCode = httpConn.getResponseCode();
					Utils.writeLog(i + " GET response code after 302: " + rspCode);
				} else {
					break;
				}
			}
			
			// process HTTP 200-OK
			if(rspCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "gb2312"));

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
				
				httpConn.getInputStream().close();
				httpConn.disconnect();
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
		try {
			
			initHttpConn(isHttps, outDataUrl);
			
			// send out data using POST method
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = httpConn.getHeaderField("Location");
					
					++i;
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataGet(): " + location);

					httpConn.disconnect();
					
					initHttpConn(false, location);
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					
					rspCode = httpConn.getResponseCode();
					Util.Trace(i + " GET response code after 302: " + rspCode);
					
				} else {
					break;
				}
			}
			
			if(HttpsURLConnection.HTTP_OK == rspCode) {
				host = httpConn.getURL().getHost();
				
				String setCookie = httpConn.getHeaderField("Set-Cookie");
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
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "gb2312"));
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
				
				httpConn.getInputStream().close();
				httpConn.disconnect();
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
		try {
			
			initHttpConn(isHttps, outDataUrl, connectTO, readTO);
			
			// send out data using POST method
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			
			// receive the response
			response = null;
			int rspCode = httpConn.getResponseCode();
			Util.Trace("G3WLANHttp:: " + "GET response code: " + rspCode);
			
			int i = 0;
			// process HTTP 302-redirection 
			while(rspCode != HttpURLConnection.HTTP_OK) {
				
				if(rspCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					String location;
					location = httpConn.getHeaderField("Location");
					
					++i;
					//Util.Trace("AuthenPortal", i + "____RspCode of SendDataGet(): " + rspCode);
					Util.Trace("AuthenPortal:: " + i + "____location of SendDataGet(): " + location);

					httpConn.disconnect();
					
					initHttpConn(false, location);
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					
					rspCode = httpConn.getResponseCode();
					Util.Trace(i + " GET response code after 302: " + rspCode);
					
				} else {
					break;
				}
			}
			
			if(HttpsURLConnection.HTTP_OK == rspCode) {
				host = httpConn.getURL().getHost();
				
				String setCookie = httpConn.getHeaderField("Set-Cookie");
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
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "gb2312"));
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
				
				httpConn.getInputStream().close();
				httpConn.disconnect();
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
