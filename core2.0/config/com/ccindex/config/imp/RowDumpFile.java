package com.ccindex.config.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowDump;

public class RowDumpFile implements RowDump<File,List<Map<String,Object>>>  {

	private String[] colums ;
	private String split ;
	
	private List<Map<String, Object>> data ;
	private File dumpToFile ;
	
	@Override
	public void dump() throws Exception {
		
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(dumpToFile),"UTF-8");
//		FileWriter fw = new FileWriter(dumpToFile);
		
		for (Map<String, Object> map : data) {
			for (int i = 0; i < colums.length; i++) {
				if(map.containsKey(colums[i])){
					fw.write(map.get( colums[i] )+ split );
					fw.flush();
				}else{
					fw.write(""+ split );
					fw.flush();
				}
			}
			fw.write("\n");
			fw.flush();
		}
		
		fw.close();
	}

	@Override
	public void setColums(String... colums) {
		this.colums = colums ;
	}
	
	@Override
	public void setSplit(String split){
		this.split = split ;
	}

	@Override
	public void setData(List<Map<String, Object>> t) {
		this.data = t ;
	}

	@Override
	public void dumpTo(File to) {
		this.dumpToFile = to ;
	}
}
