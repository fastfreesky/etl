package com.ccindex.mInterface;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @ClassName: Output
 * @Description: TODO(这里用一句话描述这个类的作用)输出接口
 * @author tianyu.yang
 * @date 2012-12-17 下午4:34:31
 * 
 */
public interface Output {

	/**
	 * 
	 * @Title: setConfig
	 * @Description: TODO(这里用一句话描述这个方法的作用)输出的配置类
	 * @param conf
	 *            void
	 * @throws
	 */
	public void setConfig(ConfContext conf);

	/**
	 * 
	 * @Title: write
	 * @Description: TODO(这里用一句话描述这个方法的作用)数据写入
	 * @param key
	 * @param value
	 * @throws IOException
	 *             void
	 * @throws
	 */
	public void write(String key, String value) throws IOException;

	public void close() throws IOException;
}
