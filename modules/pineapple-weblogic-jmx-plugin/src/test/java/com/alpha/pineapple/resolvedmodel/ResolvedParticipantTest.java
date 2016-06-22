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


package com.alpha.pineapple.resolvedmodel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;

/**
 * unit test of the class <code>ResolvedParticipantImpl>/code>
 */
public class ResolvedParticipantTest
{
    /**
     * Object under test.
     */
    ResolvedParticipant participant;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
        participant = null;
    }

    /**
     * Test that successful result can be created.
     */
    @Test
    public void testCreateSuccessfulResultString()
    {
        String name = "root";
        Class<? extends Object> type = String.class;
        Object value = "som-value";
        participant = ResolvedParticipantImpl.createSuccessfulResult( name, type, value );
        
        // test
        assertNotNull( participant );
    }

    /**
     * Test that unsuccessful result can be created.
     */
    @Test
    public void testCreateUnsuccessfulResultString()
    {
        String name = "root";
        Class<? extends Object> type = String.class;
        Object value = "som-value";
        Exception exception = new Exception("some description");
        participant = ResolvedParticipantImpl.createUnsuccessfulResult( name, type, value, exception );
        
        // test
        assertNotNull( participant );
    }
    
    /**
     * Test that is resolution successful return true for successful result.
     */
    @Test
    public void testIsResolutionSuccesfulReturnTrueForSuccessfulResult()
    {
        String name = "root";
        Class<? extends Object> type = String.class;
        Object value = "som-value";
        participant = ResolvedParticipantImpl.createSuccessfulResult( name, type, value );
        
        // test
        assertTrue( participant.isResolutionSuccesful() );
    }

    /**
     * Test that is resolution successful return false for unsuccessful result.
     */
    @Test
    public void testIsResolutionSuccesfulReturnFalseTrueForUnsuccessfulResult()
    {
        String name = "root";
        Class<? extends Object> type = String.class;
        Object value = "som-value";
        Exception exception = new Exception("some description");
        participant = ResolvedParticipantImpl.createUnsuccessfulResult( name, type, value, exception );
        
        // test
        assertFalse( participant.isResolutionSuccesful() );
    }
    
    /**
     * Test that is value state is FAILED for for unsuccessful result.
     */
    @Test
    public void testIsValueStateIsFailedForForUnsuccessfulResult()
    {
        String name = "root";
        Class<? extends Object> type = String.class;
        Object value = "som-value";
        Exception exception = new Exception("some description");
        participant = ResolvedParticipantImpl.createUnsuccessfulResult( name, type, value, exception );
        
        // test
        assertEquals( ValueState.FAILED, participant.getValueState() );
    }
    
}
