package com.alpha.pineapple.plugin.git.command;

import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_MASTER;
import static com.alpha.pineapple.plugin.git.GitConstants.MODULES_EXP;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.git.session.GitSession;

/**
 * <p>
 * Implementation of the {@linkplain Command} interface which clones a Git
 * repository.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>branch</code> contains which branch in the Git repository should be
 * cloned. If the value is blank then "master" is used as default value. The
 * type is {@linkplain String}.</li>
 * 
 * <li><code>destination</code> defines the destination directory for the clone.
 * The destination is resolved using the {@linkplain RuntimeDirectoryProvider}
 * to support resolution of module path variables. If the destination is blank
 * then value is resolved to 'modules:REPOSITORY_NAME'. If the resolved
 * destination directory exists then it is deleted prior to cloning the
 * repository.The type is {@linkplain String}.</li>
 * 
 * <li><code>session</code> defines the plugin session used communicate with an
 * agent. The type is {@linkplain GitSession}.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * {@linkplain ExecutionResult}.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * <ul>
 * 
 * <li>The Git repository is cloned at the defined destination.</li>
 * 
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the
 * command failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the command fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class CloneRepositoryCommand implements Command {

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains Git branch.
	 */
	public static final String BRANCH_KEY = "branch";

	/**
	 * Key used to identify property in context: Contains clone destination
	 * directory.
	 */
	public static final String DESTINATION_KEY = "destination";

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	GitSession session;

	/**
	 * Git branch.
	 */
	@Initialize(BRANCH_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	String branch;

	/**
	 * Destination directory for clone.
	 */
	@Initialize(DESTINATION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	String destination;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "gitMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	@Override
	public boolean execute(Context context) throws Exception {

		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		try {

			// if branch is undefined set default value to 'master'
			if (branch.isBlank())
				branch = BRANCH_MASTER;

			// add branch info message
			var message = messageProvider.get("crc.clone_repository_branch_info", branch);
			executionResult.addMessage(MSG_MESSAGE, message);

			// if undefined destination set default value
			if (destination.isBlank())
				destination = MODULES_EXP + session.getRepositoryName();

			// resolve destination directory
			var destDir = coreRuntimeDirectoryProvider.resolveModelPath(destination, executionResult);

			// add destination info message
			message = messageProvider.get("crc.clone_repository_destination_info", destination, destDir);
			executionResult.addMessage(MSG_MESSAGE, message);

			// delete destination directory if it exist
			if (destDir.exists()) {

				// add info message
				message = messageProvider.get("crc.clone_repository_delete_destination_info", destDir);
				executionResult.addMessage(MSG_MESSAGE, message);

				// delete directory sub-directories and files
				Files.walk(destDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(f -> {
					// delete file
					f.delete();

					// add info message
					var message2 = messageProvider.get("crc.clone_repository_delete_destination_file_info", f);
					executionResult.addMessage(MSG_MESSAGE, message2);
				});
			}

			// clone
			session.cloneRepository(branch, destDir);

		} catch (

		Exception e) {
			Object[] args = { e.toString() };
			executionResult.completeAsFailure(messageProvider, "crc.clone_repository_failure", args);
			return Command.CONTINUE_PROCESSING;
		}

		// handle successful execution
		executionResult.completeAsSuccessful(messageProvider, "crc.clone_repository_success");
		return Command.CONTINUE_PROCESSING;
	}

}
