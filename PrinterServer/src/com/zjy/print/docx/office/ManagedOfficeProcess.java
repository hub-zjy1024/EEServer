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

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.sun.star.frame.XDesktop;
import com.sun.star.lang.DisposedException;

public class ManagedOfficeProcess {

	private static final Integer EXIT_CODE_NEW_INSTALLATION = Integer.valueOf(81);

	private final ManagedOfficeProcessSettings settings;

	private final OfficeProcess process;
	private final OfficeConnection connection;

	private ExecutorService executor = null;
	//	private final Logger logger = Logger.getLogger(getClass().getName());
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	public ManagedOfficeProcess(ManagedOfficeProcessSettings settings) throws OfficeException {
		this.settings = settings;
		process = new OfficeProcess(settings.getOfficeHome(), settings.getUnoUrl(),
				settings.getRunAsArgs(), settings.getTemplateProfileDir(), settings.getWorkDir(),
				settings.getProcessManager());
		executor = Executors.newSingleThreadExecutor(
				new NamedThreadFactory("OfficeProcessThread-" + settings.getUnoUrl().toString()));
		connection = new OfficeConnection(settings.getUnoUrl());
	}

	public OfficeConnection getConnection() {
		return connection;
	}

	public void startAndWait() throws OfficeException {
		Future<?> future = executor.submit(new Runnable() {
			public void run() {
				doStartProcessAndConnect();
			}
		});
		try {
			future.get();
		} catch (Exception exception) {
			throw new OfficeException("failed to startAndWait", exception);
		}
	}

	public void stopAndWait() throws OfficeException {
		Future<?> future = executor.submit(new Runnable() {
			public void run() {
				doStopProcess();
			}
		});
		try {
			future.get();
		} catch (Exception exception) {
			throw new OfficeException("failed to doStopProcess", exception);
		}
	}

	public void restartAndWait() {
		Future<?> future = executor.submit(new Runnable() {
			public void run() {
				try {
					logger.info("start restart");
					doStopProcess();
					doStartProcessAndConnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			future.get(settings.getRetryTimeout(), TimeUnit.MILLISECONDS);
		} catch (Exception exception) {
			throw new OfficeException("failed to restart", exception);
		}
	}

	public void restartDueToTaskTimeout() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.warn(" restartDueToTaskTimeout");
					doTerminateProcess();
				} catch (OfficeException officeException) {
					//					officeException.printStackTrace();
					logger.warn("doTerminateProcess failed:", officeException);
				}
			}
		});
		//		try {
		//			logger.warn(" restartDueToTaskTimeout");
		//			doTerminateProcess();
		//		} catch (OfficeException officeException) {
		//			officeException.printStackTrace();
		//			logger.warn("doTerminateProcess failed:" + officeException);
		//		}
	}

	//	public void restartDueToLostConnection() {
	//		try {
	//			// doEnsureProcessExited();
	//			doStartProcessAndConnect();
	//		} catch (OfficeException officeException) {
	//			officeException.printStackTrace();
	//			logger.warning("could not restart process:" + officeException.getMessage());
	//		}
	//	}
	public void restartDueToLostConnection() {
		executor.execute(new Runnable() {
			public void run() {
				try {
					// doEnsureProcessExited();
					doStartProcessAndConnect();
				} catch (OfficeException officeException) {
					officeException.printStackTrace();
					logger.warn("could not restart process:" + officeException.getMessage());
				}
			}
		});
	}

	private void doStartProcessAndConnect() throws OfficeException {
		try {
			process.start();
			new Retryable() {
				int times = 0;

				protected void attempt() throws TemporaryException, Exception {
					try {
						if (process.isRunning()) {
							connection.connect();
							logger.info("connected at process PID={}", process.getPid());
						} else {
							throw new ConnectException("process doesn't running at:"
									+ settings.getUnoUrl().getAcceptString());
						}
					} catch (ConnectException connectException) {
						times++;
						Integer exitCode = process.getExitCode();
						//						logger.warn(String.format(
						//								"connect failed '%d' times ,exitcode=%d,ExceptionMsg=%s", times,
						//								exitCode, connectException.getMessage()));
						if (exitCode == null) {
							// process is running; retry later
							throw new TemporaryException("process dosent't exit", connectException);
						} else if (exitCode.equals(EXIT_CODE_NEW_INSTALLATION)) {
							logger.warn("office process died with exit code 81; restarting it");
							throw new TemporaryException(connectException);
						} else {
							logger.warn("office process died with exit code =" + exitCode);
							throw new TemporaryException("exit code " + exitCode, connectException);
						}
					}
				}
			}.execute(settings.getRetryInterval(), settings.getRetryTimeout());
		} catch (IOException exception) {
			process.forciblyTerminate(settings.getRetryInterval(), settings.getRetryTimeout());
			throw new OfficeException("could not start office", exception);
		} catch (RetryTimeoutException exception) {
			process.forciblyTerminate(settings.getRetryInterval(), settings.getRetryTimeout());
			throw new OfficeException("could not establish connection", exception);
		} catch (Exception exception) {
			exception.printStackTrace();
			process.forciblyTerminate(settings.getRetryInterval(), settings.getRetryTimeout());
			throw new OfficeException("other exception", exception);
		}
	}

	private void doStopProcess() {
		try {
			XDesktop desktop = OfficeUtils.cast(XDesktop.class,
					connection.getService(OfficeUtils.SERVICE_DESKTOP));
			desktop.terminate();
		} catch (DisposedException disposedException) {
			disposedException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
			doTerminateProcess();
		}
		doEnsureProcessExited();
	}

	private void doEnsureProcessExited() throws OfficeException {
		int exitCode = process.getExitCode(settings.getRetryInterval(), settings.getRetryTimeout());
		logger.info("process has exited with code " + exitCode);
		if (exitCode == -1) {
			doTerminateProcess();
		}
		// process.deleteProfileDir();
	}

	private void doTerminateProcess() {
		int exitCode = process.forciblyTerminate(settings.getRetryInterval(),
				settings.getRetryTimeout());
		logger.warn(("process forcibly terminated with code " + exitCode));
	}

	boolean isConnected() {
		return connection.isConnected();
	}

}
