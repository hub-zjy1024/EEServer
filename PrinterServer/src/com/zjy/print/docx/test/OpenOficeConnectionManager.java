package com.zjy.print.docx.test;

import java.io.File;
import java.io.IOException;

import com.sun.javafx.PlatformUtil;
import com.zjy.print.docx.office.DefaultOfficeManagerConfiguration;
import com.zjy.print.docx.office.ExternalOfficeManagerConfiguration;
import com.zjy.print.docx.office.LinuxProcessManager;
import com.zjy.print.docx.office.OfficeConnectionProtocol;
import com.zjy.print.docx.office.OfficeManager;
import com.zjy.print.docx.office.ProcessManager;
import com.zjy.print.docx.office.ProcessPoolOfficeManager;
import com.zjy.print.docx.office.PureJavaProcessManager;
import com.zjy.print.docx.office.UnoUrl;

public class OpenOficeConnectionManager {
	public static OfficeManager oManager;
	public static boolean isOpen = false;
	public OfficeManager tManager;
	private String[] runAsArgs = null;
	private File templateProfileDir = null;
	private File workDir = new File(System.getProperty("java.io.tmpdir"));
	private long taskQueueTimeout = 15*1000L; // 30 seconds
	private long taskExecutionTimeout = 15*1000L; //
	private int maxTasksPerProcess =300;
	private long retryTimeout = 15 * 1000;

	public OpenOficeConnectionManager(String oOffice_HOME, int port) {
		//
		tManager = new DefaultOfficeManagerConfiguration().setOfficeHome(new File(oOffice_HOME))
				.setConnectionProtocol(OfficeConnectionProtocol.SOCKET).setPortNumber(port)
				.setRetryTimeout(20 * 1000).buildOfficeManager();
	}

	public OpenOficeConnectionManager(String oOffice_HOME, int[] ports) {
		UnoUrl[] urls = new UnoUrl[ports.length];
		for (int i = 0; i < ports.length; i++) {
			urls[i] = UnoUrl.socket(ports[i]);
		}
		workDir = new File(oOffice_HOME);
		ProcessManager manager = new PureJavaProcessManager();
		if (PlatformUtil.isLinux()) {
			manager = new LinuxProcessManager();
		}
		tManager = new ProcessPoolOfficeManager(new File(oOffice_HOME), urls, runAsArgs,
				templateProfileDir, workDir, retryTimeout, taskQueueTimeout, taskExecutionTimeout,
				maxTasksPerProcess,manager);

	}

	public void start() {
		if (tManager != null) {
			tManager.start();
		}
	}

	public void stop() {
		if (tManager != null) {
			tManager.stop();
		}
	}

	public OfficeManager getOfficeManager() {
		return tManager;
	}
}
