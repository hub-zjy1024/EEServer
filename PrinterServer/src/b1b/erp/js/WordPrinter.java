package b1b.erp.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.utils.WordUtils;

public class WordPrinter {
	// Lenovo LJ2250N
	public static void main(String[] args) {
		// ComThread.InitMTA(true);
		ComThread.InitMTA(true);
		ActiveXComponent wd = new ActiveXComponent("Word.Application");
		Dispatch.put(wd, "Visible", new Variant(false));
		// 这里Visible是控制文档打开后是可见还是不可见，若是静默打印，那么第三个参数就设为false就好了
		Dispatch document = wd.getProperty("Documents").toDispatch();
		try {
			File templateFile = new File("d:/6.doc");
			FileInputStream fio = new FileInputStream(templateFile);
			String name = templateFile.getName();
			name = name.substring(0, name.lastIndexOf("."));
			String newPath = templateFile.getParentFile().getAbsolutePath() + name + System.currentTimeMillis()
					+ ".doc";
			FileOutputStream out = new FileOutputStream(newPath);
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = fio.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.close();
			fio.close();
			System.out.println("createNew");
			Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method, new Object[] { newPath }, new int[1])
					.toDispatch();
			// wd.setProperty("ActivePrinter", new Variant("Lenovo LJ2250N"));
			WordUtils utils = new WordUtils();
			HashMap<String, String> map = new HashMap<>();
			map.put("送货单位", "北京远大创新");
			map.put("制单日期", "2017-1-1");
			map.put("合同编号", "1204521345");
			map.put("收货人", "收货人ID");
			map.put("收货单位", "本地");
			map.put("收货地址", "新收货");
			map.put("收货电话", "12345667777");
			map.put("送货传真", "123456");
			map.put("送货电话", "12345667777");
			map.put("送货地址", "12345667777");
			map.put("送货人", "沉浮");
			utils.replaceBookmark(doc, map);
			Dispatch table = utils.getBookmark("明细表格", doc);
			utils.insertTable(wd, doc, "明细表", 3, 7, table);
			// wd.setProperty("ActivePrinter", new Variant("Microsoft XPS
			// Document Writer"));
			// Dispatch.callN(doc, "PrintOut", new Object[] {});
			// printDoc(wd, newPath, "Lenovo LJ2250N");
			// utils.closeDocument(doc,true);
			findLabelLike("", doc);
			Dispatch.call(doc, "Close", new Variant(-1));
			Dispatch.call(document, "Close", new Variant(-1));
			// printDoc(wd, newPath, "Microsoft XPS Document Writer");
			// wd.invoke("Quit", new Variant(0));
			// wd.invoke("Quit",new Variant[0]);
			// Dispatch.call(wd, "Quit");
			Dispatch template = wd.getProperty("NormalTemplate").toDispatch();
			// 判断是否保存模板
			boolean saved = Dispatch.get(template, "Saved").getBoolean();
			if (!saved) {
				// 保存模板
				Dispatch.put(template, "Saved", true);
			}
			// wd.invoke("Quit", new Variant[0]);
			// wd.invoke("Quit", new Variant(0));
			// wd. invoke("Quit", new Variant[] {});
			Dispatch.call(wd, "Quit");
			System.out.println("close");
			ComThread.Release();
			ComThread.quitMainSTA();
			Runtime.getRuntime().exec("taskkill /IM wps.exe /f");
			// Runtime.getRuntime().exec("taskkill /IM winword.exe /f");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// ComThread.Release();
		}

		// Dispatch document = Dispatch.call(documents, "Add").toDispatch(); //
		// 使用Add命令创建一个新文档
		// Dispatch wordContent = Dispatch.get(document,
		// "Content").toDispatch(); // 取得word文件的内容
		// Dispatch.call(wordContent, "InsertAfter", "这里是一个段落的内容");//插入一个段

	}

	/**
	 * 模糊查找书签,并返回准确的书签名称
	 * 
	 * @param labelName
	 * @return
	 */
	public static String findLabelLike(String labelName, Dispatch doc) {
		Dispatch bookMarks = Dispatch.call(doc, "Bookmarks").toDispatch();
		int count = Dispatch.get(bookMarks, "Count").getInt(); // 书签数
		Dispatch rangeItem = null;
		String lname = "";
		for (int i = 1; i <= count; i++) {
			rangeItem = Dispatch.call(bookMarks, "Item", new Variant(i)).toDispatch();
			lname = Dispatch.call(rangeItem, "Name").toString();// 书签名称
			System.out.println("书签：" + lname);
			// if (lname.startsWith(labelName)) {// 前面匹配
			// return lname.replaceFirst(labelName, "");
			// }
		} // 返回后面值
		return lname;
	}

	public static void printDoc(ActiveXComponent ac, String path, String printerName) {
		Dispatch document = ac.getProperty("Documents").toDispatch();
		Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method, new Object[] { path }, new int[1])
				.toDispatch();
		// wd.setProperty("ActivePrinter", new Variant("Lenovo LJ2250N"));
		ac.setProperty("ActivePrinter", new Variant(printerName));
		Dispatch.callN(doc, "PrintOut", new Object[] {});
		if (doc != null) {
			Dispatch.call(doc, "Close", new Variant(0));
		}
	}

	public void openFile(ActiveXComponent ac, String path) {
		Dispatch documents = ac.getProperty("Documents").toDispatch();
		Dispatch doc = Dispatch.invoke(documents, "Open", Dispatch.Method, new Object[] { path }, new int[1])
				.toDispatch();
	}

}
