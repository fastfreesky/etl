package com.ccindex.util.parser.ip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ccindex.config.util.CacheConfigUtil;

public class IpRangeUtil {
	/**
	 * <pre>
	 * 数据结构：
	 *	  主域[文件名称 = 省|运营商|等] : 
	 *       子域[ 省|默认值|运营商  ]  :  List [
	 *           数组第一个值 Long[2]，当前   【主域+子域】 中  IP_MIN ip 最小值, IP_MAX 最大值  
	 *       ]
	 * </pre> 
	 */
	public Map<String,Map<String,Map<String,ArrayList<Long[]>>>> ipData = new HashMap<String,Map<String,Map<String,ArrayList<Long[]>>>>();
	
	
	public void setData(List<Map<String, Object>> datas){
		
		for (Map<String, Object> map : datas) {
			String domain = map.get("domain").toString().trim();
			if(!ipData.containsKey(domain)){
				ipData.put( domain, new HashMap<String,Map<String,ArrayList<Long[]>>>() );
			}
			Map<String,Map<String,ArrayList<Long[]>>> ipRange = ipData.get(domain);
			
			String key = map.get("key_code").toString().trim();
			String ipRangeStr = map.get("ipRange").toString().trim();
			
			int index = ipRangeStr.indexOf("/");
			long start = Long.MAX_VALUE , end= Long.MIN_VALUE ;
			
			if (index > -1) {
				// 根据掩码返回起始ip和结束ip
				long[] ipR = IP2Long.ipRangeMinMax(ipRangeStr);
				start = ipR[0];
				end = ipR[1];
			}else{
				if( ipRangeStr.split("\\.").length == 4 ){
					start = Long.parseLong(ipRangeStr);
					end = Long.parseLong(ipRangeStr);
				}
			}
			
			if(start > end )  continue;
			
			
			String ip_key = ipRangeStr.split("\\.")[0];
			
			if(!ipRange.containsKey(key)){
				ipRange.put(key, new HashMap<String,ArrayList<Long[]>>());
				//ipRange.get(key).add( new Long[]{Long.MAX_VALUE,Long.MIN_VALUE} );
			}
			if(!ipRange.get(key).containsKey(ip_key) ){
				ipRange.get(key).put(ip_key, new ArrayList<Long[]>());
				ipRange.get(key).get(ip_key).add( new Long[]{Long.MAX_VALUE,Long.MIN_VALUE} );
			}
			
			insertSort( ipRange.get(key).get(ip_key) , new Long[]{start,end});
		}
	}
	
	
	/**
	 * 
	 * @Title: insertSort 
	 * @Description: TODO(这里用一句话描述这个方法的作用)  从小到大 ！
	 * @param list
	 * @param start_end
	 * void
	 * @throws
	 */
	public void insertSort(List<Long[]> list,Long[] start_end){
		int size = list.size();
		Long[] min_max = list.get(0);
		
		if( min_max[0] > start_end[0] ) min_max[0] = start_end[0] ;
		if( min_max[1] < start_end[1] ) min_max[1] = start_end[1] ;
		
		if( size == 1 ){
			list.add(start_end);
			return ;
		}
		
		for (int i = 1; i < size ; i++) {
			Long[] tse = list.get(i);
			
			if( tse[0] >  start_end[0] ){
				list.add( i, start_end );
				return ;
			}
			
		}
		
		list.add( start_end );
	}
	
	
	
	public int findIp(List<Long[]> datas,long sip, int st, int et) {
		
		
		if(st==et || et-st==1){
			Long[] rip = datas.get(st);
			
			if( rip[0] <= sip  && sip  <= rip[1] ) 
				return st;
			
			rip = datas.get(et);
			if( rip[0] <= sip  && sip  <= rip[1] ) 
				return et;
			
			return -1 ;
		}
			
		
		if (st > datas.size())
			return -1;
		if (et > datas.size())
			return -1;

		
		int mm = (st + et) / 2;
		Long[] rip = datas.get(mm);
		
		if( sip == rip[0] ) return mm;
		if( sip == rip[1] ) return mm;
		
		if (sip < rip[0]){
			return findIp(datas,sip,st, mm);
		}
		
		if (sip > rip[0]){
			if( sip < rip[1] ) return mm;
			return findIp(datas,sip, mm, et);
		}
		return -1;
	}
	
	
	/**
	 * @Title: evaluate 
	 * @Description:  
	 * <pre>
	 *    use UDF
	 *    add jar hdfs:///user/hive/udf/hive_udf.jar;
     *    create temporary function CrcIpRangeUpgrade as 'ccindex.hive.udf.CrcIpRangeUpgrade';
     *
     *    #  铁通
     *    select CrcIpRangeUpgrade('http://url',ip), ip from fc_rdb_seq;
     *    
	 * </pre> 
	 * @param type
	 * @param str
	 * @return    
	 * String
	 * @throws
	 */
	public String evaluate(String domain, String str) {
		
		long ip = 0;
		try {
			ip = IP2Long.ipToLong(str.trim());
		} catch (Exception e) {
			return "-1";
		}
		
		
		 
		if( ! ipData.containsKey(domain) ) return "-1" ;
		
		
		String key_ip = str.split("\\.")[0];
		
		
		// 如果IP不在最大最小范围内,直接返回No
		for ( Entry<String,Map<String,ArrayList<Long[]>>> ky : ipData.get(domain).entrySet()) {
			Map<String,ArrayList<Long[]>> tdatas = ky.getValue();
			
			if( !tdatas.containsKey(key_ip) ) continue; 
			
			ArrayList<Long[]> datas = tdatas.get(key_ip);
			
			Long[] min_max = datas.get(0);
			
			
			if ( ip>=min_max[0] && ip<=min_max[1]) {
			//在索引范围内
				int size = findIp(datas, ip, 1, datas.size()-1);
				if(size!=-1) 
					return ky.getKey();
			}
		}
		
		return "-1";
	}
	
	
	public static void main(String[] args) throws Exception {
		List<Map<String, Object>> oo = CacheConfigUtil
					.cache("jdbc:mysql://223.203.192.248/rule?useUnicode=true&characterEncoding=utf8&user=dba&password=dba",
							"D:\\test\\test\\131534\\1.txt",
							"select domain,key_code,ipRange from rule_ip_range ",
							"domain", "key_code","ipRange");
		IpRangeUtil ipu = new IpRangeUtil();
		ipu.setData(oo);
		
		System.out.println( ipu.evaluate("heNan_city_18", "1.198.156.1") );
	}
	
	
	
	
}
