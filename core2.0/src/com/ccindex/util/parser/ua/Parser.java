package com.ccindex.util.parser.ua;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccindex.config.util.CacheConfigUtil;

/**
 * 
 * @ClassName: Parser 
 * @Description: TODO(这里用一句话描述这个类的作用) UA解析类
 * @author  xinchao.wang
 * @date 2013-7-10 上午9:52:34 
 *
 */
public class Parser {

	private List<UAPattern> browserRegex;
	private List<UAPattern> osRegex;
	private List<UAPattern> deviceRegex;
	
	/**
	 * 构造方法
	 * @Title: Parser.java 
	 * @Description: 
	 * @param yamlPath
	 * @throws Exception 
	 */
	public Parser() throws Exception{
		String db_url = "jdbc:mysql://223.203.192.248/rule?useUnicode=true&characterEncoding=utf8&user=dba&password=dba";
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		List<Map<String, Object>> browser = CacheConfigUtil
				.cache(db_url,
						"/tmp/user_agent_browser/" + date + "/browser.txt",
						"select regex,replacement,version,isMobile from user_agent_regexes where type = 'browser'",
						"regex", "replacement","version","isMobile");
		
		List<Map<String, Object>> os = CacheConfigUtil
				.cache(db_url,
						"/tmp/user_agent_os/" + date + "/os.txt",
						"select regex,replacement,version,isMobile from user_agent_regexes where type = 'os'",
						"regex", "replacement","version","isMobile");
		
		List<Map<String, Object>> device = CacheConfigUtil
				.cache(db_url,
						"/tmp/user_agent_device/" + date + "/device.txt",
						"select regex,replacement,version,isMobile from user_agent_regexes where type = 'device'",
						"regex", "replacement","version","isMobile");
		browserRegex = this.toUAPattern(browser);
		osRegex = this.toUAPattern(os);
		deviceRegex = this.toUAPattern(device);
	}
	
	/**
	 * 
	 * @Title: parse 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 主调方法，解析UA字符串，返回结果
	 * @param agent
	 * @return    
	 * UserAgent
	 * @throws
	 */
	public UserAgent parse(String agent){
		UserAgent userAgent = new UserAgent();
		this.matchBrowser(agent,userAgent);//匹配浏览器
		this.matchOS(agent, userAgent);//匹配系统
		this.matchDevice(agent, userAgent);//匹配设备信息
		return userAgent;
	}
	
	/**
	 * 
	 * @Title: matchBrowser 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 匹配UA字符串中的浏览器信息
	 * @param agent
	 * @param userAgent    
	 * void
	 * @throws
	 */
	public void matchBrowser(String agent, UserAgent userAgent){
		for (UAPattern up : browserRegex) {
			Matcher m = up.getPattern().matcher(agent);
			
			if (m.find()) {
				int groupCount = m.groupCount();//捕获组数量
				
				String replacement = up.getReplacement();
				String version = up.getVersion();
				String mobile = up.getMobile();
				
				//浏览器信息
				if (!replacement.equals("null")) {//这里有个坑！！！
					//采用凯大师所写的缓存策略，不会存在null的情况，但是会存在“null”的情况
					if (replacement.contains("$1") && groupCount >=1 && m.group(1) != null) {
						userAgent.setBrowser(replacement.replaceFirst("\\$1", Matcher.quoteReplacement(m.group(1))));
					} else {
						userAgent.setBrowser(replacement);
					}
				} else if (groupCount >= 1) {
					userAgent.setBrowser(m.group(1));
				}

				//版本信息
				if (!version.equals("null")) {
					userAgent.setBrowser_version(version);
				} else if (groupCount >= 2) {
					userAgent.setBrowser_version(m.group(2));
				}
				
				//是否移动设备
				if(!mobile.equals("null")){
					userAgent.setMobile(true);
				}
				return ;
			}
		}
	}
	
	/**
	 * 
	 * @Title: matchOS 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 匹配UA字符串中的系统信息
	 * @param agent
	 * @param userAgent    
	 * void
	 * @throws
	 */
	public void matchOS(String agent, UserAgent userAgent){
		for (UAPattern up : osRegex) {
			Matcher m = up.getPattern().matcher(agent);
			
			if (m.find()) {
				int groupCount = m.groupCount();//捕获组数量
				
				String replacement = up.getReplacement();
				String version = up.getVersion();
				String mobile = up.getMobile();
				
				//系统信息
				if (!replacement.equals("null")) {
					userAgent.setOs(replacement);
				} else if (groupCount >= 1) {
					userAgent.setOs(m.group(1));
				}
				
				//系统版本信息
				if (!version.equals("null")) {
					userAgent.setOs_version(version);
				} else if (groupCount >= 2) {
					userAgent.setOs_version(m.group(2));
				}
				
				//是否移动设备
				if(!mobile.equals("null")){
					userAgent.setMobile(true);
				}
				return ;
			}
		}
	}
	
	/**
	 * 
	 * @Title: matchDevice 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 匹配UA字符串中的设备信息
	 * @param agent
	 * @param userAgent    
	 * void
	 * @throws
	 */
	public void matchDevice(String agent, UserAgent userAgent){
		for (UAPattern up : deviceRegex) {
			Matcher m = up.getPattern().matcher(agent);
			
			if (m.find()) {
				int groupCount = m.groupCount();//捕获组数量
				
				String replacement = up.getReplacement();
				String mobile = up.getMobile();
				
				//设备型号信息
				if (!replacement.equals("null")) {
					if (replacement.contains("$1") && groupCount >=1 && m.group(1) != null){
						userAgent.setDevice(replacement.replaceFirst("\\$1", Matcher.quoteReplacement(m.group(1))));
					} else {
						userAgent.setDevice(replacement);
					}
				} else if (groupCount >= 1) {
					userAgent.setDevice(m.group(1));
				}
				
				//是否移动终端
				if (!mobile.equals("null")) {
					userAgent.setMobile(true);
				}
				return ;
			}
		}
	}
	
	public List<UAPattern> toUAPattern(List<Map<String, Object>> list){
		List<UAPattern> result = new ArrayList<UAPattern>();
		for (Map<String,Object> map : list) {
			UAPattern up = new UAPattern();
			up.setReplacement(map.get("replacement").toString());
			up.setVersion(map.get("version").toString());
			up.setMobile(map.get("isMobile").toString());
			up.setPattern(Pattern.compile(map.get("regex").toString()));
			result.add(up);
		}
		return result;
	}
	
	/**
	 * 
	 * @ClassName: UAPattern 
	 * @Description: TODO(这里用一句话描述这个类的作用) yaml文件包装类
	 * @author  xinchao.wang
	 * @date 2013-7-12 上午10:21:45 
	 *
	 */
	public class UAPattern{
		private Pattern pattern;
		private String replacement;
		private String version;
		private String mobile;
		
		public Pattern getPattern() {
			return pattern;
		}
		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		public String getReplacement() {
			return replacement;
		}
		public void setReplacement(String replacement) {
			this.replacement = replacement;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		String ua="Mozilla/5.0 (Linux; U; Android 2.3.4; zh-cn; XT531 Build/GRJ22) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1; 360browser(securitypay,securityinstalled); 360(android,uppayplugin); 360 Aphone Browser (4.7.2)";
		Parser parser = new Parser();
		UserAgent agent = parser.parse(ua);
		System.out.println(agent.toString());
	}
	
}
