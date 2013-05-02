/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wondertek.video;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
//import java.io.RandomAccessFile;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.SocketAddress;
//import java.net.URL;
//import android.os.Build;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import com.google.ads.Ad;
//import com.google.ads.AdListener;
//import com.google.ads.AdRequest;
//import com.google.ads.AdRequest.ErrorCode;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;
//import com.wondertek.activity.R;
import android.widget.Toast;
import com.wondertek.video.Util;
import com.wondertek.video.alarm.AlarmObserver;
import com.wondertek.video.appmanager.AppManager;
//AuthPlugin
//import com.wondertek.video.auth.AuthPlugin;
import com.wondertek.video.browser.SysBrowserObserver;
import com.wondertek.video.calendar.CalendarObserver;
import com.wondertek.video.call.CallObserver;
import com.wondertek.video.camera.CameraObserver;
import com.wondertek.video.connection.ConnectionImpl;
import com.wondertek.video.connection.SystemConnectionManager;
import com.wondertek.video.email.EmailObserver;
import com.wondertek.video.gps.GPSObserver;
//add pj
//import com.wondertek.video.ifly.VoiceInputManager;
//Map
//import com.wondertek.video.map.MapPluginMgr;
import com.wondertek.video.monitor.MonitorCommon;
import com.wondertek.video.monitor.MonitorHeadset;
import com.wondertek.video.monitor.MonitorManager;
import com.wondertek.video.monitor.MonitorScreen;
import com.wondertek.video.msgpush.MsgPushManager;
//import com.wondertek.video.phonegap.PhonegapObserver;
import com.wondertek.video.sensor.SensorObserver;
import com.wondertek.video.smsspam.SMSSpamMgr;
//import com.wondertek.video.sysplayer.SysMediaPlayerMgr;
import com.wondertek.video.telephone.PhoneObserver;
import com.wondertek.video.update.UpdateMan;
import com.wondertek.video.wifi.WifiObserver;
//fractalad: for plugin FractalAD, now it's not used
//import com.wondertek.video.fractalad.FractalADPlugin;

//safetyAuthentic: for plugin CA, now it's not used
//import com.wondertek.video.authentic.AuthenticObserver;

//safetyAuthentic: for plugin CA, now it's not used
//import com.wondertek.video.authentic2.Authentic2Observer;

//lotuseed
//import com.wondertek.video.lotuseed.LotuseedObserver;

public class VenusActivity implements SurfaceHolder.Callback {
	static String TAG = "VenusActivity";
	public static String startParam = "";

	public static int PHONE_PLATFORM = 0;	//0-ANDROID, 1-OPHONE

	public static Activity appActivity = null;
	public static Context  appContext = null;

	private static VenusActivity sInstance = null;
	private VenusView venusview = null;
	//add pj
	public SurfaceHolder VenusViewHolder = null;
	private static SurfaceView videoview = null;
	private SurfaceHolder mHolder = null;
	private Surface mSurface = null;
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Rect mRectDest;
	private Matrix mMatrix;
	private int[] mRrawPicture;

	//pj382 add by fgx
	//private AdView adView = null;
	//private AdRequest adRequest  = null;

	private boolean mHaveStatusBar = true;				//Indicate that whether the application has status bar or not. 
	//private boolean mVideoIsFull = false;
	
	private static final int DISPLAY_MODE_PORTAIT		= 0;	//Portait
	private static final int DISPLAY_MODE_LANDSCAPE		= 1;	//Landscape
	private int mDispalyMode = DISPLAY_MODE_PORTAIT;
	private int mOldDispalyMode = DISPLAY_MODE_PORTAIT;

	private static RootLayout al = null;

	protected static final int GUIUPDATEIDENTIFIER = 0x101;

	//	static int screenWidth = 0;
	//	static int screenHeight = 0;
	//	static int statusHeight = 0;

	static int fakeScreenWidth = 0;
	static int fakeScreenHeight = 0;
	static int fakeScreenStatusBarHeight = 0;
	static int fakeScreenorientation = 0;

	private int videoSurfaceWidth = 1;
	private int videoSurfaceHeight = 1;
	private boolean bAutoRotateScreen = true; 

	private int videoRawWidth;
	private int videoRawHeight;

	private int g_player_handle = 0;

	private static int update_frequency = 40;

	public static String mActivityFullName = "";

	private static final int SYS_STATE_IDLE				= 0;
	private static final int SYS_STATE_UPDATING			= 1;
	private static final int SYS_STATE_RUN				= 2;

	private int sysState								= SYS_STATE_IDLE;

	private boolean mKeyPrepared		= false;

	private boolean bViewCreated = false;

	private Button Edit_yes;
	private Button Edit_no;
	private TextView Edit_title;
	private EditText Edit_text;
	private RelativeLayout Edit_buttonView;
	private LinearLayout Edit_view;


	private InputMethodManager Edit_inputone;
	private EditText Edit_textone;
	private LinearLayout Edit_viewone;
	private boolean bmultiLines;
	private int maxCounts;
	private boolean bRunSetText=false;
	private String m_defaultText;
	private int m_top;
	private int m_width;
	private int m_height;
	//private Button closeAdBtn;

	private Button Contact_yes;
	private Button Contact_no;
	private TextView Contact_title;
	private ListView Contact_list;
	private RelativeLayout Contact_buttonView;
	private LinearLayout Contact_view;
	WebView webView;
	private LinearLayout webView_LinearLayout;
	private LinearLayout webView_Center;
	private AbsoluteLayout webView_UP;
	private AbsoluteLayout webView_UP2;
	private AbsoluteLayout webView_Down;
	private AbsoluteLayout webView_Down2;
	private AbsoluteLayout webView_Left;
	private AbsoluteLayout webView_Left2;
	private AbsoluteLayout webView_Right;
	private AbsoluteLayout webView_Right2;

	boolean bCleanHistory;
	ImageView imageView;
	Animation animation;
	Stack<String> oldurls = new Stack<String>();
	private Stack<String> perurls = new Stack<String>();
	private Cursor Contact_cursor;
	private HashSet<Integer> Contact_positions;
	private ContactAdapter Contact_adapter;
	private int Contact_Count = 0;
	private ArrayList<String> Contact_NumberList;
	private ArrayList<String> Contact_NameList;
	private ArrayList<Integer> Contact_SelectedList;
	private HashMap<String, String> Contact_Map ;

	/**
	 * Must select a player.
	 */
	private int MEDIA_PLAYER_TYPE = 0;
	private static final int TMPC_MEDIA_PLAYER					= 1;
	private static final int PE_MEDIA_PLAYER					= 2;
	private static final int TMPC_MEDIA_PLAYER_PublicSurface	= 3;
	private static final int PE_MEDIA_PALYER_OVERLEY			= 4;
	private static final int WD_MEDIA_PLAYER_PublicSurface      = 5;
	
	public static final char Enum_StringEventID_VOICE					= 1;
	public static final char Enum_StringEventID_NOTIFICATION_TEXT		= 2;
	public static final char Enum_StringEventID_NOTIFICATION_PROCESS	= 3;
	public static final char Enum_StringEventID_APPCALLBACK				= 4;
	public static final char Enum_StringEventID_WAPSTART				= 5;
	public static final char Enum_StringEventID_INTENT_DATA				= 6;
	public static final char Enum_StringEventID_VEDIO_DATA				= 7;
	public static final char Enum_StringEventID_VEDIO					= 8;

	//Listen the event of G-SENSOR
	private static VenusOrientation venusOrientation;

	//Observer for media player
	//public PlayerObserver playerObserver;

	//Observer for wifi
	public WifiObserver wifiObserver;
	public CameraObserver cameraObserver;

	public SensorObserver sensorObserver;

	public CallObserver callObserver;

	public GPSObserver gpsObserver;

	public SysBrowserObserver browserObserver;

	public EmailObserver emailObserver;

	public AlarmObserver alarmObserver;

	public CalendarObserver calendarObserver;

//	public PhonegapObserver phonegapObserver;
	public AbsoluteLayout webViewRoot = null;

	public AppManager	appManager;
	public static final int CAMERA_RESULT = 100;
	public static final int CALL_RESULT = 101;
	public static final int REQUEST_PICKER_ALBUM = 102;
	
	public static long tolRxTraffic = -1;
	public static long tolTxTraffic = -1;

	//FractalADPlugin
	//public FractalADPlugin fractalADPlugin = null;
	//AuthPlugin
//	public AuthPlugin authPlugin = null;
	//TODO VoiceInput
	//add pj
//	public VoiceInputManager viManager;

	//Manage the KeyQuard
	//private KeyguardLock keyguardLock = null;

	//MsgPush
	//add PJ
	private MsgPushManager msgPushMgr = null;

	//SMSSpam
	public SMSSpamMgr smsSpamMgr = null;

	//TODO SysPlayer
	//	public SysMediaPlayerMgr sysMediaPlayer = null;

	//Map
	//	public MapPluginMgr mapPluginMgr = null;

	//safetyAuthentic: for plugin CA, now it's not used
	//public AuthenticObserver authenticObserver = null;

	//safetyAuthentic2: for plugin CA, now it's not used
	//public Authentic2Observer authentic2Observer = null;

	//for plugin lotuseed
	//public LotuseedObserver lotuseedObserver = null;

	public MediaRecorder mRecorder = null;

	public VenusActivity(Activity appActivity) {
		VenusActivity.appActivity = appActivity;
		appContext = VenusApplication.getInstance().getApplicationContext();
	}

	public static VenusActivity getInstance(Activity appActivity) {
		if (sInstance == null) {
			sInstance = new VenusActivity(appActivity);
		}
		return sInstance;
	}

	public static VenusActivity getInstance() {
		return sInstance;
	}

	public static void SetFakeScreen(int width, int height, int orientation) {
		// TODO Auto-generated method stub
		fakeScreenWidth = width;
		fakeScreenHeight = height;
		fakeScreenorientation = orientation;
	}

	Handler refashHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == VenusActivity.GUIUPDATEIDENTIFIER)
			{
				long delay = System.currentTimeMillis();
				nativetimeslice();
				delay = System.currentTimeMillis() - delay;
				delay = (delay>=update_frequency) ?  1 : update_frequency - delay;
				refashHandler.sendEmptyMessageDelayed(VenusActivity.GUIUPDATEIDENTIFIER, delay);
			}
		}
	};

	public void onPreInit()
	{
		//TODO
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		Util.Trace(TAG+"::"+"onCreate");

		appActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(VenusApplication.getInstance().getDownVenusZip())
		{
			sysState = SYS_STATE_UPDATING;
			UpdateMan.getInstance(this).downLoadVenusZip();
		}
		else
		{
			sysInit();
		}
	}

	public static final int MSG_ID_UPDATE_APK				= 0;
	public static final int MSG_ID_DOWN_VENUS_ZIP_OK		= 1;
	public static final int MSG_ID_DOWN_VENUS_ZIP_FAIL		= 2;
	public static final int MSG_ID_BOOT_APP_ASYNCHRONOUS	= 3;
	public static final int MSG_ID_EXIT						= 4;
	public static final int MSG_ID_CONTACT_VIEW_BACK		= 5;
	public static final int MSG_ID_EDIT						= 6;
	public static final int MSG_ID_EDIT_SELECT				= 7;
	public static final int MSG_ID_EDIT_TEXTCHANGE  		= 8;
	public static final int MSG_ID_FILE_EXPLORERES  		= 9;
	public static final int MSG_ID_ADVIEW_STATE				= 10;
	public static final int MSG_ID_Changed_VideoWindow		= 11;
	
    private Handler venusEventHandler =new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID_UPDATE_APK :
			{
				Bundle bundle = msg.getData();
				String apkPath = bundle.getString("PATH");
				File file = new File(apkPath);
				if(file.exists())
				{
					VenusApplication.getInstance().createUpdateFlag(true);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(apkPath)),
							"application/vnd.android.package-archive");
					//VenusActivity.appActivity.startActivityForResult(intent, 100);
					VenusActivity.appActivity.startActivity(intent);

					nativeexit();
					VenusApplication.getInstance().exit();
				}
				break;
			}
			case MSG_ID_DOWN_VENUS_ZIP_OK :
			{
				Bundle bundle = msg.getData();
				String venusZipPath = bundle.getString("PATH");
				boolean success = VenusApplication.getInstance().unzipVenus(venusZipPath);
				if(success)
				{
					sysInit();
				}
				break;
			}

			case MSG_ID_CONTACT_VIEW_BACK :
			{
				venusview.setVisibility(View.VISIBLE);
				break;
			}
			case MSG_ID_EDIT:
			{
				boolean bflag = (Boolean) msg.obj;
				if(!bflag)
				{
					//nativeneweditreturn(Edit_textone.getText().toString(), 0, false);
					Edit_textone.clearFocus();
					Edit_inputone.hideSoftInputFromWindow(Edit_textone.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					Edit_viewone.setVisibility(View.GONE);
					maxCounts = 256;
					bmultiLines = false;
				}
				else
				{
					Edit_viewone.setVisibility(View.VISIBLE);
					Edit_viewone.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth,
							fakeScreenHeight, 0, m_top+20));
					Edit_textone.setMaxWidth(m_width);
					if(!bmultiLines)
						Edit_textone.setMaxLines(1);
					else
						Edit_textone.setMaxLines(100);
					Edit_textone.setMaxHeight(m_height);
					Edit_textone.setCursorVisible(false);
					Edit_textone.requestFocus();
					bRunSetText = true;
					Edit_textone.setText(m_defaultText);
					Edit_inputone.showSoftInput(Edit_textone, 0);
				}

				break;
			}
			case MSG_ID_EDIT_SELECT:
			{
				int nSelect = (Integer) msg.obj;
				if(nSelect > Edit_textone.getText().length())
				{
					Log.e(TAG,"error msg nSelect = " + nSelect + " > TextLength = " + Edit_textone.getText().length());
					nSelect = Edit_textone.getText().length();
				}
				Edit_textone.setSelection(nSelect);
				Edit_inputone.showSoftInput(Edit_textone, 0);
				break;
			}
			case MSG_ID_EDIT_TEXTCHANGE:
			{
				String text = (String) msg.obj;
				Edit_textone.setText(text);
				break;
			}
			case MSG_ID_EXIT :
			{
				nativeexit();
				VenusApplication.getInstance().exit();
				break;
			}
			case MSG_ID_FILE_EXPLORERES:
			{
				Bundle bundle = msg.getData();
				String filePathName = bundle.getString("filePathName");
				long fileSize = bundle.getLong("fileSize");
				int allFileNum = bundle.getInt("allFileNum");
				int fileExplorePercent = bundle.getInt("fileExplorePercent");
				Util.Trace("@@handler--MSG_ID_FILE_EXPLORERES--" + filePathName + "," + fileSize + "," + allFileNum + "," + fileExplorePercent);
				nativefileexplorereturn(filePathName, fileSize, allFileNum, fileExplorePercent);
				break;
			}
			case MSG_ID_ADVIEW_STATE:
			{
				//Util.Trace("@@nativeadmobviewreturn--IN");
				//adView.setVisibility(View.INVISIBLE);
				//nativeadmobviewreturn(1);
			}
			default:
				break;
			}
		}
	};

	public void sendVenusEvent(Message msg)
	{
		venusEventHandler.sendMessage(msg);
	}

	private boolean checkCPUArmV7()
	{
		String str = "";
		boolean support = false;
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo"); 
			InputStreamReader ir = new InputStreamReader(pp.getInputStream()); 
			LineNumberReader input = new LineNumberReader(ir); 

			for (int i = 1; i < 100; i++) { 
				str = input.readLine(); 
				if (str != null) { 
					if (str.indexOf("Feature") > -1) {
						if (str.indexOf("neon") > -1 || str.indexOf("NEON") > -1)
							support = true;
						break;
					}
				} else { 
					break; 
				}
			} 
		} catch (IOException ex) { 
			ex.printStackTrace(); 
		} 
		Util.Trace("checkCPUArmV7 = " + support);
		return support;
	}

	private void sysInit()
	{
		batteryInfo = new CBatteryInfo(this);
		MonitorManager.getInstance(this).Init();
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_BATTERY);
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_SCREEN);
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_WIFI);
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_SD);
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_CONTACTS);
		MonitorManager.getInstance(this).RegisterMonitor(MonitorCommon.MONITOR_TYPE_HEADSET);

		/*
		 * Create a VenusView and set its content. the view is retrieved by
		 * calling a native function.
		 */
		int sdk = Util.GetSDK();
		if(sdk == Util.SDK_ANDROID_15 || sdk == Util.SDK_ANDROID_16 || sdk == Util.SDK_OMS_15 || sdk == Util.SDK_OMS_16)
		{
			String dirname = VenusApplication.appAbsPath + "/lib/";
			System.load(dirname + "liblauncher.so");
		}
		else
		{
			String dirname = VenusApplication.appAbsPath + "/lib2/WD/";
			if(sdk <= Util.SDK_ANDROID_21 || sdk == Util.SDK_OMS_20)	
				System.load(dirname + "sdk21/libskiaref.so");
			else if(sdk <= Util.SDK_ANDROID_23 || sdk == Util.SDK_OMS_25 || sdk == Util.SDK_OMS_26)
				System.load(dirname + "sdk23/libskiaref.so");
			else if(sdk <= Util.SDK_ANDROID_33)
				System.load(dirname + "sdk33/libskiaref.so");
			else if(sdk <= Util.SDK_ANDROID_40)
				System.load(dirname + "sdk40/libskiaref.so");
			else if(sdk <= Util.SDK_ANDROID_41)
				System.load(dirname + "sdk41/libskiaref.so");
			else
				System.load(dirname + "sdk42/libskiaref.so");

			File apiV7 = new File(dirname + "armv7/libapi.so");
			if (checkCPUArmV7() && apiV7.exists())
				System.load(dirname + "armv7/libapi.so");
			else
				System.load(dirname + "libapi.so");
			System.load(dirname + "libcomrepository.so");
			System.load(dirname + "liblua.so");
			System.load(dirname + "liblauncher.so");
			//System.load(VenusApplication.appAbsPath + "/lib2/webbrowser/" + "libwebbrowser.so");
			//System.load(VenusApplication.appAbsPath + "/lib2/admobview/" + "libadmobview.so");
		}
		//GDMAP
		//System.load(VenusApplication.appAbsPath + "/lib2/mapview/libmapview.so");
		// use for c call java find this activity.
		mActivityFullName = this.getClass().getName().replace('.', '/');

		SystemConnectionManager.getInstance().Init();

		al = new RootLayout(appActivity);

		al.setOnResizeListener(new RootLayout.OnResizeListener() {

			@Override
			public void OnResize(int maxh, int h) {
				// TODO Auto-generated method stub
				int autoh = m_top -  (maxh - h) + Edit_textone.getHeight();

				if(autoh < 0 || h == 0 || mDispalyMode != mOldDispalyMode)
				{
					mOldDispalyMode = mDispalyMode;
					autoh = 0;
				}

				venusview.setLayoutParams(new AbsoluteLayout.LayoutParams(javaGetRenderWidth(),javaGetRenderHeight(), 0, -autoh));

				nativeKeyboardSize(h);
			}
		});

		if(fakeScreenWidth>0)
		{
			int h = 0;
			try
			{
				Class c = Class.forName("com.android.internal.R$dimen");
				Object obj = c.newInstance();
				java.lang.reflect.Field field = c.getField("status_bar_height");
				int x = Integer.parseInt(field.get(obj).toString());
				h = appActivity.getResources().getDimensionPixelSize(x);
			}
			catch(Exception e)
			{

			}
			Util.Trace("fakeScreenStatusBarHeight=" +h); 
			fakeScreenStatusBarHeight = h;
		}

		Edit_view = new LinearLayout(appActivity);
		Edit_view.setOrientation(LinearLayout.VERTICAL);
		Edit_view.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth,
				fakeScreenHeight, 0, 0));
		Edit_view.setBackgroundColor(Color.parseColor("#000000"));

		Edit_buttonView = new RelativeLayout(appActivity);
		Edit_buttonView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		RelativeLayout.LayoutParams pLeft = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams pRight = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		pLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		pRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

		Edit_yes = new Button(appActivity);
		Edit_yes.setText(VenusApplication.getInstance().getResString("common_ok"));
		Edit_yes.setOnClickListener(listener);
		Edit_yes.setLayoutParams(pLeft);

		Edit_no = new Button(appActivity);
		Edit_no.setText(VenusApplication.getInstance().getResString("common_cancel"));
		Edit_no.setOnClickListener(listener);
		Edit_no.setLayoutParams(pRight);

		Edit_buttonView.addView(Edit_yes);
		Edit_buttonView.addView(Edit_no);

		Edit_title = new TextView(appActivity);
		Edit_text = new EditText(appActivity);
		Edit_text.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		Edit_text.setGravity(Gravity.TOP);

		Edit_view.addView(Edit_buttonView);
		Edit_view.addView(Edit_title);
		Edit_view.addView(Edit_text);
		Edit_view.setVisibility(View.GONE);

		bmultiLines = false;
		maxCounts = 15;
		Edit_viewone = new LinearLayout(appActivity);
		Edit_viewone.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Edit_textone = new EditText(appActivity);
		Edit_inputone = (InputMethodManager)appActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		Edit_textone.setMaxHeight(fakeScreenHeight);
		Edit_textone.setMaxWidth(fakeScreenWidth);
		Edit_viewone.addView(Edit_textone);
		Edit_textone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				try
				{
					if(count - before ==1)
					{
						String str = s.toString().substring(start, start+1);
						if(str.equals("\n")&&!bmultiLines)
						{
							if(start==(s.toString().length()-1))
								nativeneweditreturn(s.toString().substring(0, start), count, false);
							else if(start < (s.toString().length()-1))
								nativeneweditreturn(s.toString().substring(0, start)+s.toString().substring(start+1,s.toString().length()), count, false);

							Edit_textone.clearFocus();
							Edit_inputone.hideSoftInputFromWindow(Edit_textone.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
							Edit_viewone.setVisibility(View.GONE);
							return;
						}
					}
					if(s.toString().length()>maxCounts)
					{
						if(bRunSetText)
							bRunSetText = false;
						else
						{	
							Edit_textone.setSelection(start);
							nativeneweditreturn(s.toString().substring(0, start)+s.toString().substring(start+1,maxCounts+1), count-(s.toString().length()-maxCounts), false);
							Edit_textone.clearFocus();
							Edit_inputone.hideSoftInputFromWindow(Edit_textone.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
							Edit_viewone.setVisibility(View.GONE);

						}
					}
					else
					{
						if(bRunSetText)
							bRunSetText = false;
						else
							nativeneweditreturn(s.toString(), (count<maxCounts)?(count-before):0, true);

					}
				}
				catch(IndexOutOfBoundsException  ex)
				{
					ex.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		Edit_viewone.setVisibility(View.GONE);

		Contact_positions = new HashSet<Integer>();

		Contact_view = new LinearLayout(appActivity);
		Contact_view.setOrientation(LinearLayout.VERTICAL);
		Contact_view.setLayoutParams(new AbsoluteLayout.LayoutParams(
				fakeScreenWidth, fakeScreenHeight, 0, 0));
		Contact_view.setBackgroundColor(Color.parseColor("#000000"));

		Contact_buttonView = new RelativeLayout(appActivity);
		Contact_buttonView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		Contact_yes = new Button(appActivity);
		Contact_yes.setText(VenusApplication.getInstance().getResString("common_ok"));
		Contact_yes.setOnClickListener(listener);
		Contact_yes.setLayoutParams(pLeft);

		Contact_no = new Button(appActivity);
		Contact_no.setText(VenusApplication.getInstance().getResString("common_cancel"));
		Contact_no.setOnClickListener(listener);
		Contact_no.setLayoutParams(pRight);

		Contact_buttonView.addView(Contact_yes);
		Contact_buttonView.addView(Contact_no);

		Contact_title = new TextView(appActivity);

		Contact_list = new ListView(appActivity);
		Contact_list.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		//		appActivity.startManagingCursor(Contact_cursor);
		Contact_adapter = new ContactAdapter(appActivity);
		Contact_list.setAdapter(Contact_adapter);

		Contact_NumberList = new ArrayList<String>();
		Contact_NameList  = new ArrayList<String>();
		Contact_SelectedList = new ArrayList<Integer>();
		Contact_view.addView(Contact_buttonView);
		Contact_view.addView(Contact_title);
		Contact_view.addView(Contact_list);
		Contact_view.setVisibility(View.GONE);

		/////////////////////////////////////
		webView_LinearLayout = new LinearLayout(appActivity);
		webView_LinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_LinearLayout.setOrientation(LinearLayout.VERTICAL);

		webView = new WebView(appActivity);
		webView.setId(100);

		webView.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
		webView.setWebViewClient(new WDViewClient(this));
		webView.setInitialScale(0);
		webView.setVerticalScrollBarEnabled(false);
		webView.requestFocusFromTouch();
		//add tsh
		WebView.enablePlatformNotifications();
		//webView.setHttpAuthUsernamePassword("10.0.0.172", "", "", "");
		webView.setDownloadListener(new MyWebViewDownLoadListener());

		// Enable JavaScript
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		settings.setPluginsEnabled(true);
		if(sdk >= Util.SDK_ANDROID_22)
		{
			Class<?> cls;
			try {
				cls = Class.forName("android.webkit.WebSettings");
				Class<?> cls1 = Class
						.forName("android.webkit.WebSettings$PluginState");
				Object enumON = null;
				for (Object obj : cls1.getEnumConstants()) {
					if (((Enum) obj).name().equals("ON")) {
						enumON = obj;
						break;
					}
				}
				Method m_setPluginState = cls.getMethod("setPluginState",
						new Class[] { cls1 });
				m_setPluginState.invoke(settings, enumON);
				// settings.setPluginState(android.webkit.WebSettings.PluginState.ON);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//Set the nav dump for HTC
		settings.setNavDump(true);

		// Enable database
		settings.setDatabaseEnabled(true);
		String databasePath = appActivity.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(databasePath);

		// Enable DOM storage
		settings.setDomStorageEnabled(true);

		// Enable built-in geolocation
		settings.setGeolocationEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setAllowFileAccess(true);

		webView_UP = new AbsoluteLayout(appActivity);
		webView_UP.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_UP2 = new AbsoluteLayout(appActivity);

		webView.setVisibility(View.INVISIBLE);
		webView_UP.addView(webView_UP2);
		webView_LinearLayout.addView(webView_UP);

		webView_Center = new LinearLayout(appActivity);
		webView_Center.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_Center.setOrientation(LinearLayout.HORIZONTAL);

		webView_Left = new AbsoluteLayout(appActivity);
		webView_Left.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_Left2 = new AbsoluteLayout(appActivity);
		webView_Left.addView(webView_Left2);

		webView_Center.addView(webView_Left);

		webView_Center.addView(webView);

		webView_Right = new AbsoluteLayout(appActivity);
		webView_Right.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_Right2 = new AbsoluteLayout(appActivity);
		webView_Right.addView(webView_Right2);

		webView_Center.addView(webView_Right);

		webView_LinearLayout.addView(webView_Center);

		webView_Down = new AbsoluteLayout(appActivity);
		webView_Down.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		webView_Down2 = new AbsoluteLayout(appActivity);
		webView_Down.addView(webView_Down2);

		webView_LinearLayout.addView(webView_Down);

		animation = new RotateAnimation(0.0f, +3600.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(20000);
		animation.setRepeatCount(2);

		imageView = new ImageView(appActivity);
		imageView.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 50, 10, 10));
		imageView.setImageResource(appActivity.getResources().getIdentifier("loading", "drawable", appActivity.getPackageName()));
		//		imageView.setImageResource(R.drawable.loading);
		imageView.setVisibility(View.INVISIBLE);
		//		imageView.setAnimation(animation);
		//		imageView.setVisibility(View.VISIBLE);
		//      javaBrowserCreateWindow(0,100,480,400);
		//  	javaBrowserOpenUrl("http://192.168.1.19/main.htm");
		// 		javaShowWebBrowser(true);

		PhoneObserver.getInstance(appActivity); 
		wifiObserver = WifiObserver.getInstance(this);
		cameraObserver = CameraObserver.getInstance(this);
		callObserver = CallObserver.getInstance(this);
		sensorObserver = SensorObserver.getInstance(this);
		gpsObserver = GPSObserver.getInstance(this);
		browserObserver = SysBrowserObserver.getInstance(this);
		emailObserver = EmailObserver.getInstance(this);
		alarmObserver = AlarmObserver.getInstance(this);
		calendarObserver = CalendarObserver.getInstance(this);
//		phonegapObserver = PhonegapObserver.getInstance(this);

		//safetyAuthentic: for plugin CA, now it's not used
		//authenticObserver = AuthenticObserver.getInstance(this);

		//safetyAuthentic: for plugin CA, now it's not used
		//authentic2Observer = Authentic2Observer.getInstance(this);

		//for plugin lotuseed
		//lotuseedObserver = LotuseedObserver.getInstance(this);

		//SMSSpam
		if (smsSpamMgr == null)
			smsSpamMgr = SMSSpamMgr.getInstance(appActivity);
		
        // Add web view but make it invisible while loading URL
		/////////////////////////////////////
		
		////adView_LinearLayout = new LinearLayout(appActivity);
		////adView_LinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
		////		LayoutParams.FILL_PARENT));

		//adView = new AdView(appActivity, AdSize.BANNER, "a150da56d5a5621");
		//adView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
		//		LayoutParams.WRAP_CONTENT));

		////adView.setLayoutParams(new AbsoluteLayout.LayoutParams(480, 100, 0, 0));
        //adRequest = new AdRequest();
        ////adRequest.addTestDevice("8E452640BC83C672B070CDCA8AB9B06B");
        //adView.loadAd(adRequest);

        ////adView.setBackgroundColor(Color.RED);
        ////adView.setVisibility(View.VISIBLE);
		////adView_LinearLayout.addView(adView);

		//closeAdBtn = new Button(appActivity);
		////closeAdBtn.setText(VenusApplication.getInstance().getResString("open?"));
		//closeAdBtn.setBackgroundResource(R.drawable.adbutton);
		//closeAdBtn.setLayoutParams(pRight);

		//closeAdBtn.setOnClickListener(new View.OnClickListener()
		//{
		//	@Override
		//	public void onClick(View v) {
		//		if (v.equals(closeAdBtn)) {
		//			//adView.setVisibility(View.INVISIBLE);
		//			//nativeadmobviewreturn(1);
		//			Message msg = new Message();
		//			msg.what = MSG_ID_ADVIEW_STATE;
		//			venusEventHandler.sendMessage(msg);
		//	}
		//	}		
		//});

		////adView.addView(closeAdBtn);
		//adView.setVisibility(View.INVISIBLE);
		////adView_LinearLayout.addView(closeAdBtn);
		////adView_LinearLayout.setBackgroundColor(Color.RED);
        ////adView_LinearLayout.setVisibility(View.VISIBLE);
        //adView.setAdListener(new AdListener(){

		//	@Override
		//	public void onDismissScreen(Ad arg0) {
		//		// TODO Auto-generated method stub

		//	}

		//	@Override
		//	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		//		// TODO Auto-generated method stub

		//		Util.Trace("@@fgx_adviewconnect---fail!!!");
		//	}

		//	@Override
		//	public void onLeaveApplication(Ad arg0) {
		//		// TODO Auto-generated method stub

		//	}

		//	@Override
		//	public void onPresentScreen(Ad arg0) {
		//		// TODO Auto-generated method stub

		//	}

		//	@Override
		//	public void onReceiveAd(Ad arg0) {
		//		// TODO Auto-generated method stub
		//		Util.Trace("@@fgx_adviewconnect---ok!!!");
		//	}

        //});

		venusview = new VenusView(this, appActivity);
		venusview.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth,fakeScreenHeight, 0, 0));
		venusview.setVisibility(View.VISIBLE);

		videoview = new SurfaceView(appActivity);
		videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(videoSurfaceWidth, videoSurfaceHeight, 0, 0));
		//		videoview.setVisibility(View.INVISIBLE);
		videoview.setVisibility(View.VISIBLE);

		SurfaceHolder sh = videoview.getHolder();
		sh.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		sh.addCallback(this);

		al.addView(videoview);
		al.addView(Edit_viewone);
		al.addView(venusview);
		al.addView(Edit_view);
		al.addView(Contact_view);
		al.addView(webView_LinearLayout);
		al.addView(imageView);
		//al.addView(adView);


		venusview.setZOrderMediaOverlay(true);


		appActivity.setContentView(al);
		if(mHaveStatusBar)
			appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		venusOrientation = new VenusOrientation(VenusApplication.getInstance().getApplicationContext());

		//playerObserver = PlayerObserver.getInstance(this);


		appManager = AppManager.getInstance(this);
		//add pj
		// viManager = VoiceInputManager.getInstance();

		Util.saveMachineSettings(appActivity.getContentResolver());


		//MsgPush
		//add PJ
		if (msgPushMgr == null)
			msgPushMgr = MsgPushManager.getInstance(appActivity);
		msgPushMgr.init();

		//		if (sysMediaPlayer == null)
		//			sysMediaPlayer = new SysMediaPlayerMgr(appActivity);

		//Map
		//        if (mapPluginMgr == null) {
		//        	mapPluginMgr = MapPluginMgr.getInstance(appActivity);
		//        	al.addView(mapPluginMgr.getMapView());
		//        }
		
		//AuthPlugin
//		if(authPlugin == null)
//			authPlugin = AuthPlugin.getInstance(this);
		
		//FractalADPlugin
		//if(fractalADPlugin == null)
		//{
		//	fractalADPlugin = FractalADPlugin.getInstance(this);
		//	al.addView(fractalADPlugin.GetFractalADView());
		//}
		
		sysState = SYS_STATE_RUN;
	}

	private void clearViewLayout()
	{
		appActivity.setContentView(new AbsoluteLayout(appActivity));
	}

	public ViewGroup GetBaseView()
	{
		if(al != null)
			return al;
		Log.e(TAG,"not init VenusActivity");
		return null;
	}

	private void restoreViewLayout()
	{
		appActivity.setContentView(al);
		if(mHaveStatusBar)
			appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public AudioTrack mAudioTrack = null;
	public int audiotrack_buffersize = 0;

	public final static int SYS_WINDOW_MIN2MAX 		= 0;
	public final static int SYS_WINDOW_MAX2MIN		= 1;
	public final static int SYS_WINDOW_NORMAL		= 2;
	public static int sys_window_state = SYS_WINDOW_NORMAL;
	public static int ResumeCount = 0;

	public int javaGetSysState()
	{
		return sys_window_state;
	}

	public void javaSetSysState(int sysState)
	{
		sys_window_state = sysState;
	}

	public boolean javaGetScreenState()
	{
		return MonitorScreen.getScreenState();
	}

	public void onPause()
	{
		Util.Trace(TAG+"::"+"onPause");

		if(sysState == SYS_STATE_RUN)
		{
			sys_window_state = SYS_WINDOW_MAX2MIN;
			nativesendevent(Util.WDM_SYSPAUSE, 0, 0);
			SystemConnectionManager.getInstance().PostEvent(ConnectionImpl.EVENT_ID_SYSTEM_PAUSE, null);
			WifiObserver.getInstance(this).dealWithWLan(Util.WDM_SYSPAUSE);
			AppManager.getInstance(this).dealWithAppManager(Util.WDM_SYSPAUSE);
			PhoneObserver.getInstance().disablePhoneStateListener();
			//			mapPluginMgr.stop();
		}
	}

	public void onResume() {
		Util.Trace(TAG+"::"+"onResume");

		if(sysState == SYS_STATE_RUN)
		{
			restoreViewLayout();
			if (ResumeCount == 0)
			{
				sys_window_state = SYS_WINDOW_NORMAL;
				ResumeCount++;
			}
			else
			{
				sys_window_state = SYS_WINDOW_MIN2MAX;
			}
			if(mHaveStatusBar)
				appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			else
				appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			//add pj
			venusview.setVisibility(View.VISIBLE);
			nativesendevent(Util.WDM_SYSRESUME, 0, 0);
			SystemConnectionManager.getInstance().PostEvent(ConnectionImpl.EVENT_ID_SYSTEM_RESUME, null);
			WifiObserver.getInstance(this).dealWithWLan(Util.WDM_SYSRESUME);
			AppManager.getInstance(this).dealWithAppManager(Util.WDM_SYSRESUME);
			PhoneObserver.getInstance().enablePhoneStateListener(PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			//            mapPluginMgr.start();
            //sendredraw();
			//MsgPush
            //add PJ
			if (msgPushMgr != null) {
				msgPushMgr.notificationCallBack();
			}
		}
	}

	public void onStop(){
		Util.Trace(TAG+"::"+"onStop");
	}

	public void onDestroy() {
		Util.Trace(TAG+"::"+"onDestroy");

		if(sysState == SYS_STATE_RUN)
		{
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_BATTERY);
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_SCREEN);
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_WIFI);
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_SD);
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_CONTACTS);
			MonitorManager.getInstance(this).UnRegisterMonitor(MonitorCommon.MONITOR_TYPE_HEADSET);
			Util.restoreMachineSettings(appActivity.getContentResolver());
			AppManager.getInstance(this).UnRegisterReceiver();
			SystemConnectionManager.getInstance().DeInit();
			//            mapPluginMgr.destroyMap();
			javaStopRecoder();
			//Util.exitApp();
			System.exit(0);
		}
	}

	private boolean redrawall = true;
	public void sendredraw()
	{
		{
			if( VenusViewHolder!= null)
			{
				redrawall = false;
				nativeupdatemaincanvas(VenusViewHolder.getSurface(), Build.VERSION.SDK_INT);
			}
			else
				redrawall = true;
		}
	}
	
	public boolean javaInstallApp(String apkPath)
	{
		Util.Trace("javaInstallApp:: " + apkPath);
		Message msg = new Message();
		msg.what = MSG_ID_UPDATE_APK;
		Bundle bundle = new Bundle();
		bundle.putString("PATH", apkPath);
		msg.setData(bundle);
		sendVenusEvent(msg);
		return true;
	}

	public boolean javaInstallAppEx(String apkPath)
	{
		return AppManager.getInstance(this).InstallApp(apkPath);
	}

	public boolean javaInstallAppSilent(String apkPath)
	{
		return AppManager.getInstance(this).InstallAppSilent(apkPath);
	}

	public boolean javaRunApp(String packageName)
	{
		return AppManager.getInstance(this).RunApp(packageName);
	}

	public boolean javaUnInstallApp(String packageName)
	{
		return AppManager.getInstance(this).UnInstallApp(packageName);
	}

	public boolean javaOpenFile(String filePaths)
	{
		return SysBrowserObserver.getInstance(this).javaopenFile(filePaths);
	}

	public boolean javaCreateShortcut(String packageName)
	{
		return AppManager.getInstance(this).createShortcut(packageName);
	}

	public boolean javaRemoveShortcut(String packageName)
	{
		return AppManager.getInstance(this).removeShortcut(packageName);
	}

	public boolean javaShortcutExists(String packageName)
	{
		return AppManager.getInstance(this).queryShortcut(packageName);
	}

	public String javaGetStbId()
	{
		return AppManager.getInstance(this).getStbId();
	}

	public String javaGetInstalledAppInfo() {
		return AppManager.getInstance(this).getInstalledAppInfo();
	}

	public String javaGetInstalledAppInfoById(String id) {
		return AppManager.getInstance(this).getInstalledAppInfoById(id);
	}

	public boolean javaDealWithShare(String imagepath, String text, String subject, String title, String packagename, String activityname) {
		return AppManager.getInstance(this).dealWithShare(imagepath, text, subject, title,packagename,activityname);
	}

	public boolean javaShareWebByWX(String url, String title, String imagePath, String desc, boolean isFriendCircle) {
		return AppManager.getInstance(this).shareWebByWX(url, title, imagePath, desc, isFriendCircle);
	}

	public boolean javaOpenFileByApplication(String fileType, String filePath) {
		return AppManager.getInstance(this).openFileByApplication(fileType, filePath);
	}

	public static int postEventFromNative(int type, String arg1, String arg2)
			throws UnsupportedEncodingException {
		Util.Trace("postEventFromNative::"+type + "  " + arg1 + " " + arg2);
		return 0;
	}

	////////////Public interface methods for Java///////////////////////
	public int getScreenWidth()
	{
		return fakeScreenWidth;
	}

	public int getScreenHeight()
	{
		return fakeScreenHeight;
	}

	public SurfaceView getSurfaceView()
	{
		return videoview;
	}

	////////////////////////////////////////////////////////////////////


	//Be match with the cmd type in TMPCPlayer.cpp
	public static final char Enum_CmdType_MP_Create				= 0;
	public static final char Enum_CmdType_MP_SetHandle		    = 1;
	public static final char Enum_CmdType_MP_SetInfo		    = 2;
	public static final char Enum_CmdType_MP_Normal				= 3;
	public static final char Enum_CmdType_MP_FullScreen			= 4;
	public static final char Enum_CmdType_MP_AudioTrack			= 5;
	public static final char Enum_CmdType_MP_Stop			    = 6;
	public static final char Enum_CmdType_MP_Visble				= 7;
	public static final char Enum_CmdType_MP_Hidden				= 8;
	public static final char Enum_CmdType_MP_UrlRoute		    = 9;
	public static final char Enum_CmdType_MP_MoveWindow       	= 10;
	public static final char Enum_CmdType_MP_SetAutoRotateScreen= 11;

	public static int bFullScreen = 0;
	public int JavaCreateElement(int type, String title, int width,
			int height, int x, int y) {
		Util.Trace(type + "  " + title + " " + width + " "
				+ height + " " + x + " " + y);

		switch (type) {
		case Enum_CmdType_MP_Create: {
			Util.Trace("Enum_CmdType_MP_Create: width=" + width +", height=" + height);
			if (!bViewCreated && (MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER))
			{
				videoview.setBackgroundColor(Color.BLACK);
			}

			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
			videoSurfaceWidth = width;
			videoSurfaceHeight = height;

			if (MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface
					|| MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
			{
				state = PLAYER_PLAYING;
				mRedrawHandler.enable(true);
				Message msgupdate = mRedrawHandler.obtainMessage(11);
				mRedrawHandler.sendMessageDelayed(msgupdate, 0);
			}
			else if(MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
			{
				state = PLAYER_PLAYING;
				mRedrawHandler.enable(true);
				Message msgupdate = mRedrawHandler.obtainMessage(11);
				mRedrawHandler.sendMessageDelayed(msgupdate, 0);
			}
			else if (MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY)
			{
			}

			videoview.setVisibility(View.VISIBLE);

			break;
		}
		case Enum_CmdType_MP_Visble: {
			Util.Trace("Enum_CmdType_MP_Visble: width=" + width +", height=" + height);
			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
			videoSurfaceWidth = width;
			videoSurfaceHeight = height;

			if (MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface
					|| MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface) {
				state = PLAYER_PLAYING;
				mRedrawHandler.enable(true);
				Message msgupdate = mRedrawHandler.obtainMessage(11);
				mRedrawHandler.sendMessageDelayed(msgupdate, 0);
			}
			else if(MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
			{
				state = PLAYER_PLAYING;
				mRedrawHandler.enable(true);
				Message msgupdate = mRedrawHandler.obtainMessage(11);
				mRedrawHandler.sendMessageDelayed(msgupdate, 0);
			}

			videoview.setVisibility(View.VISIBLE);

			break;

		}
		case Enum_CmdType_MP_Hidden: {
			// videoview.setVisibility(View.INVISIBLE);

			if(MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY)
			{
				if(bAutoRotateScreen)
					appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else if(MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER)
			{
				cleanSurface(rawWidth, rawHeight);
			}
			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(1, 1, 0, 0));
			videoview.setVisibility(View.INVISIBLE);
			// videoSurfaceWidth = width;
			// videoSurfaceHeight = height;
			break;
		}
		case Enum_CmdType_MP_SetHandle: {
			g_player_handle = width;
			break;
		}
		case Enum_CmdType_MP_SetInfo: {
			videoRawWidth = width;
			videoRawHeight = height;
			Util.Trace("Enum_CmdType_MP_SetInfo: [" + videoRawWidth + ", " + videoRawHeight + "]");
			if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface)
			{
				rawPictureSizeChanged2(videoRawWidth, videoRawHeight);
			}
			break;
		}
		case Enum_CmdType_MP_FullScreen: {
			View v = appActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
			Rect rect = new Rect();
			if(appActivity != null)
				appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			v.getWindowVisibleDisplayFrame(rect);
			if (null == rect) {
				return 0;
			}

			Util.Trace("Enum_CmdType_MP_FullScreen: width=" + width +", height=" + height);

			//videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(rect.width(),	rect.height(), 0, 0));
			if(MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY || MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
			{
				bFullScreen = Enum_CmdType_MP_FullScreen;
				if(bAutoRotateScreen)
					appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			else
			{
				videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth,	fakeScreenHeight + fakeScreenStatusBarHeight, 0, 0));
			}
			videoSurfaceWidth = rect.width();
			videoSurfaceHeight =  rect.height();

			break;
		}
		case Enum_CmdType_MP_Normal: {
			Util.Trace("Enum_CmdType_MP_Normal: width=" + width +", height=" + height);
			if(mHaveStatusBar)
				if(appActivity != null)
					appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				else
					if(appActivity != null)
						appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			if(MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY || MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
			{
				if(bAutoRotateScreen)
					appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
			videoSurfaceWidth = width;
			videoSurfaceHeight = height;
		}
		case Enum_CmdType_MP_AudioTrack: { //Create new audio track
			creatAudioTrack(width, height, x);
			break;
		}
		case Enum_CmdType_MP_Stop:{ //Media player stop.
			//Need to stop the main loop in Java.
			if(mHaveStatusBar)
				if(appActivity != null)
					appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				else
					if(appActivity != null)
						appActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			if(MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY)
			{
				if(bAutoRotateScreen)
					appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			if(mRedrawHandler != null)
			{
				mRedrawHandler.enable(false);
			}

			if (MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER
					|| MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
			{
				cleanSurface(rawWidth, rawHeight);
			}
			break;
		}
		case Enum_CmdType_MP_UrlRoute :
		{
			String route = title;
			if(route == null || route.length() == 0)
				break;
			Bundle bundle = new Bundle();
			bundle.putString("route", route);
			SystemConnectionManager.getInstance().PostEvent(ConnectionImpl.EVENT_ID_CHANGE_ROUTE, bundle);
			break;
		}
		case Enum_CmdType_MP_MoveWindow:
		{

			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
			videoSurfaceWidth = width;
			videoSurfaceHeight = height;

			if (MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface
					|| MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
			{
				state = PLAYER_PLAYING;
				mRedrawHandler.enable(true);
				Message msgupdate = mRedrawHandler.obtainMessage(11);
				mRedrawHandler.sendMessageDelayed(msgupdate, 0);
			}

			videoview.setVisibility(View.VISIBLE);
			break;
		}
		case Enum_CmdType_MP_SetAutoRotateScreen:
		{
			if(width==0)
				bAutoRotateScreen = false;

			if(width==1)
				bAutoRotateScreen = true;
		}
		default:
			break;

		}
		return 0;
	}

	/**
	 * Each plug-in of media player must call this function to notify the Activity.
	 * 0 - WD	(Wang Da)
	 * 1 - TMPC	(Rong Chuang)
	 * 2 - PE	(Hua Wei)
	 * 3 - ...
	 * 4 - WD_NEW(surface)
	 */
	public void javaSetMediaplayerType(int type)
	{
		MEDIA_PLAYER_TYPE = type;
	}

	private int realWidth = 0;
	private int realHeight = 0;
	//public int initWidth = 0;
	//public int initHeight = 0;
	//public int initX = 0;
	//public int initY = 0;
	public void GetRealSurfaceSize(int width, int height)
	{
		realWidth = width;
		realHeight = height;
		venusEventHandler.post(new Runnable(){
			public void run() {
				mHolder.setFixedSize(realWidth, realHeight);
				videoview.invalidate();
				
//				WDPictureSizeChanged(initWidth, initHeight, initX, initY);
			}
		});
	}

	//public void WDPictureSizeChanged(int width, int height, int x, int y)
	//{
	//	float h_scale = 0;
	//	float v_scale = 0;
	//	float scale = 0;	
	//	int fullScreenWidth = 0;
	//	int fullScreenHeight = 0;
	//	int fullScreenX = 0;
	//	int fullScreenY = 0;
	//	if(realWidth > 0 && realHeight > 0)
	//	{	
	//		v_scale = (float)width/realWidth;
	//		h_scale = (float)height/realHeight;
	//		scale = h_scale < v_scale ? h_scale : v_scale;
	//		fullScreenWidth = new Float(realWidth*scale).intValue();
	//		fullScreenHeight = new Float(realHeight*scale).intValue();
    //		if (fullScreenWidth < width) {
    //			fullScreenX = x + (width - fullScreenWidth)/2;  
    //		} else {
    //			fullScreenX = x;
    //		}
    //		if (fullScreenHeight < height) {
    //			fullScreenY = y + (height - fullScreenHeight)/2;
    //		} else {
    //			fullScreenY = y;
    //		}
    //		Util.Trace("WDPictureSizeChanged====fullScreenX:" + fullScreenX + ",fullScreenY:" + fullScreenY);
	//		if (videoview != null)
	//		{
	//			Util.Trace("WDPictureSizeChanged0====width" + width + ",height:" + height + ",fullScreenWidth:" + fullScreenWidth + ",fullScreenHeight:" + fullScreenHeight);
	//			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(fullScreenWidth, fullScreenHeight, fullScreenX, fullScreenY));
	//		}
	//	}
	//	else
	//	{
	//		if (videoview != null)
	//		{
	//			Util.Trace("WDPictureSizeChanged1====width" + width + ",height:" + height + ",realWidth:" + realWidth + ",realHeight:"
	//					+ realHeight + ",fullScreenWidth:" + fullScreenWidth + ",fullScreenHeight:" + fullScreenHeight);
	//			videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, 0, 0));
	//		}
	//	}
	//}
	
	static Rect rect = null;
	public int javaGetSurfaceViewWidth()
	{
		if (mHolder != null)
			rect = mHolder.getSurfaceFrame();

		if (sys_window_state == SYS_WINDOW_MIN2MAX)
		{
			return videoSurfaceWidth;
		}

		if (rect != null)
			return rect.width();
		else
			return 0;
	}

	public int javaGetSurfaceViewHeight()
	{
		if (mHolder != null)
			rect = mHolder.getSurfaceFrame();

		if (sys_window_state == SYS_WINDOW_MIN2MAX)
			return videoSurfaceHeight;

		if (rect != null)
			return rect.height();
		else
			return 0;
	}

	public String javaGetInterfaceName()
	{
		return SystemConnectionManager.getInstance().GetInterfaceName();
	}

	static class VenusOrientation extends OrientationEventListener {
		private static final int ANGLE_DELTA = 30;

		private static final int anglePoint[]={0, 90, 180, 270, 360};//In clockwise.

		//Enum the four different orientation.
		private static final char ANGLE_0 		= 0;
		private static final char ANGLE_90 		= 1;
		private static final char ANGLE_180		= 2;
		private static final char ANGLE_270		= 3;
		private static final char ANGLE_360		= 4;

		private static final int vValidAngle[] = {	0, ANGLE_DELTA,
			anglePoint[ANGLE_360]-ANGLE_DELTA, anglePoint[ANGLE_360],
			anglePoint[ANGLE_180]-ANGLE_DELTA, anglePoint[ANGLE_180]+ANGLE_DELTA};

		private static final int hValidAngle[] = {	anglePoint[ANGLE_90]-ANGLE_DELTA, anglePoint[ANGLE_90]+ANGLE_DELTA,
			anglePoint[ANGLE_270]-ANGLE_DELTA, anglePoint[ANGLE_270]+ANGLE_DELTA};

		//Orientation of device
		private static final int Orientation_HORIZEN	= 0;
		private static final int Orientation_VERTICAL	= 1;

		//Current orientaton
		private int currOrientation =  Orientation_VERTICAL;

		public VenusOrientation(Context context) {
			super(context);
			this.enable();
			Util.Trace(">>>Construct VenusOrientation<<<");
		}

		@Override
		public void onOrientationChanged(int orientation) {
			int targetOrientation = -1;

			if( orientation < 0 )
			{
				//Put the device on the desktop
				//targetOrientation = Orientation_VERTICAL;
			}
			else
			{
				boolean find = false;
				int n = vValidAngle.length;
				for(int i = 0; i < n; i += 2)
				{
					if(vValidAngle[i] <= orientation && orientation <= vValidAngle[i+1])
					{
						find = true;
						targetOrientation = Orientation_VERTICAL;
						break;
					}
				}
				if(find == false)
				{
					n = hValidAngle.length;
					for(int i = 0; i < n; i += 2)
					{
						if(hValidAngle[i] <= orientation && orientation <= hValidAngle[i+1])
						{
							find = true;
							targetOrientation = Orientation_HORIZEN;
							break;
						}
					}
				}
			}

			//Util.Trace(">>>>angle="+orientation+" DEVICE ORIENTATION= "+targetOrientation);

			if(targetOrientation != -1 && currOrientation != targetOrientation)
			{
				currOrientation = targetOrientation;
				String ua = VenusActivity.getInstance().javaGetUserAgent();
				if (ua.equals("t89_android_A505")) {
					int realOrientation = 1 - currOrientation;
					Util.Trace("--->Send DEVICE ORIENTATION: " + realOrientation);
					VenusActivity.getInstance().nativesendevent(Util.WDM_SCREENROTATE, realOrientation, 0);
				} else {
					Util.Trace("--->Send DEVICE ORIENTATION: " + currOrientation);
					VenusActivity.getInstance().nativesendevent(Util.WDM_SCREENROTATE, currOrientation, 0);
				}
			}
		}
	}

	public native void nativeupdatesurface();

	public native void nativesendtouchevent(int touch[]);

	public native void nativesendevent(int keytype, int x, int y);
	
	public native void nativesendeventstring(int eventID, String strContent);

	public native void nativesendkeyevent(int keyValue, int param1, int param2);

	public native void nativetimeslice();

	public native void nativeinit(int screenWidth, int screenHeight, int statusHeight, String interfaceName, String activityName, String appPath, int appPassiveStart);

	public native int  nativetouchevent( int nCount, int nType, int id0, int x0, int y0, int id1, int x1, int y1 );

	public native void nativeexit();

	public native void nativeeditreturn(String editReturn, boolean flag);

	public native void nativeneweditreturn(String editReturn, int count, boolean flag);

	public native void nativeKeyboardSize(int nHeight);

	public native void nativecontactreturn(String contactReturn, boolean flag);

	public native void nativeSetParam(String paramName, String paramValue, int valueLen);

	public native int  nativeExec(String methodName, Object[] paramArray, int paramNum);

	public static native void nativesendsmsevent();

	public native void nativeupdatemaincanvas(Surface surface,int sdkint);

	public native void nativebrowserreturn(String strUrl, int uType);

	//public native void nativeadmobviewreturn(int uType);

	public static native void nativefileexplorereturn(String filePathName, long fileSize, int allFileNum, int fileExplorePercent);

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Util.Trace("===surfaceChanged===Start===");
		// TODO Auto-generated method stub
		Util.Trace("*** changed rawWidth: " + rawWidth + " rawHeight: " + rawHeight);

		mHolder = holder;
		mSurface = holder.getSurface();

		if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface)
		{
			update2();
			nativeupdatesurface();
		}
		else if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
		{
			setPictureFixToHolder(rawWidth, rawHeight, holder);
			holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			holder.setFormat(PixelFormat.RGB_565);
		}
		else if (MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY)
		{
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mHolder.setFormat(PixelFormat.TRANSLUCENT);

			Rect rect = holder.getSurfaceFrame();
			videoSurfaceWidth = rect.right - rect.left;
			videoSurfaceHeight = rect.bottom - rect.top;

			nativeupdatesurface();
		}
		else if(MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
		{
			holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			mSurface = holder.getSurface();
			nativeupdatesurface();
		}		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Util.Trace("===surfaceCreated===Start===");
		Util.Trace("*** created rawWidth: " + rawWidth + " rawHeight: " + rawHeight);

		Util.Trace("===surfaceCreated===MEDIA_PLAYER_TYPE =" + MEDIA_PLAYER_TYPE);
		mHolder = holder;
		if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface)
		{
			holder.setFixedSize(rawWidth, rawHeight);
			holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			mSurface = holder.getSurface();
			nativeupdatesurface();
		}
		else if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
		{
			holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			holder.setFormat(PixelFormat.RGB_565);
			mSurface = holder.getSurface();
			setPictureFixToHolder(rawWidth, rawHeight, holder);
		}
		else if (MEDIA_PLAYER_TYPE == PE_MEDIA_PALYER_OVERLEY)
		{
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mHolder.setFormat(PixelFormat.TRANSLUCENT);
			mSurface = mHolder.getSurface();
			setPictureFixToHolder(rawWidth, rawHeight, mHolder);
			nativeupdatesurface();
		}

		else if(MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
		{
			holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
			mSurface = holder.getSurface();
			nativeupdatesurface();
		}		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Util.Trace("====>surfaceDestroyed");
		mSurface = null;

		if (MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface || MEDIA_PLAYER_TYPE == WD_MEDIA_PLAYER_PublicSurface)
		{
			nativeupdatesurface();
		}
	}

	public void cleanSurface(int width, int height){
		Rect rectDest = mHolder.getSurfaceFrame();
		Matrix matrix = new Matrix();
		int rectw = rectDest.right - rectDest.left;
		int recth = rectDest.bottom - rectDest.top;
		float scale = 1;
		float h_scale = (float)rectw/width;
		float v_scale = (float)recth/height;
		scale = h_scale < v_scale ? h_scale : v_scale;
		pictureW = new Float(width*scale).intValue();
		pictureH = new Float(height*scale).intValue();
		pictureX = 0;
		pictureY = 0;

		matrix.postScale(scale, scale);
		videoSurfaceWidth = width;
		videoSurfaceHeight = height;
		if(videoSurfaceWidth != 0 && videoSurfaceHeight != 0)
		{
			int i = 0;
			int length = videoSurfaceWidth*videoSurfaceHeight;

			int[] mRrawPicture = new int[length];

			Bitmap mBitmap = null;
			Canvas mCanvas = null;
			if(mRrawPicture != null)
			{
				for(; i<length; i++)
					mRrawPicture[i] = 0;
				if(mBitmap == null)
				{
					mBitmap = Bitmap.createBitmap(videoSurfaceWidth, videoSurfaceHeight,
							Bitmap.Config.RGB_565);
				}
				mBitmap.setPixels(mRrawPicture, 0, videoSurfaceWidth, 0, 0,
						videoSurfaceWidth, videoSurfaceHeight);
				synchronized (surfaceLock)
				{
					if (mSurface != null && rectDest != null)
					{
						try {
							mCanvas = mSurface.lockCanvas(rectDest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (mCanvas != null)
						{
							mCanvas.concat(matrix);
							mCanvas.drawBitmap(mBitmap, pictureX,
									pictureY, paint);
							mSurface.unlockCanvasAndPost(mCanvas);
						}
					}
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Util.Trace("::"+"onKeyDown");
		if(sysState == SYS_STATE_RUN)
		{
			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				mKeyPrepared = true;
				if(Edit_viewone.getVisibility() == View.VISIBLE)
				{
					nativeneweditreturn(Edit_textone.getText().toString(), 0, false);
					Edit_textone.clearFocus();
					Edit_inputone.hideSoftInputFromWindow(Edit_textone.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					Edit_viewone.setVisibility(View.GONE);
					this.maxCounts = 15;
					this.bmultiLines = false;
				}
				//TODO
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_MENU)
			{
				mKeyPrepared = true;
				//TODO
				return true;
			}
			else if(   keyCode == KeyEvent.KEYCODE_DPAD_UP
					|| keyCode == KeyEvent.KEYCODE_DPAD_DOWN
					|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				mKeyPrepared = true;
				return true;
			}
			else if(   keyCode == KeyEvent.KEYCODE_DPAD_CENTER
					|| keyCode == KeyEvent.KEYCODE_ENTER)
			{
				mKeyPrepared = true;
				nativesendkeyevent(keyCode, 1, 0);
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			{
				mKeyPrepared = true;
				if(mDispalyMode == DISPLAY_MODE_LANDSCAPE)
					return true;
				else
					return false;
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			{
				mKeyPrepared = true;
				if(mDispalyMode == DISPLAY_MODE_LANDSCAPE)
					return true;
				else
					return false;
			}
			else if(   keyCode == KeyEvent.KEYCODE_0
					|| keyCode == KeyEvent.KEYCODE_1
					|| keyCode == KeyEvent.KEYCODE_2
					|| keyCode == KeyEvent.KEYCODE_3
					|| keyCode == KeyEvent.KEYCODE_4
					|| keyCode == KeyEvent.KEYCODE_5
					|| keyCode == KeyEvent.KEYCODE_6
					|| keyCode == KeyEvent.KEYCODE_7
					|| keyCode == KeyEvent.KEYCODE_8
					|| keyCode == KeyEvent.KEYCODE_9)
			{
				mKeyPrepared = true;
				return true;
			}
			else if(   keyCode == 166	/*android.view.KeyEvent.KEYCODE_CHANNEL_UP*/
					|| keyCode == 167)	/*android.view.KeyEvent.KEYCODE_CHANNEL_DOWN*/
			{
				mKeyPrepared = true;
				return true;
			}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Util.Trace("::"+"onKeyUp");

		if(sysState == SYS_STATE_RUN && mKeyPrepared == true)
		{
			mKeyPrepared = false;
			if( Edit_view.getVisibility() == View.VISIBLE || Contact_view.getVisibility() == View.VISIBLE ||
					(webViewRoot!=null && webViewRoot.getVisibility() == View.VISIBLE))
			{
				//Filter the KEY_BACK when we are in EDIT VIEW form.
				return true;
			}

			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				nativesendkeyevent(KeyEvent.KEYCODE_BACK, 0, 0);
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_MENU)
			{
				nativesendkeyevent(KeyEvent.KEYCODE_MENU, 0, 0);
				return true;
			}
			else if(   keyCode == KeyEvent.KEYCODE_DPAD_UP
					|| keyCode == KeyEvent.KEYCODE_DPAD_DOWN
					|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
					|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER
					|| keyCode == KeyEvent.KEYCODE_ENTER)
			{
				nativesendkeyevent(keyCode, 0, 0);
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			{
				if(mDispalyMode == DISPLAY_MODE_LANDSCAPE)
				{
					nativesendkeyevent(KeyEvent.KEYCODE_VOLUME_UP, 0, 0);
					return true;
				}
				else
				{
					nativesendkeyevent(KeyEvent.KEYCODE_VOLUME_UP, 0, 0);
				}
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			{
				if(mDispalyMode == DISPLAY_MODE_LANDSCAPE)
				{
					nativesendkeyevent(KeyEvent.KEYCODE_VOLUME_DOWN, 0, 0);
					return true;
				}
				else
				{
					nativesendkeyevent(KeyEvent.KEYCODE_VOLUME_DOWN, 0, 0);
				}
			}
			else if(   keyCode == KeyEvent.KEYCODE_0
					|| keyCode == KeyEvent.KEYCODE_1
					|| keyCode == KeyEvent.KEYCODE_2
					|| keyCode == KeyEvent.KEYCODE_3
					|| keyCode == KeyEvent.KEYCODE_4
					|| keyCode == KeyEvent.KEYCODE_5
					|| keyCode == KeyEvent.KEYCODE_6
					|| keyCode == KeyEvent.KEYCODE_7
					|| keyCode == KeyEvent.KEYCODE_8
					|| keyCode == KeyEvent.KEYCODE_9)
			{
				nativesendkeyevent(keyCode, 0, 0);
				return true;
			}
			else if(   keyCode == 166	/*android.view.KeyEvent.KEYCODE_CHANNEL_UP*/
					|| keyCode == 167)	/*android.view.KeyEvent.KEYCODE_CHANNEL_DOWN*/
			{
				nativesendkeyevent(keyCode, 0, 0);
				return true;
			}
		}
		return false;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation)
		{
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = appActivity.getWindowManager();
			wm.getDefaultDisplay().getMetrics(dm);
			if (videoview != null && bFullScreen  == Enum_CmdType_MP_FullScreen)
			{
				videoview.setLayoutParams(new AbsoluteLayout.LayoutParams(dm.widthPixels, dm.heightPixels, 0, 0));
				bFullScreen = 0;
			}
			venusview.setLayoutParams(new AbsoluteLayout.LayoutParams(dm.widthPixels, dm.heightPixels, 0, 0));
		}
		else if(Configuration.ORIENTATION_PORTRAIT == newConfig.orientation)
			venusview.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth>fakeScreenHeight?fakeScreenWidth:fakeScreenHeight, fakeScreenWidth>fakeScreenHeight?fakeScreenWidth:fakeScreenHeight, 0, 0));
	}

	private void releaseEdit() {
		venusview.setVisibility(View.VISIBLE);
		Edit_view.setVisibility(View.GONE);
		Edit_text.setText("");
		Edit_title.setText("");

		InputMethodManager imm = (InputMethodManager) appActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(Edit_text.getWindowToken(), 0);
	}

	private void releaseContact() {
		Contact_positions.clear();
		ContactClose(Contact_cursor);
		venusview.setVisibility(View.VISIBLE);
		Contact_view.setVisibility(View.GONE);
	}

	public void initEdit(int type, int maxSize, String titleText, String defaultText) {
		// edit.setInputType(type);
		Edit_title.setText(titleText);
		Edit_text.setText(defaultText);
		Edit_text.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxSize) });

		Edit_view.setVisibility(View.VISIBLE);
		venusview.setVisibility(View.GONE);
	}

	public void setNewEditSelection(int npos)
	{
		if(npos<0)
			npos=0;

		Message msg = new Message();
		msg.what = MSG_ID_EDIT_SELECT;
		msg.obj = npos;
		venusEventHandler.sendMessage(msg);
	}

	public void setNewEditText(String strText)
	{
		if(strText.length()>maxCounts)
			strText=strText.substring(0,maxCounts);
		bRunSetText = true;
		Message msg = new Message();
		msg.what = MSG_ID_EDIT_TEXTCHANGE;
		msg.obj = strText;
		venusEventHandler.sendMessage(msg);

		//		if(strText.length()>maxCounts)
		//			strText=strText.substring(0,maxCounts);
		//		
		//		Edit_textone.setText(strText);	
	}

	public void initNewEdit(int type, int maxSize, int multiLines, String titleText, String defaultText, int top, int width, int height, int uflags) {
		Log.d(TAG,"top="+top + ", width=" + width + ",height=" + height + ",maxSize=" + maxSize);
		Message msg = new Message();
		bmultiLines = (multiLines == 1)?true:false;;
		maxCounts = maxSize;
		bRunSetText=true;
		m_top = top;
		m_defaultText = "";
		if(defaultText.length()>maxSize)
			m_defaultText = defaultText.substring(0,maxSize);
		else
			m_defaultText = defaultText;
		m_width = width;
		m_height = height;
		if(type == 0)
		{
			this.maxCounts = 15;
			this.bmultiLines = false;
			nativeneweditreturn(Edit_textone.getText().toString(), 0, false);
			msg.what = MSG_ID_EDIT;
			msg.obj = false;
			venusEventHandler.sendMessage(msg);
		}
		else
		{
			msg.what = MSG_ID_EDIT;
			msg.obj = true;
			venusEventHandler.sendMessage(msg);
		}
		if (uflags == 0x0001) {
			Edit_textone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		} else {
			Edit_textone.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}

	private boolean initContact(String titleText) {
		Contact_cursor = ContactRefresh();

		Contact_Count = 0;
		Contact_NameList.clear();
		Contact_NumberList.clear();
		//Contact_SelectedList.clear();

		int i = 0;
		int c = Contact_cursor.getCount();
		if(c > 0)
		{
			Contact_cursor.moveToFirst();
			for(; i<c; i++)
			{
				String name = Contact_cursor.getString(Contact_cursor.getColumnIndex("display_name"));
				String id = Contact_cursor.getString(Contact_cursor.getColumnIndex("_id"));
				String hasPhone = Contact_cursor.getString( Contact_cursor.getColumnIndex("has_phone_number") );
				if( hasPhone.equals("1") )
				{
					Cursor phones = appActivity.getContentResolver().query(
							phoneUri,
							null,
							"contact_id" +" = ?",
							new String[] { id }, 
							null);
					while(phones != null && phones.moveToNext())
					{
						String phone = phones.getString(phones.getColumnIndex("data1"));
						Contact_NameList.add(name);
						Contact_NumberList.add(phone);
						Contact_Count++;
					}
					phones.close();
				}

				Contact_cursor.moveToNext();	
			}
		}

		if(Contact_Count > 0)
		{
			Contact_title.setText(titleText);
			Contact_view.setVisibility(View.VISIBLE);
			venusview.setVisibility(View.GONE);
			Contact_adapter.notifyDataSetChanged();
			Contact_list.setSelection(0);
			ContactClose(Contact_cursor);
			return true;
		}
		else
		{
			ContactClose(Contact_cursor);
			return false;
		}
	}

	private String getContactReturn() {
		String contactReturn = "";
		int i = 0;
		int c = Contact_SelectedList.size();
		for(; i<c; i++) {
			String name = Contact_NameList.get( Contact_SelectedList.get(i).intValue() );
			String number = Contact_NumberList.get( Contact_SelectedList.get(i).intValue() );
			number = number.replace("-", ""); // Remove the character "-", such as "XXX-XXXX-XXXX"
			contactReturn += name + "," + number + ";";
		}

		return contactReturn;
	}

	public void getContactsAsync()
	{
		Util.getInstance().getAsyncTask().execute("GetContacts");
	}

	public String getContacts()
	{
		int sdk = Util.GetSDK();
		if(sdk == Util.SDK_ANDROID_15 || sdk == Util.SDK_ANDROID_16 || sdk == Util.SDK_OMS_15 || sdk == Util.SDK_OMS_16)
		{
			return getContactsLowSDK();
		}
		else
		{
			return getContactsHighSDK();
		}
	}

	private final Uri CONTACT_PHONE_URI = Uri.parse("content://contacts/people"); // People.CONTENT_URI;
	private final Uri CONTACT_SIM_URI = Uri.parse("content://icc/adn"); // sim
	private String getContactsLowSDK()
	{
		Uri uri = null;
		String contactsList = "";
		ContentResolver resolver = appActivity.getContentResolver();
		String columns[] = new String[] { People._ID, People.NAME, People.NUMBER };
		Cursor cur = null;

		if(Contact_Map == null) Contact_Map = new HashMap<String, String>();
		Contact_Map.clear();

		for(int i=0; i<2; i++)
		{
			if(i == 0) 
				uri = CONTACT_PHONE_URI;
			else if(i == 1)
				uri = CONTACT_SIM_URI;
			else
				break;

			try {
				cur = resolver.query(uri, columns, null, null, People.NAME);
				if (cur.moveToFirst()) {
					do {
						String name = cur.getString(cur.getColumnIndex(People.NAME));
						String temp = cur.getString(cur.getColumnIndex(People.NUMBER));
						//phoneNumberfilter(name, temp);
						Contact_Map.put(name, temp);
					} while (cur.moveToNext());
				}
			} catch (Exception e) {
			} finally {
				if (cur != null) {
					cur.close();
					cur = null;
				}
			}
		}

		//Build the contacts list
		int elementN = Contact_Map.size();
		int j = 0;
		String[] nameArray = new String[elementN];
		Set<String> set = Contact_Map.keySet();
		for(String key : set)
		{
			nameArray[j++] = key;
		}
		for(j=0; j<elementN; j++)
		{
			contactsList = contactsList + nameArray[j] + "\n" + Contact_Map.get(nameArray[j]) + "\n";
		}
		return contactsList;
	}

	private String getContactsHighSDK()
	{
		Contact_cursor = ContactRefresh();
		String contactsList = "";
		Contact_Count = 0;
		if(Contact_Map == null) Contact_Map = new HashMap<String, String>();
		Contact_Map.clear();
		Cursor phones = appActivity.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				null,
				null,
				null);
		int c = phones.getCount();
		if(c > 0)
		{
			Contact_cursor.moveToFirst();
			for(int i = 0; i<c; i++)
			{
				phones.moveToNext();
				String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Contact_Map.put(name+"###" + i, phone);
				Contact_Count++;
			}
		}

		//Sort the contacts by name
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
		int elementN = Contact_Map.size();
		int j = 0;
		String[] nameArray = new String[elementN];
		Set<String> set = Contact_Map.keySet();
		for(String key : set)
		{
			nameArray[j++] = key;
		}
		Arrays.sort(nameArray, cmp);

		//Build the contacts list
		for(j=0; j<elementN; j++)
		{
			contactsList = contactsList + nameArray[j].substring(0,nameArray[j].lastIndexOf("###")) + "\n" + Contact_Map.get(nameArray[j]) + "\n";
		}
		ContactClose(Contact_cursor);
		return contactsList;
	}

	private void phoneNumberfilter(String name, String temp) {
		if (temp != null) {
			String phoneNo = temp;
			if (temp.contains(" ") || temp.contains("-") || temp.contains("_")) {
				phoneNo = temp.replaceAll("-|\\s|_", "");
			}
			if (checkMobilePhone(phoneNo)) {
				Pattern pattern = Pattern.compile("1(3[4-9]|5[012789]|8[278])\\d{8}$");
				Matcher matcher = pattern.matcher(phoneNo);
				StringBuffer phoneNoMached = new StringBuffer();
				while (matcher.find()) {
					phoneNoMached.append(matcher.group());
				}
				String phoneNoMachedString = phoneNoMached.toString();
				//				if (!phoneMap.containsKey(phoneNoMachedString)) {
				//					if (name != null && !"".equals(name)) {
				//						personsArrayList.add(phoneNoMachedString.concat("[")
				//								.concat(name).concat("]"));
				//
				//					} else {
				//						personsArrayList.add(phoneNoMachedString);
				//					}
				//					phoneMap.put(phoneNoMachedString, 1);
				//				}
			}
		}
	}

	private boolean checkMobilePhone(String phone) {
		/*
		 * Matcher China Mobile "^1(3[4-9]|5[012789]|8[78])\d{8}$"
		 * 
		 * Matcher China Telecom "^18[09]\d{8}$"
		 * 
		 * Matcher China Unicom "^1(3[0-2]|5[56]|8[56])\d{8}$"
		 * 
		 * Matcher CDMA "^1[35]3\d{8}$"
		 */
		Pattern pattern = Pattern
				.compile("^(\\+86)?1(3[4-9]|5[012789]|8[278])\\d{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	private Uri contactsUri = null;
	private Uri phoneUri = null;
	private Cursor ContactRefresh()
	{
		if(contactsUri == null || phoneUri == null)
		{
			try {
				Class<?> clazz = Class.forName("android.provider.ContactsContract$Contacts");
				Field f1 = clazz.getField("CONTENT_URI");
				contactsUri = (Uri) (f1.get(null));

				Class<?> clazz2 = Class.forName("android.provider.ContactsContract$CommonDataKinds$Phone");
				Field f2 = clazz2.getField("CONTENT_URI");
				phoneUri = (Uri) (f2.get(null));
			} catch (Exception e) {
				Util.Trace(e.toString());
			}
		}
		Cursor c = appActivity.getContentResolver().query(contactsUri/*Phones.CONTENT_URI*/, null, null, null, null);
		return c;
	}

	private void ContactClose(Cursor c)
	{
		c.deactivate();
		c.close();
	}

	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(Edit_yes)) {
				nativeeditreturn(Edit_text.getText().toString(), true);
				releaseEdit();
			} else if (v.equals(Edit_no)) {
				nativeeditreturn(null, false);
				releaseEdit();
			} else if (v.equals(Contact_yes)) {
				nativecontactreturn(getContactReturn(), true);
				releaseContact();
			} else if (v.equals(Contact_no)) {
				nativecontactreturn(null, false);
				releaseContact();
			}
		}
	};

	//Adapter for contacts
	public class ContactAdapter extends BaseAdapter {
		private Context context;

		public ContactAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			//return Contact_cursor.getCount();
			return Contact_Count;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			//Contact_cursor.moveToPosition(position);

			if (convertView == null) {
				convertView = new LinearLayout(context);
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

				RelativeLayout relative = new RelativeLayout(context);
				relative.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

				TextView name = new TextView(context);
				RelativeLayout.LayoutParams pName = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pName.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				pName.leftMargin = 4;
				name.setLayoutParams(pName);
				name.setTextSize(20);
				name.setTextColor(Color.parseColor("#FFFFFF"));

				TextView number = new TextView(context);
				RelativeLayout.LayoutParams pNumber = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pNumber.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				pNumber.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
						RelativeLayout.TRUE);
				pNumber.leftMargin = 4;
				pNumber.topMargin = 1;
				number.setLayoutParams(pNumber);
				number.setTextSize(14);
				number.setTextColor(Color.parseColor("#FFFFFF"));

				CheckBox check = new CheckBox(context);
				RelativeLayout.LayoutParams pCheck = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pCheck.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
						RelativeLayout.TRUE);
				pCheck.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				check.setLayoutParams(pCheck);

				relative.addView(name);
				relative.addView(number);
				relative.addView(check);
				((LinearLayout) convertView).addView(relative);

				holder = new ViewHolder();
				holder.name = name;
				holder.number = number;
				holder.check = check;

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String name = Contact_NameList.get(position);
			String number = Contact_NumberList.get(position);
			holder.name.setText(name);
			holder.number.setText(number);
			holder.check.setTag(position);

			//if (Contact_positions.contains(new Integer(position))) {
			if(Contact_SelectedList.contains(new Integer(position))) {
				holder.check.setChecked(true);
			} else {
				holder.check.setChecked(false);
			}

			holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton Contact_buttonView,	boolean isChecked) {
					if (isChecked) {
						//Contact_positions
						//		.add((Integer) Contact_buttonView
						//				.getTag());
						if(!Contact_SelectedList.contains((Integer) Contact_buttonView.getTag()))
							Contact_SelectedList.add((Integer) Contact_buttonView.getTag());
					} else {
						//Contact_positions
						//		.remove((Integer) Contact_buttonView
						//				.getTag());
						Contact_SelectedList.remove((Integer) Contact_buttonView.getTag());
					}
				}
			});

			return convertView;
		}

		public class ViewHolder {
			TextView name;
			TextView number;
			CheckBox check;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////
	// get machine info.
	public static int EMachineInfo_Serial 			= 0; // ID,
	public static int EMachineInfo_Signal 			= 1; // Sinal, 0 ~ 100
	public static int EMachineInfo_Battery 			= 2; // Battery energy
	public static int EMachineInfo_PhoneNumber 		= 3; // Phone Number,
	public static int EMachineInfo_IMEI 			= 4; // IMEI,
	public static int EMachineInfo_IMSI 			= 5; // IMSI,
	public static int EMachineInfo_OperatorID		= 6; // OperatorID
	public static int EMachineInfo_NetWorkType		= 7; // Network type
	public static int EMachineInfo_SignalStrength	= 8; // Sinal ,dBm, 
	public static int EMachineInfo_GSMCellId		= 9; // Cell Id
	public static int EMachineInfo_MacAddress		= 10;// WIFI Mac Address
	public static int EMachineInfo_Count 			= 11;// Boundary check 

	//Field create
	public CBatteryInfo batteryInfo = null;

	public Object javaGetMachineInfo(int index) {
		TelephonyManager telephonyManager = (TelephonyManager) appActivity.getSystemService(Context.TELEPHONY_SERVICE);

		if(EMachineInfo_IMEI == index)
		{
			String imei = telephonyManager.getDeviceId();
			if(imei == null)
			{
				imei = android.provider.Settings.System.getString(appActivity.getContentResolver(),android.provider.Settings.System.ANDROID_ID);
			}
			imei = (imei == null) ? "":imei;

			return imei;
		}
		else if(EMachineInfo_IMSI == index)
		{
			int simState = telephonyManager.getSimState();
			if(simState == TelephonyManager.SIM_STATE_READY)
			{
				String imsi = telephonyManager.getSubscriberId();
				imsi = (imsi == null)?"":imsi;

				return imsi;
			}
		}
		else if(EMachineInfo_Battery == index)
		{
			return batteryInfo;
		}
		else if (EMachineInfo_OperatorID == index)
		{
			int simState = telephonyManager.getSimState();
			if(simState == TelephonyManager.SIM_STATE_READY)
			{
				String operator = telephonyManager.getNetworkOperator();
				operator = operator == null?"":operator;

				return operator;
			}
		}
		else if (EMachineInfo_NetWorkType == index)
		{
			Integer networkType = telephonyManager.getNetworkType();
			return networkType.toString();
		}
		else if (EMachineInfo_Signal == index)
		{
			return "100";
		}
		else if (EMachineInfo_SignalStrength == index)
		{
			Integer signal = PhoneObserver.getInstance().getSignalStrength();
			return signal.toString();
		}
		else if (EMachineInfo_GSMCellId == index)
		{
			Integer cellId = PhoneObserver.getInstance().getCellId();
			return cellId.toString();
		}
		else if (EMachineInfo_MacAddress == index)
		{
			return PhoneObserver.getInstance().getMacAddress();
		}

		return "";
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////////////

	public int javaGetRenderWidth()
	{
		int width = 0;
		if(mDispalyMode == DISPLAY_MODE_PORTAIT)
		{
			width = fakeScreenWidth;
		}
		else
		{
			width = fakeScreenHeight + fakeScreenStatusBarHeight;
		}
		return width;
	}

	public int javaGetRenderHeight()
	{
		int height = 0;
		Util.Trace("javaGetRenderHeight use fake");
		if(mDispalyMode == DISPLAY_MODE_PORTAIT)
		{
			if(mHaveStatusBar)
			{
				height = fakeScreenHeight;
			}
			else
			{
				height = fakeScreenHeight + fakeScreenStatusBarHeight;
			}
		}
		else
		{
			if(mHaveStatusBar)
			{
				height = fakeScreenWidth - fakeScreenStatusBarHeight;
			}
			else
			{
				height = fakeScreenWidth;
			}
		}
		return height;
	}


	public int javaGetStatusHeight() {
		View v = appActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		Rect rect = new Rect();

		v.getWindowVisibleDisplayFrame(rect);
		if (null != rect) {
			return rect.top;
		}

		return 0;
	}

	public int GetstatusBarHeight()
	{
		int h = 0;
		try
		{
			Class c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			java.lang.reflect.Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			h = appActivity.getResources().getDimensionPixelSize(x);
		}
		catch(Exception e)
		{

		}
		return h;
	}

	public void javaScreenRotate(int mode)
	{
		if(mode == 0)
		{
			mDispalyMode = DISPLAY_MODE_LANDSCAPE;
			if(appActivity != null)
				appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{
			mDispalyMode = DISPLAY_MODE_PORTAIT;
			if(appActivity != null)
				appActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// send sms.

	public boolean javaSendSMS(String messageAddress, String messageContent,
			boolean bSilentMode) {
		if (bSilentMode) {
			SmsManager smsManager = SmsManager.getDefault();
			if (messageAddress.trim().length() != 0
					&& messageContent.trim().length() != 0) {
				try {
					Util.Trace("SMS: ADDRESS="+messageAddress+ "   CONTENT="+messageContent);
					smsManager.sendMultipartTextMessage(messageAddress, null, smsManager.divideMessage(messageContent), null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			Util.Trace("Call System SMS");
			Uri smsUri = Uri.parse("smsto:" + messageAddress);
			Intent smsIntent = new Intent(Intent.ACTION_VIEW, smsUri);
			smsIntent.putExtra("sms_body", messageContent);
			appActivity.startActivity(smsIntent);
		}
		return true;
	}

	// Set volume of system
	private AudioManager audioMgr = null;
	//pj382 add fgx
	public boolean javaSetVolume(int nVolume)
	{
		Util.Trace("javaSetVolume:" + nVolume);
		audioMgr = (AudioManager) appActivity.getSystemService(Context.AUDIO_SERVICE);
		if (nVolume < 0)
			audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		else 
		{
			int maxStreamVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			Util.Trace("javaSetVolume0:" + maxStreamVolume);
			if ((maxStreamVolume*nVolume)/4 > maxStreamVolume)
				audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxStreamVolume, 0);
			else
				audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, (maxStreamVolume*nVolume)/4, 0);

			Util.Trace("javaSetVolume1:" + (maxStreamVolume*nVolume)/4);
		}
		return true;
	}
	//pj382 add fgx
	public int javaGetVolume()
	{
		audioMgr = (AudioManager) appActivity.getSystemService(Context.AUDIO_SERVICE);
		int maxStreamVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curStreamVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		Util.Trace("javaGetVolume:" + (curStreamVolume*5)/maxStreamVolume);
		return curStreamVolume/(maxStreamVolume/4);
	}
	// ////////////////////////////////////////////////////////////////////////////////
	// Set Bluetooth
	public boolean javaGetBluetoothState()
	{
		boolean on = false;
		if(Util.GetSDK() != Util.SDK_ANDROID_15 && Util.GetSDK() != Util.SDK_ANDROID_16 && Util.GetSDK() != Util.SDK_OMS_15 && Util.GetSDK() != Util.SDK_OMS_16)
		{
			try {
				Class<?> cls;
				cls = Class.forName("android.bluetooth.BluetoothAdapter");
				Method m_getDefaultAdapter = cls.getDeclaredMethod("getDefaultAdapter");
				Object blueAdapter = m_getDefaultAdapter.invoke(null);

				if(blueAdapter != null)
				{
					Method m_isEnabled = cls.getDeclaredMethod("isEnabled");
					on = ((Boolean)m_isEnabled.invoke(blueAdapter)).booleanValue();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return on;
	}

	public boolean javaSetBluetoothState(boolean bstate)
	{
		boolean result = false;

		if(Util.GetSDK() != Util.SDK_ANDROID_15 && Util.GetSDK() != Util.SDK_ANDROID_16 && Util.GetSDK() != Util.SDK_OMS_15 && Util.GetSDK() != Util.SDK_OMS_16)
		{
			try {
				Class<?> cls;
				cls = Class.forName("android.bluetooth.BluetoothAdapter");

				Method m_getDefaultAdapter = cls.getDeclaredMethod("getDefaultAdapter");
				Object blueAdapter = m_getDefaultAdapter.invoke(null);

				if(blueAdapter != null)
				{
					if(javaGetBluetoothState())
					{
						if(bstate)
							result = true;
						else
						{
							Method m_disable = cls.getDeclaredMethod("disable");
							result = ((Boolean)m_disable.invoke(blueAdapter)).booleanValue();
						}

					}
					else
					{
						if(bstate)
						{
							Method m_enable = cls.getDeclaredMethod("enable");
							result = ((Boolean)m_enable.invoke(blueAdapter)).booleanValue();
						}
						else
						{
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String javagetLanguage() {
		Locale[] mLocales = Locale.getAvailableLocales();
		Locale defaultLocale = Locale.getDefault();
		String language=new String();
		for (Locale l : mLocales) {
			String sl = l.toString();
			if (sl.equals("zh_CN"))
				language += "SIMPLIFIED_CHINESE;";
			else if (sl.equals("zh_TW"))
				language += "TRADITIONAL_CHINESE;";
			else if (sl.equals("en"))
				language += "ENGLISH;";
			else if (sl.equals("fr"))
				language += "FRENCH;";
			else if (sl.equals("de"))
				language += "GERMAN;";
			else if (sl.equals("it"))
				language += "ITALIAN;";
			else if (sl.equals("ja"))
				language += "JAPANESE;";
			else if (sl.equals("ko"))
				language += "KOREAN;";
		}

		String sl = defaultLocale.toString();
		if (sl.equals("zh_CN"))
			language += "SIMPLIFIED_CHINESE";
		else if (sl.equals("zh_TW"))
			language += "TRADITIONAL_CHINESE";
		else if (sl.equals("en"))
			language += "ENGLISH";
		else if (sl.equals("fr"))
			language += "FRENCH";
		else if (sl.equals("de"))
			language += "GERMAN";
		else if (sl.equals("it"))
			language += "ITALIAN";
		else if (sl.equals("ja"))
			language += "JAPANESE";
		else if (sl.equals("ko"))
			language += "KOREAN";

		return language;
	}

	public boolean javasetLanguage(String language) {
		if (language == null)
			return false;

		Locale l = null;
		boolean isSuccessful = false;
		Util.Trace("javasetLanguage language=" +language);
		if (language.equals("SIMPLIFIED_CHINESE")) {
			l = Locale.SIMPLIFIED_CHINESE;
			isSuccessful = true;
		} else if (language.equals("TRADITIONAL_CHINESE")) {
			l = Locale.TRADITIONAL_CHINESE;
			isSuccessful = true;
		} else if (language.equals("ENGLISH")) {
			l = Locale.ENGLISH;
			Util.Trace("l = Locale.ENGLISH");
			isSuccessful = true;
		} else if (language.equals("FRENCH")) {
			l = Locale.FRENCH;
			isSuccessful = true;
		} else if (language.equals("GERMAN")) {
			l = Locale.GERMAN;
			isSuccessful = true;
		} else if (language.equals("ITALIAN")) {
			l = Locale.ITALIAN;
			isSuccessful = true;
		} else if (language.equals("JAPANESE")) {
			l = Locale.JAPANESE;
			isSuccessful = true;
		} else if (language.equals("KOREAN")) {
			l = Locale.KOREAN;
			isSuccessful = true;
		} else {
			isSuccessful = false;
		}
		try
		{
			Class<?> clzIActMag = Class.forName("android.app.IActivityManager");
			Class<?> clzActMagNative = Class.forName("android.app.ActivityManagerNative");
			Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
			Object objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
			Method mtdIActMagNative$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");

			Configuration config = (Configuration) mtdIActMagNative$getConfiguration.invoke(objIActMag);

			config.locale = l;

			@SuppressWarnings("rawtypes")
			Class[] clzParams = {Configuration.class};
			Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
			mtdIActMag$updateConfiguration.invoke(objIActMag, config);
		}
		catch (Exception e) {
			e.printStackTrace();

		}

		return isSuccessful;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Set Text input settings

	public String javagetInputMethod() {
		String strAllInputMethodList = new String();

		InputMethodManager inputMethodManager = (InputMethodManager)appActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> inputTypewritings = inputMethodManager.getInputMethodList();

		for(InputMethodInfo inputTypewriting : inputTypewritings)
		{
			strAllInputMethodList += inputTypewriting.getId() + ";";
		}

		String defaultID = Settings.Secure.getString(appActivity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		strAllInputMethodList +=defaultID;
		Util.Trace("id: " + strAllInputMethodList);
		return strAllInputMethodList;
	}

	public boolean javasetInputMethod(String inputMethodId) {
		int is = appActivity.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS);
		if(is!=0)
			return false;

		String enableID = Settings.Secure.getString(appActivity.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS);

		if (!enableID.contains(inputMethodId)) {
			Settings.Secure.putString(appActivity.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, enableID + ":" +inputMethodId);
		}

		String defaultID = Settings.Secure.getString(appActivity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

		if (is == 0 && !inputMethodId.equals(defaultID)) {
			Settings.Secure.putString(appActivity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, inputMethodId);
		}

		return true;
	}
	// ////////////////////////////////////////////////////////////////////////////////
	// Set brightness of LCD

	public boolean javaSetBrightness(int brightness)
	{
		final int BRIGHTNESS_MAX = 100;
		final int BRIGHTNESS_MIN = 0;

		if(brightness < 10)
			brightness =10;

		if( brightness<=BRIGHTNESS_MAX && brightness>=BRIGHTNESS_MIN )
		{
			stopAutoBrightness(appActivity.getContentResolver());
			WindowManager.LayoutParams lp = appActivity.getWindow().getAttributes();
			/**
			 * This can be used to override the user's preferred brightness of
			 * the screen.  A value of less than 0, the default, means to use the
			 * preferred screen brightness.  0 to 1 adjusts the brightness from
			 * dark to full bright.
			 */
			lp.screenBrightness = (float)brightness/BRIGHTNESS_MAX;
			appActivity.getWindow().setAttributes(lp);

			Settings.System.putInt(appActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int)(lp.screenBrightness*255));
			return true;
		}

		return false;
	}

	public int javaGetBrightness()
	{	
		WindowManager.LayoutParams lp = appActivity.getWindow().getAttributes();

		int brightness = (int)(lp.screenBrightness*100);

		if(brightness<0)
		{
			try {
				brightness = Settings.System.getInt(appActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
				brightness = brightness*100/255;
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return brightness;
	}

	private void stopAutoBrightness(ContentResolver aContentResolver) {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(aContentResolver, "screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/) == 1/*Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC*/;
			if(automicBrightness == true)
			{
				Settings.System.putInt(aContentResolver, "screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/, 0/*Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL*/);
			}
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int getScreenBrightness(ContentResolver aContentResolver) {
		int nowBrightnessValue = 0;
		try {
			nowBrightnessValue = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}

	private void saveBrightness(ContentResolver resolver, int brightness) {
		Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
		Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
		// resolver.registerContentObserver(uri, true, myContentObserver);
		resolver.notifyChange(uri, null);
	}

	//////////////////////////////////////////////////////////////////////////////////
	//Append Appointment Alert
	public boolean javaAppendAppointment(String programID, String timeInterval, String programName, String programUrl)
	// ///////////////////////////////////
	{
		Util.Trace("javaAppendAppointment:: " + programID + timeInterval + programName + programUrl);
		DameonService.Service_addAppointment(programID, timeInterval, programName, programUrl);
		return true;
	}

	//Delete Appointment Alert
	public boolean javaDeleteAppointment(String programID)
	{
		DameonService.Service_DeleteAppointment(programID);
		return true;
	}

	public void javaOpenAdView()
	{
		//Util.Trace("fgx_javaOpenAdView---IN:" + adView.getWidth() + "," + adView.getHeight());
		//adView.loadAd(adRequest);
		//adView.setLayoutParams(new AbsoluteLayout.LayoutParams(480,78, 0, 0));
		//adView.setVisibility(View.VISIBLE);
	}
	
	//Delete Appointment Alert
	public void javaCloseAdView()
	{
		//Util.Trace("fgx_javaCloseAdView---IN ");
		//adView.setVisibility(View.GONE);
	}
	
	public void javaMoveAdView(int nX, int nY, int nWidth, int nHeight)
	{
		//add pj
		//Util.Trace("fgx_javaMoveAdView---IN: " +nX + "," + nY + "," + nWidth + "," + nHeight);
		//adView.loadAd(adRequest);
		//adView.setLayoutParams(new AbsoluteLayout.LayoutParams(480,78, nX, nY));
		//adView.setVisibility(View.VISIBLE);
	}
	//////////////////////////////////////////////////////////////////////////////
	//run native 

	// Set screen time-out  of cell phone

	public boolean javaSetScreenTimeOut(int timeout/*unit: s*/)
	{
		Util.Trace(TAG+"::"+"javaSetScreenTimeOut" + timeout);
		try {
			//int t = Settings.System.getInt(appActivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
			if(timeout == 0){
				//Settings.System.putInt(appActivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
				appActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
			else
			{
				Settings.System.putInt(appActivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout*1000);
				//	appActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// get screen time-out  of cell phone

	public int javaGetScreenTimeOut(/*unit: s*/)
	{
		int screentimeout  = 0;
		try {
			screentimeout = Settings.System.getInt(appActivity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return screentimeout/1000;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// Get RINGER NOTIFICATION Vibrate  of cell phone

	public boolean javaSetVibrate(int nVibrate)
	{
		if(audioMgr == null)
			audioMgr = (AudioManager)appActivity.getSystemService(Context.AUDIO_SERVICE);
		if(audioMgr == null)
			return false;

		if(nVibrate == 1)
		{
			Vibrator VibrateMgr = (Vibrator) appActivity.getSystemService(Context.VIBRATOR_SERVICE);
			VibrateMgr.vibrate(1000);
		}

		audioMgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, nVibrate);
		audioMgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, nVibrate);

		return true;
	}


	public boolean javaGetVibrate()
	{
		if(audioMgr == null)
			audioMgr = (AudioManager)appActivity.getSystemService(Context.AUDIO_SERVICE);
		if(audioMgr == null)
			return false;

		if(audioMgr.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ON)
			return true;

		if(audioMgr.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION) == AudioManager.VIBRATE_SETTING_ON)
			return true;

		return false;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	//The necessary condition for our APP

	public boolean javaIsAirplaneModeOn()
	{
		if (Settings.System.getInt(appActivity.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
			return true;
		}
		return false;
	}

	public boolean javaHaveSimCard()
	{
		TelephonyManager telManager = (TelephonyManager) appActivity.getSystemService(Context.TELEPHONY_SERVICE);
		if (telManager.getSimCountryIso() == null || "".equals(telManager.getSimCountryIso().trim())) {
			return false;
		}
		return true;
	}

	public String javaGetSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //Judge that whether the SDCARD exists or not
		if(sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory(); //Get the ROOT directory of SDCARD.
		}
		if(sdDir != null)
			return sdDir.toString();
		else
			return "";
	}

	public String javaGetUserAgent()
	{
		String ua = Util.getUserAgent();
		return ua==null ? "" : ua;
	}

	public void javaExitApplication()
	{
		venusEventHandler.sendEmptyMessage(MSG_ID_EXIT);
	}




	// ///////////////////////////////////////////////////////////////////////////////////
	// Tmpc Player
	private native int tmpcGetPlayerState(int handle);
	private native boolean tmpcBeginShow();
	private native int tmpcGetRawPicture(int[] buffer, int size);	//return: xxx rgb_888(565)
	private native boolean tmpcEndShow();

	private static final Object surfaceLock = new Object();
	public static boolean IS_DEBUG_MODE = true;


	static final long DEF_WAIT_TIME = 15;

	private long WAIT_TIME = DEF_WAIT_TIME;

	private int tmpcState;
	private int state;
	public static final int PLAYER_CLOSE = 0;
	public static final int PLAYER_STOPED = 1;
	public static final int PLAYER_STARTED = 2;
	public static final int PLAYER_GOTINFO = 3;
	public static final int PLAYER_PAUSED = 4;
	public static final int PLAYER_BUFFERING = 5;
	public static final int PLAYER_PLAYING = 6;

	private Surface surface;
	private Canvas canvas;
	private Bitmap bitmap;
	private Rect rectDest;
	private Matrix matrix;
	private Paint paint;

	private int[] rawPicture;
	private int rawWidth = 0;
	private int rawHeight = 0;
	private int pictureX;
	private int pictureY;
	private int pictureW;
	private int pictureH;

	private void rawPictureSizeChanged(int width, int height) {
		if ( (rawWidth == width && rawHeight == height) || (width<=0) || (height<=0) ) {
			return;
		}

		rawWidth = width;
		rawHeight = height;

		Util.Trace("rawPictureSizeChanged");
		Util.Trace("rawW ="+rawWidth+"  rawH = "+rawHeight);
		synchronized (surfaceLock) {
			Util.Trace("new rawPicture");
			rawPicture = null;

			rawPicture = new int[rawWidth*rawHeight];

			bitmap = Bitmap.createBitmap(rawWidth, rawHeight, Bitmap.Config.RGB_565);
		}

		SurfaceHolder holder = videoview.getHolder();
		setPictureFixToHolder(rawWidth, rawHeight, holder);
	}

	private void rawPictureSizeChanged2(int width, int height) {
		if ( (rawWidth == width && rawHeight == height) || (width<=0) || (height<=0) ) {
			return;
		}

		rawWidth = width;
		rawHeight = height;
		if(mHolder != null)
		{
			mHolder.setFixedSize(rawWidth, rawHeight);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		}
	}

	private void setPictureFixToHolder(int width, int height, SurfaceHolder holder) {
		if (holder == null) {
			Util.Trace("TMPCPlayer::setPictureFixToHolder: holder = null");
			return;
		}
		if (width <= 0 || height <= 0) {
			Util.Trace("TMPCPlayer::setPictureFixToHolder: width = " + width + "; height = " + height);
			return;
		}
		Util.Trace(TAG + ": setPictureFixToHolder0");
		synchronized (surfaceLock) {
			Util.Trace("setPictureFixToHolder1");
			surface = null;
			surface = holder.getSurface();
			rectDest = null;
			rectDest = holder.getSurfaceFrame();
			matrix = null;
			matrix = new Matrix();
			int rectw = rectDest.right - rectDest.left;
			int recth = rectDest.bottom - rectDest.top;
			Util.Trace("rect = "+rectDest.left+" "+rectDest.top+" "+rectDest.right+" "+rectDest.bottom);

			int rotation = 0;

			Util.Trace(TAG + ": setPictureFixToHolder2 :"+rectw+"*"+recth+" => "+width+"*"+height);
			if (((width > height) && (rectw < recth)) || ((width < height) && (rectw > recth))) {
				rotation = 90;
				Util.Trace(TAG + ": rotation = 90");
			}

			float scale = 1;
			if (rotation == 0) {
				float h_scale = (float)rectw/width;
				float v_scale = (float)recth/height;
				scale = h_scale < v_scale ? h_scale : v_scale;

				pictureW = new Float(width*scale).intValue();
				pictureH = new Float(height*scale).intValue();
				if (pictureW < rectw) {
					pictureX = (rectw - pictureW)/2;
				} else {
					pictureX = 0;
				}
				if (pictureH < recth) {
					pictureY = (recth - pictureH)/2;
				} else {
					pictureY = 0;
				}
			} else if (rotation == 90) {
				float h_scale = (float)recth/width;
				float v_scale = (float)rectw/height;
				scale = h_scale < v_scale ? h_scale : v_scale;

				pictureW = new Float(width*scale).intValue();
				pictureH = new Float(height*scale).intValue();
				if (pictureW < recth) {
					pictureX = (recth - pictureW)/2;
				} else {
					pictureX = 0;
				}
				if (pictureH < rectw) {
					pictureY = (rectw - pictureH)/2 - rectw;
				} else {
					pictureY = - rectw;
				}
			}

			pictureX = new Float(pictureX/scale).intValue();
			pictureY = new Float(pictureY/scale).intValue();

			paint = null;
			if (scale != 1) {
				paint = new Paint();
				paint.setFlags(Paint.FILTER_BITMAP_FLAG);
			}

			matrix.postScale(scale, scale);
			matrix.postRotate(rotation);
		}
	}



	private RefreshHandler mRedrawHandler = new RefreshHandler(Looper.getMainLooper());

	class RefreshHandler extends Handler {

		private boolean HandlerRun = false;
		public static final int SLEEP_TIME = 5;

		// since 1.3.4
		public RefreshHandler(Looper l) {
			super(l);
			if(MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
			{
				this.getLooper().getMainLooper().getThread().setPriority(10);
			}
		}

		public void handleMessage(Message msg) {
			if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER || MEDIA_PLAYER_TYPE == PE_MEDIA_PLAYER)
			{
				update();
			}
			else if(MEDIA_PLAYER_TYPE == TMPC_MEDIA_PLAYER_PublicSurface)
			{
				update2();
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}

		public void enable(boolean en)
		{
			HandlerRun = en;
		}

		public boolean isRun()
		{
			return HandlerRun;
		}
	};

	private void creatAudioTrack(int freq, int format, int channels) {
		Util.Trace("demo: creatAudioTrack ");

		int fmt = 0;
		switch (format) {
		case 16:
			fmt = AudioFormat.ENCODING_PCM_16BIT;
			break;
		case 8:
			fmt = AudioFormat.ENCODING_PCM_8BIT;
			break;
		default:
			Util.Trace(TAG + ": format Error");
			return;
		}

		int chnls = 0;
		switch (channels) {
		case 1:
			chnls = AudioFormat.CHANNEL_CONFIGURATION_MONO;
			break;
		case 2:
			chnls = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
			break;
		default:
			Util.Trace(TAG + ": channels Error");
			return;
		}

		audiotrack_buffersize = AudioTrack.getMinBufferSize(freq, chnls, fmt);
		AudioTrack audiotrack = new AudioTrack(AudioManager.STREAM_MUSIC, freq,
				chnls, fmt, audiotrack_buffersize, AudioTrack.MODE_STREAM);

		mAudioTrack = audiotrack;

		Util.Trace(TAG + ": buffsersize = " + audiotrack_buffersize);
		if (audiotrack == null) {
			Util.Trace("creatAudioTrack failed!");
			return;
		}
		// player.param.audiotrack = audiotrack ;
	}

	private void update2()
	{
		long updatetime = System.currentTimeMillis();

		tmpcState = tmpcGetPlayerState(g_player_handle);

		rawPictureSizeChanged2(videoRawWidth, videoRawHeight);

		updatetime = System.currentTimeMillis() - updatetime;
		updatetime = WAIT_TIME - updatetime;
		if (updatetime < RefreshHandler.SLEEP_TIME) {
			updatetime = RefreshHandler.SLEEP_TIME;
		}
		if(mRedrawHandler.isRun())
		{
			mRedrawHandler.sleep(updatetime);
		}
	}

	private void update() {
		long updatetime = System.currentTimeMillis();

		tmpcState = tmpcGetPlayerState(g_player_handle);

		state = tmpcState;

		if (!bViewCreated)
		{
			videoview.setBackgroundColor(Color.TRANSPARENT);
			bViewCreated = true;
		}

		if (state > PLAYER_STOPED) {			
			rawPictureSizeChanged(videoRawWidth, videoRawHeight);

			if (tmpcBeginShow()) {		
				if (rawPicture == null || rawPicture.length == 0) {
					tmpcEndShow();
					// Error
					Util.Trace("...................... rawPicture == null!!!");
					return ;
				}
				int length = tmpcGetRawPicture(rawPicture, (rawPicture.length)<<2);
				if ((length > 0) || (null != rawPicture)) {
					bitmap.setPixels(rawPicture, 0, rawWidth, 0, 0,
							rawWidth, rawHeight);
					synchronized (surfaceLock) {
						if (surface != null && rectDest != null) {
							try {
								canvas = surface.lockCanvas(rectDest);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (canvas != null) {
								canvas.concat(matrix);
								canvas.drawBitmap(bitmap, pictureX,	pictureY, paint);
								surface.unlockCanvasAndPost(canvas);
							} else {
								if (IS_DEBUG_MODE) {
									Util.Trace(TAG + ": ...................... canvas = null");
								}
							}
						} else {
							if (IS_DEBUG_MODE) {
								Util.Trace(TAG + ": ...................... surface = null");
							}
						}
					}
				} else {
					Util.Trace(TAG + ": ...................... NO pictureBuffer");
				}
				tmpcEndShow();
			}
		}

		updatetime = System.currentTimeMillis() - updatetime;
		updatetime = WAIT_TIME - updatetime;
		if (updatetime < RefreshHandler.SLEEP_TIME) {
			updatetime = RefreshHandler.SLEEP_TIME;
		}
		if(mRedrawHandler.isRun())
		{
			mRedrawHandler.sleep(updatetime);
		}
		else
		{
			Util.Trace("---STOP REFRESH HANDLER---");
			Util.Trace("---rawWidth="+rawWidth+"  rawHeight="+rawHeight+"---");

			if(rawWidth != 0 && rawHeight != 0)
			{
				int i = 0;
				int length = rawWidth*rawWidth;

				rawPicture = new int[length];

				if(rawPicture != null)
				{
					for(; i<length; i++)
						rawPicture[i] = 0;
					if(bitmap == null)
					{
						bitmap = Bitmap.createBitmap(rawWidth, rawHeight, Bitmap.Config.RGB_565);
					}
					bitmap.setPixels(rawPicture, 0, rawWidth, 0, 0, rawWidth, rawHeight);
					synchronized (surfaceLock) 
					{
						if (surface != null && rectDest != null)
						{
							try {
								canvas = surface.lockCanvas(rectDest);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (canvas != null)
							{
								canvas.concat(matrix);
								canvas.drawBitmap(bitmap, pictureX,	pictureY, paint);
								surface.unlockCanvasAndPost(canvas);
							}
						}
					}
				}
			}

		}
	}
	//TODO webview
	public void javaBrowserCreateWindow(int nX,int nY,int nWidth, int nHeight)
	{
		Util.Trace("javaBrowserCreateWindow nX"+nX+",int nY="+nY+",int nWidth="+nWidth+", int nHeight" + nHeight);
		webView_UP2.setLayoutParams(new AbsoluteLayout.LayoutParams(nX,nY, 0, 0));
		webView_Left2.setLayoutParams(new AbsoluteLayout.LayoutParams(nX,nHeight, 0, nY));
		if(fakeScreenWidth>=nX+nWidth)
			webView_Right2.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth-nX-nWidth,nHeight, nX+nWidth, nY));
		if(fakeScreenHeight>=nY+nHeight)
			webView_Down2.setLayoutParams(new AbsoluteLayout.LayoutParams(nWidth,fakeScreenHeight - nY -nHeight, nX, nY+nHeight));
		webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		//webView_LinearLayout.setLayoutParams(new AbsoluteLayout.LayoutParams(nWidth,nHeight, nX, nY));
		imageView.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 50, nX+(nWidth-50)/2, nY+(nHeight-50)/2));
	}

	public void javaBrowserOpenUrl(String url)
	{
		bCleanHistory = true;
		oldurls.clear();
		perurls.clear();
		webView.clearView();
		webView.loadUrl(url);
		webView.clearHistory();
		imageView.setVisibility(View.VISIBLE);
		animation.setDuration(20000);
		imageView.setAnimation(animation);
	}

	public boolean javaIsBrowserRun()
	{
		if(webView.getVisibility() == View.VISIBLE)
			return true;
		else
			return false;
	}

	public boolean javabackHistory()
	{
		if(webView.canGoBack()){
			webView.goBack();
			return true;
		}
		else
			return false;
	}

	public boolean javaperHistory()
	{
		if(webView.canGoForward()){
			webView.goForward();
			return true;
		}
		else
			return false;
	}

	public void javaShowWebBrowser(boolean bShow)
	{
		if(bShow)
			webView.setVisibility(View.VISIBLE);
		else
		{
			webView.setVisibility(View.INVISIBLE);
			imageView.setVisibility(View.INVISIBLE);
			animation.setDuration(0);
			Edit_inputone.hideSoftInputFromWindow(Edit_textone.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void javaSetWebViewRect(int nX,int nY,int nWidth, int nHeight)
	{
		Util.Trace("javaSetWebViewRect nX"+nX+",int nY="+nY+",int nWidth="+nWidth+", int nHeight" + nHeight);
		webView_UP2.setLayoutParams(new AbsoluteLayout.LayoutParams(nX,nY, 0, 0));
		webView_Left2.setLayoutParams(new AbsoluteLayout.LayoutParams(nX,nHeight, 0, nY));
		if(fakeScreenWidth>=nX+nWidth)
			webView_Right2.setLayoutParams(new AbsoluteLayout.LayoutParams(fakeScreenWidth-nX-nWidth,nHeight, nX+nWidth, nY));
		if(fakeScreenHeight>=nY+nHeight)
			webView_Down2.setLayoutParams(new AbsoluteLayout.LayoutParams(nWidth,fakeScreenHeight - nY -nHeight, nX, nY+nHeight));
		webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		//webView_LinearLayout.setLayoutParams(new AbsoluteLayout.LayoutParams(nWidth,nHeight, nX, nY));
	}

	public void javaSetCookie(String url,String cookie)
	{
		Log.d(TAG,"javaSetCookie");
		CookieSyncManager.createInstance(appActivity);
		CookieManager cookieManager = CookieManager.getInstance();
		//		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(url, cookie);
		CookieSyncManager.getInstance().sync();
	}

	public void javaRemoveCookie()
	{
		Log.d(TAG,"javaSetCookie");
		CookieSyncManager.createInstance(appActivity);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

	public String javaGetCookies(String url)
	{	
		String strjson = "{";
		try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			if(entity != null)
				entity.consumeContent();

			if(!cookies.isEmpty()){
				strjson +="\"data\":{";
				for(int i = 0; i< cookies.size();i++)
				{
					strjson += "\"" + cookies.get(i).getName() +"\":";
					strjson += "\"" + cookies.get(i).getValue() +"\"";
					if(i<(cookies.size()-1))
						strjson = ",";
				}
				strjson +="}";
			}
		}
		catch(Exception e)
		{

		}
		strjson += "}";
		return strjson;
	}

	public class MyWebViewDownLoadListener implements DownloadListener{

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			// TODO Auto-generated method stub
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			appActivity.startActivity(intent);
		}

	}

	public void setWebViewRoot(AbsoluteLayout view)
	{
		if(webViewRoot == null)
		{
			webViewRoot = view;
			al.addView(webViewRoot);
			webViewRoot.setVisibility(View.VISIBLE);
		}
	}

	public native void nativeWDPageEvent(int msgId, String url);

	public void sendWDPageEvent(int msgId, String uri)
	{
		nativeWDPageEvent(msgId, uri);
	}

	public void javaVibrate(long milliseconds) {
		Vibrator vibrator = (Vibrator) appActivity.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(milliseconds);
	}

	public boolean javaGetHeadsetStatus() {
		return MonitorHeadset.getHeadsetStatus();
	}

	public void javaPlaySoundEffect(int ntype)	{
		if(al != null)
			al.playSoundEffect(ntype);
	}

	public boolean javaMakeRecord(String filePath) {
		if (mRecorder ==  null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
		try {
			mRecorder.setOutputFile(filePath);
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
			return false;
		}
		return true;
	}

	public void javaStopRecoder() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			} catch (Exception e) {
				e.printStackTrace();
				mRecorder.reset();
				mRecorder.release();
				mRecorder = null;
			}
		}
	}
    
	//public int javaGetSignalStrength() {
	//	return PhoneObserver.getInstance().getSignalStrength();
	//}
	
	//public int javaGetGSMCellId() {
	//	return PhoneObserver.getInstance().getCellId();
	//}
    
	//public String javaGetMacAddress() {
	//	return PhoneObserver.getInstance().getMacAddress();
	//}

	public byte[] javaCharSetConvert(byte[] src, String srcCharSet, String desCharSet, int bufferSize) throws UnsupportedEncodingException{
		String convert = new String(src, 0, bufferSize, srcCharSet);
		return convert.getBytes(desCharSet);
	}
	
	private class threadFileExplore extends Thread { 
		private String strFilePath; 
		private String strFileType;

		public threadFileExplore(String strFilePath, String strFileType) { 
			this.strFilePath = strFilePath; 
			this.strFileType = strFileType;
		} 

		@Override 
		public void run() { 
			while(isStart)
			{
				File myFile = new File(strFilePath);
				Message msg = new Message();
				msg.what = MSG_ID_FILE_EXPLORERES;
				Bundle bunble = new Bundle();
				bunble.putString("filePathName", "");
				bunble.putLong("fileSize", 0);
				bunble.putInt("allFileNum", GetFileSum(myFile));
				bunble.putInt("fileExplorePercent", 0);
				msg.setData(bunble);
				venusEventHandler.sendMessage(msg);
				searchFile(myFile, strFileType);
				javaCancelFileExplore();
			}
		} 

	} 
	private boolean isStart = true;
	private Thread threadFileExplore = null;
	public void javaFileExplore(String strFilePath, String strFileType)
	{
		/*Runnable fileExplore = new Runnable(){
			public void run() 
			{
			    Looper.prepare();	
			    searchFile(strFilePath, strFileType);
			    Looper.loop();
			}
		};
		Thread threadFileExplore = new Thread(fileExplore);
		threadFileExplore.start();*/
		if(threadFileExplore == null)
		{
			isStart = true;
			nFileSum = 0;
			curFileNum = 0;
			threadFileExplore = new threadFileExplore(strFilePath, strFileType);
			threadFileExplore.start(); 
		}
	}

	public void javaCancelFileExplore()
	{
		if(threadFileExplore != null)
		{
			isStart = false;
			threadFileExplore.interrupt();
			threadFileExplore = null;
		}
	}

	private static int nFileSum = 0;
	private int GetFileSum(File myFile)
	{
		if (myFile != null && isStart)
		{
			File[] files = myFile.listFiles();
			nFileSum += files.length;
			if (files.length > 0) 
			{
				for (File file : files)
				{
					if (file.isDirectory())
					{
						if (file.canRead())
						{
							GetFileSum(file); 
						}
					}
				}
			}
		}
		Util.Trace("@@GetFileSum1--in:" + nFileSum);
		return nFileSum;
	}

	private int curFileNum = 0;
	private void searchFile(File myFile,String fileType)
	{ 
		//if (Environment.getExternalStorageState().equals(
		//Environment.MEDIA_MOUNTED))
		//{
		if (myFile != null && isStart) {
			File[] files = myFile.listFiles();

			if (files.length > 0) {
				for (File file : files) {
					if(isStart)
					{
						curFileNum++;
						Util.Trace("@@@@FileFind1 success!!!curFileNum:"+ curFileNum + "," + file.getPath());
						if(nFileSum < 20)
						{
							Message msg = new Message();
							msg.what = MSG_ID_FILE_EXPLORERES;
							Bundle bunble = new Bundle();
							bunble.putString("filePathName", "");
							bunble.putLong("fileSize", 0);
							bunble.putInt("allFileNum", nFileSum);	
							bunble.putInt("fileExplorePercent", (curFileNum * 100)/nFileSum);
							Util.Trace("@@@@FileFind2 success!!!curFileNum:"+ curFileNum + ",per:" + (curFileNum * 100)/nFileSum
									+ "," + file.getPath());
							msg.setData(bunble);

							venusEventHandler.sendMessage(msg);
						}
						else 
						{
							for(int i = 1; i <= 20; i++)
							{
								if(curFileNum == (nFileSum*i)/20)
								{
									Message msg = new Message();
									msg.what = MSG_ID_FILE_EXPLORERES;
									Bundle bunble = new Bundle();
									bunble.putString("filePathName", "");
									bunble.putLong("fileSize", 0);
									bunble.putInt("allFileNum", nFileSum);	
									bunble.putInt("fileExplorePercent", (curFileNum * 100)/nFileSum);
									Util.Trace("@@@@FileFind2 success!!!:"+ (curFileNum * 100)/nFileSum);
									msg.setData(bunble);

									venusEventHandler.sendMessage(msg);
									break;
								}
							}
						}
						if (file.isDirectory()) {
							if (file.canRead()) {
								searchFile(file, fileType); 
							}
						} else {
							try {
								//String bFileType = file.getName().substring(file.getName().indexOf(".") + 1);
								if ( file.getName().substring(file.getName().indexOf(".") + 1).equals(fileType) || fileType.equals("*.*"))
								{
									Message msg = new Message();
									msg.what = MSG_ID_FILE_EXPLORERES;
									Bundle bunble = new Bundle();
									bunble.putString("filePathName", file.getPath());
									bunble.putLong("fileSize", file.length());
									bunble.putInt("allFileNum", nFileSum);	
									bunble.putInt("fileExplorePercent", (curFileNum * 100)/nFileSum);
									Util.Trace("@@@@FileFind3 success!!!:"+ file.getPath() +  file.getName() + "," + 
											file.length() + "," + (curFileNum * 100)/nFileSum);
									msg.setData(bunble);

									venusEventHandler.sendMessage(msg);
								}
							} catch (Exception e) {
								Util.Trace("@@@@FileFind fail!!!");
							}
						}
					}
				}
			}
		}
	}

	public String javaGetPhoneNumber()
	{
		String strRt = "";
		TelephonyManager phoneMgr=(TelephonyManager)appActivity.getSystemService(Context.TELEPHONY_SERVICE);
	               
		strRt = phoneMgr.getLine1Number();

		return strRt;
	}
	
	public String javaGetSimOperator()
	{
		String strRt = "";
		TelephonyManager phoneMgr=(TelephonyManager)appActivity.getSystemService(Context.TELEPHONY_SERVICE);
	               
		strRt = phoneMgr.getSimOperator();

		return strRt;
	}
	public void javaBackgroundApp()
	{
        Util.Trace("=======javaBackgroundApp=======");
        Intent home = new Intent(Intent.ACTION_MAIN);  
        home.addCategory(Intent.CATEGORY_HOME);   
        appActivity.startActivity(home);
    }
	
	private String JavaGetAppStartParamString()
    {
    	return startParam;
    }
	
	public long GetTotalRxBytes()
	{
		int sdk = Util.GetSDK();
		long RL = -1;
		if(sdk >= Util.SDK_ANDROID_22)
		{
			Class<?> cls;
			try {
				cls = Class.forName("android.net.TrafficStats");
				Util.Trace("GetTotalRxBytes cls =" + cls);
				Method m_getTotalRxBytes = cls.getMethod("getTotalRxBytes");
				Util.Trace("GetTotalRxBytes m_getTotalRxBytes =" + m_getTotalRxBytes);
				RL = (Long) m_getTotalRxBytes.invoke(RL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Util.Trace("GetTotalBytes :" + RL);
		return RL;
	}
	
	public long GetTotalTxBytes()
	{
		int sdk = Util.GetSDK();
		long TL = -1;
		if(sdk >= Util.SDK_ANDROID_22)
		{
			Class<?> cls;
			try {
				cls = Class.forName("android.net.TrafficStats");
				Util.Trace("GetTotalRxBytes cls =" + cls);
				Method m_getTotalTxBytes = cls.getMethod("getTotalTxBytes");
				Util.Trace("GetTotalTxBytes m_getTotalTxBytes =" + m_getTotalTxBytes);
				TL = (Long) m_getTotalTxBytes.invoke(TL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Util.Trace("GetTotalTxBytes :" + TL);
		return TL;
	}
	
	public long GetMobilRxBytes()
	{
		int sdk = Util.GetSDK();
		long RL = -1;
		if(sdk >= Util.SDK_ANDROID_22)
		{
			Class<?> cls;
			try {
				cls = Class.forName("android.net.TrafficStats");
				Util.Trace("GetMobilBytes cls =" + cls);
				Method m_getMobileRxBytes = cls.getMethod("getMobileRxBytes");
				Util.Trace("GetMobilBytes m_getMobileRxBytes =" + m_getMobileRxBytes);
				RL = (Long) m_getMobileRxBytes.invoke(RL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Util.Trace("GetMobilBytes :" + RL);
		return RL;
	}
	
	public void javaStartFlux()
	{
		tolRxTraffic = GetTotalRxBytes();
		tolTxTraffic = GetTotalTxBytes();
	}
	
	public int javaGetRxFlux()
	{
		if(tolRxTraffic == -1)
			return -1;
		return (int) (GetTotalRxBytes() - tolRxTraffic);
	}
	
	public int javaGetTxFlux()
	{
		if(tolTxTraffic == -1)
			return -1;
		return (int) (GetTotalTxBytes() - tolTxTraffic);
	}
	
		Handler AutoTestHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("AutoTest", "" + msg.what + " (" + msg.arg1 + "," + msg.arg2 + ")");
			Window window = null;
			switch(msg.what)
			{
			case 100:
				window = VenusActivity.getInstance().appActivity.getWindow();
			    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(appActivity, "开始回放", Toast.LENGTH_SHORT).show();
				break;
			case 101:
				window = VenusActivity.getInstance().appActivity.getWindow();
			    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(appActivity, "开始录制", Toast.LENGTH_SHORT).show();
				break;
			case 102:
				window = VenusActivity.getInstance().appActivity.getWindow();
			    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(appActivity, "回放结束", Toast.LENGTH_SHORT).show();
				break;
			case 103:
				window = VenusActivity.getInstance().appActivity.getWindow();
			    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(appActivity, "录制结束", Toast.LENGTH_SHORT).show();
				break;
			case 1000:
				break;
			case 2000:
				Toast.makeText(appActivity, "请先进行录制", Toast.LENGTH_SHORT).show();
				break;
			case 2001:
				Toast.makeText(appActivity, "无SD卡", Toast.LENGTH_SHORT).show();
				break;
			default:
				nativesendevent(msg.what, -(msg.arg1 + 1), msg.arg2);
				break;
			}
		}
	};
	
	public static void javaAutoTestSendEvent(int type, int x, int y)
	{
		Message message = Message.obtain(VenusActivity.getInstance().AutoTestHandler, type, x, y);
		message.sendToTarget();
	}
}
