package b1b.erp.js;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.javafx.application.PlatformImpl.FinishListener;

public class UpDateNewVersion {
	public static void main(String[] args) {
		try {
			java.io.File file = new java.io.File("updateXml.txt");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			if (!file.exists()) {
				TransformerFactory transF = TransformerFactory.newInstance();
				Transformer tFormer = transF.newTransformer();
				tFormer.setOutputProperty("encoding", "UTF-8");
				Document xmlDoc = docBuilder.newDocument();
				Element root = xmlDoc.createElement("detail");
				xmlDoc.appendChild(root);
				tFormer.transform(new DOMSource(xmlDoc), new StreamResult(file));
			}
			FileInputStream fileIn = new FileInputStream(file);
			String savePath = "d:/updateXml.txt";
			Scanner in = new Scanner(System.in);
			createNewUpdateXml(in, fileIn, savePath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static void createNewUpdateXml(Scanner in, FileInputStream fileIn,
			String savePath) throws SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException, ParserConfigurationException {
		// 解析的文件必须含有根节点
		DocumentBuilderFactory mdocFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder mdocBuilder = mdocFactory.newDocumentBuilder();
		Document xmlDoc = mdocBuilder.parse(fileIn);
		NodeList lastTag = xmlDoc.getElementsByTagName("latest-version");
		NodeList detail = xmlDoc.getElementsByTagName("detail");
		boolean hasRoot = false;
		if (detail != null) {
			System.out.println("root:" + detail.getLength());
			if (detail.getLength() > 0) {
				hasRoot = true;
			}
		}
		int lastCode = 0;
		if (lastTag != null) {
			Element item = (Element) lastTag.item(0);
			if (item != null) {
				lastCode = Integer.parseInt(item.getElementsByTagName("code").item(0).getTextContent());
				System.out.println("上一个版本号：" + lastCode);
			}
		}
		Element latestElement = xmlDoc.createElement("latest-version");
		Element codeElement = xmlDoc.createElement("code");
		Element contentElement = xmlDoc.createElement("content");
		Element dateElement = xmlDoc.createElement("date");
		String code = String.valueOf(lastCode + 1);
		codeElement.setTextContent(code);
		System.out.println("请输入更新内容：");
		String content = in.nextLine();
		contentElement.setTextContent(content);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		dateElement.setTextContent(date);
		latestElement.appendChild(codeElement);
		latestElement.appendChild(contentElement);
		latestElement.appendChild(dateElement);
		Element root;
		if (hasRoot) {
			root = (Element) detail.item(0);
		} else {
			root = xmlDoc.createElement("detail");
			xmlDoc.appendChild(root);
		}
		if (lastCode != 0) {
			Node old = lastTag.item(0);
			root.insertBefore((Node) latestElement, old);
			xmlDoc.renameNode(old, null, "old-version");
		} else {
			root.appendChild(latestElement);
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("encoding", "UTF-8");
		File file = new File(savePath);
		transformer.transform(new DOMSource(xmlDoc), new StreamResult(file));
		System.out.println("更新完成");
	}

}
