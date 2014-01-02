package com.ccindex.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ccindex.mInterface.Process;

/**
 * 
 * @ClassName: OctopusCount
 * @Description: TODO(这里用一句话描述这个类的作用)章鱼模块的计数模块
 * @author tianyu.yang
 * @date 2013-8-27 下午3:31:14
 * 
 */
public class OctopusCount {
	// 原始文件大小,单位为字节B
	private long srcFileSize;
	// 任务运行的设备
	private String hostNameRun;
	// 原始文件路径
	private String srcFilePath;
	// 解压的文件原始条数
	private long srcFileUnzipCount;
	// 按照process进行存储的记录
	private HashMap<String, ProcessCount> processCount;
	/**
	 * 清洗日期
	 */
	private String etlDate;

	// 格式化输出
	private ArrayList<String> outFormat;

	private StringBuffer buf = new StringBuffer();

	public HashMap<String, ProcessCount> getProcessCount() {
		return processCount;
	}

	public OctopusCount(Object[] objects) {
		processCount = new HashMap<String, ProcessCount>(objects.length);
		for (Object p : objects) {
			processCount.put(p.toString(), new ProcessCount());
		}

		outFormat = new ArrayList<String>(objects.length);
	}

	public ArrayList<String> format() {
		// 清洗类型,文件总行数 ，匹配行数 ，原文件大小, 匹配总大小,处理起始时间，处理结束时间,本机主机名
		outFormat.clear();

		Iterator<Entry<String, ProcessCount>> itr = processCount.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, ProcessCount> entry = itr.next();
			ProcessCount pc = entry.getValue();
			if (pc.getBean() == null) {
				continue;
			}
			buf.setLength(0);
			buf.append(entry.getKey()).append(".").append(pc.getBean())
					.append(",").append(getEtlDate()).append(",")
					.append(getSrcFileUnzipCount()).append(",")
					.append(pc.getDestFileUnzipHitCount()).append(",")
					.append(getSrcFileSize()).append(",")
					.append(pc.getDestFileUnzipHitSize()).append(",")
					.append(pc.getStartTime()).append(",")
					.append(pc.getEndTime()).append(",")
					.append(getHostNameRun());
			outFormat.add(buf.toString());
		}

		return outFormat;
	}

	public void clear() {
		srcFileSize = 0l;
		srcFileUnzipCount = 0l;
		hostNameRun = null;
		srcFilePath = null;

		Iterator<Entry<String, ProcessCount>> itr = processCount.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<String, ProcessCount> entry = itr.next();
			entry.getValue().clear();
		}
	}

	public long getSrcFileUnzipCount() {
		return srcFileUnzipCount;
	}

	public void setSrcFileUnzipCount() {
		this.srcFileUnzipCount += 1;
	}

	public long getSrcFileSize() {
		return srcFileSize;
	}

	public void setSrcFileSize(long srcFileSize) {
		this.srcFileSize = srcFileSize;
	}

	public String getHostNameRun() {
		return hostNameRun;
	}

	public void setHostNameRun(String hostNameRun) {
		this.hostNameRun = hostNameRun;
	}

	public String getSrcFilePath() {
		return srcFilePath;
	}

	public void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}

	public String getEtlDate() {
		return etlDate;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

}
