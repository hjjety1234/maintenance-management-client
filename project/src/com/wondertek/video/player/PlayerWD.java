package com.wondertek.video.player;

import android.view.SurfaceHolder;

import com.wonder.media.DefaultPlayer;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.VenusApplication;

public class PlayerWD implements PlayerImpl{
	
	private VenusActivity venusHandle = null;

	private	boolean m_bPlayerInited;
	private	boolean m_bFullScreen;

	private int []m_rectDisplay;		//x, y, w, h
	private int []m_rectFullScreen;		//x, y, w, h
	
	private DefaultPlayer mMediaplayer;
	
	private	boolean m_bInitPlayer;
	
	
	public PlayerWD(VenusActivity va)
	{
		venusHandle = va;
		m_bPlayerInited	= false;
		m_bFullScreen	= false;

		m_rectDisplay 		= new int[4];
		m_rectDisplay[0] = 0;
		m_rectDisplay[1] = 0;
		m_rectDisplay[2] = 0;
		m_rectDisplay[3] = 0;
		
		m_rectFullScreen	= new int[4];
		m_rectFullScreen[0] = 0;
		m_rectFullScreen[1] = 0;
		m_rectFullScreen[2] = 0;
		m_rectFullScreen[3] = 0;
		
		//Package pack = R.class.getPackage();
		//String playerWorkspace = "/data/data/" + pack.getName() + "/lib2/mediaplayer/";
		//String playerWorkspace = VenusApplication.appAbsPath;
		//mMediaplayer = DefaultPlayer.getInstance(playerWorkspace, false, 0);
		//WonderPlayer.getInstance(playerWorkspace, false, 0);
		
		m_bInitPlayer	= false;
	}
	
	public boolean Create(int nLeft, int nTop, int nWidth, int nHeight)
	{
		m_bFullScreen = false;
		m_rectDisplay[0] = nLeft;
		m_rectDisplay[1] = nTop;
		m_rectDisplay[2] = nWidth;
		m_rectDisplay[3] = nHeight;
		venusHandle.javaSetMediaplayerType(6);
		String playerWorkspace = VenusApplication.appAbsPath;
		mMediaplayer = DefaultPlayer.getInstance(playerWorkspace, false, 0);
		Util.Trace("PlayerWD ----Create"+nLeft+","+nTop+","+nWidth+","+nHeight);
		venusHandle.JavaCreateElement(VenusActivity.Enum_CmdType_MP_Create, "PlayerWD::Create", m_rectDisplay[2], m_rectDisplay[3], m_rectDisplay[0], m_rectDisplay[1]);
		Show(true);
		m_bPlayerInited = true;
		needUpdateSurface = false;
		return true;
	}
	
	public boolean Release()
	{
		if (m_bPlayerInited) {
			Util.Trace("PlayerWD ----Release" + m_bPlayerInited);
			mMediaplayer.stop();
			mMediaplayer.close();
			m_bPlayerInited = false;
		}

		return true;
	}

	public boolean MoveWindow(int nLeft, int nTop, int mWidth, int nHeight)
	{
		return true;
	}
	
	public boolean FullScreen(boolean bFullScreen)
	{
		if(bFullScreen == true)
		{
			venusHandle.JavaCreateElement(VenusActivity.Enum_CmdType_MP_FullScreen, "PlayerWD::FullScreen", 800, 480, 0, 0);
		}
		else
		{
			venusHandle.JavaCreateElement(VenusActivity.Enum_CmdType_MP_Normal, "PlayerWD::NO FullScreen", m_rectDisplay[2], m_rectDisplay[3], m_rectDisplay[0], m_rectDisplay[1]);
		}

		return true;
	}
	
	private boolean needUpdateSurface = false;
	private String m_strUrl;
	public boolean Open(String strUrl)
	{		
		playinit();

		
		mMediaplayer.updateSurface(venusHandle.getSurfaceView().getHolder(), 
				m_rectDisplay[0], m_rectDisplay[1], m_rectDisplay[2], m_rectDisplay[3]);
		
		/*if (needUpdateSurface) {
			//updatesurface();
		}
		needUpdateSurface = true;*/
		Util.Trace("PlayerWD ----Open"+strUrl);
		Show(true);
		
		//strUrl = "http://192.168.2.52/Wonder_Girls_Nobody720.mp4";
		//strUrl = "rtsp://192.168.2.52:554/10000000000000000000000005107323.3gp";
		//Log.d("PlayerWD", "PlayerWD::Open URL="+strUrl);
		
		strUrl.replace('\\', '/');
		
		m_strUrl = strUrl;
		
		//mMediaplayer.start(strUrl, 0);

		return true;
	}
	
	public boolean Play()
	{
		Show(true);
		if(venusHandle.getInstance().javaGetSysState() != venusHandle.SYS_WINDOW_MIN2MAX)
		{
			Util.Trace("PlayerWD ----Play");
			mMediaplayer.play();
		}	
		return true;
	}
	
	public boolean Pause()
	{
		Util.Trace("PlayerWD ----Pause");
		mMediaplayer.pause();
		return true;
	}
	
	public boolean Stop()
	{
		Util.Trace("PlayerWD ----Stop");
		mMediaplayer.stop();
		mMediaplayer.close();
		return true;
	}

	public boolean Seek(int nSecond)
	{
		mMediaplayer.seekTo(nSecond);
		return true;
	}

	public int GetVolume()
	{
		return 0;
	}
	
	public int SetVolume(int nVolume)
	{
		return 0;
	}

	public boolean DrawPicture(short []pixels, int nSize, int []pnWidth, int []pnHeight, int []pbFullScreen, int []pnStatus)
	{
		return false;
	}

	public int GetCurTime()
	{
		return mMediaplayer.getPassedSec();
	}
	
	public int GetTotalTime()
	{
		return mMediaplayer.getDuration();
	}
	
	public int GetBufferPercent()
	{
		Util.Trace("PlayerWD ----GetBufferPercent:" + mMediaplayer.getBufferPercentage());
		return mMediaplayer.getBufferPercentage();
	}
	
	public int GetStatus()
	{
		Util.Trace("PlayerWD ----GetStatus:" + mMediaplayer.getPlayerStatus());
		return mMediaplayer.getPlayerStatus();
	}

	public boolean Show(boolean bShow)
	{
		if(bShow == true)
		{
			venusHandle.JavaCreateElement(VenusActivity.Enum_CmdType_MP_Visble, "PlayerWD::Show", m_rectDisplay[2], m_rectDisplay[3], m_rectDisplay[0], m_rectDisplay[1]);
		}
		else
		{
			venusHandle.JavaCreateElement(VenusActivity.Enum_CmdType_MP_Hidden, "PlayerWD::Show", 1, 1, 0, 0);
		}

		return true;
	}
	
	private boolean playinit() {
		Util.Trace("PlayerWD ----playinit--in");
		String proxy = "http://10.0.0.172:80";
		//String proxy = null;
		
		int nFirmwareVersion = 0;
		int nRenderType		= 0;	/*render type*/
		int nVideoFormat	= 4;	/*video format*/
		int nColorTab		= 0;	/*color format*/
		int nAndroidVer		= 23;	/*android version*/

		m_bInitPlayer = true;
		
		return mMediaplayer.init(venusHandle.getSurfaceView(), null, proxy, nFirmwareVersion, nRenderType, nVideoFormat, nColorTab, nAndroidVer);

	}
	
	public void UpdateSurface()
	{
		Util.Trace("PlayerWD ----UpdateSurface:" + m_rectDisplay[0] + "," + m_rectDisplay[1] + "," + m_rectDisplay[2] + "," + m_rectDisplay[3]);
	    if (m_bInitPlayer) {
	    	//venusHandle.getSurfaceView().getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    	if(m_bFullScreen)
	    	{
	    		mMediaplayer.updateSurface(venusHandle.getSurfaceView().getHolder(), 
	    				m_rectFullScreen[0], m_rectFullScreen[1], m_rectFullScreen[2], m_rectFullScreen[3]);
	    	}
	    	else
	    	{
	    		mMediaplayer.updateSurface(venusHandle.getSurfaceView().getHolder(), 
	    				m_rectDisplay[0], m_rectDisplay[1], m_rectDisplay[2], m_rectDisplay[3]);
	    		
	    		Util.Trace("PlayerWD ----updateSurface---IN");
	    		if(venusHandle.getInstance().javaGetSysState() == venusHandle.SYS_WINDOW_MIN2MAX)
	    		{
	    			Util.Trace("PlayerWD ----updateSurface--SYS_WINDOW_MIN2MAX-IN");
	    			//int nTime = mMediaplayer.getPassedSec();
	    			//mMediaplayer.start(m_strUrl, nTime);
	    			mMediaplayer.play();
	    			venusHandle.getInstance().javaSetSysState(venusHandle.SYS_WINDOW_NORMAL);
	    		}
	    		
	    		if(!needUpdateSurface)
	    		{	
	    			Util.Trace("PlayerWD ----start---IN");
	    			mMediaplayer.start(m_strUrl, 0);
	    			needUpdateSurface = true;
	    		}	
	    	}
	    	
	    }
	    else {
	    	Util.Trace("PlayerWD ----UpdateSurface---playinit--in");
			if( playinit() )
				m_bInitPlayer = true;
	    }
	}
		
	public int GetPlayerState2(int handle)
	{
		return 0;
	}
	
	public boolean BeginShow()
	{
		return false;
	}
	
	public int GetRawPicture(int []rawPixels)
	{
		return 0;
	}
	
	public boolean EndShow()
	{
		return false;
	}

	
	
	
	
	public void Destroy()
	{
		mMediaplayer.destroy();
	}
}
