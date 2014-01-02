package com.ccindex.factory.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ccindex.constant.Regular;
import com.ccindex.logic.util.Hostname2BuLogByDB;
import com.ccindex.mInterface.Task;
import com.ccindex.main.Main;
import com.ccindex.struc.queue.BuLogType;
import com.ccindex.struc.queue.FileTypeGather;

/**
 * 遍历 目录中的文件 使用参考 test.file.ScanFileUnit
 */
public class ScanFile {

	private Pattern rexFile;
	private Task<FileTypeGather> ta;
	private Pattern rexFilename = Pattern.compile(Regular.FCBEAN_REXFILENAME);
	protected static Logger logger = Logger.getLogger(ScanFile.class);

	/**
	 * 
	 * @Title: ScanFile.java
	 * @Description:
	 * @param rexFileStr扫描的路径
	 * @param ta扫描的任务列表
	 */
	public ScanFile(String rexFileStr, Task<FileTypeGather> ta) {
		rexFile = Pattern.compile(rexFileStr);
		this.ta = ta;
	}

	static int runFile = 0;

	public void autoFileSize() {
		synchronized (this) {
			runFile++;
		}
	}

	public int getRunFileSize() {
		return runFile;
	}

	/**
	 * 
	 * @Title: scanPath
	 * @Description: TODO(这里用一句话描述这个方法的作用)遍历指定目录下的文件
	 * @param filepath
	 *            指定的路径
	 * @throws InterruptedException
	 *             void
	 * @throws
	 */
	public void scanPath(File filepath) throws InterruptedException {
		File f = filepath;
		if (f.exists() && f.isDirectory()) {
			File file[] = f.listFiles();
			int i = f.listFiles().length;
			for (int j = 0; j < i; j++) {
				// 创建新的集合
				File newfiletype = file[j];
				if (newfiletype.isDirectory()) {
					scanPath(newfiletype);
				} else {
					if (rexFile.matcher(newfiletype.getName()).find()) {
						Matcher mf = rexFilename.matcher(newfiletype.getName());
						String hostNameSource = null;
						if (mf.find()) {
							hostNameSource = mf.group(1);
							// 不通过hostname区分
							FileTypeGather addfile = new FileTypeGather(
									newfiletype, hostNameSource);

							ta.addTask(addfile);

							addfile = null;
						} else {
							logger.info("Error input File: "
									+ newfiletype.getName());
						}
					}
				}
			}
		}
	}
}
