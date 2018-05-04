package com.zjy.print.docx.task;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XCloseable;
import com.zjy.print.docx.office.OfficeContext;
import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.office.OfficeTask;

import b1b.erp.js.utils.UploadUtils;

public class PrintTask implements OfficeTask {

	private String fileName;
	private String printer;
	private int from = 1;
	private int to = -1;
	private boolean hideWindow = false;

	public void setHideWindow(boolean hideWindow) {
		this.hideWindow = hideWindow;
	}

	long time1 = System.currentTimeMillis();

	public PrintTask(String fileName, String printer, boolean showWindow) {
		super();
		this.fileName = fileName;
		this.printer = printer;
		this.hideWindow = showWindow;
	}

	public PrintTask(String fileName, String printer) {
		super();
		this.fileName = fileName;
		this.printer = printer;
	}

	@Override
	public void execute(OfficeContext context) throws OfficeException {
		String pages = from + "-" + to;
		if (to == -1) {
			pages = "1";
		} else if (to < from) {
			throw new OfficeException("lastpages must larger than first pages");
		}
		String[] args = new String[] { printer, fileName, pages };
		Object oDesktop = context.getService("com.sun.star.frame.Desktop");
		XComponentLoader xCompLoader = UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
		java.io.File sourceFile = new java.io.File(fileName);
		com.sun.star.view.XPrintable xPrintable = null;
		try {
			StringBuffer sUrl = new StringBuffer("file:///");
			sUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
			com.sun.star.beans.PropertyValue[] openValues = new com.sun.star.beans.PropertyValue[1];
			openValues[0] = new PropertyValue();
			openValues[0].Name = "Hidden";
			openValues[0].Value = Boolean.valueOf(hideWindow);
			com.sun.star.lang.XComponent docComp = xCompLoader.loadComponentFromURL(sUrl.toString(), "_blank", 0, openValues);
			xPrintable = UnoRuntime.queryInterface(com.sun.star.view.XPrintable.class, docComp);
			com.sun.star.beans.PropertyValue propertyValue[] = new com.sun.star.beans.PropertyValue[1];
			propertyValue[0] = new com.sun.star.beans.PropertyValue();
			propertyValue[0].Name = "Name";
			propertyValue[0].Value = args[0];
			// Setting the name of the printer
			xPrintable.setPrinter(propertyValue);
			// Setting the property "Pages" so that only the desired pages
			com.sun.star.beans.PropertyValue options[] = new com.sun.star.beans.PropertyValue[2];
			options[0] = new com.sun.star.beans.PropertyValue();
			options[0].Name = "Pages";
			options[0].Value = args[2];
			options[1] = new com.sun.star.beans.PropertyValue();
			// 同步执行打印，便于控制关闭文档，false为异步
			options[1].Name = "Wait";
			options[1].Value = Boolean.valueOf(true);

			xPrintable.print(options);
			closeInterface(xPrintable);

		} catch (DisposedException e) {
			throw new OfficeException("loadComponentFromURL exception", e);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new OfficeException("unknown exception", e);
		} finally {
			// closeInterface(xPrintable);
		}
		double len = ((double) (System.currentTimeMillis() - time1)) / 1000;
		System.out
				.println(UploadUtils.getCurrentAtSS() + " finish print " + fileName + " in " + len);
	}

	public void setFirstPage(int from) {
		this.from = from;
	}

	public void setLastPage(int to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "print:" + fileName;
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
			System.err.println(this + " closeWindow error: XComponent is null");
		}
	}

}
