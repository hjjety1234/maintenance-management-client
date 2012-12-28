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


/*添加变量信息
 * */
public class UmsConstants {
	public static boolean DebugMode = true;
	public static long kContinueSessionMillis = 30000L;
	public static final Object saveOnlineConfigMutex = new Object();
	public static final String eventUrl = "postEvent.do";
	public static final String errorUrl = "postErrorLog.do";
	public static final String clientDataUrl = "postClientData.do";
	public static final String updataUrl = "getApplicationUpdate.do";
	public static final String activityUrl = "postActivityLog.do";
	public static final String onlineConfigUrl = "getOnlineConfiguration.do";
	public static final String uploadUrl = "uploadLog.do";
	public static final String preUrl = CommonUtil.getConfig("preUrl");
}