package com.ccindex.factory.output;

import java.util.HashMap;

import com.ccindex.database.OutList;
import com.ccindex.mInterface.ConfContext;

public class WriteenFileConfContext implements ConfContext {
	
	/**
	 * 初次申请的文件缓存
	 */
	private int buffMem;
	/**
	 * 可申请的最大文件缓存
	 */
	private int maxbuffMem;
	/**
	 * 升级的次数,即达到该次数时,申请内存进行扩容
	 */
	private int level;
	
	private HashMap<String, OutList> outList;
	
	public int getBuffMem() {
		return buffMem;
	}
	public void setBuffMem(int buffMem) {
		this.buffMem = buffMem;
	}
	public int getMaxbuffMem() {
		return maxbuffMem;
	}
	public void setMaxbuffMem(int maxbuffMem) {
		this.maxbuffMem = maxbuffMem;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public HashMap<String, OutList> getOutList() {
		return outList;
	}
	
	public void setOutList(HashMap<String, OutList> outList) {
		this.outList = outList;
	}

	
}
