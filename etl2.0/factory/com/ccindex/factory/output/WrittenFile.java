package com.ccindex.factory.output;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ccindex.database.OutList;
import com.ccindex.database.OutList.ETLOUTWAY;
import com.ccindex.factory.util.file.CommUtil;
import com.ccindex.mInterface.ConfContext;
import com.ccindex.mInterface.Output;
import com.ccindex.tool.ParseYamlConf;

public class WrittenFile implements Output {

	// 全局 map 文件句柄
	private Map<String, Written> fmap = Collections
			.synchronizedMap(new HashMap<String, Written>());

	private int buffMem;
	private int maxbuffMem;
	private int level;
	private String outpath;
	private WriteenFileConfContext writeenFileConfContext;
	private HashMap<String, OutList> outList;
	private Lock lock = new ReentrantLock();

	// key为文件的名字,格式为日期.设备.清洗类型.日志格式.[local|hdfs]
	public void write(String key, String value) throws IOException {
		if (key == null) {
			return;
		}
		if (!fmap.containsKey(key)) {
			String type = CommUtil.getLogType(key);
			String localOrHdfs = CommUtil.getLogLocalOrHdfs(key);

			lock.lock();
			if (!fmap.containsKey(key)) {
				OutList list = outList.get(type);
				Written wu;
				if (localOrHdfs.equals(ETLOUTWAY.local.toString())) {
					wu = new LocalWritten(CommUtil.packageFilePath(
							list.getLocal_path(), key), level, buffMem,
							maxbuffMem);
				} else if (localOrHdfs.equals(ETLOUTWAY.hdfs.toString())) {
					wu = new HdfsWritten(CommUtil.packageFilePath(
							list.getHdfs_path(), key));
				} else {
					return;
				}

				fmap.put(key, wu);

			}
			lock.unlock();
		}

		Written wu = fmap.get(key);
		wu.write(value);
	}

	public void close() throws IOException {
		for (Map.Entry<String, Written> kv : fmap.entrySet()) {
			kv.getValue().close();
		}
	}

	@Override
	public void setConfig(ConfContext conf) {
		// TODO Auto-generated method stub
		writeenFileConfContext = (WriteenFileConfContext) conf;
		buffMem = writeenFileConfContext.getBuffMem();
		maxbuffMem = writeenFileConfContext.getMaxbuffMem();
		level = writeenFileConfContext.getLevel();
		outList = writeenFileConfContext.getOutList();
	}

}