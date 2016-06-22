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

package com.alpha.pineapple.execution;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.resource.ResourceInfo;
import com.alpha.pineapple.resource.ResourceRepository;

/**
 * Unit test of the class {@linkplain DefaultResourceResolverImpl}.
 */
public class DefaultResourceResolverImplTest {

    /**
     * SUT.
     */
    ResourceResolver resolver;

    /**
     * Mock resource repository.
     */
    ResourceRepository resourceRepository;

    /**
     * Random resource.
     */
    String randomResource;

    /**
     * Random resource.
     */
    String randomResource2;

    /**
     * Random resource.
     */
    String randomResource3;

    /**
     * Random resource.
     */
    String randomResource4;

    /**
     * Random environment.
     */
    String randomEnvironment;

    @Before
    public void setUp() throws Exception {
	randomResource = RandomStringUtils.randomAlphabetic(16);
	randomResource2 = RandomStringUtils.randomAlphabetic(16);
	randomResource3 = "alpha-" + RandomStringUtils.randomAlphabetic(16);
	randomResource4 = "alpha-" + RandomStringUtils.randomAlphabetic(16);
	randomEnvironment = RandomStringUtils.randomAlphabetic(16);

	// create resolver
	resolver = new DefaultResourceResolverImpl();

	// create an inject mocks
	resourceRepository = EasyMock.createMock(ResourceRepository.class);
	ReflectionTestUtils.setField(resolver, "resourceRepository", resourceRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Can resolve single target.
     */
    @Test
    public void testResolveSingleTarget() {
	String[] result = resolver.resolve(randomResource, randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

    }

    /**
     * Can resolve single target with trailing spaces.
     */
    @Test
    public void testResolveSingleTargetWithTrailingSpaces() {
	String[] result = resolver.resolve(randomResource + "   ", randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

    }

    /**
     * Can resolve single target with trailing spaces.
     */
    @Test
    public void testResolveSingleTargetWithTrailingSpaces2() {
	String[] result = resolver.resolve("   " + randomResource, randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

    }

    /**
     * Can resolve single target in list
     */
    @Test
    public void testResolveSingleTargetInList() {
	String[] result = resolver.resolve("{" + randomResource + "}", randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

    }

    /**
     * Can resolve two targets in list
     */
    @Test
    public void testResolveTwoTargetsInList() {
	String[] result = resolver.resolve("{" + randomResource + "," + randomResource2 + "}", randomEnvironment);

	// test
	assertEquals(2, result.length);
	assertEquals(randomResource, result[0]);
	assertEquals(randomResource2, result[1]);
    }

    /**
     * Can resolve two targets in list with trailing spaces
     */
    @Test
    public void testResolveTwoTargetsInListWithTrailingspaces() {
	String[] result = resolver.resolve("{ " + randomResource + " ," + randomResource2 + " }", randomEnvironment);

	// test
	assertEquals(2, result.length);
	assertEquals(randomResource, result[0]);
	assertEquals(randomResource2, result[1]);
    }

    /**
     * Can resolve exact match in regular expression with list of one resource.
     */
    @Test
    public void testResolveExactMatchInRegularExpression() {

	// create mocks
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getId()).andReturn(randomResource);
	EasyMock.replay(resourceInfo);
	ResourceInfo[] resources = new ResourceInfo[] { resourceInfo };

	// complete mock setup
	EasyMock.expect(resourceRepository.getResources(randomEnvironment)).andReturn(resources);
	EasyMock.replay(resourceRepository);

	// resolve
	String[] result = resolver.resolve("regex:" + randomResource, randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

	// test
	EasyMock.verify(resourceRepository);
	EasyMock.verify(resourceInfo);

    }

    /**
     * Can resolve exact match in regular expression with list of two resources
     * where one is an exact match.
     */
    @Test
    public void testResolveExactMatchInRegularExpression2() {

	// create mocks
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getId()).andReturn(randomResource);
	EasyMock.replay(resourceInfo);
	ResourceInfo resourceInfo2 = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo2.getId()).andReturn(randomResource2);
	EasyMock.replay(resourceInfo2);

	ResourceInfo[] resources = new ResourceInfo[] { resourceInfo, resourceInfo2 };

	// complete mock setup
	EasyMock.expect(resourceRepository.getResources(randomEnvironment)).andReturn(resources);
	EasyMock.replay(resourceRepository);

	// resolve
	String[] result = resolver.resolve("regex:" + randomResource, randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

	// test
	EasyMock.verify(resourceRepository);
	EasyMock.verify(resourceInfo);
	EasyMock.verify(resourceInfo2);
    }

    /**
     * Can resolve match in regular expression with list of two resources where
     * match is zero-or-more-times operation: *
     */
    @Test
    public void testResolveZeroOrMoreTimesMatchInRegularExpression2() {

	// create mocks
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getId()).andReturn(randomResource3);
	EasyMock.replay(resourceInfo);
	ResourceInfo resourceInfo2 = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo2.getId()).andReturn(randomResource4);
	EasyMock.replay(resourceInfo2);

	ResourceInfo[] resources = new ResourceInfo[] { resourceInfo, resourceInfo2 };

	// complete mock setup
	EasyMock.expect(resourceRepository.getResources(randomEnvironment)).andReturn(resources);
	EasyMock.replay(resourceRepository);

	// resolve
	String[] result = resolver.resolve("regex:alpha.*", randomEnvironment);

	// test
	assertEquals(2, result.length);
	assertEquals(randomResource3, result[0]);
	assertEquals(randomResource4, result[1]);

	// test
	EasyMock.verify(resourceRepository);
	EasyMock.verify(resourceInfo);
	EasyMock.verify(resourceInfo2);

    }

    /**
     * Can resolve regular expression despite a space between the reqex
     * identifier and the expression.
     */
    @Test
    public void testResolveWithSpaceInRegularExpression() {

	// create mocks
	ResourceInfo resourceInfo = EasyMock.createMock(ResourceInfo.class);
	EasyMock.expect(resourceInfo.getId()).andReturn(randomResource);
	EasyMock.replay(resourceInfo);
	ResourceInfo[] resources = new ResourceInfo[] { resourceInfo };

	// complete mock setup
	EasyMock.expect(resourceRepository.getResources(randomEnvironment)).andReturn(resources);
	EasyMock.replay(resourceRepository);

	// resolve
	String[] result = resolver.resolve("regex:    " + randomResource, randomEnvironment);

	// test
	assertEquals(1, result.length);
	assertEquals(randomResource, result[0]);

	// test
	EasyMock.verify(resourceRepository);
	EasyMock.verify(resourceInfo);

    }

}
