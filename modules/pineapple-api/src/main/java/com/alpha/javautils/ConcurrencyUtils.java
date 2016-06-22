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

package com.alpha.javautils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for Java concurrency.
 **/
public class ConcurrencyUtils {

    /**
     * Wait one second using a {@linkplain CountDownLatch} which times out.
     * 
     * @throws InterruptedException
     *             If operation is intercepted.
     */
    public static void waitOneSec() throws InterruptedException {
	waitSomeSeconds(1);
    }

    /**
     * Wait some seconds using a {@linkplain CountDownLatch} which times out.
     * 
     * @param seconds
     *            seconds to wait.
     * 
     * @throws InterruptedException
     *             If operation is intercepted.
     */
    public static void waitSomeSeconds(int seconds) throws InterruptedException {
	CountDownLatch latch = new CountDownLatch(1);
	latch.await(seconds, TimeUnit.SECONDS);
    }

    /**
     * Wait some milliseconds using a {@linkplain CountDownLatch} which times
     * out.
     * 
     * @param millis
     *            milli seconds to wait.
     * 
     * @throws InterruptedException
     *             If operation is intercepted.
     */
    public static void waitSomeMilliseconds(int millis) throws InterruptedException {
	CountDownLatch latch = new CountDownLatch(1);
	latch.await(millis, TimeUnit.MILLISECONDS);
    }

}
