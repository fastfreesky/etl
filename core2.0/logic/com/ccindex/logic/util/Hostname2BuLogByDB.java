package com.ccindex.logic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ccindex.struc.queue.BuLogType;

/**
 * 
 * @Title: evaluate 
 * @Description: 
 * <pre>
 * 1. 项目组提供  需求设备列表对应清单 
 *    <hostname, 所属事业部, 日志格式类型, 需求名 >
 * 
 * 2. 技术提供一个方法
 *    a) 根据 需求名词（可添多个，以逗号分隔） 提取设备归属清单列表
 *    b) 当遇到设备在不同的清单列表中有冲突（一台设备对应多个事业部、多个日志格式类型）
 *    c) 根据 当前提取 组合[需求名词]规则，由右覆盖左边清单 提取 设备，事业部，日志格式 的一一对应
 * 
 *  
 *  EG：
 *    get("FLV_MVOD,RDB",hostname)
 *    get("FLV_MVOD,HJ",hostname)
 *    
 *    
 * </pre>
 * @param demands
 * @param host
 * @return    
 * @throws
 */
public class Hostname2BuLogByDB {

	private Map<String, BuLogType> hostBuType = new HashMap<String,BuLogType>(); 
	
	private static String database=null, user=null, pwd=null;

	public static void init(String data, String usr, String pw){
		database=data;
		user=usr;
		pwd=pw;
	}
	
	private static BuLogType  No = new BuLogType("No", "No");
	public BuLogType get(String host) {
		
		host  =  host.trim()  ;
		
		if(!hostBuType.containsKey(host))
			return No ;
		else{
			return hostBuType.get(host);
		}
	}
	
	

	
	private Map<String,Integer> demandSize = new HashMap<String, Integer>();
	private Map<String, String[]> datasBu = new HashMap<String,String[]>(); 
	private Map<String, String[]> datasLogType = new HashMap<String,String[]>();
	
	
	public Hostname2BuLogByDB(String demands,List<Map<String, Object>> data) {
		
		String[] matchList = demands.toLowerCase().split(",");
		for (int i = 0; i < matchList.length; i++) {
			demandSize.put(matchList[i], i);
		}
		
		for (Map<String, Object> map : data) {
			String d_hostname = map.get("hostname").toString().trim();
			String d_business =  map.get("business").toString().toLowerCase().trim();
			String d_demand =  map.get("demand").toString().toLowerCase().trim();
			String d_log_type =  map.get("log_type").toString().toLowerCase().trim();
			
			if(!datasBu.containsKey(d_hostname)){
				datasBu.put(d_hostname, new String[matchList.length]);
				datasLogType.put(d_hostname,new String[matchList.length]);
			}
			
			String[] tt = datasBu.get(d_hostname);
			String[] cc = datasLogType.get(d_hostname);
			if( demandSize.containsKey(d_demand) ){
				tt[ demandSize.get(d_demand) ] = d_business ;
				cc[ demandSize.get(d_demand) ] = d_log_type ;
			}
		}
		
		loadConversion();
	}

	/**
	 * @Title: load
	 * @Description: 
	 * @return 
	 */
	public  Hostname2BuLogByDB(String demands) {
		
		init("223.203.192.248/ccindex", "dba", "dba");
		
		String[] matchList = demands.toLowerCase().split(",");
		for (int i = 0; i < matchList.length; i++) {
			demandSize.put(matchList[i], i);
		}
		
		String driver = "com.mysql.jdbc.Driver";
		// URL指向要访问的数据库名scutcs
		//String url = "jdbc:mysql://115.236.185.27:3306/ccindex";
		String url="jdbc:mysql://" +
				database +
				"?useUnicode=true&characterEncoding=utf8";
		// MySQL配置时的用户名
		String user = "dba";
		// Java连接MySQL配置时的密码
		String password = "dba";
		Connection conn = null;
		
		try {
		// 加载驱动程序
		Class.forName(driver);		
		// 连续数据库
		conn = DriverManager.getConnection(url, user, password);
		if(!conn.isClosed()){
			System.out.println("Succeeded connecting to the Database!");
		}
		// statement用来执行SQL语句

		Statement statement = conn.createStatement();
		// 要执行的SQL语句
		String sql = "select hostname,business,log_type,demand from node_list ;";
		ResultSet rs = statement.executeQuery(sql);  
		
		while(rs.next()) {
			String d_hostname = rs.getString("hostname").trim();
			String d_business = rs.getString("business").toLowerCase().trim();
			String d_demand = rs.getString("demand").toLowerCase().trim();
			String d_log_type = rs.getString("log_type").toLowerCase().trim();
			
			if(!datasBu.containsKey(d_hostname)){
				datasBu.put(d_hostname, new String[matchList.length]);
				datasLogType.put(d_hostname,new String[matchList.length]);
			}
			
			String[] tt = datasBu.get(d_hostname);
			String[] cc = datasLogType.get(d_hostname);
			if( demandSize.containsKey(d_demand) ){
				tt[ demandSize.get(d_demand) ] = d_business ;
				cc[ demandSize.get(d_demand) ] = d_log_type ;
			}
			
			
		}
	
		loadConversion();
		
		} catch (Exception e) {
			System.out.println("加载文件异常");
			e.printStackTrace();
			System.exit(1);
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private void loadConversion() {
		for ( Entry<String, String[]> kv : datasBu.entrySet()) {
			String valueBu = "" ;
			String valueType = "" ;
			
			String[] values = kv.getValue();
			for (int i = 0; i < values.length; i++) {
				if( values[i] != null && !values[i].equals("") ){
					
					valueBu = values[i];
					valueType = datasLogType.get(kv.getKey())[i];
				}
			}
						
			hostBuType.put(kv.getKey(), new BuLogType(valueBu,valueType));
		}
	}
	
	public static void main(String[] args) {
		Hostname2BuLogByDB h = new Hostname2BuLogByDB("RDB,ALLBU");	
		System.out.println(h.get("CHN-DG-2-38G").toString());	
	}
}
