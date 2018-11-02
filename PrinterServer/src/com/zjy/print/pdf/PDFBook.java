package com.zjy.print.pdf;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.printing.Scaling;

public class PDFBook extends Book {

	private final PDDocument document;
	private boolean showPageBorder = false;
	private final float dpi;
	private String pdfPath;
	private String printName;

	public String getPrintName() {
		return printName;
	}

	public PDFBook(String printName, String pdfPath, float dpi, Orientation orientation,
			boolean showPageBorder) throws InvalidPasswordException, IOException {
		this.document = PDDocument.load(new File(pdfPath));
		this.showPageBorder = showPageBorder;
		this.dpi = dpi;
		this.pdfPath = pdfPath;
		this.printName = printName;
		this.orientation = orientation;
	}

	private Orientation orientation = Orientation.PORTRAIT;

	public PDDocument getDocument() {
		return document;
	}

	public enum Orientation {
		LANDSCAPE, PORTRAIT, AUTO
	}

	public PDFBook(String pdfPath, float dpi) throws InvalidPasswordException, IOException {
		this(pdfPath, dpi, Orientation.PORTRAIT, false);
	}

	public PDFBook(String printName, String pdfPath, float dpi)
			throws InvalidPasswordException, IOException {
		this(printName, pdfPath, dpi, Orientation.PORTRAIT, false);
	}

	public PDFBook(String pdfPath) throws InvalidPasswordException, IOException {
		this(pdfPath, 0f, Orientation.PORTRAIT, false);
	}

	public PDFBook(String pdfPath, float dpi, Orientation orientation, boolean showPageBorder)
			throws InvalidPasswordException, IOException {
		this("", pdfPath, dpi, orientation, showPageBorder);
	}

	public int getNumberOfPages() {
		return this.document.getNumberOfPages();
	}

	public PageFormat getPageFormat(int pageIndex) {
		PDPage page = this.document.getPage(pageIndex);
		PDRectangle mediaBox = getRotatedMediaBox(page);
		PDRectangle cropBox = getRotatedCropBox(page);
		printResult("cropBoxSize:xy", cropBox.getWidth(), cropBox.getHeight());
		boolean isLandscape;
		Paper paper = new Paper();
		if (mediaBox.getWidth() > mediaBox.getHeight()) {
			paper.setSize(mediaBox.getHeight(), mediaBox.getWidth());
			paper.setImageableArea(cropBox.getLowerLeftY(), cropBox.getLowerLeftX(),
					cropBox.getHeight(), cropBox.getWidth());
			isLandscape = true;
		} else {
			paper.setSize(mediaBox.getWidth(), mediaBox.getHeight());
			paper.setImageableArea(cropBox.getLowerLeftX(), cropBox.getLowerLeftY(),
					cropBox.getWidth(), cropBox.getHeight());
			isLandscape = false;
		}
		PageFormat format = new PageFormat();
		format.setPaper(paper);
		if (orientation == Orientation.AUTO) {
			if (isLandscape) {
				format.setOrientation(0);
			} else {
				format.setOrientation(1);
			}
		} else {
			format.setOrientation(orientation.ordinal());
		}
		return format;
	}

	public Printable getPrintable(int i) {
		if (i >= getNumberOfPages()) {
			throw new IndexOutOfBoundsException(i + " >= " + getNumberOfPages());
		}
//		return new PDFPrintable(this.document, Scaling.ACTUAL_SIZE, this.showPageBorder, this.dpi);
		return new PDFPrintable(new File(pdfPath),this.document, Scaling.ACTUAL_SIZE, this.showPageBorder, this.dpi);
	}

	static PDRectangle getRotatedCropBox(PDPage page) {
		PDRectangle cropBox = page.getCropBox();
		int rotationAngle = page.getRotation();
		if ((rotationAngle == 90) || (rotationAngle == 270)) {
			return new PDRectangle(cropBox.getLowerLeftY(), cropBox.getLowerLeftX(),
					cropBox.getHeight(), cropBox.getWidth());
		}
		return cropBox;
	}

	public static void printResult(String tag, Object... obj) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < obj.length; i++) {
			sb.append("param" + i + ":" + obj[i].toString() + "===");
		}
		System.out.println(tag + ":" + sb.toString());
	}

	static PDRectangle getRotatedMediaBox(PDPage page) {
		PDRectangle mediaBox = page.getMediaBox();
		int rotationAngle = page.getRotation();
		if ((rotationAngle == 90) || (rotationAngle == 270)) {
			return new PDRectangle(mediaBox.getLowerLeftY(), mediaBox.getLowerLeftX(),
					mediaBox.getHeight(), mediaBox.getWidth());
		}
		return mediaBox;
	}

	public String getPdfPath() {
		return pdfPath;
	}
}
