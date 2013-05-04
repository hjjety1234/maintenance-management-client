package com.wondertek.video.update;

public class UpdateInfo {
	public static String localDownloadDir = "/mnt/sdcard/download";
	public static String releaseLog = null;
	public static String localApkPath = "/mnt/sdcard/download/Venus_Jttxl.apk";
	public static String localPatchPath = "/mnt/sdcard/download/Venus_Jttxl.patch";
	public static String localNewApkPath = "/mnt/sdcard/download/Venus_Jttxl_New.apk";
	
	private String remoteApkUri = null;
	private String remotePatchUri = null;
	
	private String md5sum = null;
	private long remoteApkSize = 0;
	private long remotePatchSize = 0;

	public String getReleaseLog() {
		return releaseLog;
	}

	public void setReleaseLog(String releaseLog) {
		UpdateInfo.releaseLog = releaseLog;
	}

	public String getLocalApkPath() {
		return localApkPath;
	}

	public void setLocalApkPath(String localApkPath) {
		UpdateInfo.localApkPath = localApkPath;
	}

	public String getLocalPatchPath() {
		return localPatchPath;
	}

	public void setLocalPatchPath(String localPatchPath) {
		UpdateInfo.localPatchPath = localPatchPath;
	}

	public String getLocalNewApkPath() {
		return localNewApkPath;
	}

	public void setLocalNewApkPath(String localNewApkPath) {
		UpdateInfo.localNewApkPath = localNewApkPath;
	}

	public String getRemoteApkUri() {
		return remoteApkUri;
	}

	public void setRemoteApkUri(String remoteApkUri) {
		this.remoteApkUri = remoteApkUri;
	}

	public String getRemotePatchUri() {
		return remotePatchUri;
	}

	public void setRemotePatchUri(String remotePatchUri) {
		this.remotePatchUri = remotePatchUri;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public long getRemoteApkSize() {
		return remoteApkSize;
	}

	public void setRemoteApkSize(long remoteApkSize) {
		this.remoteApkSize = remoteApkSize;
	}

	public long getRemotePatchSize() {
		return remotePatchSize;
	}

	public void setRemotePatchSize(long remotePatchSize) {
		this.remotePatchSize = remotePatchSize;
	}

}
