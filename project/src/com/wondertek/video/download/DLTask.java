/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wondertek.video.download;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wondertek.video.Util;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.download.http.HttpImpl;

/**
 *
 */
public class DLTask extends Thread implements Serializable {

	private static final long serialVersionUID	= 126148287461276024L;

	private static final int MAX_DLTHREAD_NUM	= 200;

	public static int BUFFER_SIZE = 1024*800;				//32K = 32768

	/**
	 * template file
	 */
    public final static String FILE_TMP_POSTFIX = ".tmp";


    private HttpImpl httpImpl;

    private String urlString;
    private URL url;

    private File file;
    private String filename;
    private int id;
    private int Level;
    private int m_nFinishThreadCnt = 0;
    private int m_nThreadCount;							//thread number
    private int m_nContentLen;							//File Length
    private int m_nNextDLPos;
    private int completedTot;
    private int startTime;
    private int costTime;
    private String curPercent;
    private boolean isNewTask;
    private String filePrefix;
    private String filePostfix;

    private DLThread[] dlThreads;

    public static final int MSG_TYPE_GO_SHEDULE_THREAD		= 0;

    public static final String MSG_ELEMENT_KEY_ID		= "ID";		//Value type "int".		Thread id of thread
    public static final String MSG_ELEMENT_KEY_EXP		= "EXP";	//Value type "int".		Exception happens or not.



    transient private DLListener listener;

    public DLTask(Activity activity, String url, String filePath) {
    	httpImpl = new HttpImpl(-1, url);
    	int index = filePath.lastIndexOf('.');
        if(index == -1)
        {
        	filePrefix	= filePath;
        	filePostfix	= "";
        	filename 	= filePrefix;
        }
        else
        {
        	filePrefix	= filePath.substring(0, index);
        	filePostfix	= filePath.substring(index, filePath.length());
        	filename	= filePrefix + filePostfix;
        }

        startTime	= 0;
        costTime	= 0;
        curPercent	= "0";
        m_nNextDLPos = 0;
        isNewTask = true;
        listener = new DLListener(this);
        urlString = url;
        Util.Trace( "URL  ->"+url);
        Util.Trace( "File ->"+filePath);
    }

    @Override
    public void run() {
        if (isNewTask) {
            newTask();
            return;
        }
        resumeTask();
    }

    /**
     * @param
     * @param
     *
     * @return
     */
    private void resumeTask() {
        listener = new DLListener(this);
        file = new File(filename + FILE_TMP_POSTFIX);
        for (int i = 0; i < m_nThreadCount; i++) {
            dlThreads[i].setDlTask(this);
            QSEngine.pool.execute(dlThreads[i]);
        }
        QSEngine.pool.execute(listener);
    }



    /**
     * @param
     * @param
     * @return
     * @throws RuntimeException
     */
    private void newTask() throws RuntimeException {
        try {
            isNewTask = false;

            Map<String, ?> map = null;
            map =  httpImpl.getHeaders(null);

            Set<String> set = map.keySet();

            Util.Trace( "*************************************");
            Util.Trace( "		Header Fields				  ");
            Util.Trace( "*************************************");
            for(String key : set){
            	Util.Trace( key + " : " + map.get(key));
            }
            Util.Trace( "*************************************");

            String strContentLen = (String)map.get("Content-Length");
            if(strContentLen == null)
            	strContentLen = (String)map.get("content-length");
            if(strContentLen != null)
            	m_nContentLen = Integer.parseInt( strContentLen);
            else
            {
            	Util.Trace("ERROR: Get Content-Length failure!!!");
            	return ;
            }
            Util.Trace( "Content-Length : " + m_nContentLen);

            file = new File(filename + FILE_TMP_POSTFIX);
            int fileCnt = 1;
            while (file.exists() || new File(filename).exists()) {
            	filename = filePrefix + "(" + fileCnt + ")" + filePostfix;
                file = new File( filename + FILE_TMP_POSTFIX );
                fileCnt++;
            }

            //Calculate the number of thread
            m_nFinishThreadCnt = 0;
            m_nThreadCount = (int) ( (m_nContentLen % BUFFER_SIZE == 0)?(m_nContentLen / BUFFER_SIZE) : ((m_nContentLen / BUFFER_SIZE) + 1) );
            if(m_nThreadCount > MAX_DLTHREAD_NUM)
            {
            	m_nThreadCount	= MAX_DLTHREAD_NUM;
            	m_nNextDLPos	= MAX_DLTHREAD_NUM*BUFFER_SIZE;
            }

            long averageSize		= BUFFER_SIZE;
            long startPos	= 0;
            long endPos		= 0;

            //Create container for all Down Load threads
            dlThreads = new DLThread[m_nThreadCount];


            for (int i = 0; i < m_nThreadCount; i++) {
            	if(i != (m_nThreadCount-1))
            	{
            		startPos	= averageSize * i;
            		endPos		= startPos + averageSize - 1;
            	}
            	else
            	{
            		startPos	= averageSize * i;
            		if(m_nNextDLPos > 0)
            			endPos	= m_nNextDLPos - 1;
            		else
            			endPos	= m_nContentLen - 1;
            	}

                DLThread thread = new DLThread(this, i + 1, startPos,  endPos);
                dlThreads[i] = thread;
                QSEngine.pool.execute(dlThreads[i]);	//Start the Down Load thread.
            }

//            QSEngine.pool.execute(listener);			//Start the listener thread.
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public HttpImpl getHttpImpl()
    {
    	return httpImpl;
    }

    /**
     * @param
     * @param
     * @return
     */
    public String getCurPercent() {
    	completeTot();
        curPercent = new BigDecimal(completedTot).divide(new BigDecimal(this.m_nContentLen), 2, BigDecimal.ROUND_HALF_EVEN).divide(new BigDecimal(0.01), 0, BigDecimal.ROUND_HALF_EVEN).toString();
        return curPercent;
    }

    private int completeTot(){
    	completedTot = 0;
        for (DLThread t : dlThreads) {
            completedTot += t.getHaveDLSize();
        }
        return completedTot;
    }

    /**
     * @param
     * @param
     * @return
     */
    public boolean isComplete() {
        boolean completed = true;
        for (DLThread t : dlThreads) {
            completed = t.isFinished();
            if (!completed) {
                break;
            }
        }
        if(completed && (completeTot() == m_nContentLen))
        	return true;
        else
        	return false;
    }

    /**
     * @param
     * @param
     * @param
     * @return
     */
    private synchronized void sheduleDLThread(int id)
    {
    	if(id > 0)
    	{
			int sPos = 0;
			int ePos = 0;

			sPos = m_nNextDLPos;
			ePos = (m_nContentLen - m_nNextDLPos)>BUFFER_SIZE ? (m_nNextDLPos+BUFFER_SIZE-1) : (m_nContentLen-1);

			m_nNextDLPos = (ePos + 1 == m_nContentLen) ? 0 : (ePos+1);

			id--;
			dlThreads[id].reset(sPos, ePos);
			QSEngine.pool.execute(dlThreads[id]);

    		return ;
    	}

    	Util.Trace("ERROR: Shedule invalid thread " + id);
    }

    private synchronized void monitorState(int id)
    {
    	m_nFinishThreadCnt++;

    	if(m_nFinishThreadCnt == m_nThreadCount && completeTot() == m_nContentLen)
    	{
    		costTime = (int) ((System.currentTimeMillis() - startTime));

    		rename();

    		String time = QSDownUtils.chageSecToHMSForMat(costTime/1000);
    		if(costTime >= 0 && costTime < 1000)
    		{
    			Util.Trace( "Download finished. Use time " + time + "( " + costTime + " ms)"+" (" + completedTot + " - " + " * kB/s" + " )");
    		}
    		else
    			Util.Trace( "Download finished. Use time " + time + "( " + costTime + " ms)"+" (" + completedTot + " - " + ( (completedTot/1024)/(costTime/1000) ) + "kB/s" + " )");

    		Message msg = new Message();
    		msg.what = VenusActivity.MSG_ID_DOWN_VENUS_ZIP_OK;
    		Bundle bundle = new Bundle();
    		bundle.putString("PATH", filename);
    		msg.setData(bundle);
    		VenusActivity.getInstance().sendVenusEvent(msg);
    	}
    }

	private Handler eventHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TYPE_GO_SHEDULE_THREAD :
				Bundle bundle = msg.getData();
				int id = bundle.getInt(MSG_ELEMENT_KEY_ID);
				boolean exp = bundle.getBoolean(MSG_ELEMENT_KEY_EXP);
				if(exp == false)
				{
					if(m_nNextDLPos > 0)
						sheduleDLThread(id);
					else
						monitorState(id);
				}
				else
				{
					//TODO
				}
				break;
			default:
				break;
			}
		}
	};

	public Handler getEventCatcher()
	{
		return eventHandler;
	}
//    public boolean percentChanged() {
//        percent();
//        if (curPercent.equals(prevPercent)) {
//            return false;
//        }
//        prevPercent = curPercent;
//        return true;
//    }

    public void rename(){
    	File destFile = new File(filename);
    	if(destFile.exists())
    	{
    		destFile.delete();
    	}
        this.file.renameTo(new File(filename));
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int Level) {
        this.Level = Level;
    }

    public DLThread[] getDlThreads() {
        return dlThreads;
    }

    public void setDlThreads(DLThread[] dlThreads) {
        this.dlThreads = dlThreads;
    }

    public int getTaskId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public URL getURL() {
        return url;
    }

    public String getUrlString()
    {
    	return urlString;
    }

    public int getContentLen() {
        return m_nContentLen;
    }

    public String getFilename() {
        return filename;
    }


    public int getThreadCount() {
        return m_nThreadCount;
    }

	public long getCompletedTot() {
		return completedTot;
	}

	public int getCostTime() {
		return costTime;
	}

	public void setStartTime(int sTime)
	{
		if(startTime == 0)
		{
			startTime = sTime;
		}
	}
	public void setCostTime(int costTime) {
		this.costTime = costTime;
	}

}
