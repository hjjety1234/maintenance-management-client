package com.wondertek.plugins.mapview.gdmap;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MapView.LayoutParams;
import com.mapabc.mapapi.map.PoiOverlay;
import com.mapabc.mapapi.poisearch.PoiPagedResult;
import com.mapabc.mapapi.poisearch.PoiSearch;
import com.mapabc.mapapi.poisearch.PoiSearch.Query;
import com.mapabc.mapapi.poisearch.PoiSearch.SearchBound;
import com.mapabc.mapapi.poisearch.PoiTypeDef;

public class MyPoiSearch {
	private Activity venusActivity = null;
	private MapController mMapController = null;
	private MapView mMapView = null;
	private Handler mHandler = null;
	
	private PoiPagedResult result;
	private ProgressDialog progDialog = null;
	private Query mQuery;
	private PoiOverlay poiOverlay = null;
	private static MyPoiSearch sInstance = null;
	
	public static MyPoiSearch getInstance(Activity appActivity,
			MapController mapController,MapView mapView,Handler handler) {
		if (sInstance == null) {
			sInstance = new MyPoiSearch(appActivity,mapController,mapView,handler);
		}
		return sInstance;
	}

	public static MyPoiSearch getInstance() {
		return sInstance;
	}

	public MyPoiSearch(Activity appActivity,
			MapController mapController,MapView mapView,Handler handler) {
		venusActivity = appActivity;
		mMapController = mapController;
		mMapView = mapView;
		mHandler = handler;
		
		progDialog = new ProgressDialog(venusActivity);
		
	}

	public void doSearch(String strkey, String cityNumber) {
		if (strkey.trim().equals(""))
			return;
		Query query = new Query(strkey, PoiTypeDef.All, cityNumber);
		doSearchQuery(query, null);
	}

	public void doSearch(String strkey, GeoPoint centerPoint, int radius) {
		if (strkey.trim().equals(""))
			return;
		Query query = new Query(strkey, PoiTypeDef.All, "");
		SearchBound bound = new SearchBound(centerPoint, radius);
		doSearchQuery(query, bound);
	}

	public void doSearchQuery(Query query, final SearchBound bound) {
		mQuery = query;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch(venusActivity, mQuery);
					if (bound != null)
						poiSearch.setBound(bound);
					result = poiSearch.searchPOI();
				} catch (Exception e) {
					progDialog.dismiss();
					e.printStackTrace();
				}
				if (progDialog.isShowing()) {
					if (result != null) {
						progDialog.dismiss();
						mHandler.sendMessage(Message
								.obtain(mHandler,
										Constants.POISEARCH));
					} else {
						handler.sendMessage(Message
								.obtain(mHandler,
										Constants.ERROR));
					}
				}
			}
		});
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索:\n" + query);
		progDialog.show();
		t.start();
	}

	public PoiPagedResult getPoiPagedResult() {
		return result;
	}
	
	public String doPoisearchSuccess()
	{
		String strResult ="{";
		try {
			List<PoiItem> poiItems = result.getPage(1);
			
			if (poiItems!=null||poiItems.size()>0) {
            	strResult +="\"data1\":[";
          		for(int i =0; i< poiItems.size();i++)
        		{
      				PoiItem poiInfo = poiItems.get(i);
        			strResult +="{\"latitude\":\"" +poiInfo.getPoint().getLatitudeE6()+"\",";
        			strResult +="\"longitude\":\"" +poiInfo.getPoint().getLongitudeE6()+"\",";
         		   	strResult +="\"name\":\"" +poiInfo.getTitle()+"\",";
         		   	strResult +="\"address\":\"" +poiInfo.getSnippet()+"\",";
         		   	strResult +="\"phoneNum\":\"" +poiInfo.getTel()+"\"}";
         		   	if(i<poiItems.size()-1)
         		   		strResult +=",";
        		}
        		strResult += "]";
            	
				
				
				mMapController.setZoom(13);
				mMapController.animateTo(poiItems.get(0).getPoint());
				if (poiOverlay != null) {
					poiOverlay.removeFromMap();
				}
				Drawable drawable = venusActivity.getResources().getDrawable(venusActivity.getResources().getIdentifier("da_marker_red", "drawable", venusActivity.getPackageName()));
				poiOverlay = new MyPoiOverlay(venusActivity,
						drawable, poiItems); 
				poiOverlay.addToMap(mMapView);
				poiOverlay.showPopupWindow(0);
				mMapView.invalidate();
				progDialog.dismiss();
			}
			
			
		} catch (Exception e) {
			progDialog.dismiss();
		}
		strResult += "}";
		return strResult;
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.POISEARCH) {
					try {
						List<PoiItem> poiItems = result.getPage(1);
						if (poiItems!=null||poiItems.size()>0) {
							mMapController.setZoom(13);
							mMapController.animateTo(poiItems.get(0).getPoint());
							if (poiOverlay != null) {
								poiOverlay.removeFromMap();
							}
							Drawable drawable = venusActivity.getResources().getDrawable(venusActivity.getResources().getIdentifier("da_marker_red", "drawable", venusActivity.getPackageName()));
							poiOverlay = new MyPoiOverlay(venusActivity,
									drawable, poiItems); 
							poiOverlay.addToMap(mMapView);
							poiOverlay.showPopupWindow(0);
							mMapView.invalidate();
							progDialog.dismiss();
						}
					} catch (Exception e) {
						progDialog.dismiss();
						

				}
		}else if (msg.what == Constants.ERROR) {
			progDialog.dismiss();
			
		}
    }};
}

class MyPoiOverlay extends PoiOverlay {
	private Context context;
	private Drawable drawable;
	private int number = 0;
	private List<PoiItem> poiItem;
	private LayoutInflater mInflater;
	private int height;

	public MyPoiOverlay(Context context, Drawable drawable,
			List<PoiItem> poiItem) {
		super(drawable, poiItem);
		this.context = context;
		this.poiItem = poiItem;
		mInflater = LayoutInflater.from(context);
		height = drawable.getIntrinsicHeight();
	}

	@Override
	protected Drawable getPopupBackground() {
		// TODO Auto-generated method stub
		drawable = context.getResources().getDrawable(
				context.getResources().getIdentifier("tip_pointer_button", "drawable", context.getPackageName()));
		return drawable;
	}

	@Override
	protected View getPopupView(final PoiItem item) {
		// TODO Auto-generated method stub

		View view = mInflater.inflate(context.getResources().getIdentifier("popup", "layout", context.getPackageName()), null);
		TextView nameTextView = (TextView) view.findViewById(context.getResources().getIdentifier("PoiName", "id", context.getPackageName()));
		TextView addressTextView = (TextView) view
				.findViewById(context.getResources().getIdentifier("PoiAddress", "id", context.getPackageName()));
		nameTextView.setText(item.getTitle());
		String address = item.getSnippet();
		if (address == null || address.length() == 0) {
			address =(String) context.getResources().getText(context.getResources().getIdentifier("gdmap_china", "string", context.getPackageName()));
		}
		addressTextView.setText(address);
		LinearLayout layout = (LinearLayout) view
				.findViewById(context.getResources().getIdentifier("LinearLayoutPopup", "id", context.getPackageName()));
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});

		return view;
	}

	@Override
	public void addToMap(MapView arg0) {
		// TODO Auto-generated method stub
		super.addToMap(arg0);
	}

	@Override
	protected LayoutParams getLayoutParam(int arg0) {
		// TODO Auto-generated method stub
		LayoutParams params = new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, poiItem.get(number)
						.getPoint(), 0, -height, LayoutParams.BOTTOM_CENTER);

		return params;
	}

	@Override
	protected Drawable getPopupMarker(PoiItem arg0) {
		// TODO Auto-generated method stub
		return super.getPopupMarker(arg0);
	}

	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		number = index;
		return super.onTap(index);
	}

}
