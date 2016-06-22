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

import static com.alpha.pineapple.docker.command.InspectImageCommand.EXECUTIONRESULT_KEY;
import static com.alpha.pineapple.docker.command.InspectImageCommand.IMAGE_INFO_KEY;
import static com.alpha.pineapple.docker.command.InspectImageCommand.INSPECTED_IMAGE_KEY;
import static com.alpha.pineapple.docker.command.InspectImageCommand.SESSION_KEY;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain InspectImageCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class InspectImageCommandSystemTest {

    /**
     * Object under test.
     */
    @Resource
    Command inspectImageCommand;

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
     * Default image info (CentOS).
     */
    ImageInfo defaultImageInfo;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	// create context
	context = new ContextBase();

	// create execution result
	executionResult = new ExecutionResultImpl("root");

	// create session
	session = dockerHelper.createDefaultSession();

	// create image and info's
	defaultImageInfo = dockerHelper.createDefaultImage(session);

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
	assertNotNull(inspectImageCommand);
    }

    /**
     * Test that command can inspect image.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandCanInspectImage() throws Exception {
	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);
	context.put(SESSION_KEY, session);
	context.put(IMAGE_INFO_KEY, defaultImageInfo);

	// execute command
	inspectImageCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(context.containsKey(INSPECTED_IMAGE_KEY));
    }

}
