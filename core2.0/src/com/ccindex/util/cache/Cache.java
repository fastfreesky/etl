package com.ccindex.util.cache;

public interface Cache {
	
	public void set(String key,String value) throws Exception ;
	
	public String get(String key) throws Exception;
	
	public String get(String key,Object action,String function,Object... params) throws Exception ;
}
