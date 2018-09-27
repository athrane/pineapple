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

import static com.alpha.pineapple.docker.command.DeleteContainerCommand.CONTAINER_INFO_KEY;
import static com.alpha.pineapple.docker.command.DeleteContainerCommand.EXECUTIONRESULT_KEY;
import static com.alpha.pineapple.docker.command.DeleteContainerCommand.SESSION_KEY;
import static org.junit.Assert.*;

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

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain DeleteContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class DeleteContainerCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command deleteContainerCommand;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random container name.
	 */
	String randomName;

	/**
	 * Random container ID.
	 */
	String randomId;

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
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomId = RandomStringUtils.randomAlphabetic(10);

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

		// delete tagged image - if it exists
		dockerHelper.deleteImage(session, taggedImageInfo);
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(deleteContainerCommand);
	}

	/**
	 * Test that command can delete container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteContainer() throws Exception {

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, randomName, defaultImageInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command can delete container created of tagged image.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteContainerCreateFromTaggedImage() throws Exception {

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, randomName, taggedImageInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command succeeds in deleting unknown container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsInDeletingUnknownContainer() throws Exception {

		// create fake container info's
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
		containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command can delete running container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteRunningContainer() throws Exception {

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, randomName, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command can delete stopped container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteStoppedContainer() throws Exception {

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, randomName, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		dockerHelper.stopContainer(session, containerInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command can delete paused container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeletePausedContainer() throws Exception {

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, randomName, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		dockerHelper.pauseContainer(session, containerInfo);

		// setup context
		context.put(EXECUTIONRESULT_KEY, executionResult);
		context.put(SESSION_KEY, session);
		context.put(CONTAINER_INFO_KEY, containerInstanceInfo.getContainerInfo());

		// execute command
		deleteContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

}
