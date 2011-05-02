package jym.tel;
// CatfoOD 2008.2.27

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

import jym.lan.Lang;
import jym.wit.Tools;

public class ButtonDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 2303897754317744628L;
	
	public final int OK = 0;
	public final int CANCEL = 1;
	
	private JButton cancel;
	private int buttonCount = 0;
	private int result = CANCEL;
	private int selected = -1;
	private String selectName = "";
	private Panel buttonPan;
	
	
	public ButtonDialog(Frame f) {
		this(f, Lang.get("bu.howcol"));
	}
	
	public ButtonDialog(Frame f, String title) {
		super(f, title, true);
		setResizable(false);
		
		buttonPan = new Panel(new GridLayout(0,1));
		
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		cancel = new JButton(Lang.get("all.close"));
		cancel.addActionListener(this);
		pan.add(cancel);
		
		JScrollPane buttons = new JScrollPane(buttonPan);
		
		setLayout(new BorderLayout());
		add(buttons, BorderLayout.CENTER);
		add(pan, BorderLayout.SOUTH);
	}
	
	/**
	 * 添加一个按钮
	 * @param name - 按钮名
	 */
	public void addButton(String name) {
		sButton b = new sButton(name, buttonCount++);
		b.addActionListener(this);
		buttonPan.add(b);
	}
	
	/**
	 * 根据TableModel的列数据添加按钮
	 * @param model - javax.swing.TableModel
	 */
	public void addButtons(TableModel model) {
		int colcount = model.getColumnCount();
		if (colcount<1) throw new 
			IllegalArgumentException("Model can not have column.");
		
		for (int i=0; i<colcount; ++i) {
			addButton( model.getColumnName(i) );
		}
	}
	
	/**
	 * 显示并等待选择
	 * @return OK, CANCEL
	 */
	public int showButtonDialog() {
		int width = 300;
		int height= 600;
		setSize(width, height);
		
		Tools.center(this);
		setVisible(true);
		return result;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==cancel) {
			setVisible(false);
			result = CANCEL;
			return;
		}else if ( e.getSource() instanceof sButton ) {
			setVisible(false);
			sButton b = (sButton)e.getSource();
			selected = b.index;
			selectName = b.getText();
			result = OK;
			return;
		}
	}
	
	/**
	 * 返回选择的结果
	 * @return int
	 */
	public int getSelected() {
		return selected;
	}
	
	public String getSelectedName() {
		return selectName;
	}
	
	private class sButton extends JButton {
		private static final long serialVersionUID = 6878374562331161300L;
		public int index;
		
		sButton(String name, int i) {
			super(name);
			this.index = i;
			setSize(300, 55);
		}
	}
}
