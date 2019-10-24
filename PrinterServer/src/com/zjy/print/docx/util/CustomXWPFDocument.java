package com.zjy.print.docx.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectFrameLocking;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

/**
 * 自定义 XWPFDocument，并重写 createPicture()方法
 */
public class CustomXWPFDocument extends XWPFDocument {
	public CustomXWPFDocument(InputStream in) throws IOException {
		super(in);
	}

	public CustomXWPFDocument() {
		super();
	}

	public CustomXWPFDocument(OPCPackage pkg) throws IOException {
		super(pkg);
	}

	int picCount=0;
	/**
	 * @param id
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @param paragraph
	 *            段落
	 */
	public void createPicture(String id, int width, int height, XWPFParagraph paragraph) {
		final int EMU = 9525;
		width *= EMU;
		height *= EMU;
		Long checksum = getPictureDataByID(id).getChecksum();
		int index =picCount;
		CTInline inline = paragraph.createRun().getCTR().addNewDrawing().addNewInline();
		CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr = inline.addNewCNvGraphicFramePr();
		CTGraphicalObjectFrameLocking addNewGraphicFrameLocks = addNewCNvGraphicFramePr.addNewGraphicFrameLocks();
		addNewGraphicFrameLocks.setNoChangeAspect(true);
//		String cnvgraphicFramPr="<wp:cNvGraphicFramePr><a:graphicFrameLocks noChangeAspect=\"1\" "
//				+ "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"/>"
//				+ "</wp:cNvGraphicFramePr>";
//		XmlToken nvGraphicToken = null;
//		try {
//			nvGraphicToken = XmlToken.Factory.parse(cnvgraphicFramPr);
//		} catch (XmlException xe) {
//			xe.printStackTrace();
//		}
//		inline.set(nvGraphicToken);
		String picXml = "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
				+ "<a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "<pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "<pic:nvPicPr><pic:cNvPr id=\"" + index
				+ "\" name=\"Generated\"/><pic:cNvPicPr><a:picLocks noChangeAspect=\"1\" noChangeArrowheads=\"1\"/>"
				+ "</pic:cNvPicPr></pic:nvPicPr><pic:blipFill>" + "<a:blip r:embed=\"" + id
				+ "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
				+ "<a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr bwMode=\"auto\"><a:xfrm>"
				+ "<a:off x=\"0\" y=\"0\"/><a:ext cx=\"" + width + "\" cy=\"" + height
				+ "\"/></a:xfrm>" + "<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom>"
				+ "<a:noFill/><a:ln w=\"9525\"><a:noFill/><a:miter lim=\"800000\"/><a:headEnd/><a:tailEnd/>"
				+ "</a:ln></pic:spPr></pic:pic></a:graphicData></a:graphic>";
		XmlToken xmlToken = null;
		try {
			xmlToken = XmlToken.Factory.parse(picXml);
		} catch (XmlException xe) {
			xe.printStackTrace();
		}
		inline.set(xmlToken);
		inline.setDistT(0);
		inline.setDistB(0);
		inline.setDistL(0);
		inline.setDistR(0);
		CTPositiveSize2D extent = inline.addNewExtent();
		extent.setCx(width);
		extent.setCy(height);
		CTNonVisualDrawingProps docPr = inline.addNewDocPr();
		docPr.setId(index);
		docPr.setName("");
		docPr.setDescr("测试");
		picCount++;
	}

	private int getPictureType(String imageType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void createPicture(String blipId, int id, int width, int height, XWPFParagraph paragraph,
			String picAttach) {
		final int EMU = 9525;
		width *= EMU;
		height *= EMU;
		CTInline inline = paragraph.createRun().getCTR().addNewDrawing().addNewInline();
		paragraph.createRun().setText(picAttach);

		String picXml = ""
				+ "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
				+ "<a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "<pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "<pic:nvPicPr><pic:cNvPr id=\"" + id + "\" name=\"Generated\"/><pic:cNvPicPr/>"
				+ "</pic:nvPicPr><pic:blipFill>" + "<a:blip r:embed=\"" + blipId
				+ "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
				+ "<a:stretch><a:fillRect/>" + "</a:stretch></pic:blipFill><pic:spPr>"
				+ "<a:xfrm><a:off x=\"0\" y=\"0\"/>" + "<a:ext cx=\"" + width + "\" cy=\"" + height
				+ "\"/>" + "</a:xfrm><a:prstGeom prst=\"rect\">"
				+ "<a:avLst/></a:prstGeom></pic:spPr>" + "</pic:pic></a:graphicData></a:graphic>";

		// CTGraphicalObjectData graphicData =
		// inline.addNewGraphic().addNewGraphicData();
		XmlToken xmlToken = null;
		try {
			xmlToken = XmlToken.Factory.parse(picXml);
		} catch (XmlException xe) {
			xe.printStackTrace();
		}
		inline.set(xmlToken);
		// graphicData.set(xmlToken);
		inline.setDistT(0);
		inline.setDistB(0);
		inline.setDistL(0);
		inline.setDistR(0);
		CTPositiveSize2D extent = inline.addNewExtent();
		extent.setCx(width);
		extent.setCy(height);
		CTNonVisualDrawingProps docPr = inline.addNewDocPr();
		docPr.setId(id);
		docPr.setName("Picture " + id);
		docPr.setDescr("Generated");
	}

	/**
	 * * 在 XWPFRun 中添加图片
	 * 
	 * @param picId
	 *            图片 id * @param width 宽
	 * @param height
	 *            高
	 * @param paragraph
	 *            段落
	 */
	public void createPicture(String picId, int width, int height, String imageType,
			XWPFRun paraRun) {
		final int EMU = 9525;
		width *= EMU;
		height *= EMU;
		String blipId = "";
		Long checksum = getPictureDataByID(picId).getChecksum();
		// document.getPictureDataByID(picId).getPackageRelationship() .getId();
		CTInline inline = paraRun.getCTR().addNewDrawing().addNewInline();
		String picXml = "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/dr"
				+ "awingml/2006/main\">" + "<a:graphicData uri=\"http://"
				+ "schemas.openxmlformats.org/drawingml/2006/pictu" + "re\">"
				+ "<pic:pic xmlns:pic=\"http://schemas"
				+ ".openxmlformats.org/drawingml/2006/picture\">" + "<pic:blipFill>"
				+ "<a:blip r:embed=\"" + blipId + "\" "
				+ "xmlns:r=\"http://schemas.openxmlformats.org/of"
				+ "ficeDocument/2006/relationships\" />" + "<a:stretch>" + "<a:fillRect />"
				+ "</a:stretch>" + "</pic:blipFill>" + "<pic:spPr>" + "<a:xfrm>"
				+ "<a:off x=\"0\" y=\"0\" />" + "<a:ext cx=\"" + width + "\" cy=\"" + height
				+ "\" />" + "</a:xfrm>" + "<a:prstGeom prst=\"rect\">" + "<a:avLst />"
				+ "</a:prstGeom>" + "</pic:spPr>" + "</pic:pic>" + "</a:graphicData>"
				+ "</a:graphic>";
		inline.addNewGraphic().addNewGraphicData();
		XmlToken xmlToken = null;
		try {
			xmlToken = XmlToken.Factory.parse(picXml);
		} catch (XmlException xe) {
			xe.printStackTrace();
		}
		inline.set(xmlToken);
		inline.setDistT(0);
		inline.setDistB(0);
		inline.setDistL(0);
		inline.setDistR(0);
		CTPositiveSize2D extent = inline.addNewExtent();
		extent.setCx(width);
		extent.setCy(height);
		CTNonVisualDrawingProps docPr = inline.addNewDocPr();
		docPr.setDescr(" 替换图片 ");
	}
}