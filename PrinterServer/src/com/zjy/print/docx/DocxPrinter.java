package com.zjy.print.docx;

import java.util.concurrent.ArrayBlockingQueue;

import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.office.PooledOfficeManager;
import com.zjy.print.docx.office.ProcessPoolOfficeManager;
import com.zjy.print.docx.task.PrintTask;
import com.zjy.print.docx.task.SaveDocTask;

public class DocxPrinter {
	private String printerName;
	private String docxPath;
	private PooledOfficeManager currentMgr = null;
	private String officePath;

	public DocxPrinter(String officeHome, String printerName, String docxPath) {
		super();
		this.printerName = printerName;
		this.docxPath = docxPath;
		this.officePath = officeHome;
		if (pManagers == null) {
			pManagers = (ProcessPoolOfficeManager) new OpenOficeConnectionManager(officeHome,
					arrayPorts).getOfficeManager();
			pManagers.start();
		}
	}

	public static void main(String[] args) {
		String docxPath = "d:/dyj/openoffice_print.docx";
		// String printname = "ZDesigner GK888d (EPL)";
		String name = "d:/dyj/test.docx";
		String printerName = "KY_Printer";
		String officeHome = "C:/Program Files (x86)/OpenOffice 4/";
		DocxPrinter printUtils = new DocxPrinter(officeHome, printerName, name);
		// printUtils.print();
		// name = "C:/DyjPrinter/KY/2018_27o__20772200149_7783.docx";
		String outFile = "d:/dyj/convertedPdf.pdf";
		printUtils.saveToPdf(name, outFile);
	}

	private static ArrayBlockingQueue<Integer> ports = new ArrayBlockingQueue<>(3);
	static {
		for (int i = 0; i < 3; i++) {
			ports.add(8100 + i);
		}
	}

	private static int[] arrayPorts = new int[1];
	static {
		for (int i = 0; i < arrayPorts.length; i++) {
			arrayPorts[i] = 8100 + i;
		}
	}
	private static ProcessPoolOfficeManager pManagers;

	/**
	 * 默认打印第一页,找不到打印机时会自动选择默认打印机
	 */
	public void print() throws OfficeException {
		PrintTask task = new PrintTask(docxPath, printerName);
		task.setHideWindow(true);
		pManagers.execute(task);
	}

	public void saveToPdf(String inFile, String outFile) throws OfficeException {
		SaveDocTask task = new SaveDocTask(inFile, outFile);
		pManagers.execute(task);
	}
}
