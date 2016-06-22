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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * Implementation of the <code>ResolvedAttribute</code> interface.
 */
public class ResolvedParticipantImpl implements ResolvedParticipant
{

    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger( ResolvedParticipantImpl.class.getName() );

    /**
     * toString style.
     */
    static final ToStringStyle style = new ResolvedParticipantStyle();
            
    /**
     * Name of the resolved attribute.
     */
    String name;

    /**
     * Contained resolution exception if resolution failed.
     */
    Exception exception;

    /**
     * Type of the resolved attribute.
     */
    Object type;

    /**
     * Value of the resolved attribute.
     */
    Object value;
    
    /**
     * Value state of the attribute.
     */
    ValueState state;

    /**
     * ResolvedAttributeImpl constructor;
     * 
     * @param name
     *            Name of the resolved attribute.
     * @param type
     *            Type of the resolved attribute.
     * @param value
     *            Value of the resolved attribute.
     * @param exception
     *            Contained resolution exception if resolution failed. Result is interpreted as successful is exception
     *            is null.
     */
    private ResolvedParticipantImpl( String name, Object type, Object value, Exception exception, ValueState state )
    {
        super();
        this.exception = exception;
        this.name = name;
        this.type = type;
        this.value = value;                
        this.state = state;                
    }

    public String getName()
    {
        return this.name;
    }

    public Exception getResolutionError()
    {
        return exception;
    }

    public Object getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    public boolean isResolutionSuccesful()
    {
        return ( exception == null );
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {        
        return ReflectionToStringBuilder.toString( this, style );        
    }

    public String getValueAsSingleLineString()
    {
        // value is null then return a null string
        if ( getValue() == null )
        {
            return "null";
        }
        
        // get value as array 
        if ( getValue().getClass().isArray()) {
        	
        	return ArrayUtils.toString(getValue());
        }
        
        // get value as string
        String valueAsString = getValue().toString();

        // if string contains newline then abbreviate at new line
        if ( valueAsString.contains( "\n" ) )
        {
            int width = valueAsString.indexOf( "\n" );
            return StringUtils.abbreviate( valueAsString, width );
        }

        return valueAsString;        
    }

    public String getResolutionErrorAsSingleLineString()
    {
        // value is null then return a null string
        if ( isResolutionSuccesful() )
        {
            return "";
        }
        else
        {
            // get value as string
            String valueAsString = getResolutionError().toString();

            // if string contains newline then abbreviate at new line
            if ( valueAsString.contains( "\n" ) )
            {
                int width = valueAsString.indexOf( "\n" );
                return StringUtils.abbreviate( valueAsString, width );
            }

            return valueAsString;
        }
    }

    /**
     * Create successful resolution result. 
     * The value state is defined as set, which means that the contained
     * value is explicit set in the model where the attribute was resolved from.
     * 
     * @param name
     *            Name of the resolved attribute.
     * @param type
     *            Type of the resolved attribute.
     * @param value
     *            Value of the resolved attribute.
     * 
     * @return successful resolution result.
     */
    public static ResolvedParticipant createSuccessfulResult( String name, Object type, Object value )
    {
        return new ResolvedParticipantImpl( name, type, value, null, ValueState.SET );
    }

    /**
     * Create successful resolution result with detailed set state.
     * 
     * @param name
     *            Name of the resolved attribute.
     * @param type
     *            Type of the resolved attribute.
     * @param value
     *            Value of the resolved attribute.
     * @param state
     *            Definition of how the resolved value was obtained.            
     * 
     * @return successful resolution result.
     */
    public static ResolvedParticipant createSuccessfulResult( String name, Object type, Object value, ValueState state )
    {
        return new ResolvedParticipantImpl( name, type, value, null, state );
    }
    
    
    /**
     * Create unsuccessful resolution result.
     * 
     * @param name
     *            Name of the resolved attribute.
     * @param type
     *            Type of the resolved attribute.
     * @param value
     *            Value of the resolved attribute.
     * @param exception
     *            exception describing why the resolution failed.
     * 
     * @return unsuccessful resolution result.
     */
    public static ResolvedParticipant createUnsuccessfulResult( String name, Object type, Object value,
                                                              Exception exception )
    {
        return new ResolvedParticipantImpl( name, type, value, exception, ValueState.FAILED );
    }

    /**
     * @see com.alpha.pineapple.resolvedmodel.ResolvedParticipant#getValueState()
     */
    public ValueState getValueState()
    {
        return this.state;
    }

    
}
