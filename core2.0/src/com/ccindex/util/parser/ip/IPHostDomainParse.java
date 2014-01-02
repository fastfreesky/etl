package com.ccindex.util.parser.ip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IPHostDomainParse {

	private Map<String, Map<String, ArrayList<Long[]>>> ipRange = new HashMap<String, Map<String, ArrayList<Long[]>>>();
	private Map<String, List<String>> hostnameMath = new HashMap<String, List<String>>();

	/**
	 * @Title: getData
	 * @Description: 把isp的ip段和省放入map中,同时出现ip的最大值和最小值
	 * @param path
	 * @return Map<Long[],String>
	 */
	public IPHostDomainParse(InputStream input) {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String line = null;
			line = in.readLine();
			// 按行读取
			while (line != null) {
				/*
				 * ip数据格式 1 : ip_start \t ip_end \t [ 默认值|省份|运营商等 ] ip数据格式 2 :
				 * ip_range\掩码 \t [ 默认值|省份|运营商等 ]
				 */
				String[] key_value = line.split("\t");
				int index = key_value[0].indexOf("/");

				String key = key_value[1];
				if (key_value.length == 3) {
					hostnameMath.put(key,
							Arrays.asList(key_value[2].split(",")));
				}
				long start, end;

				if (index > -1) {
					// 根据掩码返回起始ip和结束ip
					long[] ipR = IP2Long.ipRangeMinMax(key_value[0]);
					start = ipR[0];
					end = ipR[1];
				} else {
					// 直接返回起始ip和结束ip
					start = IP2Long.ipToLong(key_value[0]);
					end = IP2Long.ipToLong(key_value[1]);
				}

				if (start > end)
					continue;

				String ip_key = key_value[0].split("\\.")[0];

				if (!ipRange.containsKey(key)) {
					ipRange.put(key, new HashMap<String, ArrayList<Long[]>>());
					// ipRange.get(key).add( new
					// Long[]{Long.MAX_VALUE,Long.MIN_VALUE} );
				}
				if (!ipRange.get(key).containsKey(ip_key)) {
					ipRange.get(key).put(ip_key, new ArrayList<Long[]>());
					ipRange.get(key).get(ip_key)
							.add(new Long[] { Long.MAX_VALUE, Long.MIN_VALUE });
				}

				insertSort(ipRange.get(key).get(ip_key), new Long[] { start,
						end });
				line = in.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (Exception e2) {
			}

		}
	}

	/**
	 * 
	 * @Title: insertSort
	 * @Description: TODO(这里用一句话描述这个方法的作用) 从小到大 ！
	 * @param list
	 * @param start_end
	 *            void
	 * @throws
	 */
	private void insertSort(List<Long[]> list, Long[] start_end) {
		int size = list.size();
		Long[] min_max = list.get(0);

		if (min_max[0] > start_end[0])
			min_max[0] = start_end[0];
		if (min_max[1] < start_end[1])
			min_max[1] = start_end[1];

		if (size == 1) {
			list.add(start_end);
			return;
		}

		for (int i = 1; i < size; i++) {
			Long[] tse = list.get(i);

			if (tse[0] > start_end[0]) {
				list.add(i, start_end);
				return;
			}

		}

		list.add(start_end);
	}

	private int findIp(List<Long[]> datas, long sip, int st, int et) {

		if (st == et || et - st == 1) {
			Long[] rip = datas.get(st);

			if (rip[0] <= sip && sip <= rip[1])
				return st;

			rip = datas.get(et);
			if (rip[0] <= sip && sip <= rip[1])
				return et;

			return -1;
		}

		if (st > datas.size())
			return -1;
		if (et > datas.size())
			return -1;

		int mm = (st + et) / 2;
		Long[] rip = datas.get(mm);

		if (sip == rip[0])
			return mm;
		if (sip == rip[1])
			return mm;

		if (sip < rip[0]) {
			return findIp(datas, sip, st, mm);
		}

		if (sip > rip[0]) {
			if (sip < rip[1])
				return mm;
			return findIp(datas, sip, mm, et);
		}
		return -1;
	}

	public List<String> parse(String ipStr, String host) {
		long ip = IP2Long.ipToLong(ipStr);
		return parse(ip, host);
	}

	/**
	 * 
	 * @Title: parse
	 * @Description: TODO(这里用一句话描述这个方法的作用) 根据 运营商IP,与匹配hostname名。判断是否为 某运营商网内外
	 *               <addr_isp,网内/外>
	 * @param ip
	 *            # 日志当前 IP
	 * @param hostname
	 *            # 日志当前 设备名称
	 * @param data
	 *            # 去匹配 hostnames
	 * @return # name 0|1 => 0网内;1网外 String
	 * @throws
	 */
	public List<String> parse(long ip, String host) {
		List<String> rstr = new ArrayList<String>();

		String key_ip = (ip >> 24) + "";
		// 如果IP不在最大最小范围内,直接返回No
		for (Entry<String, Map<String, ArrayList<Long[]>>> ky : ipRange
				.entrySet()) {
			Map<String, ArrayList<Long[]>> val_datas = ky.getValue();
			String key = ky.getKey();

			if (!val_datas.containsKey(key_ip))
				continue;

			ArrayList<Long[]> datas = val_datas.get(key_ip);

			Long[] min_max = datas.get(0);

			if (ip >= min_max[0] && ip <= min_max[1]) {
				// 在索引范围内
				int size = findIp(datas, ip, 1, datas.size() - 1);
				if (size != -1) {

					String rkey = "";
					if (hostnameMath.containsKey(key)) {

						for (String hs : hostnameMath.get(key)) {
							if (host.indexOf(hs) > -1) {
								rkey = "\t0"; // 在网内
								break;
							} else {
								rkey = "\t1"; // 不在网内
							}
						}

					} else {
						rkey = "\t2"; // 无法判断网内外
					}

					rstr.add(key + rkey);
				}
			}
		}

		return rstr;
	}

	public static void main(String[] args) throws Exception {
		IPHostDomainParse ipd = new IPHostDomainParse(new FileInputStream(
				new File("C:\\Users\\tianyu.yang\\Desktop\\20130604.out")));
		// System.out.println( ipd.parse( "202.106.101.203","GWB-BJ-1-1" ) );
	}
}
