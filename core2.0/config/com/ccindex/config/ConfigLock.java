package com.ccindex.config;

import java.io.IOException;

/**
 * 
 * @ClassName: ConfigLock 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * 		多进程读取配置文件到本地时，使用本地文件锁达到只允许一个进程去中心获取配置内容。其他进程拥塞等待 
 * @author  kaiyi.liu
 * @date 2013-6-3 下午12:03:20 
 *
 */
public interface ConfigLock<T> {
	
	/**
	 * 
	 * @Title: initResourcesLock 
	 * @Description: TODO(这里用一句话描述这个方法的作用)  初始化 ，需要的锁资源
	 * @param t
	 * @throws Exception    
	 * void
	 * @throws
	 */
	public void initResourcesLock(T t)throws Exception;
	

	/**
	 * 
	 * @Title: locked 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取锁资源
	 * @param millisecond
	 * @throws IOException    
	 * void
	 * @throws
	 * <pre>
	 * 成功锁定目标资源 ,false锁定操作失败
	 * lock 拥塞
	 * millisecond 获得锁超时
	 * shared 是否获得共享锁(文件只读锁)
	 * </pre>
	 */
	public void locked(long millisecond,boolean shared) throws IOException;

	/**
	 * 
	 * @Title: unlock 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 释放锁  
	 * void
	 * @throws
	 */
	public void unlock();
	
	
}
