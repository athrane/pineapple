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

package com.alpha.pineapple.web.spring.rest;

import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_CREATE_MODEL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_DELETE_MODEL_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_DELETE_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_GET_MODULES_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_REFRESH_PATH;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_UPLOAD_PATH;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.module.info.Modules;
import com.alpha.pineapple.module.ModelNotFoundException;
import com.alpha.pineapple.module.ModuleDeletionFailedException;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleNotFoundException;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.WebApplicationConstants;
import com.alpha.pineapple.web.model.RestResultMapper;

/**
 * Module REST web service controller.
 */
@Controller
@RequestMapping(WebApplicationConstants.REST_MODULE_URI)
public class ModuleController {

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
     * Runtime directory resolver.
     */
    @Resource
    RuntimeDirectoryProvider runtimeDirectoryResolver;

    /**
     * Pineapple core component.
     */
    @Resource
    PineappleCore coreComponent;

    /**
     * REST result mapper.
     */
    @javax.annotation.Resource
    RestResultMapper restResultMapper;

    /**
     * Refresh modules.
     * 
     * Will refresh the module repository.
     * 
     * Only intended to be used when the modules are updated outside the control
     * of the web application, e.g. manually.
     */
    @RequestMapping(value = REST_MODULE_REFRESH_PATH, method = RequestMethod.POST, produces = {
	    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void refresh() {
	Administration admin = coreComponent.getAdministration();
	ModuleRepository modulesRepository = admin.getModuleRepository();
	modulesRepository.initialize();
    }

    /**
     * Create module model.
     * 
     * @param module
     *            module ID.
     * @param environment
     *            environment ID.
     * 
     * @throws ModuleNotFoundException
     *             if creation fails. The exception is handled by the spring
     *             exception handler.
     * @throws IllegalArgumentException
     *             if parameters are illegal. The exception is handled by the
     *             spring exception handler.
     */
    @RequestMapping(value = REST_MODULE_CREATE_MODEL_PATH, method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void createModel(@PathVariable String module, @PathVariable String environment) {
	Administration admin = coreComponent.getAdministration();
	ModuleRepository moduleRepository = admin.getModuleRepository();
	ModuleInfo moduleInfo = moduleRepository.get(module);
	moduleRepository.createModel(moduleInfo, environment);
    }

    /**
     * Delete module model.
     * 
     * @param module
     *            module ID.
     * @param environment
     *            environment ID.
     * 
     * @throws ModuleNotFoundException
     *             if deletion fails. The exception is handled by the spring
     *             exception handler.
     * @throws ModelNotFoundException
     *             if deletion fails. The exception is handled by the spring
     *             exception handler.
     * @throws IllegalArgumentException
     *             if parameters are illegal. The exception is handled by the
     *             spring exception handler.
     */
    @RequestMapping(value = REST_MODULE_DELETE_MODEL_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteModel(@PathVariable String module, @PathVariable String environment) {
	Administration admin = coreComponent.getAdministration();
	ModuleRepository moduleRepository = admin.getModuleRepository();
	ModuleInfo moduleInfo = moduleRepository.get(module);
	moduleRepository.deleteModel(moduleInfo, environment);
    }

    /**
     * Get modules.
     * 
     * @return collection of modules and HTTP 200 (OK).
     */
    @RequestMapping(value = REST_MODULE_GET_MODULES_PATH, method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Modules getModules() {
	Administration admin = coreComponent.getAdministration();
	ModuleRepository repository = admin.getModuleRepository();
	return restResultMapper.mapModules(repository.getInfos());
    }

    /**
     * Upload module.
     * 
     * @param module
     *            name of module to upload.
     * @return view name.... MAYBE...
     */
    @RequestMapping(value = REST_MODULE_UPLOAD_PATH, method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam(value = "file") MultipartFile file) {
	try {

	    if (file == null) {
		logger.error("File is null - exiting upload");
		return "upload";
	    }

	    String fileName = file.getOriginalFilename();

	    // store uploaded file at temp directory.
	    File tmpFile = new File(runtimeDirectoryResolver.getTempDirectory(), fileName);
	    FileOutputStream fos = new FileOutputStream(tmpFile);
	    InputStream is = file.getInputStream();
	    IOUtils.copy(is, fos);

	    // unpack zipped tmp file to module directory
	    unpackZippedModule(tmpFile);

	} catch (IOException e) {
	    logger.error("ERROR:" + StackTraceHelper.getStrackTrace(e));
	}

	return "upload";
    }

    /**
     * Delete module.
     * 
     * @param module
     *            name of module to delete.
     * 
     * @throws ModuleNotFoundException
     *             if operation ID isn't known. The exception is handled by the
     *             spring exception handler.
     * @throws ModuleDeletionFailueException
     *             if deletion failed. The exception is handled by the spring
     *             exception handler.
     */
    @RequestMapping(value = REST_MODULE_DELETE_PATH, method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String module) {
	Administration admin = coreComponent.getAdministration();
	ModuleRepository moduleRepository = admin.getModuleRepository();
	moduleRepository.delete(module);
    }

    public void unpackZippedModule(File fileName) {

	// declare streams
	FileInputStream is = null;
	ZipInputStream zis = null;
	BufferedInputStream bis = null;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;

	try {
	    // set counters
	    int entryNumber = 0;
	    int numberEntries = countArchiveEntries(fileName);

	    // get media as zip stream
	    is = new FileInputStream(fileName);
	    zis = new ZipInputStream(is);
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
		// UnpackedEntryEvent event = new
		// UnpackedEntryEvent(zipPercentage, zipEntry, entryNumber,
		// numberEntries );
		// asyncTaskHelper.scheduleEvent(event, desktop, eventListener);
		// logger.debug("DEBUG Unpacked entry:" + file);
		// logger.debug("DEBUG zipPercentage:" + zipPercentage);
		// logger.debug("DEBUG zipEntry:" + zipEntry);
		// logger.debug("DEBUG entryNumber:" + entryNumber);
		// logger.debug("DEBUG numberEntries:" + numberEntries);

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
		    // FileUnpackUpdateEvent event2 = new
		    // FileUnpackUpdateEvent(filePercentage, unpackedSizeKb,
		    // totalSizeKb);
		    // asyncTaskHelper.scheduleEvent(event2, desktop,
		    // eventListener);
		    // logger.debug("DEBUG filePercentage:" + filePercentage);
		    // logger.debug("DEBUG unpackedSizeKb:" + unpackedSizeKb);
		    // logger.debug("DEBUG totalSizeKb:" + totalSizeKb);
		}

		// close streams
		bos.flush();
		bos.close();
		fos.close();
	    }

	    // close streams
	    bis.close();
	    zis.close();
	    is.close();

	    // get modules repository
	    Administration admin = coreComponent.getAdministration();
	    ModuleRepository modulesRepository = admin.getModuleRepository();

	    // refresh module repository
	    modulesRepository.initialize();

	} catch (Exception e) {

	    // log error message
	    Object[] args = { StackTraceHelper.getStrackTrace(e) };
	    String message = messageProvider.getMessage("mc.install_failed", args);
	    logger.error(message);

	} finally {
	    IOUtils.closeQuietly(bos);
	    IOUtils.closeQuietly(fos);
	    IOUtils.closeQuietly(bis);
	    IOUtils.closeQuietly(zis);
	    IOUtils.closeQuietly(is);
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
    int countArchiveEntries(File fileName) throws Exception {

	// declare stream
	ZipInputStream zis = null;
	InputStream is = null;

	try {
	    // get zip stream
	    is = new FileInputStream(fileName);
	    zis = new ZipInputStream(is);

	    // iterate to count
	    int counter = 0;
	    ZipEntry zipEntry = null;
	    while ((zipEntry = zis.getNextEntry()) != null) {
		counter++;
	    }

	    return counter;

	} finally {
	    IOUtils.closeQuietly(zis);
	    IOUtils.closeQuietly(is);
	}
    }

    /**
     * Exception handler for handling unknown module.
     * 
     * @param e
     *            module not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return module not found HTTP status code and error message.
     */
    @ExceptionHandler(ModuleNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ModuleNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling unknown model.
     * 
     * @param e
     *            model not found exception.
     * @param response
     *            HTTP response.
     * 
     * @return model not found HTTP status code and error message.
     */
    @ExceptionHandler(ModelNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(ModelNotFoundException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling failed module deletion.
     * 
     * @param e
     *            module deletion failure exception.
     * @param response
     *            HTTP response.
     * 
     * @return module deletion failure HTTP status code and error message.
     */
    @ExceptionHandler(ModuleDeletionFailedException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(ModuleDeletionFailedException e, HttpServletResponse response) {
	return e.getMessage();
    }

    /**
     * Exception handler for handling illegal argument exception.
     * 
     * @param e
     *            illegal argument exception.
     * @param response
     *            HTTP response.
     * 
     * @return illegal argument HTTP status code and error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleException(IllegalArgumentException e, HttpServletResponse response) {
	return e.getMessage();
    }

}
