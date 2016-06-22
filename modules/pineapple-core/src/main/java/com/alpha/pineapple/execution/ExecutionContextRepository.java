/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution;

import org.apache.commons.chain.Context;

/**
 * Interface for context repository for storage of execution context's created
 * by the {@linkplain OperationTaskImpl} while the operation is executing.
 */
public interface ExecutionContextRepository {

    /**
     * Register context.
     * 
     * @param info
     *            execution info for context.
     * @param context
     *            context to store.
     */
    public void register(ExecutionInfo info, Context context);

    /**
     * Unregister context.
     * 
     * @param context
     *            context to unregister.
     */
    public void unregister(Context context);

    /**
     * get context.
     * 
     * @param info
     *            execution info to lookup context.
     */
    public Context get(ExecutionInfo info);

    /**
     * get context.
     * 
     * @param result
     *            execution result to lookup context.
     */
    public Context get(ExecutionResult result);

}
