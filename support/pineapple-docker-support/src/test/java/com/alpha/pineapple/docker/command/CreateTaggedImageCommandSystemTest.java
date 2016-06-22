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
 * System test of the class {@linkplain CreateTaggedImageCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class CreateTaggedImageCommandSystemTest {

    /**
     * Object under test.
     */
    @Resource
    Command createTaggedImageCommand;

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
     * Source image info.
     */
    ImageInfo sourceImageInfo;

    /**
     * Target image info.
     */
    ImageInfo targetImageInfo;

    /**
     * Target image info.
     */
    ImageInfo targetImageInfo2;

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
     * Random image.
     */
    String randomImage;

    /**
     * Random image.
     */
    String randomImage2;

    @Before
    public void setUp() throws Exception {
	randomRepository = RandomStringUtils.randomAlphabetic(10).toLowerCase();
	randomTag = RandomStringUtils.randomAlphabetic(10);
	randomImage = RandomStringUtils.randomAlphabetic(10).toLowerCase();
	randomImage2 = RandomStringUtils.randomAlphabetic(10).toLowerCase();

	// create context
	context = new ContextBase();

	// create execution result
	executionResult = new ExecutionResultImpl("root");

	// create session
	session = dockerHelper.createDefaultSession();

	// create image info's
	sourceImageInfo = dockerHelper.createDefaultTinyImageInfo();
	targetImageInfo = dockerHelper.createDefaultTaggedImageInfo();
	targetImageInfo2 = dockerHelper.createDefaultTaggedImageInfo();
    }

    @After
    public void tearDown() throws Exception {
	dockerHelper.deleteImage(session, targetImageInfo);
	dockerHelper.deleteImage(session, targetImageInfo2);
    }

    /**
     * Test that command instance can be created in application context.
     */
    @Test
    public void testCanGetInstance() throws Exception {
	assertNotNull(createTaggedImageCommand);
    }

    /**
     * Test that command can create tagged image and return a single successful
     * root result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTaggedImage() throws Exception {
	dockerHelper.createImage(session, sourceImageInfo);

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, sourceImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command can create tagged image from tagged image. and return a
     * single successful root result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCanTagTaggedImage() throws Exception {
	dockerHelper.createImage(session, sourceImageInfo);
	dockerHelper.tagImage(session, sourceImageInfo, targetImageInfo);

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, targetImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo2);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command can tag existing image with a successful result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTagImageIsIdempotent() throws Exception {
	dockerHelper.createImage(session, sourceImageInfo);
	dockerHelper.tagImage(session, sourceImageInfo, targetImageInfo);

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, sourceImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command can create tagged image from itself (e.g. the operation
     * is idempotent) and return a single successful root result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTagImageWithItselfIsIdempotent() throws Exception {
	dockerHelper.createImage(session, sourceImageInfo);
	dockerHelper.tagImage(session, sourceImageInfo, targetImageInfo);

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, targetImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
    }

    /**
     * Test that command can create tagged image with custom version tag.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCanTagTaggedImageWithCustomVersionedTag() throws Exception {
	dockerHelper.createImage(session, sourceImageInfo);
	targetImageInfo = dockerInfoBuilder.buildImageInfo(randomRepository, "9.9");

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, sourceImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(dockerClient.imageExists(session, targetImageInfo));

    }

    /**
     * Test that command fails if source image doesn't exists and return a
     * failed result.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFailsIfSourceImageDoesntExists() throws Exception {
	sourceImageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	targetImageInfo = dockerInfoBuilder.buildImageInfo(randomImage2, LATEST_IMAGE_TAG);

	// setup context
	context.put(CreateTaggedImageCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(CreateTaggedImageCommand.SESSION_KEY, session);
	context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, sourceImageInfo);
	context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

	// execute command
	createTaggedImageCommand.execute(context);

	// test
	assertTrue(executionResult.isFailed());
    }

}
