package com.ccindex.config.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoad;

public class RowLoadFile  implements RowLoad<File, List<Map<String, Object>> > {

	private String[] colums ;
	
	private File conn;
	private String commit ;
	private String split ;
	
	private List<Map<String, Object>> config = new ArrayList<Map<String, Object>>();
	
	@Override
	public void setColums(String... colums) {
		this.colums = colums ;
	}

	@Override
	public boolean load() throws Exception {
		
		if( conn.exists() ){
		config.clear();
		FileInputStream reader = new FileInputStream(conn) ;
		BufferedReader dr = new BufferedReader( new InputStreamReader( reader ,"UTF-8") ) ;
		String line =  dr.readLine();
		 while(line!= null){ 
			 line = new String(line.getBytes("UTF-8"));
			 Map<String, Object> map = new HashMap<String, Object>();
			 String[] rows = line.split(split);
			 for (int i = 0; i < colums.length; i++) {
				 if( (rows.length-1) >= i){
					 map.put( colums[i],rows[i] ) ;
				 }else{
					 map.put( colums[i],"" ) ;
				 }
			}
			 config.add(map);
			 line = dr.readLine();
		 } 
		 reader.close();
		 return true ;
		}else{
			return false ;
		}
	}

	@Override
	public List<Map<String, Object>> getData() {
		return config;
	}


	@Override
	public void setSplit(String split) {
		this.split = split ;
	}

	@Override
	public void setSource(File conn) {
		this.conn = conn ;
	}

	@Override
	public File getSource() {
		return this.conn;
	}

	@Override
	public void setLoadCommit(String commit) {
		this.commit = commit ;
	}
	
}
