/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.alpha.pineapple.plugin.weblogic.scriptingtool.utils;

import static org.junit.Assert.assertFalse;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.plugin.weblogic.scriptingtool.model.Wlst;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * integration test of the {@linkplain ScriptHelperImpl} class.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml",
		"/com.alpha.pineapple.process.execution-config.xml",
		"/com.alpha.pineapple.plugin.weblogic.scriptingtool-config.xml" } )
public class ScriptHelperIntegrationTest {

    /**
     * Current test directory.
     */
    File testDirectory;
	
	/**
	 * Subject under test.
	 */
    @Resource
    ScriptHelper scriptHelper;
        
    /**
     * Object mother for the WLST model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Random dir.
     */
    File randomDir;

    /**
     * Random WLST script.
     */
	File randomWlst;

	
	@Before
	public void setUp() throws Exception {

        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		randomDir = new File(testDirectory, RandomStringUtils.randomAlphabetic(10));		
		randomWlst = new File(randomDir, RandomStringUtils.randomAlphabetic(10));

        // create content mother
        contentMother = new ObjectMotherContent();		        
		
		// create directory
		FileUtils.forceMkdir(randomDir);        				
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test correct response for model with no content.
	 */
	@Test
	public void testIsContentDefined() {
		
        // create content
        Wlst model = contentMother.createEmptyWlst();
		
        // test
        assertFalse(scriptHelper.isModelContentDefined(model));
	}
		
}
