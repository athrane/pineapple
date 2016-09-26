------------------------------------------------------------
NOTES ON SETTING UP IDE + CHECKING OUT THE PROJECT
------------------------------------------------------------

1) IDE: Eclipse
-----------

1.1) Download Eclipse 4.6 (Neon) and install it.

2) Maven 
---------

2.1) Download Maven 3.3.3 and install it.

2.2 ) Add $M2_HOME/bin to your $PATH.

2.3) Create a MAVEN_OPTS environment variable and set its value to: 

-Xms512M -Xmx512M

2.4) in the settings.xml file add the java.net repository to get access to J2EE 1.5 jars:

Add this stanza to repositories:

<repository>
  <id>java.net</id>
  <url>http://download.java.net/maven/1</url>
  <layout>legacy</layout>
</repository>				

For more info: https://maven-repository.dev.java.net/

3) Add third party jars to local repository
----------------------------------------------

Since the Pineapple uses some third party jars that are not hosted on the Maven 
repositories you will need to manually install the jars into your local 
repository yourself. 

3.1) Add WebLogic 12.1.2 jar to local Maven 3.0 repository

Use the WebLogic jar builder tool to build a JDK 1.8 client jar from a WebLogic 12.1.2 
installation: http://docs.oracle.com/cd/E24329_01/web.1211/e24378/jarbuilder.htm

Then run the following commands from the directory where the wlfullclient.jar was created 
to install the jar into your local repository.

mvn install:install-file -DgroupId=oracle -DartifactId=weblogic-full-client -Dversion=12.1.2 -Dpackaging=jar -Dfile=wlfullclient.jar -DgeneratePom=true

3.2) Configure Maven to access ZK EE artifacts

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
	
a) Get CA certificate from ZKOSS.
b) Extract certificate and create ZK EE key store:

keytool.exe -importcert -alias zkee -keystore zkee.jks -storepass secret -file ZKOSS_root_CA.crt	

c) Invoke Maven with MAVEN_OPT configured with these arguments:
	
set MAVEN_OPTS=-Djavax.net.ssl.trustStore=/path/to/apache-maven-3.1.1/conf/zkee.jks -Djavax.net.ssl.trustStorePassword=secret

4) Create Maven based workspace in Eclipse
-----------

4.1) Open Eclipse

4.2) Create a new workspace named: c:\projects\pineapple-maven-ws

5) Configure Maven in Eclipse 
-----------

5.1) Open the Eclipse workspace: c:\projects\pineapple-maven-ws

5.2) Configure the Maven settings file to use by selecting Windows -> Preferences 
-> Maven -> User Settings and browse to the location of your Maven settings file.
Select the file settings.xml.
Click Apply.

6) Import Maven projects into Eclipse from GitHub
-----------

6.1) Open the Eclipse workspace: c:\projects\pineapple-maven-ws

6.2) Import projects by going to File -> Import ->  Git -> Projects from Git -> Clone URI 

URI: https://github.com/athrane/pineapple.git
Host: github.com
Repository Path: /athrane/pineapple.git
Protocol: HTTPS
User: your-GitHub-user
Password: your-GitHub-PWD
and click next.

6.3) Branch Selection

Select "master" and click next.

6.4) Local Destination

Directory: C:\Users\myuser\git\pineapple
Initial branch: "master"
Configuration -> Remote name: origin
and click next.

6.5) Select a wizard to use for importing projects
Click cancel.

6.6) Import projects by going to File -> Import ->  Maven -> Existing Maven Projects 
and browse to the Git working directory at: C:\Users\myuser\git\pineapple
Select all sub projects and click Finish.

7) Configure code template in Eclipse pineapple-java-ws workspace
-----------

7.1) Open the Eclipse workspace: c:\projects\pineapple-maven-ws

7.2) Open Eclipse -> Windows -> Preferences -> Java -> Code Style -> Code Templates
-> Import -> C:\Users\myuser\git\pineapple\src\checkstyle\maven-eclipse-codestyle.xml
and click Apply.


------------------------------------------------------------
NOTES ON BUILDING AND RUNNING PINEAPPLE
------------------------------------------------------------

8) Build pineapple
-------------------

8.1) From command-prompt , cd into the pineapple directory and run:

mvn -cpu clean install

9) Enjoy
