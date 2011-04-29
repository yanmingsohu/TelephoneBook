// CatfoOD 2011-4-29 ÏÂÎç02:34:08 yanming-sohu@sohu.com/@qq.com

package jym.wit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class MItem extends JMenuItem implements ActionListener {
	
	private static final long serialVersionUID = 223998456235494786L;
	private static String select=null;
	
	private String feelname;
	private final String dot = "¡¤";
	private MItemGroup group = null;
	
	
	public MItem(String name) {
		super(name);
		feelname = name;
		this.addActionListener(this);
	}
	
	public String toString() {
		return feelname;
	}
	
	protected void inform() {
		this.setText(feelname);
	}

	public void actionPerformed(ActionEvent arg0) {
		this.setText(dot+feelname);
		select = feelname;
		if (group!=null) {
			group.inform(this);
		}
	}
	
	protected void addGroup(MItemGroup g) {
		group = g;
	}
	
	public static String getSelectFeelName() {
		return select;
	}
}