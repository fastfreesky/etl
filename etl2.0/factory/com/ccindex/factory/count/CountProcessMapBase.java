package com.ccindex.factory.count;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ccindex.mInterface.Output;

/**
 * 注意 ： 为了性能 ，非线程安全
 * 
 * @author user
 * 
 * @param <T>
 */
public abstract class CountProcessMapBase<T> {

	Map<String, T> map = new HashMap<String, T>();
	String filename = null;

	/**
	 * 设置传出的文件名称
	 * 
	 * @param filename
	 */
	public void SetOutFileName(String filename) {
		if (this.filename == null)
			this.filename = filename;
	}

	/**
	 * 
	 * @Title: isExists
	 * @Description: TODO(这里用一句话描述这个方法的作用)是否已经存在该Key值
	 * @param key
	 * @return boolean
	 * @throws
	 */
	public boolean isExists(String key) {
		return map.containsKey(key);
	}

	/**
	 * 
	 * @Title: getValue
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取已经包含的值
	 * @param key
	 * @return T-null为不包含该键值
	 * @throws
	 */
	public T getValue(String key) {
		return map.get(key);
	}
	
	/**
	 * 
	 * @Title: getMaps 
	 * @Description: TODO(这里用一句话描述这个方法的作用)返回整体hashmap数据 
	 * @return    
	 * Map<String,T>
	 * @throws
	 */
	public Map<String, T> getMaps(){
		return map;
	}

	/**
	 * 更新数据
	 * 
	 * @param value
	 *            值
	 * @param key
	 *            键
	 */
	public void SetValue(T value, String key) {
		if (!isExists(key)) {
			map.put(key, value);
		}
	}

	/**
	 * 输出Hash值到指定文件
	 * 
	 * @throws IOException
	 */
	public abstract void OutStream(Output out) throws IOException;
}
