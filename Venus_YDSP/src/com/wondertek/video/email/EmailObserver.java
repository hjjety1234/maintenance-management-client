package com.wondertek.video.email;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

import com.wondertek.video.VenusActivity;

public class EmailObserver {
	public static final String TAG = "email";
	public VenusActivity venusHandle;
	private static EmailObserver instance = null;
	
	private EmailObserver(VenusActivity va) {
		venusHandle = va;
	}
	
	public static EmailObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new EmailObserver(va);
		}
		return instance;
	}
	
	void javasendEmail(String address, String cc, String bcc,
			String subject, String content, String attachment) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		
		if (address != null)
			intent.putExtra(Intent.EXTRA_EMAIL, address.split(";"));
		else
			intent.putExtra(Intent.EXTRA_EMAIL, address);

		if (cc != null)
			intent.putExtra(Intent.EXTRA_CC, cc.split(";"));
		else
			intent.putExtra(Intent.EXTRA_CC, cc);

		if (bcc != null)
			intent.putExtra(Intent.EXTRA_BCC, bcc.split(";"));
		else
			intent.putExtra(Intent.EXTRA_BCC, bcc);

		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		if(attachment != null)
		{
			File file = new File(attachment);
			if(file != null)
			{
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				if (file.getName().endsWith(".gz")) {
					intent.setType("application/x-gzip");
				} else if (file.getName().endsWith(".txt")) {
					intent.setType("text/plain");
				} else
					intent.setType("application/octet-stream");
			}

		}

		intent.setType("application/octet-stream");
		try
		{
			VenusActivity.appActivity.startActivity(intent);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
}
