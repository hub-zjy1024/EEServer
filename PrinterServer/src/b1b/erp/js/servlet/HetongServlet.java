package b1b.erp.js.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.entity.GoodInfo;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.Md5Utils;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.utils.WordUtils;

/**
 * Servlet implementation class HetongServlet
 */
@WebServlet("/HetongServlet")
public class HetongServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String queryString;

	// localhost:8080/PrinterServer/HetongServlet?pid=1024521;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HetongServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doget方法中只能使用getBytes("iso-8859-1","utf-8")进行编码转换，doPost中可以使用request.setCharacterEncoding("utf-8");
		// 告诉浏览器使用utf-8解析响应，这时候getPrintWriter中使用的编码格式也是utf-8，不会出现乱码情况
		response.setContentType("text/html; charset=utf-8");
		// response.setCharacterEncoding("utf-8");
		/*
		 * http://localhost:8080/PrinterServer/HetongServlet?pid=000000&
		 * proFullName=北京市供货商&hetongID=101110&proShortName=bjgh&goodInfos=name1,
		 * name2,name3&proPhone=13452525623&proAddress=北京市海淀区中关村&proReceiveMan=
		 * 晨晨&createDate=2017-07-13&fileID=6;
		 */ String proFullName = request.getParameter("proFullName");
		// proFullName=new String(proFullName.getBytes("iso-8859-1"),"UTF-8");
		String hetongID = request.getParameter("hetongID");
		String proShortName = request.getParameter("proShortName");
		String proPhone = request.getParameter("proPhone");
		String proAddress = request.getParameter("proAddress");
		String proReceiveMan = request.getParameter("proReceiveMan");
		String createDate = request.getParameter("createDate");
		String goodInfos = request.getParameter("goodInfos");
		String fileID = request.getParameter("fileID");

		String uid = request.getParameter("uid");
		/*
		 * {"partno":"TESTSCCG20170227004","brand":"+","couts":"10","price":
		 * "10.0000","totalprice":"100.0000","mark":"+"}
		 */ JSONArray array = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONObject json = new JSONObject();
			json.put("partno", "zxe_namejq" + i);
			json.put("brand", "zxsjjjd" + i);
			json.put("couts", "15" + i);
			json.put("price", "100" + i);
			json.put("totalprice", "1500" + i);
			json.put("mark", "本型号只限直流" + i);
			array.put(json);
		}
		System.out.println("jsonArray=" + array);
		// 默认使用iso-8859-1编码格式，response.setContentType()设置过编码之后，使用ContentType中设置的charset
		PrintWriter writer = response.getWriter();
		// writer.flush();
		// 需要刷新
		// if (true) {
		// writer.write("name很难撒的风景");
		// writer.flush();
		// writer.close();
		// return;
		// }
		if(goodInfos!=null){
			goodInfos = array.toString();
		}
		Gson gson = new Gson();
		// 创建一个JsonParser
		JsonParser parser = new JsonParser();
		// 通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
		JsonElement el = parser.parse(goodInfos);
		// 把JsonElement对象转换成JsonArray
		JsonArray jsonArray = el.getAsJsonArray();
		// 遍历JsonArray对象
		Iterator<JsonElement> it = jsonArray.iterator();
		java.util.List<GoodInfo> list = new ArrayList<>();
		while (it.hasNext()) {
			JsonElement e = it.next();
			// JsonElement转换为JavaBean对象
			GoodInfo info = gson.fromJson(e, GoodInfo.class);
			list.add(info);
		}
		/*
		 * 用途暂不清楚，四种都能正常运行 ComThread.InitMTA(true); ComThread.InitMTA();
		 * ComThread.InitSTA(true);
		 */

		String pid = request.getParameter("pid");
		// getServletContext().getInitParameter("name");
		String wordDir = getServletContext().getInitParameter("dyjDir");
		File hetongDir = new File(wordDir, "/hetong/");
		String templatePath = request.getServletContext().getRealPath("/hetongTemplate/");
		System.out.println("hetongpath=" + templatePath);
		if (!hetongDir.exists()) {
			hetongDir.mkdirs();
		}
		// if(true){
		// writer.write("测试目录:"+templatePath);
		// writer.flush();
		// return ;
		// }
		// String hetongTemp = "d:/dyj/hetong_template/"+fileID+".doc";
		String hetongTemp = templatePath + fileID + ".doc";

		File tempFile = new File(hetongTemp);
		System.out.println("fileID:" + fileID);
		if (!tempFile.exists()) {
			writer.write("error:没有找到对应的模板信息");
			writer.flush();
			writer.close();
			return;
		}
		InputStream in = new FileInputStream(tempFile);
		String htPatten = Myuuid.create(4);
		String newPath = hetongDir.getAbsolutePath() + "\\" + htPatten + ".doc";
		FileUtils.fileCopy(in, newPath);
		in.close();
		ComThread.InitSTA();
		ActiveXComponent ac = new ActiveXComponent("Word.Application");
		ac.setProperty("Visible", true);
		Dispatch doc = WordUtils.openDocument(newPath, ac);
		HashMap<String, String> map = new HashMap<>();
		// 书签
		// Creatdate
		// MartStockFile
		// MID
		// ProAddress
		// ProFullName
		// ProReceiveMan
		// ProShortName
		// ProPhone
		// Code128
		// 二维码
		// map.put("Code128", proFullName);
		map.put("Creatdate", createDate);
		// 表格
		// map.put("MartStockFile", goodInfos);
		map.put("MID", hetongID);
		// map.put("ProAddress", proAddress);
		map.put("ProReceiveMan", proReceiveMan);
		map.put("ProShortName", proShortName);
		map.put("ProPhone", proPhone);
		map.put("ProFullName", proFullName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		map.put("NowTime", sdf.format(new Date()));
		WordUtils.replaceBookmark(map, doc);
		File codeDir = new File(hetongDir, "code");
		if (!codeDir.exists()) {
			codeDir.mkdirs();
		}
		String codePath = codeDir.getAbsolutePath() + "\\" + hetongID + ".png";
		File codeFile = new File(codePath);
		if (!codeFile.exists()) {
			Code128CCreator c = new Code128CCreator();
			String barCode = c.getCode(hetongID, "");
			c.kiCode128C(barCode, 2, 16, codePath);
		}
//		Code128CCreator c = new Code128CCreator();
//		String barCode = c.getCode(hetongID, "");
//		c.kiCode128C(barCode, 2, 16, codePath);
		/*
		 * int width = 100; int height = 35; BitMatrix matrix; try { matrix =
		 * new MultiFormatWriter().encode(hetongID, BarcodeFormat.CODE_128,
		 * width, height); FileOutputStream fos = new FileOutputStream(path);
		 * MatrixToImageWriter.writeToStream(matrix, "png", fos); fos.close(); }
		 * catch (WriterException e) { e.printStackTrace(); }
		 */

		WordUtils.insertImageAtBookmarkByMM("Code128", codePath, 26, "width", doc);
//		WordUtils.insertImageAtBookmark("Code128",codePath,doc);
		Dispatch tablePlace = WordUtils.getBookmark("MartStockFile", doc);
		// insertTable(ac,doc,WordUtils.CHUKUDAN_COlUMN_WIDTH,3,2,table);
		insertTable(ac, doc, WordUtils.HETONG_DETAIL, list.size() + 1, 7, tablePlace, list);
		String pdfPath = hetongDir.getAbsolutePath() + "\\" + htPatten + ".pdf";
		System.out.println("*****" + htPatten+ "正在转换...*****");
		WordUtils.word2pdf(doc, pdfPath, ac);
		System.out.println("*****" + htPatten + "转换完成*****");
		// 关闭word文件,0不保存，-2提示保存，-1直接保存
		WordUtils.closeDocument(doc, true);
		String waterPdf = hetongDir.getAbsolutePath() + "\\" + htPatten + "_w.pdf";
		FileOutputStream fio = new FileOutputStream(waterPdf);
		FileInputStream upIn=null;
//			ItextTest.setWatermark(fio, pdfPath, "北方科讯电子科技有限公司" + hetongID, true);
			fio.close();
			String ftpAddres = "172.16.6.22";
			upIn = new FileInputStream(waterPdf);
			String id = Md5Utils.encodeBase64(("ht_" + hetongID + Myuuid.create(4)).getBytes("utf-8"));
			String remoteName = id + ".pdf";
			// String remotePath = "/" + UploadUtils.getCurrentDate() + "/" +
			// remoteName;
			String remotePath = "/ZJy/hetong/" + UploadUtils.getCurrentDate() + "/" + remoteName;
			String uploadPath = "ftp://" + ftpAddres + remotePath;
			/*
			 * 上传加水印的pdf文件
			 */
			/*
			 * FTPClient mFtp = new FTPClient(); mFtp.setConnectTimeout(15 *
			 * 1000); mFtp.connect(ftpAddres, 21); mFtp.login("NEW_DYJ",
			 * "GY8Fy2Gx"); mFtp.setFileType(FTP.BINARY_FILE_TYPE);
			 * mFtp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			 * mFtp.enterLocalPassiveMode(); boolean isOK =
			 * mFtp.storeFile("/ZJy/pk/pdf" + System.currentTimeMillis() +
			 * ".pdf", upIn); try { mFtp.logout(); mFtp.disconnect(); } catch
			 * (Exception e) { e.printStackTrace();
			 */
			// DownUtils mClient = new DownUtils(ftpAddres, 21, "NEW_DYJ",
			// "GY8Fy2Gx");
			// boolean isOK = false;
			// // 上传pdf到ftp
			// try {
			// mClient.login();
			// isOK = mClient.upload(upIn, remotePath);
			// System.out.println("ftppath:" + remotePath);
			// } catch (Exception e1) {
			// e1.printStackTrace();
			// }
			// // 提交文件路径到数据库
			// // string UpdateHeTongFileInfo(int pid, string filepath);
			// if (isOK) {
			// LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
			// properties.put("pid", hetongID);
			// properties.put("filepath", uploadPath);
			// SoapObject req = WebserviceUtils.getRequest(properties,
			// "UpdateHeTongFileInfo");
			// SoapPrimitive res = null;
			// try {
			// res = WebserviceUtils.getSoapPrimitiveResponse(req,
			// SoapEnvelope.VER11,
			// WebserviceUtils.MartService);
			// System.out.println("upload_result:" + res.toString());
			// } catch (XmlPullParserException e) {
			// e.printStackTrace();
			// }
			// if (res != null && res.toString().equals("保存成功")) {
			// writer.write("\n");
			// } else {
			// writer.write("error:插入信息失败");
			// }
			// } else {
			// writer.write("error:上传ftp失败");
			// }
		if(upIn!=null){
			upIn.close();
		}
		writer.flush();
		writer.close();
		WordUtils.exit(ac);
		/*
		 * writer.write("生成合同成功"); writer.flush(); writer.close();
		 * ComThread.InitSTA(); ac.getProperty("Documents");
		 */
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
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
		for (int i = 1; i <= colCount; i++) { // 循环取出每一列
			Dispatch col = Dispatch.call(cols, "Item", new Variant(i)).toDispatch();
			Dispatch cells = Dispatch.get(col, "Cells").toDispatch();// 当前列中单元格
			int cellCount = Dispatch.get(cells, "Count").changeType(Variant.VariantInt).getInt();// 当前列中单元格数
			setColumnWidth(col, (float) len[i - 1]);
			for (int j = 1; j <= cellCount; j++) {// 每一列中的单元格数
				if (j == 1) {
					putTxtToCell(newTable, j, i, tableTitle[i - 1], ac);
				} else {
					// putTxtToCell(newTable, j, i, "第" + j + "行，第" + i + "列",
					// ac);
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

}
