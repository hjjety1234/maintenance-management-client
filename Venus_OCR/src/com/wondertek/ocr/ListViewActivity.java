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
import android.view.KeyEvent;
import android.widget.ListView;

/**
 * @author 何武 com.wondrtek.ocr 创建时间：2013-10-17
 */
public class ListViewActivity extends Activity {
	protected static final String TAG = "ListViewActivity";
	private ListView listView = null;
	private List<Map<String, Object>> listItems = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		listView = (ListView) this.findViewById(R.id.listView1);
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
