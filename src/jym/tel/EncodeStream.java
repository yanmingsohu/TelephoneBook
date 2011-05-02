package jym.tel;
// CatfoOD 2008.2.24

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 数据加密流
 * 对数据加密,用CodingStream替换OutputStream
 */
public class EncodeStream extends OutputStream {
	
	private byte[] SECERN = {	(byte)0xef,(byte)0xaa,0x3f,(byte)0x86,
								0x1a,(byte)0xff,(byte)0xff,0x00,
								};
	
	private FileOutputStream fo;
	private byte[] pw;
	
	
	/**
	 * 初始化加密流
	 * @param filename - 文件名
	 * @param password - 密码
	 * @throws IOException - 创建文件出错
	 */
	public EncodeStream(String filename, String password, String bookname) 
	throws IOException {
		this(new File(filename), password, bookname);
	}
	
	/**
	 * 初始化加密流
	 * @param filename - 文件名
	 * @param password - 密码
	 * @throws IOException - 创建文件出错
	 */
	public EncodeStream(File filename, String password, String bookname) 
	throws IOException {
		if(password.length()<1) throw new IOException("password too short.");
		pw = password.getBytes();
		fo = new FileOutputStream(filename);
		plen = pw.length;
		
		//写入辨识加密文件的代码
		fo.write(SECERN);
		
		//写入密钥的md5
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("MD5 error\n"+e);
		}
		byte[] md5 = md.digest(pw);
		fo.write(md5);
		
		//写入加密前的文件名
		byte[] fname = bookname.getBytes();
		byte namelen = (byte)fname.length;
		if (namelen<0) throw new IOException("This filename is too long. >128 ");
		fo.write(namelen);
		fo.write(fname);
	}

	// 缓冲数据 定义
	private final int UNIT = 8;
	private byte[] b = new byte[UNIT];
	private byte[] w = new byte[UNIT+1];
	private int rlen = 0;
	private int plen = 0;
	private int _pw  = 0;
	private int _8bit= 0;
	// 定义end
	
	public void write(int data) throws IOException {
		b[rlen++] = (byte)data;
		if(rlen>=UNIT) {
			write();
		}
	}
	
	private void write() throws IOException {
		w[0] = 0;
		for(int i=0; i<rlen; i++){
			int add = toInt(b[i]) + toInt(pw[_pw]);
			
			w[0]  |= (byte)((add & 0x00000100) >> 8 << _8bit);
			w[i+1] = (byte) (add & 0x000000ff);
			if (++_pw  >=plen) _pw	= 0;
			if (++_8bit>=UNIT) _8bit= 0;
		}
		writeFile(w,rlen+1);
		rlen = 0;
	}
	
	public void close() throws IOException {
		flush();
		fo.close();
	}
	
	public void flush() throws IOException {
		write();
		writeFile(null, 0, true);
		fo.flush();
	}
	
	//写入文件的函数，带缓冲区
	private void writeFile(byte[] b, int filelen, boolean flush) throws IOException{
		for (int i=0; i<filelen; i++){
			fileWCache[pFile++] = b[i];
		}
		if ( pFile>CACHELONGLESS || flush) {
			fo.write(fileWCache, 0, pFile);
			pFile = 0;
		}
	}
	private void writeFile(byte[] b, int filelen) throws IOException{
		writeFile(b,filelen,false);
	}
	
	private final int CACHELONGLESS = 600000;
	private byte[] fileWCache = new byte[CACHELONGLESS+35];
	private int pFile = 0;
	
	/**
	 * byte 有符号数定义<p>
	 *
	 *	00 = 00<p>
	 *	01 = 01<p>
	 *	7f = 127<p>
	 *	80 = -128	128<p>
	 *	81 = -127	129<p>
	 *	ff = -1		255<p>
	 *
	 * @param b
	 * @return r
	 */
	private int toInt(byte b){
		int r = 0;
		if (b<0) r = 256 + b;
		else r = (int)b;
		return r;
	}
}

/*
 *  测试代码
 *  
	public static final void main(String[] s) {
		try {
			System.out.println("start");
			OutputStream o = new EncodeStream("读书.txt","1234");
			InputStream  i = new FileInputStream("读书.txt");
			int a;
			a=i.read();
			while(a!=-1) {
				System.out.println(" "+a);
				o.write(a);
				a=i.read();
			}
			i.close();
			o.close();
			System.out.println("end");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
*/
