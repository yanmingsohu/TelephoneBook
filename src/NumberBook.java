// CatfoOD 2008.2.24

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;

public class NumberBook extends JInternalFrame implements ActionListener {
	private JTable table;
	private TelTableModel telModel;
	private SortModel sortModel;
	private FilterModel filterModel;
	private TableDataPack tdp;
	private StateText stateText;
	
	private JPopupMenu popMenu = new JPopupMenu();
	private ML ml = new ML();
	
	public NumberBook(TableDataPack tdp) throws Exception {
		super("", true,true,true,true);

		setJMenuBar(creatMenu());
		creatPopMenu();
		addInternalFrameListener(new IFL());
		
		telModel = new TelTableModel(tdp);
		telModel.readData();
		setTitle(tdp.get().name);
		
		sortModel= new SortModel(telModel);
		filterModel=new FilterModel(sortModel);
		
		table = new JTable(filterModel);
		table.addMouseListener(ml);
		JScrollPane pane = new JScrollPane(table);
		pane.addMouseListener(ml);
		stateText = new StateText();
		setLayout(new BorderLayout());
		
		add(pane);
		add(stateText, BorderLayout.SOUTH);
		
		this.tdp = tdp;
	}
	
	/** 建立弹出式菜单 */
	private void creatPopMenu() {
		padd  = creatMenu("添加");
		pdel  = creatMenu("删除");
		pcopy = creatMenu("复制");
		ppaste= creatMenu("粘贴");
		psetcols = creatMenu("更改选择项目的内容..");
		
		popMenu.add(padd);
		popMenu.add(pcopy);
		popMenu.add(ppaste);
		popMenu.addSeparator();
		popMenu.add(pdel);
		popMenu.add(psetcols);
	}
	
	private JMenuItem padd;
	private JMenuItem pcopy;
	private JMenuItem ppaste;
	private JMenuItem pdel;
	private JMenuItem psetcols;
	
	/** 建立菜单条 */
	private JMenuBar creatMenu() {
		JMenuBar bar = new JMenuBar();
		
		file = new JMenu("文件");
		save = creatMenu("保存 Ctrl+S");
		changename=creatMenu("改变电话簿的名字..");
		changepw = creatMenu("改变密码..");
		quitnotsave = creatMenu("退出,不保存先前修改的数据");
		savequit = creatMenu("保存并退出 Ctrl+Q");
		file.add(save);
		file.addSeparator();
		file.add(changename);
		file.add(changepw);
		file.addSeparator();
		file.add(quitnotsave);
		file.add(savequit);
		
		edit = new JMenu("编辑");
		add  = creatMenu("添加");
		del  = creatMenu("删除");
		copy = creatMenu("复制");
		paste= creatMenu("粘贴");
		removeNull = creatMenu("移除空行");
		trim = creatMenu("移除所有项目的首尾空字符");
		find = creatMenu("查找.. Ctrl+F");
		findnext = creatMenu("查找下一个 F3");
		edit.add(add);
		edit.add(del);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(removeNull);
		edit.add(trim);
		edit.addSeparator();
		edit.add(find);
		edit.add(findnext);
		
		sort = new JMenu("排序/筛选");
		showall = creatMenu("显示全部");
		sortfor = new JMenu("按列排序");
			sortfor.addMenuListener(new MA());
		filter  = creatMenu("显示包含单词的条目");
		exclude = creatMenu("滤除包含单词的条目");
		sort.add(showall);
		sort.add(filter);
		sort.add(exclude);
		sort.addSeparator();
		sort.add(sortfor);
		
		rowItem   = new JMenu("列/项目");
		addRow    = creatMenu("添加一个项目列");
		removeRow = creatMenu("移除一个项目列");
		setcols   = creatMenu("更改选择项目的内容..");
		rowItem.add(addRow);
		rowItem.add(removeRow);
		rowItem.addSeparator();
		rowItem.add(setcols);
		
		
		bar.add(file);
		bar.add(edit);
		bar.add(sort);
		bar.add(rowItem);
		
		return bar;
	}
	
	private JMenu file;
	private JMenuItem save;
	private JMenuItem changepw;
	private JMenuItem changename;
	private JMenuItem savequit;
	private JMenuItem quitnotsave;
	
	private JMenu edit;
	private JMenuItem add;
	private JMenuItem del;
	private JMenuItem copy;
	private JMenuItem paste;
	private JMenuItem removeNull;
	private JMenuItem trim;
	private JMenuItem find;
	private JMenuItem findnext;
	
	private JMenu sort;
	private JMenuItem showall;
	private JMenu sortfor;
	private JMenuItem exclude;
	private JMenuItem filter;
	
	private JMenu rowItem;
	private JMenuItem addRow;
	private JMenuItem removeRow;
	private JMenuItem setcols;
	
	private JMenuItem creatMenu(String name) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(this);
		return item;
	}
	
	// 状态栏
	private class StateText extends JTextField implements Runnable {
		StateText() {
			this.setEditable(false);
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.setPriority(t.MIN_PRIORITY);
			t.start();
		}
		
		public boolean stop = false;
		
		public void run() {
			while( !stop ) {
				try {
					int[] s = getSelectedRows();
					String select = "";
					if( s!=null && s.length>0 ) {
						select = "\t选择了:"+s.length+"行.";
					}
					String state = 	" 总行数:"+telModel.getRowCount()+
									"\t显示的行数:"+filterModel.getRowCount();
					this.setText(state+select);
					Thread.sleep(800);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	// 排序菜单生成
	private class MA extends MenuListenerAdapter implements ActionListener {
		public void menuSelected(MenuEvent e) {
			if(e.getSource()==sortfor) {
				Component[] coms = sortfor.getComponents();
				for(int i=0; i<coms.length; ++i) {
					((JMenuItem)(coms[i])).removeActionListener(this);
				}
				sortfor.removeAll();
				int colcont = telModel.getColumnCount();
				for(int i=0; i<colcont; ++i) {
					JMenuItem it = new JMItem( telModel.getColumnName(i), i);
					it.addActionListener(this);
					sortfor.add(it);
				}
			}
		}
		
		private class JMItem extends JMenuItem {
			int index;
			JMItem(String name, int itemIndex) {
				super(name);
				index = itemIndex;
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if( e.getSource() instanceof JMItem ) {
				sortModel.sort( ((JMItem)e.getSource()).index );
			}
		}
	}
	
	// 窗口关闭事件
	private class IFL extends InternalFrameAdapter {
		public void internalFrameClosed(InternalFrameEvent e) {
			quit(true);
		}
	}
	
	/** 关闭窗口保存数据,退出 */
	public void quit() { quit(true); }
	
	/**
	 * 退出
	 * @param save - true,保存数据并退出. false,退出不保存数据
	 */
	public void quit(boolean save) {
		if(quited) return;
		
		popMenu.setVisible(false);
		setVisible(false);
		quited = true;
		stateText.stop=true;
		try{
			this.removeAll();
			table.setEnabled(false);
			
			if (save) {
				telModel.autoRemoveNullRows();
				telModel.saveData();
			}
			telModel.quit();
			//sortModel.quit();
			//filterModel.quit();
			
			table = null;
			telModel = null;
			sortModel = null;
			filterModel = null;
			tdp = null;
			stateText = null;
			popMenu = null;
			ml = null;
		}catch(Exception er){
			Tools.println(er);
		}
	}
	private boolean quited = false;
	
	/** 设置组件可见,不可见,如果已经退出,不做任何响应 */
	public void setVisible(boolean aFlag) {
		if(quited) return;
		super.setVisible(aFlag);
	}
	
	// 消息相应
	public void actionPerformed(ActionEvent e) {
		popMenu.setVisible(false);
		// 保存
		if( e.getSource()==save ) {
			try{
				telModel.saveData();
			}catch(Exception er){
				Tools.println(er);
			}
		}
		// 改变名字
		else if( e.getSource()==changename ) {
			InputDialog input = new InputDialog(null, "新的名字", false);
			input.setText(tdp.get().name);
			if( input.getInput()==input.OK ) {
				if( input.getResult().length()>1 ){
					tdp.get().name = input.getResult();
					setTitle(tdp.get().name);
				}else{
					Tools.message("名字必须多于1个字母.");
				}
			}
		}
		// 改变密码
		else if( e.getSource()==changepw ) {
			InputDialog input = new InputDialog(null, "新的密码", true);
			if( input.getInput()==input.OK ) {
				if( input.getResult().length()<1 ){
					Tools.message("密码已经被删除,不使用密码.");
					tdp.get().password = tdp.DEFAULTPASSWORD;
					return;
				}else{
					InputDialog rein = new InputDialog(null, "重新输入一遍", true);
					if( rein.getInput()==rein.OK ) {
						if( rein.getResult().compareTo( input.getResult() )==0 ) {
							tdp.get().password = rein.getResult();
							Tools.message("密码已经更改,下次登录请使用新的密码.");
							return;
						}else{
							Tools.message("两次输入的密码不同,请重试..");
							return;
						}
					}else{
						Tools.message("放弃更改密码");
						return;
					}
				}
			}
		}
		// 退出但是不保存数据
		else if( e.getSource()==quitnotsave) {
			quit(false);
		}
		// 保存退出
		else if( e.getSource()==savequit ) {
			quit(true);
		}
		// 添加
		else if( e.getSource()==add || e.getSource()==padd ) {
			telModel.addRows(5);
		}
		// 删除
		else if( e.getSource()==del || e.getSource()==pdel ) {
			telModel.removeRows( getSelectedRows() );
		}
		// 复制
		else if( e.getSource()==copy || e.getSource()==pcopy) {
			// {{添加代码}}
			Tools.functionNotComplete();
		}
		// 粘贴
		else if( e.getSource()==paste || e.getSource()==ppaste ) {
			// {{添加代码}}
			Tools.functionNotComplete();
		}
		// 移除空行
		else if( e.getSource()==removeNull ) {
			telModel.autoRemoveNullRows();
		}
		// 移除首尾空字符
		else if( e.getSource()==trim ) {
			telModel.trimAll();
		}
		// 查找
		else if( e.getSource()==find ) {
			// {{添加代码}}
			Tools.functionNotComplete();
		}
		// 查找下一个
		else if( e.getSource()==findnext ) {
			// {{添加代码}}
			Tools.functionNotComplete();
		}
		// 显示全部
		else if( e.getSource()==showall ) {
			filterModel.allDisplay();
		}
		// 过滤
		else if( e.getSource()==filter ) {
			InputDialog in = new InputDialog(null, "输入包含的单词");
			if( in.getInput()==in.OK ) {
				filterModel.IncludeWord( in.getResult() );
			}
		}
		//
		else if( e.getSource()==exclude ) {
			InputDialog in = new InputDialog(null, "输入要滤除的单词");
			if( in.getInput()==in.OK ) {
				filterModel.ExcludeWord( in.getResult() );
			}
		}
		// 添加列
		else if( e.getSource()==addRow ) {
			InputDialog in = new InputDialog(null, "新列的名字");
			if( in.getInput()==in.OK ) {
				String newcolumn = in.getResult().trim();
				if (newcolumn!=null && newcolumn.length()>0) {
					if ( !telModel.columnNameExits(newcolumn) ) {
						telModel.addColumn(newcolumn);
						table.setModel(telModel);
					} else {
						Tools.message("'"+newcolumn+"' 列已经存在，请使用不同的列名.");
					}
				} else {
					Tools.message("请输入有效的列名.");
				}
			}
		}
		// 删除列
		else if( e.getSource()==removeRow ) {
			if (telModel.getColumnCount()<=1) {
				Tools.message("有效的数据列只有一行，不能继续删除.");
				return;
			}

			ButtonDialog bdg = new ButtonDialog(null, "选择要删除的列");
			bdg.addButtons(telModel);
			
			if ( bdg.showButtonDialog()==bdg.OK ) {
				ValidateDialog vd = new ValidateDialog(null, "'"+
						bdg.getSelectedName()+ "' 列的所有数据都会丢失，确定么？");
				
				if (vd.getResult()==vd.OK) {
					telModel.removeColumn(bdg.getSelectedName());
					table.setModel(telModel);
				}
			}
		}
		// 更改选择项目的内容
		else if( e.getSource()==setcols || e.getSource()==psetcols ) {
			int[] select = getSelectedRows();
			if(select.length<1) {
				Tools.message("请先选择要更改的项目");
				return;
			}
			// 选择要更改的列
			ButtonDialog bdg = new ButtonDialog(null);
			bdg.addButtons(telModel);
			
			if ( bdg.showButtonDialog()==bdg.OK ) {
				int c = bdg.getSelected();
				// 判断是否要覆盖
				for(int x=0; x<select.length; ++x) {
					String ts = (String)telModel.getValueAt(select[x], c);
					if( ts!=null && ts.trim().length()>0 ) {
						ValidateDialog vd = 
							new ValidateDialog(null,"选择的项目有内容了,要覆盖么?");
						if (vd.getResult()==vd.OK) {
							break;
						}else{
							return;
						}
					}
				}
				// 输入列的内容
				InputDialog ind = new InputDialog(null, "新的内容");
				if ( ind.getInput()==ind.OK ) {
					String news = ind.getResult();
					for(int x=0; x<select.length; ++x) {
						telModel.setValueAt(news, select[x], c);
					}
					telModel.fireTableDataChanged();
					Tools.message("修改成功!");
				}else{
					Tools.message("取消修改..");
				}
			}else{
				return;
			}
		}
	}
	
	/**
	 * 返回选择的行
	 * @return int[] - 针对底层Table的映射
	 */
	private int[] getSelectedRows() {
		int[] s = table.getSelectedRows();
		for(int i=0; i<s.length; ++i) {
			s[i] = sortModel.getRow( filterModel.getRow(s[i]) );
		}
		return s;
	}
	
	/**
	 * 测试指定的文件是否已经被当前窗口打开
	 * @param f - 要测试的文件
	 * @return 已经打开返回true
	 */
	public boolean isFileOpened(File f) {
		if (quited) return false;
		return tdp.get().file.equals(f);
	}
	
	/**
	 * 重写超类的方法,窗口已经关闭返回true
	 */
	public boolean isClosed() {
		return quited;
	}
	
	private class ML extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if ( e.getButton()==e.BUTTON3 ) {
				popMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}else{
				popMenu.setVisible(false);
			}
		}
	}
}
