package com.wondertek.video.download.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
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

public class HttpCustomClient {
    private static HttpClient customerHttpClient;

    private static final int TIME_OUT_POOL		= 1000;
    private static final int TIME_OUT_CONN		= 2000;
    private static final int TIME_OUT_REQ_GET	= 5000;
    
    private HttpCustomClient() {
    }

    public static synchronized HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            HttpParams params = new BasicHttpParams();
            
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,  HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            
//            HttpProtocolParams
//                    .setUserAgent(
//                            params,
//                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
//                                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, TIME_OUT_POOL);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, TIME_OUT_CONN);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, TIME_OUT_REQ_GET);
         
            // 设置HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http",	PlainSocketFactory.getSocketFactory(),	80));
            schReg.register(new Scheme("https",	SSLSocketFactory.getSocketFactory(),	443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
            customerHttpClient = new DefaultHttpClient(conMgr, params);
        }

        return customerHttpClient;
    }
}
