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
package main;

import java.io.IOException;

import core.Connection;
import logging.Logger;

public class Main {

	public static void main(String args[]) {
		try {
			Config.load();
		} catch (IOException e1) {
			Logger.err.println("Cannot load config");
		}
		
		if (!Config.getBool("enable")) {
			Logger.out.println("Not enabled, exiting");
			return;
		}

		long reconnecttime = Math.max(180, Config.getInt("reconnecttime")) * 1000;
		int attempts = 0;
		Connection c = new Connection();
		for (;;) {
			if (c.connect(attempts)) {
				attempts = 0;
			}
			if (++attempts == 1) {
				Logger.err.println("out of loop, reconnecting after " + reconnecttime);
			}
			try {
				Thread.sleep(reconnecttime);
			} catch (InterruptedException e) {
				Logger.err.println("InterruptedException in main loop", e);
			}
		}
	}

}
