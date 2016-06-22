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
package com.alpha.pineapple.docker;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Integration test of the {@linkplain DockerClientImpl} class.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.docker-config.xml" })
public class DockerClientImplIntegrationTest {

    /**
     * Docker client.
     */
    @Resource
    DockerClient dockerClient;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that client instance can be created in application context.
     */
    @Test
    public void testCanGetInstance() throws Exception {
	assertNotNull(dockerClient);
    }

    /**
     * Test that image creation client fails with undefined session.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateImageFailsOnUndefinedSession() throws Exception {
	ImageInfo info = EasyMock.createMock(ImageInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	dockerClient.createImage(null, info, result);
    }

    /**
     * Test that image creation client fails with undefined image info.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateImageFailsOnUndefinedImageInfo() throws Exception {
	DockerSession session = EasyMock.createMock(DockerSession.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	dockerClient.createImage(session, null, result);
    }

    /**
     * Test that image creation client fails with undefined result.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateImageFailsOnUndefinedResult() throws Exception {
	DockerSession session = EasyMock.createMock(DockerSession.class);
	ImageInfo info = EasyMock.createMock(ImageInfo.class);
	dockerClient.createImage(session, info, null);
    }

    /**
     * Test that container creation client fails with undefined session.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContainerFailsOnUndefinedSession() throws Exception {
	ContainerInfo info = EasyMock.createMock(ContainerInfo.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	dockerClient.createContainer(null, info, result);
    }

    /**
     * Test that container creation client fails with undefined image info.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContainerFailsOnUndefinedImageInfo() throws Exception {
	DockerSession session = EasyMock.createMock(DockerSession.class);
	ExecutionResult result = EasyMock.createMock(ExecutionResult.class);
	dockerClient.createImage(session, null, result);
    }

    /**
     * Test that container creation client fails with undefined result.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateContainerFailsOnUndefinedResult() throws Exception {
	DockerSession session = EasyMock.createMock(DockerSession.class);
	ContainerInfo info = EasyMock.createMock(ContainerInfo.class);
	dockerClient.createContainer(session, info, null);
    }

}
