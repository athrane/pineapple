/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.   
 */

package com.alpha.pineapple.command;

import static com.alpha.pineapple.command.CopyExampleModulesCommand.DESTINATION_DIR_KEY;
import static com.alpha.pineapple.command.CopyExampleModulesCommand.EXAMPLE_MODULES;
import static com.alpha.pineapple.command.CopyExampleModulesCommand.EXECUTIONRESULT_KEY;
import static com.alpha.pineapple.command.CopyExampleModulesCommand.RESOURCES_SEARCH_STRING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test of the class {@linkplain CopyExampleModulesCommand }
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CopyExampleModulesCommandTest {

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Object under test.
     */
    Command command;

    /**
     * Context.
     */
    Context context;

    /**
     * Mock execution result.
     */
    ExecutionResult executionResult;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    /**
     * Mock Application context.
     */
    ApplicationContext applicationContext;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random value.
     */
    String randomValue2;

    /**
     * Random file.
     */
    File randomFile;

    @Before
    public void setUp() throws Exception {

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	randomValue = RandomStringUtils.randomAlphabetic(10);
	randomValue2 = RandomStringUtils.randomAlphabetic(10);
	randomFile = new File(testDirectory, RandomStringUtils.randomAlphabetic(10) + ".txt");

	// create context
	context = new ContextBase();

	// create command
	command = new CopyExampleModulesCommand();

	// create execution result
	executionResult = EasyMock.createMock(ExecutionResult.class);

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject
	ReflectionTestUtils.setField(command, "messageProvider", messageProvider);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();

	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(
		messageProvider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject()));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

	// create mock
	applicationContext = EasyMock.createMock(ApplicationContext.class);

	// inject
	ReflectionTestUtils.setField(command, "applicationContext", applicationContext);

    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that command fails if context is undefined.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRejectsUndefinedContext() throws Exception {
	// create context
	context = null;

	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if destination directory property is undefined.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfDestinationDirectoryKeyIsUndefinedInContext() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(EXECUTIONRESULT_KEY, executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command fails if destination directory is empty.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = CommandInitializationFailedException.class)
    public void testCommandFailsIfDestinationDirectoryKeyIsEmpty() throws Exception {
	// complete mock execution result setup
	EasyMock.replay(executionResult);

	// setup context
	context.put(DESTINATION_DIR_KEY, "");
	context.put(EXECUTIONRESULT_KEY, executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(executionResult);
    }

    /**
     * Test that command can copy example modules.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandSucceedsWithZeroModules() throws Exception {
	Resource[] resources = {};

	// complete mock execution result setup
	executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.eq("cemc.success"),
		EasyMock.isA(Object[].class));
	EasyMock.replay(executionResult);

	// complete mock setup
	EasyMock.expect(applicationContext.getResources(RESOURCES_SEARCH_STRING)).andReturn(resources);
	EasyMock.replay(applicationContext);

	// setup context
	context.put(DESTINATION_DIR_KEY, testDirectory);
	context.put(EXECUTIONRESULT_KEY, executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(messageProvider);
	EasyMock.verify(executionResult);
	EasyMock.verify(applicationContext);
    }

    /**
     * Test that command can copy example modules.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCommandSucceedsWithOneModule() throws Exception {
	FileUtils.writeStringToFile(randomFile, randomValue);

	String resource1Description = EXAMPLE_MODULES + randomValue + File.separator + randomValue2;

	Resource resource1 = EasyMock.createMock(Resource.class);
	URI someURI = new URI(randomValue);
	EasyMock.expect(resource1.getURI()).andReturn(someURI);	
	EasyMock.expect(resource1.getDescription()).andReturn(resource1Description);
	EasyMock.expect(resource1.getFilename()).andReturn(randomValue2).times(2);
	EasyMock.expect(resource1.getInputStream()).andReturn((InputStream) new FileInputStream(randomFile));
	EasyMock.replay(resource1);

	Resource[] resources = { resource1 };

	// complete mock execution result setup
	executionResult.addMessage(EasyMock.isA(String.class), EasyMock.isA(String.class));
	executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.eq("cemc.success"),
		EasyMock.isA(Object[].class));
	EasyMock.replay(executionResult);

	// complete mock setup
	EasyMock.expect(applicationContext.getResources(RESOURCES_SEARCH_STRING)).andReturn(resources);
	EasyMock.replay(applicationContext);

	// setup context
	context.put(DESTINATION_DIR_KEY, testDirectory);
	context.put(EXECUTIONRESULT_KEY, executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(messageProvider);
	EasyMock.verify(executionResult);
	EasyMock.verify(applicationContext);
	EasyMock.verify(resource1);
    }

    /**
     * Test that command can copy example modules.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFailsIfApplicationContextFailsToLocateResources() throws Exception {

	// complete mock execution result setup
	executionResult.completeAsError(EasyMock.eq(messageProvider), EasyMock.eq("cemc.locate_resources_error"),
		EasyMock.isA(FileNotFoundException.class));
	EasyMock.replay(executionResult);

	// complete mock setup
	EasyMock.expect(applicationContext.getResources(RESOURCES_SEARCH_STRING))
		.andThrow(new FileNotFoundException(randomValue2));
	EasyMock.replay(applicationContext);

	// setup context
	context.put(DESTINATION_DIR_KEY, testDirectory);
	context.put(EXECUTIONRESULT_KEY, executionResult);

	// execute command
	command.execute(context);

	// Verify mocks
	EasyMock.verify(messageProvider);
	EasyMock.verify(executionResult);
	EasyMock.verify(applicationContext);
    }

}
