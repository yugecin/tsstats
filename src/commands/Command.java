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

public class Command {

	public CommandI command;
	public String cmdtext;
	public String regex;
	public String syntax;
	public boolean needowner;
	
	public Command(String cmdtext, String regex, String syntax, CommandI command, boolean needowner){
		this.command = command;
		this.cmdtext = cmdtext;
		this.regex = regex;
		this.syntax = syntax;
		this.needowner = needowner;
	}
	
}
