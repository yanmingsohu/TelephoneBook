package jym.tel;
// CatfoOD 2008.2.24

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jym.vcf.VcfDataPack;
import jym.wit.AboutDialog;
import jym.wit.InputDialog;
import jym.wit.LookandfeelFactory;
import jym.wit.MItem;
import jym.wit.Tools;

public class ManageFrame extends JFrame {
	
	private static final long serialVersionUID = -7064444390471903015L;
	
	private JDesktopPane jdp;
	private Dimension dim;
	private int x=0,y=0;
	private final ManageFrame frame = this;
	
	public final KL keyListener = new KL();
	
	
	public ManageFrame() {
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.height -= 100;
		dim.width -= 100;
		this.setTitle("电话簿 CatfoOD 2008-2011 " + VersionCortrol.version);
		this.setBounds(50, 50, dim.width, dim.height);
		this.addWindowListener(new WL());
		this.addKeyListener(keyListener);
		
		jdp = new JDesktopPane();
		jdp.addKeyListener(keyListener);
		jdp.setBackground(Color.darkGray);
		setContentPane(jdp);
		setJMenuBar( creatMenu() );
		
		this.setVisible(true);
	}
	
	private NumberBook[] inFrame = new NumberBook[20];
	private int inFrameCont = 0;
	
	/** 关闭所有内部窗口,并保存他们的状态 */
	private void removeAllFrame() {
		for(int i=0; i<inFrameCont; ++i) {
			if(inFrame[i]!=null) {
				if( !inFrame[i].isClosed() ) {
					inFrame[i].quit();
				}
				jdp.remove(inFrame[i]);
				inFrame[i] = null;
			}
		}
		inFrameCont=0;
	}
	
	public void removeClosedFrame() {
		boolean changed = false;
		for(int i=0; i<inFrame.length; ++i) {
			if( inFrame[i]!=null ) {
				if( inFrame[i].isClosed() ) {
				//	p("移除 "+inFrame[i]);
					jdp.remove(inFrame[i]);
					inFrame[i] = null;
					changed = true;
				}
			}
		}
		if( changed ) {
			NumberBook[] _b = new NumberBook[inFrame.length];
			int count = 0;
			for(int i=0; i<inFrame.length; ++i) {
				if( inFrame[i]!=null ) {
					_b[count] = inFrame[i];
					++count;
				}
			}
			inFrameCont = count;
		}
	}
	
	/**
	 * 测试这个文件是否已经被打开
	 * @param f - 要测试的文件
	 */
	private boolean testFileOpened(File f) {
		for (int i=0; i<inFrameCont; ++i) {
			if ( inFrame[i]!=null && !inFrame[i].isClosed() ) {
				if ( inFrame[i].isFileOpened(f) ) {
					Tools.message("这个电话簿已经打开.");
					return true;
				}
			}
		}
		return false;
	}
	
	private void creatInframe(TableDataPack tdp) throws Exception {
		removeClosedFrame();
		if(inFrameCont<inFrame.length) {
			NumberBook inf = new NumberBook(tdp);
			inf.reshape(x, y, (int)(dim.width*0.7f), (int)(dim.height*0.7f));
			inf.setVisible(true);
			jdp.add(inf);
			try {
				inf.setSelected(true);
			} catch (PropertyVetoException e) {}
			inFrame[inFrameCont++] = inf;
			
			x = x<this.getWidth()-300 ? x+15 : 0;
			y = y<this.getHeight()-300? y+15 : 0;
		}
	}

	private JMenu open;
	private JMenuItem file;
	private JMenuItem del;
	private JMenuItem exits;
	private JMenuItem ivcf;
	private JMenuItem creat;
	private JMenuItem quit;
	
	private JMenu window;
	private JMenuItem cascade;
	private JMenuItem tile;
	private JMenuItem removeall;
	
	private JMenu lookandfeel;
	private JMenuItem swingfeel;
	private JMenuItem nativefeel;
	
	private JMenu abouthelp;
	private JMenuItem about;
	private JMenuItem help;
	
	/**
	 * @return
	 */
	private JMenuBar creatMenu() {
		JMenuBar menubar = new JMenuBar();
		
		open  	= new JMenu("文件");
		exits	= CreatMenuItem("打开已有的电话簿..");
		del		= CreatMenuItem("删除已有的电话簿..");
		file	= CreatMenuItem("从选择的文件打开电话簿..");
		creat	= CreatMenuItem("新建一个电话簿..");
		ivcf	= CreatMenuItem("打开VCF(Android)电话簿..");
		quit 	= CreatMenuItem("退出  Alt+F4");
		open.add(exits);
		open.add(creat);
		open.add(file);
		open.add(ivcf);
		open.addSeparator();
		open.add(del);
		open.addSeparator();
		open.add(quit);
		
		window	= new JMenu("窗口");
		cascade = CreatMenuItem("层叠所有窗口");
		tile 	= CreatMenuItem("平铺所有窗口");
		removeall=CreatMenuItem("关闭所有窗口");
		window.add(cascade);
		window.add(tile);
		window.addSeparator();
		window.add(removeall);
		
		lookandfeel = new JMenu("更换皮肤");
		swingfeel	= CreatMenuItem("Swing的皮肤"); 
		nativefeel	= CreatMenuItem("符合你系统的皮肤");
		lookandfeel.add(swingfeel);
		lookandfeel.add(nativefeel);
		lookandfeel.addSeparator();
		creatFeelMenuItem(lookandfeel);
		
		abouthelp = new JMenu("帮助");
		about = CreatMenuItem("关于 【电话簿】");
		help  = CreatMenuItem("帮助");
		abouthelp.add(help);
		abouthelp.addSeparator();
		abouthelp.add(about);
		
		menubar.add(open);
		menubar.add(window);
		menubar.add(lookandfeel);
		menubar.add(abouthelp);
		
		return menubar;
	}
	
	private JMenuItem CreatMenuItem(String name) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(ml);
		return item;
	}
	
	private void creatFeelMenuItem(JMenu feel) {
		JMenuItem[] jm = LookandfeelFactory.getLookandFeelMenuItem();
		for (int i=0; i<jm.length; ++i) {
			jm[i].addActionListener(ml);
			feel.add(jm[i]);
		}
	}
	
	private MenuListen ml = new MenuListen();
	
	private class MenuListen implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			final Object src = e.getSource();
			
			// 从文件打开
			if(src==file) {
				JFileChooser chooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("电话簿文档 *.tel", "tel");
				chooser.setFileFilter(filter);
				
				if ( chooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION  ) {
					String password = TableDataPack.DEFAULTPASSWORD;
					File f = chooser.getSelectedFile();
					if ( testFileOpened(f) ) return;
					
					try{
						new DecodeStream(f, password).close();
					}catch(Exception eee) {
						InputDialog input = new InputDialog(frame, "输入密码", true);
						if( input.getInput()==InputDialog.OK ) {
							password = input.getResult();
						}else{
							return;
						}
					}
					
					TableDataPack data = new TableDataPack(f);
					data.get().password = password;
					try{
						creatInframe(data);
					}catch(Exception ee){
						Tools.println(ee);
					}
				}
			}
			// 打开现有的
			else if(src==exits) {
				TelphoneBrowse tb = new TelphoneBrowse(frame, "打开一个电话簿..");
				File f = tb.getResult();
				if ( testFileOpened(f) ) return;
				if ( f!=null ) {
					TableDataPack data = new TableDataPack(f);
					data.get().password = tb.getPassword();
					
					try{
						creatInframe(data);
					}catch(Exception ee){
						Tools.println(ee);
					}
				}
			}
			// 导入VCF文件
			else if (src==ivcf) {
				JFileChooser chooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("Android电话簿文件 *.vcf", "vcf");
				chooser.setFileFilter(filter);
				
				if ( chooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION  ) {
					File f = chooser.getSelectedFile();
					if ( testFileOpened(f) ) return;
					
					try{
						VcfDataPack vcf = new VcfDataPack(f);
						creatInframe(vcf);
					}catch(Exception ee){
						Tools.println(ee);
					}
				}
			}
			// 删除现有的 
			else if(src==del) {
				TelphoneBrowse tb = 
					new TelphoneBrowse(frame, "删除一个电话簿!!",TelphoneBrowse.RIGHT);
				File f = tb.getResult();
				if ( testFileOpened(f) ) return;
				if ( f!=null ) {
					ValidateDialog confirm = 
						new ValidateDialog(frame, "真的要删除电话簿: "+tb.getName()+" ?" );
					if( confirm.getResult()==ValidateDialog.OK ) {
						if( f.delete() ) {
							Tools.message("已经删除电话本: "+tb.getName()+"\nlocal file: "+f.toString());
						}else{
							Tools.message("删除电话本: "+tb.getName()+" 失败..");
						}
					}
				}
			}
			// 新建
			else if(src==creat) {
				String password = null;
				InputDialog input = new InputDialog(frame, "新建电话簿密码", true);
				
				if( input.getInput()==InputDialog.OK ) {
					password = input.getResult();
					if( password.length()<1 ) {
						password = "jym";
						Tools.message("不使用密码.");
					}else{
						InputDialog input2 = new InputDialog(frame, "验证密码", true);
						if( input2.getInput()==InputDialog.OK ) {
							if( input2.getResult().compareTo(password)!=0 ) {
								Tools.message("密码验证错误.");
								return;
							}
						}else{
							Tools.message("取消新建文件.");
							return;
						}
					}
				}else{
					return;
				}

				TableDataPack data = TableDataPack.creatDeafultData();
				data.get().password = password;
				try{
					data.get().file.createNewFile();
					data.write(data.get());
					creatInframe(data);
				}catch(Exception ee){
					Tools.println(ee);
				}
			}
			// 平铺
			else if(src==tile) {
				removeClosedFrame();
				int cols = (int)Math.sqrt(inFrameCont);
				int rows = inFrameCont / cols;
				int extra = inFrameCont % cols;
				int w = jdp.getWidth() / cols;
				int h = jdp.getHeight() / rows ;
				int r = 0;
				int c = 0;
				for(int i=0; i<inFrameCont; ++i) {
					if( !inFrame[i].isIcon() ) {
						try {
							inFrame[i].setMaximum(false);
							inFrame[i].reshape(c*w, r*h, w, h);
							++r;
							if( r== rows ) {
								r=0;
								++c;
								if( c == cols-extra ) {
									++rows;
									h = getHeight() / rows;
								}
							}
						}catch(Exception eee){}
					}
				}
			}
			// 层叠
			else if(src==cascade) {
				removeClosedFrame();
				int x=0,y=0,
					w=(int)(dim.width*0.7f),
					h=(int)(dim.height*0.7f);
				
				for(int i=0; i<inFrameCont; ++i) {
					try{
						if( !inFrame[i].isIcon() ) {
							inFrame[i].setMaximum(false);
							inFrame[i].reshape(x, y, w, h);
							inFrame[i].setSelected(true);
							x = x<getWidth()-300 ? x+15 : 0;
							y = y<getHeight()-300? y+15 : 0;
						}
					}catch(Exception ee){}
				}
			}
			// 关闭所有
			else if( src==removeall ) {
				removeAllFrame();
			}
			else if( src==about ) {
				new AboutDialog(frame);
			}
			else if( src==help) {
				Tools.functionNotComplete();
			}
			// 退出
			else if(src==quit) {
				quit();
			}
			// 改变外观
			else if (src==swingfeel) {
				LookandfeelFactory.setSwingLookandFeel();
			}
			else if (src==nativefeel) {
				LookandfeelFactory.setNativeLookandFeel();
			}
			else if (src instanceof MItem) {
				String feelname = e.getSource().toString();
				LookandfeelFactory.setStanceFeel(feelname);
			}
		}
	}
	
	private void quit() {
		setVisible(false);
		LookandfeelFactory.saveConfig();
		removeAllFrame();
		System.exit(0);
	}
	
	private class WL extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			quit();
		}
		public void windowClosed(WindowEvent e) {
			quit();
		}
	}
	
	private class KL extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			if( ke.getKeyCode()==KeyEvent.VK_F1 ) {
				ActionEvent ae = new ActionEvent(exits, 0, null);
				ml.actionPerformed(ae);
			}
		}
	}
	
}
