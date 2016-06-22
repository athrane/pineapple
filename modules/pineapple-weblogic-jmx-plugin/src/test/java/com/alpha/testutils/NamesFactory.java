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

/**
 * Helper class for unit tests, which can be used to create names which used in multiple test cases.
 */
public class NamesFactory
{

    /**
     * Create name for virtual host for specific test methods.
     * 
     * @param invoker
     *            The invoking Object.
     * 
     * @param methodName
     *            Name of test method.
     * 
     * @return Name for virtual host for a specific test method.
     */
    public static String createVirtualHostName( Object invoker, String methodName )
    {
        StringBuilder name = new StringBuilder();
        name.append( createFQTestName( invoker, methodName ) );
        name.append( "-internal-vh" );
        return name.toString();
    }

    /**
     * Create fully qualified test method name, which is prefixed with the class name.
     * 
     * @param invoker
     *            The invoking Object.
     * 
     * @param methodName
     *            Name of test method.
     * 
     * @return fully qualified test method name, which is prefixed with the class name.
     */
    public static String createFQTestName( Object invoker, String methodName )
    {
        StringBuilder name = new StringBuilder();
        name.append( invoker.getClass().getSimpleName() );
        name.append( "-" );
        name.append( methodName );
        return name.toString();
    }

}
