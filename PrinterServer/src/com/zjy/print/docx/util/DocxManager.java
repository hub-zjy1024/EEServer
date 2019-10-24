package com.zjy.print.docx.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule.Enum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocxManager {
	static Logger mLogger = LoggerFactory.getLogger(DocxManager.class);

	/**
	 * 根据指定的参数值、模板，生成 word 文档
	 * 
	 * @param param
	 *            需要替换的变量
	 * @param template
	 *            模板
	 */
	public static CustomXWPFDocument generateWord(Map<String, Object> param, String template) {
		CustomXWPFDocument doc = null;
		try {
			OPCPackage pack = POIXMLDocument.openPackage(template);
			doc = new CustomXWPFDocument(pack);
			if (param != null && param.size() > 0) {
				// 处理段落
				List<XWPFParagraph> paragraphList = doc.getParagraphs();
				processParagraphs(paragraphList, param, doc, null);
				// 处理表格
				Iterator<XWPFTable> it = doc.getTablesIterator();
				while (it.hasNext()) {
					XWPFTable table = it.next();
					List<XWPFTableRow> rows = table.getRows();
					for (XWPFTableRow row : rows) {
						List<XWPFTableCell> cells = row.getTableCells();
						for (XWPFTableCell cell : cells) {
							List<XWPFParagraph> paragraphListTable = cell.getParagraphs();
							processParagraphs(paragraphListTable, param, doc, cell);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static CustomXWPFDocument generateWord(Map<String, Object> param, String template,
			String outPath) {
		CustomXWPFDocument doc = null;
		try {
			OPCPackage pack = POIXMLDocument.openPackage(template);
			doc = new CustomXWPFDocument(pack);
			if (param != null && param.size() > 0) {
				// 处理段落
				List<XWPFParagraph> paragraphList = doc.getParagraphs();
				processParagraphs(paragraphList, param, doc, null);
				// 处理表格
				Iterator<XWPFTable> it = doc.getTablesIterator();
				while (it.hasNext()) {
					XWPFTable table = it.next();
					List<XWPFTableRow> rows = table.getRows();
					for (XWPFTableRow row : rows) {
						List<XWPFTableCell> cells = row.getTableCells();
						for (XWPFTableCell cell : cells) {
							List<XWPFParagraph> paragraphListTable = cell.getParagraphs();
							processParagraphs(paragraphListTable, param, doc, cell);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream fileout = new FileOutputStream(outPath);
			doc.write(fileout);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 处理段落
	 * 
	 * @param paragraphList
	 */
	public static void processParagraphs(List<XWPFParagraph> paragraphList,
			Map<String, Object> param, CustomXWPFDocument doc, XWPFTableCell cell) {
		if (paragraphList != null && paragraphList.size() > 0) {
			for (int i = 0; i < paragraphList.size(); i++) {
				XWPFParagraph paragraph = paragraphList.get(i);
				List<XWPFRun> runs = paragraph.getRuns();
				for (int j = 0; j < runs.size(); j++) {
					XWPFRun run = runs.get(j);
					String text = run.getText(0);
					Set<Entry<String, Object>> entrySet = param.entrySet();
					for (Map.Entry<String, Object> en : entrySet) {
						Entry<String, Object> entry = en;
						String key = entry.getKey();
						if (text != null && text.indexOf(key) != -1) {
							Object value = entry.getValue();
							if (value instanceof String) {// 文本替换
								String str = value.toString();
								str = text.replace(key, str);
								run.setText(str, 0);
							} else if (value instanceof Map) {// 图片替换
								text = text.replace(key, "");
								run.setText(text, 0);
								Map<String, Object> pic = (Map<String, Object>) value;
								int width = Integer.parseInt(pic.get("width").toString());
								int height = Integer.parseInt(pic.get("height").toString());
								int picType = getPictureType(pic.get("type").toString());
								byte[] byteArray = (byte[]) pic.get("content");
								ByteArrayInputStream byteInputStream = new ByteArrayInputStream(
										byteArray);
								try {
									String ind = doc.addPictureData(byteInputStream, picType);
									doc.createPicture(ind, width, height, paragraph);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public static CTPPr getParagraphCTPPr(XWPFParagraph p) {
		CTPPr pPPr = null;
		if (p.getCTP() != null) {
			pPPr = p.getCTP().getPPr();
			if (pPPr == null) {
				pPPr = p.getCTP().addNewPPr();
			}
		}
		return pPPr;
	}

	public static void addP(String srcText, String key, String str, XWPFRun run,
			XWPFParagraph paragraph, CustomXWPFDocument doc, XWPFTableCell cell) {
		int indexOf = str.indexOf("\n");
		int tempIndex = 0;
		if (indexOf != -1) {
			run.setText(srcText.replace(key, str.substring(tempIndex, indexOf)), 0);
			for (; true;) {
				tempIndex = indexOf;
				indexOf = str.indexOf("\n", tempIndex + 1);
				System.out.println("index:" + indexOf);
				ParagraphAlignment alignment = paragraph.getAlignment();
				String styleID = paragraph.getStyleID();
				String fontFamily = run.getFontFamily();
				int fontSize = run.getFontSize();
				CTPPr pPPr = getParagraphCTPPr(paragraph);
				CTSpacing pSpacing = pPPr.getSpacing();
				if (pSpacing == null) {
					System.out.println("origin CTSpacing null");
					pSpacing = pPPr.addNewSpacing();
				}
				BigInteger line = pSpacing.getLine();
				Enum lineRule = pSpacing.getLineRule();
				XWPFParagraph createParagraph;
				if (indexOf == -1) {
					String pText = str.substring(tempIndex + 1);
					if (cell != null) {
						createParagraph = cell.addParagraph();
					} else {
						createParagraph = doc.createParagraph();
					}
					pPPr = getParagraphCTPPr(createParagraph);
					pSpacing = pPPr.getSpacing() != null ? pPPr.getSpacing() : pPPr.addNewSpacing();
					pSpacing.setLine(line);
					pSpacing.setLineRule(lineRule);
					createParagraph.setStyle(styleID);
					createParagraph.setAlignment(alignment);
					XWPFRun newRun = createParagraph.createRun();
					newRun.setFontFamily(fontFamily);
					newRun.setFontSize(fontSize);
					newRun.setText(pText, 0);
					break;
				}
				String pText = str.substring(tempIndex, indexOf);
				pText = srcText.replace(key, pText);
				if (cell != null) {
					createParagraph = cell.addParagraph();
				} else {
					createParagraph = doc.createParagraph();
				}
				pPPr = getParagraphCTPPr(createParagraph);
				CTSpacing nSpacing = pPPr.getSpacing();
				if (nSpacing == null) {
					System.out.println("new CTSpacing null");
					nSpacing = pPPr.addNewSpacing();
				}
				nSpacing.setLine(line);
				nSpacing.setLineRule(lineRule);
				createParagraph.setStyle(styleID);
				createParagraph.setAlignment(alignment);
				XWPFRun newRun = createParagraph.createRun();
				newRun.setFontFamily(fontFamily);
				newRun.setFontSize(fontSize);
				newRun.setText(pText, 0);
			}
		} else {
			srcText = srcText.replace(key, str);
			run.setText(srcText, 0);
		}
	}

	/**
	 * 根据图片类型，取得对应的图片类型代码
	 * 
	 * @param picType
	 * @return int
	 */
	private static int getPictureType(String picType) {
		int res = CustomXWPFDocument.PICTURE_TYPE_PICT;
		if (picType != null) {
			if (picType.equalsIgnoreCase("png")) {
				res = CustomXWPFDocument.PICTURE_TYPE_PNG;
			} else if (picType.equalsIgnoreCase("dib")) {
				res = CustomXWPFDocument.PICTURE_TYPE_DIB;
			} else if (picType.equalsIgnoreCase("emf")) {
				res = CustomXWPFDocument.PICTURE_TYPE_EMF;
			} else if (picType.equalsIgnoreCase("jpg") || picType.equalsIgnoreCase("jpeg")) {
				res = CustomXWPFDocument.PICTURE_TYPE_JPEG;
			} else if (picType.equalsIgnoreCase("wmf")) {
				res = CustomXWPFDocument.PICTURE_TYPE_WMF;
			}
		}
		return res;
	}

	/**
	 * 将输入流中的数据写入字节数组
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] inputStream2ByteArray(InputStream in, boolean isClose) {
		byte[] byteArray = null;
		try {
			int total = in.available();
			byteArray = new byte[total];
			in.read(byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isClose) {
				try {
					in.close();
				} catch (Exception e2) {
					System.out.println("关闭流失败:" + e2.getMessage());
				}
			}
		}
		return byteArray;
	}

	public static void replaceTemplate(Map<String, Object> map, String templatePath,
			String outPath) {
		try {
			FileInputStream in = new FileInputStream(templatePath);
			FileOutputStream fio = new FileOutputStream(outPath);
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = in.read(buf)) != -1) {
				fio.write(buf, 0, len);
			}
			in.close();
			fio.close();
			replaceTemplate(map, outPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void replaceTemplate(Map<String, Object> map, String templatePath) {
		long time0 = System.currentTimeMillis();
		try {
			CustomXWPFDocument doc = DocxManager.generateWord(map, templatePath);
			String tempFile = templatePath + ".temp.docx";
			FileOutputStream os = new FileOutputStream(templatePath + ".temp.docx");
			doc.write(os);
			os.close();
			doc.close();
			File file = new File(tempFile);
			file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long time2 = System.currentTimeMillis();
		long exTime = time2 - time0;
//		mLogger.warn("replaceTime=" + exTime);
	}
}
