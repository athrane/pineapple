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


package com.alpha.javautils.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.apache.commons.chain.Command;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.InitializeAnnotatedCommand;
import com.alpha.pineapple.command.MultipleInitializeAnnontatedCommand;
import com.alpha.pineapple.command.NullCommand;
import com.alpha.pineapple.command.initialization.Initialize;

/**
 * Unit test for the class {@link AnnotationFinder}
 */
public class AnnotationFinderTest
{

    /**
     * Object under test.
     */
    AnnotationFinder finder;

    @Before
    public void setUp() throws Exception
    {
        finder = new AnnotationFinder();
    }

    @After
    public void tearDown() throws Exception
    {
        finder = null;
    }

    /**
     * Create annotation finder instance.
     * 
     * @return annotation finder instance.
     */
    AnnotationFinder createFinder()
    {
        return new AnnotationFinder();
    }

    /**
     * Test that finder instance can be created.
     */
    @Test
    public void testCanCreateInstance()
    {
        try
        {
            finder = createFinder();
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test that annotation finder rejects undefined context.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRejectsUndefinedAnnotatedObject()
    {
        // create finder
        finder = createFinder();

        // force exception
        finder.findAnnotatedFields( null, Initialize.class );

        fail( "Test should never reach here." );
    }

    /**
     * Test that annotation finder rejects undefined annotation.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testRejectsUndefinedAnnotation()
    {
        // create finder
        finder = createFinder();

        // force exception
        finder.findAnnotatedFields( new String(), null);

        fail( "Test should never reach here." );
    }
    
    /**
     * Test that finder finds a zero annotated fields.
     */
    @Test
    public void testFindsZeroAnnotatedFields()
    {
        // create finder
        finder = createFinder();

        // setup objects
        Command command = new NullCommand();

        // find annotations
        Field[] result = finder.findAnnotatedFields( command, Initialize.class );

        // test
        assertEquals( 0, result.length );
    }

    /**
     * Test that finder finds a single annotated field.
     */
    @Test
    public void testFindsSingleAnnotatedField()
    {
        // create finder
        finder = createFinder();

        // setup objects
        Command command = new InitializeAnnotatedCommand();

        // find annotations
        Field[] result = finder.findAnnotatedFields( command, Initialize.class );

        // test
        assertEquals( 1, result.length );
    }

    /**
     * Test that finder the correct single annotated field.
     */
    @Test
    public void testFindsCorrectSingleAnnotatedField()
    {
        // create finder
        finder = createFinder();

        // setup objects
        Command command = new InitializeAnnotatedCommand();

        // find annotations
        Field[] result = finder.findAnnotatedFields( command, Initialize.class );

        // test
        assertEquals( "annotatedField", result[0].getName() );
    }

    /**
     * Test that finder finds a two annotated fields.
     */
    @Test
    public void testFindsMultipleAnnotatedFields()
    {
        // create finder
        finder = createFinder();

        // setup objects
        Command command = new MultipleInitializeAnnontatedCommand();

        // find annotations
        Field[] result = finder.findAnnotatedFields( command, Initialize.class );

        // test
        assertEquals( 2, result.length );
    }

    /**
     * Test that finder the correct annotated fields.
     */
    @Test
    public void testFindsCorrectMultipleAnnotatedFields()
    {
        // create finder
        finder = createFinder();

        // setup objects
        Command command = new MultipleInitializeAnnontatedCommand();

        // find annotations
        Field[] result = finder.findAnnotatedFields( command, Initialize.class );

        // test
        assertEquals( "annotatedField1", result[0].getName() );
        assertEquals( "annotatedField2", result[1].getName() );

    }

}
