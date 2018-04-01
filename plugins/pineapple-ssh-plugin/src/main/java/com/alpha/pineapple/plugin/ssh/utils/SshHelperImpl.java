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

package com.alpha.pineapple.plugin.ssh.utils;

import java.io.File;

import javax.annotation.Resource;

import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.test.AssertionHelper;

/**
 * implementation of the {@linkplain SshHelper} interface.
 */
public class SshHelperImpl implements SshHelper {

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Execution info provider.
	 */
	@Resource
	ExecutionInfoProvider coreExecutionInfoProvider;

	/**
	 * Assertion helper.
	 */
	@Resource
	AssertionHelper assertionHelper;

	public boolean isLocalFileValid(File installer, ExecutionResult result) {

		// assert installer is valid
		ExecutionResult assertionResult = assertionHelper.assertFileExist(installer, messageProvider,
				"shi.assert_localfile_exists", result);

		// add installer path info to execution result
		String messageHeader = messageProvider.getMessage("shi.assert_localfile_exists_info");
		assertionResult.addMessage(messageHeader, installer.getAbsolutePath());

		return (assertionResult.isSuccess());
	}

	public String resolveModulePath(ExecutionResult result, String localFile) {

		// exit if prefix isn't present
		if (!coreRuntimeDirectoryProvider.startsWithModulePathPrefix(localFile))
			return localFile;

		// get module info
		ExecutionInfo executionInfo = coreExecutionInfoProvider.get(result);
		ModuleInfo moduleInfo = executionInfo.getModuleInfo();

		// resolve path
		File resolvedPath = coreRuntimeDirectoryProvider.resolveModelPath(localFile, moduleInfo);
		localFile = resolvedPath.getAbsolutePath();

		return localFile;
	}

}
