package com.ccindex.factory.count;

/**
 * 计数模块，适用于IdleSpeedProcess类
 * @author tianyu.yang
 *
 */
public class CountProcessForIdleSpeed{
	//日期，时间（5s每周期），省，城市，运营商，total（日期，时间（5s每周期），省，城市，运营商确定的累加总数）
	private String date, time, province, city, isp, version;	
	//字节数（累加值），速度（byte/ms）(累加字节/累加毫秒)，清洗版本,总耗时毫秒
	private  int total=0, all_total=0;//all_total总条数,total为清洗合格条数
	private  double bytes=0, elapsed=0, speed=0;
	
	
	public void clean(){
		all_total=0;
		total=0;
		bytes=0;
		elapsed=0;
		speed=0;
		date=null;
		time=null;
		province=null;
		city=null;
		isp=null;
		version=null;
	}
	/**
	 * 设置日期
	 * @param date
	 */
	public void setDate(String date){
		this.date = date;
	}
	
	/**
	 * 设置时间，间隔为5分钟
	 * @param time
	 */
	public void setTime(String time){
		this.time=time;
	}
	
	/**
	 * 设置省份，城市，运营商
	 * @param province
	 */
	public void setProvinceCityIsp(String province, String city, String isp){
		this.province=province;
		this.city=city;
		this.isp=isp;

	}

	/**
	 * 设置文件版本
	 * @param version
	 */
	public void setVersion(String version){
		this.version=version;
	}
	
	/**
	 * 设置字节数
	 * @param bytes
	 */
	public void setBytes(double bytes){
		this.bytes +=bytes;
	}
	/**
	 * 设置总耗时
	 * @param elapsed
	 */
	public void setElapsed(double elapsed){
		this.elapsed += elapsed;
	}
	
	/**
	 * 设置出现次数,累加
	 */
	public void setTotal(){
		this.total +=1;
	}

	/**
	 * 设置总共循环条数累加
	 */
	public void setAllTotal(){
		this.all_total +=1;
	}
	
	/**
	 * 设置速度
	 */
	public void setSpeed(double speed){
		this.speed +=speed;
	}
	
	/**
	 * 获取字节数
	 * 
	 */
	public double getBytes(){
		return this.bytes;
	}
	/**
	 * 获取总耗时
	 * 
	 */
	public double getElapsed(){
		return this.elapsed;
	}	
	
	/**
	 * 获取速度
	 * 
	 */
	public double getSpeed(){
		return this.speed;
	}	
	/**
	 * 格式化输出
	 * @return
	 */
	public String format(){
		
		StringBuffer format_out = new StringBuffer();
		
		format_out.append(date).append('\t');
		format_out.append(time).append('\t');
		
		format_out.append(province).append('\t');
		format_out.append(city).append('\t');
		format_out.append(isp).append('\t');

		format_out.append(version).append('\t');
		
		//累加条数
		format_out.append(all_total).append('\t');
		
		//累加字节数
		format_out.append(bytes).append('\t');
		//累加耗时
		format_out.append(elapsed).append('\t');
		//累加速度
		format_out.append(speed).append('\t');	
		//参与下载的条数
		format_out.append(total).append("\t\n");
		
		return format_out.toString();
	}
}
