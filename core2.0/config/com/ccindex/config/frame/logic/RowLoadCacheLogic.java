package com.ccindex.config.frame.logic;

import java.util.List;
import java.util.Map;

import com.ccindex.config.RowLoadByCache;

public class RowLoadCacheLogic {

	static public List<Map<String, Object>> getData(RowLoadByCache cache) {
		try {
			
			try {
				if (!cache.existsCacheSource()) {
					System.out.println("! cacheLoad.getSource.exists");

					cache.getLock().locked(10000, false);

					if (!cache.getCacheRowLoad().load()) {
						cache.getRowLoad().load();

						System.out.println("ok load.load");

						cache.getCacheRowDump().setData(
								cache.getRowLoad().getData());
						cache.getCacheRowDump().dump();
						Thread.sleep(500);
					}
					if (!cache.getCacheRowLoad().load()) {
						throw new Exception("Exception cacheDump ");
					}
				} else {
					try {
						cache.getLock().locked(20000, true);
						System.out.println("cacheLoad.getSource.exists");
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						cache.getCacheRowLoad().load();	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cache.getLock().unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return (List<Map<String, Object>>) cache.getCacheRowLoad().getData();
	}
}
