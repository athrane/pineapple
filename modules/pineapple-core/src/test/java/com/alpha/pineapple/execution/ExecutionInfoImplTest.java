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

package com.alpha.pineapple.execution;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang.math.RandomUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;

/**
 * Unit test of the class {@link ExecutionInfoImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ExecutionInfoImplTest {

    /**
     * Object under test.
     */
    ExecutionInfo executionInfo;

    /**
     * Execution result mock.
     */
    ExecutionResult executionResult;

    /**
     * Random boolean.
     */
    boolean randomBoolean;

    @Before
    public void setUp() throws Exception {

	randomBoolean = RandomUtils.nextBoolean();

	// create execution result mock
	executionResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.replay(executionResult);
    }

    @After
    public void tearDown() throws Exception {
	executionInfo = null;
	executionResult = null;
    }

    /**
     * Can create execution info instance.
     */
    @Test
    public void testExecutionInfoImpl() {

	File directory = new File("module-directory");
	String[] environments = {};
	String id = "module-id";
	String environment = "environment";
	String operation = "operation";

	// create module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(id, environments, randomBoolean, directory);

	// create execution info
	executionInfo = new ExecutionInfoImpl(moduleInfo, environment, operation, executionResult);

	// test
	assertNotNull(executionInfo);

	// verify mocks
	EasyMock.verify(executionResult);
    }

    @Test
    public void testGetProperties() {

	File moduleDirectory = new File("module-directory");
	String[] environments = {};
	String id = "module-id";
	String environment = "environment";
	String operation = "operation";

	// create module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(id, environments, randomBoolean, moduleDirectory);

	// create execution info
	executionInfo = new ExecutionInfoImpl(moduleInfo, environment, operation, executionResult);

	// test
	assertEquals(moduleInfo, executionInfo.getModuleInfo());
	assertEquals(environment, executionInfo.getEnvironment());
	assertEquals(operation, executionInfo.getOperation());
	assertEquals(executionResult, executionInfo.getResult());

	// verify mocks
	EasyMock.verify(executionResult);
    }

}
