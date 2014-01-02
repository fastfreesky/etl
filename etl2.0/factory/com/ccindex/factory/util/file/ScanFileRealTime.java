package com.ccindex.factory.util.file;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ccindex.constant.Regular;
import com.ccindex.logic.util.Hostname2BuLogByDB;
import com.ccindex.mInterface.Task;
import com.ccindex.main.Main;
import com.ccindex.struc.queue.FileTypeGather;

/**
 * 遍历 目录中的文件 使用参考 test.file.ScanFileUnit
 */
public class ScanFileRealTime implements Runnable {

	private Pattern rexFile;
	// private BlockingQueue<File> filesList ;
	private Task<FileTypeGather> ta;
	private Pattern rexFilename = Pattern.compile(Regular.FCBEAN_REXFILENAME);
	private Hostname2BuLogByDB hostName = null;

	protected static Logger logger = Logger.getLogger(Main.class);

	// 扫描的间隔时间
	private int second;
	// 定时时间
	private int maxTime = 0;
	// 强制截止时间
	private String killDate = null;

	Thread runner;
	List<String> fileLList;
	public Map<String, Long> prevFiles = new HashMap<String, Long>();// 存储原来文件路径和最后修改时间
	public Map<String, Long> currentFiles = new HashMap<String, Long>();// 存储当前文件路径和最后修改时间
	public Map<String, String> tmpCurrFiles = new HashMap<String, String>();
	public Map<String, String> tmpPrevFiles = new HashMap<String, String>();

	private Timer timer = null;
	private volatile boolean flagTimer = false;

	public boolean isOver() {
		return flagTimer;
	}

	/**
	 * 
	 * @Title: ScanFileRealTime.java
	 * @Description:
	 * @param filePath
	 *            待监控的路径
	 * @param rexFileStr
	 *            匹配名字的正则表达式
	 * @param ta
	 *            扫描的任务列表
	 * @param second
	 *            -轮训对应的秒数
	 * 
	 */
	public ScanFileRealTime(List<String> fileLList, String rexFileStr,
			Task<FileTypeGather> ta, int second, int maxTime, String killDate) {
		this.fileLList = fileLList;
		rexFile = Pattern.compile(rexFileStr);
		this.ta = ta;
		this.second = second;
		this.maxTime = maxTime;
		this.killDate = killDate;
	}

	public void onStart() {
		runner = new Thread(this);
		// runner.setDaemon(true);
		runner.start();
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
	 * 获取文件的信息
	 * 
	 * @param dirPath
	 */
	public void getFilesInfo(File dirPath) {
		File dirFile = dirPath;

		File[] fileList = dirFile.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			File tmpFile = fileList[i];
			if (tmpFile.isFile())// 文件
			{
				currentFiles.put(tmpFile.getAbsolutePath(),
						tmpFile.lastModified());

			} else// 子目录则递归
			{
				// String tmpPath = tmpFile.getAbsolutePath();
				getFilesInfo(tmpFile);
			}

		}

	}

	public void addFile(String file) {
		// System.out.println(file + " is add");
	}

	public void changed(String file) {
		// System.out.println(file + " is changed");
	}

	public void delete(String file) {
		// System.out.println(file + " is delete");
	}

	/**
	 * 
	 * @Title: addRexFile
	 * @Description: TODO(这里用一句话描述这个方法的作用) 通过判断文件是否符合增加的文件,确定是否进行添加操作
	 * @param newfiletype
	 *            --待解析的文件名字
	 * @return boolean--true成功,false继续
	 * @throws
	 */
	public boolean addRexFile(File newfiletype) {
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
				
				return true;
			} else {
				logger.info("Error input File: "
						+ newfiletype.getName());
				return false;
			}
		}		

		return false;
	}

	@Override
	public void run() {
		if (maxTime != 0) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (ta.isOver()) {
						logger.info("Time out ..." + fileLList);
						flagTimer = true;
					} else {
						logger.info("Again TimeSchedule ..." + fileLList);
					}
				}

			}, maxTime, maxTime);

		}

		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (!flagTimer) {
			try {
				if (killDate != null) {
					// 比对当前时间,是否为最大时间,如果是,强制结束清洗追加
					DateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date start = new Date(System.currentTimeMillis());
					String day = fm.format(start);

					Date nowtime;
					try {
						nowtime = fm.parse(day);
						Date comtime = fm.parse(killDate);
						// 当时间超过定时任务时候,任务跳出||或者日期变为第二天
						if (nowtime.compareTo(comtime) >= 0) {
							logger.info("Kill time ..." + day
									+ "  Kill time..." + killDate);
							flagTimer = true;
							break;
						} else {
							logger.info("Now time ..." + day + "  Kill time..."
									+ killDate);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				watch(fileLList);

				logger.info("Scan again..." + second + " ms " + fileLList);
				Thread.sleep(second);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		timer.cancel();
		logger.info("Add Tasks Ok");
	}

	public void watch(List<String> listFile) {
		// 保存之前记录值
		prevFiles.clear();
		prevFiles.putAll(currentFiles);
		currentFiles.clear();

		for (String f : listFile) {
			// 扫描文件
			File filepath = new File(f);
			if (filepath.exists())
				getFilesInfo(filepath);

		}

		Iterator currentIt = currentFiles.keySet().iterator();
		while (currentIt.hasNext()) {
			String filePath = (String) currentIt.next();
			Long currentModify = (Long) currentFiles.get(filePath);
			if (!prevFiles.containsKey(filePath))// 假如原来的hashmap中不存在当前键，则为增加的
			{
				addFile(filePath);

				addRexFile(new File(filePath));
				// if()
			} else if (prevFiles.containsKey(filePath)) {
				Long prevModify = (Long) prevFiles.get(filePath);
				if (prevModify.compareTo(currentModify) != 0)// 最后修改时间不同，则为改变的
				{
					changed(filePath);
				}
			}
			// System.out.println("当前------"+filePath+","+currentModify);
		}
	}
}
