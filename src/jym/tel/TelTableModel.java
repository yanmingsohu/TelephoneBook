package jym.tel;
// CatfoOD 2008.2.27

import java.io.IOException;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import jym.tel.TableDataPack.Data;

/**
 * 表格内核模型
 */
public class TelTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1365614389441957465L;

	private TableDataPack tdp;
	private Data d;
	
	
	public TelTableModel(TableDataPack tdp) {
		this.tdp = tdp;
		d = tdp.get();
	}
	
	/** 列的数量 */
	public int getColumnCount() {
		return d.columnCount;
	}

	/** 索引列的名称 */
	public String getColumnName(int columnIndex) {
		return d.columnName[columnIndex];
	}
	
	/** 指定的列名是否存在 */
	public boolean columnNameExits(String columnname) {
		columnname = columnname.trim();
		for (int i=0; i<d.columnName.length; ++i) {
			if (d.columnName[i].compareToIgnoreCase(columnname)==0) 
				return true;
		}
		return false;
	}
	
	/** 列的名称索引 */
	public int getColumnIndex(String CName) {
		int i=0;
		boolean finded = false;
		
		for(i=0; i<d.columnCount; ++i) {
			if( d.columnName[i].compareToIgnoreCase(CName)==0 ) {
				finded=true;
				break;
			}
		}
		return finded? i: -1;
	}
	
	/** 行的数量 */
	public int getRowCount() {
		return d.rowCount;
	}

	/** 得到 行/列 的数据 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return d.data[rowIndex][columnIndex];
	}
	
	/** 读取数据 */
	public void readData() throws IOException {
		d = tdp.read();
	}
	
	/** 保存数据 */
	public void saveData() throws IOException {
		tdp.write(d);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	/** 改变 行/列 的数据 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		d.data[rowIndex][columnIndex] = aValue;
	}
	
	/** 添加行 */
	public void addRows(int crow) {
		Object[][] newData = new Object[d.rowCount+crow][d.columnCount];
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				newData[r][c] = d.data[r][c];
			}
		}
		
		for (int r=d.rowCount; r<d.rowCount+crow; ++r) {
			newData[r] = tdp.addRow(r);
		}
		
		d.data = newData;
		d.rowCount += crow;
		
		fireTableDataChanged();
	}
	
	/** 移除选择的行 */
	public void removeRows(int[] rows) {
		Object[][] newData = new Object[d.rowCount-rows.length][d.columnCount];
		int new_r = 0;
		for(int r=0; r<d.rowCount; ++r) {
			if( includeNum(rows, r) ) {
				tdp.removeRow(r);
				continue;
			}
			for(int c=0; c<d.columnCount; ++c) {
				newData[new_r][c] = d.data[r][c];
			}
			++new_r;
		}
		d.data = newData;
		d.rowCount = new_r;
		
		fireTableDataChanged();
	}
	
	/** arr[] 中包含 num 返回真,否则为假 */
	private boolean includeNum(int[] arr, int num) {
		for(int i=0; i<arr.length; ++i) {
			if(arr[i]==num) return true;
		}
		return false;
	}
	
	/** 添加列,客户保证参数的有效性 */
	public void addColumn(String name) {
		d.columnName = Arrays.copyOf(d.columnName, d.columnName.length + 1);
		d.columnName[d.columnName.length-1] = name;
		
		Object[][] newdate = new Object[d.rowCount][d.data[0].length+1];
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				newdate[r][c] = d.data[r][c];
			}
			newdate[r][newdate[0].length-1] = null;
		}
		d.data = newdate;
		d.columnCount = d.columnName.length;
		
		fireTableStructureChanged();
	}
	
	/** 移除列，客户保证参数的有效性 */
	public void removeColumn(String name) {
		final int deletecol = getColumnIndex(name);
		if (deletecol<0) return;
		--d.columnCount;
		
		String[] newcolname = new String[d.columnName.length-1];
		int oldindex = 0;
		for (int i=0; i<newcolname.length; ++i) {
			if (deletecol == oldindex) {
				++oldindex;
			}
			newcolname[i] = d.columnName[oldindex];
			++oldindex;
		}
		d.columnName = newcolname;
		
		Object[][] newdate = new Object[d.rowCount][d.columnCount];
		for(int r=0; r<d.rowCount; ++r) {
			oldindex = 0;
			for(int c=0; c<d.columnCount; ++c) {
				if (deletecol == oldindex) {
					++oldindex;
				}
				newdate[r][c] = d.data[r][oldindex];
				++oldindex;
			}
		}
		d.data = newdate;
		
		fireTableStructureChanged();
	}
	
	@SuppressWarnings("unused")
	private void printTable() {
		for (int i=0; i<d.columnName.length; ++i) {
			System.out.print(" "+ d.columnName[i]+"\t");
		}System.out.println();
		
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				System.out.print(" "+ d.data[r][c] +"\t");
			}
			System.out.println();
		}
	}
	
	/** 移除空行 */
	public void autoRemoveNullRows() {
		int[] deleteLine = new int[d.rowCount];
		int deleteIndex = 0;

		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				if(d.data[r][c]==null || 
						d.data[r][c].toString().trim().length()<1) 
				{
					if(c==d.columnCount-1) deleteLine[deleteIndex++] = r;
				}else{
					break;
				}
			}
		}
		
		int[] delRow = new int[deleteIndex];
		for(int i=0; i<deleteIndex; ++i) {
			delRow[i] = deleteLine[i];
		}
		
		removeRows(delRow);
	}
	
	/** 移除所有项目的首尾空字符 */
	public void trimAll() {
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				if( d.data[r][c] instanceof String ) {
					d.data[r][c] = ((String)d.data[r][c]).trim();
				}
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (d.data!=null && d.data[0]!=null && d.data[0][columnIndex]!=null) {
			return d.data[0][columnIndex].getClass();
		} else {
			return Object.class;
		}
	}

	/** 清空所有对象,释放内存 */
	public void quit() {
		tdp.quit();
		d = null;
	}
}
