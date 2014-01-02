package com.ccindex.struc.queue;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 
 * @ClassName: FileTypeGather
 * @Description: TODO(这里用一句话描述这个类的作用)给文件添加类型标示集合,如属于RDB日志类型,则添加RDB
 * @author tianyu.yang
 * @date 2013-1-9 下午2:47:52 目前以日志类型进行列别添加,如RDB,FLV等
 */
public class FileTypeGather {
	// 文件
	private File file = null;
	// 该文件大小,字节
	private long size;
	// 该文件属于哪个设备清洗
	private String hostNameEtl = null;
	// 日志来源的设备名称
	private String hostNameSource = null;

	// 文件及类型初始化
	public FileTypeGather(File file, String hostNameSource) {
		this.file = file;
		InetAddress ia = null;

		size = file.length();

		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hostNameEtl = ia.getHostName();// 获取计算机主机名
		this.hostNameSource = hostNameSource;
	}

	public File getFile() {
		return file;
	}

	public long getSize() {
		return size;
	}

	public String getHostNameEtl() {
		return hostNameEtl;
	}

	public String getHostNameSource() {
		return hostNameSource;
	}

}