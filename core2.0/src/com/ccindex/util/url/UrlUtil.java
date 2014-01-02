package com.ccindex.util.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {
	private static Pattern p_channel = Pattern.compile("(^\\w+://.*?)/");
	
	private static Pattern p_domain = Pattern.compile("://(.*?)/");
	private static Pattern p_url = Pattern.compile("^.*?://(.*)$");
	
	private static Pattern p_suffix = Pattern.compile("^.*/.*\\.(\\w{1,8})$");
	private static Pattern p_fileName = Pattern.compile("^.*/.*?/(.*?)$");
	
	public static String OTHER = "OTHER" ;
	
	public static String getDomain(String url){
		Matcher m_domain = p_domain.matcher(url);
		if (m_domain.find())
			return m_domain.group(1);
		return OTHER ;
	}
	
	/**
	 * xinchao.wang 2013-08-08 新增的方法
	 * @Title: getChannel 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获得带有协议名称的频道URL
	 * @param url
	 * @return    
	 * String
	 * @throws
	 */
	public static String getChannel(String url) {
		Matcher m_channel = p_channel.matcher(url);
		if (m_channel.find()) {
			return m_channel.group(1);
		} else {
			return OTHER;
		}
	}
	
	
	public  static String getUrl(String url){
		url = url.replaceAll("(\\?|&).*?$", "");
		Matcher m_url = p_url.matcher(url);
		if (m_url.find()){
			url = m_url.group(1);
			return url.split("\\?")[0].split("&")[0];
		}
		return OTHER ;
	}
	
	public  static String getSuffix(String url){
		Matcher m_suffix = p_suffix.matcher(url.replaceAll("(\\?|&).*?$", ""));
		if (m_suffix.find())
			return m_suffix.group(1);
		return OTHER ;
	}
	
	public  static String getFilename(String url){
		Matcher m_fileName = p_fileName.matcher(url.replaceAll("\\?.*?$", ""));
		if (m_fileName.find())
			return m_fileName.group(1);
		return OTHER ;
	}
	
	
	public static void main(String[] args) {
		System.out.println(  getDomain("https://www.sohu.com/a.c")   );
		System.out.println(  getUrl("https://www.sohu.com/a.c?12")   );
		System.out.println(  getUrl("https://www.sohu.com/xx&a.c?12")   );
		System.out.println(  getSuffix("https://www.sohu.com/a.c?12")   );
		System.out.println(  getFilename("https://www.sohu.com/a.c?12")   );
		System.out.println(  getChannel("https://www.sohu.com/a.c?12")  );
	}
}
