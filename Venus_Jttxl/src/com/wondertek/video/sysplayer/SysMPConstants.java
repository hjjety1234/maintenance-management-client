package com.wondertek.video.sysplayer;

public class SysMPConstants {
	// ==============================================
	//   player status
	// ==============================================
	/** 播放错误 */
	public static final int STATE_ERROR = 0;
	/** 播放器初始化 */
	public static final int STATE_INIT = 1;
	/** 正在播放 */
	public static final int STATE_PLAYING = 2;
	/** 播放暂停 */
	public static final int STATE_PAUSE = 3;
	/** 缓冲中 */
	public static final int STATE_BUFFER = 4;
	/** 就绪状态 */
	public static final int STATE_PREPARE = 5;
	/** 停止状态 */
	public static final int STATE_STOPED = 6;
	/** 结束状态 */
	public static final int STATE_CLOSED = 7;
	
	//  ============================================
	//  Source Type
	// ============================================
    /**    无播放源  **/
	public static final int NONE_SOURCE = 0;
	/**    本地源   **/
	public static final int LOCAL_SOURCE = 1;
	/**    网络点播源  **/
	public static final int NET_AUDIO_SOURCE = 2;
	/**   网络直播源  **/
	public static final int NET_LIVING_SOURCE = 3;
}
