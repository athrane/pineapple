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

import static com.alpha.pineapple.docker.DockerConstants.DUMMY_COMMAND;
import static com.alpha.pineapple.docker.command.InspectContainerCommand.CONTAINER_INFO_KEY;
import static com.alpha.pineapple.docker.command.InspectContainerCommand.EXECUTIONRESULT_KEY;
import static com.alpha.pineapple.docker.command.InspectContainerCommand.SESSION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.InspectedContainer;
import com.alpha.pineapple.docker.model.rest.InspectedContainerConfiguration;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.docker.utils.RestResponseException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain InspectContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class InspectContainerCommandSystemTest {

    /**
     * Object under test.
     */
    @Resource
    Command inspectContainerCommand;

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
     * Container info.
     */
    ContainerInfo containerInfo;

    /**
     * Container instance info.
     */
    ContainerInstanceInfo containerInstanceInfo;

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

	// clear container info
	containerInfo = null;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

	// delete container - if it exists
	if ((containerInfo != null) && (dockerHelper.containerExists(session, containerInfo))) {
	    dockerHelper.stopContainer(session, containerInfo);
	    dockerHelper.deleteContainer(session, containerInfo);
	}

	// delete tagged image - if it exist
	dockerHelper.deleteImage(session, taggedImageInfo);
    }

    /**
     * Test that command instance can be created in application context.
     */
    @Test
    public void testCanGetInstance() throws Exception {
	assertNotNull(inspectContainerCommand);
    }

    /**
     * Assert that command execution was successful and returned defined
     * inspection state.
     */
    void assertCommandWasSuccessfulWithDefinedInspectionState() {
	assertTrue(executionResult.isSuccess());
	assertTrue(context.containsKey(InspectContainerCommand.INSPECTED_CONTAINER_KEY));
	assertNotNull(context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY));
    }

    /**
     * Test that command can inspect container.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCanInspectContainer() throws Exception {

	// create container
	containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertCommandWasSuccessfulWithDefinedInspectionState();
    }

    /**
     * Test that dormant container returns running state to be false.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDormantContainerReturnsRunningIsFalse() throws Exception {

	// create container
	containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertCommandWasSuccessfulWithDefinedInspectionState();
	InspectedContainer info = (InspectedContainer) context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY);
	assertEquals(false, info.getState().isRunning());
    }

    /**
     * Test that running container returns running state to be true.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRunningContainerReturnsRunningIsTrue() throws Exception {
	// create container
	containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// start container
	dockerHelper.startContainer(session, containerInfo);

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertCommandWasSuccessfulWithDefinedInspectionState();
	InspectedContainer info = (InspectedContainer) context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY);
	assertEquals(true, info.getState().isRunning());
    }

    /**
     * Test that command fails to inspect unknown container.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = RestResponseException.class)
    public void testFailsToInspectUnknownContainer() throws Exception {

	// create fake container info's
	containerInfo = dockerInfoBuilder.buildContainerInfo(randomName, defaultImageInfo);
	containerInstanceInfo = dockerInfoBuilder.buildInstanceInfo(randomId, containerInfo);

	// setup context
	context.put(InspectContainerCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(InspectContainerCommand.SESSION_KEY, session);
	context.put(InspectContainerCommand.CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertFalse(executionResult.isSuccess());
    }

    /**
     * Test that container built from default image (CentOS) returns expected
     * name.
     * 
     * The name is prefixed with a "/".
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testContainerReturnsExpectedName() throws Exception {
	// create container
	containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertCommandWasSuccessfulWithDefinedInspectionState();
	InspectedContainer info = (InspectedContainer) context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY);
	assertEquals("/" + randomId, info.getName());
    }

    /**
     * Test that container built from default image (CentOS) returns expected
     * commands (in the container configuration object).
     * 
     * The name is prefixed with a "/".
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testContainerReturnsExpectedConfigCmds() throws Exception {
	// create container
	containerInstanceInfo = dockerHelper.createContainer(session, randomId, defaultImageInfo);
	containerInfo = containerInstanceInfo.getContainerInfo();

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(CONTAINER_INFO_KEY, containerInfo);

	// execute command
	inspectContainerCommand.execute(context);

	// test
	assertCommandWasSuccessfulWithDefinedInspectionState();
	InspectedContainer info = (InspectedContainer) context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY);
	assertNotNull(info.getConfig());
	InspectedContainerConfiguration configuration = info.getConfig();
	List<String> cmds = configuration.getCmd();
	assertNotNull(cmds);
	assertEquals(1, cmds.size());
	assertEquals(DUMMY_COMMAND, cmds.get(0));
    }

}
