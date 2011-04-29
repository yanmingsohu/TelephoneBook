package jym.test;
import java.io.UnsupportedEncodingException;

// CatfoOD 2011-4-29 下午02:01:10 yanming-sohu@sohu.com/@qq.com

public class TestVcfFormat {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// =E5=AE=8B=E7=BA=A2=E6=B1=9F
		byte[] b = new byte[] {(byte) 0xe5, (byte) 0xae, (byte) 0x8b, (byte) 0xe7, 
				(byte) 0xba, (byte) 0xa2, (byte) 0xe6, (byte) 0xb1, (byte) 0x9f};
		
		String s = new String(b, "utf-8");
		System.out.println(s);
	}

}
