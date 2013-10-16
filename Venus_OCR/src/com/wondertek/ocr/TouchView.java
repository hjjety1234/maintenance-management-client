package com.wondertek.ocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class TouchView extends View {
	private Context mContext = null;
 
    private Paint topLine;
    private Paint bottomLine;
    private Paint leftLine;
    private Paint rightLine;
	
	public TouchView(Context context){
		super(context);
		init(context);
	}
 
	public TouchView(Context context, AttributeSet attrs){
		super (context,attrs);
		init(context);
	}
 
	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
 
	private void init(Context context) {
		mContext = context;
		topLine = new Paint();
		bottomLine = new Paint();
		leftLine = new Paint();
		rightLine = new Paint();
		setLineParameters(Color.WHITE, 2);
	}
 
	private void setLineParameters(int color, float width){
		topLine.setColor(color);
		topLine.setStrokeWidth(width);
		bottomLine.setColor(color);
		bottomLine.setStrokeWidth(width);
		leftLine.setColor(color);
		leftLine.setStrokeWidth(width);
		rightLine.setColor(color);
		rightLine.setStrokeWidth(width);
	}
	
	// Draws the bounding box on the canvas. 
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth(); 
		int height = wm.getDefaultDisplay().getHeight();
		// 添加边框
		canvas.drawLine(20, 20, width - 120, 20, topLine);
		canvas.drawLine(20, height - 20,  width - 120, height -20, bottomLine);
		canvas.drawLine(20, 20, 20, height - 20, leftLine);
		canvas.drawLine(width - 120, 20, width - 120, height - 20, rightLine);
		// 添加拍照提示语
		Paint paint = new Paint(); 
		paint.setColor(Color.WHITE); 
		paint.setTextSize(30); 
		canvas.drawText("请将二代身份证尽可能撑满主界面的方框！", width / 2 - 260, height / 2, paint); 
		canvas.restore();
	}
	
	public void setInvalidate() {
		invalidate();
	}
}