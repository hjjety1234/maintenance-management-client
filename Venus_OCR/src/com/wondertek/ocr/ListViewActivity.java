/**
 * @author ����
 * <br /> ����:hewu@ahmobile.com
 * <br /> ����:IndexActivity.java
 * <br /> �汾��1.0.0
 * <br /> ���ڣ�2013-10-17
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
 * @author ���� com.wondrtek.ocr ����ʱ�䣺2013-10-17
 */
public class ListViewActivity extends Activity {
	protected static final String TAG = "ListViewActivity";
	private ListView listView = null;
	private ImageButton m_backButton = null;
	private List<Map<String, Object>> listItems = null;
	private static final int LOW_VALUE_PHONE = 1;
	private static final int HIGH_VALUE_PHONE = 2;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		listView = (ListView) this.findViewById(R.id.listView1);
		m_backButton = (ImageButton) findViewById(R.id.back);
		// ���ð�ť�ĵ���¼�
		m_backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// ��ȡ��һҳ��״̬ 
		String status = getIntent().getExtras().getString("status");
		String[] statusArray = status.split(";");
		Log.d(TAG, "[onCreate] checkbox status:" + status);
		// ���״̬
		String isHighValuePhone = getIntent().getExtras().getString("isHighValue");
		Log.d(TAG, "[onCreate] isHighValuePhone:" + isHighValuePhone);
		// ���ݲ�ͬ��״̬��ѡ��ͬ���ֻ�
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		if (isHighValuePhone.equals("true")) {
			for (int i = 1; i < 5; ++i) {
				Map<String, Object> map = new HashMap<String, Object>();
				switch (i) {
					case 1:
						map.put("image", R.drawable.lb01);
						break;
					case 2:
						map.put("image", R.drawable.lb02);
						break;
					case 3:
						map.put("image", R.drawable.lb03);
						break;
					case 4:
						map.put("image", R.drawable.lb04);
						break;
//					case 5:
//						map.put("image", R.drawable.lb05);
//						break;
//					case 6:
//						map.put("image", R.drawable.lb06);
//						break;
//					case 7:
//						map.put("image", R.drawable.lb07);
//						break;
//					case 8:
//						map.put("image", R.drawable.lb08);
//						break;
//					case 9:
//						map.put("image", R.drawable.lb09);
//						break;
				}
				listItems.add(map);
			}
		}else{
			for (int i = 5; i < 10; ++i) {
				Map<String, Object> map = new HashMap<String, Object>();
				switch (i) {
//					case 1:
//						map.put("image", R.drawable.lb01);
//						break;
//					case 2:
//						map.put("image", R.drawable.lb02);
//						break;
//					case 3:
//						map.put("image", R.drawable.lb03);
//						break;
//					case 4:
//						map.put("image", R.drawable.lb04);
//						break;
					case 5:
						map.put("image", R.drawable.lb05);
						break;
					case 6:
						map.put("image", R.drawable.lb06);
						break;
					case 7:
						map.put("image", R.drawable.lb07);
						break;
					case 8:
						map.put("image", R.drawable.lb08);
						break;
					case 9:
						map.put("image", R.drawable.lb09);
						break;
				}
				listItems.add(map);
			}
		}
		// ����������
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
