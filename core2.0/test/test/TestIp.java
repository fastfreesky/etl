package test;

import org.junit.Test;

import com.ccindex.util.parser.ip.IP2Long;

public class TestIp {

	@Test
	public void test() {
		long[] lls = IP2Long.ip2RangeLongSize("180.152.0.0/13");
		System.out.println( IP2Long.ipRange( IP2Long.ipToLong("180.153.100.163"), lls[0], lls[1]) );
	}

}
