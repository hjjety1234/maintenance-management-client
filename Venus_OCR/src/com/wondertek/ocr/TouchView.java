package com.wondertek.ocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class TouchView extends View {
	private Context mContext = null;
 
    private Paint topLine;
    private Paint bottomLine;
    private Paint leftLine;
    private Paint rightLine;
	private Path path = new Path();
	private Paint drawPath = new Paint();
	
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
		setLineParameters(Color.argb(255, 255, 181, 51), 5);
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
		// ÃÌº”±ﬂøÚ
		canvas.drawLine(10, 8, 10, 160, topLine);
		canvas.drawLine(10, height-160, 10, height-7, topLine);
		canvas.drawLine(10, height-10, 160, height-10, topLine);
		canvas.drawLine(width-160, height-10,width-10, height-10, topLine);
		canvas.drawLine(width-10, height-7, width-10, height-160, topLine);
		canvas.drawLine(width-10, 160, width-10, 8,  topLine);
		canvas.drawLine(width-10, 10, width-160, 10,  topLine);
		canvas.drawLine(160, 10, 10, 10,  topLine);
		

		drawPath.setColor(Color.argb(255, 255, 196, 102));  
		path.moveTo(25, 25);
		path.lineTo(width-25, 25);
		path.lineTo(width-25, 200);
		path.lineTo(width-50, 190);
		path.lineTo(width-50, 50);
		path.lineTo(50,50);
		path.lineTo(50, height-50);
		path.lineTo(width-50, height-50);
		path.lineTo(width-50, height-190);
		path.lineTo(width-25, height-200);
		path.lineTo(width-25, height-25);
		path.lineTo(25, height-25);
		path.close();
		canvas.drawPath(path, drawPath);
		
//		canvas.drawLine(20, height - 20,  width - 120, height -20, bottomLine);
//		canvas.drawLine(20, 20, 20, height - 20, leftLine);
//		canvas.drawLine(width - 120, 20, width - 120, height - 20, rightLine);
		// ÃÌº”≈ƒ’’Ã· æ”Ô
		
		
		Paint paint = new Paint(); 
		paint.setColor(Color.argb(255, 255, 181, 51)); 
		paint.setTextSize(35); 
		canvas.drawText("’˝‘⁄ºÏ≤‚…®√Ë°£°£°£", width-350, height-80, paint); 
		canvas.restore();
	}
	
	public void setInvalidate() {
		invalidate();
	}
}