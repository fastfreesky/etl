package com.ccindex.conf;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @ClassName: Conf
 * @Description: TODO(这里用一句话描述这个类的作用)章鱼框架配置抽象类
 * @author tianyu.yang
 * @date 2013-1-15 下午4:39:29
 * 
 * @param <T>
 */
public abstract class Conf<T> {

	LinkedBlockingQueue<T> queue = null;

	public Conf() {
		queue = new LinkedBlockingQueue<T>();
	}

	public Conf(int capacity) {
		queue = new LinkedBlockingQueue<T>(capacity);
	}

	protected void putConf(T t) {
		try {
			queue.put(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public LinkedBlockingQueue<T> getConf() {
		return queue;
	}

}
