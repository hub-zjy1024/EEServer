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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.COMM_FAILURE;

import com.sun.javafx.tk.Toolkit.Task;

public class PureJavaProcessManager implements ProcessManager {

	public long findPid(ProcessQuery query) {
		return getPidByCommand(query.getArgument());
	}

	public void kill(Process process, long pid) {
		if (process != null) {
			process.destroy();
		}
		try {
			Process exec = Runtime.getRuntime().exec("Taskkill /f /IM " + pid);
			BufferedReader input = new BufferedReader(
					new InputStreamReader(exec.getInputStream(), "GBK"));
			String line = "";
			String result = "";
			while ((line = input.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPidByCommand(String name) {
		Process process = null;
		try {
			int portIndex = name.indexOf("port");
			int endIndex = name.indexOf(";", portIndex);
			String port = name.substring(portIndex + 5);
			String tag = "netstat -ano";
			process = Runtime.getRuntime().exec(tag);
			BufferedReader input = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String line = "";
			String result = "";
			Pattern compile = Pattern.compile("[0-9]+");
			String tempResult = "";
			while ((line = input.readLine()) != null) {
				result += line + "\n";
				if (line.contains(port) && line.contains("LISTENING")) {
					tempResult = line;
				}
			}
			input.close();
			Matcher matcher = compile.matcher(tempResult);
			int pid = -1;
			while (matcher.find()) {
				String group = matcher.group();
				if (matcher.end() == tempResult.length()) {
					pid = Integer.parseInt(group);
				}
			}
			return pid;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -2;
	}

	public int search2(String name) {
		Process process = null;
		try {
			// process = Runtime.getRuntime().exec("cmd.exe /c tasklist");
			process = Runtime.getRuntime().exec("tasklist");
			BufferedReader input = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String line = "";
			String result = "";
			Pattern compile = Pattern.compile("[0-9]+");
			while ((line = input.readLine()) != null) {
				if (line.contains(name)) {
					System.out.println(line);
					Matcher matcher = compile.matcher(line);
					while (matcher.find()) {
						String group = matcher.group(0);
						System.out.println("group:" + group);
						result = group;
						break;
					}
				}
			}
			int pid = -1;
			if (!result.equals("")) {
				try {
					pid = Integer.parseInt(result);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			System.out.println("result pid:" + result);
			input.close();
			return pid;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -2;
	}
}
