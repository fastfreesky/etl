package com.ccindex.struc.queue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @ClassName: LoadTypeGather
 * @Description: TODO(这里用一句话描述这个类的作用)上载日志类型集合
 * @author tianyu.yang
 * @date 2013-3-7 上午10:13:13
 * 
 */
public class LoadTypeGather {
	// 设备编号
	private String hostname = null;
	// 部门
	private String business = null;
	// 日志类型
	private String logType = null;
	// 清洗类型
	private String process = null;
	// 上载大小 单位为M
	private long loadSize = 0;
	// 上载日志来源
	private String outWay = null;
	// 清洗数据日期
	private String etlDate = null;

	public String getOutWay() {
		return outWay;
	}

	public void setOutWay(String outWay) {
		this.outWay = outWay;
	}

	// 上载库
	private String hiveDatabase = null;
	// 上载中间表
	private String hiveTmp = null;
	// 上载最终表
	private String hiveOut = null;
	// 上载起始时间
	private String loadStart = null;
	// 上载终止时间
	private String loadEnd = null;
	// 转换起始时间
	private String changeStart = null;
	// 转换终止时间
	private String changeEnd = null;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public long getLoadSize() {
		return loadSize;
	}

	public void setLoadSize(long loadSize) {
		this.loadSize = loadSize;
	}

	public String getHiveDatabase() {
		return hiveDatabase;
	}

	public void setHiveDatabase(String hiveDatabase) {
		this.hiveDatabase = hiveDatabase;
	}

	public String getHiveTmp() {
		return hiveTmp;
	}

	public void setHiveTmp(String hiveTmp) {
		this.hiveTmp = hiveTmp;
	}

	public String getHiveOut() {
		return hiveOut;
	}

	public void setHiveOut(String hiveOut) {
		this.hiveOut = hiveOut;
	}

	public String getLoadStart() {
		return loadStart;
	}

	public void setLoadStart(String loadStart) {
		this.loadStart = loadStart;
	}

	public String getLoadEnd() {
		return loadEnd;
	}

	public void setLoadEnd(String loadEnd) {
		this.loadEnd = loadEnd;
	}

	public String getChangeStart() {
		return changeStart;
	}

	public void setChangeStart(String changeStart) {
		this.changeStart = changeStart;
	}

	public String getChangeEnd() {
		return changeEnd;
	}

	public void setChangeEnd(String changeEnd) {
		this.changeEnd = changeEnd;
	}

	public String getKey() {
		return this.hostname + this.business + this.logType + this.process;
	}

	public String getEtlDate() {
		return etlDate;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

	@Override
	public String toString() {
		Date start = new Date(System.currentTimeMillis());
		String day = fm.format(start);

		return day + " [" + etlDate + "," + hostname + "," + business + ","
				+ logType + "," + process + "," + loadSize + "," + loadStart
				+ "," + loadEnd + "," + changeStart + "," + changeEnd + ","
				+ outWay + "," + hiveDatabase + "," + hiveTmp + "," + hiveOut
				+ "]\n";
	}

	private DateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

}
