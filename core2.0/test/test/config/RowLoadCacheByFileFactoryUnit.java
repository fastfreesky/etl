package test.config;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.ccindex.config.RowLoadByCache;
import com.ccindex.config.frame.imp.RowLoadDBCacheByFileFactory;
import com.ccindex.config.frame.imp.RowLoadDelayDBCacheByFileFactory;
import com.ccindex.config.frame.imp.RowLoadURLCacheByFileFactory;
import com.ccindex.config.util.Hostname2BuLogByCacheUtil;
import com.ccindex.logic.util.Hostname2BuLogByDB;

/**
 * 
 * @ClassName: RowLoadCacheByFileFactoryUnit 
 * @Description: TODO(这里用一句话描述这个类的作用) 测试   RowLoadCacheByFileFactory
 * @author  kaiyi.liu
 * @date 2013-6-4 下午3:48:55 
 *
 */
public class RowLoadCacheByFileFactoryUnit {

	public static void main(String[] args) {
		  //new AAA().test();  
        for (int i = 0; i < 100 ; i++) {
        	 Thread th1 = new Thread(new Runnable() {
     			@Override
     			public void run() {
     				Random r = new Random();
     				try {
						Thread.sleep( r.nextInt(2000) );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
     				// URL -> cache File
     				//t_run();
     				
     				// Mysql -> cache File
     				//db_run();
     				
     				db_methon(); 
     				
     			}
     		});  
        	
        	th1.start();
		}
       
	}
	
	/**
	 * 
	 * @Title: t_run 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * 	     使用场景：
	 * 		远程获取URL配置文件 并 缓存到本地
	 * void
	 * @throws
	 */
	static public void t_run(){
		try {
			RowLoadByCache<URL, File,List<Map<String, Object>> > f = new RowLoadURLCacheByFileFactory();

			URL uurl = new URL("http://210.14.132.235:8888/down/home/kaiyi.liu/GWB_IPRange.txt");
			
			f.setSource(uurl);
			f.setCacheSource(new File("D:\\test\\GWB_IPRange.2.txt"));
			f.setSplit("\t");
			f.setColums("iprange", "name", "hostnames");
			f.setLock(new File("D:\\test\\GWB_IPRange.2.txt.lock"));
			
			List<Map<String, Object>> data = f.getData();
			
			System.out.println(data.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @Title: db_run 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * 	     使用场景：
	 * 		远程获取URL配置文件 并 缓存到本地
	 * void
	 * @throws
	 */
	static public void db_run(){
		try {
			RowLoadByCache<Connection, File, List<Map<String, Object>>> f = new RowLoadDBCacheByFileFactory();

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// Mysql 的驱动
			// 2. 获取数据库的连接
			java.sql.Connection conn = java.sql.DriverManager
					.getConnection(
							"jdbc:mysql://223.203.192.248/ccindex?useUnicode=true&characterEncoding=utf8",
							"dba", "dba");
			
			f.setSource(conn);
			f.setCacheSource(new File("D:\\test\\GWB_IPRange.txt"));
			f.setSplit("\t");
			f.setLoadCommit("select hostname , business , log_type  from node_list where demand='HS'");
			f.setColums("hostname","business","log_type");
			f.setLock(new File("D:\\test\\GWB_IPRange.txt.lock"));
			List<Map<String, Object>> data = f.getData();
			
			System.out.println(data.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 
	 * @Title: db_run 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * 	     使用场景：
	 * 		远程获取URL配置文件 并 缓存到本地
	 * void
	 * @throws
	 */
	static public void db_methon(){
		try {
//			RowLoadByCache<String, File, List<Map<String, Object>>> f = new RowLoadDelayDBCacheByFileFactory();
//
//			try {
//				Class.forName("com.mysql.jdbc.Driver");
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			// 懒散缓存加载 node_rule
//			f.setSource("jdbc:mysql://223.203.192.248/ccindex?useUnicode=true&characterEncoding=utf8&user=dba&password=dba");
//			f.setCacheSource(new File("D:\\test\\data\\node_rule.txt"));
//			f.setSplit("\t");
//			f.setLoadCommit("select demand_name from node_rule where rule_name='etl' order by grade desc");
//			f.setColums("demand_name");
//			f.setLock(new File("D:\\test\\data\\node_rule.txt.lock"));
//			List<Map<String, Object>> demand_grade = f.getData();
//			String str_demand_grade = "";
//			for (Map<String, Object> map :demand_grade ) 
//				str_demand_grade+= map.get("demand_name")+",";
//			str_demand_grade = str_demand_grade.substring(0, str_demand_grade.length()-1);
//			
//			
//			// 懒散缓存加载 node_list 
//			f.setSource("jdbc:mysql://223.203.192.248/ccindex?useUnicode=true&characterEncoding=utf8&user=dba&password=dba");
//			f.setCacheSource(new File("D:\\test\\data\\node_list.txt"));
//			f.setSplit("\t");
//			f.setLoadCommit("select hostname,business,log_type,demand  from node_list ");
//			f.setColums("hostname","business","log_type","demand");
//			f.setLock(new File("D:\\test\\data\\node_list.txt.lock"));
//			List<Map<String, Object>> data = f.getData();
			
			
			Hostname2BuLogByDB h = Hostname2BuLogByCacheUtil.getHostname2BuLogByDB(
					"jdbc:mysql://223.203.192.248/ccindex?useUnicode=true&characterEncoding=utf8&user=dba&password=dba",
					"D:\\test\\data\\",
					"etl"
					);
			System.out.println(h.get("CNC-ZZ-7-3nF").toString());	
			
			
			//System.out.println(data.size()+"\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
