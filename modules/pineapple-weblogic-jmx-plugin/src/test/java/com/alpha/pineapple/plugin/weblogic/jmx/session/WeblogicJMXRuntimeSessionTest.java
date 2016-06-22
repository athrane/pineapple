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


package com.alpha.pineapple.plugin.weblogic.jmx.session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.javautils.StackTraceHelper;

/**
 * Unit test for WeblogicJMXRuntimeSession.
 */
public class WeblogicJMXRuntimeSessionTest
{

    WeblogicJMXRuntimeSession session;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {

        session = null;
    }

    /**
     * Test that session object can be created.
     */
    @Test
    public void testCanCreateJMXSession()
    {
        try
        {
            session = WeblogicJMXRuntimeSessionImpl.getInstance();
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test that is-connected query returns false initially.
     */
    @Test
    public void testIsConnectedQueryReturnsFalseInitially()
    {
        session = WeblogicJMXRuntimeSessionImpl.getInstance();
        try
        {
            assertFalse( session.isConnected() );
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

}
