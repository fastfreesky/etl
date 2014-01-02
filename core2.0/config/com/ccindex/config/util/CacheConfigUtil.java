package com.ccindex.config.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoadByCache;
import com.ccindex.config.frame.imp.RowLoadDelayDBCacheByFileFactory;

public class CacheConfigUtil {

	public static List<Map<String, Object>> cache(String db_url,
			String cache_file, String commit, String... colums)
			throws Exception {

		RowLoadByCache<String, File, List<Map<String, Object>>> f = new RowLoadDelayDBCacheByFileFactory();

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 懒散缓存加载 node_list
		f.setSource(db_url);
		File cfile = new File(cache_file);
		new File(cfile.getParent()).mkdirs();
		f.setCacheSource(cfile);
		f.setSplit("\t");
		f.setLoadCommit(commit);
		f.setColums(colums);
		f.setLock(new File(cache_file + ".lock"));
		return f.getData();

	}

	public static void main(String[] args) throws Exception {

		for (int i = 0; i < 10; i++) {
			List<Map<String, Object>> oo = CacheConfigUtil
					.cache("jdbc:mysql://223.203.192.248/ccindex?useUnicode=true&characterEncoding=utf8&user=dba&password=dba",
							"D:\\test\\test\\12534\\1.txt",
							"select hostname,business from node_list ",
							"hostname", "business");
			System.out.println(oo.size());
		}

	}
}
