package com.wondertek.video.monitor;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class MonitorContacts extends MonitorBase{
	private static ContentObserver mContactsObserver = null;
	
	public MonitorContacts()
	{
		super();
	}
	
	public MonitorContacts(Object h)
	{
		super();
		mHandle = h;
	}
	
	@Override  
	public void onReceive(Context context, Intent intent)
	{
		Util.Trace("MonitorContacts:: " + intent.getAction());
	}
	
	@Override
	public boolean Init(VenusActivity h) {
		this.mHandle = h;
		mContactsObserver = new ContentObserver(new Handler()){
			public void onChange (boolean selfChange)
			{
				Util.SetContactsChange(true);
			}
		};
		if(mContactsObserver != null)
		{
			if(Util.GetSDK() != Util.SDK_ANDROID_15 && Util.GetSDK() != Util.SDK_ANDROID_16 && Util.GetSDK() != Util.SDK_OMS_15 && Util.GetSDK() != Util.SDK_OMS_16)
			{
				try {
					Uri uri = null;
					Class<?> clazz = Class.forName("android.provider.ContactsContract$Contacts");
					Field f1 = clazz.getField("CONTENT_URI");
					uri = (Uri) (f1.get(null));
					VenusActivity.getInstance().appActivity.getContentResolver().registerContentObserver(uri, true, mContactsObserver);
				} catch (Exception e) {
					Util.Trace(e.toString());
					e.printStackTrace();
				}

			}
			else
			{
				Uri uri = Uri.parse("content://contacts/people/");
				VenusActivity.getInstance().appActivity.getContentResolver().registerContentObserver(uri, true, mContactsObserver);
			}
		}
		return true;
	}
	
	@Override
	public boolean DeInit(VenusActivity h)
	{
		if(mContactsObserver != null)
		{
			VenusActivity.getInstance().appActivity.getContentResolver().unregisterContentObserver(mContactsObserver);
			mContactsObserver = null;
		}
		return true;
	}
	
	@Override
	public IntentFilter getIntentFilter()
	{
		return null;
	}
	

	
}
