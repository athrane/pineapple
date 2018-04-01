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

package com.alpha.pineapple.plugin.filesystem.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.filesystem.command.TestVfsFilePropertiesCommand;
import com.alpha.pineapple.plugin.filesystem.model.Filesystem;
import com.alpha.pineapple.plugin.filesystem.model.FilesystemRoot;
import com.alpha.pineapple.plugin.filesystem.model.MapperImpl;
import com.alpha.pineapple.plugin.filesystem.model.ObjectFactory;
import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;

/**
 * Unit test of the class {@link MapperImpl}.
 */
public class MapperTest {

	/**
	 * Some context key
	 */
	static final String CONFIG_KEY = "some-key";

	/**
	 * First array index.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Object under test.
	 */
	MapperImpl mapper;

	/**
	 * Test case factory
	 */
	ObjectFactory pluginFactory;

	/*
	 * Command context.
	 */
	Context context;

	/**
	 * Mock file system session.
	 */
	FileSystemSession session;

	@Before
	public void setUp() throws Exception {
		// create mapper
		mapper = new MapperImpl();

		// create test case factory
		pluginFactory = new com.alpha.pineapple.plugin.filesystem.model.ObjectFactory();

		// create context
		context = new ContextBase();

		// create mock session
		session = EasyMock.createMock(FileSystemSession.class);

	}

	@After
	public void tearDown() throws Exception {
		mapper = null;
		pluginFactory = null;
		context = null;
		session = null;
	}

	/**
	 * Test that file system root can be mapped.
	 */
	@Test
	public void testCanMapFileSystemRoot() {
		// create file system root
		FilesystemRoot root = pluginFactory.createFilesystemRoot();

		// configure root
		String path = "/some-directory/another-directory";
		root.setTargetPath(path);

		// map values
		mapper.mapVfsFilePropertiesTest(root, session, context);

		// test
		assertEquals(path, context.get(TestVfsFilePropertiesCommand.PATH_KEY));
		assertEquals(session, context.get(TestVfsFilePropertiesCommand.SESSION_KEY));
	}

}
