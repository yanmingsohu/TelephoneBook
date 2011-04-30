package jym.vcf;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

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

		@Override
		public int getRowCount() {
			return item.getValues().length;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex==1) {
				return item.getValues()[columnIndex];
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}
}
