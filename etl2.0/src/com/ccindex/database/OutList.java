package com.ccindex.database;

import java.io.Serializable;

import com.ccindex.constant.Constant;
import com.ccindex.factory.util.file.CommUtil;

/**
 * 
 * @ClassName: OutList
 * @Description: TODO(这里用一句话描述这个类的作用) 清洗输出对应的路径
 * @author tianyu.yang
 * @date 2013-2-18 上午10:56:40
 * 
 */
public class OutList implements Serializable {

	// 清洗的bean,本地路径,hdfs路径
	private String bean;
	private String local_path;
	private String hdfs_path;
	// 清洗程序
	private String process;
	// 用于process.bean确定的key值
	private String key;
	// 清洗数据的日期
	private String date;

	//process-对应清洗类型,hive_database输出的表,hive_tmp-日志输出Hive中间表,
	// hive_out-日志输出Hive最终表
	private String hive_tmp = null, hive_databse = null,
			hive_out = null;

	public enum ETLOUTWAY {
		local, hdfs, all;
	}

	private ETLOUTWAY outType;

	public ETLOUTWAY getOutType() {
		return outType;
	}

	public void setOutType(ETLOUTWAY outType) {
		this.outType = outType;
	}

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	public String getLocal_path() {
		return local_path;
	}

	public void setLocal_path(String local_path) {
		if (local_path != null) {
			CommUtil.mkDirs(local_path, true);
		}
		this.local_path = local_path;
	}

	public String getHdfs_path() {
		return hdfs_path;
	}

	public void setHdfs_path(String hdfs_path) {
		if (hdfs_path != null) {
			CommUtil.mkDirs(hdfs_path, false);
		}
		this.hdfs_path = hdfs_path;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}


	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date.replace("-", "");
	}

	// key为文件的名字,格式为日期.设备.清洗类型.日志格式.[local|hdfs]
	public String getLocalFile() {

		if (getBean().equalsIgnoreCase("default")) {
			return CommUtil
					.packageFilePath(getLocal_path(), CommUtil.packageFileName(
							"default", Constant.getHostname(), getProcess(),
							getBean(), ETLOUTWAY.local.toString()));
		} else {
			return CommUtil
					.packageFilePath(getLocal_path(), CommUtil.packageFileName(
							getDate(), Constant.getHostname(), getProcess(),
							getBean(), ETLOUTWAY.local.toString()));

		}
	}

	// key为文件的名字,格式为日期.设备.清洗类型.日志格式.[local|hdfs]
	public String getHdfsFile() {
		if (getBean().equalsIgnoreCase("default")) {
			return CommUtil
					.packageFilePath(getHdfs_path(), CommUtil.packageFileName(
							"default", Constant.getHostname(), getProcess(),
							getBean(), ETLOUTWAY.hdfs.toString()));
		} else {
			return CommUtil
					.packageFilePath(getHdfs_path(), CommUtil.packageFileName(
							getDate(), Constant.getHostname(), getProcess(),
							getBean(), ETLOUTWAY.hdfs.toString()));

		}

	}

	public String getHostName() {
		return Constant.getHostname();
	}


	public String getHive_tmp() {
		return hive_tmp;
	}

	public void setHive_tmp(String hive_tmp) {
		this.hive_tmp = hive_tmp;
	}

	public String getHive_databse() {
		return hive_databse;
	}

	public void setHive_databse(String hive_databse) {
		this.hive_databse = hive_databse;
	}

	public String getHive_out() {
		return hive_out;
	}

	public void setHive_out(String hive_out) {
		this.hive_out = hive_out;
	}

	@Override
	public String toString() {
		return "OutList [bean=" + bean + ", local_path=" + local_path
				+ ", hdfs_path=" + hdfs_path + ", process=" + process
				+ ", key=" + key + ", date=" + date + ", hive_tmp=" + hive_tmp
				+ ", hive_databse=" + hive_databse + ", hive_out=" + hive_out
				+ ", outType=" + outType + "]";
	}

	
}
