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


package com.alpha.pineapple.plugin.filesystem.command;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.filesystem.session.FileSystemSession;
import com.alpha.pineapple.plugin.filesystem.test.matchers.IsVfsFileCreationDeletionPossible;
import com.alpha.pineapple.plugin.filesystem.test.matchers.IsVfsFileExists;
import com.alpha.pineapple.plugin.filesystem.test.matchers.IsVfsFileReadable;
import com.alpha.pineapple.plugin.filesystem.test.matchers.IsVfsFileWriteable;
import com.alpha.pineapple.test.Asserter;

/**
 * <p>Implementation of the <code>org.apache.commons.chain.Command</code> interface which 
 * asserts the properties of a VFS file object.</p>
 * 
 * <p>Precondition for execution of the command is definition of these keys in 
 * the context:
 * 
 * <ul> 
 * <li><code>session</code> defines file system session object used to access the
 * virtual file system. The type is 
 * <code>com.alpha.pineapple.plugin.filesystem.session.FileSystemSession</code>.</li>
 * 
 * <li><code>path</code> defines the path to the file object in the virtual file system. 
 * The type is <code>java.lang.String</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which collects
 * information about the execution of the test. The type is 
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>  
 * </ul>
 * </p>      
 * 
 * <p>Postcondition after execution of the command is: 
 * <ul> 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the 
 * test failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught, 
 * but passed on the the invoker whose responsibility it is to catch it and update 
 * the <code>ExecutionResult</code> with the state <code>ExecutionState.ERROR</code>.
 * </li>
 * </ul>  
 * </p>        
 */
public class TestVfsFilePropertiesCommand implements Command
{
    /**
     * Key used to identify property in context: Defines the file system session.
     */
    public static final String SESSION_KEY = "session";

    /**
     * Key used to identify property in context: Defines the file object path.
     */
    public static final String PATH_KEY = "path";
    
    
    /**
     * Key used to identify property in context: Contains execution result object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";
      
    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * File system session.
     */
    @Initialize( SESSION_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    FileSystemSession session;

    /**
     * File path.
     */
    @Initialize( PATH_KEY )
    @ValidateValue( ValidationPolicy.NOT_EMPTY )    
    String path;
 
    /**
     * Defines execution result object.
     */
    @Initialize( EXECUTIONRESULT_KEY )
    @ValidateValue( ValidationPolicy.NOT_NULL )    
    ExecutionResult executionResult;
    
    /**
     * Hamcrest Matcher asserter.
     */
    @Resource    
	Asserter asserter;    
    
    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;
        
    public boolean execute( Context context ) throws Exception
    {
        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tvfpc.start") );        	
        }
        
        // initialize command
        CommandInitializer initializer = new CommandInitializerImpl();
        initializer.initialize( context, this );
        
        // configure asserter with execution result		
		asserter.setExecutionResult(executionResult);					        
        
        // run test
        doTest( context );

        // log debug message
        if ( logger.isDebugEnabled() ) 
        {
        	logger.debug( messageProvider.getMessage("tvfpc.completed") );        	
        }
        
        return Command.CONTINUE_PROCESSING;
    }
      
    /**
     * Do test.
     * 
     * @param context
     *            Command context.
     * 
     * @throws Exception
     *             If test execution fails.
     */
    void doTest( Context context ) 
    {
		// create matcher
    	Matcher isExistMatcher = IsVfsFileExists.isVfsFileExists(session);		
    	Matcher isReadableMatcher = IsVfsFileReadable.isVfsFileReadable(session);
    	Matcher isWriteableMatcher = IsVfsFileWriteable.isVfsFileWriteable(session);
    	Matcher isCreationDeletionMatcher = IsVfsFileCreationDeletionPossible.isVfsFileCreationDeletionPossible(session);
    	
    	// create assertion description and assert
    	Object[] args = { path };
    	String message = messageProvider.getMessage("tvfpc.assert_writeable_info", args );
    	asserter.assertObject(path , isWriteableMatcher, message);
    	
    	// create assertion description and assert
    	message = messageProvider.getMessage("tvfpc.assert_exists_info", args );
    	asserter.assertObject(path , isExistMatcher, message);

    	// create assertion description and assert        	
    	message = messageProvider.getMessage("tvfpc.assert_readable_info", args );
    	asserter.assertObject(path , isReadableMatcher, message);

    	// create assertion description and assert
    	message = messageProvider.getMessage("tvfpc.assert_create_and_delete_info", args );
    	asserter.assertObject(path , isCreationDeletionMatcher, message);
    	    	
        // compute result
    	executionResult.completeAsComputed(messageProvider, "tvfpc.succeed", null, "tvfpc.failed", null );    	    	
    }         
}
