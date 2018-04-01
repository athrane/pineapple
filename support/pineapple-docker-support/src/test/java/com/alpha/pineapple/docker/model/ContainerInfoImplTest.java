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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationLabelsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationVolumesMap;
import com.alpha.pineapple.docker.model.jaxb.PortMapMap;
import com.alpha.pineapple.docker.model.jaxb.PortSetMap;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationVolumeNullValue;
import com.alpha.pineapple.docker.model.rest.PortBinding;
import com.alpha.pineapple.docker.model.rest.PortSetElementNullValue;

/**
 * Unit test of the class {@linkplain ContainerInfoImpl}.
 *
 */
public class ContainerInfoImplTest {

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Image info.
	 */
	ImageInfo imageInfo;

	/**
	 * Subject under test.
	 */
	ContainerInfo containerInfo;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Info builder.
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
	 * Test that instance is created with expected name.
	 */
	@Test
	public void testHasExpectedName() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		assertEquals(randomValue, containerInfo.getName());
	}

	/**
	 * Test that instance is created with expected image info.
	 */
	@Test
	public void testHasExpectedImageInfo() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		assertEquals(imageInfo, containerInfo.getImageInfo());
	}

	/**
	 * Test that instance is created with defined container configuration..
	 */
	@Test
	public void testHasDefinedContainerConfiguration() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		assertNotNull(containerInfo.getContainerConfiguration());
	}

	/**
	 * Test that container configuration returns defined environment variables list
	 * when queried.
	 */
	@Test
	public void testContainerConfigurationInitiallyReturnsEnvironmentVariablesListWhenQueried() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNotNull(containeConfig.getEnv());
	}

	/**
	 * Test that using internal inspection of container configuration reveals
	 * initial undefined environment variables list.
	 */
	@Test
	public void testInternalInspectionOfContainerConfigurationReturnsUndefinedEnvironmentVariablesList() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(ReflectionTestUtils.getField(containeConfig, "env"));
	}

	/**
	 * Test that container configuration returns defined command list when queried.
	 */
	@Test
	public void testContainerConfigurationInitiallyReturnsCommandListWhenQueried() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNotNull(containeConfig.getCmd());
	}

	/**
	 * Test that using internal inspection of container configuration reveals
	 * initial undefined command list.
	 */
	@Test
	public void testInternalInspectionOfContainerConfigurationReturnsUndefinedCommandList() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(ReflectionTestUtils.getField(containeConfig, "cmd"));
	}

	/**
	 * Test that container configuration returns defined entrypoint list when
	 * queried.
	 */
	@Test
	public void testContainerConfigurationInitiallyReturnsEntryppointListWhenQueried() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNotNull(containeConfig.getEntrypoint());
	}

	/**
	 * Test that using internal inspection of container configuration reveals
	 * initial undefined entrypoint list.
	 */
	@Test
	public void testInternalInspectionOfContainerConfigurationReturnsUndefinedEntrypointList() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(ReflectionTestUtils.getField(containeConfig, "entrypoint"));
	}

	/**
	 * Test that container configuration returns defined onbuild list when queried.
	 */
	@Test
	public void testContainerConfigurationInitiallyReturnsOnbuildListWhenQueried() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNotNull(containeConfig.getOnBuild());
	}

	/**
	 * Test that using internal inspection of container configuration reveals
	 * initial undefined onbuild list.
	 */
	@Test
	public void testInternalInspectionOfContainerConfigurationReturnsUndefinedOnbuildList() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(ReflectionTestUtils.getField(containeConfig, "onBuild"));
	}

	/**
	 * Test that container configuration returns initially undefined environment
	 * variables map.
	 */
	@Test
	public void testContainerConfigurationHasInitiallyUndefinedLabelsMap() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		ContainerConfigurationLabelsMap labels = containeConfig.getLabels();
		assertNull(labels);
	}

	/**
	 * Test that container configuration returns initially undefined exposed ports
	 * map.
	 */
	@Test
	public void testContainerConfigurationHasInitiallyUndefinedExposedPortsMap() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertNull(exposedPorts);
	}

	/**
	 * Test that container configuration return initially undefined volumes map.
	 */
	@Test
	public void testContainerConfigurationHasInitiallyUndefinedVolumesMap() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(containeConfig.getVolumes());
	}

	/**
	 * Test that container configuration has initially undefined host configuration,
	 * e.g null.
	 */
	@Test
	public void testContainerConfigurationHasInitiallyUndefinedHostConfig() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		assertNotNull(containeConfig);
		assertNull(containeConfig.getHostConfig());
	}

	/**
	 * Test that a legal TCP port can be added to the exposed ports.
	 */
	@Test
	public void testLegalTcpPortCanBeAddedToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedTcpPort(1024);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertEquals(1, exposedPorts.size());
		assertTrue(exposedPorts.containsKey("1024/tcp"));
		Object port = exposedPorts.get("1024/tcp");
		assertTrue(port instanceof PortSetElementNullValue);
	}

	/**
	 * Test that a legal UDP port can be added to the exposed ports.
	 */
	@Test
	public void testLegalUdpPortCanBeAddedToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedUdpPort(1024);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertEquals(1, exposedPorts.size());
		assertTrue(exposedPorts.containsKey("1024/udp"));
		Object port = exposedPorts.get("1024/udp");
		assertTrue(port instanceof PortSetElementNullValue);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddZeroUdpPortToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedUdpPort(0);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddNegativeUdpPortToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedUdpPort(-1);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddOutOfBoundUdpPortToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedUdpPort(70000);
	}

	/**
	 * Test that adding the same TCP twice, then the second insert attempt is
	 * ignored.
	 */
	@Test
	public void testCanAddTheSamePortTwiceToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedTcpPort(1024);
		containerInfo.addExposedTcpPort(1024);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertEquals(1, exposedPorts.size());
		assertTrue(exposedPorts.containsKey("1024/tcp"));
		Object port = exposedPorts.get("1024/tcp");
		assertTrue(port instanceof PortSetElementNullValue);
	}

	/**
	 * Test that a TCP and UDP port with the same value can be added.
	 */
	@Test
	public void testCanAddTcpAndUdpPortWithTheSameValueToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedTcpPort(1024);
		containerInfo.addExposedUdpPort(1024);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertEquals(2, exposedPorts.size());
		assertTrue(exposedPorts.containsKey("1024/tcp"));
		assertTrue(exposedPorts.containsKey("1024/udp"));
		Object port = exposedPorts.get("1024/tcp");
		assertTrue(port instanceof PortSetElementNullValue);
		port = exposedPorts.get("1024/udp");
		assertTrue(port instanceof PortSetElementNullValue);
	}

	/**
	 * Test that two TCP ports can be added.
	 */
	@Test
	public void testCanAddTwoTcpPortsToExposedPorts() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addExposedTcpPort(1024);
		containerInfo.addExposedTcpPort(1025);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortSetMap exposedPorts = containeConfig.getExposedPorts();
		assertEquals(2, exposedPorts.size());
		assertTrue(exposedPorts.containsKey("1024/tcp"));
		assertTrue(exposedPorts.containsKey("1025/tcp"));
		Object port = exposedPorts.get("1024/tcp");
		assertTrue(port instanceof PortSetElementNullValue);
		port = exposedPorts.get("1025/tcp");
		assertTrue(port instanceof PortSetElementNullValue);
	}

	/**
	 * Test that a volume can be added.
	 */
	@Test
	public void testVolumeCanBeAdded() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addVolume(randomValue2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		ContainerConfigurationVolumesMap volumes = containeConfig.getVolumes();
		assertEquals(1, volumes.size());
		assertTrue(volumes.containsKey(randomValue2));
		Object port = volumes.get(randomValue2);
		assertTrue(port instanceof ContainerConfigurationVolumeNullValue);
	}

	/**
	 * Test that adding an illegal volume fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsAddToUndefinedVolume() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addVolume(null);
	}

	/**
	 * Test that adding an illegal volume fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddEmptyVolume() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addVolume("");
	}

	/**
	 * Test that adding the same volume, then the second insert attempt is ignored.
	 */
	@Test
	public void testCanAddTheSameVolumeTwice() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addVolume(randomValue2);
		containerInfo.addVolume(randomValue2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		ContainerConfigurationVolumesMap volumes = containeConfig.getVolumes();
		assertEquals(1, volumes.size());
		assertTrue(volumes.containsKey(randomValue2));
		Object volume = volumes.get(randomValue2);
		assertTrue(volume instanceof ContainerConfigurationVolumeNullValue);
	}

	/**
	 * Test that two volumes can be added.
	 */
	@Test
	public void testCanAddTwoVolumes() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addVolume(randomValue);
		containerInfo.addVolume(randomValue2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		ContainerConfigurationVolumesMap volumes = containeConfig.getVolumes();
		assertEquals(2, volumes.size());
		assertTrue(volumes.containsKey(randomValue));
		Object volume = volumes.get(randomValue);
		assertTrue(volume instanceof ContainerConfigurationVolumeNullValue);
		assertTrue(volumes.containsKey(randomValue2));
		volume = volumes.get(randomValue2);
		assertTrue(volume instanceof ContainerConfigurationVolumeNullValue);
	}

	/**
	 * Test that container configuration has no environment variables defined, i.e.
	 * the container is empty.
	 */
	@Test
	public void testContainerConfigurationHasNoEnvironmentVariablesDefined() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		List<String> envs = containeConfig.getEnv();
		assertTrue(envs.isEmpty());
	}

	/**
	 * Test that an environment variable can be added.
	 */
	@Test
	public void testEnvironmentVariableCanBeAdded() {
		String name = "K" + randomValue;
		String value = "V" + randomValue;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable(name, value);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		List<String> envs = containeConfig.getEnv();
		assertEquals(1, envs.size());
		assertEquals(name + "=" + value, envs.get(0));
	}

	/**
	 * Test that two environment variables can be added.
	 */
	@Test
	public void testTwoEnvironmentVariableCanBeAdded() {
		String name = "K" + randomValue;
		String value = "V" + randomValue;
		String name2 = "K" + randomValue2;
		String value2 = "V" + randomValue2;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable(name, value);
		containerInfo.addEnvironmentVariable(name2, value2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		List<String> envs = containeConfig.getEnv();
		assertEquals(2, envs.size());
		assertEquals(name + "=" + value, envs.get(0));
		assertEquals(name2 + "=" + value2, envs.get(1));
	}

	/**
	 * Test that adding an illegal environment variable name fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddUndefinedEnvironmentVariableName() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable(null, randomValue);
	}

	/**
	 * Test that adding an illegal environment variable name fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddEmptyEnvironmentVariableName() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable("", randomValue);
	}

	/**
	 * Test that adding an illegal environment variable value fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddUndefinedEnvironmentVariableValue() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable(randomValue, null);
	}

	/**
	 * Test that adding an illegal environment variable value fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddEmptyEnvironmentVariableValue() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addEnvironmentVariable(randomValue, "");
	}

	/**
	 * Test that a label can be added.
	 */
	@Test
	public void testLabelCanBeAdded() {
		String key = "K" + randomValue;
		String value = "V" + randomValue;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel(key, value);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		ContainerConfigurationLabelsMap labels = containeConfig.getLabels();
		assertEquals(1, labels.size());
		assertTrue(labels.containsKey(key));
		assertEquals(value, labels.get(key));
	}

	/**
	 * Test that two labels can be added.
	 */
	@Test
	public void testTwoLabelsCanBeAdded() {
		String key = "K" + randomValue;
		String value = "V" + randomValue;
		String key2 = "K" + randomValue2;
		String value2 = "V" + randomValue2;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel(key, value);
		containerInfo.addLabel(key2, value2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		ContainerConfigurationLabelsMap labels = containeConfig.getLabels();
		assertEquals(2, labels.size());
		assertTrue(labels.containsKey(key));
		assertEquals(value, labels.get(key));
		assertTrue(labels.containsKey(key2));
		assertEquals(value2, labels.get(key2));
	}

	/**
	 * Test that adding an illegal label key fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddUndefinedLabelKey() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel(null, randomValue);
	}

	/**
	 * Test that adding an illegal label key fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddEmptyKeyName() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel("", randomValue);
	}

	/**
	 * Test that adding an illegal label value fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddUndefinedLabelValue() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel(randomValue, null);

	}

	/**
	 * Test that adding an illegal label value fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddEmptyLabelValue() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addLabel(randomValue, "");
	}

	/**
	 * Test that a legal TCP port can be added to port bindings.
	 */
	@Test
	public void testLegalTcpPortCanBeAddedToPortBindings() {
		int containerPortNumber = 10;
		int hostPortNumber = 20020;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		String portKey = containerInfo.createPortString(containerPortNumber, "tcp");
		containerInfo.addTcpPortBinding(containerPortNumber, hostPortNumber);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortMapMap portBindings = containeConfig.getHostConfig().getPortBindings();
		assertEquals(1, portBindings.size());
		assertTrue(portBindings.containsKey(portKey));
		PortBinding[] valueArray = portBindings.get(portKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that a legal UDP port can be added to port bindings.
	 */
	@Test
	public void testLegalUdpPortCanBeAddedToPortBindings() {
		int containerPortNumber = 10;
		int hostPortNumber = 20020;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		String portKey = containerInfo.createPortString(containerPortNumber, "udp");
		containerInfo.addUdpPortBinding(containerPortNumber, hostPortNumber);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortMapMap portBindings = containeConfig.getHostConfig().getPortBindings();
		assertEquals(1, portBindings.size());
		assertTrue(portBindings.containsKey(portKey));
		PortBinding[] valueArray = portBindings.get(portKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddZeroContainerUdpPortToPortbindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(0, 10);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddNegativeContainerUdpPortToPortBindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(-1, 10);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddOutOfBoundContainerUdpPortToPortBindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(70000, 10);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddZeroHostUdpPortToPortbindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(10, 0);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddNegativeHostUdpPortToPortBindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(10, -1);
	}

	/**
	 * Test that a adding an illegal UDP port fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToAddOutOfBoundHostUdpPortToPortBindings() {
		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		containerInfo.addUdpPortBinding(10, 70000);
	}

	/**
	 * Test that adding the same TCP twice, then the second insert attempt is
	 * ignored.
	 */
	@Test
	public void testCanAddTheSamePortTwiceToPortBindings() {
		int containerPortNumber = 10;
		int hostPortNumber = 20020;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		String tcpPortKey = containerInfo.createPortString(containerPortNumber, "tcp");
		containerInfo.addTcpPortBinding(containerPortNumber, hostPortNumber);
		containerInfo.addTcpPortBinding(containerPortNumber, hostPortNumber);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortMapMap portBindings = containeConfig.getHostConfig().getPortBindings();
		assertEquals(1, portBindings.size());

		assertTrue(portBindings.containsKey(tcpPortKey));
		PortBinding[] valueArray = portBindings.get(tcpPortKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that a TCP and UDP port with the same value can be added.
	 */
	@Test
	public void testCanAddTcpAndUdpPortWithTheSameValueToPortbindings() {
		int containerPortNumber = 10;
		int hostPortNumber = 20020;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		String udpPortKey = containerInfo.createPortString(containerPortNumber, "udp");
		String tcpPortKey = containerInfo.createPortString(containerPortNumber, "tcp");
		containerInfo.addUdpPortBinding(containerPortNumber, hostPortNumber);
		containerInfo.addTcpPortBinding(containerPortNumber, hostPortNumber);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortMapMap portBindings = containeConfig.getHostConfig().getPortBindings();
		assertEquals(2, portBindings.size());
		assertTrue(portBindings.containsKey(udpPortKey));
		PortBinding[] valueArray = portBindings.get(udpPortKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());

		assertTrue(portBindings.containsKey(tcpPortKey));
		valueArray = portBindings.get(tcpPortKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());
	}

	/**
	 * Test that two TCP ports can be added.
	 */
	@Test
	public void testCanAddTwoTcpPortsToPortBindings() {
		int containerPortNumber = 10;
		int containerPortNumber2 = 11;
		int hostPortNumber = 20020;
		int hostPortNumber2 = 20021;

		imageInfo = infobuilder.buildImageInfo(randomValue, "");
		containerInfo = new ContainerInfoImpl(randomValue, imageInfo);
		String tcpPortKey = containerInfo.createPortString(containerPortNumber, "tcp");
		String tcpPortKey2 = containerInfo.createPortString(containerPortNumber2, "tcp");
		containerInfo.addTcpPortBinding(containerPortNumber, hostPortNumber);
		containerInfo.addTcpPortBinding(containerPortNumber2, hostPortNumber2);

		// test
		ContainerConfiguration containeConfig = containerInfo.getContainerConfiguration();
		PortMapMap portBindings = containeConfig.getHostConfig().getPortBindings();
		assertEquals(2, portBindings.size());
		assertTrue(portBindings.containsKey(tcpPortKey));
		PortBinding[] valueArray = portBindings.get(tcpPortKey);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber), valueArray[FIRST_INDEX].getHostPort());

		assertTrue(portBindings.containsKey(tcpPortKey2));
		valueArray = portBindings.get(tcpPortKey2);
		assertEquals(1, valueArray.length);
		assertNotNull(valueArray[FIRST_INDEX]);
		assertNull(valueArray[FIRST_INDEX].getHostIp());
		assertEquals(Integer.toString(hostPortNumber2), valueArray[FIRST_INDEX].getHostPort());
	}

}
