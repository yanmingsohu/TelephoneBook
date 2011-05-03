package jym.vcf;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jym.lan.Lang;
import jym.vcf.VcfFormat.Item;

public class MutiColumnEditor extends JPanel {

	private static final long serialVersionUID = 8834119795984093204L;
	private Item item;
	
	
	public MutiColumnEditor(Item item) {
		this.item = item;
		setLayout(new BorderLayout());
		
		JTable table = new JTable(new Model());
		add(table);
	}
	
	
	private class Model extends AbstractTableModel {
		
		private static final long serialVersionUID = -59800700186230105L;
		private String[] names = new String[0];

		public int getRowCount() {
			return item.getValues().length;
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex==1) {
				return item.getValues()[columnIndex];
			} else {
				if (columnIndex<names.length) {
					return names[columnIndex];
				} else {
					return Lang.get("def.undefine");
				}
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return rowIndex == 1;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			item.setValue(rowIndex, (String) aValue);
		}
	}
}
