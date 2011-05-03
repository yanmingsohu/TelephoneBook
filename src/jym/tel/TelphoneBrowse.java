package jym.tel;
// CatfoOD 2008.2.25

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import jym.img.icon1_gif;
import jym.img.icon2_gif;
import jym.img.icon3_gif;
import jym.lan.Lang;
import jym.wit.InputDialog;
import jym.wit.Tools;

public class TelphoneBrowse extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -9120766113929166065L;
	
	private final int W = 100;
	private final int H = 100;
	private File result = null;
	private String password = null;
	private String name = null;
	
	/** Frame的左边 */
	public static final int LIFT = 0;
	/** Frame的右边 */
	public static final int RIGHT = 1;
	
	
	/**
	 * 一个文件选择对话框, 默认在Frame的左边
	 * @param f - 上层Frame
	 * @param title - 标题String
	 */
	public TelphoneBrowse(Frame f, String title) {
		this(f, title, LIFT); 
	}
	
	/**
	 * 一个文件选择对话框
	 * @param f - 上层Frame
	 * @param title - 标题String
	 * @param position - 位置 LIFT,RIGHT
	 */
	public TelphoneBrowse(Frame f, String title, int position) {
		super(f, title, true);
		GridLayout manager = new GridLayout(0,1);
		manager.setVgap(0);
		manager.setHgap(0);
		this.setLayout(manager);
		
		File file = new File(".");
		File[] filelist = file.listFiles();
		int count = 0;
		for(int i=0; i<filelist.length; ++i) {
			if(filelist[i].isFile()) {
				try{
					String name = DecodeStream.getBookName(filelist[i]);
					creatButton(name, filelist[i]);
					count++;
				}catch(Exception e){}
			}
		}
		int y;
		if(position == RIGHT) {
			y = f.getX() + f.getWidth() -W*2 - 10;
		}else{
			y = f.getX()+10;
		}
		this.setBounds(y, f.getY()+55, W*2, H*count);
		if( count>0 ) {
			this.setVisible(true);
		}else{
			Tools.message(Lang.get("tel.brow.nobook"));
		}
	}

	private void creatButton(String name, File file) {
		fileButton bu = new fileButton(name, file);
		bu.addActionListener(this);
		add(bu);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if( o instanceof fileButton ) {
			File result = ((fileButton)o).getFile();
			String name = ((fileButton)o).getActionCommand();
			try{
				password = TableDataPack.DEFAULTPASSWORD;
				new DecodeStream(result, password).close();
				this.setVisible(false);
				this.result = result;
				this.name = name;
				return;
			}catch(Exception eee) {
				InputDialog input = new InputDialog(null, Lang.get("tel.brow.inpass"), true);
				if( input.getInput()==InputDialog.OK ) {
					try{
						password = input.getResult();
						new DecodeStream(result, password).close();
						this.setVisible(false);
						this.result = result;
						this.name = name;
						return;
					}catch(Exception ee){
						Tools.message(Lang.get("tel.brow.passwr"));
					}
				}
			}
		}
	}
	
	/**
	 * 返回选择的文件,这个文件已经确认密码,和名字
	 * @return File
	 */
	public File getResult() {
		return result;
	}
	public String getPassword() {
		return password;
	}
	public String getName() {
		return name;
	}
	
	private class fileButton extends JButton {
		private static final long serialVersionUID = -4190542227252364014L;
		private File file;
		
		
		public fileButton(String name, File file) {
			super(name, icon[iconIndex++]);
			if(iconIndex>=3) iconIndex=0;
			this.file = file;
			this.setSize(W, H);
		}
		public File getFile() {
			return file;
		}
	}
	
	private ImageIcon[] icon = {
			new ImageIcon(icon1_gif.getImage()),
			new ImageIcon(icon2_gif.getImage()),
			new ImageIcon(icon3_gif.getImage()),
	};
	
	private int iconIndex = 0;
}
