package b1b.erp.js.bussiness;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZxingBarCreator {
	public static void main(String[] args) {
		//		String content = "http://172.16.6.160:8006/DownLoad/dyj_fp/dyjapp.apk";
		String content = "http://210.51.190.36:7500/DownLoad/dyj_nahuo/dyj_nahuo_2.3_9_.apk";
		String savepath = "D:/dyj/zxingQr.png";
		int size = 300;
		int margin = size / 30;
		createQRImage(savepath, content, size, margin);
	}

	public static BufferedImage createQRImage(String content, int pxSize, int margin)
			throws Exception {
		try {
			if (content == null || "".equals(content)) {
				throw new Exception("当前输入二维码内容为空");
			}
			// 配置参数
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, pxSize,
					pxSize, hints);
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			// 去白边
			int blankLine = 0;
			int realWidth = bitMatrix.getWidth();
			int realHeight = bitMatrix.getHeight();
			int[] pixels = new int[realWidth * realHeight];
			for (int y = 0; y < realHeight; y++) {
				int count = 0;
				for (int x = 0; x < realWidth; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * realWidth + x] = 0xff000000;
					} else {
						pixels[y * realWidth + x] = 0xffffffff;
						count++;
					}
				}
				if (count == realWidth) {
					blankLine++;
				}
			}
			int lines = blankLine / 2;
			System.out.println("all lines="+lines+",realWidth="+realWidth+",inputWidth="+pxSize);
			int offset=lines;
			if (lines > margin) {
				lines = lines - margin;
			}
			//			int newWidth = realWidth - lines*2;
			//			int[] finalPixels = new int[] {newWidth*newWidth };
			//			int nBlank = lines;
			//			for (int i = 0; i < newWidth; i++) {
			//				int y = i;
			//				System.out.println("row:"+i);
			//				for (int x = 0; x < newWidth; x++) {
			//					finalPixels[y * newWidth + x] = pixels[y* realWidth + x + margin];
			//				}
			//			}
			// 生成二维码图片的格式，使用ARGB_8888
			/*BufferedImage bufImg = new BufferedImage(pxSize, pxSize, BufferedImage.TYPE_4BYTE_ABGR);
			for (int i = 0; i < realWidth; i++) {
				for (int j = 0; j < realWidth; j++) {
					int color = pixels[i * realWidth + j];
					if (j < lines || j > realWidth - lines) {
						color = 0x00000000;
					}
					if (i < lines || i > realWidth - lines) {
						color = 0x00000000;
					}
					bufImg.setRGB(i, j, color);
				}
			}*/
			int marginedWidth=realWidth-lines*2;
			int marginedHeight=realWidth-lines*2;
			BufferedImage bufImg = new BufferedImage(marginedWidth, marginedHeight, BufferedImage.TYPE_4BYTE_ABGR);
			for (int i = 0; i < marginedHeight ; i++) {
				for (int j = 0; j < marginedWidth; j++) {
					int ry =i+lines;
					int rx=j+lines;
					int color = pixels[ry* realWidth + rx];
				/*	if (j < lines || j > realWidth - lines) {
						color = 0x00000000;
					}
					if (i < lines || i > realWidth - lines) {
						color = 0x00000000;
					}*/
					bufImg.setRGB(i, j, color);
				}
			}
			return bufImg;
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage createQRDataMatrixImage(String content, int pxSize, int margin)
			throws Exception {

		try {
			if (content == null || "".equals(content)) {
				throw new Exception("当前输入二维码内容为空");
			}
			// 配置参数
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 图像数据转换，使用了矩阵转换

			/*BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.DATA_MATRIX,
					pxSize, pxSize, hints);*/
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.DATA_MATRIX,
					pxSize, pxSize, hints);
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			// 去白边
			int blankLine = 0;
			int realWidth = bitMatrix.getWidth();
			int realHeight = bitMatrix.getHeight();
			int[] pixels = new int[realWidth * realHeight];
			for (int y = 0; y < realHeight; y++) {
				int count = 0;
				for (int x = 0; x < realWidth; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * realWidth + x] = 0xff000000;
					} else {
						pixels[y * realWidth + x] = 0xffffffff;
						count++;
					}
				}
				if (count == realWidth) {
					blankLine++;
				}
			}
			int lines = blankLine / 2;
			if (lines > margin) {
				lines = lines - margin;
			}
			//			int newWidth = realWidth - lines*2;
			//			int[] finalPixels = new int[] {newWidth*newWidth };
			//			int nBlank = lines;
			//			for (int i = 0; i < newWidth; i++) {
			//				int y = i;
			//				System.out.println("row:"+i);
			//				for (int x = 0; x < newWidth; x++) {
			//					finalPixels[y * newWidth + x] = pixels[y* realWidth + x + margin];
			//				}
			//			}
			// 生成二维码图片的格式，使用ARGB_8888
			/*BufferedImage bufImg = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_4BYTE_ABGR);
			for (int i = 0; i < realWidth; i++) {
				for (int j = 0; j < realHeight; j++) {
					int color = pixels[j * realWidth + i];
					if (j < lines || j > realWidth - lines) {
						color = 0xFFFFFFFF;
					}
					if (i < lines || i > realWidth - lines) {
						color = 0xFFFFFFFF;
					}
					bufImg.setRGB(i, j, color);
				}
			}*/
			int maxW=realWidth-lines;
			int init=lines;
			BufferedImage bufImg = new BufferedImage(realWidth-2*init, realHeight-2*init, BufferedImage.TYPE_4BYTE_ABGR);
			
			for (int i = 0; i < bufImg.getWidth(); i++) {
				for (int j = 0; j < bufImg.getHeight(); j++) {
					int rx=i+init;
					int ry=j+init;
					if(rx>bufImg.getWidth()-init){
						
					}
					int color = pixels[ry * realWidth + rx];
					bufImg.setRGB(i, j, color);
				}
			}
			return bufImg;
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createQRImage(String path, String content, int pxSize, int margin) {
		try {
			BufferedImage img = createQRImage(content, pxSize, margin);
			//			FileOutputStream fio = new FileOutputStream(path);
			ImageIO.write(img, "png", new File(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createDx(String path, String content, int pxSize, int margin) {
		try {
			BufferedImage img = createQRDataMatrixImage(content, pxSize, margin);
			ImageIO.write(img, "png", new File(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createDx2(String path, String content, int pxSize, int margin) {
		try {

			// 配置参数
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 容错级别
			//			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.DATA_MATRIX,
					pxSize, pxSize, hints);
			//如果做网页版输出可以用输出到流
			//MatrixToImageWriter.writeToStream(matrix, format, stream);
			Path path2 = new File(path).toPath();
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BufferedImage toBufferedImage(BitMatrix matrix, MatrixToImageConfig config) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		int onColor = 0xFF000000;
		int offColor = 0xFFFFFFFF;
		int[] rowPixels = new int[width];
		BitArray row = new BitArray(width);
		for (int y = 0; y < height; y++) {
			row = matrix.getRow(y, row);
			for (int x = 0; x < width; x++) {
				rowPixels[x] = row.get(x) ? onColor : offColor;
			}
			image.setRGB(0, y, width, 1, rowPixels, 0, width);
		}
		return image;
	}

	public void ctMt(String path, String content, int pxSize, int margin) {
		try {
			String imagePath = path;
			File file = new File(imagePath);
			content = new String(content.getBytes("utf-8"), "iso-8859-1");
			DataMatrixWriter writerDM = new DataMatrixWriter();
			BitMatrix matrixDM = writerDM.encode(content, BarcodeFormat.DATA_MATRIX, pxSize,
					pxSize);
			MatrixToImageWriter.writeToPath(matrixDM, "png", file.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
