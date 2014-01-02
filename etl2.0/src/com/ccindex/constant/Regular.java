package com.ccindex.constant;

import java.util.regex.Pattern;

/**
 * 
 * @ClassName: Regular
 * @Description: TODO(这里用一句话描述这个类的作用)存放正则匹配的字符串
 * @author tianyu.yang
 * @date 2013-1-10 下午1:53:26
 * 
 */
public class Regular {
	// 结果记录正则式,用于匹配清洗完成后的日志记录值
	public static String OVERTASK = "overTask:\\(.*?\\)\\[([\\w|\\.]+),(.*?),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(.*?)\\](.*)$";
	// 从清洗完成日志中,获取来源地的正则
	public static String REXSRC = "\\.(([^\\-|\\.]*?)\\-([^\\-|\\.]*?)\\-[^\\-|\\.]*?\\-[^\\-|\\.]*)\\.";

	// 标准FC,另一种正则
	public static final String FCBEAN_REXROW = "^(\\d+)(?:\\.\\d+)?\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+\\S+\\s+(.*?)\\s+\\S+\\s+(\\S+)\\s+(\\S+)\\s\"(.*?)\"\\s\"(.*?)\"\\s+(\\S+)$";
	// FC日志行清洗的正则格式
	// public static String
	// FCBEAN_REXROW="^(\\d+)(?:\\.\\d+)?[ ]+(\\S+)[ ]+(\\S+)[ ]+(\\S+)[ ]+(\\S+)[ ]+\\S+[ ]+(.*?)[ ]+\\S+[ ]+(\\S+)[ ]+(\\S+)[ ]\"([^\"]+)\"[ ]\"([^\"]+)\"";
	// FC日志文件名截取正则格式
	public static String FCBEAN_REXFILENAME = "(?:.*?\\.){3}(.*?)\\..*";
	// FC日志文件清洗频道正则
	public static String FCBEAN_REXCHANNEL = "(?:\\w?://)([^\\?&/]*)";

	// FLVMOVD日志行清洗的正则格式
	public static String FLVMOVDBEAN_REXROW = "^(\\d+)(?:\\.\\d+)?\\s+(\\S+)(?:(?:\\s-){2}\\s\\S+\\s\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s\"([^\"]+)\"\\s\"([^\"]+)\"\\s(?:[^\"]+)\"([^\"]+)\"";
	// FLVMOVD日志文件名截取正则格式
	public static String FLVMOVDBEAN_REXFILENAME = FCBEAN_REXFILENAME;
	// FLVMOVD日志文件清洗频道正则
	public static String FLVMOVDBEAN_REXCHANNEL = FCBEAN_REXCHANNEL;

	// stuffprocess获取URL后缀正则
	public static String STUFFURL = "^.*/.*\\.(\\w{1,8})$";

	// 上载日志格式
	public static String LOAD = "^.*\\[(.*)\\]";

	// 通过主机名获取集群信息的正则
	public static String CLUSTER = ".*-(.*)-.*-.*";
}
