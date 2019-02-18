package com.alpha.junitutils;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.alpha.testutils.ObjectMotherIO;

/**
 * Implementation of the {@linkplain TestRule} interface. Creates a new test
 * directory prior to execution of a test method. The rule can be configured to
 * delete the test directory after the test method is executed.
 */
public class CreateTestDirectoryRule implements TestRule {

	/**
	 * Statement class for the rule.
	 */
	class TestDirStatement extends Statement {

		/**
		 * JUnit statement.
		 */
		final Statement base;

		/**
		 * JUnit description.
		 */
		Description description;

		/**
		 * Defines if directories should be deleted after execution of a test method.
		 */
		boolean deleteDirs = false;

		/**
		 * Test directory.
		 */
		File testDir = null;

		/**
		 * Constructor.
		 * 
		 * @param base       JUnit statement.
		 * @param base       JUnit description.
		 * @param deleteDirs defines if directories should be deleted after the test.
		 */
		public TestDirStatement(Statement base, Description description, boolean deleteDirs) {
			this.base = base;
			this.deleteDirs = deleteDirs;
			this.description = description;
		}

		/**
		 * Create test directory name.
		 * 
		 * @param description JUnit description.
		 * 
		 * @return test directory name.
		 */
		String createTestDirName(Description description) {
			String methodName = description.getMethodName();
			String className = description.getClassName();
			return new StringBuilder().append(className).append("-").append(methodName).toString();
		}
		
		@Override
		public void evaluate() throws Throwable {
			String testDirName = createTestDirName(description);

			// create directory
			File testDir = ObjectMotherIO.createTestMethodDirectory(testDirName);

			// log debug message
			if (logger.isDebugEnabled()) {
				String message = new StringBuilder().append("Created test directory [")
						.append(testDir.getAbsolutePath()).append("] for method [").append(description.getClassName())
						.append(".").append(description.getMethodName()).append("(..)].").toString();
				logger.debug(message);
			}

			// evaluate test
			base.evaluate();

			// if specified delete directories
			if (deleteDirs) {
				// delete directories and files
				ObjectMotherIO.deleteDirectory(testDir);
			}
		}

	}

	/**
	 * Logger object.
	 */
	static Logger logger = Logger.getLogger(CreateTestDirectoryRule.class.getName());

	/**
	 * Defines if directories should be deleted after execution of a test method.
	 */
	boolean deleteDirs = false;

	/**
	 * Junit statement.
	 */
	TestDirStatement statement;

	/**
	 * CreateTestDirectoryRule constructor.
	 * 
	 * Test directories isn't deleted after the test.
	 */
	public CreateTestDirectoryRule() {
		this.deleteDirs = false;
	}

	/**
	 * CreateTestDirectoryRule constructor.
	 * 
	 * @param deleteDirs defines if directories should be deleted after the test.
	 */
	public CreateTestDirectoryRule(boolean deleteDirs) {
		this.deleteDirs = deleteDirs;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		statement = new TestDirStatement(base, description, deleteDirs);
		return statement;
	}

	public File getDirectory() {
		return statement.testDir;
	}
}
