/*==============================================================================
            Copyright (c) 2010-2012 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary
==============================================================================*/

package com.ahmobile.ar;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

import com.wondertek.ict4.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class GUIManager {

    // Custom views:
    private View overlayView;
//    private ToggleButton startButton;
//    private Button clearButton;
//    private Button deleteButton;
    private TextView textView ;
    // A Handler for working with the GUI from other threads:
    static class MyActivityHandler extends Handler
    {
        private WeakReference<GUIManager> guiManager;
        private Context context;

        MyActivityHandler(GUIManager guim, Context c)
        {
            guiManager = new WeakReference<GUIManager>(guim);
            context = c;
        }

        public void handleMessage(Message msg)
        {
            TextView textView  = guiManager.get().textView;
        	textView.setTextColor(Color.RED);
        	textView.setTextSize(18);
            String text = (String) msg.obj;
            DebugLog.LOGI("�ж�ʶ���ͼƬ�ǣ�"+ text);
            if(text.equals("0")) {
	        	textView.setText("");
	        	return;
	        }
            String value = DeviceData.device.get(text);
            DebugLog.LOGI("value-----��"+ value);
            if(value == null) {
            	textView.setText("δʶ����豸��");
            }else {
            	textView.setText(value);
            }
/*            Set<String> key = DeviceData.device.keySet();
			for (Iterator it = key.iterator(); it.hasNext();) {
		          String colName = (String) it.next();
		          String colValue = DeviceData.device.get(colName) == null? "":DeviceData.device.get(colName).toString();
		          if(text.equals("0")) {
		        	  textView.setText("");
		        	  break;
		          }else if(text.equals(colName)) {
		        	  textView.setText(colValue);
		        	  break;
		          }else {
		        	  textView.setText("δʶ����豸��");
		          }
		    }     
*/                       
        }
    }
    private MyActivityHandler mainActivityHandler;

    // Flags for our Handler:
    public static final int SHOW_DELETE_BUTTON = 0;
    public static final int HIDE_DELETE_BUTTON = 1;
    public static final int ENABLE_START_BUTTON = 2;
    public static final int DISABLE_START_BUTTON = 3;
    public static final int TOGGLE_START_BUTTON = 4;
    public static final int DISPLAY_INFO_TOAST = 5;

    // Native methods to handle button clicks:
    public native void nativeStart();
    public native void nativeClear();
    public native void nativeReset();
    public native void nativeDelete();


    /** Initialize the GUIManager. */
    public GUIManager(Context context)
    {
        // Load our overlay view:
        // NOTE: This view will add content on top of the camera / OpenGL view
        overlayView = View.inflate(context, R.layout.ar_interface_overlay, null);

        // Create a Handler from the current thread:
        // This is the only thread that can make changes to the GUI,
        // so we require a handler for other threads to make changes
        mainActivityHandler = new MyActivityHandler(this, context);
    }


    public void initViews() {
    	if (overlayView == null)
          return;
    	textView = (TextView)overlayView.findViewById(R.id.textView1);
    }

    /** Send a message to our gui thread handler. */
    public void sendThreadSafeGUIMessage(Message message)
    {
        mainActivityHandler.sendMessage(message);
    }


    /** Getter for the overlay view. */
    public View getOverlayView()
    {
        return overlayView;
    }
}
