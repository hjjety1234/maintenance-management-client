package com.wondertek.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wondertek.banner.BannerLayout;
import com.wondertek.banner.BannerLayout.OnItemClickListener;

public class BannerActivity extends Activity {
	protected static final String TAG = "BannerActivity";
	BannerLayout bl;
	LinearLayout indexBgLayout = null;
	ImageButton imageButton1 = null;
	ImageButton imageButton2 = null;
	ImageButton imageButton3 = null;
	ImageButton imageButton4 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner);
		bl = (BannerLayout) findViewById(R.id.banner);
		bl.setOnItemClickListener(new OnItemClickListener() {
			public void onClick(int index, View childview) {
				switch (index) {
				case 0:
					Intent intent = new Intent(BannerActivity.this, MainActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.hold);
					break;
				case 1:
					break;
				case 2:
					break;
				default:
					Log.w(TAG, "[onClick] index is out of bound!");
				}
			}
		});
		// 获取界面元素的引用
		indexBgLayout = (LinearLayout) findViewById(R.id.indexbg);
		imageButton1 = (ImageButton) findViewById(R.id.nav01);
		imageButton2 = (ImageButton) findViewById(R.id.nav02);
		imageButton3 = (ImageButton) findViewById(R.id.nav03);
		imageButton4 = (ImageButton) findViewById(R.id.nav04);
		// 设置按钮1的消息处理函数
		imageButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg1);
				Intent intent = new Intent(BannerActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
			}
		});
		// 设置按钮2的消息处理函数
		imageButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg2);
			}
		});
		// 设置按钮3的消息处理函数
		imageButton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg3);
			}
		});
		// 设置按钮4的消息处理函数
		imageButton4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg4);
			}
		});
	}

	@Override
	protected void onPause() {
		bl.stopScroll();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		if (!bl.isScrolling())
			bl.startScroll();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (!bl.isScrolling())
			bl.startScroll();
		indexBgLayout.setBackgroundResource(R.drawable.indexbg);
		super.onResume();
	}

	@Override
	protected void onStop() {
		bl.stopScroll();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		bl.stopScroll();
		super.onDestroy();
	}
}
