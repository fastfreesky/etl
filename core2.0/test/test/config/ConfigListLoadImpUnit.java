package test.config;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ccindex.config.RowLoad;
import com.ccindex.config.imp.RowLoadDB;
import com.ccindex.config.imp.RowLoadURL;

public class ConfigListLoadImpUnit {

	public void testURL() throws Exception {
		URL uurl = new URL("http://210.14.132.235:8888/down/home/kaiyi.liu/GWB_IPRange.txt");
		RowLoad<URL,List<Map<String, Object>>> c = new RowLoadURL();
		c.setColums("iprange", "name", "hostnames");
		c.setSplit("\t");
		c.setSource(uurl);
		c.load();
		
		List<Map<String, Object>> ll = c.getData();
		System.out.println(ll.get(1).get("iprange"));
		
	}

	@Test
	public void testSQL() throws Exception {
		//  mysql -h223.203.192.248 -pdba -udba   ccindex
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

		RowLoad<Connection,List<Map<String, Object>>> c = new RowLoadDB();
		c.setColums("hostname","business","log_type");
		c.setSplit("select hostname , business , log_type  from node_list where demand='ALLBU' ");
		c.setSource(conn);
		c.load();
		
		List<Map<String, Object>> ll = c.getData();
		System.out.println(ll.get(1).get("hostname"));
		conn.close();
	}

}
