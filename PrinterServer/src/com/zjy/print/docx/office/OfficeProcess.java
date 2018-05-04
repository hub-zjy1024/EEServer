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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class OfficeProcess {

	private final File officeHome;
	private final UnoUrl unoUrl;
	private final String[] runAsArgs;
	private final File templateProfileDir;
	private final File instanceProfileDir;
	private final ProcessManager processManager;

	private Process process;
	private long pid = ProcessManager.PID_UNKNOWN;

	private final Logger logger = Logger.getLogger(getClass().getName());
	private ProcessQuery processQuery;

	public OfficeProcess(File officeHome, UnoUrl unoUrl, String[] runAsArgs,
			File templateProfileDir, File workDir, ProcessManager processManager) {
		this.officeHome = officeHome;
		this.unoUrl = unoUrl;
		this.runAsArgs = runAsArgs;
		this.templateProfileDir = templateProfileDir;
		this.instanceProfileDir = getInstanceProfileDir(workDir, unoUrl);
		this.processManager = processManager;
		processQuery = new ProcessQuery("soffice.bin", unoUrl.getAcceptString());
	}

	public void start() throws IOException {
		start(false);
	}

	public void start(boolean restart) throws IOException {
		long existingPid = processManager.findPid(processQuery);
		if (!(existingPid == ProcessManager.PID_NOT_FOUND
				|| existingPid == ProcessManager.PID_UNKNOWN)) {
			logger.warning(
					String.format("a process at  '%s' is already running; pid %d,kill process",
							unoUrl.getAcceptString(), existingPid));
			processManager.kill(process, existingPid);
		}
		if (!restart) {
			// prepareInstanceProfileDir();
		}
		List<String> command = new ArrayList<String>();
		File executable = OfficeUtils.getOfficeExecutable(officeHome);
		if (runAsArgs != null) {
			command.addAll(Arrays.asList(runAsArgs));
		}
		command.add(executable.getAbsolutePath());
		command.add("-accept=" + unoUrl.getAcceptString() + ";urp;");
		// command.add("-env:UserInstallation=" +
		// OfficeUtils.toUrl(instanceProfileDir));
		command.add("-headless");
		command.add("-nocrashreport");
		command.add("-nodefault");
		command.add("-nofirststartwizard");
		command.add("-nolockcheck");
		command.add("-nologo");
		command.add("-norestore");
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		if (PlatformUtils.isWindows()) {
			addBasisAndUrePaths(processBuilder);
		}
		process = processBuilder.start();
//		logger.info(String.format("starting at acceptString '%s' and profileDir '%s'", unoUrl,
//				instanceProfileDir));
		logger.info(String.format("starting at acceptString '%s'", unoUrl));
	}

	private File getInstanceProfileDir(File workDir, UnoUrl unoUrl) {
		String dirName = "jodconverter_"
				+ unoUrl.getAcceptString().replace(',', '_').replace('=', '-');
		return new File(workDir, dirName);
	}

	private void prepareInstanceProfileDir() throws OfficeException {
		if (instanceProfileDir.exists()) {
			logger.warning(String.format("profile dir '%s' already exists; deleting",
					instanceProfileDir.getName()));
			deleteProfileDir();
		}
		if (templateProfileDir != null) {
			try {
				FileUtils.copyDirectory(templateProfileDir, instanceProfileDir);
			} catch (IOException ioException) {
				throw new OfficeException("failed to create profileDir", ioException);
			}
		}
	}

	public void deleteProfileDir() {
		if (instanceProfileDir != null) {
			try {
				if (!instanceProfileDir.isDirectory()) {
					return;
				}
				FileUtils.deleteDirectory(instanceProfileDir);
			} catch (IOException ioException) {
				File oldProfileDir = new File(instanceProfileDir.getParentFile(),
						instanceProfileDir.getName() + ".old." + System.currentTimeMillis());
				if (instanceProfileDir.renameTo(oldProfileDir)) {
					logger.info("could not delete profileDir: " + ioException.getMessage()
							+ "; renamed it to " + oldProfileDir.getName());
				} else {
					logger.severe("could not rename profileDir: " + ioException.getMessage());
				}
			}
		}
	}

	private void addBasisAndUrePaths(ProcessBuilder processBuilder) throws IOException {
		// see
		// http://wiki.services.openoffice.org/wiki/ODF_Toolkit/Efforts/Three-Layer_OOo
		File basisLink = new File(officeHome, "basis-link");
		if (!basisLink.isFile()) {
			logger.fine("no %OFFICE_HOME%/basis-link found; "
					+ "assuming it's OOo 2.x and we don't need to append URE and Basic paths");
			return;
		}
		String basisLinkText = FileUtils.readFileToString(basisLink).trim();
		File basisHome = new File(officeHome, basisLinkText);
		File basisProgram = new File(basisHome, "program");
		File ureLink = new File(basisHome, "ure-link");
		String ureLinkText = FileUtils.readFileToString(ureLink).trim();
		File ureHome = new File(basisHome, ureLinkText);
		File ureBin = new File(ureHome, "bin");
		Map<String, String> environment = processBuilder.environment();
		// Windows environment variables are case insensitive but Java maps are
		// not :-/
		// so let's make sure we modify the existing key
		String pathKey = "PATH";
		for (String key : environment.keySet()) {
			if ("PATH".equalsIgnoreCase(key)) {
				pathKey = key;
			}
		}
		String path = environment.get(pathKey) + ";" + ureBin.getAbsolutePath() + ";"
				+ basisProgram.getAbsolutePath();
		logger.fine(String.format("setting %s to \"%s\"", pathKey, path));
		environment.put(pathKey, path);
	}

	public boolean isRunning() {
		if (process == null) {
			return false;
		}
		return getExitCode() == null;
	}

	private class ExitCodeRetryable extends Retryable {

		private int exitCode;

		protected void attempt() throws TemporaryException, Exception {
			try {
				if (process != null) {
					process.destroy();
					exitCode = process.exitValue();
				}
			} catch (IllegalThreadStateException illegalThreadStateException) {
				exitCode = -1;
				throw new TemporaryException(illegalThreadStateException);
			}
		}

		public int getExitCode() {
			return exitCode;
		}

	}

	public Integer getExitCode() {
		try {
			return process.exitValue();
		} catch (IllegalThreadStateException exception) {
			return null;
		}
	}

	public int getExitCode(long retryInterval, long retryTimeout) {
		ExitCodeRetryable retryable = new ExitCodeRetryable();
		try {
			retryable.execute(retryInterval, retryTimeout);
		} catch (RetryTimeoutException retryTimeoutException) {
			retryTimeoutException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return retryable.getExitCode();
	}

	public int forciblyTerminate(long retryInterval, long retryTimeout){
		try {
			pid = processManager.findPid(processQuery);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		logger.info(
				String.format("trying to forcibly terminate process: '%s' pid='%s'", unoUrl, pid));
		try {
			processManager.kill(process, pid);
		} catch (IOException e) {
			logger.warning(String.format("fail to kill process: '%s' pid='%s'", unoUrl, pid));
			e.printStackTrace();
		}
		return getExitCode(retryInterval, retryTimeout);
	}

}
