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
 * @ClassName: DefaultBean
 * @Description: TODO(这里用一句话描述这个类的作用)处理可匹配之外的最后的bean
 * @author tianyu.yang
 * @date 2013-8-21 下午5:09:26 filling 永远返回false
 */
public class DefaultBean implements Bean {

	private String values;
	private StringBuffer sb = new StringBuffer();

	@Override
	public boolean filling(String task,Object values) {
		// TODO Auto-generated method stub
		this.values = (String) values;
		return false;
	}

	@Override
	public String format() {
		// TODO Auto-generated method stub
		sb.setLength(0);
		return sb.append(values).append("\n").toString();
	}

	public String getDay() {
		return "default";
	}
}
