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
package tsadapter;

import static tsadapter.TSParser.escape;

public class TSAction {

	public static String poke(int clid, String msg){
		return "clientpoke clid=" + clid + " msg=" + escape(msg);
	}
	
	public static String move(int clid, int cid){
		return "clientmove clid=" + clid + " cid=" + cid;
	}
	
	public static String clientMessage(int clid, String msg){
		return" sendtextmessage targetmode=1 target=" + clid + " msg=" + escape(msg);
	}
	
	public static String chanMessage(int cid, String msg){
		return "sendtextmessage targetmode=2 target=" + cid + " msg=" + escape(msg);
	}

	public static String serverkick(int clid, String reason){
		return "clientkick clid=" + clid + " reasonid=5 reasonmsg=" + escape(reason);
	}
	
	public static String channelkick(int clid, String reason){
		return "clientkick clid=" + clid + " reasonid=4 reasonmsg=" + escape(reason);
	}
	
	public static String ban(int clid, int time, String reason){
		return "banclient clid=" + clid + " time=" + time + " banreason=" + escape(reason);
	}
}
