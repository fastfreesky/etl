package com.ccindex.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ccindex.conf.BeanConf;
import com.ccindex.conf.ProcessConf;
import com.ccindex.config.ConfigUtil;
import com.ccindex.constant.Constant;
import com.ccindex.database.EtlConfFromMySql;
import com.ccindex.database.EtlLogToMySql;
import com.ccindex.database.OutList;
import com.ccindex.database.OutList.ETLOUTWAY;
import com.ccindex.factory.addTask.AddFileTaskConfContext;
import com.ccindex.factory.conf.ConfContextImpl;
import com.ccindex.factory.output.WriteenFileConfContext;
import com.ccindex.factory.process.ProcessConfContext;
import com.ccindex.factory.util.file.CommUtil;
import com.ccindex.mInterface.Bean;
import com.ccindex.main.Main;
import com.ccindex.struc.queue.BuLogType;
import com.ccindex.util.factory.ObjectFactory;

/**
 * 
 * @ClassName: ParseYamlConf
 * @Description: TODO(这里用一句话描述这个类的作用)解析etl.yaml的配置文件
 * @author tianyu.yang
 * @date 2013-8-20 下午1:24:54
 * 
 */
public class ParseYamlConf {

	private ConfigUtil config;

	// 输入文件数据库配置文件
	private String mysqlDataBase;
	private String mysqlUser;
	private String mysqPwd;

	public String getMysqlDataBase() {
		return mysqlDataBase;
	}

	public void setMysqlDataBase(String mysqlDataBase) {
		this.mysqlDataBase = mysqlDataBase;
	}

	public String getMysqlUser() {
		return mysqlUser;
	}

	public void setMysqlUser(String mysqlUser) {
		this.mysqlUser = mysqlUser;
	}

	public String getMysqPwd() {
		return mysqPwd;
	}

	public void setMysqPwd(String mysqPwd) {
		this.mysqPwd = mysqPwd;
	}

	// 日志记录目录
	private String recordLogDir;

	// 清洗日期
	private String etlDate;
	// 清洗进度文件
	private String etlProcessFile;
	// 清洗状态记录文件,断点续传
	private String breakPointRecordFile;
	// 上载日志记录文件
	private String loadRecordFile;

	// 线程数
	private int threadNum;
	/*
	 * process配置文件
	 */
	private List<Map> processConf;
	/**
	 * bean配置文件
	 */
	private List<Map> beanConf;
	/**
	 * bean类实例化后的配置
	 */
	private ConfContextImpl<BeanConf> beanConfContext;
	/**
	 * process实例化的配置
	 */
	private ConfContextImpl<ProcessConf> processConfContext;
	/**
	 * out输出配置文件
	 */
	private List<Map> out;
	/**
	 * bean与process的对应关系
	 */
	private HashMap<String, ArrayList<String>> beanProcessRelation;
	/**
	 * 增加文件的confContext
	 */
	private AddFileTaskConfContext addFileTaskConfContext;
	/**
	 * 输出的confContext类
	 */
	private WriteenFileConfContext writeenFileConfContext;
	// 待实例化的任务类
	private String taskClass;
	// 待实例化的输出类
	private String outClass;

	private static Logger logger = Logger.getLogger(ParseYamlConf.class);

	private void initMySqlConfig() {

		EtlConfFromMySql.init(getMysqlDataBase(), getMysqlUser(), getMysqPwd(),
				getRecordLogDir());
		EtlLogToMySql.init(getMysqlDataBase(), getMysqlUser(), getMysqPwd());
	}

	public ParseYamlConf(String confFile, String[] args)
			throws FileNotFoundException {

		InputStream is = new FileInputStream(new File(confFile));
		config = new ConfigUtil(is);
		config.setAll(args);

		Map conndba = (Map) config.get("etl", "conndba");
		setMysqlDataBase(conndba.get("database").toString());
		setMysqlUser(conndba.get("user").toString());
		setMysqPwd(conndba.get("pwd").toString());
		setRecordLogDir(config.getString("etl", "state.path"));

		// 初始化数据库操作
		initMySqlConfig();

		setEtlDate(config.getString("etl", "date"));
		setEtlProcessFile(CommUtil.packageFileName(getEtlDate(),
				config.getString("etl", "state.record.file")));
		setBreakPointRecordFile(CommUtil.packageFileName(getEtlDate(),
				config.getString("etl", "state.freeze_file")));
		setLoadRecordFile(CommUtil.packageFileName(getEtlDate(),
				config.getString("etl", "state.load.record.file")));

		// 线程数
		setThreadNum(Integer.parseInt(config.getString("octopus",
				"run.thread.num")));
		// 任务类实例化的类名字
		setTaskClass(config.getString("octopus", "task.class.name").toString());
		// 输出类实例化的类名字
		setOutClass(config.getString("octopus", "output.class.name").toString());

		beanConf = (List<Map>) config.get("octopus", "bean.class.names");
		beanConfContext = new ConfContextImpl<BeanConf>();
		for (Map cof : beanConf) {
			if (cof.containsKey("name") && cof.containsKey("class.name")) {
				String name = (String) cof.get("name");
				String className = (String) cof.get("class.name");

				BeanConf beanConf = new BeanConf();
				beanConf.setClassname(className);
				beanConf.setName(name);
				beanConfContext.add(beanConf);
				beanConf = null;
			}
		}

		processConf = (List<Map>) config.get("octopus", "process.class.names");
		processConfContext = new ConfContextImpl<ProcessConf>();
		for (Map cof : processConf) {
			if (cof.containsKey("name") && cof.containsKey("class.name")
					&& cof.containsKey("out.way")) {
				String name = (String) cof.get("name");
				String className = (String) cof.get("class.name");
				String outWay = (String) cof.get("out.way");

				ProcessConf processConf = new ProcessConf();
				processConf.setName(name);
				processConf.setClassname(className);
				ProcessConfContext processConfContextN = new ProcessConfContext();
				processConfContextN.setHostName(Constant.getHostname());
				processConfContextN.setEtlOutWay(outWay);
				processConf.setConfContext(processConfContextN);

				processConfContext.add(processConf);
				processConf = null;
			}
		}

		List<Map> relation = (List<Map>) config.get("octopus",
				"bean.process.relation");
		beanProcessRelation = EtlConfFromMySql.getBeanProcessRelation();
		for (Map cof : relation) {
			if (cof.containsKey("bean") && cof.containsKey("process")) {
				String name = (String) cof.get("bean");
				String classNameS = (String) cof.get("process");
				if (classNameS == null) {
					continue;
				}
				ArrayList<String> relationList = new ArrayList<String>();
				String className[] = classNameS.split(",");
				for (String n : className) {
					relationList.add(n);
				}

				beanProcessRelation.put(name, relationList);
				relationList = null;
			}
		}

		setAddFileTaskConfContext();

		setWriteenFileConfContext();

		logger.info(toString());
	}

	public WriteenFileConfContext getWriteenFileConfContext() {
		return writeenFileConfContext;
	}

	public void setWriteenFileConfContext() {
		writeenFileConfContext = new WriteenFileConfContext();

		int buffMem = Integer.parseInt(config.get("etl", "write.file.buffMem")
				.toString());
		int maxbuffMem = Integer.parseInt(config.get("etl",
				"write.file.maxbuffMem").toString());
		int level = Integer.parseInt(config.get("etl", "write.file.level")
				.toString());

		writeenFileConfContext.setBuffMem(buffMem);
		writeenFileConfContext.setMaxbuffMem(maxbuffMem);
		writeenFileConfContext.setLevel(level);

		List<Map> outList = (List<Map>) config.get("etl", "output.path");
		// 输出路径,用本地配置文件中替换数据库的配置输出
		HashMap<String, OutList> outListH = EtlConfFromMySql.getOutList();
		for (Map cof : outList) {
			if (cof.containsKey("name") && cof.containsKey("local_path")
					&& cof.containsKey("hdfs_path")) {
				String name = (String) cof.get("name");
				String local_path = (String) cof.get("local_path");
				String hdfs_path = (String) cof.get("hdfs_path");

				OutList list = outListH.get(name);
				if (list == null) {
					continue;
				}
				list.setDate(getEtlDate());
				if (local_path != null && hdfs_path != null) {
					list.setOutType(ETLOUTWAY.all);
					list.setLocal_path(local_path);
					list.setHdfs_path(hdfs_path);

				} else if (local_path != null && hdfs_path == null) {
					list.setOutType(ETLOUTWAY.local);
					list.setLocal_path(local_path);

				} else if (local_path == null && hdfs_path != null) {
					list.setOutType(ETLOUTWAY.hdfs);
					list.setHdfs_path(hdfs_path);
				} else {
					logger.info(name + " Local OutPut Conf is Null");
				}

				list = null;
			}
		}

		Iterator<Entry<String, OutList>> itr = outListH.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, OutList> entry = itr.next();
			OutList list = entry.getValue();
			if (list.getHdfs_path() != null) {
				list.setHdfs_path(CommUtil.packageFilePath(list.getHdfs_path(),
						getEtlDate(), Constant.getHostname()));
			}
			if (list.getLocal_path() != null) {
				list.setLocal_path(CommUtil.packageFilePath(
						list.getLocal_path(), getEtlDate()));
			}
		}

		writeenFileConfContext.setOutList(outListH);
		outListH = null;

	}

	/**
	 * 
	 * @Title: getRandomMinute
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取一个随机的整数(0-3599) s
	 * @return int
	 * @throws
	 */
	public static int getRandomMinute() {
		// System.currentTimeMillis()
		Random rdm = new Random();
		return Math.abs(rdm.nextInt()) % 3600;
	}

	private String packageKillDate() {
		String[] confiDate = config.get("etl", "killDate").toString()
				.split(" ");
		// 比对当前时间,是否为最大时间,如果是,强制结束清洗追加
		Date start = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(start);
		String timeNow = null;
		if (confiDate[0].equalsIgnoreCase("tomorrow")) {
			calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
			timeNow = confiDate[1];
		} else {
			timeNow = confiDate[0];
		}
		start = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(start);
		return dateString + " " + timeNow;
	}

	/**
	 * 
	 * @Title: setAddFileTaskConfContext
	 * @Description: TODO(这里用一句话描述这个方法的作用)设置读入文件路径的配置,整合配置及从数据库获取的输入路径 void
	 * @throws
	 */
	private void setAddFileTaskConfContext() {
		addFileTaskConfContext = new AddFileTaskConfContext();

		addFileTaskConfContext.setBreakPointFile(CommUtil.packageFilePath(
				getRecordLogDir(), getBreakPointRecordFile()));
		addFileTaskConfContext.setProcessFile(CommUtil.packageFilePath(
				getRecordLogDir(), getEtlProcessFile()));

		// 先读取本地的路径
		String rexFileStr = config.get("etl", "input.regex.file").toString();

		addFileTaskConfContext.setRegularFileSuffix(rexFileStr);

		// 对max进行阀值调整,控制在3-4小时内随机值
		addFileTaskConfContext.setMaxTime((Integer.parseInt(config.get("etl",
				"maxTime").toString()) + getRandomMinute()) * 1000);
		addFileTaskConfContext.setLoopTime(Integer.parseInt(config.get("etl",
				"loopTime").toString()) * 1000);
		// 确定日期是否为明日
		addFileTaskConfContext.setKillDate(packageKillDate());

		// 从数据库提取输入路径
		ArrayList<String> arrayInputList = EtlConfFromMySql.getInputList();
		// 从本地获取新增路径或者覆盖路径
		List<Map> paths = (List<Map>) config.get("etl", "input.path");
		for (Map path : paths) {
			if (!path.containsKey("name")) {
				continue;
			}
			String name = path.get("name").toString().trim();
			if (name.equals("add")) {
				// 暂不做处理
				if (path.get("path") == null) {
					continue;
				}
			} else if (name.equals("over")) {
				if (path.get("path") != null) {
					arrayInputList.clear();
				} else {
					continue;
				}
			} else {
				continue;
			}

			String pathsAll = path.get("path").toString();
			for (String pathsplit : pathsAll.split(",")) {
				if (!arrayInputList.contains(pathsplit)) {
					arrayInputList.add(pathsplit);
				}
			}
		}

		// 输入日期中添加时间路径
		ArrayList<String> arrayInputListN = new ArrayList<String>();
		for (String path : arrayInputList) {
			arrayInputListN.add(CommUtil.packageFilePath(path, getEtlDate()));
		}
		// 结合本地,修改输入路径为最新路径
		arrayInputList.clear();
		arrayInputList.addAll(arrayInputListN);
		arrayInputList = null;
		addFileTaskConfContext.setInputFilePath(arrayInputListN);

	}

	public AddFileTaskConfContext getAddFileTaskConfContext() {
		return addFileTaskConfContext;
	}

	public String getBreakPointRecordFile() {
		return breakPointRecordFile;
	}

	public void setBreakPointRecordFile(String breakPointRecordFile) {
		this.breakPointRecordFile = breakPointRecordFile;
	}

	public HashMap<String, ArrayList<String>> getBeanProcessRelation() {
		return beanProcessRelation;
	}

	public ConfContextImpl<BeanConf> getBeanConfContext() {
		return beanConfContext;
	}

	public String getEtlDate() {
		return etlDate;
	}

	public String getEtlProcessFile() {
		return etlProcessFile;
	}

	public List<Map> getOut() {
		return out;
	}

	public String getOutClass() {
		return outClass;
	}

	public ConfContextImpl getProcessConfContext() {
		return processConfContext;
	}

	public String getRecordLogDir() {
		return recordLogDir;
	}

	public String getTaskClass() {
		return taskClass;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setEtlDate(String etlDate) {
		this.etlDate = etlDate;
	}

	public void setEtlProcessFile(String etlProcessFile) {
		this.etlProcessFile = etlProcessFile;
	}

	public void setOut(List<Map> out) {
		this.out = out;
	}

	public void setOutClass(String outClass) {
		this.outClass = outClass;
	}

	public void setRecordLogDir(String recordLogDir) {
		if (recordLogDir != null) {
			CommUtil.mkDirs(recordLogDir, true);
		}

		this.recordLogDir = recordLogDir;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	
	

	public String getLoadRecordFile() {
		return loadRecordFile;
	}

	public void setLoadRecordFile(String loadRecordFile) {
		this.loadRecordFile = loadRecordFile;
	}

	@Override
	public String toString() {
		return "ParseYamlConf [mysqlDataBase=" + mysqlDataBase + ", mysqlUser="
				+ mysqlUser + ", mysqPwd=" + mysqPwd + ", recordLogDir="
				+ recordLogDir + ", etlDate=" + etlDate + ", etlProcessFile="
				+ etlProcessFile + ", breakPointRecordFile="
				+ breakPointRecordFile + ", threadNum=" + threadNum
				+ ", processConf=" + processConf + ", beanConf=" + beanConf
				+ ", beanConfContext=" + beanConfContext
				+ ", processConfContext=" + processConfContext + ", out=" + out
				+ ", beanProcessRelation=" + beanProcessRelation
				+ ", addFileTaskConfContext=" + addFileTaskConfContext
				+ ", writeenFileConfContext=" + writeenFileConfContext
				+ ", taskClass=" + taskClass + ", outClass=" + outClass + "]";
	}

}
