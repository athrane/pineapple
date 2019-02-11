
pipeline {
    agent any

    stages {    		
    
        stage('Clean') {
            steps {
			    withMaven(
			    	maven: 'maven-3.5.4', 
			    	jdk: 'openjdk-11_linux-x64',
			    	mavenSettingsFilePath: '/var/jenkins_home/settings.xml',
			    	mavenLocalRepo:'~/.m2/repository' ) {							

	                    sh "echo JAVA_HOME=$JAVA_HOME"
    	                sh "mvn clean"
                }
            }
        }
    
		stage('Build') {
        	steps {	
			    withMaven(
			    	maven: 'maven-3.5.4', 
			    	jdk: 'openjdk-11_linux-x64',
			    	mavenSettingsFilePath: '/var/jenkins_home/settings.xml',
			    	mavenLocalRepo:'~/.m2/repository' ) {							

						sh 'mvn -DskipTests clean install'
			    }
            }
		}		
	}		    
}

