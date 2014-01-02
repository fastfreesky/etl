package com.ccindex.factory.conf;

import java.util.ArrayList;

import com.ccindex.mInterface.ConfContext;

public class ConfContextImpl<T> implements ConfContext {

	private ArrayList<T> beanConf = new ArrayList<T>();

	public void add(T bean) {
		beanConf.add(bean);
	}

	public ArrayList<T> get() {
		return beanConf;
	}

}
