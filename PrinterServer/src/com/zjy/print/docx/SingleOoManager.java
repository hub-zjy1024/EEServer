package com.zjy.print.docx;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.PlatformUtil;
import com.zjy.print.docx.office.LinuxProcessManager;
import com.zjy.print.docx.office.OfficeManager;
import com.zjy.print.docx.office.OfficeTask;
import com.zjy.print.docx.office.ProcessManager;
import com.zjy.print.docx.office.ProcessPoolOfficeManager;
import com.zjy.print.docx.office.PureJavaProcessManager;
import com.zjy.print.docx.office.UnoUrl;

public class SingleOoManager {
	static Logger mLogger=LoggerFactory.getLogger(SingleOoManager.class);
	public static OfficeManager oManager;
	public static boolean isOpen = false;
	public OfficeManager tManager;
	private String[] runAsArgs = null;
	private File templateProfileDir = null;
	private File workDir = new File(System.getProperty("java.io.tmpdir"));
	private long taskQueueTimeout = 20 * 1000L; // 30 seconds
	private long taskExecutionTimeout = 15 * 1000L; //
	private int maxTasksPerProcess = 50;
	private long retryTimeout = 15 * 1000;
	public static SingleOoManager single;
	private static int[] arrayPorts = new int[1];
	static {
		for (int i = 0; i < arrayPorts.length; i++) {
			arrayPorts[i] = 8100 + i;
		}
	}

	public static SingleOoManager getInstance(String oOffice_HOME, int[] ports) {
		if (single == null) {
			synchronized (SingleOoManager.class) {
				if (single == null) {
					single = new SingleOoManager(oOffice_HOME, ports);
					single.start();
				}
			}
		}
		return single;
	}

	public static SingleOoManager getInstance(String oOffice_HOME) {
		return getInstance(oOffice_HOME, arrayPorts);
	}

	private SingleOoManager(String oOffice_HOME, int[] ports) {
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
				maxTasksPerProcess, manager);

	}

	public void excute(OfficeTask task) {
		tManager.execute(task);
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
}
