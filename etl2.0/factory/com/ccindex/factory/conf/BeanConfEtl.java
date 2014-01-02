package com.ccindex.factory.conf;

import java.util.ArrayList;

import com.ccindex.conf.BeanConf;
import com.ccindex.conf.BeanConfQueue;
import com.ccindex.mInterface.ConfContext;

public class BeanConfEtl extends BeanConfQueue {

	@Override
	public void setConf(ConfContext conf) {
		// TODO Auto-generated method stub
		ConfContextImpl<BeanConf> confT = (ConfContextImpl<BeanConf>) conf;
		ArrayList<BeanConf> arr = confT.get();
		for (BeanConf b : arr) {
			putConf(b);
		}
		arr = null;
		confT = null;
	}
}
