package jym.tel;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public interface IEditorSet {
	public void set(Class<?> c, TableCellEditor editor);
	public void set(Class<?> c, TableCellRenderer rand);
}
