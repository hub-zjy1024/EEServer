package com.zjy.print.pdf;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrintQuality;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.PrintPDF;


import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.UploadUtils;

/**
 * Servlet implementation class KyPdfPrintServlet
 */
@WebServlet("/KyPdfPrintServlet")
public class KyPdfPrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KyPdfPrintServlet() {
		super();
		// http://localhost:8080/PrinterServer/KyPdfPrintServlet
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String orderID = "234123412343";
		String cardID = "01083729273";
		String destcode = "0755";
		String yundanType = "陆运件";
		String tuojiwu = "20112418121";
		String goodinfos = "20112418121";
		String counts = "1";
		String jName = "testzh";
		String jPhone = "12345567";
		String jAddress = "苏州工业园区苏州大道东381号商旅大厦6幢1804室";
		String dName = "陈丽媛";
		String dPhone = "13418759398";
		String dAddress = "深圳市福田区彩田南路彩虹新都海鹰大厦24D";
		String notes = "陆运件";
		String payType = "寄付月结";
		String jCompany = "北京测试公司";
		String dCompany = "深圳市瑞通威电子有限公司";
		String pid = "20112418121";
		String[] infos;
		StringBuilder infoBuilder = new StringBuilder();
		if (goodinfos != null) {
			infos = goodinfos.split("\\$");
			for (int i = 0; i < infos.length; i++) {
				if (i == 3) {
					if (infos.length > 3) {
						infoBuilder.append("\t等");
					}
					break;
				}
				String[] s = infos[i].split("&");
				if (s.length != 1) {
					infoBuilder.append(s[0] + " ：" + s[1]);
					if (i != 2) {
						infoBuilder.append("\n");
					}
				}
			}
		}
		String wordDir = getServletContext().getInitParameter("dyjDir");
		String printer = getServletContext().getInitParameter("KY_Printer");
		String templatePath = request.getServletContext().getRealPath("/docTemplate/kyDocx.docx");
		String batPath = request.getServletContext().getRealPath("/docTemplate/startOOSerivce.bat");
		String officeHome=getServletContext().getInitParameter("openoffice_home");
		File srcFile = new File(templatePath);
		String newWord = wordDir + "KY/" + UploadUtils.getCurrentYearAndMonth() + "/"
				+ UploadUtils.getCurrentDay() + "t_" + orderID + "_" + Myuuid.createRandom(4)
				+ ".docx";
		String savePath = wordDir + "/KY/" + UploadUtils.getCurrentYearAndMonth() + "/";
		String imgDir = savePath + "codeImg/";
		String newPDF = newWord + ".pdf";
		File desfFile = new File(newPDF);
		if (!desfFile.getParentFile().exists()) {
			desfFile.mkdirs();
		}
		File imgdir = new File(imgDir);
		if (!imgdir.exists()) {
			imgdir.mkdirs();
		}
		if (!desfFile.getParentFile().exists()) {
			desfFile.mkdirs();
		}
		String fileName = desfFile.getName();
		System.out.println(fileName);
		// srcFile = new File("D:/dyj/test_template.docx");
		Code128CCreator c = new Code128CCreator();
		String barCode;
		String imagePath = imgDir + desfFile.getName() + ".png";
		String type = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
		try {
			barCode = c.getCodeA(orderID, 1);
			c.kiCode128C(barCode, 2, 66, imagePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			Map<String, Object> header = new HashMap<String, Object>();
			float rate = 0.0264f;
			FileInputStream imgIn = new FileInputStream(new File(imagePath));
			BufferedImage read = ImageIO.read(imgIn);
			int imgW = read.getWidth();
			int imgH = read.getHeight();
			int w = (int) (5 / rate);
			int h = (int) (w *imgH/ imgW);
//			int w2 = (int) (4 / rate);
//			int h2 = (int) (w2*imgH/ imgW);
			int w2 = w;
			int h2 = h*5/6;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			// "2018-01-02 10:51"
			String printTime = sdf.format(new Date());
			header.put("width", w);
			header.put("height", h);
			header.put("type", type);
//			header.put("content",
//					DocxManager.inputStream2ByteArray(new FileInputStream(imagePath), true));
			Map<String, Object> header2 = new HashMap<String, Object>();
			header2.put("width", w2);
			header2.put("height", h2);
			header2.put("type", type);
//			header2.put("content",
//					DocxManager.inputStream2ByteArray(new FileInputStream(imagePath), true));
			param.put("tag_codeimg", header);
			param.put("tag_imgs", header2);
			param.put("tag_dst", destcode);
			param.put("tag_yundantype", yundanType);
			param.put("tag_jc", jCompany);
			param.put("tag_time", printTime);
			param.put("tag_codenumber", orderID);
			param.put("tag_jname", jName);
			param.put("tag_jphone", jPhone);
			param.put("tag_jaddress", jAddress);
			param.put("tag_dc", dCompany);
			param.put("tag_dname", dName);
			param.put("tag_dphone", dPhone);
			param.put("tag_daddress", dAddress);
			param.put("tag_tuoji", infoBuilder.toString());
			param.put("tag_counts", counts);
			param.put("tag_pt", payType);
			param.put("tag_account", cardID);
			// newWord="d:/dyj/1515564443441_openoffice.docx";
			newPDF = "d:/dyj/1515564443441_openoffice.pdf";
			printer = "";
			int dpi = 203;
//			DocxManager.docToPdf(new File(newWord), new File(newPDF), batPath);
//			PDFBook myBook1 = new PDFBook(printer, newPDF, dpi);
//			PrintPDFUtils.printPDF(myBook1);
			int pageWidth=103;
			int pageHeight=210;
			String imgp = newPDF + "_cR.png";
//			printByMain(newPDF, dpi);
			response.getWriter().append("1").close();
//			System.exit(1);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath()).close();
	}

	private void pdf2ImgByCustomRenderertomRenderer(String newPDF, int dpi)
			throws InvalidPasswordException, IOException, FileNotFoundException {
		PDDocument docx = PDDocument.load(new File(newPDF));
		CustomeRender renderer = new CustomeRender(docx);
		String imgp = newPDF + ".png";
		FileOutputStream fio = new FileOutputStream(imgp);
		BufferedImage img1 = renderer.renderPageToGraphicsC(0, dpi / 72f);
		ImageIO.write((RenderedImage) img1, "png", fio);
		fio.close();
		docx.close();
	}

	public static void printByMain(String pdfpath,int dpi) {
		try {
			PrintPDF.main(new String[] { pdfpath, "-orientation", "portrait", "-silentPrint",
					"-dpi", String.valueOf(dpi) });
		} catch (PrinterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void pdfToImg(String imgP , String pdfPath, float dpi) {
		PDDocument document = null;
		try {
			File pdfFIle = new File(pdfPath);
			document = PDDocument.load(pdfFIle);
			PDFRenderer renderer = new PDFRenderer(document);
			int pageIndex = document.getPages().getCount();
			pageIndex = 0;
			BufferedImage img1 = renderer.renderImage(pageIndex, dpi / 72, ImageType.ARGB);
			FileOutputStream fileout = new FileOutputStream(imgP);
			ImageIO.write((RenderedImage) img1, "png", fileout);
			fileout.close();
			document.close();
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printImgAt(String destImageFile, float wMM, float hMM, String printer) {
		HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		File file = new File(destImageFile);
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
		// PrintServiceLookup.lookupPrintServices(flavor, attributes);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
		for (PrintService s : services) {
			if (printer.equals(s.getName())) {
				service = s;
				break;
			}
		}
		if (service != null) {
			try {
				DocPrintJob job = service.createPrintJob();
				FileInputStream fis = new FileInputStream(file);
				DocAttributeSet das = new HashDocAttributeSet();
				PageFormat formate = new PageFormat();
				Paper paper = new Paper();
				paper.setImageableArea(0, 0, wMM, hMM);
				formate.setPaper(paper);
				das.add(new MediaPrintableArea(0, 0, wMM, hMM, MediaPrintableArea.MM));
				das.add(PrintQuality.HIGH);
				Doc doc = new SimpleDoc(fis, flavor, das);
				job.print(doc, pras);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
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
