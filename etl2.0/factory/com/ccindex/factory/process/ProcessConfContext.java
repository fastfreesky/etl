package com.ccindex.factory.process;

import com.ccindex.mInterface.ConfContext;

public class ProcessConfContext implements ConfContext {

	//输出类型
	private String etlOutWay;
	//设备
	private String hostName;
	//日期.设备.清洗类型.日志格式.[local|hdfs]

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getEtlOutWay() {
		return etlOutWay;
	}

	public void setEtlOutWay(String etlOutWay) {
		this.etlOutWay = etlOutWay;
	}

	
}
