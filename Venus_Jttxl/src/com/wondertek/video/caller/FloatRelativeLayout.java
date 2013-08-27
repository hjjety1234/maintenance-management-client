package com.wondertek.video.caller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wondertek.jttxl.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FloatRelativeLayout extends RelativeLayout {
	private static final String TAG = "FloatRelativeLayout";
	private Context mContext;
	
	// 标记是否是自定义的布局
	private static boolean bIsCustomLayout = false;
	
	public boolean isScale = false;     // 标记是否需要缩放
	private float mScaleFactor = 1;		// 缩放比例
	private ScaleGestureDetector mScaleDetector;
	
	// 触摸点的相对坐标
	private float rx; 	
	private float ry;
	
	// 触摸点的绝对坐标
	private float rawX; 
	private float rawY;
	
	// 缩放的中心点坐标
	private float px;
	private float py;
	
	// 系统桌面和布局参数
	private WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService("window");
	private WindowManager.LayoutParams wmParams = null;

	/**
	* <p>构造函数，根据条件加载对应的布局 </p>
	* @param context 
	* @param params
	* @param e
	*/
	public FloatRelativeLayout(Context context,
			WindowManager.LayoutParams params, List<Employee> empList) {
		super(context);
		// 初始化变量
		mContext = context;
		wmParams = params;
		mScaleDetector = new ScaleGestureDetector(context, new OnPinchListener());
		// 如果有照片则显示带照片的名片
		Employee employee = empList.get(0);
		if (employee.getPicutre() != null && !employee.getPicutre().trim().equals("")) {
			File dir = new File(Constants.getLocPicDir());
			dir.mkdir();
			String localPicPath = Constants.getLocPicDir() + employee.getPicutre();
			Log.d(TAG, "[initLayout] local picture path: " + localPicPath);
			File pic = new File(localPicPath);
			if (pic.exists() == true)  {
				if(empList.size() == 1){
					showCustomLayout(context, employee);
				}else{
					showCustomListViewLayout(context, empList);
				}
				return;
			}
		}
		if (empList.size() == 1) {
			showDefaultLayout(context, employee);
		}else {
			showListViewLayout(context, empList);
		}
	}
	
	/**
	* @Description 显示默认名片
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午6:01:38
	*/
	private void showDefaultLayout(Context context, Employee employee) {
		Log.d(TAG, "[showDefaultLayout]");
		bIsCustomLayout = false; 
		inflate(context, R.layout.float_view, this);

		// 显示姓名
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		callerName.setText(employee.getName());

		// 显示职位信息
		TextView headship = (TextView) findViewById(R.id.headship);
		String defaultHeadship = getResources().getString(R.string.defaultHeadship);
		if (!employee.getHeadship().trim().equals(""))
			headship.setText(employee.getHeadship());

		// 显示部门信息
		TextView dept = (TextView) findViewById(R.id.deptinfo);
		dept.setText(employee.getDepartment());

		// 显示提示信息
		showTipText();
		
		// 尝试下载员工照片
		downloadImg(employee);
		
		// 显示自定义LOGO
		ImageView iv = (ImageView) findViewById(R.id.m_logo);
		showCustomLogo(iv, employee.getEmpid());
	}
	
	/**
	* @Description 显示带图片的名片
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:54:51
	*/
	private void showCustomLayout(Context context, Employee employee) {
		Log.d(TAG, "[showCustomLayout]");
		bIsCustomLayout = true;
		inflate(context, R.layout.float_view_withphoto, this);
		
		// 显示姓名
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		callerName.setText(employee.getName());

		// 显示职位信息
		TextView headship = (TextView) findViewById(R.id.headship);
		String defaultHeadship = getResources().getString(R.string.defaultHeadship);
		if (!employee.getHeadship().trim().equals(""))
			headship.setText(employee.getHeadship());

		// 显示部门信息
		TextView dept = (TextView) findViewById(R.id.deptinfo);
		dept.setText(employee.getDepartment());
		
		// 显示自定义照片
		ImageView bg = (ImageView) findViewById(R.id.incoming_bg);
		Uri uri = Uri.parse(Constants.getLocPicDir() + employee.getPicutre());
		bg.setImageURI(uri);

		// 显示联系人信息背景图片
		ImageView iv = (ImageView) findViewById(R.id.caller_info_bg);
		iv.setVisibility(VISIBLE);
		
		// 显示自定义LOGO
		ImageView logo = (ImageView)findViewById(R.id.small_logo_icon);
		showCustomLogo(logo, employee.getEmpid());
	}
	
	
	/**
	* @Description 显示多部门带图片的名片
	* @author guokai
	* @date 2013-7-2
	*/
	private void showCustomListViewLayout(Context context, List<Employee> empList) {
		Log.d(TAG, "[showListViewLayout] employee count: " + empList.size());
		bIsCustomLayout = true;
		inflate(context, R.layout.float_view_withphoto_mutil, this);
		
		// 显示姓名
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		callerName.setText(empList.get(0).getName());
		
		// 显示多职位信息
		NonScrollableListView listview = (NonScrollableListView) findViewById(R.id.listView_mutil);
		listview.setLayout(this);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < empList.size(); i++) {  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("deptname", empList.get(i).getDepartment());  
            map.put("headship", empList.get(i).getHeadship());  
            listItem.add(map);  
        } 
		SimpleAdapter listItemAdapter = new SimpleAdapter(context, listItem,
				R.layout.float_view_withphoto_listitem, 
				new String[] { "deptname", "headship" }, 
				new int[] { R.id.deptname_photo, R.id.headship_photo});
		listview.setAdapter(listItemAdapter);
		
		// 显示联系人信息背景图片
		ImageView bg = (ImageView) findViewById(R.id.incoming_bg_mutil);
		Uri uri = Uri.parse(Constants.getLocPicDir() + empList.get(0).getPicutre());
		bg.setImageURI(uri);
		
		// 显示员工第一个职位对应的自定义LOGO
		ImageView logo = (ImageView)findViewById(R.id.m_logo);
		showCustomLogo(logo, empList.get(0).getEmpid());
	}
	
	
	
	
	/**
	* @Description 显示多部门的名片
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:36:19
	*/
	private void showListViewLayout(Context context, List<Employee> empList) {
		Log.d(TAG, "[showListViewLayout] employee count: " + empList.size());
		bIsCustomLayout = true;
		inflate(context, R.layout.float_view_listview, this);
		
		// 显示姓名
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		callerName.setText(empList.get(0).getName());
		
		// 显示多职位信息
		NonScrollableListView listview = (NonScrollableListView) findViewById(R.id.listView1);
		listview.setLayout(this);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < empList.size(); i++) {  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("deptname", empList.get(i).getDepartment());  
            map.put("headship", empList.get(i).getHeadship());  
            listItem.add(map);  
        } 
		SimpleAdapter listItemAdapter = new SimpleAdapter(context, listItem,
				R.layout.float_view_listitem, 
				new String[] { "deptname", "headship" }, 
				new int[] { R.id.deptname, R.id.headship});
		listview.setAdapter(listItemAdapter);
		
		// 尝试下载员工第一个职位的照片
		downloadImg(empList.get(0));
		
		// 显示员工第一个职位对应的自定义LOGO
		ImageView logo = (ImageView)findViewById(R.id.m_logo);
		showCustomLogo(logo, empList.get(0).getEmpid());
	}
	
	/**
	* @Description 异步下载员工的照片
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-27 下午10:33:37
	*/
	private void downloadImg(Employee employee) {
		// 如果自定义照片不存在则进行下载
		if (employee.getPicutre() != null && !employee.getPicutre().trim().equals("")) {
			File dir = new File(Constants.getLocPicDir());
			dir.mkdir();
			String localPicPath = Constants.getLocPicDir() + employee.getPicutre();
			Log.d(TAG, "[downloadImg] local picture path: " + localPicPath);
			File pic = new File(localPicPath);
			if (pic.exists() == false) {
				Log.d(TAG, "[downloadImg] trying to async download picture...");
				final DownloadFileTask downloadFile = new DownloadFileTask(employee.getPicutre());
				new Thread() {
					@Override
					public void run() {
						String requestUri = downloadFile.requestResourceUri();
						if (requestUri != null) {
							String resourceUri = Constants.RES_PIC_URL_PREFIX + requestUri;
							downloadFile.execute(resourceUri);
						} else {
							Log.w(TAG, "[downloadImg] request resource uri failed!");
						}
					}
				}.start();
			} else {
				Log.w(TAG, "[showDefaultLayout] picture path is null.");
			}
		}
	}
	
	/**
	 * 显示自定义的公司图标
	 * @param iv 待显示的ImageView
	 * @param employeeId 员工编号
	 */
	private void showCustomLogo(ImageView iv, String employeeId) {
		Log.d(TAG, "[showCustomLogo] employee id: " + employeeId);
		final String logoImg = DbHelper.getLogoImg(employeeId);
		if (logoImg == null) return;
		String logoImgPath = Constants.getLocPicDir() + logoImg;
		File pic = new File(logoImgPath);
		if (pic.isFile() && pic.exists() == true) {
			Log.d(TAG, "[showCustomLogo] find custom logo file!");
			Uri uri = Uri.parse(logoImgPath);
			iv.setImageURI(uri);
		}else {
			Log.d(TAG, "[showCustomLogo] can't find custom logo file, trying to async downloading...!");
			final DownloadFileTask downloadFile = new DownloadFileTask(logoImg);
			new Thread() {
				@Override
				public void run() {
					String resourceUri = Constants.RES_LOGO_URL_PREFIX + logoImg;
					downloadFile.execute(resourceUri);
				}
			}.start();
		}
	}
	
	/**
	* @Description 第一次显示名片需要显示提示信息
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:56:20
	*/
	public void showTipText() {
		// get SharedPreferences and editor
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		TextView tipText = (TextView) findViewById(R.id.tip_text);
		ImageView iv = (ImageView) findViewById(R.id.tip_bg);

		if (tipText == null || iv == null)
			return;

		if (popupPos.getBoolean("showTip", true)) {
			Log.d(TAG, "[showTipText] true");
			iv.setVisibility(VISIBLE);
			tipText.setVisibility(VISIBLE);
		} else {
			Log.d(TAG, "[showTipText] false");
			iv.setVisibility(INVISIBLE);
			tipText.setVisibility(INVISIBLE);
		}
	}
	
	/**
	* @Description 设置提示信息的可见性
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:58:22
	*/
	public void setTipTextVisiblity(boolean visibility) {
		Log.d(TAG, "[setTipTextVisiblity] visibility: " + visibility);
		// get SharedPreferences and editor
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = popupPos.edit();
		editor.putBoolean("showTip", visibility);
		editor.commit();
		if (visibility == false) {
			TextView tipText = (TextView) findViewById(R.id.tip_text);
			ImageView iv = (ImageView) findViewById(R.id.tip_bg);
			if (tipText == null || iv == null)
				return;
			iv.setVisibility(INVISIBLE);
			tipText.setVisibility(INVISIBLE);
			
		}
	}
	

	/* (非 Javadoc)
	* <p> 子视图重绘时调用</p>
	* @param canvas
	* @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	*/
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d(TAG, "[dispatchDraw] isScale: " + isScale + ", isCustomLayout: " + bIsCustomLayout);
		if (bIsCustomLayout == true) {
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			super.dispatchDraw(canvas);
			canvas.restore();
			return;
		}

		// get SharedPreferences and editor
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = popupPos.edit();

		// get original matrix
		float[] values = new float[9];
		Matrix m = new Matrix();
		boolean isMatrixSaved = true;
		for (int i = 0; i < 9; ++i) {
			values[i] = popupPos.getFloat("f" + i, -1000);
			if (values[i] == -1000)
				isMatrixSaved = false;
		}
		if (isMatrixSaved == true) {
			m.setValues(values);
		} else {
			m = canvas.getMatrix();
			m.getValues(values);
			for (int i = 0; i < 9; ++i) {
				editor.putFloat("f" + i, values[i]);
			}
		}
		canvas.setMatrix(m);

		// save scale pivotX and pivotY
		int nfactor = (int) (mScaleFactor * 100);
		Log.d(TAG, "[dispatchDraw] write scale factor: " + nfactor);
		ConfigUtil.setValue(String.valueOf(nfactor));
		editor.putFloat("pivotX", px);
		editor.putFloat("pivotY", py);

		// get original width and height
		float width = popupPos.getFloat("width", 0.0f);
		float height = popupPos.getFloat("height", 0.0f);
		if (width < 0.01f || height < 0.01f) {
			width = getWidth();
			height = getHeight();
			editor.putFloat("width", width);
			editor.putFloat("height", height);
		}
		editor.commit();

		// do scale text
		try {
			scaleText(mScaleFactor);
		} catch (Exception ex) {
			Log.d(TAG, "error message" + ex.getMessage());
		}
		// do scale
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(mScaleFactor, mScaleFactor, px, py);
		super.dispatchDraw(canvas);
		canvas.restore();

		// set layout's width and height
		wmParams.width = (int) (width * mScaleFactor);
		wmParams.height = (int) (height * mScaleFactor);
		if (PhoneStatReceiver.relativeLayout != null)
			wm.updateViewLayout(this, wmParams);

		// debug output
		Log.d(TAG, "[dispatchDraw] width: " + getWidth());
		Log.d(TAG, "[dispatchDraw] height: " + getHeight());
	}

	/**
	* @Description 对名片的字体进行缩放
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:55:54
	*/
	public void scaleText(float mScaleFactor) {
		TextView callerName = (TextView) findViewById(R.id.caller_name);
		TextView headship = (TextView) findViewById(R.id.headship);
		TextView dept = (TextView) findViewById(R.id.deptinfo);
		callerName.setTextScaleX(mScaleFactor);
		headship.setTextScaleX(mScaleFactor);
		dept.setTextScaleX(mScaleFactor);
	}

	/**
	* @Description 进行名片的缩放
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午6:01:04
	*/
	public void scale(float scaleFactor, float pivotX, float pivotY) {
		Log.d(TAG, "[scale] scale:" + scaleFactor + ", pivotX:" + pivotX
				+ ", pivotY: " + pivotY + ", isScale: " + isScale);
		if (isScale == false || scaleFactor <= 0.89) {
			Log.w(TAG, "[scale] isScale is false or reach minmal limit, do nothing...");
			return;
		}
		if (scaleFactor < 1) {
			mScaleFactor = scaleFactor;
			setTipTextVisiblity(false);
		} else {
			mScaleFactor = 1;
		}
		px = pivotX;
		py = pivotY;
		this.invalidate();
	}

	/**
	* @Description 设置是否允许缩放
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午6:01:26
	*/
	public void setScaleEnabled(boolean bEnable) {
		isScale = bEnable;
	}

	/* (非 Javadoc)
	* <p>监听名片的触摸消息 </p>
	* @param event
	* @return
	* @see android.view.View#onTouchEvent(android.view.MotionEvent)
	*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		rawX = event.getRawX();
		rawY = event.getRawY();
		Log.d(TAG, "[onTouchEvent] MotionEvent: " + event.getAction());
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			rx = event.getX();
			ry = event.getY();
			isScale = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isScale == false) {
				updateViewPosition();
			} else {
				mScaleDetector.onTouchEvent(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isScale == false) {
				updateViewPosition();
			}
			isScale = false;
			rx = 0;
			ry = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			isScale = true;
			mScaleDetector.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isScale = false;
			break;
		}
		Log.d(TAG, "[onTouchEvent] isScale: " + isScale);
		return true;
	}

	/**
	* @Description 更新名片的位置
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午5:58:55
	*/
	private void updateViewPosition() {
		Log.d(TAG, ">>>updateViewPosition<<<");
		wmParams.x = (int) (rawX - rx);
		wmParams.y = (int) (rawY - ry);
		if (PhoneStatReceiver.relativeLayout != null)
			wm.updateViewLayout(this, wmParams);
		SharedPreferences popupPos = mContext.getSharedPreferences(
				PhoneStatReceiver.POPUP_POS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = popupPos.edit();
		editor.putInt("rawX", wmParams.x);
		editor.putInt("rawY", wmParams.y);
		editor.commit();
	}

	/**
	* @Description 缩放的消息监听类
	* @author hewu <hewu2008@gmail.com>
	* @date 2013-6-26 下午7:34:33
	* 
	*/
	private class OnPinchListener extends SimpleOnScaleGestureListener {
		float startingSpan;
		float startFocusX;
		float startFocusY;

		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.d(TAG, ">>>onScaleBegin<<<");
			startingSpan = detector.getCurrentSpan();
			startFocusX = detector.getFocusX();
			startFocusY = detector.getFocusY();
			return true;
		}

		public boolean onScale(ScaleGestureDetector detector) {
			Log.d(TAG, ">>>onScale<<<");
			scale(detector.getCurrentSpan() / startingSpan, startFocusX, startFocusY);
			return true;
		}
	}
}
