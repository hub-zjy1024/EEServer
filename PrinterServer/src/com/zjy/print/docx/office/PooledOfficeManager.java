//
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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;

import org.apache.commons.io.DirectoryWalker.CancelException;
import org.slf4j.LoggerFactory;

public class PooledOfficeManager implements OfficeManager {

	private final PooledOfficeManagerSettings settings;
	private final ManagedOfficeProcess managedOfficeProcess;
	private final SuspendableThreadPoolExecutor taskExecutor;

	private boolean stopping = false;
	private int taskCount;
	private Future<?> currentTask;
	private UnoUrl url;

	//	private final Logger logger = Logger.getLogger(getClass().getName());
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(PooledOfficeManager.class);

	private OfficeConnectionEventListener connectionEventListener = new OfficeConnectionEventListener() {
		public void connected(OfficeConnectionEvent event) {
			taskCount = 0;
			taskExecutor.setAvailable(true);
		}

		public void disconnected(OfficeConnectionEvent event) {
			taskExecutor.setAvailable(false);
			OfficeConnection conn = (OfficeConnection) event.getSource();
			if (stopping) {
				// expected
				stopping = false;
			} else {
				logger.warn("connection lost unexpectedly; attempting restart");
				if (currentTask != null) {
					currentTask.cancel(true);
				}
				managedOfficeProcess.restartDueToLostConnection();
			}
		}
	};

	public PooledOfficeManager(UnoUrl unoUrl) {
		this(new PooledOfficeManagerSettings(unoUrl));
	}

	public PooledOfficeManager(PooledOfficeManagerSettings settings) {
		this.settings = settings;
		managedOfficeProcess = new ManagedOfficeProcess(settings);
		managedOfficeProcess.getConnection().addConnectionEventListener(connectionEventListener);
		taskExecutor = new SuspendableThreadPoolExecutor(
				new NamedThreadFactory("OfficeTaskThread"));
		url = settings.getUnoUrl();
	}

	public void execute(final OfficeTask task) throws OfficeException {
		Future<?> futureTask = taskExecutor.submit(new Runnable() {
			public void run() {
				if (settings.getMaxTasksPerProcess() > 0
						&& ++taskCount == settings.getMaxTasksPerProcess() + 1) {
					logger.warn(String.format("reached limit of %d maxTasksPerProcess: restarting",
							settings.getMaxTasksPerProcess()));
					stopping = true;
					//					managedOfficeProcess.restartDueToLostConnection();
					managedOfficeProcess.restartAndWait();
				}
				if (!isRunning()) {
					logger.warn(String.format("connection is not connected , restart"));
					stopping = true;
					managedOfficeProcess.restartAndWait();
				}
				task.execute(getConnection());
			}
		});
		currentTask = futureTask;
		String errMsg = "";
		try {
			futureTask.get(settings.getTaskExecutionTimeout(), TimeUnit.MILLISECONDS);
			logger.info(String.format("task finished [%s] ", task.toString()));
		} catch (TimeoutException timeoutException) {
			//			logger.warn(String.format(
			//					"connection status= '%s' task did not complete within %s ms, restart PooledManager",
			//					isRunning(), settings.getTaskExecutionTimeout()));
			//			stopping=true;
			//			managedOfficeProcess.restartAndWait();
			//			managedOfficeProcess.restartDueToTaskTimeout();
			managedOfficeProcess.restartDueToTaskTimeout();
			errMsg = String.format("task did not complete within %s ms",
					settings.getTaskExecutionTimeout());
		} catch (ExecutionException executionException) {
			if (executionException.getCause() instanceof OfficeException) {
				managedOfficeProcess.getConnection().setDisConnected();
				errMsg = executionException.getCause().getMessage();
			} else {
				errMsg = executionException.getMessage();
			}
		} catch (CancellationException e) {
			errMsg = "TaskCancel: " + e.getMessage();
		} catch (InterruptedException e) {
			errMsg = "Thread Interrupted: " + e.getMessage();
		} catch (Throwable e) {
			errMsg = "other throwable," + e.getMessage();
		}
		if (!"".equals(errMsg)) {
			logger.warn(errMsg);
			throw new OfficeException("Task failed," + errMsg);
		}
	}

	public UnoUrl getUrl() {
		return url;
	}

	public OfficeConnection getConnection() {
		return managedOfficeProcess.getConnection();
	}

	public void start() throws OfficeException {
		logger.info("start PooledOffice manager");
		managedOfficeProcess.startAndWait();
	}

	public void stop() throws OfficeException {
		taskExecutor.setAvailable(false);
		stopping = true;
		taskExecutor.shutdownNow();
		managedOfficeProcess.stopAndWait();
	}

	public boolean isRunning() {
		return managedOfficeProcess.isConnected();
	}

}
