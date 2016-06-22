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

import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class MockTargetModuleID implements TargetModuleID
{
    /**
     * Module id
     */
    public String id;

    /**
     * target id.
     */
    public Target target;
    
    /**
     * MockTargetModuleID constructor.
     * 
     * @param id Module id.
     */
    public MockTargetModuleID(String id, Target target ) {
        this.id = id;
        this.target = target;
    }
    
    public TargetModuleID[] getChildTargetModuleID()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getModuleID()
    {
        return this.id;
    }

    public TargetModuleID getParentTargetModuleID()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Target getTarget()
    {
        return this.target;
    }

    public String getWebURL()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString( this );
    }

    
}
