// CatfoOD 2011-4-29 下午03:11:14 yanming-sohu@sohu.com/@qq.com

package jym.vcf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jym.wit.Tools;


public class VcfFormat {

	private final static String START = "BEGIN:VCARD";
	private final static String END = "END:VCARD";
	private List<Contact> rows;
	private Set<String> columns;
	private int line_c = 1;
	private File file;

	
	public VcfFormat(File _file) throws IOException {
		file = _file;
		BufferedReader in = new BufferedReader(new FileReader(_file));
		parse(in);
	}
	
	private void parse(BufferedReader in) throws IOException {
		String line = in.readLine();
		rows = new ArrayList<Contact>();
		columns = new TreeSet<String>(new Type.Sorter());
		
		while (line!=null) {
			if (line.equalsIgnoreCase(START)) {
				Contact c = new Contact(in);
				rows.add(c);
			}
			line = in.readLine();
			line_c++;
		}
	}
	
	public void out(Appendable out) throws IOException {
		Iterator<Contact> itr = rows.iterator();
		while (itr.hasNext()) {
			itr.next().out(out);
		}
	}
	
	public List<Contact> getData() {
		return rows;
	}
	
	public Set<String> getColumns() {
		return columns;
	}
	
	public String[] getColumnNames() {
		return columns.toArray(new String[0]);
	}
	
	public File getFile() {
		return file;
	}
	
	
	public class Contact {
		
		private List<Item> items;
		private Map<String, Item> map;
		
		
		public Contact(BufferedReader in) throws IOException {
			String line = in.readLine();
			line_c++;
			items = new ArrayList<Item>();
			map = new HashMap<String, Item>();
			
			while (line!=null && line.equalsIgnoreCase(END)==false) {
				Item i = new Item(line);
				addItem(i);
				
				line = in.readLine();
				line_c++;
			}
		}
		
		private void addItem(Item i) {
			i._c = this;
			items.add(i);
			map.put(i.getName(), i);
		}
		
		public List<Item> getItems() {
			return items;
		}
		
		public Item getItem(String name) {
			return map.get(name);
		}
		
		public void out(Appendable out) throws IOException {
			Iterator<Item> itr = items.iterator();
			
			if (itr.hasNext()) {
				out.append(START).append('\n');
				while (itr.hasNext()) {
					Item item = itr.next();
					item.out(out);
				}
				out.append(END).append('\n');
			}
		}
	}
	
	
	public class Item {
		
		private String[] values;
		private String name;
		private Map<String, String> props;
		private String type;
		private Contact _c;
		

		private Item() {}
		
		private Item(String line) throws IOException {
			String[] _t = line.split(":");
			if (_t.length!=2) {
				throw new IOException("语法错误,没有找到':'分隔符,在第" + line_c + "行");
			}
			set(_t[0], _t[1]);
		}
		
		private void set(String prop, String value) {
			values = value.split(";", -1);
			
			String[] _n = prop.split(";");
			props = new HashMap<String, String>();
			
			if (_n.length>1) {
				for (int i=1; i<_n.length; ++i) {
					String p = _n[i];
					int sp = p.indexOf('=');
					
					if (sp>=0) {
						if (sp<p.length()-2) {
							props.put(p.substring(0, sp), p.substring(sp+1));
							continue;
						}
					}
					props.put(p, null);
				}
			}
			
			initName(_n[0]);
			QuotedCoder.decode(props, values);
		}
		
		private void initName(String _name) {
			String cname = null;
			type = _name;
			_name = Type.get(_name);
			
			Iterator<String> itr = props.keySet().iterator();
			while (itr.hasNext()) {
				cname = Type.get(itr.next());
				if (cname!=null) {
					break;
				}
			}
			
			name = cname==null ? _name : cname + _name;
			if (name!=null) columns.add(name);
		}
		
		public String toString() {
			StringBuilder value = new StringBuilder();
			
			for (int i=0; i<values.length; ++i) {
				if (Tools.notNull(values[i])) {
					value.append(values[i]);
					if (i+1 < values.length) {
						value.append(", ");
					}
				}
			}
			
			return value.toString();
		}
		
		/**
		 * 复制当前项目,并把新项目与联系人关联
		 * @param rowItem - 同一行的另一个Item
		 * @return
		 */
		public Item copy(Item rowItem) {
			if (rowItem._c==null) {
				throw new NullPointerException("rowItem未指定Contact");
			}

			Item i = new Item();
			i.name = name;
			i.type = type;
			i.props = props;
			i.values = new String[values.length];
			
			rowItem._c.addItem(i);
			return i;
		}
		
		public void out(Appendable out) throws IOException {
			if (cannotSave()) return;
			
			String[] c_values = QuotedCoder.encode(props, values);
			out.append(type);
			
			Iterator<String> itr = props.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String value = props.get(key);
				out.append(';');
				out.append(key);
				if (value!=null) {
					out.append('=');
					out.append(value);
				}
			}
			
			out.append(':');
			delNull(c_values);
			
			if (c_values.length>0) {
				out.append(c_values[0]);
				for (int i=1; i<c_values.length; ++i) {
					out.append(';');
					out.append(c_values[i]);
				}
			}
			
			out.append('\n');
		}
		
		private boolean cannotSave() {
			for (int i=0; i<values.length; ++i) {
				if (values[i]!=null) {
					return false;
				}
			}
			return true;
		}
		
		private void delNull(String[] s) {
			for (int i=0; i<s.length; ++i) {
				if (s[i]==null) {
					s[i] = "";
				}
			}
		}

		public final String[] getValues() {
			return values;
		}
		
		public final void setValue(int index, String value) {
			values[index] = value;
		}

		public final String getName() {
			return name;
		}

		public final Map<String, String> getProps() {
			return props;
		}

		public String getType() {
			return type;
		}
	}
}
