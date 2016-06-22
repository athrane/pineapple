------------------------------------------------------------
NOTES ON SETTING UP IDE + CHECKING OUT THE PROJECT
------------------------------------------------------------

1) Eclipse
-----------

1.1) Download Eclipse 4.4 (Luna) and install it.

1.2) Configure proxy settings in Eclipse by going to Windows -> Preferences
-> General -> Network Connections -> Manual Proxy Settings 

2) Subclipse
-----------

2.1) Search for "subclipse" in the Eclipse Marketplace and install it. 

2.2) Configure subclipse proxy settings in the servers file: 
On Windows it is located at C:\Documents and Settings\Application Data\Subversion\servers, 
on Linux and OSX at ~/.subversion/servers. Define the properties:

http-proxy-host = niceproxy
http-proxy-port = 1234

For more info: http://svnbook.red-bean.com/en/1.1/ch07.html#svn-ch-7-sect-1.3.1

2.3) Configure Subclipse settings in Eclipse: 

Open Eclipse -> Windows -> Preferences -> Team -> SVN -> SVN Interface 
and select: SVNKit (Pure Java) .....


3) Create file based workspace & checkout source from java.net
-----------

This workspace will provide a file based view of the entire project. 
Later on a workspace which provides a Java based view is created. 
Due to limitations in Eclipse and Maven 2 two workspaces are required.

3.1) Create a new workspace named: c:\projects\pineapple-file-ws

3.2) File -> Import -> Other -> Checkout Projects from SVN -> Create a new repository location

3.3) Enter the URL: https://svn.java.net/svn/pineapple~svn 

3.4) Select the folder: trunk. And click "Next".

3.5) Enter user name and password to access the site.
If you haven't got an account then create one at java.net.

3.6) Select: check out as a project configured using New Project Wizard.

3.7) Select: Head Revision. And click "Finish".

3.8) Select the wizard: General -> Project. 

3.9) Enter project name: pineapple-project. 

Unselect "Use default location". 
Enter the a location for the project. 
On windows it could be: c:\projects\pineapple-project. 

The result is:
- The source for the project is located in c:\projects\pineapple-project
- The Eclipse workspace with a file based view is located in: c:\projects\pineapple-file-ws

4) Maven
---------

4.1) Download Maven 3.1.1 and install it.

4.2 ) Add $M2_HOME/bin to your $PATH.

4.3) Create a MAVEN_OPTS environment variable and set its value to: 

-Xms512M -Xmx512M

3.4) in the settings.xml file add the java.net repository to get access to J2EE 1.5 jars:

Add this stanza to repositories:

<repository>
  <id>java.net</id>
  <url>http://download.java.net/maven/1</url>
  <layout>legacy</layout>
</repository>				

For more info: https://maven-repository.dev.java.net/

5) Add third party jars to local repository
----------------------------------------------

Since the Pineapple uses some third party jars that are not hosted on the Maven 
repositories you will need to manually install the jars into your local 
repository yourself. 

5.1) Add WebLogic 12.1.2 jar to local Maven 3.0 repository

Use the WebLogic jar builder tool to build a JDK 1.6 client jar from a WebLogic 12.1.2 
installation: http://docs.oracle.com/cd/E24329_01/web.1211/e24378/jarbuilder.htm

Then run the following commands from the directory where the wlfullclient.jar was created 
to install the jar into your local repository.

mvn install:install-file -DgroupId=oracle -DartifactId=weblogic-full-client -Dversion=12.1.2 -Dpackaging=jar -Dfile=wlfullclient.jar -DgeneratePom=true

5.2) Configure Maven to access ZK EE artifacts

The Pineapple project has a ZOL license to use ZK EE. During the build Maven will download ZK EE artifacts.
The ZK EE repository requires authentication to access the ZK EE artifacts. 
	
Configure the Maven settings.xml file to support authentication by adding a server stanza for the ZK EE repository:
	
<servers>
	<server>
		<id>ZK EE</id><!-- Same as your repository name -->
		<!-- Your premium user name and password -->
		<username>zk-ee-user</username>
		<password>zk-ee-password</password>
	</server>
</servers>
	
User and password for the server element must be provided by ZKOSS.

Detailed description can be found at the ZK Site:	
http://books.zkoss.org/wiki/ZK_Installation_Guide/Setting_up_IDE/Maven/Resolving_ZK_Framework_Artifacts_via_Maven

To support SSL communication with the ZK EE repository, the JVM which is used to execute Maven, must have access to the ZK certificate. 
	
This can be done in several different ways, but one way to do is:
1) Get CA certificate from ZKOSS.
2) Import it into cacerts for JDK used by Maven to build Pineapple.	
This process is documented at: 
http://books.zkoss.org/wiki/ZK_Installation_Guide/Setting_up_IDE/Maven/Resolving_ZK_Framework_Artifacts_via_Maven#Sample_of_pom.xml_for_licensed_Professional_Package

Example from the ZK site:
"C:\Program Files\Java\jdk1.7.0_25\bin\keytool.exe" -import -noprompt -trustcacerts -alias ZKOSS_root_CA -file c:\temp-pineapple\ZKOSS_root_CA.crt -keystore "C:\Program Files\Java\jdk1.7.0_25\jre\lib\security\cacerts" -storepass changeit
	
An alternative way is to:
	
1) Get CA certificate from ZKOSS.
2) Extract certificate and create ZK EE key store:

keytool.exe -importcert -alias zkee -keystore zkee.jks -storepass secret -file ZKOSS_root_CA.crt	

3) Invoke Maven with MAVEN_OPT configured with these arguments:
	
set MAVEN_OPTS=-Djavax.net.ssl.trustStore=/path/to/apache-maven-3.1.1/conf/zkee.jks -Djavax.net.ssl.trustStorePassword=secret

6) Build pineapple
-------------------

6.1) From command-prompt , cd into the c:\projects\pineapple-project directory and run:

mvn -cpu clean install

7) Create Java based workspace in Eclipse
-----------

7.1) Open Eclipse

7.2) Create a new workspace named: c:\projects\pineapple-java-ws

8) Configure Maven in Eclipse 
-----------

8.1) Open the Eclipse workspace: c:\projects\pineapple-java-ws

8.2) Configure the Maven settings file to use by selecting Windows -> Preferences 
-> Maven -> User Settings and browse to the location of your Maven settings file.
Select the file settings.xml.
Click Apply.

9) Import Maven projects into Eclipse
-----------

9.1) Open the Eclipse workspace: c:\projects\pineapple-java-ws

9.2) Import projects by going to File -> Import ->  Maven -> Existing Maven Projects 
and browse to your workspace and the pineapple root at c:\projects\pineapple-project. 
Select all sub projects and click Finish.

10) Configure Subclipse in Eclipse pineapple-java-ws workspace
-----------

10.1) Open the Eclipse workspace: c:\projects\pineapple-java-ws

10.2) Configure Subclipse settings in Eclipse: 

Open Eclipse -> Windows -> Preferences -> Team -> SVN -> SVN Interface 
and select: SVNKit (Pure Java) .....

10.3) Share project: 

Right click on EACH project (in sequence) in the package explorer -> Team -> Share project -> SVN
-> Finish.

The result is:
- The source for the project is located in c:\projects\pineapple-project
- The Eclipse workspace with a file based view is located in: c:\projects\pineapple-file-ws
- The Eclipse workspace with a Java based view is located in: c:\projects\pineapple-java-ws

11) Configure code template in Eclipse pineapple-java-ws workspace
-----------

11.1) Open the Eclipse workspace: c:\projects\pineapple-java-ws

11.2) Open Eclipse -> Windows -> Preferences -> Java -> Code Style -> Code Templates
-> Import -> C:\projects\pineapple-project\src\checkstyle\maven-eclipse-codestyle.xml
and click Apply.

11.3) Open Eclipse -> Windows -> Preferences -> Java -> Code Style -> Formatter
-> Import -> C:\projects\pineapple-project\src\checkstyle\maven-eclipse-codestyle.xml
and click Apply.

------------------------------------------------------------
NOTES ON BUILDING AND RUNNING PINEAPPLE
------------------------------------------------------------

12) Build pineapple
-------------------

12.1) From command-prompt , cd into the pineapple directory and run:

mvn -cpu clean install

13) Enjoy
