package com.wondertek.video.sysplayer;

import com.wondertek.video.Util;
import com.wondertek.video.VenusApplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 
 * @author yuhongwei
 *
 */

public class SysMediaPlayerMgr {
	public static final String libpath = VenusApplication.appAbsPath	+ "lib2/sysplayer/libsysplayer.so";
	static {
		System.load(libpath);
	}
    public static SysMediaPlayerMgr instance = null;
    private Context context;
    private Intent serviceIntent;
    private SystemMediaPlayerService sysMPService;
    private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Util.Trace(">>>>onServiceDisconnected<<<<");
            sysMPService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Util.Trace(">>>>onServiceConnected<<<<");
			sysMPService = ((SystemMediaPlayerService.SystemMediaPlayerServiceBinder)service).getService();
		}
	};
    
    public SysMediaPlayerMgr(Context cxt) {
        serviceIntent = new Intent(cxt, SystemMediaPlayerService.class);
    	cxt.bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        context = cxt;
    }
    
    public static SysMediaPlayerMgr getInstance() {
    	return instance;
    }
    
    public static SysMediaPlayerMgr getInstance(Context cxt) {
    	if (instance == null) {
    		instance = new SysMediaPlayerMgr(cxt);
    	}
    	return instance;
    }
    
    public native static void nativeSendPlayerStatus(int status);
    
    public static void sendPlayerStatus(int status) {
        Util.Trace("sendPlayerStatus: " + status);
    	nativeSendPlayerStatus(status);
    }
    
    public boolean javaOpen(String filePath) {
    	if (sysMPService != null) {
    		return sysMPService.open(filePath);
    	}
    	return false;
    }
    
    public void javaStart() {
    	if (sysMPService != null) {
    		sysMPService.start();
    	}
    }
    
    public void javaPause() {
    	if (sysMPService != null) {
    		sysMPService.pause();
    	}
    }
    
    public void javaStop() {
    	if (sysMPService != null) {
    		sysMPService.stop();
    	}
    }
    
    public void javaSetVolume(int vol) {
    	Util.Trace("javaSetVolume vol: " + vol);
    	if (sysMPService != null) {
    		sysMPService.setVolume(vol);
    	}
    }
    
    public int javaDuration() {
    	if (sysMPService != null) {
    		return sysMPService.duration();
    	}
    	return 0;
    }
    
    public int javaGetCurPosition() {
    	if (sysMPService != null) {
    		return sysMPService.getCurPosition();
    	}
    	return 0;
    }
    
    public int javaSeekTo(int msec) {
    	Util.Trace("javaSeekTo msec: " + msec);
    	if (sysMPService != null) {
    		return sysMPService.seekTo(msec);
    	}
    	return 0;
    }
    
    public boolean javaIsPlaying() {
    	if (sysMPService != null) {
    		return sysMPService.isPlaying();
    	}
    	return false;
    }
    
    public boolean javaIsLooping() {
    	if (sysMPService != null) {
    		return sysMPService.isLooping();
    	}
    	return false;
    }
    
    public void javaSetLooping(boolean bEnable) {
    	if (sysMPService != null) {
    		sysMPService.setLooping(bEnable);
    	}
    }
    
    public int javaGetSourceType() {
    	if (sysMPService != null) {
    		return sysMPService.getPlaySourceType();
    	}
    	return 0;
    }
    
}
