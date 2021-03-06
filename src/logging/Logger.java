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
package logging;

public class Logger {
	
	public static ConcreteLogger out = new ConcreteLogger("--", System.out);
	public static ConcreteLogger in = new ConcreteLogger("<-", System.out);
	public static ConcreteLogger send = new ConcreteLogger("->", System.out);
	public static ConcreteLogger err = new ConcreteLogger("er", System.err);
	
}
