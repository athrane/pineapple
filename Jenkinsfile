

pipeline {
    stages {    
		stage('Build') {
		    withMaven(
		    maven: 'maven-3.5.4'
		    jdk: 'openjdk-11_linux-x64') {				
				sh 'mvn -DskipTests clean install'
		    }
		}		
	}		
    
}