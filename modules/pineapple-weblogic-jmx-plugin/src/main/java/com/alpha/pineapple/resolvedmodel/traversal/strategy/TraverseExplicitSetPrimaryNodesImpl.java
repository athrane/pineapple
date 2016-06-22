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


package com.alpha.pineapple.resolvedmodel.traversal.strategy;

import org.apache.log4j.Logger;

import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;

/**
 * Traversal strategy which only traverses nodes which are explicit set
 * in the primary model.
 */

public class TraverseExplicitSetPrimaryNodesImpl implements TraversalStrategy
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    public boolean continueTraversal( ResolvedType resolvedtype )
    {        
        // skip traversal if primary isn't explicit set  
        if(!resolvedtype.getPrimaryParticipant().getValueState().equals( ValueState.SET )) {
            
            // log debug message
            if ( logger.isDebugEnabled() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "SKIPPING further traversal as primary attribute isn't explicit set on resolved type <" );
                message.append( resolvedtype );
                message.append( ">." );
                logger.debug( message.toString() );
            }
                        
            return false;
        }        

        return true;
    }

}
