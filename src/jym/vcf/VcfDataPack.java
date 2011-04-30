// CatfoOD 2011-4-29 下午02:51:54 yanming-sohu@sohu.com/@qq.com

package jym.vcf;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

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

	@Override
	public Data read() throws IOException {
		Data d = get();
		
		d.columnName 	= vcf.getColumnNames();
		d.columnCount	= vcf.getColumns().size();
		d.rowCount 		= vcf.getData().size();
		d.data 			= new Object[d.rowCount][d.columnCount];
		d.name 			= "导入的Android电话簿 " + d.file;
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

	@Override
	public void write(Data d) throws IOException {
		throw new IOException("不能保存");
	}

	@Override
	public void setEditor(IEditorSet setter) {
		setter.set(Object.class, (TableCellEditor) new VcfEditor());
	//	setter.set(Object.class, (TableCellRenderer) new VcfEditor());
	}
	
	
	private class VcfEditor extends AbstractCellEditor 
			implements TableCellEditor, TableCellRenderer {

		private static final long serialVersionUID = -6778876718985812782L;
		private Object value;
		

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			return getTableCellEditorComponent(
					table, value, isSelected, row, column);
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			if (value==null) {
				Data d = get();
				
				for (int r=0; r<d.rowCount; ++r) {
					Object _v = d.data[r][column];

					if (_v!=null && (_v instanceof Item)) {
						value = ((Item)_v).copy();
						break;
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
			
			return new TextEditor();
		}

		@Override
		public Object getCellEditorValue() {
			return value;
		}
		

		private class TextEditor extends JTextField {
			private static final long serialVersionUID = -5136933729189376420L;

			TextEditor() {
				setText(String.valueOf(value));
				this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						value = getText();
					}
				});
			}
			
			TextEditor(final Item i) {
				setText(String.valueOf(i.getValues()[0]));
				this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						i.setValue(0, String.valueOf(value));
					}
				});
			}
		}
	}
	
}
