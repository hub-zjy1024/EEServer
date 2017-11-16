package b1b.erp.js.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.DownUtils;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.SingleActiveXComponent;
import b1b.erp.js.utils.WordUtils;

/**
 * Servlet implementation class WordTestServlet
 */
@WebServlet("/WordTestServlet")
public class WordTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WordTestServlet() {
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
		String templatePath = request.getServletContext().getRealPath("/docTemplate/sf150模板.doc");
		FileInputStream testInputStream = new FileInputStream(templatePath);
		if (testInputStream != null) {
			String rootPath = getServletContext().getInitParameter("dyjDir");
			String savePath = rootPath + "/SF/saveWord/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String name = "test_" + Myuuid.create(4) + ".doc";
			FileUtils.fileCopy(testInputStream, savePath + name);
			System.out.println("=====" + DownUtils.getTimeAtSS() + "=====");
			testInputStream.close();
//			ComThread.InitMTA();
//			ComThread.InitMTA(true);
//			ComThread.InitSTA(true);
			ActiveXComponent ac = null;
			try {
//				ac = new ActiveXComponent("Word.Application");
				ac = SingleActiveXComponent.getApp();
				// ActiveXComponent ac = new
				// ActiveXComponent("{00020906-0000-0000-C000-000000000046}");
//				ac.setProperty("Visible", new Variant(true));
				Dispatch doc = WordUtils.openDocument(savePath + name, ac);
				HashMap<String, String> bMarksAndValue = new HashMap<>();
				bMarksAndValue.put("业务类型", "测试");
				WordUtils.replaceBookmark(bMarksAndValue, doc);
				
				String print1 = getServletContext().getInitParameter("printer1");
				String printerName = print1;
				if (printerName != null && !printerName.equals("")) {
					String nowPrinter = ac.getProperty("ActivePrinter").toString();
					System.out.println("nowPrint:" + nowPrinter);
					printerName=null;
					if (!printerName.equals(nowPrinter)) {
						try {
							System.out.println("start set printer:" + printerName);
							ac.setProperty("ActivePrinter", new Variant(printerName));
						} catch (Exception e) {
							System.out.println(DownUtils.getTimeAtSS()+":set printer fail:" + e.getMessage());
						}
					}
				}
				WordUtils.closeDocument(doc, true);
//				WordUtils.exit(ac);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ac != null) {
				System.out.println("dispatch_formmer id:" + ac.m_pDispatch);
			}
//			ComThread.Release();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
