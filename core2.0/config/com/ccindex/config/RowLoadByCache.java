package com.ccindex.config;


/**
 * 
 * @ClassName: RowLoadByCache 
 * @Description: 
 * <pre>
 * 	         加载耗时配置 到 非耗时地方 ，并且接口继承类需保证 多进程、多线程并发获取配置时，只有第一个进程或线程去拉取资源配置
 *     非第一个进程或线程有下面不同两类处理逻辑：
 *       a. 拥塞在锁后，会等待一定时间获取锁后并读去读取本地缓存配置
 *       b. 非拥塞锁前（本地缓存配置资源已经形成），直接读取本地缓存配置即可
 * </pre>
 * @author  kaiyi.liu
 * @date 2013-6-4 下午3:20:47 
 *
 * @param <F>
 * @param <T>
 * @param <E>
 */
public interface RowLoadByCache<F,T,E> extends Row<T> {
	
	/**
	 * 
	 * @Title: setSource 
	 * @Description: TODO(这里用一句话描述这个方法的作用) get/set 源数据地址
	 * @param f    
	 * void
	 * @throws
	 */
	public void setSource(F f);
	public RowLoad<F,E> getRowLoad();
	public void setLoadCommit(String commit);
	
	/**
	 * 
	 * @Title: setCacheSource 
	 * @Description: TODO(这里用一句话描述这个方法的作用) set cache 地址
	 * @param t    
	 * void
	 * @throws
	 */
	public void setCacheSource(T t);
	public RowLoad<T,E>  getCacheRowLoad();
	public RowDump<T,E>  getCacheRowDump();
	
	/**
	 * 
	 * @Title: existsCacheSource 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 判断本地缓存是否存在 
	 * @return    
	 * boolean
	 * @throws
	 */
	public boolean existsCacheSource();
	
	/**
	 * 
	 * @Title: setLock 
	 * @Description: TODO(这里用一句话描述这个方法的作用) set 锁
	 * @param lock
	 * @throws Exception    
	 * void
	 * @throws
	 */
	public void setLock(T lock) throws Exception;
	public ConfigLock<T> getLock() throws Exception;
	
	/**
	 * 
	 * @Title: getData 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * 	   加载耗时或远程配置至易加载缓存地址，且保证在多进程、多线程时只有一个 进程或线程去拉取资源配置到 内存中
	 * @return    
	 * E
	 * @throws
	 */
	public E getData();
	
	
}
