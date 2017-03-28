/*
 * Bot for teamspeak3 to collect data for generating statistics
 * Copyright (C) 2014-2017  Robin C.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package core;

import java.util.Calendar;

import logging.Logger;

public class Job extends Thread {

	private Connection con;
	
	public boolean stopReq = false;
	public static Calendar calendar;
	int lastMinute = -1;
	
	public Job(Connection con){
		this.con = con;
	}
	
	@Override
	public void run() {
		while(!stopReq) {
			try {
				Thread.sleep(20000); // checkn iedere 20s, why not?
				calendar.setTimeInMillis(System.currentTimeMillis());
				int m = calendar.get(Calendar.MINUTE);
				if (lastMinute == -1) lastMinute = m; // 2x stats preventie bij restart op de minuut
				if ((m-1)%5 == 0 && lastMinute != m) { // om de 5 minten, @1,6,11,16,21,26,31,36,41,46,51,56 (hence the (m-1)%5)
					lastMinute = m;
					con.update();
				}
			} catch (InterruptedException e) {
				Logger.err.println("InterruptedException in job", e);
			}
		}
	}

}
