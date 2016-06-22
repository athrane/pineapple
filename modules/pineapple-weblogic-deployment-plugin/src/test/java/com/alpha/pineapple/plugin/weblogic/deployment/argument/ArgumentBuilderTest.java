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


package com.alpha.pineapple.plugin.weblogic.deployment.argument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.jsr88.command.result.AvailableModulesResult;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test for the {@linkplain ArgumentBuilder}class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class})
public class ArgumentBuilderTest
{
	/**
     * Current test directory.
     */
	File testDirectory;
	
    /**
     * First list entry.
     */
    final static int FIRST_LIST_INDEX = 0;

    /**
     * Second list entry.
     */    
    final static int SECOND_LIST_INDEX = 1;
        
    /**
     * Object under test.
     */
    ArgumentBuilder builder;
    
    /**
     * Mock file locator.
     */
    FileLocator locator;
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    
    
    /**
     * Random name.
     */
    String randomName;

    /**
     * Random name.
     */
    String randomName2;

    /**
     * Random argument.
     */
    String randomArgument;

    /**
     * Random directory.
     */
    File randomDir;
    
    @Before
    public void setUp() throws Exception
    {
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
    	
    	randomName = RandomStringUtils.randomAlphabetic(10);
    	randomName2 = RandomStringUtils.randomAlphabetic(10);
    	randomArgument = RandomStringUtils.randomAlphabetic(10);
    	randomDir= new File(testDirectory, RandomStringUtils.randomAlphabetic(10));
    	
    	// create builder
    	builder = new ArgumentBuilder();
    	
        // create mock asserter
    	locator = EasyMock.createMock( FileLocator.class );
        
        // inject asserter
        ReflectionTestUtils.setField( builder, "fileLocator", locator );
        
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( builder, "messageProvider", messageProvider );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 

        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                                
    }    

    @After
    public void tearDown() throws Exception
    {
    }
    
    
    /**
     * Test that empty build process returns defined argument list.
     */
    @Test
    public void testBuildReturnsDefinedArgumentList()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
    	        
        // start building argument list
        builder.buildArgumentList();
        
        // test
        assertNotNull(  builder.getArgumentList() );
        
    	// test
    	EasyMock.verify(locator);                
    }

    /**
     * Test that empty build process returns empty argument list.
     */
    @Test
    public void testEmptyBuildReturnsEmptyArgumentList()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
    	    	        
        // start building argument list
        builder.buildArgumentList();
        
        // get size
        int actual = builder.getArgumentList().length;
        
        // test
        assertEquals(  0, actual );
        
    	// test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Test that single argument is added to argument list.
     */
    @Test
    public void testAddSingleArgument()
    {    	    
    	// complete mock setup
    	EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addSingleArgument( randomArgument  );
        
        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 1, product.length );   
        assertEquals( randomArgument, product[FIRST_LIST_INDEX]);   
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that deployer argument is added to argument list. 
     */
    @Test
    public void testAddDeployerArgument()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addWebLogicDeployerArgument();
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 1, product.length );   
        assertEquals( ArgumentBuilder.DEPLOYER_ARGUMENT, product[FIRST_LIST_INDEX]);          
        
    	// test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Test that upload argument is added to argument list. 
     */
    @Test
    public void testAddUploadArgument()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addUploadArgument();
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 1, product.length );   
        assertEquals( ArgumentBuilder.UPLOAD_ARGUMENT, product[FIRST_LIST_INDEX]);          
        
    	// test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Test that stage argument is added to argument list. 
     */
    @Test
    public void testAddStageArgument()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addStageArgument();
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 1, product.length );   
        assertEquals( ArgumentBuilder.STAGE_ARGUMENT, product[FIRST_LIST_INDEX]);          
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that no exclusive lock argument is added to argument list. 
     */
    @Test
    public void testNoExclusiveLockArgument()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addNoExclusiveLockArgument();
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 1, product.length );   
        assertEquals( ArgumentBuilder.NOEXCLUSIVELOCK_ARGUMENT, product[FIRST_LIST_INDEX]);          
        
    	// test
    	EasyMock.verify(locator);                        
    }

    
    
    
    /**
     * Test that name argument is added to argument list. 
     */
    @Test
    public void testAddNameArgument()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addNameArgument( randomName );
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.NAME_ARGUMENT, product[FIRST_LIST_INDEX]);          
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that name value is added to argument list. 
     */
    @Test
    public void testAddNameValue()
    {
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addNameArgument( randomName );
        
        // get product
        String[] product = builder.getArgumentList();
        
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.NAME_ARGUMENT, product[FIRST_LIST_INDEX]);   
        assertEquals( randomName, product[SECOND_LIST_INDEX] );       
                
    	// test
    	EasyMock.verify(locator);                        
    }

    
    /**
     * Test that time stamped name argument is added to argument list. 
     */
    @Test
    public void testAddTimestampedNameArgument()
    {
        // complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addTimeStampedNameArgument( randomName );
        
        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.NAME_ARGUMENT, product[FIRST_LIST_INDEX]);   
        
    	// test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Test that time stamped name value is added to argument list. 
     */
    @Test
    public void testAddTimestampedNameValue()
    {       
    	// complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList( );        
        
        // add argument
        builder.addTimeStampedNameArgument( randomName );
        
        // get product
        String[] product = builder.getArgumentList();
        
        // get argument        
        String actual = product[SECOND_LIST_INDEX];
                
        // time stamp: under score
        String expectedUnderscore = "_";
        String actualUnderscore = actual.substring( 10, 11 );
        
        // time stamp: YYYYmmDD
        String actualDate = actual.substring( 11, 19 );       

        // time stamp: -
        String expectedMinus = "-";
        String actualMinus = actual.substring( 19, 20 );

        // time stamp: HHmmss
        String actualTime = actual.substring( 20, 26 );        
        
        //test
        assertEquals( expectedUnderscore, actualUnderscore);        
        assertTrue( StringUtils.isNumeric( actualDate ) );
        assertEquals( expectedMinus, actualMinus);        
        assertTrue( StringUtils.isNumeric( actualTime ) );
        
    	// test
    	EasyMock.verify(locator);                        
    }

    
    /**
     * Test that adding time stamped name argument 
     * fails if module name can't be found. 
     * 
     * @throws ArgumentBuilderException If test fails.
     */
    @Test(expected=ArgumentBuilderException.class)
    public void testAddTimestampedNameFailsIfModuleCantBeFound() throws ArgumentBuilderException
    {
    	TargetModuleID[] emptyResult = {};
    	
    	// create mock
    	AvailableModulesResult modules = EasyMock.createMock(AvailableModulesResult.class);
		EasyMock.expect(modules.findModulesStartingWith(randomName))
    		.andReturn(emptyResult);
    	
    	EasyMock.replay(modules);
    	
        // complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addTimeStampedNameArgument( randomName, modules );
        
        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.NAME_ARGUMENT, product[FIRST_LIST_INDEX]);   
        
    	// test
    	EasyMock.verify(locator);
    	EasyMock.verify(modules);    	
    }
    
    /**
     * Test that adding time stamped name argument  
     * succeeds if module name is found. 
     * 
     * @throws ArgumentBuilderException If test fails.
     */
    @Test
    public void testAddTimestampedNameSucceedsIfModuleIsFound() throws ArgumentBuilderException
    {
    	// create mock    	
    	TargetModuleID moduleId = EasyMock.createMock(TargetModuleID .class);
		EasyMock.expect(moduleId.getModuleID())
			.andReturn(randomName+randomName2);    	    	
    	EasyMock.replay(moduleId);    	
    	
		TargetModuleID[] emptyResult = { moduleId };
    	
    	// create mock
    	AvailableModulesResult modules = EasyMock.createMock(AvailableModulesResult.class);
		EasyMock.expect(modules.findModulesStartingWith(randomName))
    		.andReturn(emptyResult);    	
    	EasyMock.replay(modules);
    	
        // complete mock setup
    	EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
        
        // add argument
        builder.addTimeStampedNameArgument( randomName, modules );
        
        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.NAME_ARGUMENT, product[FIRST_LIST_INDEX]);   
        
    	// test
    	EasyMock.verify(locator);
    	EasyMock.verify(modules);    	
    }

    
    
    /**
     * Test that source argument is added to argument list. 
     */
    @Test
    public void testAddSourceArgument() throws Exception
    {    	
    	// complete mock setup
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addSourceArgument( randomDir );

        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.SOURCE_ARGUMENT, product[FIRST_LIST_INDEX]);   
                
    	// test
    	EasyMock.verify(locator);    	
    }

    /**
     * Test that source value is added to argument list. 
     */
    @Test
    public void testAddSourceValue() throws Exception
    {        
    	// complete mock setup
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addSourceArgument( randomDir );

        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.SOURCE_ARGUMENT, product[FIRST_LIST_INDEX]);   
        assertEquals( randomDir.getAbsolutePath(), product[SECOND_LIST_INDEX] );       
        
    	// test
    	EasyMock.verify(locator);                        
    }
        
    /**
     * Add source argument fails if deployment artifact can't be found 
     */
    @Test( expected = ArgumentBuilderException.class )
    public void testAddGeneratedSourceArgumentFailsIfDeploymentArtifactCantBeLocated () throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] emptyFileSet = { }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("war"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("jar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("rar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateSubDirectories(
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);        
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );
        
    	// test
    	EasyMock.verify(locator);                        
    }
    
    
    /**
     * Add source argument fails if RAR deployment artifact is found. 
     */
    @Test
    public void testAddGeneratedSourceArgumentSuccedsIfRarDeploymentArtifactIsLocated () throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] emptyFileSet = { }; 
        String[] fileSet = { randomName }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("war"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("jar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("rar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(fileSet);
        EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Add source argument fails if JAR deployment artifact is found. 
     */
    @Test
    public void testAddSourceArgumentSuccedsIfJarDeploymentArtifactIsLocated () throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] emptyFileSet = { }; 
        String[] fileSet = { randomName }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("war"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("jar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(fileSet);
        EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList( );        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Add source argument fails if War deployment artifact is found. 
     */
    @Test
    public void testAddSourceArgumentSuccedsIfWarDeploymentArtifactIsLocated () throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] emptyFileSet = { }; 
        String[] fileSet = { randomName }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("war"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(fileSet);
        EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );

        // test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Add source argument fails if EAR deployment artifact is found. 
     */
    @Test
    public void testAddSourceArgumentSuccedsIfEarDeploymentArtifactIsLocated() throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] fileSet = { randomName }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(fileSet);
        EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Add source argument succeeds if directory deployment artifact is found. 
     */
    @Test
    public void testAddSourceArgumentSuccedsIfDirDeploymentArtifactIsLocated() throws Exception
    {
        File queriedPath = new File(randomDir,"app");
        String[] emptyFileSet = { }; 
        String[] fileSet = { randomName }; 
        
    	// complete mock setup
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("ear"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("war"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("jar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateFileSet(
        		EasyMock.eq("rar"), 
        		EasyMock.eq(queriedPath)))
        	.andReturn(emptyFileSet);
        EasyMock.expect(locator.locateSubDirectories(
        		EasyMock.eq(queriedPath)))
        	.andReturn(fileSet);        
        EasyMock.replay(locator);
        
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedSourceArgument( randomDir );
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that plan argument is added to argument list. 
     */
    @Test
    public void testAddPlanArgument() throws Exception
    {
    	
    	// complete mock setup
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addPlanArgument( randomDir );

        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.PLAN_ARGUMENT, product[FIRST_LIST_INDEX]);   
                
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that plan value is added to argument list. 
     */
    @Test
    public void testAddPlanValue() throws Exception
    {        
    	// complete mock setup
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addPlanArgument( randomDir );

        // get product
        String[] product = builder.getArgumentList();
        
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.PLAN_ARGUMENT, product[FIRST_LIST_INDEX]);   
        assertEquals( randomDir.getAbsolutePath(), product[SECOND_LIST_INDEX] );       
                
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that non existing plan directory results 
     * in plan argument isn't added to argument list. 
     */
    @Test
    public void testAddGeneratedPlanArgumentIgnoresNonExistingPlanDir() throws Exception
    {
    	// complete mock setup
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedPlanArgument( randomDir, randomName );

        // get product
        String[] product = builder.getArgumentList();
               
        //test
        assertEquals( 0, product.length );   
                
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that non existing plan file results 
     * in plan argument isn't added to argument list. 
     */
    @Test
    public void testAddGeneratedPlanArgumentIgnoresNonExistingPlanFile() throws Exception
    {
    	String randomPlan = randomName+".xml";
    	
    	// create plan dir
    	File planDir = new File(randomDir, "plan"); 
    	planDir.mkdirs();

        //test
        assertTrue( planDir.exists() );       	
    	
    	// complete mock setup
        EasyMock.expect(locator.fileExists(
        		EasyMock.eq(planDir), 
        		EasyMock.eq("plan.xml")))
        	.andReturn(false);
        EasyMock.expect(locator.fileExists(
        		EasyMock.eq(planDir), 
        		EasyMock.eq(randomPlan)))
        	.andReturn(false);        		        
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedPlanArgument( randomDir, randomName );

        // get product
        String[] product = builder.getArgumentList();
               
        //test
        assertEquals( 0, product.length );   
                
    	// test
    	EasyMock.verify(locator);                        
    }
    
    /**
     * Test that plan file named "plan.xml" is added. 
     */
    @Test
    public void testAddGeneratedPlanArgumentAddsPlanXml() throws Exception
    {    	
    	// create plan dir
    	File planDir = new File(randomDir, "plan"); 
    	planDir.mkdirs();

    	File expectedPlan = new File(planDir,"plan.xml"); 
    	    	
        //test
        assertTrue( planDir.exists() );       	
    	
    	// complete mock setup
        EasyMock.expect(locator.fileExists(
        		EasyMock.eq(planDir), 
        		EasyMock.eq("plan.xml")))
        	.andReturn(true);
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedPlanArgument( randomDir, randomName );

        // get product
        String[] product = builder.getArgumentList();
               
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.PLAN_ARGUMENT, product[FIRST_LIST_INDEX]);   
        assertEquals( expectedPlan.getAbsolutePath(), product[SECOND_LIST_INDEX] );       
        
    	// test
    	EasyMock.verify(locator);                        
    }

    /**
     * Test that custom named plan file is added. 
     */
    @Test
    public void testAddGeneratedPlanArgumentAddsCustomPlanFile() throws Exception
    {
    	String randomPlan = randomName+".xml";
    	
    	// create plan dir
    	File planDir = new File(randomDir, "plan"); 
    	planDir.mkdirs();

    	File expectedPlan = new File(planDir,randomPlan); 
    	    	
        //test
        assertTrue( planDir.exists() );       	
    	
    	// complete mock setup
        EasyMock.expect(locator.fileExists(
        		EasyMock.eq(planDir), 
        		EasyMock.eq("plan.xml")))
        	.andReturn(false);
        EasyMock.expect(locator.fileExists(
        		EasyMock.eq(planDir), 
        		EasyMock.eq(randomPlan)))
        	.andReturn(true);        		                
        EasyMock.replay(locator);
                
        // start building argument list
        builder.buildArgumentList();        
                
        // add argument
        builder.addGeneratedPlanArgument( randomDir, randomName );

        // get product
        String[] product = builder.getArgumentList();
               
        //test
        assertEquals( 2, product.length );   
        assertEquals( ArgumentBuilder.PLAN_ARGUMENT, product[FIRST_LIST_INDEX]);   
        assertEquals( expectedPlan.getAbsolutePath(), product[SECOND_LIST_INDEX] );       
        
    	// test
    	EasyMock.verify(locator);                        
    }
        
}
