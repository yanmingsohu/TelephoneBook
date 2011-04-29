// CatfoOD 2011-4-29 下午02:51:54 yanming-sohu@sohu.com/@qq.com

package jym.vcf;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
		Data d = new Data();
		
		d.columnName 	= vcf.getColumnNames();
		d.columnCount	= vcf.getColumns().size();
		d.rowCount 		= vcf.getData().size();
		d.data 			= new Object[d.rowCount][d.columnCount];
		d.file 			= Tools.getRandFile();
		d.name 			= "导入的Android电话簿";
		d.password		= DEFAULTPASSWORD;
		
		List<Contact> list = vcf.getData();
		Iterator<Contact> itr = list.iterator();
		int row = 0;
		
		while (itr.hasNext()) {
			Contact c = itr.next();
			Object[] col_arr = new Object[d.columnCount];
			d.data[row++] = col_arr;
				
			for (int col=0; col<d.columnCount; ++col) {
				Item it = c.getItem(d.columnName[col]);
				
				if (it==null) continue;
				StringBuilder col_value = new StringBuilder();
				String[] value = it.getValues();
				for (int i=0; i<value.length; ++i) {
					col_value.append(value[i]).append(",");
				}
				col_arr[col] = col_value;
			}
		}
		
		return d;
	}

	@Override
	public void write(Data d) throws IOException {
		super.write(d);
	}
}
