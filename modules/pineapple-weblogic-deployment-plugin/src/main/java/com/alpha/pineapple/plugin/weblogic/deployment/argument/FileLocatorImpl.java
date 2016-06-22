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


package com.alpha.pineapple.plugin.weblogic.deployment.argument;

import java.io.File;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Implementation of the {@link FileLocator} interface.
 */
public class FileLocatorImpl implements FileLocator
{

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    public String[] locateFileSet( String suffix, File searchDirectory )
    {
        // define search filter
        IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(suffix);
        IOFileFilter notDirectory = FileFilterUtils.notFileFilter( DirectoryFileFilter.INSTANCE );
        IOFileFilter andFilter = FileFilterUtils.andFileFilter( suffixFilter, notDirectory );

        // get ear files
        String[] files = searchDirectory.list( andFilter );

        // null is returned then create empty file array.
        if(files == null) {
            files = new String[0]; 
        }
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Found <" );
            message.append( files.length );
            message.append( "> files with suffix <" );
            message.append( suffix );
            message.append( "> in the directory <" );
            message.append( searchDirectory );            
            message.append( "> with the names <" );            
            message.append( ReflectionToStringBuilder.toString( files ));
            message.append( ">." );
            
            // log debug message
            logger.debug( message.toString() );
        }
        
        return files;
    }

    public String[] locateSubDirectories( File searchDirectory )
    {
        // search for directories
        String[] files = searchDirectory.list( DirectoryFileFilter.INSTANCE );

        // null is returned then create empty file array.
        if(files == null) {
            files = new String[0]; 
        }
        
        // log debug message
        if ( logger.isDebugEnabled() )
        {
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Found <" );
            message.append( files.length );
            message.append( "> sub directories  in the directory <" );
            message.append( searchDirectory  );            
            message.append( "> with the names <" );            
            message.append( ReflectionToStringBuilder.toString( files ));
            message.append( ">." );
            
            // log debug message
            logger.debug( message.toString() );
        }
        
        return files;               
    }

	public boolean fileExists(File searchDirectory, String fileName) {
		
		// validate parameters
		Validate.notNull(searchDirectory, "searchDirectory is undefined.");
		Validate.notNull(fileName, "fileName is undefined.");
		
        // define search filter
        IOFileFilter nameFilter = FileFilterUtils.nameFileFilter(fileName);
        IOFileFilter notDirectory = FileFilterUtils.notFileFilter( DirectoryFileFilter.INSTANCE );
        IOFileFilter andFilter = FileFilterUtils.andFileFilter(nameFilter, notDirectory);

        // get file
        String[] files = searchDirectory.list( andFilter );

        // log debug message
        if ( logger.isDebugEnabled() )
        {
            // create debug message
            StringBuilder message = new StringBuilder();
            message.append( "Found <" );
            message.append( files.length );
            message.append( "> files with name <" );
            message.append( fileName );
            message.append( "> in the directory. <" );
            message.append( searchDirectory );            
            message.append( ">." );
            
            // log debug message
            logger.debug( message.toString() );
        }
       
        // select whether a file was found
		return (files.length == 1 );
	}

    
}
