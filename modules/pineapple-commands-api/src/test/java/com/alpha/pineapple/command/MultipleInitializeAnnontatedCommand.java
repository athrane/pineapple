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


package com.alpha.pineapple.command;


import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.Initialize;

/**
 * Annotated command used for unit testing.
 * This command contains a two annotated fields.
 */
public class MultipleInitializeAnnontatedCommand implements Command
{
    /**
     * Context key
     */
    public static final String CONTEXT_KEY1 = "key1";

    /**
     * Context key
     */
    public static final String CONTEXT_KEY2 = "key2";
    
    /**
     * Annotated field #1
     */   
    @Initialize( MultipleInitializeAnnontatedCommand.CONTEXT_KEY1 )
    String annotatedField1;

    /**
     * Annotated field #2
     */   
    @Initialize( MultipleInitializeAnnontatedCommand.CONTEXT_KEY2 )
    int annotatedField2;
    
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    public boolean execute( Context context ) throws Exception
    {
        if( logger.isDebugEnabled()) {
            
            StringBuilder message = new StringBuilder();
            message.append( "Executing command <" );
            message.append( this.getClass().getName());
            message.append( ">." );   
            logger.debug( message.toString() );            
        }
        
        return Command.CONTINUE_PROCESSING;
    }

}
