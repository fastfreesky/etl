package com.ccindex.main;

import org.apache.log4j.Logger;

import com.ccindex.database.EtlLogToMySql;
import com.ccindex.factory.util.file.CommUtil;
import com.ccindex.frame.run.Run;
import com.ccindex.load.LoadToHive;
import com.ccindex.tool.ParseYamlConf;
import com.ccindex.tool.Time;

/**
 * 清洗模块入口
 * 
 * @author tianyu.yang
 * 
 */
public class Main {

	public static volatile long RUNVERSION;
	protected static Logger logger = Logger.getLogger(Main.class);

	private static ParseYamlConf yamlConf;

	public enum RECORD {
		recordOpen, recordClose, recordOnly;

		public static boolean contains(String key) {
			if (!key.equalsIgnoreCase(recordOpen.toString())
					&& !key.equalsIgnoreCase(recordClose.toString())
					&& !key.equalsIgnoreCase(recordOnly.toString())) {
				return false;
			}
			return true;
		}
	};

	public enum HIVE {
		hiveOpen, hiveClose, hiveOnly;

		public static boolean contains(String key) {
			if (!key.equalsIgnoreCase(hiveOpen.toString())
					&& !key.equalsIgnoreCase(hiveClose.toString())
					&& !key.equalsIgnoreCase(hiveOnly.toString())) {
				return false;
			}
			return true;
		}
	};

	/**
	 * 清洗 入口,传入指定清洗与写出目录的时间戳,目前暂定为同步清洗1天的
	 */
	public static String etl() throws Exception {

		String start = Time.getCurrentTime();

		SetConfEtl setConf = new SetConfEtl(yamlConf);

		Run run = new Run();
		run.run(setConf);
		setConf.close();

		String end = Time.getCurrentTime();

		logger.info("ETL start : " + start);
		logger.info("ETL end : " + end);
		logger.info("ETL run time [ms]: " + Time.TimeDiff(start, end));

		return Time.getCurrentTime("yyyyMMdd") + "_"
				+ Time.TimeDiff(start, end);
	}

	/**
	 * 
	 * @Title: main
	 * @Description: TODO(这里用一句话描述这个方法的作用)入口
	 * @param args
	 * @throws Exception
	 *             void
	 * @throws 传参格式
	 *             :指定配置目录, 修改yaml组,对应最内的key,value值,目前主要为文件夹的最后时间后缀
	 *             /tmp/etl.yaml --etl -Ddate=2012-07-01
	 *             附加参数:是否入数据库,默认为入库,如果不如,加参数-debug
	 */

	public static void main(String[] args) throws Exception {

		if ((args.length != 3 && args.length != 4 && args.length != 5)
				|| (args.length == 4 && !args[3].contains("-record"))
				|| (args.length == 5 && !args[4].contains("-hive"))) {
			System.out
					.println("Error paramers:\n[1]-配置文件</Application/etl2/conf/etl.yaml>\n[2]-yaml所属组<--etl>\n[3]-组内修改的key-value值<-Ddate=yyyy-MM-dd>\n[4]-清洗记录入库开关(-recordOpen/-recordClose/-recordOnly)\n[5]-Hive入库记录开关(-hiveOpen/-hiveClose/-hiveOnly)");
			return;
		}
		// //
		// // // 初始化配置文件
		//
		// if (true) {
		// String etl_yaml = "./resources/etl.yaml";
		// yamlConf = new ParseYamlConf(etl_yaml, args);
		//
		// // EtlLogToMySql.etlLogToMySql(null, CommUtil.packageFilePath(
		// // yamlConf.getRecordLogDir(),
		// // yamlConf.getEtlProcessFile()));
		// // etl();
		// // LoadToHive.loadAndRecord(CommUtil.packageFilePath(
		// // yamlConf.getRecordLogDir(), yamlConf.getLoadRecordFile()));
		//
		// // 入库hive表
		// // EtlLogToMySql.loadLogToMySql(CommUtil.packageFilePath(
		// // yamlConf.getRecordLogDir(), yamlConf.getLoadRecordFile()));
		//
		// // 记录入库
		// EtlLogToMySql.loadLogToMySql(CommUtil.packageFilePath(
		// yamlConf.getRecordLogDir(),
		// yamlConf.getLoadRecordFile()));
		// return;
		// }
		String etl_yaml = args[0];
		yamlConf = new ParseYamlConf(etl_yaml, args);
		// LoadToHive.hdfsFileToHive();
		// 清洗
		String job = null;
		String record = null;
		String hive = null;
		String etl = null;
		if (args.length == 3) {
			etl = "etlOpen";
			record = RECORD.recordOpen.toString();
			hive = HIVE.hiveOpen.toString();
		} else if (args.length == 4) {
			record = args[3].substring(1);
			if (!RECORD.contains(record)) {
				logger.info("Error Record Params: " + record);
				return;
			}

			if (!record.equalsIgnoreCase(RECORD.recordOnly.toString())) {
				etl = "etlOpen";
			}

		} else if (args.length == 5) {
			record = args[3].substring(1);
			if (!RECORD.contains(record)) {
				logger.info("Error Record Params: " + record);
				return;
			}
			hive = args[4].substring(1);
			if (!HIVE.contains(hive)) {
				logger.info("Error Hive Params: " + hive);
				return;
			}

			if (!record.equalsIgnoreCase(RECORD.recordOnly.toString())
					&& !hive.equalsIgnoreCase(HIVE.hiveOnly.toString())) {
				etl = "etlOpen";
			}

		} else {
			logger.info("Error params");
			return;
		}

		if (etl != null) {
			job = etl();
		}

		if (record != null) {
			if (record.equalsIgnoreCase(RECORD.recordOpen.toString())) {
				// 记录导入
				EtlLogToMySql.etlLogToMySql(job, CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getEtlProcessFile()));
			} else if (record.equalsIgnoreCase(RECORD.recordOnly.toString())) {
				// 记录导入
				EtlLogToMySql.etlLogToMySql(job, CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getEtlProcessFile()));

				return;
			}
		}

		if (hive != null) {
			if (hive.equalsIgnoreCase(HIVE.hiveOpen.toString())) {
				// 入库hive表
				LoadToHive.loadAndRecord(CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getLoadRecordFile()));
				// 记录入库
				EtlLogToMySql.loadLogToMySql(CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getLoadRecordFile()));

			} else if (hive.equalsIgnoreCase(HIVE.hiveOnly.toString())) {
				// 入库hive表
				LoadToHive.loadAndRecord(CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getLoadRecordFile()));
				// 记录入库
				EtlLogToMySql.loadLogToMySql(CommUtil.packageFilePath(
						yamlConf.getRecordLogDir(),
						yamlConf.getLoadRecordFile()));
				return;
			}
		}
		return;
	}
}
