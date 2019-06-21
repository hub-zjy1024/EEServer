//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package com.zjy.print.docx.office;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XBridge;
import com.sun.star.bridge.XBridgeFactory;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.connection.NoConnectException;
import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnector;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalAccessException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.XComponentContext;

public class OfficeConnection implements OfficeContext {

	private static AtomicInteger bridgeIndex = new AtomicInteger();

	private final UnoUrl unoUrl;

	private XComponent bridgeComponent;
	private XMultiComponentFactory serviceManager;
	private XComponentContext componentContext;

	private final List<OfficeConnectionEventListener> connectionEventListeners = new ArrayList<OfficeConnectionEventListener>();

	private volatile boolean connected = false;

	private XEventListener bridgeListener = new XEventListener() {
		public void disposing(EventObject event) {
			if (connected) {
				connected = false;
				logger.warn(String.format("disconnected: '%s'", unoUrl));
				OfficeConnectionEvent connectionEvent = new OfficeConnectionEvent(
						OfficeConnection.this);
				for (OfficeConnectionEventListener listener : connectionEventListeners) {
					listener.disconnected(connectionEvent);
				}
			}
		}
	};

	//	private final Logger logger = Logger.getLogger(getClass().getName());
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	public OfficeConnection(UnoUrl unoUrl) {
		this.unoUrl = unoUrl;
	}

	public void addConnectionEventListener(OfficeConnectionEventListener connectionEventListener) {
		connectionEventListeners.add(connectionEventListener);
	}

	public void connect() throws ConnectException {
		//logger.info(String.format("connecting to '%s'", unoUrl.getAcceptString()));
		try {
			XComponentContext localContext = Bootstrap.createInitialComponentContext(null);
			//			 XUnoUrlResolver urlResolver = UnoUrlResolver.create(localContext);
			//			 Object initialObject = urlResolver.resolve(
			//				      "uno:"+unoUrl.getAcceptString()+";urp;StarOffice.ServiceManager");
			//				  XMultiComponentFactory xOfficeFactory = (XMultiComponentFactory) OfficeUtils.cast(
			//				      XMultiComponentFactory.class, initialObject);
			//				  serviceManager=xOfficeFactory;
			//				  // retrieve the component context as property (it is not yet exported from the office)
			//				  // Query for the XPropertySet interface.
			//				  XPropertySet xProperySet = (XPropertySet)OfficeUtils.cast( 
			//				      XPropertySet.class, xOfficeFactory);
			//				  
			//				  // Get the default context from the office server.
			//				  Object oDefaultContext = xProperySet.getPropertyValue("DefaultContext");
			//				  // Query for the interface XComponentContext.
			//				  XComponentContext xOfficeComponentContext = OfficeUtils.cast(
			//				      XComponentContext.class, oDefaultContext);
			//				  componentContext=xOfficeComponentContext;
			// now create the desktop service
			// NOTE: use the office component context here!
			XMultiComponentFactory localServiceManager = localContext.getServiceManager();
			XConnector connector = OfficeUtils.cast(XConnector.class, localServiceManager
					.createInstanceWithContext("com.sun.star.connection.Connector", localContext));
			XConnection connection = connector.connect(unoUrl.getConnectString());
			XBridgeFactory bridgeFactory = OfficeUtils.cast(XBridgeFactory.class,
					localServiceManager.createInstanceWithContext(
							"com.sun.star.bridge.BridgeFactory", localContext));
			String bridgeName = "jodconverter_" + bridgeIndex.getAndIncrement();
			XBridge bridge = bridgeFactory.createBridge(bridgeName, "urp", connection, null);
			bridgeComponent = OfficeUtils.cast(XComponent.class, bridge);
			bridgeComponent.addEventListener(bridgeListener);
			serviceManager = OfficeUtils.cast(XMultiComponentFactory.class,
					bridge.getInstance("StarOffice.ServiceManager"));
			XPropertySet properties = OfficeUtils.cast(XPropertySet.class, serviceManager);
			componentContext = OfficeUtils.cast(XComponentContext.class,
					properties.getPropertyValue("DefaultContext"));
			connected = true;
			//			IllegalStateException
			//logger.info(String.format("connected: '%s'", unoUrl.getAcceptString()));
			OfficeConnectionEvent connectionEvent = new OfficeConnectionEvent(this);
			for (OfficeConnectionEventListener listener : connectionEventListeners) {
				listener.connected(connectionEvent);
			}
		} catch (NoConnectException exception) {
			throw new ConnectException(exception.getMessage());
		} catch (Exception exception) {
			throw new ConnectException(exception.getMessage());
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void setDisConnected() {
		connected = false;
	}

	public synchronized void disconnect() {
		logger.info(String.format("disconnecting: '%s'", unoUrl.getAcceptString()));
		bridgeComponent.dispose();
	}

	public XComponentContext getXCompContext() {
		return componentContext;
	}

	public Object getService(String serviceName) {
		if (!isConnected()) {
			throw new OfficeException(
					String.format("connection is Closed ,can't obtain service '%s'", serviceName));
		}
		try {
			return serviceManager.createInstanceWithContext(serviceName, componentContext);
		} catch (Exception exception) {
			throw new OfficeException(String.format("failed to obtain service '%s'", serviceName),
					exception);
		}
	}

}
