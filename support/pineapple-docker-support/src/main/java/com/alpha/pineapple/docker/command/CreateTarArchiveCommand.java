/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple.docker.command;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.BIGNUMBER_POSIX;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_POSIX;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.nio.TarArchiverVisitorImpl;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which creates a TAR archive from a source directory.
 *
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>source-directory</code> defines path to the source directory which
 * compressed into a TAR archive. The type is <code>java.io.File</code>.</li>
 * 
 * <li><code>tar-archive</code> defines path to the the created TAR archive. The
 * type is <code>java.io.File</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class CreateTarArchiveCommand implements Command {

    /**
     * Key used to identify property in context: Source directory.
     */
    public static final String SOURCE_DIRECTORY_KEY = "source-directory";

    /**
     * Key used to identify property in context: TAR archive.
     */
    public static final String TAR_ARCHIVE_KEY = "tar-archive";

    /**
     * Key used to identify property in context: Contains execution result
     * object,.
     */
    public static final String EXECUTIONRESULT_KEY = "execution-result";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Source directory.
     */
    @Initialize(SOURCE_DIRECTORY_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    File sourceDirectory;

    /**
     * Target directory.
     */
    @Initialize(TAR_ARCHIVE_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    File tarArchive;

    /**
     * Defines execution result object.
     */
    @Initialize(EXECUTIONRESULT_KEY)
    @ValidateValue(ValidationPolicy.NOT_NULL)
    ExecutionResult executionResult;

    /**
     * Message provider for I18N support.
     */
    @Resource(name = "dockerMessageProvider")
    MessageProvider messageProvider;

    public boolean execute(Context context) throws Exception {
	// initialize command
	CommandInitializer initializer = new CommandInitializerImpl();
	initializer.initialize(context, this);

	createTarArchive(context);

	return Command.CONTINUE_PROCESSING;
    }

    @SuppressWarnings("unchecked")
    void createTarArchive(Context context) {

	// validate source directory exists
	if (!sourceDirectory.exists()) {
	    Object[] args = { sourceDirectory };
	    executionResult.completeAsFailure(messageProvider, "ctac.validate_sourcedirectory_exist_failed", args);
	    return;
	}

	// validate source directory is an directory
	if (!sourceDirectory.isDirectory()) {
	    Object[] args = { sourceDirectory };
	    executionResult.completeAsFailure(messageProvider, "ctac.validate_sourcedirectory_isdir_failed", args);
	    return;
	}

	// TODO: validate target directory exists otherwise create it

	// delete file if it exists
	if (tarArchive.exists()) {
	    boolean succeded = tarArchive.delete();

	    // handle failure to delete file
	    if (!succeded) {
		Object[] args = { tarArchive };
		executionResult.completeAsFailure(messageProvider, "ctac.delete_oldtararchive_failed", args);
		return;
	    }
	}

	// TODO: validate source directory contains DockerFile.

	FileOutputStream fileOutStream = null;
	TarArchiveOutputStream tarOutStream = null;

	try {

	    // create TAR stream
	    fileOutStream = new FileOutputStream(tarArchive);
	    // GzipCompressorOutputStream gzipOutStream = new
	    // GzipCompressorOutputStream(fileOutStream);
	    tarOutStream = new TarArchiveOutputStream(fileOutStream);
	    // TarArchiveOutputStream tarOutStream = new
	    // TarArchiveOutputStream(gzipOutStream);
	    tarOutStream.setLongFileMode(LONGFILE_POSIX);
	    tarOutStream.setBigNumberMode(BIGNUMBER_POSIX);

	    // create TAR visitor
	    FileVisitor<Path> tarVisitor = new TarArchiverVisitorImpl(sourceDirectory, tarOutStream);

	    // traverse source directory
	    Files.walkFileTree(sourceDirectory.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
		    tarVisitor);

	    // store tar archive file
	    context.put(TAR_ARCHIVE_KEY, tarArchive);

	    // complete as success
	    Object[] args = { tarArchive, sourceDirectory };
	    executionResult.completeAsSuccessful(messageProvider, "ctac.succeed", args);
	    return;

	} catch (Exception e) {

	    // complete with error
	    executionResult.completeAsError(messageProvider, "ctac.create_archive_error", e);

	} finally {

	    // close streams
	    IOUtils.closeQuietly(tarOutStream);
	    IOUtils.closeQuietly(fileOutStream);
	}
    }

}
