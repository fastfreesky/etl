package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccindex.util.parser.ip.IP2Long;

public class Test {
	
	public static void readFileByLines(String fileName) {
		IP2Long ip = new IP2Long();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	String[] ss = tempString.split(",");
            	
            	System.out.println(   
            			ip.longToIP( Long.parseLong(ss[0]) )+","+
            			ip.longToIP(Long.parseLong(ss[1])    ));
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
	
	public static void main(String[] args) throws Exception {
		//readFileByLines(args[0]);
		String str =
				"<table> \n" +
				" <tr> \n" +
				" <td> \n" +
				" Hello World! \n" +
				" </td> \n" +
				
				" <td> \n" +
				" 2.Hello World! \n" +
				" </td> \n" +
				
				" </tr> \n" +
				"</table>";
				String regex = "<td>(.+?)</td>";
				Pattern pattern = Pattern.compile(regex,Pattern.DOTALL);
				Matcher matcher = pattern.matcher(str);
				while(matcher.find()) {
					System.out.println(matcher.group(1).trim());
				} 
	}
}
