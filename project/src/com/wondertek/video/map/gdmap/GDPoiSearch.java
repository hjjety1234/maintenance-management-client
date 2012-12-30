package com.wondertek.video.map.gdmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.PoiOverlay;
import com.mapabc.mapapi.poisearch.PoiPagedResult;
import com.mapabc.mapapi.poisearch.PoiSearch;
import com.mapabc.mapapi.poisearch.PoiSearch.Query;
import com.mapabc.mapapi.poisearch.PoiSearch.SearchBound;

/**
 * 
 * @author yuhongwei
 *
 */
public class GDPoiSearch {
	private static final String TAG = "GDPoiSearch";
	private static GDPoiSearch instance = null;
	private ProgressDialog progDialog = null;
	private Context context;
	private MapView mMapView;
	private PoiPagedResult result;
	private Stack<PoiOverlay> poiOverlays = new Stack<PoiOverlay>();
	private GDPoiSearch(Context cxt, MapView mv) {
		this.progDialog = new ProgressDialog(cxt);
		this.context = cxt;
		this.mMapView = mv;
	}

	public static GDPoiSearch getInstance(Context cxt, MapView mv) {
		if (instance == null) {
			instance = new GDPoiSearch(cxt, mv);
		}
		return instance;
	}

	public void search(String key, String category, String cityCode) {
		Log.d(TAG, "SearchByWord  key: " + key + " Category: " + category + " CityCode: " + cityCode);
		Query query = new Query(key, category, cityCode);
		doPoiSearch(query, null,key,cityCode);
	}
	
	public void search(String key, String category, GeoPoint point, int radius) {
		Query query = new Query(key, category, "");
		SearchBound bound = new SearchBound(point, radius);
		doPoiSearch(query, bound,null,null);
	}

	public void refreshMapView() {
		Log.d(TAG, ">>>refreshMapView<<<");
		try {
			if(result.getPage(1).size()>0){
				List<PoiItem> singleItem = new ArrayList<PoiItem>();
				for(int i = 0; i < result.getPage(1).size(); i++){
					singleItem.add(result.getPage(1).get(i));
					if (singleItem != null && singleItem.size() > 0) {
						mMapView.getController().setZoom(13);
						Drawable marker = context.getResources().getDrawable(context.getResources().
								getIdentifier("gdmap_iconmarka" +  i, "drawable", context.getPackageName()));
						GDPoiOverlay poiOverlay = new GDPoiOverlay(marker, singleItem);
						poiOverlay.addToMap(mMapView);
						poiOverlays.push(poiOverlay);
						//poiOverlay.showPopupWindow(0);
						mMapView.invalidate();
						progDialog.dismiss();
					}
					singleItem.remove(0);
				}
				mMapView.getController().animateTo(result.getPage(1).get(0).getPoint());
			}else{
				mapError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			progDialog.dismiss();
		}
	}

	public String getPoiSearchData() {
		StringBuilder data = new StringBuilder();
		data.append('{');
		try {
			List<PoiItem> poiItems = result.getPage(1);
			if (poiItems != null && poiItems.size() > 0) {
				data.append("\"data1\":[");
				for(int i =0; i< poiItems.size();i++) {
					PoiItem poiInfo = poiItems.get(i);
					data.append("{\"latitude\":\"" +poiInfo.getPoint().getLatitudeE6()+"\",");
					data.append("\"longitude\":\"" +poiInfo.getPoint().getLongitudeE6()+"\",");
					data.append("\"name\":\"" +poiInfo.getTitle()+"\",");
					data.append("\"address\":\"" +poiInfo.getSnippet()+"\",");
					data.append("\"phoneNum\":\"" +poiInfo.getTel()+"\"}");
					if(i<poiItems.size()-1)
						data.append(',');
				}
				data.append(']');
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		data.append('}');
		return data.toString();
	}

	public void removeAllPOIs() {
		while (!poiOverlays.empty()) {
			PoiOverlay ol = poiOverlays.pop();
            mMapView.getOverlays().remove(ol);
		}
	}
    
	public void mapError() {
		if(progDialog.isShowing()){
			progDialog.dismiss();
		}
		Log.d(TAG, "mapError!");
		Toast.makeText(context, context.getResources().getString(context.getResources().getIdentifier("gdmap_error", "string",
				context.getPackageName())), Toast.LENGTH_LONG).show();
	}

	private void doPoiSearch(final Query query, final SearchBound bound,String address,String cityCode) {
		Log.d(TAG, ">>>doPoiSearch<<<");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch((Activity) context, query);
					if (bound != null) {
						poiSearch.setBound(bound);
					}
					poiSearch.setPoiNumber(10);
					result = poiSearch.searchPOI();
				} catch (Exception e) {
					e.printStackTrace();
					progDialog.dismiss();
				}
				if (progDialog.isShowing()) {
					Handler handler = GDMapManager.getInstance(context).getHandler();
					if (result != null) {
						//progDialog.dismiss();
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POISEARCH_RESULT));
					} else {
						//progDialog.dismiss();
						handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_ERROR));
					}
				}
			}
		});
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(context.getResources().getString(context.getResources().
				getIdentifier("gdmap_searching", "string", context.getPackageName())));
		progDialog.show();
		t.start();
	}

	class GDPoiOverlay extends PoiOverlay {
		private Drawable marker;
		public GDPoiOverlay(Drawable marker, List<PoiItem> items) {
			super(marker, items);
			this.marker = marker;
			this.populate();
		}

		@Override
		protected LayoutParams getLayoutParam(int index) {
			PoiItem poi = getItem(index);
			int mHeight = marker.getIntrinsicHeight();
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					poi.getPoint(), 0, -mHeight, LayoutParams.BOTTOM_CENTER);
			return params;
		}

		@Override
		protected Drawable getPopupBackground() {
			return context.getResources().getDrawable(context.getResources().
					getIdentifier("gdmap_tip_pointer_button", "drawable", context.getPackageName()));
		}

		@Override
		protected Drawable getPopupMarker(PoiItem item) {
			// TODO Auto-generated method stub
			return super.getPopupMarker(item);
		}

		@Override
		protected View getPopupView(final PoiItem item) {
			View view = ((Activity) context).getLayoutInflater().inflate(context.getResources().
					getIdentifier("gdmap_popview_item", "layout", context.getPackageName()), null);
			final TextView title = (TextView) view.findViewById(context.getResources().
					getIdentifier("item_name", "id", context.getPackageName()));
			title.setText(item.getTitle());
			final Button detail = (Button) view.findViewById(context.getResources().
					getIdentifier("item_detail_btn", "id", context.getPackageName()));
			detail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getPopupView(item);
					detail.setText("");
					Log.d(TAG, ">>>onClick<<<");
					StringBuilder sb = new StringBuilder();
					sb.append("{\"latitude\":\"" +item.getPoint().getLatitudeE6()+"\",");
					sb.append("\"longitude\":\"" +item.getPoint().getLongitudeE6()+"\",");
					sb.append("\"name\":\"" +item.getTitle()+"\",");
					sb.append("\"address\":\"" +item.getSnippet()+"\",");
					sb.append("\"tel\":\"" +item.getTel()+"\"}");
					Log.d(TAG, sb.toString());
					title.setText(item.getTitle()+"\n"+item.getSnippet()+"\n"+item.getTel());
					Handler handler = GDMapManager.getInstance(context).getHandler();
					handler.sendMessage(Message.obtain(handler, GDMapConstants.GDMAP_POIDETAIL, sb.toString()));
				}
			});
			return view;
		}

		@Override
		protected boolean onTap(int index) {
			Log.d(TAG, ">>>onTap<<<" + " index: " + index);
			// TODO Auto-generated method stub
			return super.onTap(index);
		}
	}
}
