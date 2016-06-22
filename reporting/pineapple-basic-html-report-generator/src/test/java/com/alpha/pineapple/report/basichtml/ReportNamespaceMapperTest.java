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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Unit test of the class <code>ReportNamespaceMapper</code>.
 */
public class ReportNamespaceMapperTest {

	/**
	 * Objec under test.
	 */
	NamespacePrefixMapper mapper;
	
	@Before
	public void setUp() throws Exception {
		
		mapper = new ReportNamespaceMapper();
	}

	@After
	public void tearDown() throws Exception {
		mapper = null;
	}

	/**
	 * Test that PreDeclaredNamespaceUri list contains a single entry.
	 */
	@Test
	public void testPreDeclaredNamespaceUriContainsSingleEntry() {
		assertEquals(1, mapper.getPreDeclaredNamespaceUris().length);
	}

	/**
	 * Test that PreDeclaredNamespaceUri list contains report name space.
	 */
	@Test
	public void testPreDeclaredNamespaceUriContainsReportNamespace() {
		assertEquals(ReportNamespaceMapper.BASIC_HTML_REPORT_NS, mapper.getPreDeclaredNamespaceUris()[0]);
	}
	
	/**
	 * Test that report name space is mapped to rbh prefix.
	 */
	@Test
	public void testNamespaceIsMappedToCorrextPrefix() {
		String namespace = ReportNamespaceMapper.BASIC_HTML_REPORT_NS;		
		String expected = ReportNamespaceMapper.RBH_PREFIX;
		assertEquals(expected, mapper.getPreferredPrefix(namespace, null, true));
		
	}

	/**
	 * Test that unknown name space is mapped to null.
	 */
	@Test
	public void testUnknownNamespaceIsMappedToNull() {
		String namespace = "http://unknown-namespace";		
		assertEquals(null, mapper.getPreferredPrefix(namespace, null, true));
		
	}
	
}
