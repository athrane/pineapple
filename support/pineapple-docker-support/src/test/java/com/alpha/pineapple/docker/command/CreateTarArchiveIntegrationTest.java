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

import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_ROOT_BUSYBOX_IMAGE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
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

import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.test.matchers.PineappleMatchers;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.DockerTestHelper;

/**
 * Integration test of the class {@linkplain CreateTarArchiveCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class CreateTarArchiveIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command createTarArchiveCommand;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Source directory for TAR archive.
	 */
	File sourceDirectory;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

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
	 * Random archive.
	 */
	String randomArchive;

	/**
	 * Random source directory.
	 */
	String randomSourceDirectoryName;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomArchive = RandomStringUtils.randomAlphabetic(10);
		randomSourceDirectoryName = RandomStringUtils.randomAlphabetic(10);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create source directory for TAR archive
		sourceDirectory = new File(testDirectory, randomSourceDirectoryName);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(createTarArchiveCommand);
	}

	/**
	 * Test that TAR archive can be created.
	 */
	@Test
	public void testCanCreateArchive() throws Exception {

		// create source directory with single docker file
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);

		// create context
		ContextBase context = new ContextBase();
		context.put(CreateTarArchiveCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateTarArchiveCommand.SOURCE_DIRECTORY_KEY, sourceDirectory);
		context.put(CreateTarArchiveCommand.TAR_ARCHIVE_KEY, tarArchive);

		// execute command
		createTarArchiveCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(PineappleMatchers.doesFileExist().matches(tarArchive));
	}

	/**
	 * Test that multiple TAR archive can be created in succession.
	 */
	@Test
	public void testCanCreateMultipleArchivesInSuccession() throws Exception {

		// create source directory with single docker file
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(TEST_DOCKER_ROOT_BUSYBOX_IMAGE, LATEST_IMAGE_TAG);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);

		// create context
		ContextBase context = new ContextBase();
		context.put(CreateTarArchiveCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateTarArchiveCommand.SOURCE_DIRECTORY_KEY, sourceDirectory);
		context.put(CreateTarArchiveCommand.TAR_ARCHIVE_KEY, tarArchive);

		// execute command
		createTarArchiveCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(PineappleMatchers.doesFileExist().matches(tarArchive));

		// execute command
		createTarArchiveCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertTrue(PineappleMatchers.doesFileExist().matches(tarArchive));
	}

	/**
	 * Test that archive creation fails if source directory doesn't exist.
	 */
	@Test
	public void testCreationFailsIfSourceDirectoryDoesntExist() throws Exception {
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);

		// create context
		ContextBase context = new ContextBase();
		context.put(CreateTarArchiveCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateTarArchiveCommand.SOURCE_DIRECTORY_KEY, sourceDirectory);
		context.put(CreateTarArchiveCommand.TAR_ARCHIVE_KEY, tarArchive);

		// execute command
		createTarArchiveCommand.execute(context);

		// test
		assertFalse(executionResult.isSuccess());
		assertFalse(PineappleMatchers.doesFileExist().matches(tarArchive));
	}

	/**
	 * Test that archive creation fails if source directory isn't a directory.
	 */
	@Test
	public void testCreationFailsIfSourceDirectoryIsntADirectory() throws Exception {
		File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);

		// create file with source directory name
		Collection<String> lines = new ArrayList<String>();
		lines.add("MAINTAINER Pineapple");
		FileUtils.writeLines(sourceDirectory, lines);

		// create context
		ContextBase context = new ContextBase();
		context.put(CreateTarArchiveCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateTarArchiveCommand.SOURCE_DIRECTORY_KEY, sourceDirectory);
		context.put(CreateTarArchiveCommand.TAR_ARCHIVE_KEY, tarArchive);

		// execute command
		createTarArchiveCommand.execute(context);

		// test
		assertFalse(executionResult.isSuccess());
		assertFalse(PineappleMatchers.doesFileExist().matches(tarArchive));
	}

}
