package com.wondertek.video.update;

import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;
import com.wondertek.video.download.QSEngine;

public class UpdateMan {

	private static UpdateMan instance = null;


	private UpdateAPKMan apkMan = null;

	private UpdateMan(VenusActivity act)
	{
		apkMan = new UpdateAPKMan();
	}

	public static UpdateMan getInstance(VenusActivity activity)
	{
		if(instance == null)
		{
			instance = new UpdateMan(activity);
		}
		return instance;
	}

	public boolean checkResUpdate()
	{

		return false;
	}

	public void downLoadVenusZip()
	{
		apkMan.downLoadVenusZip();
	}

	class UpdateAPKMan {


		public void downLoadVenusZip()
		{
			VenusApplication.getInstance();
			QSEngine.getInstance(VenusActivity.appActivity).createDLTask(
					Config.getUpdateServer() + Config.getUpdateResName(),
					VenusApplication.appAbsPath,
					Config.getUpdateResName());
		}

	}
}
