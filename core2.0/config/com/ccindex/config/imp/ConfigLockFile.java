package com.ccindex.config.imp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import com.ccindex.config.ConfigLock;
/**
 * 
 * @ClassName: FileProgrameLock 
 * @Description: TODO(这里用一句话描述这个类的作用) ConfigLock实现类
 * <pre>
 *  
 *  参考： http://sosuny.iteye.com/blog/704587
 *  
 * </pre>
 * @author  kaiyi.liu
 * @date 2013-6-3 下午12:05:33 
 */
public class ConfigLockFile implements  ConfigLock<File>{
	
	private File lockFile ; 
	
    FileChannel channel = null;  
    private FileLock lock = null;  
    
    
    /** 
     * 获取锁资源 
     * @return true成功锁定目标资源 ,false锁定操作失败 
     * */  
    @Override
    public void locked(long time,boolean shared) throws IOException {
    	long num = time / 100  ;
    	while(!__lock(shared)){
			if( --num  <= 0 )
				throw new IOException("lock time out");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }  
    
    private boolean __lock(boolean shared)  throws IOException {
		channel = new RandomAccessFile(lockFile, "rw").getChannel();
		try {
			lock = channel.lock(0,Long.MAX_VALUE,shared);
			return true;
		} catch (OverlappingFileLockException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
    }
  
    /** 
     * 释放锁 
     * */  
    @Override
    public void unlock() {  
        try {  
            if(lock != null){  
                lock.release();  
            }  
            if(channel != null && channel.isOpen()){  
                channel.close();  
            }  
            lock = null;  
            channel = null;  
        } catch (IOException e) {  
        }  
    }  
      
    
    @Override
    public void initResourcesLock(File tf) throws Exception{  
    	this.lockFile = tf ;
        try{  
            if(! tf.exists()){  
                tf.createNewFile();  
            }  
        }catch(IOException e){  
            throw e;  
        }  
    }  
	
}
