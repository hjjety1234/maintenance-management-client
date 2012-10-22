package com.wondertek.video.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {    
	 @Override  
	 public void onReceive(Context context, Intent intent) {   
	   // TODO Auto-generated method stub  
		 Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show();   
		 Vibrator VibrateMgr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 VibrateMgr.vibrate(1000*60);
	 }
	  
} 

