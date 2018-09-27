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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.docker.command.ListAllContainersCommand;
import com.alpha.pineapple.docker.command.ListAllImagesCommand;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.HostConfig;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the {@linkplain Mapper}interface which maps model content
 * to command context.
 */
public class MapperImpl implements Mapper {

	/**
	 * Regular expression for splitting a string using space as separator.
	 */
	static final String REGEX_SPACE_SPLIT = "\\s+";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Docker info objects builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	@Override
	public ImageInfo mapImageForCreation(Image command) {
		return dockerInfoBuilder.buildImageInfo(command.getRepository(), command.getTag());
	}

	@Override
	public ImageInfo mapImageForDeletion(Image command) {
		return dockerInfoBuilder.buildImageInfo(command.getRepository(), command.getTag());
	}

	@Override
	public ImageInfo mapTaggedSourceImageForCreation(TaggedImage command) {
		return dockerInfoBuilder.buildImageInfo(command.getSourceImage().getRepository(),
				command.getSourceImage().getTag());
	}

	@Override
	public ImageInfo mapTaggedTargetImageImageForCreation(TaggedImage command) {
		return dockerInfoBuilder.buildImageInfo(command.getTargetImage().getRepository(),
				command.getTargetImage().getTag());
	}

	@Override
	public ImageInfo mapTaggedImageForDeletion(TaggedImage command) {
		return dockerInfoBuilder.buildImageInfo(command.getTargetImage().getRepository(),
				command.getTargetImage().getTag());
	}

	@Override
	public ImageInfo mapImageFromDockerfileForCreation(ImageFromDockerfile command) {
		return dockerInfoBuilder.buildImageInfo(command.getTargetImage().getRepository(),
				command.getTargetImage().getTag());
	}

	@Override
	public Boolean getPullImageBehavior(ImageFromDockerfile command) {
		return Boolean.valueOf(command.isPullImage());
	}

	@Override
	public ImageInfo mapImageFromDockerfileForDeletion(ImageFromDockerfile command) {
		return dockerInfoBuilder.buildImageInfo(command.getTargetImage().getRepository(),
				command.getTargetImage().getTag());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mapListImages(Context context, DockerSession session) {
		context.put(ListAllImagesCommand.SESSION_KEY, session);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mapListContainers(Context context, DockerSession session) {
		context.put(ListAllContainersCommand.SESSION_KEY, session);
	}

	@Override
	public ContainerInfo mapContainerForCreation(Container command,
			Map<String, ContainerConfiguration> containerConfigs) {

		// get image info
		Image image = command.getImage();
		ImageInfo imageInfo = dockerInfoBuilder.buildImageInfo(image.getRepository(), image.getTag());

		// create container info
		String name = command.getName();
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(name, imageInfo);

		// map container properties if source configuration is defined in model
		EmbeddedContainerConfiguration sourceContainerConfig = command.getConfiguration();
		if (sourceContainerConfig != null)
			mapContainerProperties(containerConfigs, sourceContainerConfig, containerInfo);

		return containerInfo;
	}

	@Override
	public ContainerInfo mapContainerForDeletion(Container command) {
		Image image = command.getImage();
		ImageInfo imageInfo = null;
		if (image != null) {
			imageInfo = dockerInfoBuilder.buildImageInfo(image.getRepository(), image.getTag());
		}
		String name = command.getName();
		return dockerInfoBuilder.buildContainerInfo(name, imageInfo);
	}

	@Override
	public ContainerInfo mapContainerForControl(Container command) {
		Image image = command.getImage();
		ImageInfo imageInfo = null;
		if (image != null) {
			imageInfo = dockerInfoBuilder.buildImageInfo(image.getRepository(), image.getTag());
		}
		String name = command.getName();
		return dockerInfoBuilder.buildContainerInfo(name, imageInfo);
	}

	@Override
	public Map<String, ContainerConfiguration> extractContainerDefinitions(Docker dockerModel) {

		// create map
		Map<String, ContainerConfiguration> map = null;
		map = new HashMap<String, ContainerConfiguration>();

		// get configurations from model
		if (dockerModel == null)
			return map;
		List<ContainerConfiguration> configList = dockerModel.getContainerConfiguration();
		if (configList == null)
			return map;

		// add configs to map
		for (ContainerConfiguration config : configList) {
			String id = config.getId();
			if (id == null)
				continue;
			if (id.isEmpty())
				continue;
			map.put(id, config);
		}

		return map;
	}

	/**
	 * Returns true if reference is defined and valid. The reference is considered
	 * valid if it isn't null, empty and references a defined container
	 * configuration.
	 * 
	 * @param configurationRef
	 *            configuration reference which is validated.
	 * @param containerConfigs
	 *            map of defined container configurations.
	 * 
	 * @return true if container reference is valid.
	 */
	boolean resolveConfigurationReference(String configurationRef,
			Map<String, ContainerConfiguration> containerConfigs) {
		if (configurationRef == null)
			return false;
		if (configurationRef.isEmpty())
			return false;
		return containerConfigs.containsKey(configurationRef);
	}

	/**
	 * Map container properties from source configuration to target configuration.
	 * 
	 * If a referenced configuration is defined then values from the referenced
	 * configuration is used (if defined) if no value is defined on the container.
	 * 
	 * @param containerConfigs
	 *            map of reference container configurations.
	 * @param sourceContainerConfig
	 *            source configuration defined on directly on the container.
	 * @param targetContainerInfo
	 *            target container info, which contains the container configuration
	 *            that is applied to the created container.
	 */
	void mapContainerProperties(Map<String, ContainerConfiguration> containerConfigs,
			EmbeddedContainerConfiguration sourceContainerConfig, ContainerInfo targetContainerInfo) {

		// get target configuration
		com.alpha.pineapple.docker.model.rest.ContainerConfiguration targetContainerConfig;
		targetContainerConfig = targetContainerInfo.getContainerConfiguration();

		// resolve referenced configuration
		String configurationRef = sourceContainerConfig.getRef();
		ContainerConfiguration referencedConfig = null;
		boolean isRefDefined = resolveConfigurationReference(configurationRef, containerConfigs);
		if (isRefDefined)
			referencedConfig = containerConfigs.get(configurationRef);

		// map properties
		Boolean attachStderr = sourceContainerConfig.isAttachStderr();
		if (attachStderr == null)
			if (isRefDefined)
				attachStderr = referencedConfig.isAttachStderr();
		if (attachStderr != null)
			targetContainerConfig.setAttachStderr(attachStderr);

		Boolean attachStdin = sourceContainerConfig.isAttachStdin();
		if (attachStdin == null)
			if (isRefDefined)
				attachStdin = referencedConfig.isAttachStdin();
		if (attachStdin != null)
			targetContainerConfig.setAttachStdin(attachStdin);

		Boolean attachStdout = sourceContainerConfig.isAttachStdout();
		if (attachStdout == null)
			if (isRefDefined)
				attachStdout = referencedConfig.isAttachStdout();
		if (attachStdout != null)
			targetContainerConfig.setAttachStdout(attachStdout);

		List<String> commands = sourceContainerConfig.getCmd();
		if (commands == null)
			if (isRefDefined)
				commands = referencedConfig.getCmd();
		if (commands != null)
			targetContainerConfig.getCmd().addAll(commands);

		String domainname = sourceContainerConfig.getDomainname();
		if (domainname == null)
			if (isRefDefined)
				domainname = referencedConfig.getDomainname();
		if (domainname != null)
			targetContainerConfig.setDomainname(domainname);

		List<String> entrypoint = sourceContainerConfig.getEntrypoint();
		if (entrypoint == null)
			if (isRefDefined)
				entrypoint = referencedConfig.getEntrypoint();
		if (entrypoint != null)
			targetContainerConfig.getEntrypoint().addAll(entrypoint);

		ContainerConfigurationEnvironmentVariables envs = sourceContainerConfig.getEnv();
		if (envs == null)
			if (isRefDefined)
				envs = referencedConfig.getEnv();
		if (envs != null)
			mapEnvironmentVariables(targetContainerInfo, envs);

		ContainerConfigurationExposedPorts exposedPorts = sourceContainerConfig.getExposedPorts();
		if (exposedPorts == null)
			if (isRefDefined)
				exposedPorts = referencedConfig.getExposedPorts();
		if (exposedPorts != null)
			mapExposedPorts(targetContainerInfo, exposedPorts);

		String hostname = sourceContainerConfig.getHostname();
		if (hostname == null)
			if (isRefDefined)
				hostname = referencedConfig.getHostname();
		if (hostname != null)
			targetContainerConfig.setHostname(hostname);

		ContainerConfigurationLabels labels = sourceContainerConfig.getLabels();
		if (labels == null)
			if (isRefDefined)
				labels = referencedConfig.getLabels();
		if (labels != null)
			mapLabels(targetContainerInfo, labels);

		String macAddress = sourceContainerConfig.getMacAddress();
		if (macAddress == null)
			if (isRefDefined)
				macAddress = referencedConfig.getMacAddress();
		if (macAddress != null)
			targetContainerConfig.setMacAddress(macAddress);

		Boolean networkDisabled = sourceContainerConfig.isNetworkDisabled();
		if (networkDisabled == null)
			if (isRefDefined)
				networkDisabled = referencedConfig.isNetworkDisabled();
		if (networkDisabled != null)
			targetContainerConfig.setNetworkDisabled(networkDisabled);

		List<String> onBuild = sourceContainerConfig.getOnBuild();
		if (onBuild == null)
			if (isRefDefined)
				onBuild = referencedConfig.getOnBuild();
		if (onBuild != null)
			targetContainerConfig.getOnBuild().addAll(onBuild);

		Boolean openStdin = sourceContainerConfig.isOpenStdin();
		if (openStdin == null)
			if (isRefDefined)
				openStdin = referencedConfig.isOpenStdin();
		if (openStdin != null)
			targetContainerConfig.setOpenStdin(openStdin);

		Boolean stdinOnce = sourceContainerConfig.isStdinOnce();
		if (stdinOnce == null)
			if (isRefDefined)
				stdinOnce = referencedConfig.isStdinOnce();
		if (stdinOnce != null)
			targetContainerConfig.setStdinOnce(stdinOnce);

		Boolean tty = sourceContainerConfig.isTty();
		if (tty == null)
			if (isRefDefined)
				tty = referencedConfig.isTty();
		if (tty != null)
			targetContainerConfig.setTty(tty);

		String user = sourceContainerConfig.getUser();
		if (user == null)
			if (isRefDefined)
				user = referencedConfig.getUser();
		if (user != null)
			targetContainerConfig.setUser(user);

		ContainerConfigurationVolumes volumes = sourceContainerConfig.getVolumes();
		if (volumes == null)
			if (isRefDefined)
				volumes = referencedConfig.getVolumes();
		if (volumes != null)
			mapVolumes(targetContainerInfo, volumes);

		String workingDir = sourceContainerConfig.getWorkingDir();
		if (workingDir == null)
			if (isRefDefined)
				workingDir = referencedConfig.getWorkingDir();
		if (workingDir != null)
			targetContainerConfig.setWorkingDir(workingDir);

		// map host config
		if (isHostConfigDefined(sourceContainerConfig)) {

			ContainerConfigurationHostConfig srcHostConfig = sourceContainerConfig.getHostConfig();

			// get target host configuration
			HostConfig targetHostConfig = targetContainerConfig.getHostConfig();
			if(targetHostConfig == null) {
				targetContainerInfo.createHostConfiguration();
				targetHostConfig = targetContainerConfig.getHostConfig();
			} 			
			
			// resolve referenced configuration
			ContainerConfigurationHostConfig refHostConfig = null;
			if (isRefDefined)
				refHostConfig = referencedConfig.getHostConfig();
			boolean isRefHostConfigDefined = refHostConfig != null;

			List<String> binds = srcHostConfig.getBinds();
			if (binds == null)
				if (isRefHostConfigDefined)
					binds = refHostConfig.getBinds();
			if (binds != null)
				targetHostConfig.getBinds().addAll(binds);

			Long cpuShares = srcHostConfig.getCpuShares();
			if (cpuShares == null)
				if (isRefHostConfigDefined)
					cpuShares = refHostConfig.getCpuShares();
			if (cpuShares != null)
				targetHostConfig.setCpuShares(cpuShares);

			Long cpuPeriod = srcHostConfig.getCpuPeriod();
			if (cpuPeriod == null)
				if (isRefHostConfigDefined)
					cpuPeriod = refHostConfig.getCpuPeriod();
			if (cpuPeriod != null)
				targetHostConfig.setCpuPeriod(cpuPeriod);

			String cpusetCpus = srcHostConfig.getCpusetCpus();
			if (cpusetCpus == null)
				if (isRefHostConfigDefined)
					cpusetCpus = refHostConfig.getCpusetCpus();
			if (cpusetCpus != null)
				targetHostConfig.setCpusetCpus(cpusetCpus);

			String cpusetMems = srcHostConfig.getCpusetMems();
			if (cpusetMems == null)
				if (isRefHostConfigDefined)
					cpusetMems = refHostConfig.getCpusetMems();
			if (cpusetMems != null)
				targetHostConfig.setCpusetMems(cpusetMems);

			List<String> dns = srcHostConfig.getDns();
			if (dns == null)
				if (isRefHostConfigDefined)
					dns = refHostConfig.getDns();
			if (dns != null)
				targetHostConfig.getDns().addAll(dns);

			List<String> dnsSearch = srcHostConfig.getDnsSearch();
			if (dnsSearch == null)
				if (isRefHostConfigDefined)
					dnsSearch = refHostConfig.getDnsSearch();
			if (dnsSearch != null)
				targetHostConfig.getDnsSearch().addAll(dnsSearch);

			List<String> extraHosts = srcHostConfig.getExtraHosts();
			if (extraHosts == null)
				if (isRefHostConfigDefined)
					extraHosts = refHostConfig.getExtraHosts();
			if (extraHosts != null)
				targetHostConfig.getExtraHosts().addAll(extraHosts);

			List<String> groupAdd = srcHostConfig.getGroupAdd();
			if (groupAdd == null)
				if (isRefHostConfigDefined)
					groupAdd = refHostConfig.getGroupAdd();
			if (groupAdd != null)
				targetHostConfig.getGroupAdd().addAll(groupAdd);

			List<String> links = srcHostConfig.getLinks();
			if (links == null)
				if (isRefHostConfigDefined)
					links = refHostConfig.getLinks();
			if (links != null)
				targetHostConfig.getLinks().addAll(links);

			Long memory = srcHostConfig.getMemory();
			if (memory == null)
				if (isRefHostConfigDefined)
					memory = refHostConfig.getMemory();
			if (memory != null)
				targetHostConfig.setMemory(memory);

			Long memorySwap = srcHostConfig.getMemorySwap();
			if (memorySwap == null)
				if (isRefHostConfigDefined)
					memorySwap = refHostConfig.getMemorySwap();
			if (memorySwap != null)
				targetHostConfig.setMemorySwap(memorySwap);

			ContainerConfigurationHostConfigPortBindings portBindings = srcHostConfig.getPortBindings();
			if (portBindings == null)
				if (isRefHostConfigDefined)
					portBindings = refHostConfig.getPortBindings();
			if (portBindings != null)
				mapPortBindings(targetContainerInfo, portBindings);

			List<String> securityOpt = srcHostConfig.getSecurityOpt();
			if (securityOpt == null)
				if (isRefHostConfigDefined)
					securityOpt = refHostConfig.getSecurityOpt();
			if (securityOpt != null)
				targetHostConfig.getSecurityOpt().addAll(securityOpt);

			List<String> volumesFrom = srcHostConfig.getVolumesFrom();
			if (volumesFrom == null)
				if (isRefHostConfigDefined)
					volumesFrom = refHostConfig.getVolumesFrom();
			if (volumesFrom != null)
				targetHostConfig.getVolumesFrom().addAll(volumesFrom);
		}

	}

	/**
	 * Returns true is host configuration is defined.
	 * 
	 * @param config
	 *            container configuration.
	 * 
	 * @return true is host configuration is defined.
	 */
	boolean isHostConfigDefined(EmbeddedContainerConfiguration config) {
		return (config.getHostConfig() != null);
	}

	/**
	 * Map environment variables.
	 * 
	 * @param targetContainerInfo
	 *            target container info.
	 * @param envs
	 *            environment variables.
	 */
	void mapEnvironmentVariables(ContainerInfo targetContainerInfo, ContainerConfigurationEnvironmentVariables envs) {
		List<ContainerConfigurationEnvironmentVariable> envsList = envs.getVariable();
		for (ContainerConfigurationEnvironmentVariable srcEnv : envsList) {
			targetContainerInfo.addEnvironmentVariable(srcEnv.getName(), srcEnv.getValue());
		}
	}

	/**
	 * Map exposed ports.
	 * 
	 * @param targetContainerInfo
	 *            target container info.
	 * @param exposedPorts
	 *            source exposed ports.
	 */
	void mapExposedPorts(ContainerInfo targetContainerInfo, ContainerConfigurationExposedPorts exposedPorts) {

		List<ContainerConfigurationExposedPort> portList = exposedPorts.getPort();
		for (ContainerConfigurationExposedPort srcExposedPort : portList) {
			switch (srcExposedPort.getType()) {
			case TCP:
				targetContainerInfo.addExposedTcpPort(srcExposedPort.getValue());
				break;

			case UDP:
				targetContainerInfo.addExposedUdpPort(srcExposedPort.getValue());
				break;
			default:
				Object[] args = { srcExposedPort.getType() };
				String message = messageProvider.getMessage("mi.unknown_ipporttype_error", args);
				logger.error(message);
			}
		}
	}

	/**
	 * Map labels.
	 * 
	 * @param targetContainerInfo
	 *            target container info.
	 * @param labels
	 *            source labels.
	 */
	void mapLabels(ContainerInfo targetContainerInfo, ContainerConfigurationLabels labels) {
		List<ContainerConfigurationLabel> labelList = labels.getLabel();
		for (ContainerConfigurationLabel srcLabel : labelList) {
			targetContainerInfo.addLabel(srcLabel.getKey(), srcLabel.getValue());
		}
	}

	/**
	 * Map volumes.
	 * 
	 * @param targetContainerInfo
	 *            target container info.
	 * @param volumes
	 *            source volumes.
	 */
	void mapVolumes(ContainerInfo targetContainerInfo, ContainerConfigurationVolumes volumes) {
		List<ContainerConfigurationVolume> volumeList = volumes.getVolume();
		for (ContainerConfigurationVolume srcVolume : volumeList) {
			targetContainerInfo.addVolume(srcVolume.getMountpoint());
		}
	}

	/**
	 * Map host configuration port bindings.
	 * 
	 * @param targetContainerInfo
	 *            target container info.
	 * @param portBindings
	 *            source bindings.
	 */
	void mapPortBindings(ContainerInfo targetContainerInfo, ContainerConfigurationHostConfigPortBindings portBindings) {

		List<ContainerConfigurationHostConfigPortBinding> portList = portBindings.getBind();
		for (ContainerConfigurationHostConfigPortBinding srcBinding : portList) {
			switch (srcBinding.getType()) {
			case TCP:
				targetContainerInfo.addTcpPortBinding(srcBinding.getContainerPort(), srcBinding.getHostPort());
				break;

			case UDP:
				targetContainerInfo.addTcpPortBinding(srcBinding.getContainerPort(), srcBinding.getHostPort());
				break;
			default:
				Object[] args = { srcBinding.getType() };
				String message = messageProvider.getMessage("mi.unknown_ipportbindingtype_error", args);
				logger.error(message);
			}
		}
	}

	/**
	 * Map string to list of string using space as separator.
	 * 
	 * @param srcValue
	 *            source value.
	 * @param targetList
	 *            target list where splitted string parts are added to..
	 */
	void mapStringToList(String srcValue, List<String> targetList) {
		if (srcValue == null)
			return;
		if (srcValue.isEmpty())
			return;
		String[] stringArray = srcValue.trim().split(REGEX_SPACE_SPLIT);
		for (String entry : stringArray) {
			targetList.add(entry);
		}
	}

}
