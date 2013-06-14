package com.wondertek.video.map.bdmap;

/**
 * 
 * @author yuhongwei
 *
 */
public class BDMapConstants {
    /** API Key */
	public static final String BDMAP_API_KEYS = "679749BA00AF67C35F02506C6A917EC6BB294ACC";

	/** Handler Message ID */
    public static final int BDMAP_LOCATION_FINISHED = 0x1000;
    public static final int BDMAP_POISEARCH_RESULT = 0x1001;
    public static final int BDMAP_GEOCODER_RESULT = 0x1002;
    public static final int BDMAP_ROUTESEARCH_RESULT = 0x1003;
    public static final int BDMAP_AUTOLOCATION = 0x1004; 
    public static final int BDMAP_SIGNTOMAP = 0x1005;
    public static final int BDMAP_POIDETAIL = 0x1006;
    public static final int BDMAP_POIPRESSED = 0x1007;
    public static final int BDMAP_ERROR = 0xffff;
    
    /** Search Route Type */
    public static final int BDMAP_ROUTE_SearchType_Driving = 1;
    public static final int BDMAP_ROUTE_SearchType_Bus = 2;
    public static final int BDMAP_ROUTE_SearchType_Walking = 3;
}
