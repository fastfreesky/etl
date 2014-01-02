package com.ccindex.conf;

import java.util.List;
import java.util.Map;

import com.ccindex.mInterface.ConfContext;

public abstract class ProcessConfQueue extends Conf<ProcessConf>{

	public abstract void setConf(ConfContext conf);
}
