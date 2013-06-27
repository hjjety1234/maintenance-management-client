package com.wondertek.video.player;


public interface PlayerImpl {

	public boolean Create(int nLeft, int nTop, int nWidth, int nHeight);
	public boolean Release();

	public boolean MoveWindow(int nLeft, int nTop, int mWidth, int nHeight);
	public boolean FullScreen(boolean bFullScreen);

	public boolean Open(String strUrl);
	public boolean Play();
	public boolean Pause();
	public boolean Stop();

	public boolean Seek(int nSecond);

	public int GetVolume();
	public int SetVolume(int nVolume);

	public boolean DrawPicture(short []pixels, int nSize, int []pnWidth, int []pnHeight, int []pbFullScreen, int []pnStatus); 

	public int GetCurTime();
	public int GetTotalTime();
	public int GetBufferPercent();
	public int GetStatus();

	public boolean Show(boolean bShow);
		
	public void UpdateSurface();
		
	public int GetPlayerState2(int handle);
	public boolean BeginShow(); 
	public int GetRawPicture(int []rawPixels);
	public boolean EndShow();	
	
	public void Destroy();		//To destroy the media player when we want to DO UPDATE OPERATION.
	
}
