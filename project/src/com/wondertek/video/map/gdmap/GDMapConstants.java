package com.wondertek.video.map.gdmap;

/**
 * 
 * @author yuhongwei
 *
 */
public class GDMapConstants {
    /** Api_Key */
    public static final String GDMAP_API_KEYS = "c2b0f58a6f09cafd1503c06ef08ac7aeb7ddb91ada929925831b67f673bf10ad69195de3c7db4836";
    //add pj
	public static final String GDMAP_CMCC_API_KEYS = "5bcb93cef3bf75163c161bb7cea81b302f3815e5d9935e84bd763cd4b8be14cb7861f1266f659f31";
    
    /** Location Update Min Time */
    public static final long GDMAP_LOCATION_UPDATE_MIN_TIME = 2000;
    /** Location Update Min Distance */
    public static final float GDMAP_LOCATION_UPDATE_MIN_DISTANCE = 10;
    
    /** Handler Message ID */
    public static final int GDMAP_LOCATION_FINISHED = 0x1000;
    public static final int GDMAP_POISEARCH_RESULT = 0x1001;
    public static final int GDMAP_GEOCODER_RESULT = 0x1002;
    public static final int GDMAP_ROUTESEARCH_RESULT = 0x1003;
    public static final int GDMAP_AUTOLOCATION = 0x1004; 
    public static final int GDMAP_SIGNTOMAP = 0x1005;
    public static final int GDMAP_POIDETAIL = 0x1006;
    public static final int GDMAP_POIPRESSED = 0x1007;
    public static final int GDMAP_GUIJIHUIFANG = 0x1008;
    public static final int GDMAP_ERROR = 0xffff;
    
    public static final int GDMAP_GEOCODER_COUNT = 3;
    
    /** Weather type */
    public static final int GDMAP_WEATHER_DUOYUN = 0;
    public static final int GDMAP_WEATHER_QING = 1;
    public static final int GDMAP_WEATHER_YU = 2;
    public static final int GDMAP_WEATHER_LEIYU = 3;
    public static final int GDMAP_WEATHER_WU = 4;
    public static final int GDMAP_WEATHER_XUE = 5;
    
    /** Route Search Mode */
    public static final int GDMAP_DRIVING_Default = 0;
    public static final int GDMAP_DRIVING_LeastDistance = 1;
    public static final int GDMAP_DRIVING_NoFastRoad = 2;
    public static final int GDMAP_DRIVING_SaveMoney  = 3;
    public static final int GDMAP_BUS_Default = 4;
    public static final int GDMAP_BUS_LeastChange = 5;
    public static final int GDMAP_BUS_LeastWalk = 6;
    public static final int GDMAP_BUS_MostComfortable = 7;
    public static final int GDMAP_BUS_SaveMoney = 8;
    
    /**Route Draw Type */
    public static final int GDMAP_BUS_ROUTE = 0;
    public static final int GDMAP_CAR_ROUTE = 1;
    public static final int GDMAP_WALK_ROUTE = 2;
}

