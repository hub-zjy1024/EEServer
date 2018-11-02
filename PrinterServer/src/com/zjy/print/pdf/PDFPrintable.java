package com.zjy.print.pdf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.sun.media.jfxmedia.Media;

import javafx.scene.control.ButtonBar.ButtonData;

public class PDFPrintable implements Printable {

	private final PDDocument document;
	private final PDFRenderer renderer;
	private final boolean showPageBorder;
	private final Scaling scaling;
	private final float dpi;
	private final boolean center;
	private String name;
	public PDFPrintable(PDDocument document) {
		this(document, Scaling.SHRINK_TO_FIT);
	}

	public PDFPrintable(PDDocument document, Scaling scaling) {
		this(document, scaling, false, 0.0F);
	}

	public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder) {
		this(document, scaling, showPageBorder, 0.0F);
	}

	public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder, float dpi) {
		this(document, scaling, showPageBorder, dpi, true);
	}
	public PDFPrintable(File path,PDDocument document, Scaling scaling, boolean showPageBorder, float dpi) {
		this(document, scaling, showPageBorder, dpi, true);
		name=path.getName();
	}

	public PDFPrintable(PDDocument document, Scaling scaling, boolean showPageBorder, float dpi,
			boolean center) {
		this.document = document;
		this.renderer = new PDFRenderer(document);
		this.scaling = scaling;
		this.showPageBorder = showPageBorder;
		this.dpi = dpi;
		this.center = center;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		System.out.println("pageFormate:"+pageFormat.getWidth()+"x"+pageFormat.getHeight());
		if ((pageIndex < 0) || (pageIndex >= this.document.getNumberOfPages())) {
			return NO_SUCH_PAGE;
		}
		try {
			Graphics2D graphics2D = (Graphics2D) graphics;
			PDPage page = this.document.getPage(pageIndex);
			PDRectangle cropBox = getRotatedCropBox(page);
			double imageableWidth = pageFormat.getImageableWidth();
			double imageableHeight = pageFormat.getImageableHeight();
			double scale = 1.0D;
			if (this.scaling != Scaling.ACTUAL_SIZE) {
				double scaleX = imageableWidth / cropBox.getWidth();
				double scaleY = imageableHeight / cropBox.getHeight();
				scale = Math.min(scaleX, scaleY);
				if ((scale > 1.0D) && (this.scaling == Scaling.SHRINK_TO_FIT)) {
					scale = 1.0D;
				}
				if ((scale < 1.0D) && (this.scaling == Scaling.STRETCH_TO_FIT)) {
					scale = 1.0D;
				}
			}
			float dpiScale = this.dpi / 72.0F;
			graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			double border=2.0d;
			if (this.center) {
				graphics2D.translate((imageableWidth - cropBox.getWidth() * scale) /border,
						(imageableHeight - cropBox.getHeight() * scale) / border);
			}
			Graphics2D printerGraphics = null;
			BufferedImage image = null;
			if (this.dpi > 0.0F) {
				image = new BufferedImage((int) (imageableWidth * dpiScale / scale),
						(int) (imageableHeight * dpiScale / scale),BufferedImage.TYPE_INT_ARGB);
				System.out.println("dpiScal-scale:" + dpiScale+"-"+scale);
				printerGraphics = graphics2D;
				graphics2D = image.createGraphics();
//				image.setRGB(x, y, rgb);
				printerGraphics.scale(scale / dpiScale, scale / dpiScale);
				scale = dpiScale;
				AffineTransform transform = (AffineTransform) graphics2D.getTransform().clone();
				graphics2D.setBackground(Color.WHITE);
				System.out.println("pages:"+document.getNumberOfPages());
				renderer.renderPageToGraphics(pageIndex, graphics2D, (float) scale);
				if (this.showPageBorder) {
					graphics2D.setTransform(transform);
					graphics2D.setClip(0, 0, (int) imageableWidth, (int) imageableHeight);
					graphics2D.scale(scale, scale);
					graphics2D.setColor(Color.GRAY);
					graphics2D.setStroke(new BasicStroke(0.5F));
					graphics2D.drawRect(0, 0, (int) cropBox.getWidth(), (int) cropBox.getHeight());
				}
				printerGraphics.setBackground(Color.WHITE);
				printerGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());
				printerGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				printerGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				printerGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				printerGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				printerGraphics.drawImage(image, 0, 0, null);
				printerGraphics.dispose();
				graphics2D.dispose();
			}
			System.out.println("graphics2D:w-h:" + image.getWidth() + "-" + image.getHeight());
			return PAGE_EXISTS;
		} catch (IOException e) {
			throw new PrinterIOException(e);
		}
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

	static PDRectangle getRotatedMediaBox(PDPage page) {
		PDRectangle mediaBox = page.getMediaBox();
		int rotationAngle = page.getRotation();
		if ((rotationAngle == 90) || (rotationAngle == 270)) {
			return new PDRectangle(mediaBox.getLowerLeftY(), mediaBox.getLowerLeftX(),
					mediaBox.getHeight(), mediaBox.getWidth());
		}
		return mediaBox;
	}
}
