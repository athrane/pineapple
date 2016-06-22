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


package com.alpha.pineapple.command.test;

import org.apache.commons.chain.Command;

/**
 * Marker interface for Chain commands which implements a test. 
 * 
 * The interface defines keys for test commands. Test commands should use 
 * these keys to report test results.
 */
@Deprecated
public interface TestCommand extends Command
{
    /**
     * Key used to identify property in context: Describes the test in a human readable form.
     */
    public static final String DESCRIPTION_KEY = "description";
    
    /**
     * Key used to identify property in context: Describes the result of the test in a human readable form.
     */
    public static final String MESSAGE_KEY = "message";

    /**
     * Key used to identify property in context: Defines the result of the test as a boolean value.
     */
    public static final String RESULT_KEY = "result";

}
