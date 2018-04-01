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

import static org.junit.Assert.assertFalse;
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
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain DeleteImageCommand}.
 * 
 * Please notice: The "default tiny image" is used for testing to speed up
 * download times.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class DeleteImageCommandSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command deleteImageCommand;

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
	 * Base image info.
	 */
	ImageInfo baseImageInfo;

	/**
	 * Tagged image info.
	 */
	ImageInfo taggedImageInfo;

	/**
	 * Docker info objects builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Random repository.
	 */
	String randomRepository;

	/**
	 * Random tag.
	 */
	String randomTag;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomRepository = RandomStringUtils.randomAlphabetic(10).toLowerCase();
		randomTag = RandomStringUtils.randomNumeric(2);

		// create context
		context = new ContextBase();

		// create execution result
		executionResult = new ExecutionResultImpl("root");

		// create session
		session = dockerHelper.createDefaultSession();

		// create image info's
		baseImageInfo = dockerHelper.createDefaultTinyImageInfo();
		taggedImageInfo = dockerHelper.createDefaultTaggedImageInfo();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dockerHelper.deleteImage(session, baseImageInfo);
	}

	/**
	 * Test that command instance can be created in application context.
	 */
	@Test
	public void testCanGetInstance() throws Exception {
		assertNotNull(deleteImageCommand);
	}

	/**
	 * Test that command can delete base image and returns single successful root
	 * result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteImageFromBaseRepository() throws Exception {
		dockerHelper.createImage(session, baseImageInfo);
		assertTrue(dockerClient.imageExists(session, baseImageInfo));

		// setup context
		context.put(DeleteImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(DeleteImageCommand.SESSION_KEY, session);
		context.put(DeleteImageCommand.IMAGE_INFO_KEY, baseImageInfo);

		// execute command
		deleteImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertFalse(dockerClient.imageExists(session, baseImageInfo));
	}

	/**
	 * Test that command can delete tagged image in user repository and returns
	 * single successful root result.
	 * 
	 * Please notice: The "default tiny image" is used for testing.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteImageFromUserRepository() throws Exception {
		dockerHelper.createImage(session, baseImageInfo);
		dockerHelper.tagImage(session, baseImageInfo, taggedImageInfo);
		assertTrue(dockerClient.imageExists(session, taggedImageInfo));

		// setup context
		context.put(DeleteImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(DeleteImageCommand.SESSION_KEY, session);
		context.put(DeleteImageCommand.IMAGE_INFO_KEY, taggedImageInfo);

		// execute command
		deleteImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertFalse(dockerClient.imageExists(session, taggedImageInfo));
	}

	/**
	 * Test that command succeeds in deletion of unknown image from the base
	 * repository.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSucceedsInDeletionOfUnknownImageFromBaseRepository() throws Exception {

		// fails test if image already exists
		assertFalse(dockerClient.imageExists(session, taggedImageInfo));

		// setup context
		context.put(DeleteImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(DeleteImageCommand.SESSION_KEY, session);
		context.put(DeleteImageCommand.IMAGE_INFO_KEY, taggedImageInfo);

		// execute command
		deleteImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
	}

	/**
	 * Test that command can delete tagged image with a versioned tag and returns
	 * single successful root result.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteImageWithVersionedTag() throws Exception {
		dockerHelper.createImage(session, baseImageInfo);
		taggedImageInfo = dockerInfoBuilder.buildImageInfo(randomRepository, randomTag);
		dockerHelper.tagImage(session, baseImageInfo, taggedImageInfo);
		assertTrue(dockerClient.imageExists(session, taggedImageInfo));

		// setup context
		context.put(DeleteImageCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(DeleteImageCommand.SESSION_KEY, session);
		context.put(DeleteImageCommand.IMAGE_INFO_KEY, taggedImageInfo);

		// execute command
		deleteImageCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		assertFalse(dockerClient.imageExists(session, taggedImageInfo));
	}

}
