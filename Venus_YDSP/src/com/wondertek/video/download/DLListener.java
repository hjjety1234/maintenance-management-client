/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wondertek.video.download;

import java.math.BigDecimal;

import com.wondertek.video.Util;

public class DLListener extends Thread {

	private DLTask dlTask;

	DLListener(DLTask dlTask) {
		this.dlTask = dlTask;
	}

	@Override
	public void run() {

		BigDecimal completeTot = null;
		long start = System.currentTimeMillis();
		long end = start;

		while (!dlTask.isComplete()) {
			String percent = dlTask.getCurPercent();
			completeTot = new BigDecimal(dlTask.getCompletedTot());
			end = System.currentTimeMillis();
			if (end - start > 1000) {
				BigDecimal pos = new BigDecimal(((end - start) / 1000) * 1024);
				Util.Trace( "Speed :" + completeTot.divide(pos, 0, BigDecimal.ROUND_HALF_EVEN) + "k/s   " + percent + "% completed. " + "(" + completeTot + ")" );
			}
			try {
				sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}

		}
	}
}
