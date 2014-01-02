package com.ccindex.factory.addTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import com.ccindex.logic.util.Hostname2BuLogByDB;
import com.ccindex.mInterface.ConfContext;
import com.ccindex.mInterface.Task;
import com.ccindex.main.SetConfEtl;
import com.ccindex.struc.queue.BuLogType;
import com.ccindex.struc.queue.FileQueue;
import com.ccindex.struc.queue.FileTypeGather;
import com.ccindex.struc.queue.FileTypeQueue;

public class AddFileTask implements Task<FileTypeGather> {

	protected static Logger logger = Logger.getLogger(AddFileTask.class);
	// 任务 队列 <待处理文件>
	private BlockingQueue<FileTypeGather> filesList = new FileTypeQueue(100000);
	// 处理过的文件列表
	private List<String> treatedFile = new ArrayList<String>();

	private AddFileTaskConfContext addFileTaskConfContext;
	// 状态记录
	private FileWriter processFileWriter;
	private Lock lock = new ReentrantLock();

	@Override
	public void setConfig(ConfContext conf) {
		// TODO Auto-generated method stub
		try {
			addFileTaskConfContext = (AddFileTaskConfContext) conf;

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
	public void addTasks() {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> inputPath = addFileTaskConfContext
					.getInputFilePath();

			ScanFile sf = new ScanFile(
					addFileTaskConfContext.getRegularFileSuffix(), this);

			for (String path : inputPath) {
				// 获取别名及涉及路径
				File tf = new File(path);
				sf.scanPath(tf);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addTask(FileTypeGather t) {
		// TODO Auto-generated method stub
		try {
			if (treatedFile.contains(t.getFile().getAbsolutePath())) {
				logger.info("Already Exists File: "
						+ t.getFile().getAbsolutePath());
				return;
			} else {
				//logger.info("Add File: " + t.getFile().getAbsolutePath());
				filesList.put(t);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public FileTypeGather getTask() {
		// TODO Auto-generated method stub
		try {
			FileTypeGather tf = (FileTypeGather) filesList.poll(10000,
					TimeUnit.MILLISECONDS);
			return tf;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isOver() {
		// TODO Auto-generated method stub
		return false;
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
