package com.ccindex.config;

/**
 * 
 * @ClassName: Row 
 * @Description: TODO(这里用一句话描述这个类的作用) config row 类型 
 * @author  kaiyi.liu
 * @date 2013-6-4 下午1:59:07 
 */
public interface Row<T> {

	/**
	 * 
	 * @Title: setColums 
	 * @Description: TODO(这里用一句话描述这个方法的作用) title 名称 
	 * @param colums    
	 * void
	 * @throws
	 */
	public void setColums(String ... colums);
	
	/**
	 * 
	 * @Title: setCommit 
	 * @Description: TODO(这里用一句话描述这个方法的作用)  字段提取命令
	 * @param split    
	 * void
	 * @throws
	 * 
	 * <pre>
	 *  txt commit = split string 比如 : tab 
	 *  db commit = sql 
	 * </pre>
	 */
	public void setSplit(String split);


}
