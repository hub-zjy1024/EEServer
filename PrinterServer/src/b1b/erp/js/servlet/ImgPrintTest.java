package b1b.erp.js.servlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import b1b.erp.js.Code128CCreator;
import b1b.erp.js.utils.FileUtils;
import b1b.erp.js.utils.KyImageUtils;
import b1b.erp.js.utils.UploadUtils;

/**
 * Servlet implementation class ImgPrintTest
 */
@WebServlet("/ImgPrintTest")
public class ImgPrintTest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImgPrintTest() {
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
		String imgPath = request.getServletContext().getRealPath("/docTemplate/ky_mod.png");
		// if (true) {
		// imgPath="D:/dyj/SF/pdf2.pdf_mod15132486887210.png";
		// KyImageUtils.printImgAt(imgPath, 103, 210);
		// return;
		// }
		String wordDir = getServletContext().getInitParameter("dyjDir");
		String path2 = wordDir + "dyj/" + UploadUtils.getCurrentYearAndMonth();
		File saveDir = new File(path2);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		String orderID = "123412341234";
		String cardID = "123456789";
		String destcode = "212";
		String yundanType = "陆运件";
		String tuojiwu = "图片托寄";
		String goodinfos = "good";
		String counts = "1";
		String jName = "杨晚华";
		String jPhone = "123654789";
		String jAddress = "北京市海淀区彩和坊路10号1号楼503室";
		// 25
		String dName = "张进梅 ";
		String dPhone = "021-67742578";
		// 48
		String dAddress = "上海市松江区车墩镇香泾路79号4号楼3层北京市海淀区彩和坊路10号1号楼503室";
		String notes = "測試測試測試測試";
		String payType = "寄付月结";
		String name = orderID + Math.random() + ".jpg";
		String finalImg = path2 + name;
		String resultImg = finalImg + "save.jpg";
		try {
			FileInputStream fin = new FileInputStream(imgPath);
			FileUtils.fileCopy(fin, finalImg);
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String cTime = sdf.format(new Date());
		for (; true;) {
			jName += " ";
			if (jName.length() == 5) {
				break;
			}
		}
		for (; true;) {
			dName += " ";
			if (dName.length() == 5) {
				break;
			}
		}
		// double scaleType = 203 / 2.54;
		// double scaleType = 600 / 2.54;
		double scaleType = 203 / 2.54;
		int totalWidth = (int) (10.05 * scaleType);
		int totalHeight = (int) (21 * scaleType);
		int tableWidth = (int) (9.85 * scaleType);
		int tableHeight = (int) (20.8 * scaleType);
		int blankWidth = (int) (0.1 * scaleType);
		int blankHeight = (int) (0.1 * scaleType);
		int cellPadding = (int) (0.1 * scaleType);
		int lineX1 = blankWidth + (int) (0.6 * scaleType);
		int tableLeft = blankWidth;
		int tableRight = totalWidth - blankWidth;
		BufferedImage baseImage = new BufferedImage(totalWidth, totalHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = baseImage.createGraphics();
		// g. = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
		// g.PixelOffsetMode = System.Drawing.Drawing2D.PixelOffsetMode.Half;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Stroke smoothStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g.setStroke(smoothStroke);
		g.setBackground(Color.white);
		g.setColor(Color.white);
		g.fillRect(0, 0, totalWidth, totalHeight);
		g.setColor(Color.BLACK);
		int quater4 = tableWidth / 4;
		int lineX2 = tableLeft + quater4 * 3;
		float hArrays[] = new float[] { 0, 1.9f, 0.8f, 1.2f, 0.8f, 0.4f, 0.4f, 0.8f, 1.64f, 1.06f,
				0.8f, 1.2f, 0.8f, 0.4f, 0.8f, 1.47f, 1.06f, 0.8f, 1.2f, 0.8f, 0.4f, 0.8f, 0.86f };
		int[] lineYs = new int[] { 11, 233, 326, 466, 560, 607, 653, 747, 938, 1062, 1155, 1294,
				1387, 1437, 1530, 1701, 1825, 1918, 2058, 2152, 2200, 2293, 2366 };
		int currentX = blankHeight;
		lineYs[0] = blankHeight;
		double fontScale = 0.8f;
		int dcodeFontSize = (lineYs[2] - lineYs[1]);
		System.out.println("destcodefont:" + dcodeFontSize);
		int codeStrFontSize = (lineYs[9] - lineYs[8]) * 3 / 8;
		for (int i = 1; i < lineYs.length; i++) {
			int x = (int) (hArrays[i] * scaleType);
			currentX += x;
			lineYs[i] = currentX;
		}
		int tagJSize = (lineYs[2] - lineYs[1]) * 3 / 8;
		int tagJDur = tagJSize * 11 / 10;
		int codeH1 = (lineYs[1] - lineYs[0]) / 2;
		int codeH2 = (lineYs[9] - lineYs[8]) / 2;
		int sizeHeight = (lineYs[5] - lineYs[4]) * 7 / 8;
		int commonMarginTop = sizeHeight * 4 / 5;
		int yundanTypeFontSize = (lineYs[4] - lineYs[3]) * 9 / 10;
		// 竖线
		g.drawLine(tableLeft, lineYs[0], tableLeft, lineYs[22]);
		g.drawLine(tableRight, lineYs[0], tableRight, lineYs[22]);
		// 横线
		g.drawLine(lineX2, lineYs[1], lineX2, lineYs[2]);
		g.drawLine(lineX2, lineYs[3], lineX2, lineYs[5]);
		g.drawLine(lineX2, lineYs[11], lineX2, lineYs[12]);
		g.drawLine(lineX2, lineYs[16], lineX2, lineYs[17]);
		g.drawLine(lineX2, lineYs[18], lineX2, lineYs[19]);
		g.drawLine(lineX1, lineYs[1], lineX1, lineYs[3]);
		g.drawLine(lineX1, lineYs[9], lineX1, lineYs[11]);
		g.drawLine(lineX1, lineYs[16], lineX1, lineYs[18]);
		String[] row5 = new String[] { "寄件签署", "收件员", "收方签名", "派件员" };
		for (int i = 0; i < row5.length; i++) {
			int tempLineX = tableLeft + (i * quater4);
			int tempLineY = lineYs[4] + commonMarginTop;
			if (i < 3) {
				g.drawLine(tempLineX, lineYs[4], tempLineX, lineYs[5]);
			}
			int tempX = tempLineX + cellPadding;
			KyImageUtils.pressTextByHeightPxTop(row5[i], g, "微软雅黑", Font.PLAIN, Color.black,
					sizeHeight, tempX, tempLineY);
		}
		KyImageUtils.pressTextByHeightPx("打印时间：" + cTime, g, "微软雅黑", Font.PLAIN, Color.black,
				sizeHeight, blankWidth + cellPadding, lineYs[1] - sizeHeight / 5);
		int reshowDur = 7;
		int dcodeX = lineX2 + cellPadding;
		int dcodeY = lineYs[1] + (int) (dcodeFontSize * 5/8);
		KyImageUtils.pressTextByHeightPxTop(destcode, g, "微软雅黑", Font.BOLD, Color.black,
				dcodeFontSize, dcodeX, dcodeY);
		int firstYudan = 3;
		for (int i = 0; i < 3; i++) {
			int yundanY = lineYs[firstYudan] + yundanTypeFontSize * 4 / 5;
			KyImageUtils.pressTextByHeightPxTop(yundanType, g, "微软雅黑", Font.BOLD, Color.black,
					yundanTypeFontSize, dcodeX, yundanY);
			if (i == 0) {
				firstYudan += reshowDur + 1;
			} else {
				firstYudan += reshowDur;
			}
		}
		KyImageUtils.pressTextByHeightPxTop("寄件签署", g, "微软雅黑", Font.PLAIN, Color.black, sizeHeight,
				lineX2 + cellPadding, lineYs[16] + (int) (sizeHeight * fontScale));
		for (int i = 0; i < lineYs.length; i++) {
			// 横线
			g.drawLine(tableLeft, lineYs[i], tableRight, lineYs[i]);
		}
		String[] row6 = new String[] { "寄件时间:", "签收时间:" };
		int row6First = 5;
		int row6Y = lineYs[row6First] + commonMarginTop;
		for (int j = 0; j < 3; j++) {
			row6Y = lineYs[row6First] + commonMarginTop;
			for (int i = 0; i < row6.length; i++) {
				int tempLineX = tableLeft + (i * quater4) * 2;
				g.drawLine(tempLineX, lineYs[row6First], tempLineX, lineYs[row6First + 1]);
				int tempX = tempLineX + cellPadding;
				KyImageUtils.pressTextByHeightPxTop(row6[i] + "    年   月    日    时    分", g, "微软雅黑",
						Font.PLAIN, Color.black, sizeHeight, tempX, row6Y);
			}
			row6First += reshowDur;
		}
		String[] row71 = new String[] { "件数:", "实重:", "计重:", "费用合计:" };
		String[] row72 = new String[] { "运费:", "付款方式:", "月结卡号:", "" };
		int row7First = 6;
		int row7LineDur = sizeHeight * 11 / 10;
		for (int j = 0; j < 3; j++) {
			int lens[] = new int[] { 0, tableWidth * 12 / 100, tableWidth * 25 / 100,
					tableWidth * 30 / 100 };
			int tempX = tableLeft + cellPadding;
			for (int i = 0; i < row71.length; i++) {
				tempX += lens[i];
				int tempY = lineYs[row7First] + commonMarginTop;
				String temp1 = row71[i];
				String temp2 = row72[i];
				if (i == 0) {
					temp1 += counts;
				}
				if (i == 1) {
					temp2 += payType;
				} else if (i == 2) {
					temp2 += cardID;
				}
				KyImageUtils.pressTextByHeightPxTop(temp1, g, "微软雅黑", Font.PLAIN, Color.black,
						sizeHeight, tempX, tempY);
				KyImageUtils.pressTextByHeightPxTop(temp2, g, "微软雅黑", Font.PLAIN, Color.black,
						sizeHeight, tempX, tempY + row7LineDur);
			}
			row7First += reshowDur;
		}
		String infos[] = new String[] { "exse1:200", "exse2:500", "exse3:100" };
		if (goodinfos != null) {
			String[] tempinfos = goodinfos.split("\\$");
			for (int i = 0; i < tempinfos.length; i++) {
				String[] s = tempinfos[i].split("&");
				if (i == 3) {
					if (infos.length > 3) {
						infos[i] = s[0] + " ：" + s[1] + "等";
					}
					break;
				}
				if (s.length != 1) {
					infos[i] = s[0] + " ：" + s[1];
				}
			}
		}
		int row4First = 3;
		int row4Font = (lineYs[4] - lineYs[3]) / 3;
		int row4Line = row4Font * 9 / 10;
		for (int j = 0; j < 3; j++) {
			int tempY = lineYs[row4First] + row4Font * 4 / 5;
			int tempX = tableLeft + cellPadding;
			for (int i = 0; i < infos.length; i++) {
				String line = infos[i];
				if (i == 0) {
					line = "托寄物:" + line;
				}
				KyImageUtils.pressTextByHeightPxTop(line, g, "微软雅黑", Font.PLAIN, Color.black,
						row4Font, tempX, tempY);
				tempY += row4Line;
			}
			if (j == 0) {
				row4First += reshowDur + 1;
			} else {
				row4First += reshowDur;
			}
		}
		int row2First = 1;
		int row2chars1 = 20;
		int row2chars2 = 30;
		int row2LineDur = sizeHeight * 5 / 100;
		for (int j = 0; j < 3; j++) {
			int tempY = lineYs[row2First] + commonMarginTop;
			int tempY2 = lineYs[row2First + 1] + commonMarginTop;
			int tempX = lineX1 + cellPadding;
			KyImageUtils.pressTextByHeightPxTop(jName + "   " + jPhone, g, "微软雅黑", Font.PLAIN,
					Color.black, sizeHeight, tempX, tempY);
			KyImageUtils.pressTextAtLength(jAddress, sizeHeight, tempY + commonMarginTop, tempX, g,
					row2chars1, row2LineDur, 2);
			KyImageUtils.pressTextByHeightPxTop(dName + "   " + jPhone, g, "微软雅黑", Font.PLAIN,
					Color.black, sizeHeight, tempX, tempY2);
			KyImageUtils.pressTextAtLength(dAddress, sizeHeight, tempY2 + commonMarginTop, tempX, g,
					row2chars2, row2LineDur, 3);
			if (j == 0) {
				row2First += reshowDur + 1;
			} else {
				row2First += reshowDur;
			}
		}
		Code128CCreator c = new Code128CCreator();
		String barCode = "";
		try {
			barCode = c.getCodeA(orderID, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int codeW = tableWidth*7 / (12 * barCode.length());
		int tagJX1 = blankWidth + cellPadding;
		String[] tagJ = new String[] { "寄", "方" };
		String[] tagD = new String[] { "收", "方" };
		int codex = tableLeft + tableWidth / 2 + cellPadding;
		int codey = lineYs[0] + cellPadding;
		int codebelowDur = codeStrFontSize * 4 / 5;
		KyImageUtils.drawCode(codex, codey, barCode, g, codeW, codeH1);
		KyImageUtils.pressTextByHeightPxTop(orderID, g, "微软雅黑", Font.BOLD, Color.black,
				codeStrFontSize, codex, codey + codeH1 + codebelowDur);
		codey = lineYs[8] + cellPadding;
		KyImageUtils.drawCode(codex, codey, barCode, g, codeW, codeH2);
		KyImageUtils.pressTextByHeightPxTop(orderID, g, "微软雅黑", Font.BOLD, Color.black,
				codeStrFontSize, codex, codey + codeH2 + codebelowDur);
		codey = lineYs[15] + cellPadding;
		KyImageUtils.drawCode(codex, codey, barCode, g, codeW, codeH2);
		KyImageUtils.pressTextByHeightPxTop(orderID, g, "微软雅黑", Font.BOLD, Color.black,
				codeStrFontSize, codex, codey + codeH2 + codebelowDur);
		int firstTagJ = 1;
		for (int i = 0; i < 3; i++) {
			int jTagY = lineYs[firstTagJ] + tagJSize * 4 / 5;
			for (int j = 0; j < tagJ.length; j++) {
				KyImageUtils.pressTextByHeightPxTop(tagJ[j], g, "微软雅黑", Font.BOLD, Color.black,
						tagJSize, tagJX1, jTagY);
				jTagY += tagJDur;
			}
			jTagY = lineYs[firstTagJ + 1] + tagJSize * 4 / 5;
			for (int j = 0; j < tagD.length; j++) {
				KyImageUtils.pressTextByHeightPxTop(tagD[j], g, "微软雅黑", Font.BOLD, Color.black,
						tagJSize, tagJX1, jTagY);
				jTagY += tagJDur;
			}
			if (i == 0) {
				firstTagJ += reshowDur + 1;
			} else {
				firstTagJ += reshowDur;
			}
		}
		int firstNote = 7;
		for (int i = 0; i < 3; i++) {
			int jTagY = lineYs[firstNote] + commonMarginTop;
			KyImageUtils.pressTextByHeightPxTop("备注：" + notes, g, "微软雅黑", Font.BOLD, Color.black,
					tagJSize, tagJX1, jTagY);
			firstNote += reshowDur;
		}
		BufferedImage ruiBufImg = new BufferedImage(totalWidth, totalHeight,
				BufferedImage.TYPE_INT_ARGB);
		float[] data = { -1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f, -1.0f, -1.0f };
		Kernel kernel = new Kernel(3, 3, data);
		RenderingHints ruiHint = new RenderingHints(null);
		ruiHint.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, ruiHint);
		co.filter(baseImage, ruiBufImg);
		String ruiPath = resultImg + "_rui.jpg";
		try {
			ImageIO.write(ruiBufImg, "jpg", new File(ruiPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		KyImageUtils.save(resultImg, g, baseImage);
		KyImageUtils.printImgAt(resultImg, 102, 210);
		
//		KyImageUtils.printImgAt(ruiPath, 100, 210);
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

	public static void printTest() {
		Book book = new Book();
		// 设置成竖打
		PageFormat pf = new PageFormat();
		pf.setOrientation(PageFormat.PORTRAIT);
		// 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
		Paper paper = new Paper();
		double width = 10.2;
		double height = 21;
		double persent = 72 / 2.54;
		double fW = width * persent;
		double fH = height * persent;
		paper.setSize(fW, fH);// 纸张大小
		paper.setImageableArea(0.5 * persent, 0.1 * persent, (width - 1) * persent,
				(height - 0.2) * persent);// A4(595 X
		pf.setPaper(paper);
		book.append(new Printable() {

			@Override
			public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
					throws PrinterException {
				// TODO Auto-generated method stub
				// 转换成Graphics2D 拿到画笔
				Graphics2D g2 = (Graphics2D) graphics;
				// 设置打印颜色为黑色
				g2.setColor(Color.black);
				// 打印起点坐标
				double x = pageFormat.getImageableX();
				double y = pageFormat.getImageableY();
				double width2 = pageFormat.getImageableWidth();
				double height2 = pageFormat.getImageableHeight();
				// 虚线
				float[] dash1 = { 1f };
				// width - 此 BasicStroke 的宽度。此宽度必须大于或等于 0.0f。如果将宽度设置为
				// 0.0f，则将笔划呈现为可用于目标设备和抗锯齿提示设置的最细线条。
				// cap - BasicStroke 端点的装饰
				// join - 应用在路径线段交汇处的装饰
				// miterlimit - 斜接处的剪裁限制。miterlimit 必须大于或等于 1.0f。
				// dash - 表示虚线模式的数组
				// dash_phase - 开始虚线模式的偏移量
				// 设置画虚线
				g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
						4.0f, dash1, 0.0f));
				// 设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
				Font font = new Font("微软雅黑", Font.PLAIN, 11);
				Code128CCreator c = new Code128CCreator();
				String codeStr = "201716002311";
				String barCode = c.getCode(codeStr, "");
				// content0 = "12u!^&*($@$*)~!_%(";
				String codeA = "";
				try {
					codeA = c.getCodeA(codeStr, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				char[] cs = codeA.toCharArray();
				int height = (int) (1.5 * persent);
				int len = (int) (width2 / 2 / cs.length);
				int barcodeOff = (int) (x + width2 / 2);
				Graphics g = g2;
				for (int i = 0; i < cs.length; i++) {
					if ("1".equals(cs[i] + "")) {
						g.setColor(Color.BLACK);
						g.fillRect(barcodeOff + i * len, 0, len, height);
					} else {
						g.setColor(Color.WHITE);
						g.fillRect(barcodeOff + i * len, 0, len, height);
					}
				}
				float hArrays[] = new float[] { 1.9f, 0.8f, 1.2f, 0.8f, 0.4f, 0.4f, 0.8f, 1.64f,
						1.06f, 0.8f, 1.2f, 0.8f, 0.4f, 0.8f, 1.47f, 1.06f, 0.8f, 1.2f, 0.8f, 0.4f,
						0.8f, 0.86f };
				g2.setFont(font);// 设置字体
				switch (pageIndex) {
				case 0:
					return PAGE_EXISTS;
				default:
					return NO_SUCH_PAGE;
				}
			}
		}, pf);
		// 获取打印服务对象
		PrinterJob job = PrinterJob.getPrinterJob();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 0; i < services.length; i++) {
			PrintService s = services[i];
			Object supportedAttributeValues = s.getSupportedAttributeValues(PrintQuality.class,
					null, null);
			Class<?>[] supportedAttributeCategories = s.getSupportedAttributeCategories();
			supportedAttributeValues = s.getSupportedAttributeValues(Attribute.class, null, null);
			supportedAttributeValues = s.getSupportedAttributeValues(Destination.class, null, null);
			supportedAttributeValues = s.getSupportedAttributeValues(PrinterResolution.class, null,
					null);
			sBuilder.append("=========").append(s.getName()).append("\n");
			if (supportedAttributeValues != null) {
				sBuilder.append(supportedAttributeValues.getClass().getName() + "\n");
			}
			if (supportedAttributeCategories != null) {
				for (Class cla : supportedAttributeCategories) {
					sBuilder.append(cla.getName() + "\n");
				}
			}
			// s.getSupportedAttributeValues(HashAttributeSet.class, null,
			// null);
		}
		System.out.println("result:" + sBuilder.toString());
		// 设置打印类
		job.setPageable(book);
		try {
			HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
			set.add(new MediaPrintableArea(0, 0, 10.3f, 21, MediaPrintableArea.MM));
			File f = new File("d:/dyj/print.prn");
			f.toURI();
			set.add(new Destination(f.toURI()));
			boolean a = job.printDialog(set);
			if (a) {
				job.print();
			} else {
				job.cancel();
			}
		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

}
