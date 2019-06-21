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

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.LoggerFactory;

public class ProcessPoolOfficeManager implements OfficeManager {

	private final BlockingQueue<PooledOfficeManager> pool;
	private final PooledOfficeManager[] pooledManagers;
	private final long taskQueueTimeout;

	private volatile boolean running = false;

	//	private final Logger logger = Logger.getLogger(ProcessPoolOfficeManager.class.getName());
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(ProcessPoolOfficeManager.class);

	public ProcessPoolOfficeManager(File officeHome, UnoUrl[] unoUrls, String[] runAsArgs,
			File templateProfileDir, File workDir, long retryTimeout, long taskQueueTimeout,
			long taskExecutionTimeout, int maxTasksPerProcess, ProcessManager processManager) {
		this.taskQueueTimeout = taskQueueTimeout;
		pool = new ArrayBlockingQueue<PooledOfficeManager>(unoUrls.length);
		pooledManagers = new PooledOfficeManager[unoUrls.length];
		for (int i = 0; i < unoUrls.length; i++) {
			PooledOfficeManagerSettings settings = new PooledOfficeManagerSettings(unoUrls[i]);
			settings.setRunAsArgs(runAsArgs);
			settings.setTemplateProfileDir(templateProfileDir);
			settings.setWorkDir(workDir);
			settings.setOfficeHome(officeHome);
			settings.setRetryTimeout(retryTimeout);
			settings.setTaskExecutionTimeout(taskExecutionTimeout);
			settings.setMaxTasksPerProcess(maxTasksPerProcess);
			settings.setProcessManager(processManager);
			pooledManagers[i] = new PooledOfficeManager(settings);
		}
	}

	public synchronized void start() throws OfficeException {
		for (int i = 0; i < pooledManagers.length; i++) {
			pooledManagers[i].start();
			releaseManager(pooledManagers[i]);
		}
		running = true;
	}

	public void execute(OfficeTask task) throws IllegalStateException, OfficeException {
		if (!running) {
			throw new IllegalStateException("this OfficeManager is currently stopped");
		}
		PooledOfficeManager manager = null;
		try {
			manager = acquireManager();
			if (manager == null) {
				throw new TimeoutException("获取 manager失败");
			}
			running = true;
			manager.execute(task);
		} catch (OfficeException e) {
			logger.warn("execute failed:[{}]", task.toString(), e);
			throw new OfficeException(String.format("execute failed:[%s]", task.toString()), e);
		} catch (InterruptedException e) {
			String msg = "acquireManager interupted:" + task.toString();
			logger.warn(msg, e);
			throw new OfficeException(msg);
		} catch (TimeoutException e) {
			//			e.printStackTrace();
			String msg = "acquireManager timeout " + task.toString();
			logger.warn(msg, e);
			throw new OfficeException(msg);
		} finally {
			if (manager != null) {
				releaseManager(manager);
			}
		}
	}

	public synchronized void stop() throws OfficeException {
		running = false;
		logger.info("stopping processPooledManager");
		pool.clear();
		for (int i = 0; i < pooledManagers.length; i++) {
			pooledManagers[i].stop();
		}
		logger.info("stopped processPooledManager");
	}

	public PooledOfficeManager acquireManager() throws InterruptedException {
		return pool.poll(taskQueueTimeout, TimeUnit.MILLISECONDS);
	}

	public void releaseManager(PooledOfficeManager manager) {
		try {
			pool.put(manager);
		} catch (InterruptedException interruptedException) {
			throw new OfficeException("interrupted", interruptedException);
		}
	}

	public boolean isRunning() {
		return running;
	}

}
