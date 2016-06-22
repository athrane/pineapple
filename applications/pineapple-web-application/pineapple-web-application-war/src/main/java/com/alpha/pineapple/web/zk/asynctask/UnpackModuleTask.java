/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */

package com.alpha.pineapple.web.zk.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.zk.asynctask.event.FileUnpackUpdateEvent;
import com.alpha.pineapple.web.zk.asynctask.event.UnpackedEntryEvent;

/**
 * Implementation of the {@linkplain AsyncTask} interface which implement upload
 * of a zipped module and unpacking it into the module repository.
 */
public class UnpackModuleTask implements AsyncTask {

    /**
     * 100% percentage.
     */
    static final int HUNDRED_PERCENTAGE = 100;

    /**
     * Copy buffer size.
     */
    static final int ONE_KB_BUFFER = 1024;

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource(name = "webMessageProvider")
    MessageProvider messageProvider;

    /**
     * Asynchronous task execution helper.
     */
    @Resource
    AsyncTaskHelper asyncTaskHelper;

    /**
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryResolver;

    /**
     * Module repository.
     */
    @Resource
    ModuleRepository moduleRepository;

    /**
     * The ZK desktop that will receive the resulting event
     */
    public Desktop desktop = null;

    /**
     * The ZK event listener that will receive the resulting event
     */
    public EventListener<Event> eventListener = null;

    /**
     * Uploaded media.
     */
    Media uploadedMedia;

    /**
     * Set media.
     * 
     * @param media
     *            Set media.
     */
    public void setMedia(Media media) {
	this.uploadedMedia = media;
    }

    @Override
    public void runAsync(Desktop desktop, EventListener<Event> eventListener) {
	this.desktop = desktop;
	this.eventListener = eventListener;

	// declare streams
	ZipInputStream zis = null;
	BufferedInputStream bis = null;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;

	try {
	    // set counters
	    int entryNumber = 0;
	    int numberEntries = countArchiveEntries();

	    // get media as zip stream
	    zis = new ZipInputStream(uploadedMedia.getStreamData());
	    bis = new BufferedInputStream(zis);

	    // iterate over archive content
	    ZipEntry zipEntry;
	    while ((zipEntry = zis.getNextEntry()) != null) {

		// define next file to unpack
		File file = new File(runtimeDirectoryResolver.getModulesDirectory(), zipEntry.getName());

		// compute progress
		entryNumber++;
		int zipPercentage = (HUNDRED_PERCENTAGE * entryNumber) / numberEntries;

		// schedule event
		UnpackedEntryEvent event = new UnpackedEntryEvent(zipPercentage, zipEntry, entryNumber, numberEntries);
		asyncTaskHelper.scheduleEvent(event, desktop, eventListener);

		// if entry is directory then create it
		if (zipEntry.isDirectory()) {
		    file.mkdirs();
		    continue;
		}

		// unpack entry as file
		createParentDirectory(file);

		// set counters
		int unpackedSizeKb = 0;
		long totalSizeKb = computeTotalFileSizeInKb(zipEntry);
		int lastFilePercentage = 0;
		long lastTimeStamp = System.currentTimeMillis();
		long timeStamp = lastTimeStamp;

		// open streams
		fos = new FileOutputStream(file);
		bos = new BufferedOutputStream(fos);
		byte buffer[] = new byte[ONE_KB_BUFFER];

		// unzip file
		for (int b; (b = bis.read(buffer, 0, ONE_KB_BUFFER)) != -1;) {
		    bos.write(buffer, 0, b);

		    // compute progress
		    unpackedSizeKb++;
		    int filePercentage = computeFileProgress(totalSizeKb, unpackedSizeKb);

		    // we don't skip at hundred percentage
		    if (filePercentage < HUNDRED_PERCENTAGE) {

			// skip scheduling event if we haven't changed
			// percentage
			if (filePercentage == lastFilePercentage)
			    continue;

			// skip scheduling event if last event is less than a
			// second old
			timeStamp = System.currentTimeMillis();
			if ((timeStamp - lastTimeStamp) < 1000)
			    continue;
		    }

		    // update
		    lastTimeStamp = timeStamp;
		    lastFilePercentage = filePercentage;

		    // schedule event
		    FileUnpackUpdateEvent event2 = new FileUnpackUpdateEvent(filePercentage, unpackedSizeKb,
			    totalSizeKb);
		    asyncTaskHelper.scheduleEvent(event2, desktop, eventListener);
		}

		// close streams
		bos.flush();
		bos.close();
		fos.close();
	    }

	    // close streams
	    bis.close();
	    zis.close();

	    // refresh module repository
	    moduleRepository.initialize();

	} catch (Exception e) {

	    // log error message
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String message = messageProvider.getMessage("umt.install_failed", args);
	    logger.error(message);

	} finally {
	    IOUtils.closeQuietly(bos);
	    IOUtils.closeQuietly(fos);
	    IOUtils.closeQuietly(bis);
	    IOUtils.closeQuietly(zis);
	}

    }

    /**
     * Compute total file size in KB.
     * 
     * If file size is less than 1 KB then 1 KB is returned.
     * 
     * @param zipEntry
     *            Zip entry to calculate size for.
     * 
     * @return total file size in KB.
     */
    long computeTotalFileSizeInKb(ZipEntry zipEntry) {
	if (zipEntry.getSize() < ONE_KB_BUFFER)
	    return 1;
	double sizeKb = (double) zipEntry.getSize() / (double) ONE_KB_BUFFER;
	sizeKb = Math.ceil(sizeKb);
	return (long) sizeKb;
    }

    /**
     * Compute file unpack progress in percentage.
     * 
     * @param totalSize
     *            Total file size in KB.
     * @param counter
     *            Number of KB unpacked.
     * 
     * @return file unpack progress in percentage.
     */
    int computeFileProgress(long totalSize, int counter) {
	if (totalSize == 0)
	    return 0;
	int result = (int) ((100 * counter) / totalSize);
	return result;
    }

    /**
     * Create parent directory for zipped file.
     * 
     * @param file
     *            Zipped file.
     */
    File createParentDirectory(File file) {
	File dir = new File(file.getParent());
	dir.mkdirs();
	return dir;
    }

    /**
     * Count the number of archive entries.
     * 
     * @return number of archive entries.
     * 
     * @throws Exception
     *             If count operation fails.
     */
    int countArchiveEntries() throws Exception {

	// declare stream
	ZipInputStream zis = null;

	try {
	    // get media as zip stream
	    zis = new ZipInputStream(uploadedMedia.getStreamData());

	    // iterate to count
	    int counter = 0;
	    ZipEntry zipEntry = null;
	    while ((zipEntry = zis.getNextEntry()) != null) {
		counter++;
	    }

	    return counter;

	} finally {
	    IOUtils.closeQuietly(zis);
	}
    }

}
