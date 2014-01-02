package com.ccindex.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @ClassName: dataBase
 * @Description: TODO(这里用一句话描述这个类的作用)连接数据库初始化动作
 * @author tianyu.yang
 * @date 2013-5-15 上午11:43:35 连接成功,更新本地文件;连接失败,调用本地数据
 */
public class DataBase {
	// 数据库,用户,密码
	private String database = null, user = null, pwd = null;
	private Connection conn = null;

	public DataBase(String database, String user, String pwd) {
		this.database = database;
		this.user = user;
		this.pwd = pwd;

		getConnectDba();
	}

	/**
	 * 
	 * @Title: getConnectDba
	 * @Description: TODO(这里用一句话描述这个方法的作用)连接数据库,获取连接句柄
	 * @return Connection
	 * @throws
	 */
	public Connection getConnectDba() {
		// 数据库连接
		if (conn == null) {
			try {
				// 装载驱动
				Class.forName("com.mysql.jdbc.Driver");
				String packageConn = "jdbc:mysql://" + database
						+ "?useUnicode=true&characterEncoding=utf8";
				// 2. 获取数据库的连接
				// "jdbc:mysql://115.236.185.27/ccindex?useUnicode=true&characterEncoding=utf8"
				// "dba", "dba"
				conn = java.sql.DriverManager.getConnection(packageConn, user,
						pwd);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return conn;
	}

	public void Close() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ObjectToFile(String fileName, Object values) {

		File file = new File(fileName);

		ObjectOutputStream outWriter = null;
		try {
			outWriter = new ObjectOutputStream(new FileOutputStream(file));
			outWriter.writeObject(values);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (outWriter != null)
					outWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Object FileToObject(String fileName) {
		File file = new File(fileName);
		ObjectInputStream inputReader = null;
		Object value = null;
		try {
			inputReader = new ObjectInputStream(new FileInputStream(file));
			value = inputReader.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inputReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return value;
	}

	public ResultSet QuarySql(String sql) {
		Statement stam = null;
		ResultSet result = null;
		try {
			if (conn == null) {
				return result;
			}
			stam = conn.createStatement();
			result = stam.executeQuery(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {
		DataBase data = new DataBase("1", "2", "3");
		data.getConnectDba();
	}
}
