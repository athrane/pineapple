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

import com.alpha.pineapple.docker.model.rest.ContainerConfigurationExposedPort;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationExposedPorts;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain ContainerConfigurationExposedPorts} JAXB generated type.
 */
public class ContainerConfigurationExposedPortsMapAdapter
	extends XmlAdapter<ContainerConfigurationExposedPorts, ContainerConfigurationExposedPortsMap> {

    /**
     * Object factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public ContainerConfigurationExposedPorts marshal(ContainerConfigurationExposedPortsMap map) throws Exception {
	ContainerConfigurationExposedPorts ports = objectFactory.createContainerConfigurationExposedPorts();
	Set<Entry<String, Object>> entries = map.entrySet();
	for (Entry<String, Object> entry : entries) {
	    ContainerConfigurationExposedPort port = objectFactory.createContainerConfigurationExposedPort();
	    port.setPort(entry.getKey());
	    port.setNullValue(objectFactory.createContainerConfigurationExposedPortNullValue());
	    ports.getPort().add(port);
	}
	return ports;
    }

    @Override
    public ContainerConfigurationExposedPortsMap unmarshal(ContainerConfigurationExposedPorts value) throws Exception {
	ContainerConfigurationExposedPortsMap map = new ContainerConfigurationExposedPortsMap();
	for (ContainerConfigurationExposedPort port : value.getPort()) {
	    map.put(port.getPort(), port.getNullValue());
	}
	return map;
    }

}
