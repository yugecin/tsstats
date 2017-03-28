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
package commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.Config;
import tsadapter.TSAction;
import tsadapter.TSParser;
import core.Connection;
import core.RawParser;
import data.Client;
import logging.Logger;

public class CommandHandler {

	HashMap<String, Command> commands;

	public CommandHandler(final RawParser core, final Connection con) {
		commands = new HashMap<String, Command>();

		/**
		 * @Command update - perform an update
		 */
		this.registerCommand(true, "update", "^update$", "update", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				con.update();
				con.send(TSAction.clientMessage(invokerid, "Updated!"));
			}
		});

		/**
		 * @Command loadconfig - reloads the config
		 */
		this.registerCommand(true, "loadconfig", "^loadconfig$", "loadconfig", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				try {
					Config.load();
				} catch (IOException e) {
					con.send(TSAction.clientMessage(invokerid, "Error while loading config"));
					Logger.err.println("Error reloading config", e);
				}
			}
		});

		/**
		 * @Command kick
		 */
		this.registerCommand(true, "kick", "^kick [0-9]+ (.+?)$", "kick <id> <reason>", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				int id = Integer.parseInt(params[0]);
				String reason = String.join(" ", params);
				reason = reason.substring(params[0].length() + 1);
				con.send(TSAction.serverkick(id, reason));
				con.send(TSAction.clientMessage(invokerid, "Kicking " + id + " for: " + reason));
			}
		});

		/**
		 * @Command poke
		 */
		this.registerCommand(true, "poke", "^poke [0-9]+ (.+?)$", "poke <id> <text>", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				int id = Integer.parseInt(params[0]);
				String reason = String.join(" ", params);
				reason = reason.substring(params[0].length() + 1);
				con.send(TSAction.poke(id, reason));
				con.send(TSAction.clientMessage(invokerid, "Poked " + id + ": " + reason));
			}
		});

		/**
		 * @Command clientlist - sends clientlist to client
		 */
		this.registerCommand(true, "loadclientlist", "^loadclientlist$", "loadclientlist", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				con.loadclientlist();
				con.fifo.add("");
			}
		});
		this.registerCommand(true, "clientlist", "^clientlist$", "clientlist", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				List<String> list = new ArrayList<String>();
				Iterator<Entry<String, Client>> i = con.clientlist.entrySet().iterator();
				while(i.hasNext()){
					Client c = ((Map.Entry<String, Client>)i.next()).getValue();
					list.add("[color=#ff0000]"+c.clid+"[/color] "+c.nickname+" ");
				}
				list = TSParser.parseList(list);
				for (String s : list) {
					con.send(TSAction.clientMessage(invokerid, s));
				}
			}
		});

		/**
		 * @Command chanlist - send chanlist to client
		 */
		this.registerCommand(true, "chanlist", "^chanlist$", "chanlist", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				return;
				/*
				core.loadChannellist();
				core.cmdQueue.add(null); //wacht tot chanlist received is
				core.cmdQueue.add(new CommandQueueI(){
					@Override
					public void exec(RawParser core) {
						List<String> list = new ArrayList<String>();
						for(Integer c : core.chanlist.keySet()){
							list.add("[color=#ff0000]"+c+"[/color] "+core.chanlist.get(c).name+" ");
						}
						TSParser.sendList(core, invokerid, list);
					}
				});
				 */
			}
		});

		/**
		 * @Command forcestats - force stats for developement only
		 */
		this.registerCommand(true, "forcestats", "^forcestats$", "forcestats", new CommandI(){
			@Override
			public void onCommandText(final int invokerid, final String invokeruid, final String[] params) {
				return;
				/*
				core.cmdQueue.add(new CommandQueueI(){
					@Override
					public void exec(RawParser core) {
						core.loadChannellist();
						core.loadClientlist();
						core.connection.sendQueue.add(""); // voor extra error=0 msg
						core.connection.sendQueue.add(""); // voor extra error=0 msg
						core.connection.sendQueue.add("updateyo"); //of core.send, maakt nie uit
					}
				});
				 */
			}
		});

	}

	public void registerCommand(String cmdtext, String regex, String syntax, CommandI command){
		commands.put(cmdtext, new Command(cmdtext, regex, syntax, command, false));
	}

	public void registerCommand(boolean needowner, String cmdtext, String regex, String syntax, CommandI command){
		commands.put(cmdtext, new Command(cmdtext, regex, syntax, command, needowner));
	}

	public boolean execute(Connection con, int invokerid, String invokeruid, String msg){
		String cmdtext;
		String[] params = null;
		if (msg.contains(" ")) {
			String temp[] = msg.split(" ", 2);
			cmdtext = temp[0];
			params = temp[1].split(" ");
		} else {
			cmdtext = msg;
		}

		if (!commands.containsKey(cmdtext)) return false;
		Command command = commands.get(cmdtext);

		if (!msg.matches(command.regex)) {
			con.send(TSAction.clientMessage(invokerid, "[color=#ff0000]Sytax: "+command.syntax+"[/color]"));
			return true;
		}

		if (command.needowner) {
			if (!Config.inList("owners", invokeruid)) {
				con.send(TSAction.clientMessage(invokerid, "[color=#ff0000]Permission denied.[/color]"));
				return true;
			}
		}

		command.command.onCommandText(invokerid, invokeruid, params);
		return true;
	}

}
