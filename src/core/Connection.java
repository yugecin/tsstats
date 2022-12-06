/*
 * Bot for teamspeak3 to collect data for generating statistics
 * Copyright (C) 2014-2022  Robin C.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;

import data.Channel;
import data.Client;

import java.util.ArrayDeque;
import java.util.HashMap;

import logging.Logger;
import main.Config;
import stats.Stats;
import viewer.ViewerUpdater;

public class Connection {

	private boolean canSend;

	private Job job;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public HashMap<Integer, Channel> chanlist = new HashMap<Integer, Channel>();
	public HashMap<String, Client> clientlist = new HashMap<String, Client>();

	private RawParser rawparser;
	public Stats stats;
	private ViewerUpdater viewersupdater;

	public Queue<String> fifo;

	public Connection() {
		viewersupdater = new ViewerUpdater();
		stats = new Stats();
		rawparser = new RawParser(this);
	}

	public boolean connect(int attempt) {

		chanlist.clear();
		clientlist.clear();
		canSend = false;

		socket = new Socket();
		if (!opensocket()) {
			return false;
		}

		Logger.out.println("Connected in " + attempt + " attempts");

		fifo = new ArrayDeque<String>();
		fifo.add(""); // dem hacks :D
		fifo.add("use " + Config.getInt("virtualserver"));
		fifo.add("login " + Config.getStr("queryuser") + " " + Config.getStr("querypass"));
		fifo.add("clientupdate client_nickname=" + Config.getStr("name"));
		fifo.add("servernotifyregister event=server");
		fifo.add("servernotifyregister event=textserver");
		fifo.add("servernotifyregister event=textprivate");
		loadclientlist();
		loadchanlist();
		fifo.add("addusers");

		job = new Job(this);
		job.start();

		try {
			loop();
		} catch (IOException e) {
			Logger.err.println("IOException in main connection loop", e);
		}

		out.close();
		try {
			in.close();
		} catch (IOException e) {
			Logger.err.println("IOException while closing in", e);
		}
		try {
			socket.close();
		} catch (IOException e) {
			Logger.err.println("IOException while closing socket", e);
		}

		while (job.isAlive()) { // need to wait till jobthread is stopped too
			job.stopReq = true;
			job.interrupt();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Logger.err.println("InterruptedException while stopping job", e);
			}
		}

		return true;
	}

	private void loop() throws IOException {
		String response;
		String q;
		while ((response = in.readLine()) != null) {

			if (response.equals("")) continue;

			if (Config.getBool("verbose")) {
				Logger.in.println(response);
			}

			canSend = true;

			if (viewersupdater.isUpdating) {
				viewersupdater.update(response);
			} else {
				rawparser.onReceive(response);
			}

			q = fifo.poll();
			if (q != null) {
				if (q.equals("updateyo")) { // if next item is update, lets update :D
					stats.update(clientlist, chanlist);
					if (Config.getBool("enablesiteviewer") || Config.getBool("enabletmviewer")) {
						viewersupdater.startUpdating(this);
					}
				} else if (q.equals("addusers")) {
					stats.addUsers(clientlist);
				} else {
					send(q); // no predefined shizzle, send tha codez!
				}
			}

		}
		Logger.err.println("Received null response. Is this serverquery user (temp) banned?");
	}

	private boolean opensocket() {
		try {
			socket.connect(new InetSocketAddress(Config.getStr("host"), Config.getInt("port")));
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			Logger.err.println("IOException in socket connect", e);
			return false;
		} catch (Exception e) {
			Logger.err.println("Exception in socket init", e);
			return false;
		}
		return true;
	}

	public void send(String message) {
		if (message.equals("")) return;
		if (canSend) {
			if (Config.getBool("verbose")) {
				Logger.send.println(message);
			}
			out.println(message);
			canSend = false;
		} else {
			fifo.add(message);
		}
	}

	public void loadchanlist() {
		send("channellist -topic -limits");
	}

	public void loadclientlist() {
		send("clientlist -uid");
	}
	
	public void update() {
		loadclientlist();
		fifo.add("");
		loadchanlist();
		fifo.add(""); // extra error=0 msgs
		fifo.add("updateyo");
	}

	public void keepalive() {
		send("whoami");
	}
}
