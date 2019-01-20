/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.docker.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.alpha.pineapple.docker.DockerConstants;
import com.alpha.pineapple.docker.model.rest.JsonMessage;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * Unit test of the class {@linkplain ModelUtils}.
 *
 */
public class ModelUtilsTest {

	/**
	 * Docker REST model object factory.
	 */
	ObjectFactory objectFactory;

	/**
	 * Test that for null input, null is returned.
	 */
	@Test
	public void testRemoveLfFromStreamWithNullReturnsNull() {
		String result = ModelUtils.remoteLfFromStreamUpdate(null);
		assertNull(result);
	}

	/**
	 * Test that for text without LF nothing is modified.
	 */
	@Test
	public void testRemoveLfFromStreamWithNoLfNothingIsModified() {
		String input = RandomStringUtils.randomAlphabetic(10);
		String result = ModelUtils.remoteLfFromStreamUpdate(input);
		assertEquals(input, result);
	}

	/**
	 * Test that for text with LF then LF is removed..
	 */
	@Test
	public void testRemoveLfFromStreamWithLfThenLfIsRemoved() {
		String input = RandomStringUtils.randomAlphabetic(10);
		String inputWithLf = input + "\n";
		String result = ModelUtils.remoteLfFromStreamUpdate(inputWithLf);
		assertEquals(input, result);
	}

	/**
	 * Test that true is returned for null image.
	 */
	@Test
	public void testReturnsTrueForNullImage() {
		assertTrue(ModelUtils.isNoImageRepoTagsDefined(null));
	}

	/**
	 * Test that true is returned for empty list of repo tags.
	 */
	@Test
	public void testReturnsTrueEmptyListOfRepoTags() {
		objectFactory = new ObjectFactory();
		ListedImage image = objectFactory.createListedImage();
		assertTrue(ModelUtils.isNoImageRepoTagsDefined(image));
	}

	/**
	 * Test that false is returned for list of repo tags with one element.
	 */
	@Test
	public void testReturnsFalseForListOfRepoTagsWithOneElement() {
		objectFactory = new ObjectFactory();
		ListedImage image = objectFactory.createListedImage();
		List<String> tagsList = image.getRepoTags();
		tagsList.add(RandomStringUtils.randomAlphabetic(10));
		assertFalse(ModelUtils.isNoImageRepoTagsDefined(image));
	}

	/**
	 * Test that true is returned for list of repo tags with one element with the
	 * none:none name.
	 */
	@Test
	public void testReturnsTrueForListOfRepoTagsWithOneElementWithNoneName() {
		objectFactory = new ObjectFactory();
		ListedImage image = objectFactory.createListedImage();
		List<String> tagsList = image.getRepoTags();
		tagsList.add(DockerConstants.UNDEFINED_REPO_TAG);
		assertTrue(ModelUtils.isNoImageRepoTagsDefined(image));
	}

	/**
	 * Test that false is returned for list of repo tags with two elements.
	 */
	@Test
	public void testReturnsFalseForListOfRepoTagsWithTwoElements() {
		objectFactory = new ObjectFactory();
		ListedImage image = objectFactory.createListedImage();
		List<String> tagsList = image.getRepoTags();
		tagsList.add(RandomStringUtils.randomAlphabetic(10));
		tagsList.add(RandomStringUtils.randomAlphabetic(10));
		assertFalse(ModelUtils.isNoImageRepoTagsDefined(image));
	}

	/**
	 * Test that true is returned for list of repo tags with two elements with the
	 * none:none name.
	 */
	@Test
	public void testReturnsTrueForListOfRepoTagsWithTwoElementsWithNoneName() {
		objectFactory = new ObjectFactory();
		ListedImage image = objectFactory.createListedImage();
		List<String> tagsList = image.getRepoTags();
		tagsList.add(DockerConstants.UNDEFINED_REPO_TAG);
		tagsList.add(DockerConstants.UNDEFINED_REPO_TAG);
		assertTrue(ModelUtils.isNoImageRepoTagsDefined(image));
	}

	/**
	 * Test that true if string contain separator.
	 */
	@Test
	public void testContainsSeparatorReturnsTrueIfStringContainsSeparator() {
		assertTrue(ModelUtils.containsSeparator("alpha:alpha"));
	}

	/**
	 * Test that true if string contain separator.
	 */
	@Test
	public void testContainsSeparatorReturnsTrueIfStringContainsSeparator2() {
		assertTrue(ModelUtils.containsSeparator(":alpha"));
	}

	/**
	 * Test that true if string contain separator.
	 */
	@Test
	public void testContainsSeparatorReturnsTrueIfStringContainsSeparator3() {
		assertTrue(ModelUtils.containsSeparator("alpha:"));
	}

	/**
	 * Test that true if string contain multiple separators.
	 */
	@Test
	public void testContainsSeparatorReturnsTrueIfStringContainsMultipleSeparators() {
		assertTrue(ModelUtils.containsSeparator("::::"));
	}

	/**
	 * Test that false if string doens't contain separator.
	 */
	@Test
	public void testContainsSeparatorReturnsFalseIfStringDoesntContainsSeparator() {
		assertFalse(ModelUtils.containsSeparator("alpha"));
	}

	/**
	 * Test that false if string doens't contain separator.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsSeparatorThrowsExceptionIfStringIsUndefined() {
		ModelUtils.containsSeparator(null);
	}

	/**
	 * Test that true is returned if container image name contains separator.
	 */
	@Test
	public void testReturnsTrueIfContainerImageNameContainsTag() {
		objectFactory = new ObjectFactory();
		ListedContainer container = objectFactory.createListedContainer();
		container.setImage("alpha:tag");
		assertTrue(ModelUtils.isImageRepoTagDefined(container));
	}

	/**
	 * Test that false is returned if container image name doens't contains
	 * separator.
	 */
	@Test
	public void testReturnsFalseIfContainerImageNameDoesntContainsTag() {
		objectFactory = new ObjectFactory();
		ListedContainer container = objectFactory.createListedContainer();
		container.setImage("alpha");
		assertFalse(ModelUtils.isImageRepoTagDefined(container));
	}

	/**
	 * Test that false is returned if container image name doens't contains
	 * separator.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExceptionIfContainerImageNameIsUndefined() {
		objectFactory = new ObjectFactory();
		ListedContainer container = objectFactory.createListedContainer();
		container.setImage(null);
		ModelUtils.isImageRepoTagDefined(container);
	}

	/**
	 * Test that false is returned for JSON message with no error message.
	 */
	@Test
	public void testReturnsFalseForUndefinedErrorMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setError(null);
		assertFalse(ModelUtils.containsErrorUpdate(message));
	}

	/**
	 * Test that false is returned for JSON message with empty error message.
	 */
	@Test
	public void testReturnsFalseForEmptyErrorMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setError("");
		assertFalse(ModelUtils.containsErrorUpdate(message));
	}
	
	/**
	 * Test that true is returned for JSON message with error message.
	 */
	@Test
	public void testReturnsTrueForDefinedErrorMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setError(RandomStringUtils.randomAlphabetic(10));
		assertTrue(ModelUtils.containsErrorUpdate(message));
	}

	/**
	 * Test that false is returned for JSON message with no stream message.
	 */
	@Test
	public void testReturnsFalseForUndefinedStreamMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setStream(null);
		assertFalse(ModelUtils.containsStreamUpdate(message));
	}

	/**
	 * Test that false is returned for JSON message with empty stream message.
	 */
	@Test
	public void testReturnsFalseForEmptyStreamMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setStream("");
		assertFalse(ModelUtils.containsStreamUpdate(message));
	}
	
	/**
	 * Test that true is returned for JSON message with stream message.
	 */
	@Test
	public void testReturnsTrueForDefinedStreamMessage() {
		objectFactory = new ObjectFactory();
		JsonMessage message = objectFactory.createJsonMessage();
		message.setStream(RandomStringUtils.randomAlphabetic(10));
		assertTrue(ModelUtils.containsStreamUpdate(message));
	}
	
}
