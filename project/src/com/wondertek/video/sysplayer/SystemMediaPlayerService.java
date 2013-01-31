package com.wondertek.video.sysplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author yuhongwei
 *
 */

public class SystemMediaPlayerService extends Service
		implements OnErrorListener, OnBufferingUpdateListener, OnInfoListener,
			OnCompletionListener, OnPreparedListener, OnSeekCompleteListener {
	
	private static final String TAG = "SystemMediaPlayerService";
	private MediaPlayer mediaPlayer = null;
	private int playerStatus;
	private int playSourceType = SysMPConstants.NONE_SOURCE;
	private final IBinder serviceBinder = new SystemMediaPlayerServiceBinder();
	
	public class SystemMediaPlayerServiceBinder extends Binder {
		SystemMediaPlayerService getService() {
			return SystemMediaPlayerService.this;
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onCreate");
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnInfoListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			playerStatus = SysMPConstants.STATE_INIT;
            SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
		super.onCreate();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDestroy");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
		playerStatus = SysMPConstants.STATE_CLOSED;
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (mediaPlayer.isLooping()) {
			mediaPlayer.start();
			playerStatus = SysMPConstants.STATE_PLAYING;
            SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		} else {
			playerStatus = SysMPConstants.STATE_CLOSED;
            SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.start();
		playerStatus = SysMPConstants.STATE_PLAYING;
        SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
	}

	
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		Toast.makeText(this, TAG + ">>onError<<", Toast.LENGTH_LONG).show();
		playerStatus = SysMPConstants.STATE_ERROR;
        SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		switch (what) {
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				Log.e(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:" + extra);
				break;
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				Log.e(TAG, "MEDIA_ERROR_SERVER_DIED:" + extra);
				break;
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				Log.e(TAG, "MEDIA_INFO_UNKNOWN:" + extra);
				break;
		}
		return false;
	}
	
	
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
        /*
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
				Log.i(TAG, "MEDIA_INFO_BAD_INTERLEAVING:" + extra);
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				Log.i(TAG, "MEDIA_INFO_BUFFERING_END:" + extra);
				playerStatus = SysMPConstants.STATE_PREPARE;
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				Log.i(TAG, "MEDIA_INFO_BUFFERING_START:" + extra);
				playerStatus = SysMPConstants.STATE_BUFFER;
				break;
			case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
				Log.i(TAG, "MEDIA_INFO_METADATA_UPDATE:" + extra);
				break;
			case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
				Log.i(TAG, "MEDIA_INFO_NOT_SEEKABLE:" + extra);
				break;
			case MediaPlayer.MEDIA_INFO_UNKNOWN:
				Log.i(TAG, "MEDIA_INFO_UNKNOWN:" + extra);
				break;
			case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
				Log.i(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:" + extra);
				break;
			default:
				break;	
		}
		*/
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "onBind");
		// TODO Auto-generated method stub
		return serviceBinder;
	}

	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "onUnbind");
		// TODO Auto-generated method stub
		return true;
	}


	public int getSysMPStatus() {
		Log.v(TAG, ">>>>>>>getSysMPStatus<<<<<<<<");
		return playerStatus;
	}


	public boolean open(String filePath) {
		Log.v(TAG, ">>>>>>>open<<<<<<<<");
		Log.v(TAG, "open Path :" + filePath);
		if (!"".equals(filePath) && mediaPlayer != null) {
			mediaPlayer.reset();
			playerStatus = SysMPConstants.STATE_INIT;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
			if (filePath.startsWith("http://"))
				return openUrl(filePath);
			else
				return openPath(filePath);
		}
		return false;
	}


	public void start() {
		Log.v(TAG, ">>>>>>>start<<<<<<<<");
		if (playerStatus == SysMPConstants.STATE_STOPED) {
			if (playSourceType == SysMPConstants.LOCAL_SOURCE) {
				try {
					mediaPlayer.prepare();
					mediaPlayer.seekTo(0);
					mediaPlayer.start();
					playerStatus = SysMPConstants.STATE_PLAYING;
					SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					playerStatus = SysMPConstants.STATE_ERROR;
					SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
				}
			} else {
				try {
					mediaPlayer.prepareAsync();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					playerStatus = SysMPConstants.STATE_ERROR;
					SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
				}
			}
		} else if (playerStatus == SysMPConstants.STATE_PAUSE) {
			mediaPlayer.start();
			playerStatus = SysMPConstants.STATE_PLAYING;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
	}


	public void pause() {
		Log.v(TAG, ">>>>>>>pause<<<<<<<<");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			playerStatus = SysMPConstants.STATE_PAUSE;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
	}


	public void stop() {
		Log.v(TAG, ">>>>>>>stop<<<<<<<<");
		mediaPlayer.stop();
		playerStatus = SysMPConstants.STATE_STOPED;
		SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
	}

    public void setVolume(int vol) {
        float v = (float)vol / 100.0f;
    	mediaPlayer.setVolume(v, v);
    }

	public int duration() {
		Log.v(TAG, ">>>>>>>duration<<<<<<<<");
		return mediaPlayer.getDuration();
	}


	public int seekTo(int msec) {
		Log.v(TAG, ">>>>>>>seekTo<<<<<<<<");
		if (playerStatus == SysMPConstants.STATE_STOPED)
			return 0;
		if (msec > mediaPlayer.getDuration())
			return mediaPlayer.getCurrentPosition();
		mediaPlayer.seekTo(msec);
		return msec;
	}


	public boolean isPlaying() {
		Log.v(TAG, ">>>>>>>isPlaying<<<<<<<<");
		return mediaPlayer.isPlaying();
	}


	public boolean isLooping() {
		Log.v(TAG, ">>>>>>>isLooping<<<<<<<<");
		return mediaPlayer.isLooping();
	}


	public int getCurPosition() {
		Log.v(TAG, ">>>>>>>getCurPosition<<<<<<<<");
		return mediaPlayer.getCurrentPosition();
	}


	public void setLooping(boolean isloop) {
		Log.v(TAG, ">>>>>>>setLooping<<<<<<<<");
		mediaPlayer.setLooping(isloop);
	}
	
	
	public int getPlaySourceType() {
		Log.v(TAG, ">>>>>>>getPlaySourceType<<<<<<<<");
		return playSourceType;
	}


	private boolean openUrl(String url) {
		try {
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
			playSourceType = SysMPConstants.NET_AUDIO_SOURCE;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			playerStatus = SysMPConstants.STATE_ERROR;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
		return true;
	}
	
	private boolean openPath(String path) {
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.start();
			playerStatus = SysMPConstants.STATE_PLAYING;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
			playSourceType = SysMPConstants.LOCAL_SOURCE;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			playerStatus = SysMPConstants.STATE_ERROR;
			SysMediaPlayerMgr.sendPlayerStatus(playerStatus);
		}
		return true;
	}
	
}
