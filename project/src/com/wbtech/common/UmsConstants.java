/**
 * Cobub Razor
 *
 * An open source analytics android sdk for mobile applications
 *
 * @package		Cobub Razor
 * @author		WBTECH Dev Team
 * @copyright	Copyright (c) 2011 - 2012, NanJing Western Bridge Co.,Ltd.
 * @license		http://www.cobub.com/products/cobub-razor/license
 * @link		http://www.cobub.com/products/cobub-razor/
 * @since		Version 0.1
 * @filesource
 */
package com.wbtech.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.wondertek.video.VenusApplication;

/*添加变量信息
 * */
public class UmsConstants {
public static boolean DebugMode=true;
public static long kContinueSessionMillis = 30000L;
public static final Object saveOnlineConfigMutex = new Object();
public static final String eventUrl="ums/postEvent";
public static final String errorUrl = "ums/postErrorLog";
public static final String clientDataUrl = "ums/postClientData";
public static final String updataUrl = "ums/getApplicationUpdate";
public static final String activityUrl = "ums/postActivityLog";
public static final String onlineConfigUrl ="ums/getOnlineConfiguration";
public static final String uploadUrl = "ums/uploadLog";
public static String preUrl = "";
//public static String preUrl="http://www.cobub.com/ums/service/index.php";
static {
		try {
			String file = VenusApplication.appAbsPath + "module/preUrl.ini";
			BufferedReader input = new BufferedReader(new FileReader(file));
			preUrl = input.readLine();
			preUrl = preUrl.trim();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
