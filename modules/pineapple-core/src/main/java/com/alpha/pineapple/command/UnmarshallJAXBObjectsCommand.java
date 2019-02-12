/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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
import java.net.URL;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.configuration.ConfigurationUtils;

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
 * which can unmarshall objects from an XML file using JAXB. The root object of
 * the unmarshalled objects is stored in the context.
 * </p>
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>file</code> Defines the file to unmarshall objects from. The type
 * is <code>java.io.File</code>. If the parent part of the file is undefined
 * (i.e. only a file name is specifiied) then command is searching the user home
 * directory, the current classpath and the system classpath for the file.</li>
 * 
 * <li><code>package</code> Defines the Java package which contains the JAXB
 * generated classes which should be use to unmarshall the XML file into. The
 * type is <code>java.lang.Package</code>.</li>
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
 * <li><code>unmarshalling-result</code> contains the root object of the
 * unmarshalled objects. The type is <code>java.lang.Object</code> as the type
 * depends on the types defined in the schema.</li>
 * 
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
public class UnmarshallJAXBObjectsCommand implements Command {
	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: Defines the file to unmarshall
	 * objects from.
	 */
	public static final String FILE_KEY = "file";

	/**
	 * Key used to identify property in context: Defines the Java package which
	 * contains the JAXB generated classes which should be use to unmarshall the XML
	 * file into.
	 */
	public static final String PACKAGE_KEY = "package";

	/**
	 * Key used to identify property in context: The root object of the unmarshalled
	 * objects is stored in the context using this key.
	 */
	public static final String UNMARSHALLING_RESULT_KEY = "unmarshalling-result";

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * File to unmarshall objects from.
	 */
	@Initialize(FILE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	File file;

	/**
	 * Package which contains JAXB generated classes.
	 */
	@Initialize(PACKAGE_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	Package targetPackage;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		try {

			// declare variables
			Object result = null;
			URL fileURL = null;

			// create unmarshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(targetPackage.getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			if (file.isAbsolute()) {
				result = unmarshaller.unmarshal(file);

			} else {
				// if file isn't absolute then locate the file
				fileURL = ConfigurationUtils.locate(file.getName());

				// complete as failure URL is undefined
				if (fileURL == null) {
					Object[] args2 = { file.getName() };
					executionResult.completeAsFailure(messageProvider, "ujoc.fileurl_location_failure", args2);
					
					// set successful result
					return Command.CONTINUE_PROCESSING;					
				}

				// load located file
				Object[] args2 = { fileURL };
				executionResult.addMessage(ExecutionResult.MSG_MESSAGE,
						messageProvider.getMessage("ujoc.unmarshall_url_info", args2));

				result = unmarshaller.unmarshal(fileURL);
			}

			// store result in context
			context.put(UNMARSHALLING_RESULT_KEY, result);

			// complete as success
			Object[] args3 = { file.isAbsolute() ? file.getAbsolutePath() : fileURL };
			executionResult.completeAsSuccessful(messageProvider, "ujoc.success", args3);

		} catch (Exception e) {
			Object[] args = { e };
			executionResult.completeAsError(messageProvider, "ujoc.error", args, e);
		}

		// set successful result
		return Command.CONTINUE_PROCESSING;
	}
}
