package com.ccindex.frame.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import com.ccindex.conf.BeanChoice;
import com.ccindex.conf.OctopusCount;
import com.ccindex.conf.ProcessCount;
import com.ccindex.mInterface.Bean;
import com.ccindex.mInterface.Output;
import com.ccindex.mInterface.Process;
import com.ccindex.mInterface.OctopusConf;
import com.ccindex.mInterface.Task;
import com.ccindex.struc.queue.BuLogType;
import com.ccindex.struc.queue.FileTypeGather;
import com.ccindex.util.cache.Cache;

public class RunThread implements Runnable {

	// 映射任务类与类名称曾
	private Map<String, Process> processClass;
	private Task<FileTypeGather> at;
	private Output out;
	private CountDownLatch latch;
	// 待使用的bean集合
	private BeanChoice beanChoice;
	// 选中的bean集
	private Entry<String, Bean> choiceBean;
	// bean和process的对应关系
	private HashMap<String, ArrayList<String>> beanProcessRelation;
	//
	private String key;
	private Bean value;
	private ArrayList<String> listProcess;
	private Process process;
	private OctopusCount octopusCount;
	private ProcessCount processCount;
	private OctopusConf conf;

	protected static Logger logger = Logger.getLogger(RunThread.class);

	public RunThread(Map<String, Bean> beansClass,
			Map<String, Process> processClass, Task<FileTypeGather> at,
			Output out, CountDownLatch latch, OctopusConf conf) {
		this.at = at;
		this.out = out;
		this.latch = latch;
		this.processClass = processClass;
		this.conf = conf;
		beanChoice = new BeanChoice(beansClass);
		beanProcessRelation = conf.getBeanProcessRelation();

		octopusCount = new OctopusCount(processClass.keySet().toArray());
	}

	public void run() {
		while (true) {
			try {
				// 获取一个任务,为null时候,任务结束
				FileTypeGather fileTypeGather = at.getTask();
				if (fileTypeGather == null) {
					break;
				}

				// 读取.gz文件
				BufferedReader reader = getFileStream(fileTypeGather.getFile());
				if (reader == null) {
					continue;
				}
				octopusCount.clear();
				octopusCount.setHostNameRun(fileTypeGather.getHostNameEtl());
				octopusCount.setSrcFilePath(fileTypeGather.getFile()
						.getAbsolutePath());
				octopusCount.setSrcFileSize(fileTypeGather.getFile().length());
				octopusCount.setEtlDate(conf.getEtlDate());

				String line = null;
				// 行处理
				while ((line = reader.readLine()) != null) {
					choiceBean = beanChoice.getBean(fileTypeGather.getFile().getAbsolutePath(),line);
					key = choiceBean.getKey();
					value = choiceBean.getValue();
					listProcess = beanProcessRelation.get(key);

					octopusCount.setSrcFileUnzipCount();

					for (String t : listProcess) {

						processCount = octopusCount.getProcessCount().get(t);
						processCount.setStartTime();
						processCount.setBean(key);

						process = processClass.get(t);
						int result = process.processing(null, value, out, t,
								key);
						if (result > 0) {
							processCount.setDestFileUnzipHitCount();
							processCount.setDestFileUnzipHitSize(result);
						}

						processCount.setEndTime();

						processCount = null;
					}
				}

				reader.close();

				overTask(fileTypeGather);
				fileTypeGather = null;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logger.info(this.toString() + " All: " + beanChoice.getCountAll());
		logger.info(this.toString() + " Hit:  " + beanChoice.getCountHit());
		logger.info(this.toString() + " Change:  "
				+ beanChoice.getCountChange());
		logger.info(this.toString() + " Default: "
				+ beanChoice.getCountDefault());

		latch.countDown();
	}

	private void overTask(FileTypeGather fileTypeGather) {
		// 文件总行数 ，匹配行数 ，原文件大小, 匹配总大小,处理起始时间，处理结束时间,本机主机名
		for (String t : octopusCount.format()) {
			at.overTask(fileTypeGather, t);
		}

	}

	private BufferedReader getFileStream(File file) {
		// 该文件异常
		if (file == null) {
			logger.info("Error File Is Null");
			return null;
		}

		InputStream ins = null;
		try {
			ins = new GZIPInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ins == null)
			return null;
		else {
			return new BufferedReader(new InputStreamReader(ins));
		}
	}

}
