package b1b.erp.js.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.Date2StringUtils;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.SingleActiveXComponent;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.utils.WordUtils;

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
		// TODO Auto-generated constructor stub
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
		String templatePath = request.getServletContext().getRealPath("/docTemplate/ky模板.doc");
//		String templatePath = request.getServletContext().getRealPath("/docTemplate/ky模板.doc");
		String homeDir = getServletContext().getInitParameter("dyjDir");
		String printer = getServletContext().getInitParameter("KY_Printer");
		String savePath = homeDir + "/KY/"+UploadUtils.getCurrentYearAndMonth()+"/";
		String imgDir = savePath + "codeImg/";
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File imgDirFile = new File(imgDir);
		if (!imgDirFile.exists()) {
			imgDirFile.mkdirs();
		}
		String wordName = UploadUtils.getCurrentDay()+"_ky" + Myuuid.create(4) + ".doc";
		String docFilepath=savePath+wordName;
		String[] infos;
		if (goodinfos != null) {
			infos = goodinfos.split("\\$");
		} else {
			infos = new String[] { "test1&1000", "test3&1024", "test2&500", "test" };
		}
		StringBuilder infoBuilder = new StringBuilder();
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
		
		System.out.println("===="+UploadUtils.getCurrentAtSS()+"========");
		System.out.println("goodinfo:"+infoBuilder.toString());
		FileInputStream testInputStream = new FileInputStream(templatePath);
		FileUtils.fileCopy(testInputStream, docFilepath);
		HashMap<String, String> bMarksAndValue = new HashMap<>();
		String printTime = Date2StringUtils.style1();
		bMarksAndValue.put("打印时间", "打印时间：" + printTime);
		bMarksAndValue.put("目的地区号", destcode);
		for (int i = 1; i <= 3; i++) {
			bMarksAndValue.put("时效类型" + i, yundanType);
			bMarksAndValue.put("条码号" + i, orderID);
			bMarksAndValue.put("寄件人" + i, jName);
			bMarksAndValue.put("寄件人" + i + "电话", jPhone);
			bMarksAndValue.put("寄件人" + i + "地址", jAddress);
			bMarksAndValue.put("收件人" + i, dName);
			bMarksAndValue.put("收件人" + i + "电话", dPhone);
			bMarksAndValue.put("收件人" + i + "地址", dAddress);
			bMarksAndValue.put("托寄物" + i, tuojiwu);
			bMarksAndValue.put("件数" + i, counts);
			bMarksAndValue.put("付款方式" + i,payType );
			bMarksAndValue.put("月结卡号" + i, cardID);
			bMarksAndValue.put("备注" + i, notes);
			bMarksAndValue.put("托寄物" + i, infoBuilder.toString());
		}
//		ActiveXComponent ac = SingleActiveXComponent.getApp();
//		ActiveXComponent ac = new ActiveXComponent("Word.Application");
//		HKEY_LOCAL_MACHINE\SOFTWARE\Classes\CLSID\{000209FF-0000-0000-C000-000000000046}
//		ActiveXComponent ac = new ActiveXComponent("000209FF-0000-0000-C000-000000000046");
		ComThread.InitMTA(true);
		ActiveXComponent ac = new ActiveXComponent("Word.Application");
		ac.setProperty("Visible", true);
		System.out.println("dispath:"+ac.m_pDispatch);
		Dispatch doc = WordUtils.openDocument(docFilepath, ac);
		WordUtils.replaceBookmark(bMarksAndValue, doc);
		Code128CCreator c = new Code128CCreator();
		String barCode;
		String imagePath = imgDir+wordName+".png";
		try {
			barCode = c.getCodeA(orderID, 1);
			c.kiCode128C(barCode, 2, 66, imagePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		float h1 = 9;
		float h2 = 7;
		float codeWidth=40;
//		WordUtils.insertImageAtBookmarkByMM("条码1", imagePath, h2, "height", doc);
		WordUtils.insertImageAtBookmarkByMM("条码1", imagePath, codeWidth, "width", doc);
		WordUtils.insertImageAtBookmarkByMM("条码2", imagePath, codeWidth, "width", doc);
		WordUtils.insertImageAtBookmarkByMM("条码3", imagePath, codeWidth, "width", doc);
		try{
			 WordUtils.print2(doc, printer, ac);
		}catch (Exception e) {
			ByteArrayOutputStream bao=new ByteArrayOutputStream();
			PrintWriter writer=new PrintWriter(bao);
			e.printStackTrace(writer);
			writer.flush();
			System.out.println("error-----"+new String (bao.toByteArray(),"utf-8"));
			writer.close();
			WordUtils.closeDocument(doc, true);
			WordUtils.exit(ac);
			ComThread.Release();
			response.getWriter().append("error"+e.getMessage()).close();
			return;
		}
//		WordUtils.closeDocument(doc, true);
		WordUtils.exit(ac);
		ComThread.Release();
		response.getWriter().append("ok").close();;
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
