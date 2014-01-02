package com.ccindex.config.imp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.yaml.snakeyaml.Yaml;

public class ConfigDumpYaml {

	private Yaml yaml = new Yaml(); 
	
	public void dump(Object load, String toFile) {
		Writer writer = null ;
		try {
			writer = new FileWriter(new File(toFile));
			yaml.dump(load,writer);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
}