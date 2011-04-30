package jym.tel;
// CatfoOD 2008.2.25

import java.io.File;
import java.io.IOException;

import jym.wit.Tools;

public class TableDataPack {
	
	private Data d;
	
	public static final String[] DEFALUTNAME = {
		"姓名","手机","家庭电话","关系","住址","日期","其他",
	};
	public static final String DEFAULTPASSWORD = "jym";
	
	
	public TableDataPack() {
		d = new Data();
	}
	public TableDataPack(File f) {
		d = new Data();
		d.file = f;
	}
	
	/**
	 * 返回 Data 的引用;
	 * @return Data
	 */
	public Data get() {
		return d;
	}
	
	/**
	 * 建立一个默认的DataPack;
	 * @return TableDataPack实列
	 */
	public static TableDataPack creatDeafultData() {
		TableDataPack data = new TableDataPack();
		TableDataPack.Data d = data.get();
		
		d.columnName 	= DEFALUTNAME;
		d.columnCount	= d.columnName.length;
		d.rowCount 		= 5;
		d.data 			= new Object[d.rowCount][d.columnCount];
		d.file 			= Tools.getRandFile();
		d.name 			= "我的电话簿";
		d.password		= DEFAULTPASSWORD;
		
		return data;
	}

	/**
	 * 写入
	 */
	public void write(Data d) throws IOException {
		if (d!=this.d) throw new IOException("非法的函数参数");
		EncodeStream out = new EncodeStream(d.file, d.password, d.name);
		// 写入数据头
		byte[] col = intTo4bytes( d.columnCount );
		byte[] row = intTo4bytes( d.rowCount );
		out.write(col);
		out.write(row);
		// 写入列名
		for(int i=0; i<d.columnCount; ++i) {
			byte[] b = d.columnName[i].getBytes();
			byte len = (byte)b.length;
			out.write(len);
			out.write(b);
		}
		// 写入表格
		for(int i=0; i<d.rowCount; ++i) {
			for(int j=0; j<d.columnCount; ++j) {
				if( d.data[i][j]==null ) {
					out.write(0);
				}else{
					byte[] b = d.data[i][j].toString().getBytes();
					byte len = (byte)b.length;
					out.write(len);
					out.write(b);
				}
			}
		}
		out.close();
	}
	
	/**
	 * 读取
	 */
	public Data read() throws IOException {
		DecodeStream in = new DecodeStream(d.file, d.password);
		d.name = in.getBookName();
		// 读取头
		byte[] byt = new byte[4];
		in.read(byt);
		d.columnCount = bytesToint(byt);
		in.read(byt);
		d.rowCount = bytesToint(byt);
		int len=0;
		// 读取列名
		d.columnName = new String[d.columnCount];
		for(int i=0; i<d.columnCount; ++i) {
			len = in.read();
			byt = new byte[len];
			in.read(byt);
			d.columnName[i] = new String(byt);
		}
		// 读取数据
		d.data = new Object[d.rowCount][d.columnCount];
		for(int i=0; i<d.rowCount; ++i) {
			for(int j=0; j<d.columnCount; ++j) {
				len = in.read();
				if(len>0) {
					byt = new byte[len];
					in.read(byt, 0, len);
					d.data[i][j] = new String(byt);
				}else{
					d.data[i][j] = null;
				}
			}
		}
		in.close();
		return d;
	}
	 
	/**
	 * 设置表格编辑器
	 */
	public void setEditor(IEditorSet setter) {
	}
	
	/**
	 * 内部数据类,通过 TableDataPack.get() 直接使用
	 */
	public class Data {
		/** 二维数组数据 */
		public Object data[][];
		/** 列名 数组 */
		public String columnName[];
		/** 电话簿的名字 */
		public String name;
		/** 开启的密码 */
		public String password = DEFAULTPASSWORD;
		/** File 引用 */
		public File file;
		/** 行的数量 */
		public int rowCount;
		/** 列的数量 */
		public int columnCount;
		
		private Data() {}
	}
	
	public static byte[] intTo4bytes(int i) {
		byte[] b = new byte[4];// 0x0000 0000
		b[3] = (byte)( (i&0xff000000)>>24 );
		b[2] = (byte)( (i&0x00ff0000)>>16 );
		b[1] = (byte)( (i&0x0000ff00)>>8 );
		b[0] = (byte)( (i&0x000000ff) );
		return b;
	}
	public static int bytesToint(byte[] b) {
		int i = b[0];
		i += toInt( b[1] ) << 8;
		i += toInt( b[2] ) << 16;
		i += toInt( b[3] ) << 24;
		return i;
	}
	
	private static final int toInt(byte b){
		int r = 0;
		if (b<0) r = 256 + b;
		else r = (int)b;
		return r;
	}
	
	/** 清空所有对象,释放内存 */
	public void quit() {
		d.columnCount = -1;
		d.rowCount = -1;
		d.columnName = null;
		d.data = null;
		d.file = null;
		d.password = null;
		d.name = null;
		d = null;
	}
}
