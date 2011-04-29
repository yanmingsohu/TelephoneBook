package jym.wit;
// CatfoOD 2008.5.28

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 * 使用了substance.jar这个包，
 * 保证在类库的路径中能找到它。
 */
public class LookandfeelFactory {
	private LookandfeelFactory() {}
	
	private static final String setfile = ".lookandfeel";
	
	/**
	 * 返回可用外观的字符串表示
	 */
	public static String[] getLookandFeel() {
		return lookandfeel;
	}
	
	/**
	 * 读取保存的设置
	 */
	public static void loadConfig() throws IOException {
		FileInputStream in = new FileInputStream(setfile);
		byte[] b = new byte[150];
		int c = in.read(b);
		set( new String(b,0,c) );
	}
	
	/**
	 * 保存外观到配置文件
	 */
	public static void saveConfig(String name) throws IOException {
		FileOutputStream out = new FileOutputStream(setfile);
		out.write( name.getBytes() );
		out.flush();
		out.close();
	}
	
	/**
	 * 当前的外观保存到配置文件
	 */
	public static void saveConfig() {
		try {
			String s = UIManager.getLookAndFeel().getClass().getName();
			if (s!=null) saveConfig( s );
		} catch (IOException e) {}
	}
	
	/**
	 * 用指定的外观名改变当前进程的皮肤
	 * @param subname - 新外观的名字
	 */
	public static void setStanceFeel(String subname) {
		String subskin = "Substance"+subname+"LookAndFeel";
		String sublook = "org.jvnet.substance.skin."+subskin;
		// String defaultlook = "org.jvnet.substance.SubstanceLookAndFeel";
		set(sublook);
	}

	/**
	 * 获取所有外观的菜单项的便捷方法
	 * @return JMenuItem[] - 可用外观的菜单数组
	 */
	public static JMenuItem[] getLookandFeelMenuItem() {
		JMenuItem[] jm = new JMenuItem[lookandfeel.length];
		MItemGroup group = new MItemGroup();
		for (int i=0; i<jm.length; ++i) {
			jm[i] = new MItem(lookandfeel[i]);
			group.add((MItem)jm[i]);
		}
		return jm;
	}
	
	/**
	 * 随机的选择一外观
	 */
	public static void setRandomLookandFeel() {
		setStanceFeel( lookandfeel[(int)(Math.random()*lookandfeel.length)] );
	}
	
	private static String[] lookandfeel = {
		"Autumn", "BusinessBlackSteel", "BusinessBlueSteel", "Business",
		"ChallengerDeep", "CremeCoffee", "Creme", "EmeraldDusk",
		"FieldOfWheat", "FindingNemo", "GreenMagic", "Magma", "Mango",
		"MistAqua", "MistSilver", "Moderate", "NebulaBrickWall",
		"Nebula", "OfficeBlue2007", "OfficeSilver2007", "RavenGraphiteGlass",
		"RavenGraphite", "Raven", "Sahara",
	};
	
	/**
	 * 设置外观为Swing的默认外观
	 */
	public static void setSwingLookandFeel() {
		set("javax.swing.plaf.metal.MetalLookAndFeel");
	}
	
	/**
	 * 设置外观为本地系统的外观
	 */
	public static void setNativeLookandFeel() {
		set(UIManager.getSystemLookAndFeelClassName());
	}
	
	private static void set(String s) {
		try {
			UIManager.setLookAndFeel(s);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}


class MItemGroup {
	private ArrayList<MItem> list = new ArrayList<MItem>();
	
	public void add(MItem i) {
		list.add(i);
		i.addGroup(this);
	}
	
	protected void inform(MItem item) {
		for (int i=0; i<list.size(); ++i) {
			if (list.get(i)!=item) {
				list.get(i).inform();
			}
		}
	}
}