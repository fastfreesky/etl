package com.ccindex.conf;

import com.ccindex.mInterface.ConfContext;

public class ProcessConf {
	private String name, classname;
	private ConfContext confContext;
	
	public String getClassname() {
		return classname;
	}

	public String getName() {
		return name;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConfContext getConfContext() {
		return confContext;
	}

	public void setConfContext(ConfContext confContext) {
		this.confContext = confContext;
	}

	
}
