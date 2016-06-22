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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.oracle.xmlns.weblogic.weblogicDiagnostics.WldfResourceDocument;
import com.oracle.xmlns.weblogic.domain.DomainDocument;

/**
 * Interface for initialization of operations.
 */
public interface ResolvedModelInitializer {

	/**
     * Undefined domain name
     */
    static final String UNDEFINED_DOMAIN_NAME = "null-name";
	
	/**
	 * Initialize resolved model root for traversal of the domain document.
	 * 
	 * @param domainDoc Domain document for primary participant.
	 * 
	 * @return Resolved model root where the primary participant is initialized
	 * with the domain document and the secondary participant is initialized
	 * the object name of the domain MBean.
	 * 
	 * @throws if initialization fails.
	 */
	ResolvedType initialize(DomainDocument domainDoc, WeblogicJMXEditSession session ) throws Exception;

	/**
	 * Initialize resolved model root for traversal of the WLDF resource document.
	 * 
	 * @param wldfResourceDoc WLDF resource document for primary participant.
	 * 
	 * @return Resolved model root where the primary participant is initialized
	 * with the WLDF resource document and the secondary participant is initialized
	 * the object name of the ...
	 * 
	 * @throws if initialization fails. 
	 */	
	ResolvedType initialize(WldfResourceDocument wldfResourceDoc, WeblogicJMXEditSession session ) throws Exception;
	
	
	/**
	 * Initialize resolved model root for traversal of any object. This method
	 * should dispatch calls to the other type specific methods in this interface.
	 * 
	 * @param content document for primary participant.
	 * 
	 * @return Resolved model root where the primary participant is initialized
	 * with the type specific document and the secondary participant is initialized
	 * the object name of the corresponding MBean.
	 * 
	 * @throws if initialization fails. 
	 */
	ResolvedType initialize(Object content, WeblogicJMXEditSession session ) throws Exception;	
}
