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
package com.alpha.pineapple.docker.model;

import static org.junit.Assert.*;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of the class {@linkplain ImageInfoImpl}.
 */
public class ImageInfoImplTest {

	/**
	 * Subject under test.
	 */
	ImageInfoImpl imageInfo;

	/**
	 * Random repository.
	 */
	String randomRepository;

	/**
	 * Random tag.
	 */
	String randomTag;

	@Before
	public void setUp() throws Exception {
		randomRepository = RandomStringUtils.randomAlphabetic(10).toLowerCase();
		randomTag = RandomStringUtils.randomAlphabetic(10);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsTagDefined() {
		imageInfo = new ImageInfoImpl(randomRepository, randomTag);
		assertTrue(imageInfo.isTagDefined());
	}

	@Test
	public void testIsTagDefinedReturnsFalseIfTagIsEmpty() {
		imageInfo = new ImageInfoImpl(randomRepository, "");
		assertFalse(imageInfo.isTagDefined());
	}

	@Test
	public void testIsTagDefinedReturnsFalseIfTagIsUndefined() {
		imageInfo = new ImageInfoImpl(randomRepository, null);
		assertFalse(imageInfo.isTagDefined());
	}

}
