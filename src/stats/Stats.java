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
package stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import core.Job;

import data.Channel;
import data.Client;
import database.Database;
import database.Statement;
import logging.Logger;

public class Stats {

	private Database db;

	// serverinfo cache
	private int[] servertod;
	private int lastday;
	private int days;

	public Stats(){
		db = new Database();
		loadServerTOD();
	}
	
	public int getAndIncServerChats(String uid) {
		int kicks = 0;
		try {
			Statement s = db.createStatement(false, "SELECT serverchats FROM users WHERE uid=?");
			s.setString(1, uid);
			ResultSet r = s.executeSelect();
			if (r == null) {
				s.close();
				throw new Exception("hi");
			}
			if (r.next()) {
				kicks = r.getInt(1);
			}
			r.close();
			s.close();
			s = db.createStatement(true, "UPDATE users SET serverchats=serverchats+1 WHERE uid=?");
			s.setString(1, uid);
			s.executeUpdate();
			s.close();
		} catch (Exception e) {
			Logger.err.println("Could not get the amount of serverkicks", e);
		}
		return ++kicks;
	}

	private void loadServerTOD() {
		try {
			Statement s = db.createStatement(true, "SELECT lastday,days,timeofday FROM serverinfo");
			ResultSet r = s.executeSelect();
			while (r.next()) {
				lastday = r.getInt(1);
				days = r.getInt(2);
				servertod = new int[24];
				String[] stod = r.getString(3).split(",");
				for (int i = 0; i < 24; i++) {
					servertod[i] = Integer.parseInt(stod[i]);
				}
			}
			s.close();
		} catch (Exception e) {
			Logger.err.println("Ex loadServerTOD, exiting to prevent dataloss", e);
			System.exit(0);
		}
	}

	public void addUsers(HashMap<String, Client> clientlist) {
		Statement s = null;
		for (Client c : clientlist.values()) {
			try {
				s = db.createStatement(false, "SELECT uid FROM users WHERE uid = ?");
				s.setString(1, c.uid);
				ResultSet r = s.executeSelect();

				if (!r.next()) {
					s = db.createStatement(false, "INSERT INTO users(uid,currentname,updates,country,lastonline) VALUES (?,?,?,?,UNIX_TIMESTAMP())");
					s.setString(1, c.uid);
					s.setUnicode(2, c.nickname);
					s.setInt(3, 0);
					s.setString(4, "");
					s.executeUpdate();
					
					String values = "";
					for (int i = 0; i < 24; i++) {
						values += ",('" + c.uid + "'," + i + ",0)";
					}
					s = db.createStatement(false, "INSERT INTO tod(uid,tod,`count`) VALUES " + values.substring(1));
					s.executeUpdate();
				}
			} catch (SQLException e) {
				Logger.err.println("Exception addUserS", e);
			}
		}
		if (s != null) {
			try {
				s.close();
			} catch (SQLException e) {
				Logger.err.println("Exception addUserS close", e);
			}
		}
	}

	public void addUser(String uid, String username, String country){
		try {
			Statement s = db.createStatement(true, "INSERT INTO `users` (`uid`,`currentname`,`country`,`updates`,`lastonline`) VALUES (?,?,?,?,UNIX_TIMESTAMP()) ON DUPLICATE KEY UPDATE `country`=?, lastonline=UNIX_TIMESTAMP()");
			s.setString(1, uid);
			s.setUnicode(2, username);
			s.setString(3, country);
			s.setInt(4, 1);
			s.setString(5, country);
			s.executeUpdate();

			String values = "";
			for (int i = 0; i < 24; i++) {
				values += ",('" + uid + "'," + i + ",0)";
			}
			s = db.createStatement(false, "INSERT IGNORE INTO tod(uid,tod,`count`) VALUES " + values.substring(1));
			s.executeUpdate();
		} catch (Exception e) {
			Logger.err.println("Ex addUser", e);
		}
	}

	public void addTimeout(String uid){
		try {
			Statement s = db.createStatement(true, "UPDATE `users` SET `timeouts`=`timeouts`+1 WHERE `uid`=?");
			s.setString(1, uid);
			s.executeUpdate();
		} catch (Exception e) {
			Logger.err.println("Ex addTimeout", e);
		}
	}

	public void addKick(String uid, String invokeruid, String reason){
		if(reason == null || reason.length() == 0) reason = "-";
		try {
			Statement s = db.createStatement(true, "INSERT INTO `kicks`(`uid`,`invokeruid`,`reason`,`time`) VALUES (?,?,?,UNIX_TIMESTAMP())'");
			s.setString(1, uid);
			s.setString(2, invokeruid);
			s.setUnicode(3, reason);
			s.executeUpdate();
		} catch (Exception e) {
			Logger.err.println("Ex addKick", e);
		}
	}
	public void addBan(String uid, String invokeruid, String reason, int bantime){
		if(reason == null || reason.length() == 0) reason = "-";
		try {
			Statement s = db.createStatement(true, "INSERT INTO `bans`(`uid`,`invokeruid`,`reason`,`length`,`time`) VALUES (?,?,?,?,UNIX_TIMESTAMP())");
			s.setString(1, uid);
			s.setString(2, invokeruid);
			s.setUnicode(3, reason);
			s.setInt(4, bantime);
			s.executeUpdate();
		} catch (Exception e) {
			Logger.err.println("Ex addBan", e);
		}
	}

	public void update(HashMap<String, Client> clientlist, HashMap<Integer, Channel> chanlist) {
		long m = System.currentTimeMillis();
		int day = Job.calendar.get(java.util.Calendar.DAY_OF_MONTH);
		int hour = Job.calendar.get(java.util.Calendar.HOUR_OF_DAY);
		db.queries = 0;
		updateServer(day, hour);
		try {
			updateClients(day, hour, clientlist);
		} catch (SQLException e) {
			Logger.err.println("Ex updateClients (this is bad)", e);
		}
		try {
			updateChannels(chanlist);
		} catch (SQLException e) {
			Logger.err.println("Ex updateChannels (this is bad)", e);
		}
		db.close();
		Logger.out.println("updated: " + db.queries + " queries in "+(System.currentTimeMillis()-m)+"ms");
	}

	private void updateChannels(HashMap<Integer, Channel> chanlist) throws SQLException {
		Statement updateChan = db.createStatement(false, "INSERT INTO `channels`(`cid`,`pid`,`order`,`name`,`topic`,`maxclients`,`totalusers`,`lastseen`) VALUES (?,?,?,?,?,?,?,UNIX_TIMESTAMP()) ON DUPLICATE KEY UPDATE `maxclients`=GREATEST(maxclients,?),`lastseen`=UNIX_TIMESTAMP(),`totalusers`=`totalusers`+?, `pid`=?,`order`=?,`name`=?,`topic`=?");
		for (Channel c : chanlist.values()) {
			try {
				updateChan.setInt(1, c.cid);
				updateChan.setInt(2, c.pid);
				updateChan.setInt(3, c.order);
				updateChan.setUnicode(4, c.name);
				updateChan.setUnicode(5, c.topic);
				updateChan.setInt(6, c.total_clients);
				updateChan.setInt(7, c.total_clients);

				updateChan.setInt(8, c.total_clients);
				updateChan.setInt(9, c.total_clients);
				updateChan.setInt(10, c.pid);
				updateChan.setInt(11, c.order);
				updateChan.setUnicode(12, c.name);
				updateChan.setUnicode(13, c.topic);
				updateChan.executeUpdate();
			} catch (Exception e) {
				Logger.err.println("Ex updateChannels for channel " + c.name, e);
			}
		}
		updateChan.close();
	}

	private void updateClients(int day, int hour, HashMap<String, Client> clientlist) throws SQLException {
		Statement updateUser = db.createStatement(false, "UPDATE `users` SET `currentname`=?,`days`=IF(lastday!=?,days+1,days),`lastday`=?,`lastonline`=UNIX_TIMESTAMP(),`updates`=`updates`+1 WHERE `uid`=?");
		Statement updateName = db.createStatement(false, "INSERT INTO `usednames`(`uid`,`name`,`count`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `count`=`count`+1");
		Statement updateChan = db.createStatement(false, "INSERT INTO `usedchannels`(`uid`,`cid`,`count`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `count`=`count`+1");
		Statement updateTime = db.createStatement(false, "UPDATE `tod` SET `count`=`count`+1 WHERE `uid`=? AND `tod`=?");
		for (Client c : clientlist.values()) {
			try {
				updateUser.setUnicode(1, c.nickname);
				updateUser.setInt(2, day);
				updateUser.setInt(3, day);
				updateUser.setString(4, c.uid);
				updateUser.executeUpdate();

				updateName.setString(1, c.uid);
				updateName.setUnicode(2, c.nickname);
				updateName.setInt(3, 1);
				updateName.executeUpdate();

				updateChan.setString(1, c.uid);
				updateChan.setInt(2, c.cid);
				updateChan.setInt(3, 1);
				updateChan.executeUpdate();

				updateTime.setString(1, c.uid);
				updateTime.setInt(2, hour);
				updateTime.executeUpdate();
			} catch (Exception e) {
				Logger.err.println("Ex updateClients for client " + c.nickname, e);
			}
		}
		updateUser.close();
		updateName.close();
		updateChan.close();
		updateTime.close();
	}

	private void updateServer(int day, int hour) {
		if (lastday != day) {
			days++;
			lastday = day;
		}
		servertod[hour]++;
		String tod = "";
		for (int i : servertod){
			tod += ","+i;
		}
		tod = tod.substring(1);
		try {
			Statement s = db.createStatement(false, "UPDATE serverinfo SET timeofday=?, lastday=?, days=?, lastupdate=UNIX_TIMESTAMP()");
			s.setString(1, tod);
			s.setInt(2, lastday);
			s.setInt(3, days);
			s.executeUpdate();
			s.close();
		} catch (Exception e) {
			Logger.err.println("Ex addBan", e);
		}
	}

}
