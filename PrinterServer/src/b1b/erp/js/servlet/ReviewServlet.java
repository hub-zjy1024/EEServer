package b1b.erp.js.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.WordUtils;

/**
 * Servlet implementation class ReviewServlet
 */
@WebServlet("/ReviewServlet")
public class ReviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ActiveXComponent activeX;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReviewServlet() {
		super();
	}

	// http://localhost:8080/PrinterServer/ReviewServlet?goodinfos=url1-500,url2-6000,url3-700&orderID=123456789123
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		String orderID = request.getParameter("orderID");
		// localhost:8080/PrinterServer/ReviewServlet?goodinfos=url1-500,url2-6000,url3-700
		// &orderID=123456789123&baojiaprice=100&cardID=12345678&payType=寄方&payPerson=&serverType=标准快递
		// &j_name=张三&j_phone=1234567&j_address=北京市海淀区&d_name=李四&d_phone=18865240122&d_address=湖北省荆州市
		String goodinfos = request.getParameter("goodinfos");
		String price = request.getParameter("baojiaprice");
		String destcode = request.getParameter("destcode");
		String yundanType = request.getParameter("yundanType");
		String hasE = request.getParameter("hasE");
		String wordDir = getServletContext().getInitParameter("dyjDir");
		String templatePath = request.getServletContext().getRealPath("/docTemplate/sf210模板.doc");
		if (yundanType.equals("150")) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf150模板.doc");
		}
		FileInputStream testInputStream = new FileInputStream(templatePath);
		if (destcode == null) {
			destcode = "1024";
		}
		if (price != null) {
			if (price.equals("-1.0")) {
				price = "";
			}
		} else {
			price = "";
		}

		String cardID = request.getParameter("cardID");
		// 寄方，到方，第三方
		String payType = request.getParameter("payType");
		String payPerson = request.getParameter("payPerson");
		String serverType = request.getParameter("serverType");
		String jName = request.getParameter("j_name");
		String jPhone = request.getParameter("j_phone");
		String jAddress = request.getParameter("j_address");
		String dName = request.getParameter("d_name");
		String dPhone = request.getParameter("d_phone");
		String dAddress = request.getParameter("d_address");
		if (orderID == null) {
			response.getWriter().append("error:参数错误");
			testInputStream.close();
			return;
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
			infos = goodinfos.split(",");
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
		String[] orders = orderID.split(",");
		System.out.println("builder:" + builder.toString());
		if (testInputStream != null) {
			String savePath = wordDir+"/SF/reviewWord/";
			ComThread.InitSTA(true);
			ActiveXComponent activeX = new ActiveXComponent("Word.Application");
			activeX.setProperty("Visible", new Variant(true));
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String name = "word" + Myuuid.create(4) + ".doc";
			FileUtils.fileCopy(testInputStream, savePath + name);
			testInputStream.close();
			Dispatch doc;
			doc = WordUtils.openDocument(savePath + name, activeX);
			HashMap<String, String> bMarksAndValue = new HashMap<>();
			bMarksAndValue.put("业务类型", serverType);
			bMarksAndValue.put("主单号", orderID);
			bMarksAndValue.put("目的地", destcode);
			bMarksAndValue.put("付款方式", payType);
			bMarksAndValue.put("派件提示", "");
			bMarksAndValue.put("月结账号", cardID);
			bMarksAndValue.put("第三方地区", payPerson);
			bMarksAndValue.put("声明价格", price);
			if (hasE.equals("1")) {
				bMarksAndValue.put("E标", "E");
			}
			bMarksAndValue.put("收件人", dName);
			bMarksAndValue.put("收件人电话", dPhone);
			bMarksAndValue.put("收件地址", dAddress);

			bMarksAndValue.put("寄件人", jName);
			bMarksAndValue.put("寄件人电话", jPhone);
			bMarksAndValue.put("寄件人地址", jAddress);

			bMarksAndValue.put("托寄物", builder.toString());
			// bMarksAndValue.put("收件员", "12345268");
			// bMarksAndValue.put("收件时间", "2017.6.20");
			bMarksAndValue.put("子单号1", orderID);
			bMarksAndValue.put("收件人1", dName);
			bMarksAndValue.put("收件人1电话", dPhone);
			bMarksAndValue.put("收件人1地址", dAddress);
			bMarksAndValue.put("寄件人1", jName);
			bMarksAndValue.put("寄件人1电话", jPhone);
			bMarksAndValue.put("寄件人1地址", jAddress);
			bMarksAndValue.put("备注1", builder.toString());
			if (yundanType.equals("210")) {
				bMarksAndValue.put("子单号2", orderID);
				bMarksAndValue.put("收件人2", dName);
				bMarksAndValue.put("收件人2电话", dPhone);
				bMarksAndValue.put("收件人2地址", dAddress);
				bMarksAndValue.put("寄件人2", jName);
				bMarksAndValue.put("寄件人2电话", jPhone);
				bMarksAndValue.put("寄件人2地址", jAddress);
				bMarksAndValue.put("备注2", builder.toString());
			}
			WordUtils.replaceBookmark(bMarksAndValue, doc);
			int widthOffset = 2;
			int width2 = 45;
			
			int widthChild = 45;
			int widthMain = 60;
			File barcodeDir = new File(wordDir, "/SF/codeImg/");
			if (!barcodeDir.exists()) {
				barcodeDir.mkdirs();
			}
			String mainOrder = orders[0];
			String cImagName = "/" + mainOrder + ".png";
			String cImgPath = barcodeDir.getAbsolutePath() + cImagName;
			String mainImgPath = barcodeDir.getAbsolutePath() + "/" + mainOrder + ".png";
			File childCode = new File(cImagName);
			if (!childCode.exists()) {
				Code128CCreator c = new Code128CCreator();
				String barCode = c.getCode(mainOrder, "");
				c.kiCode128C(barCode, 2, 35, cImgPath);
//				BitMatrix matrix;
//				FileOutputStream fos=null;
//				try {
//					int height = 30;
//					int height2 = 38;
//					matrix = new MultiFormatWriter().encode(orderID, BarcodeFormat.CODE_128, width, height2);
//					fos = new FileOutputStream(cImgPath);
//					MatrixToImageWriter.writeToStream(matrix, "png", fos);
//					fos.close();
//					matrix = new MultiFormatWriter().encode(orderID, BarcodeFormat.CODE_128, width, height);
//					fos = new FileOutputStream(mainImgPath);
//					MatrixToImageWriter.writeToStream(matrix, "png", fos);
//					fos.close();
//				} catch (com.jacob.com.ComFailException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if(fos!=null){
//					fos.close();
//				}
			}
			WordUtils.insertImageAtBookmarkByMM("子条码1", cImgPath, widthChild, "width", doc);
			if (yundanType.equals("210")) {
				WordUtils.insertImageAtBookmarkByMM("子条码2", cImgPath, widthChild, "width", doc);
			}
			WordUtils.insertImageAtBookmarkByMM("主条码", cImgPath, widthMain, "width", doc);
			WordUtils.closeDocument(doc, true);
			String pdfPath = savePath + name + ".pdf";
			WordUtils.word2pdf(savePath+name, pdfPath, activeX);
			WordUtils.pdf2Image(savePath + name + ".png", pdfPath);
			FileInputStream fin = new FileInputStream(savePath + name + ".png");
			OutputStream out = response.getOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = fin.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			fin.close();
			out.close();
			WordUtils.exit(activeX);
			ComThread.Release();
		}
//		OutputStream out = response.getOutputStream();
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
//		PrintWriter writer=response.getWriter();
//		writer.write("预览完成");
//		writer.flush();
//		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
