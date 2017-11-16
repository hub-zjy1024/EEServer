package b1b.erp.js.servlet;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import b1b.erp.js.MyPDFPrintPage;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.WordUtils;

/**
 * Servlet implementation class PrintServlet
 */
@WebServlet("/PrintServlet")
public class PrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static String printer1 = "Lenovo LJ2250N";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PrintServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	ActiveXComponent activeX;
	// localhost:8080/PrinterService/PrintServlet?
	// localhost:8080/PrinterServer/PrintServlet?

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		String sendName = request.getParameter("sendName");
		String sendComp = request.getParameter("sendComp");
		String sendAddress = request.getParameter("sendAddress");
		String sendCz = request.getParameter("sendCz");
		String sendPhone = request.getParameter("sendPhone");
		String reName = request.getParameter("reName");
		String rePhone = request.getParameter("rePhone");
		String reAddresss = request.getParameter("reAddresss");
		String reComp = request.getParameter("reComp");
		String pidDate = request.getParameter("pidDate");
		String htID = request.getParameter("htID");
		String tableDetail = request.getParameter("tableDetail");
		String flag = request.getParameter("flag");
		String suffix = request.getParameter("filename");
		String lastName = "";
		if (suffix != null) {
			lastName = suffix.substring(suffix.lastIndexOf("."), suffix.length());
		}
		String wordDir = getServletContext().getInitParameter("dyjDir");
		System.out.println("get-flag:" + flag);
		System.out.println("content-length:" + request.getContentLength());
		// System.out.println("input-available:" +
		// request.getInputStream().available());
		if (flag != null) {
			if (flag.equals("excel")) {
				InputStream in = request.getInputStream();
				String savePath = wordDir + "print_excel/";
				String name = streamToNewFile(in, lastName, savePath, "word");
				printExcel(name, printer1);
			} else if (flag.equals("word")) {
				InputStream in = request.getInputStream();
				String savePath = wordDir + "print_word/";
				String name = streamToNewFile(in, lastName, savePath, "word");
				try {
					printWord( savePath + name, printer1);
					response.getWriter().append("ok:word:"+savePath+name);
				} catch (Exception e) {
					response.getWriter().append("error:"+e.getMessage());
					e.printStackTrace();
				}
				return;
			} else if (flag.equals("jpg") || flag.equals("jpeg") || flag.equals("png") || flag.equals("gif")) {
				try {
					InputStream in = request.getInputStream();
					String name = "img.png";
					String path = "D:/" + name;
					FileUtils.fileCopy(in, path);
					in.close();
					FileInputStream print = new FileInputStream(path);
					WordUtils.printImageByService(print, flag);
					response.getWriter().append("OK").close();
				} catch (Exception e) {
					e.printStackTrace();
					response.getWriter().append("ERROR:" + e.getMessage()).close();
				}
				return;
			}
		}
		System.out.println(tableDetail);
		// String[] datas = tableDetail.split("-");
		// List<String[]> list = new ArrayList();
		// list.add(WordUtils.SONGHUO_TABLE);
		// for (int i = 0; i < datas.length; i++) {
		// list.add(datas[i].split(","));
		// }
		String type = "";
		HashMap<String, String> map = new HashMap<>();
		map.put("送货单位", sendComp);
		map.put("制单日期", pidDate);
		map.put("合同编号", htID);
		map.put("收货人", reName);
		map.put("收货单位", reComp);
		map.put("收货地址", reAddresss);
		map.put("收货电话", rePhone);
		map.put("送货传真", sendCz);
		map.put("送货电话", sendPhone);
		map.put("送货地址", sendAddress);
		map.put("送货人", sendName);
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
		// ComThread.InitMTA(true);
		// activeX = new ActiveXComponent("Word.Application");
		// activeX = new ActiveXComponent("Excel.Application");
		// Dispatch.put(activeX, "Visible", new Variant(false));
		// // 这里Visible是控制文档打开后是可见还是不可见，若是静默打印，那么第三个参数就设为false就好了
		// Dispatch document = activeX.getProperty("Documents").toDispatch();
		boolean isOK = false;
		try {
			File templateFile = new File("d:/6.doc");
			FileInputStream fio = new FileInputStream(templateFile);
			String name = templateFile.getName();
			name = name.substring(0, name.lastIndexOf("."));
			String newPath = templateFile.getParentFile().getAbsolutePath() + name + System.currentTimeMillis()
					+ ".doc";
			FileUtils.fileCopy(fio, newPath);
			fio.close();
			System.out.println("createNew");
			// Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
			// new Object[] { newPath }, new int[1])
			// .toDispatch();
			// Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
			// new Object[] { "d:/excelTest.xls" }, new int[1])
			// .toDispatch();
			// WordUtils utils = new WordUtils(activeX);
			// Dispatch table = utils.getBookmark("明细表格", doc);
			// utils.insertTable(activeX, doc, WordUtils.SONGHUO_TABLE, 3, 7,
			// WordUtils.CHUKUDAN_COlUMN_WIDTH, table);
			// utils.insertTable(activeX, doc,
			// list,WordUtils.CHUKUDAN_COlUMN_WIDTH, table);
			// utils.replaceBookmark(doc, map);
			// utils.print(doc,"Lenovo LJ2250N");
			// utils.print(doc);
			// Dispatch.call(doc, "Close", new Variant(-1));
			// Dispatch.call(document, "Close", new Variant(0));
			// Dispatch template =
			// activeX.getProperty("NormalTemplate").toDispatch();
			// // 判断是否保存模板
			// boolean saved = Dispatch.get(template, "Saved").getBoolean();
			// if (!saved) {
			// // 保存模板
			// Dispatch.put(template, "Saved", true);
			// }
			// Dispatch.call(activeX, "Quit");
			// System.out.println("close");
			ComThread.Release();
			// // ComThread.quitMainSTA();
			isOK = true;
			// response.getWriter().append("1").close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		// response.setCharacterEncoding("UTF-8");
		if (isOK) {
			response.getWriter().append("成功打印: ").append(request.getContextPath());
		} else {
			response.getWriter().append("打印失败 : ").append(request.getContextPath());
		}
		response.getWriter().close();

	}

	public  static boolean printExcel(String filePath, String printerName) {
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

	public boolean printWord(String filePath, String printerName) throws Exception {
		try {
			ComThread.InitSTA(true);
			ActiveXComponent ac = new ActiveXComponent("Word.Application");
			ac.setProperty("Visible", new Variant(true));
			Dispatch doc = WordUtils.openDocument(filePath, ac);
			System.out.println("startPrint");
			WordUtils.print(doc, printerName, ac);
			WordUtils.exit(ac);
			// WordUtils.print(doc,"Microsoft XPS Document Writer", ac);
		} catch (Exception e) {
			throw e;
		} finally {
			ComThread.Release();
			// utils.exit();
		}
		return true;
	}

	private String streamToNewFile(InputStream in, String suffix, String savePath, String firstName) {
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String name = firstName +Myuuid.create(4)+ suffix;
		try {
			FileUtils.fileCopy(in, savePath + name);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		String printer = request.getParameter("printer");
		String flag = request.getParameter("flag");
		String filename = request.getParameter("filename");
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		System.out.println("recevie:" + printer);
		if (printer.equals("")) {
			printer = null;
		}
		printer1 = printer;
		String wordDir = getServletContext().getInitParameter("dyjDir");
		System.out.println("printerName:" + printer);
		String lastName = filename.substring(filename.lastIndexOf("."), filename.length());
		System.out.println("post-falg:" + flag);
		System.out.println(request.getContentLength());
		if (flag != null) {
			if (flag.equals("jpg") || flag.equals("jpeg") || flag.equals("png") || flag.equals("gif")) {
				InputStream in = request.getInputStream();
				String savePath = wordDir + "print_img/";
				String name = streamToNewFile(in, lastName, savePath, "img");
				FileInputStream inImg = new FileInputStream(savePath + name);
				System.out.println("path:" + (savePath + name));
				WordUtils.printImageByService(inImg, flag, printer);
				response.getWriter().append("ok").close();
			} else if (flag.equals("excel")) {
				InputStream in = request.getInputStream();
				String savePath = wordDir + "print_excel/";
				String name = streamToNewFile(in, lastName, savePath, "word");
				System.out.println("path:" + (savePath + name));
				printExcel( savePath + name, printer);
				response.getWriter().append("ok").close();
			} else if (flag.equals("word")) {
				InputStream in = request.getInputStream();
				String savePath = wordDir + "print_word/";
				String name = streamToNewFile(in, lastName, savePath, "word");
				System.out.println("path:" + (savePath + name));
				try {
					printWord( savePath + name, printer);
					response.getWriter().append("ok");
				} catch (Exception e) {
					response.getWriter().append("error:"+e.getMessage());
					e.printStackTrace();
				}
			} else if (flag.equals("pdf")) {
				String savePath = wordDir + "print_pdf/";
				InputStream in = request.getInputStream();
				try{
					WordUtils.printPDF(in, savePath,printer, 0, 0);
					response.getWriter().append("ok");
				}catch (IOException e) {
					response.getWriter().append("error:"+e.getMessage());
				}
			}
		}
	}

}