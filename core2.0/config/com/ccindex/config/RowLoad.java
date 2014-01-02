package com.ccindex.config;

/**
 * 
 * @ClassName: RowLoad 
 * @Description: TODO(这里用一句话描述这个类的作用) load 数据到内存
 * @author  kaiyi.liu
 * @date 2013-6-4 下午2:46:54 
 * 
 * @param <T>
 * @param <E>
 */
public interface RowLoad<T,E> extends Row  {
	

	/**
	 * 
	 * @Title: setSource 
	 * @Description: TODO(这里用一句话描述这个方法的作用) set  源数据地址 
	 * @param conn    
	 * void
	 * @throws
	 */
	public void setSource(T conn);
	
	/**
	 * 
	 * @Title: getSource 
	 * @Description: TODO(这里用一句话描述这个方法的作用) getSource
	 * @return    
	 * T
	 * @throws
	 */
	public T getSource();
	/**
	 * 
	 * @Title: load 
	 * @Description: TODO(这里用一句话描述这个方法的作用) load 动作
	 * @return
	 * @throws Exception    
	 * boolean
	 * @throws
	 */
	public boolean load() throws Exception;
	public void setLoadCommit(String commit) ;
	
	/**
	 * 
	 * @Title: getData 
	 * @Description: TODO(这里用一句话描述这个方法的作用) getData
	 * @return    
	 * E
	 * @throws
	 */
	public  E getData();
	
}
