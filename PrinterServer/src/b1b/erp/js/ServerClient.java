package b1b.erp.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

public class ServerClient {
	public static void main(String[] args) {

		ServerSocket server = null;
		try {
			server = new ServerSocket(55555);
			while (true) {
				try {
					Socket recSocket = server.accept();
					System.out.println("recevice a client");
					InputStream recIn = recSocket.getInputStream();
					BufferedReader read = new BufferedReader(new InputStreamReader(recIn, "UTF-8"));
					System.out.println("come from client:" + read.readLine());
					OutputStream outputStream = recSocket.getOutputStream();
					outputStream.write(("sendResponse:" + System.currentTimeMillis()).getBytes("UTF-8"));
					outputStream.close();
					recIn.close();
					recSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static void pdf2word() {
		File file = new File("D:\\dyj\\save\\15027053847681855.pdf");
		PDDocument pdf;
		try {
			pdf = PDDocument.load(file);
			int pagenumber = pdf.getNumberOfPages();
			System.out.println("pageCounts:" + pagenumber);
			FileOutputStream fos = new FileOutputStream("D:\\dyj\\save\\testPDF2Word.doc");
			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSortByPosition(true);// 排序
			stripper.setStartPage(1);// 设置转换的开始页
			// stripper.setEndPage(pagenumber-1);// 设置转换的结束页
			stripper.writeText(pdf, writer);
			writer.flush();
			writer.close();
			pdf.close();
		} catch (InvalidPasswordException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("export OK");
	}
}
