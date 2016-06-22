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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
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
 * System test of the class {@linkplain PauseContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class PauseContainerCommandSystemTest {

    /**
     * Object under test.
     */
    @Resource
    Command pauseContainerCommand;

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
     * Default image info (CentOS).
     */
    ImageInfo defaultImageInfo;

    /**
     * Tiny image info (Busybox).
     */
    ImageInfo tinyImageInfo;

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
	session = dockerHelper.createDefaultSessionWithLoggingInterceptor();

	// create image and info's
	defaultImageInfo = dockerHelper.createDefaultImage(session);
	tinyImageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerHelper.createImage(session, tinyImageInfo);
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
	assertNotNull(pauseContainerCommand);
    }

    /**
     * Test that command can pause container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandPauseContainer() throws Exception {
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
	dockerHelper.startContainer(session, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

    /**
     * Test that command can start container.
     */
    @SuppressWarnings("unchecked")
    // @Test
    public void testCommandPauseContainerPineappleHttpd() throws Exception {
	ImageInfo pineappleHttpdImage = dockerInfoBuilder.buildImageInfo("pineapple/httpd", "1.0");
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, pineappleHttpdImage);
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();
	dockerHelper.startContainer(session, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

    /**
     * Test that command can start busybox container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandPauseBusyboxContainer() throws Exception {
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, tinyImageInfo);
	containerInfo.getContainerConfiguration().getCmd().add("/bin/sh");
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();
	dockerHelper.startContainer(session, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

    /**
     * Test that command can start container.
     */
    @SuppressWarnings("unchecked")
    // @Test
    public void testCommandPauseContainerOracleLinux() throws Exception {
	ImageInfo oelImage = dockerInfoBuilder.buildImageInfo("oraclelinux", "6.6");
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, oelImage);
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

    /**
     * Test that command fails to pause unknown container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFailsToPauseUnknownContainer() throws Exception {
	// create fake container info's
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
	containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	assertTrue(executionResult.isFailed());
    }

    /**
     * Test that command can pause a stopped container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPauseStoppedContainer() throws Exception {
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
	containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

    /**
     * Test that command can pause paused container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandPausePausedContainer() throws Exception {
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
	containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
	dockerHelper.startContainer(session, containerInfo);
	dockerHelper.pauseContainer(session, containerInfo);

	// setup context
	context.put(PauseContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(PauseContainerCommand.SESSION_KEY, session);
	context.put(PauseContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	pauseContainerCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.isContainerPaused(session, containerInfo));
    }

}
