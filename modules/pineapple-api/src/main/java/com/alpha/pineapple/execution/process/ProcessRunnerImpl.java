/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
 * 
 * This file is part of Pineapple.
 * 
 * Pineapple is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * Pineapple is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public 
 * license for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.alpha.pineapple.execution.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;

@Deprecated
public class ProcessRunnerImpl implements ProcessRunner {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Root execution result.
	 * 
	 */
	ExecutionResult rootResult;

	/**
	 * Root directory where the process will execute in.
	 */
	File rootDirectory;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public ExecutionResult execute(String description, List<String> argumentList, Map<String, String> environment) {

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { argumentList.toArray() };
			logger.debug(messageProvider.getMessage("pri.start", args));
		}

		// create execution result object
		ExecutionResult executionResult = getExecutionResult().addChild(description);

		// declare streams
		InputStream inStream = null;
		OutputStream outStream;

		try {

			// configure process to use java temp dir
			File tempDirectory = SystemUtils.getJavaIoTmpDir();

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { tempDirectory };
				logger.debug(messageProvider.getMessage("pri.temp_dir_info", args));
			}

			// setup process
			ProcessBuilder builder = new ProcessBuilder(argumentList);
			builder.directory(tempDirectory);
			builder.redirectErrorStream(true);

			// setup the environment
			Map<String, String> builderEnvironment = builder.environment();
			for (String key : environment.keySet()) {

				// clean the value
				builderEnvironment.put(key, "");

				// set the value
				builderEnvironment.put(key, environment.get(key));
			}

			// start
			Process process = builder.start();

			// get output stream
			outStream = process.getOutputStream();
			inStream = process.getInputStream();

			// wait for process to complete
			int exitValue = process.waitFor();

			// catpure stream content
			String input = captureInput(inStream);

			// close streams
			outStream.flush();
			outStream.close();
			inStream.close();

			// throw exception if process fails
			if (exitValue != 0) {

				// set execution state
				Object[] args = { exitValue };
				executionResult.addMessage("Message", messageProvider.getMessage("pri.failed", args));
				executionResult.addMessage("Stdout", input);
				executionResult.setState(ExecutionState.FAILURE);

				return executionResult;
			}

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug(messageProvider.getMessage("pri.completed"));
			}
		} catch (Exception e) {

			// set execution state
			executionResult.addMessage("StackTrace", StackTraceHelper.getStrackTrace(e));

			try {
				// capture stream content

				if (inStream != null) {
					String input = captureInput(inStream);
					executionResult.addMessage("Stdout", input);
				} else {
					executionResult.addMessage("Stdout", "n/a");
				}

			} catch (Exception e1) {
				executionResult.addMessage("Stdout", "n/a");
			}

			executionResult.setState(ExecutionState.ERROR);
		}

		// return result
		return executionResult;
	}

	public void setDirectory(File processDirectory) {
		this.rootDirectory = processDirectory;
	}

	public void setExecutionResult(ExecutionResult result) {
		this.rootResult = result;
	}

	/**
	 * Return the registered root execution result, otherwise a new root result is
	 * created.
	 * 
	 * @return the registered root execution result, otherwise a new root result is
	 *         created.
	 */
	ExecutionResult getExecutionResult() {

		if (rootResult == null) {
			return new ExecutionResultImpl(null, "Root result, generated by process runner.");
		} else {
			return rootResult;
		}
	}

	String captureInput(InputStream inStream) throws Exception {

		// create reader
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

		// capture input
		StringBuilder message = new StringBuilder();
		String line = "";
		while ((line = br.readLine()) != null) {
			message.append(line);
		}
		return message.toString();
	}

}
