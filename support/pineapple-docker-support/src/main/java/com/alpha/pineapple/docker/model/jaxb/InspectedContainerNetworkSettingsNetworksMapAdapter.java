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
package com.alpha.pineapple.docker.model.jaxb;

import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.alpha.pineapple.docker.model.rest.InspectedContainerNetworkSettingsNetwork;
import com.alpha.pineapple.docker.model.rest.InspectedContainerNetworkSettingsNetworkValue;
import com.alpha.pineapple.docker.model.rest.InspectedContainerNetworkSettingsNetworks;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain InspectedContainerNetworkSettingsNetworks} JAXB generated
 * type.
 */
public class InspectedContainerNetworkSettingsNetworksMapAdapter
		extends XmlAdapter<InspectedContainerNetworkSettingsNetworks, InspectedContainerNetworkSettingsNetworksMap> {

	/**
	 * Object factory.
	 */
	ObjectFactory objectFactory = new ObjectFactory();

	@Override
	public InspectedContainerNetworkSettingsNetworks marshal(InspectedContainerNetworkSettingsNetworksMap map)
			throws Exception {
		InspectedContainerNetworkSettingsNetworks networks = objectFactory
				.createInspectedContainerNetworkSettingsNetworks();
		Set<Entry<String, InspectedContainerNetworkSettingsNetworkValue>> entries = map.entrySet();

		for (Entry<String, InspectedContainerNetworkSettingsNetworkValue> entry : entries) {
			InspectedContainerNetworkSettingsNetwork network = objectFactory
					.createInspectedContainerNetworkSettingsNetwork();
			network.setKey(entry.getKey());
			network.setValue(entry.getValue());
			networks.getNetwork().add(network);
		}
		return networks;

	}

	@Override
	public InspectedContainerNetworkSettingsNetworksMap unmarshal(InspectedContainerNetworkSettingsNetworks value)
			throws Exception {
		InspectedContainerNetworkSettingsNetworksMap map = new InspectedContainerNetworkSettingsNetworksMap();
		for (InspectedContainerNetworkSettingsNetwork network : value.getNetwork()) {

			map.put(network.getKey(), network.getValue());
		}
		return map;
	}

}
