package com.ccindex.factory.conf;

import java.util.ArrayList;

import com.ccindex.conf.ProcessConf;
import com.ccindex.conf.ProcessConfQueue;
import com.ccindex.mInterface.ConfContext;

public class ProcessConfEtl extends ProcessConfQueue {

	@Override
	public void setConf(ConfContext conf) {
		// TODO Auto-generated method stub
		ConfContextImpl<ProcessConf> confT = (ConfContextImpl<ProcessConf>) conf;
		ArrayList<ProcessConf> arr = confT.get();
		for (ProcessConf b : arr) {
			putConf(b);
		}
		arr = null;
		confT = null;
	}
}
