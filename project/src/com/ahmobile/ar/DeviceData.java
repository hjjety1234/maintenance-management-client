package com.ahmobile.ar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ahmobile.ar.ustc.HttpConnectionUtil;
import com.ahmobile.ar.ustc.HttpConnectionUtil.HttpMethod;
import com.ahmobile.ar.ustc.domain.HttpGetListResult;
import com.ahmobile.ar.ustc.handler.GetListHandler;

public class DeviceData extends Thread{

	private static final String url = "http://10.152.22.223:8080/testMapValue";
	
	@SuppressWarnings("serial")
	public static final Map<String, String> device =  new HashMap<String, String>() {
	     {
	         put( "mhq" ,  "目标设备是灭火器" );
	         put( "ibm" ,  "1.检查指示灯是否正常ibm \n2.检查指数是否超出正常值范围   \n3.检查出风口是否正常" );
	     }
	};
	
	public void initData() throws Exception {
    	HttpGetListResult result = null;
    	try {
			result = HttpConnectionUtil.syncConnect(url, null, HttpMethod.GET, new GetListHandler("result"));
			DebugLog.LOGD("服务端查询图片对应关系数量：" + result.getListMap().size());
			for(Map<String, String> map : result.getListMap()) {
				String key = map.get("name");
				String value = map.get("value");
				DeviceData.device.put(key, value);
			}
			
			DebugLog.LOGD("最终设备图片对应关系数量：" + DeviceData.device.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void run() {
		try {
			initData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
