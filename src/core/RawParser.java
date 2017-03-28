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

import java.util.List;
import java.util.ArrayList;

import tsadapter.TSAction;
import tsadapter.TSParser;
import tsadapter.parserdata.NotifyClientEnterView;
import tsadapter.parserdata.NotifyClientLeftView;
import tsadapter.parserdata.NotifyTextMessage;
import commands.CommandHandler;
import commands.CommandQueueI;

import data.Channel;
import data.Client;
import main.Config;

public class RawParser {
	
	public Connection con;
	// TODO: refactor commandhandler
	private CommandHandler commandhandler;

	public int myid;
	
	public List<CommandQueueI> cmdQueue;
	
	public RawParser(Connection con) {
		this.con = con;
		commandhandler = new CommandHandler(this, con);
		// TODO: what is this queue again
		cmdQueue = new ArrayList<CommandQueueI>();
	}
	
	public void onReceive(String response){
		/*
		 * notifycliententerview cfid=0 ctid=1 reasonid=0 clid=24 client_unique_identifier=ServerQuery client_nickname=Unknown\sfrom\s178.117.71.96:53710 client_input_muted=0 client_output_muted=0 client_outputonly_muted=0 client_input_hardware=0 client_output_hardware=0 client_meta_data client_is_recording=0 client_database_id=196 client_channel_group_id=8 client_servergroups=36 client_away=0 client_away_message client_type=1 client_flag_avatar client_talk_power=17 client_talk_request=0 client_talk_request_msg client_description client_is_talker=0 client_is_priority_speaker=0 client_unread_messages=0 client_nickname_phonetic client_needed_serverquery_view_power=75 client_icon_id=0 client_is_channel_commander=0 client_country=BE client_channel_group_inherited_channel_id=1 client_badges
	 	 * 
		 */
		
		if (response.startsWith("clid=")){
			
			loadClientlist(response);
			
		} else if (response.startsWith("cid=")) {
			
			loadChannellist(response);
			
		} else if (response.startsWith("notifytextmessage")) {

			NotifyTextMessage ntm = TSParser.parseTextMessage(response);
			if (ntm.invokerid != myid) { //inf loop prevented
				
				if (ntm.targetmode == 1) { //privatechat
					
					if (ntm.msg.startsWith(Config.getStr("commandprefix"))){ //check for commands
						if(!commandhandler.execute(con, ntm.invokerid, ntm.invokeruid, ntm.msg.substring(Config.getStr("commandprefix").length()))){
							con.send(TSAction.clientMessage(ntm.invokerid, "[color=#ff0000]Command not found.[/color]"));
						}
					}
					
				} else if (ntm.targetmode == 2) { //channelchat
					
					// channel has to be registered + client has to be in the channel
					
				} else if (ntm.targetmode == 3) { //serverchat, kicken die handel
					
					if (Config.getBool("denyserverchat")) {
						auditServerchat(ntm);
					}
					
				}
			}
				
		} else if (response.startsWith("notifyclientleftview")){

			NotifyClientLeftView nclv = TSParser.parseClientLeftView(response);
			if (nclv.clid != this.myid){
				
				//reason: 3 = timeout, 5 = kick, 6 = ban, 8 = left
				if (nclv.reasonid == 3){ //timeout
					con.stats.addTimeout(getuid(nclv.clid));
				} else if (nclv.reasonid == 5){ //kick
					con.stats.addKick(getuid(nclv.clid), nclv.invokeruid, nclv.reasonmsg);
				} else if (nclv.reasonid == 6){ //ban
					con.stats.addBan(getuid(nclv.clid), nclv.invokeruid, nclv.reasonmsg, nclv.bantime);
				} else if (nclv.reasonid == 8){ //left
					// /care
				}

				con.clientlist.remove(getuid(nclv.clid));
			}
			
		} else if (response.startsWith("notifycliententerview")){

			NotifyClientEnterView ncev = TSParser.parseClientEnterView(response);
			if (ncev.client_type != 1) { // no serverqueries
				
				con.clientlist.put(ncev.client_unique_identifier, new Client(ncev.client_unique_identifier, ncev.client_nickname, ncev.clid, 0, ncev.client_database_id, ncev.client_type));
				con.stats.addUser(ncev.client_unique_identifier, ncev.client_nickname, ncev.client_country);
				
			}
			
		}
		
		if (!cmdQueue.isEmpty()){
			CommandQueueI c = cmdQueue.get(0);
			if(c != null){
				c.exec(this);
			}
			cmdQueue.remove(0);
		}
		
	}
	
	private void auditServerchat(NotifyTextMessage ntm) {
		int amount = con.stats.getAndIncServerChats(ntm.invokeruid);
		
		int warnings = Config.getInt("serverchatwarnings");
		if(amount > warnings){
			con.send(TSAction.ban(ntm.invokerid, (amount-warnings)*Config.getInt("serverchatbanmp"), "Sending server messages is forbidden (ban #"+(amount-warnings)+": "+(amount-warnings)+"m)"));
		} else {
			con.send(TSAction.serverkick(ntm.invokerid, "Sending server messages is forbidden (warning "+amount+"/"+warnings+")"));
		}
	}
	
	public String getuid(int clid){
		for (String uid : con.clientlist.keySet()){
			if (con.clientlist.get(uid).clid == clid) return uid;
		}
		return "null";
	}
	
	public void loadClientlist(String list){
		con.clientlist.clear();
		for (String c : list.split("\\|")){
			Client client = TSParser.parseClient(c);
			
			if (client.client_type == 1) { // if client = serverquery
				if (client.nickname.equals(Config.getStr("name"))){ // if client is me
					myid = client.clid; // store my id
					int campchan = Config.getInt("campchan");
					if (client.cid != campchan){ // check if i'm in campchan
						con.send(TSAction.move(client.clid, campchan)); // if not, move my ass
					}
				}
			} else {
				con.clientlist.put(client.uid, client); //add to le list
			}
		}
	}
	
	public void loadChannellist(String list){
		//cid=1 pid=0 channel_order=0 channel_name=Rules\s(No\sTalk\sChannel) channel_topic=Public total_clients_family=1 channel_maxclients=-1 channel_maxfamilyclients=-1 total_clients=1 channel_needed_subscribe_power=0|cid=213 pid=0 channel_order=1 channel_name=Public\sChannel\s-Basdon-\sOpeningstijden channel_topic total_clients_family=0 channel_maxclients=-1 channel_maxfamilyclients=-1 total_clients=0 channel_needed_subscribe_power=0|cid=10 pid=0 channel_order=213 channel_name=Tribunal\s(server-rechtbank) channel_topic total_clients_family=0 channel_maxclients=-1 channel_maxfamilyclients=-1 total_clients=0 channel_needed_subscribe_power=0
		con.chanlist.clear();
		int order = 0; //own order, cuz order in params unknown?
		for (String c : list.split("\\|")){
			Channel p = TSParser.parseChannel(c, ++order);
			con.chanlist.put(p.cid, p);
		}
	}
	
}
