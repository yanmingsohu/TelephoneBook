// CatfoOD 2011-4-29 下午02:51:54 yanming-sohu@sohu.com/@qq.com

package jym.vcf;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jym.lan.Lang;
import jym.tel.IEditorSet;
import jym.tel.TableDataPack;
import jym.vcf.VcfFormat.Contact;
import jym.vcf.VcfFormat.Item;
import jym.wit.Tools;


public class VcfDataPack extends TableDataPack {
	
	private VcfFormat vcf;
	
	
	public VcfDataPack(File f) throws IOException {
		Tools.p(f);
		get().file = f;
		vcf = new VcfFormat(f);
	}

	public Data read() throws IOException {
		Data d = get();
		
		d.columnName 	= vcf.getColumnNames();
		d.columnCount	= vcf.getColumns().size();
		d.rowCount 		= vcf.getData().size();
		d.data 			= new Object[d.rowCount][d.columnCount];
		d.name 			= Lang.get("def.android.name") + " " + d.file;
		d.password		= DEFAULTPASSWORD;
		
		List<Contact> list = vcf.getData();
		Iterator<Contact> itr = list.iterator();
		int row = 0;
		
		while (itr.hasNext()) {
			Contact c = itr.next();
			Object[] col_arr = new Object[d.columnCount];
			d.data[row++] = col_arr;
				
			for (int col=0; col<d.columnCount; ++col) {
				col_arr[col] = c.getItem(d.columnName[col]);
			}
		}
		
		return d;
	}

	public void write(Data d) throws IOException {
		FileWriter fileout = new FileWriter(d.file);
		BufferedWriter out = new BufferedWriter(fileout);
		vcf.out(out);
		out.flush();
		out.close();
	}

	public void setEditor(IEditorSet setter) {
		setter.set(Object.class, (TableCellEditor) new VcfEditor());
	//	setter.set(Object.class, (TableCellRenderer) new VcfEditor());
	}
	
	public Object[] addRow(int rowIdx) {
		final int cIdx = 0;
		Data d = get();
		Item item = null;
		Object[] row = new Object[d.columnCount];
		
		for (int r=0; r<d.rowCount; ++r) {
			Object o = d.data[r][cIdx];
			if (o instanceof Item) {
				item = (Item) o;
				break;
			}
		}
		
		if (item!=null) {
			Contact c = vcf.createContact();
			row[cIdx] = item.copy(c);
		}
		
		return row;
	}

	public void removeRow(int rowIdx) {
		Data d = get();
		Object[] row = d.data[rowIdx];
		
		for (int c=0; c<d.columnCount; ++c) {
			Object o = row[c];
			if (o instanceof Item) {
				((Item)o).removeContact();
			}
		}
	}


	private class VcfEditor extends AbstractCellEditor 
			implements TableCellEditor, TableCellRenderer {

		private static final long serialVersionUID = -6778876718985812782L;
		private Object value;
		

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			return getTableCellEditorComponent(
					table, value, isSelected, row, column);
		}
		
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			if (value==null) {
				Item rowItem = null;
				
				for (int c=table.getColumnCount()-1; c>=0; --c) {
					Object _v = table.getValueAt(row, c);
					if (_v!=null && (_v instanceof Item)) {
						rowItem = (Item) _v;
						break;
					}
				}
				
				if (rowItem!=null) {
					for (int r=table.getRowCount()-1; r>=0; --r) {
						Object _v = table.getValueAt(r, column);
	
						if (_v!=null && (_v instanceof Item)) {
							value = ((Item)_v).copy(rowItem);
							break;
						}
					}
				}
			}

			this.value = value;
			
			if (value instanceof Item) {
				Item item = (Item) value;

				if (item.getValues().length==1) {
					return new TextEditor(item);
				} else {
					return new MutiColumnEditor(item);
				}
			} 
			
			return new JLabel("不支持");
		}

		public Object getCellEditorValue() {
			return value;
		}
		

		private class TextEditor extends JTextField {
			private static final long serialVersionUID = -5136933729189376420L;
			
			TextEditor(final Item i) {
				setBorder(null);
				String v = i.getValues()[0];
				if (v!=null) setText(v);
				
				this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						i.setValue(0, getText());
					}
				});
			}
		}
	}
	
}
