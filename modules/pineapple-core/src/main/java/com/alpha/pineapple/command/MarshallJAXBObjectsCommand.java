/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
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
 * which can marshall objects to a XML file using JAXB. The root object of the
 * object graph from the context is written to file.
 * </p>
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>file</code> Defines the file to marshall objects to. The type is
 * <code>java.io.File</code>. If the file isn't specified as a absolute path the
 * the command terminates with a failure. If any directories in the path doesn't
 * exist the command will create them.</li>
 * <li><code>root</code> Defines the root object in the object graph which
 * should be marshalled. The type is <code>java.lang.Object</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is definition of these keys in
 * the context:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the command succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class MarshallJAXBObjectsCommand implements Command {
    
    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Key used to identify property in context: Defines the file to marshall
     * objects to.
     */
    public static final String FILE_KEY = "file";

    /**
     * Key used to identify property in context: Defines root of the object
     * graph which should be marshalled.
     */
    public static final String ROOT_KEY = "root";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * File to marshall objects to.
     */
    @Initialize(FILE_KEY)
    @ValidateValue(ValidationPolicy.NOT_EMPTY)
    File file;

    /**
     * Root object in object graph which should be marshalled.
     */
    @Initialize(ROOT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    Object rootObject;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    public boolean execute(Context context) throws Exception {

	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	// marshall
	doMarshall();

	// set successful result
	return Command.CONTINUE_PROCESSING;
    }

    /**
     * Marshall the file.
     */
    void doMarshall() {

	// get package name
	String packageName = rootObject.getClass().getPackage().getName();

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { file.getAbsolutePath(), packageName };
	    logger.debug(messageProvider.getMessage("mjoc.marshall_info", args));
	}

	// define stream for exception handling
	OutputStream os = null;

	try {

	    // get parent directory
	    File parent = file.getParentFile();

	    // create parent directories
	    FileUtils.forceMkdir(parent);

	    // create JAXB context
	    JAXBContext jaxbContext = JAXBContext.newInstance(packageName);

	    // create marshaller
	    Marshaller marshaller = jaxbContext.createMarshaller();
	    marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

	    // marshall
	    os = new FileOutputStream(file);
	    marshaller.marshal(rootObject, os);
	    os.close();

	} catch (Exception e) {

	    // complete operation with error
	    Object[] args = { e.getMessage() };
	    executionResult.completeAsError(messageProvider, "mjoc.error", args, e);

	} finally {
	    // close OS
	    if (os != null) {

		try {
		    os.close();
		} catch (IOException e) {

		    // fail if still executing
		    if (executionResult.isExecuting()) {

			// complete operation with error
			executionResult.completeAsError(messageProvider, "mjoc.io_error", e);
			return;
		    } else {

			// create message
			String message = messageProvider.getMessage("mjoc.io_error");

			// store the messages
			executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
			executionResult.addMessage(ExecutionResult.MSG_STACKTRACE, StackTraceHelper.getStrackTrace(e));
			return;
		    }
		}
	    }
	}

	// complete as success
	if (executionResult.isExecuting()) {
	    Object[] args = { file };
	    executionResult.completeAsSuccessful(messageProvider, "mjoc.success", args);
	}
    }
}
