package com.wondertek.video.download.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpImpl {

    private static final String HV_DefaultUserAgent			= "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) " + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1";
    private static final String HV_DefaultContentType		= "application/x-www-form-urlencoded";
    private static final String HV_DefaultAccept			= "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
    
    
    private static final HttpHost HOST_PROXY = new HttpHost("10.0.0.172", 80, "http");  
    
    private URL				url;
    private String			urlStr;
    private String 			ENCODE = "UTF-8";
//    private URLConnection	con;
    
	public static final int		APN_MODE_CMWAP	= 0;
	public static final int		APN_MODE_CMNET	= 1;
	public static final int		APN_MODE_WIFI	= 2;
	
	private int APN_MODE		= APN_MODE_WIFI;
	
	public HttpImpl(int apnMode, String urlString)
	{
		if(apnMode != -1)
		{
			APN_MODE = apnMode;
		}
		
		urlStr		= urlString;
	}
	
	public void setAPNMode(int apnMode)
    {
    	APN_MODE = apnMode;
    }
	
	public int getAPNMode()
    {
    	return APN_MODE;
    }
	
	public InputStream sendPost(String urlPath, String data, String contentType) {
		InputStream ins = null;
		
		if(urlPath == null)
    		urlPath = urlStr;
		
		if(APN_MODE == APN_MODE_CMWAP)
		{
			HttpResponse response = null;
	        HttpClient httpClient = HttpCustomClient.getHttpClient();
	        HttpContext localContext = new BasicHttpContext();
	        HttpPost httpPost = new HttpPost(urlPath);
	        HttpEntity entity = null;
	        
	        StringEntity sEntity = null;
	        
	        response = null;
	        
	        //Set Headers Now
	        httpPost.setHeader("User-Agent", HV_DefaultUserAgent);
	        httpPost.setHeader("Accept", HV_DefaultAccept);
	        if (contentType != null) {
	        	httpPost.setHeader("Content-Type", contentType);
	        } else {
	        	httpPost.setHeader("Content-Type", HV_DefaultContentType);
	        }
	        
	        try {
	        	sEntity = new StringEntity(data,"UTF-8");
	        } catch (UnsupportedEncodingException e) {
	        	throw new RuntimeException(e);
	        }
	        
	        httpPost.setEntity(sEntity);
	        
	        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, HOST_PROXY);
	        
	        try {
	        	response = httpClient.execute(httpPost,localContext);
	        	entity = response.getEntity();
    			ins = entity.getContent();
	        } catch (ClientProtocolException e) {
	        	e.printStackTrace();
	        	throw new RuntimeException(e);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        	throw new RuntimeException(e);
	        }      	
	        
//	        if (response != null) {
//	        	entity = response.getEntity();
//	        	try {
//					EntityUtils.toString(entity);
//				} catch (ParseException e) {
//					e.printStackTrace();
//					throw new RuntimeException(e);
//				} catch (IOException e) {
//					e.printStackTrace();
//					throw new RuntimeException(e);
//				}
//	        }
		}
		else if(APN_MODE == APN_MODE_CMNET || APN_MODE == APN_MODE_WIFI)
		{
			URLConnection con = null;
    		try {
				url = new URL(urlPath);
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				String postData = URLEncoder.encode("xxx", ENCODE) + "=" + URLEncoder.encode(data, ENCODE);
				DataOutputStream dout = new DataOutputStream(con.getOutputStream());
				dout.writeBytes(postData);				
				ins = con.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
        return ins;
    }

    public Map<String, String> getHeaders(String urlPath) {
    	HashMap<String, String> headersMap = new HashMap<String, String>();
    	
    	if(urlPath == null)
    		urlPath = urlStr;
    	
    	if(APN_MODE == APN_MODE_CMWAP)
    	{
    		HttpGet httpGet = null;
        	HttpResponse response = null;
        	HttpClient httpClient = HttpCustomClient.getHttpClient();
        	httpGet = new HttpGet(urlPath);

            //Set Headers Now
            httpGet.setHeader("Accept", HV_DefaultAccept);
            httpGet.setHeader("Content-Type", HV_DefaultContentType);
            
            //Set the proxy host
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, HOST_PROXY);
            
            try {
    			response = httpClient.execute(httpGet);
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    		
    		//Check if server response is valid
    		StatusLine status = response.getStatusLine();
    		if (status.getStatusCode() != 200) {
    			//throw new RuntimeException("Invalid response from server: " + status.toString());
    			return null;
    		}

    		HeaderIterator iterator = response.headerIterator();
    		
    		while(iterator.hasNext())
    		{
    			Header head = iterator.nextHeader();
    			String name = head.getName();
    			String value = head.getValue();
    			headersMap.put(name, value);
    		}
    	}
    	else if(APN_MODE == APN_MODE_CMNET || APN_MODE == APN_MODE_WIFI)
    	{
        	Map<String, List<String>> map = null;
    		URLConnection con = null;
    		try {
    			url = new URL(urlPath);
    		} catch (MalformedURLException ex) {
    			ex.printStackTrace();
    			throw new RuntimeException(ex);
    		}
    		
    		try {
				con = url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
    		
    		map = con.getHeaderFields();
    		Set<String> set = map.keySet();
    		for(String key : set){
            	//Util.Trace( key + " : " + map.get(key));
    			String name		= key;
    			String value	= ""+map.get(key);	//Can't be (String)map.get(key)
    			value = value.replace('[', ' ');
    			value = value.replace(']', ' ');
    			value = value.trim();
    			headersMap.put(name, value);
            }
    	}
    	
    	return headersMap;
        
    }
    
    public InputStream sendGet(String urlPath, long rangeStart, long rangeEnd) {
    	InputStream ins = null;
    	
    	if(urlPath == null)
    		urlPath = urlStr;
    	
    	if(APN_MODE == APN_MODE_CMWAP)
    	{
    		HttpGet httpGet = null;
        	HttpResponse response = null;
        	HttpClient httpClient = HttpCustomClient.getHttpClient();
        	HttpEntity entity = null;
        	
            httpGet = new HttpGet(urlPath); 

            //Set Headers Now
            httpGet.setHeader("Accept", 		HV_DefaultAccept);
            httpGet.setHeader("Content-Type",	HV_DefaultContentType);
            if(rangeStart >= 0 && rangeEnd > 0)
            	httpGet.setHeader("Range",			"bytes="+rangeStart+"-"+rangeEnd);
            
            //Set the proxy host
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, HOST_PROXY);
            
            try {
    			response = httpClient.execute(httpGet);
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}  
            
    		try {
    			entity = response.getEntity();
    			ins = entity.getContent();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    	}
    	else if(APN_MODE == APN_MODE_CMNET || APN_MODE == APN_MODE_WIFI)
    	{
    		URLConnection con = null;
    		try {
				url = new URL(urlPath);
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				con.setRequestProperty("Range", "bytes=" + rangeStart + "-" + rangeEnd);
				ins = con.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
        return ins;
    }
    
    public InputStream sendGetStream(String urlPath) {
    	InputStream ins = null;
    	
    	if(urlPath == null)
    		urlPath = urlStr;
    	
    	if(APN_MODE == APN_MODE_CMWAP)
    	{
    		HttpGet httpGet = null;
        	HttpResponse response = null;
        	HttpClient httpClient = HttpCustomClient.getHttpClient();
        	HttpEntity entity = null;
        	
            httpGet = new HttpGet(urlPath); 

            //Set Headers Now
            httpGet.setHeader("Accept", 		HV_DefaultAccept);
            httpGet.setHeader("Content-Type",	HV_DefaultContentType);
            
            //Set the proxy host
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, HOST_PROXY);
            
            try {
    			response = httpClient.execute(httpGet);
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}  
            
    		try {
    			entity = response.getEntity();
    			ins = entity.getContent();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    	}
    	else if(APN_MODE == APN_MODE_CMNET || APN_MODE == APN_MODE_WIFI)
    	{
    		URLConnection con = null;
    		try {
				url = new URL(urlPath);
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				ins = con.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
        return ins;
    }
    
    public String sendGetString(String urlPath) {
    	InputStream ins = null;
    	
    	if(urlPath == null)
    		urlPath = urlStr;
    		
    	if(APN_MODE == APN_MODE_CMWAP)
    	{
    		HttpGet httpGet = null;
        	HttpResponse response = null;
        	HttpClient httpClient = HttpCustomClient.getHttpClient();
        	HttpEntity entity = null;
        	
            httpGet = new HttpGet(urlPath); 

            //Set Headers Now
            httpGet.setHeader("Accept", 		HV_DefaultAccept);
            httpGet.setHeader("Content-Type",	HV_DefaultContentType);
            
            //Set the proxy host
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, HOST_PROXY);
            
            try {
    			response = httpClient.execute(httpGet);
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}  
            
    		try {
    			entity = response.getEntity();
    			ins = entity.getContent();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    	}
    	else if(APN_MODE == APN_MODE_CMNET || APN_MODE == APN_MODE_WIFI)
    	{
    		URLConnection con = null;
    		
    		try {
				url = new URL(urlPath);
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				ins = con.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(ins != null)
    	{
    		StringBuffer rspBuf = new StringBuffer(""); 
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(ins, "gb2312"));
				String strLine;
				
				while((strLine=br.readLine()) != null) {
					 rspBuf.append(strLine + "\r\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return rspBuf.toString();
    	}
        return "";
    }

	
}
