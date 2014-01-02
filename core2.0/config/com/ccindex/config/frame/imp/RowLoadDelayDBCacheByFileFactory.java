package com.ccindex.config.frame.imp;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.ccindex.config.ConfigLock;
import com.ccindex.config.RowDump;
import com.ccindex.config.RowLoad;
import com.ccindex.config.RowLoadByCache;
import com.ccindex.config.frame.logic.RowLoadCacheLogic;
import com.ccindex.config.imp.ConfigLockFile;
import com.ccindex.config.imp.RowDumpFile;
import com.ccindex.config.imp.RowLoadDB;
import com.ccindex.config.imp.RowLoadDelayDB;
import com.ccindex.config.imp.RowLoadFile;

/**
 * 
 * @ClassName: RowLoadCacheByFileFactory
 * @Description: TODO(这里用一句话描述这个类的作用) 加载远程URL配置 加载缓存到 本地文件系统，后面线程、进程直接去读取本地缓存配置
 * @author kaiyi.liu
 * @date 2013-6-4 下午3:15:59
 * 
 */
public class RowLoadDelayDBCacheByFileFactory implements
		RowLoadByCache<String, File, List<Map<String, Object>>> {
	
	// 远程 load Url Text
	private RowLoad<String, List<Map<String, Object>>> load = new RowLoadDelayDB();
	
	// 本地缓存 load 动作对象
	private RowLoad<File, List<Map<String, Object>>> cacheLoad = new RowLoadFile();
	// 本地缓存 dump 动作对象
	private RowDump<File, List<Map<String, Object>>> cacheDump = new RowDumpFile();
	// 多进程、多线程 文件拥塞锁
	private ConfigLock<File> lock = new ConfigLockFile();

	/**
	 * 
	 * (非 Javadoc)
	 * 
	 * @Title: getData
	 * @Description:
	 * @return
	 * @see <pre>
	 * 
	 * </pre>
	 */
	@Override
	public List<Map<String, Object>> getData() {
		return RowLoadCacheLogic.getData(this);
	}

	@Override
	public void setSource(String f) {
		load.setSource(f);
	}

	@Override
	public void setCacheSource(File t) {
		cacheLoad.setSource(t);
		cacheDump.dumpTo(t);
	}

	@Override
	public void setLock(File lockFile) throws Exception {
		this.lock.initResourcesLock(lockFile);
	}

	@Override
	public void setSplit(String commit) {
		this.load.setSplit(commit);
		this.cacheLoad.setSplit(commit);
		this.cacheDump.setSplit(commit);
	}

	@Override
	public void setColums(String... colums) {
		this.load.setColums(colums);
		this.cacheLoad.setColums(colums);
		this.cacheDump.setColums(colums);
	}

	@Override
	public boolean existsCacheSource() {
		return cacheLoad.getSource().exists();
	}

	@Override
	public ConfigLock<File> getLock() throws Exception {
		return this.lock;
	}

	@Override
	public RowLoad<String, List<Map<String, Object>>> getRowLoad() {
		return this.load;
	}

	@Override
	public RowLoad<File, List<Map<String, Object>>> getCacheRowLoad() {
		return cacheLoad;
	}

	@Override
	public RowDump<File, List<Map<String, Object>>> getCacheRowDump() {
		return cacheDump;
	}

	@Override
	public void setLoadCommit(String commit) {
		this.load.setLoadCommit(commit);
		this.cacheLoad.setLoadCommit(commit);
	}


}
