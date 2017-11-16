package b1b.erp.js.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.entity.GoodInfo;

/**
 * @author zjy 使用该工具类需要用到 jacob.jar 包及 jacob-1.18-M2-x86.dll/
 *         jacob-1.18-M2-x64.dll文件 .dll文件复制到服务端 %java_home%/bin下 32位系统使用
 *         x86，64位使用x64 本地打印工具类，使用程序所在电脑能使用的打印机打印
 *         该工具类使用前提：必须安装能打开word，excel的程序，如wps、office；
 *         打印PDF文件使用到fontbox-2.0.6.jar,pdfbox-2.0.6.jar,levigo-jbig2-imageio-2.0.jar
 */
public class MyWordUtils {
	/**
	 * 根据书签替换文本
	 * 
	 * @param bookmarkName
	 * @param text
	 */
	public static void replaceBookmark(HashMap<String, String> bookAndValue, Dispatch doc) {
		for (Entry<String, String> e : bookAndValue.entrySet()) {
			Dispatch bookMark = getBookmark(e.getKey(), doc);
			if (bookMark != null) {
				String value = "";
				if (e.getValue() != null) {
					value = e.getValue();
				}
				Dispatch.put(bookMark, "Text", value);
			}
		}
	}

	/**
	 * 获取书签的位置
	 * 
	 * @param bookmarkName
	 * @return 书签的位置
	 */
	public static Dispatch getBookmark(String bookmarkName, Dispatch doc) {
		try {
			Dispatch bookMarks = Dispatch.call(doc, "Bookmarks").toDispatch();
			// 检测书签是否存在
			boolean isExists = Dispatch.call(bookMarks, "Exists", bookmarkName).getBoolean();
			if (!isExists) {
				System.err.println("not found bookmark:" + bookmarkName);
				return null;
			} else {
				Dispatch bookmark = Dispatch.call(doc, "Bookmarks", bookmarkName).toDispatch();
				return Dispatch.get(bookmark, "Range").toDispatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * word转pdf，需要saveAsPDF插件支持，如果word的另存为选项中有pdf格式，即已安装
	 * 
	 * @param docfile
	 * @param pdfPath
	 */
	public static void word2pdf(String docfile, String pdfPath, ActiveXComponent wordApp) {
		// 启动word应用程序(Microsoft Office Word 2003)
		try {
			System.out.println("*****正在转换...*****");
			Dispatch docs = wordApp.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", docfile).toDispatch();
			word2pdf(doc, pdfPath, wordApp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("*****转换完毕********");
	}

	public void insertTable(ActiveXComponent ac, Dispatch doc, String[] tableTitle, int rowId, int colId,
			Dispatch selection, java.util.List<GoodInfo> list) {
		// 建立表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		// /当前光标位置或者选中的区域
		Dispatch newTable = Dispatch
				.call(tables, "Add", selection, new Variant(rowId), new Variant(colId), new Variant(1)).toDispatch(); // 设置row,column,表格外框宽度
		Dispatch cols = Dispatch.get(newTable, "Columns").toDispatch(); // 此表的所有列，
		int colCount = Dispatch.get(cols, "Count").changeType(Variant.VariantInt).getInt();// 一共有多少列
		System.out.println(colCount + "列");
		int[] len = new int[] { 40, 90, 90, 50, 50, 70, 80 };
		//行和列序号从1开始
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
			setColumnWidth(col, (float) len[i - 1]);
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				if (j == 1) {
					putTxtToCell(newTable, j, i, tableTitle[i - 1], ac);
				} else {
					String text = "";
					switch (i) {
					case 1:
						text = String.valueOf(j - 1);
						break;
					case 2:
						text = list.get(j - 2).getPartno();
						break;
					case 3:
						text = list.get(j - 2).getBrand();
						break;
					case 4:
						text = list.get(j - 2).getCouts();
						break;
					case 5:
						text = list.get(j - 2).getPrice();
						break;
					case 6:
						text = list.get(j - 2).getTotalprice();
						break;
					case 7:
						text = list.get(j - 2).getMark();
						break;
					}
					putTxtToCell(newTable, j, i, text, ac);
				}
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
	public void putTxtToCell(Dispatch table, int cellRowIdx, int cellColIdx, String txt, ActiveXComponent ac) {
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(cellRowIdx), new Variant(cellColIdx)).toDispatch();
		// Dispatch selection = Dispatch.call(cell, "Select").toDispatch();
		Dispatch.call(cell, "Select");
		// Dispatch selection = getSelection(); // 输入内容需要的对象
		Dispatch selection = Dispatch.get(ac, "Selection").toDispatch();
		Dispatch.put(selection, "Text", txt);
	}

	public void setColumnWidth(Dispatch column, float columnWidth) {
		Dispatch.put(column, "Width", new Variant(columnWidth));
	}

	/**
	 * word转pdf，需要saveAsPDF插件支持，如果word的另存为选项中有pdf格式，即已安装
	 * 
	 * @param docfile
	 * @param pdfPath
	 */
	public static void word2pdf(Dispatch doc, String pdfPath, ActiveXComponent wordApp) {
		try {
			// 17为pdf格式
			Dispatch.call(doc, "ExportAsFixedFormat", pdfPath, new Variant(17));
			// 关闭word文件,0不保存，-2，提示保存，-1直接保存
			// Dispatch.call(doc, "Close", new Variant(-1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("*****转换完毕********");
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
	 * 打印Excel文件，Excel文件的打印设置打印机方式和word不相同
	 * 
	 * @param filePath
	 * @param printerName
	 * @return
	 */
	public static boolean printExcel(String filePath, String printerName) {
		ComThread.InitMTA(true);
		ActiveXComponent activeX = new ActiveXComponent("Excel.Application");
		Dispatch.put(activeX, "Visible", new Variant(true));
		Dispatch workbooks = activeX.getProperty("Workbooks").toDispatch();
		// 打开文档
		Dispatch excel = Dispatch.call(workbooks, "Open", filePath).toDispatch();
		// 每张表都横向打印2013-10-31
		Dispatch sheets = Dispatch.get(excel, "Sheets").toDispatch();
		// 获得几个sheet
		int count = Dispatch.get(sheets, "Count").getInt();
		System.out.println(count);
		for (int j = 1; j <= count; j++) {
			Dispatch sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[] { new Integer(j) }, new int[1])
					.toDispatch();
			Dispatch pageSetup = Dispatch.get(sheet, "PageSetup").toDispatch();
			Dispatch.put(pageSetup, "Orientation", new Variant(2));
			// Dispatch.call(sheet, "PrintOut");
		}
		// 开始打印
		if (excel != null) {
			System.out.println("startPrint");
			if (printerName == null) {
				Dispatch.call(excel, "PrintOut");
			} else {
				// Excel 打印参数 arglist
				// 参数1-数值：起始页号，省略则默认为开始位置
				// 参数2-数值：终止页号，省略则默认为最后一页
				// 参数3-数值：打印份数，省略则默认为1份
				// 参数4-逻辑值：是否预览，省略则默认为直接打印(.F.)
				// 参数5-字符值：设置活动打印机名称，省略则为默认打印机
				// 参数6-逻辑值：是否输出到文件，省略则默认为否(.F.)，若选.T.且参数8为空，则Excel提示输入要输出的文件名
				// 参数7-逻辑值：输出类型，省略则默认为(.T.)逐份打印，否则逐页打印
				// 参数8-字符值：当参数6为.T.时，设置要打印到的文件名
				Dispatch.callN(excel, "PrintOut", new Object[] { Variant.VT_MISSING, Variant.VT_MISSING, new Integer(1),
						new Boolean(false), printerName, new Boolean(false), Variant.VT_MISSING, "" });
			}
			Dispatch.call(excel, "save");
		}
		activeX.invoke("Quit", new Variant[] {});
		ComThread.Release();
		return true;
	}

	/**
	 * 打印pdf文件，实际上是PDF转png，然后打印png图片，使用pdfbox
	 * 
	 * @param in
	 * @param outImgDir
	 *            存储转换后的图片的文件夹
	 * @param printerName
	 * @param from
	 *            起始页
	 * @param to
	 *            终止页
	 */
	public static void printPDF(InputStream in, String outImgDir, String printerName, int from, int to) {
		File dir = new File(outImgDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			PDDocument doc = PDDocument.load(in);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();
			long time = System.currentTimeMillis();
			String random = String.valueOf(Math.random()).substring(2, 6);
			for (int i = 0; i < pageCount; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 296);
				// BufferedImage image = renderer.renderImage(i, 2.5f);
				if (from == 0 && to == 0) {
					String name = "pdfimg_" + time + random + "_" + i + ".png";
					ImageIO.write(image, "PNG", new File(dir, name));
					printImageByService(new FileInputStream(outImgDir + name), "png", printerName);
				} else if (i >= from && i <= to) {
					String name = "pdfimg_" + time + "_" + i + ".png";
					ImageIO.write(image, "PNG", new File(dir, name));
					printImageByService(new FileInputStream(outImgDir + name), "png", printerName);
				}
			}
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭word文档
	 * 
	 * @param doc
	 *            文档Dispatch对象
	 * @param ifSave
	 *            是否保存
	 */
	public static void closeDocument(Dispatch doc, boolean ifSave) {
		if (doc != null) {
			if (ifSave) {
				Dispatch.call(doc, "Close", new Variant(-1));
			} else {
				Dispatch.call(doc, "Close", new Variant(0));
			}

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
		if (printerName != null && !printerName.equals("")) {
			for (int i = 0; i < totalService.length; i++) {
				if (totalService[i].getName().equals(printerName)) {
					service = totalService[i];
					break;
				}
			}
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
		}
	}

	/**
	 * 打印
	 * 
	 * @param doc
	 *            待打印的文档对象
	 * @param printerName
	 *            打印机名称无效时不改变默认打印机
	 * @param ac
	 *            Word主程序
	 */
	public synchronized static void print(Dispatch doc, String printerName, ActiveXComponent ac) {
		if (printerName != null) {
			String nowPrinter = ac.getProperty("ActivePrinter").toString();
			if (nowPrinter != null) {
				if (!nowPrinter.equals(printerName)) {
					System.out.println("start set printer");
					ac.setProperty("ActivePrinter", new Variant(printerName));
				}
			} else {
				System.out.println("start set printer");
				ac.setProperty("ActivePrinter", new Variant(printerName));
			}
		}
		Dispatch.call(doc, "PrintOut");
	}

	/**
	 * 打开一个已经存在的word文档
	 *
	 * @param docPath
	 */
	public static Dispatch openDocument(String docPath, ActiveXComponent activex) {
		Dispatch docs = activex.getProperty("Documents").toDispatch();
		Dispatch doc = Dispatch.call(docs, "Open", docPath).toDispatch();
		return doc;
	}

	/**
	 * 退出ActiveXComponent
	 * 
	 * @param activeX
	 */
	public static void exit(ActiveXComponent activeX) {
		activeX.invoke("Quit", new Variant[] { new Variant(-1) });
	}

	/**
	 * 打印word文档
	 * 
	 * @param filePath
	 * @param printerName
	 * @return
	 * @throws Exception
	 */
	public boolean printWord(String filePath, String printerName) throws Exception {
		try {
			ComThread.InitSTA(true);
			ActiveXComponent ac = new ActiveXComponent("Word.Application");
			ac.setProperty("Visible", new Variant(true));
			Dispatch doc = openDocument(filePath, ac);
			System.out.println("startPrint");
			print(doc, printerName, ac);
			exit(ac);
			// WordUtils.print(doc,"Microsoft XPS Document Writer", ac);
		} catch (Exception e) {
			throw e;
		} finally {
			ComThread.Release();
			// utils.exit();
		}
		return true;
	}
}
