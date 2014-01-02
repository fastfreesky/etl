package com.ccindex.mInterface;

import java.util.ArrayList;
import java.util.HashMap;

import com.ccindex.conf.BeanConfQueue;
import com.ccindex.conf.ProcessConfQueue;

/**
 * 
 * @ClassName: OctopusConf
 * @Description: TODO(这里用一句话描述这个类的作用)章鱼框架的配置接口类
 * @author tianyu.yang
 * @date 2013-8-20 下午3:16:32
 * 
 */
public interface OctopusConf {

	/**
	 * 
	 * @Title: getThreadNum
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取即将实例化的线程数量
	 * @return int
	 * @throws
	 */
	public int getThreadNum();

	/**
	 * 
	 * @Title: getTask
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取执行任务的任务类,已经完成实例化
	 * @return Task
	 * @throws
	 */
	public Task getTask();

	/**
	 * 
	 * @Title: getBeanConfQueue
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取实例化好的bean类列表
	 * @return BeanConfQueue
	 * @throws
	 */
	public BeanConfQueue getBeanConfQueue();

	/**
	 * 
	 * @Title: getProcessConfQueue
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取实例化好的process类列表
	 * @return ProcessConfQueue
	 * @throws
	 */
	public ProcessConfQueue getProcessConfQueue();

	/**
	 * 
	 * @Title: getOutput
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取实例化好的输出类
	 * @return Output
	 * @throws
	 */
	public Output getOutPut();

	/**
	 * 
	 * @Title: getBeanProcessRelation
	 * @Description: 
	 *               TODO(这里用一句话描述这个方法的作用)获取bean和process的对应关系,一个bean可以使用多个process进行处理
	 * @return HashMap<String,ArrayList<String>> key值为bean对应的name,
	 *         value为process对应的name
	 * @throws
	 */
	public HashMap<String, ArrayList<String>> getBeanProcessRelation();

	/**
	 * 
	 * @Title: getEtlDate
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取清洗数据的时间
	 * @return String
	 * @throws
	 */
	public String getEtlDate();
}
