package com.wondertek.video;

import java.io.File;
import java.io.FileWriter;


import com.wondertek.video.DameonService;
import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;



public class DameonReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Util.Trace( "DameonReceiver::onReceive: " + intent.getAction());
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Util.Trace( "DameonReceiver::onReceive: ACTION_TIME_TICK===IN");
			Intent Intent = new Intent(context, DameonService.class);
			context.startService(Intent);
		}
		else if(intent.getAction().equals("com.wondertek.mobilevideo3.appointmentclick"))
		{
			final String resp = intent.getStringExtra("RESPTEXT");
			writeToFile(resp);

			Message msg = new Message();
			msg.what = VenusApplication.MSG_ID_RECEIVE_MESSAGE;
			VenusApplication.applicationHandler.sendMessage(msg);
			
			/*Message msg = new Message();
			msg.what = VenusApplication.MSG_ID_BOOT_APPACTIVITY;
			Bundle bundle = new Bundle();
			bundle.putInt("ID", 0);
			msg.setData(bundle);
			VenusApplication.applicationHandler.sendMessage(msg);*/
		}
	}
	
	private void writeToFile(String text) {
		File file = new File(VenusApplication.appAbsPath, "msgpush.txt");
		Util.Trace("write: " + file.getAbsolutePath());
		Util.Trace(text);

		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			FileWriter writer = new FileWriter(file, true);
			writer.write(text);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}