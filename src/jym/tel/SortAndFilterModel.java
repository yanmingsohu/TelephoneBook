package jym.tel;
// CatfoOD 2008.2.26

import java.util.Arrays;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


/**
 * 表格排序模型
 */
class SortModel extends AbstractTableModel implements TableModelListener {
	
	private static final long serialVersionUID = 6981192206330752408L;
	private AbstractTableModel base;
	private Row[] rows;
	private int sortColumn = 0;
	
	
	public SortModel(AbstractTableModel tablemodel) {
		base = tablemodel;
		base.addTableModelListener(this);
		dataChange();
	}
	
	private void dataChange() {
		rows = new Row[base.getRowCount()];
		for(int i=0; i<rows.length; ++i) {
			rows[i] = new Row(i);
		}
		sort(sortColumn);
	}
	
	public int getColumnCount() {
		return base.getColumnCount();
	}

	public int getRowCount() {
		return base.getRowCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return base.getValueAt(rows[rowIndex].index, columnIndex);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	public String getColumnName(int columnIndex) {
		return base.getColumnName(columnIndex);
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		base.setValueAt(aValue, rows[rowIndex].index, columnIndex);
	}
	
	/** 以c列 为索引排序 */
	public void sort(int c) {
		sortColumn = c;
		Arrays.sort(rows);
		fireTableStructureChanged();
	}
	
	private class Row implements Comparable<Object> {
		public int index;
		public Row(int c) {index=c;}
		
		public int compareTo(Object o) {
			Row r = (Row)o;
			Object a = base.getValueAt(index, sortColumn);
			Object b = base.getValueAt(r.index, sortColumn);

			if(isNull(a)) {
				if(isNull(b)) return 0;
				else return 1;
			}
			if(isNull(b)) {
				return -1;
			}
			
			return a.toString().compareTo(b.toString());
		}
		boolean isNull(Object o) {
			return o==null || o.toString()=="" || o.toString()=="null";
		}
	}
	
	/** 返回底层数据模型 选择的行 */
	public int getRow(int crow) {
		return rows[crow].index;
	}

	public void tableChanged(TableModelEvent e) {
		dataChange();
	}
	
	public void quit() {
		base.removeTableModelListener(this);
		this.base = null;
		this.rows = new Row[0];
		this.sortColumn = -1;
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return base.getColumnClass(arg0);
	}
}

/**
 * 表格过滤模型
 */
class FilterModel extends AbstractTableModel implements TableModelListener {
	
	private static final long serialVersionUID = -2243887060202383321L;
	private final int ALL = 0;
	private final int INCLUDE = 1;
	private final int EXCLUDE = 2;
	
	private AbstractTableModel base;
	private int[] rows;
	private int state = ALL;
	private String oldword;
	
	
	public FilterModel(AbstractTableModel sortmodel) {
		base = sortmodel;
		base.addTableModelListener(this);
		allDisplay();
	}
	
	private void reInit() {
		rows = new int[base.getRowCount()];
		for(int i=0; i<rows.length; ++i) {
			rows[i] = i;
		}
	}
	
	/**
	 * 只显示包含的单词
	 */
	public void IncludeWord(String word) {
		rows = filterWord(word, INCLUDE);
		fireTableDataChanged();
	}
	
	/**
	 * 不显示包含的单词
	 */
	public void ExcludeWord(String word) {
		rows = filterWord(word, EXCLUDE);
		fireTableDataChanged();
	}

	/**
	 * 显示全部项目
	 */
	public void allDisplay() {
		state = ALL;
		reInit();
		fireTableDataChanged();
	}
	
	private int[] filterWord(String word, int mode) {
		reInit();
		word = word.trim();
		oldword = word;
		state = mode;
		
		int[] in = new int[rows.length];
		int newr = 0;
		
		for(int r=0; r<in.length; ++r) {
			for(int c=base.getColumnCount()-1; c>=0; --c) {
				String word1 = String.valueOf( base.getValueAt(rows[r], c) );
				
				if ( word1!=null ) {
					if ( mode==INCLUDE && word1.lastIndexOf(word)!=-1 ) {
						in[newr++] = rows[r];
						break;
					}else if ( mode==EXCLUDE ) {
						if (word1.lastIndexOf(word)!=-1) {
							break;
						}
					}
				}else if( mode==INCLUDE && c==0 ){
					in[newr++] = rows[r];
				}
				
				if ( mode==EXCLUDE && c==0 ) {
					in[newr++] = rows[r];
				}
			}
		}
		int[] newrows = new int[newr];
		for(int i=0; i<newr; ++i) {
			newrows[i] = in[i];
		}
		return newrows;
	}
	
	public int getColumnCount() {
		return base.getColumnCount();
	}

	public int getRowCount() {
		return rows.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return base.getValueAt(rows[rowIndex], columnIndex);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	public String getColumnName(int columnIndex) {
		return base.getColumnName(columnIndex);
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		base.setValueAt(aValue, rows[rowIndex], columnIndex);
	}
	
	/** 返回底层数据模型 选择的行 */
	public int getRow(int crow) {
		return rows[crow];
	}

	public void tableChanged(TableModelEvent e) {
		switch(state) {
		case EXCLUDE:
			ExcludeWord(oldword);
			break;
		case INCLUDE:
			IncludeWord(oldword);
			break;
		default:
			allDisplay();
		}
	}

	public Class<?> getColumnClass(int arg0) {
		return base.getColumnClass(arg0);
	}
	
	public void quit() {
		base.removeTableModelListener(this);
		this.rows = new int[0];
		this.base = null;
	}
}