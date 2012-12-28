package com.wondertek.video.player;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;

public class PlayerObserver {
	
	public final static String PROVIDER_WD 		=	"wd";
	public final static String PROVIDER_CMMB	=	"cmmb"; 
	
	private String ACTIVE_PROVIDER = null;
	
	private static PlayerObserver instance = null;
	
	private PlayerImpl player = null;
	private VenusActivity venusHandle = null;
	
	private PlayerObserver(VenusActivity va)
	{
		venusHandle = va;
	}
	
	public static PlayerObserver getInstance(VenusActivity va)
	{
		if(instance == null)
		{
			instance = new PlayerObserver(va);
		}
		
		return instance;
	}
	
	/**
	 * {@literal description: After we call PlayerObserver.getInstance(), we must call 'InitObserver' to init it.}
	 * @param provider
	 *         The provider of media player. 
	 * @param 
	 *           
	 * @return 
	 * @see 	
	 * 	[ Something etc. 	]
	 */
	public boolean InitObserver(String provider)
	{
		ACTIVE_PROVIDER = provider;
		Util.Trace("InitObserver ----ACTIVE_PROVIDER--" + ACTIVE_PROVIDER);
		if(PROVIDER_WD.equals(ACTIVE_PROVIDER))
		{
			if(player == null)
			{	
				player = new PlayerWD(venusHandle);
				Util.Trace("InitObserver ----player--" + player);
			}	
		}
		else if(PROVIDER_CMMB.equals(ACTIVE_PROVIDER))
		{
			//TODO
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * {@literal description: We should invoke this function when we uninstall the media-player plugin}
	 * @param 
	 *         
	 * @param 
	 *           
	 * @return 
	 * @see 	
	 * 	[ Something etc. 	]
	 */
	public void DeInitObserver()
	{
		ACTIVE_PROVIDER = null;
		if(player != null)
		{
			player.Destroy();
			player = null;
		}
	}
	
	public String getPlayerProvider()
	{
		return ACTIVE_PROVIDER;
	}
	
	public boolean Create(int nLeft, int nTop, int nWidth, int nHeight)
	{
		Util.Trace("playObserver ----Create--"+nLeft+","+nTop+","+nWidth+","+nHeight);
		return player.Create(nLeft, nTop, nWidth, nHeight);
	}
	
	public boolean Release()
	{
		return player.Release();
	}

	public boolean MoveWindow(int nLeft, int nTop, int mWidth, int nHeight)
	{
		return player.MoveWindow(nLeft, nTop, mWidth, nHeight);
	}
	
	public boolean FullScreen(boolean bFullScreen)
	{
		return player.FullScreen(bFullScreen);
	}

	public boolean Open(String strUrl)
	{
		Util.Trace("Open ----IN--" + strUrl);
		return player.Open(strUrl);
	}
	
	public boolean Play()
	{
		return player.Play();
	}
	
	public boolean Pause()
	{
		Util.Trace("PlayerWD ----Pause");
		return player.Pause();
	}
	
	public boolean Stop()
	{
		return player.Stop();
	}

	public boolean Seek(int nSecond)
	{
		return player.Seek(nSecond);
	}

	public int GetVolume()
	{
		return player.GetVolume();
	}
	
	public int SetVolume(int nVolume)
	{
		return player.SetVolume(nVolume);
	}

	public boolean DrawPicture(short []pixels, int nSize, int []pnWidth, int []pnHeight, int []pbFullScreen, int []pnStatus)
	{
		return player.DrawPicture(pixels, nSize, pnWidth, pnHeight, pbFullScreen, pnStatus);
	}

	public int GetCurTime()
	{
		return player.GetCurTime();
	}
	
	public int GetTotalTime()
	{
		return player.GetTotalTime();
	}
	
	public int GetBufferPercent()
	{
		return player.GetBufferPercent();
	}
	
	public int GetStatus()
	{
		Util.Trace("PlayerWD ----GetStatus--"+player.GetStatus());
		return player.GetStatus();
	}

	public boolean Show(boolean bShow)
	{
		return player.Show(bShow);
	}
		
	public void UpdateSurface()
	{
		player.UpdateSurface();
	}
		
	public int GetPlayerState2(int handle)
	{
		return player.GetPlayerState2(handle);
	}
	
	public boolean BeginShow()
	{
		return player.BeginShow();
	}
	
	public int GetRawPicture(int []rawPixels)
	{
		return player.GetRawPicture(rawPixels);
	}
	
	public boolean EndShow()
	{
		return player.EndShow();
	}

}
