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

package com.alpha.pineapple.plugin.ssh.model;

import static com.alpha.pineapple.plugin.ssh.SshConstants.DISABLE_CHMOD;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.plugin.ssh.command.SecureCopyToCommand;
import com.alpha.pineapple.plugin.ssh.session.SshSession;

/**
 * Implementation of the {@linkplain Mapper} interface.
 */
public class MapperImpl implements Mapper {

	@SuppressWarnings("unchecked")
	@Override
	public void mapSecureCopy(SecureCopy command, Context context, SshSession session) {
		context.put(SecureCopyToCommand.LOCAL_FILE_KEY, command.getSource());
		context.put(SecureCopyToCommand.REMOTE_FILE_KEY, command.getDestination());

		// map chmod
		int chmodValue = DISABLE_CHMOD;
		if (command.getChmod() != null) {
			chmodValue = command.getChmod().intValue();
		}
		context.put(SecureCopyToCommand.FILE_PERMISSIONS_KEY, new Integer(chmodValue));

		// map chown
		int chownValue = DISABLE_CHMOD;
		if (command.getChown() != null) {
			chownValue = command.getChown().intValue();
		}
		context.put(SecureCopyToCommand.USER_OWNERSHIP_KEY, new Integer(chownValue));

		// map chgrp
		int chgrpValue = DISABLE_CHMOD;
		if (command.getChgrp() != null) {
			chgrpValue = command.getChgrp().intValue();
		}
		context.put(SecureCopyToCommand.GROUP_OWNERSHIP_KEY, new Integer(chgrpValue));

		context.put(SecureCopyToCommand.SUBSTITUTE_VARIABLES_KEY, new Boolean(command.isSubstituteVariables()));
		context.put(SecureCopyToCommand.SESSION_KEY, session);
	}

}
