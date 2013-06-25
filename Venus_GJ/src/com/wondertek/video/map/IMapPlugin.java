package com.wondertek.video.map;

import android.view.View;

/**
 * Interface for Map Plugin 
 * @author yuhongwei
 *
 */

public interface IMapPlugin {
    public View getMapView();
    public void start();
    public void stop();
    public void setMapViewRect(int x, int y, int width, int height);
    public void setMapViewVisible(boolean bVisible);
    public void setAutoLocationButton(boolean bEnable);
    public void getCurrentPosition();
    public void signPointOnMap();
    public void showPoiPopView(int latitude, int longitude, String desc, boolean bRemoveCover);
    public void showPoisPopView(String pois, boolean  bRemoveCover);
    public void poiSearch(String key, int latitude, int longitude, int radius);
    public void poiSearch(String key, String city);
    public void geoCode(int latitude, int longitude);
    public void geoCode(String addr);
    public boolean searchRoute(int routeType, String startCityname,String startAddress, int startlatitude, int startlongitude, 
			String endCityname,String endAddress, int endlatitude, int endlongitude);
    public void drawRoute(int routeId);
    public void showWeather(int nlatitude, int nlongitude, int ntype, String title, String desc);
    public void destroyMap();
    public void GetCurrentPositionFuncCalled(boolean isCalled);
}
