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
package logging;

import java.io.PrintStream;
import java.util.Calendar;

import core.Job;

public class ConcreteLogger {
	
	private String prefix;
	private PrintStream out;
	
	public ConcreteLogger(String prefix, PrintStream out) {
		this.prefix = prefix + " ";
		this.out = out;
	}

	public void println(String msg) {
		Job.calendar.setTimeInMillis(System.currentTimeMillis());
		String d = Job.calendar.get(Calendar.DAY_OF_MONTH)+"";
		String month = Job.calendar.get(Calendar.MONTH)+1+"";
		String h = Job.calendar.get(Calendar.HOUR_OF_DAY)+"";
		String m = Job.calendar.get(Calendar.MINUTE)+"";
		String s = Job.calendar.get(Calendar.SECOND)+"";
		if(h.length() == 1) h = "0"+h;
		if(m.length() == 1) m = "0"+m;
		if(s.length() == 1) s = "0"+s;
		out.println("["+d+"/"+month+" "+h+":"+m+":"+s+"] " + prefix + msg);
	}
	
	public void println(String msg, Exception ex) {
		println(msg);
		out.println("-- -- " + ex.getMessage());
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i = 0, max = trace.length; i < max; i++) {
			String n = i+"";
			if (n.length() < 2) n = " " + n;
			out.println(n + " at " + trace[i]);
		}
	}
	
}
