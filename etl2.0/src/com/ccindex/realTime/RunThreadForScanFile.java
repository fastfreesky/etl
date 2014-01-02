package com.ccindex.realTime;

import com.ccindex.mInterface.Task;
import com.ccindex.struc.queue.FileTypeGather;

public abstract class RunThreadForScanFile implements Runnable {

	private int second;
	private String filePath;
	Thread runner;

	/**
	 * @param second
	 *            时间间隔
	 * @param filePath
	 *            文件路径
	 */
	public RunThreadForScanFile(int second, String filePath) {
		this.second = second * 1000;
		this.filePath = filePath;
	}

	public RunThreadForScanFile(String rexFileStr, Task<FileTypeGather> ta,
			String perce, int sleepTime) {
	}

	public void onStart() {
		runner = new Thread(this);
		runner.setDaemon(true);
		runner.start();
	}

	public void run() {
		// TODO Auto-generated method stub
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true) {
			try {
				watch(filePath);
				System.out.println("Scan again..." + second + " s");
				Thread.sleep(second);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public abstract void watch(String file);

}
