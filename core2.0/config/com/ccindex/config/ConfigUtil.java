package com.ccindex.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * 
* @ClassName: ConfigUtil 
* @Description: TODO(配置类) 
* @author  tianyu.yang
* @date 2012-12-13 下午3:26:35 
*
 */
public class ConfigUtil {
	
	private Map<String,Object> config ;

	/**
	* 
	* @Title: ConfigUtil.java 
	* @Description: 构造函数
	* @param in
	*/
	public ConfigUtil(InputStream in){
		Yaml yaml = new Yaml(new SafeConstructor());
		config = (Map<String,Object>) yaml.load(in);
	}
	
	/**
	 * 
	 * @Title: get 
	 * @Description: TODO(获取yaml组中某key值对应的value) 
	 * @param group 想要获取的组名
	 * @param key 对应组名下的key值
	 * @return Object   返回的对象 
	 * @throws
	 */
	public Object get(String group,String key){
		return ((Map)config.get(group)).get(key);
	}
	
	/**
	 * 
	 * @Title: getString 
	 * @Description: TODO(获取yaml组中某key值对应的value) 
	 * @param group 想要获取的组名
	 * @param key 对应组名下的key值
	 * @return    
	 * String 返回的字符串
	 * @throws
	 */
	public String getString(String group,String key){
		return ((Map)config.get(group)).get(key).toString();
	}
	
	/**
	 * 
	 * @Title: getGroup 
	 * @Description: TODO(获取组信息) 
	 * @param group 想要获取的组名
	 * @return    
	 * Map<String,Object> 
	 * @throws
	 */
	public Map<String,Object> getGroup(String group){
		return ((Map)config.get(group));
	}
	
	/**
	 * 
	 * @Title: set 
	 * @Description: TODO(设置某组的key,value值) 
	 * @param group
	 * @param key
	 * @param value    
	 * void
	 * @throws
	 */
	public void set(String group,String key,Object value){
		getGroup(group).put(key,value);
	}

	/**
	 * 
	 * @Title: setAll 
	 * @Description: TODO(通过参数设置yaml文件) 
	 * @param args    --（组名） -D
	 * void
	 * @throws
	 */
	public void setAll(String[] args) {
		String group = "default";
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].startsWith("--")) {
				group = args[i].substring(2) ;
			}else if (args[i].startsWith("-D")) {
				String[] vd = args[i].substring(2).split("=");
				set(group,vd[0],vd[1]);
			}
		}
	}
	
	/**
	 * 
	 * @Title: main 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 测试配置文件读入函数
	 * @param args
	 * @throws Exception    
	 * void
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream(new File("E:\\svn\\coding\\java_project\\etl\\resources\\etl.yaml"));
		ConfigUtil config = new ConfigUtil(is);
		System.out.println(  config.getString("etl","task.class.name") );
		config.set("etl", "task.class.name", "google");
		System.out.println(  config.getString("etl","task.class.name") );
	}
}
