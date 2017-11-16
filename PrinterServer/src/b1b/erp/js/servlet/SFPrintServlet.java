package b1b.erp.js.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.DownUtils;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.SingleActiveXComponent;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.utils.WordUtils;

/**
 * Servlet implementation class SFPrintServlet
 */
@WebServlet("/SFPrintServlet")
public class SFPrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ActiveXComponent ac;

	// localhost:8080/PrinterServer/SFPrintServlet
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SFPrintServlet() {
		super();
	}

	public static String rootPath;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		// localhost:8080/PrinterServer/SFPrintServlet?goodinfos=url1-500,url2-6000,url3-700&orderID=123456789123&baojiaprice=100&cardID=12345678&
		// payType=寄方&payPerson=&serverType=标准快递&j_name=张三&j_phone=1234567&
		// j_address=北京市海淀区&d_name=张三&d_phone=1234567&d_address=北京市海淀区&
		// yundanType=150&hasE=1
		String orderID = request.getParameter("orderID");
		String goodinfos = request.getParameter("goodinfos");
		String price = request.getParameter("baojiaprice");
		String cardID = request.getParameter("cardID");
		String destcode = request.getParameter("destcode");
		String yundanType = request.getParameter("yundanType");
		String printer = request.getParameter("printer");
		String hasE = request.getParameter("hasE");
		String[] orders = orderID.split(",");
		if (destcode == null) {
			destcode = "1024";
		}
		// 寄方，到方，第三方
		String payType = request.getParameter("payType");
		String payPerson = request.getParameter("payPerson");
		if (price != null) {
			if (price.equals("-1.0")) {
				price = "";
			}
		} else {
			price = "";
		}
		// String payType = request.getParameter("payType");
		String serverType = request.getParameter("serverType");
		String jName = request.getParameter("j_name");
		String jPhone = request.getParameter("j_phone");
		String jAddress = request.getParameter("j_address");
		String dName = request.getParameter("d_name");
		String dPhone = request.getParameter("d_phone");
		String dAddress = request.getParameter("d_address");
		if (jName == null) {
			jName = "管理员1";
		}
		if (jPhone == null) {
			jPhone = "18855587255";
		}
		if (jAddress == null) {
			jAddress = "北京市海淀区";
		}
		if (dName == null) {
			dName = "李四";
		}
		if (dPhone == null) {
			dPhone = "16202021240";
		}
		if (dAddress == null) {
			dAddress = "北京市海淀区";
		}
		String[] infos;
		if (goodinfos != null) {
			infos = goodinfos.split("\\$");
		} else {
			infos = new String[] { "test1&1000", "test3&1024", "test2&500", "test" };
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < infos.length; i++) {
			if (i == 3) {
				if (infos.length > 3) {
					builder.append("\t等");
				}
				break;
			}
			String[] s = infos[i].split("&");
			if (s.length != 1) {
				builder.append(s[0] + " ：" + s[1]);
				if (i != 2) {
					builder.append("\n");
				}
			}
		}
		System.out.println("=====" + DownUtils.getTimeAtSS() + "=====");
		System.out.println("builder:" + builder.toString());
		String wordDir = getServletContext().getInitParameter("dyjDir");
		rootPath = wordDir;
		PrintWriter writer = response.getWriter();
		try {
			for (int i = 0; i < orders.length; i++) {
				printYunDan(i, orders, response, price, cardID, destcode, yundanType, printer, payType, payPerson,
						serverType, jName, jPhone, jAddress, dName, dPhone, dAddress, builder, hasE, request);
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			writer.write("error=" + msg);
			writer.flush();
			writer.close();
			System.out.println(msg);
			return;
		}
		writer.write("ok");
		writer.flush();
		writer.close();
	}

	public String getCurrentDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.format(new Date());
		return sdf.format(new Date());
				
	}
	private void printYunDan(int index, String[] orders, HttpServletResponse response, String price, String cardID,
			String destcode, String yundanType, String printer, String payType, String payPerson, String serverType,
			String jName, String jPhone, String jAddress, String dName, String dPhone, String dAddress,
			StringBuilder builder, String hasE, HttpServletRequest request)
			throws IOException, FileNotFoundException, UnsupportedEncodingException {
		String templatePath = request.getServletContext().getRealPath("/docTemplate/sf210模板.doc");
		String mainOrder = orders[0];
		String cOrder = orders[index];
		System.out.println("currentOrder:" + cOrder);
		if (yundanType.equals("150")) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf150模板.doc");
		}
		FileInputStream testInputStream = new FileInputStream(templatePath);
		if (mainOrder == null) {
			response.getWriter().append("error:运单号参数错误");
			testInputStream.close();
			return;
		}
		if (testInputStream != null) {
			String savePath = rootPath + "/SF/"+UploadUtils.getCurrentYearAndMonth()+"/";
			String imgPath=savePath+"codeImg/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File imgDir = new File(imgPath);
			if (!imgDir.exists()) {
				imgDir.mkdirs();
			}
			String name = UploadUtils.getCurrentDay()+"_" + Myuuid.create(4) + ".doc";
			String wordPath=savePath+name;
			FileUtils.fileCopy(testInputStream, wordPath);
			testInputStream.close();
			ActiveXComponent ac = SingleActiveXComponent.getApp();
			Dispatch doc = WordUtils.openDocument(wordPath, ac);
			HashMap<String, String> bMarksAndValue = new HashMap<>();
			bMarksAndValue.put("业务类型", serverType);
			int len = orders.length;
			if (len > 1) {
				bMarksAndValue.put("件数", (index + 1) + "/" + len);
				if (index == 0) {
					bMarksAndValue.put("主单号", "母单号  " + mainOrder);
				} else {
					bMarksAndValue.put("主单号", "子单号  " + cOrder + "\n母单号  " + mainOrder);
				}
			} else {
				bMarksAndValue.put("主单号", "单号  " + mainOrder);
			}
			bMarksAndValue.put("目的地", destcode);
			bMarksAndValue.put("付款方式", payType);
			bMarksAndValue.put("派件提示", "");
			bMarksAndValue.put("月结账号", cardID);
			bMarksAndValue.put("第三方地区", payPerson);
			bMarksAndValue.put("声明价格", price);

			bMarksAndValue.put("收件人", dName);
			bMarksAndValue.put("收件人电话", dPhone);
			bMarksAndValue.put("收件地址", dAddress);

			bMarksAndValue.put("寄件人", jName);
			bMarksAndValue.put("寄件人电话", jPhone);
			bMarksAndValue.put("寄件人地址", jAddress);
			if (hasE.equals("1")) {
				bMarksAndValue.put("E标", "E");
			}
			bMarksAndValue.put("托寄物",builder.toString());
			bMarksAndValue.put("子单号1", cOrder);
			bMarksAndValue.put("收件人1", dName);
			bMarksAndValue.put("收件人1电话", dPhone);
			bMarksAndValue.put("收件人1地址", dAddress);
			bMarksAndValue.put("寄件人1", jName);
			bMarksAndValue.put("寄件人1电话", jPhone);
			bMarksAndValue.put("寄件人1地址", jAddress);
			bMarksAndValue.put("备注1",	getCurrentDate()+ builder.toString());
			if (yundanType.equals("210")) {
				bMarksAndValue.put("子单号2", cOrder);
				bMarksAndValue.put("收件人2", dName);
				bMarksAndValue.put("收件人2电话", dPhone);
				bMarksAndValue.put("收件人2地址", dAddress);
				bMarksAndValue.put("寄件人2", jName);
				bMarksAndValue.put("寄件人2电话", jPhone);
				bMarksAndValue.put("寄件人2地址", jAddress);
				bMarksAndValue.put("备注2", 	getCurrentDate()+builder.toString());
			}
			WordUtils.replaceBookmark(bMarksAndValue, doc);
			int childWidth = 45;
			String childImgPath = imgPath+name + ".png";
			Code128CCreator codeCreator = new Code128CCreator();
			String barCode = codeCreator.getCode(cOrder, "");
			codeCreator.kiCode128C(barCode, 2, 35, childImgPath);
			WordUtils.insertImageAtBookmarkByMM("子条码1", childImgPath, childWidth, "width", doc);
			if (yundanType.equals("210")) {
				WordUtils.insertImageAtBookmarkByMM("子条码2", childImgPath, childWidth, "width", doc);
			}
			WordUtils.insertImageAtBookmarkByMM("主条码", childImgPath, childWidth, "width", doc);
			Dispatch docs= ac.getProperty("Documents").getDispatch();
			WordUtils.save(docs);
			String nowPrinter = ac.getProperty("ActivePrinter").toString();
			System.out.println("nowPrinter:" + nowPrinter);
			 WordUtils.print(doc, printer, ac);
			try {
				WordUtils.closeDocument(doc, true);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		BufferedReader reader = request.getReader();
		Map<String, String> map = new HashMap<>();
		String s = null;
		while ((s = reader.readLine()) != null) {
			String[] pm = s.split("=");
			if (pm.length == 1) {
				System.out.println(s);
				map.put(pm[0], "");
			} else {
				map.put(pm[0], pm[1]);
			}
		}
		String orderID = map.get("orderID");
		String goodinfos = map.get("goodinfos");
		String price = map.get("baojiaprice");
		String cardID = map.get("cardID");
		String destcode = map.get("destcode");
		String yundanType = map.get("yundanType");
		String printer = map.get("printer");
		String hasE = map.get("hasE");
		String serverType = map.get("serverType");
		String jName = map.get("j_name");
		String jPhone = map.get("j_phone");
		String jAddress = map.get("j_address");
		String dName = map.get("d_name");
		String dPhone = map.get("d_phone");
		String dAddress = map.get("d_address");
		// 寄方，到方，第三方
		String payType = map.get("payType");
		String payPerson = map.get("payPerson");
		if (destcode == null) {
			destcode = "1024";
		}

		String[] orders = orderID.split(",");
		if (price != null) {
			if (price.equals("-1.0")) {
				price = "";
			}
		} else {
			price = "";
		}
		if (jName == null) {
			jName = "管理员1";
		}
		if (jPhone == null) {
			jPhone = "18855587255";
		}
		if (jAddress == null) {
			jAddress = "北京市海淀区";
		}
		if (dName == null) {
			dName = "李四";
		}
		if (dPhone == null) {
			dPhone = "16202021240";
		}
		if (dAddress == null) {
			dAddress = "北京市海淀区";
		}
		String[] infos;
		if (goodinfos != null) {
			infos = goodinfos.split("\\$");
		} else {
			infos = new String[] { "test1&1000", "test3&1024", "test2&500", "test&787" };
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < infos.length; i++) {
			if (i == 3) {
				if (infos.length > 3) {
					builder.append("\t等");
				}
				break;
			}
			String[] str = infos[i].split("&");
			if (str.length != 1) {
				builder.append(str[0] + " ：" + str[1]);
				if (i != 2) {
					builder.append("\n");
				}
			}
		}
		System.out.println("builder:" + builder.toString());
		String wordDir = getServletContext().getInitParameter("dyjDir");
		rootPath = wordDir;
		File barcodeDir = new File(rootPath, "/SF/codeImg/");
		if (!barcodeDir.exists()) {
			barcodeDir.mkdirs();
		}
		System.out.println(barcodeDir.getAbsolutePath());
		ComThread.InitSTA(true);
		// ComThread.InitMTA(true);
		PrintWriter writer = response.getWriter();
		for (int i = 0; i < orders.length; i++) {
			printYunDan(i, orders, response, price, cardID, destcode, yundanType, printer, payType, payPerson,
					serverType, jName, jPhone, jAddress, dName, dPhone, dAddress, builder, hasE, request);
		}
		writer.write("ok");
		writer.flush();
		writer.close();
		ComThread.Release();
		response.getWriter().append("over");
	}

}
