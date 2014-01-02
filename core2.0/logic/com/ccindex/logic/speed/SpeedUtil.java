package com.ccindex.logic.speed;

public class SpeedUtil {
	
	/**
	 * 根据下载日志的 【下载文件大小】和  【下载耗时】判断当前数据能否加入到计算  【平均下载网速】
	 * 
	 * <pre>
	 *	1.下载耗时 $elapsed 大于 100毫秒 
	 *  2.下载字节 $bytes 大于 2MB ，小于 50MB  
	 *  3.下载速度  (单位 KB)  $k_speed = $bytes/$elapsed / 1.024 ; 
	 *  	大于 30KB/s，小于 2500KB/s 
	 * </pre>
	 * 
	 * @param btyes :  (单位)  Byte 
	 * @param elapsed : (单位) 毫秒 
	 * @return
	 */
	public static boolean isDownSpeed(float dbtyes, float delapsed){
		// 下载大小 单位转换为 ： MB
		float mbytes = dbtyes / 1024 / 1024;
		// 速度单位 转换   : KB/s
		float k_speed = (dbtyes / 1024) / (delapsed / 1000 );
		if ((mbytes >= 2 && mbytes <= 50) && delapsed >= 100 && (k_speed >= 30 && k_speed <= 2500)) {
			return true;
		}
		return false ;
	}
	
	
	/**
	 * 合并   加权下载速度<p>
	 * 
	 * 速度单位  Mb/s <p>
	 * 返回单位  Mb/s <p>
	 * return   ( s1*c1 + s2*c2 ) / ( c1+c2 )
	 */
	public static float modifyDownSpeed(double sum_speed_1,int count_speed_1,double sum_speed_2,int count_speed_2){
		return (float)( sum_speed_1 * count_speed_1 + sum_speed_2 * count_speed_2 ) / ( count_speed_1 +  count_speed_2 );
	}
	
	/**
	 * 计算单条数据  【下载网速】
	 * 
	 * <pre>
	 *	 返回单位 : Mb/s
	 * </pre>
	 * 
	 * @param btyes :  (单位)  Byte 
	 * @param elapsed : (单位) 毫秒 
	 * @return
	 */
	public static float downSpeedMb(float bytes, float elapsed){
		return  (  (bytes / 1024 / 1024 ) / (elapsed / 1000)  ) * 8 ;
	}
	
	/**
	 * 计算单条数据  【下载网速】
	 * 
	 * <pre>
	 *	 返回单位 : kb/s
	 * </pre>
	 * 
	 * @param btyes :  (单位)  Byte 
	 * @param elapsed : (单位) 毫秒 
	 * @return
	 */
	public static float downSpeedKb(float bytes, float elapsed){
		return  (  (bytes / 1024  ) / (elapsed / 1000)  ) * 8 ;
	}
}
