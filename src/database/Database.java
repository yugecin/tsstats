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
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import logging.Logger;
import main.Config;

public class Database {

	private Connection c = null;
	public int queries = 0;
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://" + Config.getStr("dbhost") + ":" + Config.getStr("dbport") + "/" + Config.getStr("db") + "?useUnicode=true&characterEncoding=utf-8" /*"?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8"*/, Config.getStr("dbuser"), Config.getStr("dbpass"));
			c.createStatement().executeUpdate("SET NAMES utf8");
			c.setAutoCommit(false);
		} catch (SQLException e) {
			Logger.err.println("Could not connect to MySQL server ", e);
			Logger.err.println("System will halt");
			System.exit(0);
		} catch (ClassNotFoundException e) {
			Logger.err.println("JDBC Driver not found!");
		}
	}

	public void close() {
		try {
			if (checkConnection()) {
				c.close();
				c = null;
			}
		} catch (SQLException e) {
			Logger.err.println("Error closing the MySQL Connection", e);
		}
	}
	
	private boolean checkConnection() throws SQLException {
		return c != null && c.isValid(0);
	}
	
	public Statement createStatement(boolean close, String sql) throws SQLException {
		if (!checkConnection()) {
			connect();
		}
		queries++;
		return new Statement(this.c, close, sql);
	}
	
}
