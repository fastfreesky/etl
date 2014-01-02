package com.ccindex.util.url;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccindex.config.util.CacheConfigUtil;

/**
 * 此类构造方法需要查询数据库，为减小数据库压力，使用单例模式。
 * 用法：传入频道url，返回与之对应的task。
 * 		 传入泛域名，返回与泛域名对应的task数组。
 * @ClassName: ChannelTask 
 * @Description: TODO(这里用一句话描述这个类的作用) 分析apple频道和task的关系
 * @author  xinchao.wang
 * @date 2013-7-23 下午4:02:46 
 *
 */
public class ChannelTask {

	private static ChannelTask ct;
	private Map<String,Object> map = new HashMap<String,Object>();
	
	static{
		if(ct == null){
			ct = new ChannelTask();
		}
	}
	
	/**
	 * 
	 * @Title: getInstance 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取ChannelTask实例
	 * @return    
	 * ChannelTask
	 * @throws
	 */
	public static ChannelTask getInstance(){
		return ct;
	}
	
	/**
	 * 私有构造方法
	 * @Title: ChannelTask.java 
	 * @Description:
	 */
	private ChannelTask(){
		List<List<String>> list = null;
		try {
			list = query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(List<String> keys : list){
			deep(keys, 0, map);
		}
	}
	
	/**
	 * 传入域名，得到对应的task数组，去重后，返回
	 * @Title: getTasks 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 传入域名，得到对应的task数组
	 * @return    
	 * String[]
	 * @throws
	 */
	public String[] getTasks(String url){
		//中间数组
		String[] temp = url.split("(://|\\.)");
		String[] channel = new String[temp.length+1];
		//将切割出的url数组排列成需要的顺序
		channel[0]=temp[0];
		int index = 1;
		for(int i = temp.length-1; i>0; i--){
			channel[index] = temp[i];
			index++;
		}
		channel[channel.length-1] = "final";
		
		//去重用的map
		Map<String,Integer> result = new HashMap<String,Integer>();
		this.deepValue(channel, 0, map, result);
		
		//装载成String数组
		Set<String> resultSet = result.keySet();
		String[] resultArr = new String[resultSet.size()];
		
		return resultSet.toArray(resultArr);
	}
	
	/**
	 * 
	 * @Title: deepValue 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 递归取值
	 * @param key
	 * @param map
	 * @param result    
	 * void
	 * @throws
	 */
	private void deepValue(String[] key, int index, Map<String,Object> map, Map<String,Integer> result){
		if(index < key.length){
			Object o = map.get(key[index]);
			if ( o != null ){
				if (o.getClass().isAssignableFrom(HashMap.class)) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> child = (HashMap<String, Object>) o;
					this.deepValue(key, index+1, child, result);
				}else{
					String[] split = o.toString().split(",");
					for(String s : split){
						result.put(s, 1);
					}
				}
			} else {
				o = map.get("*");
				if( o != null){
					if (o.getClass().isAssignableFrom(HashMap.class)) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> child = (HashMap<String, Object>) o;
						String[] split = child.get("final").toString().split(",");
						for(String s : split){
							result.put(s, 1);
						}
					}else{
						String[] split = o.toString().split(",");
						for(String s : split){
							result.put(s, 1);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 递归方法，将从数据库中查询得到的数据按照反域名形式装载到Map字典中，以备使用。
	 * @Title: deep 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 递归装载数据
	 * @param keys
	 * @param index
	 * @param map
	 * @return    
	 * Map<String,Object>
	 * @throws
	 */
	private void deep(List<String> keys, int index, Map<String,Object> map){
		if(keys.size() > index+2){
			//如果index所指向的不是list中的最后一个元素，即非task字段
			Object o = map.get(keys.get(index));
			if(o != null){
				if(o.getClass().isAssignableFrom(HashMap.class)){
					//判断o是否为HashMap的实例
					@SuppressWarnings("unchecked")
					Map<String,Object> child = (HashMap<String,Object>)o;
					//递归
					this.deep(keys,index+1,child);
				}
			}else{
				//如果map中没有list中第index个元素为键的所对应的值,递归新增
				HashMap<String,Object> child = new HashMap<String,Object>();
				map.put(keys.get(index), child);
				this.deep(keys, index+1, child);
			}
		}else{
			//如果index所指向的是list中的最后一个元素，即task字段
			Object o = map.get(keys.get(index));
			if (o != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> child = (HashMap<String, Object>) o;
				Object oo = child.get("final");
				if(oo != null){
					child.put("final", keys.get(index+1) + "," + oo.toString());
				}else{
					child.put("final", keys.get(index+1));
				}
				map.put(keys.get(index), child);
			} else {
				HashMap<String,Object> child = new HashMap<String,Object>();
				child.put("final", keys.get(index+1));
				map.put(keys.get(index), child);
			}
		}
	}
	
	/**
	 * 查询channel_task表，将结果集加工成需要的形式。
	 * @Title: query 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 查询channel_task表
	 * @return
	 * @throws Exception
	 * @throws ClassNotFoundException    
	 * List<List<String>>
	 * @throws
	 */
	private List<List<String>> query() throws Exception{
		
		//存储结果
		List<List<String>> result = new ArrayList<List<String>>();
		
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		List<Map<String, Object>> oo = CacheConfigUtil
				.cache("jdbc:mysql://223.203.192.248:3306/rule?useUnicode=true&characterEncoding=utf8&user=dba&password=dba",
						"/tmp/channel_task/" + date + "/channel_task.txt",
						"select channel,task from task_channel ",
						"channel", "task");
		//循环缓存结果，形成需要的格式
		for(Map<String,Object> map : oo){
			String[] channel = map.get("channel").toString().split("(://|\\.)");
			List<String> element = new ArrayList<String>();
			//第一个元素为channel的协议
			element.add(channel[0]);
			//倒着循环
			for(int i = channel.length-1; i>0; i--){
				element.add(channel[i]);
			}
			//最后一个元素为task
			element.add(map.get("task").toString());
			result.add(element);
		}
		return result;
	}
	
	/**
	 * 使用示例
	 * @Title: main 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 测试入口
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * void
	 * @throws
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		ChannelTask ct = ChannelTask.getInstance();
		String[] tasks = ct.getTasks("http://a1.sohu.com");
		System.out.println(Arrays.toString(tasks));
	}
}
