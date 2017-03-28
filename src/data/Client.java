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
package data;

public class Client {

	public int clid;
	public int cid;
	public int dbid;
	// TODO is client_type needed?
	public int client_type;
	public String uid;
	public String nickname;
	
	public Client(){
	
	}
	
	public Client(String uid, String nickname, int clid, int cid, int dbid, int client_type){
		this.uid = uid;
		this.nickname = nickname;
		this.clid = clid;
		this.cid = cid;
		this.dbid = dbid;
		this.client_type = client_type;
	}
	
}
