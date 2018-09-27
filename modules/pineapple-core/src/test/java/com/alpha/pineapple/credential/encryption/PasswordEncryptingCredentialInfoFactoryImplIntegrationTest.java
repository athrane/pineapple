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

import static com.alpha.pineapple.CoreConstants.ENCRYPTED_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.credential.CredentialInfo;
import com.alpha.pineapple.credential.CredentialInfoFactory;

/**
 * Integration test of the class
 * {@linkplain PasswordEncryptingCredentialInfoFactoryImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class PasswordEncryptingCredentialInfoFactoryImplIntegrationTest {

	/**
	 * Subject under test.
	 */
	@Resource
	CredentialInfoFactory credentialInfoFactory;

	/**
	 * Text encryptor.
	 */
	@Resource
	PBEStringEncryptor textEncryptor;

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
	String randomId;

	@Before
	public void setUp() throws Exception {

		// initialize random fields
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that instance can created from context.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(credentialInfoFactory);
	}

	/**
	 * Test that credential info can created.
	 */
	@Test
	public void testCanCreateCredentialInfo() {
		CredentialInfo info = credentialInfoFactory.createCredentialInfo(randomId, randomUser, randomPassword);
		assertNotNull(info);
		assertEquals(randomId, info.getId());
		assertEquals(randomUser, info.getUser());
	}

	/**
	 * Test that credential info contains encrypted password with prefix.
	 */
	@Test
	public void testCredentialInfoReturnsEncryptedPasswordWithPrefix() {
		String encryptedPassword = new StringBuilder().append(ENCRYPTED_PREFIX)
				.append(textEncryptor.encrypt(randomPassword)).toString();
		CredentialInfo info = credentialInfoFactory.createCredentialInfo(randomId, randomUser, randomPassword);
		assertEquals(encryptedPassword, info.getPassword());
	}

	/**
	 * Test that returned credential contains expected values.
	 */
	@Test
	public void testCredentialReturnsExpectedValues() {
		CredentialInfo info = credentialInfoFactory.createCredentialInfo(randomId, randomUser, randomPassword);
		assertEquals(randomId, info.getId());
		assertEquals(randomUser, info.getUser());
	}

}
