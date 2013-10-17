/**
 * @author 何武
 * <br /> 邮箱:hewu@ahmobile.com
 * <br /> 描述:ListViewAdapter.java
 * <br /> 版本：1.0.0
 * <br /> 日期：2013-10-17
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
 * @author 何武 com.wondertek.ocr 创建时间：2013-10-17
 */
public class ListViewAdapter extends BaseAdapter {
	private static final String TAG = "ListViewAdapter";
	private Context context; // 运行上下文
	private List<Map<String, Object>> listItems; // 信息集合
	private LayoutInflater listContainer; // 视图容器

	public final class ListItemView { // 自定义控件集合
		public ImageView image;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param listItems
	 */
	public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
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
