package jym.wit;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JDialog;

import jym.lan.Lang;

public class Tools {
	private Tools() {}
	private static MessageDialog md = new MessageDialog();
	
	/**
	 * 一个对话框错误消息
	 * @param e - 要显示的错误
	 */
	public static void println(Exception e) {
		md.show(e);
	}
	public static void message(String s) {
		md.show(s);
	}
	public static void messageid(String id) {
		message(Lang.get(id));
	}
	public static void p(Object o) {
		System.out.println(o);
	}
	
	public static String getRandString() {
		char[] str = new char[16];
		int i=0;
		while( i<str.length ) {
			str[i++]=(char)(Math.random()*26+'a');
		}
		return new String(str);
	}
	
	public static boolean notNull(String s) {
		return s!=null && s.trim().length()>0;
	}
	
	public static File getRandFile() {
		File file;
		do{
			file = new File( Tools.getRandString()+".tel" );
		}while(file.exists());
		
		return file;
	}
	
	public static void functionNotComplete() {
		md.show(Lang.get("about.unachieve") +
				"\nyanming-sohu@sohu.com\nqq:412475540");
	}
	
	public static void center(JDialog w) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = w.getWidth();
		int height= w.getHeight();
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		w.setBounds(x, y, width, height);
	}
}
