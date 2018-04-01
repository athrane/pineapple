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

import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_CENTOS_REPOSITORY;
import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_BASE_UBUNTU_IMAGE;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_ROOT_BUSYBOX_IMAGE;
import static org.junit.Assert.*;

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
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.JsonMessage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain CreateImageCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class CreateImageCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command createImageCommand;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Random repository.
	 */
	String randomRepository;

	/**
	 * Random tag.
	 */
	String randomTag;

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
	 * Image info.
	 */
	ImageInfo imageInfo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomRepository = RandomStringUtils.randomAlphabetic(10).toLowerCase();
		randomTag = RandomStringUtils.randomAlphabetic(10);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// create session
		session = dockerHelper.createDefaultSession();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if (imageInfo != null)
			dockerHelper.deleteImage(session, imageInfo);
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(createImageCommand);
	}

	/**
	 * Test that command can create Ubuntu base image with empty tag. and return a
	 * single successful root result.
	 * 
	 * Due to the lack of tag, Docker with pull the tag with the "latest" tag and
	 * some house keeping is required at the end of the test to clean up properly.
	 */
	@SuppressWarnings("unchecked")
	// @Test
	public void testCreateUntaggedImageFromUbuntuBaseRepository() throws Exception {
		imageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_BASE_UBUNTU_IMAGE, "");
		ImageInfo actualImageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_BASE_UBUNTU_IMAGE, LATEST_IMAGE_TAG);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);

		// test that image is actually is created with the "latest" tag
		assertTrue(dockerClient.imageExists(session, actualImageInfo));

		// set actual image info (with the "latest" tag) for deletion
		imageInfo = actualImageInfo;
	}

	/**
	 * Test that command can create tagged Ubuntu base image and return a single
	 * successful root result.
	 */
	@SuppressWarnings("unchecked")
	// @Test
	public void testCreateTaggedImageFromUbuntuBaseRepository() throws Exception {
		imageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_BASE_UBUNTU_IMAGE, LATEST_IMAGE_TAG);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command can create tagged CentOS base image and return a single
	 * successful root result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedImageFromCentOsRepository() throws Exception {
		imageInfo = dockerInfoBuilder.buildImageInfo(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command can create untagged busybox image with empty tag. and
	 * return a single successful root result.
	 * 
	 * Due to the lack of tag, Docker with pull the tag with the "latest" tag and
	 * some house keeping is required at the end of the test to clean properly.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateUntaggedImageFromUserRepository() throws Exception {
		imageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, "");
		ImageInfo actualImageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);

		// test that image is actually is created with the "latest" tag
		assertTrue(dockerClient.imageExists(session, actualImageInfo));

		// set actual image info (with the "latest" tag) for deletion
		imageInfo = actualImageInfo;
	}

	/**
	 * Test that command can create tagged busybox image and return a single
	 * successful root result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedImageFromUserRepository() throws Exception {
		imageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command fails to create image from unknown repository.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsToCreateImageFromUnknownRepository() throws Exception {
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo(randomRepository, "latest");

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertFalse(executionResult.isSuccess());
		assertFalse(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertFalse(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command fails to create image with unknown tag.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsToCreateImageWithUnknownTag() throws Exception {
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_BASE_UBUNTU_IMAGE, randomTag);

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertFalse(executionResult.isSuccess());
		assertFalse(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertFalse(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command success if image already exists. A busybox:latest image is
	 * created prior to creating the image again.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsIfImageAlreadyExists() throws Exception {
		imageInfo = dockerHelper.createDefaultTinyImageInfo();
		dockerHelper.createImage(session, imageInfo);
		assertTrue(dockerClient.imageExists(session, imageInfo));

		// setup context
		context.put(CreateImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		createImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertEquals(0, infos.length);
	}

}
