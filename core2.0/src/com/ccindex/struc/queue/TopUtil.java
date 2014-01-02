package com.ccindex.struc.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class TopUtil {


	/**
	 * 
	 * @Title: top 
	 * @Description: TODO(这里用一句话描述这个方法的作用)排名算法,内部使用插入排序,将新增数据进行排序 
	 * @param pt 起始位置,必须为-1,代表已经存在的数据个数
	 * @param count url对应的个数,即value值
	 * @param url url即key值
	 * @param counts count按大小存放的空间,传出空间
	 * @param urls url存储空间,顺序与conuts同步
	 * @return 当前已经排序的个数    
	 * 
	 * @throws
	 */
	public static int top(int pt,int count,String url,int[] counts ,String[] urls ){
		
		int size = counts.length;
		
		/* 
		 * pt 也需要保证 最大  : 199
		 * 下面计算的 pt ，当前位置 是 空，无值的 
		 */
		if( ++pt > (size-1) ) pt = (size-1) ;
		
		
		// 初始数组
		if(pt==0){
			
			if(  counts.length == 1 ){
				if( counts[pt] <= count ){
					urls[pt]=url;
					counts[pt]=count;
				} 
			}else{
				urls[pt]=url;
				counts[pt]=count;
			}
			
			
			
			
		}else{
			// 最后最小标识  - 匹配 优化 
			if( counts[pt] >= count ){
				// pass
			}else{
				// 正常 插入排序  流程 ，（并且 此次插入不是最小 最后一位 ）
				for (int i = 0; i < pt ; i++) {
					if( counts[i] < count ){
						int tcount = counts[i] ;
						String turl = urls[i] ;
						
						counts[i] = count ;
						urls[i] = url ;
						
						for (int j = i+1 ; j <= pt ; j++) {
							int ttcount = counts[j] ;
							String tturl = urls[j] ;
							
							counts[j] = tcount ;
							urls[j] = turl ;
							
							tcount = ttcount;
							turl = tturl ;
						}
						break;
					}
				}
			}
		}
		
		return pt;
	}
	
	public static void main(String[] args) {
		int size = 4 ,pt=-1 ;
		int[] counts = new int[size];
		String[] urls = new String[size];
		
		String base = "AABBBCDEFGHIJJJJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < 10000; i++) {   
	        int number = random.nextInt(base.length());
	        
	        String c = base.charAt(number)+"" ;
	        
	        if( !map.containsKey(c))map.put(c,0);
	        
	        map.put(c, map.get(c)+1);
	    }
		
		System.out.println(map.size());
		
		for(Entry<String, Integer> kv : map.entrySet()){
			pt = TopUtil.top(pt, kv.getValue(), kv.getKey() , counts, urls);
		}
		
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i]+"\t"+counts[i]);
		}
	}
}
