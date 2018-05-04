package b1b.erp.js.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
import javax.print.attribute.standard.Compression;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrintQuality;

import b1b.erp.js.Code128CCreator;

public class KyImageUtils {
	public final static void pressTextByHeightPx(String pressText, Graphics2D g, String fontName,
			int fontStyle, Color color, int fHeight, int x, int y) {
		try {
			g.setColor(color);
			int suitHeight = 0;
			for (int i = 1; true; i++) {
				Font font = new Font(fontName, fontStyle, i);
				g.setFont(font);
				FontRenderContext context = g.getFontRenderContext();
				Rectangle2D stringBounds = font.getStringBounds(pressText, context);
				double fontHeight = stringBounds.getHeight();
				if (fontHeight - fHeight > 0.5) {
					suitHeight = (int) fontHeight;
					break;
				}
			}
			g.drawString(pressText, x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static void pressTextByHeightPxTop(String pressText, Graphics2D g, String fontName,
			int fontStyle, Color color, int fHeight, int x, int y) {
		try {
			g.setColor(color);
			int suitHeight = 0;
			double minOffset = Integer.MAX_VALUE;
			int actualSize = Integer.MAX_VALUE;
			for (int i = 1; i < 200; i++) {
				Font font = new Font(fontName, fontStyle, i);
				g.setFont(font);
				FontRenderContext context = g.getFontRenderContext();
				Rectangle2D stringBounds = font.getStringBounds(pressText, context);
				double fontHeight = stringBounds.getHeight();
				double offset = Math.abs(fontHeight - fHeight);
//				if (fontHeight - fHeight > 2) {
//					break;
//				}
				if (minOffset > offset) {
					if(pressText.equals("212")){
						System.out.println("offset:"+offset+"-intsize:"+i+"-dSize:"+fontHeight);
					}
					minOffset = offset;
					actualSize = i;
				}
			}
			Font font = new Font(fontName, fontStyle, actualSize);
			if(pressText.equals("212")){
				System.out.println("calculate-fontsize:"+actualSize);
				font=new Font(fontName, fontStyle, actualSize);
			}
			g.setFont(font);
			g.drawString(pressText, x, y + suitHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static double getStringWidth(String pressText, Graphics2D g, String fontName,
			int fontStyle, int fontSize) {
		try {
			Font font = new Font(fontName, fontStyle, fontSize);
			g.setFont(font);
			FontRenderContext context = g.getFontRenderContext();
			Rectangle2D stringBounds = font.getStringBounds(pressText, context);
			double fontHeight = stringBounds.getWidth();
			return fontHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1d;
	}

	public static void save(String destImageFile, Graphics g, Image image) {
		g.dispose();
		try {
			ImageIO.write((BufferedImage) image, "jpg", new File(destImageFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printImgAt(String destImageFile, int wMM, int hMM) {
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
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		if (service != null) {
			try {
				DocPrintJob job = service.createPrintJob();
				FileInputStream fis = new FileInputStream(file);
				DocAttributeSet das = new HashDocAttributeSet();
				das.add(new MediaPrintableArea(0, 0, wMM, hMM, MediaPrintableArea.MM));
				das.add(PrintQuality.HIGH);
				// javax.print.attribute.standard.Compression.NONE
				// das.add(attribute))
				Doc doc = new SimpleDoc(fis, flavor, das);
				job.print(doc, pras);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	public final static void drawImg(int x, int y, String srcImageFile, Graphics2D g) {
		try {
			File img = new File(srcImageFile);
			Image src = ImageIO.read(img);
			drawImg(x, y, src, g);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final static void drawCode(int x, int y, String codeResult, Graphics2D g, int len,
			int height) {
		char[] cs = codeResult.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if ("1".equals(cs[i] + "")) {
				g.setColor(Color.BLACK);
				g.fillRect(x + i * len, y, len, height);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x + i * len, y, len, height);
			}
		}
	}

	public final static void drawImg(int x, int y, Image src, Graphics2D g) {
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		g.drawImage(src, x, y, width, height, null);
	}

	public static void pressTextAtLength(String s, int fontHeight, int cY, int cX, Graphics2D g,
			int charCounts, int lineDur,int lines) {
		int temY = cY;
		String lStr = s;
		for (int i = 0; i < lines; i++) {
			if (lStr.length() >= charCounts) {
				String sub = lStr.substring(0, charCounts);
				pressTextByHeightPxTop(sub, g, "微软雅黑", Font.PLAIN, Color.black, fontHeight, cX,
						temY);
				lStr = lStr.substring(charCounts);
			} else {
				pressTextByHeightPxTop(lStr, g, "微软雅黑", Font.PLAIN, Color.black, fontHeight, cX,
						temY);
				break;
			}
			temY += fontHeight *4/5;
		}
	}
}
