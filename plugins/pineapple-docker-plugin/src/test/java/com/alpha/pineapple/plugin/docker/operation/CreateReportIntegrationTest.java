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

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
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

import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>CreateReport</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.docker-config.xml" })
public class CreateReportIntegrationTest {

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

    @Before
    public void setUp() throws Exception {
	randomRepo = RandomStringUtils.randomAlphanumeric(10);
	randomTag = RandomStringUtils.randomAlphanumeric(10);
	randomRepo2 = RandomStringUtils.randomAlphanumeric(10);
	randomTag2 = RandomStringUtils.randomAlphanumeric(10);

	// create mock session
	session = EasyMock.createMock(DockerSession.class);

	// create execution result
	result = new ExecutionResultImpl("Root result");

	// create content mother
	contentMother = new ObjectMotherContent();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that deploy configuration operation can be looked up from the
     * context.
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
	EasyMock.replay(session);

	// create content
	Object content = contentMother.createEmptyDockerModel();

	// invoke operation
	deployOperation.execute(content, session, result);

	// test
	assertTrue(result.isSuccess());
	EasyMock.verify(session);
    }

}
