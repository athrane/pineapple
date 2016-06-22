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


package com.alpha.pineapple.command;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
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
 * which starts a controlled execution of an external sub process.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>executable</code> defines name of the executable. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>arguments</code> defines the arguments to the executable.
 * The type is <code>java.lang.String[]s</code>.</li>
 * 
 * <li><code>timeout</code> defines the time out in milliseconds before the executable
 * is killed. If no value is defined i.e. it is null or 0, the default value of 5000 
 * milliseconds is used. The type is <code>java.lang.Long</code>.</li> 
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
public class ProcessExecutionCommand implements Command {
	
	/**
	 * Message id, to post messages capturing the standard out 
	 * content from the process. 
	 */
	public static final String MSG_STANDARD_OUT = "Standard Out";

	/**
	 * String array separator.
	 */
	static final String CHAR_SEPARATOR = ",";
	
	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Default time out value for external process which is 5000ms.
	 */
	static final long PROCESS_TIMEOUT = 5000;

	/**
	 * Key used to identify property in context: Defines the executable to run.
	 */
	public static final String EXECUTABLE_KEY = "executable";

	/**
	 * Key used to identify property in context: Defines the arguments to the executable.
	 */
	public static final String ARGUMENTS_KEY = "arguments";

	/**
	 * Key used to identify property in context: Defines the time out value for executable 
	 * before it is killed.
	 */
	public static final String TIMEOUT_KEY = "timeout";
	
	/**
	 * Key used to identify property in context: Contains execution result
	 * object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Defines the executable to run.
	 */
	@Initialize(EXECUTABLE_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	String executable;
		
	/**
	 * Defines the arguments to the executable.
	 */
	@Initialize(ARGUMENTS_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	String[] arguments;

	/**
	 * Defines the time out.
	 */
	@Initialize(TIMEOUT_KEY)	
	Long timeout;
	
	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name="processExecutionMessageProvider")	
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {
		// log debug message
		if (logger.isDebugEnabled()) {
			logger.debug(messageProvider.getMessage("pec.start"));
		}

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// add execution info
        executionResult.addMessage("Executable", executable);
        executionResult.addMessage("Arguments", StringUtils.join(arguments, CHAR_SEPARATOR ));
        executionResult.addMessage("Timeout", createTimeOutDescription());
		
		// resolve working directory
		File workDirectory = resolveWorkDirectory();

		// initialize the command line 
		CommandLine commandLine = initializeCommandLine();			
		
		// create watchdog
		ExecuteWatchdog watchdog = initializeWatchDog();

		// create stream handler
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdout);
		
		// initialize the executor
		Executor executor = initializeExecutor(workDirectory, watchdog, streamHandler);

		try {
												
			// execute process
			int exitValue = executor.execute(commandLine);
			
			// capture stdout
            captureOutput(stdout);
			
            // complete as failed if process fails
            if (exitValue != 0) {
            	
    			// compute execution state from children
        		Object[] args = { exitValue };            
    			executionResult.completeAsFailure(messageProvider, "pec.exitvalue_failed", args);
    			return Command.CONTINUE_PROCESSING;    			
            } 
						
			// compute execution state from children
			executionResult.completeAsComputed(messageProvider, "pec.completed", null, "pec.failed", null);

			// log debug message
			if (logger.isDebugEnabled()) {
				logger.debug(messageProvider.getMessage("pec.completed"));
			}

		} catch (Exception e) {
			
			// capture stdout
            captureOutput(stdout);
			
			Object[] args = { e.toString() };
			executionResult.completeAsError(messageProvider, "pec.error", args, e);

			
		} finally {
			
			// close stdout
			if (stdout != null) {
				stdout.close();
			}			
		}

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Create time out description.
	 * 
	 * @return time out description.
	 */
	String createTimeOutDescription() {
		Object[] args = { PROCESS_TIMEOUT };		
		if((timeout == null) || (timeout.longValue() == 0 )) {
			return messageProvider.getMessage("pec.timeout_info", args);
		}
		return timeout.toString();
	}

	/**
	 * Initialize the watch dog.
	 * 
	 * @return initialize watch dog with time out value.
	 */
	ExecuteWatchdog initializeWatchDog() {		
		if (timeout == null) return new ExecuteWatchdog(PROCESS_TIMEOUT);			
		if (timeout.longValue() == 0 ) return new ExecuteWatchdog(PROCESS_TIMEOUT);		
		return new ExecuteWatchdog(timeout.longValue());		
	}

	/**
	 * Capture the process output and remove trailing white spaces 
	 * and new line.
	 * 
	 * @param stdout Stream where process output is captured from.
	 */
	void captureOutput(ByteArrayOutputStream stdout) {
		String stdString = StringUtils.chomp(stdout.toString().trim());
        executionResult.addMessage(MSG_STANDARD_OUT, stdString );		
	}

	/**
	 * Initialize the executor.
	 * 
	 * @param workDirectory Process work directory.
	 * @param watchdog Process watch dog.
	 * @param streamHandler Process output capture.
	 * 
	 * @return initialized executor object.
	 */
	Executor initializeExecutor(File workDirectory, ExecuteWatchdog watchdog, PumpStreamHandler streamHandler) {
		Executor executor = new DefaultExecutor();
		executor.setExitValue(0);			
		executor.setStreamHandler(streamHandler);
		executor.setWatchdog(watchdog);
		executor.setWorkingDirectory(workDirectory);
		return executor;
	}

	/**
	 * Initialize the command line.
	 * 
	 * @return initialized command line.
	 */
	CommandLine initializeCommandLine() {
		
		CommandLine commandLine = new CommandLine(executable);
		commandLine.addArguments(arguments);
		
		// log debug message
		if( logger.isDebugEnabled()) {
			Object[] args = { commandLine.getExecutable(), ToStringBuilder.reflectionToString(commandLine.getArguments())};            
			logger.debug( messageProvider.getMessage( "pec.argslist_info", args));				
		}
		return commandLine;
	}

	/**
	 * Resolve the work directory used by the process.
	 *   
	 * @return the work directory used by the process.
	 */
	File resolveWorkDirectory() {
				
		// configure process to use java temp dir
		File tempDirectory = SystemUtils.getJavaIoTmpDir();

		// log debug message
		if ( logger.isDebugEnabled() )
		{
			Object[] args = { tempDirectory };            
			logger.debug( messageProvider.getMessage( "pec.tempdir_info", args));
		}
		return tempDirectory;
	}

}
