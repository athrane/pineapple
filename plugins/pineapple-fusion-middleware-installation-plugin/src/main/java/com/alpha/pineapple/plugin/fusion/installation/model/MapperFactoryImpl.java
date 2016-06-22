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


package com.alpha.pineapple.plugin.fusion.installation.model;

import javax.annotation.Resource;





/**
 * Factory which provides release specific mapper for a model.  
 */
public class MapperFactoryImpl {
    
    /**
     * Release 11.x mapper.
     */
    @Resource
    ModelMapper appDevRuntimeR11Mapper;       

    /**
     * Release 11.x mapper.
     */
    @Resource
    ModelMapper webCenterR11Mapper;       

    /**
     * Release 11.x mapper.
     */
    @Resource
    ModelMapper soaSuiteR11Mapper;       

    /**
     * Release 11.x mapper.
     */
    @Resource
    ModelMapper serviceBusR11Mapper;       
    
	/**
	 * Initialize mapper for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized mapper for current installer release.
	 */
	public ModelMapper getApplicationDevelopmentRuntimeMapper(FusionMiddlewareInstallation model) {
		
		if (model.getApplicationDevelopmentRuntime().getRelease11() != null ) return appDevRuntimeR11Mapper;  		
		return null;
	}

	/**
	 * Initialize mapper for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized mapper for current installer release.
	 */
	public ModelMapper getWebCenterMapper(FusionMiddlewareInstallation model) {
		
		if (model.getWebcenter().getRelease11() != null ) return webCenterR11Mapper;  		
		return null;
	}

	/**
	 * Initialize mapper for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized mapper for current installer release.
	 */
	public ModelMapper getSoaSuiteMapper(FusionMiddlewareInstallation model) {
		
		if (model.getSoaSuite().getRelease11() != null ) return soaSuiteR11Mapper;  		
		return null;
	}

	/**
	 * Initialize mapper for current installer release.
	 * 
	 * @param model Plugin model. 
	 * 
	 * @return initialized mapper for current installer release.
	 */
	public ModelMapper getServiceBusMapper(FusionMiddlewareInstallation model) {
		
		if (model.getSoaOsb().getRelease11() != null ) return serviceBusR11Mapper;  		
		return null;
	}
	
}
