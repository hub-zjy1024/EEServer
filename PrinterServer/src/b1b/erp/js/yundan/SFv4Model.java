package b1b.erp.js.yundan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.entity.RetJsonObj;
import b1b.erp.js.entity.YundanInfo;
import b1b.erp.js.yundan.kdn.KdApiEOrderDemo;
import b1b.erp.js.yundan.ky.util.KyService;
import b1b.erp.js.yundan.sf.bussiness.HtmlModelMaker;
import b1b.erp.js.yundan.sf.bussiness.OrderMgr;
import b1b.erp.js.yundan.sf.bussiness.SFdocMaker;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.entity.YundanModel;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils.OrderResponse;

public class SFv4Model {
	YundanInput minput;
	HttpServletRequest req;

	public SFv4Model(YundanInput minput, HttpServletRequest req) {
		super();
		this.minput = minput;
		this.req = req;
	}

	public void makeModle(YundanInput mData) {

		String fileName = getRandom("123") + ".pdf";
		File mFile = new File(fileName);
	}

	public YundanInput getTestInfo() {
		return null;
	}

	public String getData(Object obj) {
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
		String yundan = req.getParameter("yundanType");
		if (yundan == null) {
			yundan = "210";
		}
		// SFdocMaker maker = new SFdocMaker(req, yundan);

		RetJsonObj retJson = new RetJsonObj();
		try {
			// List<String> getFiles = maker.GetFiles(minfo);
			// YundanInput orderResponse =getTestInfo();
			// String json =
			// "{\"flag\":0,\"goodInfos\":\"AFCT-5805AZ&100$AFCT-5805AZ&200$GD25Q127CSIGR&150$GD25Q127CSIGR&85\",\"isSpecial\":\"0\",\"mSender\":{\"bagCounts\":\"1\",\"custid\":\"7550065577\",\"d_address\":\"成都市成华区建设北路三段168号内3栋\",\"d_cellphone\":\"\",\"d_city\":\"\",\"d_code\":\"\",\"d_company\":\"\",\"d_country\":\"\",\"d_district\":\"\",\"d_name\":\"俞元剑\",\"d_postcode\":\"\",\"d_province\":\"\",\"d_tel\":\"13540255168\",\"d_username\":\"\",\"expressType\":\"2\",\"j_address\":\"243234\",\"j_cellphone\":\"\",\"j_city\":\"\",\"j_code\":\"\",\"j_company\":\"234234\",\"j_country\":\"\",\"j_district\":\"\",\"j_name\":\"234\",\"j_postcode\":\"\",\"j_province\":\"\",\"j_tel\":\"234\",\"j_username\":\"\",\"needCode\":false,\"need_return_tracking_no\":\"\",\"orderID\":\"160044662396\",\"payType\":\"3\",\"routelabelForReturn\":1,\"routelabelService\":1},\"payType\":\"寄付月结\",\"pid\":\"123\",\"printer\":\"\",\"response\":{\"HK_in\":\"\",\"HK_out\":\"\",\"destRouteLable\":\"028\",\"destcode\":\"\",\"inputXml\":\"\",\"isReturn\":\"0\",\"proCode\":\"T6\",\"qrInfo\":\"MMM={'k1':'028','k2':'028','k3':'','k4':'T6','k5':'SF1041311802247','k6':'','k7':''}\",\"retXml\":\"\",\"yundanId\":\"SF1041311802247\"},\"tuoji\":\",2020/09/18
			// 16:30:37_123_0\",\"weight\":\"\",\"yundanType\":\"210\"}";
			// String json =
			// "{\"flag\":0,\"goodInfos\":\"AFCT-5805AZ&100$AFCT-5805AZ&200$GD25Q127CSIGR&150$GD25Q127CSIGR&85\",\"isSpecial\":\"0\",\"mSender\":{\"bagCounts\":\"1\",\"custid\":\"7550065577\",\"d_address\":\"北京市大兴区亦庄经济技术开发区地盛中路2号院\",\"d_cellphone\":\"\",\"d_city\":\"北京\",\"d_code\":\"\",\"d_company\":\"杭州和利时自动化有限公司（北京）\",\"d_country\":\"\",\"d_district\":\"北京\",\"d_name\":\"周伟光\",\"d_postcode\":\"\",\"d_province\":\"北京\",\"d_tel\":\"010
			// 57637244\",\"d_username\":\"\",\"expressType\":\"2\",\"j_address\":\"北京市海淀区高里掌路1号院2号楼1层101-262\",\"j_cellphone\":\"\",\"j_city\":\"北京市\",\"j_code\":\"\",\"j_company\":\"北京爱伯乐电子技术有限公司\",\"j_country\":\"\",\"j_district\":\"海淀区\",\"j_name\":\"王雪\",\"j_postcode\":\"\",\"j_province\":\"北京市\",\"j_tel\":\"010-62681069\",\"j_username\":\"\",\"needCode\":false,\"need_return_tracking_no\":\"\",\"orderID\":\"152273022246\",\"payType\":\"3\",\"routelabelForReturn\":1,\"routelabelService\":1},\"payType\":\"寄付月结\",\"pid\":\"1522730\",\"printer\":\"\",\"response\":{\"HK_in\":\"V7B\",\"HK_out\":\"\",\"destRouteLable\":\"010WB-HG-098\",\"destcode\":\"010\",\"inputXml\":\"\",\"isReturn\":\"0\",\"proCode\":\"T4\",\"qrInfo\":\"MMM={'k1':'010WB','k2':'010HG','k3':'098','k4':'T4','k5':'SF1028966790202','k6':'','k7':'56da3200'}\",\"retXml\":\"\",\"yundanId\":\"SF1028966790202\"},\"tuoji\":\"AFCT-5805AZ:100\\nAFCT-5805AZ:200\\nGD25Q127CSIGR:150...,2020/08/14
			// 12:05:36_1522730_and\",\"yundanType\":\"210\"}";
			//回单
//			String json = "{\"exLog\":\"kdn\",\"flag\":0,\"goodInfos\":\"AFCT-5805AZ&100$AFCT-5805AZ&200$GD25Q127CSIGR&150$GD25Q127CSIGR&85\",\"isSpecial\":\"0\",\"mSender\":{\"bagCounts\":\"2\",\"custid\":\"7550065577\",\"d_address\":\"北京市大兴区亦庄经济技术开发区地盛中路2号院\",\"d_cellphone\":\"\",\"d_city\":\"北京\",\"d_code\":\"\",\"d_company\":\"杭州和利时自动化有限公司（北京）\",\"d_country\":\"\",\"d_district\":\"北京\",\"d_name\":\"周伟光\",\"d_postcode\":\"\",\"d_province\":\"北京\",\"d_tel\":\"010 57637244\",\"d_username\":\"\",\"expressType\":\"2\",\"j_address\":\"北京市海淀区高里掌路1号院2号楼1层101-262\",\"j_cellphone\":\"\",\"j_city\":\"北京市\",\"j_code\":\"\",\"j_company\":\"北京爱伯乐电子技术有限公司\",\"j_country\":\"\",\"j_district\":\"海淀区\",\"j_name\":\"王雪\",\"j_postcode\":\"\",\"j_province\":\"北京市\",\"j_tel\":\"010-62681069\",\"j_username\":\"\",\"needCode\":false,\"need_return_tracking_no\":\"1\",\"orderID\":\"152273005613\",\"payType\":\"3\",\"routelabelForReturn\":1,\"routelabelService\":1},\"payType\":\"寄付月结\",\"pid\":\"1522730\",\"printer\":\"\",\"response\":{\"HK_in\":\"V7B\",\"HK_out\":\"\",\"destRouteLable\":\"010WB-HG-098\",\"destcode\":\"010\",\"inputXml\":\"\",\"isReturn\":\"0\",\"proCode\":\"T6\",\"qrInfo\":\"MMM={'k1':'010WB','k2':'010HG','k3':'098','k4':'T6','k5':'SF1028969068531','k6':'','k7':'e3eb4582'}\",\"retXml\":\"\",\"returnResponse\":{\"HK_in\":\"\",\"HK_out\":\"\",\"destRouteLable\":\"010WE-ABN-009\",\"destcode\":\"010\",\"inputXml\":\"\",\"isReturn\":\"1\",\"proCode\":\"T6\",\"qrInfo\":\"MMM={'k1':'010WE','k2':'010ABN','k3':'009','k4':'T6','k5':'SF1060712363325','k6':'','k7':'f73a81a6'}\",\"retXml\":\"\",\"yundanId\":\"SF1060712363325\"},\"yundanId\":\"SF1028969068531,SF2040007618112\"},\"tuoji\":\"AFCT-5805AZ:100\\nAFCT-5805AZ:200\\nGD25Q127CSIGR:150...,2020/08/14 12:58:33_1522730_and\",\"yundanType\":\"210\"}";
			//非回单
			String json = "{\"exLog\":\"kdn\",\"flag\":0,\"goodInfos\":\"AFCT-5805AZ&100$AFCT-5805AZ&200$GD25Q127CSIGR&150$GD25Q127CSIGR&85\",\"isSpecial\":\"0\",\"mSender\":{\"bagCounts\":\"2\",\"custid\":\"7550065577\",\"d_address\":\"北京市大兴区亦庄经济技术开发区地盛中路2号院\",\"d_cellphone\":\"\",\"d_city\":\"北京\",\"d_code\":\"\",\"d_company\":\"杭州和利时自动化有限公司（北京）\",\"d_country\":\"\",\"d_district\":\"北京\",\"d_name\":\"周伟光\",\"d_postcode\":\"\",\"d_province\":\"北京\",\"d_tel\":\"010 57637244\",\"d_username\":\"\",\"expressType\":\"2\",\"j_address\":\"北京市海淀区高里掌路1号院2号楼1层101-262\",\"j_cellphone\":\"\",\"j_city\":\"北京市\",\"j_code\":\"\",\"j_company\":\"北京爱伯乐电子技术有限公司\",\"j_country\":\"\",\"j_district\":\"海淀区\",\"j_name\":\"王雪\",\"j_postcode\":\"\",\"j_province\":\"北京市\",\"j_tel\":\"010-62681069\",\"j_username\":\"\",\"needCode\":false,\"need_return_tracking_no\":\"0\",\"orderID\":\"152273005613\",\"payType\":\"3\",\"routelabelForReturn\":1,\"routelabelService\":1},\"payType\":\"寄付月结\",\"pid\":\"1522730\",\"printer\":\"\",\"response\":{\"HK_in\":\"V7B\",\"HK_out\":\"\",\"destRouteLable\":\"010WB-HG-098\",\"destcode\":\"010\",\"inputXml\":\"\",\"isReturn\":\"0\",\"proCode\":\"T6\",\"qrInfo\":\"MMM={'k1':'010WB','k2':'010HG','k3':'098','k4':'T6','k5':'SF1028969068531','k6':'','k7':'e3eb4582'}\",\"retXml\":\"\",\"returnResponse\":{\"HK_in\":\"\",\"HK_out\":\"\",\"destRouteLable\":\"010WE-ABN-009\",\"destcode\":\"010\",\"inputXml\":\"\",\"isReturn\":\"1\",\"proCode\":\"T6\",\"qrInfo\":\"MMM={'k1':'010WE','k2':'010ABN','k3':'009','k4':'T6','k5':'SF1060712363325','k6':'','k7':'f73a81a6'}\",\"retXml\":\"\",\"yundanId\":\"SF1060712363325\"},\"yundanId\":\"SF1028969068531,SF2040007618112\"},\"tuoji\":\"AFCT-5805AZ:100\\nAFCT-5805AZ:200\\nGD25Q127CSIGR:150...,2020/08/14 12:58:33_1522730_and\",\"yundanType\":\"210\"}";
			YundanInput mInput = JSONObject.parseObject(json, YundanInput.class);
			if (obj != null && !"null".equals(obj)) {
				OrderMgr mgr = new OrderMgr();
				mInput = mgr.getData(obj.toString());
			}

			if ("150".equals(yundan)) {
				mInput.isSpecial = "1";
				System.out.println(getClass() + " getData 150  is special,  test!!!!!!!!!!!!!!1");
			}
			StringBuilder mdata = new StringBuilder();
			for (String s : minfo.yundans) {
				mdata.append(s);
				mdata.append(",");
			}
			if (mdata.length() > 0) {
				mdata = mdata.deleteCharAt(mdata.length() - 1);
			}
			// orderResponse.response = new OrderResponse();
			// orderResponse.response.yundanId = mdata.toString();
			// orderResponse.response.destcode = minfo.destRouteLable;
		
			if ("kdn".equals(mInput.exLog)) {
				KdApiEOrderDemo mKdn = new KdApiEOrderDemo();
				// YundanModel retYundan = mKdn.startOrder(mInput);
				KyService mKyMgr = new KyService(null, req);
				YundanModel retYundan = mKyMgr.startOrder(mInput);
				retJson.data.clear();
				retJson.data.add(retYundan.yundanId);
				retJson.data.add(retYundan.destcode);
				retJson.data.add(retYundan.url);
				// retJson.data.add(getFiles);
				retJson.data.add("nofiles");
				// String kdnHtml = String.format("<div class='item'>%s</div>", retYundan.htmls);
				// retJson.data.add(kdnHtml);
				retJson.data.add(retYundan.htmls);
			}else{
				HtmlModelMaker mMaker = new HtmlModelMaker(req, yundan);
				// yundanType
				// 生成html模版
				List<String> htmls = mMaker.getHtmls(mInput);
				retJson.data.add(mInput.response.yundanId);
				retJson.data.add(mInput.response.destcode);
				retJson.data.add("http://testUrl");
				// retJson.data.add(getFiles);
				retJson.data.add("nofiles");
				retJson.data.add(htmls);
			}
			retJson.errCode = 0;
			retJson.errMsg = "成功";
		} catch (NullPointerException e) {
			e.printStackTrace();
			retJson.errMsg = "缺少必要参数," + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			retJson.errMsg = "异常," + e.getMessage();
		} catch (Throwable e) {
			e.printStackTrace();
			retJson.errMsg = "未知异常:" + e.getMessage();
		}
		return JSONObject.toJSONString(retJson);
	}

	public List<String> getOnlyHtmls() throws IOException {
		YundanInput orderResponse = minput;
		HtmlModelMaker mMaker = new HtmlModelMaker(req, orderResponse.yundanType);
		// yundanType
		// 生成html模版
		List<String> htmls = mMaker.getHtmls(orderResponse);
		return htmls;
	}

	public static String objToJsonStr(Object obj) {
		return JSONObject.toJSONString(obj);
	}

	public String getRandom(String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
		String realData = String.valueOf(Math.random());
		return realData;
	}

	public static void main(String[] args) throws Exception {
		String fileName = "D:\\dyj\\save\\itxPdfTest.pdf";
		test(fileName);
	}

	public static String readFileBase64(String file) throws IOException {
		File mfile = new File(file);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(mfile);

		byte[] buffer = new byte[1024 * 1000];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			bao.write(buffer, 0, len);
		}
		byte[] mData = bao.toByteArray();
		bao.close();
		fis.close();
		String data = Base64.getEncoder().encodeToString(mData);
		return data;
	}

	private static void test(String fileName) {
		Document document = new Document();
		try {
			System.out.println("start");

			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();

			PdfPTable table = new PdfPTable(1);
			table.setKeepTogether(true);
			table.setSplitLate(false);

			PdfPTable table1 = new PdfPTable(1);
			PdfPCell cell0 = new PdfPCell();
			Paragraph p = new Paragraph("table title sample");
			p.setAlignment(1);
			p.setSpacingBefore(15f);
			cell0.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);// 然并卵
			cell0.setPaddingTop(-2f);// 把字垂直居中
			cell0.setPaddingBottom(8f);// 把字垂直居中
			cell0.addElement(p);
			cell0.setBorder(0);
			table1.addCell(cell0);

			PdfPTable pTable = new PdfPTable(table1);
			document.add(pTable);

			PdfPTable table2 = new PdfPTable(2);
			float border = 1.5f;
			for (int a = 0; a < 20; a++) {
				PdfPCell cell = new PdfPCell();
				Paragraph pp;
				if (a == 0 || a == 1) {
					pp = str2ParaByTwoFont("tableTitle" + (a + 1), 9f, BaseColor.BLACK, Font.BOLD); // 小五
																									// 加粗
					cell.setBorderWidthBottom(border);
					cell.setBorderWidthTop(border);
				} else {
					if (a == 18 || a == 19) {
						cell.setBorderWidthTop(0);
						cell.setBorderWidthBottom(border);
					} else {
						cell.setBorderWidthBottom(0);
						cell.setBorderWidthTop(0);
					}
					pp = str2ParaByTwoFont("tableContent" + (a - 1), 9f, BaseColor.BLACK); // 小五
				}
				// 设置间隔的背景色
				if ((a + 1) % 2 == 0) {
					if (((a + 1) / 2) % 2 == 1) {
						cell.setBackgroundColor(new BaseColor(128, 128, 255));
					} else {
						cell.setBackgroundColor(new BaseColor(128, 255, 255));
					}
				} else {
					if (((a + 1) / 2) % 2 == 1) {
						cell.setBackgroundColor(new BaseColor(128, 255, 255));
					} else {
						cell.setBackgroundColor(new BaseColor(128, 128, 255));
					}
				}
				pp.setAlignment(1);
				cell.setBorderWidthLeft(0);
				cell.setBorderWidthRight(0);
				cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);// 然并卵
				cell.setPaddingTop(-2f);// 把字垂直居中
				cell.setPaddingBottom(8f);// 把字垂直居中
				cell.addElement(pp);
				table2.addCell(cell);
			}

			PdfPCell c1 = new PdfPCell();
			c1.setBorder(0);
			c1.addElement(table1);

			PdfPCell c2 = new PdfPCell();
			c2.setBorder(0);
			c2.addElement(table2);

			table.addCell(c1);
			table.addCell(c2);

			document.add(table);
			document.close();
			System.out.println("finished");
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 两种字体显示文字
	 *
	 * @param cont
	 * @param size
	 * @param color
	 * @return
	 */
	private static Paragraph str2ParaByTwoFont(String cont, float size, BaseColor color) {
		Paragraph res = new Paragraph();
		FontSelector selector = new FontSelector();
		// 非汉字字体颜色
		Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, size);
		f1.setColor(color);
		// 汉字字体颜色
		Font f2 = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, size);
		f2.setColor(color);

		selector.addFont(f1);
		selector.addFont(f2);
		Phrase ph = selector.process(cont);
		res.add(ph);
		return res;
	}

	/**
	 * 两种字体显示文字
	 *
	 * @param cont
	 * @param size
	 * @param color
	 * @param bold
	 * @return
	 */
	private static Paragraph str2ParaByTwoFont(String cont, float size, BaseColor color, int bold) {

		Paragraph res = new Paragraph();
		FontSelector selector = new FontSelector();
		// 非汉字字体颜色
		Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, size);
		f1.setColor(color);
		f1.setStyle(bold);
		// 汉字字体颜色
		Font f2 = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, size);
		f2.setColor(color);
		f2.setStyle(bold);

		selector.addFont(f1);
		selector.addFont(f2);
		Phrase ph = selector.process(cont);
		res.add(ph);
		return res;
	}
}
