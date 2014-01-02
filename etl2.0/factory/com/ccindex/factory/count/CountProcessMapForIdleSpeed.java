package com.ccindex.factory.count;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.ccindex.mInterface.Output;

public class CountProcessMapForIdleSpeed extends CountProcessMapBase<CountProcessForIdleSpeed> {

	@Override
	public void OutStream(Output out) throws IOException {
		// TODO Auto-generated method stub
		Iterator iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();

			CountProcessForIdleSpeed countProcess = (CountProcessForIdleSpeed) entry.getValue();		
			
			out.write(filename, countProcess.format());
		}
	}

}
