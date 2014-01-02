package com.ccindex.config.imp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoad;

public class RowLoadDB implements RowLoad<Connection, List<Map<String, Object>> > {

	private List<Map<String, Object>> config = new ArrayList<Map<String, Object>>();
	private String[] colums ;
	private Connection conn;
	private String commit ;
	private String split ; 
	@Override
	public boolean load() throws SQLException {
		
		if(!conn.isClosed()){
			config.clear();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(commit);
			while(rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				for (int i = 0; i < colums.length; i++) {
					map.put(colums[i],rs.getObject(i+1));
				}
				config.add(map);
			}
			return true ;
		}
		return false ;
	}

	
	
	@Override
	public  List<Map<String, Object>> getData() {
		return config ;
	}




	@Override
	public void setColums(String... colums) {
		this.colums = colums;
	}


	@Override
	public void setSplit(String split) {
		this.split = split ;
	}


	@Override
	public void setSource(Connection conn) {
		this.conn = conn ;
	}


	@Override
	public Connection getSource() {
		return this.conn ;
	}



	@Override
	public void setLoadCommit(String commit) {
		this.commit = commit ;
	}
	
}
