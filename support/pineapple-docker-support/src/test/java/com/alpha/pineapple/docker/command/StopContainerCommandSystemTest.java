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

package com.alpha.pineapple.docker.command;

import static com.alpha.pineapple.docker.DockerConstants.CONTAINER_STOP_TIMEOUT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain StopContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class StopContainerCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command stopContainerCommand;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Docker session.
	 */
	DockerSession session;

	/**
	 * Docker helper.
	 */
	@Resource
	DockerTestHelper dockerHelper;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Docker info objects builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Container instance info.
	 */
	ContainerInstanceInfo containerInstanceInfo;

	/**
	 * Container info.
	 */
	ContainerInfo containerInfo;

	/**
	 * Tagged image info.
	 */
	ImageInfo taggedImageInfo;

	/**
	 * Default image info (CentOS).
	 */
	ImageInfo defaultImageInfo;

	/**
	 * Random container ID.
	 */
	String randomId;

	/**
	 * Random container name.
	 */
	String randomName;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomName = RandomStringUtils.randomAlphabetic(10);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// create session
		session = dockerHelper.createDefaultSession();

		// create image and info's
		defaultImageInfo = dockerHelper.createDefaultImage(session);
		taggedImageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// tag image
		dockerHelper.tagImage(session, defaultImageInfo, taggedImageInfo);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		// delete container - if it exists
		if ((containerInfo != null) && (dockerHelper.containerExists(session, containerInfo))) {
			dockerHelper.deleteContainer(session, containerInfo);
		}

		// delete tagged image - if it exists
		dockerHelper.deleteImage(session, taggedImageInfo);
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(stopContainerCommand);
	}

	/**
	 * Test that command can stop running container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStopRunningContainer() throws Exception {

		// create and start container
		containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);

		// setup context
		context.put(StopContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StopContainerCommand.SESSION_KEY, session);
		context.put(StopContainerCommand.CONTAINER_INFO_KEY, containerInfo);
		context.put(StopContainerCommand.TIMEOUT_ID_KEY, CONTAINER_STOP_TIMEOUT);

		// execute command
		stopContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertFalse(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can stop paused container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStopPausedContainer() throws Exception {

		// create and start container
		containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		dockerHelper.pauseContainer(session, containerInfo);

		// test
		assertTrue(dockerClient.isContainerPaused(session, containerInfo));

		// setup context
		context.put(StopContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StopContainerCommand.SESSION_KEY, session);
		context.put(StopContainerCommand.CONTAINER_INFO_KEY, containerInfo);
		context.put(StopContainerCommand.TIMEOUT_ID_KEY, CONTAINER_STOP_TIMEOUT);

		// execute command
		stopContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertFalse(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command fails to stop unknown container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsToStopUnknownContainer() throws Exception {

		// create fake container info's
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
		containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);

		// setup context
		context.put(StopContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StopContainerCommand.SESSION_KEY, session);
		context.put(StopContainerCommand.CONTAINER_INFO_KEY, containerInfo);
		context.put(StopContainerCommand.TIMEOUT_ID_KEY, CONTAINER_STOP_TIMEOUT);

		// execute command
		stopContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isFailed());
	}

}
