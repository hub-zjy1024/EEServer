package b1b.erp.js.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.impl.code128.Code128Encoder;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import b1b.erp.js.bussiness.SFPrinterUtil;

public class TestBarcode4j {

	static Logger mLogger = LoggerFactory.getLogger(TestBarcode4j.class);

	/**
		* 生成code128条形码
		 * @param message 要生成的文本
		* @param height 条形码的高度
		
		* @param width 条形码的宽度
		
		* @param withQuietZone 是否两边留白
		
		* @param hideText 隐藏可读文本
		
		* @return 图片对应的字节码
		
		*/

	public static byte[] generateBarCode128(String message, Double height, Double width,
			boolean withQuietZone, boolean hideText) {
		Code128Bean bean = new Code128Bean();

		// 分辨率

		int dpi = 72;

		// 设置两侧是否留白

		bean.doQuietZone(withQuietZone);

		// 设置条形码高度和宽度

		bean.setBarHeight(height);

		if (width != null) {

			bean.setModuleWidth(width);

		}

		// 设置文本位置（包括是否显示）

		if (hideText) {

			bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);

		}

		// 设置图片类型

		String format = "image/png";

		ByteArrayOutputStream ous = new ByteArrayOutputStream();

		BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,

				BufferedImage.TYPE_BYTE_BINARY, false, 0);

		// 生产条形码

		bean.generateBarcode(canvas, message);

		try {

			canvas.finish();

		} catch (IOException e) {

			// ByteArrayOutputStream won't happen
		}

		return ous.toByteArray();
	}

	public static void main(String[] args) {
		String code = "SF1040211359633";
		getCode(code);
		Random mRandom = new Random();
		for (int i = 0; i < 100; i++) {
			String temp = String.valueOf(mRandom.nextFloat());
			String nowId = temp.substring(2, 4);
			String tempCode = "SF10402113596" + nowId;
			getCode(tempCode);
			// SF104021135
		}
		// byte[] generateBarCode128 = generateBarCode128(code, 25d, 0.35d, false, true);
	}

	public static void getCode(String code) {
		byte[] generateBarCode128 = generateBarCode128(code, 20d, 0.6d, false, true);
		File file = new File("D:/dyingjia/testbc4j/bcode4j_test_" + code + ".png");
		FileOutputStream fio = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fio = new FileOutputStream(file);
			fio.write(generateBarCode128);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mLogger.info("file ok " + file.getAbsolutePath());
	}

	static double mCodeWidth = 6.6d;
	static double mCodeHeight = 1.3d;

	public static void saveCodeold2(String code, String filePath) {
		byte[] generateBarCode128 = generateBarCode128(code, 20d, 0.37d, false, true);
		File file = new File(filePath);
		FileOutputStream fio = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fio = new FileOutputStream(file);
			fio.write(generateBarCode128);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mLogger.info("file ok " + file.getAbsolutePath());
	}
	public static void saveCodeold(String code, String filePath) {
		byte[] generateBarCode128 = generateBarCode128(code, 20d, 0.61d, false, true);
		File file = new File(filePath);
		FileOutputStream fio = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fio = new FileOutputStream(file);
			fio.write(generateBarCode128);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mLogger.info("file ok " + file.getAbsolutePath());
	}

	static double kywidth = 0.15d, kyheight = 2d;
	static double kywidth2 = 0.15d, kyheight2 = 2d;

	public static void saveCodeBKyWithWidth(String code, String filePath, double width,
			double height) {
		boolean withQuietZone = false;
		boolean hideText = true;
		String message = code;

		Code128Bean bean = new Code128Bean();
		// 分辨率

		int dpi = 300;

		// 设置两侧是否留白

		bean.doQuietZone(withQuietZone);

		// 设置条形码高度和宽度

		bean.setBarHeight(height);

		bean.setModuleWidth(width);

		// 设置文本位置（包括是否显示）

		if (hideText) {
			bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
		}
		// bean.setCodeset(2);

		bean.setCodeset(Code128Constants.CODESET_B);

		// 设置图片类型

		String format = "image/png";

		ByteArrayOutputStream ous = new ByteArrayOutputStream();

		BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,

				BufferedImage.TYPE_BYTE_BINARY, false, 0);
		// 生产条形码
		bean.generateBarcode(canvas, message);
		try {

			canvas.finish();

		} catch (IOException e) {

			// ByteArrayOutputStream won't happen
		}
		byte[] generateBarCode128 = ous.toByteArray();
		File file = new File(filePath);
		FileOutputStream fio = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fio = new FileOutputStream(file);
			fio.write(generateBarCode128);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mLogger.info("codeB file ok " + file.getAbsolutePath());

	}

	public static void saveCodeBKy(String code, String filePath) {
		// saveCodeBKyWithWidth(code, filePath, kywidth, kyheight);
		try {
			SFPrinterUtil.makeCode128Thin(code, 10, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveCodeBKy2(String code, String filePath) {
		// saveCodeBKyWithWidth(code, filePath, kywidth2, kyheight2);
		try {
			SFPrinterUtil.makeCode128Thin(code, 10, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveCode(String code, String filePath) {
		byte[] generateBarCode128 = generateBarCode128(code, 20d, 0.6d, false, true);
		File file = new File(filePath);
		FileOutputStream fio = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fio = new FileOutputStream(file);
			fio.write(generateBarCode128);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fio != null) {
				try {
					fio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mLogger.info("file ok " + file.getAbsolutePath());
	}
}
