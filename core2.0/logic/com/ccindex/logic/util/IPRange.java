package com.ccindex.logic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.ccindex.util.parser.ip.IP2Long;

public class IPRange {

	private List<long[]> ipList = new ArrayList<long[]>();
	private long maxIpLong = Long.MIN_VALUE;
	private long minIpLong = Long.MAX_VALUE;

	public IPRange(String ipRangeFile) throws IOException {
		InputStream in = new FileInputStream(new File(ipRangeFile));
		// 读jar包根目录下的idcheck-file.properties文件</span><span>
		Reader f = new InputStreamReader(in);
		BufferedReader fb = new BufferedReader(f);
		String s = "";
		while ((s = fb.readLine()) != null) {
			if (s.isEmpty())
				continue;
			long tip = IP2Long.ipToLong(s.trim().split("/")[0]);
			long[] mi_mx = IP2Long.ipRangeMinMax(s.trim());

			minIpLong = mi_mx[0] > minIpLong ? minIpLong : mi_mx[0];
			maxIpLong = mi_mx[1] > maxIpLong ? mi_mx[1] : maxIpLong;

			long[] iip = IP2Long.ip2RangeLongSize(s.trim());
			ipList.add(iip);
		}
		in.close();
		f.close();
	}

	public Boolean isRange(Long ip) {
		
		if (ip > maxIpLong || ip < minIpLong)
			return false;

		if (ip != 0) {

			for (long[] ipRange : ipList) {
				if (IP2Long.ipRange(ip, ipRange[0], ipRange[1])) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		IPRange test =  new IPRange("D:\\ipRange.txt");
	}

}
