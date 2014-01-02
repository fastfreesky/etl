package com.ccindex.struc.queue;

import java.io.Serializable;

/**
 * 
 * @ClassName: BuLogType 
 * @Description: TODO(这里用一句话描述这个类的作用)事业部及日志类型类 
 * @author  tianyu.yang
 * @date 2013-2-6 下午3:29:16 
 *
 */
public class BuLogType implements Serializable{
	//BU 事业部
	private String bu="No";
	//LogTye 日志类型
	private String logType="No";

	public BuLogType(){
	}
	
	public BuLogType(String bu, String logType){
		this.bu = bu;
		this.logType = logType;		
	}
	
	public String getBu() {
		return bu;
	}
	public void setBu(String bu) {
		this.bu = bu;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogTypeBu(){
		return logType+"."+bu;
	}
	@Override
	public String toString() {
		return "BuLogType [bu=" + bu + ", logType=" + logType + "]";
	}
	
	

}
