package com.ccindex.factory.output;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

public class HdfsWritten implements Written {

	private FSDataOutputStream out;
	private Lock lock = new ReentrantLock();
	private static Logger logger = Logger.getLogger(HdfsWritten.class);
	private String filename;

	public HdfsWritten(String filename) {
		this.filename = filename;
		Configuration config = new Configuration();
		Path inPath = new Path(filename);
		FileSystem hdfs;
		try {
			hdfs = FileSystem.get(config);
			out = hdfs.create(inPath);
			// if (hdfs.exists(inPath)) {
			// out = hdfs.append(inPath);
			// } else {
			// out = hdfs.create(inPath);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("createFile: " + filename);
	}

	@Override
	public void write(String line) throws IOException {
		// TODO Auto-generated method stub
		lock.lock();
		out.write(line.getBytes());
		lock.unlock();
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		out.close();
		logger.info("closeFile:" + filename);		
	}

}
