package test.config;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ccindex.config.RowDump;
import com.ccindex.config.RowLoad;
import com.ccindex.config.imp.RowDumpFile;
import com.ccindex.config.imp.RowLoadURL;

public class ConfigDumpTxtUnit {

	@Test
	public void test() throws Exception {
		RowDump<File, List<Map<String, Object>>> rw = new RowDumpFile();

		URL uurl = new URL(
				"http://210.14.132.235:8888/down/home/kaiyi.liu/GWB_IPRange.txt");
		RowLoad<URL, List<Map<String, Object>>> c = new RowLoadURL();
		c.setColums("iprange", "name", "hostnames");
		c.setSplit("\t");
		c.setSource(uurl);
		c.load();

		List<Map<String, Object>> ll = c.getData();
		System.out.println(ll.get(1).get("iprange"));

		rw.setSplit("\t");
		rw.setColums("iprange", "name", "hostnames");
		rw.dumpTo( new File("D:\\test\\GWB_IPRange.txt"));
		rw.setData(ll);
		rw.dump();

	}

}
