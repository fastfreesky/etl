package com.ccindex.struc.queue;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

public class FileQueue extends LinkedBlockingQueue<File> {
	
	public  FileQueue(int capacity){
		super(capacity);
	}
	
	@Override
	public void put(File file){
		try {
			super.put(file);
			//System.err.println( " input file " + file.toString() );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
