package b1b.erp.js.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamRead {
	public static final String DEF_CHARSET = "utf-8";

	public static String readFrom(InputStream in) throws IOException {
		return readFrom(in, DEF_CHARSET);
	}

	public static String readFrom(InputStream in, String charSet) throws IOException {
		StringBuilder sb = new StringBuilder();
		String temp = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(in, charSet));
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		return sb.toString();
	}
}
