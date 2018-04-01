/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple.test.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test of the {@link DoesFileExist} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.hamcrest-config.xml" })
public class IsFileSmallerThanTest {

	/**
	 * Object under test.
	 */
	@SuppressWarnings("unchecked")
	Matcher matcher;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Random file name.
	 */
	String randomFileName;

	/**
	 * Mock description.
	 */
	Description description;

	@Before
	public void setUp() throws Exception {
		randomFileName = RandomStringUtils.randomAlphabetic(10) + "-.txt";

		// create mock description
		description = EasyMock.createMock(Description.class);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
	}

	@After
	public void tearDown() throws Exception {
		matcher = null;
		description = null;
		testDirectory = null;
	}

	/**
	 * Test that smaller file is a positive match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testSmallerFileIsPositiveMatch() throws Exception {
		long legalSize = 1024 * 1024 * 1; // 1MB

		// create file bigger then the maximum siz
		File localFile = new File(testDirectory, randomFileName);
		RandomAccessFile randomFile = new RandomAccessFile(localFile, "rw");
		randomFile.setLength(legalSize - 100);
		randomFile.close();

		// create matcher
		matcher = IsFileSizeSmaller.isFileSizeSmaller(legalSize);

		// test
		assertTrue(matcher.matches(localFile));
	}

	/**
	 * Test that larger file is a negative match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLargerFileIsNegativeMatch() throws Exception {
		long legalSize = 1024 * 1024 * 1; // 1MB

		// create file bigger then the maximum siz
		File localFile = new File(testDirectory, randomFileName);
		RandomAccessFile randomFile = new RandomAccessFile(localFile, "rw");
		randomFile.setLength(legalSize + 100);
		randomFile.close();

		// create matcher
		matcher = IsFileSizeSmaller.isFileSizeSmaller(legalSize);

		// test
		assertFalse(matcher.matches(localFile));
	}

}
