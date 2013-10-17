/**
 * @author 何武
 * <br /> 邮箱:hewu@ahmobile.com
 * <br /> 描述:IndexActivity.java
 * <br /> 版本：1.0.0
 * <br /> 日期：2013-10-17
 */
package com.wondertek.ocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * @author 何武 com.wondrtek.ocr 创建时间：2013-10-17
 */
public class ListViewActivity extends Activity {
	protected static final String TAG = "ListViewActivity";
	private ListView listView = null;
	private ImageButton m_backButton = null;
	private List<Map<String, Object>> listItems = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		listView = (ListView) this.findViewById(R.id.listView1);
		m_backButton = (ImageButton) findViewById(R.id.back);
		// 设置按钮的点击事件
		m_backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 获取上一页的状态 
		String status = getIntent().getExtras().getString("status");
		String[] statusArray = status.split(";");
		Log.d(TAG, "[onCreate] checkbox status:" + status);
		// 根据不同的状态，选择不同的手机
		// 构造ListItem数据
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 9; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (i % 3 == 0) {
				map.put("image", R.drawable.lb01);
			} else if (i % 3 == 1) {
				map.put("image", R.drawable.lb02);
			} else {
				map.put("image", R.drawable.lb03);
			}
			listItems.add(map);
		}
		// 创建适配器
		ListViewAdapter listViewAdapter = new ListViewAdapter(this, listItems);
		listView.setAdapter(listViewAdapter);
		listView.setDividerHeight(0);
		listView.setFocusable(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}
