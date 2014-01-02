package com.ccindex.constant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @ClassName: Constant
 * @Description: TODO(这里用一句话描述这个类的作用)常用的变量
 * @author tianyu.yang
 * @date 2013-3-13 上午10:57:51
 * 
 */
public class Constant {

	// 主机
	private static String hostname = null;
	// 集群
	private static String cluster;
	private static Pattern clusterP = Pattern.compile(Regular.CLUSTER);
	static {
		InetAddress ia;
		try {
			ia = InetAddress.getLocalHost();
			hostname = ia.getHostName();
			Matcher mathcer = clusterP.matcher(hostname);
			if (mathcer.find()) {
				cluster = mathcer.group(1).toString().toLowerCase();
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static String getHostname() {
		return hostname;
	}

	/**
	 * 
	 * @Title: getCluser
	 * @Description: TODO(这里用一句话描述这个方法的作用)通过本机名获取集群信息 ,不区分大小写
	 * @return String
	 * @throws
	 */
	public static String getCluser() {
		if (cluster == null) {
			cluster = "default";
		}

		return cluster;
	}

	public static void main(String[] args) {

		System.out.println(getHostname());
		System.out.println(getCluser());

	}
}
