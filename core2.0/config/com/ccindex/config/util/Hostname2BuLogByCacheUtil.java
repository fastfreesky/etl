package com.ccindex.config.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoadByCache;
import com.ccindex.config.frame.imp.RowLoadDelayDBCacheByFileFactory;
import com.ccindex.logic.util.Hostname2BuLogByDB;

public class Hostname2BuLogByCacheUtil {
	
	public static Hostname2BuLogByDB getHostname2BuLogByDB(
			String db_url,String cache_path,String  node_rule ) throws Exception {
		RowLoadByCache<String, File, List<Map<String, Object>>> f = new RowLoadDelayDBCacheByFileFactory();

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 懒散缓存加载 node_rule
		f.setSource(db_url);
		f.setCacheSource(new File(cache_path+".node_rule.txt"));
		f.setSplit("\t");
		f.setLoadCommit("select demand_name from node_rule where rule_name='"+node_rule+"' order by grade desc");
		f.setColums("demand_name");
		f.setLock(new File(cache_path+".node_rule.txt.lock"));
		List<Map<String, Object>> demand_grade = f.getData();
		String str_demand_grade = "";
		for (Map<String, Object> map : demand_grade)
			str_demand_grade += map.get("demand_name") + ",";
		str_demand_grade = str_demand_grade.substring(0,
				str_demand_grade.length() - 1);

		// 懒散缓存加载 node_list
		f.setSource(db_url);
		f.setCacheSource(new File(cache_path+".node_list.txt"));
		f.setSplit("\t");
		f.setLoadCommit("select hostname,business,log_type,demand  from node_list ");
		f.setColums("hostname", "business", "log_type", "demand");
		f.setLock(new File(cache_path+".node_list.txt.lock"));
		List<Map<String, Object>> data = f.getData();
		
		return new Hostname2BuLogByDB(str_demand_grade, data); 
	}
}
