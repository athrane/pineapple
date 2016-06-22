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

import com.alpha.pineapple.docker.model.rest.ContainerConfigurationVolume;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationVolumes;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain ContainerConfigurationVolumesMapAdapter} JAXB generated type.
 */
public class ContainerConfigurationVolumesMapAdapter
	extends XmlAdapter<ContainerConfigurationVolumes, ContainerConfigurationVolumesMap> {

    /**
     * Object factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public ContainerConfigurationVolumes marshal(ContainerConfigurationVolumesMap map) throws Exception {
	ContainerConfigurationVolumes volumes = objectFactory.createContainerConfigurationVolumes();
	Set<Entry<String, Object>> entries = map.entrySet();
	for (Entry<String, Object> entry : entries) {
	    ContainerConfigurationVolume volume = objectFactory.createContainerConfigurationVolume();
	    volume.setMountpoint(entry.getKey());
	    volume.setNullValue(objectFactory.createContainerConfigurationVolumeNullValue());
	    volumes.getVolume().add(volume);
	}
	return volumes;
    }

    @Override
    public ContainerConfigurationVolumesMap unmarshal(ContainerConfigurationVolumes value) throws Exception {
	ContainerConfigurationVolumesMap map = new ContainerConfigurationVolumesMap();
	for (ContainerConfigurationVolume volume : value.getVolume()) {
	    map.put(volume.getMountpoint(), volume.getNullValue());
	}
	return map;
    }

}
