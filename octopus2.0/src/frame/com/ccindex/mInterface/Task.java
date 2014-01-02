package com.ccindex.mInterface;

import java.util.Map;

/**
 * 
 * @ClassName: Task 
 * @Description: TODO(这里用一句话描述这个类的作用)添加任务接口，泛型类 
 * @author  tianyu.yang
 * @date 2012-12-17 下午12:07:10 
 * 
 * @param <T>
 */
public interface Task<T> {

	/**
	 * 
	 * @Title: setConfig 
	 * @Description: TODO(这里用一句话描述这个方法的作用)设置配置文件 
	 * @param conf    
	 * void
	 * @throws
	 */
	public void setConfig(ConfContext conf);
	
	/**
	 * 
	 * @Title: addTasks 
	 * @Description: TODO(这里用一句话描述这个方法的作用)添加全部任务，通过配置文件，读取配置文件目录下的任务     
	 * void
	 * @throws
	 */
	public void addTasks();
	/**
	 * 
	 * @Title: addTask 
	 * @Description: TODO(这里用一句话描述这个方法的作用)添加指定任务 
	 * @param t    
	 * void
	 * @throws
	 */
	public void addTask(T t);
	
	/**
	 * 
	 * @Title: getTask 
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取任务，按照队列方式获取 
	 * @return    
	 * T
	 * @throws
	 */
	public T getTask();
	

	/**
	 * 
	 * @Title: isOver 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 判断目前任务是否执行完成
	 * @return    
	 * boolean
	 * @throws
	 */
	public boolean isOver();
	
	/**
	 * 
	 * @Title: overTask 
	 * @Description: TODO(这里用一句话描述这个方法的作用)记录任务的执行状态 
	 * @param t
	 * @param status    
	 * void
	 * @throws
	 */
	public void overTask(T t,String status);
	
	/**
	 * 
	 * @Title: close 
	 * @Description: TODO(这里用一句话描述这个方法的作用)关闭操作,如文件句柄数据库等     
	 * void
	 * @throws
	 */
	public void close();	
	
}
