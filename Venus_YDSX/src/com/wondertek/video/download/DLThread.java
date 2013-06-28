/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wondertek.video.download;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

import android.os.Bundle;
import android.os.Message;

import com.wondertek.video.Util;

public class DLThread extends Thread implements Serializable {

	private static final long serialVersionUID = -3317849201046281359L;
	private static final int DL_STATE_IDLE		= 0;
	private static final int DL_STATE_BUSY		= 1;
	
	transient private DLTask dlTask;
	private int id;
	private long startPos;
	private long endPos;
	private long curPos;
	private long readByte;
	private boolean finished;
	private boolean isNewThread;
	
	private int m_nDLState;
	private int m_nRunTimes;
	private long m_nHaveDLSize;

	transient boolean m_bException;
	
	public DLThread(DLTask task, int taskID, long sPos, long ePos) {
		dlTask		= task;
		id			= taskID;
		//url			= task.getUrl();
		curPos		= startPos = sPos;
		endPos		= ePos;
		finished 	= false;
		isNewThread = true;
		readByte	= 0;
		m_nRunTimes	= 0;
		m_nHaveDLSize	= 0;
		m_bException	= false;
		m_nDLState		= DL_STATE_IDLE;
	}

	public void reset(long sPos, long ePos)
	{
		if(m_nDLState == DL_STATE_IDLE)
		{
			finished	= false;
			curPos		= sPos;
			endPos		= ePos;
			readByte	= 0;			
		}
	}
	
	public void run() {
		Util.Trace("Thread_" + id + " Run..." + (++m_nRunTimes));
		
		m_nDLState = DL_STATE_BUSY;
		m_bException= false;
		
		InputStream 		httpIns  = null;
		BufferedInputStream	bis = null;
		RandomAccessFile	fos = null;
		byte[] buf = new byte[DLTask.BUFFER_SIZE];
		
		try {
			long sPos = curPos;
			if(isNewThread)
			{
				isNewThread = false;
				sPos = startPos;
			}
			fos = new RandomAccessFile(dlTask.getFile(), "rw");
			fos.seek(sPos);
			
			Util.Trace("S: "+sPos+" , "+"E: "+endPos);
			
			dlTask.setStartTime((int)System.currentTimeMillis());
			
			httpIns = dlTask.getHttpImpl().sendGet(null, sPos, endPos);
			
			if(httpIns != null)
				bis = new BufferedInputStream( httpIns );
			else
				throw new IOException();
			
			while (curPos < endPos) {
//				try {
//					sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				int len = bis.read(buf, 0, DLTask.BUFFER_SIZE);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
				curPos = curPos + len;
				if (curPos > endPos) {
					readByte += len - (curPos - endPos) + 1; // 获取正确读取的字节数
				} else {
					readByte += len;
				}
			}
			Util.Trace( "Thread_" + id + "Download Finish...");
			finished = true;
			m_nHaveDLSize += readByte;
			readByte = 0;
			bis.close();
			fos.close();
		} catch (IOException ex) {
			m_bException= true;
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (IllegalStateException ex) {
			throw new RuntimeException(ex);
		}finally {
			m_nDLState = DL_STATE_IDLE;
			Message msg = new Message();
			msg.what = DLTask.MSG_TYPE_GO_SHEDULE_THREAD;
			Bundle bundle = new Bundle();
			bundle.putInt(DLTask.MSG_ELEMENT_KEY_ID, id);
			bundle.putBoolean(DLTask.MSG_ELEMENT_KEY_EXP, m_bException);
			
			msg.setData(bundle);
			dlTask.getEventCatcher().sendMessage(msg);
		}
	}

	public DLTask getBelongDLIns() {
		return dlTask;
	}

	public void setBelongDLIns(DLTask belongDLIns) {
		this.dlTask = belongDLIns;
	}

	public boolean isFinished() {
		return finished;
	}

	public long getHaveDLSize() {
		if(finished)
			return m_nHaveDLSize;
		else
			return m_nHaveDLSize + readByte;
	}

	public void setDlTask(DLTask dlTask) {
		this.dlTask = dlTask;
	}
	
	public int getID()
	{
		return id;
	}
}
