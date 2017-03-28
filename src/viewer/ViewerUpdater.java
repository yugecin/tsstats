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
package viewer;

import java.io.File;
import java.io.PrintWriter;

import core.Connection;
import logging.Logger;
import main.Config;

public class ViewerUpdater {

	public boolean isUpdating = false;

	private int updateindex;

	private String serverinfo;
	private String channellist;
	private String clientlist;
	private String servergrouplist;
	private String channelgrouplist;

	public void update(String response){
		response += "\n\r";
		switch (updateindex) {
			case 0:
				serverinfo = response;
				break;
			case 1:
			case 2:
				channellist += response;
				break;
			case 3:
			case 4:
				clientlist += response;
				break;
			case 5:
			case 6:
				servergrouplist += response;
				break;
			case 7:
				channelgrouplist += response;
				break;
			case 8:
				channelgrouplist += response;
				dump();
				break;
				
		}
		updateindex++;
	}

	public void startUpdating(Connection con){
		isUpdating = true;
		serverinfo = "";
		channellist = "";
		clientlist = "";
		servergrouplist = "";
		channelgrouplist = "";
		updateindex = 0;

		// update voor webviewer & tmviewer
		// "" are for extra error=0 messages
		con.send("serverinfo");
		con.fifo.add("");
		con.fifo.add("channellist -topic -flags -voice -limits");
		con.fifo.add("");
		con.fifo.add("clientlist -uid -away -voice -groups");
		con.fifo.add("");
		con.fifo.add("servergrouplist");
		con.fifo.add("");
		con.fifo.add("channelgrouplist");
	}

	private void dump(){
		StringBuilder b = new StringBuilder();
		b.append("error id=0 msg=ok\n\r").append(serverinfo).append(channellist).append(clientlist);
		if (Config.getBool("enabletmviewer")) {
			save(b.toString(), Config.getStr("tmviewerfile"));
		}
		if (Config.getBool("enablesiteviewer")) {
			b.append(servergrouplist).append(channelgrouplist);
			save(b.toString(), Config.getStr("siteviewerfile"));
		}
		isUpdating = false;	
	}

	private void save(String data, String path){
		data += "error id=0 msg=ok\n\r";
		try
		{
			PrintWriter out = new PrintWriter(path.replace("/", File.separator));
			out.print(data);
			out.close();
		}
		catch(Exception e)
		{
			Logger.err.println("Exception while dumping viewerstuff", e);
		}
	}

}
