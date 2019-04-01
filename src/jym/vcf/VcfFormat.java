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

import jym.lan.Lang;
import jym.wit.Base64;
import jym.wit.Tools;


/**
 * http://www.ietf.org/rfc/rfc2426.txt
 */
public class VcfFormat {
	
	private final static int MAX_LINE_LEN = 9999; 
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
		rows = new ArrayList<Contact>();
		columns = new TreeSet<String>(new Type.Sorter());

		while (true) {
			String line = in.readLine();
			line_c++;
			if (line==null) break;
			
			if (line.equalsIgnoreCase(START)) {
				Contact c = new Contact(in);
				rows.add(c);
			}
		}
	}
	
	public Contact createContact() {
		Contact c = new Contact();
		rows.add(c);
		return c;
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
		
		
		private Contact() {
			items = new ArrayList<Item>();
			map = new HashMap<String, Item>();
		}
		
		private Contact(BufferedReader in) throws IOException {
			this();
			StringBuffer msg = new StringBuffer();
			
			while (true) {
				String line = in.readLine();
				line_c++;
				
				if (line==null || line.equalsIgnoreCase(END))
					break;
				
				try {
					Item i = new Item(line, in);
					addItem(i);
				} catch(Exception e) {
					msg.append(e.getMessage());
					msg.append('\n');
				}
			}
			
			if (msg.length() > 0) {
				Tools.message(msg.toString());
			}
		}
		
		public void addItem(Item i) {
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
		private byte[] img;
		

		private Item() {}
		
		private Item(String line, BufferedReader in) throws IOException {
			String[] _t = line.split(":");
			if (_t.length!=2) {
				throw new IOException(Lang.get("error.vcfline", line_c) + ":" + line);
			}
			
			setProp(_t[0]);
			
			/* 数据可能是多行的 */
			if (!isImage()) {
				StringBuilder buffer = new StringBuilder(_t[1]);
				
				while (true) {
					in.mark(MAX_LINE_LEN);
					String tmp = in.readLine();
					if (tmp.indexOf(':')>=0) {
						in.reset();
						break;
					}
					line_c++;
					
					if (tmp==null || tmp.length()<=0) break;
					tmp = tmp.trim();
					buffer.append(tmp);
				}
				
				_t[1] = buffer.toString();
			}
			
			setValue(_t[1]);
			
			if (isImage()) {
				readImg(_t[1], in);
			}
		}
		
		private void readImg(String line, BufferedReader in) throws IOException {
			String enc = props.get("ENCODING");
			if (enc==null || !enc.equalsIgnoreCase("BASE64")) {
				throw new IOException("PHOTO unsupport ENCODING:" + enc);
			}
			
			StringBuilder buff = new StringBuilder(line);
			
			while (true) {
				line = in.readLine();
				line_c++;
				if (line==null || line.length()<=0) break;
				buff.append(line.trim());
			}
			
			img = Base64.decode(buff.toString());
		}
		
		/** 解析数据 */
		private void setValue(String value) {
			values = value.split(";", -1);
			QuotedCoder.decode(props, values);
		}
		
		/** 解析属性 */
		private void setProp(String prop) {
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
				throw new NullPointerException(Lang.get("error.vcf.copyitem"));
			}

			return copy(rowItem._c);
		}
		
		public Item copy(Contact c) {
			Item i = new Item();
			i.name = name;
			i.type = type;
			i.props = props;
			i.values = new String[values.length];
			c.addItem(i);
			return i;
		}
		
		/** 删除与当前项目关联的联系人信息 */
		public void removeContact() {
			rows.remove(this._c);
		}
		
		private void outProp(Appendable out) throws IOException {
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
		}
		
		private void outImage(Appendable out) throws IOException {
			int a = 48;
			String en = new String(Base64.encode(img));
			out.append(sub(en, 0, a)).append('\n');
			
			while (a<en.length()) {
				out.append(' ').append(sub(en, a, a+73)).append('\n');
				a += 73;
			}
		}
		
		private void outValues(Appendable out, String[] _vs) throws IOException {
			if (_vs.length>0) {
				out.append(_vs[0]);
				for (int i=1; i<_vs.length; ++i) {
					out.append(';');
					out.append(_vs[i]);
				}
			}
		}
		
		/** ....稀烂 */
		public void out(Appendable out) throws IOException {
			if (cannotSave()) return;
			
			String[] c_values = null;
			
			if (!isImage()) {
				c_values = QuotedCoder.encode(props, values);
				delNull(c_values);
			}
			
			out.append(type);
			outProp(out);
			out.append(':');
			
			if (isImage()) {
				outImage(out);
			} else {
				outValues(out, c_values);
			}
			
			out.append('\n');
		}
		
		private String sub(String str, int begin, int end) {
			if (end>str.length()) end = str.length();
			return str.substring(begin, end);
		}
		
		private boolean cannotSave() {
			if (isImage()) {
				return img==null;
			}
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
		
		public boolean isImage() {
			return type.equalsIgnoreCase(Type.PHOTO);
		}
		
		/** 可以返回null */
		public byte[] getImage() {
			if (!isImage()) throw new IllegalStateException();
			return img;
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
