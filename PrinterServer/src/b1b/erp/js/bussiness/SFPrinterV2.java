package b1b.erp.js.bussiness;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zjy.print.bussiness.DocxPrinter;
import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.util.DocxManager;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.entity.YundanInfo;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.UploadUtils;

public class SFPrinterV2 {
	HttpServletRequest request;
	private String officeHome;
	private String templatePath;
	private String wordDir;
	private String rootPath;
	private String printer;
	public static boolean isDebug = false;
	String savePath = "";

	static org.apache.log4j.Logger mLogger = org.apache.log4j.Logger.getLogger(SFPrinterV2.class);
	String yundanType;

	public SFPrinterV2(HttpServletRequest request) {
		super();
		this.request = request;
		yundanType = request.getParameter("yundanType");
		if ("210".equals(yundanType)) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_210_v2.docx");
		} else {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_150_v2.docx");
		}
		wordDir = request.getServletContext().getInitParameter("dyjDir");
		rootPath = wordDir;
		officeHome = request.getServletContext().getInitParameter("openoffice_home");
		printer = request.getServletContext().getInitParameter("printer1");
		if (isDebug) {
			if ("210".equals(yundanType)) {
				templatePath = "D:/dyingjia/运单/yundan_顺丰/2020年4月30日/sf_210_v2.docx";
			} else {
				templatePath = "D:/dyingjia/运单/yundan_顺丰/2020年4月30日/sf_150_v2.docx";
			}
			printer = "BTP-L540H";
		}

	}

	public void testApi() throws Exception {
		YundanInfo minfo = new YundanInfo();
		minfo.yundans = new String[] { "SF1231112221234", "SF2223334441234" };
		minfo.pid = "1231233";
		minfo.destRouteLable = "d_CODE-1234";
		minfo.print_time = SFPrinterUtil.getCurrentDate();
		//
		minfo.j_name = "寄张三";
		minfo.j_phone = "12312312311";
		// minfo.j_phone = phoneEncode(minfo.j_phone);
		minfo.j_comp = "北京远大创新";
		minfo.j_addr = "北京市海淀区中关村1+1大厦";
		//
		minfo.d_name = "收李四";
		minfo.d_phone = "13311238742";
		// minfo.d_phone = phoneEncode(minfo.d_phone);

		minfo.d_comp = "深圳创新恒远";
		minfo.d_addr = "深圳市莆田区姐姐附近的十分讲究见附件附件姐姐姐夫姐夫亟待解决分解方法";
		// 其他
		minfo.qr_code = "MMM={'k1':'010WB','k2':'010MC','k3':'060','k4':'T6','k5':'322108530494','k6':'','k7':'ae7cc797'}";
		minfo.pay_type = "寄付月结";
		minfo.proCode = "T6";
		minfo.typeA = "";
		minfo.HK_in = "123";
		minfo.HK_out = "321";
		minfo.tuoji = "我是托寄物，测试脱机1";
		minfo.note = "我是备注，测试备注1";
		/*
		 * public String qr_code; public String pay_type; public String typeA; public String HK_in;
		 * public String HK_out; public String tuoji; public String note;
		 */
		Print(minfo);
	}

	public void Print(YundanInfo minfo) throws Exception {
		savePath = rootPath + "SF_V2/" + UploadUtils.getCurrentYearAndMonth() + "/";
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		String[] yundans = minfo.yundans;
		String mainCode = "";
		if (yundans.length > 0) {
			mainCode = yundans[0];
		}
		for (int i = 0; i < yundans.length; i++) {
			String nowCode = yundans[i];
			String index = (i + 1) + "/" + yundans.length;
			// 准备拷贝一份模板文件
			String name = UploadUtils.getCurrentDay() + "-" + minfo.pid + "_" + nowCode + "_"
					+ Myuuid.createRandom(4) + ".docx";
			String filePath = savePath + name;
			FileInputStream testInputStream = null;
			try {
				testInputStream = new FileInputStream(templatePath);
				FileUtils.fileCopy(testInputStream, filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				mLogger.warn("template error", e);
				throw new IOException("文件不存在");
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("模板文件读取失败");
			} finally {
				if (testInputStream != null) {
					testInputStream.close();
				}
			}
			if (i > 0) {
				printSingle(filePath, minfo, index, mainCode, nowCode);
			} else {
				printSingle(filePath, minfo, index, mainCode, mainCode);
			}
		}
	}

	/*
	 * function getFormatYunStr(mCode){ var index=[3,3,3,4]; var finalCode=""; var tIndx=0; for(var
	 * i=0;i<index.length;i++){ var tempIndex=index[i]; if(tIndx+tempIndex>mCode.length){ break; }
	 * var StrCode=mCode.slice(tIndx,tIndx+tempIndex); finalCode+=StrCode; finalCode+=" ";
	 * tIndx+=tempIndex; } finalCode="SF "+finalCode; return finalCode; }
	 */
	private static String yundanIDFormat(String mCode) {
		int[] index = new int[] { 2, 3, 3, 3, 4 };
		String finalCode = "";
		int tIndx = 0;
		for (int i = 0; i < index.length; i++) {
			int tempIndex = index[i];
			int endIndex = tIndx + tempIndex;
			if (endIndex > mCode.length()) {
				break;
			}
			String StrCode = mCode.substring(tIndx, endIndex);
			finalCode += StrCode;
			finalCode += " ";
			tIndx += tempIndex;
		}
		return finalCode;
	}

	private static String phoneEncode(String mPhone) {
		String token = "*";
		int tokenCount = 4;
		int start = 3;
		if (start > mPhone.length()) {
			return mPhone;
		}
		String encoded = mPhone.substring(0, start);
		for (int i = 0; i < tokenCount; i++) {
			encoded += token;
		}
		int start2 = start + tokenCount;
		if (start2 < mPhone.length()) {
			encoded += mPhone.substring(start2, mPhone.length());
		}
		return encoded;
	}

	private void printSingle(String wordPath, YundanInfo minfo, String index, String mainCode,
			String child) throws Exception {
		String imgPath = savePath + "codeImg/";

		File imgDir = new File(imgPath);
		if (!imgDir.exists()) {
			imgDir.mkdirs();
		}
		String maincode_str = "母单号";
		String code_main = mainCode;
		String code_child = "";
		String childcode_str = "";
		if (!mainCode.equals(child)) {
			childcode_str = "子单号";
			code_child = child;
			code_child = yundanIDFormat(code_child);
		}
		code_main = yundanIDFormat(code_main);
		minfo.j_phone = phoneEncode(minfo.j_phone);
		minfo.d_phone = phoneEncode(minfo.d_phone);

		HashMap<String, Object> bMarksAndValue = new HashMap<>();
		bMarksAndValue.put("index", index);
		bMarksAndValue.put("destRouteLable", minfo.destRouteLable);

		bMarksAndValue.put("code_child", code_child);
		bMarksAndValue.put("childcode_str", childcode_str);

		bMarksAndValue.put("code_main", code_main);
		bMarksAndValue.put("maincode_str", maincode_str);
		//
		bMarksAndValue.put("print_time", minfo.print_time);
		bMarksAndValue.put("HK_in", minfo.HK_in);
		bMarksAndValue.put("HK_out", minfo.HK_out);
		bMarksAndValue.put("pay_type", minfo.pay_type);

		bMarksAndValue.put("j_name", minfo.j_name);
		bMarksAndValue.put("j_phone", minfo.j_phone);
		bMarksAndValue.put("j_comp", minfo.j_comp);
		bMarksAndValue.put("j_addr", minfo.j_addr);

		bMarksAndValue.put("d_name", minfo.d_name);
		bMarksAndValue.put("d_phone", minfo.d_phone);
		bMarksAndValue.put("d_comp", minfo.d_comp);
		bMarksAndValue.put("d_addr", minfo.d_addr);
		float rate = 0.0264f;
		Map<String, Object> headerTimeType = new HashMap<String, Object>();
		String timeImg = "";
		if ("T1".equals(minfo.proCode)) {
			timeImg = request.getServletContext().getRealPath("/imgs/sf/20_20 T1.png");
		} else if ("T4".equals(minfo.proCode)) {
			timeImg = request.getServletContext().getRealPath("/imgs/sf/20_20 T4.png");
		} else if ("T6".equals(minfo.proCode)) {
			timeImg = request.getServletContext().getRealPath("/imgs/sf/20_20 T6.png");
		} else if ("T8".equals(minfo.proCode)) {
			timeImg = request.getServletContext().getRealPath("/imgs/sf/20_20 T8.png");
		} else if ("T9".equals(minfo.proCode)) {
			timeImg = request.getServletContext().getRealPath("/imgs/sf/资源 26.png");
		}
		if (!"".equals(timeImg)) {
			int w2 = (int) (2f / rate);
			int h2 = (int) (2f / rate);
			byte[] imgBytes = DocxManager.inputStream2ByteArray(new FileInputStream(timeImg), true);
			headerTimeType.put("width", w2);
			headerTimeType.put("height", h2);
			headerTimeType.put("type", "png");
			headerTimeType.put("content", imgBytes);
			bMarksAndValue.put("timeType", headerTimeType);
		}
		/*
		 * minfo.yundans = new String[] { "SF1231112221234", "SF2223334441234" }; minfo.pid =
		 * "1231233"; minfo.destRouteLable = "d_CODE-1234"; minfo.print_time =
		 * SFPrinterUtil.getCurrentDate(); // 其他 minfo.qr_code =
		 * "MMM={'k1':'010WB','k2':'010MC','k3':'060','k4':'T6','k5':'322108530494','k6':'','k7':'ae7cc797'}";
		 * minfo.pay_type = "寄付月结"; minfo.proCode = "T6"; minfo.typeA = "";
		 */

		bMarksAndValue.put("note", minfo.note);
		bMarksAndValue.put("tuoji", minfo.tuoji);
		File mFile = new File(wordPath);
		String imageFileName = wordPath.substring(wordPath.lastIndexOf("/") + 1);
		int dotIndex = imageFileName.lastIndexOf(".");
		if (dotIndex > 0) {
			imageFileName = imageFileName.substring(0, dotIndex);
		}
		Map<String, Object> header2 = new HashMap<String, Object>();
		String childImgPath = imgPath + imageFileName + ".png";
		SFPrinterUtil.makeCode128B(child, 20, childImgPath);
		byte[] imgBytes = DocxManager.inputStream2ByteArray(new FileInputStream(childImgPath),
				true);

		FileInputStream imgIn = new FileInputStream(new File(childImgPath));
		BufferedImage read = ImageIO.read(imgIn);
		int imgW = read.getWidth();
		int imgH = read.getHeight();
		imgIn.close();

		float cmW = 5.6f;
		int w = (int) (cmW / rate);
		int h = (int) (1.2f / rate);
		int w2 = w;
		int h2 = h;
		// 5.6
		String type = childImgPath.substring(childImgPath.lastIndexOf(".") + 1,
				childImgPath.length());
		header2.put("width", w2);
		header2.put("height", h2);
		header2.put("type", type);
		header2.put("content", imgBytes);
		bMarksAndValue.put("barcode", header2);

		header2 = new HashMap<String, Object>();
		childImgPath = imgPath + imageFileName + "_qr.png";
		int qrWidth = 80;
		int qrDocSize = (int) (2.5f / rate);
		int realQrWidth = (int) (qrDocSize * (1 + 0.4));
		SFPrinterUtil.makeQrFile(minfo.qr_code, realQrWidth, 2, childImgPath);
		byte[] imgBytes2 = DocxManager.inputStream2ByteArray(new FileInputStream(childImgPath),
				true);
		type = childImgPath.substring(childImgPath.lastIndexOf(".") + 1, childImgPath.length());
		header2.put("width", qrDocSize);
		header2.put("height", qrDocSize);
		header2.put("type", type);
		header2.put("content", imgBytes2);
		bMarksAndValue.put("qr_code", header2);

		if ("210".equals(yundanType)) {
			header2 = new HashMap<String, Object>();
			childImgPath = imgPath + imageFileName + "_2.png";
			SFPrinterUtil.makeCode128B(child, 10, childImgPath);
			imgBytes = DocxManager.inputStream2ByteArray(new FileInputStream(childImgPath), true);
			imgIn = new FileInputStream(new File(childImgPath));
			read = ImageIO.read(imgIn);
			imgW = read.getWidth();
			imgH = read.getHeight();
			imgIn.close();
			cmW = 5.6f;
			w = (int) (cmW / rate);
			h = (int) (0.5f / rate);
			w2 = w;
			h2 = h;
			// 5.6
			type = childImgPath.substring(childImgPath.lastIndexOf(".") + 1, childImgPath.length());
			header2.put("width", w2);
			header2.put("height", h2);
			header2.put("type", type);
			header2.put("content", imgBytes);
			bMarksAndValue.put("barcode2", header2);
		}
		DocxManager.replaceTemplate(bMarksAndValue, wordPath);
		mLogger.info(String.format("officeHome=%s{},wordPath=%s", officeHome, wordPath));
		DocxPrinter manager = new DocxPrinter(officeHome, printer, wordPath);
		try {
			manager.print();
		} catch (OfficeException e) {
			throw new IOException(e.getMessage());
		} catch (Exception e) {
			throw new IOException("其他异常," + e.getMessage());
		}
	}
}
