package com.wondertek.video.caller;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

public class CallLogWriter {
	private static final String TAG = "CallLogWriter";
	private Context mContext;

	public CallLogWriter(Context context) {
		mContext = context;
	}

	public void writeCallLog() {
		Log.d(TAG, ">>>writeCallLog<<<");
		DbHelper helper = new DbHelper(mContext);
		if (helper.getHisCount() > 0) {
			Log.d(TAG, "[writeCallLog] not first time startup, do noting...");
		} else {
			Log.d(TAG,
					"[writeCallLog] first time startup, trying to read call log...");
			// write to sqlite database
			Uri uri = CallLog.Calls.CONTENT_URI;
			Long l = System.currentTimeMillis() - 3 * 24 * 3600 * 1000; 
			Cursor cursor = ((Activity) mContext).managedQuery(uri,
					new String[] { "name", "number", "date", "type", },
					CallLog.Calls.DATE + " > " + l, null, null);
			while (cursor.moveToNext()) {
				// get name
				String name = cursor.getString(cursor.getColumnIndex("name"));
				Log.d(TAG, "[writeCallLog] name: " + name);
				// get number
				String number = cursor.getString(cursor
						.getColumnIndex("number"));
				Log.d(TAG, "[writeCallLog] number: " + number);
				// format call type
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				Log.d(TAG, "[writeCallLog] type: " + type);
				String strType = "0";
				if (type == CallLog.Calls.INCOMING_TYPE) {
					strType = "0"; // incoming call
				} else if (type == CallLog.Calls.OUTGOING_TYPE) {
					strType = "1"; // outgoing call
				} else if (type == CallLog.Calls.MISSED_TYPE) {
					strType = "0"; // incoming call
				} else {
					//	Log.w(TAG, "[writeCallLog] unknown call log type: " + type);
					strType  = "0"; // 4 is voice mail, 5 is rejected, 7 is rejected lists 
					continue;
				}
				// format call date
				long date = cursor.getLong(cursor.getColumnIndex("date"));
				Log.d(TAG, "[writeCallLog] date: " + date);
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
				String dateString = formatter.format(date);
				Log.d(TAG, "[writeCallLog] format date:" + dateString);
				// query employee info by number
				String id = helper.getEmployeeId(number);
				if (id != null) {
					// write to database
					helper.recordCallHistory(number, strType, id, dateString);
				} else {
					Log.w(TAG, "[writeCallLog] can't find number " + number
							+ " employee's info in database.");
				}
			}
		}
	}
}
