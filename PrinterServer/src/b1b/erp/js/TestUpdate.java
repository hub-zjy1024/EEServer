package b1b.erp.js;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestUpdate {
	public static void main(String[] args) {
		String path = "d:/updateXml.txt";
		updateXml(0, path);
	}

	public static void updateXml(int mode, String path) {
		String url = "http://172.16.6.160:8006/DownLoad/updateXml.txt";
		String savePath = "d:/dyj/updateXml.txt";
		// getUpdateInfo(url);
		switch (mode) {
		case 0:
			// kf
			System.out.println("version:kf");
			url = "http://172.16.6.160:8006/DownLoad/dyj_kf/updateXml.txt";
			savePath = "d:/Zjys/dyj_app/dyj_kf/updateXml.txt";
			break;
		case 1:
			System.out.println("version:mgr");
			// mgr
			url = "http://172.16.6.160:8006/DownLoad/dyj_mgr/updateXml.txt";
			savePath = "d:/Zjys/dyj_app/dyj_mgr/updateXml.txt";
			break;
		case 2:
			// market
			System.out.println("version:market");
			url = "http://172.16.6.160:8006/DownLoad/dyj_market/updateXml.txt";
			savePath = "d:/Zjys/dyj_app/dyj_market/updateXml.txt";
			break;
		default:
			break;
		}
		File file = new File(savePath);
		File pFile = file.getParentFile();
		if (!pFile.exists()) {
			pFile.mkdirs();
		}
		Scanner in = new Scanner(System.in);
		String result = getUpdateXml(url);
		getUpdateInfo(result);
		FileInputStream fis = null;
		if (path != null) {
			try {
				fis = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		createNewXml(result, in, savePath, fis);

	}

	private static HashMap<String, String> getUpdateInfo(String xml) {
		HashMap<String, String> result = new HashMap<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			ByteArrayInputStream bin = new ByteArrayInputStream(xml.getBytes("utf-8"));
			Document xmlDoc = docBuilder.parse(bin);
			// readXmlSax(new ByteArrayInputStream(res.getBytes("utf-8")));
			NodeList newVersion = xmlDoc.getElementsByTagName("latest-version");
			Node item = newVersion.item(0);
			NodeList childNodes = item.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node n = childNodes.item(i);
				String nName = n.getNodeName();
				System.out.println(n.getTextContent());
				if (nName.equals("code")) {
					result.put("code", n.getTextContent());
				} else if (nName.equals("content")) {
					result.put("content", n.getTextContent());
				} else if (nName.equals("date")) {
					result.put("date", n.getTextContent());
				}
			}
			return result;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUpdateXml(String url) {
		URL urll;
		try {
			urll = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(10000);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String len = reader.readLine();
				StringBuilder stringBuilder = new StringBuilder();
				while (len != null) {
					stringBuilder.append(len);
					len = reader.readLine();
				}
				String res = stringBuilder.toString();
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createNewXml(String sourXml, Scanner in, String savePath, InputStream log) {
		if (sourXml != null) {
			try {
				ByteArrayInputStream fileIn = new ByteArrayInputStream(sourXml.getBytes("utf-8"));
				createNewUpdateXml(in, fileIn, savePath, log);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void createNewUpdateXml(Scanner in, InputStream fileIn, String savePath, InputStream log)
			throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerConfigurationException,
			TransformerException, ParserConfigurationException {
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
		String content = "";
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		if (log != null) {
			byte[] buf = new byte[1024];
			int len;
			while ((len = log.read(buf)) != -1) {
				bao.write(buf,0,len);
			}
			content += new String(bao.toByteArray(), "utf-8");
		} else {
			content = in.nextLine();
		}
		System.out.println("更新内容如下：" + content);
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

	public static void readXmlSax(InputStream in) {
		SAXParserFactory saxF = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxF.newSAXParser();
			saxParser.parse(in, new MySaxHandler());
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static class MySaxHandler extends DefaultHandler {
		String preTag = null;
		int i = 0;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);
			if ("code".equals(qName)) {
				preTag = qName;
				i++;
			}
			// 将正在解析的节点名称赋给preTag
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();

		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			if ("code".equals(qName)) {
				preTag = null;
			}

		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			// TODO Auto-generated method stub
			System.out.println("characters");
			super.characters(ch, start, length);
			if (preTag != null) {
				String content = new String(ch, start, length);
				if ("code".equals(preTag)) {
					if (i == 1) {
						System.out.println("sax:" + content);
						throw new SAXException("finsih");
					}

				}
			}
		}

	}
}
