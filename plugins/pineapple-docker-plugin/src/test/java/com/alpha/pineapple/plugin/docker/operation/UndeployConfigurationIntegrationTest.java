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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.docker.DockerConstants;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.Image;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>UndeployConfiguration</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.docker-config.xml" })
public class UndeployConfigurationIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	UndeployConfiguration undeployOperation;

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
	 * Random source directory.
	 */
	String randomSourceDirectoryName;

	@Before
	public void setUp() throws Exception {
		randomRepo = RandomStringUtils.randomAlphanumeric(10);
		randomTag = RandomStringUtils.randomAlphanumeric(10);
		randomRepo2 = RandomStringUtils.randomAlphanumeric(10);
		randomTag2 = RandomStringUtils.randomAlphanumeric(10);
		randomSourceDirectoryName = RandomStringUtils.randomAlphabetic(10);

		// create mock session
		session = EasyMock.createMock(DockerSession.class);

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create content mother
		contentMother = new ObjectMotherContent();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that deploy configuration operation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(undeployOperation);
	}

	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

		// complete session initialization
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createEmptyDockerModel();

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command with
	 * empty tag.
	 */
	@Test
	public void testCanExecuteWithModelWithImageCommandWithEmptyTag() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo);
		uriVariables.put("force", "true");

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createDockerModelWithImageCommand(randomRepo, "");

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command with
	 * undefined tag.
	 * 
	 * Since model {@linkplain Image} has "latest" as default value, then that value
	 * is returned if model image is created with null value.
	 */
	@Test
	public void testCanExecuteWithModelWithImageCommandWithUndefinedTag() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo);
		uriVariables.put("force", "true");

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Docker content = contentMother.createDockerModelWithImageCommand(randomRepo, null);

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image command.
	 */
	@Test
	public void testCanExecuteWithModelWithImageCommand() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo);
		uriVariables.put("force", "true");

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createDockerModelWithImageCommand(randomRepo, randomTag);

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with tagged image command.
	 */
	@Test
	public void testCanExecuteWithModelWithTaggedImageCommand() throws Exception {
		Map<String, String> uriVariables = new HashMap<String, String>(3);
		uriVariables.put("image", randomRepo2);
		uriVariables.put("force", "true");

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createDockerModelWithTaggedImageCommand(randomRepo, randomTag, randomRepo2,
				randomTag2);

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image-from-dockerfile
	 * command. With image pulling defined in the model.
	 * 
	 * The forced delete parameter will be set in the REST request and it not
	 * related to the pulling argument in the model.
	 */
	@Test
	public void testCanExecuteWithModelWithImageFromDockerfileCommand() throws Exception {

		// create source directory with single docker file for TAR archive
		File sourceDirectory = new File(testDirectory, randomSourceDirectoryName);

		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("image", randomRepo);
		uriVariables.put("force", "true"); // alway true

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createDockerModelWithImageFromDockerfileCommand(randomRepo, randomTag,
				sourceDirectory.getAbsolutePath(), true);

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

	/**
	 * Test that the operation can execute with a model with image-from-dockerfile
	 * command. With no image pulling defined in the model.
	 * 
	 * The forced delete parameter will be set in the REST request and it not
	 * related to the pulling argument in the model.
	 */
	@Test
	public void testCanExecuteWithModelWithImageFromDockerfileCommandNoForcedDelete() throws Exception {

		// create source directory with single docker file for TAR archive
		File sourceDirectory = new File(testDirectory, randomSourceDirectoryName);

		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("image", randomRepo);
		uriVariables.put("force", "true"); // always true

		// complete session initialization
		session.httpDelete(DockerConstants.DELETE_IMAGE_URI, uriVariables);
		EasyMock.replay(session);

		// create content
		Object content = contentMother.createDockerModelWithImageFromDockerfileCommand(randomRepo, randomTag,
				sourceDirectory.getAbsolutePath(), false);

		// invoke operation
		undeployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		EasyMock.verify(session);
	}

}
