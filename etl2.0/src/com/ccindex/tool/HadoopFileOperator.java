package com.ccindex.tool;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopFileOperator {

	public static boolean isExists(String file) {

		try {
			Path filePath = new Path(file);
			Configuration conf = new Configuration();
			FileSystem hdfs = FileSystem.get(conf);
			return hdfs.exists(filePath);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static long getHadoopFileSize(String file) {

		try {
			Path filePath = new Path(file);
			Configuration conf = new Configuration();
			FileSystem hdfs = FileSystem.get(conf);
			return hdfs.getLength(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0l;
	}
}
