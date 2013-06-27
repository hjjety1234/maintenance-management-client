package com.wondertek.video.caller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @Description
 * @author hewu <hewu2008@gmail.com>
 * @date 2013-6-26 下午8:45:48
 * 
 */
public class NonScrollableListView extends ListView {

	FloatRelativeLayout floatLayout = null;

	/**
	 * <p>
	 * Description
	 * </p>
	 * 
	 * @param context
	 */
	public NonScrollableListView(Context context) {
		super(context);
	}

	public NonScrollableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// Build from XML layout
	public NonScrollableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLayout(FloatRelativeLayout floatLayout) {
		this.floatLayout = floatLayout;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (floatLayout != null && floatLayout.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

}
