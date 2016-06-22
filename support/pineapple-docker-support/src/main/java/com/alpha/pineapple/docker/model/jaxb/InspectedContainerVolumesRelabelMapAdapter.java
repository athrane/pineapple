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

import com.alpha.pineapple.docker.model.rest.InspectedContainerVolumeRelabel;
import com.alpha.pineapple.docker.model.rest.InspectedContainerVolumesRelabel;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain InspectedContainerVolumesRelabelMapAdapter} JAXB generated
 * type.
 */
public class InspectedContainerVolumesRelabelMapAdapter
	extends XmlAdapter<InspectedContainerVolumesRelabel, InspectedContainerVolumesRelabelMap> {

    /**
     * Object factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public InspectedContainerVolumesRelabel marshal(InspectedContainerVolumesRelabelMap map) throws Exception {
	InspectedContainerVolumesRelabel volumes = objectFactory.createInspectedContainerVolumesRelabel();
	Set<Entry<String, String>> entries = map.entrySet();
	for (Entry<String, String> entry : entries) {
	    InspectedContainerVolumeRelabel volume = objectFactory.createInspectedContainerVolumeRelabel();
	    volume.setMountpoint(entry.getKey());
	    volume.setLabel(entry.getValue());
	    volumes.getVolume().add(volume);
	}
	return volumes;
    }

    @Override
    public InspectedContainerVolumesRelabelMap unmarshal(InspectedContainerVolumesRelabel value) throws Exception {
	InspectedContainerVolumesRelabelMap map = new InspectedContainerVolumesRelabelMap();
	for (InspectedContainerVolumeRelabel volume : value.getVolume()) {
	    map.put(volume.getMountpoint(), volume.getLabel());
	}
	return map;
    }

}
