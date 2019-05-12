package com.alpha.testutils;

import javax.annotation.Resource;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.git.session.GitSession;
import com.alpha.pineapple.plugin.git.session.GitSessionImpl;

/**
 * Helper class which supports usage of Git in test cases.
 */
public class GitTestHelper {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STR = "";

	/**
	 * Object mother for the Git model.
	 */
	ObjectMotherContent contentMother = new ObjectMotherContent();

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother = new ObjectMotherEnvironmentConfiguration();

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "gitMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Create connected Git session with default settings:
	 * 
	 * @return connected Git session with default settings.
	 * 
	 * @throws Exception if session creation fails.
	 */
	public GitSession createDefaultSession() throws Exception {
		GitSession session = new GitSessionImpl(messageProvider);
		return session;
	}

	/**
	 * Initialize session with no authentification.
	 * 
	 * The credential is created with blank user/password to disable
	 * authentification.
	 * 
	 * @param uri repository URI.
	 * @param id  credential ID.
	 * 
	 * @return connected Git session.
	 * 
	 * @throws Exception if initialization fails.
	 */
	public GitSession initSessionWithNoAuth(String uri, String id) throws Exception {
		var session = createDefaultSession();
		var resource = contentMother.createResource(uri);
		var credential = envConfigMother.createCredential(id, EMPTY_STR, EMPTY_STR);
		session.connect(resource, credential);
		return session;
	}

}
