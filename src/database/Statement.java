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

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statement {

	private Connection c;
	private boolean close;
	public PreparedStatement p;
	
	public Statement(Connection c, boolean close, String sql) throws SQLException {
		this.c = c;
		this.close = close;
		p = c.prepareStatement(sql);
	}
	
	public void setInt(int pos, int value) throws SQLException {
		p.setInt(pos, value);
	}
	
	public void setString(int pos, String value) throws SQLException {
		p.setString(pos, value);
	}
	
	public void setUnicode(int pos, String value) throws SQLException {
		p.setBytes(pos, Charset.forName("UTF-8").encode(value).array());
		//p.setString(pos, value);
	}
	
	public void executeUpdate() throws SQLException {
		p.executeUpdate();
		c.commit();
		if (close) c.close();
	}
	
	public ResultSet executeSelect() throws SQLException {
		ResultSet r = p.executeQuery();
		c.commit();
		return r;
	}
	
	public void close() throws SQLException {
		c.close();
	}

}
