/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Utility class for packing and unpacking files.
 *
 */
public class ZipUtils {

	/**
	 * Null implementation of the {@linkplain ZipProgressCallback} interface.
	 */
	static class NullProgressCallbackImpl implements ZipProgressCallback {

		@Override
		public void entryProcessed(ZipArchiveEntry entry) {
		}
	}

	/**
	 * Null progress call back implementation.
	 */
	private static final ZipProgressCallback NULL_PROGRESS_CALLBACK = new NullProgressCallbackImpl();

	/**
	 * Compress the content of a folder into a ZIP archive.
	 * 
	 * @param targetDirectory directory whose content is packed.
	 * @param archiveFile     File name of ZIP archive.
	 * @param progress        ZIP progress callback object which can be used by
	 *                        client to continuously notified about the compression
	 *                        operation.
	 * 
	 * @throws Exception if archiving fails.
	 */
	public static void zipFolder(File targetDirectory, File archiveFile, ZipProgressCallback progress)
			throws Exception {

		// creates streams
		final OutputStream fos = new FileOutputStream(archiveFile);
		ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, fos);

		// exit if target directory isn't a folder
		if (!targetDirectory.isDirectory()) {
			// TODO: throw exception
			return;
		}

		// add directory to archive
		internalAddEntryToArchive(aos, targetDirectory, "", progress);

		// close
		aos.finish();
		aos.close();
		fos.close();
	}

	/**
	 * Add entry to archive.
	 * 
	 * @param os          archive output stream.
	 * @param source      file object for entry to add.
	 * @param archivePath internal path in archive.
	 * @param progress    ZIP progress callback object which can be used by client
	 *                    to continuously notified about the compression operation.
	 * 
	 * @throws Exception if archiving fails.
	 */
	static void internalAddEntryToArchive(ArchiveOutputStream os, File source, String archivePath,
			ZipProgressCallback progress) throws Exception {

		// declare
		FileInputStream is = null;

		// calculate entry name
		String entryName = archivePath + source.getName();

		// handle file
		if (source.isFile()) {
			try {

				// create and add archive entry
				ZipArchiveEntry zipEntry = new ZipArchiveEntry(source, entryName);
				os.putArchiveEntry(zipEntry);

				// copy entry content into archive
				is = new FileInputStream(source);
				IOUtils.copy(is, os);

				// notify call back
				progress.entryProcessed(zipEntry);

				os.closeArchiveEntry();

			} finally {
				is.close();
			}

			return;
		}

		// handle directory

		// create and add archive entry
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(source, entryName);
		os.putArchiveEntry(zipEntry);

		// notify call back
		progress.entryProcessed(zipEntry);

		os.closeArchiveEntry();

		// add children recursively
		File[] children = source.listFiles();
		if (children != null) {
			for (File child : children)
				internalAddEntryToArchive(os, child, entryName + "/", progress);
		}
	}

	/**
	 * Compress the content of a folder into a ZIP archive.
	 * 
	 * @param targetDirectory directory whose content is packed.
	 * @param archiveFile     File name of ZIP archive.
	 * @throws Exception if archiving fails.
	 */
	public static void zipFolder(File targetDirectory, File archiveFile) throws Exception {
		zipFolder(targetDirectory, archiveFile, NULL_PROGRESS_CALLBACK);
	}

}
