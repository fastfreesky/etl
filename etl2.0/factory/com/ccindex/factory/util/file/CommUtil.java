package com.ccindex.factory.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ccindex.main.SetConfEtl;

/**
 * 
 * @ClassName: CommUtil
 * @Description: TODO(这里用一句话描述这个类的作用)常用的UTR工具类
 * @author tianyu.yang
 * @date 2013-1-11 下午6:44:51
 * 
 */
public class CommUtil {

	/**
	 * 
	 * @Title: packageFilePath
	 * @Description: TODO(这里用一句话描述这个方法的作用)组装路径,最后一级保持最后一个参数形式
	 * @param args
	 * @return String
	 * @throws
	 */
	public static String packageFilePath(String... args) {
		StringBuffer buf = new StringBuffer();
		if (args.length <= 0) {
			return null;
		} else {
			for (int i = 0; i < args.length; ++i) {
				if (i != args.length - 1) {
					if (args[i].endsWith("/")) {
						buf.append(args[i]);
					} else {
						buf.append(args[i]).append("/");
					}

				} else {
					buf.append(args[i]);
				}
			}

			return buf.toString();
		}

	}

	/**
	 * 
	 * @Title: getLogType
	 * @Description: TODO(这里用一句话描述这个方法的作用)通过文件名,获取日志格式
	 * @param fileName
	 *            格式:日期.设备.清洗类型.日志格式.[local|hdfs]
	 * @return String
	 * @throws
	 */
	public static String getLogType(String fileName) {
		return fileName.split("\\.")[3];
	}

	public static String getLogLocalOrHdfs(String fileName) {
		return fileName.split("\\.")[4];
	}

	public static String packageFileName(String... args) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < args.length; ++i) {
			if (i == args.length - 1) {
				buf.append(args[i]);
			} else {
				buf.append(args[i]).append(".");
			}
		}
		return buf.toString();
	}

	public static void mkDirs(String dirPath, boolean isLocal) {
		if (isLocal) {
			File file = new File(dirPath);
			file.mkdirs();

		} else {
			try {
				Configuration conf = new Configuration();
				FileSystem fs = FileSystem.get(conf);

				fs.mkdirs(new Path(dirPath));
				fs.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		System.out.println(packageFilePath("/home", "dest", "package"));
	}
}
