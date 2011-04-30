package jym.vcf;

import java.util.HashMap;
import java.util.Map;

public class Type {
	
	private final static Map<String, String> map = new HashMap<String, String>();
	
	static {
		set("N", 						"名字");
		set("FN",						"名字");
		set("TEL",						"电话");
		set("X-MSN",					"MSN");
		set("X-AIM",					"QQ");
		set("NOTE",						"备注");
		set("URL",						"网址");
		set("TITLE",					"标题");
		set("ORG",						"公司");
		set("ADR",						"地址");
		set("EMAIL",					"邮箱");
		set("X-ANDROID-CUSTOM",			"自定义");
		set("X-PHONETIC-LAST-NAME",		"last name");
		set("X-PHONETIC-MIDDLE-NAME",	"middle name");
		set("X-PHONETIC-FIRST-NAME",	"first name");
		

		set("HOME",		"家庭");
		set("WORK",		"工作");
		set("VOICE",	"语音");
		set("CELL",		"");
	}
	
	
	public static String get(String name) {
		return map.get(name);
	}
	
	private static void set(String name, String cname) {
		map.put(name, cname);
	}
}
