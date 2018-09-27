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

package com.alpha.pineapple.plugin.docker.operation;

import static com.alpha.pineapple.docker.DockerConstants.BUILD_IMAGE_URI;
import static com.alpha.pineapple.docker.DockerConstants.CONTENT_TYPE_TAR;
import static com.alpha.pineapple.docker.DockerConstants.CREATE_CONTAINER_URI;
import static com.alpha.pineapple.docker.DockerConstants.CREATE_IMAGE_URI;
import static com.alpha.pineapple.docker.DockerConstants.CREATE_TAGGED_IMAGE_URI;
import static com.alpha.pineapple.docker.DockerConstants.RENAME_CONTAINER_URI;
import static com.alpha.pineapple.docker.DockerConstants.TAG_IMAGE_URI;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.CreatedContainer;
import com.alpha.pineapple.docker.model.rest.JsonMessage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.Image;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.DockerTestHelper;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.docker-config.xml" })
public class DeployConfigurationIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	DeployConfiguration deployOperation;

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
	 * Object mother for the docker model.
	 */
	ObjectMotherContent contentMother;

	/**
	 * Docker session.
	 */
	DockerSession session;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Mock runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Random value.
	 */
	String randomRepo;

	/**
	 * Random value.
	 */
	String randomTag;

	/**
	 * Random value.
	 */
	String randomRepo2;

	/**
	 * Random value.
	 */
	String randomTag2;

	/**
	 * Random model container ID.
	 */
	String randomModelContaineID;

	/**
	 * Random Docker container ID.
	 */
	String randomDockerContainerID;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Random module.
	 */
	String randomModule;

	/**
	 * Random key.
	 */
	String randomKey;

	/**
	 * Random archive.
	 */
	String randomArchive;

	/**
	 * Random source directory.
	 */
	String randomSourceDirectoryName;

	@Before
	public void setUp() throws Exception {
		randomKey = RandomStringUtils.randomAlphanumeric(10);
		randomModule = RandomStringUtils.randomAlphanumeric(10);
		randomEnvironment = RandomStringUtils.randomAlphanumeric(10);
		randomModelContaineID = RandomStringUtils.randomAlphanumeric(10);
		randomDockerContainerID = RandomStringUtils.randomAlphanumeric(10);
		randomRepo = RandomStringUtils.randomAlphanumeric(10);
		randomTag = RandomStringUtils.randomAlphanumeric(10);
		randomRepo2 = RandomStringUtils.randomAlphanumeric(10);
		randomTag2 = RandomStringUtils.randomAlphanumeric(10);
		randomArchive = RandomStringUtils.randomAlphabetic(10);
		randomSourceDirectoryName = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock session
		session = createMock(DockerSession.class);

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create content mother
		contentMother = new ObjectMotherContent();

		// reset plugin provider
		reset(coreRuntimeDirectoryProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that deploy configuration operation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(deployOperation);
	}

	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

		// complete session initialization
		replay(session);

		// create content
		Docker content = contentMother.createEmptyDockerModel();

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command with
	 * empty tag.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteWithModelWithImageCommandWithEmptyTag() throws Exception {
		// complete session initialization
		JsonMessage[] JsonMessageInfos = {};
		expect(session.httpPostForObjectWithMultipleRootElements(eq(CREATE_IMAGE_URI), isA(Map.class),
				isA(JsonMessage[].class.getClass()))).andReturn(JsonMessageInfos);
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithImageCommand(randomRepo, "");

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command with
	 * undefined tag.
	 * 
	 * Since model {@linkplain Image} has "latest" as default value, then that value
	 * is returned if model image is created with null value.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteWithModelWithImageCommandWithUndefinedTag() throws Exception {

		// complete session initialization
		JsonMessage[] JsonMessageInfos = {};
		expect(session.httpPostForObjectWithMultipleRootElements(eq(CREATE_TAGGED_IMAGE_URI), isA(Map.class),
				isA(JsonMessage[].class.getClass()))).andReturn(JsonMessageInfos);
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithImageCommand(randomRepo, null);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteWithModelWithImageCommand() throws Exception {

		// complete session initialization
		JsonMessage[] JsonMessageInfos = {};
		expect(session.httpPostForObjectWithMultipleRootElements(eq(CREATE_TAGGED_IMAGE_URI), isA(Map.class),
				isA(JsonMessage[].class.getClass()))).andReturn(JsonMessageInfos);
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithImageCommand(randomRepo, randomTag);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}

	/**
	 * Test that the operation can execute with a model with tagged image command.
	 */
	@Test
	public void testCanExecuteWithModelWithTaggedImageCommand() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo + ":" + randomTag);
		uriVariables.put("repository", randomRepo2);

		// complete session initialization
		session.httpPost(TAG_IMAGE_URI, uriVariables);
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithTaggedImageCommand(randomRepo, randomTag, randomRepo2,
				randomTag2);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image from DockerFile
	 * command.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteWithModelWithImageFromDockerfileCommand() throws Exception {

		// create source directory with single docker file for TAR archive
		File sourceDirectory = new File(testDirectory, randomSourceDirectoryName);
		ImageInfo imageInfo2 = dockerInfoBuilder.buildImageInfo(randomRepo, randomTag);
		dockerHelper.createDockerFileWithFromCommand(sourceDirectory, imageInfo2);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(sourceDirectory.getAbsolutePath(), result))
				.andReturn(sourceDirectory);
		expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory);
		replay(coreRuntimeDirectoryProvider);

		// complete session initialization
		JsonMessage[] JsonMessageInfos = {};
		expect(session.httpPostForObjectWithMultipleRootElements(eq(BUILD_IMAGE_URI), isA(Map.class),
				isA(HttpEntity.class), isA(JsonMessage[].class.getClass()), eq(CONTENT_TYPE_TAR)))
						.andReturn(JsonMessageInfos);
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithImageFromDockerfileCommand(randomRepo, randomTag,
				sourceDirectory.getAbsolutePath(), false);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that the operation can execute with a model with container command.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanExecuteWithModelWithContainerCommand() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo + ":" + randomTag);
		uriVariables.put("repository", randomRepo2);

		// complete mock session initialization
		CreatedContainer createdContainer = createMock(CreatedContainer.class);
		expect(createdContainer.getId()).andReturn(randomDockerContainerID).times(3);
		List<String> emptyWarnings = new ArrayList<String>();
		expect(createdContainer.getWarnings()).andReturn(emptyWarnings);
		replay(createdContainer);
		expect(session.httpPostForObject(contains(CREATE_CONTAINER_URI), isA(ContainerConfiguration.class),
				isA(CreatedContainer.class.getClass()))).andReturn(createdContainer);
		session.httpPost(contains(RENAME_CONTAINER_URI), isA(Map.class));
		replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithContainerCommand(randomModelContaineID, randomRepo,
				randomTag);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
		verify(createdContainer);
	}

	/**
	 * Test that the operation fails to execute with a model with container command
	 * if image info isn't defined.
	 */
	@Test(expected = PluginExecutionFailedException.class)
	public void testFailsExecutionWithModelWithContainerCommandIfImageInfoIsntDefined() throws Exception {
		replay(session);

		// create content
		Docker content = contentMother
				.createDockerModelWithContainerCommandWithUndefinedImageInfo(randomModelContaineID);

		// invoke operation
		deployOperation.execute(content, session, result);
	}

}
