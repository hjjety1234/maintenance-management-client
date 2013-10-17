/**
 * @author ����
 * <br /> ����:hewu@ahmobile.com
 * <br /> ����:ListViewAdapter.java
 * <br /> �汾��1.0.0
 * <br /> ���ڣ�2013-10-17
 */
package com.wondertek.ocr;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * @author ���� com.wondertek.ocr ����ʱ�䣺2013-10-17
 */
public class ListViewAdapter extends BaseAdapter {
	private static final String TAG = "ListViewAdapter";
	private Context context; // ����������
	private List<Map<String, Object>> listItems; // ��Ϣ����
	private LayoutInflater listContainer; // ��ͼ����

	public final class ListItemView { // �Զ���ؼ�����
		public ImageView image;
	}

	/**
	 * ���캯��
	 * 
	 * @param context
	 * @param listItems
	 */
	public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.listItems = listItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return listItems.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "[getView] position:" + position);
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.activity_listitem,null);
			listItemView.image = (ImageView) convertView.findViewById(R.id.list_item_bg);
			listItemView.image.setImageResource((Integer) listItems.get(position).get("image"));
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();  
		}
		return convertView;
	}

}
