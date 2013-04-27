/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wondertek.video.msgpush;

import com.wondertek.video.VenusApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

/**
 * Activity for displaying the notification details view.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

	public NotificationDetailsActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPrefs = this.getSharedPreferences(
				MsgPushManager.MSG_PUSH_PREFS, Context.MODE_PRIVATE);
		/*
		 * String callbackActivityPackageName = sharedPrefs.getString(
		 * Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, ""); String
		 * callbackActivityClassName = sharedPrefs.getString(
		 * Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
		 */
		Editor editor = sharedPrefs.edit();

		Intent intent = getIntent();
		String notificationId = intent.getStringExtra("NOTIFICATION_ID");
		editor.putString("NOTIFICATION_ID", notificationId);
		String notificationTitle = intent.getStringExtra("NOTIFICATION_TITLE");
		editor.putString("NOTIFICATION_TITLE", notificationTitle);
		String notificationMessage = intent
				.getStringExtra("NOTIFICATION_MESSAGE");
		editor.putString("NOTIFICATION_MESSAGE", notificationMessage);
		String notificationUri = intent.getStringExtra("NOTIFICATION_URI");
		editor.putString("NOTIFICATION_URI", notificationUri);
		editor.commit();
		MsgPushManager.setNotificationMessageUpdated();

		/*
		 * Intent nIntent = new Intent().setClassName(
		 * callbackActivityPackageName, callbackActivityClassName);
		 * nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * nIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 * nIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 */
		// VenusApplication.startAppActivity(true);
		VenusApplication.startAppFakeActivity();
		NotificationDetailsActivity.this.finish();
	}
}
