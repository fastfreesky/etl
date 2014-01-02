package com.ccindex.util.parser.ua;

/**
 * 
 * @ClassName: UserAgent
 * @Description: TODO(这里用一句话描述这个类的作用) UA结果对应实体类
 * @author xinchao.wang
 * @date 2013-7-9 下午5:13:52
 * 
 */
public class UserAgent {

	private String browser = "Other";// 浏览器
	private String browser_version = "0";// 浏览器版本
	private String os = "Other";// 系统
	private String os_version = "0";// 系统版本
	private String device = "Other";// 设备型号
	private boolean mobile;// 是否移动设备

	//getters and setters
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		if(browser != null){
			this.browser = browser.length() > 64 ? browser.substring(0, 64) : browser;
		}
	}

	public String getBrowser_version() {
		return browser_version;
	}

	public void setBrowser_version(String browser_version) {
		if(browser_version != null){
			this.browser_version = browser_version.length() > 10 ? browser_version.substring(0, 10) : browser_version;
		}
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		if(os != null){
			this.os = os.length() > 64 ? os.substring(0, 64) : os;
		}
	}

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		if(os_version != null){
			this.os_version = os_version.length() > 10 ? os_version.substring(0, 10) : os_version;
		}
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		if(device != null){
			this.device = device.length() > 64 ? device.substring(0, 64) : device;
		}
	}

	public boolean isMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	@Override
	/**
	 * 重写toString方法，按照浏览器"\t浏览器版本\t系统\t系统版本\t设备型号\t是否移动设备"格式输出
	 * (非 Javadoc) 
	 * @Title: toString
	 * @Description:
	 * @return 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return browser + "\t" + browser_version + "\t" + os + "\t" + os_version
				+ "\t" + device + "\t" + mobile;
	}

}
