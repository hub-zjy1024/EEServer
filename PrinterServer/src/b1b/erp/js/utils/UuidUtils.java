package b1b.erp.js.utils;

import java.util.Random;

public class UuidUtils {
	public static String getIDWithDate(int len) {
		Random ran = new Random();
		double d = Math.random();
		double val = 1;
		for (int i = 0; i < len; i++) {
			val = d * 10;
		}
		String s = "" + val;
		return s;
	}
}
