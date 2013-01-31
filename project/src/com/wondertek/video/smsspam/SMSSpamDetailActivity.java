package com.wondertek.video.smsspam;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.wondertek.video.VenusApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author yuhongwei
 *
 */

public class SMSSpamDetailActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		//String address = intent.getStringExtra(SMSSpamConstant.SPAM_ADDR);
		String body = intent.getStringExtra(SMSSpamConstant.SPAM_BODY);
		String filePath = VenusApplication.appAbsPath;
		if( writeFile(filePath, SMSSpamConstant.FILE_NAME, body) )
		{
			Message msg = new Message();
			msg.what = VenusApplication.MSG_ID_RECEIVE_MESSAGE;
			VenusApplication.applicationHandler.sendMessage( msg );
		}
		this.finish();
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
