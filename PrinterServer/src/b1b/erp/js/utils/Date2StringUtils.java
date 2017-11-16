package b1b.erp.js.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Date2StringUtils {
	public static String style1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date());
	}
}
