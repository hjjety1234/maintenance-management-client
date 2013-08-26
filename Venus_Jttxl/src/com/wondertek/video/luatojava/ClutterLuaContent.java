package com.wondertek.video.luatojava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import android.util.Log;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.caller.DbHelper;
import com.wondertek.video.caller.Employee;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class ClutterLuaContent extends LuaContent {
	private static final String ACTION_CTRACE = "CTrace";
	private static final String ACTION_CADD = "CAdd";
	private static final String ACTION_CCBTEST = "CCBTest";
	private static final String ACTION_CCBTESTTWO = "CCBTestTwo";

	private static final String ACTION_SETMAPZOOM = "SetMapZoom";
	private static final String ACTION_DELETECALLCONTENT = "DeleteCallContent";
	
	private static final String TAG = "ClutterLuaContent";
	private static final String ACTION_DELETE_CALLLOG = "deleteCallLog";
	private static final String ACTION_BACKUP_APK = "backupApk";
	private static final String ACTION_GET_SMS_CONTACT = "getSmsContact";
	private static ClutterLuaContent instance = null;

	public static ClutterLuaContent getInstance()
	{
		if(instance == null)
		{
			instance = new ClutterLuaContent();
		}
		
		return instance;
	}
	
	/**
     * Constructor.
     */
    public ClutterLuaContent() {
    	instance = this;
    }
    
	@Override
	public LuaResult execute(String action, JSONArray args, String callbackId) {
		// TODO Auto-generated method stub
		LuaResult.Status status = LuaResult.Status.OK;
        String result = "";
        
        try {
        	if (action.equals(ACTION_CTRACE)) {
        		CTrace(args.getString(0));
        		return null;
        	}
        	else if (action.equals(ACTION_CADD)) {
        		result += CAdd(args.getInt(0),args.getInt(1));
        	}
        	else if(action.equals(ACTION_CCBTEST))
        	{
        		result += CCBTest(args.getInt(0),args.getInt(1));
        	}
        	else if(action.equals(ACTION_CCBTESTTWO))
        	{
        		CCBTestTwo(args.getString(0), args.getString(1), args.getString(2));
        		return null;
        	}
        	else if(action.equals(ACTION_SETMAPZOOM))
        	{
        		Log.d(TAG, " @@@ACTION_SETMAPZOOM: ");
        		// GDMapManager.getInstance().setMapZoom(args.getInt(0));
        		return null;
        	}
        	else if(action.equals(ACTION_DELETECALLCONTENT))
        	{
        		DeleteCallContent();
        		return null;
        	}
        	else if(action.equals(ACTION_DELETE_CALLLOG)) 
        	{
        		result += deleteCallLog(args.getString(0));
        	}
        	else if (action.equals(ACTION_BACKUP_APK)) 
        	{
        		result += backupApplication(args.getString(0), args.getString(1));
        	}
        	else if (action.equals(ACTION_GET_SMS_CONTACT)) 
        	{
        		result += getJsonSMSContact(8);
        	}
        	else
        	{
        		return new LuaResult(LuaResult.Status.INVALID_ACTION);
        	}
            return new LuaResult(status, result);
        } catch (JSONException e) {
            return new LuaResult(LuaResult.Status.JSON_EXCEPTION);
        }
	}

	/**
	* @Description 将app由data/app目录拷贝到sd卡下的指定目录中
	* @param appId 应用程序的ID号，如com.wondertek.jttxl
	* @param dest 需要将应用程序拷贝的目标位置
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-7-22 下午3:32:12
	*/
	private String backupApplication(String appId, String dest) {
		if (appId == null || appId.length() == 0 
				|| dest == null || dest.length() == 0) { 
			return "illegal parameters";
		}
		Util.Trace("[backupApplication] appId: " + appId + ", dest:" + dest);
		// check file /data/app/appId-1.apk exists
		String apkPath = "/data/app/" + appId + "-1.apk";
		File apkFile = new File(apkPath);
		if (apkFile.exists() == false) {
			return apkPath +  " doesn't exist!";
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(apkFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		// create dest folder if necessary
		int i = dest.lastIndexOf('/');
		if (i != -1) {
			File dirs = new File(dest.substring(0, i));
			dirs.mkdirs();
			dirs = null;
		}
		// do file copy operation
		byte[] c = new byte[1024];
		int slen;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			while ((slen = in.read(c, 0, c.length)) != -1)
				out.write(c, 0, slen);
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return e.getMessage();
				}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return e.getMessage();
				}
			}
		}
		return "success";
	}

	@Override
	public boolean isSynch(String action) {
		// TODO Auto-generated method stub
		if (action.equals(ACTION_CCBTEST)) {
			return false;
		}
		return true;
	}
	
	public void CTrace(String str)
	{
		Util.Trace("ClutterLuaContent:CTrace = " + str);
	}
	
	public int CAdd(int a, int b)
	{
		return a + b;
	}
	
	public int CCBTest(int a, int b)
	{
		for(int i=0; i<b;i++)
		{
			try {
				Thread.sleep(a);
				Util.Trace("sleep i= " + i + ", time =" + a);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return a*b;
	}
	
	public void CCBTestTwo(String str1,String str2,String str3)
	{
		Util.Trace("CCBTestTwo =" + str1 + ","+str2+ ","+str3);
		Intent sendIntent =new Intent();
		sendIntent.setAction(Intent.ACTION_VIEW);

//		sendIntent.setData(Uri.parse("file:///mnt/sdcard/3D/TOPGIRL.mp4"));
		sendIntent.setDataAndType(Uri.parse("file:///mnt/sdcard/3D/TOPGIRL.mp4"), "video/mp4");

//		sendIntent.setType("video/mp4");
//		sendIntent.setComponent(new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivit"));
//		final PackageManager packageManager = VenusActivity.appActivity.getPackageManager();
//        List<ResolveInfo> list = packageManager.queryIntentActivities(sendIntent, PackageManager.GET_ACTIVITIES);
//        if (list.size() > 0) {
//        	VenusActivity.appActivity.startActivity(Intent.createChooser(sendIntent, "ѡ��Ӧ��"));
//   
//        } else {
//        }
        
		VenusActivity.appActivity.startActivity(sendIntent); 
	}
	
	public void DeleteCallContent()
	{
		Util.Trace("DeleteCallContent");
		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}
	
	public boolean deleteCallLog(String number) {
		Util.Trace("[deleteCallLog] number: " + number);
		if (number == null) {
			return false;
		}
		ContentResolver resolver = VenusActivity.appActivity
				.getContentResolver();
		int ret = resolver.delete(CallLog.Calls.CONTENT_URI,
				CallLog.Calls.NUMBER + "=?", new String[] { number });
		if (ret > 0)
			return true;
		else
			return false;
	}
	
	/**
	* @Description 获取以JSON格式最近nLength个联系人
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-26 上午11:29:10
	*/
	public String getJsonSMSContact(int nLength) {
		Log.d(TAG, "[getJsonSMSContact] nLength: " + nLength);
		List<Contact> contactList = getSMSContactList(nLength);
		int i = 0;
		StringBuilder strBuilder = new StringBuilder();
		for (Contact contact : contactList) {
			strBuilder.append(contact.name + ",");
			strBuilder.append(contact.mobile + ",");
			strBuilder.append(contact.type + ",");
			strBuilder.append(contact.date);
			if (i < contactList.size() - 1) {
				strBuilder.append(";");
			}
			i++;
		}
		return strBuilder.toString();
	}
	
	
	/**
	* @Description 获取短信联系人列表
	* @param nLength 联系人个数
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-26 上午10:44:02
	*/
	private List<Contact> getSMSContactList(int nLength) {
		Log.d(TAG, "[getSMSContactList] nLength: " + nLength);
		// 查询短信列表
		final String SMS_URI_ALL = "content://sms/";
		StringBuilder smsBuilder = new StringBuilder();
		Uri uri = Uri.parse(SMS_URI_ALL);  
        String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };  
        Activity appActivity = VenusActivity.appActivity;
        Cursor cursor = appActivity.getContentResolver().query(uri, projection, null, null, "date desc");
        List<Contact> result = new ArrayList<Contact>();
        Map<String, String> map = new HashMap<String, String>();
        // 迭代短信列表 
        try {
        	if (cursor.moveToFirst()) {  
                int indexAddress = cursor.getColumnIndex("address");  
                int indexDate = cursor.getColumnIndex("date");
                int indexType = cursor.getColumnIndex("type");
                do {  
                	// 检查是否已经获得足够的联系人
                	if (result.size() >= nLength) break;
                    String strMobile = formatMobile(cursor.getString(indexAddress));   // 电话号码
                    long longDate = cursor.getLong(indexDate);  		 			   // 短信日期
                    int intType = cursor.getInt(indexType);  			               // 短信类型
                    // 格式化日期
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
                    Date d = new Date(longDate);  
                    String strDate = dateFormat.format(d);  
                    // 格式化短信类型
                    String strType = "";  
                    if (intType == 1) {  
                        strType = "receive";  
                    } else if (intType == 2) {  
                        strType = "send";  
                    } else {  
                       continue;  
                    }
                    // 去除重复的记录
                    if (map.containsKey(strMobile) == false) {
                    	// 由电话号码获取联系人姓名
                        String strName = getNameByMobile(strMobile); 
                        if (strName.equals(strMobile) == true) {
                        	strName = "null";
                        }
	                    // 构造联系人对象
	                    Contact contact = new Contact();
	                    contact.name = strName;
	                    contact.mobile = strMobile;
	                    contact.type = strType;
	                    contact.date = strDate;
	                    result.add(contact);
	                    map.put(strMobile, "1");
                    }
                } while (cursor.moveToNext());  
            } 
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
        	if (!cursor.isClosed()) {  
            	cursor.close();  
            	cursor = null;  
            }  
        }
        // 查询数据库获取数据库中对应的姓名
        DbHelper dbHelper = new DbHelper(VenusActivity.appContext);
        result = dbHelper.getEmployeeName(result);
		return result;
	}
	
	/**
	* @Description 格式化手机号
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-26 下午12:01:04
	*/
	private String formatMobile(String rawMobile) {
		if (rawMobile == null) return "";
		if (rawMobile.length() == 14 && rawMobile.contains("+86"))  
			return rawMobile.replace("+86", "");
		else if  (rawMobile.length() == 16 && rawMobile.startsWith("12520")) 
			return rawMobile.replace("12520", "");
		return rawMobile;
	}
	
	/**
	* @Description 判断电话号码是否有效
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-26 下午12:30:48
	*/
	private boolean isMobileValid(String rawMobile) {
		if (rawMobile == null || rawMobile.equals("")) return false;
		if (rawMobile.length() == 13 && rawMobile.contains("+86")) return true;
		else if (rawMobile.length() == 16 && rawMobile.startsWith("12520")) return true;
		else if (rawMobile.length() == 11) return true;
		return false;
	}
	
	 /**
	* @Description 由手机号码获取联系人姓名
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-8-26 上午10:57:14
	*/
	private String getNameByMobile(String mobile){ 
        if(mobile == null || mobile == ""){  
            return "null";  
        }  
        String strPerson = "null";  
        String[] projection = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};  
        Activity appActivity = VenusActivity.appActivity;  
        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, mobile); 
        Cursor cursor = appActivity.getContentResolver().query(uri_Person, projection, null, null, null);  
        try {
        	 if(cursor.moveToFirst()){  
                 int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);  
                 String strPeopleName = cursor.getString(index_PeopleName);  
                 strPerson = strPeopleName;  
             }  
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if (!cursor.isClosed()) {  
            	cursor.close();  
            	cursor = null;  
            }  
        }
        return strPerson;  
	}  
}
