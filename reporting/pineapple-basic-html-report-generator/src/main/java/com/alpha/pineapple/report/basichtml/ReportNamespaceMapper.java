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


package com.alpha.pineapple.report.basichtml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Implementation of the <code>NamespacePrefixMapper</code>
 * which map name spaces for the basic HTML report generator.
 */
class ReportNamespaceMapper extends NamespacePrefixMapper {

	/**
	 * Report name space prefix.
	 */
	static final String RBH_PREFIX = "rbh";
	
	/**
	 * Name space URL. 
	 */
	static final String BASIC_HTML_REPORT_NS = "http://pineapple.dev.java.net/ns/report/basic_html_1_0";

	@Override
	public String[] getPreDeclaredNamespaceUris() {
		return new String[] { BASIC_HTML_REPORT_NS };
	}

	@Override
	public String getPreferredPrefix(String namespaceUri, String arg1, boolean arg2) {
		if (namespaceUri.equals(BASIC_HTML_REPORT_NS)) return RBH_PREFIX;
		return null; // use default namespace
	}
}
