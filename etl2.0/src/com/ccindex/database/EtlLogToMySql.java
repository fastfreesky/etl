package com.ccindex.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ccindex.constant.Regular;
import com.ccindex.factory.count.CountProcessForEtl;
import com.ccindex.factory.count.CountProcessForIdleSpeed;
import com.ccindex.factory.count.CountProcessMapForEtl;
import com.ccindex.main.Main;

/**
 * 
 * @ClassName: EtlLog
 * @Description: TODO(这里用一句话描述这个类的作用)清洗结果入库操作
 * @author tianyu.yang
 * @date 2013-1-10 下午6:51:22
 * 
 */
public class EtlLogToMySql {

	// 数据库连接
	// Statement stmt = null;
	// 输入缓冲区
	private static CountProcessMapForEtl etlOut = new CountProcessMapForEtl();
	private static DataBase dataBase = null;
	protected static Logger logger = Logger.getLogger(EtlLogToMySql.class);

	public static void init(String data, String usr, String pw) {
		dataBase = new DataBase(data, usr, pw);
	}

	/**
	 * 
	 * @Title: etlLog
	 * @Description: TODO(这里用一句话描述这个方法的作用)将清洗日志入库
	 * @param job
	 *            任务名字
	 * @param logFile
	 *            日志文件
	 * @throws Exception
	 *             void
	 * @throws
	 */
	public static void etlLogToMySql(String job, String logFile) {

		logger.info("Begin to input etlLog to MySql " + logFile);
		// 数据库连接
		Connection conn = dataBase.getConnectDba();
		if (conn == null) {
			logger.info("Connect Failed ");
			return;
		}

		// 处理数据
		boolean ret = dialEtlData(logFile);
		if (!ret) {
			dataBase.Close();
			logger.info("Such File is Not exists " + logFile);
			return;
		}

		// 入库
		insertEtlDataToMysql(conn);

		dataBase.Close();

		logger.info("End to input etlLog to MySql " + logFile);
	}

	public static boolean loadLogToMySql(String loadFile) {
		logger.info("Begin to input loadLog to MySql " + loadFile);
		// 数据库连接
		Connection conn = dataBase.getConnectDba();
		// 入库
		insertLoadDataToMysql(conn, loadFile);

		dataBase.Close();
		logger.info("End to input loadLog to MySql " + loadFile);

		return true;
	}

	private static void insertLoadDataToMysql(Connection conn, String loadFile) {
		PreparedStatement prestate = null;
		String sql = "insert into etl_load values(null,? ,?,?,?,?,?,?,?,?,?,?,?, ?,?)";
		Pattern rexFilename = Pattern.compile(Regular.LOAD);

		InputStream is = null;

		try {
			is = new FileInputStream(new File(loadFile));
			prestate = conn.prepareStatement(sql);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String line;

		try {
			while ((line = in.readLine()) != null) {
				try {

					Matcher match = rexFilename.matcher(line);
					if (match.find()) {
						String[] values = match.group(1).trim().split(",");
						if (values.length < 14) {
							continue;
						}
						int index = 1;
						prestate.setString(index++, values[0]);
						prestate.setString(index++, values[1]);
						prestate.setString(index++, values[2]);
						prestate.setString(index++, values[3]);
						prestate.setString(index++, values[4]);
						prestate.setLong(index++, Long.parseLong(values[5]));
						if (values[6].equals("null")) {
							prestate.setString(index++, null);
						} else {
							prestate.setString(index++, values[6]);
						}

						if (values[7].equals("null")) {
							prestate.setString(index++, null);
						} else {
							prestate.setString(index++, values[7]);
						}

						if (values[8].equals("null")) {
							prestate.setString(index++, null);
						} else {
							prestate.setString(index++, values[8]);
						}

						if (values[9].equals("null")) {
							prestate.setString(index++, null);
						} else {
							prestate.setString(index++, values[9]);
						}

						prestate.setString(index++, values[10]);
						prestate.setString(index++, values[11]);
						prestate.setString(index++, values[12]);
						prestate.setString(index++, values[13]);
						prestate.executeUpdate();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println(e);
					continue;
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				prestate.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Title: insertData
	 * @Description: TODO(这里用一句话描述这个方法的作用)将数据插入数据库 void
	 * @throws
	 */
	private static void insertEtlDataToMysql(Connection conn) {
		Map<String, CountProcessForEtl> etlresults = etlOut.getMaps();

		PreparedStatement prestate = null;
		String sql = "insert into etl_db_new values(null,? ,?,?,?,?,?,?,?,?,?,?,?, ? ,?,?)";

		try {
			prestate = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator iterator = etlresults.entrySet().iterator();

		while (iterator.hasNext()) {
			try {
				Map.Entry entry = (Map.Entry) iterator.next();
				CountProcessForEtl countProcess = (CountProcessForEtl) entry
						.getValue();

				int index = 1;
				prestate.setString(index++, countProcess.getEtl_type());
				prestate.setString(index++, countProcess.getEtlDate());
				prestate.setString(index++, countProcess.getAccept_node());
				prestate.setString(index++, countProcess.getSrc_node());
				prestate.setString(index++,
						countProcess.getSrc_file_comp_type());
				prestate.setLong(index++, countProcess.getSrc_file_size());
				prestate.setLong(index++, countProcess.getMatch_len());
				prestate.setLong(index++, countProcess.getSrc_file_num());
				prestate.setLong(index++, countProcess.getMatch_num());
				prestate.setString(index++, countProcess.getSrc_province());
				prestate.setString(index++, countProcess.getSrc_isp());
				prestate.setString(index++, countProcess.getEtl_start());
				prestate.setString(index++, countProcess.getEtl_end());

				// 转化到ms
				prestate.setLong(index++, countProcess.getEtl_run_time());
				prestate.setString(index++, countProcess.getMatch_type());

				prestate.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				logger.info("Alread exists " + e.getSQLState());
				continue;
			}
		}

		try {
			prestate.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static boolean dialEtlData(String ansLog) {

		try {
			// String accept_ip = ia.getHostAddress();// 获取计算机IP
			SimpleDateFormat lformat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");

			File file = new File(ansLog);
			if (!file.exists()) {
				return false;
			}

			InputStream is = new FileInputStream(file);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			Pattern rexFilename = Pattern.compile(Regular.OVERTASK);
			Pattern rexSrc = Pattern.compile(Regular.REXSRC);

			String line;
			while ((line = in.readLine()) != null) {
				Matcher m = rexFilename.matcher(line);
				if (m.find()) {
					// 清洗文件类型
					String processtype = m.group(1);
					String etlDate = m.group(2);
					long src_file_num = Long.parseLong(m.group(3));
					long match_num = Long.parseLong(m.group(4));
					long src_len = Long.parseLong(m.group(5));
					long match_len = Long.parseLong(m.group(6));
					long etl_run_start = Long.parseLong(m.group(7));
					long etl_run_end = Long.parseLong(m.group(8));
					String accept_host = m.group(9);// 获取计算机主机名

					String src_file = m.group(10);
					String src_province = "";
					String src_isp = "";
					String src_node = "";
					Matcher ms = rexSrc.matcher(src_file);
					if (ms.find()) {
						src_node = ms.group(1);
						src_isp = ms.group(2);
						src_province = ms.group(3);
					}

					String key = processtype + "1.0" + src_node + accept_host;

					CountProcessForEtl etlCount = null;
					if (etlOut.isExists(key)) {
						etlCount = etlOut.getValue(key);
					} else {
						etlCount = new CountProcessForEtl();
						// 清洗文件类型
						etlCount.setEtlDate(etlDate);
						etlCount.setEtl_type(processtype);
						etlCount.setSrc_node(src_node);
						etlCount.setSrc_isp(src_isp);
						etlCount.setSrc_province(src_province);
						etlCount.setSrc_file_comp_type("gz");
						etlCount.setMatch_type("1.0");
					}
					// 记录当前值

					etlCount.setSrc_file_num(src_file_num);
					etlCount.setMatch_num(match_num);
					etlCount.setMatch_len(match_len);

					String start_time = lformat.format(new Date(etl_run_start));
					String end_time = lformat.format(new Date(etl_run_end));

					// 比对出最早的时间,比对出最晚的时间
					String old_time_start = etlCount.getEtl_start();
					String old_time_end = etlCount.getEtl_end();
					if (old_time_start == null || old_time_end == null) {
						etlCount.setEtl_start(start_time);
						etlCount.setEtl_end(end_time);
					} else {
						if (0 > start_time.compareTo(old_time_start)) {
							etlCount.setEtl_start(start_time);
						}
						if (0 < end_time.compareTo(old_time_end)) {
							etlCount.setEtl_end(end_time);
						}
					}
					// 目前单位为ms毫秒
					etlCount.setEtl_run_time(etl_run_end - etl_run_start);

					etlCount.setAccept_node(accept_host);

					etlCount.setSrc_file_size(src_len);

					etlOut.SetValue(etlCount, key);

				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public static void main(String[] args) {
		SimpleDateFormat lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String[] arg = { "1366946134030", "1366923806633", "1366923842482",
				"1366923789394", "1366946138324", "1366923829384",
				"1366923874005", "1366923889027", "1366923902610",
				"1366923953080", "1366923879998", "1366923967605",
				"1366923860074" };
		for (String ar : arg) {
			long a = Long.parseLong(ar);

			System.out.println(lformat.format(new Date(a)));

		}

	}
}
