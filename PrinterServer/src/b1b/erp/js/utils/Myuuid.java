package b1b.erp.js.utils;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class Myuuid {
	public static String create(int lastLen){
		String s=System.currentTimeMillis()+String.valueOf(Math.random()).substring(2,2+lastLen);
		return s;
	}
	public static String createRandom(int len){
		String s=String.valueOf(Math.random()).substring(2,2+len);
		return s;
	}
}
