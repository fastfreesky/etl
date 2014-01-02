package com.ccindex.util.time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TimeUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public TimeUtil() {
//		System.out.println("Init TimeUtil-----------------------");
	}

	public String getTime(Long time) {
		String date = sdf.format(time);
		return date;
	}

	public String getTime(String time) {
		return getTime(Long.parseLong(time));
	}

	/**
	 * 
	 * @param yyyymm
	 */
	private static SimpleDateFormat sdym = new SimpleDateFormat("yyyy-MM");
	public Calendar getMonthStart(String yyyy_mm) {
		Calendar ca = Calendar.getInstance();
		int yyyy = Integer.parseInt( yyyy_mm.split("-")[0] );
		int month = Integer.parseInt( yyyy_mm.split("-")[1] )-1;
		
		ca.set( Calendar.YEAR, yyyy );
		ca.set( Calendar.MONTH, month );
		ca.set( Calendar.DATE, 1);
		ca.set( Calendar.HOUR_OF_DAY, 0);
		ca.set( Calendar.MINUTE, 0);
		ca.set( Calendar.SECOND, 0);
		ca.set( Calendar.MILLISECOND, 0);
		return ca ;
	}
	
	public List<Long>  getMonthDayTLong(String yyyy_mm){
		List<Long> li = new ArrayList<Long>();
		Calendar ca = getMonthStart(yyyy_mm);
		int month = ca.get(Calendar.MONTH)  ;
		
		for (int i = 0; i < 32; i++) {
			Calendar tca = getMonthStart(yyyy_mm);
			tca.set( Calendar.DATE, ca.get(Calendar.DATE)+i );
			if( month == tca.get(Calendar.MONTH) )
				li.add( tca.getTime().getTime() );
			else
				break ;
		}
		
		return li ;
	}
	
	
	public static void main(String[] args) {
		TimeUtil tu = new TimeUtil();
		for (Long ll : tu.getMonthDayTLong("2012-12")) {
			System.out.println(ll+"\t"+tu.getTime(ll));
		}
		System.out.println(  "1351550400000".compareTo( "1352651893000" ));
	}
}
