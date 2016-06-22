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


package com.alpha.pineapple.plugin.weblogic.jmx.model.xmlbeans;

import javax.xml.namespace.QName;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaProperty;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.resolvedmodel.ResolvedCollection;
import com.alpha.pineapple.resolvedmodel.ResolvedEnum;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedPrimitive;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.UnresolvedType;
import com.alpha.pineapple.resolvedmodel.traversal.ResolvedModelVisitor;
import com.alpha.pineapple.session.Session;

/**
 * Implementation of the <code>ResolvedModelVisitor </code>
 * which can produce a string based description of the visited node.  
 */
public class XmlBeansDescriptionGeneratorImpl implements ResolvedModelVisitor {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	    
	public void setSession(Session session) {
		// ignores session for now.	
	}
    
	public Object visit(ResolvedCollection resolvedCollection, ExecutionResult result) {

		// get primary participant
		ResolvedParticipant primary = resolvedCollection.getPrimaryParticipant();

		// create description
        StringBuilder description = new StringBuilder();
		
		// complete description
		description.append( primary.getName() );
		description.append( ":" );
		description.append( describeTypeFromSchemaProperty(primary));
        description.append( "[]" );

        /**
        description.append( " [" );
        description.append( primary.getValueState() );
        description.append( "/" );        

        // get primary participant
        ResolvedParticipant secondary = resolvedCollection.getSecondaryParticiant();
        
        description.append( secondary.getValueState() );
        description.append( "]" );
        **/        
        
		return description.toString();		
	}

	public Object visit(ResolvedEnum resolvedEnum, ExecutionResult result) {

		// get primary participant
		ResolvedParticipant primary = resolvedEnum.getPrimaryParticipant();

		// create description
        StringBuilder description = new StringBuilder();
		description.append( primary.getName() );
        description.append( "=" );            
        description.append( primary.getValue() );
        
        /**
        description.append( " [" );
        description.append( primary.getValueState() );
        description.append( "/" );        

        // get primary participant
        ResolvedParticipant secondary = resolvedEnum.getSecondaryParticiant();
        
        description.append( secondary.getValueState() );
        description.append( "]" );
        **/        
		
		return description.toString();
	}

	public Object visit(ResolvedObject resolvedObject, ExecutionResult result) {
		return createDescription(resolvedObject);
	}

	public Object visit(ResolvedPrimitive resolvedPrimitive, ExecutionResult result ) {
		
		// get primary participant
		ResolvedParticipant primary = resolvedPrimitive.getPrimaryParticipant();

		// create description
        StringBuilder description = new StringBuilder();
		description.append( primary.getName() );
        description.append( "=" );            
        description.append( primary.getValue() );

        /**
        description.append( " [" );
        description.append( primary.getValueState() );
        description.append( "/" );        

        // get primary participant
        ResolvedParticipant secondary = resolvedPrimitive.getSecondaryParticiant();
        
        description.append( secondary.getValueState() );
        description.append( "]" );
        **/        
        
		return description.toString();
	}

	public Object visit(ResolvedType resolvedType, ExecutionResult result) {
		return createDescription(resolvedType);
	}

	public Object visit(UnresolvedType unresolved, ExecutionResult result) {
		return createDescription(unresolved);	
	}

    /**
     * Create description of the primary participant in the resolved type.
     *  
     * @param resolvedType Resolved type to describe.
     * 
     * @return description of the primary participant in the resolved type.
     */
	String createDescription(ResolvedType resolvedType) {
		                                        
        // get primary participant
        ResolvedParticipant primary = resolvedType.getPrimaryParticipant();

		// create description
        StringBuilder description = new StringBuilder();
        description.append( primary.getName() );
        description.append( ":" );
        description.append( describeTypeFromSchemaProperty(primary));

        /**
        description.append( " [" );
        description.append( primary.getValueState() );
        description.append( "/" );        

        // get primary participant
        ResolvedParticipant secondary = resolvedType.getSecondaryParticiant();
        
        description.append( secondary.getValueState() );
        description.append( "]" );
        **/        
                
		return description.toString();            
	}

	/**
	 * Create description from schema property if the type of
	 * the resolved attribute is an XMLBeans schema property.
	 * 
	 * @param resolvedAttribute Resolve attribute whose type is described.
	 * 
	 * @return Description of resolved attribute.  
	 */
	String describeTypeFromSchemaProperty( ResolvedParticipant resolvedAttribute ) {
				
		// generate type from schema property
		if (resolvedAttribute.getType() instanceof SchemaProperty ) {
			
			// get schema property
			SchemaProperty schemaProperty = (SchemaProperty) resolvedAttribute.getType();
			
			// get name			
			QName schemaPropertyName = schemaProperty.getName();
			
			// return local part of QName 
			return schemaPropertyName.getLocalPart();
			
		} else {
		
			// get type
			Object type = resolvedAttribute.getType();
			
			// return as string
			return ReflectionToStringBuilder.toString(type);						
		}
	}	
	
}
