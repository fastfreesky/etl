package com.ccindex.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ccindex.constant.Constant;
import com.ccindex.database.OutList.ETLOUTWAY;
import com.ccindex.factory.util.file.CommUtil;

/**
 * 
 * @ClassName: EtlConfFromMySql
 * @Description: TODO(这里用一句话描述这个类的作用)从数据库获取相关配置文件
 * @author tianyu.yang
 * @date 2013-8-26 下午3:54:07
 * 
 */
public class EtlConfFromMySql {
	protected static Logger logger = Logger.getLogger(EtlConfFromMySql.class);
	private static DataBase dataBase = null;
	// 获取输入路径,根据各个集群自动调整输入路径
	private static ArrayList<String> inputList = new ArrayList<String>();
	// 文件备份路径
	private static String beanProcessRelationPathBak;
	private static String outListPathBak;
	private static String inputListPathBak;

	/**
	 * bean与process的对应关系
	 */
	private static HashMap<String, ArrayList<String>> beanProcessRelation = new HashMap<String, ArrayList<String>>();
	/**
	 * 输出bean及路径的对应关系
	 */
	private static HashMap<String, OutList> outList = new HashMap<String, OutList>();

	public static void init(String database, String user, String pwd,
			String statePath) {

		beanProcessRelationPathBak = CommUtil.packageFilePath(statePath,
				"beanProcessRelationPathBak.bak");
		outListPathBak = CommUtil.packageFilePath(statePath,
				"outListPathBak.bak");
		inputListPathBak = CommUtil.packageFilePath(statePath,
				"inputListPathBak.bak");

		dataBase = new DataBase(database, user, pwd);
		setInputList();
		dialOutPutList();
		dataBase.Close();

		// 记录最新数据到备份
		recordToFile();
		// 从备份数据中获取
		fileToRecord();

	}

	public static ArrayList<String> getInputList() {
		logger.info("Etl Mysql Input: " + inputList.toString());
		return inputList;
	}

	public static HashMap<String, ArrayList<String>> getBeanProcessRelation() {
		logger.info("Etl Mysql BeanProcessRelation: " + beanProcessRelation);

		return beanProcessRelation;
	}

	public static HashMap<String, OutList> getOutList() {
		logger.info("Etl Mysql OutList: " + outList);

		return outList;
	}

	public static void setInputList() {

		String inputQuerSql = "select intput_path from etl_input_list where flag = 1 and cluster=\""
				+ Constant.getCluser() + "\"";

		ResultSet result = dataBase.QuarySql(inputQuerSql);
		if (result == null) {
			return;
		}
		try {
			while (result.next()) {
				inputList.add(result.getString("intput_path"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void recordToFile() {

		if (beanProcessRelation.size() > 0) {
			dataBase.ObjectToFile(beanProcessRelationPathBak,
					beanProcessRelation);
		}

		if (outList.size() > 0) {
			dataBase.ObjectToFile(outListPathBak, outList);
		}

		if (inputList.size() > 0) {
			dataBase.ObjectToFile(inputListPathBak, inputList);
		}

	}

	public static void fileToRecord() {
		if (beanProcessRelation.size() <= 0) {
			beanProcessRelation.clear();
			beanProcessRelation
					.putAll((HashMap<String, ArrayList<String>>) dataBase
							.FileToObject(beanProcessRelationPathBak));
		}

		if (outList.size() <= 0) {
			outList.clear();
			outList.putAll((HashMap<String, OutList>) dataBase
					.FileToObject(outListPathBak));

		}

		if (inputList.size() <= 0) {
			inputList.clear();
			inputList.addAll((ArrayList<String>) dataBase
					.FileToObject(inputListPathBak));
		}

	}

	public static void dialOutPutList() {
		String outputListSql = "select log_type,process,local_path,hdfs_path,hive_database,hive_tmp, hive_out from etl_out_list where flag=1";
		ResultSet result = dataBase.QuarySql(outputListSql);
		if (result == null) {
			return;
		}
		try {
			while (result.next()) {
				OutList outlistT = new OutList();
				outlistT.setBean(result.getString("log_type"));

				String local_path = result.getString("local_path");
				String hdfs_path = result.getString("hdfs_path");

				if (!local_path.equalsIgnoreCase("no")
						&& !hdfs_path.equalsIgnoreCase("no")) {
					outlistT.setLocal_path(local_path);
					outlistT.setHdfs_path(hdfs_path);
					outlistT.setOutType(ETLOUTWAY.all);
				} else if (!local_path.equalsIgnoreCase("no")
						&& hdfs_path.equalsIgnoreCase("no")) {
					outlistT.setLocal_path(local_path);
					outlistT.setOutType(ETLOUTWAY.local);
				} else if (local_path.equalsIgnoreCase("no")
						&& !hdfs_path.equalsIgnoreCase("no")) {
					outlistT.setHdfs_path(hdfs_path);
					outlistT.setOutType(ETLOUTWAY.hdfs);
				}

				outlistT.setProcess(result.getString("process"));
				outlistT.setKey(outlistT.getBean());

				String hive_database = result.getString("hive_database");
				String hive_tmp = result.getString("hive_tmp");
				String hive_out = result.getString("hive_out");
				if (hive_database != null
						&& !hive_database.equalsIgnoreCase("no")) {
					outlistT.setHive_databse(hive_database);
				}
				if (hive_tmp != null && !hive_tmp.equalsIgnoreCase("no")) {
					outlistT.setHive_tmp(hive_tmp);
				}
				if (hive_out != null && !hive_out.equalsIgnoreCase("no")) {
					outlistT.setHive_out(hive_out);
				}

				// 输出列表对应关系
				outList.put(outlistT.getKey(), outlistT);

				if (beanProcessRelation.containsKey(outlistT.getBean())) {
					beanProcessRelation.get(outlistT.getBean()).add(
							result.getString("process"));
				} else {
					ArrayList<String> beanP = new ArrayList<String>();
					beanP.add(result.getString("process"));
					beanProcessRelation.put(outlistT.getBean(), beanP);
				}
				outlistT = null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
