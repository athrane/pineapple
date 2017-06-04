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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.NetworkUtils;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationExposedPortsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationHostConfigPortBindingsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationLabelsMap;
import com.alpha.pineapple.docker.model.jaxb.InspectedContainerVolumesMap;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfig;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfigPortBindingValue;
import com.alpha.pineapple.docker.model.rest.ContainerJson;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.docker.utils.RestResponseException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain CreateContainerCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class CreateContainerCommandSystemTest {

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Docker object factory.
	 */
	static final ObjectFactory dockerModelObjectFactory = new ObjectFactory();

	/**
	 * Object under test.
	 */
	@Resource
	Command createContainerCommand;

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
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Random value.
	 */
	String randomValue3;

	/**
	 * Random value.
	 */
	String randomValue4;

	/**
	 * Random repository.
	 */
	String randomRepository;

	/**
	 * Random tag.
	 */
	String randomTag;

	/**
	 * Random container ID.
	 */
	String randomId;

	/**
	 * Random port.
	 */
	int randomPort;

	/**
	 * Random port.
	 */
	int randomPort2;

	/**
	 * Random
	 */
	Random random;

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
	 * Default image info (CentOS).
	 */
	ImageInfo defaultImageInfo;

	/**
	 * Container info for linked container.
	 */
	ContainerInfo linkedContainerInfo;

	/**
	 * Container info for linked container.
	 */
	ContainerInfo linkedContainerInfo2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random();
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);
		randomValue3 = RandomStringUtils.randomAlphabetic(10);
		randomValue4 = RandomStringUtils.randomAlphabetic(10);
		randomRepository = RandomStringUtils.randomAlphabetic(10);
		randomTag = RandomStringUtils.randomAlphabetic(10);
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomPort = NetworkUtils.getRandomPort(random);
		randomPort2 = NetworkUtils.getRandomPort(random);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// create session
		session = dockerHelper.createDefaultSessionWithLoggingInterceptor();

		// clear container info
		containerInstanceInfo = null;
		linkedContainerInfo = null;
		linkedContainerInfo2 = null;

		// create image and info's
		defaultImageInfo = dockerHelper.createDefaultImage(session);
	}

	/***
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// delete container
		if (containerInstanceInfo == null)
			return;
		dockerHelper.deleteContainer(session, containerInstanceInfo.getContainerInfo());

		if (linkedContainerInfo != null) {
			dockerHelper.deleteContainer(session, linkedContainerInfo);
		}
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(createContainerCommand);
	}

	/**
	 * Test that command can create container image with minimal configuration
	 * (image only) and return a single successful root result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCreateContainerWithMinimalConfiguration() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExistsQueryById(session, containerInstanceInfo));
	}

	/**
	 * Test that command can create container from tagged image and return a
	 * single successful root result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCreateContainerFromTaggedImage() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container ID for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExistsQueryById(session, containerInstanceInfo));
	}

	/**
	 * Test that command fails to create container from unknown image.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RestResponseException.class)
	public void testFailsToCreateContainerFromUnknownImage() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo(randomRepository, randomTag);
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);
	}

	/**
	 * Test that command can created container has expected name.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatedContainerHasExpectedName() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		assertEquals("/" + randomId, inspectedContainer.getName());
	}

	/**
	 * Test that command can created container has expected default values.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatedContainerHasExpectedDefaultValues() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
	}

	/**
	 * Test that command succeeds to create container with the name of an
	 * existing container.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCreateContainerWithExistingName() throws Exception {

		// create container info
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// create container
		containerInstanceInfo = dockerHelper.createContainer(session, containerInfo);
		assertTrue(dockerClient.containerExists(session, containerInfo));

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		assertTrue(executionResult.isSuccess());
		assertFalse(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));
	}

	/**
	 * Test that command can create container with one environment variable.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneEnvironmentVariable() throws Exception {
		String name = "K" + randomValue;
		String value = "V" + randomValue;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addEnvironmentVariable(name, value);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		List<String> envs = inspectedContainer.getConfig().getEnv();
		assertNotNull(envs);
		assertEquals(2, envs.size());
		assertEquals(name + "=" + value, envs.get(0));
	}

	/**
	 * Test that command can create container with two environment variables.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoEnvironmentVariables() throws Exception {
		String name = "K" + randomValue;
		String value = "V" + randomValue;
		String name2 = "K" + randomValue2;
		String value2 = "V" + randomValue2;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addEnvironmentVariable(name, value);
		containerInfo.addEnvironmentVariable(name2, value2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		List<String> envs = inspectedContainer.getConfig().getEnv();
		assertNotNull(envs);
		assertEquals(3, envs.size());
		assertEquals(name + "=" + value, envs.get(0));
		assertEquals(name2 + "=" + value2, envs.get(1));
	}

	/**
	 * Test that container is created with default command.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithWithDefaultCommand() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration actualConfiguration = inspectedContainer.getConfig();
		assertNotNull(actualConfiguration);
		List<String> cmds = actualConfiguration.getCmd();
		assertNotNull(cmds);
		assertEquals(1, cmds.size());
		assertEquals(DUMMY_COMMAND, cmds.get(0));
	}

	/**
	 * Test that command can create container with one exposed TCP port.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneExposedTcpPort() throws Exception {
		final int PORT_NUMBER = 1024;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addExposedTcpPort(PORT_NUMBER);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration actualConfiguration = inspectedContainer.getConfig();
		assertNotNull(actualConfiguration);
		ContainerConfigurationExposedPortsMap exposedPorts = actualConfiguration.getExposedPorts();
		assertNotNull(exposedPorts);
		assertEquals(1, exposedPorts.size());
		assertTrue(exposedPorts.containsKey(containerInfo.createPortString(PORT_NUMBER, "tcp")));
	}

	/**
	 * Test that command can create container with one exposed UDP port.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneExposedUdpPort() throws Exception {
		final int PORT_NUMBER = 1024;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addExposedUdpPort(PORT_NUMBER);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration actualConfiguration = inspectedContainer.getConfig();
		assertNotNull(actualConfiguration);
		ContainerConfigurationExposedPortsMap exposedPorts = actualConfiguration.getExposedPorts();
		assertNotNull(exposedPorts);
		assertEquals(1, exposedPorts.size());
		assertTrue(exposedPorts.containsKey(containerInfo.createPortString(PORT_NUMBER, "udp")));
	}

	/**
	 * Test that command can create container with two exposed TCP ports.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoExposedTcpPort() throws Exception {
		final int PORT_NUMBER = 1024;
		final int PORT_NUMBER2 = 1025;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addExposedTcpPort(PORT_NUMBER);
		containerInfo.addExposedTcpPort(PORT_NUMBER2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration actualConfiguration = inspectedContainer.getConfig();
		assertNotNull(actualConfiguration);
		ContainerConfigurationExposedPortsMap exposedPorts = actualConfiguration.getExposedPorts();
		assertNotNull(exposedPorts);
		assertEquals(2, exposedPorts.size());
		assertTrue(exposedPorts.containsKey(containerInfo.createPortString(PORT_NUMBER, "tcp")));
		assertTrue(exposedPorts.containsKey(containerInfo.createPortString(PORT_NUMBER2, "tcp")));
	}

	/**
	 * Test that command can create container with one volume.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneVolume() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addVolume("/tmp");

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		InspectedContainerVolumesMap volumes = inspectedContainer.getVolumes();
		assertNotNull(volumes);
		assertEquals(1, volumes.size());
		assertTrue(volumes.containsKey("/tmp"));
	}

	/**
	 * Test that command can create container with two volumes.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoVolumes() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addVolume("/tmp");
		containerInfo.addVolume("/beast");

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		InspectedContainerVolumesMap volumes = inspectedContainer.getVolumes();
		assertNotNull(volumes);
		assertEquals(2, volumes.size());
		assertTrue(volumes.containsKey("/tmp"));
		assertTrue(volumes.containsKey("/beast"));
	}

	/**
	 * Test that command can create container with zero labels. The CentOS image
	 * will contain two image by default.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithZeroLabels() throws Exception {
		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		// add no labels

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationLabelsMap labels = inspectedContainer.getConfig().getLabels();
		assertNotNull(labels);
		assertEquals(4, labels.size());
	}

	/**
	 * Test that command can create container with one label.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneLabel() throws Exception {
		String key = "K" + randomValue;
		String value = "V" + randomValue;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addLabel(key, value);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationLabelsMap labels = inspectedContainer.getConfig().getLabels();
		;
		assertNotNull(labels);
		assertEquals(1 + 4, labels.size()); // +4 labels from the CentOS image
		assertTrue(labels.containsKey(key));
		assertEquals(value, labels.get(key));
	}

	/**
	 * Test that command can create container with two labels.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoLabels() throws Exception {
		String key = "K" + randomValue;
		String value = "V" + randomValue;
		String key2 = "K" + randomValue2;
		String value2 = "V" + randomValue2;

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addLabel(key, value);
		containerInfo.addLabel(key2, value2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationLabelsMap labels = inspectedContainer.getConfig().getLabels();
		;
		assertNotNull(labels);
		assertEquals(2 + 4, labels.size()); // +4 labels from the CentsOS image
		assertTrue(labels.containsKey(key));
		assertEquals(value, labels.get(key));
		assertTrue(labels.containsKey(key2));
		assertEquals(value2, labels.get(key2));
	}

	/**
	 * Test that command can create container with one TCP port binding.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneTcpPortBinding() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addTcpPortBinding(randomPort, randomPort);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationHostConfigPortBindingsMap portBindings = inspectedContainer.getHostConfig()
				.getPortBindings();
		assertNotNull(portBindings);
		assertEquals(1, portBindings.size());
		String bindingKey = containerInfo.createPortString(randomPort, "tcp");
		assertTrue(portBindings.containsKey(bindingKey));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray = portBindings.get(bindingKey);
		assertEquals(1, bindingArray.length);
		assertEquals("", bindingArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort), bindingArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that command can create container with one UDP port binding.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneUdpPortBinding() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addUdpPortBinding(randomPort, randomPort);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationHostConfigPortBindingsMap portBindings = inspectedContainer.getHostConfig()
				.getPortBindings();
		assertNotNull(portBindings);
		assertEquals(1, portBindings.size());
		String bindingKey = containerInfo.createPortString(randomPort, "udp");
		assertTrue(portBindings.containsKey(bindingKey));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray = portBindings.get(bindingKey);
		assertEquals(1, bindingArray.length);
		assertEquals("", bindingArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort), bindingArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that command can create container with two TCP port bindings.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoTcpPortBindings() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addTcpPortBinding(randomPort, randomPort);
		containerInfo.addTcpPortBinding(randomPort2, randomPort2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationHostConfigPortBindingsMap portBindings = inspectedContainer.getHostConfig()
				.getPortBindings();
		assertNotNull(portBindings);
		assertEquals(2, portBindings.size());
		String bindingKey = containerInfo.createPortString(randomPort, "tcp");
		assertTrue(portBindings.containsKey(bindingKey));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray = portBindings.get(bindingKey);
		assertEquals(1, bindingArray.length);
		assertEquals("", bindingArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort), bindingArray[FIRST_INDEX].getHostPort());
		String bindingKey2 = containerInfo.createPortString(randomPort2, "tcp");
		assertTrue(portBindings.containsKey(bindingKey2));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray2 = portBindings.get(bindingKey2);
		assertEquals(1, bindingArray2.length);
		assertEquals("", bindingArray2[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort2), bindingArray2[FIRST_INDEX].getHostPort());

	}

	/**
	 * Test that command can create container with one UDP port binding with
	 * different host and container port.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneUdpPortBindingWithDifferentHostAndContainerPort() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addUdpPortBinding(randomPort, randomPort2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationHostConfigPortBindingsMap portBindings = inspectedContainer.getHostConfig()
				.getPortBindings();
		assertNotNull(portBindings);
		assertEquals(1, portBindings.size());
		String bindingKey = containerInfo.createPortString(randomPort, "udp");
		assertTrue(portBindings.containsKey(bindingKey));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray = portBindings.get(bindingKey);
		assertEquals(1, bindingArray.length);
		assertEquals("", bindingArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort2), bindingArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that command can create container with two identical UDP port
	 * bindings. The second binding is ignored
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoIdenticalUdpPortBindings() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		containerInfo.addUdpPortBinding(randomPort, randomPort);
		containerInfo.addUdpPortBinding(randomPort, randomPort);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfigurationHostConfigPortBindingsMap portBindings = inspectedContainer.getHostConfig()
				.getPortBindings();
		assertNotNull(portBindings);
		assertEquals(1, portBindings.size());
		String bindingKey = containerInfo.createPortString(randomPort, "udp");
		assertTrue(portBindings.containsKey(bindingKey));
		ContainerConfigurationHostConfigPortBindingValue[] bindingArray = portBindings.get(bindingKey);
		assertEquals(1, bindingArray.length);
		assertEquals("", bindingArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(randomPort), bindingArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that command can create container with one entry point.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneEntryPoint() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		ContainerConfiguration config = containerInfo.getContainerConfiguration();
		List<String> entryPointList = config.getEntrypoint();
		entryPointList.add("/bin/true");

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		List<String> inspectedEntryPoint = containerConfig.getEntrypoint();
		assertNotNull(inspectedEntryPoint);
		assertEquals(1, inspectedEntryPoint.size());
	}

	/**
	 * Test that command can create container with two entry points.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoEntryPoints() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		ContainerConfiguration config = containerInfo.getContainerConfiguration();
		List<String> entryPointList = config.getEntrypoint();
		entryPointList.add("ls");
		entryPointList.add("-l");

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		List<String> inspectedEntryPoint = containerConfig.getEntrypoint();
		assertNotNull(inspectedEntryPoint);
		assertEquals(2, inspectedEntryPoint.size());
	}

	/**
	 * Test that command can create container with zero commands. The result
	 * command is inherited from the CMD directive in the CentOS Dockerfile.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithZeroCmds() throws Exception {

		// create container info - with zero commands
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		List<String> inspectedCmds = containerConfig.getCmd();
		assertNotNull(inspectedCmds);
		assertEquals(1, inspectedCmds.size());
	}

	/**
	 * Test that command can create Registry container with zero commands. The
	 * result command is inherited from the CMD directive in the registry
	 * Dockerfile.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithZeroCmds2() throws Exception {

		// create container info - with zero commands
		ImageInfo imageInfo = dockerHelper.createDefaultRegistryImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		List<String> inspectedCmds = containerConfig.getCmd();
		assertNotNull(inspectedCmds);
		assertEquals(2, inspectedCmds.size());
	}

	/**
	 * Test that command can create container with one link.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithOneLink() throws Exception {
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		linkedContainerInfo = dockerInfoBuilder.buildContainerInfo(randomValue, imageInfo);
		dockerHelper.createContainer(session, linkedContainerInfo);

		// create container info - with one exposed port
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		ContainerConfiguration config = containerInfo.getContainerConfiguration();
		if (config.getHostConfig() == null)
			config.setHostConfig(dockerModelObjectFactory.createContainerConfigurationHostConfig());
		ContainerConfigurationHostConfig hostConfig = config.getHostConfig();
		List<String> linkList = hostConfig.getLinks();
		linkList.add(randomValue + ":" + randomValue2);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		ContainerConfigurationHostConfig inspectedHostConfig = inspectedContainer.getHostConfig();
		List<String> inspectedLinks = inspectedHostConfig.getLinks();
		assertNotNull(inspectedLinks);
		assertEquals(1, inspectedLinks.size());
	}

	/**
	 * Test that command can create container with two links.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandCanCreateContainerWithTwoLinks() throws Exception {
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		linkedContainerInfo = dockerInfoBuilder.buildContainerInfo(randomValue, imageInfo);
		dockerHelper.createContainer(session, linkedContainerInfo);
		linkedContainerInfo2 = dockerInfoBuilder.buildContainerInfo(randomValue3, imageInfo);
		dockerHelper.createContainer(session, linkedContainerInfo2);

		// create container info - with one exposed port
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		ContainerConfiguration config = containerInfo.getContainerConfiguration();
		if (config.getHostConfig() == null)
			config.setHostConfig(dockerModelObjectFactory.createContainerConfigurationHostConfig());
		ContainerConfigurationHostConfig hostConfig = config.getHostConfig();
		List<String> linkList = hostConfig.getLinks();
		linkList.add(randomValue + ":" + randomValue2);
		linkList.add(randomValue3 + ":" + randomValue4);

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY));

		// store container instance info for deletion
		containerInstanceInfo = (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);

		// test
		assertNotNull(containerInstanceInfo);
		assertTrue(dockerClient.containerExists(session, containerInstanceInfo.getContainerInfo()));
		ContainerJson inspectedContainer = dockerHelper.inspectContainer(session, containerInfo);
		assertNotNull(inspectedContainer);
		ContainerConfiguration containerConfig = inspectedContainer.getConfig();
		assertNotNull(containerConfig);
		ContainerConfigurationHostConfig inspectedHostConfig = inspectedContainer.getHostConfig();
		List<String> inspectedLinks = inspectedHostConfig.getLinks();
		assertNotNull(inspectedLinks);
		assertEquals(2, inspectedLinks.size());
	}

	/**
	 * Test that command fails to create link to unknwon container.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RestResponseException.class)
	public void testCommandFailsToCreateLinkToUnknownContainer() throws Exception {

		// create container info - with one exposed port
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(randomId, imageInfo);
		ContainerConfiguration config = containerInfo.getContainerConfiguration();
		if (config.getHostConfig() == null)
			config.setHostConfig(dockerModelObjectFactory.createContainerConfigurationHostConfig());
		ContainerConfigurationHostConfig hostConfig = config.getHostConfig();
		List<String> linkList = hostConfig.getLinks();
		linkList.add("alpha01:httpd1");

		// setup context
		context.put(CreateContainerCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, containerInfo);

		// execute command
		createContainerCommand.execute(context);
	}

}
