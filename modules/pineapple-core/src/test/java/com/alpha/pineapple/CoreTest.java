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

package com.alpha.pineapple;

import static com.alpha.pineapple.CoreConstants.MSG_ENVIRONMENT;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE_FILE;
import static com.alpha.pineapple.CoreConstants.MSG_OPERATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Random;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.command.CommandFacade;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.pineapple.module.ModuleNotFoundException;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;
import com.alpha.testutils.TestUtilsTestConstants;

/**
 * Unit test of the class {@link CoreImpl}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CoreTest {

    /**
     * String returned by mocked but initialized message provider.
     */
    static final String PINEAPPLE_VERSION = "ci.name";

    /**
     * String returned by mocked but initialized message provider.
     */
    static final String PINEAPPLE_SUCCESFUL_INIT_INFO = "ci.initialization_info_success";

    /**
     * String returned by mocked but initialized message provider.
     */
    static final String PINEAPPLE_FAILED_INIT_INFO = "ci.initialization_info_failure";

    /**
     * String returned by mocked but initialized message provider.
     */
    static final String PINEAPPLE_NOT_INIT_INFO = "ci.initialization_info_notinitialized";

    /**
     * Test operation supported by the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     */
    static final String testOperation = "test-operation";

    /**
     * Previous context value for a specific key.
     */
    static final Object PREVIOUS_VALUE = null;

    /**
     * Class under test.
     */
    CoreImpl coreImpl;

    /**
     * Credential provider.
     */
    CredentialProvider provider;

    /**
     * Object mother for credential provider
     */
    ObjectMotherCredentialProvider providerMother;

    /**
     * Object mother for environment configuration.
     */
    ObjectMotherEnvironmentConfiguration envConfigMother;

    /**
     * Object mother for module.
     */
    ObjectMotherModule moduleMother;

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;

    /**
     * Mock module repository.
     */
    ModuleRepository moduleRepository;
    
    /**
     * Mock result repository.
     */
    ResultRepository resultRepository;

    /**
     * Mock scheduled operation repository.
     */
    ScheduledOperationRespository scheduledOperationRespository;
    
    /**
     * Mock command runner
     */
    CommandRunner commandRunner;

    /**
     * Command facade.
     */
    CommandFacade commandFacade;

    /**
     * Mock context
     */
    Context context;

    /**
     * Mock plugin activator.
     */
    PluginActivator pluginActivator;

    /**
     * Mock configuration.
     */
    Configuration configuration;

    /**
     * Mock asynchronous operation task.
     */
    OperationTask asyncOperationTask;

    /**
     * Mock administration provider.
     */
    Administration administrationProvider;

    /**
     * Random execution time.
     */
    Long randomTime;

    /**
     * Random file name.
     */
    String randomFileName;

    @Before
    public void setUp() throws Exception {
	randomTime = new Random().nextLong();
	randomFileName = RandomStringUtils.randomAlphabetic(10);

	// create coreImpl
	coreImpl = new CoreImpl();

	// create mothers
	providerMother = new ObjectMotherCredentialProvider();
	envConfigMother = new ObjectMotherEnvironmentConfiguration();
	moduleMother = new ObjectMotherModule();

	// create mock credential provider
	provider = EasyMock.createMock(CredentialProvider.class);
	EasyMock.replay(provider);

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject message provider
	ReflectionTestUtils.setField(coreImpl, "messageProvider", messageProvider);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
		(Object[]) EasyMock.isA(Object[].class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

	// create mocks
	resultRepository = EasyMock.createMock(ResultRepository.class);
	moduleRepository = EasyMock.createMock(ModuleRepository.class);
	scheduledOperationRespository = EasyMock.createMock(ScheduledOperationRespository.class);	
	asyncOperationTask = EasyMock.createMock(OperationTask.class);
	commandRunner = EasyMock.createMock(CommandRunner.class);
	commandFacade = EasyMock.createMock(CommandFacade.class);
	context = EasyMock.createMock(Context.class);
	administrationProvider = EasyMock.createMock(Administration.class);

	// inject repository
	ReflectionTestUtils.setField(coreImpl, "resultRepository", resultRepository);
	ReflectionTestUtils.setField(coreImpl, "moduleRepository", moduleRepository);	
	ReflectionTestUtils.setField(coreImpl, "scheduledOperationRepository", scheduledOperationRespository);	
	ReflectionTestUtils.setField(coreImpl, "asyncOperationTask", asyncOperationTask);
	ReflectionTestUtils.setField(coreImpl, "commandRunner", commandRunner);
	ReflectionTestUtils.setField(coreImpl, "commandFacade", commandFacade);
	ReflectionTestUtils.setField(coreImpl, "administrationProvider", administrationProvider);

	// create mock plugin activator
	pluginActivator = EasyMock.createMock(PluginActivator.class);
	EasyMock.replay(pluginActivator);

	// create mock configuration
	configuration = org.easymock.classextension.EasyMock.createMock(Configuration.class);
	org.easymock.classextension.EasyMock.replay(configuration);
    }

    @After
    public void tearDown() throws Exception {
	// verify credential provider
	EasyMock.verify(provider);

	testDirectory = null;
	provider = null;
	coreImpl = null;
	providerMother = null;
	envConfigMother = null;
	pluginActivator = null;
	configuration = null;
	asyncOperationTask = null;
    }

    /**
     * Complete mock administration initialization.
     * 
     * @return mock execution result.
     */
    void completeMockAdministrationInitialization() {
	administrationProvider.setCredentialProvider(provider);
	EasyMock.replay(administrationProvider);
    }

    /**
     * Complete mock repository initialization.
     * 
     * @return mock execution result.
     */
    ExecutionResult completeMockRepositoryInitialization() {
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	EasyMock.replay(resultRepository);
	return result;
    }

    /**
     * Complete mock repository initialization for execution of operations.
     * 
     * @param info
     *            Execution info object.
     * 
     * @return mock execution result.
     */
    ExecutionResult completeMockRepositoryInitializationForExecution(ExecutionInfo info) {
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	EasyMock.replay(resultRepository);
	return result;
    }

    /**
     * Complete mock context initialization.
     * 
     * @param configuration
     *            Environment configuration.
     */
    void completeMockContextInitialization(Configuration configuration) {
	EasyMock.replay(context);
    }

    /**
     * Complete mock command runner initialization.
     * 
     * @param result
     *            Mock Execution result.
     */
    void completeCommandRunnerInitialization(ExecutionResult result) {
	commandRunner.setExecutionResult(result);
	EasyMock.replay(commandRunner);
    }

    /**
     * Complete mock command facade initialization.
     * 
     * @param result
     *            Mock Execution result.
     */
    void completeCommandFacadeInitialization(ExecutionResult result) {
	EasyMock.expect(commandFacade.loadJaxbObjects(EasyMock.isA(File.class), EasyMock.isA(Class.class),
		EasyMock.isA(ExecutionResult.class))).andReturn(configuration);
	EasyMock.expect(commandFacade.initializePluginActivator(EasyMock.isA(CredentialProvider.class),
		EasyMock.isA(Configuration.class), EasyMock.isA(ExecutionResult.class))).andReturn(pluginActivator);
	EasyMock.replay(commandFacade);
    }

    /**
     * Complete mock scheduled operation repository initialization.
     * 
     * @param result execution result used to initialize repository.
     */
    void completeMockScheduledOperationRepositoryInitialization(ExecutionResult result) {
	scheduledOperationRespository.initialize(result);
	EasyMock.replay(scheduledOperationRespository);
    }
    
    /**
     * Complete mock execution result initialization with report metadata.
     * 
     * @param result mock execution result
     */
    void completeMockResultInitializationWithReportMetadata(ExecutionResult result) {
	result.addMessage(MSG_OPERATION, "ci.report_metadata_operation_info");
	result.addMessage(MSG_ENVIRONMENT, "ci.report_metadata_environment_info");
	result.addMessage(MSG_MODULE, "ci.report_metadata_module_info");
	result.addMessage(MSG_MODULE_FILE, "ci.report_metadata_module_file_info");	
    }
    
    /**
     * Test that core component can be initialized.
     * 
     * The core component will load the resources file
     * src/test/resources/resources.xml and activate the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializeCore() throws CoreException {
	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);
	
	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(true);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider);

	// test
	assertTrue(coreImpl.toString().startsWith(PINEAPPLE_VERSION));

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(scheduledOperationRespository);	
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
    }

    /**
     * Test that core component can be initialized with external resources file.
     * The file is specified by an absolute file name.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testInitializeCoreWithExternalResourcesFile() throws Exception {
	// create test resources configuration
	Configuration resourcesConfiguration;
	resourcesConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

	// define resources file
	File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

	// save file
	envConfigMother.jaxbMarshall(resourcesConfiguration, resourcesFile);

	// complete mock initializations
	completeMockContextInitialization(resourcesConfiguration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(true);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider, resourcesFile);

	// test
	assertTrue(coreImpl.toString().startsWith(PINEAPPLE_VERSION));

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);		
    }

    /**
     * Test that core component can be initialized with external resources file,
     * which isn't named "resources.xml". The resources file is specified by an
     * absolute file name.
     * 
     * @throws Exception
     *             If test fails
     */
    @Test
    public void testInitializeCoreWithExternalResourcesFile2() throws Exception {
	// create test resources configuration
	Configuration resourcesConfiguration;
	resourcesConfiguration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

	// define resources file
	File resourcesFile = new File(testDirectory, randomFileName + ".xml");

	// save file
	envConfigMother.jaxbMarshall(resourcesConfiguration, resourcesFile);

	// complete mock initializations
	completeMockContextInitialization(resourcesConfiguration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);
	
	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider, resourcesFile);

	// test
	assertTrue(coreImpl.toString().startsWith(PINEAPPLE_VERSION));

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);			
    }

    /**
     * Test that core component can be initialized with internal resources file,
     * with a named resources file, f.x. "resources.xml". The test case will
     * attempt to load the file from the class path which contains
     * src/test/resources/resources.xml.
     * 
     * The resources file defines the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testInitializeCoreWithNamedInternalResourcesFile() throws Exception {
	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(true);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// convert file name to file
	File resourcesFile = new File("resources.xml");

	// initialize core component
	coreImpl.initialize(provider, resourcesFile);

	// test
	assertTrue(coreImpl.toString().startsWith(PINEAPPLE_VERSION));

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);				
    }

    /**
     * Test that test operation can be executed.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testCanExecuteOperation() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = completeMockRepositoryInitializationForExecution(info);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(true);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.replay(info);

	// complete mock module info initialization
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.replay(moduleInfo);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.expect(asyncOperationTask.execute(operation, environmentName, moduleName)).andReturn(info);
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, environmentName, moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);					
    }

    /**
     * Test that test operation can be executed which fails with module not
     * found exception.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testCanExecuteOperationWhichFailsWithException() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(ModuleInfo.class), EasyMock.isA(String.class),
		EasyMock.isA(String.class))).andReturn(info);
	EasyMock.replay(resultRepository);
		
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module info initialization
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.replay(moduleInfo);

	// complete mock module repository initialization which THROWS an
	// exception
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	Exception e = new ModuleNotFoundException("exception-message");	
	EasyMock.expect(asyncOperationTask.execute(operation, environmentName, moduleName)).andThrow(e);
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation	
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, environmentName, moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);						
    }

    /**
     * Test that undefined operation is rejected.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteRejectsUndefinedOperation() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();	
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for NULL operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, environmentName, null)).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(null, environmentName, moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);							
    }

    /**
     * Test that empty operation is rejected.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteRejectsEmptyOperation() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for EMPTY operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, environmentName, "")).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation("", environmentName, moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);							
    }

    /**
     * Test that undefined environment is rejected.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteRejectsUndefinedEnvironment() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for EMPTY operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, null, operation)).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, null, moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
	
    }

    /**
     * Test that empty environment is rejected.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteRejectsEmptyEnvironment() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for EMPTY operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, "", operation)).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, "", moduleName);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that undefined module is rejected.
     * 
     * @throws Exception
     *             If test fails.
     */
    @Test
    public void testExecuteRejectsUndefinedModule() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for EMPTY operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, environmentName, operation)).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, environmentName, null);

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that empty module is rejected.
     * 
     * @throws Exception
     *             If test fails
     */
    @Test
    public void testExecuteRejectsEmptyModule() throws Exception {
	File rootDir = testDirectory;
	String moduleName = "some-module";
	String environmentName = "yeti-environment";
	String operation = TestUtilsTestConstants.helloWorldOperation;

	// create null module info
	ModuleInfo moduleInfo = ModuleInfoImpl.getNullInstance();

	// complete mock initializations
	completeMockContextInitialization(configuration);
	completeMockAdministrationInitialization();
	ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(resultRepository.startExecution(EasyMock.isA(String.class))).andReturn(result);
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// initialize result repository for EMPTY operation
	EasyMock.expect(resultRepository.startExecution(moduleInfo, environmentName, operation)).andReturn(info);
	EasyMock.replay(resultRepository);
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(false);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	result.completeAsError((MessageProvider) EasyMock.isA(MessageProvider.class),
		(String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject(), (Exception) EasyMock.anyObject());
	EasyMock.replay(result);

	// complete mock execution info initialization
	EasyMock.expect(info.getResult()).andReturn(result);
	EasyMock.replay(info);

	// complete mock module repository initialization
	moduleRepository.initialize();
	EasyMock.replay(moduleRepository);

	// complete mock task initialization
	EasyMock.replay(asyncOperationTask);

	// initialize core component
	coreImpl.initialize(provider);

	// create module
	moduleMother.createModuleWithSingleEmptyModel(rootDir, moduleName, environmentName);

	// execute operation
	ExecutionInfo executionInfo = coreImpl.executeOperation(operation, environmentName, "");

	// test
	assertNotNull(executionInfo);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	EasyMock.verify(moduleRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(info);
	EasyMock.verify(asyncOperationTask);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that initialization info (as a string) is defined after core
     * component is initialized.
     * 
     * The core component will load the resources file
     * src/test/resources/resources.xml and activate the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializationInfoStringIsDefinedBeforeCoreInitialization() throws CoreException {
	// complete mock initializations
	EasyMock.replay(commandRunner);
	EasyMock.replay(commandFacade);
	EasyMock.replay(context);
	EasyMock.replay(resultRepository);
	EasyMock.replay(administrationProvider);

	// no core component initialization

	// test
	assertEquals(PINEAPPLE_NOT_INIT_INFO, coreImpl.getInitializationInfoAsString());

	// assert
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
    }

    /**
     * Test that initialization info (as an string) is defined after core
     * component is initialized successfully.
     * 
     * The core component will load the resources file
     * src/test/resources/resources.xml and activate the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializationInfoStringIsDefinedAfterSuccessfulCoreInitialization() throws CoreException {
	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);
	
	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime).times(2);
	EasyMock.expect(result.isSuccess()).andReturn(true).times(2);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider);

	// test
	assertEquals(PINEAPPLE_SUCCESFUL_INIT_INFO, coreImpl.getInitializationInfoAsString());

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that initialization info (as an string) is defined after core
     * component is initialization fails.
     * 
     * The core component will load the resources file
     * src/test/resources/resources.xml and activate the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializationInfoStringIsDefinedAfterFailedCoreInitialization() throws CoreException {

	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime).times(2);
	EasyMock.expect(result.isSuccess()).andReturn(false).times(2);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider);

	// test
	assertEquals(PINEAPPLE_FAILED_INIT_INFO, coreImpl.getInitializationInfoAsString());

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that initialization info (as an execution result) isn't defined
     * before core component is initialized.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializationInfoIsntDefinedBeforeCoreInitialization() throws CoreException {
	// complete mock initializations
	EasyMock.replay(commandRunner);
	EasyMock.replay(commandFacade);
	EasyMock.replay(context);
	EasyMock.replay(resultRepository);
	EasyMock.replay(administrationProvider);
	EasyMock.replay(scheduledOperationRespository);

	// no core component initialization

	// test
	assertNull(coreImpl.getInitializationInfo());

	// assert
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

    /**
     * Test that initialization info (as an execution result) is defined after
     * core component is initialized.
     * 
     * The core component will load the resources file
     * src/test/resources/resources.xml and activate the test plugin
     * "com.alpha.pineapple.plugin.test" located in the test-utils project.
     * 
     * @throws CoreException
     *             If test fails.
     */
    @Test
    public void testInitializationInfoIsDefinedAfterCoreInitialization() throws CoreException {
	// complete mock initializations
	completeMockContextInitialization(configuration);
	ExecutionResult result = completeMockRepositoryInitialization();
	completeCommandRunnerInitialization(result);
	completeCommandFacadeInitialization(result);
	completeMockAdministrationInitialization();
	completeMockScheduledOperationRepositoryInitialization(result);
	completeMockResultInitializationWithReportMetadata(result);

	// complete mock execution result initialization
	EasyMock.expect(result.getTime()).andReturn(randomTime);
	EasyMock.expect(result.isSuccess()).andReturn(true);
	result.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed", null);
	EasyMock.replay(result);

	// initialize core component
	coreImpl.initialize(provider);

	// test
	assertEquals(coreImpl.getInitializationInfo(), result);

	// assert
	EasyMock.verify(result);
	EasyMock.verify(provider);
	EasyMock.verify(commandRunner);
	EasyMock.verify(commandFacade);
	EasyMock.verify(context);
	EasyMock.verify(resultRepository);
	org.easymock.classextension.EasyMock.verify(configuration);
	EasyMock.verify(pluginActivator);
	EasyMock.verify(administrationProvider);
	EasyMock.verify(scheduledOperationRespository);								
    }

}
