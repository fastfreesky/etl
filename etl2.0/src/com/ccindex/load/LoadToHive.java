package com.ccindex.load;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ccindex.database.EtlConfFromMySql;
import com.ccindex.database.OutList;
import com.ccindex.struc.queue.LoadTypeGather;
import com.ccindex.tool.HadoopFileOperator;
import com.ccindex.tool.Time;

/**
 * 
 * @ClassName: LoadToHive
 * @Description: TODO(这里用一句话描述这个类的作用)将数据导入到Hive表中
 * @author tianyu.yang
 * @date 2013-8-28 下午6:54:40 目前仅仅实现,在Hadoop内部文件实现导入功能
 */
public class LoadToHive {
	protected static Logger logger = Logger.getLogger(LoadToHive.class);
	/**
	 * 记录入库的信息,key值由LoadTypeGather中getKey获得
	 */
	private static HashMap<String, LoadTypeGather> loadRecord = new HashMap<String, LoadTypeGather>();

	public static boolean loadAndRecord(String recordFile) {

		hdfsFileToHive();

		loadRecordToFile(recordFile);

		return true;
	}

	public static void loadRecordToFile(String file) {
		// 落本地文件
		File fileLoad = new File(file);

		try {
			FileWriter statFile = new FileWriter(fileLoad, false);

			Iterator iter = loadRecord.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				String key = (String) entry.getKey();
				LoadTypeGather value = (LoadTypeGather) entry.getValue();
				statFile.write(value.toString());
			}

			statFile.flush();
			statFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean hdfsFileToHive() {

		logger.info("Begin to Load To Hive ");

		HashMap<String, OutList> outListS = EtlConfFromMySql.getOutList();

		Iterator<Entry<String, OutList>> itr = outListS.entrySet().iterator();

		while (itr.hasNext()) {
			Entry<String, OutList> entry = itr.next();
			OutList value = entry.getValue();
			String file = value.getHdfsFile();
			String shellCmd;
			if (!HadoopFileOperator.isExists(file)) {
				logger.info("No Such File: " + file);
				continue;
			} else {

				if (value.getHive_databse() == null) {
					logger.info("Such File Not To Hive: " + file);
					continue;
				}

				LoadTypeGather loadFileRecord = new LoadTypeGather();
				loadFileRecord.setHostname(value.getHostName());
				loadFileRecord.setBusiness("no");
				loadFileRecord.setLogType(value.getBean());
				loadFileRecord.setProcess(value.getProcess());
				loadFileRecord.setHiveDatabase(value.getHive_databse());
				loadFileRecord.setHiveTmp(value.getHive_tmp());
				loadFileRecord.setHiveOut(value.getHive_out());
				loadFileRecord.setOutWay(file);
				loadFileRecord.setEtlDate(value.getDate());
				// 单位为B
				loadFileRecord.setLoadSize(HadoopFileOperator
						.getHadoopFileSize(file));

				String partitions = "pt=" + value.getDate() + "," + "host="
						+ value.getHostName();
				// 脚本执行指令
				shellCmd = "/Application/etl2/conf/Load" + " " + "load" + " "
						+ file + " " + value.getHive_databse() + " "
						+ value.getHive_tmp() + " " + partitions;
				logger.info("Load exec:" + shellCmd);

				loadFileRecord.setLoadStart(Time.getCurrentTime());

				execShell(shellCmd);

				loadFileRecord.setLoadEnd(Time.getCurrentTime());

				// 加入记录值
				loadRecord.put(loadFileRecord.getKey(), loadFileRecord);

			}
			// System.out.println(entry.getValue().getHdfsFile());
		}

		logger.info("End to Load To Hive ");

		return true;
	}

	/**
	 * 运行shell脚本
	 * 
	 * @param shell
	 *            需要运行的shell脚本
	 */
	public static void execShell(String shell) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(shell);
			process.waitFor();

			InputStreamReader ir = new InputStreamReader(
					process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			process.waitFor();
			while ((line = input.readLine()) != null) {
				logger.info("Load return:" + line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
