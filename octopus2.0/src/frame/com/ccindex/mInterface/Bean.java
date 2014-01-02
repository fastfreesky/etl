package com.ccindex.mInterface;

import com.ccindex.util.cache.Cache;

/**
 * 
 * @ClassName: Bean 
 * @Description: TODO(这里用一句话描述这个类的作用)清洗模块的文件清洗框架 接口 
 * @author  tianyu.yang
 * @date 2012-12-17 下午4:04:44 
 * 该接口，旨在从大量数据中清洗出可用字段，以满足之后的Process的全部需求为准
 */
public interface Bean extends Cloneable {
	/**
	 * 
	 * @Title: filling 
	 * @Description: TODO(这里用一句话描述这个方法的作用)清洗判断，判定输入的task是否符合对应的正则匹配规则 
	 * @param task 任务名称,从中提取设备名字
	 * @param values
	 * @return    
	 * boolean
	 * @throws
	 */
	public boolean filling(String task,Object values);
	
	/**
	 *
	 * @Title: format 
	 * @Description: TODO(这里用一句话描述这个方法的作用)格式化输出清洗的字段 
	 * @return    
	 * String
	 * @throws
	 */
	public String format();
	
}
