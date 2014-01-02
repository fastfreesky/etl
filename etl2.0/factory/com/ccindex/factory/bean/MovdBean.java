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
 * @ClassName: FlvBean
 * @Description: TODO(这里用一句话描述这个类的作用)FLV日志清洗模块Bean文件
 * @author tianyu.yang
 * @date 2012-12-27 上午10:20:30
 * 
 */
public class MovdBean implements Bean {

	public MovdBean() {
		init();
	}

	@Override
	public MovdBean clone() {
		return new MovdBean();
	}

	private Pattern rexRow = Pattern.compile(Regular.FLVMOVDBEAN_REXROW);
	private Pattern rexFilename = Pattern
			.compile(Regular.FLVMOVDBEAN_REXFILENAME);
	private IPParse ipParser = new IPParse();
	private TimeUtil timeUtil = new TimeUtil();

	/**
	 * 
	 * (非 Javadoc)
	 * 
	 * @Title: setMCache
	 * @Description:
	 * @param cache
	 * @see com.ccindex.mInterface.Bean#setMCache(com.ccindex.util.cache.Cache)
	 *      设置内存
	 */
	public void setMCache(Cache cache) {
	}

	private String formatStr;

	private void init() {
		ttime = "";
		// 时间 yyyy-MM-dd HH:mm:ss
		time = "";
		// 时间 yyyyMMdd
		day = "";
		ip = "";
		province = "";
		city = "";
		isp = "";
		code = "";
		channel = "";
		bytes = 0f;
		elapsed = 0f;
		speed = 0f;
		way = "";
		src_url = "";
		head = "";
		refer = "";
		dest_url = "";
		version = "";
	}

	/*
	 * 获取时间,为时间s累加值
	 */
	public String getTTime() {
		return ttime;
	}

	/**
	 * 
	 * @Title: getTime
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取时间值,按照固定格式输出 yyyy-MM-dd HH:mm:ss
	 * @return String
	 * @throws
	 */
	public String getTime() {

		// time = timeUtil.getTime(ttime);

		return time;
	}

	/**
	 * 
	 * @Title: getDay
	 * @Description: TODO(这里用一句话描述这个方法的作用)获取日期,格式为YYYYMMDD
	 * @return String
	 * @throws
	 */
	public String getDay() {
		// day = timeUtil.getTime(ttime).split(" ")[0];
		day = time.split(" ")[0];
		// day = getTime().split(" ")[0];
		day = day.replace("-", "");
		return day;
	}

	private Pattern rexChannel = Pattern
			.compile(Regular.FLVMOVDBEAN_REXCHANNEL);

	public String getReferChannel() {
		Matcher mreferchannel = rexChannel.matcher(refer);
		if (mreferchannel.find())
			return mreferchannel.group(1);
		return "";
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

				ip = ma.group(2);
				ipLong = IPParse.ip2Long(ip);
				// String ip_local = cache.get(ip, ipParser, "get", ipLong);
				String ip_local = ipParser.get(ipLong);
				String[] locals = ip_local.split("\t");
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

				code = ma.group(3);
				channel = ma.group(5);

				try {
					bytes = Float.parseFloat(ma.group(4));
					elapsed = Float.parseFloat(ma.group(6));
				} catch (Exception e) {
					elapsed = 0f;
				}

				// speed
				if (elapsed == 0f)
					speed = 0f;
				else
					speed = bytes / elapsed;

				way = ma.group(7);
				src_url = ma.group(8);
				int end = src_url.indexOf("?");
				if (end > 0)
					src_url = src_url.substring(0, end);
				head = ma.group(9);
				refer = ma.group(10);
				dest_url = ma.group(11);
				end = dest_url.indexOf("?");
				if (end > 0)
					dest_url = dest_url.substring(0, end);
				version = "1.0";
				runFormat();

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	private void runFormat() throws Exception {
		StringBuffer sb = new StringBuffer();

		sb.append(getTime()).append("\t");
		// ip
		sb.append(getIp()).append("\t");

		sb.append(getProvince()).append("\t");
		sb.append(getCity()).append("\t");
		sb.append(getIsp()).append("\t");
		sb.append(getCode()).append("\t");
		sb.append(getChannel()).append("\t");
		sb.append(getBytes()).append("\t");
		sb.append(getElapsed()).append("\t");
		sb.append(getSpeed()).append("\t");

		sb.append(getWay()).append("\t");
		sb.append(getSrcUrl()).append("\t");
		sb.append(getHead()).append("\t");

		sb.append(getRefer()).append("\t");

		sb.append(getDestUrl()).append("\t");

		sb.append(getVersion()).append("\t");

		// host
		sb.append(getHost()).append("\t\n");

		formatStr = sb.toString();
	}

	public String format() {
		return formatStr;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Long getIpLong() {
		return ipLong;
	}

	public String getIp() {
		return ip;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getIsp() {
		return isp;
	}

	public float getElapsed() {
		return elapsed;
	}

	public float getSpeed() {
		return speed;
	}

	public float getBytes() {
		return bytes;
	}

	public String getWay() {
		return way;
	}

	public String getSrcUrl() {
		return src_url;
	}

	public String getHead() {
		return head;
	}

	public String getRefer() {
		// refer
		Matcher mreferchannel = rexChannel.matcher(this.refer);
		if (mreferchannel.find())
			this.refer = mreferchannel.group(1);

		return this.refer;
	}

	public String getChannel() {
		return channel;
	}

	public String getDestUrl() {
		return dest_url;
	}

	public String getCode() {
		return code;
	}

	public String getVersion() {
		return version;
	}

	private long ipLong;

	private String host;
	// 变量定义参看CCIndex底层清洗算法文档中2.3 FLV采集清洗算法映射算法
	// 时间戳
	private String ttime;
	// 时间 yyyy-MM-dd HH:mm:ss
	private String time;
	// 时间 yyyyMMdd
	private String day;
	// 请求MVOD的客户端的IP地址
	private String ip;
	private String province;
	private String city;
	private String isp;
	// 下载成功情况
	private String code;
	// 频道
	private String channel;
	// 下载字节bytes
	private float bytes;
	// 下载耗时ms
	private float elapsed;
	// 下载速度
	private float speed;
	// 请求方法
	private String way;
	// 原始的url; MVOD提供服务的URL是经过base64后的，本参数是反base64,将其还原
	private String src_url;
	// 用户访问信息； HTTP头，用户访问的客户端类型
	private String head;
	// 请求的referer值；客户端请求时带有的referer值
	private String refer;
	// 请求的URL; MVOD提供服务的最终URL
	private String dest_url;
	private String version;

	public static void main(String[] args) {
		// Pattern rexRow = Pattern
		// .compile("^(\\d+)(?:\\.\\d+)?\\s+(\\S+)(?:(?:\\s-){2}\\s\\S+\\s\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)\\s\"([^\"]+)\"\\s\"([^\"]+)\"\\s([^\"]+)\"([^\"]+)\"");
		// Pattern rexRow = Pattern.compile("^\\d+.*$");
		String ss = "1355998214.359 223.202.2.25 - - 2012-12-20 18:10:14 200 312 v.cctv.ccgslb.net 115 HEAD http://v.cctv.ccgslb.net/flash/mp4video25/TMS/2012/12/19/b471622fbddd4dd895fa93b181b8a212_h264818000nero_aac32-8.mp4?start=0 \"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_5; zh-cn) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5\" \"http://cctv.cntv.cn/lm/yixian/\" - \"/data4/v.cctv.ccgslb.net/di5jY3R2LmNjZ3NsYi5uZXQ-/Zmxhc2g-/bXA0dmlkZW8yNQ--/VE1T/MjAxMg--/MTI-/MTk-/YjQ3MTYyMmZiZGRkNGRkODk1ZmE5M2IxODFiOGEyMTJfaDI2NDgxODAwMG5lcm9fYWFjMzItOA--.mp4\"";
		String ss1 = "1356045029.216 202.100.92.36 - - 2012-12-21 07:10:29 200 275 hcflv.yinyuetai.com 47 HEAD http://hcflv.yinyuetai.com/uploads/videos/common/BD9C012FAB0393A2B224C0369D612FAF.flv?sc=236043b5b43b6f4b&br=751&ptp=mv&rd=unknown \"360WebApp\" \"http://www.yinyuetai.com/swf/mvplayer.$41210.swf\" - \"/data6/hcflv.yinyuetai.com/aGNmbHYueWlueXVldGFpLmNvbQ--/dXBsb2Fkcw--/dmlkZW9z/Y29tbW9u/QkQ5QzAxMkZBQjAzOTNBMkIyMjRDMDM2OUQ2MTJGQUY-.flv\"";
		// String ss = "1355998200.121";
		// Matcher ma = rexRow.matcher(ss);
		// if (ma.matches()) {
		// System.out.println(1);
		// } else {
		// System.out.println(2);
		// }

		MovdBean mvod = new MovdBean();
		// System.out.println(mvod.filling(ss));
		// System.out.println(mvod.filling(ss1));
	}
}
