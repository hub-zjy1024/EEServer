package com.zjy.print.docx.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XCloseable;
import com.zjy.print.docx.office.OfficeContext;
import com.zjy.print.docx.office.OfficeException;
import com.zjy.print.docx.office.OfficeTask;

public class PrintTask implements OfficeTask {
	private String fileName;
	private String printer;
	private int from = 1;
	private int to = -1;
	private boolean hideWindow = false;
	double len = 0;

	static Logger mLogger=LoggerFactory.getLogger(PrintTask.class);
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
	
		java.io.File sourceFile = new java.io.File(fileName);
		com.sun.star.view.XPrintable xPrintable = null;
		String errMsg = "";
		com.sun.star.lang.XComponent docComp =null;
		try {
			Object oDesktop = context.getService("com.sun.star.frame.Desktop");
			if(oDesktop==null){
				throw new Exception("com.sun.star.frame.Desktop==null");
			}
			XComponentLoader xCompLoader = UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
			if(xCompLoader==null){
				throw new Exception("xCompLoader==null");
			}
			StringBuffer sUrl = new StringBuffer("file:///");
			sUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
			com.sun.star.beans.PropertyValue[] openValues = new com.sun.star.beans.PropertyValue[1];
			openValues[0] = new PropertyValue();
			openValues[0].Name = "Hidden";
			openValues[0].Value = Boolean.valueOf(hideWindow);
			docComp=xCompLoader.loadComponentFromURL(sUrl.toString(),
					"_blank", 0, openValues);
			xPrintable = UnoRuntime.queryInterface(com.sun.star.view.XPrintable.class, docComp);
			if(xPrintable==null){
				throw new Exception("xPrintable==null");
			}
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
//			options[1].Value = Boolean.valueOf(true);
			options[1].Value = Boolean.valueOf(false);

			xPrintable.print(options);
			
		/*} catch (DisposedException e) {
			throw new OfficeException("loadComponentFromURL exception", e);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new OfficeException("unknown exception", e);*/
		} catch (Throwable e) {
			throw new OfficeException("打印过程出错", e);
		} 
		finally {
			//closeInterface(docComp);
			closeInterface(docComp);
			len = ((double) (System.currentTimeMillis() - time1)) / 1000;
		}
	}

	public void setFirstPage(int from) {
		this.from = from;
	}

	public void setLastPage(int to) {
		this.to = to;
	}

	@Override
	public String toString() {
		String end = "";
		if (len != 0) {
			end = " finished in " + len;
		}
		return "print:" + fileName + end;
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
				mLogger.warn("not colseable:" + obj.toString());
			}
		} else {
			mLogger.warn(this + " closeWindow error: XComponent is null");
//			System.err.println(this + " closeWindow error: XComponent is null");
		}
	}

}
