package com.wondertek.video.map.gdmap;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author yuhongwei
 *
 */
public class GDPopView {
	private static GDPopView instance = null;
	private Context context;
	
	public GDPopView(Context cxt) {
		this.context = cxt;
	}

	public static GDPopView getInstance(Context cxt) {
		if (instance == null) {
			instance = new GDPopView(cxt);
		}
		return instance;
	}

	public View getItemPopView(String desc) {
		if (context instanceof Activity) {
			View popview = ((Activity)context).getLayoutInflater().inflate(context.getResources().
					getIdentifier("gdmap_popview_item", "layout", context.getPackageName()), null);
			TextView tv = (TextView)popview.findViewById(context.getResources().
					getIdentifier("item_name", "id", context.getPackageName()));
			tv.setText(desc);
			return popview;
		} else {
			return null;
		}
	}

	public View getWeatherPopView(int type, String title, String desc) {
		if (context instanceof Activity) {
			View popview = ((Activity)context).getLayoutInflater().inflate(context.getResources().
					getIdentifier("gdmap_popview_weather", "layout", context.getPackageName()), null);
			ImageView image = (ImageView)popview.findViewById(context.getResources().
					getIdentifier("weather", "id", context.getPackageName()));
			switch (type) {
			case GDMapConstants.GDMAP_WEATHER_DUOYUN:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_duoyun", "drawable", context.getPackageName()));
				break;
			case GDMapConstants.GDMAP_WEATHER_LEIYU:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_leiyu", "drawable", context.getPackageName()));
				break;
			case GDMapConstants.GDMAP_WEATHER_QING:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_qing", "drawable", context.getPackageName()));
				break;
			case GDMapConstants.GDMAP_WEATHER_WU:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_wu", "drawable", context.getPackageName()));
				break;
			case GDMapConstants.GDMAP_WEATHER_XUE:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_xue", "drawable", context.getPackageName()));
				break;
			case GDMapConstants.GDMAP_WEATHER_YU:
				image.setImageResource(context.getResources().
						getIdentifier("gdmap_yu", "drawable", context.getPackageName()));
				break;
			default:
				break;
			}
			TextView tv = (TextView)popview.findViewById(context.getResources().
					getIdentifier("title", "id", context.getPackageName()));
			tv.setText(desc);
			return popview;
		} else {
			return null;
		}
	}

	public View getLocationPopView() {
		if (context instanceof Activity) {
			View view = ((Activity)context).getLayoutInflater().inflate(context.getResources().
					getIdentifier("gdmap_popview_location", "layout", context.getPackageName()), null);
			ImageButton location = (ImageButton) view.findViewById(context.getResources().
					getIdentifier("map_location_btn", "id", context.getPackageName()));
			location.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GDMapManager.getInstance().enableAutoLocation();
				}
			});
			return view;
		}
		return null;
	}
}
