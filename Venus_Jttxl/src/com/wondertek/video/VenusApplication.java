package com.wondertek.video;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.wondertek.video.update.Config;
//for plugin lotuseed
//import com.lotuseed.android.Lotuseed;

public class VenusApplication  extends Application {

	private static VenusApplication sInstance = null;
	private static VenusActivity mHandle = null;
	public static String appAbsPath = null;
	public static final String activityPackage = "com.wondertek.activity";
	public static String packageName = null;
	public static int	 appPassiveStart = 0;

	private boolean mDownVenusZip = false;

	public static boolean bAppActivityIsRunning = false;
	
	private String ts;
	private String tslocal;
	
	DefaultHandler saxhandler = new DefaultHandler() {

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (localName.equals("package")) {
				if (attributes.getValue("name").equals(packageName)) {
					ts = attributes.getValue("ts");
					if (ts == null) {
						ts = attributes.getValue("ft");
					}
					Util.Trace("ts: name: " + attributes.getValue("name") + ", ts: " + ts);
					throw new SAXException();
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
		}
	};

	public VenusApplication() {
		sInstance = this;
	}

	public static VenusApplication getInstance() {
		if (sInstance == null) {
			sInstance = new VenusApplication();
		}
		return sInstance;
	}

	//Prepare all the resources for application here.
	public void onCreate() {
		VenusActivity.PHONE_PLATFORM = Util.initPhonePlatform();
		Util.getInstance().Init();
		
		Util.Trace("VenusApplication::onCreate");
		
		Config.setUpdateServer(this.getResources().getText(getResources().getIdentifier("update_server", "string", getPackageName())).toString());
		Config.setUpdateResName(this.getResources().getText(getResources().getIdentifier("update_res_name", "string", getPackageName())).toString());
		Config.setUpdateResVerJSON(this.getResources().getText(getResources().getIdentifier("update_res_ver_json", "string", getPackageName())).toString());
		Config.setInstallVenusZipLive(Integer.parseInt( this.getResources().getText(getResources().getIdentifier("install_res_live", "string", getPackageName())).toString()) == 1?true:false);
//		Config.setUpdateServer(this.getResources().getText(R.string.update_server).toString());
//		Config.setUpdateResName(this.getResources().getText(R.string.update_res_name).toString());
//		Config.setUpdateResVerJSON(this.getResources().getText(R.string.update_res_ver_json).toString());
//		Config.setInstallVenusZipLive(Integer.parseInt( this.getResources().getText(R.string.install_res_live).toString()) == 1?true:false);

		
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		String appPackage = getApplicationContext().getPackageName();
        String dirname="/data/data/" + appPackage+"/";
        appAbsPath = dirname;
        packageName = appPackage;
        boolean lib2Exists = new File(dirname + "lib2").exists();
        if(Util.GetSDK() == Util.SDK_ANDROID_15 || Util.GetSDK() == Util.SDK_ANDROID_16 || Util.GetSDK() == Util.SDK_OMS_15 || Util.GetSDK() == Util.SDK_OMS_16)
        {
        	lib2Exists = true;
        }
        boolean updateFlag = false; //mPerferences.getBoolean("updateFlag", true);
        int     oldVerCode = mPerferences.getInt("version", 0);
        int     newVerCode = Config.getVerCode(getApplicationContext());
        if(newVerCode > oldVerCode) updateFlag = true;
        File dir = new File(dirname);
		dir.mkdir();
		if(Config.getInstallVenusZipLive() == false)
		{
		      //get ts
			try {
				 SAXParserFactory factory = SAXParserFactory.newInstance();
				 SAXParser parser = factory.newSAXParser();  
				 XMLReader reader = parser.getXMLReader();  
				 reader.setContentHandler(saxhandler);  
				 InputStream is = new BufferedInputStream(new FileInputStream("/data/system/packages.xml"));
				 reader.parse(new InputSource(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//get tslocal
			tslocal = mPerferences.getString("tslocal", "");
			
			// ts == tslocal ?
			if (tslocal.equals(ts) || ts == null)
			{
				Util.Trace("ts: OK");
			}
			else
			{
				Util.Trace("ts: Update");
				updateFlag = true;
			}
			

	        if( lib2Exists == false || updateFlag == true)
	        {
	        	Util.Trace("lib2Exists = " + (lib2Exists==true?1:0));
	        	Util.Trace("updateFlag = " + (updateFlag==true?1:0));

	        	//Util.deleteDir(new File(appAbsPath + "module/"));
	        	Util.clearOldData(new File(appAbsPath));
	        	unzipVenusFromApk();
	        	
	        	if (updateFlag) {
					//save ts
			        SharedPreferences.Editor mEditor = mPerferences.edit();
			        mEditor.putString("tslocal", ts);
			        mEditor.commit();
	        	}

//	            SharedPreferences.Editor mEditor = mPerferences.edit();
//	            mEditor.putBoolean("isFirstRun", false);
//	            mEditor.commit();
	        }
		}
		else
		{
			 if( lib2Exists == false )
			 {
				 mDownVenusZip = true;
			 }
			 else
			 {
				 if(updateFlag == true)
				 {
					 //Util.deleteDir(new File(appAbsPath + "module/"));
					 Util.clearOldData(new File(appAbsPath));
					 unzipVenusFromApk();
				 }
			 }
		}
		//for plugin lotuseed
		//Lotuseed.init(this);
		//Lotuseed.onCrashLog();
		//Lotuseed.tryUpdateSlient();
		DameonService.start(this);
	}

	public void onConfigurationChanged (Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	public void onLowMemory ()
	{
		super.onLowMemory();
	}

	public void onTerminate ()
	{
		Util.Trace("VenusApplication::onTerminate");
		super.onTerminate();
	}

	private boolean unzipVenusFromApk()
	{
		Util.Trace("Unzip venus.zip now...");
		try {
			InputStream is = getAssets().open("venus.zip");
			ZipInputStream in = new ZipInputStream(is);
			ZipEntry file = in.getNextEntry();

			byte[] c = new byte[1024];

			int slen;

			while (file != null) {

				int i = file.getName().lastIndexOf('/');
				if (i != -1) {
					File dirs = new File(appAbsPath + File.separator
							+ file.getName().substring(0, i));
					dirs.mkdirs();
					dirs = null;
				}

				if (file.isDirectory()) {
					File dirs = new File(file.getName());
					dirs.mkdir();
					dirs = null;
				} else {
					File f = new File(appAbsPath + File.separator + file.getName());
					f.delete();
					FileOutputStream out = new FileOutputStream(appAbsPath
							+ File.separator + file.getName());
					while ((slen = in.read(c, 0, c.length)) != -1)
						out.write(c, 0, slen);
					out.close();
				}
				file = in.getNextEntry();
			}
			in.close();

		} catch (Exception e) {
			// Should never happen!
			throw new RuntimeException(e);
		}
		createUpdateFlag(false);
		saveVerCode(Config.getVerCode(getApplicationContext()));
		
		return true;
	}
	public boolean unzipVenus(String localZipPath)
	{
		try {
			File localFile = new File(localZipPath);
			if(localFile.exists() == false)
			{
				return false;
			}
			FileInputStream fin = new FileInputStream(localFile);
			ZipInputStream in = new ZipInputStream(fin);
			ZipEntry file = in.getNextEntry();
			File newdir = new File(appAbsPath);
			newdir.mkdir();

			byte[] c = new byte[1024];

			int slen;

			while (file != null) {

				int i = file.getName().lastIndexOf('/');
				if (i != -1) {
					File dirs = new File(appAbsPath + File.separator
							+ file.getName().substring(0, i));
					dirs.mkdirs();
					dirs = null;
				}

				if (file.isDirectory()) {
					File dirs = new File(file.getName());
					dirs.mkdir();
					dirs = null;
				} else {
					FileOutputStream out = new FileOutputStream(appAbsPath
							+ File.separator + file.getName());
					while ((slen = in.read(c, 0, c.length)) != -1)
						out.write(c, 0, slen);
					out.close();
				}
				file = in.getNextEntry();
			}
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean createUpdateFlag(boolean flag)
	{
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEditor = mPerferences.edit();
        mEditor.putBoolean("updateFlag", flag);
        mEditor.commit();
		return true;
	}
	
	public boolean saveVerCode(int verCode)
	{
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEditor = mPerferences.edit();
        mEditor.putInt("version", verCode);
        mEditor.commit();
		return true;
	}
	
	public boolean getDownVenusZip()
	{
		return mDownVenusZip;
	}

	public void setAppRunning(boolean run)
	{
		bAppActivityIsRunning = run;
	}

	public String getResString(String id)
	{
		String s = "";

		if(id.equals("common_ok"))
		{
			s = this.getResources().getText(getResources().getIdentifier("common_ok", "string", getPackageName())).toString();
		}
		else if(id.equals("common_cancel"))
		{
			s = this.getResources().getText(getResources().getIdentifier("common_cancel", "string", getPackageName())).toString();
		}
		else if(id.equals("apk_id"))
		{
			s = this.getResources().getText(getResources().getIdentifier("apk_id", "string", getPackageName())).toString();
		}
		else if(id.equals("platform"))
		{
			s = this.getResources().getText(getResources().getIdentifier("platform", "string", getPackageName())).toString();
		}
		else if(id.equals("debug"))
		{
			s = this.getResources().getText(getResources().getIdentifier("debug", "string", getPackageName())).toString();
		}
		return s;
	}
	public static final int MSG_ID_BOOT_APPACTIVITY						= 0;
	public static final int MSG_ID_RECEIVE_RECOMMEND_MESSAGE			= 1;
	public static final int MSG_ID_RECEIVE_WIDGETSNOTIFY				= 2;
	public static final int MSG_ID_RECEIVE_NOTIFICATION_NEWS_NOTIFY		= 3;
	public static final int MSG_ID_RECEIVE_NOTIFICATION_CUSTOM_MESSAGE	= 4;
	public static final int MSG_ID_RECEIVE_MESSAGE						= 5;
	public static final int MSG_ID_RECEIVE_APPOINTMENT					= 6;
	public static final int MSG_ID_RECEIVE_COMMUNITY					= 7;
	public static final int MSG_ID_RECEIVE_SMS_MESSAGE					= 8;

	public static Handler applicationHandler = new Handler(){
		public void handleMessage(Message message) {
			Util.Trace("applicationHandler::handleMessage message.what =" + message.what );
			switch(message.what)
			{
			case MSG_ID_BOOT_APPACTIVITY :
				int id = message.getData().getInt("ID");
				Util.Trace("MSG_ID_BOOT_APPACTIVITY, id = " + id);
				if(id == 0)		//Message from AppVolunteerActivity
				{
					int width = message.getData().getInt("Width");
					int height = message.getData().getInt("Height");
					int orientation = message.getData().getInt("orientation");
					VenusActivity.SetFakeScreen(width,height,orientation);
					startAppActivity(0);
				}
				break;
			case MSG_ID_RECEIVE_MESSAGE :
			{
				if(VenusApplication.bAppActivityIsRunning)
				{
					VenusActivity.nativesendsmsevent();
					startAppActivity(0);
				}
				else
				{
					startAppFakeActivity();
				}
				break;
			}
			case MSG_ID_RECEIVE_SMS_MESSAGE:
			{
				Util.Trace("applicationHandler::handleMessage VenusApplication.bAppActivityIsRunning" + VenusApplication.bAppActivityIsRunning );
				if(VenusApplication.bAppActivityIsRunning)
				{
					VenusActivity.getInstance().nativesendevent(Util.WDM_SMS, MSG_ID_RECEIVE_SMS_MESSAGE, 0);
				}
				else
				{
					if(message.getData().getBoolean("SMS_STARTUP"))
						startAppFakeActivity();
				}
				break;
			}
			default :
				break;
			}
		}
	};

	public static void startAppActivity(int passiveStart)
	{
		Util.Trace("VenusApplication:: start " + activityPackage + ".AppActivity");
		appPassiveStart = passiveStart;
		
		if(VenusApplication.bAppActivityIsRunning)
		{
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClassName(VenusApplication.getInstance().getApplicationContext(), activityPackage + ".AppActivity");
			PendingIntent mIntent = PendingIntent.getActivity(VenusApplication.getInstance().getApplicationContext(), 0, intent, 0);
			try {
				mIntent.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			Intent mIntent = new Intent();
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			mIntent.setClassName(VenusApplication.getInstance().getApplicationContext(), activityPackage + ".AppActivity");
			VenusApplication.getInstance().getApplicationContext().startActivity(mIntent);

			if(activityList!= null && activityList.size()>0)
			{
				if(Util.GetSDK() != Util.SDK_ANDROID_15 && Util.GetSDK() != Util.SDK_ANDROID_16 && Util.GetSDK() != Util.SDK_OMS_15 && Util.GetSDK() != Util.SDK_OMS_16)
				{
					try {
						Activity obj = ((Activity)activityList.get(0));
						Class<?> cls = obj.getClass();
						Method m_overridePendingTransition;
						m_overridePendingTransition = cls.getMethod("overridePendingTransition", new Class[]{int.class, int.class});
						m_overridePendingTransition.invoke(obj, new Object[]{new Integer(0), new Integer(0)});
					} catch (Exception e) {
						e.printStackTrace();
						Util.Trace(e.toString());
					}
				}
				((Activity)activityList.get(0)).finish();
				activityList.remove(0);
			}
		}
		VenusApplication.getInstance().setAppRunning(true);
	}

	public static void startAppFakeActivity()
	{
		if(VenusApplication.bAppActivityIsRunning)
		{
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClassName(VenusApplication.getInstance().getApplicationContext(), activityPackage + ".AppActivity");
			PendingIntent mIntent = PendingIntent.getActivity(VenusApplication.getInstance().getApplicationContext(), 0, intent, 0);
			try {
				mIntent.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			Intent mIntent = new Intent();
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setClassName(VenusApplication.getInstance().getApplicationContext(), activityPackage + ".AppFakeActivity");
			VenusApplication.getInstance().getApplicationContext().startActivity(mIntent);
		}
	}
	//Manager for the exit of application
	private static List<Activity> activityList = new LinkedList<Activity>();

    public void addActivity(Activity activity)
    {
    	Util.Trace("ADD: Activity name =" + activity.getComponentName());
    	activityList.add(activity);
    }    
    
    public void setEngineHandle(VenusActivity handle)
    {
    	mHandle = handle;
    }
    
    public void exit()   
    {   
    	Util.restoreMachineSettings(VenusActivity.appActivity.getContentResolver());

		Activity activity = null;

	    for( ; activityList.isEmpty() == false; )
	    {
	    	activity = (Activity) activityList.get(0);
	    	Util.Trace("DEL: Activity name = " + activity.getComponentName());
	    	activityList.remove(0);
	    	activity.finish();
	    }
	  

		VenusActivity.getInstance().onDestroy();
	    System.exit(0);   
    }
}
