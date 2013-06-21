package com.wondertek.video.map;

import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.map.bdmap.BDMapManager;
import com.wondertek.video.map.gdmap.GDMapManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author yuhongwei
 *
 */
public class MapPluginMgr {
    private static final String TAG = "MapPluginMgr";
    private static final int MAP_PLUGIN_GDMAP = 1;
    private static final int MAP_PLUGIN_BDMAP =2;
    private static final int MAP_PLUGIN_TYPE = MAP_PLUGIN_GDMAP;
    private static MapPluginMgr instance = null;
    private Context mContext;
    private IMapPlugin map;
    
    static {
    	System.load(VenusApplication.appAbsPath + "/lib2/mapview/libmapview.so");
    }
    
    private MapPluginMgr(Context cxt) {
    	mContext = cxt;
    	switch (MAP_PLUGIN_TYPE) {
			case MAP_PLUGIN_GDMAP :
				map = GDMapManager.getInstance(mContext);
				break;
			case MAP_PLUGIN_BDMAP :
				map = BDMapManager.getInstance(mContext);
				break;
			default :
				map = null;
				break;
		}
    }
    
    public static MapPluginMgr getInstance(Context cxt) {
    	if (instance == null) {
    		instance = new MapPluginMgr(cxt);
    	}
    	return instance;
    }
    
    public static MapPluginMgr getInstance() {
    	return instance;
    }
    
    public boolean javaSetMapViewRect(int x, int y, int w, int h) {
    	if (map != null) {
    		map.setMapViewRect(x, y, w, h);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean javaShowMapView(boolean bShow) {
    	if (map != null) {
    		map.setMapViewVisible(bShow);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean javaShowLocationButton(boolean bShow) {
    	if (map != null) {
    		map.setAutoLocationButton(bShow);
    		return true;
    	} else {
    		return false;
    	}
    }
    
	public boolean javaSignPointToMap() {
		if (map != null) {
			map.signPointOnMap();
			return true;
		} else {
			return false;
		}
	}
	
	public void javaSetMapViewCenter(int latitude, int longitude, String desc, boolean bRemoveCover) {
		if (map != null) {
			map.showPoiPopView(latitude, longitude, desc, bRemoveCover);
		}
	}
	public void javaStartGetCurrentPosition() {
		if (map != null) {
			map.getCurrentPosition();
		}
	}
	
	public boolean javaStartSearchNearBy(String key, int latitude, int longitude, int radius) {
        if (map != null) {
        	map.poiSearch(key, latitude, longitude, radius);
        	return true;
        } else {
        	return false;
        }
	}
    
	public boolean javaPoiSearchInCity(String cityName,String address) {
		if (map != null) {
			map.poiSearch(address,  cityName);
			return true;
		} else {
			return false;
		}
	}
    
	public boolean javaStartreverseGeocode(int latitude, int longitude) {
		if (map != null) {
			map.geoCode(latitude, longitude);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean javaSearchRoute(int routeType, String startCityname,String startAddress, int startlatitude, int startlongitude, 
			String endCityname,String endAddress, int endlatitude, int endlongitude) {
		if (map != null) {
			return map.searchRoute(routeType, startCityname, startAddress, startlatitude, startlongitude, 
					endCityname, endAddress, endlatitude, endlongitude);
		} else {
			return false;
		}
	}
    
	public boolean javaDrawRoute(int routeID){
        if (map != null) {
            map.drawRoute(routeID);
        	return true;
        } else {
        	return false;
        }
	}
    
	public boolean javaShowWeatherDailog(int nlatitude, int nlongitude, int ntype, String title, String desc) {
		if (map != null) {
            map.showWeather(nlatitude, nlongitude, ntype, title, desc);
        	return true;
        } else {
        	return false;
        }
	}
    
	public boolean javaShowPoisOnMap(String jsonInfo, boolean bRemoveCover) {
		if (map != null) {
			map.showPoisPopView(jsonInfo, bRemoveCover);
            return true;
		} else {
			return false;
		}
	}
    
	public View getMapView() {
		if (map != null) {
			return map.getMapView();
		}
        return null;
	}
    
	public void start() {
		if (map != null) {
			map.start();
		}
	}
	
	public void stop() {
		if (map != null) {
			map.stop();
		}
	}
    
	public void destroyMap() {
		if (map != null) {
			map.destroyMap();
		}
	}

	public native void nativeSearchCallback(String strResult);
	public native void nativeReverseCallback(String strResult);
	public native void nativeCurrentPositionCallback(String strResult);
	public native void nativeSendMapPoint(String strResult);
	public native void nativeRoutesCallback(String strResult);
	public native void nativePoiItemDetailCallback(String strResult);
    public native void nativePoiPopViewPressedCallback(String strResult);
    
}
