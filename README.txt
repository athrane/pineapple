------------------------------------------------------------
NOTES ON SETTING UP IDE + CHECKING OUT THE PROJECT
------------------------------------------------------------

1) IDE: Eclipse
-----------

1.1) Download Eclipse and install it.

2) Maven 
---------

2.1) Download Maven 3.5.3 and install it.

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

3) Create Maven based workspace in Eclipse
-----------

3.1) Open Eclipse

3.2) Create a new workspace named: c:\projects\pineapple-ws

4) Configure Maven in Eclipse 
-----------

4.1) Open the Eclipse workspace: c:\projects\pineapple-ws

4.2) Configure the Maven settings file to use by selecting Windows -> Preferences 
-> Maven -> User Settings and browse to the location of your Maven settings file.
Select the file settings.xml.
Click Apply.

5) Import Maven projects into Eclipse from GitHub
-----------

5.1) Open the Eclipse workspace: c:\projects\pineapple-ws

5.2) Import projects by going to File -> Import ->  Git -> Projects from Git -> Clone URI 

URI: https://github.com/athrane/pineapple.git
Host: github.com
Repository Path: /athrane/pineapple.git
Protocol: HTTPS
User: your-GitHub-user
Password: your-GitHub-PWD
and click next.

5.3) Branch Selection

Select "master" and click next.

5.4) Local Destination

Directory: C:\Users\myuser\git\pineapple
Initial branch: "master"
Configuration -> Remote name: origin
and click next.

5.5) Select a wizard to use for importing projects
Click cancel.

5.6) Import projects by going to File -> Import ->  Maven -> Existing Maven Projects 
and browse to the Git working directory at: C:\Users\myuser\git\pineapple
Select all sub projects and click Finish.

6) Configure code template in Eclipse pineapple-ws workspace
-----------

6.1) Open the Eclipse workspace: c:\projects\pineapple-ws

6.2) Open Eclipse -> Windows -> Preferences -> Java -> Code Style -> Code Templates
-> Import -> C:\Users\myuser\git\pineapple\src\checkstyle\maven-eclipse-codestyle.xml
and click Apply.

7) Configure run configuration in Eclipse

7.1) Open Eclipse -> Run -> Run Configurations -> Maven Build -> 
Name: Maven jetty run
Base-directory: $workspace_loc:/pineapple-web-application-war}
User settings: C:\tools\apache-maven-3.5.3\conf\settings.xml
Offline: true

------------------------------------------------------------
NOTES ON BUILDING AND RUNNING PINEAPPLE
------------------------------------------------------------

8) Build pineapple
-------------------

8.1) From command-prompt , cd into the pineapple directory and run:

mvn -cpu clean install

9) Deploy Pineapple to Bintray

9.1) Add Bintray profile to settings.xml

       <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-pineapple-maven</id>
                    <name>bintray</name>
                    <url>http://dl.bintray.com/pineapple/maven</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-pineapple-maven</id>
                    <name>bintray-plugins</name>
                    <url>http://dl.bintray.com/pineapple/maven</url>
                </pluginRepository>
            </pluginRepositories>
            <id>bintray</id>
        </profile>

9.2) Activate profile

	<activeProfiles>
		..other profiles...
        <activeProfile>bintray</activeProfile>		
	</activeProfiles>

9.3) Configure server

 		<server>
            <id>bintray-pineapple-maven</id>
            <username>some-user</username>
            <password>some-password</password>
		</server>		

9.4) Invoke to deploy

mvn deploy
