package com.wondertek.video.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class CalendarObserver {
	public static final String TAG = "CalendarObserver";
	public VenusActivity venusHandle;
	private static CalendarObserver instance = null;

	private static String calanderURL = "";     
	private static String calanderEventURL = "";     
	private static String calanderRemiderURL = "";     

	static{     
		if(Integer.parseInt(Build.VERSION.SDK) >= 8){     
			calanderURL = "content://com.android.calendar/calendars";     
			calanderEventURL = "content://com.android.calendar/events";     
			calanderRemiderURL = "content://com.android.calendar/reminders";     

		}else{     
			calanderURL = "content://calendar/calendars";     
			calanderEventURL = "content://calendar/events";     
			calanderRemiderURL = "content://calendar/reminders";             
		}     
	} 

	private CalendarObserver(VenusActivity va) {
		venusHandle = va;
	}

	public static CalendarObserver getInstance(VenusActivity va) {
		if (instance == null) {
			instance = new CalendarObserver(va);
		}
		return instance;
	}

	public int javaAddEvent(String eventName, String eventDescription, 
			int eventBeginDate0,int eventBeginDate1,int eventBeginDate2,
			int eventBeginTime0,int eventBeginTime1, 
			int eventEndDate0,int eventEndDate1,int eventEndDate2,
			int eventEndTime0,int eventEndTime1,
			int reminderMinutus)
	{
		long startMillis = 0;
		long endMillis = 0;
		String calId = "";

		Cursor userCursor = VenusActivity.appActivity.getContentResolver().query(Uri.parse(calanderURL), null,null, null, null); 
		if(userCursor == null){
			return -1;
		}

		if(userCursor.getCount() > 0){ 
			userCursor.moveToFirst();     
			calId = userCursor.getString(userCursor.getColumnIndex("_id"));            
		}

		if(calId.trim().equals("") || calId == null)
			calId = "87654321";
		
		Util.Trace(TAG  + "calId="+calId);

		Calendar beginTime = Calendar.getInstance();
		beginTime.set(eventBeginDate0,eventBeginDate1-1,eventBeginDate2,eventBeginTime0,eventBeginTime1);
		startMillis = beginTime.getTimeInMillis();

		Calendar endTime = Calendar.getInstance();
		endTime.set(eventEndDate0,eventEndDate1-1,eventEndDate2,eventEndTime0,eventEndTime1);
		endMillis = endTime.getTimeInMillis();

		VenusActivity.appActivity.getContentResolver();
		ContentValues event = new ContentValues();
		event.put("dtstart", startMillis);
		event.put("dtend", endMillis);
		event.put("title", eventName);
		event.put("description", eventDescription);
		event.put("eventTimezone", "GMT+8");		
		event.put("hasAlarm",1);
		event.put("calendar_id",calId); 

		Uri newEvent = VenusActivity.appActivity.getContentResolver().insert(Uri.parse(calanderEventURL), event);   

		long id = Long.parseLong(newEvent.getLastPathSegment());   
		Util.Trace(TAG  +"getLastPathSegment="+newEvent.getLastPathSegment());
		Util.Trace(TAG  +"id="+id);
		ContentValues values = new ContentValues();
		values.put( "event_id", id );

		values.put( "minutes", reminderMinutus );

		VenusActivity.appActivity.getContentResolver().insert(Uri.parse(calanderRemiderURL), values);

		return (int)id;
	}

	public boolean javaDeleteEvent(int nId)
	{
		String calId = "";
		boolean bRet=false;

		ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
		Cursor userCursor = resolver.query(Uri.parse(calanderURL), null,null, null, null);
		if(userCursor == null)
			return false;
		if(userCursor.getCount() > 0){ 
			userCursor.moveToFirst();     
			calId = userCursor.getString(userCursor.getColumnIndex("_id"));            
		}
		Util.Trace(TAG  + "javaDeleteEvent calId="+calId);
		Cursor cursor;

		Uri eventsUri = Uri.parse(calanderEventURL);

		if (android.os.Build.VERSION.SDK_INT <= 7) { //up-to Android 2.1 
			cursor = resolver.query(eventsUri, new String[]{"_id"}, "Calendars._id=" + calId, null, null);
		} else { 
			cursor = resolver.query(eventsUri, new String[]{"_id"}, "calendar_id=" + calId, null, null);
		}

		while(cursor.moveToNext()) {
			long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
			Util.Trace(TAG  + "javaDeleteEvent event_id="+eventId);
			if((int)eventId == nId)
			{
				int nums = resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
				if(nums>0)
					bRet = true;
			}
		}
		cursor.close();
		return bRet;
	}

	public String javaGetEvent()
	{
		Cursor cursor= VenusActivity.appActivity.getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);
		if(cursor==null)
			return "{}";
		cursor.moveToFirst();
		String nstr="{";

		// fetching calendars id
		if(cursor.getCount()>0)
		{
			if(!cursor.isAfterLast())
			{
				ContentResolver resolver = VenusActivity.appActivity.getContentResolver();
				Cursor eventCursor = resolver.query(Uri.parse(calanderEventURL), null, null, null, null);     
				nstr+="\"data\":[";
				while(eventCursor.moveToNext()) {
					long eventId = eventCursor.getLong(eventCursor.getColumnIndex("_id"));
					nstr +="{\"id\":\"" +eventId+"\",";
					String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
					nstr +="\"title\":\"" +eventTitle+"\",";
					String eventDescription = eventCursor.getString(eventCursor.getColumnIndex("description"));
					nstr +="\"description\":\"" +eventDescription+"\",";
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
					String eventDtstart = formatter.format(eventCursor.getLong(eventCursor.getColumnIndex("dtstart")));
					nstr +="\"dtstart\":\"" +eventDtstart+"\",";
					String eventDtend = formatter.format(eventCursor.getLong(eventCursor.getColumnIndex("dtend")));
					nstr +="\"dtend\":\"" +eventDtend+"\"}";
					if(!eventCursor.isLast())
						nstr +=",";
					Util.Trace(TAG  + "eventTitle="+eventTitle);
					Util.Trace(TAG  + "eventDescription="+eventDescription);
				}
				nstr +="]";
			}
		}
		nstr +="}";
		Util.Trace(TAG  + "nstr="+nstr);
		return nstr;
	}
}
