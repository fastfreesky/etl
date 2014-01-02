package com.ccindex.config;

/**
 * 
 * @ClassName: ConfigLoad 
 * @Description: TODO(这里用一句话描述这个类的作用) dump 配置文件出来相应地方 
 * @author  kaiyi.liu
 * @date 2013-6-3 下午2:29:02 
 */
public interface RowDump<E,T>  extends Row {
	
	/**
	 * 
	 * @Title: setData 
	 * @Description: TODO(这里用一句话描述这个方法的作用) set 数据加载至内存 
	 * @param t    
	 * void
	 * @throws
	 */
	public void setData(T t);
	
	/**
	 * 
	 * @Title: dumpTo 
	 * @Description: TODO(这里用一句话描述这个方法的作用) set 数据dump到相应地址
	 * @param to    
	 * void
	 * @throws
	 */
	public void dumpTo(E to);
	
	/**
	 * 
	 * @Title: dump 
	 * @Description: TODO(这里用一句话描述这个方法的作用) dump 动作
	 * @param file    
	 * void
	 * @throws
	 */
	public void dump() throws Exception;
	
}


