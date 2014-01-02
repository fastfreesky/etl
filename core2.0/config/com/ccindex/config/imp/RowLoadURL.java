package com.ccindex.config.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoad;

public class RowLoadURL implements RowLoad<URL, List<Map<String, Object>> > {

	private List<Map<String, Object>> config = new ArrayList<Map<String, Object>>();
	private String[] colums ;
	
	private URL url;
	private String split ;
	private String commit ;
	
	@Override
	public boolean load()throws IOException  {
		 InputStream input = url.openStream() ;
		 if( input != null ){
		 config.clear();
         //打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
		 BufferedReader dr = new BufferedReader( new InputStreamReader(input) ) ;
		 String line =  dr.readLine();
		 while(line!= null){ 
			 Map<String, Object> map = new HashMap<String, Object>();
			 String[] rows = line.split("\t");
			 for (int i = 0; i < colums.length; i++) {
				 if( (rows.length-1) >= i)
					 map.put( colums[i],rows[i] ) ;
				 else
					 map.put( colums[i],"" ) ;
			}
			 config.add(map);
			 line = dr.readLine();
		 } 
		 input.close();
		 return true ;
		 }else{
			 return false ;
		 }
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
	public void setSource(URL conn) {
		this.url = conn ;
	}

	@Override
	public URL getSource() {
		return this.url ;
	}

	@Override
	public void setLoadCommit(String commit) {
		this.commit = commit ;
	}
	
}
