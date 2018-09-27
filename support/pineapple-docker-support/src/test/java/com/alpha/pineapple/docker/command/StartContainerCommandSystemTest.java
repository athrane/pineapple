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
 * System test of the class {@linkplain StartContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class StartContainerCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command startContainerCommand;

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
	 * Docker info objects builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Container instance info.
	 */
	ContainerInstanceInfo containerInstanceInfo;

	/**
	 * Container info.
	 */
	ContainerInfo containerInfo;

	/**
	 * Default image info (CentOS).
	 */
	ImageInfo defaultImageInfo;

	/**
	 * Tiny image info (Busybox).
	 */
	ImageInfo tinyImageInfo;

	/**
	 * Docker registry image info .
	 */
	ImageInfo registryImageInfo;

	/**
	 * Random container ID.
	 */
	String randomId;

	/**
	 * Random container name.
	 */
	String randomName;

	private ImageInfo registryImage;

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
		session = dockerHelper.createDefaultSessionWithLoggingInterceptor();

		// create image and info's
		defaultImageInfo = dockerHelper.createDefaultImage(session);
		tinyImageInfo = dockerHelper.createDefaultTinyImageInfo();
		dockerHelper.createImage(session, tinyImageInfo);
		registryImage = dockerHelper.createDefaultRegistryImageInfo();
		dockerHelper.createImage(session, registryImage);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		// delete container - if it exists
		if ((containerInstanceInfo != null) && (dockerClient.containerExists(session, containerInfo))) {
			dockerHelper.stopContainer(session, containerInfo);
			dockerHelper.deleteContainer(session, containerInfo);
		}
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(startContainerCommand);
	}

	/**
	 * Test that command can start container (CentOS).
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandStartContainer() throws Exception {

		// create container
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start container.
	 */
	@SuppressWarnings("unchecked")
	// @Test
	public void testCommandStartContainerPineappleHttpd() throws Exception {

		// create container
		ImageInfo pineappleHttpdImage = dockerInfoBuilder.buildImageInfo("pineapple/httpd", "1.0");
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, pineappleHttpdImage);
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start busybox container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandStartBusyboxContainer() throws Exception {

		// create container
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, tinyImageInfo);
		containerInfo.getContainerConfiguration().getCmd().add("/bin/sh");
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start container.
	 */
	@SuppressWarnings("unchecked")
	// @Test
	public void testCommandStartContainerOracleLinux() throws Exception {

		// create container
		ImageInfo oelImage = dockerInfoBuilder.buildImageInfo("oraclelinux", "6.6");
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, oelImage);
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command fails to start unknown container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsToStartUnknownContainer() throws Exception {

		// create fake container info's
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
		containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isFailed());
	}

	/**
	 * Test that command can start paused container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStartPausedContainer() throws Exception {

		// create and start container
		containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		dockerHelper.pauseContainer(session, containerInfo);
		assertTrue(dockerClient.isContainerPaused(session, containerInfo));

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start stopped container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStartStoppedContainer() throws Exception {

		// create and start container
		containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		dockerHelper.stopContainer(session, containerInfo);
		assertFalse(dockerClient.isContainerRunning(session, containerInfo));

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start started container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStartAlreadyStartedContainer() throws Exception {

		// create and start container
		containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
		containerInfo = containerInstanceInfo.getContainerInfo();
		dockerHelper.startContainer(session, containerInfo);
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

	/**
	 * Test that command can start container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandStartContainerDockerRegistry() throws Exception {

		// create container
		containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, registryImage);
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
		assertNotNull(containerInfo);

		// setup context
		context.put(StartContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		startContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.isContainerRunning(session, containerInfo));
	}

}
