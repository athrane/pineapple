/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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


package com.alpha.pineapple.plugin.weblogic.installation.model;

import javax.annotation.Resource;

/**
 * Factory which provides release specific mapper for a model.  
 */
public class MapperFactoryImpl {

    /**
     * Release 9.x mapper.
     */
	
	@Resource
    Mapper release9Mapper;   
    
    /**
     * Release 10.x mapper.
     */
    @Resource
    Mapper release10Mapper;       

    /**
     * Release 12.1.1 mapper.
     */
    @Resource
    Mapper release12Mapper;       

    /**
     * Release 12.1.2 mapper.
     */
    @Resource
    UniversalInstallerMapper release1212Mapper;       
    
	/**
	 * Initialize mapper for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized mapper for current installer release.
	 */
	public Mapper getMapper(WeblogicInstallation model) {
		
		if (model.getRelease9() != null ) return release9Mapper;;  		
		if (model.getRelease10() != null ) return release10Mapper;
		if (model.getRelease12() != null ) return release12Mapper;
		return null;
	}

	/**
	 * get mapper for release 12.1.2.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return mapper for release.
	 */
	public UniversalInstallerMapper getRelease1212Mapper(WeblogicInstallation model) {
		
		if (model.getRelease1212() != null ) return release1212Mapper;  		
		return null;
	}
	
}
