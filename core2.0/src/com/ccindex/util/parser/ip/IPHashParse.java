package com.ccindex.util.parser.ip;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class IPHashParse {
	
	 
	private Map<Long, int[]> code2addr = new HashMap<Long, int[]>();
	private Map<String, Integer> codeHash = new HashMap<String, Integer>();
	
	public IPHashParse() {
		try {
			InputStream codeIs =  getClass().getResourceAsStream("/code.ip.20130502.data");
			BufferedReader codeFb = new BufferedReader(new InputStreamReader(codeIs));
			String sc = "";  
	        while((sc = codeFb.readLine()) != null) {  
	        	String[] arrs = sc.split("\t");
	        	codeHash.put(arrs[1], Integer.parseInt(arrs[0]));
	        }
	        codeFb.close();
	        codeIs.close();
	        
			InputStream is =  getClass().getResourceAsStream("/ip.20130502.data.gz");
			GZIPInputStream gis = new GZIPInputStream(is); 
			BufferedReader fb = new BufferedReader(new InputStreamReader(gis));
			
	        String s = "";  
	        while((s = fb.readLine()) != null) {  
	        	String[] arrs = s.split("\t");
	        	long start = Long.parseLong(arrs[0]);
	        	long end = Long.parseLong(arrs[1]);
	        	int codePro = codeHash.get(arrs[2]);
	        	int codeCity = codeHash.get(arrs[3]);
	        	int codeAdd = codeHash.get(arrs[4]);
	        	int codeIsp = codeHash.get(arrs[5]);
	        	
	        	for (long i = start; i <= end; i++) {
	        		//code2addr.put(i, new int[]{codePro,codeCity,codeAdd,codeIsp});
	        		code2addr.put(i, null);
	        		
	        		if( code2addr.size() % 1000000 == 0)
		        		System.out.println( code2addr.size() );
				}
	        }
	        fb.close();
	        is.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
    public static long ip2Long(String strIP){
        long[] ip=new long[4];
        //先找到IP地址字符串中.的位置
        int position1=strIP.indexOf(".");
        int position2=strIP.indexOf(".",position1+1);
        int position3=strIP.indexOf(".",position2+1);
        //将每个.之间的字符串转换成整型
        ip[0]=Long.parseLong(strIP.substring(0,position1));
        ip[1]=Long.parseLong(strIP.substring(position1+1,position2));
        ip[2]=Long.parseLong(strIP.substring(position2+1,position3));
        ip[3]=Long.parseLong(strIP.substring(position3+1));
        return (ip[0]<<24)+(ip[1]<<16)+(ip[2]<<8)+ip[3];
   }
   
   //将10进制整数形式转换成127.0.0.1形式的IP地址
   public static String long2IP(long longIP){
        StringBuffer sb=new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf(longIP>>>24));
        sb.append(".");        
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIP&0x00FFFFFF)>>>16));
        sb.append(".");
        sb.append(String.valueOf((longIP&0x0000FFFF)>>>8));
        sb.append(".");
        sb.append(String.valueOf(longIP&0x000000FF));
        return sb.toString();
   } 
   
   public long o2l(Object longstr){
	   return Long.parseLong(longstr.toString());
   }

	public String get(String ip){
		Long lip = ip2Long(ip) ;
		int[] dints = new int[]{-1,-1,-1,-1};
		
		if(code2addr.containsKey(lip)){
			dints = code2addr.get(lip);
		}
		String s = "";
		for (int i = 0; i < dints.length; i++) {
			s +=  dints[i]+"\t";
		}
		return s.substring(0,s.length()-1);
	}
}
