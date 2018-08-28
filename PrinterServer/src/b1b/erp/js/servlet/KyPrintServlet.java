package b1b.erp.js.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zjy.print.DocxManager;
import com.zjy.print.docx.DocxPrinter;
import com.zjy.print.docx.office.OfficeException;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.UploadUtils;

/**
 * Servlet implementation class KyPrintServlet
 */
@WebServlet("/KyPrintServlet")
public class KyPrintServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KyPrintServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		String orderID = request.getParameter("orderID");
		String cardID = request.getParameter("cardID");
		String destcode = request.getParameter("destcode");
		String yundanType = request.getParameter("yundanType");
		String tuojiwu = request.getParameter("tuojiwu");
		String goodinfos = request.getParameter("goodinfos");
		String counts = request.getParameter("counts");
		String jName = request.getParameter("j_name");
		String jPhone = request.getParameter("j_phone");
		String jAddress = request.getParameter("j_address");
		String dName = request.getParameter("d_name");
		String dPhone = request.getParameter("d_phone");
		String dAddress = request.getParameter("d_address");
		String notes = request.getParameter("notes");
		String payType = request.getParameter("payType");
		String jCompany = request.getParameter("j_company");
		String dCompany = request.getParameter("d_company");
		String pid = request.getParameter("pid");
		if (jCompany == null) {
			jCompany = "";
		}
		if (dCompany == null) {
			dCompany = "";
		}
		if (pid == null) {
			pid = "";
		}
		StringBuilder infoBuilder = new StringBuilder();
		String infos[];
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
		String officeHome = getServletContext().getInitParameter("openoffice_home");
		String savePath = wordDir + "KY/" + UploadUtils.getCurrentYearAndMonth() + "/";
		String newWord = savePath + UploadUtils.getCurrentDay() + "o_" + pid + "_" + orderID + "_"
				+ Myuuid.createRandom(4) + ".docx";
		String imgDir = savePath + "codeImg/";
		File imgdir = new File(imgDir);
		if (!imgdir.exists()) {
			imgdir.mkdirs();
		}
		File desfFile = new File(newWord);
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
			imgIn.close();
			int w = (int) (5 / rate);
			int h = (int) (w * imgH / imgW);
			// int w2 = (int) (4 / rate);
			// int h2 = (int) (w2*imgH/ imgW);
			int w2 = w;
			int h2 = h * 5 / 6;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			// "2018-01-02 10:51"
			String printTime = sdf.format(new Date());
			byte[] imgBytes = DocxManager.inputStream2ByteArray(new FileInputStream(imagePath),
					true);
			header.put("width", w);
			header.put("height", h);
			header.put("type", type);
			header.put("content", imgBytes);
			Map<String, Object> header2 = new HashMap<String, Object>();
			header2.put("width", w2);
			header2.put("height", h2);
			header2.put("type", type);
			header2.put("content", imgBytes);
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
			param.put("tag_tuoji", infoBuilder.toString() + pid+"_and");
			param.put("tag_counts", counts);
			param.put("tag_pt", payType);
			param.put("tag_account", cardID);
			FileInputStream inStream = new FileInputStream(templatePath);
			FileUtils.fileCopy(inStream, newWord);
			DocxManager.replaceTemplate(param, newWord);
			// printer = "ZDesigner GK888d (EPL)";
			DocxPrinter printUtils = new DocxPrinter(officeHome, printer, newWord);
			try {
				printUtils.print();
				response.getWriter().append("ok").close();
			} catch (OfficeException e) {
				response.getWriter().append("error:" + e.getMessage()).close();
			}
			return;			
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath()).close();
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
