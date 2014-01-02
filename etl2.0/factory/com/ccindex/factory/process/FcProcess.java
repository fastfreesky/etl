package com.ccindex.factory.process;

import com.ccindex.database.OutList.ETLOUTWAY;
import com.ccindex.factory.bean.FcBean;
import com.ccindex.factory.util.file.CommUtil;
import com.ccindex.mInterface.Bean;
import com.ccindex.mInterface.ConfContext;
import com.ccindex.mInterface.Output;
import com.ccindex.mInterface.Process;

public class FcProcess implements Process {

	// 是否组装成功输出目录
	private volatile boolean isPackagedFile = false;
	private String local_file = null;
	private String hdfs_file = null;

	private ProcessConfContext processConfContext;

	public int processing(String task, Bean bean, Output out, String... args)
			throws Exception {

		if (bean == null)
			return 0;

		FcBean fb = (FcBean) bean;

		String format = fb.format();

		if (!isPackagedFile) {
			// 日期.设备.清洗类型.日志格式.[local|hdfs]
			String etlOutWay = processConfContext.getEtlOutWay();
			if (etlOutWay.equals(ETLOUTWAY.all.toString())) {
				local_file = CommUtil.packageFileName(fb.getDay(),
						processConfContext.getHostName(), args[0], args[1],
						ETLOUTWAY.local.toString());
				hdfs_file = CommUtil.packageFileName(fb.getDay(),
						processConfContext.getHostName(), args[0], args[1],
						ETLOUTWAY.hdfs.toString());

			} else if (etlOutWay.equals(ETLOUTWAY.local.toString())) {
				local_file = CommUtil.packageFileName(fb.getDay(),
						processConfContext.getHostName(), args[0], args[1],
						ETLOUTWAY.local.toString());

			} else if (etlOutWay.equals(ETLOUTWAY.hdfs.toString())) {
				hdfs_file = CommUtil.packageFileName(fb.getDay(),
						processConfContext.getHostName(), args[0], args[1],
						ETLOUTWAY.hdfs.toString());
			}

			isPackagedFile = true;
		}
		// 日期.设备.清洗类型.日志格式.[local|hdfs]
		out.write(local_file, format);
		out.write(hdfs_file, format);

		return format.length();

	}

	@Override
	public void setConfig(ConfContext conf) {
		// TODO Auto-generated method stub
		processConfContext = (ProcessConfContext) conf;
	}

}
