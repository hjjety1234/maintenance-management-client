package com.wondertek.video.map.bdmap;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 
 * @author yuhongwei
 *
 */
public class BDPopView {
    private static BDPopView instance = null;
    private Context mContext;
    
    private BDPopView(Context cxt) {
    	mContext = cxt;
    }
    
    public static BDPopView getInstance(Context cxt) {
    	if (instance == null) {
    		instance = new BDPopView(cxt);
    	}
    	return instance;
    }
    
    public View getItemPopView(String desc) {
		if (mContext instanceof Activity) {
			View popview = ((Activity)mContext).getLayoutInflater().inflate(mContext.getResources().
					getIdentifier("bdmap_popview_item", "layout", mContext.getPackageName()), null);
			TextView tv = (TextView)popview.findViewById(mContext.getResources().
					getIdentifier("item_name", "id", mContext.getPackageName()));
			tv.setText(desc);
			return popview;
		} else {
			return null;
		}
	}
    
    public View getPoiPopView(String tag, String desc) {
    	if (mContext instanceof Activity) {
    		View view = ((Activity)mContext).getLayoutInflater().inflate(mContext.getResources().
    				getIdentifier("bdmap_popview_poi.xml", "layout", mContext.getPackageName()), null);
    		view.setTag(tag);
    		TextView tv = (TextView)view.findViewById(mContext.getResources().getIdentifier(
    				"poi_pop_desc", "id", mContext.getPackageName()));
            tv.setText(desc);
            view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String tag = (String)v.getTag();
                    Handler handler = BDMapManager.getInstance().getHandler();
                    handler.sendMessage(Message.obtain(handler, BDMapConstants.BDMAP_POIPRESSED, tag));
				}
			});
            return view;
    	} else {
    		return null;
    	}
    }
}
