package jym.vcf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jym.lan.Lang;

public class Type {
	
	private final static Map<String, String> map = new HashMap<String, String>();
	private final static Map<String, Integer> sorter = new HashMap<String, Integer>();
	private static Integer count = 0;
	
	static {
	//	set("N", 		Lang.get("vcf.type.n")			);
		set("PHOTO",	Lang.get("vcf.type.photo")		);
		set("FN",		Lang.get("vcf.type.fn")			);
		set("TEL",		Lang.get("vcf.type.tel")		);
		set("X-MSN",	Lang.get("vcf.type.msn")		);
		set("X-AIM",	Lang.get("vcf.type.aim")		);
		set("NOTE",		Lang.get("vcf.type.note")		);
		set("URL",		Lang.get("vcf.type.url")		);
		set("TITLE",	Lang.get("vcf.type.title")		);
		set("ORG",		Lang.get("vcf.type.org")		);
		set("ADR",		Lang.get("vcf.type.adr")		);
		set("EMAIL",	Lang.get("vcf.type.email")		);
		
		set("X-ANDROID-CUSTOM",			Lang.get("vcf.type.cust"));
		set("X-PHONETIC-FIRST-NAME",	"first name"	);
		set("X-PHONETIC-MIDDLE-NAME",	"middle name"	);
		set("X-PHONETIC-LAST-NAME",		"last name"		);
		

		set("HOME",		Lang.get("vcf.type.home")		);
		set("WORK",		Lang.get("vcf.type.work")		);
		set("VOICE",	Lang.get("vcf.type.voice")		);
		set("CELL",		"");
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
