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

package com.alpha.pineapple.plugin.net.command;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which asserts whether a UNC path can be accessed. This can be used to test
 * unprotected shares on Windows servers.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>hostname</code> defines name of the host. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>share</code> defines the shares name which should be accessed. The
 * type is<code>java.lang.String</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */

public class TestUncPathCommand implements Command {

	/**
	 * Key used to identify property in context: Name of <machine> in the mapping
	 * drive to \\<machine>\<share>
	 */
	public static final String HOSTNAME_KEY = "hostname";

	/**
	 * Key used to identify property in context: Name of <share> in the mapping
	 * drive to \\<machine>\<share>
	 */
	public static final String SHARE_KEY = "share";

	/**
	 * Key used to identify property in context: Contains execution result object.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Name of <machine> in the mapping drive to \\<machine>\<share>
	 */
	@Initialize(HOSTNAME_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String hostname;

	/**
	 * Name of <share> in the mapping drive to \\<machine>\<share>
	 */
	@Initialize(SHARE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String share;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("tupc.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// run test
		doTest(context);

		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("tupc.completed"));
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Do test.
	 * 
	 * @param context
	 *            Command context.
	 * 
	 * @throws Exception
	 *             If test execution fails.
	 */
	void doTest(Context context) throws Exception {
		// create UNC path
		String fileString = "\\\\" + this.hostname + "\\" + this.share;
		File f = new File(fileString);
		f.exists();

		// assert
		if (f.exists()) {

			// set successful result
			Object[] args = { fileString };
			executionResult.completeAsSuccessful(messageProvider, "tupc.succeed", args);
			return;
		}

		// set failed result
		Object[] args = { fileString };
		executionResult.completeAsFailure(messageProvider, "tupc.succeed", args);
	}

}
