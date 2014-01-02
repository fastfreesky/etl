package com.ccindex.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ccindex.conf.BeanConfQueue;
import com.ccindex.conf.ProcessConfQueue;
import com.ccindex.factory.conf.BeanConfEtl;
import com.ccindex.factory.conf.ProcessConfEtl;
import com.ccindex.mInterface.Output;
import com.ccindex.mInterface.OctopusConf;
import com.ccindex.mInterface.Task;
import com.ccindex.tool.ParseYamlConf;
import com.ccindex.util.factory.ObjectFactory;

/**
 * 
 * @ClassName: SetConf
 * @Description: TODO(这里用一句话描述这个类的作用)设置配置类
 * @author tianyu.yang
 * @date 2013-1-15 下午5:30:06
 * 
 */
public class SetConfEtl implements OctopusConf {

	/*
	 * process配置文件
	 */
	private ProcessConfEtl processConfQueue;
	/**
	 * bean配置文件
	 */
	private BeanConfEtl beanConfQueue;
	/**
	 * out输出配置文件
	 */
	private Output outPut;
	/**
	 * at任务配置文件
	 */
	private Task task;

	/**
	 * bean与process的对应关系
	 */
	private HashMap<String, ArrayList<String>> beanProcessRelation;

	/**
	 * 清洗日期
	 */
	private String etlDate;
	// 线程数
	int threadNum;

	private static Logger logger = Logger.getLogger(SetConfEtl.class);

	public SetConfEtl(ParseYamlConf conf) {

		setThreadNum(conf.getThreadNum());

		// 任务添加
		Task task = (Task) ObjectFactory.newInstance(conf.getTaskClass());
		task.setConfig(conf.getAddFileTaskConfContext());
		setTask(task);
		task = null;

		// 输出配置
		Output outPut = (Output) ObjectFactory.newInstance(conf.getOutClass());
		outPut.setConfig(conf.getWriteenFileConfContext());
		setOutPut(outPut);
		outPut = null;

		// bean配置
		BeanConfEtl beanConfEtl = new BeanConfEtl();
		beanConfEtl.setConf(conf.getBeanConfContext());
		setBeanConfQueue(beanConfEtl);
		beanConfEtl = null;

		// process配置
		ProcessConfEtl processConfEtl = new ProcessConfEtl();
		processConfEtl.setConf(conf.getProcessConfContext());
		setProcessConfQueue(processConfEtl);
		processConfEtl = null;

		// bean及process关系配置
		setBeanProcessRelation(conf.getBeanProcessRelation());

		setEtlDate(conf.getEtlDate());
	}

	@Override
	public BeanConfQueue getBeanConfQueue() {
		// TODO Auto-generated method stub
		return beanConfQueue;
	}

	@Override
	public Output getOutPut() {
		// TODO Auto-generated method stub
		return outPut;
	}

	@Override
	public ProcessConfQueue getProcessConfQueue() {
		// TODO Auto-generated method stub
		return processConfQueue;
	}

	@Override
	public Task getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	@Override
	public int getThreadNum() {
		// TODO Auto-generated method stub
		return threadNum;
	}

	public void setBeanConfQueue(BeanConfEtl beanConfQueue) {
		this.beanConfQueue = beanConfQueue;
	}

	public void setOutPut(Output outPut) {
		this.outPut = outPut;
	}

	public void setProcessConfQueue(ProcessConfEtl processConfQueue) {
		this.processConfQueue = processConfQueue;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	@Override
	public HashMap<String, ArrayList<String>> getBeanProcessRelation() {
		// TODO Auto-generated method stub
		return beanProcessRelation;
	}

	public void setBeanProcessRelation(
			HashMap<String, ArrayList<String>> beanProcessRelation) {
		this.beanProcessRelation = beanProcessRelation;
	}

	public void close() {
		task.close();
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

	@Override
	public String getEtlDate() {
		// TODO Auto-generated method stub
		return etlDate;
	}

}
