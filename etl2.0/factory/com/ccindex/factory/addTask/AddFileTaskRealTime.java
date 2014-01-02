package com.ccindex.factory.addTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ccindex.constant.Regular;
import com.ccindex.database.OutList;
import com.ccindex.factory.util.file.CommUtil;
import com.ccindex.factory.util.file.ScanFile;
import com.ccindex.factory.util.file.ScanFileRealTime;
import com.ccindex.logic.util.Hostname2BuLogByDB;
import com.ccindex.mInterface.ConfContext;
import com.ccindex.mInterface.Task;
import com.ccindex.main.SetConfEtl;
import com.ccindex.realTime.MonitorDir;
import com.ccindex.struc.queue.BuLogType;
import com.ccindex.struc.queue.FileQueue;
import com.ccindex.struc.queue.FileTypeGather;
import com.ccindex.struc.queue.FileTypeQueue;

public class AddFileTaskRealTime implements Task<FileTypeGather> {

	protected static Logger logger = Logger
			.getLogger(AddFileTaskRealTime.class);

	// 任务 队列 <待处理文件>
	private BlockingQueue<FileTypeGather> filesList = new FileTypeQueue(100000);
	private List<String> treatedFile = new ArrayList<String>();
	// 是否结束的全局判断标志,判定线程是否结束了定时任务
	private ArrayList<ScanFileRealTime> isOver = new ArrayList<ScanFileRealTime>();
	Pattern rexFilename = Pattern.compile(Regular.OVERTASK);
	// 状态记录
	private FileWriter processFileWriter;

	int maxTime = 60 * 1000, loopTime = 5 * 1000;
	private String killDate;

	// .compile("overTask:\\(.*?\\)\\[(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)\\](.*)$");
	private AddFileTaskConfContext addFileTaskConfContext;
	private Lock lock = new ReentrantLock();

	/**
	 * 
	 * (非 Javadoc)
	 * 
	 * @Title: addTask
	 * @Description:增加单个文件属性集
	 * @param f
	 * @see com.ccindex.mInterface.Task#addTask(java.lang.Object)
	 */
	public void addTask(FileTypeGather t) {
		try {
			if (treatedFile.contains(t.getFile().getAbsolutePath())) {
				logger.info("Already Exists File: "
						+ t.getFile().getAbsolutePath());
				return;
			} else {
				logger.info("Add File: " + t.getFile().getAbsolutePath());
				filesList.put(t);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * (非 Javadoc)
	 * 
	 * @Title: addTasks
	 * @Description: 添加任务,即文件名字
	 * @see com.ccindex.mInterface.Task#addTasks() 目前支持多路径,多类型,但后缀支持统一一种
	 */
	public void addTasks() {
		try {
			ArrayList<String> inputPath = addFileTaskConfContext
					.getInputFilePath();

			ScanFileRealTime sf = new ScanFileRealTime(inputPath,
					addFileTaskConfContext.getRegularFileSuffix(), this,
					loopTime, maxTime, killDate);

			sf.onStart();

			Thread.sleep(10000);

			// 将当前状态保存
			isOver.add(sf);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public FileTypeGather getTask() {
		try {
			while (true) {
				boolean flagOver = true;
				FileTypeGather tf = (FileTypeGather) filesList.poll(loopTime,
						TimeUnit.MILLISECONDS);
				if (tf != null) {
					// System.out.println("Now Size: " + filesList.size());
					return tf;
				} else {

					for (ScanFileRealTime tmp : isOver) {
						if (!tmp.isOver()) {
							flagOver = false;
							logger.info("Now Size: " + filesList.size()
									+ " Wait For new input");
						}
					}

					if (flagOver) {
						return null;
					} else {
						continue;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean isOver() {
		// TODO Auto-generated method stub
		int num = filesList.size();

		return (num == 0) ? true : false;
	}

	@Override
	public void setConfig(ConfContext conf) {
		// TODO Auto-generated method stub
		try {
			addFileTaskConfContext = (AddFileTaskConfContext) conf;

			loopTime = addFileTaskConfContext.getLoopTime();
			maxTime = addFileTaskConfContext.getMaxTime();
			killDate = addFileTaskConfContext.getKillDate();

			String breakPointFile = addFileTaskConfContext.getBreakPointFile();
			String processFile = addFileTaskConfContext.getProcessFile();
			// 创建断点记录文件,如果存在则不创建
			File breakFile = new File(breakPointFile);
			if (breakFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(
						breakFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					treatedFile.add(line.trim());
				}

				reader.close();
			} else {
				new FileWriter(breakPointFile, true).close();
			}

			processFileWriter = new FileWriter(processFile, false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void overTask(FileTypeGather t, String status) {
		// TODO Auto-generated method stub
		try {
			lock.lock();
			String value = "overTask:(" + filesList.size() + ")" + "[" + status
					+ "]" + t.getFile().getAbsolutePath() + "\r\n";
			processFileWriter.write(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			if (processFileWriter != null)
				processFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
