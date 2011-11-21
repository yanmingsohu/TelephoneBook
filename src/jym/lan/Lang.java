// CatfoOD 2011-5-1 œ¬ŒÁ05:21:51

package jym.lan;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/** ∂‡”Ô—‘∑˚∫≈”≥…‰ */
public class Lang {
	
	private static final Properties lang;
	
	static {
		lang = new Properties();
		init();
	}
	
	public static String get(String id) {
		return lang.getProperty(id, id + "is undefined");
	}
	
	public static String get(String id, Object... parm) {
		return String.format(get(id), parm);
	}
	
	public static void init() {
		String u_lang = getLanguage() + ".lang.txt";
		
		if (!load(u_lang)) {
			load("en.lang.txt");
		}
	}
	
	private static boolean load(String src) {
		try {
			InputStream in = Lang.class.getResourceAsStream(src);
			InputStreamReader read = new InputStreamReader(in, "UTF-8");

			lang.load(read);
			read.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getLanguage() {
		return System.getProperty("user.language", "zh");
	}
	
	public static void main(String[] s) {
		System.out.println(get("error.vcfline", 22));
	}
}
