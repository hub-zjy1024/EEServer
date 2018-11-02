package com.zjy.print.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

public class CustomeRender extends PDFRenderer {

	public CustomeRender(PDDocument document) {
		super(document);
		// TODO Auto-generated constructor stub
	}

	public BufferedImage renderImage(int pageIndex, float scale, ImageType imageType)
			throws IOException {
		PDPage page = document.getPage(pageIndex);

		PDRectangle cropbBox = page.getCropBox();
		float widthPt = cropbBox.getWidth();
		float heightPt = cropbBox.getHeight();
		int widthPx = Math.round(widthPt * scale);
		int heightPx = Math.round(heightPt * scale);
		int rotationAngle = page.getRotation();

		// swap width and height
		BufferedImage image;
		if (rotationAngle == 90 || rotationAngle == 270) {
			image = new BufferedImage(heightPx, widthPx, BufferedImage.TYPE_INT_ARGB);
		} else {
			image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB);
		}

		// use a transparent background if the imageType supports alpha
		Graphics2D g = image.createGraphics();
		if (imageType == ImageType.ARGB) {
			g.setBackground(new Color(0, 0, 0, 0));
		} else {
			g.setBackground(Color.WHITE);
		}
		g.clearRect(0, 0, image.getWidth(), image.getHeight());

		transform(g, page, scale);

		// the end-user may provide a custom PageDrawer
		// the end-user may provide a custom PageDrawer
		Class<PageDrawerParameters> c = PageDrawerParameters.class;
		PageDrawerParameters parameters = null;
		try {
			Constructor<PageDrawerParameters> constructor = c.getConstructor(PDFRenderer.class,
					PDPage.class);
			constructor.setAccessible(true);
			parameters = (PageDrawerParameters) constructor.newInstance(this, page);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PageDrawer drawer = createPageDrawer(parameters);
		drawer.drawPage(g, page.getCropBox());
		g.dispose();
		return image;
	}

	/**
	 * Renders a given page to an AWT Graphics2D instance.
	 * 
	 * @param pageIndex
	 *            the zero-based index of the page to be converted
	 * @param graphics
	 *            the Graphics2D on which to draw the page
	 * @throws IOException
	 *             if the PDF cannot be read
	 */
	public void renderPageToGraphics(int pageIndex, Graphics2D graphics) throws IOException {
		renderPageToGraphics(pageIndex, graphics, 1);
	}

	/**
	 * Renders a given page to an AWT Graphics2D instance.
	 * 
	 * @param pageIndex
	 *            the zero-based index of the page to be converted
	 * @param graphics
	 *            the Graphics2D on which to draw the page
	 * @param scale
	 *            the scale to draw the page at
	 * @throws IOException
	 *             if the PDF cannot be read
	 */
	public BufferedImage renderPageToGraphicsC(int pageIndex, float scale) throws IOException {
		PDPage page = document.getPage(pageIndex);
		PDRectangle cropbBox = page.getCropBox();
		float widthPt = cropbBox.getWidth();
		float heightPt = cropbBox.getHeight();
		int widthPx = Math.round(widthPt * scale);
		int heightPx = Math.round(heightPt * scale);
		System.out.println("CustomeRender wh:"+ widthPx+"\t"+heightPx);
		BufferedImage image;
		image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setBackground(Color.white);
		graphics.clearRect(0, 0, widthPx, heightPx);
		transform(graphics, page, scale);
		Class<PageDrawerParameters> c = PageDrawerParameters.class;
		PageDrawerParameters parameters;
		try {
			Constructor<PageDrawerParameters> constructor = c.getDeclaredConstructor(PDFRenderer.class,
					PDPage.class);
			constructor.setAccessible(true);
			parameters = (PageDrawerParameters) constructor.newInstance(this, page);
			PageDrawer drawer = createPageDrawer(parameters);
			drawer.drawPage(graphics, cropbBox);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		graphics.dispose();
		return image;
	}

	// scale rotate translate
	private void transform(Graphics2D graphics, PDPage page, float scale) {
		graphics.scale(scale, scale);
		int rotationAngle = page.getRotation();
		PDRectangle cropBox = page.getCropBox();
		if (rotationAngle != 0) {
			float translateX = 0;
			float translateY = 0;
			switch (rotationAngle) {
			case 90:
				translateX = cropBox.getHeight();
				break;
			case 270:
				translateY = cropBox.getWidth();
				break;
			case 180:
				translateX = cropBox.getWidth();
				translateY = cropBox.getHeight();
				break;
			default:
				break;
			}
			graphics.translate(translateX, translateY);
			graphics.rotate((float) Math.toRadians(rotationAngle));
		}
	}
}
