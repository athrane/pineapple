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
package com.alpha.pineapple.docker.utils;

import static com.alpha.pineapple.docker.DockerConstants.UNDEFINED_REPO_TAG;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.rest.ImageCreation;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedImage;

/**
 * Helper class for querying model data.
 */
public class ModelUtils {

    /**
     * String index for removal of prefix.
     */
    static final int INDEX_FOR_PREFIX_REMOVAL = 1;

    /**
     * Start index for truncated Id.
     */
    static final int START_INDEX = 0;

    /**
     * End index for truncated Id.
     */
    static final int END_INDEX = 12;

    /**
     * First list index.
     */
    static final int FIRST_INDEX = 0;

    /**
     * Returns true if image object is defined with a non-null or non-empty tag
     * parameter.
     * 
     * @param info
     *            image info object.
     * 
     * @return true if the tag parameter is defined and not empty.
     */
    public static boolean isTaggedImage(ImageInfo info) {
	Validate.notNull(info, "info is undefined");

	if (info.getTag() == null)
	    return false;
	if (info.getTag().isEmpty())
	    return false;
	return true;
    }

    /**
     * Returns true if image creation object contains a regular status update
     * information in the status field, i.e. it is defined with a non-null or
     * non-empty value.
     * 
     * @param info
     *            image creation object.
     * 
     * @return true if image creation object contains regular status update
     *         information in the status field. Returns false if the status
     *         field is null or empty.
     */
    public static boolean containsStatusUpdate(ImageCreation info) {
	Validate.notNull(info, "info is undefined");

	if (info.getStatus() == null)
	    return false;
	if (info.getStatus().isEmpty())
	    return false;
	return true;
    }

    /**
     * Returns true if image creation object contains a stream update
     * information in the stream field, i.e. it is defined with a non-null or
     * non-empty value.
     * 
     * @param info
     *            image creation object.
     * 
     * @return true if image creation object contains stream update information
     *         in the stream field. Returns false if the status field is null or
     *         empty.
     */
    public static boolean containsStreamUpdate(ImageCreation info) {
	Validate.notNull(info, "info is undefined");

	if (info.getStream() == null)
	    return false;
	if (info.getStream().isEmpty())
	    return false;
	return true;
    }

    /**
     * Returns true if image creation object contains error information in the
     * error field, i.e. it is defined with a non-null or non-empty value.
     * 
     * @param info
     *            image creation object.
     * 
     * @return true if image creation object contains error information in the
     *         error field, i.e. it is defined with a non-null or non-empty
     *         value.
     */
    public static boolean containsErrorMessage(ImageCreation info) {
	Validate.notNull(info, "info is undefined");

	if (info.getError() == null)
	    return false;
	if (info.getError().isEmpty())
	    return false;
	return true;
    }

    /**
     * Returns true if image creation was successful, i.e. no errors was
     * returned.
     * 
     * @param infos
     *            image creation objects.
     * 
     * @return true if image creation was successful, i.e. no errors was
     *         returned.
     */
    public static boolean isImageCreationSuccessful(ImageCreation[] infos) {
	Validate.notNull(infos, "infos is undefined");

	for (ImageCreation info : infos) {
	    if (containsErrorMessage(info))
		return false;
	}

	return true;
    }

    /**
     * Create truncated ID which is reduced to 12 characters long.
     * 
     * @param id
     *            id to be truncated. If the id is null or empty then the empty
     *            string is returned.
     */
    public static String createTruncatedId(String id) {
	if (id == null)
	    return "";
	if (id.isEmpty())
	    return "";
	return id.substring(START_INDEX, END_INDEX);
    }

    /**
     * Remove LF from Docker stream update.
     * 
     * @param update
     *            stream update.
     */
    public static String remoteLfFromStreamUpdate(String update) {
	if (update == null)
	    return update;
	return StringUtils.trim(update);
    }

    /**
     * Returns true if no repository tags are defined.
     * 
     * @param image
     *            listed Docker image.
     * 
     * @return true if no repository tags are defined.
     */
    public static boolean isNoImageRepoTagsDefined(ListedImage image) {
	if (image == null)
	    return true;
	if (image.getRepoTags() == null)
	    return true;

	List<String> tagsList = image.getRepoTags();
	if (tagsList.isEmpty())
	    return true;
	for (String tag : tagsList) {
	    if (!UNDEFINED_REPO_TAG.equals(tag))
		return false;
	}
	return true;
    }

    /**
     * Returns true if tag is defined in image name in container.
     * The tag is defined using the ":" separator.
     * 
     * @param image
     *            Docker image name .
     * 
     * @return true if repository tag is defined in image name.
     */
    public static boolean isImageRepoTagDefined(ListedContainer container) {
	Validate.notNull(container, "container is undefined.");
	return containsSeparator(container.getImage());
    }
    
    /**
     * Return true if string message is not null or empty.
     * 
     * @param stringMessage
     *            optional string message.
     * 
     * @return true if string message is not null or empty.
     */
    public static boolean isStringMessageDefined(String stringMessage) {
	if (stringMessage == null)
	    return false;
	return (!stringMessage.isEmpty());
    }

    /**
     * Return true if container name is prefixed with "/".
     * 
     * @param name
     *            container name.
     * 
     * @return true if container name is prefixed with "/"..
     */
    public static boolean isContainerNamePrefixed(String name) {
	if (name == null)
	    return false;
	if (name.isEmpty())
	    return false;
	return name.startsWith("/");
    }

    /**
     * If container name is prefixed with "/" then the prefix is removed.
     * 
     * @param name
     *            container name.
     * 
     * @return container name with prefix removed.
     */
    public static String removeContainerNamePrefix(String name) {
	if (!isContainerNamePrefixed(name))
	    return name;
	return name.substring(INDEX_FOR_PREFIX_REMOVAL);
    }

    /**
     * Get first list entry. If list is empty the null is returned.
     * 
     * @param list
     *            string list.
     * 
     * @return first list entry.
     */
    public static String getFirstListEntry(List<String> list) {
	if (list.isEmpty())
	    return null;
	return list.get(FIRST_INDEX);
    }

    /**
     * Returns true if name contains separator ":".
     * 
     * @param name name to test.
     * 
     * @return true if string contains separator ":".
     */
    public static boolean containsSeparator(String name) {
	Validate.notNull(name, "name is undefined.");
	int index = name.indexOf(":");
	return (index != -1);
    }
    
}
