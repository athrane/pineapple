/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Subclass of the {@linkplain SimpleFileVisitor} class which creates a TAR
 * archive.
 */
public class TarArchiverVisitorImpl extends SimpleFileVisitor<Path> {

    /**
     * Constant used to query whether OS supports a POSIX compliant view of the
     * file system.
     */
    static final String POSIX_FILE_VIEW = "posix";

    /**
     * Root directory where traversal starts.
     */
    Path rootDirectory;

    /**
     * TAR archive where files are added to.
     */
    TarArchiveOutputStream tarOutStream;

    /**
     * TarArchiverVisitorImpl constructor.
     * 
     * @param rootDirectory
     *            root directory which is compressed.
     * @param tarOutStream
     *            TAR archive as stream.
     */
    public TarArchiverVisitorImpl(File rootDirectory, TarArchiveOutputStream tarOutStream) {
	this.rootDirectory = rootDirectory.toPath();
	this.tarOutStream = tarOutStream;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

	// create TAR entry
	TarArchiveEntry entry = new TarArchiveEntry(file.toFile());
	Path relativePath = rootDirectory.relativize(file);
	entry.setName(relativePath.toString());
	entry.setMode(getFileMode(file));
	entry.setSize(attrs.size());
	tarOutStream.putArchiveEntry(entry);
	Files.copy(file, tarOutStream);

	// close entry
	tarOutStream.closeArchiveEntry();
	return FileVisitResult.CONTINUE;
    }

    /**
     * Return file permissions.
     * 
     * If file system is POSIX compliant then the permission
     * 
     * @param file
     * @return
     * @throws IOException
     */
    int getFileMode(Path file) throws IOException {
	if (isPosixComplantFileSystem())
	    return getPosixFileMode(file);
	return TarArchiveEntry.DEFAULT_FILE_MODE;
    }

    /**
     * Returns true if file system support POSIX compliant view.
     * 
     * @return true if file system support POSIX compliant view.
     */
    boolean isPosixComplantFileSystem() {
	FileSystem fileSystem = FileSystems.getDefault();
	return fileSystem.supportedFileAttributeViews().contains(POSIX_FILE_VIEW);
    }

    /**
     * Calculate POSIX permissions for file.
     * 
     * @param file
     *            to calculate permissions for.
     * 
     * @return permission for file.
     * 
     * @throws IOException
     *             calculation of file permission fails.
     */
    int getPosixFileMode(Path file) throws IOException {
	final PosixFileAttributes attr = Files.readAttributes(file, PosixFileAttributes.class);
	final Set<PosixFilePermission> perm = attr.permissions();

	// retain file permissions. Values are octal.
	int mode = 0100000;
	mode += 0100 * getModeFromPermissions(perm.contains(PosixFilePermission.OWNER_READ),
		perm.contains(PosixFilePermission.OWNER_WRITE), perm.contains(PosixFilePermission.OWNER_EXECUTE));

	mode += 010 * getModeFromPermissions(perm.contains(PosixFilePermission.GROUP_READ),
		perm.contains(PosixFilePermission.GROUP_WRITE), perm.contains(PosixFilePermission.GROUP_EXECUTE));

	mode += getModeFromPermissions(perm.contains(PosixFilePermission.OTHERS_READ),
		perm.contains(PosixFilePermission.OTHERS_WRITE), perm.contains(PosixFilePermission.OTHERS_EXECUTE));

	return mode;
    }

    /**
     * Calculate permission mode from file permissions.
     * 
     * @param read
     *            read flag.
     * @param write
     *            write flag.
     * @param execute
     *            execute flag.
     * 
     * @return permission mode.
     */
    int getModeFromPermissions(boolean read, boolean write, boolean execute) {
	int result = 0;
	if (read)
	    result += 4;
	if (write)
	    result += 2;
	if (execute)
	    result += 1;
	return result;
    }

}
