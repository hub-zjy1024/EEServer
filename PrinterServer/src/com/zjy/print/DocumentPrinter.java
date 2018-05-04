package com.zjy.print;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XBridge;
import com.sun.star.bridge.XBridgeFactory;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnector;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XCloseable;
import com.zjy.print.docx.OpenOficeConnectionManager;

public class DocumentPrinter {
	private static AtomicInteger bridgeIndex = new AtomicInteger();

	private static ArrayBlockingQueue<Integer> ports = new ArrayBlockingQueue<>(10);
	static {
		for (int i = 0; i < 10; i++) {
			ports.add(8100 + i);
		}
	}

	public static void main(String args[]) {
		// doc2pdf();
		String path = "d:/dyj/openoffice_print.docx";
		String printname = "ZDesigner GK888d (EPL)";
		// String printname = "KY_Printer";
		testOoPrint(printname, path);
	}

	public static void testOoPrint(String printname, String path) {
		String[] args;
		args = new String[] { printname, path, "1" };
		String officePath = "C:/Program Files (x86)/OpenOffice 4/program/";
		com.sun.star.uno.XComponentContext xContext = null;
		OpenOficeConnectionManager manager = null;
		try {
			// get the remote office component context
			String host = "127.0.0.1";
			int port = 8100;
			long time0 = System.currentTimeMillis();
			port = ports.take();
			System.out.println("Thread_" + Thread.currentThread().getId() + ";get_waittime:"
					+ (System.currentTimeMillis() - time0));
			String hostAndPort = "host=" + host + ",port=" + port;
			String accept = "socket,host=" + host + ",port=" + port + ";urp";
			String acceptStr = "-accept=socket," + hostAndPort + ";urp;";
			String unoConnectString = "uno:socket," + hostAndPort
					+ ";urp;StarOffice.ComponentContext";
			String connStr = "uno:socket," + hostAndPort + ";urp;StarOffice.ComponentContext";
			String oOffice_HOME = "C:/Program Files (x86)/OpenOffice 4/";
			manager = new OpenOficeConnectionManager(oOffice_HOME, port);
			manager.start();
			xContext = getXCompContextByBridge(host, port);
			xContext = buildCompContext(host, port);
			// xContext=BootstrapSocketConnector.bootstrap(officePath);
			System.out.println("start start office acceptstr:" + acceptStr);
			System.out.println(
					"Connected to a running office ...XConponentContext:" + xContext.toString());
			// netstat -ano|findstr 8100
			com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();
			Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop",
					xContext);
			System.out.println("deskTopString:" + oDesktop.toString());
			com.sun.star.frame.XComponentLoader xCompLoader = UnoRuntime
					.queryInterface(com.sun.star.frame.XComponentLoader.class, oDesktop);
			java.io.File sourceFile = new java.io.File(args[1]);
			StringBuffer sUrl = new StringBuffer("file:///");
			sUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
			com.sun.star.lang.XComponent docComp = xCompLoader.loadComponentFromURL(sUrl.toString(),
					"_blank", 0, new com.sun.star.beans.PropertyValue[0]);
			com.sun.star.view.XPrintable xPrintable = UnoRuntime
					.queryInterface(com.sun.star.view.XPrintable.class, docComp);
			com.sun.star.beans.PropertyValue propertyValue[] = new com.sun.star.beans.PropertyValue[1];
			propertyValue[0] = new com.sun.star.beans.PropertyValue();
			propertyValue[0].Name = "Name";
			propertyValue[0].Value = args[0];
			// Setting the name of the printer
			xPrintable.setPrinter(propertyValue);
			// Setting the property "Pages" so that only the desired pages
			// will be printed.
			propertyValue[0] = new com.sun.star.beans.PropertyValue();
			propertyValue[0].Name = "Pages";
			propertyValue[0].Value = args[2];
			xPrintable.print(propertyValue);
			Thread.sleep(8 * 1000);
			closeInterFace(docComp);
			// XDesktop xDesktop = UnoQuery.cast(XDesktop.class, oDesktop);
			// xDesktop.terminate();
			long time1 = System.currentTimeMillis();
			ports.offer(port);
			System.out.println("Thread_" + Thread.currentThread().getId() + ";add_waittime:"
					+ (System.currentTimeMillis() - time1));
			// socket.disconnect();
			System.out.println("over");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		} finally {
			if (manager != null) {
				manager.stop();
			}
		}
	}

	public static void closeInterFace(Object docComp) {
		if (docComp != null) {
			XCloseable closeable = UnoRuntime.queryInterface(XCloseable.class, docComp);
			if (closeable != null) {
				System.out.println("close interface" + docComp.toString());
				try {
					closeable.close(true);
				} catch (Exception closeVetoException) {
					closeVetoException.printStackTrace();
				}
			} else {
				System.out.println("not colseable:" + docComp.toString());
			}
		}
	}

	public static void saveAsPDF(String filePath, String outPut, XComponentLoader loader)
			throws com.sun.star.io.IOException, IllegalArgumentException {
		String preFix = "file:///";
		String urlI = preFix + filePath;
		String urlOut = preFix + outPut;
		loader.loadComponentFromURL(urlI, "_blank", 0, new com.sun.star.beans.PropertyValue[0]);
		XStorable store = UnoRuntime.queryInterface(XStorable.class, loader);
		com.sun.star.beans.PropertyValue[] properties = new com.sun.star.beans.PropertyValue[1];
		properties[0].Name = "FilterName";
		properties[0].Value = "writer_pdf_Export";
		store.storeToURL(urlOut, properties);
	}

	public static com.sun.star.uno.XComponentContext getXCompContextByBridge(String host,
			int port) {
		XComponent bridgeComponent = null;
		XComponentContext cContext = null;
		try {
			String socketConnStr = "socket,host=" + host + ",port=" + port + ",tcpNoDelay=1";
			XComponentContext localContext = Bootstrap.createInitialComponentContext(null);
			XMultiComponentFactory localServiceManager = localContext.getServiceManager();
			XConnector connector = UnoRuntime.queryInterface(XConnector.class, localServiceManager
					.createInstanceWithContext("com.sun.star.connection.Connector", localContext));
			XConnection connection = connector.connect(socketConnStr);
			XBridgeFactory bridgeFactory = UnoRuntime.queryInterface(XBridgeFactory.class,
					localServiceManager.createInstanceWithContext(
							"com.sun.star.bridge.BridgeFactory", localContext));
			String bridgeName = "zjysConnection_" + bridgeIndex.getAndIncrement();
			XBridge bridge = bridgeFactory.createBridge(bridgeName, "urp", connection, null);
			bridgeComponent = UnoRuntime.queryInterface(XComponent.class, bridge);
			XEventListener bridgeListener = new XEventListener() {
				@Override
				public void disposing(EventObject arg0) {
					System.out.println("bridgeComponent isDisposing=" + arg0.Source.toString());
				}
			};
			bridgeComponent.addEventListener(bridgeListener);
			XMultiComponentFactory serviceManager = UnoRuntime.queryInterface(
					XMultiComponentFactory.class, bridge.getInstance("StarOffice.ServiceManager"));
			XPropertySet properties = UnoRuntime.queryInterface(XPropertySet.class, serviceManager);
			cContext = UnoRuntime.queryInterface(XComponentContext.class,
					properties.getPropertyValue("DefaultContext"));
		} catch (java.lang.Exception exception) {
			exception.printStackTrace();
		}
		return cContext;
	}

	public static void doc2pdf() {
		String word = "d:/dyj/openoffice_print.docx";
		String newPDF = "d:/dyj/1515564443441_openoffice" + System.currentTimeMillis() + ".pdf";
		int dpi = 203;
	}

	public static XComponentContext buildCompContext(String host, int port) {
		XComponentContext xContext = null;
		try {
			XComponentContext xcomponentcontext = Bootstrap.createInitialComponentContext(null);
			XUnoUrlResolver urlResolver = UnoUrlResolver.create(xcomponentcontext);
			Object initialObject = urlResolver.resolve(
					"uno:socket,host=" + host + ",port=" + port + ";urp;StarOffice.ServiceManager");
			System.out.println("resovler null " + (initialObject == null));
			XMultiComponentFactory xOfficeFactory = (XMultiComponentFactory) UnoRuntime
					.queryInterface(XMultiComponentFactory.class, initialObject);
			XPropertySet xProperySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
					xOfficeFactory);
			Object oDefaultContext = xProperySet.getPropertyValue("DefaultContext");
			XComponentContext xOfficeComponentContext = (XComponentContext) UnoRuntime
					.queryInterface(XComponentContext.class, oDefaultContext);
			xContext = xOfficeComponentContext;
		} catch (java.lang.RuntimeException e) {
			throw e;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		return xContext;
	}
}
