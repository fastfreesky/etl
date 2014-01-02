package com.ccindex.conf;

public class ProcessCount {
	// 对应的bean
	private String bean;
	// 解压之后的文件匹配到的大小
	private long destFileUnzipHitSize;
	// 匹配该模式的数据条数
	private long destFileUnzipHitCount;
	// 任务起始时间,使用long型
	private long startTime;
	// 任务终止时间
	private long endTime;

	public void clear() {
		destFileUnzipHitSize = 0l;
		destFileUnzipHitCount = 0l;
		startTime = 0l;
		endTime = 0l;
		bean = null;
	}

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		if (this.bean == null) {
			this.bean = bean;
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public ProcessCount() {
		clear();
	}

	public long getDestFileUnzipHitSize() {
		return destFileUnzipHitSize;
	}

	public void setDestFileUnzipHitSize(long destFileUnzipHitSize) {
		this.destFileUnzipHitSize += destFileUnzipHitSize;
	}

	public long getDestFileUnzipHitCount() {
		return destFileUnzipHitCount;
	}

	public void setDestFileUnzipHitCount() {
		this.destFileUnzipHitCount += 1;
	}

	public void setStartTime() {
		if (startTime == 0l) {
			this.startTime = System.currentTimeMillis();
		}
	}

	public void setEndTime() {
		this.endTime = System.currentTimeMillis();
	}

	public long getTimeDiff() {
		return endTime - startTime;
	}

}
