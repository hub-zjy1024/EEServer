package b1b.erp.js.yundan.sf.jee;

public class Base64 {

	public static int NO_WRAP = 0;

	public static byte[] encode(byte[] data, int type) {
		return java.util.Base64.getEncoder().encode(data);
	}
}
