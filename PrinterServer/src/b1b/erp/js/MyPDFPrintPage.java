package b1b.erp.js;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.ROT;
import com.jacob.com.Variant;
import com.sun.pdfview.PDFPage;

import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.SingleActiveXComponent;
import b1b.erp.js.utils.WordUtils;

public class MyPDFPrintPage implements Printable {

	public MyPDFPrintPage(PDFPage page) {
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return 0;
	}

	private static String hostname = "192.168.10.65";
	private static int port = 21;
	private static String username = "zjy";
	private static String password = "123456";

	public static void main(String[] args) {
		// testLocalWordCom(50, 1000);
		printChar(0x7FFF0000, 0x7FFFFFFF);
	}

	private static void testLocalWordCom(int counts, int timeDuration) {
		int num = 0;
		while (num < counts) {
			try {
				Thread.sleep(timeDuration);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			new Thread() {
				public void run() {
					// 使用SF的打印机
					String sUrl = "http://192.168.10.65:8080/PrinterServer/SFPrintServlet?orderID=666655554444&hasE=1&yundanType=210&goodinfos=74HC04D%265%24SN74HC245DWR%2610%2474HC04D%2610%2474HC08D%2615&printer=BTP-V540L%28BPLZ%29%28U%291&cardID=7555205283&baojiaprice=-1.0&payPerson=&payType=%E5%AF%84%E4%BB%98%E6%9C%88%E7%BB%93&serverType=%E9%A1%BA%E4%B8%B0%E9%9A%94%E6%97%A5&j_name=%E5%A7%9C%E7%9B%BC&j_phone=4008901060&j_address=%E6%B2%B3%E5%8C%97%E7%9C%81%E5%BB%8A%E5%9D%8A%E5%B8%82%E5%B9%BF%E9%98%B3%E5%8C%BA%E5%87%AF%E5%88%9B%E5%A4%A7%E5%8E%A6%E7%AC%AC1%E5%B9%A22%E5%8D%95%E5%85%8311%E5%B1%822-1102%EF%B9%911103%E5%8F%B7%E6%88%BF++++0316-8667313&destcode=110&d_name=%E5%88%98%E6%98%A5%E7%A7%80&d_phone=13590282886&d_address=%E6%B7%B1%E5%9C%B3%E5%B8%82%E7%A6%8F%E7%94%B0%E5%8C%BA+%E5%8D%8E%E5%BC%BA%E5%8C%97+%E5%8D%8E%E5%BC%BA%E7%94%B5%E5%AD%90%E4%B8%96%E7%95%8C%E6%B7%B1%E5%9C%B3%E4%B8%89%E5%BA%975C039%E5%AE%A4";
					// String sUrl =
					// "http://192.168.10.65:8080/PrinterServer/WordTestServlet";
					try {
						URL url = new URL(sUrl);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.getResponseCode();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
			num++;
		}
	}

	/**
	 * 需要开启本地FTPServer 服务器
	 */
	private static void testLocalFTPUpload() {
		try {
			// File file = new File("D:/wyzzjdh.txt");
			File file = new File("D:/downloads/Office2010_2010_XiTongZhiJia.rar");
			FileInputStream in = new FileInputStream(file);
			String dir = "/m/d/";
			String name = "ta1.txt";
			String remote = dir + name;
			File parent = new File("D:/dyj/testFTP");
			DownUtils utils = new DownUtils(hostname, port, username, password);
			String mUrl = "172.16.6.22";
			mUrl = "192.168.10.65";
			// utils = new DownUtils(mUrl, 21, "zjy", "123456");
			// 上传parent目录下面的所有文件到FtpServer下
			for (File f : parent.listFiles()) {
				new Thread() {
					public void run() {
						if (!utils.serverIsOpen()) {
							try {
								utils.login();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						String path;
						try {
							path = dir + new String(f.getName().getBytes("UTF-8"), "ISO-8859-1");
							FileInputStream tInputStream;
							tInputStream = new FileInputStream(f);
							utils.upload(tInputStream, path);
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					};
				}.start();
			}
			RandomAccessFile randomAccess = new RandomAccessFile(file, "rws");
			randomAccess.seek(100);
			byte[] buf = new byte[4 * 1024];
			int singleLength = 1024 * 1024;
			randomAccess.read(buf);
			int hasRead = 0;
			for (; hasRead < singleLength;) {
				int bytes = randomAccess.read(buf);
				hasRead += bytes;
			}
			name = "yasuo.7z";
			remote = dir + name;
			// in.close();
			// mClient.storeFile(remote, in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printChar(int from, int end) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("d:/testChar.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (out != null) {
			for (int i = from; i < end; i++) {
				String hexString = Integer.toHexString(i);
				String str = "int:" + i + "\tchar:" + (char) i + "\thex:" + hexString;
				System.out.println(str);
				try {
					out.write((str + "\n").getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static void wordTest() {
		String templatePath = "/WebContent/docTemplate/sf150模板.doc";
		File context = new File("");
		System.out.println(context.getAbsolutePath());
		templatePath = context.getAbsolutePath() + templatePath;
		FileInputStream testInputStream = null;
		try {
			testInputStream = new FileInputStream(templatePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (testInputStream != null) {
			String rootPath = "C:/DyjPrinter";
			String savePath = rootPath + "/SF/saveWord/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String name = "test_" + Myuuid.create(4) + ".doc";
			ActiveXComponent ac = null;
			try {
				FileUtils.fileCopy(testInputStream, savePath + name);
				System.out.println("=====" + DownUtils.getTimeAtSS() + "=====");
				testInputStream.close();
				ac = SingleActiveXComponent.getApp();
				Dispatch doc = WordUtils.openDocument(savePath + name, ac);
				HashMap<String, String> bMarksAndValue = new HashMap<>();
				bMarksAndValue.put("业务类型", "测试");
				WordUtils.replaceBookmark(bMarksAndValue, doc);
				String print1 = "beijin";
				String printerName = print1;
				if (printerName != null && !printerName.equals("")) {
					String nowPrinter = ac.getProperty("ActivePrinter").toString();
					System.out.println("nowPrint:" + nowPrinter);
					if (!printerName.equals(nowPrinter)) {
						try {
							System.out.println("start set printer:" + printerName);
							ac.setProperty("ActivePrinter", new Variant(printerName));
						} catch (Exception e) {
							System.err.println("set printer fail:" + e.getMessage());
						}
					}
				}
				WordUtils.closeDocument(doc, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ac != null) {
				System.out.println("dispatch_formmer id:" + ac.m_pDispatch);
			}
		}
	}

	public static void getFileList(File dir, String type) {
		LinkedList<File> dirs = new LinkedList<>();
		dirs.add(dir);
		File temp_file;
		while (!dirs.isEmpty()) {
			temp_file = dirs.removeFirst();
			File[] files = temp_file.listFiles();
			System.out.println("dir:" + temp_file.getAbsolutePath());
			if (files != null) {
				for (File f : files) {
					System.out.println("currrerentFile==" + f.getName());
					if (f.isDirectory()) {
						dirs.addLast(f);
					}
				}

			}
			System.out.println("currrerentFile===================");
		}
	}
}
