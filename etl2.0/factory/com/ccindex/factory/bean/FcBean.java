package com.ccindex.factory.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccindex.constant.Regular;
import com.ccindex.mInterface.Bean;
import com.ccindex.struc.queue.FileTypeGather;
import com.ccindex.util.cache.Cache;
import com.ccindex.util.parser.ip.IPParse;
import com.ccindex.util.time.TimeUtil;

/**
 * 
 * @ClassName: FcBean
 * @Description: TODO(这里用一句话描述这个类的作用)非线程安全类
 * @author tianyu.yang
 * @date 2013-1-10 下午3:48:48
 * 
 */
public class FcBean implements Bean {

	public FcBean() {
		init();
	}

	@Override
	public FcBean clone() {
		return new FcBean();
	}

	private Pattern rexRow = Pattern.compile(Regular.FCBEAN_REXROW);
	private Pattern rexFilename = Pattern.compile(Regular.FCBEAN_REXFILENAME);
	// private static Cache cache;
	// private static IPParse ipParser = new IPParse();
	// private static TimeUtil timeUtil = new TimeUtil();

	private IPParse ipParser = new IPParse();
	private TimeUtil timeUtil = new TimeUtil();

	private String formatStr;

	private void init() {
		formatStr = "";
		time = "";
		ttime = "";
		day = "";
		code = "";
		url = "";

		// province ; city ; isp ;
		ip = "";
		province = "";
		city = "";
		isp = "";

		speed = 0.0f;
		bytes = 0.0f;
		elapsed = 0.0f;

		host = "";
		ua = "";
		source = "";
		mime = "";
		refer = "";
	}

	// 时间戳
	private String ttime;
	// 时间 yyyy-MM-dd HH:mm:ss
	private String time;
	// 时间 yyyyMMdd
	private String day;

	public String getTTime() {
		return ttime;
	}

	public String getTime() {
		// try {
		// //time="1900 12:12:12";//耗时地方
		// time = cache.get(ttime, timeUtil, "getTime", ttime);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return time;
	}

	public String getDay() {
		day = time.split(" ")[0];
		day = day.replace("-", "");
		return day;
	}

	private String code;

	public String getCode() {
		return code;
	}

	private String url;

	public String getUrl() {
		return url;
	}

	private String ip;
	private long ipLong;

	public String getIp() {
		return ip;
	}

	public Long getIpLong() {
		return ipLong;
	}

	private String province;

	public String getProvince() {
		return province;
	}

	private String city;

	public String getCity() {
		return city;
	}

	private String isp;

	public String getIsp() {
		return isp;
	}

	private float speed;

	public float getSpeed() {
		return speed;
	}

	private float bytes;

	public float getBytes() {
		return bytes;
	}

	private float elapsed;

	public float getElapsed() {
		return elapsed;
	}

	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private String ua;

	public String getUa() {
		return ua;
	}

	private String source;

	public String getSource() {
		return source;
	}

	private String mime;

	public String getMime() {
		return mime;
	}

	private String refer;

	public String getRefer() {
		return refer;
	}

	private Pattern rexChannel = Pattern.compile(Regular.FCBEAN_REXCHANNEL);

	public String getReferChannel() {
		Matcher mreferchannel = rexChannel.matcher(refer);
		if (mreferchannel.find())
			return mreferchannel.group(1);
		return "";
	}

	public String getVersion() {
		return "1.0";
	}

	public boolean filling(String task, Object values) {

		String data = (String) values;
		Matcher ma = rexRow.matcher(data);
		if (ma.find()) {
			init();
			
			Matcher mf = rexFilename.matcher(task);
			if (mf.find())
				host = mf.group(1);

			try {
				// 时间
				ttime = ma.group(1);
				ttime += "000";

				// 转换时间到固定格式
				time = timeUtil.getTime(ttime);

				// code
				code = ma.group(4);

				// url
				url = ma.group(6);
				int end = url.indexOf("?");
				if (end > 0)
					url = url.substring(0, end);

				// ip
				ip = ma.group(3);
				ipLong = IPParse.ip2Long(ip);
				if (ipLong == -1) {
					return false;
				}
				// String ip_local = cache.get(ip, ipParser, "get", ipLong);
				String ip_local = ipParser.get(ipLong);
				String[] locals = ip_local.split("\t");
				// 耗时地方
				// String[] locals={"1", "2", "3", "4"};
				if (locals.length != 4) {
					// province
					province = "no";
					// city
					city = "no";
					// isp
					isp = "no";
				} else {
					// province
					province = locals[0];
					// city
					city = locals[1];
					// isp
					isp = locals[3];

				}

				// System.out.println("ip_Source:"+ip+"\t"+"ip_Long:"+ipLong+"\t"+"ipString:"+locals[0]+"\t"+locals[1]+"\t"+locals[2]);
				try {
					// bytes
					bytes = Float.parseFloat(ma.group(5));
					// elapsed
					elapsed = Float.parseFloat(ma.group(2));
				} catch (Exception e) {
				}
				// speed
				if (elapsed == 0f)
					speed = 0f;
				else
					speed = bytes / elapsed;

				// ua
				ua = ma.group(10);

				// source
				source = ma.group(7);

				// mime
				mime = ma.group(8);

				// refer
				refer = ma.group(9);

				runFormat();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	private void runFormat() throws Exception {
		StringBuffer sb = new StringBuffer();

		sb.append(getTime()).append("\t");

		sb.append(getCode()).append("\t");

		String url = getUrl();
		sb.append(url).append("\t");

		String ip = getIp();

		sb.append(getProvince()).append("\t");
		// city
		sb.append(getCity()).append("\t");
		// isp
		sb.append(getIsp()).append("\t");

		// ip
		sb.append(ip).append("\t");

		// speed
		float bytes = getBytes();
		float elapsed = getElapsed();

		if (elapsed == 0f)
			sb.append(0f).append("\t");
		else
			sb.append(bytes / elapsed).append("\t");

		// elapsed
		sb.append(elapsed).append("\t");

		// bytes
		sb.append(bytes).append("\t");

		// host
		sb.append(getHost()).append("\t");

		// ua
		sb.append(getUa()).append("\t");

		// source
		sb.append(getSource()).append("\t");

		// mime
		sb.append(getMime()).append("\t");

		// refer
		String refer = getRefer();
		Matcher mreferchannel = rexChannel.matcher(refer);
		String refer_channel = refer;
		if (mreferchannel.find())
			refer_channel = mreferchannel.group(1);
		sb.append(refer_channel).append("\t");

		// version
		sb.append("1.0").append("\n");

		formatStr = sb.toString();
	}

	public String format() {
		return formatStr;
	}

	public static void main(String[] args) {

		String aaa = "1369842700.947      0 65.255.44.15 TCP_MEM_HIT/200 2718 GET http://comment.news.sohu.com/upload/comment4_1/images/upload_img.gif  - NONE/- image/gif \"http://news.sohu.com/20130529/n377445619.shtml\" \"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1\" vjuids=-a84c48624.13e785440af.0.62b18e56;%20gn12=w:1;%20gj12=w:1;%20shenhui12=w:1;%20pgv_pvi=8812753920;%20bbs_guide_thread=1;%20jkang12=w:1;%20bbs_guide=1;%20T_U=464702444;%20lyou12=w:1;%20bin12=w:1;%20wmen12=w:1;%20ent12=w:1;%20__utma=1.216117276.1368202659.1368761735.1369145919.4;%20__utmv=1.weibo328294020%40sohu.com;%20ppnewsinfo=1019%7Cd2VpYm8zMjgyOTQwMjBAc29odS5jb20=%7C%7Chttp://img3.pp.sohu.com/ppp/blog/images/common/nobody.gif;%20networkmp_del=check:1;%20lastdomain=1370789798%7Cd2VpYm8zMjgyOTQwMjBAc29odS5jb218%7Csohu.com;%20_ga=1.2.216117276.1368202659;%20COOKIEMAPPING1=suv:1305061330415406;%20gj11=w:1;%20weibo_visited=1;%20lyou11=w:1;%20scrnSize=1280*1024;%20beans_frequency=%7B%2220q0Y0q1A0000000q9e000000%22%3A2%2C%2220q120q1z0000000q9r000000%22%3A2%2C%2220q0Y0q1A0000000q9f000000%22%3A1%2C%2220q120q1z0000000q9t000000%22%3A3%7D;%20wmen11=w:1;%20notLoginCard=1;%20IPLOC=CN3101;%20SUV=1305061330415406;%20sohutag=8HsmeSc5NTEsJ3NmOiNsJ2ImOiYsJ2EmOiAsJ2YmOiAsJ2cmOiAsJ24mOig4LCdqJzowLCd3JzoxMzcsJ2gmOiNsJ2NmOiAsJ2UmOiEsJ20mOik4LCd0JzoxfQ;%20viewed=370201522,367710551,365909465,360674062,375609859,377364884,377379960,377395482,377373662,377387093,377365023,355764021,377007611,355715017,370749952,368216869,347157072,371550848,368045878,341846211;%20beans_frequency_all=%7B%2220q120q1z0000000q9t000qW7_10270%22%3A1%2C%2220q13000z0000000q9v000qWM_10428%22%3A1%2C%22undefined_10211%22%3A1%2C%2220q13000z0000000q92000qME_10296%22%3A2%2C%2220q13000z0000000q9w000qWU_10571%22%3A5%2C%2220q13000z0000000q9w000qWR_10055%22%3A3%2C%2220q13000z0000000q9v000qWM_10220%22%3A1%2C%2220q13000z0000000q91000qMu_10316%22%3A1%2C%2220q13000z0000000q91000qMu_10317%22%3A1%2C%22undefined_10222%22%3A1%7D;%20vjlast=1369752861.1369798451.10;%20weibo_msg_version=201305291930;%20client_ip=10.11.16.40";
		FcBean bean = new FcBean();
		// bean.filling(aaa);

		String url = "http://kad.www.wps.cn/wps/cdnwps/service/app/bubble/scripts/player.js????";
		int end = url.indexOf("?");
		if (end > 0)
			url = url.substring(0, end);

		System.out.println(url);
	}
}
