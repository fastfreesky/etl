package com.ccindex.frame.run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import com.ccindex.conf.BeanConf;
import com.ccindex.conf.BeanConfQueue;
import com.ccindex.conf.ProcessConf;
import com.ccindex.conf.ProcessConfQueue;
import com.ccindex.frame.imp.RunThread;
import com.ccindex.mInterface.Bean;
import com.ccindex.mInterface.Output;
import com.ccindex.mInterface.Process;
import com.ccindex.mInterface.OctopusConf;
import com.ccindex.mInterface.Task;
import com.ccindex.util.cache.Cache;
import com.ccindex.util.factory.ObjectFactory;

public class Run {
	// 结束信号量
	private CountDownLatch latch;

	public void run(OctopusConf conf) throws InterruptedException, IOException {
		int numThreads = conf.getThreadNum();
		latch = new CountDownLatch(numThreads);

		BeanConfQueue beanConfQueue = conf.getBeanConfQueue();
		Task at = conf.getTask();
		Output out = conf.getOutPut();
		ProcessConfQueue processConfQueue = conf.getProcessConfQueue();

		// 添加任务,完成之后可确定输入路径及输出路径
		at.addTasks();

		Thread.sleep(100);
		for (int i = 0; i < numThreads; i++) {
			// 存储Bean及对应的类型,多Bean文件
			Map<String, Bean> beansClass = new HashMap<String, Bean>();
			for (BeanConf becof : beanConfQueue.getConf()) {
				Bean beanz = (Bean) ObjectFactory.newInstance(becof
						.getClassname());
				beansClass.put(becof.getName(), beanz);
				beanz = null;
			}

			// 实例化所有的Process
			Map<String, Process> processClass = new HashMap<String, Process>();
			for (ProcessConf mp : processConfQueue.getConf()) {
				Process p = (Process) ObjectFactory.newInstance(mp
						.getClassname());
				p.setConfig(mp.getConfContext());
				processClass.put(mp.getName(), p);// 唯一性通过功能名-框架名确定
				p = null;
			}

			new Thread(new RunThread(beansClass, processClass, at, out, latch,
					conf)).start();
		}

		Thread.sleep(200);
		latch.await();

		out.close();
	}

}
