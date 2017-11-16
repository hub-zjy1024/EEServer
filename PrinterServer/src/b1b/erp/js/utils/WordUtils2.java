package b1b.erp.js.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import javafx.print.Printer;

/**
 * @author zjy 使用该工具类需要用到 jacob.jar 包及 jacob-1.18-M2-x86.dll/
 *         jacob-1.18-M2-x64.dll文件 .dll文件复制到服务端 %java_home%/bin下 32位系统使用
 *         x86，64位使用x64 本地打印工具类，使用程序所在电脑能使用的打印机打印
 *         该工具类使用前提：必须安装能打开word，excel的程序，如wps、office；
 *         打印PDF文件使用到fontbox-2.0.6.jar,pdfbox-2.0.6.jar,levigo-jbig2-imageio-2.0.jar
 */
public class WordUtils2 { // word运行程序对象
	private static ActiveXComponent activeX;
	// 所有word文档集合
	private static Dispatch docList;
	// word文档
	// 保存退出
	private boolean saveOnExit;
	public static String[] SONGHUO_TABLE = new String[] { "型号", "厂家", "封装", "数量", "单价", "金额", "备注" };
	public static int[] CHUKUDAN_COlUMN_WIDTH = new int[] { 70, 60, 60, 60, 60, 60, 60 };
	public static String PRINTER_LENOVO = "Lenovo LJ2250N";
	public static String PRINTER_XPS = "Microsoft XPS Document Writer";

	public static ActiveXComponent getApp() {
		if (activeX == null) {
			// ComThread.InitSTA(true);
			ComThread.InitMTA();
			activeX = new ActiveXComponent("Word.Application");
			docList = activeX.getProperty("Documents").toDispatch();
		}
		System.out.println(Thread.currentThread().getId() + "-dispatch=" + activeX.m_pDispatch);
		// if (activeX == null||activeX.m_pDispatch==0) {
		// activeX = new ActiveXComponent("Word.Application");
		// activeX.setProperty("Visible", new Variant(false));
		// activeX.setProperty("DisplayAlerts", new Variant(false));
		// }
		// if (docList == null||activeX.m_pDispatch==0) {
		// docList = activeX.getProperty("Documents").toDispatch();
		// }
		return activeX;
	}

	public static void save(Dispatch doc) {
		Dispatch.put(doc, "Saved", true);
	}

	/**
	 * 创建一个新的word文档
	 */
	public static Dispatch createNewDocument() {
		Dispatch doc = Dispatch.call(docList, "Add").toDispatch();
		return doc;
	}

	/**
	 * 打开一个已经存在的word文档
	 *
	 * @param docPath
	 */
	public static Dispatch openDocument(String docPath) {
		Dispatch doc = Dispatch.call(docList, "Open", docPath).toDispatch();
		return doc;
	}

	public static void init() {
		activeX = new ActiveXComponent("Word.Application");
		docList = activeX.getProperty("Documents").toDispatch();
	}

	/**
	 * 打开一个已经存在的word文档
	 *
	 * @param docPath
	 */
	public static Dispatch openDocument(String docPath, ActiveXComponent activex) {
		// Dispatch documents = activex.getProperty("Documents").toDispatch();
		Dispatch doc = Dispatch.call(docList, "Open", docPath).toDispatch();
		return doc;
	}

	/**
	 * 打开一个有密码保护的word文档
	 * 
	 * @param docPath
	 * @param password
	 */
	public static void openDocument(String docPath, String password) {
		Dispatch doc = Dispatch.call(docList, "Open", docPath).toDispatch();
		unProtect(password, doc);
	}

	public void insertTable(ActiveXComponent ac, Dispatch doc, String tableTitle, int row, int column,
			Dispatch selection) {
		// Dispatch.call(selection, "TitleText", tableTitle); // 写入标题内容 // 标题格行
		// Dispatch.call(selection, "TypeParagraph"); // 空一行段落
		// Dispatch.call(selection, "MoveDown"); // 游标往下一行
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		// int count = Dispatch.get(tables,
		// "Count").changeType(Variant.VariantInt).getInt(); // document中的表格数量
		// Dispatch table = Dispatch.call(tables, "Item", new Variant(
		// 1)).toDispatch();//文档中第一个表格
		// Dispatch range = Dispatch.get(selection, "Range").toDispatch();//
		// /当前光标位置或者选中的区域
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(row), new Variant(column), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
																							// 实际上这个数==column
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
																									// 实际上这个数等于row
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				// Dispatch cell = Dispatch.call(cells, "Item", new
				// Variant(j)).toDispatch(); //当前单元格
				// Dispatch cell = Dispatch.call(newTable, "Cell", new
				// Variant(j) , new Variant(i) ).toDispatch(); //取单元格的另一种方法
				// Dispatch.call(cell, "Select");//选中当前单元格
				// Dispatch.put(selection, "Text",
				// "第"+j+"行，第"+i+"列");//往选中的区域中填值，也就是往当前单元格填值
				System.out.println(i + "列" + j + "行");
				putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列", ac);// 与上面四句的作用相同
			}
		}
	}

	public static void insertTable(ActiveXComponent ac, Dispatch doc, String[] tableTitle, int row, int column,

			Dispatch selection) {
		// Dispatch.call(selection, "TitleText", tableTitle); // 写入标题内容 // 标题格行
		// Dispatch.call(selection, "TypeParagraph"); // 空一行段落
		// Dispatch.call(selection, "MoveDown"); // 游标往下一行
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		// int count = Dispatch.get(tables,
		// "Count").changeType(Variant.VariantInt).getInt(); // document中的表格数量
		// Dispatch table = Dispatch.call(tables, "Item", new Variant(
		// 1)).toDispatch();//文档中第一个表格
		// Dispatch range = Dispatch.get(selection, "Range").toDispatch();//
		// /当前光标位置或者选中的区域
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(row), new Variant(column), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
																							// 实际上这个数==column
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
																									// 实际上这个数等于row
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				if (i == 1) {
					putTxtToCell(newTable, j, i, tableTitle[j - 1], ac);
				} else {
					putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列", ac);
				}
				// Dispatch cell = Dispatch.call(cells, "Item", new
				// Variant(j)).toDispatch(); //当前单元格
				// Dispatch cell = Dispatch.call(newTable, "Cell", new
				// Variant(j) , new Variant(i) ).toDispatch(); //取单元格的另一种方法
				// Dispatch.call(cell, "Select");//选中当前单元格
				// Dispatch.put(selection, "Text",
				// "第"+j+"行，第"+i+"列");//往选中的区域中填值，也就是往当前单元格填值
				System.out.println(i + "列" + j + "行");
				// putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列",
				// ac);// 与上面四句的作用相同
			}
		}
	}

	public void insertTable(ActiveXComponent ac, Dispatch doc, String[] tableTitle, int row, int column,
			int[] colmWidth, Dispatch selection) {
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(row), new Variant(column), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			// setColumnWidth(i, colmWidth[i-1], newTable);
			setColumnWidth(col, colmWidth[i - 1]);
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				if (j == 1) {
					putTxtToCell(newTable, j, i, tableTitle[i - 1], ac);
				} else {
					putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列", ac);
				}
			}
		}
	}

	public void insertTable(ActiveXComponent ac, Dispatch doc, List<String[]> list, int[] colmWidth,
			Dispatch selection) {
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch newTable = Dispatch.call(tables, "Add", selection, new Variant(list.size()),
				new Variant(list.get(0).length), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			// setColumnWidth(i, colmWidth[i-1], newTable);
			setColumnWidth(col, colmWidth[i - 1]);
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				String[] data = list.get(j - 1);
				putTxtToCell(newTable, j, i, data[i - 1], ac);
			}
		}
	}

	public void insertTable(ActiveXComponent ac, Dispatch doc, int row, int column, int[] colmWidth,
			Dispatch selection) {
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(row), new Variant(column), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			// setColumnWidth(i, colmWidth[i-1], newTable);
			setColumnWidth(col, colmWidth[i - 1]);
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列", ac);// 与上面四句的作用相同
			}
		}
	}

	/**
	 * 设置当前表格指定列的列宽
	 * 
	 * @param columnWidth
	 * @param columnIndex
	 * @throws 如果不是整齐的表格不能使用
	 */
	public void setColumnWidth(int columnIndex, float columnWidth, Dispatch table) {
		Dispatch cols = Dispatch.get(table, "Columns").toDispatch(); // 此表的所有列，
		Dispatch column = Dispatch.call(cols, "Item", new Variant(columnIndex)).toDispatch();
		Dispatch.put(column, "Width", new Variant(columnWidth));
	}

	/**
	 * 设置当前表格指定列的列宽
	 * 
	 * @param columnWidth
	 * @param columnIndex
	 * @throws 如果不是整齐的表格不能使用
	 */
	public void setColumnWidth(Dispatch column, float columnWidth) {
		Dispatch.put(column, "Width", new Variant(columnWidth));
	}

	/**
	 * @param ac
	 * @param doc
	 * @param tableTitle
	 * @param row
	 * @param column
	 * @param selection
	 * @param list
	 */
	public void insertTable(ActiveXComponent ac, Dispatch doc, String tableTitle, int row, int column,
			Dispatch selection, ArrayList<String> list) {
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();

		// /当前光标位置或者选中的区域
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(row), new Variant(column), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度

		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
																							// 实际上这个数==column
		System.out.println(colCount + "列");
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
																									// 实际上这个数等于row
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				// Dispatch cell = Dispatch.call(cells, "Item", new
				// Variant(j)).toDispatch(); //当前单元格
				// Dispatch cell = Dispatch.call(newTable, "Cell", new
				// Variant(j) , new Variant(i) ).toDispatch(); //取单元格的另一种方法
				// Dispatch.call(cell, "Select");//选中当前单元格
				// Dispatch.put(selection, "Text",
				// "第"+j+"行，第"+i+"列");//往选中的区域中填值，也就是往当前单元格填值
				System.out.println(i + "列" + j + "行");
				putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列", ac);// 与上面四句的作用相同
			}
		}
	}

	/**
	 * 在指定的单元格里填写数据
	 * 
	 * @param tableIndex
	 * @param cellRowIdx
	 * @param cellColIdx
	 * @param txt
	 */
	public static void putTxtToCell(Dispatch table, int cellRowIdx, int cellColIdx, String txt, ActiveXComponent ac) {
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(cellRowIdx), new Variant(cellColIdx)).toDispatch();
		Dispatch.call(cell, "Select");
		// Dispatch selection = getSelection(); // 输入内容需要的对象
		Dispatch selection = Dispatch.get(ac, "Selection").toDispatch();
		Dispatch.put(selection, "Text", txt);
	}

	/**
	 * 去掉密码保护
	 * 
	 * @param password
	 */
	public static void unProtect(String password, Dispatch doc) {
		try {
			String protectionType = Dispatch.get(doc, "ProtectionType").toString();
			if (!"-1".equals(protectionType)) {
				Dispatch.call(doc, "Unprotect", password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加密码保护
	 * 
	 * @param password
	 */
	public static void protect(Dispatch doc, String password) {
		String protectionType = Dispatch.get(doc, "ProtectionType").toString();
		if ("-1".equals(protectionType)) {
			Dispatch.call(doc, "Protect", new Object[] { new Variant(3), new Variant(true), password });
		}
	}

	/**
	 * 显示审阅的最终状态
	 */
	public void showFinalState(Dispatch doc) {
		Dispatch.call(doc, "AcceptAllRevisionsShown");
	}

	/**
	 * 打印预览：
	 */
	public void printpreview(Dispatch doc) {
		Dispatch.call(doc, "PrintPreView");
	}

	/**
	 * 打印
	 */
	public static void print(Dispatch doc) {
		Dispatch.call(doc, "PrintOut");
	}

	private static void printImageByService(File file) {
		// FileInputStream fis=new FileInputStream(file);
		HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		if (file == null || !file.exists())
			return;
		String fName = file.getName();
		String type = fName.substring(fName.lastIndexOf(".") + 1, fName.length());
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		if (fName.endsWith(".jpeg") || fName.endsWith(".jpg")) {
			flavor = DocFlavor.INPUT_STREAM.JPEG;
		} else if (fName.endsWith(".png")) {
			flavor = DocFlavor.INPUT_STREAM.PNG;
		} else if (fName.endsWith(".gif")) {
			flavor = DocFlavor.INPUT_STREAM.GIF;
		} else {
			return;
		}
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		if (service != null) {
			try {
				DocPrintJob job = service.createPrintJob(); // ������ӡ��ҵ
				FileInputStream fis = new FileInputStream(file); // �������ӡ���ļ���
				DocAttributeSet das = new HashDocAttributeSet();
				Doc doc = new SimpleDoc(fis, flavor, das);
				job.print(doc, pras);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	private static void printImageByService(String imgPath) {
		File file = new File(imgPath);
		printImageByService(file);
	}

	/**
	 * 打印图片流
	 * 
	 * @param in
	 *            图片输入流
	 * @param type
	 *            图片类型
	 */
	public static void printImageByService(InputStream in, String type) {
		printImageByService(in, type, 0);
	}

	/**
	 * @param in
	 * @param type
	 * @param printerIndex
	 */
	public static void printImageByService(InputStream in, String type, int printerIndex) {
		HashPrintRequestAttributeSet requestAttrs = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		if (type.equals("jpeg") || type.equals("jpg")) {
			flavor = DocFlavor.INPUT_STREAM.JPEG;
		} else if (type.equals("png")) {
			flavor = DocFlavor.INPUT_STREAM.PNG;
		} else if (type.equals("gif")) {
			flavor = DocFlavor.INPUT_STREAM.GIF;
		} else {
			return;
		}
		PrintService[] totalService = PrintServiceLookup.lookupPrintServices(flavor, requestAttrs);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		if (printerIndex < totalService.length) {
			service = totalService[printerIndex];
		}
		if (service != null) {
			try {
				DocPrintJob job = service.createPrintJob(); // ������ӡ��ҵ
				DocAttributeSet das = new HashDocAttributeSet();
				Doc doc = new SimpleDoc(in, flavor, das);
				job.print(doc, requestAttrs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("没有可用的打印机");
		}
	}

	/**
	 * 打印图片流
	 * 
	 * @param in
	 *            图片输入流
	 * @param type
	 *            图片类型
	 */
	public static void printImageByService(InputStream in, String type, String printerName) {
		HashPrintRequestAttributeSet requestAttrs = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		if (type.equals("jpeg") || type.equals("jpg")) {
			flavor = DocFlavor.INPUT_STREAM.JPEG;
		} else if (type.equals("png")) {
			flavor = DocFlavor.INPUT_STREAM.PNG;
		} else if (type.equals("gif")) {
			flavor = DocFlavor.INPUT_STREAM.GIF;
		} else {
			return;
		}
		PrintService[] totalService = PrintServiceLookup.lookupPrintServices(flavor, requestAttrs);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		int index = 0;
		if (printerName != null && !printerName.equals("")) {
			for (int i = 0; i < totalService.length; i++) {
				if (totalService[i].getName().equals(printerName)) {
					service = totalService[i];
					index = i;
					break;
				}
			}
		}
		printImageByService(in, type, index);
	}

	/**
	 * 打印
	 */
	public static void print(Dispatch doc, String printerName) {
		activeX.setProperty("ActivePrinter", new Variant(printerName));
		Dispatch.call(doc, "PrintOut");
	}

	public static void printDoc(String path, String printerName) {
		Dispatch doc = Dispatch.invoke(docList, "Open", Dispatch.Method, new Object[] { path }, new int[1])
				.toDispatch();
		// wd.setProperty("ActivePrinter", new Variant("Lenovo LJ2250N"));
		print(doc, printerName);
	}

	/**
	 * 指定打印机名称和打印输出工作名称
	 * 
	 * @param printerName
	 * @param outputName
	 */
	public void print(String printerName, String outputName, Dispatch doc) {
		activeX.setProperty("ActivePrinter", new Variant(printerName));
		Dispatch.call(doc, "PrintOut",
				new Object[] { new Variant(false), new Variant(false), new Variant(0), new Variant(outputName) });
	}

	/**
	 * 把选定的内容或插入点向上移动
	 *
	 * @param pos
	 */
	public void moveUp(int pos) {
		move("MoveUp", pos);
	}

	public static void printPDF(String filePath, String savePath) {
		printPDF(filePath, savePath, "");
	}

	public static void printPDF(String filePath, String savePath, String printerName) {
		File file = new File(filePath);
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			PDDocument doc = PDDocument.load(file);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();
			long time = System.currentTimeMillis();
			for (int i = 0; i < pageCount; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 296);
				// BufferedImage image = renderer.renderImage(i, 2.5f);
				String name = "pdfbox_image" + time + "_" + i + ".png";
				ImageIO.write(image, "PNG", new File(dir, name));
				printImageByService(new FileInputStream(name), "png", printerName);
			}
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printPDF(InputStream in, String savePath, String printerName) {
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			PDDocument doc = PDDocument.load(in);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();
			long time = System.currentTimeMillis();
			for (int i = 0; i < pageCount; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 296);
				// BufferedImage image = renderer.renderImage(i, 2.5f);
				String name = "pdfbox_image" + time + "_" + i + ".png";
				ImageIO.write(image, "PNG", new File(dir, name));
				printImageByService(new FileInputStream(name), "png", printerName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printPDF(InputStream in, String savePath, String printerName, int from, int to) {
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			PDDocument doc = PDDocument.load(in);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();
			long time = System.currentTimeMillis();
			for (int i = 0; i < pageCount; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 296);
				// BufferedImage image = renderer.renderImage(i, 2.5f);
				if (i >= from && i <= to) {
					String name = "pdfbox_image" + time + "_" + i + ".png";
					ImageIO.write(image, "PNG", new File(dir, name));
					printImageByService(new FileInputStream(savePath + name), "png", printerName);
				}
			}
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printPDF(InputStream in, String savePath) {
		printPDF(in, savePath, "");
	}

	/**
	 * 把选定的内容或者插入点向下移动
	 *
	 * @param pos
	 */
	public void moveDown(int pos) {
		move("MoveDown", pos);
	}

	/**
	 * 把选定的内容或者插入点向左移动
	 *
	 * @param pos
	 */
	public void moveLeft(int pos) {
		move("MoveLeft", pos);
	}

	/**
	 * 把选定的内容或者插入点向右移动
	 *
	 * @param pos
	 */
	public void moveRight(int pos) {
		move("MoveRight", pos);
	}

	/**
	 * 把选定的内容或者插入点向右移动
	 */
	public void moveRight() {
		Dispatch.call(getSelection(), "MoveRight");
	}

	/**
	 * 把选定的内容或者插入点向指定的方向移动
	 * 
	 * @param actionName
	 * @param pos
	 */
	private void move(String actionName, int pos) {
		for (int i = 0; i < pos; i++)
			Dispatch.call(getSelection(), actionName);
	}

	/**
	 * 把插入点移动到文件首位置
	 */
	public void moveStart() {
		Dispatch.call(getSelection(), "HomeKey", new Variant(6));
	}

	/**
	 * 把插入点移动到文件末尾位置
	 */
	public void moveEnd() {
		Dispatch.call(getSelection(), "EndKey", new Variant(6));
	}

	/**
	 * 插入换页符
	 */
	public void newPage() {
		Dispatch.call(getSelection(), "InsertBreak");
	}

	public void nextPage() {
		moveEnd();
		moveDown(1);
	}

	public int getPageCount() {
		Dispatch selection = Dispatch.get(activeX, "Selection").toDispatch();
		return Dispatch.call(selection, "information", new Variant(4)).getInt();
	}

	/**
	 * 获取当前的选定的内容或者插入点
	 * 
	 * @return 当前的选定的内容或者插入点
	 */
	public static Dispatch getSelection() {
		Dispatch selection = Dispatch.get(activeX, "Selection").toDispatch();
		return selection;
	}

	/**
	 * 从选定内容或插入点开始查找文本
	 * 
	 * @param findText
	 *            要查找的文本
	 * @return boolean true-查找到并选中该文本，false-未查找到文本
	 */
	public boolean find(String findText) {
		if (findText == null || findText.equals("")) {
			return false;
		}
		// 从selection所在位置开始查询
		Dispatch find = Dispatch.call(getSelection(), "Find").toDispatch();
		// 设置要查找的内容
		Dispatch.put(find, "Text", findText);
		// 向前查找
		Dispatch.put(find, "Forward", "True");
		// 设置格式
		Dispatch.put(find, "Format", "True");
		// 大小写匹配
		Dispatch.put(find, "MatchCase", "True");
		// 全字匹配
		Dispatch.put(find, "MatchWholeWord", "True");
		// 查找并选中
		return Dispatch.call(find, "Execute").getBoolean();
	}

	/**
	 * 查找并替换文字
	 * 
	 * @param findText
	 * @param newText
	 * @return boolean true-查找到并替换该文本，false-未查找到文本
	 */
	public boolean replaceText(String findText, String newText) {
		moveStart();
		if (!find(findText))
			return false;
		Dispatch.put(getSelection(), "Text", newText);
		return true;
	}

	/**
	 * 进入页眉视图
	 */
	public void headerView() {
		// 取得活动窗体对象
		Dispatch ActiveWindow = activeX.getProperty("ActiveWindow").toDispatch();
		// 取得活动窗格对象
		Dispatch ActivePane = Dispatch.get(ActiveWindow, "ActivePane").toDispatch();
		// 取得视窗对象
		Dispatch view = Dispatch.get(ActivePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", "9");
	}

	/**
	 * 进入页脚视图
	 */
	public void footerView() {
		// 取得活动窗体对象
		Dispatch ActiveWindow = activeX.getProperty("ActiveWindow").toDispatch();
		// 取得活动窗格对象
		Dispatch ActivePane = Dispatch.get(ActiveWindow, "ActivePane").toDispatch();
		// 取得视窗对象
		Dispatch view = Dispatch.get(ActivePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", "10");
	}

	/**
	 * 进入普通视图
	 */
	public void pageView() {
		// 取得活动窗体对象
		Dispatch ActiveWindow = activeX.getProperty("ActiveWindow").toDispatch();
		// 取得活动窗格对象
		Dispatch ActivePane = Dispatch.get(ActiveWindow, "ActivePane").toDispatch();
		// 取得视窗对象
		Dispatch view = Dispatch.get(ActivePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", new Variant(0));// 普通视图
	}

	/**
	 * 全局替换文本
	 * 
	 * @param findText
	 * @param newText
	 */
	public void replaceAllText(String findText, String newText) {
		int count = getPageCount();
		for (int i = 0; i < count; i++) {
			/* 2015-6-8 20:15:41用于控制内循环次数,防止无法替换后出现的死循环 */
			int max = 30, a = 0;
			headerView();
			while (find(findText) && a <= max) {
				Dispatch.put(getSelection(), "Text", newText);
				moveEnd();
				a++;
			}

			footerView();
			a = 0;
			while (find(findText) && a <= max) {
				Dispatch.put(getSelection(), "Text", newText);
				moveStart();
				a++;
			}
			pageView();
			moveStart();
			a = 0;
			while (find(findText) && a <= max) {
				Dispatch.put(getSelection(), "Text", newText);
				moveStart();
				a++;
			}
			nextPage();
		}
	}

	/**
	 * 全局替换文本
	 * 
	 * @param findText
	 * @param newText
	 */
	public void replaceAllText(String findText, String newText, String fontName, int size) {
		/**** 插入页眉页脚 *****/
		// 取得活动窗体对象
		Dispatch ActiveWindow = activeX.getProperty("ActiveWindow").toDispatch();
		// 取得活动窗格对象
		Dispatch ActivePane = Dispatch.get(ActiveWindow, "ActivePane").toDispatch();
		// 取得视窗对象
		Dispatch view = Dispatch.get(ActivePane, "View").toDispatch();
		/**** 设置页眉 *****/
		Dispatch.put(view, "SeekView", "9");
		while (find(findText)) {
			Dispatch.put(getSelection(), "Text", newText);
			moveStart();
		}
		/**** 设置页脚 *****/
		Dispatch.put(view, "SeekView", "10");
		while (find(findText)) {
			Dispatch.put(getSelection(), "Text", newText);
			moveStart();
		}
		Dispatch.put(view, "SeekView", new Variant(0));// 恢复视图
		moveStart();
		while (find(findText)) {
			Dispatch.put(getSelection(), "Text", newText);
			putFontSize(getSelection(), fontName, size);
			moveStart();
		}
	}

	/**
	 * 设置选中或当前插入点的字体
	 * 
	 * @param selection
	 * @param fontName
	 * @param size
	 */
	public void putFontSize(Dispatch selection, String fontName, int size) {
		Dispatch font = Dispatch.get(selection, "Font").toDispatch();
		Dispatch.put(font, "Name", new Variant(fontName));
		Dispatch.put(font, "Size", new Variant(size));
	}

	/**
	 * 在当前插入点插入字符串
	 */
	public void insertText(String text) {
		Dispatch.put(getSelection(), "Text", text);
	}

	/**
	 * 将指定的文本替换成图片
	 * 
	 * @param findText
	 * @param imagePath
	 * @return boolean true-查找到并替换该文本，false-未查找到文本
	 */
	public boolean replaceImage(String findText, String imagePath, int width, int height) {
		moveStart();
		if (!find(findText))
			return false;
		Dispatch picture = Dispatch
				.call(Dispatch.get(getSelection(), "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
		Dispatch.call(picture, "Select");
		Dispatch.put(picture, "Width", new Variant(width));
		Dispatch.put(picture, "Height", new Variant(height));
		moveRight();
		return true;
	}

	/**
	 * 全局将指定的文本替换成图片
	 * 
	 * @param findText
	 * @param imagePath
	 */
	public void replaceAllImage(String findText, String imagePath, int width, int height) {
		moveStart();
		while (find(findText)) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(getSelection(), "InLineShapes").toDispatch(), "AddPicture", imagePath)
					.toDispatch();
			Dispatch.call(picture, "Select");
			Dispatch.put(picture, "Width", new Variant(width));
			Dispatch.put(picture, "Height", new Variant(height));
			moveStart();
		}
	}

	/**
	 * 在当前插入点中插入图片
	 * 
	 * @param imagePath
	 */
	public void insertImage(String imagePath, int width, int height) {
		Dispatch picture = Dispatch
				.call(Dispatch.get(getSelection(), "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
		Dispatch.call(picture, "Select");
		Dispatch.put(picture, "Width", new Variant(width));
		Dispatch.put(picture, "Height", new Variant(height));
		moveRight();
	}

	/**
	 * 在当前插入点中插入图片
	 * 
	 * @param imagePath
	 */
	public void insertImage(String imagePath) {
		Dispatch.call(Dispatch.get(getSelection(), "InLineShapes").toDispatch(), "AddPicture", imagePath);
	}

	/**
	 * 获取书签的位置
	 * 
	 * @param bookmarkName
	 * @return 书签的位置
	 */
	public static Dispatch getBookmark(String bookmarkName, Dispatch doc) {
		try {
			Dispatch bookmark = Dispatch.call(doc, "Bookmarks", bookmarkName).toDispatch();
			return Dispatch.get(bookmark, "Range").toDispatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 */
	public void insertImageAtBookmark(String bookmarkName, String imagePath, Dispatch doc) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null)
			Dispatch.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath);
	}
	// /**
	// * 在指定的书签位置插入图片
	// *
	// * @param bookmarkName
	// * @param imagePath
	// */
	// public void insertImageAtBookmark(String bookmarkName, String
	// imagePath,int width,int height) {
	// Dispatch dispatch = getBookmark(bookmarkName, doc);
	// if (dispatch != null)
	// Dispatch.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(),
	// "AddPicture", imagePath);
	// }

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 * @param width
	 * @param height
	 */
	public void insertImageAtBookmark(String bookmarkName, String imagePath, int width, int height, Dispatch doc) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
			Dispatch.call(picture, "Select");
			Dispatch.put(picture, "Width", new Variant(width));
			Dispatch.put(picture, "Height", new Variant(height));
			// LockAspectRatio
		}
	}

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 * @param width
	 * @param height
	 */
	public void insertImageAtBookmark(Dispatch doc, String bookmarkName, String imagePath, float height) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
			Dispatch.call(picture, "Select");
			Dispatch.put(picture, "Height", new Variant(height));
			// LockAspectRatio
		}
	}

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 * @param width
	 * @param height
	 */
	public void insertImageAtBookmark(Dispatch doc, String bookmarkName, String imagePath, float heightOrWidth,
			String flag) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
			Dispatch.call(picture, "Select");
			Dispatch.put(picture, "LockAspectRatio", new Variant(true));
			if (flag.equals("width")) {
				Dispatch.put(picture, "Width", new Variant(heightOrWidth));
			} else if (flag.equals("height")) {
				Dispatch.put(picture, "Height", new Variant(heightOrWidth));
			}
			// LockAspectRatio
		}
	}

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 * @param width
	 * @param height
	 */
	public void insertImageAtBookmarkByMM(Dispatch doc, String bookmarkName, String imagePath, float heightOrWidth,
			String flag) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
			Dispatch.call(picture, "Select");
			heightOrWidth = heightOrWidth * (2.83f);
			Dispatch.put(picture, "LockAspectRatio", new Variant(true));
			if (flag.equals("width")) {
				Dispatch.put(picture, "Width", new Variant(heightOrWidth));
			} else if (flag.equals("height")) {
				Dispatch.put(picture, "Height", new Variant(heightOrWidth));
			}
			// LockAspectRatio
		}
	}

	/**
	 * 在指定的书签位置插入图片
	 * 
	 * @param bookmarkName
	 * @param imagePath
	 * @param width
	 * @param height
	 */
	public static void insertImageAtBookmarkByMM(String bookmarkName, String imagePath, float heightOrWidth,
			String flag, Dispatch doc) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null) {
			Dispatch picture = Dispatch
					.call(Dispatch.get(dispatch, "InLineShapes").toDispatch(), "AddPicture", imagePath).toDispatch();
			Dispatch.call(picture, "Select");
			heightOrWidth = heightOrWidth * (2.83f);
			Dispatch.put(picture, "LockAspectRatio", new Variant(true));
			if (flag.equals("width")) {
				Dispatch.put(picture, "Width", new Variant(heightOrWidth));
			} else if (flag.equals("height")) {
				Dispatch.put(picture, "Height", new Variant(heightOrWidth));
			}
			// LockAspectRatio
		}
	}

	/**
	 * 在指定的书签位置插入文本
	 * 
	 * @param bookmarkName
	 * @param text
	 */
	public void insertAtBookmark(String bookmarkName, String text, Dispatch doc) {
		Dispatch dispatch = getBookmark(bookmarkName, doc);
		if (dispatch != null)
			Dispatch.put(dispatch, "Text", text);
	}

	/**
	 * 根据书签替换文本
	 * 
	 * @param bookmarkName
	 * @param text
	 */
	public void replaceBookmark(Dispatch doc, HashMap<String, String> bookAndValue) {
		for (Entry<String, String> e : bookAndValue.entrySet()) {
			// System.out.println("replaceBk:" + e.getKey());
			Dispatch dispatch = getBookmark(e.getKey(), doc);
			if (dispatch != null) {
				String value = "";
				if (e.getValue() != null) {
					value = e.getValue();
				}
				Dispatch.put(dispatch, "Text", value);
			}
		}
	}

	/**
	 * 根据书签替换文本
	 * 
	 * @param bookmarkName
	 * @param text
	 */
	public static void replaceBookmark(HashMap<String, String> bookAndValue, Dispatch doc) {
		for (Entry<String, String> e : bookAndValue.entrySet()) {
			// System.out.println("replaceBk:" + e.getKey());
			Dispatch dispatch = getBookmark(e.getKey(), doc);
			if (dispatch != null) {
				String value = "";
				if (e.getValue() != null) {
					value = e.getValue();
				}
				Dispatch.put(dispatch, "Text", value);
			}
		}
	}

	/**
	 * 文档另存为
	 * 
	 * @param savePath
	 */
	public void saveAs(Dispatch doc, String savePath) {
		Dispatch.call(doc, "SaveAs", savePath);
	}

	/**
	 * 文档另存为PDF <b>
	 * <p>
	 * 注意：此操作要求word是2007版本或以上版本且装有加载项：Microsoft Save as PDF 或 XPS
	 * </p>
	 * </b>
	 * 
	 * @param savePath
	 */
	public void saveAsPdf(Dispatch doc, String savePath) {
		Dispatch.call(doc, "SaveAs", savePath, new Variant(17));
	}

	/**
	 * 保存文档
	 * 
	 * @param savePath
	 */
	public void save(String savePath) {
		Dispatch.call(Dispatch.call(activeX, "WordBasic").getDispatch(), "FileSaveAs", savePath);
	}

	/**
	 * 只适合一页的pdf
	 * 
	 * @param imagePath
	 * @param pdfPath
	 */
	public static void pdf2Image(String imagePath, String pdfPath) {
		File imgFile = new File(imagePath);
		File pdf = new File(pdfPath);
		try {
			PDDocument doc = PDDocument.load(pdf);
			PDFRenderer renderer = new PDFRenderer(doc);
			long time = System.currentTimeMillis();
			BufferedImage image = renderer.renderImageWithDPI(0, 200);
			// BufferedImage image = renderer.renderImage(i, 2.5f);
			String name = "pdfbox_image" + time + "_" + 0 + ".png";
			// ImageIO.write(image, "PNG", new File(imgFile.getParent(),
			// name));
			ImageIO.write(image, "PNG", imgFile);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭word文档
	 */
	public static void closeDocument(Dispatch doc, boolean ifSave) {
		if (doc != null) {
			Dispatch.call(doc, "Close", new Variant(ifSave));
		}
	}

	/**
	 * 调用word里的宏以调整表格的宽度,其中宏保存在document下
	 */
	public void callWordMacro(Dispatch doc) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();

		int count = Dispatch.get(tables, "Count").toInt();

		Variant vMacroName = new Variant("Normal.NewMacros.tableFit");

		@SuppressWarnings("unused")

		Variant vParam = new Variant("param1");

		@SuppressWarnings("unused")

		Variant para[] = new Variant[] { vMacroName };

		for (int i = 0; i < count; i++) {

			Dispatch table = Dispatch.call(tables, "Item", new Variant(i + 1))

					.toDispatch();

			Dispatch.call(table, "Select");

			Dispatch.call(doc, "Run", "tableFitContent");

		}

	}

	/**
	 * @author yzh 2015年5月26日 16:58:22
	 */
	public synchronized static void exit() {
		if (activeX != null) {
			activeX.invoke("Quit", new Variant[0]);
			activeX = null;
		}
	}

	/**
	 * 生成PDF文件
	 * 
	 * @author yzh 2014-12-24 09:07:31
	 * @param soruceFile
	 * @param outputFile
	 * @param replaceMap
	 *            key=oldValue value=newValue
	 */
	public boolean replacePlate(Dispatch doc, String soruceFile, String outputFile, Map<String, String> replaceMap) {
		boolean flag = true;
		Long startTime = System.currentTimeMillis();
		File oldFile = new File(soruceFile);
		// 为防止出现占用情况，每次生成word或pdf前，先将模版复制一份出来，
		// 使用复制的模版进行操作，用完后，删除复制的模版文件
		try {
			ComThread.InitMTA(true);
			// 打开模版world文件
			openDocument(oldFile.getAbsolutePath());
			// 替换文字
			if (!replaceMap.isEmpty()) {
				for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					replaceAllText(key, value);
				}
			}
			// 保存为pdf文件
			saveAsPdf(doc, outputFile);
		} catch (Exception e) {
			flag = false;
		} finally {
			closeDocument(doc, false);
			exit();
			Long endTime = System.currentTimeMillis();
		}
		return flag;
	}

	/**
	 * 生成WORD文件
	 * 
	 * @author yzh 2014-12-24 09:07:31
	 * @param soruceFile
	 * @param outputFile
	 * @param replaceMap
	 *            key=oldValue value=newValue
	 * @throws com.jxtech.jbo.util.JxException
	 */
	public boolean wordToWORD(Dispatch doc, String soruceFile, String outputFile, Map<String, String> replaceMap) {
		boolean flag = true;
		Long startTime = System.currentTimeMillis();
		File oldFile = new File(soruceFile);
		// 为防止出现占用情况，每次生成word或pdf前，先将模版复制一份出来，
		// 使用复制的模版进行操作，用完后，删除复制的模版文件
		try {
			ComThread.InitMTA(true);
			String path = oldFile.getAbsolutePath();
			// 打开模版world文件
			openDocument(path);
			// 替换文字
			if (!replaceMap.isEmpty()) {
				for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					replaceAllText(key, value);
				}
			}
			// 保存为pdf文件
			saveAs(doc, outputFile);
		} catch (Exception e) {
			flag = false;
		} finally {
			closeDocument(doc, false);
			exit();
			Long endTime = System.currentTimeMillis();
		}
		return flag;
	}

	public void fileCopy(InputStream in, String path) throws IOException {
		FileOutputStream fio = new FileOutputStream(path);
		int len = 0;
		byte[] buf = new byte[1024];
		while ((len = in.read(buf)) != -1) {
			fio.write(buf, 0, len);
		}
		fio.close();
	}
}
