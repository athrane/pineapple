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

package com.alpha.pineapple.docker.command;

import static com.alpha.pineapple.docker.DockerConstants.CUSTOM_JAXB_MAPS;
import static com.alpha.pineapple.docker.utils.ModelUtils.getFirstListEntry;
import static com.alpha.pineapple.docker.utils.ModelUtils.removeContainerNamePrefix;
import static com.alpha.pineapple.execution.report.JaxbReportUtils.reportOnObject;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.javautils.reflection.GetterMethodMatcher;
import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ContainerJson;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedContainerPort;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which list all containers in a Docker daemon.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.docker.session.DockerSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * 
 * <ul>
 * <li><code>containers</code> contains array of container info objects for the
 * existing containers. The type is
 * <code>com.alpha.pineapple.docker.model.ListedContainer[]</code>.</li>
 * 
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
public class ReportOnContainersCommand implements Command {

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * First container name entry.
	 */
	static final int FIRST_ENTRY = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	DockerSession session;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "dockerMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Docker info object builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Docker JAXB Getter method matcher.
	 */
	@Resource
	GetterMethodMatcher dockerJaxbGetterMethodMatcher;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// list containers
		ListedContainer[] containerInfos = dockerClient.listContainers(session, executionResult);

		// add messages
		Object[] args = { containerInfos.length };
		String message = messageProvider.getMessage("rocc.list_container_info", args);
		executionResult.addMessage(ExecutionResult.MSG_MESSAGE, message);

		for (ListedContainer listedContainer : containerInfos) {

			// get container name
			String firstName = getFirstListEntry(listedContainer.getNames());
			firstName = removeContainerNamePrefix(firstName);

			// add child execution result
			Object[] args2 = { firstName, listedContainer.getImage() };
			String message2 = messageProvider.getMessage("rocc.list_single_container_header_info", args2);
			ExecutionResult childResult = executionResult.addChild(message2);

			inspectContainer(listedContainer, childResult);
		}

		// complete result
		executionResult.completeAsComputed(messageProvider, "rocc.list_containers_completed");
		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Inspect container.
	 * 
	 * @param container
	 *            container meta data.
	 * @param result
	 *            result where container data should be added to.
	 */
	void inspectContainer(ListedContainer container, ExecutionResult result) {

		try {

			// get a container name
			List<String> names = container.getNames();
			String name = getFirstListEntry(names);
			name = removeContainerNamePrefix(name);

			// inspect container
			ImageInfo imageInfo = dockerInfoBuilder.buildImageInfoFromFQName(container.getImage());
			ContainerInfo info = dockerInfoBuilder.buildContainerInfo(name, imageInfo);
			ContainerJson inspectedContainer = dockerClient.inspectContainer(session, info, result);

			// report using reflection
			reportOnObject(result, inspectedContainer, dockerJaxbGetterMethodMatcher, CUSTOM_JAXB_MAPS);

			// add ports
			String message3 = messageProvider.getMessage("rocc.list_container_ports_header");
			ExecutionResult portsResult = result.addChild(message3);
			List<ListedContainerPort> ports = container.getPorts();
			Object[] args4 = { ports.size() };
			String message4 = messageProvider.getMessage("rocc.list_container_ports_info", args4);
			portsResult.addMessage(ExecutionResult.MSG_MESSAGE, message4);

			for (ListedContainerPort port : ports) {
				Object[] args5 = { port.getIp(), port.getPublicPort(), port.getPrivatePort(), port.getType() };
				String message5 = messageProvider.getMessage("rocc.list_single_container_port_info", args5);
				ExecutionResult portResult = portsResult.addChild(message5);
				portResult.addMessage("IP", port.getIp());
				portResult.addMessage("Public port", port.getPublicPort());
				portResult.addMessage("Private port", port.getPrivatePort());
				portResult.addMessage("Type", port.getType());

				portResult.setState(ExecutionResult.ExecutionState.SUCCESS);
			}
			portsResult.setState(ExecutionResult.ExecutionState.SUCCESS);

			// complete as computed
			result.completeAsComputed(messageProvider, "rocc.list_single_container_completed");

		} catch (Exception e) {
			result.completeAsError(messageProvider, "rocc.inpect_container_failure", e);
		}
	}

}
