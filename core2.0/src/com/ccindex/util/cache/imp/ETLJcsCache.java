package com.ccindex.util.cache.imp;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.jcs.JCS;

import com.ccindex.util.cache.Cache;

public class ETLJcsCache implements Cache {
	
	private static  JCS jcscache ; 
	public static int jcscache_num = 0 ;
	
	private static final String DEFAULT_NAME_SPACE=".";
	static {
		try {
			jcscache = JCS.getInstance(DEFAULT_NAME_SPACE);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String group ;
	public ETLJcsCache(String group) {
		this.group  = group ;
	}

	public String getDataByRunFun(Object action,String function,Object... params) throws Exception{
		Class<? extends Object> cobj = action.getClass();
		Class[] cparams = new Class[params.length];
		for (int i = 0; i < cparams.length; i++) {
			cparams[i] = params[i].getClass();
		}

		Method method = cobj.getMethod(function,cparams);
		return (String)method.invoke(action, params);
	}
	
	public String get(String key) throws Exception{
		return  (String)jcscache.getFromGroup(key,group);	
	}
	
	/**
	 * 
	 * (非 Javadoc) 
	 * @Title: set
	 * @Description:设置常用值到cache中,如果使用一份匹配,则设置set需要加锁解锁,消耗时间
	 * @param key
	 * @param value
	 * @throws Exception 
	 * @see com.ccindex.util.cache.Cache#set(java.lang.String, java.lang.String)
	 */
	public void set(String key,String value) throws Exception{
		jcscache_num++;
		synchronized (this) {
			jcscache.putInGroup(key, group, value);
		}
	}
	
	public String get(String key,Object action,String function,Object... params) throws Exception{
		String ret_str = get(key);
		if(  ret_str == null  ){
			ret_str = getDataByRunFun(action, function, params);
			//set(key, ret_str);
		}
		return ret_str;
	}
	
	
}
