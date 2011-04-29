package jym.test;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import jym.vcf.VcfFormat;
import jym.vcf.VcfFormat.Contact;
import jym.vcf.VcfFormat.Item;

// CatfoOD 2011-4-29 下午02:01:10 yanming-sohu@sohu.com/@qq.com

public class TestVcfFormat {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File("h:/00003.vcf");
		
		VcfFormat vcf = new VcfFormat(f);
		List<Contact> list = vcf.getData();
		Iterator<Contact> itr = list.iterator();
		
		while (itr.hasNext()) {
			Contact c = itr.next();
			Iterator<Item> citr = c.getItems().iterator();
			
			while (citr.hasNext()) {
				Item it = citr.next();
				System.out.print(it.getName() + ":");
				
				String[] value = it.getValues();
				for (int i=0; i<value.length; ++i) {
					System.out.print(value[i] + ",");
				}
				System.out.println(" props:" + it.getProps());
			}
		}
		
		System.out.println(vcf.getColumns());
	}

	public static void testEncode() throws UnsupportedEncodingException {
		// =E5=AE=8B=E7=BA=A2=E6=B1=9F
		byte[] b = new byte[] {(byte) 0xe5, (byte) 0xae, (byte) 0x8b, (byte) 0xe7, 
				(byte) 0xba, (byte) 0xa2, (byte) 0xe6, (byte) 0xb1, (byte) 0x9f};
		
		String s = new String(b, "utf-8");
		System.out.println(s);
	}
}

/** a line:
	BEGIN:VCARD
	VERSION:2.1
	N;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:;=E5=AE=8B=E7=BA=A2=E6=B1=9F;;;
	FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:=E5=AE=8B=E7=BA=A2=E6=B1=9F
	TEL;CELL:1-514-241-5958
	END:VCARD
	
	BEGIN:VCARD
	VERSION:2.1
	N:fn;gn;middlename;;suffix
	FN:gn middlename fn, suffix
	X-PHONETIC-FIRST-NAME:phonetic gn
	X-PHONETIC-MIDDLE-NAME:pmn
	X-PHONETIC-LAST-NAME: xxx
	X-ANDROID-CUSTOM:vnd.android.cursor.item/nickname;nick;1;;;;;;;;;;;;;
	TEL;HOME:1
	TEL;CELL:2
	TEL;WORK:3
	TEL;VOICE:4
	EMAIL;HOME:home@a.com
	EMAIL;WORK:work@a.com
	EMAIL:other@a.com
	EMAIL;CELL:mobile@a.com
	ADR;HOME:;;stree;city;116011;zipcode;
	ADR;WORK:;;stree;city;state;zip;
	ORG:company
	TITLE:title
	URL:web
	NOTE:note
	X-AIM:im
	X-MSN:live
	END:VCARD
*/