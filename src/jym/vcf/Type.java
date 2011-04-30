package jym.vcf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Type {
	
	private final static Map<String, String> map = new HashMap<String, String>();
	private final static Map<String, Integer> sorter = new HashMap<String, Integer>();
	private static Integer count = 0;
	
	static {
	//	set("N", 		"名字"		);
		set("FN",		"名字"		);
		set("TEL",		"电话"		);
		set("X-MSN",	"MSN"		);
		set("X-AIM",	"QQ"		);
		set("NOTE",		"备注"		);
		set("URL",		"网址"		);
		set("TITLE",	"标题"		);
		set("ORG",		"公司"		);
		set("ADR",		"地址"		);
		set("EMAIL",	"邮箱"		);
		
		set("X-ANDROID-CUSTOM",			"自定义"			);
		set("X-PHONETIC-FIRST-NAME",	"first name"	);
		set("X-PHONETIC-MIDDLE-NAME",	"middle name"	);
		set("X-PHONETIC-LAST-NAME",		"last name"		);
		

		set("HOME",		"家庭"		);
		set("WORK",		"工作"		);
		set("VOICE",	"语音"		);
		set("CELL",		""			);
	}
	
	
	public static String get(String name) {
		return map.get(name);
	}
	
	/** 压入的顺序会影响显示的顺序  */
	private static void set(String name, String cname) {
		map.put(name, cname);
		sorter.put(cname, count);
		count = count + 1;
	}
	
	
	public static class Sorter implements Comparator<String> {

		public int compare(String a, String b) {
			Integer ai = sorter.get(a);
			Integer bi = sorter.get(b);
			
			if (ai!=null && bi!=null) {
				return ai.compareTo(bi);
			} else {
				return a.compareTo(b);
			}
		}
	}
	
}
