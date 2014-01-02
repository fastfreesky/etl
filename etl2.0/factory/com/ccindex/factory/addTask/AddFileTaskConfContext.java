package com.ccindex.factory.addTask;

import java.util.ArrayList;

import com.ccindex.mInterface.ConfContext;

public class AddFileTaskConfContext implements ConfContext {

	private String breakPointFile;
	private String processFile;
	private ArrayList<String> inputFilePath;
	int maxTime = 60 * 1000, loopTime = 5 * 1000;
	private String killDate;
	/**
	 * 用于匹配文件的后缀正则
	 */
	private String regularFileSuffix;

	public String getRegularFileSuffix() {
		return regularFileSuffix;
	}

	public void setRegularFileSuffix(String regularFileSuffix) {
		this.regularFileSuffix = regularFileSuffix;
	}

	public String getBreakPointFile() {
		return breakPointFile;
	}

	public void setBreakPointFile(String breakPointFile) {
		this.breakPointFile = breakPointFile;
	}

	public String getProcessFile() {
		return processFile;
	}

	public void setProcessFile(String processFile) {
		this.processFile = processFile;
	}

	public ArrayList<String> getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(ArrayList<String> inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getLoopTime() {
		return loopTime;
	}

	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}

	public String getKillDate() {
		return killDate;
	}

	public void setKillDate(String killDate) {
		this.killDate = killDate;
	}

	
	
}
