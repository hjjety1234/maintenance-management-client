package com.wondertek.video.appmanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.appmanager.AES;

public class AppManager {

	private static AppManager instance = null;
	private MyReceiver mReceiver;

	private static final int STATE_IDLE = 0;
	private static final int STATE_INSTALLING = 1;
	private static final int STATE_UNINSTALLING = 2;
	private static final String AES_PASSWORD = "wondertek";

	private String mPackageName = "";
	public int mState = STATE_IDLE;

	private AppManager(VenusActivity va) {
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		VenusApplication.getInstance().getApplicationContext().registerReceiver(mReceiver, filter);
	}
	
	public boolean UnRegisterReceiver()
	{
		if(mReceiver != null)
		{
			VenusApplication.getInstance().getApplicationContext().unregisterReceiver(mReceiver);
			return true;
		}
		return false;
	}


	public synchronized static AppManager getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new AppManager(va);
		}

		return instance;
	}

	public boolean InstallApp(String path) {
		if (path == null || path.equals("")) {
			onPackageInstalled(false, null);
			return false;
		}

		Util.Trace("[install] in");
		File file = new File(path);
		if (file.exists()) {
			final Uri uri = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			VenusActivity.appActivity.startActivity(intent);
			mState = STATE_INSTALLING;
			mPackageName = "";
			return true;
		} else {
			Util.Trace("[install] error: file no exist");
			onPackageInstalled(false, null);
			return false;
		}
	}
	
	public boolean InstallAppSilent(String path) {
		if (path == null || path.equals("")) {
			onPackageInstalled(false, null);
			return false;
		}

		String[] args;
		String result = "";
		ProcessBuilder processBuilder;
		Process process;
		InputStream errIs;
		InputStream inIs;
		
		args = new String[] {"pm", "getInstallLocation"};
		processBuilder = new ProcessBuilder(args);
		process = null;
		errIs = null;
		inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		
		Util.Trace("pm: " + result);
		int loc;
		try {
			loc = Integer.parseInt(result.substring(1, 2));
		} catch (Exception e) {
			loc = 0;
			e.printStackTrace();
		}
		Util.Trace("pm: " + loc);
		String [] options = {"", "-f", "-s"};
		args = new String[] { "pm", "install", "-r", options[loc], path };
		processBuilder = new ProcessBuilder(args);
		process = null;
		errIs = null;
		inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}

		if (result.endsWith("Success\n")) {
			return true;
		} else {
			onPackageInstalled(false, null);
			return false;
		}
	}
	
	public void AnalyAppString(String name, StringBuffer packageName, List<String> Param ) {
		int nlen = name.indexOf('?');
		if ( nlen == -1 ) {
			packageName.append(name);
		}else {
			packageName.append(name.substring(0, nlen));
			
			String startArg; 
			startArg 	= name.substring(nlen+1, name.length());
			
			int nlen1 = startArg.indexOf('&');
			while( nlen1 != -1 ) {
				String strParam = startArg.substring(0, nlen1);
				Param.add(strParam);
				
				startArg 	= startArg.substring(nlen1+1, startArg.length());
				nlen1 = startArg.indexOf('&');
			}
			
			if( 0 != startArg.length() ) {
				Param.add(startArg);
			}
		}
	}
	
	public boolean RunApp(String name) {
		if (name == null || name.equals("")) {
			return false;
		} 
		
		StringBuffer packageName = new StringBuffer();
		List<String> Param = new ArrayList<String>();
		AnalyAppString( name, packageName, Param );
	
		String activityName = null;
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		try {
			// Intent intent = pm.getLaunchIntentForPackage(name);
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			List<ResolveInfo> resolveInfos = pm.queryIntentActivities(
					mainIntent, PackageManager.GET_INTENT_FILTERS);
			for (ResolveInfo reInfo : resolveInfos) {
				if (packageName.toString().equals(reInfo.activityInfo.packageName)) {
					activityName = reInfo.activityInfo.name;
					break;
				}
			}

            if (activityName != null) {
                Intent intent = new Intent();
                ComponentName component = new ComponentName(
                                packageName.toString(), activityName);
                int nCount = Param.size();
                if (0 != nCount) {
                        Bundle param = new Bundle();
                        for (int i = 0; i < nCount; i++) {
                                String strParam = Param.get(i);
                                String[] keyValue = strParam.split("=");
                                if (2 == keyValue.length) {
                                        if ("SIG".equals(keyValue[0]) && "com.jifenkaahdx".equals(packageName.toString())) {
                                                keyValue[1] = AES.parseByte2HexStr(AES.encrypt(keyValue[1], AES_PASSWORD));
                                        }
                                        param.putString(keyValue[0], keyValue[1]);
                                }
                        }
                        intent.putExtras(param);
                }

                intent.setComponent(component);
                VenusActivity.appActivity.startActivity(intent);
                return true;
        } else {
                return false;
        }
		} catch (Exception e) {
			Util.Trace("[start] error: package no found");
		}
		return false;

	}

	public boolean UnInstallApp(String packageName) {
		if (packageName == null || packageName.equals("")) {
			onPackageUninstalled(false, null);
			return false;
		}

		final Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		VenusActivity.appActivity.startActivity(intent);
		mState = STATE_UNINSTALLING;
		mPackageName = "";
		return true;
	}
	
	public boolean createShortcut(String pkgName) {
		if (pkgName == null || pkgName.equals("")
				|| getAppName(pkgName) == null)
			return false;

		try {
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getAppName(pkgName));
			shortcut.putExtra("duplicate", false);

			String clsName = getClassName(pkgName);
			String action = pkgName + ".action.test";
			String appClass = clsName;

			ComponentName comp = new ComponentName(pkgName, appClass);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(action)
					.setComponent(comp));

			ShortcutIconResource iconRes = new ShortcutIconResource();
			iconRes.packageName = pkgName;
			iconRes.resourceName = getResName(pkgName);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

			VenusActivity.appActivity.sendBroadcast(shortcut);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeShortcut(String pkgName) {
		if (pkgName == null || pkgName.equals("")
				|| getAppName(pkgName) == null)
			return false;

		try {
			Intent shortcut = new Intent(
					"com.android.launcher.action.UNINSTALL_SHORTCUT");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getAppName(pkgName));

			String clsName = getClassName(pkgName);
			String action = pkgName + ".action.test";
			String appClass = clsName;

			ComponentName comp = new ComponentName(pkgName, appClass);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(action)
					.setComponent(comp));

			VenusActivity.appActivity.sendBroadcast(shortcut);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean queryShortcut(String pkgName) {
		if (pkgName == null || pkgName.equals("")
				|| getAppName(pkgName) == null)
			return false;

		String url = "";
		if (android.os.Build.VERSION.SDK_INT < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}

		String appName = getAppName(pkgName);
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?",
				new String[] { appName }, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		} else {
			return false;
		}
	}

	private String getClassName(String pkgName) {
		String clsName = null;
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		try {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			List<ResolveInfo> resolveInfos = pm.queryIntentActivities(
					mainIntent, PackageManager.GET_INTENT_FILTERS);
			for (ResolveInfo reInfo : resolveInfos) {
				if (pkgName.equals(reInfo.activityInfo.packageName)) {
					clsName = reInfo.activityInfo.name;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clsName;
	}

	private String getAppName(String pkgName) {
		String appName = null;
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		try {
			PackageInfo paInfo = pm.getPackageInfo(pkgName, 0);
			appName = paInfo.applicationInfo.loadLabel(pm).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appName;
	}

	private String getResName(String pkgName) {
		String resName = null;
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		try {
			PackageInfo paInfo = pm.getPackageInfo(pkgName, 0);
			int icon = paInfo.applicationInfo.icon;
			Resources res = pm.getResourcesForApplication(pkgName);
			resName = res.getResourceName(icon);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resName;
	}

	public String getStbId() {
		SharedPreferences sharedPrefs = VenusActivity.appActivity
				.getSharedPreferences("STBID", Context.MODE_PRIVATE);
		String stbid = sharedPrefs.getString("UUID", null);
		if (stbid == null) {
			stbid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
			Editor editor = sharedPrefs.edit();
			editor.putString("UUID", stbid);
			editor.commit();
		}
		return (stbid != null) ? stbid : "";
	}

	public void dealWithAppManager(char eventType) {
		if (eventType == VenusActivity.EVENT_PAUSE) {

		} else if (eventType == VenusActivity.EVENT_RESUME) {
			if (mState == STATE_INSTALLING) {
				onPackageInstalled(!mPackageName.equals(""), mPackageName);
			}
			if (mState == STATE_UNINSTALLING) {
				onPackageUninstalled(!mPackageName.equals(""), mPackageName);
			}
			mState = STATE_IDLE;
		}
	}

	private void onPackageInstalled(boolean result, String name) {
		if (result) {
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_APP_Installed, 1, 0);
			Util.Trace("[onPackageInstalled] success: " + name);
		} else {
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_APP_Installed, 0, 0);
			Util.Trace("[onPackageInstalled] failed");
		}
	}

	private void onPackageUninstalled(boolean result, String name) {
		if (result) {
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_APP_UnInstalled, 1, 0);
			Util.Trace("[onPackageUninstalled] success: " + name);
		} else {
			VenusActivity.getInstance().nativesendevent(Util.MsgFromJava_APP_UnInstalled, 0, 0);
			Util.Trace("[onPackageUninstalled] failed");
		}
	}
	
	public String getInstalledAppInfo() {
		String appInfo = "";
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		
        for(int i=0;i<packages.size();i++) { 
            PackageInfo packageInfo = packages.get(i); 
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0)
            {
            	String dir = packageInfo.applicationInfo.dataDir;
            	DecimalFormat format = new DecimalFormat("#0.0");
            	String apkDir = packageInfo.applicationInfo.publicSourceDir;
            	String size = format.format((new File(apkDir)).length()/(1024*1024.0)).toString() + "MB";
            	String iconPath = getAppIconFile(packageInfo.packageName);
            	appInfo += packageInfo.applicationInfo.loadLabel(pm).toString() + ","+ packageInfo.packageName
            		+ "," + packageInfo.versionName + "," + dir + "," + iconPath + "," + size + ";";
            }
        }
        
        return appInfo;
	}
	
	public String getInstalledAppInfoById(String id) {
		String appInfo = "";
		PackageManager pm = VenusActivity.appActivity.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		
        for(int i=0;i<packages.size();i++) { 
            PackageInfo packageInfo = packages.get(i); 
            if(packageInfo.packageName.equals(id)
            	&& (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0 )
            {
            	String dir = packageInfo.applicationInfo.dataDir;
            	DecimalFormat format = new DecimalFormat("#0.0");
            	String apkDir = packageInfo.applicationInfo.publicSourceDir;
            	String size = format.format((new File(apkDir)).length()/(1024*1024.0)).toString() + "MB";
            	String iconPath = getAppIconFile(packageInfo.packageName);
            	appInfo += packageInfo.applicationInfo.loadLabel(pm).toString() + ","+ packageInfo.packageName
            		+ "," + packageInfo.versionName + "," + dir + "," + iconPath + "," + size + ";";
            }
        }
        
        return appInfo;
	}
	
    private String getAppIconFile(String pkgName) {
        final String iconPath = VenusActivity.appActivity.getFilesDir() + "/";
        final int BUFFER = 4096;

        String iconName = (pkgName.hashCode() & 0x7fffffff) + ".png";
        File file = new File(iconPath, iconName);
        if (file.exists()) {
                return iconPath + iconName;
        }

        PackageManager pm = VenusActivity.appActivity.getPackageManager();
        try {
                PackageInfo paInfo = pm.getPackageInfo(pkgName, 0);
                int icon = paInfo.applicationInfo.icon;
                Resources res = pm.getResourcesForApplication(pkgName);

                InputStream istream = res.openRawResource(icon);
                OutputStream ostream = VenusActivity.appActivity.openFileOutput(iconName,
                                Context.MODE_WORLD_READABLE);
                byte[] b = new byte[BUFFER];

                int count = 0;
                while ((count = istream.read(b, 0, BUFFER)) != -1) {
                        ostream.write(b, 0, count);
                }
                ostream.flush();
                ostream.close();
                istream.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
        return iconPath + iconName;
    }

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Util.Trace("[MyReceiver] " + intent.getAction());
			String action = intent.getAction();

			// intent.getDataString() is package name,
			// such as 'package:com.wondertek.venus',
			// so we neglect eight chars in head
			if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
				Util.Trace("[MyReceiver] install: " + intent.getDataString());
				mPackageName = intent.getDataString().substring(8);
				if (mState == STATE_IDLE) {
					onPackageInstalled(!mPackageName.equals(""), mPackageName);
				}
			} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
				Util.Trace("[MyReceiver] uninstall: " + intent.getDataString());
				mPackageName = intent.getDataString().substring(8);
			}
		}
	}
}
