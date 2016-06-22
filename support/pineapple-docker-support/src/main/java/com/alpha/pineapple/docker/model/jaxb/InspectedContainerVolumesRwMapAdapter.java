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

import com.alpha.pineapple.docker.model.rest.InspectedContainerVolumeRw;
import com.alpha.pineapple.docker.model.rest.InspectedContainerVolumesRw;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;

/**
 * {@linkplain XmlAdapter} sub class to support customized binding to a map for
 * the {@linkplain InspectedContainerVolumesRwMapAdapter} JAXB generated type.
 */
public class InspectedContainerVolumesRwMapAdapter
	extends XmlAdapter<InspectedContainerVolumesRw, InspectedContainerVolumesRwMap> {

    /**
     * Object factory.
     */
    ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public InspectedContainerVolumesRw marshal(InspectedContainerVolumesRwMap map) throws Exception {
	InspectedContainerVolumesRw volumes = objectFactory.createInspectedContainerVolumesRw();
	Set<Entry<String, Boolean>> entries = map.entrySet();
	for (Entry<String, Boolean> entry : entries) {
	    InspectedContainerVolumeRw volume = objectFactory.createInspectedContainerVolumeRw();
	    volume.setMountpoint(entry.getKey());
	    volume.setEnabled(entry.getValue().booleanValue());
	    volumes.getVolume().add(volume);
	}
	return volumes;
    }

    @Override
    public InspectedContainerVolumesRwMap unmarshal(InspectedContainerVolumesRw value) throws Exception {
	InspectedContainerVolumesRwMap map = new InspectedContainerVolumesRwMap();
	for (InspectedContainerVolumeRw volume : value.getVolume()) {
	    map.put(volume.getMountpoint(), volume.isEnabled());
	}
	return map;
    }

}
