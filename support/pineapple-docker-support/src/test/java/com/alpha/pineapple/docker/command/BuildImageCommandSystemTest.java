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
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_ROOT_BUSYBOX_IMAGE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.JsonMessage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain BuildImageCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class BuildImageCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command buildImageCommand;

	/**
	 * Current test directory.
	 */
	File testDirectory;

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
	 * Random archive.
	 */
	String randomArchive;

	/**
	 * Source directory for TAR archive.
	 */
	File sourceDirectory;

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
		randomArchive = RandomStringUtils.randomAlphabetic(10);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// create session
		session = dockerHelper.createDefaultSession();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create source directory for TAR archive
		sourceDirectory = new File(testDirectory, RandomStringUtils.randomAlphabetic(10));
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
		assertNotNull(buildImageCommand);
	}

	/**
	 * Test that command can create tagged CentOS base image and return a single
	 * successful root result.
	 * 
	 * New image isn't pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedCentosImage() throws Exception {

		// create image info for tagged built image
		imageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// create TAR archive
		ImageInfo imageInfo2 = dockerHelper.createDefaultCentOSImageInfo();
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArhive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
		dockerHelper.createTarArchive(sourceDirectory, tarArhive);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArhive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(false));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command can create tagged tiny base image and return a single
	 * successful root result.
	 * 
	 * New image isn't pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedTinyImage() throws Exception {

		// create image info for tagged built image
		imageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// create TAR archive
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
		dockerHelper.createTarArchive(sourceDirectory, tarArchive);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArchive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(false));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command can create tagged tiny base image and return a single
	 * successful root result.
	 * 
	 * New image is pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedTinyImageWithNewPulledImage() throws Exception {

		// create image info for tagged built image
		imageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// create TAR archive
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
		dockerHelper.createTarArchive(sourceDirectory, tarArchive);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArchive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(true));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command fails if TAR archive doesn't exist.
	 * 
	 * New image isn't pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandFailsIfTarArchiveDoesntExist() throws Exception {
		File tarArhive = new File("non-existing-tar-archive");

		imageInfo = dockerInfoBuilder.buildImageInfo(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArhive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(false));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isFailed());
	}

	/**
	 * Test that command can create tagged tiny base image and return a single
	 * successful root result.
	 * 
	 * New image isn't pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTaggedTinyImageWithMultipleLinesInDockerfile() throws Exception {

		// create image info for tagged built image
		imageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// create TAR archive
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromAndMaintainerCommands(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
		dockerHelper.createTarArchive(sourceDirectory, tarArchive);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArchive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(false));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		assertNotNull(context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));
		JsonMessage[] infos = (JsonMessage[]) context.get(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);
		assertTrue(infos.length != 0);
		assertTrue(dockerClient.imageExists(session, imageInfo));
	}

	/**
	 * Test that command fails if TAR archive is undefined in context.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfTarArchiveKeyIsUndefinedInContext() throws Exception {

		imageInfo = dockerInfoBuilder.buildImageInfo(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);

		// execute command
		buildImageCommand.execute(context);
	}

	/**
	 * Test that command fails if pull image is undefined in context.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfPullImageKeyIsUndefinedInContext() throws Exception {

		imageInfo = dockerInfoBuilder.buildImageInfo(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, randomArchive);

		// execute command
		buildImageCommand.execute(context);
	}

	/**
	 * Test that command succeeds if image already exists.
	 * 
	 * New image isn't pulled.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandIdempotent() throws Exception {

		// create image info for tagged built image
		imageInfo = dockerHelper.createDefaultTaggedImageInfo();

		// create TAR archive
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
		dockerHelper.createTarArchive(sourceDirectory, tarArchive);

		// setup context
		context.put(BuildImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArchive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, Boolean.valueOf(false));

		// execute command
		buildImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.imageExists(session, imageInfo));
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));

		// remove image creation infos
		context.remove(CreateImageCommand.IMAGE_CREATION_INFOS_KEY);

		// execute command
		buildImageCommand.execute(context);

		// execute command - twice
		assertTrue(executionResult.isSuccess());
		assertTrue(dockerClient.imageExists(session, imageInfo));
		assertTrue(context.containsKey(CreateImageCommand.IMAGE_CREATION_INFOS_KEY));

	}

}
