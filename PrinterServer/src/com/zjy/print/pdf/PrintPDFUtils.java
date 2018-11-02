package com.zjy.print.pdf;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.PrintService;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PrintPDFUtils {

	public static void printPDF(PDFBook myBook) {
		String pdfFile = myBook.getPdfPath();
		String printerName = myBook.getPrintName();
		PDDocument document = null;
		try {
			document = myBook.getDocument();
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setJobName(new File(pdfFile).getName());
			if (printerName != null) {
				PrintService[] printService = PrinterJob.lookupPrintServices();
				boolean printerFound = false;
				for (int i = 0; (!printerFound) && (i < printService.length); i++) {
					if (printService[i].getName().contains(printerName)) {
						printJob.setPrintService(printService[i]);
						printerFound = true;
					}
				}
			}
			printJob.setPageable(myBook);
			printJob.print();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
