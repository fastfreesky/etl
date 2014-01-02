package com.ccindex.mInterface;

public interface Process {

	/**
	 * 
	 * @Title: setConfig
	 * @Description: TODO(这里用一句话描述这个方法的作用)配置文件
	 * @param conf
	 *            void
	 * @throws
	 */
	public void setConfig(ConfContext conf);

	/**
	 * 处理程序主体
	 * 
	 * @param task
	 *            ：处理文件的任务，一般为处理文件的存放目录
	 * @param bean
	 *            ：该参数为对传入文件进行第一次清洗，确定最大字段个数
	 * @param out
	 *            ：处理程序的输出配置
	 * @param args
	 *            ：新增的额外参数
	 * @return
	 * @throws Exception
	 */
	public int processing(String task, Bean bean, Output out, String... args)
			throws Exception;

}
