package com.zjy.print.docx.test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.zjy.print.docx.util.CustomXWPFDocument;
import com.zjy.print.docx.util.DocxManager;

public class DocxEditor {
	private String officeHome;

	/**
	 * 根据指定的参数值、模板，生成 word 文档
	 * 
	 * @param param
	 *            需要替换的变量
	 * @param template
	 *            模板
	 */
	public CustomXWPFDocument generateWord(Map<String, Object> param, String template) {
		CustomXWPFDocument doc = null;
		try {
			OPCPackage pack = POIXMLDocument.openPackage(template);
			doc = new CustomXWPFDocument(pack);
			if (param != null && param.size() > 0) {
				// 处理段落
				List<XWPFParagraph> paragraphList = doc.getParagraphs();
				processParagraphs(paragraphList, param, doc);
				// 处理表格
				Iterator<XWPFTable> it = doc.getTablesIterator();
				while (it.hasNext()) {
					XWPFTable table = it.next();
					List<XWPFTableRow> rows = table.getRows();
					for (XWPFTableRow row : rows) {
						List<XWPFTableCell> cells = row.getTableCells();
						for (XWPFTableCell cell : cells) {
							List<XWPFParagraph> paragraphListTable = cell.getParagraphs();
							processParagraphs(paragraphListTable, param, doc);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 处理段落
	 * 
	 * @param paragraphList
	 */
	public void processParagraphs(List<XWPFParagraph> paragraphList, Map<String, Object> param,
			CustomXWPFDocument doc) {
		if (paragraphList != null && paragraphList.size() > 0) {
			for (int i = 0; i < paragraphList.size(); i++) {
				XWPFParagraph paragraph = paragraphList.get(i);
				List<XWPFRun> runs = paragraph.getRuns();
				StringBuilder b = new StringBuilder();
				for (int j = 0; j < runs.size(); j++) {
					XWPFRun run = runs.get(j);
					String text = run.getText(0);
					// run.get
					b.append(text);
					Set<Entry<String, Object>> entrySet = param.entrySet();
					for (Map.Entry<String, Object> en : entrySet) {
						Entry<String, Object> entry = en;
						String key = entry.getKey();
						if (text != null && text.indexOf(key) != -1) {
							Object value = entry.getValue();
							if (value instanceof String) {// 文本替换
								text = text.replace(key, value.toString());
								run.setText(text, 0);
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


	public void replaceTemplate(Map<String, Object> map, String templatePath, String outPath) {
		try {
			CustomXWPFDocument doc = DocxManager.generateWord(map, templatePath);
			FileOutputStream fopts = new FileOutputStream(outPath);
			doc.write(fopts);
			fopts.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
