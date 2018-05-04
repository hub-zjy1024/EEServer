package com.zjy.print.docx.task;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XCloseable;
import com.zjy.print.docx.office.OfficeContext;
import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.office.OfficeTask;

public class SaveDocTask implements OfficeTask {

	private String fileIn;
	private String fileOut;

	public SaveDocTask(String fileIn, String fileOut) {
		super();
		this.fileIn = fileIn;
		this.fileOut = fileOut;
	}

	@Override
	public void execute(OfficeContext context) throws OfficeException {
		Object oDesktop = context.getService("com.sun.star.frame.Desktop");
		XComponentLoader xCompLoader = UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
		saveAsPDF(fileIn, fileOut, xCompLoader);
	}

	public void closeInterface(Object obj) {
		if (obj != null) {
			XCloseable closeable = UnoRuntime.queryInterface(XCloseable.class, obj);
			if (closeable != null) {
				try {
					closeable.close(false);
				} catch (Exception closeVetoException) {
					closeVetoException.printStackTrace();
				}
			} else if (obj instanceof XComponent) {
				((XComponent) obj).dispose();
				System.out.println("not colseable:" + obj.toString());
			}
		} else {
			System.err.println("closeInterface error obj is null");
		}
	}

	// netstat -ano|findstr 8100
	public void saveAsPDF(String filePath, String outPut, XComponentLoader loader) {
		String preFix = "file:///";
		String urlI = preFix + filePath;
		String urlOut = preFix + outPut;
		com.sun.star.lang.XComponent docComp = null;
		try {
			docComp = loader.loadComponentFromURL(urlI, "_blank", 0,
					new com.sun.star.beans.PropertyValue[0]);
			XStorable store = UnoRuntime.queryInterface(XStorable.class, docComp);
			com.sun.star.beans.PropertyValue[] properties = new com.sun.star.beans.PropertyValue[1];
			properties[0] = new PropertyValue();
			properties[0].Name = "FilterName";
			properties[0].Value = "writer_pdf_Export";
			store.storeToURL(urlOut, properties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeInterface(docComp);
		}
	}
}
