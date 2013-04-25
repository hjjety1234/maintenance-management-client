package com.wondertek.video.msgpush;

/**
 * Interface for message push
 * @author yuhongwei
 *
 */

public interface IMsgPush {
	/**
	 * initialize Message Push Service
	 */
	public void initializeService();
	
    /**
     * Setting the Message Push Service enable or disable
     * If it's enable, the Service will start when app launching 
     * or device boot completed. Otherwise, the Service will
     * stop when set Service disable.
     */
	public void setServiceEnable(boolean bEnable);
    
	/**
	 * Getting the Message Push Service enable or disable
	 */
	public boolean getServiceState();
    
	/**
	 * Setting Message Push Service binding the message push
	 *  server IP or binding a url for the Service polling request
	 *  message background
	 */
	public void setBindUrl(String url);
    
	/**
	 * Getting Message Push Service had binded Server IP or
	 * Url for polling request background
	 */
	public String getBindUrl();
    
	/**
	 * Setting Message Push Service to listen Server Port
	 */
	public void setBindPort(int port);
    
	/**
	 * Getting Message Push Service to listen Server Port
	 */
	public int getBindPort();
    
	/**
	 * Setting Message Push Service the Style to notify user when
	 * the service receiver a message from Server
	 */
	public void setNotifyStyle(String key, boolean bEnable);
    
	/**
	 * Getting Message Push Service the Style to notify user when
	 * the service receiver a message from Server
	 */
	public boolean getNotifyStyle(String key);
    
	/**
	 * Setting some user definition information for Message Push
	 * Service for custom Style. For example, username or password
	 * to login Message Push Server
	 */
    public void setUserDefinition(String key, String value);
    
    /**
     * Getting some information for user definition
     */
    public String getUserDefinition(String key);
    
    /**
     * To set Androidpn SDK apiKey
     */
    public void setMsgPushApiKey(String apiKey);
}
