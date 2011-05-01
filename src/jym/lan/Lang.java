// CatfoOD 2011-5-1 ÏÂÎç05:21:51

package jym.lan;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Lang {
	
	private static final Properties lang;
	
	static {
		lang = new Properties();
		init();
	}
	
	public static String get(String id) {
		return lang.getProperty(id, id + "is undefined");
	}
	
	public static void init() {
		String u_lang = "/jym/lan/" + getLanguage() + ".lang.txt";

		try {
			InputStream in = Lang.class.getResourceAsStream(u_lang);
			InputStreamReader read = new InputStreamReader(in, "GBK");

			lang.load(read);
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getLanguage() {
		return System.getProperty("user.language", "zh");
	}
	
	public static void main(String[] s) {
		System.out.println(get("help.message"));
	}
}
