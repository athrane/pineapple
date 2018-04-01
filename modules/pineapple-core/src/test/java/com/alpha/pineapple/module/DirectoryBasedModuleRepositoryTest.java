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

package com.alpha.pineapple.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Unit test of the class <code>DirectoryBasedModuleRepositoryImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class DirectoryBasedModuleRepositoryTest {

	/**
	 * Environment ID.
	 */
	static final String DEEP_ENV = "deep-env";

	/**
	 * Module ID.
	 */
	static final String MODULE_1_ID = "module1";

	/**
	 * Module ID.
	 */
	static final String MODULE_0_ID = "module0";

	/**
	 * Object under test.
	 */
	DirectoryBasedModuleRepositoryImpl repository;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock directory provider.
	 */
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Random value
	 */
	String randomHomeValue;

	/**
	 * Random value
	 */
	String randomModulesValue;

	/**
	 * Random value
	 */
	String randomEnvironment;

	/**
	 * Random value
	 */
	String randomEnvironment2;

	/**
	 * Random value
	 */
	String randomModelContent;

	/**
	 * Random value
	 */
	String randomModuleId;

	@Before
	public void setUp() throws Exception {

		randomHomeValue = RandomStringUtils.randomAlphabetic(10);
		randomModulesValue = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment2 = RandomStringUtils.randomAlphabetic(10);
		randomModelContent = RandomStringUtils.randomAlphabetic(2000);
		randomModuleId = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create repository
		repository = new DirectoryBasedModuleRepositoryImpl();

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create runtime directory resolver
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// inject directory resolver
		ReflectionTestUtils.setField(repository, "runtimeDirectoryProvider", runtimeDirectoryProvider,
				RuntimeDirectoryProvider.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(repository, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);

	}

	@After
	public void tearDown() throws Exception {
		moduleMother = null;
		testDirectory = null;
		repository = null;
	}

	/**
	 * Complete mock provider with custom directory.
	 * 
	 * @param repositoryDirectory
	 *            custom directory.
	 */
	void completeProviderMockSetupWithCustomDirectory(File repositoryDirectory) {
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(repositoryDirectory);
		EasyMock.replay(runtimeDirectoryProvider);
		ReflectionTestUtils.setField(repository, "runtimeDirectoryProvider", runtimeDirectoryProvider,
				RuntimeDirectoryProvider.class);
	}

	/**
	 * Create modules directory.
	 * 
	 * @return modules directory.
	 */
	File createModulesDirectory() {

		// define custom pineapple runtime directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);

		// define modules directory
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);

		// create modules directory
		assertTrue(repositoryDirectory.mkdirs());
		return repositoryDirectory;
	}

	/**
	 * Can create repository instance
	 */
	@Test
	public void testCreateInstance() {
		assertNotNull(repository);
	}

	/**
	 * Test that repository can be initialized at default location.
	 */
	@Test
	public void testInitializeAtDefaultDirectory() {

		// initialize repository
		repository.initialize();

		// test by getting initialized repository directory
		assertNotNull(repository.getModuleRepositoryDirectory());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that repository can be initialized.
	 */
	@Test
	public void testInitializeByRefresh() {

		// initialize repository
		repository.initialize();

		// test by getting initialized repository directory
		assertNotNull(repository.getModuleRepositoryDirectory());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test expected default default location.
	 */
	@Test
	public void testDefaultDirectory() {

		// initialize repository
		repository.initialize();

		// test by getting initialized repository directory
		assertEquals(testDirectory, repository.getModuleRepositoryDirectory());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that repository can be initialized at custom location.
	 */
	@Test
	public void testInitializeAtCustomDirectory() {

		// complete mock provider setup with custom directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// test
		assertEquals(repositoryDirectory, repository.getModuleRepositoryDirectory());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that repository can be initialized with refresh at custom location.
	 */
	@Test
	public void testInitializeWithRefreshAtCustomDirectory() {

		// complete mock provider setup with custom directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// trigger initialization by getting directory
		repository.initialize();

		// test
		assertEquals(repositoryDirectory, repository.getModuleRepositoryDirectory());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that custom location isn't created if it doesn't exist.
	 */
	@Test
	public void testCustomDirectoryIsntCreatedIfItDoesntExist() {

		// complete mock provider setup with custom directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// test directory doesn't exist
		assertFalse(repositoryDirectory.exists());

		// initialize repository
		repository.initialize();

		// test
		assertEquals(repositoryDirectory, repository.getModuleRepositoryDirectory());

		// test directory doesn't exist
		assertFalse(repositoryDirectory.exists());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that repository doesn't contain any modules if location doesn't exist.
	 */
	@Test
	public void testDoesntContainAnyModulesIfDirectoryDoesntExist() {

		// complete mock provider setup with custom directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// test directory doesn't exist
		assertFalse(repositoryDirectory.exists());

		// initialize repository
		repository.initialize();

		// test directory doesn't exist
		assertFalse(repositoryDirectory.exists());

		// test repository contains no modules
		assertEquals(0, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that zero modules exists at newly created custom location.
	 */
	@Test
	public void testGetInfosReturnsZeroModules() {

		// complete mock provider setup with custom directory
		File runtimeDirectory = new File(this.testDirectory, randomHomeValue);
		File repositoryDirectory = new File(runtimeDirectory, randomModulesValue);
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// test directory doesn't exist
		assertFalse(repositoryDirectory.exists());

		// initialize repository
		repository.initialize();

		// test repository contains no modules
		assertEquals(0, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that one module exists at custom location.
	 */
	@Test
	public void testGetInfosReturnsOneModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create single module directory
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());

		// initialize repository
		repository.initialize();

		// test repository contains expected modules
		assertEquals(1, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that two modules exists at custom location.
	 */
	@Test
	public void testGetInfosReturnsTwoModules() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module directories
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());
		moduleDirectory = new File(repositoryDirectory, MODULE_1_ID);
		assertTrue(moduleDirectory.mkdirs());

		// initialize repository
		repository.initialize();

		// test repository contains expected modules
		assertEquals(2, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that expected modules is registered in repository.
	 */
	@Test
	public void testContainsModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create two module directory
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());
		moduleDirectory = new File(repositoryDirectory, MODULE_1_ID);
		assertTrue(moduleDirectory.mkdirs());

		// initialize repository
		repository.initialize();

		// test repository contains expected modules
		assertTrue(repository.contains(MODULE_0_ID));
		assertTrue(repository.contains(MODULE_1_ID));

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that contains module fails for module with undefined name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsContainsForModuleWithNullName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// get to trigger exception
		repository.contains(null);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that contains module fails for module with empty name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsContainsForModuleWithEmptyName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// get to trigger exception
		repository.contains("");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that get fails if module repository isn't initialized.
	 */
	@Test(expected = ModuleNotFoundException.class)
	public void testFailsGetIfRepositorIsntInitialized() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create single module directory
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());
		moduleDirectory = new File(repositoryDirectory, MODULE_1_ID);
		assertTrue(moduleDirectory.mkdirs());

		// get to trigger exception
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
	}

	/**
	 * Test that expected module can be looked up from repository.
	 */
	@Test
	public void testGet() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create single module directory
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());
		moduleDirectory = new File(repositoryDirectory, MODULE_1_ID);
		assertTrue(moduleDirectory.mkdirs());

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that get module fails for module with undefined name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsGetForModuleWithNullName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// get to trigger exception
		repository.get(null);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that get module fails for module with empty name.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsGetForModuleWithEmptyName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// get to trigger exception
		repository.get("");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be initialized which contains nothing but the module
	 * directory, i.e. no module descriptor, e.g. module.xml file and no models
	 * directory and model files.
	 */
	@Test
	public void testCanInitializeModuleWithNoModuleDescriptorAndNoModelsDirectory() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithWithNoModuleXmlAndNoModelsDirectory(repositoryDirectory, MODULE_0_ID);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(0, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(false, repository.get(MODULE_0_ID).isDescriptorDefined());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be initialized which contains a module.xml file and no
	 * models directory and model files.
	 */
	@Test
	public void testCanInitializeModuleWithNoModelsDirectoryAndModels() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithNoModels(repositoryDirectory, MODULE_0_ID);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(0, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(true, repository.get(MODULE_0_ID).isDescriptorDefined());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be initialized which contains a module.xml file and no
	 * model files.
	 */
	@Test
	public void testCanInitializeModuleWithNoModels() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithNoModels(repositoryDirectory, MODULE_0_ID);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(0, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(true, repository.get(MODULE_0_ID).isDescriptorDefined());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be initialized which contains a module.xml file and one
	 * model files.
	 */
	@Test
	public void testCanInitializeModuleWithOneModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(1, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(DEEP_ENV, repository.get(MODULE_0_ID).getModelEnvironments()[0]);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be resolved which contains a module.xml file and one
	 * model files.
	 */
	@Test
	public void testCanResolveModuleWithOneModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		ModuleInfo moduleInfo = repository.resolveModule(MODULE_0_ID, DEEP_ENV);
		assertNotNull(moduleInfo);
		assertEquals(MODULE_0_ID, moduleInfo.getId());
		assertEquals(1, moduleInfo.getModelEnvironments().length);
		assertEquals(DEEP_ENV, moduleInfo.getModelEnvironments()[0]);
		assertEquals(true, repository.get(MODULE_0_ID).isDescriptorDefined());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that resolution of module fails if no model exist for requested
	 * environment.
	 */
	@Test(expected = ModelNotFoundException.class)
	public void testFailsResolutionOfModuleWithOneModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository
		repository.initialize();

		// test repository contains expected module
		repository.resolveModule(MODULE_0_ID, "another environment");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * < * Test that resolution of module fails if module doesn't exist.
	 */
	@Test(expected = ModuleNotFoundException.class)
	public void testFailsResolutionOfUnknownModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(repositoryDirectory).times(2);
		EasyMock.replay(runtimeDirectoryProvider);
		ReflectionTestUtils.setField(repository, "runtimeDirectoryProvider", runtimeDirectoryProvider,
				RuntimeDirectoryProvider.class);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// trigger initialization by getting module
		// test repository contains expected module
		repository.resolveModule("another-module", "another environment");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that repository can be refreshed to reflect the addition of new modules.
	 */
	@Test
	public void testRefreshDetectsAddedModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(repositoryDirectory).times(2);
		EasyMock.replay(runtimeDirectoryProvider);
		ReflectionTestUtils.setField(repository, "runtimeDirectoryProvider", runtimeDirectoryProvider,
				RuntimeDirectoryProvider.class);

		// trigger initialization by getting directory
		repository.initialize();

		// test repository contains expected modules
		assertEquals(0, repository.getInfos().length);

		// create module directories
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());

		// trigger initialization
		repository.initialize();

		// test repository contains expected modules
		assertEquals(1, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);

	}

	/**
	 * Test that repository can be refreshed to reflect the removal of a module.
	 */
	@Test
	public void testRefreshDetectsRemovedModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(repositoryDirectory).times(2);
		EasyMock.replay(runtimeDirectoryProvider);
		ReflectionTestUtils.setField(repository, "runtimeDirectoryProvider", runtimeDirectoryProvider,
				RuntimeDirectoryProvider.class);

		// create module directories
		File moduleDirectory = new File(repositoryDirectory, MODULE_0_ID);
		assertTrue(moduleDirectory.mkdirs());

		// initialize repository
		repository.initialize();

		// test repository contains expected modules
		assertEquals(1, repository.getInfos().length);

		// delete module directories
		assertTrue(moduleDirectory.delete());

		// trigger initialization by getting directory
		repository.initialize();

		// test repository contains expected modules
		assertEquals(0, repository.getInfos().length);

		// test
		EasyMock.verify(runtimeDirectoryProvider);

	}

	/**
	 * Test that existing model can be saved.
	 * 
	 * @throws IOException
	 *             If test fails.
	 */
	@Test
	public void testCanSaveExistingModel() throws IOException {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// test
		assertEquals(randomModuleId, repository.get(randomModuleId).getId());
		assertEquals(1, repository.get(randomModuleId).getModelEnvironments().length);
		assertEquals(randomEnvironment, repository.get(randomModuleId).getModelEnvironments()[0]);

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// create checksum for model
		File modelsDirectory = new File(info.getDirectory(), "models");
		File modelFile = new File(modelsDirectory, randomEnvironment + ".xml");
		long checksum = FileUtils.checksumCRC32(modelFile);

		// save model
		repository.saveModel(info, randomEnvironment, randomModelContent);

		// create checksum for updated model
		long checksum2 = FileUtils.checksumCRC32(modelFile);

		// test
		assertFalse(checksum == checksum2);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that new model can be saved.
	 * 
	 * @throws IOException
	 *             If test fails.
	 */
	@Test
	public void testCanSaveNewModel() throws IOException {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// save model
		repository.saveModel(info, randomEnvironment2, randomModelContent);

		// test model exists
		File modelsDirectory = new File(info.getDirectory(), "models");
		File modelFile = new File(modelsDirectory, randomEnvironment + ".xml");
		assertTrue(modelFile.exists());
		assertTrue(modelFile.isFile());

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that newly save model is added to module info.
	 * 
	 * @throws IOException
	 *             If test fails.
	 */
	@Test
	public void testCanNewlySavedModelIsStoredInModuleInfo() throws IOException {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// save model
		repository.saveModel(info, randomEnvironment2, randomModelContent);

		// test
		assertEquals(randomModuleId, repository.get(randomModuleId).getId());
		assertEquals(2, repository.get(randomModuleId).getModelEnvironments().length);
		assertTrue(info.containsModel(randomEnvironment));
		assertTrue(info.containsModel(randomEnvironment2));

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that null model is saved as empty file.
	 * 
	 * @throws IOException
	 *             If test fails.
	 */
	@Test
	public void testNullModelIsSavedAsEmptyFile() throws IOException {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// save model
		repository.saveModel(info, randomEnvironment2, null);

		// test
		assertEquals(randomModuleId, repository.get(randomModuleId).getId());
		assertEquals(2, repository.get(randomModuleId).getModelEnvironments().length);
		assertTrue(info.containsModel(randomEnvironment));
		assertTrue(info.containsModel(randomEnvironment2));

		// test model exists
		File modelsDirectory = new File(info.getDirectory(), "models");
		File modelFile = new File(modelsDirectory, randomEnvironment2 + ".xml");
		assertTrue(modelFile.exists());
		assertTrue(modelFile.isFile());

		// test model is empty
		assertEquals("", FileUtils.readFileToString(modelFile));

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that attempt to deleted unknown module fails.
	 */
	@Test(expected = ModuleNotFoundException.class)
	public void testFailsToDeleteUnknownModule() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository and test
		repository.initialize();
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(1, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(DEEP_ENV, repository.get(MODULE_0_ID).getModelEnvironments()[0]);

		// delete module
		repository.delete(randomModuleId);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that attempt to deleted module with undefined name fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteModuleWithNullName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository and test
		repository.initialize();
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(1, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(DEEP_ENV, repository.get(MODULE_0_ID).getModelEnvironments()[0]);

		// delete module
		repository.delete(null);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that attempt to deleted module with empty name fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteModuleWithEmptyName() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, MODULE_0_ID, DEEP_ENV);

		// initialize repository and test
		repository.initialize();
		assertEquals(MODULE_0_ID, repository.get(MODULE_0_ID).getId());
		assertEquals(1, repository.get(MODULE_0_ID).getModelEnvironments().length);
		assertEquals(DEEP_ENV, repository.get(MODULE_0_ID).getModelEnvironments()[0]);

		// delete module
		repository.delete("");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that module can be deleted which contains a module.xml file and one
	 * model files.
	 */
	@Test
	public void testCanDeleteModuleWithOneModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository and test
		repository.initialize();
		assertEquals(randomModuleId, repository.get(randomModuleId).getId());
		assertEquals(1, repository.get(randomModuleId).getModelEnvironments().length);
		assertEquals(randomEnvironment, repository.get(randomModuleId).getModelEnvironments()[0]);

		// delete module
		repository.delete(randomModuleId);

		assertFalse(repository.contains(randomModuleId));

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that delete model fails for undefined module info.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsDeleteModelForUndefinedModuleInfo() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// initialize repository
		repository.initialize();

		// invoke to trigger exception
		repository.deleteModel(null, randomEnvironment);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that delete model fails for undefined environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsDeleteModelForUndefinedEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.deleteModel(info, null);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model can be deleted.
	 */
	public void testDeleteModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// test model exists
		ModuleInfo info = repository.get(randomModuleId);
		assertNotNull(repository.resolveModule(randomModuleId, randomEnvironment));
		assertTrue(info.containsModel(randomEnvironment));

		// delete
		repository.deleteModel(info, randomEnvironment);

		// test
		assertFalse(info.containsModel(randomEnvironment));
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that delete model fails for empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsDeleteModelForEmptyEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.deleteModel(info, "");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that delete model fails for unknown environment.
	 */
	@Test(expected = ModelNotFoundException.class)
	public void testFailsDeleteModelForUnknownEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.deleteModel(info, randomEnvironment2);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that delete model fails twice.
	 */
	@Test(expected = ModelNotFoundException.class)
	public void testFailsToDeleteModelTwice() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.deleteModel(info, randomEnvironment);
		repository.deleteModel(info, randomEnvironment);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model can be created.
	 */
	public void testCreateModel() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// create model
		repository.createModel(info, randomEnvironment2);

		// test model exists
		assertNotNull(repository.resolveModule(randomModuleId, randomEnvironment2));
		assertTrue(info.containsModel(randomEnvironment2));

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model creation fails for undefined module info.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsCreateModelForUndefinedModuleInfo() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// invoke to trigger exception
		repository.createModel(null, randomEnvironment);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model creation fails for undefined environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsCreateModelForUnknownEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.createModel(info, null);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model creation fails for empty environment.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsCreateModelForEmptyEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.createModel(info, "");

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that model creation fails for existing environment.
	 */
	@Test(expected = ModelAlreadyExistsException.class)
	public void testFailsCreateModelForExistingEnvironment() {

		// complete mock provider setup with custom directory
		File repositoryDirectory = createModulesDirectory();
		completeProviderMockSetupWithCustomDirectory(repositoryDirectory);

		// create module
		moduleMother.createModuleWithSingleEmptyModel(repositoryDirectory, randomModuleId, randomEnvironment);

		// initialize repository
		repository.initialize();

		// get module info
		ModuleInfo info = repository.get(randomModuleId);

		// invoke to trigger exception
		repository.createModel(info, randomEnvironment);

		// test
		EasyMock.verify(runtimeDirectoryProvider);
	}

}
