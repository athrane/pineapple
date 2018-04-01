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

package com.alpha.pineapple.docker.model;

import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_CENTOS_REPOSITORY;
import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the class {@linkplain InfoBuilderImpl}.
 *
 */
public class InfoBuilderImplTest {

	/**
	 * Image info.
	 */
	ImageInfo imageInfo;

	/**
	 * Container info.
	 */
	ContainerInfo containerInfo;

	/**
	 * Container instance info.
	 */
	ContainerInstanceInfo containerInstanceInfo;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Subject under test.
	 */
	InfoBuilderImpl infobuilder;

	@Before
	public void setUp() throws Exception {
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);
		infobuilder = new InfoBuilderImpl();
	}

	@After
	public void tearDown() throws Exception {
		imageInfo = null;
	}

	/**
	 * Test that instance is created with custom repository and empty tag.
	 */
	@Test
	public void testBuildImageInfoWithRepositoryAndEmptyTag() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");

		// test
		assertNotNull(imageInfo);
		assertEquals(randomValue, imageInfo.getRepository());
		assertEquals("", imageInfo.getTag());
	}

	/**
	 * Test failure to create image info with null repository.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateImageInfoWithNullRepository() {
		imageInfo = infobuilder.buildImageInfo(null);
	}

	/**
	 * Test failure to create image info with empty repository.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateImageInfoWithEmptyRepository() {
		imageInfo = infobuilder.buildImageInfo(null);
	}

	/**
	 * Test failure to create image info with null tag.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateImageInfoWithNullTag() {
		imageInfo = infobuilder.buildImageInfo(randomValue, null);
	}

	/**
	 * Test that image info is created with custom repository and tag.
	 */
	@Test
	public void testBuildImageInfoWithRepositoryAndTag() {
		imageInfo = infobuilder.buildImageInfo(randomValue, randomValue2);

		// test
		assertNotNull(imageInfo);
		assertEquals(randomValue, imageInfo.getRepository());
		assertEquals(randomValue2, imageInfo.getTag());
	}

	/**
	 * Test that image info is created with custom repository.
	 */
	@Test
	public void testBuildImageInfoWithRepository() {
		imageInfo = infobuilder.buildImageInfo(randomValue);

		// test
		assertNotNull(imageInfo);
		assertEquals(randomValue, imageInfo.getRepository());
		assertEquals(LATEST_IMAGE_TAG, imageInfo.getTag());
	}

	/**
	 * Test that image info is created from fully qualified image name.
	 */
	@Test
	public void testBuildImageInfoFromFqName() {
		String fqName = randomValue + ":" + randomValue2;
		imageInfo = infobuilder.buildImageInfoFromFQName(fqName);

		// test
		assertNotNull(imageInfo);
		assertEquals(randomValue, imageInfo.getRepository());
		assertEquals(randomValue2, imageInfo.getTag());
	}

	/**
	 * Test that image info with is created with default values.
	 */
	@Test
	public void testBuildDefaultImageInfo() {
		imageInfo = infobuilder.buildImageInfo();

		// test
		assertNotNull(imageInfo);
		assertEquals(DEFAULT_CENTOS_REPOSITORY, imageInfo.getRepository());
		assertEquals(LATEST_IMAGE_TAG, imageInfo.getTag());
	}

	/**
	 * Test that container info is created with custom repository.
	 */
	@Test
	public void testBuildContainerInfoWithRepository() {
		imageInfo = infobuilder.buildImageInfo();
		containerInfo = infobuilder.buildContainerInfo(randomValue, imageInfo);

		// test
		assertNotNull(imageInfo);
		assertEquals(DEFAULT_CENTOS_REPOSITORY, imageInfo.getRepository());
		assertEquals(LATEST_IMAGE_TAG, imageInfo.getTag());
	}

	/**
	 * Test that container info creation fails with null image.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToBuildContainerInfoWithNullImage() {
		containerInfo = infobuilder.buildContainerInfo(randomValue, null);
	}

	/**
	 * Test that container instance info creation fails with null id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToBuildContainerInstanceInfoWithNullId() {
		imageInfo = infobuilder.buildImageInfo();
		containerInfo = infobuilder.buildContainerInfo(randomValue, imageInfo);
		containerInstanceInfo = infobuilder.buildInstanceInfo(null, containerInfo);
	}

	/**
	 * Test that container instance info creation fails with null image.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToBuildContainerInstanceInfoWithNullImage() {
		containerInstanceInfo = infobuilder.buildInstanceInfo(randomValue, null);
	}

}
