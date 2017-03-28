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

import java.util.ArrayList;
import java.util.List;

import data.Channel;
import data.Client;
import tsadapter.parserdata.NotifyClientEnterView;
import tsadapter.parserdata.NotifyClientLeftView;
import tsadapter.parserdata.NotifyTextMessage;

public class TSParser {

	public static String unescape(String str){
		str = str.replace("\\\\", "\\");
		str = str.replace("\\/", "/");
		str = str.replace("\\s", " ");
		str = str.replace("\\p", "|");
		return str;
	}
	
	public static String escape(String str){
		str = str.replace("\\", "\\\\");
		str = str.replace("/", "\\/");
		str = str.replace(" ", "\\s");
		str = str.replace("|", "\\p");
		return str;
	}

	// TODO refactor this?
	public static NotifyTextMessage parseTextMessage(String text){
		NotifyTextMessage ret = new NotifyTextMessage();
		String params[] = text.split(" ");
		for(String param : params){
			if(param.contains("=")){
				String p[] = param.split("=", 2);
				if(p.length > 1){
					p[1] = unescape(p[1]);
					if(p[0].equals("msg")) ret.msg = p[1];
					if(p[0].equals("targetmode")) ret.targetmode = Integer.parseInt(p[1]);
					if(p[0].equals("invokerid")) ret.invokerid = Integer.parseInt(p[1]);
					if(p[0].equals("invokeruid")) ret.invokeruid = p[1];
				}
			}
		}
		return ret;
	}
	
	public static NotifyClientLeftView parseClientLeftView(String text){
		NotifyClientLeftView ret = new NotifyClientLeftView();
		String params[] = text.split(" ");
		for(String param : params){
			if(param.contains("=")){
				String p[] = param.split("=", 2);
				if(p.length > 1){
					p[1] = unescape(p[1]);
					if(p[0].equals("reasonid")) ret.reasonid = Integer.parseInt(p[1]);
					if(p[0].equals("clid")) ret.clid = Integer.parseInt(p[1]);
					if(p[0].equals("bantime")) ret.bantime = Integer.parseInt(p[1]);
					if(p[0].equals("invokeruid")) ret.invokeruid = p[1];
					if(p[0].equals("reasonmsg")) ret.reasonmsg = p[1];
				}
			}
		}
		return ret;
	}
	
	public static NotifyClientEnterView parseClientEnterView(String text){
		NotifyClientEnterView ret = new NotifyClientEnterView();
		String params[] = text.split(" ");
		for(String param : params){
			if(param.contains("=")){
				String p[] = param.split("=", 2);
				if(p.length > 1){
					p[1] = unescape(p[1]);
					if(p[0].equals("client_unique_identifier")) ret.client_unique_identifier = p[1];
					if(p[0].equals("client_nickname")) ret.client_nickname = p[1];
					if(p[0].equals("client_country")) ret.client_country = p[1];
					if(p[0].equals("clid")) ret.clid = Integer.parseInt(p[1]);
					if(p[0].equals("client_database_id")) ret.client_database_id = Integer.parseInt(p[1]);
					if(p[0].equals("client_type")) ret.client_type = Integer.parseInt(p[1]);
				}
			}
		}
		return ret;
	}
	
	public static Client parseClient(String text){
		Client ret = new Client();
		String params[] = text.split(" ");
		for(String param : params){
			if(param.contains("=")){
				String p[] = param.split("=", 2);
				if(p.length > 1){
					p[1] = unescape(p[1]);
					if(p[0].equals("clid")) ret.clid = Integer.valueOf(p[1]);
					if(p[0].equals("cid")) ret.cid = Integer.valueOf(p[1]);
					if(p[0].equals("client_database_id")) ret.dbid = Integer.valueOf(p[1]);
					if(p[0].equals("client_type")) ret.client_type = Integer.valueOf(p[1]);
					if(p[0].equals("client_nickname")) ret.nickname = p[1];
					if(p[0].equals("client_unique_identifier")) ret.uid = p[1];
				}
			}
		}
		return ret;
	}
	
	public static Channel parseChannel(String text, int order){
		Channel ret = new Channel();
		String params[] = text.split(" ");
		for(String param : params){
			if(param.contains("=")){
				String p[] = param.split("=", 2);
				if(p.length > 1){
					p[1] = unescape(p[1]);
					if(p[0].equals("cid")) ret.cid = Integer.valueOf(p[1]);
					if(p[0].equals("pid")) ret.pid = Integer.valueOf(p[1]);
					if(p[0].equals("channel_name")) ret.name = p[1];
					if(p[0].equals("channel_topic")) ret.topic = p[1];
					if(p[0].equals("total_clients_family")) ret.total_clients_family = Integer.valueOf(p[1]);
					if(p[0].equals("channel_maxclients")) ret.maxclients = Integer.valueOf(p[1]);
					if(p[0].equals("channel_maxfamilyclients")) ret.maxfamilyclients = Integer.valueOf(p[1]);
					if(p[0].equals("total_clients")) ret.total_clients = Integer.valueOf(p[1]);
					if(p[0].equals("channel_needed_subscribe_power")) ret.needed_subscribe_power = Integer.valueOf(p[1]);
				} else {
					if(param.equals("channel_topic")) ret.topic = "";
				}
			} else {
				if(param.equals("channel_topic")) ret.topic = "";
			}
		}
		ret.order = order;
		return ret;
	}
	
	public static List<String> parseList(List<String> items){
		List<String> ret = new ArrayList<String>();
		String li = "";
		for(String itm : items){
			li += itm;
			if(li.length() < 900){
				li += "[color=#00aa00]|[/color] ";
			} else {
				ret.add(li);
				li = "";
			}
		}
		if(!li.equals("")) ret.add(li.substring(0, li.length()-"[color=#00aa00]|[/color] ".length()));
		return ret;
	}
	
}
