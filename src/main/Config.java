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
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import core.Job;
import logging.Logger;

public class Config {
	
	private static HashMap<String, String> kv = new HashMap<String, String>();
	
	public static void load() throws IOException
	{
		kv.clear();

		BufferedReader reader = new BufferedReader(new FileReader("tsstats.ini"));
		String line;
		
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(";") || line.length() == 0) {
				continue;
			}
			
			if (line.matches("^[_a-zA-Z0-9]+: ?.*?$")) {
				String info[] = line.replaceAll("^([_a-zA-Z0-9]+): ?(.*?)$", "$1 $2").split(" ", 2);
				kv.put(info[0], info[1]);
			}
		}
		reader.close();
		
		try {
			Job.calendar = Calendar.getInstance(TimeZone.getTimeZone(Config.getStr("timezone")));
		} catch (Exception ex) {
			Logger.err.println("Error on setting calendar, timezone might be unknown", ex);
			Logger.err.println("Cannout continue without succesfully setting timezone, exiting", ex);
			System.exit(1);
		}
	}
	
	public static boolean inList(String tag, String item)
	{
		if (kv.containsKey(tag)) {
			String[] list = kv.get(tag).split(",");
			for (String s : list) {
				if (s.equals(item)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getStr(String tag)
	{
		if (kv.containsKey(tag)) {
			return kv.get(tag);
		}
		return "";
	}
	
	public static int getInt(String tag)
	{
		if (kv.containsKey(tag)) {
			try {
				return Integer.parseInt(kv.get(tag));
			} catch (Exception ex) {
				return 0;
			}
		}
		return 0;
	}
	
	public static boolean getBool(String tag)
	{
		if (kv.containsKey(tag)) {
			return kv.get(tag).equals("1") || kv.get(tag).equals("yes") || kv.get(tag).equals("true");
		}
		return false;
	}
	
}
