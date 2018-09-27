/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.credential.encryption;

import static com.alpha.pineapple.CoreConstants.CREDENTIALS_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.credential.ConfigurationInfo;
import com.alpha.pineapple.credential.CredentialConfigurationMarshaller;
import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.credential.CredentialInfoImpl;
import com.alpha.pineapple.credential.EnvironmentInfo;
import com.alpha.pineapple.credential.EnvironmentInfoImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the class
 * {@linkplain PasswordEncryptingCredentialConfigurationMarshallerImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PasswordEncryptingCredentialConfigurationMarshallerImplIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Subject under test.
	 */
	@Resource
	CredentialConfigurationMarshaller credentialConfigurationMarshaller;

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Text encryptor.
	 */
	@Resource
	PBEStringEncryptor textEncryptor;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Random environment id.
	 */
	String randomEnvId;

	/**
	 * Random environment id.
	 */
	String randomEnvId2;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random user.
	 */
	String randomUser;

	/**
	 * Random password.
	 */
	String randomPassword;

	/**
	 * Random ID.
	 */
	String randomCredentialId;

	@Before
	public void setUp() throws Exception {

		// initialize random fields
		randomEnvId = RandomStringUtils.randomAlphabetic(10);
		randomEnvId2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomCredentialId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// delete credential configuration
		File crdentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		if (crdentialsFile.exists())
			FileUtils.deleteQuietly(crdentialsFile);
	}

	@After
	public void tearDown() throws Exception {

		// delete credentials configuration
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		if (credentialsFile.exists())
			FileUtils.deleteQuietly(credentialsFile);

		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
	}

	/**
	 * Test that empty configuration can be saved.
	 */
	@Test
	public void testSaveEmptyConfiguration() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		credentialConfigurationMarshaller.save(envConfiguration);

		// test
		File resourcesFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
	}

	/**
	 * Test that configuration can be saved.
	 */
	@Test
	public void testSaveConfiguration() {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);
		credentialConfigurationMarshaller.save(envConfiguration);

		// test
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		assertTrue(credentialsFile.exists());
		assertTrue(credentialsFile.isFile());
	}

	/**
	 * Test that credentials configuration with no environments can be mapped.
	 */
	@Test
	public void testMapWithNoEnvironments() {
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);
	}

	/**
	 * Test that resource configuration with single environments can be mapped.
	 */
	@Test
	public void testMapWithOneEnvironments() {
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(1, configInfo.getEnvironments().length);
	}

	/**
	 * Test that resource configuration with multiple environments can be mapped.
	 */
	@Test
	public void testMapMultipleEnvironments() {
		// initialize repository
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId,
				randomDescription);
		envConfigMother.addEnvironment(randomEnvId, envConfiguration.getEnvironments().getEnvironment());
		envConfigMother.addEnvironment(randomEnvId2, envConfiguration.getEnvironments().getEnvironment());

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertTrue(configInfo.containsEnvironment(randomEnvId));
		assertTrue(configInfo.containsEnvironment(randomEnvId2));
	}

	/**
	 * Test that credential configuration with no environments can be mapped, then
	 * credential configuration with no environments can be mapped again and the
	 * number of environments is zero.
	 */
	@Test
	public void testMapWithNoEnvironmentsTwice() {
		// map
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

		// map
		envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

	}

	/**
	 * Test that credential configuration with no environments can be mapped, then a
	 * environment is added to the configuration info, then credential configuration
	 * with no environments can be mapped again and the number of environments is
	 * zero.
	 */
	@Test
	public void testMapWithNoEnvironmentsTwiceDespiteAddingEnvironment() {
		// map
		Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

		// add environment
		TreeMap<String, CredentialInfo> resourceInfos = new TreeMap<String, CredentialInfo>();
		EnvironmentInfo environmentInfo = new EnvironmentInfoImpl(randomEnvId, randomDescription, resourceInfos);
		configInfo.addEnvironment(environmentInfo);

		// map
		envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(0, configInfo.getEnvironments().length);

	}

	/**
	 * Test that credential configuration with one environment with no credential
	 * can be mapped.
	 * 
	 * The configuration isn't updated (e.g. saved).
	 */
	@Test
	public void testMapEmptyEnvironmentIsntSaved() throws Exception {
		// initialize
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvId);

		// test
		assertFalse(credentialsFile.exists());

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertFalse(credentialsFile.exists());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password isn't encrypted initially in the configuration. The
	 * marshaller will encrypt the password and append the "encrypted:" prefix.
	 */
	@Test
	public void testMapUnencryptedCredentialIsEncrypted() throws Exception {
		// initialize
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, randomPassword);
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(1, configInfo.getEnvironments().length);
		EnvironmentInfo environmentInfo = configInfo.getEnvironments()[0];
		assertTrue(environmentInfo.containsCredential(randomCredentialId));
		assertNotNull(environmentInfo.getCredential(randomCredentialId));
		assertEquals(randomCredentialId, environmentInfo.getCredential(randomCredentialId).getId());
		assertEquals(randomUser, environmentInfo.getCredential(randomCredentialId).getUser());
		assertEquals(encryptedPassword, environmentInfo.getCredential(randomCredentialId).getPassword());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password isn't encrypted initially in the configuration. The
	 * marshaller will encrypt the password and append the "encrypted:" prefix.
	 * 
	 * The configuration is updated (e.g. saved) with the encrypted credential.
	 */
	@Test
	public void testMapUnencryptedCredentialIsSaved() throws Exception {
		// initialize
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, randomPassword);

		// test
		assertFalse(credentialsFile.exists());

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertTrue(credentialsFile.exists());
		assertTrue(credentialsFile.isFile());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password is encrypted initially in the configuration. The
	 * marshaller will map the encrypted password.
	 */
	@Test
	public void testMapEncryptedCredential() throws Exception {
		// initialize
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, encryptedPassword);
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(1, configInfo.getEnvironments().length);
		EnvironmentInfo environmentInfo = configInfo.getEnvironments()[0];
		assertTrue(environmentInfo.containsCredential(randomCredentialId));
		assertNotNull(environmentInfo.getCredential(randomCredentialId));
		assertEquals(randomCredentialId, environmentInfo.getCredential(randomCredentialId).getId());
		assertEquals(randomUser, environmentInfo.getCredential(randomCredentialId).getUser());
		assertEquals(encryptedPassword, environmentInfo.getCredential(randomCredentialId).getPassword());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password is encrypted initially in the configuration. The
	 * marshaller will map the encrypted password. The configuration isn't updated
	 * (e.g. saved) since the state isn't changed.
	 */
	@Test
	public void testMapEncryptedCredentialIsntSaved() throws Exception {
		// initialize
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, encryptedPassword);

		// test
		assertFalse(credentialsFile.exists());

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertFalse(credentialsFile.exists());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password isn't encrypted initially in the configuration, even
	 * though the password is prefixed with the "encrypted:" prefix.
	 * 
	 * The marshaller will encrypt the password and append the "encrypted:" prefix.
	 */
	@Test
	public void testMapUnencryptedCredentialWithTheEncryptedPrefix() throws Exception {
		// initialize
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		String unencryptedPrefixedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(randomPassword).toString();
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, unencryptedPrefixedPassword);
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertNotNull(configInfo.getEnvironments());
		assertEquals(1, configInfo.getEnvironments().length);
		EnvironmentInfo environmentInfo = configInfo.getEnvironments()[0];
		assertTrue(environmentInfo.containsCredential(randomCredentialId));
		assertNotNull(environmentInfo.getCredential(randomCredentialId));
		assertEquals(randomCredentialId, environmentInfo.getCredential(randomCredentialId).getId());
		assertEquals(randomUser, environmentInfo.getCredential(randomCredentialId).getUser());
		assertEquals(encryptedPassword, environmentInfo.getCredential(randomCredentialId).getPassword());
	}

	/**
	 * Test that credential configuration with one environment with one credential
	 * can be mapped.
	 * 
	 * The credential password isn't encrypted initially in the configuration, even
	 * though the password is prefixed with the "encrypted:" prefix.
	 * 
	 * The marshaller will encrypt the password and append the "encrypted:" prefix.
	 * The configuration is updated (e.g. saved) with the encrypted credential.
	 */
	@Test
	public void testMapUnencryptedCredentialWithTheEncryptedPrefixIsSaved() throws Exception {
		// initialize
		File credentialsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), CREDENTIALS_FILE);
		String unencryptedPrefixedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(randomPassword).toString();
		Configuration envConfiguration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvId,
				randomCredentialId, randomUser, unencryptedPrefixedPassword);

		// test
		assertFalse(credentialsFile.exists());

		// map
		ConfigurationInfo configInfo = credentialConfigurationMarshaller.map(envConfiguration);

		// test
		assertNotNull(configInfo);
		assertTrue(credentialsFile.exists());
		assertTrue(credentialsFile.isFile());
	}

	/**
	 * Test that mapped credential object contains expected values.
	 */
	@Test
	public void testMappedCredentialContainsExpectedValues() throws Exception {
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		CredentialInfoImpl info = new CredentialInfoImpl(randomCredentialId, randomUser, encryptedPassword);
		Credential credential = credentialConfigurationMarshaller.mapToCredential(info);

		// test
		assertEquals(randomCredentialId, credential.getId());
		assertEquals(randomUser, credential.getUser());
	}

	/**
	 * Test that credential info object can be mapped to credential object.
	 */
	@Test
	public void testMapCredentialInfoToCredential() throws Exception {
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		CredentialInfoImpl info = new CredentialInfoImpl(randomCredentialId, randomUser, encryptedPassword);
		Credential credential = credentialConfigurationMarshaller.mapToCredential(info);

		// test
		assertNotNull(credential);
	}

	/**
	 * Test that different credential instances are returned on each mapping.
	 */
	@Test
	public void testMapCredentialInfoToCredentialReturnsDifferentInstances() throws Exception {
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		CredentialInfoImpl info = new CredentialInfoImpl(randomCredentialId, randomUser, encryptedPassword);
		Credential credential = credentialConfigurationMarshaller.mapToCredential(info);
		Credential credential2 = credentialConfigurationMarshaller.mapToCredential(info);

		// test
		assertFalse(credential.hashCode() == credential2.hashCode());
	}

	/**
	 * Test that mapped credential object return decrypted password.
	 */
	@Test
	public void testMappedCredentialReturnsDecryptedPassword() throws Exception {
		String encryptedPassword = new StringBuilder().append(CoreConstants.ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		CredentialInfoImpl info = new CredentialInfoImpl(randomCredentialId, randomUser, encryptedPassword);
		Credential credential = credentialConfigurationMarshaller.mapToCredential(info);

		// test
		assertEquals(randomPassword, credential.getPassword());
	}

}
