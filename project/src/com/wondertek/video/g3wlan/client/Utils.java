package com.wondertek.video.g3wlan.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.wondertek.video.Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.text.TextUtils;

public class Utils {
	private static final String PREFERENCE_NAME = "g3wlan_pref";

	private static final String PSK = "PSK";
	private static final String WEP = "WEP";
	private static final String EAP = "EAP";
	private static final String OPEN = "Open";
	public static final String CONSTANT_DEFAULT_VERSION = "1.0.0";

	public static String getPreference(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String value = prefs.getString(key, "");
		return value;
	}
	public static String getPreference(Context context, String key,String defValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		String value = prefs.getString(key, defValue);
		return value;
	}
	public static boolean getPreferenceBoolean(Context context, String key,boolean defValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		boolean value = prefs.getBoolean(key, defValue);
		return value;
	}

	public static void setPreference(Context context, String key, String value) {
		SharedPreferences.Editor prefsEditor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
		prefsEditor.putString(key, value);
		prefsEditor.commit();
	}

	public static void setPreference(Context context, String key, boolean value) {
		SharedPreferences.Editor prefsEditor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
		prefsEditor.putBoolean(key, value);
		prefsEditor.commit();
	}

	public static String getHumanReadableSsid(String ssid) {
		if (TextUtils.isEmpty(ssid)) {
			return "";
		}
		final int lastPos = ssid.length() - 1;
		if (ssid.charAt(0) == '"' && ssid.charAt(lastPos) == '"') {
			return ssid.substring(1, lastPos);
		}
		return ssid;
	}

	public static WifiConfiguration transScanResult2WifiConfig(ScanResult scanResult) {
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = convertToQuotedString(scanResult.SSID);
		setupSecurity(config, getScanResultSecurity(scanResult));
		// TODO for test
		// config.preSharedKey="\"lanbao+888\"";
		return config;
	}

	private static String convertToQuotedString(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}

		final int lastPos = string.length() - 1;
		if (lastPos < 0 || (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
			return string;
		}

		return "\"" + string + "\"";
	}

	private static String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { WEP, PSK, EAP };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				return securityModes[i];
			}
		}
		return OPEN;
	}

	private static void setupSecurity(WifiConfiguration config, String security) {
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();

		if (TextUtils.isEmpty(security)) {
			security = OPEN;
		}

		if (security.equals(WEP)) {
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (security.equals(PSK)) {
		} else if (security.equals(EAP)) {
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
		} else if (security.equals(OPEN)) {
			config.allowedKeyManagement.set(KeyMgmt.NONE);
		}
	}

	public static String getVersion(Context context) {
		String version = "";
		// get current version
		try {
			ComponentName comp = new ComponentName(context, "");
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			version = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * transform time readable. example,20100818164714 to 2010/08/18 16:47:14
	 * 
	 * @param str
	 * @return
	 */
	public static String formatTime(String str) {
		if (str != null && str.length() == 14) {
			StringBuffer sb = new StringBuffer(str.substring(0, 4));
			sb.append("/");
			sb.append(str.substring(4, 6));
			sb.append("/");
			sb.append(str.substring(6, 8));
			sb.append(" ");
			sb.append(str.substring(8, 10));
			sb.append(":");
			sb.append(str.substring(10, 12));
			sb.append(":");
			sb.append(str.substring(12));
			return sb.toString();
		}
		return str;
	}

	/**
	 * �����Ի���
	 * 
	 * @author x_tanghebiao
	 * @param message
	 *            ��ʾ��Ϣ
	 * @param listener
	 *            ����
	 * @param mContext
	 *            ҳ�����
	 * @param buttonText
	 *            ��ť��ʾ����
	 * @return
	 */
	public static ProgressDialog createProgressDialog(CharSequence message, DialogInterface.OnClickListener listener, Context mContext, CharSequence buttonText) {
		ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (listener != null) {
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, buttonText, listener);
		}
		return dialog;
	}

//	/**
//	 * ����Ƿ�������cmcc����
//	 * 
//	 * @author x_tanghebiao
//	 * @param mCM
//	 * @param mWifiManager
//	 * @return boolean true:���ӳɹ�; false����ʧ��
//	 */
//	public static boolean isCMCCConnected(ConnectivityManager mCM, WifiManager mWifiManager) {
//
//		State wifiState = mCM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//		if (wifiState == State.CONNECTED) {
//			WifiInfo connInfo = mWifiManager.getConnectionInfo();
//			if (connInfo != null && connInfo.getSSID() != null) {
//				if (LoginActivity.CMCC.equals(connInfo.getSSID())||LoginActivity.CMCC_EDU.equals(connInfo.getSSID())) {
//					return true;
//				}
//			}
//		}
//		return false;
////		return true;
//	}
	
	/**
	 * ����Ƿ�������cmcc����
	 * 
	 * @author x_tanghebiao
	 * @param mCM
	 * @param mWifiManager
	 * @param wlanName �ȵ����
	 * @return boolean true:���ӳɹ�; false����ʧ��
	 */
	public static boolean isCMCCConnected(ConnectivityManager mCM, WifiManager mWifiManager,String wlanName) {

		State wifiState = mCM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifiState == State.CONNECTED) {
			WifiInfo connInfo = mWifiManager.getConnectionInfo();
			if (connInfo != null && connInfo.getSSID() != null) {
				if (wlanName.equals(connInfo.getSSID())) {
					return true;
				}
			}
		}
		return false;
//		return true;
	}

//	/**
//	 * ��ʾ����Ի���
//	 * 
//	 * @author x_tanghebiao
//	 * @param message
//	 *            Ҫ��ʾ����Ϣ
//	 * @param mContext
//	 *            ҳ�����
//	 */
//	public static void showErrorDialog(String message, Context mContext) {
//		new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.app_name).setMessage(message).setPositiveButton(R.string.ok,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				}).show();
//	}

	/**
	 * ���绰�����Ƿ���11λ���ֻ����
	 * 
	 * @author x_tanghebiao
	 * @param phone
	 *            �绰����
	 * @return boolean true:�ֻ�����ʽ��ȷ; false:�ֻ�����ʽ����
	 */
	public static boolean checkPhoneNum(String phone) {
		Pattern pattern = Pattern.compile("^[0-9]{11}$");
		if (!pattern.matcher(phone).find()) {
			return false;
		}
		return true;
	}
	
	/**
	 * ��ӡ��־���ֻ��sd����
	 * @param log  Ҫ��ӡ����־��Ϣ
	 */
	public static void writeLog(String log) {
		Util.Trace(log);
	}

}
