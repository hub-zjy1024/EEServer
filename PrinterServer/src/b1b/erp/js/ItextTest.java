package b1b.erp.js;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ItextTest {
	public static void main(String[] args) {
		// step 1
		String filename = "d:/zjy.pdf";
		Document document = new Document();
		// step 2
		BufferedOutputStream bos = null;
		try {
			/*
			 * PdfWriter.getInstance(document, new FileOutputStream(filename));
			 * // step 3 document.open(); // HttpURLConnection conn =
			 * (HttpURLConnection) new //
			 * URL("http://www.baidu.com").openConnection(); // step 4
			 * document.add(new Paragraph("Hello World!")); // step 5 //
			 * 要输出的pdf文件
			 * 
			 * document.close();
			 */
			bos = new BufferedOutputStream(new FileOutputStream(new File("d:/zjy_water.pdf")));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			// 将pdf文件先加水印然后输出
			setWatermark(bos, filename, format.format(new Date()) + "  下载使用人：测试user");
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (bos != null) {
				bos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setWatermark(OutputStream out, String srcPDF, String waterMarkName)
			throws DocumentException, IOException {

		PdfReader reader = new PdfReader(srcPDF);
		PdfStamper stamper = new PdfStamper(reader, out);
		int total = reader.getNumberOfPages() + 1;
		PdfContentByte content;
		BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		PdfGState gs = new PdfGState();
		for (int i = 1; i < total; i++) {
			content = stamper.getOverContent(i);// 在内容上方加水印
			// content = stamper.getUnderContent(i);//在内容下方加水印
			gs.setFillOpacity(0.3f);
			// content.setGState(gs);
			content.beginText();
			content.setColorFill(BaseColor.LIGHT_GRAY);
			content.setFontAndSize(base, 20);
			content.setTextMatrix(70, 200);
			content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 300, 800, 20);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			content.showTextAligned(Element.ALIGN_CENTER,sdf.format(new Date()), 300, 750, 20);
//			Image image = Image.getInstance("D:/testimg2.jpg");
//			image.setBorder(Image.BOX);
//			image.setBorderWidth(1);
//			image.setBorderColor(BaseColor.WHITE);
//			image.scaleToFit(200, 200);// 大小
//			image.setRotationDegrees(0);// 旋转
//			image.setAbsolutePosition(200, 500); // set the first background
			// image of the absolute
//			image.scaleToFit(200, 200);
//			content.addImage(image);
//			content.setColorFill(BaseColor.BLACK);
//			content.setFontAndSize(base, 8);
//			content.showTextAligned(Element.ALIGN_CENTER, "下载时间：" + waterMarkName + "", 300, 10, 0);
			content.endText();
		}
		stamper.close();
	}
	public static void setWatermark(OutputStream out, String srcPDF, String waterMarkName,boolean isFull)
			throws DocumentException, IOException {
		PdfReader reader = new PdfReader(srcPDF);
		PdfStamper stamper = new PdfStamper(reader, out);
		int total = reader.getNumberOfPages() ;
		PdfContentByte content;
		//仿宋
//		C:\Windows\Fonts\simfang.ttf
//		BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont base = BaseFont.createFont("C:/Windows/Fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		PdfGState gs = new PdfGState();
		for (int i = 1; i <=total; i++) {
			content = stamper.getOverContent(i);// 在内容上方加水印
			// content = stamper.getUnderContent(i);//在内容下方加水印
			gs.setFillOpacity(0.2f);
			// content.setGState(gs);
			content.beginText();
			content.setColorFill(BaseColor.LIGHT_GRAY);
			content.setFontAndSize(base, 30);
			content.setTextMatrix(70, 200);
			Rectangle size=	 reader.getPageSize(i);
			float height=size.getHeight();
			float width=size.getWidth();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String cDate=sdf.format(new Date());
			int y=100;
			for(;y<height;){
				content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 250, y, 25);
//				content.showTextAligned(Element.ALIGN_CENTER,cDate, 250, y+50, 25);
				y=y+100;
			}
			content.endText();
		}
		stamper.close();
		reader.close();
	}
}
