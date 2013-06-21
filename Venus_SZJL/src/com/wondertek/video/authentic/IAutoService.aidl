package com.wondertek.video.authentic;


interface IAutoService {

	/*
	鑾峰彇搴旂敤
	杩斿洖鍊硷細
		搴旂敤鍚嶇О1=鐩戝惉鍦板潃:鐩戝惉绔彛
		搴旂敤鍚嶇О2=鐩戝惉鍦板潃:鐩戝惉绔彛
		...
		搴旂敤鍚嶇Оn=鐩戝惉鍦板潃:鐩戝惉绔彛
	*/
	String getApps();


	/*
	鑾峰彇璇佷功椤逛俊鎭紙鎴愬姛鍚姩鏈嶅姟鍚庢墠鑳借幏鍙栵級
	[IN]opt: 1=SN锛�=CN锛�=DN
		DN涓殑瀵瑰簲椤癸細
			"CN"	濮撳悕
			"T" 	TF鍗℃爣璇嗗彿
			"G"		璀﹀彿
			"ALIAS"	韬唤璇佸彿鐮�
			"S"		鐪�
			"L"		甯�
			"O"		缁勭粐
			"OU"	鏈烘瀯
			"E"		鐢靛瓙閭欢
			"I"		瀹瑰櫒鍚嶇О
	杩斿洖鍊硷細鎸囧畾椤逛俊鎭�
	*/
	String getCertInfo(int opt);

	/*
	鏈嶅姟鏄惁宸插惎鍔�
	杩斿洖鍊硷細true=宸插惎鍔紝false=鏈惎鍔�
	*/
	boolean isStarted();

	/*
	璁剧疆鏈嶅姟鍣ㄥ湴鍧�
	[IN]ip锛�鏈嶅姟鍣ㄥ湴鍧�
	[IN]port锛氭湇鍔″櫒绔彛
	*/
	void setServerAddr(String ip, String port);

	/*
	鍚姩鏈嶅姟
	*/
	void start();

	/*
	鍋滄鏈嶅姟
	*/
	void stop();

	/*
	鑷姩鍗囩骇
	*/
	void upgrade();
	
	/*
	鑾峰彇搴旂敤鏈嶅姟鐘舵�
	[IN]appName锛�搴旂敤鏈嶅姟鍚嶇О锛堝嵆鏈湴IP锛屼緥濡傦細127.0.0.1:10001锛�
	杩斿洖鍊硷細0 = 鏈摼鎺ワ紝1=宸查摼鎺�
	*/
	int getAppStatus(String appName);

	/*
	璁剧疆PIN鐮�
	[IN]pin锛�pin鐮�
	*/
	void setPin(String pin);
	
	/*
	璁剧疆鏈嶅姟妫�煡闂撮殧锛堢锛�
	[IN]seconds锛�闂撮殧鏃堕棿(绉�锛�鍒欎笉妫�煡杩滅鏈嶅姟
	*/
	void setCheckAppInterval(int seconds);
		
	/*
	璁剧疆鏄剧ずtoast娑堟伅
	[IN]display锛�true=鏄剧ず锛沠alse=涓嶆樉绀�
	*/
	void setDisplayToast(boolean display);
}
