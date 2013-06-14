//package com.wondertek.video.lotuseed;
//
//import android.os.RemoteException;
//
//import com.lotuseed.android.Lotuseed;
//import com.wondertek.video.Util;
//import com.wondertek.video.VenusActivity;
//
//public class LotuseedObserver {	
//	
//	private static LotuseedObserver instance = null;
//	private VenusActivity venusHandle = null;
//	
//	
//	private LotuseedObserver(VenusActivity va)
//	{
//		venusHandle = va;
//	}
//	
//	public static LotuseedObserver getInstance(VenusActivity va)
//	{
//		if(instance == null)
//		{
//			instance = new LotuseedObserver(va);
//		}
//		
//		return instance;
//	}
//	
//	/*
//	start page
//	*/
//	void javaStart(final String pageName)
//	{
//		Util.Trace("Lotuseed java start service " + pageName);
//		Lotuseed.onPageViewBegin(pageName);
//	}
//	
//	/*
//	stop page
//	*/
//	void javaStop(final String pageName)
//	{
//		Util.Trace("Lotuseed java stop service " + pageName);
//		Lotuseed.onPageViewEnd(pageName);
//	}
//	
//	/*
//	CustomLog
//	*/
//	void javaCustomLog(final String log)
//	{
//		Util.Trace("Lotuseed java CustomLog " + log);
//		Lotuseed.onCustomLog(log);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID);
//	*/
//	void javaOnEvent(String eventID)
//	{
//		Util.Trace("Lotuseed javaOnEvent " + eventID);
//		Lotuseed.onEvent(eventID);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, long count);
//	*/
//	void javaOnEventWithCount(String eventID, int count)
//	{
//		Util.Trace("Lotuseed javaOnEventWithCount " + eventID + " count : " + count);
//		Lotuseed.onEvent(eventID, count);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, String label);
//	*/
//	void javaOnEventWithLabel(String eventID, String label)
//	{
//		Util.Trace("Lotuseed javaOnEventWithLabel " + eventID + " label : " + label);
//		Lotuseed.onEvent(eventID, label);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, String label, long count);
//	*/
//	void javaOnEventWithLabelCount(String eventID, String label, int count)
//	{
//		Util.Trace("Lotuseed javaOnEventWithLabelCount " + eventID + " label : " + label + " count : " + count);
//		Lotuseed.onEvent(eventID, label, count);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, boolean immediately);
//	*/
//	void javaOnEvent2(String eventID, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent2 " + eventID);
//		Lotuseed.onEvent(eventID, immediately);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, long count, boolean immediately);
//	*/
//	void javaOnEvent2WithCount(String eventID, int count, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent2WithCount " + eventID + " count : " + count);
//		Lotuseed.onEvent(eventID, count, immediately);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, String label, boolean immediately)
//	*/
//	void javaOnEvent2WithLabel(String eventID, String label, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent2WithLabel " + eventID + " label : " + label);
//		Lotuseed.onEvent(eventID, label, immediately);
//	}
//	
//	/*
//	Lotuseed.onEvent(String eventID, String label, long count, boolean immediately);
//	*/
//	void javaOnEvent2WithLabelCount(String eventID, String label, int count, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent2WithLabelCount " + eventID + " label : " + label + " count : " + count);
//		Lotuseed.onEvent(eventID, label, immediately);
//	}
//	
//	/*
//	Lotuseed.onEventDuration(String eventID, long duration);
//	*/
//	void javaOnEvent3(String eventID, int duration)
//	{
//		Util.Trace("Lotuseed javaOnEvent3 " + eventID + " duration : " + duration);
//		Lotuseed.onEventDuration(eventID, duration);
//	}
//	
//	/*
//	Lotuseed.onEventDuration(String eventID, String label, long duration);
//	*/
//	void javaOnEvent3WithLabel(String eventID, String label, int duration)
//	{
//		Util.Trace("Lotuseed javaOnEvent3WithLabel " + eventID + " label : " + label + " duration : " + duration);
//		Lotuseed.onEventDuration(eventID, label, duration);
//	}
//	
//	/*
//	Lotuseed.onEventDuration(String eventID, long duration, boolean immediately);
//	*/
//	void javaOnEvent3Immediately(String eventID, int duration, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent3Immediately " + eventID + " duration : " + duration);
//		Lotuseed.onEventDuration(eventID, duration, immediately);
//	}
//	
//	/*
//	Lotuseed.onEventDuration(String eventID, String label, long duration, boolean immediately);
//	*/
//	void javaOnEvent3ImmediatelyWithLabel(String eventID, String label, int duration, boolean immediately)
//	{
//		Util.Trace("Lotuseed javaOnEvent3ImmediatelyWithLabel " + eventID + " label : " + label + " duration : " + duration);
//		Lotuseed.onEventDuration(eventID, label, duration, immediately);
//	}
//}
