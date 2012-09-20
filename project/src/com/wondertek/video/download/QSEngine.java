/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wondertek.video.download;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;

public class QSEngine {
	private static QSEngine instance = null;
	
	private Activity activityHandle;
    private DLTask[] dlTask;
    public static ExecutorService pool = Executors.newCachedThreadPool();

    private QSEngine(Activity activity)
    {
    	activityHandle = activity;
    }
    
    public static QSEngine getInstance(Activity activity)
    {
    	if(instance == null)
    	{
    		instance = new QSEngine(activity);
    	}
    	return instance;
    }
    public DLTask[] getDlTask() {
        return dlTask;
    }

    public void setDlTask(DLTask[] dlInstance) {
        this.dlTask = dlInstance;
    }

    public void createDLTask(String url, String path, String filename) {
        DLTask task = new DLTask(activityHandle, url, path.endsWith("/") || path.endsWith("\\") ? path + filename : path + "/" + filename);
        pool.execute(task);
    }

    public void resumeDLTask(int threadQut, String url, String path, String filename) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(path.endsWith("/") || path.endsWith("\\") ? path + filename + ".tsk" : path + "/" + filename + ".tsk"));
            DLTask task = (DLTask)in.readObject();
            pool.execute(task);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(QSEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QSEngine.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(QSEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void exitDLTask()
    {
    	pool.shutdownNow();
    	dlTask = null;
    }
}
