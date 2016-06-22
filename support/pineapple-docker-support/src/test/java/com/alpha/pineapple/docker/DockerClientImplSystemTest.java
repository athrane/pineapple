/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.docker;

import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

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

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ContainerState;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.test.matchers.PineappleMatchers;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the {@linkplain DockerClientImpl} class.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class DockerClientImplSystemTest {

    /**
     * Subject under test.
     */
    @Resource
    DockerClient dockerClient;

    /**
     * Docker test helper.
     */
    @Resource
    DockerTestHelper dockerHelper;

    /**
     * Docker info object builder.
     */
    @Resource
    InfoBuilder dockerInfoBuilder;

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Source directory for TAR archive.
     */
    File sourceDirectory;

    /**
     * Docker session.
     */
    DockerSession session;

    /**
     * Execution result.
     */
    ExecutionResult executionResult;

    /**
     * Image info.
     */
    ImageInfo imageInfo;

    /**
     * Image info.
     */
    ImageInfo imageInfo2;

    /**
     * Random image.
     */
    String randomImage;

    /**
     * Random container name.
     */
    String randomContainerName;

    /**
     * Random image.
     */
    String randomImage2;

    /**
     * Random archive.
     */
    String randomArchive;

    /**
     * Random source directory.
     */
    String randomSourceDirectoryName;

    /**
     * Default image.
     */
    ImageInfo defaultImage;

    /**
     * Container instance info.
     */
    ContainerInstanceInfo containerInstanceInfo;

    @Before
    public void setUp() throws Exception {
	randomImage = RandomStringUtils.randomAlphabetic(10).toLowerCase();
	randomImage2 = RandomStringUtils.randomAlphabetic(10).toLowerCase();
	randomContainerName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
	randomArchive = RandomStringUtils.randomAlphabetic(10);
	randomSourceDirectoryName = RandomStringUtils.randomAlphabetic(10);

	// create execution result
	executionResult = new ExecutionResultImpl("root");

	// create session
	session = dockerHelper.createDefaultSession();

	// create default image
	defaultImage = dockerHelper.createDefaultImage(session);

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// create source directory for TAR archive
	sourceDirectory = new File(testDirectory, randomSourceDirectoryName);
    }

    @After
    public void tearDown() throws Exception {
	if (containerInstanceInfo != null) {
	    ContainerInfo info = containerInstanceInfo.getContainerInfo();
	    if (dockerClient.containerExists(session, info)) {
		if (dockerClient.isContainerRunning(session, containerInstanceInfo))
		    dockerHelper.stopContainer(session, info);
		dockerHelper.deleteContainer(session, containerInstanceInfo.getContainerInfo());
	    }
	}

	if (imageInfo != null) {
	    if (dockerClient.imageExists(session, imageInfo))
		dockerHelper.deleteImage(session, imageInfo);
	}
	if (imageInfo2 != null) {
	    if (dockerClient.imageExists(session, imageInfo2))
		dockerHelper.deleteImage(session, imageInfo2);
	}
    }

    /**
     * Test that client instance can be created in application context.
     */
    @Test
    public void testCanGetInstance() throws Exception {
	assertNotNull(dockerClient);
    }

    /**
     * Test that client returns true if image exists.
     */
    @Test
    public void testImageExistReturnsTrueIfImageExists() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerClient.createImage(session, imageInfo, executionResult);

	// test
	assertTrue(dockerClient.imageExists(session, imageInfo));
    }

    /**
     * Test that client return false if image doesn't exists.
     */
    @Test
    public void testImageExsistReturnsFalseIfImageDoesntExists() throws Exception {
	imageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);

	// test
	assertFalse(dockerClient.imageExists(session, imageInfo));
    }

    /**
     * Test that client can create image.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testCreateImage() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerClient.createImage(session, imageInfo, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(dockerClient.imageExists(session, imageInfo));
    }

    /**
     * Test that client returns successful result even if already image exists.
     */
    @Test
    public void testCreateImageReturnsSuccessfulIfImageAlreadyExists() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();

	// build image once
	dockerClient.createImage(session, imageInfo, executionResult);

	// test
	assertTrue(dockerClient.imageExists(session, imageInfo));

	// build again
	dockerClient.createImage(session, imageInfo, executionResult);
	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getChildren()[1]);

    }

    /**
     * Test that client throws exception if image is unknown.
     */
    @Test(expected = DockerClientException.class)
    public void testCreateImagethrowsExceptionIfImageIsUnknown() throws Exception {
	imageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);

	// build image
	dockerClient.createImage(session, imageInfo, executionResult);
    }

    /**
     * Test that client can delete image.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testDeleteImage() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerClient.createImage(session, imageInfo, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(dockerClient.imageExists(session, imageInfo));

	// delete
	dockerClient.deleteImage(session, imageInfo, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getChildren()[1]);
	assertFalse(dockerClient.imageExists(session, imageInfo));
    }

    /**
     * Test that client throws exception if can image selected for deletion
     * doesn't exists.
     * 
     * Validation is done on the first child result.
     */
    @Test(expected = DockerClientException.class)
    public void testDeleteImageThrowsExceptionIfImageDoesntExists() throws Exception {
	imageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	dockerClient.deleteImage(session, imageInfo, executionResult);
    }

    /**
     * Test that client can tag image.
     */
    @Test
    public void testTagImage() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	imageInfo2 = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	dockerClient.createImage(session, imageInfo, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(dockerClient.imageExists(session, imageInfo));

	// tag
	dockerClient.tagImage(session, imageInfo, imageInfo2, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getChildren()[1]);
	assertTrue(dockerClient.imageExists(session, imageInfo2));
    }

    /**
     * Test tagging image succeeds if image already exists.
     */
    @Test
    public void testTagImageSucceedsIfTargetImageAlreadyExists() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	imageInfo2 = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	dockerClient.createImage(session, imageInfo, executionResult);
	dockerClient.tagImage(session, imageInfo, imageInfo2, executionResult);

	// test
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(dockerClient.imageExists(session, imageInfo));
	dockerHelper.validateResultIsSuccessful(executionResult.getChildren()[1]);
	assertTrue(dockerClient.imageExists(session, imageInfo2));

	// tag existing image
	dockerClient.tagImage(session, imageInfo, imageInfo2, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(3, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getChildren()[2]);
    }

    /**
     * Test tagging image fails if source image doesn't exists.
     */
    @Test(expected = DockerClientException.class)
    public void testTagImageFailsIfSourceImageDoesntExists() throws Exception {
	imageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	imageInfo2 = dockerInfoBuilder.buildImageInfo(randomImage2, LATEST_IMAGE_TAG);
	dockerClient.tagImage(session, imageInfo, imageInfo2, executionResult);
    }

    /**
     * Test tagging image throw exception if tagging fails.
     */
    @Test(expected = DockerClientException.class)
    public void testTagImageThrowsExceptionIfTaggingFails() throws Exception {
	imageInfo = dockerInfoBuilder.buildImageInfo(randomImage, LATEST_IMAGE_TAG);
	imageInfo2 = dockerInfoBuilder.buildImageInfo("11111111", LATEST_IMAGE_TAG);
	dockerClient.tagImage(session, imageInfo, imageInfo2, executionResult);
    }

    /**
     * Test that client can list images.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testListImages() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	ListedImage[] images = dockerClient.listImages(session, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertNotNull(images);
    }

    /**
     * Test that client contains expected image.
     */
    @Test
    public void testListImagesContainesExpectedImage() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerClient.createImage(session, imageInfo, executionResult);
	ListedImage[] images = dockerClient.listImages(session, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length); // image creation
							       // + list images
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertNotNull(images);

	// iterate over images
	boolean foundImage = false;
	for (ListedImage image : images) {
	    List<String> imageTags = image.getRepoTags();

	    // skip image if no tags are defined
	    if (imageTags == null)
		continue;
	    if (imageTags.size() == 0)
		continue;

	    // iterate over tags
	    for (String imageTag : imageTags) {
		if (imageTag == null)
		    continue;
		if (imageTag.isEmpty())
		    continue;
		String imageFqName = imageInfo.getFullyQualifiedName();
		if (imageFqName.equalsIgnoreCase(imageTag))
		    foundImage = true;
	    }
	}
	assertTrue(foundImage);
    }

    /**
     * Test that client can report on images.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testReportOnImages() throws Exception {
	dockerClient.reportOnImages(session, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
    }

    /**
     * Test that client can create TAR archive.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testCreateTarArchive() throws Exception {

	// create TAR archive - from source directory with single docker file
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo);
	File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
	dockerClient.createTarArchive(sourceDirectory, tarArchive, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(PineappleMatchers.doesFileExist().matches(tarArchive));
    }

    /**
     * Test that client can build image from Dockerfile.
     */
    @Test
    public void testBuildImage() throws Exception {

	// create TAR archive - from source directory with single docker file
	imageInfo = dockerHelper.createDefaultTinyImageInfo();
	dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo);
	File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
	dockerClient.createTarArchive(sourceDirectory, tarArchive, executionResult);

	// build image
	Boolean pullImageBehavior = false;
	dockerClient.buildImage(session, imageInfo, tarArchive, pullImageBehavior, executionResult);

	// test
	assertTrue(dockerClient.imageExists(session, imageInfo));
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
    }

    /**
     * Test that client returns successful result even if already image exists.
     */
    @Test
    public void testBuildImageReturnsSuccessfulIfImageAlreadyExists() throws Exception {
	imageInfo = dockerHelper.createDefaultTinyImageInfo();

	// build image once
	dockerClient.createImage(session, imageInfo, executionResult);

	// create TAR archive - from source directory with single docker file
	dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo);
	File tarArchive = dockerHelper.createTarArchiveName(testDirectory, randomArchive);
	dockerClient.createTarArchive(sourceDirectory, tarArchive, executionResult);

	// build image
	Boolean pullImageBehavior = false;
	dockerClient.buildImage(session, imageInfo, tarArchive, pullImageBehavior, executionResult);

	// test
	assertTrue(dockerClient.imageExists(session, imageInfo));

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(3, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
    }

    /**
     * Test that client can create container.
     */
    @Test
    public void testCreateContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
	assertTrue(dockerClient.containerExists(session, info));
    }

    /**
     * Test that container creation succeeds if container already exists.
     */
    @Test
    public void testCreateContainerSucceedsIfContainerAlreadyExists() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);

	// test
	assertTrue(dockerClient.containerExists(session, info));

	// create twice
	dockerClient.createContainer(session, info, executionResult);

    }

    /**
     * Test that client can delete container.
     */
    @Test
    public void testDeleteContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);

	// delete
	dockerClient.deleteContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertFalse(dockerClient.containerExists(session, info));
    }

    /**
     * Test that container deletion succeeds if container doesn't exist..
     */
    @Test
    public void testDeleteContainerSucceedsIfContainerDoesntExist() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);

	// delete
	dockerClient.deleteContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertFalse(dockerClient.containerExists(session, info));
    }

    /**
     * Test that container query returns true if container exists
     */
    @Test
    public void testContainerExsistReturnsTrueIfContainerExists() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);
	assertTrue(dockerClient.containerExists(session, info));
    }

    /**
     * Test that container query returns false if container doesn't exists
     */
    @Test
    public void testContainerExsistReturnsFalseIfContainerDoesntExists() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	assertFalse(dockerClient.containerExists(session, info));
    }

    /**
     * Test that client can start container.
     */
    @Test
    public void testStartContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);

	// start
	dockerClient.startContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(2, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertTrue(dockerClient.isContainerRunning(session, containerInstanceInfo));
    }

    /**
     * Test that client can stop container.
     */
    @Test
    public void testStopContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);
	dockerClient.startContainer(session, info, executionResult);

	// stop
	dockerClient.stopContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(3, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertFalse(dockerClient.isContainerRunning(session, containerInstanceInfo));
    }

    /**
     * Test that client can pause container.
     */
    @Test
    public void testPauseContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);
	dockerClient.startContainer(session, info, executionResult);

	// pause
	dockerClient.pauseContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(3, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertTrue(dockerClient.isContainerPaused(session, info));
    }

    /**
     * Test that client can unpause container.
     */
    @Test
    public void testUnpauseContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);
	dockerClient.startContainer(session, info, executionResult);
	dockerClient.pauseContainer(session, info, executionResult);

	// unpause
	dockerClient.unpauseContainer(session, info, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(4, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
	assertFalse(dockerClient.isContainerPaused(session, info));
	assertTrue(dockerClient.isContainerRunning(session, containerInstanceInfo));
    }

    /**
     * Test that client can report on containers.
     * 
     * Validation is done on the first child result.
     */
    @Test
    public void testReportOnContainers() throws Exception {
	dockerClient.reportOnContainers(session, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(1, executionResult.getChildren().length);
	dockerHelper.validateResultIsSuccessful(executionResult.getFirstChild());
    }

    /**
     * Test that client can test running container.
     */
    @Test
    public void testTestContainer() throws Exception {
	ContainerInfo info = dockerInfoBuilder.buildContainerInfo(randomContainerName, defaultImage);
	containerInstanceInfo = dockerClient.createContainer(session, info, executionResult);
	dockerClient.startContainer(session, info, executionResult);	
	assertTrue(dockerClient.isContainerRunning(session, containerInstanceInfo));
	
	// start
	dockerClient.testContainer(session, info, ContainerState.RUNNING, executionResult);

	// test
	assertTrue(executionResult.isExecuting());
	assertEquals(3, executionResult.getChildren().length);
	dockerHelper.validateChildResultsAreSuccessful(executionResult);
    }
    
}
