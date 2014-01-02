package com.ccindex.util.parser.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccindex.util.bytes.ByteUtils;

public class IP2Long {

	// 将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理
	public static long ipToLong(String strIp) {
		strIp = strIp.split(",")[0].trim();
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将十进制整数形式转换成127.0.0.1形式的ip地址
	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	
	
	
	/**
	 * 获得 网段( 101.38.0.0/15 ),并把ip格式化为 <br>
	 * 1. 101.38.0.0 右挪 ( 17位 = 32-15) => 二进制数: XX,XXX,XXX,XXX,XXX,XXX <br>
	 * 2. 在 ( 18位 = 32-15+1 )的位置上 => 生成一个 二进制 数 : 100,000,000,000,000,000 <br>
	 * 3. 合并 (1) 和 (2) => 1XX,XXX,XXX,XXX,XXX,XXX <br>
	 * <br>
	 * <strong>使用 ipRange(long,long) 方法匹配:</strong>
	 * 
	 * <pre>
	 *     	ipLong 
	 *         1. 位数用最左的 1 标示，   就是  (15 = 16 -1 )      
	 *         		上面的第二步 ：100,000,000,000,000,000 =>  15 
	 *     	   2. 剩下的   XX,XXX,XXX,XXX,XXX,XXX  左挪 至   32 位  
	 *               XX,XXX,XXX,XXX,XXX,XXX =>  101.38.0.0
	 * </pre>
	 * 
	 * <br>
	 * <br>
	 */
	public static long ip2RangeLong(String ipRangeStr) {
		String[] ips = ipRangeStr.split("/");
		int ysize = Integer.parseInt(ips[1]);
		return ipToLong(ips[0]) >> (32 - ysize)
				| (ByteUtils.getAllBitOne(ysize) + 1);
	}
	/**
	 * 使用 ipRange 方法匹配:
	 *    这种方法最后放弃 ， ByteUtils.getAllBitOne 性能不是很理想
	 *    类似请参考   ip2RangeLongSize,ipRange(long,long,long)
	 *    
	 * <pre>
	 *     	ipLong 
	 *         1. 位数用最左的 1 标示，   就是  (15 = 16 -1 )      
	 *         		上面的第二步 ：100,000,000,000,000,000 =>  15 
	 *     	   2. 剩下的   XX,XXX,XXX,XXX,XXX,XXX  左挪 至   32 位  
	 *               XX,XXX,XXX,XXX,XXX,XXX =>  101.38.0.0
	 * </pre>
	 */
	public static boolean ipRange(long ip, long iptRange) {
		int ysize = ByteUtils.getMaxOneSize(iptRange) - 1;
		long ipRange = iptRange & ByteUtils.getAllBitOne(ysize);
		long cc = (ip >> (32 - ysize)) ^ ipRange;
		return cc == 0;
	}
	
	
	
	/**
	 * 根据  网段( 101.38.0.0/15 )
	 * 		返回 ipRange Min,Max 
	 */
	public static long[] ipRangeMinMax(String ipRangeStr) {
		String[] ips = ipRangeStr.split("/");
		int ysize = Integer.parseInt(ips[1]);
		int size = (32 - ysize) ;
		return new long[]{(ipToLong(ips[0]) >> size) <<  size , ipToLong(ips[0])| (ByteUtils.getAllBitOne(size))} ;
	}

	
	
	
	/**
	 * 
	 * @param : 
	 * 	ipRangeStr 参数类似   60.253.128.0/17
	 * 
	 * <pre>
	 * 
	 * 结果返回
	 * 101.38.0.0/15 =>
	 *   XX,XXX,XXX,XXX,XXX,XXX
	 *   15
	 * </pre>
	 * 
	 * <strong>使用 ipRange(long,long,long) 方法匹配:</strong>
	 */
	public static long[] ip2RangeLongSize(String ipRangeStr) {
		String[] ips = ipRangeStr.split("/");
		int ysize = Integer.parseInt(ips[1]);
		return new long[] { 
				ipToLong(ips[0]) >> (32 - ysize),
				ysize
		};
	}
	/**
	 * 根据    起始IP，结束IP  
	 *    返回  IP返回
	 */
	public static long[] ip2RangeLongSize(String ipStratStr,String ipEndStr) {
		long sip = ipToLong(ipStratStr);
		long eip = ipToLong(ipEndStr);
		
		int min_one_size = ByteUtils.getMinOneSize(sip);
		int min_zero_size = ByteUtils.getMinZeroSize(eip);
		
		int size = Math.min(min_one_size, min_zero_size)-1;
		
		return new long[] {
				sip >> size,
				32-size
		};
	}
	
	public static void main(String[] args) {
		
		long[] aa = ipRangeMinMax("1.195.192.0/18");
		System.out.println(aa[0]);
		System.out.println(aa[1]);
		System.out.println(longToIP(29605888));
		System.out.println(longToIP(29622271));
		
		long[] ll = ip2RangeLongSize("1.195.192.0","1.195.255.255");
		System.out.println( longToIP( ll[0] << (32-ll[1])  ) +"/"+ll[1] );		
		
//		//转换IP段到IP掩码格式
//		File file= new File(args[0]);
//		FileInputStream is;
//		int count= 1;
//		try {
//			is = new FileInputStream(file);
//			BufferedReader in = new BufferedReader(new InputStreamReader(is));	
//			Pattern  pat = Pattern.compile(".*-.*");
//			String line;
//			try {
//				while((line = in.readLine()) != null){
//					Matcher math =  pat.matcher(line);
//					if(math.find()){
//						String arr[] = line.split("-");
//						long[] ll = ip2RangeLongSize(arr[0],arr[1]);
//						System.out.println( longToIP( ll[0] << (32-ll[1])  ) +"/"+ll[1] );
//						count++;				
//					}
//					
//				}
//				
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//
//				
//				e.printStackTrace();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("Error Num: " + count);
		
//		
//		long[] ll = ip2RangeLongSize("111.161.72.0","111.161.72.31");
//		System.out.println( longToIP( ll[0] << (32-ll[1])  ) +"/"+ll[1] );
	}
	/**
	 * 使用 ipRange 方法匹配:
	 * <pre>
	 * 		结合   ip2RangeLongSize 方法，下面意思很明了了
	 * </pre>
	 */
	public static boolean ipRange(long ip, long ipRange,long ysize) {
		long cc = (ip >> (32 - ysize)) ^ ipRange;
		return cc == 0;
	}
	
	//有问题
	public static String get32BinStr(long ip){
		String ss =  Long.toBinaryString( ip ) ;
		String cc = "";
		int i = 1;
		for (char c : ss.toCharArray()) {
			cc+=c;
			if( i++ %8==0 && i!= 33) cc+=",";
		}
		return cc ;
	}
	
}
