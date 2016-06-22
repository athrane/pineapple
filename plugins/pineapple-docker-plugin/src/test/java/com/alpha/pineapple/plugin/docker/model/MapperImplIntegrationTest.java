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

package com.alpha.pineapple.plugin.docker.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.NetworkUtils;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInfoImpl;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationExposedPortsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationHostConfigPortBindingsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationLabelsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationVolumesMap;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfig;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationVolumeNullValue;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>MapperImpl</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.docker-config.xml" })
public class MapperImplIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    Mapper mapper;

    /**
     * Object mother for the docker model.
     */
    ObjectMotherContent contentMother;

    /**
     * Container configurations.
     */
    Map<String, ContainerConfiguration> containerConfigs;

    /**
     * Random value.
     */
    String randomRepo;

    /**
     * Random value.
     */
    String randomTag;

    /**
     * Random value.
     */
    String randomRepo2;

    /**
     * Random value.
     */
    String randomTag2;

    /**
     * Random value.
     */
    String randomId;

    /**
     * Random value.
     */
    String randomMountpoint;

    /**
     * Random value.
     */
    String randomMountpoint2;

    /**
     * Random name.
     */
    String randomName;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random name.
     */
    String randomName2;

    /**
     * Random value.
     */
    String randomValue2;

    /**
     * Random
     */
    Random random;

    @Before
    public void setUp() throws Exception {
	random = new Random();
	randomRepo = RandomStringUtils.randomAlphanumeric(10);
	randomTag = RandomStringUtils.randomAlphanumeric(10);
	randomRepo2 = RandomStringUtils.randomAlphanumeric(10);
	randomTag2 = RandomStringUtils.randomAlphanumeric(10);
	randomId = RandomStringUtils.randomAlphanumeric(10);
	randomMountpoint = RandomStringUtils.randomAlphanumeric(10);
	randomMountpoint2 = RandomStringUtils.randomAlphanumeric(10);
	randomName = RandomStringUtils.randomAlphanumeric(10);
	randomValue = RandomStringUtils.randomAlphanumeric(10);
	randomName2 = RandomStringUtils.randomAlphanumeric(10);
	randomValue2 = RandomStringUtils.randomAlphanumeric(10);

	// create content mother
	contentMother = new ObjectMotherContent();

	// create container configs
	containerConfigs = new HashMap<String, ContainerConfiguration>();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Assert container configuration has default value as set by
     * {@linkplain ContainerInfoImpl} during creation of the target container
     * configuration.
     * 
     * @param containerConfig
     *            container configuration to test.
     */
    void assertDefaultProperties(com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig) {
	// test default properties
	assertEquals(true, containerConfig.isAttachStderr());
	assertEquals(true, containerConfig.isAttachStdin());
	assertEquals(true, containerConfig.isAttachStdout());
	assertEquals("", containerConfig.getDomainname());
	assertEquals("", containerConfig.getHostname());
	assertEquals(null, containerConfig.getImage()); // !!!!!!!!!!!!!!!
	assertEquals(null, containerConfig.getMacAddress());
	assertEquals(false, containerConfig.isNetworkDisabled());
	assertEquals(true, containerConfig.isOpenStdin());
	assertEquals(false, containerConfig.isStdinOnce());
	assertEquals(true, containerConfig.isTty());
	assertEquals("", containerConfig.getUser());
	assertEquals("", containerConfig.getWorkingDir());

	// test default host-config properties
	ContainerConfigurationHostConfig hostConfig = containerConfig.getHostConfig();
	assertNotNull(hostConfig);
	assertEquals(Long.valueOf(0), hostConfig.getCpuShares());
	assertEquals(Long.valueOf(0), hostConfig.getCpuPeriod());
	assertEquals("", hostConfig.getCpusetCpus());
	assertEquals("", hostConfig.getCpusetMems());
	assertEquals(Long.valueOf(0), hostConfig.getMemory());
	assertEquals(Long.valueOf(0), hostConfig.getMemorySwap());
    }

    /**
     * Set random properties for source container configuration.
     * 
     * @param containerConfig
     *            source container configuration.
     */
    void setRandomProperties(AbstractContainerConfiguration containerConfig) {
	containerConfig.setAttachStderr(RandomUtils.nextBoolean());
	containerConfig.setAttachStdin(RandomUtils.nextBoolean());
	containerConfig.setAttachStdout(RandomUtils.nextBoolean());
	containerConfig.setDomainname(RandomStringUtils.randomAlphanumeric(10));
	containerConfig.setHostname(RandomStringUtils.randomAlphanumeric(10));
	containerConfig.setMacAddress(RandomStringUtils.randomAlphanumeric(10));
	containerConfig.setNetworkDisabled(RandomUtils.nextBoolean());
	containerConfig.setOpenStdin(RandomUtils.nextBoolean());
	containerConfig.setStdinOnce(RandomUtils.nextBoolean());
	containerConfig.setTty(RandomUtils.nextBoolean());
	containerConfig.setUser(RandomStringUtils.randomAlphanumeric(10));
	containerConfig.setWorkingDir(RandomStringUtils.randomAlphanumeric(10));

	// set host-config properties
	com.alpha.pineapple.plugin.docker.model.ContainerConfigurationHostConfig hostConfig;
	hostConfig = containerConfig.getHostConfig();
	assertNotNull(hostConfig);
	hostConfig.setCpuShares(RandomUtils.nextLong());
	hostConfig.setCpuPeriod(RandomUtils.nextLong());
	hostConfig.setCpusetCpus(RandomStringUtils.randomAlphanumeric(10));
	hostConfig.setCpusetMems(RandomStringUtils.randomAlphanumeric(10));
	hostConfig.setMemory(RandomUtils.nextLong());
	hostConfig.setMemorySwap(RandomUtils.nextLong());

    }

    /**
     * Assert that properties defined on the source configuration is set on the
     * target configuration.
     * 
     * @param srcContainerConfig
     *            source container configuration.
     * @param targetContainerConfig
     *            target container configuration.
     */
    void assertProperties(AbstractContainerConfiguration srcContainerConfig,
	    com.alpha.pineapple.docker.model.rest.ContainerConfiguration targetContainerConfig) {

	assertEquals(srcContainerConfig.isAttachStderr(), targetContainerConfig.isAttachStderr());
	assertEquals(srcContainerConfig.isAttachStdin(), targetContainerConfig.isAttachStdin());
	assertEquals(srcContainerConfig.isAttachStdout(), targetContainerConfig.isAttachStdout());
	assertEquals(srcContainerConfig.getDomainname(), targetContainerConfig.getDomainname());
	assertEquals(srcContainerConfig.getHostname(), targetContainerConfig.getHostname());
	assertEquals(srcContainerConfig.getMacAddress(), targetContainerConfig.getMacAddress());
	assertEquals(srcContainerConfig.isNetworkDisabled(), targetContainerConfig.isNetworkDisabled());
	assertEquals(srcContainerConfig.isOpenStdin(), targetContainerConfig.isOpenStdin());
	assertEquals(srcContainerConfig.isStdinOnce(), targetContainerConfig.isStdinOnce());
	assertEquals(srcContainerConfig.isTty(), targetContainerConfig.isTty());
	assertEquals(srcContainerConfig.getUser(), targetContainerConfig.getUser());
	assertEquals(srcContainerConfig.getWorkingDir(), targetContainerConfig.getWorkingDir());

	// test default host-config properties
	com.alpha.pineapple.plugin.docker.model.ContainerConfigurationHostConfig srcHostConfig;
	srcHostConfig = srcContainerConfig.getHostConfig();
	assertNotNull(srcHostConfig);
	ContainerConfigurationHostConfig targetHostConfig = targetContainerConfig.getHostConfig();
	assertNotNull(targetHostConfig);

	assertEquals(srcHostConfig.getCpuShares(), targetHostConfig.getCpuShares());
	assertEquals(srcHostConfig.getCpuPeriod(), targetHostConfig.getCpuPeriod());
	assertEquals(srcHostConfig.getCpusetCpus(), targetHostConfig.getCpusetCpus());
	assertEquals(srcHostConfig.getCpusetMems(), targetHostConfig.getCpusetMems());
	assertEquals(srcHostConfig.getMemory(), targetHostConfig.getMemory());
	assertEquals(srcHostConfig.getMemorySwap(), targetHostConfig.getMemorySwap());
    }

    /**
     * Assert container configuration has default object as set by
     * {@linkplain ContainerInfoImpl} during creation of the target container
     * configuration.
     * 
     * @param containerConfig
     *            container configuration to test.
     */
    void assertDefaultObjects(com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig) {
	assertNotNull(containerConfig.getCmd());
	assertEquals(1, containerConfig.getCmd().size());
	assertNotNull(containerConfig.getEnv());
	assertTrue(containerConfig.getEnv().isEmpty());
	assertNotNull(containerConfig.getEntrypoint());
	assertEquals(0, containerConfig.getEntrypoint().size());
	assertNotNull(containerConfig.getExposedPorts());
	assertEquals(0, containerConfig.getExposedPorts().size());
	assertNotNull(containerConfig.getLabels());
	assertEquals(0, containerConfig.getLabels().size());
	assertNotNull(containerConfig.getOnBuild());
	assertEquals(0, containerConfig.getOnBuild().size());
	assertNotNull(containerConfig.getVolumes());
	assertEquals(0, containerConfig.getVolumes().size());
    }

    /**
     * Assert that container reference is defined on container configuration.
     * 
     * @param containerCmd
     *            container definition.
     */
    void assertReferenceIsDefined(Container containerCmd) {
	// test container configuration reference is defined
	EmbeddedContainerConfiguration sourceContainerConfig = containerCmd.getConfiguration();
	assertNotNull(sourceContainerConfig);
	assertEquals(randomId, sourceContainerConfig.getRef());
    }

    /**
     * Test that mapper can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext() {
	assertNotNull(mapper);
    }

    /**
     * Test image can be mapped for creation.
     */
    @Test
    public void testMapImageForCreation() throws Exception {

	// create content
	Image command = contentMother.createImageCommand(randomRepo, randomTag);

	// map
	ImageInfo info = mapper.mapImageForCreation(command);

	// test
	assertEquals(randomRepo, info.getRepository());
	assertEquals(randomTag, info.getTag());
    }

    /**
     * Test tagged source image can be mapped for creation.
     */
    @Test
    public void testMapTaggedSourceImageForCreation() throws Exception {

	// create content
	TaggedImage command = contentMother.createTaggedImageCommand(randomRepo, randomTag, randomRepo2, randomTag2);

	// map
	ImageInfo info = mapper.mapTaggedSourceImageForCreation(command);

	// test
	assertEquals(randomRepo, info.getRepository());
	assertEquals(randomTag, info.getTag());
    }

    /**
     * Test tagged target image can be mapped for creation.
     */
    @Test
    public void testMapTaggedTargetImageForCreation() throws Exception {

	// create content
	TaggedImage command = contentMother.createTaggedImageCommand(randomRepo, randomTag, randomRepo2, randomTag2);

	// map
	ImageInfo info = mapper.mapTaggedTargetImageImageForCreation(command);

	// test
	assertEquals(randomRepo2, info.getRepository());
	assertEquals(randomTag2, info.getTag());
    }

    /**
     * Test image can be mapped for deletion.
     */
    @Test
    public void testMapImageForDeletion() throws Exception {

	// create content
	Image command = contentMother.createImageCommand(randomRepo, randomTag);

	// map
	ImageInfo info = mapper.mapImageForDeletion(command);

	// test
	assertEquals(randomRepo, info.getRepository());
	assertEquals(randomTag, info.getTag());
    }

    /**
     * Test tagged image can be mapped for creation.
     */
    @Test
    public void testMapTaggedImageForDeletion() throws Exception {

	// create content
	TaggedImage command = contentMother.createTaggedImageCommand(randomRepo, randomTag, randomRepo2, randomTag2);

	// map
	ImageInfo info = mapper.mapTaggedImageForDeletion(command);

	// test
	assertEquals(randomRepo2, info.getRepository());
	assertEquals(randomTag2, info.getTag());
    }

    /**
     * Test image created from Docker file can be mapped for creation.
     */
    @Test
    public void testmapImageFromDockerfileForCreation() throws Exception {

	// create content
	ImageFromDockerfile command = contentMother.createImageFromDockerfileCommand(randomRepo, randomTag, randomName,
		false);

	// map
	ImageInfo info = mapper.mapImageFromDockerfileForCreation(command);

	// test
	assertEquals(randomRepo, info.getRepository());
	assertEquals(randomTag, info.getTag());
    }

    /**
     * Test image created from Docker file can be mapped for deletion.
     */
    @Test
    public void testmapImageFromDockerfileForDeletion() throws Exception {

	// create content
	ImageFromDockerfile command = contentMother.createImageFromDockerfileCommand(randomRepo, randomTag, randomName,
		false);

	// map
	ImageInfo info = mapper.mapImageFromDockerfileForDeletion(command);

	// test
	assertEquals(randomRepo, info.getRepository());
	assertEquals(randomTag, info.getTag());
    }

    /**
     * Test container can be mapped for creation.
     */
    @Test
    public void testMapContainerForCreation() throws Exception {

	// create content
	Container command = contentMother.createContainerCommand(randomId, randomRepo, randomTag);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	ImageInfo imageInfo = info.getImageInfo();
	assertNotNull(imageInfo);
	assertEquals(randomRepo, imageInfo.getRepository());
	assertEquals(randomTag, imageInfo.getTag());
    }

    /**
     * Test container can be mapped for creation with undefined container
     * configuration, i.e. the source container configuration is undefined.
     * 
     * The values in the mapped container configuration are set by the
     * {@linkplain ContainerInfoImpl} during creation of the target container
     * configuration.
     * 
     * A target configuration is created and mapped with default values.
     */
    @Test
    public void testMapContainerForCreationWithUndefinedContainerConfig() throws Exception {

	// create content
	Container command = contentMother.createContainerCommand(randomId, randomRepo, randomTag);

	// test source container configuration is defined
	assertNull(command.getConfiguration());

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);
	assertDefaultProperties(containerConfig);
    }

    /**
     * Test container can be mapped for creation with empty container
     * configuration, i.e. the source container configuration is defined but
     * contains no values
     * 
     * The values in the mapped container configuration are set by the
     * {@linkplain ContainerInfoImpl} during creation of the target container
     * configuration.
     * 
     * A target configuration is created and mapped with default values.
     */
    @Test
    public void testMapContainerForCreationWithEmptyContainerConfig() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// test source container configuration is defined
	assertNotNull(command.getConfiguration());

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);
	assertDefaultProperties(containerConfig);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains some
     * custom values.
     * 
     * A target configuration is created and mapped with the custom property
     * values.
     */
    @Test
    public void testMapContainerForCreationWithCustomPropertyValues() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);
	setRandomProperties(srcContainerConfig);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);
	assertProperties(srcContainerConfig, containerConfig);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined commands.
     * 
     * A target configuration is created and mapped with defined commands.
     */
    @Test
    public void testMapContainerForCreationWith_ZeroCmds() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// NO-OP: define command list with zero entries

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test that command list is mapped to one default command
	List<String> actualCmds = containerConfig.getCmd();
	assertNotNull(actualCmds);
	assertEquals(1, containerConfig.getCmd().size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined commands.
     * 
     * A target configuration is created and mapped with defined commands.
     */
    @Test
    public void testMapContainerForCreationWith_OneCmd() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define command list with one entry
	List<String> cmds = srcContainerConfig.getCmd();
	cmds.add(RandomStringUtils.randomAlphanumeric(10));

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test that command list is mapped to one command
	List<String> actualCmds = containerConfig.getCmd();
	assertNotNull(actualCmds);
	assertEquals(1, containerConfig.getCmd().size());
	assertEquals(actualCmds.get(0), containerConfig.getCmd().get(0));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined commands.
     * 
     * A target configuration is created and mapped with defined commands.
     */
    @Test
    public void testMapContainerForCreationWith_TwoCmds() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define command list with two entries
	List<String> cmds = srcContainerConfig.getCmd();
	cmds.add(RandomStringUtils.randomAlphanumeric(10));
	cmds.add(RandomStringUtils.randomAlphanumeric(10));

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test that command list is mapped to two commands
	List<String> actualCmds = containerConfig.getCmd();
	assertNotNull(actualCmds);
	assertEquals(2, containerConfig.getCmd().size());
	assertEquals(actualCmds.get(0), containerConfig.getCmd().get(0));
	assertEquals(actualCmds.get(1), containerConfig.getCmd().get(1));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_ZeroEnvs() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// NO-OP: define env list with zero entries

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	List<String> actualEnvs = containerConfig.getEnv();
	assertNotNull(actualEnvs);
	assertEquals(0, containerConfig.getEnv().size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_OneEnv() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define env list with one entry
	contentMother.addEnvironmentVariable(command, randomName, randomValue);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test that envs list is mapped to one env
	List<String> actualEnvs = containerConfig.getEnv();
	assertNotNull(actualEnvs);
	assertEquals(1, containerConfig.getCmd().size());
	assertEquals(actualEnvs.get(0), containerConfig.getEnv().get(0));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_TwoEnvs() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define env list with two entries
	contentMother.addEnvironmentVariable(command, randomName, randomValue);
	contentMother.addEnvironmentVariable(command, randomName2, randomValue);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test that envs list is mapped to two envs
	List<String> actualEnvs = containerConfig.getEnv();
	assertNotNull(actualEnvs);
	assertEquals(2, containerConfig.getEnv().size());
	assertEquals(actualEnvs.get(0), containerConfig.getEnv().get(0));
	assertEquals(actualEnvs.get(1), containerConfig.getEnv().get(1));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_ZeroExposedPorts() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// NO-OP: define exposed ports list with zero entries

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationExposedPortsMap actualPorts = containerConfig.getExposedPorts();
	assertNotNull(actualPorts);
	assertEquals(0, actualPorts.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_OneExposedPort() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port list with one entry
	contentMother.addExposedPort(command, random);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationExposedPortsMap actualPorts = containerConfig.getExposedPorts();
	assertNotNull(actualPorts);
	assertEquals(1, actualPorts.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_TwoExposedPorts() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port list with one entry
	contentMother.addExposedPort(command, random);
	contentMother.addExposedPort(command, random);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationExposedPortsMap actualPorts = containerConfig.getExposedPorts();
	assertNotNull(actualPorts);
	assertEquals(2, actualPorts.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_TwoIdenticalExposedPortsAreOnlyAddedOnce() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port list with one entry
	int portNumber = NetworkUtils.getRandomPort(random);
	contentMother.addExposedPort(command, portNumber);
	contentMother.addExposedPort(command, portNumber);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationExposedPortsMap actualPorts = containerConfig.getExposedPorts();
	assertNotNull(actualPorts);
	assertEquals(1, actualPorts.size());
    }

    /**
     * Test container can be mapped for creation with referenced container
     * configuration, i.e. the source container configuration is undefined but a
     * referenced container configuration is defined and the values are set from
     * the referenced configuration.
     * 
     * A target configuration is created and mapped with values from the
     * referenced container configuration.
     */
    @Test
    public void testMapContainerForCreationWithReferencedPropertyValues() throws Exception {

	// create and add referenced container configuration
	ContainerConfiguration refContainerConfig;
	refContainerConfig = contentMother.ReferencedContainerConfiguration(randomId);
	containerConfigs.put(randomId, refContainerConfig);

	// create container with reference
	Container command = contentMother.createContainerCommandWithReferencedContainerConfig(randomId, randomRepo,
		randomTag, randomId);
	assertReferenceIsDefined(command);

	// set custom property values in referenced container configuration
	setRandomProperties(refContainerConfig);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);
	assertProperties(refContainerConfig, containerConfig);
    }

    /**
     * Test container can be mapped for creation with referenced container
     * configuration, i.e. the source container configuration is undefined but a
     * referenced container configuration is defined but contains no values.
     * 
     * A target configuration is created and mapped with default values from the
     * referenced container configuration.
     */
    @Test
    public void testMapContainerForCreationWithReferencedConfigurationWithUndefinedValues() throws Exception {

	// create and add referenced container configuration
	ContainerConfiguration refContainerConfig;
	refContainerConfig = contentMother.ReferencedContainerConfiguration(randomId);
	containerConfigs.put(randomId, refContainerConfig);

	// create container with reference
	Container command = contentMother.createContainerCommandWithReferencedContainerConfig(randomId, randomRepo,
		randomTag, randomId);
	assertReferenceIsDefined(command);

	// NO property values is set in referenced container configuration

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);
	assertDefaultProperties(containerConfig);
    }

    /**
     * Test container can be mapped for creation with source and referenced
     * container configuration, i.e. the source container configuration is
     * defined and a referenced container configuration is defined and the
     * values are set from the source configuration.
     * 
     * A target configuration is created and mapped with values from the source
     * container configuration.
     */
    @Test
    public void testMapContainerForCreationWithSourceAndReferencedPropertyValues() throws Exception {

	// create and add referenced container configuration
	ContainerConfiguration refContainerConfig;
	refContainerConfig = contentMother.ReferencedContainerConfiguration(randomId);
	containerConfigs.put(randomId, refContainerConfig);

	// create container with reference
	Container command = contentMother.createContainerCommandWithReferencedContainerConfig(randomId, randomRepo,
		randomTag, randomId);
	assertReferenceIsDefined(command);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);
	setRandomProperties(srcContainerConfig);

	// set custom property values in referenced container configuration
	setRandomProperties(refContainerConfig);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);
	assertDefaultObjects(containerConfig);

	// test that container configuration contains values from the source
	// configuration.
	assertProperties(srcContainerConfig, containerConfig);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined volumes.
     * 
     * A target configuration is created and mapped with defined volumes.
     */
    @Test
    public void testMapContainerForCreationWith_ZeroVolumes() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// NO-OP: define zero volumes

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationVolumesMap actualVolumes = containerConfig.getVolumes();
	assertNotNull(actualVolumes);
	assertEquals(0, actualVolumes.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined volumes.
     */
    @Test
    public void testMapContainerForCreationWith_OneVolume() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define one volume
	contentMother.addVolume(command, randomMountpoint);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationVolumesMap actualVolumes = containerConfig.getVolumes();
	assertNotNull(actualVolumes);
	assertEquals(1, actualVolumes.size());
	assertTrue(actualVolumes.containsKey(randomMountpoint));
	assertTrue(actualVolumes.get(randomMountpoint) instanceof ContainerConfigurationVolumeNullValue);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined volumes.
     */
    @Test
    public void testMapContainerForCreationWith_TwoVolumes() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define one volume
	contentMother.addVolume(command, randomMountpoint);
	contentMother.addVolume(command, randomMountpoint2);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationVolumesMap actualVolumes = containerConfig.getVolumes();
	assertNotNull(actualVolumes);
	assertEquals(2, actualVolumes.size());
	assertTrue(actualVolumes.containsKey(randomMountpoint));
	assertTrue(actualVolumes.containsKey(randomMountpoint2));
	assertTrue(actualVolumes.get(randomMountpoint) instanceof ContainerConfigurationVolumeNullValue);
	assertTrue(actualVolumes.get(randomMountpoint2) instanceof ContainerConfigurationVolumeNullValue);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined volumes.
     */
    @Test
    public void testMapContainerForCreationWith_TwoIdenticalVolumesAreOnlyAddedOnce() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define one volume
	contentMother.addVolume(command, randomMountpoint);
	contentMother.addVolume(command, randomMountpoint);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationVolumesMap actualVolumes = containerConfig.getVolumes();
	assertNotNull(actualVolumes);
	assertEquals(1, actualVolumes.size());
	assertTrue(actualVolumes.containsKey(randomMountpoint));
	assertTrue(actualVolumes.get(randomMountpoint) instanceof ContainerConfigurationVolumeNullValue);
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined labels.
     * 
     * A target configuration is created and mapped with defined labels.
     */
    @Test
    public void testMapContainerForCreationWith_ZeroLabels() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// NO-OP: define zero labels

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationLabelsMap actualLabels = containerConfig.getLabels();
	assertNotNull(actualLabels);
	assertEquals(0, actualLabels.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined labels.
     * 
     * A target configuration is created and mapped with defined labels.
     */
    @Test
    public void testMapContainerForCreationWith_OneLabel() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define one label
	contentMother.addLabel(command, randomName, randomValue);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationLabelsMap actualLabels = containerConfig.getLabels();
	assertNotNull(actualLabels);
	assertEquals(1, actualLabels.size());
	assertTrue(actualLabels.containsKey(randomName));
	assertEquals(randomValue, actualLabels.get(randomName));
	assertEquals(randomValue, actualLabels.get(randomName));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined labels.
     * 
     * A target configuration is created and mapped with defined labels.
     */
    @Test
    public void testMapContainerForCreationWith_TwoLabel() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define two labels
	contentMother.addLabel(command, randomName, randomValue);
	contentMother.addLabel(command, randomName2, randomValue2);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationLabelsMap actualLabels = containerConfig.getLabels();
	assertNotNull(actualLabels);
	assertEquals(2, actualLabels.size());
	assertTrue(actualLabels.containsKey(randomName));
	assertEquals(randomValue, actualLabels.get(randomName));
	assertTrue(actualLabels.containsKey(randomName2));
	assertEquals(randomValue2, actualLabels.get(randomName2));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined labels.
     * 
     * A target configuration is created and mapped with defined labels.
     */
    @Test
    public void testMapContainerForCreationWith_TwoIdenticalLabelsAreOnlyAddedOnce() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define two labels
	contentMother.addLabel(command, randomName, randomValue);
	contentMother.addLabel(command, randomName, randomValue);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationLabelsMap actualLabels = containerConfig.getLabels();
	assertNotNull(actualLabels);
	assertEquals(1, actualLabels.size());
	assertTrue(actualLabels.containsKey(randomName));
	assertEquals(randomValue, actualLabels.get(randomName));
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_OnePortBinding() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port binding
	contentMother.addTcpPortBinding(command, random);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationHostConfig hostConfig = containerConfig.getHostConfig();
	assertNotNull(hostConfig);
	ContainerConfigurationHostConfigPortBindingsMap bindings = hostConfig.getPortBindings();
	assertEquals(1, bindings.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_TwoPortBindings() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port binding
	contentMother.addTcpPortBinding(command, random);
	contentMother.addTcpPortBinding(command, random);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationHostConfig hostConfig = containerConfig.getHostConfig();
	assertNotNull(hostConfig);
	ContainerConfigurationHostConfigPortBindingsMap bindings = hostConfig.getPortBindings();
	assertEquals(2, bindings.size());
    }

    /**
     * Test container can be mapped for creation with container configuration,
     * i.e. the source container configuration is defined and contains only
     * defined environments.
     * 
     * A target configuration is created and mapped with defined environments.
     */
    @Test
    public void testMapContainerForCreationWith_TwoIdenticalPortBindingsAreOnlyAddedOnce() throws Exception {

	// create content
	Container command = contentMother.createContainerCommandWithContainerConfig(randomId, randomRepo, randomTag);

	// create source container configuration
	EmbeddedContainerConfiguration srcContainerConfig = command.getConfiguration();
	assertNotNull(srcContainerConfig);

	// define port binding
	int portNumber = NetworkUtils.getRandomPort(random);
	contentMother.addTcpPortBinding(command, portNumber, portNumber);
	contentMother.addTcpPortBinding(command, portNumber, portNumber);

	// map
	ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);

	// test
	com.alpha.pineapple.docker.model.rest.ContainerConfiguration containerConfig = info.getContainerConfiguration();
	assertNotNull(containerConfig);

	// test
	ContainerConfigurationHostConfig hostConfig = containerConfig.getHostConfig();
	assertNotNull(hostConfig);
	ContainerConfigurationHostConfigPortBindingsMap bindings = hostConfig.getPortBindings();
	assertEquals(1, bindings.size());
    }

}
