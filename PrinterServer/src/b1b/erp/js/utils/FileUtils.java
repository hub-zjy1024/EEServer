package b1b.erp.js.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	public static void fileCopy(InputStream in, String path) throws IOException {
		FileOutputStream fio = new FileOutputStream(path);
		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = in.read(buf)) != -1) {
			fio.write(buf, 0, len);
		}
		fio.close();
	}
}
