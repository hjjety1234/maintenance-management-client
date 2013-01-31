package com.wondertek.video;

//Class declare
public class CBatteryInfo extends Object
{
	/**
	 * 
	 */
	private final VenusActivity CBatteryInfo;
	public boolean isPresent;
	public String technology;
	public int plugged;
	public int scale;
	public int health;
	public int charging;	/*status*/
	public int rawlevel;

	public CBatteryInfo(VenusActivity venusActivity)
	{
		super();
		CBatteryInfo = venusActivity;

		//Refer to the NOTE in MonitorBattery.java
		this.isPresent = false;
		this.technology = "";
		this.plugged = 0x02;
		this.scale = 100;
		this.health = 0x02;
		this.charging = 0;
		this.rawlevel = 50;
	}
}