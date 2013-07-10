package com.wondertek.video.smsspam;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

public class SMSHandler extends Handler {
	public static final int MSG_ID_SAVE_SMS			= 0;
	
	private static SMSHandler instance = null;
	
	public SMSHandler(Context context) {
		super();
	}
	
	private SMSHandler()
	{
		
	}
	
	public static SMSHandler getInstance()
	{
		if(instance == null)
		{
			instance = new SMSHandler();
		}
		return instance;
	}
	
	public void handleMessage(Message message) {
		Util.Trace("SMSHandler::handleMessage " + message.what );
		
		switch(message.what)
		{
		case MSG_ID_SAVE_SMS :
		{
			String content = (String)message.obj;
			Util.Trace("SMS = " + content);
			Bundle bundle = message.getData();
			String filePath = bundle.getString("SMS_PATH");
			String fileName = bundle.getString("SMS_NAME");
			boolean bStart = bundle.getBoolean("SMS_STARTUP");
			if( writeFile(filePath, fileName, content) )
			{
				Message msg = new Message();
				msg.what = VenusApplication.MSG_ID_RECEIVE_SMS_MESSAGE;
				Bundle bd = new Bundle();
				bd.putBoolean("SMS_STARTUP", bStart);
				msg.setData(bd);
				VenusApplication.applicationHandler.sendMessage( msg );
			}
			break;
		}
		default :
			break;
		}
	}
	
	private boolean writeFile(String filePath, String fileName, String content) {
		boolean ret = false;
		FileWriter writer = null;
		File smsFile = new File(filePath, fileName);
		
		try {
			if (smsFile.exists()) {
				smsFile.delete();
			}
			smsFile.createNewFile();
			writer = new FileWriter(smsFile, true);
			String smsInfo = content;
			writer.write(smsInfo);
			ret = true;
		} catch (IOException e) {
			ret = false;
			throw new RuntimeException(e);
		} finally {
			try {
				if(writer != null) writer.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}

}