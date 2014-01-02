package com.ccindex.conf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ccindex.mInterface.Bean;

public class BeanChoice {

	private Entry<String, Bean> lastBean = null;
	private Map<String, Bean> beansClass;
	private Entry<String, Bean> beanClassEntry = null;
	private Iterator<Entry<String, Bean>> beansClassItor;
	private Bean value;
	private long countHit = 0l;// 被缓存命中的bean
	private long countChange = 0l;// 重新选取缓存的次数
	private long countDefault = 0l;// 不符合标准Bean的击中次数
	private long countAll = 0l;// 一共运行次数
	private HashMap<String, Bean> otherBean = new HashMap<String, Bean>(1);

	public BeanChoice(Map<String, Bean> beansClass) {
		otherBean.put("default", beansClass.get("default"));
		beansClass.remove("default");
		this.beansClass = beansClass;
	}

	public Entry<String, Bean> getBean(String fileName, String line) {

		countAll++;

		if (lastBean != null) {
			value = lastBean.getValue();
			boolean isBean = value.filling(fileName, line);
			if (isBean) {
				countHit++;
				return lastBean;
			}
		}

		beansClassItor = beansClass.entrySet().iterator();
		while (beansClassItor.hasNext()) {
			beanClassEntry = beansClassItor.next();
			if (lastBean != null
					&& beanClassEntry.getKey().equals(lastBean.getKey())) {
				continue;
			}
			value = beanClassEntry.getValue();

			boolean isBean = value.filling(fileName, line);
			if (isBean) {
				countChange++;
				lastBean = beanClassEntry;
				return beanClassEntry;
			}
		}

		countDefault++;

		otherBean.get("default").filling(fileName, line);

		return otherBean.entrySet().iterator().next();
	}

	public long getCountDefault() {
		return countDefault;
	}

	public long getCountHit() {
		return countHit;
	}

	public long getCountChange() {
		return countChange;
	}

	public long getCountAll() {
		return countAll;
	}

}
