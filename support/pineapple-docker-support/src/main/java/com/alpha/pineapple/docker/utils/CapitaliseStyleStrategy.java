/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.docker.utils;

import org.apache.commons.lang3.StringUtils;

import com.alpha.pineapple.docker.model.rest.CreatedContainer;
import com.alpha.pineapple.docker.model.rest.HostConfig;
import com.alpha.pineapple.docker.model.rest.InspectedContainerNetworkSettings;
import com.alpha.pineapple.docker.model.rest.InspectedContainerNetworkSettingsNetworkValue;
import com.alpha.pineapple.docker.model.rest.InspectedContainerState;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.model.rest.Mount;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Extension of the {@linkplain PropertyNamingStrategy} which implements a capitalise 
 * strategy for JSON marshalled object attributes.
 *
 */
public class CapitaliseStyleStrategy extends PropertyNamingStrategy {
	
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 6837299198068815802L;

	@Override
	public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
				
		// get class name
		String className = method.getDeclaringClass().getName();
				
		// handle case for ListedImage.id
		// - map received JSON property "Id" to ListedImage.id as defined in the
		// Docker schema
		if (className.equals(ListedImage.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("id"))
				return "Id";
		}

		// handle case for ListedContainer.imageId
		// - map received JSON property "ImageID" to ListedImage.imageId as
		// defined in the Docker schema
		if (className.equals(ListedContainer.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("imageId"))
				return "ImageID";
		}

		// handle case for CreatedContainer.id
		// - map received JSON property "Id" to CreatedContainer.id as defined
		// in the Docker schema
		if (className.equals(CreatedContainer.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("id"))
				return "Id";
		}

		// handle case for HostConfig properties
		// example: map received JSON property "UTSMode" to
		// HostConfig.utsMode as defined in the Docker
		// schema
		if (className.equals(HostConfig.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("utsMode"))
				return "UTSMode";
		}

		// handle case for HostConfig properties
		// example: map received JSON property "IOMaximumIOps" to
		// HostConfig.ioMaximumIOps as defined in the
		// Docker
		// schema
		if (className.equals(HostConfig.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ioMaximumIOps"))
				return "IOMaximumIOps";
		}

		// handle case for HostConfig properties
		// example: map received JSON property "IOMaximumBandwidth" to
		// HostConfig.ioMaximumBandwidth as defined in the
		// Docker
		// schema
		if (className.equals(HostConfig.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ioMaximumBandwidth"))
				return "IOMaximumBandwidth";
		}

		// handle case for Mount.rw
		// - map received JSON property "RW" to
		// ContainerMounts.rw as defined in the Docker schema
		if (className.equals(Mount.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("rw"))
				return "RW";
		}

		// handle case for InspectedContainerNetworkSettings properties
		// - example: map received JSON property "IPAddress" to
		// InspectedContainerNetworkSettings.ipAddress as defined in the Docker
		// schema
		if (className.equals(InspectedContainerNetworkSettings.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipAddress"))
				return "IPAddress";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipPrefixLen"))
				return "IPPrefixLen";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipv6Gateway"))
				return "IPv6Gateway";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("endpointId"))
				return "EndpointID";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("networkId"))
				return "NetworkID";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("sandboxId"))
				return "SandboxID";
		}

		// handle case for InspectedContainerNetworkSettingsNetworkValue
		// properties. Example: map received JSON property "IPAddress" to
		// InspectedContainerNetworkSettingsNetworkValue.ipAddress as defined in
		// the Docker schema
		if (className.equals(InspectedContainerNetworkSettingsNetworkValue.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipAddress"))
				return "IPAddress";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipPrefixLen"))
				return "IPPrefixLen";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipv6Gateway"))
				return "IPv6Gateway";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("ipamConfig"))
				return "IPAMConfig";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("endpointId"))
				return "EndpointID";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("globalIpv6Address"))
				return "GlobalIPv6Address";

			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("globalIpv6PrefixLen"))
				return "GlobalIPv6PrefixLen";
		}

		// handle case for InspectedContainerState.oomKilled
		// - map received JSON property "OOMKilled" to
		// InspectedContainerState.oomKilled as defined in the Docker schema
		if (className.equals(InspectedContainerState.class.getName())) {
			// if field name matches name from schema then return name of JSON
			// property.
			if (defaultName.equals("oomKilled"))
				return "OOMKilled";
		}

		// use default capitalise strategy.
		return convert(defaultName);
	}

	/**
	 * Capitalise property.
	 * 
	 * @param name property name.
	 * 
	 * @return capitalized property name.
	 */
	String convert(String name) {
		if (name != null) {
			if (name.equalsIgnoreCase("ip"))
				return name.toUpperCase();
		}
		return StringUtils.capitalize(name);
	}
}
