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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.xmlbeans.XmlObject;

/**
 * Formatter class which formats the string representation of
 * resolved participant as single line strings.
 */
public class ResolvedParticipantStyle extends ToStringStyle {

    /**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 1831991767075860479L;
				
	/**
	 * Standard ToString style object.
	 */
	ToStringStyle standardToStringStyle = new StandardToStringStyle();
	        
    protected void appendDetail( StringBuffer buffer, String fieldName, Object value )
    {
        // value is null then return a null string
        if ( value == null )
        {
            buffer.append( "null" );
            return;
        }

        if ( value instanceof XmlObject )
        {
            // get value as string
            String valueAsString = value.toString();

            // if string contains newline then abbreviate at new line
            if ( valueAsString.contains( "\n" ) )
            {
                int width = valueAsString.indexOf( "\n" );
                buffer.append( StringUtils.abbreviate( valueAsString, width ) );
            }
        } else {
            buffer.append( value );                
        }
    }
	
}
