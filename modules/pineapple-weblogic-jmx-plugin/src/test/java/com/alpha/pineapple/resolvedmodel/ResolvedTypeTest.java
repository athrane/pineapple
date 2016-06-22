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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;

/**
 * Unit test of the class <code>ResolvedTypeImpl</code>.
 */
public class ResolvedTypeTest
{

    /**
     * Object under test.
     */
    ResolvedType type;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
        type = null;
    }


    /**
     * Test that primary participant is stored correct.
     */
    @Test
    public void testGetPrimaryParticipantForResolvedType()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );        
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( primaryParticipant);
        EasyMock.replay( secondaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );

        // test
        assertNotNull( type.getPrimaryParticipant() );
        assertEquals( primaryParticipant, type.getPrimaryParticipant() );
    }

    /**
     * Test that secondary participant is stored correct.
     */
    @Test
    public void testGetSecondaryParticipantForResolvedType()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( primaryParticipant);
        EasyMock.replay( secondaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );

        // test
        assertNotNull( type.getSecondaryParticiant() );
        assertEquals( secondaryParticipant, type.getSecondaryParticiant() );
    }
    
    /**
     * Test that primary participant is stored correct.
     */
    @Test
    public void testGetPrimaryParticipantForResolvedObject()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        assertNotNull( type.getPrimaryParticipant() );
        assertEquals( primaryParticipant, type.getPrimaryParticipant().getValue() );
    }

    /**
     * Test that secondary participant is stored correct.
     */    
    @Test
    public void testGetSecondaryParticiantForResolvedObject()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        // test
        assertNotNull( type.getSecondaryParticiant() );
        assertEquals( secondaryParticipant, type.getSecondaryParticiant().getValue() );
    }

    /**
     * Get parent returns null when is created as resolved objects.
     */
    @Test
    public void testGetParentReturnsNull()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        // test 
        assertNull( type.getParent() );
    }

    /**
     * Get parent returns defined object when created as resolved type.
     */
    @Test
    public void testGetParentReturnsDefinedObjectForResolvedType()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );

        // test
        assertNotNull( type.getParent() );
        assertEquals( parent, type.getParent() );
    }
            
    /**
     * Get parent returns defined object when created as resolved object.
     */
    @Test
    public void testGetParentReturnsDefinedObjectForResolvedObject()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        
        type = ResolvedTypeImpl.createResolvedObject(parent, primaryParticipant, secondaryParticipant );

        // test
        assertNotNull( type.getParent() );
        assertEquals( parent, type.getParent() );
    }
    
    /**
     * Test that resolved type can be created from object.
     */
    @Test
    public void testCanCreateResolvedObject()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        assertNotNull( type );
    }

    /**
     * Test that resolved type can be created from object.
     */
    @Test
    public void testCanCreateResolvedObject2()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( "ugi-bugi", primaryParticipant, secondaryParticipant );

        assertNotNull( type );
    }
        
    /**
     * Test that the participant names are 'root'.
     */
    @Test
    public void testParticipantNamesAreRoot()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        // test
        assertEquals( "root", type.getPrimaryParticipant().getName() );
        assertEquals( "root", type.getSecondaryParticiant().getName() );
    }

    /**
     * Test that the participant names are set correct.
     */
    @Test
    public void testParticipantNamesAreSetCorrectForResolvedObject()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        String rootName = "myroot";
        type = ResolvedTypeImpl.createResolvedObject( rootName, primaryParticipant, secondaryParticipant );

        // test
        assertEquals( rootName, type.getPrimaryParticipant().getName() );
        assertEquals( rootName, type.getSecondaryParticiant().getName() );
    }

    /**
     * Fails to create resolved type if primary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedObjectIfPrimaryParticipantIsNull()
    {
        Object primaryParticipant = null;
        Object secondaryParticipant = "a-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );
    }

    /**
     * Fails to create resolved type if secondary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedObjectIfSecondaryParticipantIsNull()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = null;
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );
    }

    /**
     * Fails to create resolved type if primary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedTypeIfPrimaryParticipantIsNull()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = null;        
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( secondaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );    	
    }

    /**
     * Fails to create resolved type if primary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedTypeIfSecondaryParticipantIsNull()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );        
        ResolvedParticipant secondaryParticipant = null;
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( primaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );    	
    }    

    /**
     * Fails to create resolved type if primary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedTypeIfPrimaryParticipantIsNull2()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = null;        
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( secondaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedObject(parent, primaryParticipant, secondaryParticipant );    	
    }

    /**
     * Fails to create resolved type if primary participant is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsToCreateResolvedTypeIfSecondaryParticipantIsNull2()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );        
        ResolvedParticipant secondaryParticipant = null;
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        EasyMock.replay( primaryParticipant);
        EasyMock.replay( parent);        
        
        type = ResolvedTypeImpl.createResolvedObject( parent, primaryParticipant, secondaryParticipant );    	
    }    
    
    
    /**
     * Test that isRoot is true when pair is created from objects.
     */
    @Test
    public void testIsRootIsTrue()
    {
        Object primaryParticipant = "a-string-object";
        Object secondaryParticipant = "another-string-object";
        type = ResolvedTypeImpl.createResolvedObject( primaryParticipant, secondaryParticipant );

        // test
        assertTrue(type.isRoot());
    }

    
    /**
     * Test that isRoot is false when pair is created from resolved attributes.
     */
    @Test
    public void testIsRootIsFalseWhenCreatedWithResolvedAttributes()
    {
        // create mock objects
        ResolvedParticipant primaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedParticipant secondaryParticipant = EasyMock.createMock( ResolvedParticipant.class );
        ResolvedType parent = EasyMock.createMock( ResolvedType.class );
        
        type = ResolvedTypeImpl.createResolvedType( parent, primaryParticipant, secondaryParticipant );

        // test
        assertFalse(type.isRoot());
    }
    
}
