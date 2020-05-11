package b1b.erp.js.utils.ex;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class NullPointExHandler {

	public static String getErrMsg(NullPointerException e) {
		return getErrMsg(e, 5);
	}

	public static String getErrMsg(NullPointerException e, int depth) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		PrintWriter exWriter = new PrintWriter(bao);
		StackTraceElement[] stackTrace = e.getStackTrace();
		for (StackTraceElement tempEx : stackTrace) {
			depth--;
			exWriter.println("\tat " + tempEx);
			if (depth == 0) {
				break;
			}
		}
		// e.printStackTrace(exWriter);
		exWriter.flush();
		String result = "";
		try {
			result = new String(bao.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		exWriter.close();
		return result;
	}
}
