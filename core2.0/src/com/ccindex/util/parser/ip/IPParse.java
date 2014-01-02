package com.ccindex.util.parser.ip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @ClassName: IPParse
 * @Description: TODO(这里用一句话描述这个类的作用)Ip解析算法
 * @author tianyu.yang
 * @date 2013-1-14 下午2:33:43
 * 
 */
public class IPParse {

	private Object[][] datas;

	// private Map<String, Integer> codeHash = new HashMap<String, Integer>();

	public IPParse() {
		// System.out.println("Init IPParse-----------------------");
		Map<String, Integer> codeHash = new HashMap<String, Integer>();
		Map<Long, String[]> code2addr = new TreeMap<Long, String[]>();
		try {
			InputStream codeIs = getClass().getResourceAsStream(
					"/code.ip.20130502.data");
			BufferedReader codeFb = new BufferedReader(new InputStreamReader(
					codeIs, "UTF-8"));
			String sc = "";
			while ((sc = codeFb.readLine()) != null) {
				String[] arrs = sc.split("\t");
				codeHash.put(arrs[1], Integer.parseInt(arrs[0]));
				// System.out.println(arrs[1]+"\t"+ Integer.parseInt(arrs[0]));
			}
			codeFb.close();
			codeIs.close();

			// InputStream is =
			// getClass().getResourceAsStream("/ip_address.csv.gz");
			InputStream is = getClass().getResourceAsStream(
					"/ip.20130502.data.gz");
			GZIPInputStream gis = new GZIPInputStream(is);
			BufferedReader fb = new BufferedReader(new InputStreamReader(gis,
					"UTF-8"));

			String s = "";
			while ((s = fb.readLine()) != null) {
				// String[] arrs = s.split(",");
				String[] arrs = s.split("\t");
				if (arrs.length == 6)
					code2addr.put(new Long(arrs[1]), arrs);
			}
			fb.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		datas = new Object[code2addr.size()][6];

		int ii = -1;
		for (Map.Entry<Long, String[]> entry : code2addr.entrySet()) {
			ii++;
			String[] rows = entry.getValue();
			datas[ii][0] = Long.parseLong(rows[0]);
			datas[ii][1] = Long.parseLong(rows[1]);
			datas[ii][2] = codeHash.get(rows[2]);
			datas[ii][3] = codeHash.get(rows[3]);
			datas[ii][4] = codeHash.get(rows[4]);
			datas[ii][5] = codeHash.get(rows[5]);
		}
		code2addr.clear();
		code2addr = null;
		// for(Object[] data:datas){
		// System.out.println(data[0]+"\t"+data[1]+"\t"+data[2]+"\t"+data[3]+"\t"+data[4]);
		// }
	}

	public static long ip2Long(String strIP) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIP.indexOf(".");
		int position2 = strIP.indexOf(".", position1 + 1);
		int position3 = strIP.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		try {

			ip[0] = Long.parseLong(strIP.substring(0, position1));
			ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
			ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
			ip[3] = Long.parseLong(strIP.substring(position3 + 1));
		} catch (NumberFormatException e) {
			return -1;
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将10进制整数形式转换成127.0.0.1形式的IP地址
	public static String long2IP(long longIP) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	public long o2l(Object longstr) {
		return Long.parseLong(longstr.toString());
	}

	/**
	 * 
	 * @Title: findIp
	 * @Description: TODO(这里用一句话描述这个方法的作用)二分法查找IP
	 * @param sip
	 * @param st
	 * @param et
	 * @return int
	 * @throws
	 */
	public int findIp(long sip, int st, int et) {

		if (st > et)
			return -1;
		if (st > datas.length)
			return -1;
		if (et > datas.length)
			return -1;

		if (et - st <= 1)
			return st;

		int mm = (st + et) / 2;
		Object[] rip = datas[mm];

		if (sip >= o2l(rip[0]))
			return findIp(sip, mm, et);

		if (sip < o2l(rip[0]))
			return findIp(sip, st, mm);

		return -1;
	}

	public Object[] getIp(Long sip) {
		// long sip = ip2Long(ip);
		int num = findIp(sip, 0, datas.length - 1);

		for (int step = num, ii = 0; step >= 0 && ii < 15; step--, ii++) {
			Object[] rip = datas[step];

			if (o2l(rip[0]) == sip)
				return rip;

			if (sip <= o2l(rip[1]))
				return rip;
			if (sip > o2l(rip[1]))
				continue;
		}

		return new Object[] { -1L, -1L, "", "", "", "" };
	}

	public String get(Long ip) {
		Object[] objs = getIp(ip);
		StringBuffer sb = new StringBuffer();
		for (int i = 2; i < objs.length - 1; i++) {
			sb.append(objs[i]).append("\t");
		}
		sb.append(objs[5]);
		return sb.toString();
	}

	public static void main(String[] args) {
		IPParse ip = new IPParse();

		File file =new  File("C:\\Users\\tianyu.yang\\Desktop\\ip.all");
		File fileo =new  File("C:\\Users\\tianyu.yang\\Desktop\\ip.all.out");

		try {
			BufferedReader read = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileo, false));
			String line;
			while ((line = read.readLine())!= null){
				long idp = IPParse.ip2Long(line);
				String sss;
				if (idp != -1){
					String msg[] = ip.get(idp).split("\t");
					if (msg.length != 4){
						sss = line+"\t"+ "no" +"\t"+ "no"+"\n";						
					}else{
						sss = line+"\t"+ msg[0] +"\t"+ msg[3]+"\n";						
					}

				}else{
					sss = line+"\t"+ "no" +"\t"+ "no"+"\n";						
				}
				
				writer.write(sss);
			}
			
			read.close();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		long idp = IPParse.ip2Long("11111");
//		if (idp != -1){
//			System.out.println(ip.get(idp));			
//		}

		// System.out.println(ip.get( IP2Long.ipToLong("101.158.0.0") ));
		//
		// while (true) {
		// Scanner in = new Scanner(System.in);
		// String readLine = in.nextLine(); // 读取键盘输入的一行（以回车换行为结束输入）
		// System.out.println("src:"+readLine+"\t"+ip.get(IP2Long.ipToLong(readLine)));
		// }
	}

}
