package com.wondertek.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class TagActivity extends Activity {
	private static final String TAG = "TagActivity";
	private ImageButton m_selButton1 = null;
	private ImageButton m_selButton2 = null;
	private ImageButton m_selButton3 = null;
	private ImageButton m_selButton4 = null;
	private ImageButton m_selButton5 = null;
	private ImageButton m_selButton6 = null;
	private ImageButton m_selButton7 = null;
	private ImageButton m_selButton8 = null;
	private ImageButton m_backButton = null;
	private ImageButton m_nextButton = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tag);
		m_selButton1 = (ImageButton) findViewById(R.id.btnSel1);
		m_selButton2 = (ImageButton) findViewById(R.id.btnSel2);
		m_selButton3 = (ImageButton) findViewById(R.id.btnSel3);
		m_selButton4 = (ImageButton) findViewById(R.id.btnSel4);
		m_selButton5 = (ImageButton) findViewById(R.id.btnSel5);
		m_selButton6 = (ImageButton) findViewById(R.id.btnSel6);
		m_selButton7 = (ImageButton) findViewById(R.id.btnSel7);
		m_selButton8 = (ImageButton) findViewById(R.id.btnSel8);
		m_backButton = (ImageButton) findViewById(R.id.back);
		m_nextButton = (ImageButton) findViewById(R.id.next);
		setClickListener();
	}

	/**
	 * 重设选择按钮的状态
	 */
	public void resetCheckBoxStatus() {
		m_selButton1.setImageResource(R.drawable.uncheck);
		m_selButton2.setImageResource(R.drawable.uncheck);
		m_selButton3.setImageResource(R.drawable.uncheck);
		m_selButton4.setImageResource(R.drawable.uncheck);
		m_selButton5.setImageResource(R.drawable.uncheck);
		m_selButton6.setImageResource(R.drawable.uncheck);
		m_selButton7.setImageResource(R.drawable.uncheck);
		m_selButton8.setImageResource(R.drawable.uncheck);
		m_selButton1.setTag("0");
		m_selButton2.setTag("0");
		m_selButton3.setTag("0");
		m_selButton4.setTag("0");
		m_selButton5.setTag("0");
		m_selButton6.setTag("0");
		m_selButton7.setTag("0");
		m_selButton8.setTag("0");
	}

	/**
	 * 设置按钮的点击事件
	 */
	public void setClickListener() {
		// 设置按钮的点击事件
		m_backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 下一步按钮的点击事件
		m_nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TagActivity.this, ListViewActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
			}
		});
		// 选择按钮的消息处理函数
		m_selButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton1.getTag().equals("0")) {
					m_selButton1.setTag("1");
					m_selButton1.setImageResource(R.drawable.check);
				} else {
					m_selButton1.setTag("0");
					m_selButton1.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton2.getTag().equals("0")) {
					m_selButton2.setTag("1");
					m_selButton2.setImageResource(R.drawable.check);
				} else {
					m_selButton2.setTag("0");
					m_selButton2.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton3.getTag().equals("0")) {
					m_selButton3.setTag("1");
					m_selButton3.setImageResource(R.drawable.check);
				} else {
					m_selButton3.setTag("0");
					m_selButton3.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton4.getTag().equals("0")) {
					m_selButton4.setTag("1");
					m_selButton4.setImageResource(R.drawable.check);
				} else {
					m_selButton4.setTag("0");
					m_selButton4.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton5.getTag().equals("0")) {
					m_selButton5.setTag("1");
					m_selButton5.setImageResource(R.drawable.check);
				} else {
					m_selButton5.setTag("0");
					m_selButton5.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton6.getTag().equals("0")) {
					m_selButton6.setTag("1");
					m_selButton6.setImageResource(R.drawable.check);
				} else {
					m_selButton6.setTag("0");
					m_selButton6.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton7.getTag().equals("0")) {
					m_selButton7.setTag("1");
					m_selButton7.setImageResource(R.drawable.check);
				} else {
					m_selButton7.setTag("0");
					m_selButton7.setImageResource(R.drawable.uncheck);
				}
			}
		});
		// 选择按钮的消息处理函数
		m_selButton8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_selButton8.getTag().equals("0")) {
					m_selButton8.setTag("1");
					m_selButton8.setImageResource(R.drawable.check);
				} else {
					m_selButton8.setTag("0");
					m_selButton8.setImageResource(R.drawable.uncheck);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		resetCheckBoxStatus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}