package com.wondertek.ocr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wondertek.banner.BannerLayout;
import com.wondertek.banner.BannerLayout.OnItemClickListener;

public class BannerActivity extends Activity {
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
				Toast.makeText(getApplicationContext(), "�����index��" + index,
						Toast.LENGTH_SHORT).show();
			}
		});
		// ��ȡ����Ԫ�ص�����
		indexBgLayout = (LinearLayout) findViewById(R.id.indexbg);
		imageButton1 = (ImageButton) findViewById(R.id.nav01);
		imageButton2 = (ImageButton) findViewById(R.id.nav02);
		imageButton3 = (ImageButton) findViewById(R.id.nav03);
		imageButton4 = (ImageButton) findViewById(R.id.nav04);
		// ���ð�ť1����Ϣ������
		imageButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg1);
			}
		});
		// ���ð�ť2����Ϣ������
		imageButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg2);
			}
		});
		// ���ð�ť3����Ϣ������
		imageButton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				indexBgLayout.setBackgroundResource(R.drawable.indexbg3);
			}
		});
		// ���ð�ť4����Ϣ������
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
