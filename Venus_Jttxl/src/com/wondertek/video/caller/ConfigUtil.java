package com.wondertek.video.caller;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.content.Context;
import android.util.Log;

import com.wondertek.video.VenusApplication;

/**
 * 业务配置文件读写类
 * @author 何武
 *
 */
public class ConfigUtil {
	private static final String TAG = "ConfigUtil";
	
	/**
	 * 获取配置文件中对应键的值
	 * @param key 键值
	 * @return 返回配置文件中键对应的值
	 */
	public static String getValue(Context context, String key) {
		// DocumentBuilderFactory factory=null;
		// DocumentBuilder builder=null;
		// Document document=null;
		// InputStream inputStream=null;
		// factory=DocumentBuilderFactory.newInstance();
		// String filename = getConfigFilePath();
		// if (filename == null) return null;
		// try {
		// builder=factory.newDocumentBuilder();
		// inputStream=context.openFileInput(filename);
		// document=builder.parse(inputStream);
		// //找到根Element
		// Element root=document.getDocumentElement();
		// NodeList nodes=root.getElementsByTagName(RIVER);
		// //遍历根节点所有子节点,rivers 下所有river
		// River river=null;
		// for(int i=0;i<nodes.getLength();i++){
		// river=new River();
		// //获取river元素节点
		// Element riverElement=(Element)(nodes.item(i));
		// //获取river中name属性值
		// river.setName(riverElement.getAttribute(NAME));
		// river.setLength(Integer.parseInt(riverElement.getAttribute(LENGTH)));
		// //获取river下introduction标签
		// Element
		// introduction=(Element)riverElement.getElementsByTagName(INTRODUCTION).item(0);
		// river.setIntroduction(introduction.getFirstChild().getNodeValue());
		// Element
		// imageUrl=(Element)riverElement.getElementsByTagName(IMAGEURL).item(0);
		// river.setImageurl(imageUrl.getFirstChild().getNodeValue());
		// rivers.add(river);
		// }
		// }
		return "";
	}
	
	/**
	 * 设置配置文件中的键值
	 * @param key
	 * @param value
	 */
	public static void setValue(Context context, String key, String value) {
		
	}
	
	/**
	 * 获取业务层代码配置文件的全路径
	 * @return 配置文件路径
	 */
	private static String getConfigFilePath() {
		String config = VenusApplication.appAbsPath
				+ "/module/com_wondertek_tx/config.xml";
		File file = new File(config);
		Log.d(TAG, "config file: " + config);
		if (file.exists() == false) {
			Log.w(TAG, "config file not found!");
			return null;
		} else {
			return config;
		}
	}
}
