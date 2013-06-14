package com.wondertek.video.download;

public class QSDownUtils {
	private static final String GAP = ":";
	
	public static String changeSecToHMS(int second){
		int min = second / 60;
		int hour = min / 60;
		int sec = second % 60;
		return hour + GAP + min + GAP + sec + "S";
	}
	
	public static String chageSecToHMSForMat(int second){
		String timeHMS = changeSecToHMS(second);
		return timeHMS;
	}
}
