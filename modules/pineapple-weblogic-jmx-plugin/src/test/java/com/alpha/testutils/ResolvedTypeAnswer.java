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


package com.alpha.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

import com.alpha.pineapple.resolvedmodel.ResolvedType;

/**
 * Helper class is used to inspect values from methods which returns resolved types.
 */
public class ResolvedTypeAnswer implements IAnswer
{
    public String primaryName;
    public Object primaryValue;
    public Object primaryType;
    public String secondaryName;
    public Object secondaryValue;
    public Object secondaryType;

    
    
    public Object answer() throws Throwable {
                    
        Object[] args = EasyMock.getCurrentArguments();
        ResolvedType pair = (ResolvedType) args[0];
        
        assertNotNull( pair );
        assertEquals( primaryName, pair.getPrimaryParticipant().getName() );
        assertEquals( primaryValue, pair.getPrimaryParticipant().getValue() );
        assertEquals( primaryType, pair.getPrimaryParticipant().getType() );
        assertEquals( secondaryName, pair.getSecondaryParticiant().getName() );
        assertEquals( secondaryValue, pair.getSecondaryParticiant().getValue() );
        assertEquals( secondaryType, pair.getSecondaryParticiant().getType() );            
        return null;
    }
}
