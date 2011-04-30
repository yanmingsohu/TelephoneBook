// CatfoOD 2011-4-29 下午03:11:14 yanming-sohu@sohu.com/@qq.com

package jym.vcf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jym.wit.Tools;


public class VcfFormat {

	private final static String START = "BEGIN:VCARD";
	private final static String END = "END:VCARD";
	private List<Contact> rows;
	private int line_c = 1;
	private Set<String> columns;
	private File file;

	
	public VcfFormat(File f) throws IOException {
		file = f;
		BufferedReader in = new BufferedReader(new FileReader(f));
		parse(in);
	}
	
	private void parse(BufferedReader in) throws IOException {
		String line = in.readLine();
		rows = new ArrayList<Contact>();
		columns = new HashSet<String>();
		
		while (line!=null) {
			if (line.equalsIgnoreCase(START)) {
				Contact c = new Contact(in);
				rows.add(c);
			}
			line = in.readLine();
			line_c++;
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
		
		
		public Contact (BufferedReader in) throws IOException {
			String line = in.readLine();
			line_c++;
			items = new ArrayList<Item>();
			map = new HashMap<String, Item>();
			
			while (line!=null && line.equalsIgnoreCase(END)==false) {
				Item i = new Item(line);
				items.add(i);
				map.put(i.getName(), i);
				
				line = in.readLine();
				line_c++;
			}
		}
		
		public List<Item> getItems() {
			return items;
		}
		
		public Item getItem(String name) {
			return map.get(name);
		}
	}
	
	
	public class Item {
		
		private String[] values;
		private String name;
		private Map<String, String> props;
		private String type;
		
		
		private Item(String line) throws IOException {
			String[] _t = line.split(":");
			if (_t.length!=2) {
				throw new IOException("语法错误,没有找到':'分隔符,在第" + line_c + "行");
			}
			set(_t[0], _t[1]);
		}
		
		private Item() {}
		
		private void set(String prop, String value) {
			values = value.split(";");
			
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
			encode();
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

		private void encode() {
			String encoding = props.get("ENCODING");
			if ("QUOTED-PRINTABLE".equalsIgnoreCase(encoding)) {
				String charset = props.get("CHARSET");
				if (charset==null) {
					charset = "UTF-8";
				}
				
				for (int i=0; i<values.length; ++i) {
					values[i] = quoted2string(values[i], charset);
				}
			}
		}
		
		private String quoted2string(String str, String charset) {
			String[] _t = str.split("=");
			byte[] _b = new byte[_t.length-1];
			
			for (int i=1; i<_t.length; ++i) {
				_b[i-1] = (byte) Integer.parseInt(_t[i], 16);
			}
			
			try {
				return new String(_b, charset);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		
		public String toString() {
			StringBuilder value = new StringBuilder();
			
			for (int i=0; i<values.length; ++i) {
				if (Tools.notNull(values[i]))
					value.append(values[i]).append(",");
			}
			
			return value.toString();
		}
		
		public Item copy() {
			Item i = new Item();
			i.name = name;
			i.type = type;
			i.props = props;
			i.values = new String[values.length];
			return i;
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
