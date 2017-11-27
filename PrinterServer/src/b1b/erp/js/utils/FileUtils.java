package b1b.erp.js.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	public static List<String> getFileList(File dir, String type) {
		LinkedList<File> dirs = new LinkedList<>();
		List<String> tFiles = new ArrayList<>();
		dirs.add(dir);
		File temp_file;
		while (!dirs.isEmpty()) {
			temp_file = dirs.removeFirst();
			File[] files = temp_file.listFiles();
			System.out.println("dir:" + temp_file.getAbsolutePath());
			if (files != null) {
				for (File f : files) {
					tFiles.add(f.getName());
					System.out.println("currrerentFile==" + f.getName());
					if (f.isDirectory()) {
						dirs.addLast(f);
					}
				}
			}
		}
		return tFiles;
	}
}
