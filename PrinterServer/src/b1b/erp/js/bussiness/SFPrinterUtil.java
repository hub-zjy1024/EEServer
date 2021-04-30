package b1b.erp.js.bussiness;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zjy.print.bussiness.DocxPrinter;
import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.util.DocxManager;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.UploadUtils;

public class SFPrinterUtil {

	private String pid;
	private String officeHome;
	private String rootPath;

	public void startPrint(HttpServletRequest request, HttpServletResponse response) {
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
		String jCompany = request.getParameter("j_company");
		String dCompany = request.getParameter("d_company");
		if (printer == null) {
			printer = "";
		}
		printer = request.getServletContext().getInitParameter("printer1");
		pid = request.getParameter("pid");
		if (jCompany == null) {
			jCompany = "";
		}
		if (dCompany == null) {
			dCompany = "";
		}
		if (pid == null) {
			pid = "";
		}
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
		System.out.println("goodinfos :" + goodinfos);
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
		String wordDir = request.getServletContext().getInitParameter("dyjDir");
		
		rootPath = wordDir;
		officeHome = request.getServletContext().getInitParameter("openoffice_home");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			for (int i = 0; i < orders.length; i++) {
				printYunDan(i, orders, response, price, cardID, destcode, yundanType, printer,
						payType, payPerson, serverType, jName, jPhone, jAddress, dName, dPhone,
						dAddress, builder, hasE, jCompany, dCompany, request);
			}
			writer.write("ok");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			String msg = e.getMessage();
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintWriter exWriter = new PrintWriter(bao);
			e.printStackTrace(exWriter);
			exWriter.flush();
			try {
				String result = new String(bao.toByteArray(), "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			exWriter.close();
			writer.write("error:" + msg);
			writer.flush();
			writer.close();
		}
	}

	private void printYunDan(int index, String[] orders, HttpServletResponse response, String price,
			String cardID, String destcode, String yundanType, String printer, String payType,
			String payPerson, String serverType, String jName, String jPhone, String jAddress,
			String dName, String dPhone, String dAddress, StringBuilder builder, String hasE,
			String jC, String dC, HttpServletRequest request) throws IOException, OfficeException,
			FileNotFoundException, UnsupportedEncodingException {
		String templatePath = request.getServletContext().getRealPath("/docTemplate/sf210模板.docx");
		String mainOrder = orders[0];
		String cOrder = orders[index];
		if (yundanType.equals("150")) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf150模板.docx");
		}
		File mFile = new File(templatePath);
		if (mainOrder == null) {
			throw new IOException("error:运单号参数错误");
		}
		if (mFile.exists()) {
			FileInputStream testInputStream = new FileInputStream(templatePath);
			String savePath = rootPath + "SF/" + UploadUtils.getCurrentYearAndMonth() + "/";
			String imgPath = savePath + "codeImg/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File imgDir = new File(imgPath);
			if (!imgDir.exists()) {
				imgDir.mkdirs();
			}
			String name = UploadUtils.getCurrentDay() + "-" + pid + "_" + cOrder + "_"
					+ Myuuid.createRandom(4) + ".docx";
			HashMap<String, Object> bMarksAndValue = new HashMap<>();
			bMarksAndValue.put("${type}", serverType);
			int len = orders.length;
			if (len > 1) {
				name = name + "_multiple.docx";
				bMarksAndValue.put("${cs}", (index + 1) + "/" + len);
				if (index == 0) {
					bMarksAndValue.put("${morderid}", "母单号  " + mainOrder);
					bMarksAndValue.put("${corderid}", "");
				} else {
					bMarksAndValue.put("${morderid}", "子单号  " + cOrder);
					bMarksAndValue.put("${corderid}", "母单号  " + mainOrder);
				}
			} else {
				bMarksAndValue.put("${corderid}", "");
				bMarksAndValue.put("${cs}", "");
				bMarksAndValue.put("${morderid}", "单号  " + mainOrder);
			}

			String wordPath = savePath + name;
			FileUtils.fileCopy(testInputStream, wordPath);
			testInputStream.close();
			bMarksAndValue.put("${destcode}", destcode);
			bMarksAndValue.put("${paytype}", payType);
			bMarksAndValue.put("${account}", cardID);
			bMarksAndValue.put("${part}", payPerson);
			bMarksAndValue.put("${price}", price);
			bMarksAndValue.put("${dname}", dName);
			bMarksAndValue.put("${dphone}", dPhone + " " + dC);
			bMarksAndValue.put("${daddress}", dAddress);
			bMarksAndValue.put("${jname}", jName);
			bMarksAndValue.put("${jphone}", jPhone);
			bMarksAndValue.put("${jaddress}", jAddress + " " + jC);
			bMarksAndValue.put("${orderid}", cOrder);
			if (hasE.equals("1")) {
				bMarksAndValue.put("${e}", "E");
			} else {
				bMarksAndValue.put("${e}", "E");
			}
			bMarksAndValue.put("${tuoji}", builder.toString());
			bMarksAndValue.put("${notes}",
					getCurrentDate() + "  " + builder.toString() + "_" + pid + "_and");
			String childImgPath = imgPath + name + ".png";
			Code128CCreator codeCreator = new Code128CCreator();
			String barCode = codeCreator.getCode(cOrder, "");
			int height = 40;
			codeCreator.kiCode128C(barCode, 2, height, childImgPath);
			float rate = 0.0264f;
			FileInputStream imgIn = new FileInputStream(new File(childImgPath));
			BufferedImage read = ImageIO.read(imgIn);
			int imgW = read.getWidth();
			int imgH = read.getHeight();
			imgIn.close();
			float cmW = 5.34f;
			int w = (int) (cmW / rate);
			int h = (int) (w * imgH / imgW);
			int w2 = w;
			int h2 = h * 5 / 6;
			String type = childImgPath.substring(childImgPath.lastIndexOf(".") + 1,
					childImgPath.length());
			byte[] imgBytes = DocxManager.inputStream2ByteArray(new FileInputStream(childImgPath),
					true);
			Map<String, Object> header1 = new HashMap<String, Object>();
			header1.put("width", w);
			header1.put("height", h);
			header1.put("type", type);
			header1.put("content", imgBytes);
			bMarksAndValue.put("${imgmain}", header1);
			Map<String, Object> header2 = new HashMap<String, Object>();
			header2.put("width", w2);
			header2.put("height", h2);
			header2.put("type", type);
			header2.put("content", imgBytes);
			bMarksAndValue.put("${imgchild}", header2);
			DocxManager.replaceTemplate(bMarksAndValue, wordPath);
			// printer="123";
			DocxPrinter manager = new DocxPrinter(officeHome, printer, wordPath);
			manager.print();
		}
	}

	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.format(new Date());
		return sdf.format(new Date());

	}

	public static void makeQrFile(String str, int size, String savePath) {
		int margin = size / 30;
		ZxingBarCreator.createQRImage(savePath, str, size, margin);
	}

	public static void makeQrFile(String str, int size, int margin, String savePath) {
		ZxingBarCreator.createQRImage(savePath, str, size, margin);
	}

	public static void main(String[] args) {
		try {
			String workDir = "D:/dyingjia/运单/yundan_顺丰/";
			makeCode128B("SF1231112223333", 49,
					workDir+"testcode128B.jpg");
			String savepath = workDir + "zxingQr.png";
			int size = 300;
			String qrData = "sdfjjjjj123123";
			makeQrFile(qrData, size, savepath);
			System.out.println("make codeb ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void makeCode128B(String str, int height, String savePath) throws Exception {
		Code128CCreator codeCreator = new Code128CCreator();
		String barCode = codeCreator.getCodeA(str, 1);
		// (str, "");
		codeCreator.kiCode128C(barCode, 2, height, savePath);
	}
	
	public static void makeCode128Thin(String str, int height, String savePath) throws Exception {
		Code128CCreator codeCreator = new Code128CCreator();
		String barCode = codeCreator.getCodeA(str, 1);
		// (str, "");
		codeCreator.kiCode128C(barCode, 1, height, savePath);
	}
}
