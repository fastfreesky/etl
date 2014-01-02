package com.ccindex.realTime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * 监控文件目录
 * 
 * @author han
 * @date 2009-10-10
 */
public class MonitorDir extends RunThreadForScanFile {
	public Map prevFiles = new HashMap();// 存储原来文件路径和最后修改时间
	public Map currentFiles = new HashMap();// 存储当前文件路径和最后修改时间
	public Map tmpCurrFiles = new HashMap();
	public Map tmpPrevFiles = new HashMap();
	public static Vector vList = new Vector();
	public Vector vtList = new Vector();

	// 输出对外的新增路径
	public ArrayList<String> pathFile = new ArrayList<String>();

	/**
	 * @param second
	 *            定时扫描间隔
	 * @param filePath
	 *            目录路径
	 */
	public MonitorDir(int second, String filePath) {
		super(second, filePath);
		File dirFile = new File(filePath);
		if (dirFile.isDirectory()) {
			System.out.println("start to watch " + dirFile.getAbsolutePath());
		}

	}

	/**
	 * 
	 * @Title: MonitorDir.java
	 * @Description:
	 * @param second
	 *            扫描间隔时间
	 * @param time
	 *            定时时间
	 * @param filePath
	 *            目录路径
	 */
	public MonitorDir(int second, int time, String filePath) {
		super(second, filePath);
		File dirFile = new File(filePath);
		if (dirFile.isDirectory()) {
			System.out.println("start to watch " + dirFile.getAbsolutePath());
		}

	}

	/**
	 * 获取文件的信息
	 * 
	 * @param dirPath
	 */
	public void getFilesInfo(String dirPath) {
		File dirFile = new File(dirPath);

		File[] fileList = dirFile.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			File tmpFile = fileList[i];
			if (tmpFile.isFile())// 文件
			{
				currentFiles.put(tmpFile.getAbsolutePath(),
						tmpFile.lastModified());

			} else// 子目录则递归
			{
				String tmpPath = tmpFile.getAbsolutePath();
				getFilesInfo(tmpPath);
			}

		}

	}

	public void addFile(String file) {
		System.out.println(file + " is add");
	}

	public void changed(String file) {
		System.out.println(file + " is changed");
	}

	public void delete(String file) {
		System.out.println(file + " is delete");
	}

	/*
	 * 监控
	 */
	public void watch(String dirPath) {

		// 保存之前记录值
		prevFiles.clear();
		prevFiles.putAll(currentFiles);
		currentFiles.clear();
		pathFile.clear();
		// 扫描文件
		getFilesInfo(dirPath);

		Iterator currentIt = currentFiles.keySet().iterator();
		while (currentIt.hasNext()) {
			String filePath = (String) currentIt.next();
			Long currentModify = (Long) currentFiles.get(filePath);
			if (!prevFiles.containsKey(filePath))// 假如原来的hashmap中不存在当前键，则为增加的
			{
				addFile(filePath);

				pathFile.add(filePath);
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

		// 删除操作
		Iterator prevIt = prevFiles.keySet().iterator();
		while (prevIt.hasNext()) {
			String prevFilePath = prevIt.next().toString();
			if (!currentFiles.containsKey(prevFilePath))// 原来的键不在当前hashmap中，则为删除的
			{
				delete(prevFilePath);
			}
			// System.out.println("原来------"+prevFilePath+","+tmpPrevFiles.get(prevFilePath));
		}

	}
	static boolean flagOver=false;
	public static void main(String args[]) {
		

		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			int count=1;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (count == 3){
					System.out.println("time out over");
					flagOver = true;
				}else{
					System.out.println("time out" + count++);					
				}
			}
			
		}, 1000, 10000);
		
		while(!flagOver){

		}
		timer.cancel();		
		//timer.cancel();
//		timer.schedule(new TimerTask(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				System.out.println("time out");
//			}
//			
//		}, 1000);

//		MonitorDir md = new MonitorDir(5, "D:\\Application");
//		md.onStart();
//		try {
//			Thread.sleep(60000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
