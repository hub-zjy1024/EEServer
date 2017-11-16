package b1b.erp.js.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Md5Utils {

	public static String charset="gbk";
	public static byte[] md5Encrypt(String encryptStr) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(encryptStr.getBytes("utf8"));
			return md5.digest();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encodeBase64(byte[] b) {
		return org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(b);
	}

	public static String encodeBase641(byte[] b) {
		try {
			return new String(Base64.getEncoder().encode(b), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encodeBase642(byte[] b) {
			return new BASE64Encoder().encode(b);
	}

	public static String encodeBase643(byte[] b) {
		try {
			return new String(org.apache.commons.net.util.Base64.encodeBase64(b),charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
//	emhlc2hp6L+Z5Liq6aKd5Liq5a2j6IqC
//	emhlc2hp6L+Z5Liq6aKd5Liq5a2j6IqC

	public static String encodeBase644(byte[] b) {
		try {
			return new String(org.apache.xmlbeans.impl.util.Base64.encode(b),charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		byte[] b;
		try {
			b = "zheshi这个额个季节".getBytes(charset);
			System.out.println(encodeBase64(b));
			System.out.println(encodeBase641(b));
			System.out.println(encodeBase642(b));
			System.out.println(encodeBase643(b));
			System.out.println(encodeBase644(b));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	

	}
}
