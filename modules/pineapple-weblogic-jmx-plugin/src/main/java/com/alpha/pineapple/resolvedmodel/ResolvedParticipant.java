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

/**
 * Interface for capturing result of attribute resolution.
 */
public interface ResolvedParticipant
{
    /**
     * The value state of an resolved value. 
     * Defines how a resolved value was obtained. 
     */
    enum ValueState {
        SET,
        DEFAULT,
        NIL,
        FAILED 
    }
    
    /**
     * Return the name of the resolved attribute.
     * 
     * @return Name of the resolved attribute.
     */
    public String getName();

    /**
     * Return the type of the returned attribute.
     * 
     * @return Type of the returned attribute.
     */
    public Object getType();
    
    /**
     * Return the value of the resolved attribute.
     * 
     * @return Value of the resolved attribute.
     */
    public Object getValue();
    
    /**
     * Returns the value state of an resolved value. If the resolution failed, 
     * then state <code>ValueState.FAILED</code>.
     * 
     * @return the state of the resolved attribute value.  
     */
    public ValueState getValueState();
    
    /**
     * Return true the name of the resolved attribute.
     * 
     * @return Name of the resolved attribute.
     */
    public boolean isResolutionSuccesful();
    
    /**
     * Return contained resolution exception if resolution failed.
     * 
     * @return contained resolution exception if resolution failed.
     */
    public Exception getResolutionError(); 
    
    /**
     * Returns the value as a single line string.
     * <p/> 
     * If the value is null then the string 'null' is returned.
     * If the value <code>toString</code> representation
     * doesn't span more than one line then the toString
     * representation is returned. Else the toStrong
     * representation is abbreviated at the first new line.   
     *  
     * @return the value as a single line string.
     */
    String getValueAsSingleLineString();

    /**
     * Returns the resolution error as a single line string.
     *  
     * @return the resolution error as a single line string.
     */
    String getResolutionErrorAsSingleLineString();
    
}
