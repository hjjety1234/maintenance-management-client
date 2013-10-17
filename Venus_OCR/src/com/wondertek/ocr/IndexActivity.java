/**
 * @author 何武
 * <br /> 邮箱:hewu@ahmobile.com
 * <br /> 描述:IndexActivity.java
 * <br /> 版本：1.0.0
 * <br /> 日期：2013-10-16
 */
package com.wondertek.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author 何武 com.wondrtek.ocr 创建时间：2013-10-16
 */
public class IndexActivity extends Activity {
	protected static final String TAG = "IndexActivity";
	Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_index);
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, ">>>run<<<");
				handler.removeCallbacks(this);
				Intent intent = new Intent(IndexActivity.this, BannerActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				finish();
			}
		};
		handler.postDelayed(runnable, 2000);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}
