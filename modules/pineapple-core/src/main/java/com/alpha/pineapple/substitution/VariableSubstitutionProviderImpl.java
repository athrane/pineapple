/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.substitution;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.execution.ExecutionContextRepository;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.session.NullSessionImpl;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.pineapple.test.AssertionHelper;

/**
 * Implementation of the {@linkplain VariableSubstitutionProvider} interface
 * which implements variable substitution functionality for plugins.
 */
public class VariableSubstitutionProviderImpl implements VariableSubstitutionProvider {

    /**
     * Default file encoding.
     */
    static final String DEFAULT_FILEENCODING_UTF_8 = "UTF-8";

    /**
     * Default maximum legal file for value substitution (in bytes). Current
     * maximum is 1 MB.
     */
    static final long DEFAULT_MAXIMUM_SIZE = 1 * (1024 * 1024);

    /**
     * Null file returned if substitution process fails.
     */
    static final File NULL_FILE = null;

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

    /**
     * Assertion helper.
     */
    @Resource
    AssertionHelper assertionHelper;

    /**
     * Model variable substitutor.
     */
    @Resource
    ModelVariableSubstitutor modelVariableSubstitutor;

    /**
     * Execution context repository.
     */
    @Resource
    ExecutionContextRepository executionContextRepository;

    @Override
    public String substitute(String source, ExecutionResult result) throws VariableSubstitutionException {
	ExecutionResult substitutionResult = createSubstitutionResultHeader(result);

	try {

	    // create null session
	    Session nullSession = new NullSessionImpl();
	    nullSession.connect(new com.alpha.pineapple.model.configuration.Resource(), new Credential());

	    // substitute
	    String processedContent = resolveVariables(source, nullSession, result);

	    // complete as success and return
	    substitutionResult.completeAsSuccessful(messageProvider, "dvpp.varsub_info_complete");
	    return processedContent;

	} catch (SessionConnectException e) {

	    // complete as error and re-throw
	    Object[] args = { e.getCause() };
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_info_error", args, e);
	    String message = messageProvider.getMessage("dvpp.varsub_info_error", args);
	    throw new VariableSubstitutionException(message, e);
	}
    }

    @Override
    public String substitute(String source, Session session, ExecutionResult result)
	    throws VariableSubstitutionException {
	ExecutionResult substitutionResult = createSubstitutionResultHeader(result);

	// substitute
	String processedContent = resolveVariables(source, session, result);

	// complete as success and return
	substitutionResult.completeAsSuccessful(messageProvider, "dvpp.varsub_info_complete");
	return processedContent;
    }

    @Override
    public File createSubstitutedFile(File source, ExecutionResult result) throws VariableSubstitutionException {
	ExecutionResult substitutionResult = createSubstitutionResultHeader(result);

	try {
	    // create null session
	    Session nullSession = new NullSessionImpl();
	    nullSession.connect(new com.alpha.pineapple.model.configuration.Resource(), new Credential());

	    // substitute
	    validateFileIsFitforSubstitution(source, substitutionResult);
	    String sourceFileContent = loadSourceFile(source);
	    File destinationFile = createDestinationFileName(source);
	    deletePreviousFile(destinationFile);
	    String processedContent = resolveVariables(sourceFileContent, nullSession, result);
	    saveProcessedFile(destinationFile, processedContent);

	    // complete as success and return
	    substitutionResult.completeAsSuccessful(messageProvider, "dvpp.varsub_info_complete");
	    return destinationFile;

	} catch (SourceFileValidationFailureException e) {

	    // complete as error and re-throw
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_validation_error", e);
	    throw e;

	} catch (VariableSubstitutionException e) {

	    // complete as error and re-throw
	    Object[] args = { e.getCause() };
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_info_error", args, e);
	    throw e;

	} catch (SessionConnectException e) {

	    // complete as error and re-throw
	    Object[] args = { e.getCause() };
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_info_error", args, e);
	    String message = messageProvider.getMessage("dvpp.varsub_info_error", args);
	    throw new VariableSubstitutionException(message, e);
	}
    }

    @Override
    public File createSubstitutedFile(File source, Session session, ExecutionResult result)
	    throws VariableSubstitutionException {
	ExecutionResult substitutionResult = createSubstitutionResultHeader(result);

	try {

	    // substitute
	    validateFileIsFitforSubstitution(source, substitutionResult);
	    String sourceFileContent = loadSourceFile(source);
	    File destinationFile = createDestinationFileName(source);
	    deletePreviousFile(destinationFile);
	    String processedContent = resolveVariables(sourceFileContent, session, result);
	    saveProcessedFile(destinationFile, processedContent);

	    // complete as success and return
	    substitutionResult.completeAsSuccessful(messageProvider, "dvpp.varsub_info_complete");
	    return destinationFile;

	} catch (SourceFileValidationFailureException e) {

	    // complete as error and re-throw
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_validation_error", e);
	    throw e;

	} catch (VariableSubstitutionException e) {

	    // complete as error and re-throw
	    Object[] args = { e.getCause() };
	    substitutionResult.completeAsError(messageProvider, "dvpp.varsub_info_error", args, e);
	    throw e;
	}

    }

    /**
     * Perform variable substitution on content.
     * 
     * The properties defined on the resource contained by the plugin session is
     * used for variable resolution.
     * 
     * @param source
     *            source content which is processed for variables.
     * @param session
     *            plugin session.
     * 
     * @return processed file content.
     * 
     * @throws VariableSubstitutionException
     *             if variable substitution fails.
     */
    String resolveVariables(String source, Session session, ExecutionResult result)
	    throws VariableSubstitutionException {

	// execution context for operation
	Context executionContext = executionContextRepository.get(result);

	// get module and model from context
	Module module = (Module) executionContext.get(CoreConstants.MODULE_KEY);
	Models model = (Models) executionContext.get(CoreConstants.MODULE_MODEL_KEY);

	// create variable substituted string
	return modelVariableSubstitutor.createObjectWithSubstitution(module, model, session.getResource(), source);
    }

    /**
     * Validate file is fit for substitution. It is validated whether file is a
     * file and whether file size is smaller than maximum size.
     * 
     * @throws VariableSubstitutionException
     *             if validation fails.
     */
    void validateFileIsFitforSubstitution(File source, ExecutionResult result) throws VariableSubstitutionException {

	// assert file is valid
	ExecutionResult assertionResult = assertionHelper.assertFileExist(source, messageProvider,
		"dvpp.assert_sourcefile_exists", result);

	// add file path info to execution result
	String messageHeader = messageProvider.getMessage("dvpp.assert_sourcefile_exists_info");
	assertionResult.addMessage(messageHeader, source.getAbsolutePath());

	// throw exception on failure
	if (!assertionResult.isSuccess()) {
	    String message = messageProvider.getMessage("dvpp.createvarsubtempfile_sourcefile_exists_failure");
	    throw new SourceFileValidationFailureException(message);
	}

	// assert file size valid
	assertionResult = assertionHelper.assertFileSizeIsSmaller(source, DEFAULT_MAXIMUM_SIZE, messageProvider,
		"dvpp.assert_sourcefile_size", result);

	// add file size info to execution result
	messageHeader = messageProvider.getMessage("dvpp.assert_sourcefile_size_info");
	assertionResult.addMessage(messageHeader, Long.toString(source.length()));

	// add maximum file size info to execution result
	messageHeader = messageProvider.getMessage("dvpp.assert_sourcefile_max_size_info");
	assertionResult.addMessage(messageHeader, Long.toString(DEFAULT_MAXIMUM_SIZE));

	// throw exception on failure
	if (!assertionResult.isSuccess()) {
	    Object[] arg = { DEFAULT_MAXIMUM_SIZE };
	    String message = messageProvider.getMessage("dvpp.createvarsubtempfile_sourcefile_size_failure", arg);
	    throw new SourceFileValidationFailureException(message);
	}
    }

    /**
     * Save processed file.
     * 
     * @param destinationFile
     *            destination file name.
     * @param processedContent
     *            processed file content.
     * 
     * @throws VariableSubstitutionException
     *             if save fails.
     */
    void saveProcessedFile(File destinationFile, String processedContent) throws VariableSubstitutionException {
	try {
	    FileUtils.write(destinationFile, processedContent, DEFAULT_FILEENCODING_UTF_8);

	} catch (IOException e) {
	    Object[] args = { destinationFile.getAbsolutePath() };
	    String message = messageProvider.getMessage("dvpp.createvarsubtempfile_save_failure", args);
	    throw new VariableSubstitutionException(message, e);
	}
    }

    /**
     * Delete existing destination file if it exists.
     * 
     * @param destinationFile
     *            to be deleted.
     * 
     * @throws VariableSubstitutionException
     *             if deletion fails.
     */
    void deletePreviousFile(File destinationFile) throws VariableSubstitutionException {

	try {
	    if (destinationFile.exists())
		FileUtils.forceDelete(destinationFile);

	} catch (IOException e) {
	    Object[] args = { destinationFile.getAbsolutePath() };
	    String message = messageProvider.getMessage("dvpp.createvarsubtempfile_delete_failure", args);
	    throw new VariableSubstitutionException(message, e);
	}
    }

    /**
     * Create destination file name. The destination file will be placed in the
     * used temp directory.
     * 
     * @param source
     *            source file.
     * 
     * @return path to destination file which is located in the used temp
     *         directory.
     */
    File createDestinationFileName(File source) {
	File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();

	// create destination file
	String destinationFileName = new StringBuilder().append(this.getClass().getCanonicalName()).append("-")
		.append(source.getName()).toString();
	File destinationFile = new File(tempDirectory, destinationFileName);

	return destinationFile;
    }

    /**
     * Load source file into string with default encoding.
     * 
     * @param source
     *            path to source file.
     * 
     * @throws VariableSubstitutionException
     *             if loading fails.
     */
    String loadSourceFile(File source) throws VariableSubstitutionException {
	try {
	    return FileUtils.readFileToString(source, DEFAULT_FILEENCODING_UTF_8);

	} catch (IOException e) {
	    Object[] args = { source.getAbsolutePath() };
	    String message = messageProvider.getMessage("dvpp.createvarsubtempfile_load_failure", args);
	    throw new VariableSubstitutionException(message, e);
	}
    }

    /**
     * Create substitution result.
     * 
     * @param result
     *            parent result
     * 
     * @return substitution result.
     */
    ExecutionResult createSubstitutionResultHeader(ExecutionResult result) {
	// create execution result for substitution
	String substitutionHeader = messageProvider.getMessage("dvpp.varsub_info");
	ExecutionResult validationResult = result.addChild(substitutionHeader);
	return validationResult;
    }

}
