package com.alpha.pineapple.plugin.ssh.session;

import com.jcraft.jsch.UserInfo;

/**
 * Implementation of the {@linkplain UserInfo} interface to support silent user
 * info.
 */
public class SilentUserInfoImpl implements UserInfo {

	/**
	 * Password.
	 */
	String password;

	/**
	 * Passphrase.
	 */
	String passphrase;

	SilentUserInfoImpl(String password, String passphrase) {
		this.password = password;
		this.passphrase = passphrase;
	}

	@Override
	public String getPassphrase() {
		return passphrase;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		return false;
	}

	@Override
	public boolean promptPassword(String arg0) {
		return false;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		return false;
	}

	@Override
	public void showMessage(String arg0) {
	}

}
