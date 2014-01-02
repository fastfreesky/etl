package com.ccindex.struc.queue;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @ClassName: FileTypeQueue 
 * @Description: TODO(这里用一句话描述这个类的作用)包含文件及文件属性的队列集合 
 * @author  tianyu.yang
 * @date 2013-1-9 下午3:10:02 
 *
 */
public class FileTypeQueue extends LinkedBlockingQueue<FileTypeGather> {
	
	public  FileTypeQueue(int capacity){
		super(capacity);
	}
	
	@Override
	public void put(FileTypeGather file){
		try {
			super.put(file);
			//System.err.println( " input file " + file.toString() );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
