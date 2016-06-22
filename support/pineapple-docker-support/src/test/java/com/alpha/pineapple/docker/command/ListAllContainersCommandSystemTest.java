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

import static com.alpha.pineapple.docker.command.ListAllContainersCommand.CONTAINERS_KEY;
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

import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.testutils.DockerTestHelper;

/**
 * System test of the class {@linkplain ReportOnContainersCommand}.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class ListAllContainersCommandSystemTest {

    /**
     * Object under test.
     */
    @Resource
    Command listAllContainersCommand;

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
	assertNotNull(listAllContainersCommand);
    }

    /**
     * Test that command can list containers.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandCanListContainers() throws Exception {
	// setup context
	context.put(ListAllContainersCommand.EXECUTIONRESULT_KEY, executionResult);
	context.put(ListAllContainersCommand.SESSION_KEY, session);

	// execute command
	listAllContainersCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertTrue(context.containsKey(CONTAINERS_KEY));
    }

}
