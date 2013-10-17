package com.wondertek.ocr;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HmActivity extends Activity {
	protected static final String TAG = "HmActivity";
	ImageButton imageButton1 = null;
	ImageButton imageButton2 = null;
	ImageView iv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hm);
		imageButton1 = (ImageButton) findViewById(R.id.tab1Btn);
		imageButton2 = (ImageButton) findViewById(R.id.tab2Btn);
		iv = (ImageView)findViewById(R.id.hmbg);
		
		// 设置按钮1的消息处理函数
		imageButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				iv.setImageResource(R.drawable.hmfirst);
			}
		});
		// 设置按钮2的消息处理函数
		imageButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				iv.setImageResource(R.drawable.hmnext);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}
